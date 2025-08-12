package mhir.optimize

import mhir.ir._
import mhir.ir.typecheck.TypeCheck

/** Transformation that tries to match the latency of different branches of a
  * [[mhir.ir.LetStm]].
  *
  * If successful, the transformation may increase resource usage slightly but
  * decrease the initiation interval, which should decrease the total latency
  * and therefore increase the real throughput (i.e., total elements / total
  * cycles).
  */
object StmLatencyMatcher {

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
    val result = e match {
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
      case LetStm(x, in, out) =>
        val out1 = matchLatencies(out)
        latencyOfLongestPath(out1, x) match {
          case Some(lat) =>
            val out2 = increaseLatencyTo(out1, x, lat)
            LetStm(x, in, out2)()
          case None =>
            e
        }
      case Function(x, body) if body.typ.isInstanceOf[TyStm] =>
        Function(x, matchLatencies(body))()
      case _ =>
        e
    }
    val checkedResult = result.tchk()
    assert(checkedResult.typ ~= e.typ)
    checkedResult
  }

  private def latencyOfLongestPath(e: Expr, src: Param): Option[Int] = {
    assert(e.freeVars().contains(src))
    e match {
      case x: Param if x == src =>
        Some(0)
      case s: StmBuild =>
        s.valid match {
          case True =>
            val allReadyTrue = s.equations
              .filter({ case (x, _) => x.typ.isInstanceOf[TyStm] })
              .forall({ case (_, (_, ready)) => ready == True })
            if (allReadyTrue) {
              val latencies = s.equations
                .filter({ case (_, (z, _)) => z.freeVars().contains(src) })
                .map({ case (_, (z, _)) => latencyOfLongestPath(z, src) })
              if (latencies.forall(_.nonEmpty)) {
                Some(latencies.map(_.get).max + 1)
              } else {
                None
              }
            } else {
              None
            }
          case _ =>
            None
        }
      case LetStm(x, in, out) =>
        if (in.freeVars().contains(src)) {
          // (1) Latency from `src` to `in` plus latency from `x` to `out`
          latencyOfLongestPath(in, src)
            .flatMap(part1 => {
              latencyOfLongestPath(out, x).map(part2 =>
                // Add 1 to account for the buffer
                part1 + part2 + 1
              )
            })
            // (2) Latency from `src` directly to `out`
            .flatMap(latencyViaIn => {
              if (out.freeVars().contains(src)) {
                latencyOfLongestPath(out, src).map(math.max(latencyViaIn, _))
              } else {
                Some(latencyViaIn)
              }
            })
        } else {
          // Only latency from `src` directly to `out`
          latencyOfLongestPath(out, src)
        }
      case _ =>
        None
    }
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
        case s: StmBuild =>
          assert(s.valid == True)
          val newEquations = s.equations
            .map({
              case (x, (z, next)) if z.freeVars().contains(src) =>
                assert(next == True)
                x -> (increaseLatencyTo(z, src, targetLatency - 1), next)
              case eqn => eqn
            })
          StmBuild(
            n = s.n,
            data = s.data,
            valid = s.valid,
            equations = newEquations
          )()
        case LetStm(x, in, out) =>
          val newIn = latencyOfLongestPath(out, x) match {
            case Some(lat) =>
              increaseLatencyTo(in, src, targetLatency - lat - 1)
            case None =>
              in
          }
          val newOut = increaseLatencyTo(out, src, targetLatency)
          LetStm(x, newIn, newOut)()
      }
    }
  }
}
