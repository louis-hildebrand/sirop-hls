package mhir.canonicalize

import mhir.ir.typecheck.TypeCheck
import mhir.ir.{Canonicalizer, Expr}
import mhir.optimize.{PartialEvalPass => PE}
import mhir.sugar.ExprLowering

/** A [[mhir.ir.Canonicalizer]] that works by partially evaluating the
  * expression.
  */
object SimplifyingCanonicalizer extends Canonicalizer {
  override def canonicalize(n: Expr): Expr = {
    PE.partialEval(n.tchk().lower())
  }

  override def sameLen(n1: Expr, n2: Expr): Boolean = {
    PE.isEqual(n1.tchk(), n2.tchk())().getOrElse(false)
  }
}
