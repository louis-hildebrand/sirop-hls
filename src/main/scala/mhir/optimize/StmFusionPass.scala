package mhir.optimize

import StreamFuser.StmBuildFusion
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.optimize.cost.{SimpleAreaCostModel, SimpleDelayCostModel}

/** Stream fusion combines producer and consumer into a single
  * [[mhir.ir.StmBuild]].
  *
  * This eliminates the overhead from the handshake interface and may reveal
  * further optimization opportunities. It may increase combinational delay,
  * which is why a cost model is needed to decide when to apply fusion.
  */
trait StmFusionPass {
  def enabled: Boolean
  def disabled: Boolean = !enabled

  def fuse(stm: Expr): Expr
}

object StmFusionPass {
  def apply(
      simplifier: StmBuildSimplifier,
      enabled: Boolean = true
  ): StmFusionPass = {
    if (enabled) new GreedyStmFusionPass(simplifier)
    else DisabledStmFusionPass
  }
}

class GreedyStmFusionPass(simplifier: StmBuildSimplifier)
    extends StmFusionPass {

  override def enabled: Boolean = true

  override def fuse(stm: Expr): Expr = {
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
          val fused = simplifier.simplify(acc.fuseWith(x), skipConst = true)()
          val oldDelay = SimpleDelayCostModel.cost(acc)
          val newDelay = SimpleDelayCostModel.cost(fused)
          val keep = newDelay <= oldDelay
          if (keep) fused else acc
        })
      case LetStm(_, x, in, out) =>
        val TyStm(_, inLen) = in.typ
        // Fusion may change the latency along the different branches, so reset
        // the buffer size to the worst-case value
        LetStm(inLen, x, fuse(in), fuse(out))()
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

object DisabledStmFusionPass extends StmFusionPass {
  override def enabled: Boolean = false

  override def fuse(stm: Expr): Expr = stm
}
