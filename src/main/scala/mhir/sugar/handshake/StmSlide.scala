package mhir.sugar
package handshake

import mhir.ir._
import mhir.typecheck._

/** Return a stream of "windows" from a stream. Note that if the input stream is
  * multidimensional, the inner dimensions will be converted to vectors and
  * flattened.
  *
  * @note
  *   [[winSize]] must be between 1 and `n`, inclusive.
  *
  * @param input
  *   (`Stm[A, n]`) a stream of length n.
  * @param winSize
  *   (`Int`) window size.
  * @param stride
  *   (`Int`) how much to move the window per step.
  */
case class StmSlide(
    input: Expr /* Stm<A; n> */,
    winSize: Expr /* Int */,
    stride: Expr = C(1)() /* Int */
)(typ: Type = Missing) /* Stm<Vec<A; m>; n-m+1> */
    extends ResolvedSyntaxSugar(input, winSize, stride)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmSlide = {
    newChildren match {
      case Seq(s, winSize, stride) => StmSlide(s, winSize, stride)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmSlide = {
    val newWinSize = this.winSize.tchk(context, constValues).expectUInt()
    val newStride = this.stride.tchk(context, constValues).expectUInt()
    val newInput = this.input.tchk(context, constValues)
    newInput.typ match {
      case TyStm(t, n) if t.isData =>
        // First window start index (inclusive): 0
        // Last window start index (inclusive): n - winSize
        // We want to know how many multiples of `stride` there are in the
        // range [0, n-winSize].
        // In general, that is ceil( (n - winSize + 1) / stride )
        val newLen = CeilDiv(
          ToUnsigned(SafeSum(n, C(-1)() * newWinSize, 1)())(),
          newStride
        )().tchk().lower
        this.rebuild(
          TyStm(TyVec(t, newWinSize), newLen),
          Seq(newInput, newWinSize, newStride)
        )
      case t =>
        throw new TypeError(
          s"Stream in StmSlideV has type $t. Expected a non-nested stream."
        )
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val input = this.input.lower
    val winSize = this.winSize.lower
    val stride = this.stride.lower
    val TyStm(_, myLen) = this.typ
    val TyStm(t, _) = input.typ
    val s = Param("s")(TyStm(t, -1))
    val i = {
      val typ = TyAnyInt.tightest(
        1 - stride.typ.asInstanceOf[TyAnyInt].maxInt,
        myLen.typ.asInstanceOf[TyAnyInt].maxInt
      )
      Param("i")(typ)
    }
    val v = Param("v")(TyVec(t, winSize))
    val lowered = StmBuild(
      myLen,
      Tuple()(),
      Undefined(v.typ),
      VecShiftLeft(v, StmData(s)())().tchk().lower,
      (i + 1 === winSize).tchk().lower,
      Map[Param, (Expr, Expr, Expr)](
        // Number of elements loaded so far
        i -> (
          C(0)(i.typ),
          Mux(
            i + 1 === winSize,
            Cast(i + 1 - stride, i.typ)().tchk().lower,
            i + 1
          )().tchk().lower,
          Tuple()()
        ),
        // Vector for the window
        v -> (
          Undefined(TyVec(t, winSize)),
          VecShiftLeft(v, StmData(s)())().tchk().lower,
          Tuple()()
        )
      ),
      Map[Param, (Expr, Expr, Expr)](
        s -> (input, True, 0)
      )
    )().annotate(NoInputsAfterLastOut).annotateWithName(this.className)
    lowered.tchk()
  }
}
