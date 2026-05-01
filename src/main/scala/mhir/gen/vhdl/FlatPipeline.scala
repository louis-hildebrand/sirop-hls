package mhir.gen.vhdl

import mhir.ir._

private[vhdl] sealed trait PipelineNode

/** @param out
  *   the name for the output stream.
  */
private[vhdl] case class StmBuildNode(
    out: Param,
    s: StmBuild
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
