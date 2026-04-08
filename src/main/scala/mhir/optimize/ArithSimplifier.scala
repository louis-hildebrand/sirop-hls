package mhir.optimize

import lift.arithmetic.{simplifier => aes}
import lift.{arithmetic => ae}
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.sugar._

import java.util.concurrent.atomic.AtomicLong
import scala.annotation.tailrec
import scala.collection.mutable

/** An `ArithExpr` containing an expression that cannot be translated to any
  * other kind of `ArithExpr` (e.g., a vector access).
  *
  * @param e
  *   The expression inside the black box
  * @param range
  *   The range for the expression
  */
private[optimize] case class BlackBox(
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

private[optimize] object BlackBox {

  /** Choose a unique identifier for the given expression. The identifier is
    * chosen such that `identify(e1) == identify(e2)` iff `e1 == e2`. In other
    * words, if another instance of the same expression (as determined by
    * `equals` and `hashCode`) has previously been put in a BlackBox, then the
    * same ID will be returned. Otherwise, the chosen ID will be different from
    * all other previously-chosen IDs.
    *
    * @param e
    *   An expression
    * @return
    *   A unique identifier for the given expression
    */
  private def identify(e: Expr): Long = {
    this.synchronized {
      e match {
        case ToSigned(e) =>
          // We can treat ToSigned(x) as being synonymous with x.
          // ToSigned only changes the type, not the value, and ArithExpr
          // doesn't care about the type.
          identify(e)
        case PadTo(e, _) =>
          // ... likewise, PadTo(x) is equivalent to x from the library's
          // point of view
          identify(e)
        case _ =>
          idByExpr.get(e) match {
            case None =>
              val id = idCtr.incrementAndGet()
              idByExpr.update(e, id)
              id
            case Some(id) => id
          }
      }
    }
  }

  /** Store previously-generated IDs so that I can return the same one later.
    * This seems like a nasty hack, but the `id` is part of the public interface
    * for `ExtensibleVar` so `BlackBox` must have one.
    *
    * Note that it is NOT valid to use [[mutable.WeakHashMap]] here. Even if no
    * references exist to key `e1`, there may still be references to another
    * expression `e2` such that `e2 == e1`.
    */
  private val idByExpr = mutable.HashMap[Expr, Long]()
  private val idCtr = new AtomicLong()
}

private[optimize] object ArithSimplifier {

  def simplifyArithmetic(expr: Expr)(facts: FactSet): Expr = {
    // The ArithExpr library does not support parallel execution due to some
    // uses of global mutable state (e.g., `PerformSimplification`).
    this.synchronized {
      val e = simplifyWithoutLibrary(expr.tchk())
      val a =
        try {
          Some(toSimplifiedArithExpr(e)(facts))
        } catch {
          case _: ArithmeticException => None
        }
      a.flatMap(a => fromArithExpr(a, e.typ)).map(e => unwrapMux(e)) match {
        case None => e
        case Some(newE) =>
          if (e.typ != Missing) {
            assert(
              newE.typ == e.typ,
              s"arithmetic simplification should preserve type annotations (expected ${e.typ}, found ${newE.typ})"
            )
          }
          newE
      }
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
        toSimplifiedArithExpr(Mux(eq, True, False)())(facts)
      case lt: LessThan =>
        // TODO: This is a nasty hack. It would be better if ArithExpr just supported booleans
        toSimplifiedArithExpr(Mux(lt, True, False)())(facts)
      case Not(e) =>
        toSimplifiedArithExpr(Mux(e, False, True)())(facts)
      case Mux(c, t, f) =>
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
        BlackBox(and)
      case or: Or =>
        // Don't bother looking up a range for this black box, since the
        // operands of And should be booleans, not integers
        BlackBox(or)
      case e =>
        BlackBox(e, range = findRange(e)(facts))
    }
  }

  private def findRange(e: Expr)(facts: FactSet): ae.Range = {
    facts.getRange(e).getOrElse(ScalarRange(None, None)) match {
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

  /** Convert a [[ae.ArithExpr]] back to an [[Expr]].
    *
    * @param a
    *   the expression to convert.
    * @param typ
    *   the expected type.
    * @return
    *   [[None]] if the conversion failed, otherwise the converted expression.
    */
  private def fromArithExpr(
      a: ae.ArithExpr,
      typ: Type
  ): Option[Expr] = {
    assert(typ != Missing)
    val result = a match {
      case ae.Cst(c) =>
        try {
          Some(IntCst(c)(typ))
        } catch {
          case _: OverflowException => None
        }
      case ae.Sum(terms) =>
        val exprTerms = terms.map(e => fromArithExpr(e, typ))
        if (exprTerms.forall(e => e.isDefined)) {
          Some(Sum(exprTerms.map(e => e.get): _*)())
        } else {
          None
        }
      case ae.Prod(factors) =>
        val exprFactors = factors.map(e => fromArithExpr(e, typ))
        if (exprFactors.forall(e => e.isDefined)) {
          Some(Prod(exprFactors.map(e => e.get): _*)())
        } else {
          None
        }
      case ae.IntDiv(n, d) =>
        (fromArithExpr(n, typ), fromArithExpr(d, typ)) match {
          case (Some(n), Some(d)) =>
            Some(Div(n, d)())
          case _ => None
        }
      case ae.Mod(dividend, divisor) =>
        (fromArithExpr(dividend, typ), fromArithExpr(divisor, typ)) match {
          case (Some(dividend), Some(divisor)) => Some(Mod(dividend, divisor)())
          case _                               => None
        }
      case ae.IfThenElse(
            // The expressions within the predicate must be black boxes.
            // Otherwise, how can we recover their type?
            ae.Predicate(BlackBox(a, _), BlackBox(b, _), op),
            t,
            f
          ) =>
        (fromArithExpr(t, typ), fromArithExpr(f, typ)) match {
          case (Some(t), Some(f)) =>
            val cond = simplifyBoolExpr(op match {
              case ae.Predicate.Operator.<  => LessThan(a, b)()
              case ae.Predicate.Operator.>  => LessThan(b, a)()
              case ae.Predicate.Operator.<= => Not(LessThan(b, a)())()
              case ae.Predicate.Operator.>= => Not(LessThan(a, b)())()
              case ae.Predicate.Operator.!= => Not(Equal(a, b)())()
              case ae.Predicate.Operator.== => Equal(a, b)()
            })
            (cond, t, f) match {
              case (_, False, False)     => Some(False)
              case (Not(c), False, True) => Some(c)
              case (c, False, True)      => Some(Not(c)())
              case (_, True, False)      => Some(cond)
              case (_, True, True)       => Some(True)
              case (c, t, f)             => Some(Mux(c, t, f)())
            }
          case _ => None
        }
      case BlackBox(e, _) => Some(e)
      case _              => None
    }
    result match {
      case Some(e) =>
        val typedE = e.tchk()
        assert(typedE.typ == typ, s"expected type $typ but found ${typedE.typ}")
        Some(typedE)
      case None => None
    }
  }

  private def unwrapMux(e: Expr): Expr = {
    e match {
      case Mux(c, True, False) =>
        unwrapMux(c)
      case Mux(c, False, True) =>
        Not(unwrapMux(c))()
      case e =>
        e
    }
  }

  private def simplifyWithoutLibrary(e: Expr): Expr = {
    require(e.hasType)
    val simplified = e match {
      case ll: LLShift     => simplifyLLShift(ll)
      case lr: LRShift     => simplifyLRShift(lr)
      case s: Sum          => simplifySum(s)
      case p: Prod         => simplifyProd(p)
      case s: WrappingSum  => simplifyWrappingSum(s)
      case d: WrappingDiff => simplifyWrappingDiff(d)
      case p: WrappingProd => simplifyWrappingProd(p)
      case p: IntFixProd   => simplifyIntFixProd(p)
      case e if e.typ == TyBool =>
        simplifyBoolExpr(e)
      case e =>
        e
    }
    simplified.tchk()
  }

  private def simplifyLLShift(ll: LLShift): Expr = {
    ll match {
      case LLShift(_: IntCst, _: IntCst) =>
        mhir.eval.eval(ll)
      case LLShift(e, IntCst(0)) =>
        // TODO: Should this actually be done after calling the library in
        //       case e2 is simplified to 0 but is not originally 0?
        simplifyWithoutLibrary(e)
      case LLShift(e1, e2) =>
        LLShift(simplifyWithoutLibrary(e1), simplifyWithoutLibrary(e2))().tchk()
    }
  }

  private def simplifyLRShift(lr: LRShift): Expr = {
    lr match {
      case LRShift(_: IntCst, _: IntCst) =>
        mhir.eval.eval(lr)
      case LRShift(e, IntCst(0)) =>
        simplifyWithoutLibrary(e)
      case LRShift(e1, e2) =>
        LRShift(simplifyWithoutLibrary(e1), simplifyWithoutLibrary(e2))().tchk()
    }
  }

  private def simplifySum(sum: Sum): Expr = {
    Sum(
      sum.terms
        .map(simplifyWithoutLibrary)
        // Flatten to represent associativity
        .flatMap({
          case s: Sum => s.terms
          case e      => Seq(e)
        })
        // Sort to represent commutativity
        .sorted(ExprOrdering): _*
    )()
  }

  private def simplifyProd(prod: Prod): Expr = {
    Prod(
      prod.factors
        .map(simplifyWithoutLibrary)
        // Flatten to represent associativity
        .flatMap({
          case p: Prod => p.factors
          case e       => Seq(e)
        })
        // Sort to represent commutativity
        .sorted(ExprOrdering): _*
    )()
  }

  private def simplifyWrappingSum(sum: WrappingSum): Expr = {
    if (sum.terms.forall(_.isInstanceOf[IntCst])) {
      mhir.eval.eval(sum)
    } else {
      val (constants, otherTerms) =
        sum.terms
          .map(simplifyWithoutLibrary)
          // Flatten to represent associativity
          // This is valid because (x % n) + (y % n) == (x + y) % n
          .flatMap({
            case s: WrappingSum => s.terms
            case e              => Seq(e)
          })
          .partition(_.isInstanceOf[IntCst])
      val const = mhir.eval.eval(WrappingSum(constants: _*)())
      val allTerms = if (const == IntCst(0)()) {
        otherTerms
      } else {
        const +: otherTerms
      }
      WrappingSum(allTerms: _*)().tchk()
    }
  }

  private def simplifyWrappingDiff(diff: WrappingDiff): Expr = {
    diff match {
      case WrappingDiff(_: IntCst, _: IntCst) =>
        mhir.eval.eval(diff)
      case _ => diff
    }
  }

  private def simplifyWrappingProd(prod: WrappingProd): Expr = {
    if (prod.factors.forall(_.isInstanceOf[IntCst])) {
      mhir.eval.eval(prod)
    } else {
      val (constants, otherFactors) =
        prod.factors
          .map(simplifyWithoutLibrary)
          // Flatten to represent associativity
          // This is valid because (x % n) * (y % n) == (x * y) % n
          .flatMap({
            case p: WrappingProd => p.factors
            case e               => Seq(e)
          })
          .partition(_.isInstanceOf[IntCst])
      val const = mhir.eval.eval(WrappingProd(constants: _*)())
      if (const == IntCst(0)()) {
        C(0)(prod.typ)
      } else if (const == IntCst(1)()) {
        WrappingProd(otherFactors: _*)().tchk()
      } else {
        WrappingProd(const +: otherFactors: _*)().tchk()
      }
    }
  }

  private def simplifyIntFixProd(prod: IntFixProd): Expr = {
    val newProd = prod.map(simplifyWithoutLibrary).tchk()
    newProd match {
      case IntFixProd(_: IntCst, _: FixCst) =>
        mhir.eval.eval(newProd)
      case IntFixProd(x, FixCst(0)) =>
        C(0)(x.typ)
      case IntFixProd(x @ IntCst(0), _) =>
        C(0)(x.typ)
      case IntFixProd(x, c: FixCst) if isPowerOfTwo(c.numer) =>
        val netRightShift = c.typ.shift - log2(c.numer)
        if (netRightShift > 0) {
          LRShift(x, netRightShift)().tchk()
        } else if (netRightShift < 0) {
          LLShift(x, -netRightShift)().tchk()
        } else {
          x
        }
      case e => e
    }
  }

  /** Decides whether the given number is a power of two.
    */
  private def isPowerOfTwo(n: Long): Boolean = {
    // https://stackoverflow.com/a/19383296
    //
    // Positive example:
    //   n     : 00010000
    //   n - 1 : 00001111
    //   &     : 00000000
    //
    // Negative example:
    //   n     : 00010001
    //   n - 1 : 00010000
    //   &     : 00010000
    (n > 0) && ((n & (n - 1)) == 0)
  }

  /** Finds the log base 2 of the given number, <i>which must be a power of
    * two</i>.
    *
    * @param n
    *   a power of two.
    * @return
    *   `shift` such that `n == (1 << shift)`.
    */
  private def log2(n: Long): Int = {
    @tailrec
    def loop(n: Long, shift: Int): Int = {
      if (n == 0) shift else loop(n >>> 1, shift + 1)
    }
    loop(n >>> 1, 0)
  }

  private def simplifyBoolExpr(e: Expr): Expr = {
    val out = e.rebuild(e.typ, e.children.map(e => simplifyBoolExpr(e))) match {
      case eq: Equal    => simplifyEqual(eq)
      case lt: LessThan => simplifyLessThan(lt)
      case and: And     => simplifyAnd(and)
      case or: Or       => simplifyOr(or)
      case not: Not     => simplifyNot(not)
      case e            => e
    }
    out.tchk()
  }

  @tailrec
  private def simplifyEqual(eq: Equal): Expr = {
    eq.tchk() match {
      // TODO: generalize by rewriting to (a && b) || (!a && !b) ?
      case Equal(c, True)  => c.tchk()
      case Equal(True, c)  => c.tchk()
      case Equal(c, False) => (!c).tchk()
      case Equal(False, c) => (!c).tchk()
      case Equal(ToSignedOrIntCst(x), ToSignedOrIntCst(y)) =>
        simplifyEqual(Equal(x, y)())
      case Equal(PadOrIntCst(x), PadOrIntCst(y)) =>
        // If both sides are padded, the conversions are unnecessary and can
        // be stripped away
        val lhsW = x.typ.asInstanceOf[TyAnyInt].w
        val rhsW = y.typ.asInstanceOf[TyAnyInt].w
        val w = math.max(lhsW, rhsW)
        val newLhs = IntConversionMover.widen(PadTo(x, w)())
        val newRhs = IntConversionMover.widen(PadTo(y, w)())
        simplifyEqual(Equal(newLhs, newRhs)())
      case Equal(TruncateTo(x, _), y) =>
        val w = x.typ.asInstanceOf[TyAnyInt].w
        val newRhs = IntConversionMover.widen(PadTo(y, w)())
        simplifyEqual(Equal(x, newRhs)())
      case Equal(x, TruncateTo(y, _)) =>
        val w = y.typ.asInstanceOf[TyAnyInt].w
        val newLhs = IntConversionMover.widen(PadTo(x, w)())
        simplifyEqual(Equal(newLhs, y)())
      case Equal(ToUnsigned(x), y) =>
        val newRhs = IntConversionMover.widen(ToSigned(y)())
        simplifyEqual(Equal(x, newRhs)())
      case Equal(x, ToUnsigned(y)) =>
        val newLhs = IntConversionMover.widen(ToSigned(x)())
        simplifyEqual(Equal(newLhs, y)())
      case _ => eq
    }
  }

  @tailrec
  private def simplifyLessThan(lt: LessThan): Expr = {
    lt.tchk() match {
      case LessThan(ToSignedOrIntCst(x), ToSignedOrIntCst(y)) =>
        // If both sides are converted to signed, the conversions are
        // unnecessary and can be stripped away
        simplifyLessThan(LessThan(x, y)())
      case LessThan(PadOrIntCst(x), PadOrIntCst(y)) =>
        // If both sides are padded, the conversions are unnecessary and can
        // be stripped away
        val lhsW = x.typ.asInstanceOf[TyAnyInt].w
        val rhsW = y.typ.asInstanceOf[TyAnyInt].w
        val w = math.max(lhsW, rhsW)
        val newLhs = IntConversionMover.widen(PadTo(x, w)())
        val newRhs = IntConversionMover.widen(PadTo(y, w)())
        simplifyLessThan(LessThan(newLhs, newRhs)())
      case LessThan(TruncateTo(x, _), y) =>
        // It's always safe to widen both sides, and here it is beneficial
        // because it will remove the truncation
        val w = x.typ.asInstanceOf[TyAnyInt].w
        val newRhs = IntConversionMover.widen(PadTo(y, w)())
        simplifyLessThan(LessThan(x, newRhs)())
      case LessThan(x, TruncateTo(y, _)) =>
        // It's always safe to widen both sides, and here it is beneficial
        // because it will remove the truncation
        val w = y.typ.asInstanceOf[TyAnyInt].w
        val newLhs = IntConversionMover.widen(PadTo(x, w)())
        simplifyLessThan(LessThan(newLhs, y)())
      case LessThan(ToUnsigned(x), y) =>
        // It's always safe to make both sides signed, and here it is
        // beneficial because it will remove the ToUnsigned(_)
        val newRhs = IntConversionMover.widen(ToSigned(y)())
        simplifyLessThan(LessThan(x, newRhs)())
      case LessThan(x, ToUnsigned(y)) =>
        // It's always safe to make both sides signed, and here it is
        // beneficial because it will remove the ToUnsigned(_)
        val newLhs = IntConversionMover.widen(ToSigned(x)())
        simplifyLessThan(LessThan(newLhs, y)())
      case e => e.tchk()
    }
  }

  private def simplifyAnd(and: And): Expr = {
    val flat = And(
      and.terms
        .map(simplifyWithoutLibrary)
        // Flatten to represent associativity
        .flatMap({
          case a: And => a.terms
          case e      => Seq(e)
        })
        // Filter out True to represent the fact that x && true == x
        .filter(_ != True)
        // Deduplicate to represent the fact that x && x == x
        .distinct
        // Sort to represent commutativity
        .sorted(ExprOrdering): _*
    )()
    // TODO: While simplifying a given term, assume all previous terms are true?
    val out = flat match {
      case And(terms @ _*) if terms.contains(False) =>
        False
      // TODO: Generalize these rules by looking for pairs of terms (a, b) such that a ==> b or a ==> !b ?
      case And(terms @ _*) if hasContradictoryTerms(terms) =>
        False
      case And(LessThan(e1, c1: IntCst), LessThan(e2, c2: IntCst))
          if e1 == e2 =>
        if (c1.i <= c2.i) {
          LessThan(e1, c1)()
        } else {
          LessThan(e2, c2)()
        }
      case And(LessThan(c1: IntCst, e1), LessThan(c2: IntCst, e2))
          if e1 == e2 =>
        assert(c1.hasType)
        assert(c1.typ == c2.typ)
        if (c1.i >= c2.i) {
          LessThan(c1, e1)()
        } else {
          LessThan(c2, e2)()
        }
      case And(Not(LessThan(x0, c0)), LessThan(x1, c1))
          if x0 == x1
            && PartialEvalPass
              .isEqual(c1, (c0 + 1).tchk().lower())()
              .getOrElse(false) =>
        Equal(x0, c0)()
      case And(terms @ _*) if terms.length <= 3 =>
        And(terms.filter({
          case Not(Equal(x0, IntCst(k0))) =>
            !terms.exists({
              case Equal(x1, IntCst(k1)) => x0 == x1 && k0 != k1
              case _                     => false
            })
          case _ => true
        }): _*)()
      case e =>
        e
    }
    out.tchk()
  }

  private def simplifyOr(or: Or): Expr = {
    val flat = Or(
      or.terms
        .map(simplifyWithoutLibrary)
        // Flatten to represent associativity
        .flatMap({
          case a: Or => a.terms
          case e     => Seq(e)
        })
        // Filter out False to represent the fact that x || false == x
        .filter(_ != False)
        // Deduplicate to represent the fact that x || x == x
        .distinct
        // Sort to represent commutativity
        .sorted(ExprOrdering): _*
    )()
    // TODO: While simplifying a given term, assume all previous terms are false?
    val out = flat match {
      case Or(terms @ _*) if terms.contains(True) =>
        True
      // TODO: Generalize these rules by looking for pairs of terms (a, b) such that a ==> b or a ==> !b ?
      case Or(terms @ _*) if hasContradictoryTerms(terms) =>
        True
      case Or(LessThan(e1, c1: IntCst), LessThan(e2, c2: IntCst)) if e1 == e2 =>
        assert(c1.hasType)
        assert(c1.typ == c2.typ)
        LessThan(e1, IntCst(math.max(c1.i, c2.i))(c1.typ))()
      case Or(LessThan(c1: IntCst, e1), LessThan(c2: IntCst, e2)) if e1 == e2 =>
        assert(c1.hasType)
        assert(c1.typ == c2.typ)
        LessThan(IntCst(math.min(c1.i, c2.i))(c1.typ), e1)()
      case Or(Not(LessThan(x0, k0: IntCst)), Equal(x1, k1: IntCst))
          if x0 == x1 && k1.i == k0.i - 1 =>
        Not(LessThan(x1, k1)())()
      case Or(Equal(x0, y0), LessThan(x1, y1)) if x0 == x1 && y0 == y1 =>
        x0 leq y0
      case Or(e @ Not(Equal(x0, k0: IntCst)), Equal(x1, k1: IntCst))
          if x0 == x1 && k0 != k1 =>
        e
      case e => e
    }
    out.tchk()
  }

  private def simplifyNot(not: Not): Expr = {
    val out = not match {
      case Not(True)   => False
      case Not(False)  => True
      case Not(Not(e)) => e
      case Not(And(terms @ _*)) =>
        Or(terms.map(e => simplifyNot(Not(e)())): _*)() match {
          case e: Or => simplifyOr(e)
          case e     => e
        }
      case Not(Or(terms @ _*)) =>
        And(terms.map(e => simplifyNot(Not(e)())): _*)() match {
          case e: And => simplifyAnd(e)
          case e      => e
        }
      case _ => not
    }
    out.tchk()
  }

  private def hasContradictoryTerms(terms: Seq[Expr]): Boolean = {
    terms.exists({
      case Not(e) => terms.contains(e)
      case Equal(x0, IntCst(k0)) =>
        terms.exists({
          case Equal(x1, IntCst(k1)) =>
            x1 == x0 && k0 != k1
          case _ => false
        })
      case _ =>
        false
    })
  }
}

private[optimize] object ToSignedOrIntCst {
  def unapply(e: Expr): Option[Expr] = {
    e.typ match {
      case TySInt(w) =>
        assert(w >= 1)
        e match {
          case ToSigned(x)         => Some(x)
          case IntCst(k) if k >= 0 => Some(C(k)(TyUInt(w - 1)))
          case _                   => None
        }
      case _ =>
        None
    }
  }
}

private[optimize] object PadOrIntCst {
  def unapply(e: Expr): Option[Expr] = {
    e match {
      case PadTo(x, _) => Some(x)
      case IntCst(k) =>
        val originalTyp = e.typ.asInstanceOf[TyAnyInt]
        val typ = originalTyp.shrinkToFit(k)
        if (typ.w < originalTyp.w) {
          Some(IntCst(k)(typ))
        } else {
          None
        }
      case _ => None
    }
  }
}
