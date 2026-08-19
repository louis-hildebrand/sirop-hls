package mhir.sugar

import mhir.ir._
import mhir.typecheck.TypeCheck

trait StmLiteralUtils {

  implicit class StmLiteralUtilsImplicit(stm: StmLiteral) {
    def toStmBuild(implicit c: Canonicalizer): StmBuild = {
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
      val TyStm(elemTyp, _) = this.stm.typ
      val delay = math.max(1, this.stm.physical.length)
      val (initData, vecElems) = this.stm.physical match {
        case Seq() =>
          (Undefined(elemTyp), this.stm.logical)
        case Seq(initData, physical @ _*) =>
          (initData, physical ++ this.stm.logical)
      }
      val i = {
        // The index type must be at least wide enough to fit the value 1, since
        // the index accumulator is updated by i + 1
        val idxTyp = TyAnyInt.tightest(0, math.max(1, vecElems.length))
        Param("i")(idxTyp)
      }
      val nextData = if (vecElems.isEmpty) {
        Undefined(elemTyp)
      } else {
        VecAccess(VecLiteral(vecElems: _*)(), i)()
      }
      val lowered = StmBuild(
        this.stm.logical.length,
        C(delay)(),
        initData,
        nextData,
        True,
        Map(
          i -> (C(0)(i.typ), Sum(C(1)(i.typ), i)(), C(1)())
        ),
        Map()
      )().tchk().asInstanceOf[StmBuild]
      assert(
        !lowered.hasSyntaxSugar,
        s"converting ${stm.className} to a StmBuild should not introduce any syntax sugar"
      )
      lowered
        .annotate(NoInputsAfterLastOut)
        .annotateWithName("StmLiteral")
    }
  }
}
