package mhir.optimize

import mhir.ir._
import mhir.optimize.{BinOpTreeMaker => BOTM}

/** Top-level optimizer.
  */
object Optimizer {
  def optimize(e: Expr): Expr = {
    val simplified = SafeSimplifier.simplify(e)
    BOTM.makeBinOpTrees(simplified)
  }
}
