package mhir.optimize.cost

import mhir.ir._

object SimpleDelayCostModel {

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
    math.max(FullCycleDelay, cost(staticVars, varCosts)(e))
  }

  /** The raw delay cost of the given expression, which may be less than the
    * full cycle delay.
    */
  def rawCost(
      e: Expr,
      staticVars: Set[Param] = Set(),
      varCosts: Map[Param, Long] = Map()
  ): Long = {
    cost(staticVars, varCosts)(e)
  }

  private def cost(staticVars: Set[Param], varCosts: Map[Param, Long])(
      e: Expr
  ): Long = {
    e match {
      case _: StmData | _: BoolCst | _: IntCst | _: FixCst | _: Undefined =>
        0
      case x: Param          => varCosts.getOrElse(x, 0)
      case Tuple(elems @ _*) => elems.map(cost(staticVars, varCosts)).max
      case TupleAccess(t, _) => cost(staticVars, varCosts)(t)
      case Function(_, body) => cost(staticVars, varCosts)(body)
      case FunCall(f, arg) =>
        cost(staticVars, varCosts)(f) + cost(staticVars, varCosts)(arg)
      case Sum(terms @ _*) =>
        val adderDelay = log2(terms.length) * FullCycleDelay / MaxAddsPerCycle
        val childDelay = terms
          .map({
            // Subtraction has the same cost as addition
            case Prod(IntCst(-1), e) => cost(staticVars, varCosts)(e)
            case e                   => cost(staticVars, varCosts)(e)
          })
          .max
        adderDelay + childDelay
      case WrappingSum(terms @ _*) =>
        val adderDelay = log2(terms.length) * FullCycleDelay / MaxAddsPerCycle
        val childDelay = terms
          .map({
            // Subtraction has the same cost as addition
            case WrappingProd(IntCst(-1), e) => cost(staticVars, varCosts)(e)
            case e                           => cost(staticVars, varCosts)(e)
          })
          .max
        adderDelay + childDelay
      case WrappingDiff(e1, e2) =>
        math.max(
          cost(staticVars, varCosts)(e1),
          cost(staticVars, varCosts)(e2)
        ) + FullCycleDelay / MaxAddsPerCycle
      case Prod(factors @ _*) =>
        (factors.map(cost(staticVars, varCosts)).max
          + log2(factors.length) * FullCycleDelay)
      case WrappingProd(factors @ _*) =>
        (factors.map(cost(staticVars, varCosts)).max
          + log2(factors.length) * FullCycleDelay)
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
      case LLShift(e1, e2) =>
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
        Seq(c, t, f).map(cost(staticVars, varCosts)).max + 3
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
      case e: StmNextK =>
        throw new IllegalArgumentException(s"Cannot compute cost for $e")
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
}
