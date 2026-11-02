package mhir.delay

import com.typesafe.scalalogging.Logger
import mhir.ir._

/** Analysis for finding the latency of each node in the pipeline.
  *
  * The latency of a node is the number of clock cycles until its output
  * represents a valid logical element. For example, if `s` is available at time
  * 0, an expression like `s.StmMap(f).StmMap(g)` (where `f` and `g` are scalar
  * functions) has a latency of 2 cycles. In VHDL, the first two outputs from
  * this expression will be undefined (when the handshake protocol is enabled)
  * or part of the "physical output" (when the handshake protocol is disabled).
  *
  * @param handshake
  *   whether the handshake protocol is enabled.
  */
class LatencyAnalysis(handshake: Boolean) {

  private val logger: Logger = Logger(getClass.getName)

  private def letStmLatency: Int = if (this.handshake) 2 else 0

  /** Find the latency of each node, but only if the latency along each path is
    * consistent.
    *
    * If a [[mhir.ir.StmBuild]] node has multiple producer streams, this method
    * will insist that their latencies all be consistent with the corresponding
    * delay annotations.
    */
  def actualLatency(
      e: Expr,
      inputLatencies: Map[Param, Latency]
  ): LatencyNode = {
    val aggregator = (xs: Iterable[Int]) =>
      if (xs.toSet.size > 1) None else xs.headOption
    latency(e, inputLatencies, aggregator)
  }

  /** Find the latency of each node assuming latency matching will be
    * successful.
    *
    * If a [[mhir.ir.StmBuild]] node has multiple producer streams, this method
    * will take the maximum of all their latencies (plus the delay added by the
    * [[mhir.ir.StmBuild]] itself).
    */
  def idealLatency(
      e: Expr,
      inputLatencies: Map[Param, Latency]
  ): LatencyNode = {
    latency(e, inputLatencies, xs => Some(xs.max))
  }

  private def latency(
      e: Expr,
      latencyByVar: Map[Param, Latency],
      sbuildAggregator: Iterable[Int] => Option[Int]
  ): LatencyNode = {
    e match {
      case x: Param =>
        latencyByVar.get(x) match {
          case Some(lat) => LatencySource(lat)
          case None =>
            logger.warn(s"delay is unknown for input $x")
            LatencySource(LatencyUnknown)
        }
      case StmLiteral(physical, _) =>
        LatencySource(LatencyConcrete(physical.length))
      case LetStm(_, x, in, out) =>
        val inLatency = latency(in, latencyByVar, sbuildAggregator)
        val selfLatency = this.letStmLatency
        val inPlusSelfLatency = inLatency.latency match {
          case LatencyConcrete(n) => LatencyConcrete(n + selfLatency)
          case LatencyUnknown     => LatencyUnknown
        }
        val outLatency = latency(
          out,
          latencyByVar + (x -> inPlusSelfLatency),
          sbuildAggregator
        )
        LatencyLetStm(outLatency.latency, inLatency, outLatency)
      case s: StmBuild =>
        // Recurse
        val latencyChildren = s.producers.map({ case (x, (p, _, _)) =>
          x -> latency(p, latencyByVar, sbuildAggregator)
        })
        // Collect the delay annotations
        val delayAnnotations = s.producers.map({
          case (x, (_, _, IntCst(delay)))            => x -> Some(delay.toInt)
          case (x, (_, _, delay)) if !this.handshake =>
            // TODO: support non-static delay annotations?
            throw new IllegalArgumentException(
              s"non-static delay annotation $delay (on producer $x) is not supported yet"
            )
          case (x, p) => x -> guessProducerDelay(p)
        })
        val outDelay = s.delay match {
          case IntCst(delay)            => Some(delay.toInt)
          case delay if !this.handshake =>
            // TODO: support non-static delay annotations?
            throw new IllegalArgumentException(
              s"non-static delay annotation $delay (for sbuild output) is not supported yet"
            )
          case _ => guessOutDelay(s)
        }
        // Assume "internal source nodes" have local epoch 0
        if (s.producers.isEmpty) {
          return LatencyStmBuild(
            outDelay match {
              case Some(n) => LatencyConcrete(n)
              case None    => LatencyUnknown
            },
            Some(0),
            Map()
          )
        }
        // For each producer, we can work out what the local epoch should be
        // knowing that the absolute latency of that producer should equal the
        // local epoch plus the corresponding delay annotation...
        val localEpochByProducer = s.producers.map({ case (x, _) =>
          val epoch = (latencyChildren(x).latency, delayAnnotations(x)) match {
            case (LatencyConcrete(latency), Some(delay)) =>
              Some(latency - delay)
            case _ => None
          }
          x -> epoch
        })
        // ... then we aggregate those answers using the given function (take
        // the maximum, insist they all be the same, etc.)
        val localEpoch = if (localEpochByProducer.values.forall(_.nonEmpty)) {
          sbuildAggregator(localEpochByProducer.values.map(_.get))
        } else {
          // At least one of the producers are missing a latency or a delay
          // annotation, so I have no idea what the epoch of this sbuild is
          None
        }
        val outLatency = localEpoch
          .zip(outDelay)
          .map({ case (localEpoch, outDelay) => localEpoch + outDelay })
          .headOption match {
          case Some(n) => LatencyConcrete(n)
          case None    => LatencyUnknown
        }
        LatencyStmBuild(outLatency, localEpoch, latencyChildren)
      case e =>
        throw new IllegalArgumentException(s"cannot find latency for $e")
    }
  }

  private def guessProducerDelay(producer: (Expr, Expr, Expr)): Option[Int] = {
    producer match {
      case (_, True, Tuple()) => Some(0)
      // TODO: add case for StmConcat?
      case _ => None
    }
  }

  /** A guess at the output delay for one [[mhir.ir.StmBuild]] node, i.e., the
    * time at which the output becomes valid, relative to the <i>local<i> epoch.
    *
    * In other words, this is the latency the node would have if all its inputs
    * were immediately valid, at the global epoch.
    */
  private def guessOutDelay(s: StmBuild): Option[Int] = {
    s.valid match {
      case True => Some(1)
      case Equal(t: Param, IntCst(k))
          if s.namesDefinedHere.contains(t) && (k + 1).isValidInt =>
        s.accumulators.get(t) match {
          case Some(
                (
                  IntCst(0),
                  Mux(
                    Equal(t1: Param, IntCst(k1)),
                    IntCst(0),
                    Sum(IntCst(1), t2: Param)
                  ),
                  _
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
