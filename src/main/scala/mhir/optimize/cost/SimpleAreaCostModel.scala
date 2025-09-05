package mhir.optimize.cost

import mhir.ir._

/** A very simple cost model for estimating the area of an expression.
  */
object SimpleAreaCostModel {

  /** Estimates the resource usage of the given expression.
    */
  def cost(e: Expr): AreaCost = {
    cost(Set[Param]())(e)
  }

  /** Estimates the resource usage of the given expression.
    *
    * @param sv
    *   the set of variables which can be considered static within this
    *   expression (e.g., the index within [[mhir.ir.VecBuild]]).
    * @param e
    *   the expression whose resource usage to estimate.
    */
  private def cost(sv: Set[Param])(e: Expr): AreaCost = {
    require(e.hasType)
    e match {
      case _: Param => AreaCost.Zero
      case e if e.typ.isData && isStatic(e, sv) =>
        AreaCost(BitWidth(e.typ), 0, 0)
      case Tuple(elems @ _*) =>
        elems.map(cost(sv)).foldLeft(AreaCost.Zero)(_ + _)
      case TupleAccess(t, _) => cost(sv)(t)
      case Function(_, body) => cost(sv)(body)
      case FunCall(f, arg)   => cost(sv)(f) + cost(sv)(arg)
      case sum @ Sum(terms @ _*) =>
        val adderArea = AreaCost(9 * BitWidth(sum.typ), 0, 0)
        val childArea = terms
          .map({
            // Subtraction has the same cost as addition
            case Prod(IntCst(-1), e) => cost(sv)(e)
            case e                   => cost(sv)(e)
          })
          .foldLeft(AreaCost.Zero)(_ + _)
        adderArea + childArea
      case sum @ WrappingSum(terms @ _*) =>
        val adderArea = AreaCost(9 * BitWidth(sum.typ), 0, 0)
        val childArea = terms
          .map({
            // Subtraction has the same cost as addition
            case WrappingProd(IntCst(-1), e) => cost(sv)(e)
            case e                           => cost(sv)(e)
          })
          .foldLeft(AreaCost.Zero)(_ + _)
        adderArea + childArea
      case diff @ WrappingDiff(e1, e2) =>
        cost(sv)(e1) + cost(sv)(e2) + AreaCost(9 * BitWidth(diff.typ), 0, 0)
      case Prod(factors @ _*) =>
        factors.map(cost(sv)).foldLeft(AreaCost.Zero)(_ + _) + AreaCost(0, 0, 1)
      case WrappingProd(factors @ _*) =>
        factors.map(cost(sv)).foldLeft(AreaCost.Zero)(_ + _) + AreaCost(0, 0, 1)
      case IntFixProd(e1, e2) =>
        cost(sv)(e1) + cost(sv)(e2) + AreaCost(0, 0, 1)
      case Div(e1, e2) =>
        cost(sv)(e1) + cost(sv)(e2) + AreaCost(0, 0, 1)
      case Mod(e1, e2) =>
        cost(sv)(e1) + cost(sv)(e2) + AreaCost(0, 0, 1)
      case PadTo(e, _)                         => cost(sv)(e)
      case TruncateTo(e, _)                    => cost(sv)(e)
      case ToSigned(e)                         => cost(sv)(e)
      case ToUnsigned(e)                       => cost(sv)(e)
      case LLShift(e1, e2) if isStatic(e2, sv) => cost(sv)(e1)
      case LLShift(e1, e2)                     => cost(sv)(e1) + cost(sv)(e2)
      case LRShift(e1, e2) if isStatic(e2, sv) => cost(sv)(e1)
      case LRShift(e1, e2)                     => cost(sv)(e1) + cost(sv)(e2)
      case Equal(e1, e2) =>
        cost(sv)(e1) + cost(sv)(e2) + AreaCost(BitWidth(e1.typ), 0, 0)
      case LessThan(e1, e2) =>
        cost(sv)(e1) + cost(sv)(e2) + AreaCost(2 * BitWidth(e2.typ), 0, 0)
      case Not(e) => cost(sv)(e) + AreaCost(1, 0, 0)
      case And(terms @ _*) =>
        terms.map(cost(sv)).foldLeft(AreaCost.Zero)(_ + _) + AreaCost(1, 0, 0)
      case Or(terms @ _*) =>
        terms.map(cost(sv)).foldLeft(AreaCost.Zero)(_ + _) + AreaCost(1, 0, 0)
      case Mux(c, t, f) if isStatic(c, sv) =>
        // The MUX can disappear in hardware if the index is static
        cost(sv)(t) max cost(sv)(f)
      case mux @ Mux(c, t, f) =>
        (Seq(c, t, f).map(cost(sv)).foldLeft(AreaCost.Zero)(_ + _)
          + AreaCost(3 * BitWidth(mux.typ), 0, 0))
      case s: StmBuild =>
        val ram =
          BitWidth(s.typ.asInstanceOf[TyStm].t) + 1 +
            s.equations
              .map({
                case (x, _) if x.typ.isData => BitWidth(x.typ)
                case _                      => 0
              })
              .sum
        AreaCost(5, ram, 0) +
          cost(sv)(s.data) +
          cost(sv)(s.valid) +
          s.equations
            .map({ case (_, (z, next)) => cost(sv)(z) + cost(sv)(next) })
            .foldLeft(AreaCost.Zero)(_ + _)
      case LetStm(x, in, out) =>
        (cost(sv)(in) + cost(sv)(out)
          + AreaCost(BitWidth(x.typ.asInstanceOf[TyStm].t), 0, 0))
      case _: StmData                             => AreaCost(0, 0, 0)
      case VecBuild(IntCst(n), Function(i, body)) => cost(sv + i)(body) * n
      case VecBuild(n, _) =>
        throw new IllegalArgumentException(
          s"Cannot compute cost for VecBuild with non-constant length $n"
        )
      case VecAccess(v, i) if isStatic(i, sv) => cost(sv)(v)
      case VecAccess(v, i) =>
        (cost(sv)(v) + cost(sv)(i)
          + AreaCost(3 * BitWidth(v.typ.asInstanceOf[TyVec].t), 0, 0))
      case VecLiteral(elems @ _*) =>
        elems.map(cost(sv)).foldLeft(AreaCost.Zero)(_ + _)
      case stm @ StmLiteral(elems @ _*) =>
        val c = elems.map(cost(sv)).foldLeft(AreaCost.Zero)(_ + _)
        val w = 1 + (elems.length + 1) * BitWidth(stm.typ.asInstanceOf[TyStm].t)
        c + AreaCost(5, w, 0)
      case e: StmNextK =>
        throw new IllegalArgumentException(s"Cannot compute cost for $e")
      case e: SyntaxSugar =>
        throw new IllegalArgumentException(
          s"Cannot compute cost for syntax sugar $e"
        )
    }
  }

  private def isStatic(e: Expr, sv: Set[Param]): Boolean = {
    (e.freeVars() -- sv).isEmpty
  }
}
