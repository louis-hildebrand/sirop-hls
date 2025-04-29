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
      case Some(e) =>
        e
    }
  }

  private def toSimplifiedArithExpr(
      e: Expr
  )(facts: FactSet): ae.ArithExpr with ae.SimplifiedExpr = {
    e match {
      case IntCst(n) => ae.Cst(n)
      case Sum(terms @ _*) =>
        val arithTerms = terms.map(e => toSimplifiedArithExpr(e)(facts)).toList
        aes.SimplifySum(arithTerms)
      case Prod(factors @ _*) =>
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
        toSimplifiedArithExpr(IfThenElse(eq, True, False)())(facts)
      case lt: LessThan =>
        // TODO: This is a nasty hack. It would be better if ArithExpr just supported booleans
        toSimplifiedArithExpr(IfThenElse(lt, True, False)())(facts)
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
          case None => BlackBox(e, range = findRange(e)(facts))
        }
      case and: And =>
        // Don't bother looking up a range for this black box, since the
        // operands of And should be booleans, not integers
        BlackBox(simplifyAnd(and))
      case or: Or =>
        // Don't bother looking up a range for this black box, since the
        // operands of And should be booleans, not integers
        BlackBox(simplifyOr(or))
      case not: Not =>
        BlackBox(simplifyNot(not))
      case e =>
        BlackBox(e, range = findRange(e)(facts))
    }
  }

  private def findRange(e: Expr)(facts: FactSet): ae.Range = {
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
  }

  private def fromArithExpr(a: ae.ArithExpr): Option[Expr] = {
    a match {
      case ae.Cst(c) => Some(IntCst(c.toInt))
      case ae.Sum(terms) =>
        val exprTerms = terms.map(fromArithExpr)
        if (exprTerms.forall(e => e.isDefined)) {
          Some(Sum(exprTerms.map(e => e.get): _*)())
        } else {
          None
        }
      case ae.Prod(factors) =>
        val exprFactors = factors.map(fromArithExpr)
        if (exprFactors.forall(e => e.isDefined)) {
          Some(Prod(exprFactors.map(e => e.get): _*)())
        } else {
          None
        }
      case ae.IntDiv(n, d) =>
        (fromArithExpr(n), fromArithExpr(d)) match {
          case (Some(n), Some(d)) => Some(Div(n, d)())
          case _                  => None
        }
      case ae.Mod(dividend, divisor) =>
        (fromArithExpr(dividend), fromArithExpr(divisor)) match {
          case (Some(dividend), Some(divisor)) => Some(Mod(dividend, divisor)())
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
            val cond = simplifyBoolExpr(c.op match {
              case ae.Predicate.Operator.<  => lhs < rhs
              case ae.Predicate.Operator.>  => lhs > rhs
              case ae.Predicate.Operator.<= => lhs <= rhs
              case ae.Predicate.Operator.>= => lhs >= rhs
              case ae.Predicate.Operator.!= => lhs !== rhs
              case ae.Predicate.Operator.== => lhs === rhs
            })
            // TODO: This is a nasty hack. It would be better if ArithExpr just supported booleans
            (t, f) match {
              case (False, False) => Some(False)
              case (False, True)  => Some(Not(cond)())
              case (True, False)  => Some(cond)
              case (True, True)   => Some(True)
              case (t, f)         => Some(IfThenElse(cond, t, f)())
            }
          case _ => None
        }
      //      case AbsFunction(ae)                   => ???
      //      case FloorFunction(ae)                 => ???
      //      case CeilingFunction(ae)               => ???
      case BlackBox(e, _) =>
        Some(e)
      case _ => None
    }
  }

  private def simplifyBoolExpr(e: Expr): Expr = {
    e.rebuild(e.typ, e.children.map(e => simplifyBoolExpr(e))) match {
      case eq: Equal => simplifyEqual(eq)
      case and: And  => simplifyAnd(and)
      case or: Or    => simplifyOr(or)
      case not: Not  => simplifyNot(not)
      case e         => e
    }
  }

  private def simplifyEqual(eq: Equal): Expr = {
    eq match {
      case Equal(c, True)  => c
      case Equal(True, c)  => c
      case Equal(c, False) => Not(c)()
      case Equal(False, c) => Not(c)()
      case _               => eq
    }
  }

  private def simplifyAnd(and: And): Expr = {
    and.remove(True) match {
      case And(terms @ _*) if terms.contains(False) =>
        False
      // TODO: Generalize these rules by looking for pairs of terms (a, b) such that a ==> b or a ==> !b ?
      case And(terms @ _*) if hasContradictoryTerms(terms) =>
        False
      case And(LessThan(e1, IntCst(c1)), LessThan(e2, IntCst(c2)))
          if e1 == e2 =>
        LessThan(e1, math.min(c1, c2))()
      case And(LessThan(IntCst(c1), e1), LessThan(IntCst(c2), e2))
          if e1 == e2 =>
        LessThan(math.max(c1, c2), e1)()
      case e => e
    }
  }

  private def simplifyOr(or: Or): Expr = {
    or.remove(False) match {
      case Or(terms @ _*) if terms.contains(True) =>
        True
      // TODO: Generalize these rules by looking for pairs of terms (a, b) such that a ==> b or a ==> !b ?
      case Or(terms @ _*) if hasContradictoryTerms(terms) =>
        True
      case Or(LessThan(e1, IntCst(c1)), LessThan(e2, IntCst(c2))) if e1 == e2 =>
        LessThan(e1, math.max(c1, c2))()
      case Or(LessThan(IntCst(c1), e1), LessThan(IntCst(c2), e2)) if e1 == e2 =>
        LessThan(math.min(c1, c2), e1)()
      case e => e
    }
  }

  private def simplifyNot(not: Not): Expr = {
    not match {
      case Not(True)   => False
      case Not(False)  => True
      case Not(Not(e)) => e
      case Not(And(terms @ _*)) =>
        simplifyOr(Or(terms.map(e => simplifyNot(Not(e)())): _*)())
      case Not(Or(terms @ _*)) =>
        simplifyAnd(And(terms.map(e => simplifyNot(Not(e)())): _*)())
      case _ => not
    }
  }

  private def hasContradictoryTerms(terms: Seq[Expr]): Boolean = {
    terms.exists({
      case Not(e) => terms.contains(e)
      case _      => false
    })
  }
}
