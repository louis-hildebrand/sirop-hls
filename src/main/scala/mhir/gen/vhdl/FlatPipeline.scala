package mhir.gen.vhdl

import mhir.ir._

private[vhdl] sealed trait PipelineNode

/** @param out
  *   the name for the output stream.
  * @param inputLatency
  *   the latency of the inputs to this node. 0 means the inputs are valid
  *   immediately, 1 means the inputs are valid after 1 clock cycle, etc.
  */
private[vhdl] case class StmBuildNode(
    out: Param,
    s: GenStmBuild,
    inputLatency: Option[Int]
) extends PipelineNode

/** @param in
  *   the stream feeding into this node.
  * @param bufSize
  *   the size of the [[mhir.ir.LetStm]] buffer.
  * @param out
  *   the names of the output streams.
  */
private[vhdl] case class LetStmNode(
    in: Param,
    bufSize: Int,
    out: Set[Param]
) extends PipelineNode

private[vhdl] case class FlatPipeline(
    sbuilds: Seq[StmBuildNode],
    lets: Seq[LetStmNode],
    inputs: Set[Param],
    unusedInputs: Set[Param],
    sink: Param
)
