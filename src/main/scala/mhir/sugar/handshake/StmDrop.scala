package mhir.sugar
package handshake

import mhir.ir._
import mhir.typecheck._

case class StmDrop(s: Expr, k: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s, k)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, k) => StmDrop(s, k)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val k = this.k.tchk(context, constValues).expectUInt()
    val s = this.s.tchk(context, constValues)
    val (elemTyp, n) = s.typ match {
      case TyStm(t, n) => (t, n)
      case t           => throw new TypeError(s"Stream in StmDrop has type $t.")
    }
    val outLen = ToUnsigned(SafeDiff(n, k)())().tchk()
    this.rebuild(TyStm(elemTyp, outLen), Seq(s, k))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val s = this.s.lower
    val k = this.k.lower
    val TyStm(innerTyp, _) = this.s.typ
    val perRow = innerTyp.lower match {
      case TyStm(_, n) => n
      case _           => C(1)()
    }
    val totDrop = SafeProd(k, perRow)().tchk().lower
    val TyStm(elemTyp, outLen) = this.typ.lower
    val p = Param("p")(TyStm(elemTyp, -1))
    val iTyp = if (totDrop.typ.asInstanceOf[TyAnyInt].contains(1)) {
      totDrop.typ
    } else {
      TyUInt(1)
    }
    val i = Param("i")(iTyp)
    StmBuild(
      outLen,
      SafeSum(totDrop, C(1)())().tchk().lower,
      Undefined(Missing),
      StmData(p)(),
      (i === totDrop).tchk().lower,
      Map(
        i -> (
          C(0)(i.typ),
          Mux(i === totDrop, i, Sum(C(1)(i.typ), i)())().tchk().lower,
          Tuple()()
        )
      ),
      Map(
        p -> (s, True, C(0)())
      )
    )().tchk()
  }
}
