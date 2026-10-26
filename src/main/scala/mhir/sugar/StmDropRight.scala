package mhir.sugar

import mhir.ir._
import mhir.typecheck._

case class StmDropRight(s: Expr, k: Expr, head: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s, k, head)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, k, head) => StmDropRight(s, k, head)(typ)
      case _               => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val s = this.s.tchk(context, constValues)
    val (innerTyp, n) = s.typ match {
      case TyStm(t, n) => (t, n)
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
    val outLen = SmartDiff(n, k)().tchk()
    this.rebuild(TyStm(innerTyp, outLen), Seq(s, k, head))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val TyStm(_, outLen) = this.typ
    StmTake(this.s, outLen, this.head, sourceName = "StmDropRight")()
      .tchk()
      .lower
  }
}
