package mhir.sugar

import mhir.ir.{ExprPrinter => EP, _}
import mhir.typecheck._

abstract class BinOpSyntaxSugar(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends SyntaxSugar(e1, e2)(typ) {

  def rebuild: PartialFunction[(Type, Seq[Expr]), Expr]
  def symbol: String

  def checkInputType(typ: Type): Option[String]

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    if (this.rebuild.isDefinedAt((typ, newChildren))) {
      this.rebuild((typ, newChildren))
    } else {
      throw new BadRebuildError(this, newChildren)
    }
  }

  override def displayOneLine(): String = {
    EP.displayOneLineInfixOp(this.children, this.symbol, this.precedence)
  }

  override def displayMultiLine(maxWidth: Int): String = {
    EP.displayMultiLineInfixOp(
      this.children,
      this.symbol,
      maxWidth = maxWidth,
      precedence = this.precedence
    )
  }
}

abstract class RelationalOpSyntaxSugar(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends BinOpSyntaxSugar(e1, e2)(typ) {

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val e1 = this.e1.tchk(context, constValues)
    this.checkInputType(e1.typ) match {
      case Some(err) =>
        throw new TypeError(
          s"invalid type for left-hand side of $symbol : $err"
        )
      case None => ()
    }
    val e2 = this.e2.tchk(context, constValues)
    this.checkInputType(e2.typ) match {
      case Some(err) =>
        throw new TypeError(
          s"invalid type for right-hand side of $symbol : $err"
        )
      case None => ()
    }
    ReshapeData.narrowestCommonAncestor(e1.typ, e2.typ, constValues) match {
      case Some(_) =>
        this.rebuild(TyBool, Seq(e1, e2))
      case None =>
        throw new TypeError(
          s"left-hand side of $className has type ${e1.typ}," +
            s" but right-hand side has type ${e2.typ}"
        )
    }
  }
}

/** Decides whether two values are the same, even ones with slightly different
  * types.
  *
  * @param e1
  *   the first expression to compare.
  * @param e2
  *   the second expression to compare.
  */
