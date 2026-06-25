package mhir.optimize.cost

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck.TypeCheck

/** Cost model that estimates the maximum combinational delay of an expression.
  *
  * @param madd
  *   whether the DSPs on the target device support multiplication and addition
  *   in one cycle.
  */
case class SimpleDelayCostModel(madd: Boolean) {

  val FullCycleDelay: Long = 100
  private val MaxAddsPerCycle = 3

  /** The delay cost of the given expression, which will be at least as large as
    * the full cycle delay.
    */
  def cost(
      e: Expr,
      staticVars: Set[Param] = Set(),
      varCosts: Map[Param, Long] = Map()
  ): Long = {
    // Doesn't really matter what the delay is, as long as it's low enough to
    // meet the timing requirements
    math.max(FullCycleDelay, cost(staticVars, varCosts)(e.tchk()))
  }

  /** The raw delay cost of the given expression, which may be less than the
    * full cycle delay.
    */
  def rawCost(
      e: Expr,
      staticVars: Set[Param] = Set(),
      varCosts: Map[Param, Long] = Map()
  ): Long = {
    cost(staticVars, varCosts)(e.tchk())
  }

  private def cost(staticVars: Set[Param], varCosts: Map[Param, Long])(
      e: Expr
  ): Long = {
    require(e.hasType)
    e match {
      case _: StmData | _: BoolCst | _: IntCst | _: FixCst | _: Undefined =>
        0
      case _ if isStatic(e, staticVars) && e.typ.isData => 0
      case x: Param          => varCosts.getOrElse(x, 0)
      case Tuple(elems @ _*) => elems.map(cost(staticVars, varCosts)).max
      case TupleAccess(t, _) => cost(staticVars, varCosts)(t)
      case Function(_, body) => cost(staticVars, varCosts)(body)
      case FunCall(f, arg) =>
        cost(staticVars, varCosts)(f) + cost(staticVars, varCosts)(arg)
      case MulAdd(a1, a2, b1, b2) if this.madd =>
        FullCycleDelay + Seq(a1, a2, b1, b2).map(cost(staticVars, varCosts)).max
      case AnyAdd(terms @ _*) =>
        val adderDelay = log2(terms.length) * FullCycleDelay / MaxAddsPerCycle
        val childDelay = terms
          .map({
            // Subtraction has the same cost as addition
            case AnyMul(IntCst(-1), e) => cost(staticVars, varCosts)(e)
            case e                     => cost(staticVars, varCosts)(e)
          })
          .max
        adderDelay + childDelay
      case WrappingDiff(e1, e2) =>
        math.max(
          cost(staticVars, varCosts)(e1),
          cost(staticVars, varCosts)(e2)
        ) + FullCycleDelay / MaxAddsPerCycle
      case AnyMul(factors @ _*) =>
        val nonPowersOfTwo = factors.filterNot({
          case IntCst(k) => isPowerOfTwo(k)
          case _         => false
        })
        val childCosts = factors.map(cost(staticVars, varCosts)).max
        val selfCost = nonPowersOfTwo.length match {
          case 0 | 1 => 0
          case n     => log2(n) * FullCycleDelay
        }
        childCosts + selfCost
      case IntFixProd(e1, e2) =>
        math.max(
          cost(staticVars, varCosts)(e1),
          cost(staticVars, varCosts)(e2)
        ) + FullCycleDelay
      case Div(e1, e2) =>
        math.max(
          cost(staticVars, varCosts)(e1),
          cost(staticVars, varCosts)(e2)
        ) + FullCycleDelay
      case Mod(e1, e2) =>
        math.max(
          cost(staticVars, varCosts)(e1),
          cost(staticVars, varCosts)(e2)
        ) + FullCycleDelay
      case PadTo(e, _)      => cost(staticVars, varCosts)(e)
      case TruncateTo(e, _) => cost(staticVars, varCosts)(e)
      case ToSigned(e)      => cost(staticVars, varCosts)(e)
      case ToUnsigned(e)    => cost(staticVars, varCosts)(e)
      case Bits(e) =>
        cost(staticVars, varCosts)(e)
      case LShift(e1, e2) =>
        math.max(cost(staticVars, varCosts)(e1), cost(staticVars, varCosts)(e2))
      case ARShift(e1, e2) =>
        math.max(cost(staticVars, varCosts)(e1), cost(staticVars, varCosts)(e2))
      case LRShift(e1, e2) =>
        math.max(cost(staticVars, varCosts)(e1), cost(staticVars, varCosts)(e2))
      case Equal(e1, e2) =>
        math.max(
          cost(staticVars, varCosts)(e1),
          cost(staticVars, varCosts)(e2)
        ) + 1
      case LessThan(e1, e2) =>
        math.max(
          cost(staticVars, varCosts)(e1),
          cost(staticVars, varCosts)(e2)
        ) + 1
      case Not(e)          => cost(staticVars, varCosts)(e) + 1
      case And(terms @ _*) => terms.map(cost(staticVars, varCosts)).sum + 1
      case Or(terms @ _*)  => terms.map(cost(staticVars, varCosts)).sum + 1
      case Mux(c, t, f) =>
        val childDelay = Seq(c, t, f).map(cost(staticVars, varCosts)).max
        if (isStatic(c, staticVars)) {
          childDelay
        } else {
          childDelay + 3
        }
      case s: StmBuild =>
        (cost(staticVars, varCosts)(s.data) +: cost(staticVars, varCosts)(
          s.valid
        ) +: s.equations
          .map({
            case (x, (s, _)) if x.typ.isInstanceOf[TyStm] =>
              cost(staticVars, varCosts)(s)
            case (_, (_, next)) =>
              cost(staticVars, varCosts)(next)
          })
          .toSeq).max
      case LetStm(_, _, in, out) =>
        math.max(
          cost(staticVars, varCosts)(in),
          cost(staticVars, varCosts)(out)
        )
      case VecBuild(_, Function(i, body)) =>
        cost(staticVars + i, varCosts)(body)
      case VecAccess(v, i) if isStatic(i, staticVars) =>
        cost(staticVars, varCosts)(v)
      case VecAccess(v, i) =>
        math.max(
          cost(staticVars, varCosts)(v),
          cost(staticVars, varCosts)(i)
        ) + 1
      case VecLiteral(elems @ _*) => elems.map(cost(staticVars, varCosts)).max
      case StmLiteral(elems @ _*) => elems.map(cost(staticVars, varCosts)).max
      case e: SyntaxSugar =>
        throw new IllegalArgumentException(
          s"Cannot compute cost for syntax sugar $e"
        )
    }
  }

  private def log2(n: Int): Int = {
    math.ceil(math.log10(n) / math.log10(2)).toInt
  }

  private def isStatic(e: Expr, staticVars: Set[Param]): Boolean = {
    (e.freeVars -- staticVars).isEmpty
  }

  // TODO: This method is duplicated in several places :/
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
}

object MulAdd {

  def unapply(e: Expr): Option[(Expr, Expr, Expr, Expr)] = {
    e match {
      case AnyAdd(AnyMul(a1, a2), AnyMul(b1, b2)) =>
        Some(a1, a2, b1, b2)
      case _ =>
        None
    }
  }
}

object AnyAdd {

  def unapplySeq(e: Expr): Option[Seq[Expr]] = {
    e match {
      case Sum(terms @ _*)         => Some(terms)
      case WrappingSum(terms @ _*) => Some(terms)
      case _                       => None
    }
  }
}

object AnyMul {

  def unapplySeq(e: Expr): Option[Seq[Expr]] = {
    e match {
      case Prod(factors @ _*)         => Some(factors)
      case WrappingProd(factors @ _*) => Some(factors)
      case _                          => None
    }
  }
}
