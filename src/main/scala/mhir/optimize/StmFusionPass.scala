package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.optimize.StreamFuser.StmBuildFusion
import mhir.optimize.cost.SimpleDelayCostModel
import mhir.typecheck.TypeCheck

/** Stream fusion combines producer and consumer into a single
  * [[mhir.ir.StmBuild]].
  *
  * This eliminates the overhead from the handshake interface and may reveal
  * further optimization opportunities. It may increase combinational delay,
  * which is why a cost model is needed to decide when to apply fusion.
  */
trait StmFusionPass {

  /** Brief, user-readable summary of the fusion strategy.
    */
  def strategy: String

  def fuse(stm: Expr): Expr
}

object StmFusionPass {
  def apply(
      simplifier: StmBuildSimplifier,
      delayCostModel: SimpleDelayCostModel,
      handshake: Boolean,
      enabled: Boolean = true
  ): StmFusionPass = {
    if (enabled) new GreedyStmFusionPass(simplifier, delayCostModel)
    else if (!handshake) StmSourceFusionPass(simplifier)
    else DisabledStmFusionPass
  }
}

class GreedyStmFusionPass(
    simplifier: StmBuildSimplifier,
    delayCostModel: SimpleDelayCostModel
) extends StmFusionPass {

  override def strategy: String = "greedy"

  override def fuse(stm: Expr): Expr = {
    require(stm.hasType)
    val result = stm match {
      case s: StmBuild =>
        val candidates = s.producers
          .collect({ case (x, (_: StmBuild, _, _)) => x })
        val withFusedProducers = s
          .mapProducers({ case (x, (s, ready, delay)) =>
            x -> (fuse(s), ready, delay)
          })
          .tchk()
          .asInstanceOf[StmBuild]
        candidates.foldLeft(withFusedProducers)({ case (acc, x) =>
          val fused = simplifier.simplify(acc.fuseWith(x), skipConst = true)()
          val oldDelay = delayCostModel.cost(acc)
          val newDelay = delayCostModel.cost(fused)
          val keep = newDelay <= oldDelay
          if (keep) fused else acc
        })
      case LetStm(_, x, in, out) =>
        // Fusion may change the latency along the different branches, so reset
        // the buffer size to the worst-case value
        val TyStm(_, inLen) = in.typ
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

case class StmSourceFusionPass(simplifier: StmBuildSimplifier)
    extends StmFusionPass {

  override def strategy: String = "only internal streams sources"

  override def fuse(stm: Expr): Expr = {
    stm.requireType("fusion")
    val result = stm match {
      case s: StmBuild =>
        val withFusedProducers = s
          .mapProducers({ case (x, (s, ready, delay)) =>
            x -> (fuse(s), ready, delay)
          })
          .tchk()
          .asInstanceOf[StmBuild]
        val toFuseWith = withFusedProducers.producers
          .collect({ case (x, (stm, _, _)) if isSource(stm) => x })
        toFuseWith.foldLeft(withFusedProducers)({ case (acc, x) =>
          simplifier.simplify(acc.fuseWith(x), skipConst = true)()
        })
      case LetStm(_, x, in, out) =>
        val newIn = this.fuse(in)
        if (isSource(newIn)) {
          this.fuse(out.subPreserveType(x -> newIn))
        } else {
          val newOut = this.fuse(out)
          // Fusion may change the latency along the different branches, so reset
          // the buffer size to the worst-case value
          val TyStm(_, inLen) = in.typ
          LetStm(inLen, x, newIn, newOut)()
        }
      case e => e.map(fuse)
    }
    val checkedResult = result.tchk()
    assert(checkedResult.typ ~= stm.typ, "fusion should preserve the type")
    checkedResult
  }

  private def isSource(e: Expr): Boolean = {
    e match {
      case s: StmBuild => s.producers.isEmpty
      case _           => false
    }
  }
}

object DisabledStmFusionPass extends StmFusionPass {

  override def strategy: String = "disabled"

  override def fuse(stm: Expr): Expr = stm
}
