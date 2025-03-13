package opt

import ir._

import org.scalatest.funsuite.AnyFunSuite

class StmAccRemovalPassTests extends AnyFunSuite {
  test("RemoveUnusedCounters") {
    val n = Param("n")
    val stm = StmBuild(
      n,
      Tuple(0, 1, 2, 3),
      (acc: Expr) =>
        Tuple(
          Tuple(0, acc.__1 + 1, acc.__2 + acc.__3, acc.__3 + 2),
          SSome(Tuple(acc.__2))
        )
    )
    val expected = StmBuild(
      n,
      Tuple(2, 3),
      (acc: Expr) =>
        Tuple(
          Tuple(acc.__0 + acc.__1, acc.__1 + 2),
          SSome(Tuple(acc.__0))
        )
    )
    assert(StmAccRemovalPass.removeUnusedElems(stm) == expected)
  }

  test("RemoveUnusedStream") {
    val n = Param("n")
    val s = Param("s")
    val original = StmBuild(
      n,
      Tuple(s, s, 0),
      (acc: Expr) =>
        IfThenElse(
          acc.__2 < n,
          Tuple(
            Tuple(StmNext(acc.__0).__0, acc.__1, 1 + acc.__2),
            NNone
          ),
          Tuple(
            Tuple(acc.__0, StmNext(acc.__1).__0, 1 + acc.__2),
            SSome(StmNext(acc.__1).__1)
          )
        )
    )
    val expected = StmBuild(
      n,
      Tuple(s, 0),
      (acc: Expr) =>
        IfThenElse(
          acc.__1 < n,
          Tuple(
            Tuple(acc.__0, 1 + acc.__1),
            NNone
          ),
          Tuple(
            Tuple(StmNext(acc.__0).__0, 1 + acc.__1),
            SSome(StmNext(acc.__0).__1)
          )
        )
    )
    assert(StmAccRemovalPass.removeUnusedElems(original) == expected)
  }
}
