package mhir.ir.evaluate

import com.typesafe.scalalogging.Logger
import mhir.ir._

import scala.annotation.tailrec

object CycleCounter {

  private implicit val logger: Logger = Logger(getClass.getName)

  def count(s: Expr, maxCycles: Option[Int] = None): Option[Int] = {
    @tailrec
    def countCycles(
        pipe: StmPipeline,
        t: Int,
        maxCycles: Option[Int]
    ): Option[Int] = {
      maxCycles match {
        case Some(tMax) if tMax <= t =>
          logger.warn(s"reached max cycle count ($tMax cycles)")
          return None
        case _ => ()
      }
      if (pipe.isEmpty) {
        return Some(t)
      }
      if (pipe.isStuck) {
        logger.error("pipeline got stuck")
        return None
      }
      val newPipe =
        try {
          pipe.step()
        } catch {
          case ex: EvalException =>
            logger.error(s"an error occurred during evaluation: $ex")
            return None
        }
      if (newPipe.sameState(pipe)) {
        logger.error(s"pipeline reached fixpoint")
        return None
      }
      countCycles(newPipe, t + 1, maxCycles)
    }

    val pipe = StmPipeline(s)
    try {
      countCycles(pipe, t = 0, maxCycles)
        // Subtract one for the last step, which shows the empty pipeline
        .map(_ - 1)
    } catch {
      case _: EvalException => None
    }
  }
}
