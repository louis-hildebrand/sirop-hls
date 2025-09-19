package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.logging.time
import org.slf4j.event.Level

/** Pass for making expressions like [[mhir.ir.Sum]] and [[mhir.ir.Or]] into
  * balanced binary trees.
  */
trait BinOpTreeBalancingPass {
  def enabled: Boolean
  final def disabled: Boolean = !enabled

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
  def balance(e: Expr): Expr
}

object BinOpTreeBalancingPass {
  def apply(enabled: Boolean = true): BinOpTreeBalancingPass = {
    if (enabled) EnabledBinOpTreeBalancingPass
    else DisabledBinOpTreeBalancingPass
  }
}

object EnabledBinOpTreeBalancingPass extends BinOpTreeBalancingPass {

  private implicit val logger: Logger = Logger(getClass.getName)

  override def enabled: Boolean = true

  override def balance(e: Expr): Expr = {
    time("balancing binop trees", Level.DEBUG) {
      doBalance(e)
    }
  }

  private def doBalance(e: Expr): Expr = {
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
            doBalance(Sum(posTerms.take(nLeft): _*)()),
            doBalance(Sum(posTerms.drop(nLeft) ++ negTermsWithMinus: _*)())
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
            doBalance(Sum(posTerms ++ negTermsWithMinus.take(m): _*)())
          val rhsPos =
            doBalance(Sum(negTermsWithoutMinus.drop(m): _*)()).tchk()
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
        val lhs = doBalance(Prod(lhsFactors: _*)())
        val rhs = doBalance(Prod(rhsFactors: _*)())
        Prod(lhs, rhs)()
      case s @ WrappingSum(_, _) => s
      case WrappingSum(terms @ _*) =>
        assert(terms.length >= 3)
        val (lhsTerms, rhsTerms) = terms.splitAt(terms.length / 2)
        val lhs = doBalance(WrappingSum(lhsTerms: _*)())
        val rhs = doBalance(WrappingSum(rhsTerms: _*)())
        WrappingSum(lhs, rhs)()
      case p @ WrappingProd(_, _) => p
      case WrappingProd(factors @ _*) =>
        assert(factors.length >= 3)
        val (lhsFactors, rhsFactors) = factors.splitAt(factors.length / 2)
        val lhs = doBalance(WrappingProd(lhsFactors: _*)())
        val rhs = doBalance(WrappingProd(rhsFactors: _*)())
        WrappingProd(lhs, rhs)()
      case a @ And(_, _) => a
      case And(terms @ _*) =>
        assert(terms.length >= 3)
        val (lhsTerms, rhsTerms) = terms.splitAt(terms.length / 2)
        val lhs = doBalance(And(lhsTerms: _*)())
        val rhs = doBalance(And(rhsTerms: _*)())
        And(lhs, rhs)()
      case o @ Or(_, _) => o
      case Or(terms @ _*) =>
        assert(terms.length >= 3)
        val (lhsTerms, rhsTerms) = terms.splitAt(terms.length / 2)
        val lhs = doBalance(Or(lhsTerms: _*)())
        val rhs = doBalance(Or(rhsTerms: _*)())
        Or(lhs, rhs)()
      case _ =>
        e.map(doBalance)
    }
    val typedResult = result.tchk()
    assert(typedResult.typ ~= typedE.typ)
    typedResult
  }
}

object DisabledBinOpTreeBalancingPass extends BinOpTreeBalancingPass {

  private val logger: Logger = Logger(getClass.getName)
  private var hasLogged: Boolean = false

  override def enabled: Boolean = false

  override def balance(e: Expr): Expr = {
    if (!hasLogged) {
      hasLogged = true
      logger.debug("binop tree balancing is disabled")
    }
    e
  }
}
