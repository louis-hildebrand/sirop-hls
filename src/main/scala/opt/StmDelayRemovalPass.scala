package opt

import ir._

object StmDelayRemovalPass {
  def skipFirstCycles(stm: StmBuild, c: Expr)(
      facts: FactSet = FactSet()
  ): StmBuild = {
    // Necessary conditions:
    //  (1) Need to know the value of the accumulator after c cycles
    //  (2) For the first c cycles, there is no valid output
    val indVarRemover = StmInductionVarRemovalPass(facts)
    indVarRemover.tryFindAccumulatorAtTime(stm, c) match {
      case None =>
        stm
      case Some(newSeed) =>
        indVarRemover.tryFindClosedFormForOutput(stm) match {
          case None => stm
          case Some(Function(t, e)) =>
            val facts = FactSet().between(t, 0, c)
            val noOutputInFirstCycles =
              PartialEvalPass.partialEval(IsNone(e))(facts) == True
            if (noOutputInFirstCycles) {
              StmBuild(stm.length, newSeed, stm.nextF)
            } else {
              stm
            }
        }
    }
  }
}
