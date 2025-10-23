package mhir.main.aetherling

import mhir.ir._
import mhir.ir.typecheck.TypeCheck

object AetherlingPrimitivesIO {
  def apply(testName: String): TestIO = {
    testName match {
      case "lut_1" =>
        AbstractTestIO(
          AbstractTestInput(Seq()),
          AbstractTestOutput(Seq(C(0)(I32), C(15)(I32), C(30)(I32), C(45)(I32)))
        )
      case "tuple_values_1" =>
        AbstractTestIO(
          AbstractTestInput(Seq()),
          AbstractTestOutput(
            Seq(
              Tuple(Tuple(False, True)(), VecLiteral(C(42)(U8), C(26)(U8))())()
                .tchk()
            )
          )
        )
      case "count_ts_1" =>
        AbstractTestIO(
          AbstractTestInput(Seq()),
          AbstractTestOutput((0 until 70).map(3 * _).map(C(_)(U8))).vec(7)
        )
      case "count_tn_1" =>
        AbstractTestIO(
          AbstractTestInput(Seq()),
          AbstractTestOutput((0 until 6).map(2 * _).map(C(_)(U8)), skip = 3)
        )
      case "shift_tn_1" =>
        AbstractTestIO(
          AbstractTestInput(Seq()),
          AbstractTestOutput(
            Seq(Undefined(U8), C(0)(U8), C(3)(U8), C(6)(U8)),
            skip = 2
          )
        )
      case "up_1d_s_1" =>
        AbstractTestIO(
          AbstractTestInput(Seq()),
          AbstractTestOutput(
            Seq(VecLiteral((0 until 8).map(_ => C(-42)(I16)): _*)().tchk())
          )
        )
      case "up_1d_t_1" =>
        AbstractTestIO(
          AbstractTestInput(Seq()),
          AbstractTestOutput((0 until 4).map(_ => C(42)(I16)))
        )
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
      case "unpartition_t_1" =>
        AbstractTestIO(
          AbstractTestInput(Seq()),
          AbstractTestOutput(Seq(C(0)(U16), C(1)(U16), C(2)(U16)))
        )
      case "remove_1_t_1" =>
        AbstractTestIO(
          AbstractTestInput(Seq()),
          AbstractTestOutput(Seq(C(42)(I8), C(0)(I8), C(9)(I8)))
        )
      case _ =>
        ???
    }
  }
}
