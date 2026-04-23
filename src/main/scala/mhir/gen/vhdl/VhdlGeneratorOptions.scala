package mhir.gen.vhdl

case class VhdlGeneratorOptions(
    topName: String = "top",
    clock: String = "clk",
    reset: String = "rst",
    outName: Option[String] = None,
    deviceFamily: String = "Agilex 7",
    device: String = "AGIC040R39A1E1VC"
)
