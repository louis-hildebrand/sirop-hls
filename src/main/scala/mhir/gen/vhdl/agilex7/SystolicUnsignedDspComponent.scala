package mhir.gen.vhdl
package agilex7

import os.Path

import scala.collection.immutable.ListMap

case class SystolicUnsignedDspComponent(
    axWidth: Int,
    ayWidth: Int,
    bxWidth: Int,
    byWidth: Int,
    resultWidth: Int,
    pipeline: Int,
    enableChainIn: Boolean
) extends PredefinedComponent {

  override def entityName: String = "work.dsp_systolic_unsigned"

  override def generics: ListMap[String, String] = {
    ListMap(
      "AX_WIDTH" -> this.axWidth.toString,
      "AY_WIDTH" -> this.ayWidth.toString,
      "BX_WIDTH" -> this.bxWidth.toString,
      "BY_WIDTH" -> this.byWidth.toString,
      "RESULT_WIDTH" -> this.resultWidth.toString,
      "PIPELINE" -> this.pipeline.toString,
      "ENABLE_CHAININ" -> (if (this.enableChainIn) "true" else "false")
    )
  }

  override def portNames: Set[String] = {
    Set(
      "clk",
      "ena",
      "ax",
      "ay",
      "bx",
      "by",
      "chainin",
      "chainout",
      "result"
    )
  }

  override def filesToCopy(vhdlDir: Path): Map[Path, String] = {
    Map(
      vhdlDir / "dsp_systolic_unsigned.vhd" -> "mhir/gen/vhdl/agilex7/dsp_systolic_unsigned.vhd",
      vhdlDir / "dsp_systolic.vhd" -> "mhir/gen/vhdl/agilex7/dsp_systolic.vhd"
    )
  }

  override def typesUsed: Set[VhdlType] = {
    Set(
      VhdlStdLogic,
      VhdlUnsigned(this.axWidth),
      VhdlUnsigned(this.ayWidth),
      VhdlUnsigned(this.bxWidth),
      VhdlUnsigned(this.byWidth),
      VhdlUnsigned(44)
    )
  }
}
