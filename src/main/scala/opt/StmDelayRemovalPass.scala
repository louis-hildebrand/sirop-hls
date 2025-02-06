package opt

import ir._

object StmDelayRemovalPass {
  def skipFirstCycles(stm: StmBuild, c: Expr): StmBuild = {
    StmInductionVarRemovalPass.tryRemoveAllInductionVars(stm) match {
      case None =>
        stm
      case Some(s) =>
        assert(s.seed.isInstanceOf[Tuple])
        assert(s.seed.asInstanceOf[Tuple].elems.length == 1)
        val a = Param("a")
        val bounds = FactSet().range(a.__0, ScalarRange(None, Some(c)))
        val isTransformationValid =
          PartialEvalPass.partialEval(IsNone(FunCall(s.nextF, a).__1))(
            bounds
          ) == True
        if (isTransformationValid) {
          StmBuild(s.length, Tuple(c), s.nextF)
        } else {
          stm
        }
    }
  }
}
