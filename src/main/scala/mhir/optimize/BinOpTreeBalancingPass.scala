package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.logging.time
import mhir.typecheck.TypeCheck
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

object DisabledBinOpTreeBalancingPass extends BinOpTreeBalancingPass {

  override def enabled: Boolean = false

  override def balance(e: Expr): Expr = e
}

object EnabledBinOpTreeBalancingPass extends BinOpTreeBalancingPass {

  override def enabled: Boolean = true

  override def balance(e: Expr): Expr = {
    doBalance(e)
  }

  private def doBalance(e: Expr): Expr = {
    val typedE = e.tchk()
    val result = typedE match {
      case s @ Sum(_, _) => s
      case Sum(terms @ _*) =>
        val nLeft = this.nLeft(terms.length)
        val (negTermsWithMinus, posTerms) = terms.partition({
          case Prod(cst @ IntCst(k), _ @_*) =>
            // Consider cst = -128:i8. 128 does not fit within type i8.
            // Therefore, pretend this term is positive so that the rest of the
            // code leaves it as-is.
            k < 0 && cst.typ.asInstanceOf[TyAnyInt].contains(-k)
          case _ => false
        })
        val (lhs, rhs) = if (nLeft < posTerms.length) {
          (
            doBalance(MaybeSum(posTerms.take(nLeft): _*)()),
            doBalance(MaybeSum(posTerms.drop(nLeft) ++ negTermsWithMinus: _*)())
          )
        } else {
          val negTermsWithoutMinus = negTermsWithMinus.map({
            case Prod(IntCst(-1), rest @ _*) =>
              MaybeProd(rest: _*)()
            case Prod(cst @ IntCst(k), rest @ _*) if k < 0 =>
              MaybeProd(IntCst(-k)(cst.typ) +: rest: _*)()
            case _ =>
              ???
          })
          val m = nLeft - posTerms.length
          val lhs =
            doBalance(MaybeSum(posTerms ++ negTermsWithMinus.take(m): _*)())
          val rhsPos =
            doBalance(MaybeSum(negTermsWithoutMinus.drop(m): _*)()).tchk()
          assert(rhsPos.typ.isInstanceOf[TySInt])
          (
            lhs,
            Prod(C(-1)(rhsPos.typ), rhsPos)()
          )
        }
        Sum(lhs, rhs)()
      case p @ Prod(_, _) => p
      case Prod(factors @ _*) =>
        val (lhsFactors, rhsFactors) =
          factors.splitAt(this.nLeft(factors.length))
        val lhs = doBalance(MaybeProd(lhsFactors: _*)())
        val rhs = doBalance(MaybeProd(rhsFactors: _*)())
        Prod(lhs, rhs)()
      case s @ WrappingSum(_, _) => s
      case WrappingSum(terms @ _*) =>
        val (lhsTerms, rhsTerms) = terms.splitAt(this.nLeft(terms.length))
        val lhs = doBalance(MaybeWrappingSum(lhsTerms: _*)())
        val rhs = doBalance(MaybeWrappingSum(rhsTerms: _*)())
        WrappingSum(lhs, rhs)()
      case p @ WrappingProd(_, _) => p
      case WrappingProd(factors @ _*) =>
        val (lhsFactors, rhsFactors) =
          factors.splitAt(this.nLeft(factors.length))
        val lhs = doBalance(MaybeWrappingProd(lhsFactors: _*)())
        val rhs = doBalance(MaybeWrappingProd(rhsFactors: _*)())
        WrappingProd(lhs, rhs)()
      case a @ And(_, _) => a
      case And(terms @ _*) =>
        val (lhsTerms, rhsTerms) = terms.splitAt(this.nLeft(terms.length))
        val lhs = doBalance(MaybeAnd(lhsTerms: _*)())
        val rhs = doBalance(MaybeAnd(rhsTerms: _*)())
        And(lhs, rhs)()
      case o @ Or(_, _) => o
      case Or(terms @ _*) =>
        val (lhsTerms, rhsTerms) = terms.splitAt(this.nLeft(terms.length))
        val lhs = doBalance(MaybeOr(lhsTerms: _*)())
        val rhs = doBalance(MaybeOr(rhsTerms: _*)())
        Or(lhs, rhs)()
      case _ =>
        e.map(doBalance)
    }
    val typedResult = result.tchk()
    assert(typedResult.typ ~= typedE.typ)
    typedResult
  }

  /** Decide how many terms should go on the left.
    *
    * @param n
    *   the total number of terms.
    */
  private def nLeft(n: Int): Int = {
    require(n >= 3)
    /* Prefer even splits so that we end up with
     *                  (+)
     *                 /   \
     *                /     \
     *               /       \
     *              /         \
     *             /           (+)
     *            /           /   \
     *           /           /     \
     *          /           /       \
     *       (+)         (+)         (+)
     *      /   \       /   \       /   \
     *   x0*y0 x1*y1 x2*y2 x3*y3 x4*y4 x5*y5
     *
     * rather than
     *
     *                  (+)
     *                 /   \
     *                /     \
     *               /       \
     *              /         \
     *             /           \
     *            /             \
     *         (+)               (+)
     *        /   \             /   \
     *       /     (+)         /     (+)
     *      /     /   \       /     /   \
     *   x0*y0 x1*y1 x2*y2 x3*y3 x4*y4 x5*y5
     *
     * The former maps well to the multiplier adder mode on some DSPs: there
     * are clearly three mul-add operations followed by two adders.
     * The latter only has two clear mul-add operations, and there are three
     * other adders.
     */
    val closestSplit = n / 2
    val result = if (n % 2 == 0 && closestSplit % 2 == 1) {
      // Turn odd + odd into even + even
      closestSplit - 1
    } else {
      // We have (_ + even) or (even + _), so we can't do any better
      closestSplit
    }
    assert(result > 0)
    assert(result < n)
    assert(if (n % 2 == 0) result % 2 == 0 else true)
    result
  }
}

case class BinOpTreeBalancingPassWithLogging(underlying: BinOpTreeBalancingPass)
    extends BinOpTreeBalancingPass {

  private implicit val logger: Logger = Logger(getClass.getName)
  private var hasLogged: Boolean = false

  override def enabled: Boolean = this.underlying.enabled

  override def balance(e: Expr): Expr = {
    if (this.disabled && !this.hasLogged) {
      logger.debug(s"binop balancing is disabled")
      this.hasLogged = true
    }
    time("binop balancing", Level.DEBUG, mute = this.disabled) {
      this.underlying.balance(e)
    }
  }
}
