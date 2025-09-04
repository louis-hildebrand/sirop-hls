package mhir.main.stored

import mhir.gen.vhdl.TestIO
import mhir.main.aetherling.AetherlingBenchmarkIO

object ProgramIO {
  def apply(name: String): TestIO = {
    if (name.startsWith("map_")) {
      AetherlingBenchmarkIO.vhdlIO("map_1")
    } else {
      ???
    }
  }
}
