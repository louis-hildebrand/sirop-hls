package mhir.gen.vhdl
package agilex7

import os.Path

import scala.collection.immutable.ListMap

case class MacUnsignedFallbackComponent(
    axWidth: Int,
    ayWidth: Int,
    bWidth: Int,
    resultWidth: Int
) extends PredefinedComponent {

  override def entityName: String = "work.mac_fallback_unsigned"

  override def generics: ListMap[String, String] = {
    ListMap(
      "AX_WIDTH" -> this.axWidth.toString,
      "AY_WIDTH" -> this.ayWidth.toString,
      "B_WIDTH" -> this.bWidth.toString,
      "RESULT_WIDTH" -> this.resultWidth.toString
    )
  }

  override def portNames: Set[String] = {
    Set("clk", "ena", "ax", "ay", "b", "result")
  }

  override def filesToCopy(vhdlDir: Path): Map[Path, String] = {
    Map(
      vhdlDir / "mac_fallback_unsigned.vhd" -> "mhir/gen/vhdl/agilex7/mac_fallback_unsigned.vhd"
    )
  }

  override def typesUsed: Set[VhdlType] = {
    Set(
      VhdlStdLogic,
      VhdlUnsigned(this.axWidth),
      VhdlUnsigned(this.ayWidth),
      VhdlUnsigned(this.bWidth),
      VhdlUnsigned(this.resultWidth)
    )
  }
}
