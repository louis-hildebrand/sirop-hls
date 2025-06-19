package opt

import ir._
import opt.{PartialEvalPass => PE}

case class FactSet(
    rangeByExpr: Map[Expr, Range] = Map(),
    boolByExpr: Map[Expr, Boolean] = Map()
) {

  /** Find the range of the given expression.
    *
    * @param e
    *   the expression for which to find the range.
    */
  def getRange(e: Expr): Option[Range] = {
    this.rangeByExpr
      .get(e)
      .orElse(deriveRange(e))
      .orElse(getRangeForType(e.typ))
  }

  /** Find the range of an expression based on the ranges of its children.
    *
    * @param e
    *   the expression for which to find the range.
    */
  private def deriveRange(e: Expr): Option[Range] = {
    e match {
      case ToSigned(e) => getRange(e)
      case PadTo(e, _) => getRange(e)
      // TODO: Add more cases
      case _ => None
    }
  }

  /** Find the range of the given type.
    */
  private def getRangeForType(typ: Type): Option[Range] = {
    typ match {
      case t: TyAnyInt =>
        val min = t.minInt
        val lowerBound =
          if (min.isValidLong) Some(C(min.toLong)()) else None
        val max = t.maxInt + 1
        val upperBound =
          if (max.isValidLong) Some(C(max.toLong)()) else None
        Some(ScalarRange(lowerBound, upperBound))
      case _ => None
    }
  }

  /** Update the range information for <code>e</code>.
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
    val updatedRange = getRange(e) match {
      case Some(oldRange) => oldRange.merge(r)
      case _              => r
    }
    FactSet(rangeByExpr + (e -> updatedRange), boolByExpr)
  }

  /** <code>x</code> is greater than or equal to <code>y</code>.
    */
  def geq(x: Expr, y: Expr): FactSet = {
    // TODO: Add some more cases?
    x match {
      case ToSigned(x) => geq(x, y)
      case PadTo(x, _) => geq(x, y)
      case _           => range(x, ScalarRange(Some(y), None))
    }
  }

  /** <code>x</code> is strictly less than <code>y</code>.
    */
  def lt(x: Expr, y: Expr): FactSet = {
    // TODO: Add some more cases?
    x match {
      case ToSigned(x) => lt(x, y)
      case PadTo(x, _) => lt(x, y)
      case _           => range(x, ScalarRange(None, Some(y)))
    }
  }

  /** <code>e</code> is greater than or equal to <code>lowerInclusive</code> but
    * strictly less than <code>upperExclusive</code>.
    */
  def between(e: Expr, lowerInclusive: Expr, upperExclusive: Expr): FactSet = {
    geq(e, lowerInclusive).lt(e, upperExclusive)
  }

  /** <code>x</code> is exactly equal to <code>c</code>.
    */
  def eq(x: Expr, c: Long): FactSet = {
    geq(x, c).lt(x, c + 1)
  }

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
      case False =>
        throw new IllegalArgumentException(
          s"Attempt to assume that False is True."
        )
      case Not(e) => newFacts.assumeFalse(e)
      // If (x0 && ... && xn) = True, then xi = True for each i
      case And(terms @ _*) =>
        terms.foldLeft(newFacts)({ case (acc, e) => acc.assumeTrue(e) })
      case LessThan(e, IntCst(0)) =>
        e match {
          // ---------- c - x < 0 <==> x > c ==> x >= c + 1 ----------
          case Sum(IntCst(c), Prod(IntCst(-1), x)) => newFacts.geq(x, c + 1)
          // ---------- c + x < 0 <==> x < -c ----------
          case Sum(IntCst(c), x) => newFacts.lt(x, -c)
          // ---------- -x < 0 <==> x > 0 ----------
          case Prod(IntCst(-1), x) => newFacts.geq(x, 1)
          // ---------- x < 0 ----------
          case x => newFacts.lt(x, 0)
        }
      case LessThan(e1, e2) =>
        val diff = PE.partialEval((e1 - e2).tchk().lower())
        assert(diff.typ.isInstanceOf[TyAnyInt], "difference must be an integer")
        val lt = LessThan(diff, C(0)(diff.typ))().tchk()
        newFacts.assumeTrue(lt)
      case Equal(e, IntCst(0)) =>
        e match {
          case Sum(IntCst(c), Prod(IntCst(-1), x)) => newFacts.eq(x, c)
          case Sum(IntCst(c), x)                   => newFacts.eq(x, -c)
          case Prod(IntCst(-1), x)                 => newFacts.eq(x, 0)
          case x                                   => newFacts.eq(x, 0)
        }
      case Equal(e1, e2) if e1.typ.isInstanceOf[TyAnyInt] =>
        val diff = PE.partialEval((e1 - e2).tchk().lower())
        assert(diff.typ.isInstanceOf[TyAnyInt], "difference must be an integer")
        val eq = Equal(diff, C(0)(diff.typ))().tchk()
        newFacts.assumeTrue(eq)
      // ---------- unknown ----------
      case _ => newFacts
    }
  }

  /** Construct a new fact set taking into account that <code>e</code> evaluates
    * to <code>False</code>.
    */
  def assumeFalse(e: Expr): FactSet = {
    val newFacts = FactSet(rangeByExpr, boolByExpr + (e -> false))
    e match {
      case True =>
        throw new IllegalArgumentException(
          s"Attempt to assume that True is False."
        )
      case Not(e) => newFacts.assumeTrue(e)
      // If (x0 || ... || xn) = False, then xi = False for each i
      case Or(terms @ _*) =>
        terms.foldLeft(newFacts)({ case (acc, e) => acc.assumeFalse(e) })
      case LessThan(e, IntCst(0)) =>
        e match {
          // ---------- !(c - x < 0) <==> x <= c <==> x < c + 1 ----------
          case Sum(IntCst(c), Prod(IntCst(-1), x)) => newFacts.lt(x, c + 1)
          // ---------- !(c + x < 0) <==> c + x >= 0 <==> x >= -c ----------
          case Sum(IntCst(c), x) => newFacts.geq(x, -c)
          // ---------- !(-x < 0) <==> !(x > 0) <==> x <= 0 ----------
          case Prod(IntCst(-1), x) => newFacts.lt(x, 1)
          // ---------- !(x < 0) <==> x >= 0 ----------
          case x => newFacts.geq(x, 0)
        }
      case LessThan(e1, e2) =>
        val diff = PE.partialEval((e1 - e2).tchk().lower())
        assert(diff.typ.isInstanceOf[TyAnyInt], "difference must be an integer")
        val lt = LessThan(diff, C(0)(diff.typ))().tchk()
        newFacts.assumeFalse(lt)
      case _ => newFacts
    }
  }
}
