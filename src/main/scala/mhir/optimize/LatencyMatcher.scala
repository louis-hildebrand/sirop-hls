package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.logging.time
import mhir.typecheck._
import org.slf4j.event.Level

trait LatencyMatcher {
  def enabled: Boolean
  def disabled: Boolean = !enabled

  def matchLatencies(e: Expr, headByVar: Map[Param, Expr]): Expr
}

object LatencyMatcher {
  def apply(
      latencyAnalysis: LatencyAnalysis,
      handshake: Boolean,
      enabled: Boolean = true
  ): LatencyMatcher = {
    if (enabled) {
      new EnabledLatencyMatcher(latencyAnalysis, handshake = handshake)
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
class EnabledLatencyMatcher(
    latencyAnalysis: LatencyAnalysis,
    handshake: Boolean
) extends LatencyMatcher {

  private implicit val logger: Logger = Logger(getClass.getName)

  override def enabled: Boolean = true

  /** Inserts extra [[mhir.ir.StmBuild]]s in the stream pipeline to try to match
    * the latency across different branches in [[mhir.ir.LetStm]]s.
    *
    * @param e
    *   the stream pipeline to process.
    */
  def matchLatencies(e: Expr, headByParam: Map[Param, Expr]): Expr = {
    time("latency matching", Level.DEBUG) {
      // TODO: move this unwrapping and wrapping out of this method?
      val (inputs, body) = TypeChecker.unwrapTopLevelFunction(e)
      val lat =
        latencyAnalysis.idealLatency(body, inputs.map(_ -> Some(0)).toMap)
      val newBody = matchLatencies(body, lat, headByParam)
      TypeChecker.wrapTopLevelFunction(inputs, newBody)
    }
  }

  private def matchLatencies(
      e: Expr,
      lat: LatencyNode,
      headByVar: Map[Param, Expr]
  ): Expr = {
    lat match {
      case _: LatencySource => e
      case LatencyStmBuild(_, localEpoch, producersLat) =>
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
        s.mapProducers({ case (x, (p0, ready, delayExpr)) =>
          val p = matchLatencies(p0, producersLat(x), headByVar)
          (localEpoch, delayExpr, producersLat(x).latency) match {
            case (Some(localEpoch), IntCst(delay), Some(actualLatency)) =>
              // TODO: be smarter about where to insert the delay. Maybe the bitwidth of the stream will be lower at an earlier point in the pipeline
              val expectedLatency = localEpoch + delay.toInt
              assert(
                expectedLatency >= actualLatency,
                "can't perform latency matching if the current latency is greater than the target latency"
              )
              x -> (
                increaseLatency(
                  p,
                  expectedLatency - actualLatency,
                  findInitData(p, headByVar)
                ),
                ready,
                delayExpr
              )
            case _ =>
              x -> (p, ready, delayExpr)
          }
        }).tchk()
      case LatencyLetStm(_, inLat, outLat) =>
        assert(
          e.isInstanceOf[LetStm],
          s"expression $e does not correspond to latency node $lat"
        )
        val LetStm(bufSize, x, in, out) = e.asInstanceOf[LetStm]
        val newIn = matchLatencies(in, inLat, headByVar)
        val newHeadByVar = headByVar + (x -> findInitData(newIn, headByVar))
        val newOut = matchLatencies(out, outLat, newHeadByVar)
        LetStm(bufSize, x, newIn, newOut)().tchk()
    }
  }

  private def findInitData(e: Expr, headByVar: Map[Param, Expr]): Expr = {
    if (this.handshake) {
      Undefined(Missing)
    } else {
      e match {
        case s: StmBuild => s.initData
        case x: Param =>
          headByVar.get(x) match {
            case Some(e) => e
            case None =>
              logger.warn(
                s"no head specified for input stream '$x'."
                  + " The latency matcher will delay the stream by prepending undefined elements."
                  + s" To dismiss this warning, add 'head($x)=undefined' to the top-level annotations."
                  + " To choose a different value, add the same annotation but using your value instead of undefined."
              )
              Undefined(Missing)
          }
        case LetStm(_, x, in, out) =>
          val inHead = findInitData(in, headByVar)
          findInitData(out, headByVar + (x -> inHead))
        case e =>
          ???
      }
    }
  }

  private def increaseLatency(s: Expr, delay: Int, initData: => Expr): Expr = {
    require(s.typ != Missing)
    if (delay <= 0) {
      s
    } else {
      val TyStm(t, n) = s.typ
      val acc = Param("s")(TyStm(t, -1))
      StmBuild(
        n,
        C(1)(),
        initData,
        StmData(acc)(),
        True,
        accumulators = Map(),
        producers = Map[Param, (Expr, Expr, Expr)](
          acc -> (increaseLatency(s, delay - 1, initData), True, C(0)())
        )
      )().tchk()
    }
  }
}

object DisabledLatencyMatcher extends LatencyMatcher {

  private val logger: Logger = Logger(getClass.getName)
  private var hasLogged: Boolean = false

  override def enabled: Boolean = false

  override def matchLatencies(e: Expr, headByVar: Map[Param, Expr]): Expr = {
    if (!hasLogged) {
      hasLogged = true
      logger.debug("latency matching is disabled")
    }
    e
  }
}
