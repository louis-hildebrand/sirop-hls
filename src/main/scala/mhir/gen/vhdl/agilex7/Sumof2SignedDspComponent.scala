package mhir.gen.vhdl
package agilex7

import os.Path

import scala.collection.immutable.ListMap

case class Sumof2SignedDspComponent(
    axWidth: Int,
    ayWidth: Int,
    bxWidth: Int,
    byWidth: Int,
    resultWidth: Int,
    pipeline: Int
) extends PredefinedComponent {

  override def entityName: String = "dsp_sumof2_signed"

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
      vhdlDir / "dsp_sumof2_signed.vhd" -> "mhir/gen/vhdl/agilex7/dsp_sumof2_signed.vhd",
      vhdlDir / "dsp_sumof2.vhd" -> "mhir/gen/vhdl/agilex7/dsp_sumof2.vhd"
    )
  }

  override def typesUsed: Set[VhdlType] = {
    Set(
      VhdlStdLogic,
      VhdlSigned(this.axWidth),
      VhdlSigned(this.ayWidth),
      VhdlSigned(this.bxWidth),
      VhdlSigned(this.byWidth),
      VhdlSigned(this.resultWidth)
    )
  }
}
