package opt

import ir._

object StmAccRemovalPass {

  /** Remove unused accumulator elements from the given stream. This pass
    * assumes the stream is already in canonical form (e.g., the accumulator is
    * a flat tuple).
    */
  def removeUnusedElems(stm: StmBuild): StmBuild = {
    // TODO: Repeat this pass until no more elements can be removed?
    stm.seed match {
      case Tuple(elems @ _*) =>
        val indicesToRemove = elems.indices.filter(i => isElemUnused(stm, i))
        StmUtils.removeAccumulatorElemsByIndex(stm, indicesToRemove)
      case _ => stm
    }
  }

  private def isElemUnused(stm: StmBuild, i: Int): Boolean = {
    // TODO: this seems a bit hacky
    try {
      StmUtils.removeAccumulatorElemsByIndex(stm, Seq(i))
      true
    } catch {
      case ElemStillInUseException => false
    }
  }
}
