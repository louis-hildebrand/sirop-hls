package mhir.canonicalize

import mhir.ir._
import mhir.optimize.{PartialEvalPass => PE}
import mhir.sugar.ExprLowering
import mhir.typecheck.TypeCheck

/** A [[mhir.ir.Canonicalizer]] that works by partially evaluating the
  * expression.
  */
object SimplifyingCanonicalizer extends Canonicalizer {
  override def canonicalize(n: Expr): Expr = {
    val n1 = n.tchk()
    val n2 = (n1, n1.typ) match {
      case (_: IntCst, _) => n1
      case (_, _: TyUInt) => n1
      case _              => ToUnsigned(n1)().tchk()
    }
    PE.partialEval(n2.tchk().lower())
  }

  override def sameLen(n1: Expr, n2: Expr): Boolean = {
    PE.isEqual(n1.tchk(), n2.tchk())().getOrElse(false)
  }
}
