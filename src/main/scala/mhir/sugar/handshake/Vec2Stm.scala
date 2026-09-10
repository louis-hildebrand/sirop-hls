package mhir.sugar
package handshake

import mhir.ir._
import mhir.typecheck._

case class Vec2Stm(v: Expr /* Vec<A; n> */ )(
    typ: Type = Missing
) /* Stm<A; n> */
    extends ResolvedSyntaxSugar(v)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Vec2Stm = {
    newChildren match {
      case Seq(v) => Vec2Stm(v)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Vec2Stm = {
    val newV = v.tchk(context, constValues)
    newV.typ match {
      case TyVec(t, n) =>
        this.rebuild(TyStm(t, n), Seq(newV))
      case t => throw new TypeError(s"Vector in Vec2Stm has type $t.")
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val v = this.v.lower
    v.typ match {
      case TyVec(_, n) =>
        val acc = Param("v")(v.typ)
        val TyVec(elemTyp, _) = v.typ
        StmBuild(
          n,
          C(1)(),
          Undefined(elemTyp),
          VecAccess(acc, 0)(),
          True,
          Map[Param, (Expr, Expr, Expr)](
            acc -> (
              v,
              VecShiftLeft(acc, Undefined(elemTyp))().tchk().lower,
              C(1)()
            )
          ),
          Map()
        )()
          .annotate(NoInputsAfterLastOut)
          .annotateWithName(this.className)
          .tchk()
      case TyStm(tv: TyVec, _) =>
        StmMap(v, tv ::+ (v => Vec2Stm(v)()))().tchk().lower
      case t => throw new TypeError(s"Invalid type for vector in Vec2Stm: $t.")
    }
  }
}
