package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.logging.time
import mhir.typecheck.TypeCheck
import org.slf4j.event.Level

trait LatencyMatcher {
  def enabled: Boolean
  def disabled: Boolean = !enabled

  def matchLatencies(e: Expr): Expr
}

object LatencyMatcher {
  def apply(
      latencyAnalysis: LatencyAnalysis,
      enabled: Boolean = true
  ): LatencyMatcher = {
    if (enabled) {
      new EnabledLatencyMatcher(latencyAnalysis)
    } else {
      DisabledLatencyMatcher
    }
  }
}

/** Transformation that tries to match the latency of different nodes (e.g.,
  * branches of a [[mhir.ir.LetStm]], different accelerator inputs).
  *
  * If successful, the transformation may increase resource usage slightly but
  * decrease the initiation interval, which should decrease the total latency
  * and therefore increase the real throughput (i.e., total elements / total
  * cycles).
  */
class EnabledLatencyMatcher(latencyAnalysis: LatencyAnalysis)
    extends LatencyMatcher {

  private implicit val logger: Logger = Logger(getClass.getName)

  override def enabled: Boolean = true

  /** Inserts extra [[mhir.ir.StmBuild]]s in the stream pipeline to try to match
    * the latency across different branches in [[mhir.ir.LetStm]]s.
    *
    * @param e
    *   the stream pipeline to process.
    */
  def matchLatencies(e: Expr): Expr = {
    time("latency matching", Level.DEBUG) {
      val lat = latencyAnalysis.idealLatency(e)
      matchLatencies(e, lat)
    }
  }

  private def matchLatencies(e: Expr, lat: LatencyNode): Expr = {
    e match {
      case Function(x, body) =>
        Function(x, matchLatencies(body, lat))().tchk()
      case _ =>
        lat match {
          case _: LatencyParam =>
            assert(
              e.isInstanceOf[Param],
              s"expression $e does not correspond to latency node $lat"
            )
            e
          case LatencyStmBuild(outLat, selfLat, producersLat) =>
            assert(
              e.isInstanceOf[StmBuild],
              s"expression $e does not correspond to latency node $lat"
            )
            val s = e.asInstanceOf[StmBuild]
            assert(
              s.producers.keySet == producersLat.keySet,
              "stream producers in expression do not match latency node" +
                s" (${s.producers.keySet} vs ${producersLat.keySet})"
            )
            val newEquations = s.equations
              // Recursive call
              .map({
                case (x, (p, ready)) if x.typ.isInstanceOf[TyStm] =>
                  x -> (matchLatencies(p, producersLat(x)), ready)
                case eqn => eqn
              })
              // Insert no-op nodes if needed
              .map({
                case (x, (p, ready)) if x.typ.isInstanceOf[TyStm] =>
                  (outLat, producersLat(x).latency) match {
                    case (Some(outLat), Some(pLat)) =>
                      assert(
                        selfLat.nonEmpty,
                        "missing self latency for sbuild node"
                      )
                      assert(
                        outLat >= pLat + selfLat.get,
                        "consumer's latency is too small"
                      )
                      x -> (increaseLatency(
                        p,
                        outLat - selfLat.get - pLat
                      ), ready)
                    case _ =>
                      x -> (p, ready)
                  }
                case eqn => eqn
              })
            StmBuild(
              s.n,
              s.data,
              s.valid,
              newEquations
            )().tchk()
          case LatencyLetStm(_, inLat, outLat) =>
            assert(
              e.isInstanceOf[LetStm],
              s"expression $e does not correspond to latency node $lat"
            )
            val LetStm(bufSize, x, in, out) = e.asInstanceOf[LetStm]
            val newIn = matchLatencies(in, inLat)
            val newOut = matchLatencies(out, outLat)
            LetStm(bufSize, x, newIn, newOut)().tchk()
        }
    }
  }

  private def increaseLatency(s: Expr, delay: Int): Expr = {
    require(s.typ != Missing)
    if (delay <= 0) {
      s
    } else {
      val TyStm(t, n) = s.typ
      val acc = Param("s")(TyStm(t, -1))
      StmBuild(
        n,
        StmData(acc)(),
        True,
        Map[Param, (Expr, Expr)](
          acc -> (increaseLatency(s, delay - 1), True)
        )
      )().tchk()
    }
  }
}

object DisabledLatencyMatcher extends LatencyMatcher {

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
