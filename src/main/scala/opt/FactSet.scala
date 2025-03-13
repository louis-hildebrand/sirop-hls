package opt

import ir._

case class FactSet(rangeByExpr: Map[Expr, Range] = Map()) {

  /** Update the range information for <code>x</code>.
    *
    * @param e
    *   An expression. This could be a variable, a tuple access
    *   (<code>acc._1</code>), etc. However, it must <i>not</i> be an arithmetic
    *   expression (a sum, a product, etc.).
    * @param r
    *   A range
    * @return
    *   A fact set where the range of <code>x</code> is <code>r</code>.
    */
  def range(e: Expr, r: Range): FactSet = {
    val updatedRange = rangeByExpr.get(e) match {
      case Some(oldRange) => oldRange.merge(r)
      case _              => r
    }
    FactSet(rangeByExpr + (e -> updatedRange))
  }

  /** Construct a new fact set in which the range of <code>e</code> is entirely
    * unknown.
    */
  def clearRange(e: Expr): FactSet = {
    FactSet(rangeByExpr.filter({ case (k, _) => !k.contains(e) }))
  }

  /** Construct a new fact set taking into account that <code>e</code> evaluates
    * to <code>True</code>.
    */
  def isTrue(e: Expr): FactSet = {
    e match {
      case Not(e) => isFalse(e)
      // If (x0 && ... && xn) = True, then xi = True for each i
      case And(terms @ _*) =>
        terms.foldLeft(this)({ case (acc, e) => acc.isTrue(e) })
      // TODO: Add some more cases?
      case LessThan(e1, e2) =>
        PartialEvalPass.partialEval(e1 - e2)(this) match {
          case Sum(Seq(IntCst(c), x: Param)) =>
            // c + x < 0 <==> x < -c
            this.range(x, ScalarRange(None, Some(-c)))
          case Sum(Seq(IntCst(c), Prod(Seq(IntCst(-1), x: Param)))) =>
            // c - x < 0 <==> x > c ==> x >= c + 1
            this.range(x, ScalarRange(Some(c + 1), None))
          case x: Param =>
            // x < 0
            this.range(x, ScalarRange(None, Some(0)))
          case _ => this
        }
      case Equal(x: Param, IntCst(c)) =>
        this.range(x, ScalarRange(Some(c), Some(c + 1)))
      case _ => this
    }
  }

  /** Construct a new fact set taking into account that <code>e</code> evaluates
    * to <code>False</code>.
    */
  def isFalse(e: Expr): FactSet = {
    e match {
      case Not(e) => isTrue(e)
      // If (x0 || ... || xn) = False, then xi = False for each i
      case Or(terms @ _*) =>
        terms.foldLeft(this)({ case (acc, e) => acc.isFalse(e) })
      // TODO: Add some more cases?
      case LessThan(e1, e2) =>
        PartialEvalPass.partialEval(e1 - e2)(this) match {
          case Sum(Seq(IntCst(c), x: Param)) =>
            // !(c + x < 0) <==> c + x >= 0 <==> x >= -c
            this.range(x, ScalarRange(Some(-c), None))
          case Sum(Seq(IntCst(c), Prod(Seq(IntCst(-1), x: Param)))) =>
            // !(c - x < 0) <==> c - x >= 0 <==> x <= c <==> x < c + 1
            this.range(x, ScalarRange(None, Some(c + 1)))
          case x: Param =>
            // !(x < 0) <==> x >= 0
            this.range(x, ScalarRange(Some(0), None))
          case _ => this
        }
      case _ => this
    }
  }
}
