package mhir.main.aetherling

import mhir.ir._

object AetherlingPrimitivesIO {
  def apply(testName: String): TestIO = {
    testName match {
      case "down_1d_s_1" =>
        AbstractTestIO(
          AbstractTestInput(Seq()),
          AbstractTestOutput(Seq(C(6)(U8))).vec(1)
        )
      case "down_1d_t_1" =>
        AbstractTestIO(
          AbstractTestInput(Seq()),
          AbstractTestOutput(Seq(C(6)(U8)))
        )
      case "count_ts_1" =>
        AbstractTestIO(
          AbstractTestInput(Seq()),
          AbstractTestOutput((0 until 70).map(3 * _).map(C(_)(U8))).vec(7)
        )
      case _ =>
        ???
    }
  }
}
