package opt

import ir._

case class FactSet(
    rangeByExpr: Map[Expr, Range] = Map(),
    boolByExpr: Map[Expr, Boolean] = Map()
) {

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
    FactSet(rangeByExpr + (e -> updatedRange), boolByExpr)
  }

  /** <code>x</code> is greater than or equal to <code>y</code>.
    */
  def geq(x: Expr, y: Expr): FactSet = range(x, ScalarRange(Some(y), None))

  /** <code>x</code> is strictly less than <code>y</code>.
    */
  def lt(x: Expr, y: Expr): FactSet = range(x, ScalarRange(None, Some(y)))

  /** <code>e</code> is greater than or equal to <code>lowerInclusive</code> but
    * strictly less than <code>upperExclusive</code>.
    */
  def between(e: Expr, lowerInclusive: Expr, upperExclusive: Expr): FactSet =
    geq(e, lowerInclusive).lt(e, upperExclusive)

  /** Construct a new fact set in which the range of <code>e</code> is entirely
    * unknown.
    */
  def clearRange(e: Expr): FactSet = {
    FactSet(
      rangeByExpr.filter({ case (k, _) => !k.contains(e) }),
      boolByExpr.filter({ case (k, _) => !k.contains(e) })
    )
  }

  /** Check whether <code>e</code> is known to be true.
    *
    * @return
    *   <code>Some(true)</code> if <code>e</code> is definitely true,
    *   <code>Some(false)</code> if <code>e</code> is definitely false, and
    *   <code>None</code> if it is unknown.
    */
  def isTrue(e: Expr): Option[Boolean] = boolByExpr.get(e)

  /** Construct a new fact set taking into account that <code>e</code> evaluates
    * to <code>True</code>.
    */
  def assumeTrue(e: Expr): FactSet = {
    val newFacts = FactSet(rangeByExpr, boolByExpr + (e -> true))
    e match {
      case Not(e) => newFacts.assumeFalse(e)
      // If (x0 && ... && xn) = True, then xi = True for each i
      case And(terms @ _*) =>
        terms.foldLeft(newFacts)({ case (acc, e) => acc.assumeTrue(e) })
      // TODO: Add some more cases?
      case LessThan(e1, e2) =>
        PartialEvalPass.partialEval((e1 - e2).tchk().lower())(newFacts) match {
          case Sum(IntCst(c), x: Param) =>
            // c + x < 0 <==> x < -c
            newFacts.range(x, ScalarRange(None, Some(-c)))
          case Sum(IntCst(c), Prod(IntCst(-1), x: Param)) =>
            // c - x < 0 <==> x > c ==> x >= c + 1
            newFacts.range(x, ScalarRange(Some(c + 1), None))
          case x: Param =>
            // x < 0
            newFacts.range(x, ScalarRange(None, Some(0)))
          case _ => newFacts
        }
      case Equal(x: Param, IntCst(c)) =>
        newFacts.range(x, ScalarRange(Some(c), Some(c + 1)))
      case _ => newFacts
    }
  }

  /** Construct a new fact set taking into account that <code>e</code> evaluates
    * to <code>False</code>.
    */
  def assumeFalse(e: Expr): FactSet = {
    val newFacts = FactSet(rangeByExpr, boolByExpr + (e -> false))
    e match {
      case Not(e) => newFacts.assumeTrue(e)
      // If (x0 || ... || xn) = False, then xi = False for each i
      case Or(terms @ _*) =>
        terms.foldLeft(newFacts)({ case (acc, e) => acc.assumeFalse(e) })
      // TODO: Add some more cases?
      case LessThan(e1, e2) =>
        PartialEvalPass.partialEval((e1 - e2).tchk().lower())(newFacts) match {
          case Sum(IntCst(c), x: Param) =>
            // !(c + x < 0) <==> c + x >= 0 <==> x >= -c
            newFacts.range(x, ScalarRange(Some(-c), None))
          case Sum(IntCst(c), Prod(IntCst(-1), x: Param)) =>
            // !(c - x < 0) <==> c - x >= 0 <==> x <= c <==> x < c + 1
            newFacts.range(x, ScalarRange(None, Some(c + 1)))
          case x: Param =>
            // !(x < 0) <==> x >= 0
            newFacts.range(x, ScalarRange(Some(0), None))
          case _ => newFacts
        }
      case _ => newFacts
    }
  }
}
