package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.logging.time
import mhir.typecheck.TypeCheck
import org.slf4j.event.Level

class StaticLetStmBufferShrinker(
    latencyAnalysis: LatencyAnalysis,
    private val handshake: Boolean,
    private val assumeThroughputsMatch: Boolean
) extends LetStmBufferShrinker {
  private implicit val logger: Logger = Logger(getClass.getName)

  override def enabled: Boolean = true

  override def shrinkBuffers(e: Expr): Expr = {
    time("statically shrinking letstm buffers", Level.DEBUG) {
      shrinkBuffers(e, latencyAnalysis.actualLatency(e))
    }
  }

  private def shrinkBuffers(e: Expr, lat: LatencyNode): Expr = {
    val result = e match {
      case Function(x, body) =>
        Function(x, shrinkBuffers(body, lat))()
      case _ =>
        lat match {
          case _: LatencyParam =>
            assert(
              e.isInstanceOf[Param],
              s"expression $e does not correspond to latency node $lat"
            )
            e
          case LatencyStmBuild(_, _, producersLat) =>
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
              .map({
                case (x, (stm, ready)) if x.typ.isInstanceOf[TyStm] =>
                  x -> (shrinkBuffers(stm, producersLat(x)), ready)
                case (x, (z, next)) =>
                  assert(x.typ.isData)
                  x -> (z, next)
              })
            StmBuild(
              n = s.n,
              data = s.data,
              valid = s.valid,
              equations = newEquations
            )()
          case LatencyLetStm(latency, inLat, outLat) =>
            assert(
              e.isInstanceOf[LetStm],
              s"expression $e does not correspond to latency node $lat"
            )
            val LetStm(bufSize, x, in, out) = e
            val in1 = shrinkBuffers(in, inLat)
            val out1 = shrinkBuffers(out, outLat)
            val newBufSize = latency match {
              case Some(_) if !this.handshake =>
                assert(bufSize.typ.isInstanceOf[TyUInt])
                assert(bufSize.typ.asInstanceOf[TyUInt].w >= 1)
                C(1)(bufSize.typ)
              case Some(_) if this.assumeThroughputsMatch =>
                assert(bufSize.typ.isInstanceOf[TyUInt])
                assert(bufSize.typ.asInstanceOf[TyUInt].w >= 1)
                C(1)(bufSize.typ)
              case Some(_) =>
                logger.warn(
                  s"could not shrink buffer for letstm $x = ... because the" +
                    s" handshake protocol is enabled and assumeThroughputsMatch=false"
                )
                bufSize
              case None =>
                logger.warn(
                  s"could not show that latencies are matched for letstm $x = ..."
                )
                bufSize
            }
            LetStm(newBufSize, x, in1, out1)()
        }
    }
    val checkedResult = result.tchk()
    assert(checkedResult.typ ~= e.typ)
    checkedResult
  }
}
