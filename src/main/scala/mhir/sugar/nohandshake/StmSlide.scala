package mhir.sugar
package nohandshake

import mhir.ir._
import mhir.typecheck._

/** Return a stream of "windows" from a stream. Note that if the input stream is
  * multidimensional, the inner dimensions will be converted to vectors and
  * flattened.
  *
  * @note
  *   [[winSize]] must be between 1 and `n`, inclusive.
  * @param input
  *   (`Stm[A, n]`) a stream of length n.
  * @param winSize
  *   (`Int`) window size.
  * @param stride
  *   (`Int`) how much to move the window per step.
  */
case class StmSlide(
    input: Expr,
    winSize: Expr,
    head: Expr = Undefined(Missing)
)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(input, winSize, head)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmSlide = {
    newChildren match {
      case Seq(s, winSize, head) => StmSlide(s, winSize, head)(typ)
      case _                     => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmSlide = {
    val newWinSize = this.winSize.tchk(context, constValues).expectUInt()
    val newInput = this.input.tchk(context, constValues)
    val (elemTyp, n) = newInput.typ match {
      case TyStm(t, n) if t.isData => (t, n)
      case t =>
        throw new TypeError(
          s"Stream in $className has type $t. Expected a non-nested stream."
        )
    }
    val newHead = this.head match {
      case Undefined(Missing) => Undefined(elemTyp)
      case head =>
        head.tchk(context, constValues).expectType(elemTyp, constValues)
    }
    val newLen =
      ToUnsigned(SafeSum(n, C(-1)() * newWinSize, 1)())().tchk().lower
    this.rebuild(
      TyStm(TyVec(elemTyp, newWinSize), newLen),
      Seq(newInput, newWinSize, newHead)
    )
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val input = this.input.lower
    val winSize = this.winSize.lower
    val head = this.head.lower
    val TyStm(_, myLen) = this.typ
    val TyStm(t, _) = input.typ
    val p = Param("p")(TyStm(t, -1))
    val bufSize = SmartDiff(winSize, C(1)())().tchk().lower
    val v = Param("slide_buf")(TyVec(t, bufSize))
    val lowered = StmBuild(
      myLen,
      winSize,
      VecCst(winSize, head)().tchk().lower,
      VecAppend(v, StmData(p)())().tchk().lower,
      True,
      Map[Param, (Expr, Expr, Expr)](
        // Vector for the window
        v -> (
          VecCst(bufSize, head)().tchk().lower,
          VecShiftLeft(v, StmData(p)())().tchk().lower,
          Tuple()()
        )
      ),
      Map[Param, (Expr, Expr, Expr)](
        p -> (input, True, C(0)())
      )
    )().annotate(NoInputsAfterLastOut).annotateWithName(this.className)
    lowered.tchk()
  }
}
