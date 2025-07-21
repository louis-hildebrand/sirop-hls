package mhir.optimize

import mhir.ir._

/** Top-level optimizer.
  */
object Optimizer {
  def optimize(e: Expr): Expr = {
    val simplified = SafeSimplifier.simplify(e)
    simplified
  }
}
