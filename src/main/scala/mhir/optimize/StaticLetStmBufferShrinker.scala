package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.optimize.{LatencyAnalysis => LA}

class StaticLetStmBufferShrinker(
    private val assumeThroughputsMatch: Boolean
) extends LetStmBufferShrinker {
  override def enabled: Boolean = false

  private val logger = Logger(getClass.getName)

  override def shrinkBuffers(e: Expr): Expr = {
    val result = e match {
      case s: StmBuild =>
        val newEquations = s.equations
          .map({
            case (x, (stm, ready)) if x.typ.isInstanceOf[TyStm] =>
              x -> (shrinkBuffers(stm), ready)
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
        val in1 = shrinkBuffers(in)
        val out1 = shrinkBuffers(out)
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
      case Function(x, body) if body.typ.isInstanceOf[TyStm] =>
        Function(x, shrinkBuffers(body))()
      case e => e
    }
    val checkedResult = result.tchk()
    assert(checkedResult.typ ~= e.typ)
    checkedResult
  }
}
