package mhir.optimize

import mhir.ir._
import mhir.optimize.{BinOpTreeMaker => BOTM}

/** Top-level optimizer.
  */
object Optimizer {
  def optimize(s: Expr): Expr = {
    val s0 = SafeSimplifier.simplify(s)
    val s1 = LetStmMover.moveUp(s0)
    val s2 = StmLatencyMatcher.matchLatencies(s1)
    BOTM.makeBinOpTrees(s2)
  }
}
