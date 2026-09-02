package mhir.sugar
package handshake

import mhir.ir._
import mhir.typecheck._

case class Stm2Vec(s: Expr /* Stm<A; n> */ )(
    typ: Type = Missing
) /* Stm<Vec<A; n>; 1> */
    extends ResolvedSyntaxSugar(s)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Stm2Vec = {
    newChildren match {
      case Seq(s) => Stm2Vec(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Stm2Vec = {
    val newS = s.tchk(context, constValues)
    newS.typ match {
      case TyStm(t, n) => this.rebuild(TyStm(TyVec(t, n), 1), Seq(newS))
      case t           => throw new TypeError(s"Stream in Stm2Vec has type $t.")
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val s = this.s.lower
    val (t, n) = this.typ match {
      case TyStm(TyVec(t, n), IntCst(1)) => (t, n)
      case t =>
        throw new IllegalArgumentException(s"Stm2Vec has wrong type $t.")
    }
    val p = Param("s")(TyStm(t, -1))
    val v = Param("v")(TyVec(t, n))
    val ctrTyp = n match {
      case IntCst(n) => TyAnyInt.tightest(0, n)
      case _         => n.typ
    }
    val i = Param("i")(ctrTyp)
    StmBuild(
      1,
      Tuple()(),
      Undefined(Missing),
      VecShiftLeft(v, StmData(p)())().tchk().lower,
      (Sum(C(1)(i.typ), i)() >= n).tchk().lower,
      Map[Param, (Expr, Expr, Expr)](
        v -> (
          Undefined(v.typ).lower,
          VecShiftLeft(v, StmData(p)())().tchk().lower,
          Tuple()()
        ),
        i -> (C(0)(i.typ), Sum(C(1)(i.typ), i)(), Tuple()())
      ),
      Map[Param, (Expr, Expr, Expr)](
        p -> (s, True, Tuple()())
      )
    )().annotateWithName("Stm2Vec").tchk()
  }
}
