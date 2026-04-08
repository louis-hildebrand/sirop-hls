package mhir.sugar

import mhir.ir._

trait SugaryExprUtils {

  /** More helper methods and shorthands for common operations related to all
    * [[Expr]]s.
    */
  implicit class SugaryExprUtilsImplicit(expr: Expr) {

    /** See [[SmartSum]].
      */
    def +(that: Expr): Expr = SmartSum(this.expr, that)()

    /** See [[SmartSum]] and [[mhir.sugar.SmartProd]].
      */
    def -(that: Expr): Expr = this.expr + that * -1

    /** See [[SmartProd]].
      */
    def *(that: Expr): Expr = SmartProd(this.expr, that)()

    /** See [[SmartDiv]].
      */
    def /(that: Expr): Expr = SmartDiv(this.expr, that)()

    /** See [[SmartMod]].
      */
    def %(that: Expr): Expr = SmartMod(this.expr, that)()

    /** See [[SmartEqual]].
      */
    def ===(that: Expr): Expr = SmartEqual(this.expr, that)()

    /** See [[SmartEqual]].
      */
    def !==(that: Expr): Expr = !(this.expr === that)

    /** See [[SmartLessThan]].
      */
    def <(that: Expr): Expr = SmartLessThan(this.expr, that)()

    /** See [[SmartLessThan]].
      */
    def <=(that: Expr): Not = !(this.expr > that)

    /** See [[SmartLessThan]].
      */
    def >(that: Expr): Expr = that < this.expr

    /** See [[SmartLessThan]].
      */
    def >=(that: Expr): Expr = that <= this.expr
  }
}
