package mhir.sugar
package handshake

import mhir.ir._
import mhir.sugar.Streamifier.Streamify
import mhir.typecheck._

case class StmMap2(s1: Expr, s2: Expr, f: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s1, s2, f)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmMap2 = {
    newChildren match {
      case Seq(s1, s2, f) => StmMap2(s1, s2, f)(typ)
      case _              => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmMap2 = {
    val s1 = this.s1.tchk(context, constValues)
    val (t1, n1) = s1.typ match {
      case TyStm(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"First stream in $className has type $t."
            + " Expected a stream."
        )
    }
    val s2 = this.s2.tchk(context, constValues)
    val (t2, n2) = s2.typ match {
      case TyStm(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"Second stream in $className has type $t."
            + " Expected a stream."
        )
    }
    if (!c.sameLen(n1, n2, constValues)) {
      throw new TypeError(
        s"Stream lengths in $className do not match: $n1 and $n2."
      )
    }
    val f = this.f.annotateFunc(t1, t2).tchk(context, constValues)
    val t3 = f.typ match {
      case TyArrow(ft1, TyArrow(ft2, ft3))
          if ft1.equalsGivenConstants(t1, constValues)
            && ft2.equalsGivenConstants(t2, constValues) =>
        ft3
      case t =>
        throw new TypeError(
          s"Function in $className has type $t."
            + s" Expected a function with input types $t1 and $t2",
          TypeChecker.relevantBindings(constValues, t, t1, t2)
        )
    }
    this.rebuild(TyStm(t3, n1), Seq(s1, s2, f))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    SL.logger.trace(s"lowering $className: $this")
    requireType()
    val s1 = this.s1.lower
    val s2 = this.s2.lower
    val f = this.f.lower.asInstanceOf[Function]
    val n = this.typ.asInstanceOf[TyStm].n
    val Function(s1Param, Function(s2Param, innerStm)) = f.streamify
    StmReset(n, innerStm, Map(s1Param -> s1, s2Param -> s2))().tchk().lower
  }
}
