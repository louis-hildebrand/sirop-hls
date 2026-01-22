package mhir.sugar

import mhir.ir.Lowering.{ExprLowering, TypeLowering}
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
    requireType()
    val e = this.e.lower()
    (e.typ, this.targetTyp.lower) match {
      case (t1, t2) if t1 ~= t2 =>
        e
      case (TyVec(t1, IntCst(1)), t2) if t1 ~= t2 =>
        VecAccess(e, 0)().tchk().lower()
      case (t1, TyVec(t2, IntCst(1))) if t1 ~= t2 =>
        VecBuild(1, U8 ::+ (_ => e))().tchk().lower()
      case (TyStm(_, n1), TyStm(t2, n2)) if Type.sameLen(n1, n2) =>
        StmMap(e, Missing ::+ (x => ReshapeSeq(x, t2)()))().tchk().lower()
      case (TyVec(_, n1), TyVec(t2, n2)) if Type.sameLen(n1, n2) =>
        VecMap(e, Missing ::+ (x => ReshapeSeq(x, t2)()))().tchk().lower()
      case (t1, t2) =>
        throw new IllegalArgumentException(
          s"Reshaping from $t1 to $t2 is not currently supported."
        )
    }
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

  override def fullyConsumesInputs: Option[Boolean] = Some(true)
}
