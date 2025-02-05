package opt

import ir._

import org.scalatest.funsuite.AnyFunSuite

class StmAccRangeAnalysisTests extends AnyFunSuite {
  test("RangeForNonDecreasingElem") {
    val n = Param("n")
    val z = Param("z")
    val stm = StmBuild(
      n,
      Tuple(z, -3, 2),
      (acc: Expr) =>
        Tuple(Tuple(acc.__0 + 1, acc.__1 + 9, 2 * acc.__2 + 2), SSome(acc.__0))
    )

    val expectedRanges = Seq(
      Range(Some(z), None),
      Range(Some(-3), None),
      Range(Some(2), None)
    )
    assert(StmAccRangeAnalysis.findAccRanges(stm) == expectedRanges)
  }

  test("RangeForNonIncreasingElem") {
    val n = Param("n")
    val z = Param("z")
    val stm = StmBuild(
      n,
      Tuple(z, 3, -4),
      (acc: Expr) =>
        Tuple(Tuple(acc.__0 - 1, acc.__1 - 9, 2 * acc.__2 + 2), SSome(acc.__0))
    )

    val expectedRanges = Seq(
      Range(None, Some(1 + z)),
      Range(None, Some(4)),
      Range(None, Some(-3))
    )
    assert(StmAccRangeAnalysis.findAccRanges(stm) == expectedRanges)
  }

  test("IncreasingAndDecreasing") {
    val n = Param("n")
    val stm = StmBuild(
      n,
      Tuple(3, 1),
      (acc: Expr) =>
        Tuple(
          Tuple(
            IfThenElse(acc.__0 % 2 === 0, acc.__0 + 1, acc.__0 - 3),
            acc.__1 * -2
          ),
          SSome(acc)
        )
    )

    val expectedRanges = Seq(
      Range(None, None),
      Range(None, None)
    )
    assert(StmAccRangeAnalysis.findAccRanges(stm) == expectedRanges)
  }

  test("StreamAccumulator") {
    val s = Param("s")
    val n = Param("n")
    val stm = StmBuild(
      n,
      Tuple(s),
      (acc: Expr) =>
        Tuple(Tuple(StmNext(acc.__0).__0), SSome(StmNext(acc.__0).__1))
    )

    assert(StmAccRangeAnalysis.findAccRanges(stm) == Seq(Range(None, None)))
  }
}
