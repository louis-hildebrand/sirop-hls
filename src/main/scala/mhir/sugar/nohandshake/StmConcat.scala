package mhir.sugar
package nohandshake

import mhir.ir._
import mhir.typecheck._

case class StmConcat(stm1: Expr, stm2: Expr, head: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(stm1, stm2, head)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmConcat = {
    newChildren match {
      case Seq(s1, s2, head) => StmConcat(s1, s2, head)(typ)
      case _                 => throw new BadRebuildError(this, newChildren)
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
    val elemTyp = t1.lower match {
      case TyStm(TyData(t), _) => t
      case TyData(t)           => t
      case t =>
        throw new AssertionError(
          s"could not find element type because $t1 lowered to $t"
        )
    }
    val head = this.head match {
      case Undefined(Missing) => Undefined(elemTyp)
      case head =>
        head.tchk(context, constValues).expectType(elemTyp, constValues)
    }
    this.rebuild(TyStm(t1, SafeSum(n1, n2)()), Seq(newS1, newS2, head))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val stm1 = this.stm1.lower
    val stm2 = this.stm2.lower
    val head = this.head.lower
    val TyStm(elemTyp, n1) = stm1.typ
    val TyStm(_, n2) = stm2.typ
    val s1 = Param("s1")(TyStm(elemTyp, -1))
    val s2 = Param("s2")(TyStm(elemTyp, -1))
    val readFirst = Param("read_first")(TyBool)
    StmBuild(
      SafeSum(n1, n2)().tchk().lower,
      1,
      head,
      Mux(readFirst, StmData(s1)(), StmData(s2)())(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        // Start reading second input after `n1` steps
        readFirst -> (True, False, n1)
      ),
      Map[Param, (Expr, Expr, Expr)](
        s1 -> (stm1, True, C(0)()),
        s2 -> (stm2, True, n1)
      )
    )()
      .annotate(NoInputsAfterLastOut)
      .annotateWithName("StmConcat")
      .tchk()
  }
}