case class SmartEqual(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends RelationalOpSyntaxSugar(e1, e2)(typ) {

  override def rebuild: PartialFunction[(Type, Seq[Expr]), Expr] = {
    case (typ, Seq(e1, e2)) => SmartEqual(e1, e2)(typ)
  }

  override def symbol: String = "=="

  override def precedence: Int = Precedence.Equal

  override def checkInputType(typ: Type): Option[String] = {
    if (!typ.isData) Some(s"expected a data type but got $typ") else None
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val e1 = this.e1.lower
    val e2 = this.e2.lower
    val t = ReshapeData.narrowestCommonAncestor(e1.typ, e2.typ).get
    Equal(ReshapeData(e1, t)(), ReshapeData(e2, t)())().tchk().lower
  }
}

/** Decides whether two values are different, even ones with slightly different
  * types.
  *
  * @param e1
  *   the first expression to compare.
  * @param e2
  *   the second expression to compare.
  */
case class SmartNotEqual(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends RelationalOpSyntaxSugar(e1, e2)(typ) {

  override def rebuild: PartialFunction[(Type, Seq[Expr]), Expr] = {
    case (typ, Seq(e1, e2)) => SmartNotEqual(e1, e2)(typ)
  }

  override def symbol: String = "!="

  override def precedence: Int = Precedence.Equal

  override def checkInputType(typ: Type): Option[String] = {
    if (!typ.isData) Some(s"expected a data type but got $typ") else None
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val e1 = this.e1.lower
    val e2 = this.e2.lower
    val t = ReshapeData.narrowestCommonAncestor(e1.typ, e2.typ).get
    Not(Equal(ReshapeData(e1, t)(), ReshapeData(e2, t)())())().tchk().lower
  }
}

/** Decides whether one value is strictly less than another, even if the values
  * have slightly different types.
  *
  * @param e1
  *   the first expression to compare.
  * @param e2
  *   the second expression to compare.
  */
case class SmartLessThan(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends RelationalOpSyntaxSugar(e1, e2)(typ) {

  override def rebuild: PartialFunction[(Type, Seq[Expr]), Expr] = {
    case (typ, Seq(e1, e2)) => SmartLessThan(e1, e2)(typ)
  }

  override def symbol: String = "<"

  override def precedence: Int = Precedence.LessThan

  override def checkInputType(typ: Type): Option[String] = {
    typ match {
      case _: TyAnyInt => None
      case typ         => Some(s"expected an integer but got $typ")
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val e1 = this.e1.lower
    val e2 = this.e2.lower
    val t = ReshapeData.narrowestCommonAncestor(e1.typ, e2.typ).get
    LessThan(ReshapeData(e1, t)(), ReshapeData(e2, t)())().tchk().lower
  }
}

/** Decides whether one value is strictly greater than another, even if the
  * values have slightly different types.
  *
  * @param e1
  *   the first expression to compare.
  * @param e2
  *   the second expression to compare.
  */
case class SmartGreaterThan(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends RelationalOpSyntaxSugar(e1, e2)(typ) {

  override def rebuild: PartialFunction[(Type, Seq[Expr]), Expr] = {
    case (typ, Seq(e1, e2)) => SmartGreaterThan(e1, e2)(typ)
  }

  override def symbol: String = ">"

  override def precedence: Int = Precedence.LessThan

  override def checkInputType(typ: Type): Option[String] = {
    typ match {
      case _: TyAnyInt => None
      case typ         => Some(s"expected an integer but got $typ")
    }
  }
  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val e1 = this.e1.lower
    val e2 = this.e2.lower
    val t = ReshapeData.narrowestCommonAncestor(e1.typ, e2.typ).get
    LessThan(ReshapeData(e2, t)(), ReshapeData(e1, t)())().tchk().lower
  }
}

/** Decides whether one value is less than or equal to another, even if the
  * values have slightly different types.
  *
  * @param e1
  *   the first expression to compare.
  * @param e2
  *   the second expression to compare.
  */
case class SmartLessThanOrEqual(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends RelationalOpSyntaxSugar(e1, e2)(typ) {

  override def rebuild: PartialFunction[(Type, Seq[Expr]), Expr] = {
    case (typ, Seq(e1, e2)) => SmartLessThanOrEqual(e1, e2)(typ)
  }

  override def symbol: String = "<="

  override def precedence: Int = Precedence.LessThan

  override def checkInputType(typ: Type): Option[String] = {
    typ match {
      case _: TyAnyInt => None
      case typ         => Some(s"expected an integer but got $typ")
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val e1 = this.e1.lower
    val e2 = this.e2.lower
    val t = ReshapeData.narrowestCommonAncestor(e1.typ, e2.typ).get
    Not(LessThan(ReshapeData(e2, t)(), ReshapeData(e1, t)())())().tchk().lower
  }
}

/** Decides whether one value is greater than or equal to another, even if the
  * values have slightly different types.
  *
  * @param e1
  *   the first expression to compare.
  * @param e2
  *   the second expression to compare.
  */
case class SmartGreaterThanOrEqual(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends RelationalOpSyntaxSugar(e1, e2)(typ) {

  override def rebuild: PartialFunction[(Type, Seq[Expr]), Expr] = {
    case (typ, Seq(e1, e2)) => SmartGreaterThanOrEqual(e1, e2)(typ)
  }

  override def symbol: String = ">="

  override def precedence: Int = Precedence.LessThan

  override def checkInputType(typ: Type): Option[String] = {
    typ match {
      case _: TyAnyInt => None
      case typ         => Some(s"expected an integer but got $typ")
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val e1 = this.e1.lower
    val e2 = this.e2.lower
    val t = ReshapeData.narrowestCommonAncestor(e1.typ, e2.typ).get
    Not(LessThan(ReshapeData(e1, t)(), ReshapeData(e2, t)())())().tchk().lower
  }
}
