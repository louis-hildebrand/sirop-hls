package mhir.sugar
package nohandshake

import mhir.ir._
import mhir.typecheck._

case class StmMap(s: Expr, f: Expr, head: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s, f, head)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmMap = {
    newChildren match {
      case Seq(s, f, init) => StmMap(s, f, init)(typ)
      case _               => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmMap = {
    val s = this.s.tchk(context, constValues)
    val (inElemTyp, n) = s.typ match {
      case TyStm(TyData(t), n) => (t, n)
      case typ =>
        throw new TypeError(
          s"input to $className has type $typ. Expected a non-nested stream."
        )
    }
    val f = this.f.annotateFunc(inElemTyp).tchk(context, constValues)
    val outElemTyp = f.typ match {
      case TyArrow(t1, t2) if t1.equalsGivenConstants(inElemTyp, constValues) =>
        t2
      case typ =>
        throw new TypeError(
          s"function in $className has type $typ. Expected a function with input type $inElemTyp."
        )
    }
    val head = this.head match {
      case Undefined(Missing) => Undefined(outElemTyp)
      case _ =>
        this.head.tchk(context, constValues).expectType(outElemTyp, constValues)
    }
    this.rebuild(TyStm(outElemTyp, n), Seq(s, f, head))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val s = this.s.lower
    val f = this.f.lower
    val head = this.head.lower
    val TyStm(inElemTyp, n) = s.typ
    val p = Param("p")(TyStm(inElemTyp, -1))
    StmBuild(
      n = n,
      delay = C(1)(),
      initData = head,
      nextData = FunCall(f, StmData(p)())(),
      valid = True,
      accumulators = Map(),
      producers = Map(p -> (s, True, C(0)()))
    )().tchk()
  }
}
