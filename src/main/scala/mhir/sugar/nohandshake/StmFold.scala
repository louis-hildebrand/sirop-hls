package mhir.sugar
package nohandshake

import mhir.ir._
import mhir.typecheck._

case class StmFold(s: Expr, z: Expr, f: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s, z, f)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmFold = {
    newChildren match {
      case Seq(s, z, f) => StmFold(s, z, f)(typ)
      case _            => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmFold = {
    val s = this.s.tchk(context, constValues)
    val t1 = s.typ match {
      case TyStm(TyData(t), _) => t
      case TyStm(_: TyStm, _) =>
        throw new TypeError(s"$className does not accept nested streams.")
      case t =>
        throw new TypeError(
          s"First input to $className has type $t."
            + s" Expected a stream."
        )
    }
    val z = this.z.tchk(context, constValues)
    val t2 = z.typ match {
      case TyData(t) => t
      case t =>
        throw new TypeError(
          s"Second input to $className has type $t."
            + s" Expected a data type."
        )
    }
    val f = this.f
      .annotateFunc(TyTuple(t2, t1))
      .tchk(context, constValues)
      .expectType((t2, t1) ->: t2, constValues)
    this.rebuild(TyStm(z.typ, 1), Seq(s, z, f))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val s = this.s.lower
    val TyStm(t1, n) = s.typ
    val z = this.z.lower
    val f = this.f.lower
    val p = Param("p")(TyStm(t1, -1))
    val acc = Param("acc")(z.typ)
    StmBuild(
      1,
      n,
      z,
      FunCall(f, Tuple(acc, StmData(p)())())(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        acc -> (
          z,
          FunCall(f, Tuple(acc, StmData(p)())())(),
          C(1)()
        )
      ),
      Map[Param, (Expr, Expr, Expr)](
        p -> (s, True, C(0)())
      )
    )().tchk()
  }
}
