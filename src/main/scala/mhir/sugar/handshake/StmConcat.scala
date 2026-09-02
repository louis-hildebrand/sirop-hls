package mhir.sugar
package handshake

import mhir.ir._
import mhir.typecheck._

case class StmConcat(stm1: Expr /* Stm<A; n1> */, stm2: Expr /* Stm<A; n2> */ )(
    typ: Type = Missing
) /* Stm<A; n1+n2> */
    extends ResolvedSyntaxSugar(stm1, stm2)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmConcat = {
    newChildren match {
      case Seq(s1, s2) => StmConcat(s1, s2)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmConcat = {
    val newS1 = stm1.tchk(context, constValues)
    val (t1, n1) = newS1.typ match {
      case TyStm(t, n1) => (t, n1)
      case t =>
        throw new TypeError(
          s"First input in StmConcat has type $t. Expected a stream."
        )
    }
    val newS2 = stm2.tchk(context, constValues)
    val n2 = newS2.typ match {
      case TyStm(t2, n2) if t2.equalsGivenConstants(t1, constValues) => n2
      case t =>
        throw new TypeError(
          s"second input in StmConcat has type $t. Expected a stream of $t1",
          TypeChecker.relevantBindings(constValues, t, t1)
        )
    }
    this.rebuild(TyStm(t1, SafeSum(n1, n2)()), Seq(newS1, newS2))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val stm1 = this.stm1.lower
    val stm2 = this.stm2.lower
    val TyStm(elemTyp, n1) = stm1.typ
    val TyStm(_, n2) = stm2.typ
    val s1 = Param("s1")(TyStm(elemTyp, -1))
    val s2 = Param("s2")(TyStm(elemTyp, -1))
    val i = Param("i")(U32)
    StmBuild(
      SafeSum(n1, n2)(),
      1,
      Undefined(elemTyp),
      Mux(i === n1, StmData(s2)(), StmData(s1)())(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        i -> (C(0)(U32), Mux(i === n1, i, i + 1)(), Tuple()())
      ),
      Map[Param, (Expr, Expr, Expr)](
        s1 -> (stm1, i !== n1, Tuple()()),
        s2 -> (stm2, i === n1, Tuple()())
      )
    )()
      .annotate(NoInputsAfterLastOut)
      .annotateWithName("StmConcat")
      .tchk()
      .lower
  }
}
