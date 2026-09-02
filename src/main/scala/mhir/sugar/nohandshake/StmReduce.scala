package mhir.sugar
package nohandshake

import mhir.ir._
import mhir.typecheck._

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
    val elemTyp = s.typ match {
      case TyStm(TyData(t), _) => t
      case TyStm(_: TyStm, _) =>
        throw new TypeError(s"$className does not accept nested streams.")
      case t =>
        throw new TypeError(
          s"First input to $className has type $t."
            + s" Expected a stream."
        )
    }
    val f = this.f
      .annotateFunc(TyTuple(elemTyp, elemTyp))
      .tchk(context, constValues)
      .expectType((elemTyp, elemTyp) ->: elemTyp, constValues)
    this.rebuild(TyStm(elemTyp, 1), Seq(s, f))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val s = this.s.lower
    val TyStm(elemTyp, n) = s.typ
    val f = this.f.lower
    val p = Param("p")(TyStm(elemTyp, -1))
    val acc = Param("acc")(elemTyp)
    val first = Param("first")(TyBool)
    StmBuild(
      1,
      n,
      Undefined(Missing),
      FunCall(f, Tuple(acc, StmData(p)())())(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        first -> (True, False, C(1)()),
        acc -> (
          Undefined(acc.typ),
          Mux(first, StmData(p)(), FunCall(f, Tuple(acc, StmData(p)())())())(),
          C(1)()
        )
      ),
      Map[Param, (Expr, Expr, Expr)](
        p -> (s, True, C(0)())
      )
    )().tchk()
  }
}
