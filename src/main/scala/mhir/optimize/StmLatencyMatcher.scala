package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

trait StmLatencyMatcher {
  def enabled: Boolean
  def disabled: Boolean = !enabled

  def matchLatencies(e: Expr): Expr
}

object StmLatencyMatcher {
  def apply(enabled: Boolean = true): StmLatencyMatcher = {
    if (enabled) new EnabledStmLatencyMatcher(LetStmMover)
    else DisabledStmLatencyMatcher
  }
}

/** Transformation that tries to match the latency of different branches of a
  * [[mhir.ir.LetStm]].
  *
  * If successful, the transformation may increase resource usage slightly but
  * decrease the initiation interval, which should decrease the total latency
  * and therefore increase the real throughput (i.e., total elements / total
  * cycles).
  */
class EnabledStmLatencyMatcher(letStmMover: LetStmMover.type)
    extends StmLatencyMatcher {

  private val logger = Logger(getClass.getName)

  override def enabled: Boolean = true

  /** Inserts extra [[mhir.ir.StmBuild]]s in the stream pipeline to try to match
    * the latency across different branches in [[mhir.ir.LetStm]]s.
    *
    * @param e
    *   the stream pipeline to process.
    */
  def matchLatencies(e: Expr): Expr = {
    require(
      e.hasType,
      s"Cannot match latencies in expression of type ${e.typ}."
    )
    def rec(e: Expr): Expr = {
      e match {
        case s: StmBuild =>
          val newEquations = s.equations
            .map({
              case (x, (z, next)) if x.typ.isInstanceOf[TyStm] =>
                x -> (matchLatencies(z), next)
              case eqn => eqn
            })
          StmBuild(
            n = s.n,
            data = s.data,
            valid = s.valid,
            equations = newEquations
          )()
        case LetStm(_, x, in, out) =>
          val in1 = matchLatencies(in).tchk()
          val out1 = matchLatencies(out).tchk()
          latencyOfLongestPath(x, out1) match {
            case Some(lat) =>
              val out2 = increaseLatencyTo(out1, x, lat)
              // Changing the latencies may actually increase the required
              // buffer size, so reset it to the max value.
              val TyStm(_, n) = in1.typ
              LetStm(n, x, in1, out2)()
            case None => e
          }
        case Function(x, body) if body.typ.isInstanceOf[TyStm] =>
          Function(x, matchLatencies(body))()
        case _ =>
          e
      }
    }
    // There may be some strange things happening after the joins, but that
    // shouldn't prevent latency matching because the strange things
    // contribute the same latency to all paths.
    val result = rec(letStmMover.moveDown(e))
    val checkedResult = result.tchk()
    assert(checkedResult.typ ~= e.typ)
    checkedResult
  }

  private def latencyOfLongestPath(src: Param, e: Expr): Option[Int] = {
    assert(e.freeVars().contains(src))
    e match {
      case x: Param if x == src =>
        Some(0)
      case s: StmBuild =>
        s.equations
          .filter({ case (_, (z, _)) => z.freeVars().contains(src) })
          .map({ case (x, _) => x })
          .foldLeft[Option[Option[Int]]](None)({
            // Outer option will be None if we haven't checked any paths yet
            // Inner option will be None if we were unable to find the latency
            // for at least one path
            case (None, x)       => Some(latencyOfLongestPath(src, x, s))
            case (Some(None), _) => Some(None)
            case (Some(Some(lat1)), x) =>
              Some(latencyOfLongestPath(src, x, s).map(math.max(lat1, _)))
          })
          .get // There must be at least one path containing `src`
      case LetStm(_, x, in, out) =>
        if (in.freeVars().contains(src)) {
          // (1) Latency from `src` to `in` plus latency from `x` to `out`
          latencyOfLongestPath(src, in)
            .flatMap({ part1 =>
              latencyOfLongestPath(x, out).map(part2 =>
                // Add 1 to account for the buffer
                part1 + part2 + 1
              )
            })
            // (2) Latency from `src` directly to `out`
            .flatMap(latencyViaIn => {
              if (out.freeVars().contains(src)) {
                latencyOfLongestPath(src, out).map(math.max(latencyViaIn, _))
              } else {
                Some(latencyViaIn)
              }
            })
        } else {
          // Only latency from `src` directly to `out`
          latencyOfLongestPath(src, out)
        }
      case e =>
        logger.warn(
          s"I don't know how to find the latency through an expression of type ${e.getClass.getName}"
        )
        None
    }
  }

  /** Find the latency of the longest path from `src` through `s` via input
    * stream `acc`.
    *
    * The latency is from `src` to the <i>output</i> of `s`.
    *
    * @param s
    *   the end of the path to check.
    * @param acc
    *   the input stream via which to measure the latency.
    * @param src
    *   the beginning of the path.
    * @return
    *   `Some(latency)` if the latency can be calculated statically; otherwise
    *   `None`.
    */
  private def latencyOfLongestPath(
      src: Param,
      acc: Param,
      s: StmBuild
  ): Option[Int] = {
    val inputStm = s.seedByVar(acc)
    latencyOfLongestPath(src, inputStm)
      .flatMap(before => latency(src, acc, s).map(thru => before + thru))
  }

  /** Finds the latency that the given [[mhir.ir.StmBuild]] contributes to the
    * path from `src` via `acc`.
    *
    * In other words, this is the latency of the longest path from `src` through
    * `s` via `acc`, minus the latency of the longest path from `src` to `acc`.
    */
  private def latency(
      src: Param,
      acc: Param,
      s: StmBuild
  ): Option[Int] = {
    val lat = s.valid match {
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
      case _ =>
        None
    }
    logger.warn(s"failed to find latency from $src through StmBuild")
    logger.trace(s"sbuild: $s")
    lat
  }

  private def increaseLatencyTo(
      e: Expr,
      src: Param,
      targetLatency: Int
  ): Expr = {
    if (targetLatency == 0) {
      e
    } else {
      e match {
        case x: Param if x == src =>
          val TyStm(t, n) = x.typ
          val s = Param("s")(TyStm(t, -1))
          StmBuild(
            n,
            StmData(s)(),
            True,
            Map[Param, (Expr, Expr)](
              s -> (increaseLatencyTo(x, src, targetLatency - 1), True)
            )
          )()
        case x: Param =>
          x
        case s: StmBuild =>
          val newEquations = s.equations
            .map({
              case (x, (z, ready)) if z.freeVars().contains(src) =>
                val newTarget = latency(src, x, s).map(targetLatency - _).get
                x -> (increaseLatencyTo(z, src, newTarget), ready)
              case eqn => eqn
            })
          StmBuild(
            n = s.n,
            data = s.data,
            valid = s.valid,
            equations = newEquations
          )()
        case LetStm(_, x, in, out) =>
          val newIn = latencyOfLongestPath(x, out) match {
            case Some(lat) =>
              increaseLatencyTo(in, src, targetLatency - lat - 1)
            case None =>
              in
          }
          val newOut = increaseLatencyTo(out, src, targetLatency)
          val TyStm(_, n) = in.typ
          LetStm(n, x, newIn, newOut)()
        case _ =>
          ???
      }
    }
  }
}

object DisabledStmLatencyMatcher extends StmLatencyMatcher {
  override def enabled: Boolean = false

  override def matchLatencies(e: Expr): Expr = e
}
