package mhir.main.stored

import mhir.gen.vhdl.{DirectTestInput, DirectTestOutput, TestIO}
import mhir.main.aetherling.AetherlingBenchmarkIO
import mhir.ir._

object ProgramIO {
  def apply(name: String): TestIO = {
    if (name.startsWith("map_")) {
      mapIO
    } else if (name.startsWith("dot_")) {
      dotIO
    } else if (name.startsWith("conv1d_")) {
      conv1dIO
    } else {
      ???
    }
  }

  private def mapIO: TestIO = {
    AetherlingBenchmarkIO.vhdlIO("map_1")
  }

  private def dotIO: TestIO = {
    AetherlingBenchmarkIO.vhdlIO("dot_1_840")
  }

  private def conv1dIO: TestIO = {
    // Square wave with 50% duty cycle and period 8
    val inputs = (0 until 16).map(t => if (t % 8 < 4) -42 else 42)
    val outputs = inputs.sliding(3).map(v => v(2) - v(0)).toSeq
    TestIO(
      Seq(DirectTestInput(inputs.map(C(_)(I8)).map(Some(_)))),
      DirectTestOutput(outputs.map(C(_)(I8)))
    )
  }
}
