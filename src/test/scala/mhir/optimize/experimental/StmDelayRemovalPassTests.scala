package mhir.optimize.experimental

import mhir.ir._
import mhir.optimize.FactSet
import mhir.sugar._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class StmDelayRemovalPassTests extends AnyFunSuite {
  test("SkipFirstCycles") {
    val n = Param("n")(U8)
    val t = Param("t")(I32)
    val s = StmBuild(
      n,
      t,
      t >= n,
      Map[Param, (Expr, Expr)](t -> (C(0)(I32), t + C(1)(I32)))
    )().tchk().lower().asInstanceOf[StmBuild]
    val actual = StmDelayRemovalPass.skipFirstCycles(s, n)(FactSet())
    val expected = StmBuild(
      n,
      t,
      t geq PadTo(ToSigned(n)(), 32)(),
      Map[Param, (Expr, Expr)](
        t -> (PadTo(ToSigned(n)(), 32)(), Sum(t, 1)())
      )
    )()
    assert(actual == expected)
  }
}
