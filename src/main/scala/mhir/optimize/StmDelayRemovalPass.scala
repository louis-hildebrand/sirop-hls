package mhir.optimize

import mhir.ir._
import mhir.optimize.{PartialEvalPass => PE}

object StmDelayRemovalPass {
  def skipFirstCycles(stm: StmBuild, cc: Expr)(
      facts: FactSet = FactSet()
  ): StmBuild = {
    val c = PE.partialEval(cc)
    // Necessary conditions:
    //  (1) Need to know the value of the accumulator after c cycles
    //  (2) For the first c cycles, there is no valid output
    val indVarRemover = StmInductionVarRemovalPass(facts)
    indVarRemover.tryFindAccumulatorAtTime(stm, c) match {
      case None =>
        stm
      case Some(seedByVar) =>
        assert(seedByVar.keySet == stm.accVars)
        indVarRemover.tryFindClosedFormForValid(stm) match {
          case None => stm
          case Some(Function(t, e)) =>
            val facts = FactSet().between(t, 0, c)
            val noOutputInFirstCycles = PE.partialEval(e)(facts) == False
            if (noOutputInFirstCycles) {
              val newEquations =
                seedByVar.map({ case (x, z) => x -> (z, stm.nextByVar(x)) })
              StmBuild(stm.n, stm.data, stm.valid, newEquations)()
            } else {
              stm
            }
        }
    }
  }
}
