package mhir.gen.vhdl
package agilex7

import os.Path

import scala.collection.immutable.ListMap

case class Sumof2UnsignedDspComponent(
    axWidth: Int,
    ayWidth: Int,
    bxWidth: Int,
    byWidth: Int,
    resultWidth: Int,
    pipeline: Int
) extends PredefinedComponent {

  override def entityName: String = "dsp_sumof2_unsigned"

  override def generics: ListMap[String, String] = {
    ListMap(
      "AX_WIDTH" -> this.axWidth.toString,
      "AY_WIDTH" -> this.ayWidth.toString,
      "BX_WIDTH" -> this.bxWidth.toString,
      "BY_WIDTH" -> this.byWidth.toString,
      "RESULT_WIDTH" -> this.resultWidth.toString,
      "PIPELINE" -> this.pipeline.toString
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
      "result"
    )
  }

  override def filesToCopy(vhdlDir: Path): Map[Path, String] = {
    Map(
      vhdlDir / "dsp_sumof2_unsigned.vhd" -> "mhir/gen/vhdl/agilex7/dsp_sumof2_unsigned.vhd",
      vhdlDir / "dsp_sumof2.vhd" -> "mhir/gen/vhdl/agilex7/dsp_sumof2.vhd"
    )
  }

  override def typesUsed: Set[VhdlType] = {
    Set(
      VhdlStdLogic,
      VhdlUnsigned(this.axWidth),
      VhdlUnsigned(this.ayWidth),
      VhdlUnsigned(this.bxWidth),
      VhdlUnsigned(this.byWidth),
      VhdlUnsigned(this.resultWidth)
    )
  }
}
