package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

class ManualLetStmBufferShrinker(maxBufSize: Int) extends LetStmBufferShrinker {

  private val logger: Logger = Logger(getClass.getName)

  override def enabled: Boolean = true

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
      case LetStm(bufSize, x, in, out) =>
        val in1 = shrinkBuffers(in)
        val out1 = shrinkBuffers(out)
        val newBufSize = bufSize match {
          case IntCst(oldBufSize) =>
            C(math.min(maxBufSize, oldBufSize))(bufSize.typ)
          case bufSize =>
            logger.warn(
              s"cannot shrink non-constant buffer size $bufSize; leaving it as-is"
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
