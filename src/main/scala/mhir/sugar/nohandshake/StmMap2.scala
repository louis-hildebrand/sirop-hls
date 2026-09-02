package mhir.sugar
package nohandshake

import mhir.ir._
import mhir.typecheck._

case class StmMap2(s1: Expr, s2: Expr, f: Expr, head: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s1, s2, f, head)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmMap2 = {
    newChildren match {
      case Seq(s1, s2, f, head) => StmMap2(s1, s2, f, head)(typ)
      case _                    => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmMap2 = {
    val s1 = this.s1.tchk(context, constValues)
    val (t1, n1) = s1.typ match {
      case TyStm(TyData(t), n) => (t, n)
      case t =>
        throw new TypeError(
          s"First stream in $className has type $t."
            + " Expected a non-nested stream."
        )
    }
    val s2 = this.s2.tchk(context, constValues)
    val (t2, n2) = s2.typ match {
      case TyStm(TyData(t), n) => (t, n)
      case t =>
        throw new TypeError(
          s"Second stream in $className has type $t."
            + " Expected a non-nested stream."
        )
    }
    if (!c.sameLen(n1, n2, constValues)) {
      throw new TypeError(
        s"Stream lengths in $className do not match: $n1 and $n2."
      )
    }
    val f = this.f.annotateFunc(t1, t2).tchk(context, constValues)
    val t3 = f.typ match {
      case TyArrow(ft1, TyArrow(ft2, TyData(ft3)))
          if ft1.equalsGivenConstants(t1, constValues)
            && ft2.equalsGivenConstants(t2, constValues) =>
        ft3
      case t =>
        throw new TypeError(
          s"Function in $className has type $t."
            + s" Expected a function with input types $t1 and $t2 and whose output type is 'data', not a stream",
          TypeChecker.relevantBindings(constValues, t, t1, t2)
        )
    }
    val head = this.head match {
      case Undefined(Missing) => Undefined(t3)
      case head => head.tchk(context, constValues).expectType(t3, constValues)
    }
    this.rebuild(TyStm(t3, n1), Seq(s1, s2, f, head))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    SL.logger.trace(s"lowering $className: $this")
    requireType()
    val s1 = this.s1.lower
    val s2 = this.s2.lower
    val f = this.f.lower
    val head = this.head.lower
    val TyStm(elemTyp1, n) = s1.typ
    val TyStm(elemTyp2, _) = s2.typ
    val p1 = Param("p1")(TyStm(elemTyp1, -1))
    val p2 = Param("p2")(TyStm(elemTyp2, -1))
    StmBuild(
      n,
      C(1)(),
      head,
      FunCall(FunCall(f, StmData(p1)())(), StmData(p2)())(),
      True,
      Map(),
      Map(
        p1 -> (s1, True, C(0)()),
        p2 -> (s2, True, C(0)())
      )
    )().tchk()
  }
}
