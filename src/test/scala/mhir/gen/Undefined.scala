package mhir.gen

import mhir.ir._
import mhir.ir.typecheck.TypeError

case class Undefined(override val typ: Type) extends SyntaxSugar()(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq() => this
      case _     => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    if (!this.typ.isData) {
      throw new TypeError(
        s"Cannot have undefined value of non-data type ${this.typ}."
      )
    }
    this
  }

  override def lowerSyntaxSugar(): Expr = {
    throw new NotImplementedError(s"$className should not be lowered.")
  }

  override def displayOneLine(): String = s"undefined[$typ]"

  override def displayMultiLine(maxWidth: Int): String = s"undefined[$typ]"
}
