package mhir.gen.vhdl
package agilex7

import mhir.canonicalize._
import mhir.gen.vhdl.ir.{EntityInstantiation, Intermediate, IpBlockInst, Target}
import mhir.ir._
import os.Path

import scala.collection.immutable.ListMap

case class AgilexMac2(
    ax: Expr,
    ay: Expr,
    bx: Expr,
    by: Expr,
    chainin: Expr,
    pipeline: Int
) extends AgilexDsp {

  assert(this.ax.typ.isInstanceOf[TyAnyInt])
  assert(this.ay.typ.isInstanceOf[TyAnyInt])
  assert(this.bx.typ.isInstanceOf[TyAnyInt])
  assert(this.by.typ.isInstanceOf[TyAnyInt])
  assert(this.chainin.typ.isInstanceOf[TyAnyInt])
  assert(
    this.chainin.typ.asInstanceOf[TyAnyInt].w == 44,
    s"chainin for ${this.getClass.getName} must have a bitwidth of 44"
      + s" (found type ${this.chainin.typ} for ${this.chainin})"
  )
  assert(this.pipeline >= 0 && this.pipeline <= 3)

  override def freeVars: Set[Param] = {
    ax.freeVars ++ ay.freeVars ++ bx.freeVars ++ by.freeVars ++ chainin.freeVars
  }

  override def substitute(subs: Map[Expr, Expr]): AgilexMac2 = {
    AgilexMac2(
      this.ax.subPreserveType(subs),
      this.ay.subPreserveType(subs),
      this.bx.subPreserveType(subs),
      this.by.subPreserveType(subs),
      this.chainin.subPreserveType(subs),
      this.pipeline
    )
  }

  def map(f: Expr => Expr): AgilexMac2 = {
    AgilexMac2(
      f(this.ax),
      f(this.ay),
      f(this.bx),
      f(this.by),
      f(this.chainin),
      this.pipeline
    )
  }

  override def exprChildren: Seq[Expr] = {
    Seq(this.ax, this.ay, this.bx, this.by, this.chainin)
  }

  override def intermediateChildren: Seq[Intermediate] = Seq.empty

  override def resourcesToCopy(vhdlDir: Path): Map[Path, String] = {
    Map(
      vhdlDir / s"dsp_systolic_${this.signedOrUnsigned}.vhd" -> s"mhir/gen/vhdl/agilex7/dsp_systolic_${this.signedOrUnsigned}.vhd",
      vhdlDir / "dsp_systolic.vhd" -> "mhir/gen/vhdl/agilex7/dsp_systolic.vhd"
    )
  }

  override def usesVendorIpBlocks: Boolean = true

  def signedOrUnsigned: String = {
    if (this.ax.typ.asInstanceOf[TyAnyInt].signed) "signed" else "unsigned"
  }

  override def toConcurrentStatement(
      target: Target,
      options: VhdlGeneratorOptions,
      enable: String
  ): String = {
    val (resultTyp, enableChainOut) = target.typ match {
      case resTyp: TyAnyInt                    => (resTyp, false)
      case TyTuple(resTyp: TySInt, TySInt(44)) => (resTyp, true)
      case TyTuple(resTyp: TyUInt, TyUInt(44)) => (resTyp, true)
      case typ => throw new AssertionError(s"wrong type for DSP target: $typ")
    }
    (ax.typ, ay.typ, bx.typ, by.typ, chainin.typ, resultTyp) match {
      case (
            tax: TyAnyInt,
            tay: TyAnyInt,
            tbx: TyAnyInt,
            tby: TyAnyInt,
            tch: TyAnyInt,
            tr: TyAnyInt
          ) =>
        assert(
          Seq(tax, tay, tbx, tby, tch, tr).map(_.signed).toSet.size == 1,
          "DSP inputs and outputs should all have the same signedness"
        )
        val (resultVhdl, chainOutVhdl) = if (enableChainOut) {
          val r = VhdlExprGenerator.makeTupleAccess(target.name, 0)
          val c = VhdlExprGenerator.makeTupleAccess(target.name, 1)
          (r, c)
        } else {
          (target.name, "open")
        }
        EntityInstantiation(
          entityName =
            options.fullyQualify(s"dsp_systolic_${this.signedOrUnsigned}"),
          genericArgs = ListMap(
            "AX_WIDTH" -> tax.w.toString,
            "AY_WIDTH" -> tay.w.toString,
            "BX_WIDTH" -> tbx.w.toString,
            "BY_WIDTH" -> tby.w.toString,
            "RESULT_WIDTH" -> tr.w.toString,
            "PIPELINE" -> this.pipeline.toString,
            "ENABLE_CHAININ" -> (this.chainin match {
              case IntCst(0) => "false"
              case _         => "true"
            })
          ),
          portArgs = ListMap(
            "clk" -> options.clock,
            "ena" -> s"bool2sl($enable)",
            "ax" -> VhdlExprGenerator.toVhdl(this.ax),
            "ay" -> VhdlExprGenerator.toVhdl(this.ay),
            "bx" -> VhdlExprGenerator.toVhdl(this.bx),
            "by" -> VhdlExprGenerator.toVhdl(this.by),
            "chainin" -> VhdlExprGenerator.toVhdl(this.chainin),
            "chainout" -> chainOutVhdl,
            "result" -> resultVhdl
          ),
          label = Param("DSP")().name
        )
      case _ => throw new AssertionError("wrong types in DSP")
    }
  }
}
