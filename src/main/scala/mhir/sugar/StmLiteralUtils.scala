package mhir.sugar

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.typecheck.TypeCheck

trait StmLiteralUtils {

  private implicit val logger: Logger = Logger(getClass.getName)

  implicit class StmLiteralUtilsImplicit(stm: StmLiteral) {
    def toStmBuild(implicit c: Canonicalizer): StmBuild = {
      this.stm.requireType("translation to StmBuild")
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
      val physical =
        if (mhir.ir.globalOptions.handshake && this.stm.physical.nonEmpty) {
          val elems = this.stm.physical.mkString("[", ", ", "]s")
          logger.warn(
            s"physical prefix of stream literal ($elems) will be ignored because the handshake protocol is enabled"
          )
          Seq()
        } else {
          this.stm.physical
        }
      val (initData, vecElems) = physical match {
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
        C(math.max(1, physical.length))(),
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
