package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.logging.time
import mhir.optimize.{LatencyAnalysis => LA}
import org.slf4j.event.Level

trait StmLatencyMatcher {
  def enabled: Boolean
  def disabled: Boolean = !enabled

  def matchLatencies(e: Expr): Expr
}

object StmLatencyMatcher {
  def apply(enabled: Boolean = true): StmLatencyMatcher = {
    if (enabled) {
      new EnabledStmLatencyMatcher(LetStmMover)
    } else {
      DisabledStmLatencyMatcher
    }
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

  private implicit val logger: Logger = Logger(getClass.getName)

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
    time("latency matching", Level.DEBUG) {
      // There may be some strange things happening after the joins, but that
      // shouldn't prevent latency matching because the strange things
      // contribute the same latency to all paths.
      val e1 = letStmMover.moveDown(e)
      val result = doMatchLatencies(e1)
      val checkedResult = result.tchk()
      assert(checkedResult.typ ~= e.typ)
      checkedResult
    }
  }

  private def doMatchLatencies(e: Expr): Expr = {
    val result = e match {
      case s: StmBuild =>
        val newEquations = s.equations
          .map({
            case (x, (z, next)) if x.typ.isInstanceOf[TyStm] =>
              x -> (doMatchLatencies(z), next)
            case eqn => eqn
          })
        StmBuild(
          n = s.n,
          data = s.data,
          valid = s.valid,
          equations = newEquations
        )()
      case LetStm(_, x, in, out) =>
        val in1 = doMatchLatencies(in).tchk()
        val out1 = doMatchLatencies(out).tchk()
        val out2 = LA.latencyOfLongestPath(x, out1) match {
          case Some(lat) => increaseLatencyTo(out1, x, lat)
          case None      => out1
        }
        // Changing the latencies may actually increase the required
        // buffer size, so reset it to the max value.
        val TyStm(_, n) = in1.typ
        LetStm(n, x, in1, out2)()
      case Function(x, body) if body.typ.isInstanceOf[TyStm] =>
        Function(x, doMatchLatencies(body))()
      case _ =>
        e
    }
    val checkedResult = result.tchk()
    assert(checkedResult.typ ~= e.typ)
    checkedResult
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
                val newTarget = LA.latency(src, x, s).map(targetLatency - _).get
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
          val newIn = LA.latencyOfLongestPath(x, out) match {
            case Some(lat) =>
              increaseLatencyTo(in, src, targetLatency - lat - LA.LetStmLatency)
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

  private val logger: Logger = Logger(getClass.getName)
  private var hasLogged: Boolean = false

  override def enabled: Boolean = false

  override def matchLatencies(e: Expr): Expr = {
    if (!hasLogged) {
      hasLogged = true
      logger.debug("latency matching is disabled")
    }
    e
  }
}
