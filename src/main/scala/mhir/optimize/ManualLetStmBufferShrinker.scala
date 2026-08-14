package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.logging.time
import mhir.typecheck.TypeCheck
import org.slf4j.event.Level

class ManualLetStmBufferShrinker(maxBufSize: Int) extends LetStmBufferShrinker {

  private implicit val logger: Logger = Logger(getClass.getName)

  override def enabled: Boolean = true

  override def shrinkBuffers(e: Expr): Expr = {
    time(s"shrinking all letstm buffers above $maxBufSize", Level.DEBUG) {
      doShrinkBuffers(e)
    }
  }

  private def doShrinkBuffers(e: Expr): Expr = {
    val result = e match {
      case s: StmBuild =>
        StmBuild(
          n = s.n,
          data = s.data,
          valid = s.valid,
          accumulators = s.accumulators,
          producers = s.producers.map({ case (x, (stm, ready)) =>
            x -> (doShrinkBuffers(stm), ready)
          })
        )()
      case LetStm(bufSize, x, in, out) =>
        val in1 = doShrinkBuffers(in)
        val out1 = doShrinkBuffers(out)
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
      case Function(x, body) =>
        Function(x, doShrinkBuffers(body))()
      case e => e
    }
    val checkedResult = result.tchk()
    assert(checkedResult.typ ~= e.typ)
    checkedResult
  }
}
