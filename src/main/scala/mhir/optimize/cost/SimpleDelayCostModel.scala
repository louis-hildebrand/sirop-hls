package mhir.optimize.cost

import mhir.ir._

object SimpleDelayCostModel {
  val FullCycleDelay: Long = 100
  private val MaxAddsPerCycle = 3

  def cost(e: Expr): Long = {
    // Doesn't really matter what the delay is, as long as it's low enough to
    // meet the timing requirements
    math.max(FullCycleDelay, cost(Set[Param]())(e))
  }

  private def cost(staticVars: Set[Param])(e: Expr): Long = {
    e match {
      case Tuple(elems @ _*) => elems.map(cost(staticVars)).max
      case TupleAccess(t, _) => cost(staticVars)(t)
      case Function(_, body) => cost(staticVars)(body)
      case FunCall(f, arg)   => cost(staticVars)(f) + cost(staticVars)(arg)
      case _: Param | True | False | _: IntCst | _: FixCst => 0
      case Sum(terms @ _*) =>
        val adderDelay = log2(terms.length) * FullCycleDelay / MaxAddsPerCycle
        val childDelay = terms
          .map({
            // Subtraction has the same cost as addition
            case Prod(IntCst(-1), e) => cost(staticVars)(e)
            case e                   => cost(staticVars)(e)
          })
          .max
        adderDelay + childDelay
      case WrappingSum(terms @ _*) =>
        val adderDelay = log2(terms.length) * FullCycleDelay / MaxAddsPerCycle
        val childDelay = terms
          .map({
            // Subtraction has the same cost as addition
            case WrappingProd(IntCst(-1), e) => cost(staticVars)(e)
            case e                           => cost(staticVars)(e)
          })
          .max
        adderDelay + childDelay
      case WrappingDiff(e1, e2) =>
        math.max(cost(staticVars)(e1), cost(staticVars)(e2)) +
          FullCycleDelay / MaxAddsPerCycle
      case Prod(factors @ _*) =>
        (factors.map(cost(staticVars)).max
          + log2(factors.length) * FullCycleDelay)
      case WrappingProd(factors @ _*) =>
        (factors.map(cost(staticVars)).max
          + log2(factors.length) * FullCycleDelay)
      case IntFixProd(e1, e2) =>
        math.max(cost(staticVars)(e1), cost(staticVars)(e2)) + FullCycleDelay
      case Div(e1, e2) =>
        math.max(cost(staticVars)(e1), cost(staticVars)(e2)) + FullCycleDelay
      case Mod(e1, e2) =>
        math.max(cost(staticVars)(e1), cost(staticVars)(e2)) + FullCycleDelay
      case PadTo(e, _)      => cost(staticVars)(e)
      case TruncateTo(e, _) => cost(staticVars)(e)
      case ToSigned(e)      => cost(staticVars)(e)
      case ToUnsigned(e)    => cost(staticVars)(e)
      case LLShift(e1, e2) =>
        math.max(cost(staticVars)(e1), cost(staticVars)(e2))
      case LRShift(e1, e2) =>
        math.max(cost(staticVars)(e1), cost(staticVars)(e2))
      case Equal(e1, e2) =>
        math.max(cost(staticVars)(e1), cost(staticVars)(e2)) + 1
      case LessThan(e1, e2) =>
        math.max(cost(staticVars)(e1), cost(staticVars)(e2)) + 1
      case Not(e)          => cost(staticVars)(e) + 1
      case And(terms @ _*) => terms.map(cost(staticVars)).sum + 1
      case Or(terms @ _*)  => terms.map(cost(staticVars)).sum + 1
      case Mux(c, t, f) =>
        Seq(c, t, f).map(cost(staticVars)).max + 3
      case s: StmBuild =>
        (cost(staticVars)(s.data) +: cost(staticVars)(s.valid) +: s.equations
          .map({
            case (x, (s, _)) if x.typ.isInstanceOf[TyStm] =>
              cost(staticVars)(s)
            case (_, (_, next)) =>
              cost(staticVars)(next)
          })
          .toSeq).max
      case LetStm(_, _, in, out) =>
        math.max(cost(staticVars)(in), cost(staticVars)(out))
      case _: StmData                     => 0
      case VecBuild(_, Function(i, body)) => cost(staticVars + i)(body)
      case VecAccess(v, i) if isStatic(i, staticVars) => cost(staticVars)(v)
      case VecAccess(v, i) =>
        math.max(cost(staticVars)(v), cost(staticVars)(i)) + 1
      case VecLiteral(elems @ _*) => elems.map(cost(staticVars)).max
      case StmLiteral(elems @ _*) => elems.map(cost(staticVars)).max
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
    (e.freeVars() -- staticVars).isEmpty
  }
}
