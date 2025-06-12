package opt

import ir._

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
    val n = Param("n")()
    val z = Param("z")()
    val a = Param("a")()
    val b = Param("b")()
    val c = Param("c")()
    val stm = StmBuild(
      n,
      a,
      True,
      Map[Param, (Expr, Expr)](
        a -> (z, a - 1),
        b -> (3, b - 9),
        c -> (-4, 2 * c + 2)
      )
    )()

    val expectedRanges = StmAccRange(
      Map(
        a -> ScalarRange(None, Some(1 + z)),
        b -> ScalarRange(None, Some(4)),
        c -> ScalarRange(None, Some(-3))
      )
    )
    assert(StmAccRangeAnalysis.findAccRanges(stm) == expectedRanges)
  }

  test("IncreasingAndDecreasing") {
    val n = Param("n")()
    val a = Param("a")()
    val b = Param("b")()
    val stm = StmBuild(
      n,
      Tuple(a, b)(),
      True,
      Map[Param, (Expr, Expr)](
        a -> (3, Mux(a % 2 === 0, a + 1, a - 3)()),
        b -> (1, b * -2)
      )
    )()

    val expectedRanges = StmAccRange(
      Map(
        a -> ScalarRange(None, None),
        b -> ScalarRange(None, None)
      )
    )
    assert(StmAccRangeAnalysis.findAccRanges(stm) == expectedRanges)
  }

  test("StreamAccumulator") {
    val s = Param("s")()
    val n = Param("n")()
    val a = Param("a")()
    val stm = StmBuild(
      n,
      StmData(a)(),
      True,
      Map(a -> (s, True))
    )()

    val expectedRanges = StmAccRange(Map(a -> ScalarRange(None, None)))
    assert(
      StmAccRangeAnalysis.findAccRanges(stm) == expectedRanges
    )
  }
}
