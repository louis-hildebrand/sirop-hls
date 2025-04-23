package opt

import ir._

import org.scalatest.funsuite.AnyFunSuite

class StmAccRangeAnalysisTests extends AnyFunSuite {
  test("RangeForNonDecreasingElem") {
    val n = Param("n")
    val z = Param("z")
    val a = Param("a")
    val b = Param("b")
    val c = Param("c")
    val stm =
      StmBuild(
        n,
        SSome(a)(),
        Map[Param, (Expr, Expr)](
          a -> (z, a + 1),
          b -> (-3, b + 9),
          c -> (2, 2 * c + 2)
        )
      )()

    val expectedRanges = StmAccRange(
      Map(
        a -> ScalarRange(Some(z), None),
        b -> ScalarRange(Some(-3), None),
        c -> ScalarRange(Some(2), None)
      )
    )
    assert(StmAccRangeAnalysis.findAccRanges(stm) == expectedRanges)
  }

  test("RangeForNonIncreasingElem") {
    val n = Param("n")
    val z = Param("z")
    val a = Param("a")
    val b = Param("b")
    val c = Param("c")
    val stm = StmBuild(
      n,
      SSome(a)(),
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
    val n = Param("n")
    val a = Param("a")
    val b = Param("b")
    val stm = StmBuild(
      n,
      SSome(Tuple(a, b)())(),
      Map[Param, (Expr, Expr)](
        a -> (3, IfThenElse(a % 2 === 0, a + 1, a - 3)()),
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
    val s = Param("s")
    val n = Param("n")
    val a = Param("a")
    val stm = StmBuild(
      n,
      SSome(StmNext(a)().__1)(),
      Map(a -> (s, StmNext(a)().__0))
    )()

    val expectedRanges = StmAccRange(Map(a -> ScalarRange(None, None)))
    assert(
      StmAccRangeAnalysis.findAccRanges(stm) == expectedRanges
    )
  }
}
