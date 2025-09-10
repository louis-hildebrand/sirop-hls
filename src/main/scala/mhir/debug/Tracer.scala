package mhir.debug

import mhir.ir._
import mhir.ir.evaluate._

import scala.annotation.tailrec

/** Provides methods for generating traces showing the state of a pipeline at
  * each cycle.
  */
object Tracer {

  /** Traces execution step-by-step, showing the accumulator values and outputs
    * for all streams.
    *
    * @param s
    *   the stream to trace.
    * @return
    *   a summary of the stream state and output at each step.
    */
  def traceAll(s: Expr, maxCycles: Option[Int] = None): Trace = {
    @tailrec
    def trace(
        pipe: StmPipeline,
        steps: Seq[TraceStep],
        maxCycles: Option[Int]
    ): Seq[TraceStep] = {
      val newSteps = ValidTraceStep(pipe) +: steps
      if (maxCycles.contains(0) || pipe.isEmpty || pipe.isStuck) {
        newSteps.reverse
      } else {
        val newPipe =
          try {
            pipe.step()
          } catch {
            case ex: EvalException =>
              return (ErrorTraceStep(ex) +: newSteps).reverse
          }
        if (newPipe.sameState(pipe)) {
          val ex = new DeadlockError(Seq(PipelineFixpoint))
          return (ErrorTraceStep(ex) +: newSteps).reverse
        }
        trace(newPipe, newSteps, maxCycles.map(_ - 1))
      }
    }

    val pipe = StmPipeline(s)
    try {
      Trace(
        structure = pipe.connections,
        sink = pipe.sinkId,
        steps = trace(pipe, Seq(), maxCycles)
      )
    } catch {
      case ex: EvalException =>
        Trace(
          structure = pipe.connections,
          sink = pipe.sinkId,
          steps = Seq(ErrorTraceStep(ex))
        )
    }
  }
}
