package mhir.optimize

import mhir.ir._

/** Analysis for finding the latency of each node in the pipeline.
  *
  * The latency of a node is the number of clock cycles until its output
  * represents a valid logical element. For example, inputs to the accelerator
  * are available immediately. By contrast, an expression like
  * `s.StmMap(f).StmMap(g)` (where `s` is an accelerator input and `f` and `g`
  * are scalar functions) has a latency of 2 cycles. In VHDL, the first two
  * outputs from this expression will be undefined.
  *
  * @param handshake
  *   whether the handshake protocol is enabled (this affects the latency of
  *   some nodes, like [[mhir.ir.LetStm]]).
  */
class LatencyAnalysis(handshake: Boolean) {

  private def letStmLatency: Int = if (this.handshake) 2 else 0

  /** Decide whether the actual latency of the final node in the given
    * expression is well-defined.
    *
    * If this method returns `true`, it means all branches in [[mhir.ir.LetStm]]
    * are balanced, the latency of different accelerator inputs is matched, etc.
    */
  def allLatenciesMatch(e: Expr): Boolean = {
    this.actualLatency(e).latency.nonEmpty
  }

  /** Find the latency of each node, but only if the latency along each path is
    * consistent.
    *
    * If a [[mhir.ir.StmBuild]] node has multiple producer streams, this method
    * will take the maximum of all their latencies (plus the delay added by the
    * [[mhir.ir.StmBuild]] itself).
    */
  def actualLatency(e: Expr): LatencyNode = {
    latency(e, Map(), xs => if (xs.toSet.size > 1) None else xs.headOption)
  }

  /** Find the latency of each node assuming latency matching will be
    * successful.
    *
    * If a [[mhir.ir.StmBuild]] node has multiple producer streams, this method
    * will take the maximum of all their latencies (plus the delay added by the
    * [[mhir.ir.StmBuild]] itself).
    */
  def idealLatency(e: Expr): LatencyNode = {
    latency(e, Map(), xs => Some(xs.max))
  }

  private def latency(
      e: Expr,
      latencyByVar: Map[Param, Option[Int]],
      sbuildAggregator: Iterable[Int] => Option[Int]
  ): LatencyNode = {
    e match {
      case Function(x, body) =>
        latency(body, latencyByVar + (x -> Some(0)), sbuildAggregator)
      case x: Param =>
        latencyByVar.get(x) match {
          case Some(lat) => LatencyParam(lat)
          case None =>
            throw new IllegalArgumentException(
              s"cannot find latency for unknown variable: '$x'"
            )
        }
      case LetStm(_, x, in, out) =>
        val inLatency = latency(in, latencyByVar, sbuildAggregator)
        val selfLatency = this.letStmLatency
        val outLatency = latency(
          out,
          latencyByVar + (x -> inLatency.latency.map(_ + selfLatency)),
          sbuildAggregator
        )
        LatencyLetStm(outLatency.latency, inLatency, outLatency)
      case s: StmBuild =>
        val latencyChildren = s.producers
          .map({ case (x, (p, _)) =>
            x -> latency(p, latencyByVar, sbuildAggregator)
          })
        val alwaysReady = s.producers
          .forall({ case (_, (_, ready)) => ready == True })
        val selfLatency = stmBuildSelfLatency(s)
        val outLatency = if (latencyChildren.isEmpty) {
          Some(1)
        } else if (alwaysReady) {
          if (latencyChildren.exists({ case (_, c) => c.latency.isEmpty })) {
            None
          } else {
            sbuildAggregator(
              latencyChildren.map({ case (_, c) => c.latency.get })
            ).zip(selfLatency)
              .map({ case (x, y) => x + y })
              .headOption
          }
        } else {
          None
        }
        LatencyStmBuild(outLatency, selfLatency, latencyChildren)
      case e =>
        throw new IllegalArgumentException(s"cannot find latency for $e")
    }
  }

  /** The latency added by one [[mhir.ir.StmBuild]] node, without considering
    * the latencies of its producer streams.
    *
    * In other words, this is the latency the node would have if all its inputs
    * have zero latency.
    */
  private def stmBuildSelfLatency(s: StmBuild): Option[Int] = {
    s.valid match {
      case True => Some(1)
      case Equal(t: Param, IntCst(k))
          if s.accVars.contains(t) && (k + 1).isValidInt =>
        s.equations.get(t) match {
          case Some(
                (
                  IntCst(0),
                  Mux(
                    Equal(t1: Param, IntCst(k1)),
                    IntCst(0),
                    Sum(IntCst(1), t2: Param)
                  )
                )
              ) if t1 == t && t2 == t && k1 == k =>
            Some((k + 1).toInt)
          case _ =>
            None
        }
      case _ => None
    }
  }
}
