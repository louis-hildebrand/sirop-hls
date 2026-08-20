package mhir.debug

import mhir.eval._
import mhir.ir.DiGraph

/** A step-by-step account of the execution of a pipeline.
  *
  * @param structure
  *   a graph showing the nodes in the graph and their connections, which should
  *   not change during execution.
  * @param sink
  *   the ID of the node which gives the output of the entire pipeline.
  * @param steps
  *   the state of the pipeline at each time step.
  */
case class Trace(
    structure: DiGraph[StmNodeId],
    sink: StmNodeId,
    steps: Seq[TraceStep]
)
