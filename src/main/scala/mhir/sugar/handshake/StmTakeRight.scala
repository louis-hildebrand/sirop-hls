package mhir.sugar
package handshake

import mhir.ir._
import mhir.typecheck._

case class StmTakeRight(s: Expr, k: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s, k)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, k) => StmTakeRight(s, k)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val s = this.s.tchk(context, constValues)
    val innerTyp = s.typ match {
      case TyStm(t, _) => t
      case t => throw new TypeError(s"Stream in $className has type $t.")
    }
    val k = this.k.tchk(context, constValues).expectUInt()
    this.rebuild(TyStm(innerTyp, k), Seq(s, k))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val TyStm(_, n) = this.s.typ
    val numToDrop = SmartDiff(n, this.k)().tchk().lower
    StmDrop(this.s, numToDrop, sourceName = this.className)().tchk().lower
  }
}
