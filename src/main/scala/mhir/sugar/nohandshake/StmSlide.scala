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
case class StmSlide(input: Expr, winSize: Expr /* Int */ )(
    typ: Type = Missing
) /* Stm<Vec<A; m>; n-m+1> */
    extends ResolvedSyntaxSugar(input, winSize)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmSlide = {
    newChildren match {
      case Seq(s, winSize) => StmSlide(s, winSize)(typ)
      case _               => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmSlide = {
    val newWinSize = this.winSize.tchk(context, constValues).expectUInt()
    val newInput = this.input.tchk(context, constValues)
    newInput.typ match {
      case TyStm(t, n) if t.isData =>
        val newLen =
          ToUnsigned(SafeSum(n, C(-1)() * newWinSize, 1)())().tchk().lower
        this.rebuild(
          TyStm(TyVec(t, newWinSize), newLen),
          Seq(newInput, newWinSize)
        )
      case t =>
        throw new TypeError(
          s"Stream in $className has type $t. Expected a non-nested stream."
        )
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val input = this.input.lower
    val winSize = this.winSize.lower
    val TyStm(_, myLen) = this.typ
    val TyStm(t, _) = input.typ
    val s = Param("s")(TyStm(t, -1))
    val v = Param("v")(TyVec(t, winSize))
    val lowered = StmBuild(
      myLen,
      winSize,
      Undefined(v.typ),
      VecShiftLeft(v, StmData(s)())().tchk().lower,
      True,
      Map[Param, (Expr, Expr, Expr)](
        // Vector for the window
        v -> (
          Undefined(TyVec(t, winSize)),
          VecShiftLeft(v, StmData(s)())().tchk().lower,
          Tuple()()
        )
      ),
      Map[Param, (Expr, Expr, Expr)](
        s -> (input, True, C(0)())
      )
    )().annotate(NoInputsAfterLastOut).annotateWithName(this.className)
    lowered.tchk()
  }
}
