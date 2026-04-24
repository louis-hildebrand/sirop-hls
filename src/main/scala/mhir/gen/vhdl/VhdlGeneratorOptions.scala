package mhir.gen.vhdl

import scala.io.Source

/** Settings for VHDL generation.
  *
  * @param topName
  *   the name for the top-level entity.
  * @param clock
  *   the name for the clock port.
  * @param reset
  *   the name for the reset port.
  * @param outName
  *   the name for the output stream.
  * @param handshake
  *   whether to use the handshake (ready/valid) protocol. If `false`, the
  *   `ready` and `valid` ports will be omitted for the entire design.
  * @param deviceFamily
  *   value for the FAMILY setting in the .qsf file.
  * @param device
  *   value for the DEVICE setting in the .qsf file.
  */
case class VhdlGeneratorOptions(
    topName: String = "top",
    clock: String = "clk",
    reset: String = "rst",
    outName: Option[String] = None,
    handshake: Boolean = true,
    deviceFamily: String = "Agilex 7",
    device: String = "AGIC040R39A1E1VC"
) {

  def reservedKeywords: Set[String] = {
    Source.fromResource("mhir/gen/vhdl/reserved_words.txt").getLines().toSet
  }
}
