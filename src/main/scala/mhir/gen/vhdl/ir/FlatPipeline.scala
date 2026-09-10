package mhir.gen.vhdl.ir

import mhir.ir._

private[vhdl] sealed trait PipelineNode

/** @param out
  *   the name for the output stream.
  */
private[vhdl] case class StmBuildNode(out: Param, s: GenStmBuild)
    extends PipelineNode

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
) {

  def mapSbuilds(f: GenStmBuild => GenStmBuild): FlatPipeline = {
    this.copy(sbuilds = this.sbuilds.map(node => node.copy(s = f(node.s))))
  }

  def usesIpBlocks: Boolean = {
    this.sbuilds.exists({ case StmBuildNode(_, s) =>
      s.intermediates.exists({
        case (_, _: IpBlockInst) => true
        case _                   => false
      })
    })
  }
}
