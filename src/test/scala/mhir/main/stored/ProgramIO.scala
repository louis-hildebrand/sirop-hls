package mhir.main.stored

import mhir.gen.vhdl.{DirectTestInput, DirectTestOutput, PositionalTestIO}
import mhir.main.aetherling.AetherlingBenchmarkIO
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

object ProgramIO {
  def apply(name: String): PositionalTestIO = {
    if (name.startsWith("map_")) {
      mapIO
    } else if (name == "shir:map") {
      mapIO
    } else if (name.startsWith("dot_")) {
      dotIO
    } else if (name == "shir:dot") {
      dotIO
    } else if (name.startsWith("conv1d_")) {
      conv1dIO
    } else if (name == "shir:conv1d") {
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
      val parStr = {
        val suffix = name.substring("matvec_".length)
        val prefix = suffix.takeWhile(_.isDigit)
        if (prefix.isEmpty) {
          throw new IllegalArgumentException(
            s"Unrecognized benchmark name: $name (missing throughput)"
          )
        }
        prefix
      }
      val par = parStr.toInt
      matVecMulIO(
        width = Program.MatVecSize,
        height = Program.MatVecSize,
        par = par,
        uint = U16
      )
    } else if (name == "shir:matvec") {
      matVecMulIO(width = 256, height = 256, par = 1, uint = U16)
    } else if (name.startsWith("sqrt_")) {
      sqrtIO
    } else if (name.startsWith("sobel_")) {
      sobelIO
    } else {
      throw new IllegalArgumentException(s"unknown program: $name")
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

  /** Matrix-vector multiplication.
    *
    * @param width
    *   the number of columns in the matrix.
    * @param height
    *   the number of rows in the matrix.
    * @param par
    *   the degree of spatial parallelism.
    * @param uint
    *   the type of the elements in the matrix.
    */
  def matVecMulIO(
      width: Int,
      height: Int,
      par: Int,
      uint: TyUInt
  ): PositionalTestIO = {
    val mat = (0 until height).map(i => (0 until width).map(j => (i + j) % 16))
    val vec = (0 until width).map(_ % 16)
    val outputs = mat.map(row => row.zip(vec).map({ case (x, y) => x * y }).sum)
    PositionalTestIO(
      Seq(
        DirectTestInput(
          mat.flatten
            .map(C(_)(uint))
            .grouped(par)
            .map(xs => VecLiteral(xs: _*)().tchk())
            .map(Some(_))
            .toSeq
        ),
        DirectTestInput(
          vec
            .map(C(_)(uint))
            .grouped(par)
            .map(xs => VecLiteral(xs: _*)().tchk())
            .map(Some(_))
            .toSeq
        )
      ),
      DirectTestOutput(outputs.map(C(_)(uint)))
    )
  }

  private def sqrtIO: PositionalTestIO = {
    AetherlingBenchmarkIO.vhdlIO("sqrt_1")
  }

  private def sobelIO: PositionalTestIO = {
    AetherlingBenchmarkIO.vhdlIO("bigsobel_1")
  }
}
