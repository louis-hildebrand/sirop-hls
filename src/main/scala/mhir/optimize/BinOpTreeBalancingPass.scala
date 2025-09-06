package mhir.optimize

import mhir.ir._
import mhir.ir.typecheck.TypeCheck

trait BinOpTreeBalancingPass {
  def enabled: Boolean
  def disabled: Boolean = !enabled

  def balance(e: Expr): Expr
}

object BinOpTreeBalancingPass {
  def apply(enabled: Boolean = true): BinOpTreeBalancingPass = {
    if (enabled) EnabledBinOpTreeBalancingPass
    else DisabledBinOpTreeBalancingPass
  }
}

/** Pass for making expressions like [[mhir.ir.Sum]] and [[mhir.ir.Or]] into
  * binary trees.
  */
object EnabledBinOpTreeBalancingPass extends BinOpTreeBalancingPass {

  override def enabled: Boolean = true

  /** Convert flat operators with many operands (e.g., [[mhir.ir.Sum]],
    * [[mhir.ir.Prod]], [[mhir.ir.And]], [[mhir.ir.Or]]) into roughly balanced
    * binary trees.
    *
    * For example, consider a flat sum with 4 operands. If this is converted to
    * a VHDL expression like `x + y + z + w`, that defines a linear chain of
    * three adders. The delay of this circuit is equal to the delay of three
    * adders. If this expression is reshaped into a balanced binary tree like (x
    * + y) + (z + w), the number of adders remains three but now the delay is
    * only two adders.
    *
    * @param e
    *   the expression to process.
    */
  def balance(e: Expr): Expr = {
    val typedE = e.tchk()
    val result = typedE match {
      case s @ Sum(_, _) => s
      case Sum(terms @ _*) =>
        assert(terms.length >= 3)
        val nLeft = terms.length / 2
        val (negTermsWithMinus, posTerms) = terms.partition({
          case Prod(IntCst(k), _ @_*) => k < 0
          case _                      => false
        })
        val (lhs, rhs) = if (nLeft < posTerms.length) {
          (
            balance(Sum(posTerms.take(nLeft): _*)()),
            balance(Sum(posTerms.drop(nLeft) ++ negTermsWithMinus: _*)())
          )
        } else {
          val negTermsWithoutMinus = negTermsWithMinus.map({
            case Prod(IntCst(-1), rest @ _*) =>
              Prod(rest: _*)()
            case Prod(IntCst(k), rest @ _*) if k < 0 =>
              Prod(IntCst(-k)() +: rest: _*)()
            case _ =>
              ???
          })
          val m = nLeft - posTerms.length
          val lhs =
            balance(Sum(posTerms ++ negTermsWithMinus.take(m): _*)())
          val rhsPos =
            balance(Sum(negTermsWithoutMinus.drop(m): _*)()).tchk()
          assert(rhsPos.typ.isInstanceOf[TySInt])
          (
            lhs,
            Prod(C(-1)(rhsPos.typ), rhsPos)()
          )
        }
        Sum(lhs, rhs)()
      case p @ Prod(_, _) => p
      case Prod(factors @ _*) =>
        assert(factors.length >= 3)
        val (lhsFactors, rhsFactors) = factors.splitAt(factors.length / 2)
        val lhs = balance(Prod(lhsFactors: _*)())
        val rhs = balance(Prod(rhsFactors: _*)())
        Prod(lhs, rhs)()
      case s @ WrappingSum(_, _) => s
      case WrappingSum(terms @ _*) =>
        assert(terms.length >= 3)
        val (lhsTerms, rhsTerms) = terms.splitAt(terms.length / 2)
        val lhs = balance(WrappingSum(lhsTerms: _*)())
        val rhs = balance(WrappingSum(rhsTerms: _*)())
        WrappingSum(lhs, rhs)()
      case p @ WrappingProd(_, _) => p
      case WrappingProd(factors @ _*) =>
        assert(factors.length >= 3)
        val (lhsFactors, rhsFactors) = factors.splitAt(factors.length / 2)
        val lhs = balance(WrappingProd(lhsFactors: _*)())
        val rhs = balance(WrappingProd(rhsFactors: _*)())
        WrappingProd(lhs, rhs)()
      case a @ And(_, _) => a
      case And(terms @ _*) =>
        assert(terms.length >= 3)
        val (lhsTerms, rhsTerms) = terms.splitAt(terms.length / 2)
        val lhs = balance(And(lhsTerms: _*)())
        val rhs = balance(And(rhsTerms: _*)())
        And(lhs, rhs)()
      case o @ Or(_, _) => o
      case Or(terms @ _*) =>
        assert(terms.length >= 3)
        val (lhsTerms, rhsTerms) = terms.splitAt(terms.length / 2)
        val lhs = balance(Or(lhsTerms: _*)())
        val rhs = balance(Or(rhsTerms: _*)())
        Or(lhs, rhs)()
      case _ =>
        e.map(balance)
    }
    val typedResult = result.tchk()
    assert(typedResult.typ ~= typedE.typ)
    typedResult
  }
}

object DisabledBinOpTreeBalancingPass extends BinOpTreeBalancingPass {
  override def enabled: Boolean = false

  override def balance(e: Expr): Expr = e
}
