package mhir.sugar

import mhir.ir._
import mhir.ir.typecheck.TypeCheck

case class ReshapeSeq(e: Expr, targetTyp: Type)(typ: Type = Missing)
    extends SyntaxSugar(e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => ReshapeSeq(e, this.targetTyp)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val e = this.e.tchk
    // TODO: Check whether the given in/out type combination is supported?
    this.rebuild(targetTyp, Seq(e))
  }

  override def lowerSyntaxSugar(): Expr = {
    ???
  }

  override def displayOneLine(): String = {
    ExprPrinter.displayFunCallOneLine(
      s"ReshapeSeq[${this.targetTyp}]",
      Seq(this.e)
    )
  }

  override def displayMultiLine(maxWidth: Int): String = {
    ExprPrinter.displayFunCallMultiLine(
      s"ReshapeSeq[${this.targetTyp}]",
      Seq(this.e),
      maxWidth = maxWidth
    )
  }
}
