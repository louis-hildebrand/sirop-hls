package mhir.sugar
package handshake

import mhir.ir._
import mhir.sugar.Streamifier.Streamify
import mhir.typecheck._

case class StmMap(
    input: Expr /* Stm<A; n> */,
    f: Expr /* A -> B */
)(typ: Type = Missing) /* Stm<B; n> */
    extends ResolvedSyntaxSugar(input, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmMap = {
    newChildren match {
      case Seq(s, f) => StmMap(s, f)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmMap = {
    val newS = input.tchk(context, constValues)
    val (t1, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t           => throw new TypeError(s"Stream in StmMap has type $t.")
    }
    val newF = f.annotateFunc(t1).tchk(context, constValues)
    val t2 = newF.typ match {
      case TyArrow(t, t2) if t.equalsGivenConstants(t1, constValues) =>
        t2
      case t =>
        throw new TypeError(
          s"function in StmMap has type $t. Expected a function whose input type is $t1",
          TypeChecker.relevantBindings(constValues, t, t1)
        )
    }
    this.rebuild(TyStm(t2, n), Seq(newS, newF))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    SL.logger.trace(s"lowering $className: $this")
    requireType()
    val input = this.input.lower
    val f = this.f.lower.asInstanceOf[Function]
    val TyStm(_, n) = this.typ
    val Function(s, innerStm) = f.streamify
    StmReset(n, innerStm, Map(s -> input))().tchk().lower
  }
}
