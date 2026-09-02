package mhir.sugar
package handshake

import mhir.ir._
import mhir.typecheck._

import scala.annotation.tailrec

/** Reduction over a stream.
  *
  * This is a bit like [[StmFold]], but the head of the stream is used as the
  * initial value.
  *
  * This is meant to mirror the `reduce_t` primitive from
  * [[https://dl.acm.org/doi/10.1145/3385412.3385983 Aetherling]]. Therefore,
  * strange expressions like `reduce_t (map_s (add I) I) I` must unfortunately
  * be supported.
  *
  * @param s
  *   `Stm[T, n]`. The stream to reduce over.
  * @param f
  *   `(T, T) -> T`. The function to use for reducing.
  */
case class StmReduce(s: Expr, f: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmReduce = {
    newChildren match {
      case Seq(s, f) => StmReduce(s, f)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmReduce = {
    val s = this.s.tchk(context, constValues)
    // The type of the accumulator, but possibly wrapped in a bunch of vectors
    // and streams of length 1
    val wrappedTyp = s.typ match {
      case TyStm(t, _) => t
      case t =>
        throw new TypeError(
          s"Stream in $className has type $t. Expected a stream."
        )
    }
    val tupledTyp = tupleElemType(wrappedTyp, this.f)
    val f = this.f
      .annotateFunc(tupledTyp)
      .tchk(context, constValues)
      .expectType(tupledTyp ->: wrappedTyp, constValues)
    this.rebuild(TyStm(wrappedTyp, 1), Seq(s, f))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    SL.logger.trace(s"lowering $className: $this")
    requireType()
    val s = this.s.lower
    val n = this.s.typ.asInstanceOf[TyStm].n
    if (c.sameLen(n, C(1)())) {
      // Reduce over a stream of length 1 is a no-op
      s
    } else {
      val wrappedTyp = this.typ.asInstanceOf[TyStm].t
      val f = unwrapFunc(wrappedTyp, this.f).lower
      val elemTyp = unwrapTyp(wrappedTyp, this.f).lower
      val acc = Param("acc")(elemTyp)
      val t = Param("t")(n.typ)
      val sAcc = Param("s")(s.typ)
      val sData = unwrapElem(wrappedTyp, this.f, StmData(sAcc)())
      val firstStep = Param("first_step")(TyBool)
      val outData =
        wrapResult(wrappedTyp, this.f, f(Tuple(acc, sData)())).tchk()
      StmBuild(
        1,
        Tuple()(),
        Undefined(outData.typ),
        outData,
        Sum(C(1)(t.typ), t)() equ n,
        Map[Param, (Expr, Expr, Expr)](
          firstStep -> (True, False, Tuple()()),
          t -> (C(0)(n.typ), Sum(C(1)(n.typ), t)(), Tuple()()),
          acc -> (
            AllZero(elemTyp).lower,
            Mux(firstStep, sData, f(Tuple(acc, sData)()))(),
            Tuple()()
          )
        ),
        Map[Param, (Expr, Expr, Expr)](
          sAcc -> (s, True, Tuple()())
        )
      )().annotate(NoInputsAfterLastOut).annotateWithName(this.className).tchk()
    }
  }

  private def tupleElemType(wrappedTyp: Type, f: Expr)(implicit
      c: Canonicalizer
  ): Type = {
    (wrappedTyp, f) match {
      case (TyVec(t, IntCst(1)), Function(v0, VecMap(v1, g))) if v0 == v1 =>
        TyVec(tupleElemType(t, g), 1)
      case (TyStm(t, IntCst(1)), Function(s0, StmMap(s1, g))) if s0 == s1 =>
        TyStm(tupleElemType(t, g), 1)
      case (TyStm(t, IntCst(1)), Function(s0, mhir.sugar.StmMap(s1, g)))
          if s0 == s1 =>
        TyStm(tupleElemType(t, g), 1)
      case _ =>
        (wrappedTyp, wrappedTyp)
    }
  }

  @tailrec
  private def unwrapTyp(wrappedTyp: Type, f: Expr): Type = {
    (wrappedTyp, f) match {
      case (TyVec(t, IntCst(1)), Function(v0, VecMap(v1, g))) if v0 == v1 =>
        unwrapTyp(t, g)
      case (TyStm(t, IntCst(1)), Function(s0, StmMap(s1, g))) if s0 == s1 =>
        unwrapTyp(t, g)
      case _ =>
        wrappedTyp
    }
  }

  @tailrec
  private def unwrapFunc(wrappedTyp: Type, f: Expr): Expr = {
    (wrappedTyp, f) match {
      case (TyVec(t, IntCst(1)), Function(v0, VecMap(v1, g))) if v0 == v1 =>
        unwrapFunc(t, g)
      case (TyStm(t, IntCst(1)), Function(s0, StmMap(s1, g))) if s0 == s1 =>
        unwrapFunc(t, g)
      case _ =>
        f
    }
  }

  private def unwrapElem(wrappedTyp: Type, f: Expr, x: Expr): Expr = {
    (wrappedTyp, f) match {
      case (TyVec(t, IntCst(1)), Function(v0, VecMap(v1, g))) if v0 == v1 =>
        VecAccess(unwrapElem(t, g, x), 0)()
      case (TyStm(t, IntCst(1)), Function(s0, StmMap(s1, g))) if s0 == s1 =>
        // Streams should be moved to the outside during lowering, so no need
        // to do anything here
        unwrapElem(t, g, x)
      case _ =>
        x
    }
  }

  private def wrapResult(wrappedTyp: Type, f: Expr, x: Expr): Expr = {
    (wrappedTyp, f) match {
      case (TyVec(t, IntCst(1)), Function(v0, VecMap(v1, g))) if v0 == v1 =>
        VecBuild(1, U8 ::+ (_ => wrapResult(t, g, x)))()
      case (TyStm(t, IntCst(1)), Function(s0, StmMap(s1, g))) if s0 == s1 =>
        // Streams should be moved to the outside during lowering, so no need
        // to do anything here
        wrapResult(t, g, x)
      case _ =>
        x
    }
  }
}
