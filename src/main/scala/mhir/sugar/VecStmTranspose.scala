package mhir.sugar

import mhir.ir._
import mhir.typecheck._

case class VecStmTranspose(v: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(v)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v) => StmVecTranspose(v)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val v = this.v.tchk(context, constValues)
    val (elemTyp, n, m) = v.typ match {
      case TyVec(TyStm(t, m), n) => (t, n, m)
      case typ =>
        throw new TypeError(
          s"input to $className has type $typ. Expected a vector of streams."
        )
    }
    this.rebuild(TyStm(TyVec(elemTyp, n), m), Seq(v))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    this.v.lower
  }
}
