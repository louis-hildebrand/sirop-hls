package mhir.debug

import mhir.canonicalize._
import mhir.eval._
import mhir.ir._
import mhir.sugar.ExprLowering
import mhir.typecheck._

import scala.annotation.tailrec

/** Provides methods for generating traces showing the state of a pipeline at
  * each cycle.
  */
object Tracer {

  /** Traces execution step-by-step, showing the accumulator values and outputs
    * for all streams.
    *
    * @param s
    *   the streaming accelerator to trace.
    * @param inputs
    *   the inputs to provide to the accelerator.
    * @return
    *   a summary of the stream state and output at each step.
    */
  def traceAll(
      s: Expr,
      inputs: Map[Param, Expr] = Map(),
      maxCycles: Option[Int] = None
  ): Trace = {
    @tailrec
    def trace(
        pipe: StmPipeline,
        steps: Seq[TraceStep],
        maxCycles: Option[Int]
    ): Seq[TraceStep] = {
      val newSteps =
        try {
          ValidTraceStep(pipe) +: steps
        } catch {
          case ex: EvalException =>
            return (ErrorTraceStep(ex) +: steps).reverse
        }
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

    val pipe = StmPipeline(
      s.tchk().lower,
      inputs.map({ case (x, e) => x -> e.tchk().lower })
    )
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
