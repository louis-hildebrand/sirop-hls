package mhir.eval

import mhir.ir._
import mhir.typecheck.TypeCheck

case class TestInput(e: Expr, x: String)(typ: Type)
    extends SyntaxSugar(e)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => TestInput(e, this.x)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val e = this.e.tchk(context, constValues)
    this.rebuild(e.typ, Seq(e))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    throw new IllegalArgumentException(
      s"$className should not be lowered; it is only meant to be used temporarily in the evaluator"
    )
  }
}
