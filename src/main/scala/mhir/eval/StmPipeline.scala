package mhir.eval

import mhir.ir._

trait StmPipeline {

  /** Computes the next state of the pipeline.
    */
  def step(): StmPipeline

  // TODO: Should this be combined with the `isStuck` method?
  def reachedFixpoint(that: StmPipeline): Boolean

  def connections: DiGraph[StmNodeId]

  def nodes: Map[StmNodeId, StmNode]

  def sinkId: StmNodeId

  /** The unique node in the pipeline with no consumers, which gives the output
    * of the entire pipeline.
    */
  def sink: StmNode = this.nodes(sinkId)

  /** Whether this pipeline is empty; i.e., has successfully produced the number
    * of outputs that it was supposed to and no longer has valid output.
    */
  def isEmpty: Boolean = this.sink.isEmpty

  /** The reasons for which this pipeline is stuck, if any (see [[isStuck]]).
    */
  def deadlockReasons: Set[DeadlockReason] = this.sink.deadlockReasons

  /** Whether this pipeline is stuck and will no longer produce any output
    * despite (supposedly) being non-empty.
    */
  def isStuck: Boolean = this.deadlockReasons.nonEmpty

  /** The type of the stream produced by this pipeline.
    */
  def typ: Type = this.sink.typ
}

object StmPipeline {

  /** Makes a pipeline representing the given stream expression.
    *
    * @param e
    *   an expression representing a stream ([[StmBuild]], [[LetStm]], etc.).
    */
  def apply(
      e: Expr,
      inputs: Map[Param, Expr],
      handshake: Boolean
  ): StmPipeline = {
    if (handshake) {
      mhir.eval.handshake.StmPipeline(e, inputs)
    } else {
      mhir.eval.nohandshake.StmPipeline(e, inputs)
    }
  }
}
