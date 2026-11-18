package mhir.gen.vhdl
package agilex7

import mhir.canonicalize._
import mhir.gen.vhdl.ir.{EntityInstantiation, Intermediate, IpBlockInst, Target}
import mhir.ir._
import os.Path

import scala.collection.immutable.ListMap

case class AgilexMadd(
    ax: Expr,
    ay: Expr,
    bx: Expr,
    by: Expr,
    pipeline: Int
) extends IpBlockInst {

  assert(this.ax.typ.isInstanceOf[TyAnyInt])
  assert(this.ay.typ.isInstanceOf[TyAnyInt])
  assert(this.bx.typ.isInstanceOf[TyAnyInt])
  assert(this.by.typ.isInstanceOf[TyAnyInt])
  assert(this.pipeline >= 0 && this.pipeline <= 3)

  override def freeVars: Set[Param] = {
    ax.freeVars ++ ay.freeVars ++ bx.freeVars ++ by.freeVars
  }

  override def substitute(subs: Map[Expr, Expr]): AgilexMadd = {
    AgilexMadd(
      this.ax.subPreserveType(subs),
      this.ay.subPreserveType(subs),
      this.bx.subPreserveType(subs),
      this.by.subPreserveType(subs),
      this.pipeline
    )
  }

  def map(f: Expr => Expr): AgilexMadd = {
    AgilexMadd(
      f(this.ax),
      f(this.ay),
      f(this.bx),
      f(this.by),
      this.pipeline
    )
  }

  override def exprChildren: Seq[Expr] = Seq(ax, ay, bx, by)

  override def intermediateChildren: Seq[Intermediate] = Seq.empty

  override def usesVendorIpBlocks: Boolean = true

  def signedOrUnsigned: String = {
    if (this.ax.typ.asInstanceOf[TyAnyInt].signed) "signed" else "unsigned"
  }

  override def resourcesToCopy(vhdlDir: Path): Map[Path, String] = {
    Map(
      vhdlDir / s"dsp_sumof2_${this.signedOrUnsigned}.vhd" -> s"mhir/gen/vhdl/agilex7/dsp_sumof2_${this.signedOrUnsigned}.vhd",
      vhdlDir / "dsp_sumof2.vhd" -> "mhir/gen/vhdl/agilex7/dsp_sumof2.vhd"
    )
  }

  override def toConcurrentStatement(
      target: Target,
      options: VhdlGeneratorOptions,
      enable: String
  ): String = {
    (this.ax.typ, this.ay.typ, this.bx.typ, this.by.typ, target.typ) match {
      case (
            tax: TyAnyInt,
            tay: TyAnyInt,
            tbx: TyAnyInt,
            tby: TyAnyInt,
            tr: TyAnyInt
          ) =>
        assert(
          Seq(tax, tay, tbx, tby, tr).map(_.signed).toSet.size == 1,
          "DSP inputs and outputs should all have the same signedness"
        )
        EntityInstantiation(
          entityName =
            options.fullyQualify(s"dsp_sumof2_${this.signedOrUnsigned}"),
          genericArgs = ListMap(
            "AX_WIDTH" -> tax.w.toString,
            "AY_WIDTH" -> tay.w.toString,
            "BX_WIDTH" -> tbx.w.toString,
            "BY_WIDTH" -> tby.w.toString,
            "RESULT_WIDTH" -> tr.w.toString,
            "PIPELINE" -> this.pipeline.toString
          ),
          portArgs = ListMap(
            "clk" -> options.clock,
            "ena" -> enable,
            "ax" -> VhdlExprGenerator.toVhdl(this.ax),
            "ay" -> VhdlExprGenerator.toVhdl(this.ay),
            "bx" -> VhdlExprGenerator.toVhdl(this.bx),
            "by" -> VhdlExprGenerator.toVhdl(this.by),
            "result" -> target.name
          ),
          label = Param("DSP")().name
        )
      case _ => throw new AssertionError("wrong types in DSP")
    }
  }
}
