package mhir.sugar
package nohandshake

import mhir.ir._
import mhir.typecheck._

case class VecReduce(v: Expr, f: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(v, f)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, f) => VecReduce(v, f)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val v = this.v.tchk(context, constValues)
    val (elemTyp, _) = v.typ match {
      case TyVec(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"vector in $className has type $t. Expected a vector."
        )
    }
    val f = this.f
      .annotateFunc(TyTuple(elemTyp, elemTyp))
      .tchk(context, constValues)
      .expectType((elemTyp, elemTyp) ->: elemTyp, constValues)
    this.rebuild(TyVec(elemTyp, 1), Seq(v, f))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val v = this.v.lower
    val TyVec(_, nExpr) = this.v.typ
    val n = nExpr.getIntCstOrElse({ case e =>
      throw new IllegalArgumentException(
        s"cannot reduce over vector with non-constant size $e"
      )
    })
    if (n <= 0) {
      throw new IllegalArgumentException("cannot reduce over empty vector")
    }
    val f = this.f.lower
    val result = (0 until n.toInt)
      .map(i => VecAccess(v, C(i)())())
      .reduce[Expr]({ case (e1, e2) => FunCall(f, Tuple(e1, e2)())() })
      .tchk()
    VecBuild(C(1)(), U8 ::+ (_ => result))().tchk()
  }
}
