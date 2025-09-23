package mhir.main.stored

import mhir.gen.vhdl.{DirectTestInput, DirectTestOutput, PositionalTestIO}
import mhir.main.aetherling.AetherlingBenchmarkIO
import mhir.ir._

object ProgramIO {
  def apply(name: String): PositionalTestIO = {
    if (name.startsWith("map_")) {
      mapIO
    } else if (name.startsWith("dot_")) {
      dotIO
    } else if (name.startsWith("conv1d_")) {
      conv1dIO
    } else if (name.startsWith("conv2d_")) {
      conv2dIO
    } else if (name.startsWith("convb2b_")) {
      convb2bIO
    } else if (name.startsWith("sharpen_")) {
      sharpenIO
    } else if (name.startsWith("camera_")) {
      cameraIO
    } else if (name.startsWith("matvec_")) {
      matVecMulIO
    } else {
      ???
    }
  }

  private def mapIO: PositionalTestIO = {
    AetherlingBenchmarkIO.vhdlIO("map_1")
  }

  private def dotIO: PositionalTestIO = {
    AetherlingBenchmarkIO.vhdlIO("dot_1_840")
  }

  private def conv1dIO: PositionalTestIO = {
    // Square wave with 50% duty cycle and period 8
    val inputs = (0 until 16).map(t => if (t % 8 < 4) -42 else 42)
    val outputs = inputs.sliding(3).map(v => v(2) - v(0)).toSeq
    PositionalTestIO(
      Seq(DirectTestInput(inputs.map(C(_)(I8)).map(Some(_)))),
      DirectTestOutput(outputs.map(C(_)(I8)))
    )
  }

  private def conv2dIO: PositionalTestIO = {
    AetherlingBenchmarkIO.vhdlIO("bigconv2d_1")
  }

  private def convb2bIO: PositionalTestIO = {
    AetherlingBenchmarkIO.vhdlIO("bigconvb2b_1")
  }

  private def sharpenIO: PositionalTestIO = {
    AetherlingBenchmarkIO.vhdlIO("bigsharpen_1")
  }

  private def cameraIO: PositionalTestIO = {
    AetherlingBenchmarkIO.vhdlIO("bigcamera_1")
  }

  private def matVecMulIO: TestIO = {
    val width = 8
    val height = 8
    val uint = U16
    val mat = (0 until height).map(i => (0 until width).map(j => i + j))
    val vec = 0 until width
    val outputs = mat.map(row => row.zip(vec).map({ case (x, y) => x * y }).sum)
    TestIO(
      Seq(
        DirectTestInput(mat.flatten.map(C(_)(uint)).map(Some(_))),
        DirectTestInput(vec.map(C(_)(uint)).map(Some(_)))
      ),
      DirectTestOutput(outputs.map(C(_)(uint)))
    )
  }
}
