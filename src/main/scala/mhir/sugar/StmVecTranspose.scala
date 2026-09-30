package mhir.sugar

import mhir.ir._
import mhir.typecheck._

case class StmVecTranspose(s: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s) => StmVecTranspose(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val s = this.s.tchk(context, constValues)
    val (elemTyp, n, m) = s.typ match {
      case TyStm(TyVec(t, m), n) => (t, n, m)
      case typ =>
        throw new TypeError(
          s"input to $className has type $typ. Expected a stream of vectors."
        )
    }
    this.rebuild(TyVec(TyStm(elemTyp, n), m), Seq(s))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    this.s.lower
  }
}
