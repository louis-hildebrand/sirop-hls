package mhir.debug

import mhir.ir.DiGraph
import mhir.ir.evaluate._
import os.Path

/** A step-by-step account of the execution of a pipeline.
  *
  * @param structure
  *   a graph showing the nodes in the graph and their connections, which should
  *   not change during execution.
  * @param sink
  *   the node which gives the output of the entire pipeline.
  * @param steps
  *   the state of the pipeline at each time step.
  */
case class Trace(
    structure: DiGraph[StmNodeId],
    sink: StmNodeId,
    steps: Seq[TraceStep]
) {

  /** Converts this trace to JSON.
    */
  def json: String = {
    upickle.default.write(this, indent = 4)(TraceSerialization.traceWriter)
  }

  /** Saves this trace to a file.
    *
    * @param path
    *   the file in which to save the trace.
    */
  def dump(path: Path): Unit = {
    os.write(path, this.json)
  }

  /** Saves this trace to a file in the current working directory.
    *
    * @param path
    *   the name of the file in which to save the trace.
    */
  def dump(path: String): Unit = this.dump(os.pwd / path)
}
