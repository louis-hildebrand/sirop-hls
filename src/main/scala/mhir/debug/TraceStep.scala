package mhir.debug

import mhir.ir.evaluate.{EvalException, StmNodeId, StmPipeline}

/** One time step in a trace.
  */
sealed trait TraceStep

/** A time step in which evaluation proceeded successfully.
  *
  * @param nodes
  *   the state of each node in the pipeline.
  */
case class ValidTraceStep(nodes: Map[StmNodeId, TraceNode]) extends TraceStep

/** Companion object for [[ValidTraceStep]].
  */
object ValidTraceStep {

  /** Factory for [[ValidTraceStep]].
    *
    * @param pipe
    *   the stream pipeline whose state to save.
    */
  def apply(pipe: StmPipeline): ValidTraceStep = {
    ValidTraceStep(pipe.nodes.map({ case (id, node) => id -> TraceNode(node) }))
  }
}

/** A time step in which the evaluator encountered an error.
  *
  * @param err
  *   the error that the evaluator encountered.
  */
case class ErrorTraceStep(err: EvalException) extends TraceStep
