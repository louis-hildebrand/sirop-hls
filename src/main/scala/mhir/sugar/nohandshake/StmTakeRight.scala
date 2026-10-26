package mhir.sugar
package nohandshake

import mhir.ir._
import mhir.typecheck._

case class StmTakeRight(s: Expr, k: Expr, head: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s, k, head)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, k, head) => StmTakeRight(s, k, head)(typ)
      case _               => throw new BadRebuildError(this, newChildren)
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
    val elemTyp = innerTyp.lower match {
      case TyStm(TyData(t), _) => t
      case TyData(t)           => t
      case t =>
        throw new AssertionError(
          s"could not find element type because $innerTyp lowered to $t"
        )
    }
    val head = this.head match {
      case Undefined(Missing) => Undefined(elemTyp)
      case head =>
        head.tchk(context, constValues).expectType(elemTyp, constValues)
    }
    this.rebuild(TyStm(innerTyp, k), Seq(s, k, head))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val TyStm(_, n) = this.s.typ
    val numToDrop = SmartDiff(n, this.k)().tchk().lower
    StmDrop(this.s, numToDrop, this.head)().tchk().lower
  }
}
