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

  test("RemoveConstantVars:EmptyStream") {
    val s = StmBuild(
      5,
      SSome(42),
      Map[Param, (Expr, Expr)]()
    )
    assert(StmAccRemovalPass.removeConstantVars(s) == s)
  }

  test("RemoveConstantVars:OneInt") {
    val n = Param("n")
    val a = Param("a")
    val b = Param("b")
    val s = StmBuild(
      n,
      SSome(Tuple(a, b)),
      Map[Param, (Expr, Expr)](
        a -> (1, IfThenElse(a - 1 === 0, 1, b + 42)),
        b -> (1, b + 1)
      )
    )
    // `a` will always be 1, so the optimizer should be able to get rid of it
    val expected = StmBuild(
      n,
      SSome(Tuple(1, b)),
      Map[Param, (Expr, Expr)](
        b -> (1, b + 1)
      )
    )
    val actual = StmAccRemovalPass.removeConstantVars(s)
    assert(actual == expected)
  }

  test("RemoveConstantVars:TwoInts") {
    val n = Param("n")
    val a = Param("a")
    val b = Param("b")
    val s = StmBuild(
      n,
      SSome(Tuple(a, b)),
      Map[Param, (Expr, Expr)](
        a -> (1, IfThenElse(a - 1 === 0 && b + 2 === 4, b - 1, b + 42)),
        b -> (2, IfThenElse(a - 1 === 0 && b + 2 === 4, a + 1, b + 1))
      )
    )
    val expected = StmBuild(
      n,
      SSome(Tuple(1, 2)),
      Map[Param, (Expr, Expr)]()
    )
    val actual = StmAccRemovalPass.removeConstantVars(s)
    assert(actual == expected)
  }

  test("RemoveConstantVars:EmptyTuples") {
    val n = Param("n")
    val a = Param("a")
    val b = Param("b")
    val c = Param("c")
    val d = Param("d")
    val e = Param("e")
    val s = StmBuild(
      n,
      SSome(a * c * d),
      Map[Param, (Expr, Expr)](
        a -> (0, a + 1),
        b -> (Tuple(), b),
        c -> (1, c + 2),
        d -> (2, d + 3),
        e -> (Tuple(), Tuple())
      )
    )
    val expected = StmBuild(
      n,
      SSome(a * c * d),
      Map[Param, (Expr, Expr)](
        a -> (0, a + 1),
        c -> (1, c + 2),
        d -> (2, d + 3)
      )
    )
    val actual = StmAccRemovalPass.removeConstantVars(s)
    assert(actual == expected)
  }
}
