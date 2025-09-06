package mhir.optimize

import mhir.ir.StreamFuser.StmBuildFusion
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.optimize.cost.{SimpleAreaCostModel, SimpleDelayCostModel}

object GreedyStmFuser {
  def fuse(stm: Expr): Expr = {
    require(stm.hasType)
    val result = stm match {
      case s: StmBuild =>
        val candidates = s.seedByVar.flatMap({
          case (x, _: StmBuild) => Some(x)
          case _                => None
        })
        val withFusedProducers = StmBuild(
          s.n,
          s.data,
          s.valid,
          s.equations.map({
            case (x, (s, ready)) if x.typ.isInstanceOf[TyStm] =>
              x -> (fuse(s), ready)
            case eqn => eqn
          })
        )().tchk().asInstanceOf[StmBuild]
        candidates.foldLeft(withFusedProducers)({ case (acc, x) =>
          val fused =
            SafeSimplifier.simplify(acc.fuseWith(x))().asInstanceOf[StmBuild]
          val oldArea = SimpleAreaCostModel.cost(acc)
          val newArea = SimpleAreaCostModel.cost(fused)
          val oldDelay = SimpleDelayCostModel.cost(acc)
          val newDelay = SimpleDelayCostModel.cost(fused)
          val keep = newArea <= oldArea && newDelay <= oldDelay
          if (keep) fused else acc
        })
      case LetStm(x, in, out) =>
        LetStm(x, fuse(in), fuse(out))()
      case e => e.map(fuse)
    }
    val checkedResult = result.tchk()
    assert(
      checkedResult.typ ~= stm.typ,
      "greedy fusion should preserve the type"
    )
    checkedResult
  }
}
