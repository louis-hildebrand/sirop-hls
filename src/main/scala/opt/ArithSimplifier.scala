package opt

import ir._
import lift.{arithmetic => ae}
import lift.arithmetic.{simplifier => aes}

import scala.collection.mutable

object ArithSimplifier {

  /** An `ArithExpr` containing an expression that cannot be translated to any
    * other kind of `ArithExpr` (e.g., a vector access).
    *
    * @param e
    *   The expression inside the black box
    * @param range
    *   The range for the expression
    */
  private case class BlackBox(
      e: Expr,
      override val range: ae.Range = ae.RangeUnknown
  ) extends ae.ExtensibleVar("", ae.RangeUnknown, Some(BlackBox.identify(e)))
      with ae.SimplifiedExpr {
    override def copy(r: ae.Range): BlackBox = {
      BlackBox(e, r)
    }

    override def cloneSimplified(): BlackBox = {
      BlackBox(e, range)
    }

    override def visitAndRebuild(
        f: ae.ArithExpr => ae.ArithExpr
    ): ae.ArithExpr = {
      f(BlackBox(e, range.visitAndRebuild(f)))
    }
  }
  private object BlackBox {

    /** Choose a unique identifier for the given expression. The identifier is
      * chosen such that `identify(e1) == identify(e2)` iff `e1 == e2`. In other
      * words, if another instance of the same expression (as determined by
      * `equals` and `hashCode`) has previously been put in a BlackBox (and has
      * not been garbage collected), then the same ID will be returned.
      * Otherwise, the chosen ID will be different from all other
      * previously-chosen IDs.
      *
      * @param e
      *   An expression
      * @return
      *   A unique identifier for the given expression
      */
    def identify(e: Expr): Long = {
      idByExpr.get(e) match {
        case None =>
          val id = getFreshId()
          idByExpr.update(e, id)
          id
        case Some(id) => id
      }
    }

    /** Store previously-generated IDs so that I can return the same one later.
      * use a `WeakHashMap` because, if an expression is garbage collected, it
      * presumably doesn't matter if the ID changes. This seems like a nasty
      * hack, but the `id` is part of the public interface for `ExtensibleVar`
      * so `BlackBox` must have one.
      */
    private val idByExpr = mutable.WeakHashMap[Expr, Long]()

    private var nextId: Long = 0
    private def getFreshId(): Long = {
      nextId += 1
      nextId
    }
  }

  def simplifyArithmetic(e: Expr): Expr = {
    fromArithExpr(toArithExpr(e)) match {
      case None    => e
      case Some(e) => e
    }
  }

  private def toArithExpr(e: Expr): ae.ArithExpr with ae.SimplifiedExpr = {
    e match {
      case DontCare  => ae.?
      case IntCst(n) => ae.Cst(n)
      case Sum(terms) =>
        val arithTerms = terms.map(toArithExpr).toList
        aes.SimplifySum(arithTerms)
      case Prod(factors) =>
        val arithFactors = factors.map(toArithExpr).toList
        aes.SimplifyProd(arithFactors)
      case Div(n, d) => ae.IntDiv(toArithExpr(n), toArithExpr(d))
      case Mod(n, d) => ae.Mod(toArithExpr(n), toArithExpr(d))
      //      case IfThenElse(c, t, f) => ???
      case e => new BlackBox(e)
    }
  }

  private def fromArithExpr(a: ae.ArithExpr): Option[Expr] = {
    a match {
      case ae.?      => Some(DontCare)
      case ae.Cst(c) => Some(IntCst(c.toInt))
      case ae.Sum(terms) =>
        val exprTerms = terms.map(fromArithExpr)
        if (exprTerms.forall(e => e.isDefined)) {
          Some(new Sum(exprTerms.map(e => e.get)))
        } else {
          None
        }
      case ae.Prod(factors) =>
        val exprFactors = factors.map(fromArithExpr)
        if (exprFactors.forall(e => e.isDefined)) {
          Some(new Prod(exprFactors.map(e => e.get)))
        } else {
          None
        }
      case ae.IntDiv(n, d) =>
        (fromArithExpr(n), fromArithExpr(d)) match {
          case (Some(n), Some(d)) => Some(Div(n, d))
          case _                  => None
        }
      case ae.Mod(dividend, divisor) =>
        (fromArithExpr(dividend), fromArithExpr(divisor)) match {
          case (Some(dividend), Some(divisor)) => Some(Mod(dividend, divisor))
          case _                               => None
        }
      //      case AbsFunction(ae)                   => ???
      //      case FloorFunction(ae)                 => ???
      //      case CeilingFunction(ae)               => ???
      //      case arithmetic.IfThenElse(test, t, e) => ???
      case BlackBox(e, _) => Some(e)
      case _              => None
    }
  }
}
