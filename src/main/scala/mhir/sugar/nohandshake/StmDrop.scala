package mhir.sugar
package nohandshake

import mhir.ir._
import mhir.typecheck._

case class StmDrop(s: Expr, k: Expr, head: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s, k, head)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, k, head) => StmDrop(s, k, head)(typ)
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
      case t           => throw new TypeError(s"Stream in StmDrop has type $t.")
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
    val outLen = ToUnsigned(SafeDiff(n, k)())().tchk()
    this.rebuild(TyStm(innerTyp, outLen), Seq(s, k, head))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val s = this.s.lower
    val k = this.k.lower
    val head = this.head.lower
    val TyStm(innerTyp, _) = this.s.typ
    val perRow = innerTyp.lower match {
      case TyStm(_, n) => n
      case _           => C(1)()
    }
    val totDrop = SafeProd(k, perRow)().tchk().lower
    val TyStm(elemTyp, outLen) = this.typ.lower
    val p = Param("p")(TyStm(elemTyp, -1))
    StmBuild(
      outLen,
      SafeSum(totDrop, C(1)())().tchk().lower,
      head,
      StmData(p)(),
      True,
      Map(),
      Map(
        p -> (s, True, C(0)())
      )
    )()
      .annotate(NoOutputsAfterLastIn)
      .annotate(NoInputsAfterLastOut)
      .annotateWithName("StmDrop")
      .tchk()
  }
}
