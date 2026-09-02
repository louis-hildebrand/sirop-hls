package mhir.sugar
package nohandshake

import mhir.ir._
import mhir.typecheck._

case class StmExtendBy(s: Expr, k: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s, k)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, k) => StmExtendBy(s, k)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val s = this.s.tchk(context, constValues)
    val (innerTyp, inLen) = s.typ match {
      case TyStm(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"input to $className has type $t. Expected a stream."
        )
    }
    val k = this.k.tchk(context, constValues).expectUInt()
    val outLen = SafeSum(inLen, k)()
    this.rebuild(TyStm(innerTyp, outLen), Seq(s, k))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val s = this.s.lower
    val TyStm(elemTyp, outLen) = this.typ.lower
    val p = Param("p")(TyStm(elemTyp, -1))
    StmBuild(
      outLen,
      C(1)(),
      Undefined(Missing),
      StmData(p)(),
      True,
      Map(),
      Map(
        // We'll read past the end of the input stream, but that's OK when the
        // handshake protocol is disabled; we'll just get back undefined.
        p -> (s, True, C(0)())
      )
    )().tchk()
  }
}
