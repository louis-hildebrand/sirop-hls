package mhir.optimize

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.optimize.{PartialEvalPass => PE}

/** Applies various transformations with the goal of making an expression easier
  * to analyze.
  *
  * This pass doesn't do any exploration, it just applies common sense
  * transformations (e.g., partial evaluation) that should pretty much always be
  * helpful. Note that in some cases this may increase the cost of the hardware
  * (e.g., by increasing the bit width of arithmetic expressions), but it should
  * make expressions easier to analyze.
  */
object SafeSimplifier {
  def simplify(e: Expr): Expr = {
    val pe = PE.partialEval(e)
    simplifyStreams(pe)
  }

  private def simplifyStreams(e: Expr): Expr = {
    val result = e match {
      case s: StmBuild =>
        StmSimplifier.simplify(s)(FactSet())
      case e =>
        e.map(simplifyStreams)
    }
    result.tchk()
  }
}
