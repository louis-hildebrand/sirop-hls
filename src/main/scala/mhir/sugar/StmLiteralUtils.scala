package mhir.sugar

import mhir.ir._
import mhir.ir.typecheck.TypeCheck

trait StmLiteralUtils {

  implicit class StmLiteralUtilsImplicit(stm: StmLiteral) {
    def toStmBuild: StmBuild = {
      if (!this.stm.hasType) {
        throw new IllegalArgumentException(
          s"StmLiteral must be type-checked before it can be translated to a StmBuild."
        )
      }
      val isLowered = (
        !this.stm.hasSyntaxSugar
          && this.stm.typ == this.stm.typ.lower
      )
      if (!isLowered) {
        throw new IllegalArgumentException(
          s"StmLiteral must be lowered before it can be translated to a StmBuild."
        )
      }
      val TyStm(t, n) = this.stm.typ
      val lowered = this.stm.elems match {
        case Seq()  => StmBuild(0, Default(t).lower(), True)()
        case Seq(e) => StmBuild(1, e, True)()
        case _      =>
          // The index type must be at least wide enough to fit the value 1, since
          // the index accumulator is updated by i + 1
          val idxTyp = TyAnyInt.tightest(0, math.max(1, this.stm.elems.length))
          val i = Param("i")(idxTyp)
          val v = Param("v")(TyVec(t, n))
          StmBuild(
            this.stm.elems.length,
            VecAccess(v, i)(),
            True,
            Map[Param, (Expr, Expr)](
              i -> (C(0)(idxTyp), Sum(C(1)(idxTyp), i)()),
              v -> (VecLiteral(this.stm.elems: _*)(TyVec(t, n)), v)
            )
          )()
      }
      assert(
        !lowered.hasSyntaxSugar,
        s"converting ${stm.className} to a StmBuild should not introduce any syntax sugar"
      )
      lowered
        .annotate(NoInputsAfterLastOut)
        .annotateWithName("StmLiteral")
        .tchk()
        .asInstanceOf[StmBuild]
    }
  }
}
