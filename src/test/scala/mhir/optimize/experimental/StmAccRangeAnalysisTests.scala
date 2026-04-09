package mhir.optimize.experimental

import mhir.canonicalize._
import mhir.ir._
import mhir.optimize.{ScalarRange, StmAccRange}
import mhir.sugar._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class StmAccRangeAnalysisTests extends AnyFunSuite {
  test("RangeForNonDecreasingElem") {
    val n = Param("n")(U8)
    val z = Param("z")(U8)
    val a = Param("a")(U8)
    val b = Param("b")(I8)
    val c = Param("c")(I32)
    val stm =
      StmBuild(
        n,
        a,
        True,
        Map[Param, (Expr, Expr)](
          a -> (z, a + 1),
          b -> (C(-3)(I8), b + 9),
          c -> (C(2)(I32), 2 * c + 2)
        )
      )().tchk().lower().asInstanceOf[StmBuild]

    val expectedRanges = StmAccRange(
      Map(
        a -> ScalarRange(Some(z), None),
        b -> ScalarRange(Some(-3), None),
        c -> ScalarRange(Some(2), None)
      )
    )
    val actualRanges = StmAccRangeAnalysis.findAccRanges(stm)
    assert(actualRanges == expectedRanges)
  }

  test("RangeForNonIncreasingElem") {
    val n = Param("n")(U8)
    val z = Param("z")(I8)
    val a = Param("a")(I8)
    val b = Param("b")(I8)
    val c = Param("c")(I32)
    val stm = StmBuild(
      n,
      a,
      True,
      Map[Param, (Expr, Expr)](
        a -> (z, a - 1),
        b -> (C(3)(I8), b - 9),
        c -> (C(-4)(I32), 2 * c + 2)
      )
    )().tchk().lower().asInstanceOf[StmBuild]

    val expectedRanges = StmAccRange(
      Map(
        a -> ScalarRange(None, Some(Sum(1, z)())),
        b -> ScalarRange(None, Some(4)),
        c -> ScalarRange(None, Some(-3))
      )
    )
    val actualRanges = StmAccRangeAnalysis.findAccRanges(stm)
    assert(actualRanges == expectedRanges)
  }

  test("IncreasingAndDecreasing") {
    val n = Param("n")(U8)
    val a = Param("a")(I8)
    val b = Param("b")(I8)
    val stm = StmBuild(
      n,
      Tuple(a, b)(),
      True,
      Map[Param, (Expr, Expr)](
        a -> (C(3)(I8), Mux(a % 2 === 0, a + 1, a - 3)()),
        b -> (C(1)(I8), b * -2)
      )
    )().tchk().lower().asInstanceOf[StmBuild]

    val expectedRanges = StmAccRange(
      Map(
        a -> ScalarRange(None, None),
        b -> ScalarRange(None, None)
      )
    )
    val actualRanges = StmAccRangeAnalysis.findAccRanges(stm)
    assert(actualRanges == expectedRanges)
  }

  test("StreamAccumulator") {
    val n = Param("n")(U8)
    val s = Param("s")(TyStm((I32, I16), n))
    val a = Param("a")(TyStm((I32, I16), -1))
    val stm = StmBuild(
      n,
      StmData(a)(),
      True,
      Map(a -> (s, True))
    )().tchk().lower().asInstanceOf[StmBuild]

    val actualRanges = StmAccRangeAnalysis.findAccRanges(stm)
    assert(actualRanges == StmAccRange(Map()))
  }
}
