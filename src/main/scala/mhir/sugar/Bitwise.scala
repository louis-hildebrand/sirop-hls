package mhir.sugar

import mhir.ir.{ExprPrinter => EP, _}
import mhir.typecheck.{TypeCheck, TypeError}

case class BitwiseAnd(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends BinOpSyntaxSugar(e1, e2)(typ) {

  override def rebuild: PartialFunction[(Type, Seq[Expr]), Expr] = {
    case (typ, Seq(e1, e2)) => BitwiseAnd(e1, e2)(typ)
  }

  override def symbol: String = "&"

  override def precedence: Int = Precedence.BitwiseAnd

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val e1 = this.e1.tchk(context, constValues).expectData()
    val e2 = this.e2.tchk(context, constValues).expectType(e1.typ, constValues)
    this.rebuild(e1.typ, Seq(e1, e2))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    InterpretAs(
      VecMap2(
        Bits(this.e1)(),
        Bits(this.e2)(),
        TyBool ::+ (x => TyBool ::+ (y => x && y))
      )(),
      this.e1.typ
    )().tchk().lower
  }
}

case class BitwiseOr(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends BinOpSyntaxSugar(e1, e2)(typ) {

  override def rebuild: PartialFunction[(Type, Seq[Expr]), Expr] = {
    case (typ, Seq(e1, e2)) => BitwiseOr(e1, e2)(typ)
  }

  override def symbol: String = "|"

  override def precedence: Int = Precedence.BitwiseOr

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val e1 = this.e1.tchk(context, constValues).expectData()
    val e2 = this.e2.tchk(context, constValues).expectType(e1.typ, constValues)
    this.rebuild(e1.typ, Seq(e1, e2))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    InterpretAs(
      VecMap2(
        Bits(this.e1)(),
        Bits(this.e2)(),
        TyBool ::+ (x => TyBool ::+ (y => x || y))
      )(),
      this.e1.typ
    )().tchk().lower
  }

}

case class BitwiseNot(e: Expr)(typ: Type = Missing)
    extends SyntaxSugar(e)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => BitwiseNot(e)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val e = this.e.tchk(context, constValues).expectData()
    this.rebuild(e.typ, Seq(e))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    InterpretAs(VecMap(Bits(this.e)(), TyBool ::+ (x => !x))(), this.typ)()
      .tchk()
      .lower
  }

  override def precedence: Int = Precedence.Not

  override def displayOneLine(): String = {
    s"~${EP.displayOneLine(this.e, this.precedence)}"
  }

  override def displayMultiLine(maxWidth: Int): String = {
    s"~${EP.displayMultiLine(this.e, maxWidth = maxWidth, this.precedence)}"
  }
}
