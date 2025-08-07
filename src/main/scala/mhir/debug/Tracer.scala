package mhir.debug

import mhir.ir._
import mhir.ir.evaluate._

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
    def trace(
        pipe: StmPipeline,
        steps: Seq[TraceStep],
        maxCycles: Option[Int]
    ): Seq[TraceStep] = {
      val newSteps = ValidTraceStep(pipe) +: steps
      if (maxCycles.contains(0) || pipe.isEmpty || pipe.isStuck) {
        newSteps.reverse
      } else {
        try {
          val newPipe = pipe.step()
          trace(newPipe, newSteps, maxCycles.map(_ - 1))
        } catch {
          case ex: EvalException =>
            (ErrorTraceStep(ex) +: newSteps).reverse
        }
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
