package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.logging.time
import mhir.optimize.{LatencyAnalysis => LA}
import mhir.typecheck.TypeCheck
import org.slf4j.event.Level

class StaticLetStmBufferShrinker(
    private val assumeThroughputsMatch: Boolean
) extends LetStmBufferShrinker {
  private implicit val logger: Logger = Logger(getClass.getName)

  override def enabled: Boolean = true

  override def shrinkBuffers(e: Expr): Expr = {
    time("statically shrinking letstm buffers", Level.DEBUG) {
      doShrinkBuffers(e)
    }
  }

  private def doShrinkBuffers(e: Expr): Expr = {
    val result = e match {
      case s: StmBuild =>
        val newEquations = s.equations
          .map({
            case (x, (stm, ready)) if x.typ.isInstanceOf[TyStm] =>
              x -> (doShrinkBuffers(stm), ready)
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
      case let @ LetStm(bufSize, x, in, out) =>
        val in1 = doShrinkBuffers(in)
        val out1 = doShrinkBuffers(out)
        val newBufSize = if (LA.allLatenciesMatch(let)) {
          if (this.assumeThroughputsMatch) {
            assert(bufSize.typ.isInstanceOf[TyUInt])
            assert(bufSize.typ.asInstanceOf[TyUInt].w >= 1)
            C(1)(bufSize.typ)
          } else {
            logger.warn(
              s"could not shrink buffer for letstm $x = ... because assumeThroughputsMatch=false"
            )
            bufSize
          }
        } else {
          logger.warn(
            s"could not show that latencies are matched for letstm $x = ...; buffer size will be left at $bufSize"
          )
          bufSize
        }
        LetStm(newBufSize, x, in1, out1)()
      case Function(x, body) =>
        Function(x, doShrinkBuffers(body))()
      case e => e
    }
    val checkedResult = result.tchk()
    assert(checkedResult.typ ~= e.typ)
    checkedResult
  }
}
