package opt

import ir._
import lift.{arithmetic => ae}
import lift.arithmetic.{simplifier => aes}

import java.util.concurrent.atomic.AtomicLong
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
  case class BlackBox(
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
  object BlackBox {

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
    private def identify(e: Expr): Long = {
      this.synchronized {
        idByExpr.get(e) match {
          case None =>
            val id = idCtr.incrementAndGet()
            idByExpr.update(e, id)
            id
          case Some(id) => id
        }
      }
    }

    /** Store previously-generated IDs so that I can return the same one later.
      * use a `WeakHashMap` because, if an expression is garbage collected, it
      * presumably doesn't matter if the ID changes. This seems like a nasty
      * hack, but the `id` is part of the public interface for `ExtensibleVar`
      * so `BlackBox` must have one.
      */
    private val idByExpr = mutable.WeakHashMap[Expr, Long]()
    private val idCtr = new AtomicLong()
  }

  def simplifyArithmetic(e: Expr)(facts: FactSet): Expr = {
    val a =
      try {
        Some(toSimplifiedArithExpr(e)(facts))
      } catch {
        case _: ArithmeticException => None
      }
    a.flatMap(a => fromArithExpr(a)) match {
      case None => e
      // TODO: This is a nasty hack to deal with LessThan(e1, e2). It would be better if ArithExpr supported booleans
      case Some(IfThenElse(c, True, False)) => c
      case Some(e)                          => e
    }
  }

  private def toSimplifiedArithExpr(
      e: Expr
  )(facts: FactSet): ae.ArithExpr with ae.SimplifiedExpr = {
    e match {
      case DontCare  => ae.?
      case IntCst(n) => ae.Cst(n)
      case Sum(terms) =>
        val arithTerms = terms.map(e => toSimplifiedArithExpr(e)(facts)).toList
        aes.SimplifySum(arithTerms)
      case Prod(factors) =>
        val arithFactors =
          factors.map(e => toSimplifiedArithExpr(e)(facts)).toList
        aes.SimplifyProd(arithFactors)
      case Div(n, d) =>
        aes.SimplifyIntDiv(
          toSimplifiedArithExpr(n)(facts),
          toSimplifiedArithExpr(d)(facts)
        )
      case Mod(n, d) =>
        aes.SimplifyMod(
          toSimplifiedArithExpr(n)(facts),
          toSimplifiedArithExpr(d)(facts)
        )
      case eq: Equal =>
        // TODO: This is a nasty hack. It would be better if ArithExpr just supported booleans
        toSimplifiedArithExpr(IfThenElse(eq, True, False))(facts)
      case lt: LessThan =>
        // TODO: This is a nasty hack. It would be better if ArithExpr just supported booleans
        toSimplifiedArithExpr(IfThenElse(lt, True, False))(facts)
      case IfThenElse(c, t, f) =>
        val pred = c match {
          case LessThan(e1, e2) =>
            Some(
              ae.Predicate(
                toSimplifiedArithExpr(e1)(facts),
                toSimplifiedArithExpr(e2)(facts),
                ae.Predicate.Operator.<
              )
            )
          case Not(LessThan(e1, e2)) =>
            Some(
              ae.Predicate(
                toSimplifiedArithExpr(e1)(facts),
                toSimplifiedArithExpr(e2)(facts),
                ae.Predicate.Operator.>=
              )
            )
          case Equal(e1, e2) =>
            Some(
              ae.Predicate(
                toSimplifiedArithExpr(e1)(facts),
                toSimplifiedArithExpr(e2)(facts),
                ae.Predicate.Operator.==
              )
            )
          case Not(Equal(e1, e2)) =>
            Some(
              ae.Predicate(
                toSimplifiedArithExpr(e1)(facts),
                toSimplifiedArithExpr(e2)(facts),
                ae.Predicate.Operator.!=
              )
            )
          case _ => None
        }
        pred match {
          case Some(p) =>
            aes.SimplifyIfThenElse(
              p,
              toSimplifiedArithExpr(t)(facts),
              toSimplifiedArithExpr(f)(facts)
            )
          case None => BlackBox(e)
        }
      case e =>
        val range =
          facts.rangeByExpr.getOrElse(e, ScalarRange(None, None)) match {
            case ScalarRange(None, None) | _: StmAccRange => ae.RangeUnknown
            case ScalarRange(None, Some(upper)) =>
              ae.GoesToRange(toSimplifiedArithExpr(upper)(facts))
            case ScalarRange(Some(lower), None) =>
              ae.StartFromRange(toSimplifiedArithExpr(lower)(facts))
            case ScalarRange(Some(lower), Some(upper)) =>
              ae.ContinuousRange(
                toSimplifiedArithExpr(lower)(facts),
                toSimplifiedArithExpr(upper)(facts)
              )
          }
        new BlackBox(e, range = range)
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
      case ae.IfThenElse(c, t, f) =>
        (
          fromArithExpr(c.lhs),
          fromArithExpr(c.rhs),
          fromArithExpr(t),
          fromArithExpr(f)
        ) match {
          case (Some(lhs), Some(rhs), Some(t), Some(f)) =>
            val cond = c.op match {
              case ae.Predicate.Operator.<  => lhs < rhs
              case ae.Predicate.Operator.>  => lhs > rhs
              case ae.Predicate.Operator.<= => lhs <= rhs
              case ae.Predicate.Operator.>= => lhs >= rhs
              case ae.Predicate.Operator.!= => lhs !== rhs
              case ae.Predicate.Operator.== => lhs === rhs
            }
            Some(IfThenElse(cond, t, f))
          case _ => None
        }
      //      case AbsFunction(ae)                   => ???
      //      case FloorFunction(ae)                 => ???
      //      case CeilingFunction(ae)               => ???
      case BlackBox(e, _) => Some(e)
      case _              => None
    }
  }
}
