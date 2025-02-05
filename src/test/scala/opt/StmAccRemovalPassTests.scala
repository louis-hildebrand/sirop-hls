package opt

import ir._

import org.scalatest.funsuite.AnyFunSuite

class StmAccRemovalPassTests extends AnyFunSuite {
  test("RemoveUnusedElems") {
    val n = Param("n")
    val s = Param("s")
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
}
