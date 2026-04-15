package mhir

import mhir.ir._

import scala.language.implicitConversions

/** Syntax sugar for common arithmetic operations (e.g., [[Min]]), vector
  * operations (e.g., [[VecMap]]), and stream operations (e.g., [[StmMap]]).
  */
package object sugar
    extends mhir.sugar.Lowering
    with SugaryExprUtils
    with StmLiteralUtils {

  /** Implicitly converts an integer to a [[SugaryExprUtilsImplicit]] so that
    * shorthands like [[SugaryExprUtilsImplicit.+]] can be used.
    */
  implicit def int2SugaryExprOps(i: Int): SugaryExprUtilsImplicit = {
    new SugaryExprUtilsImplicit(C(i)())
  }
}
