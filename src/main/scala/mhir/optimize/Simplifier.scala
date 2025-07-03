package mhir.optimize

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.optimize.{PartialEvalPass => PE}

object Simplifier {
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
