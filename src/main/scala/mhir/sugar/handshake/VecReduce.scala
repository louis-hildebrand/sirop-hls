package mhir.sugar
package handshake

import mhir.ir._
import mhir.typecheck._

import scala.annotation.tailrec

/** Combinational reduce over a vector
  *
  * This is a bit like [[VecFoldComb]], but the first element of the vector is
  * used as the initial value.
  *
  * This is meant to mirror the `reduce_s` primitive from
  * [[https://dl.acm.org/doi/10.1145/3385412.3385983 Aetherling]]. Therefore,
  * strange expressions like `reduce_s (map_s (add I) I) I` must unfortunately
  * be supported.
  */
case class VecReduce(
    v: Expr /* Vec<T; n> */,
    f: Expr /* (T, T) -> T */
)(typ: Type = Missing) /* Vec<T; 1> */
    extends ResolvedSyntaxSugar(v, f)(typ) /* T */ {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): VecReduce = {
    newChildren match {
      case Seq(v, f) => VecReduce(v, f)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): VecReduce = {
    val v = this.v.tchk(context, constValues)
    // The type of the accumulator, but possibly wrapped in a bunch of vectors
    // and streams of length 1
    val wrappedTyp = v.typ match {
      case TyVec(t, _) => t
      case t =>
        throw new TypeError(
          s"Vector in $className has type $t. Expected a vector."
        )
    }
    val tupledTyp = tupleElemType(wrappedTyp, this.f)
    val f =
      this.f
        .annotateFunc(tupledTyp)
        .tchk(context, constValues)
        .expectType(tupledTyp ->: wrappedTyp, constValues)
    this.rebuild(TyVec(wrappedTyp, 1), Seq(v, f))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val v = this.v.lower
    val wrappedTyp = this.typ.asInstanceOf[TyVec].t
    val f = unwrapFunc(wrappedTyp, this.f).lower
    val n = this.v.typ.asInstanceOf[TyVec].n match {
      case IntCst(n) if n > 0 => n
      case IntCst(n) if n <= 0 =>
        throw new IllegalArgumentException(
          s"Cannot reduce over empty vector (length $n)."
        )
      case e =>
        throw new IllegalArgumentException(
          s"Cannot reduce over vector with non-constant size $e."
        )
    }
    val result = (v: Expr) => {
      val elem =
        (i: Int) => unwrapElem(wrappedTyp, this.f, VecAccess(v, i)())
      (1 until n.toInt)
        .foldLeft(elem(0))({ case (acc, i) => f(Tuple(acc, elem(i))()) })
    }
    wrapResult(result, v).tchk().lower
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

  private def wrapResult(result: Expr => Expr, v: Expr)(implicit
      c: Canonicalizer
  ): Expr = {
    def wrap(t: Type, x: Expr): Expr = {
      assert(x.hasType)
      if (t == x.typ) {
        x
      } else {
        t match {
          case TyVec(t, IntCst(1)) =>
            VecBuild(1, U8 ::+ (_ => wrap(t, x)))()
          case TyStm(t, IntCst(1)) =>
            wrap(t, x)
          case t =>
            throw new IllegalArgumentException(
              s"Cannot wrap result of $className to have type $t."
            )
        }
      }
    }
    this.typ.lower match {
      case TyStm(t, m) =>
        require(
          c.sameLen(m, 1),
          s"Cannot wrap result of $className into a stream of length $m."
        )
        val vv = Param("v")(v.typ.asInstanceOf[TyStm].t)
        val res = result(vv).tchk()
        StmMap(v, Function(vv, wrap(t, res))())()
      case t =>
        val res = result(v).tchk()
        wrap(t, res)
    }
  }
}
