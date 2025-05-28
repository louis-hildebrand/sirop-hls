package opt

import ir._

import org.scalatest.funsuite.AnyFunSuite

class StmAccRemovalPassTests extends AnyFunSuite {
  test("RemoveUnusedCounters") {
    val n = Param("n")()
    val a0 = Param("a")()
    val a1 = Param("a")()
    val a2 = Param("a")()
    val a3 = Param("a")()
    val a4 = Param("a")()
    val stm = StmBuild(
      n,
      Tuple(a2)(),
      True,
      Map[Param, (Expr, Expr)](
        a0 -> (0, 0),
        a1 -> (1, a1 + 1),
        a2 -> (2, a2 + a3),
        a3 -> (3, a3 + a4),
        a4 -> (4, a4 * a2 + 2)
      )
    )()
    val expected = StmBuild(
      n,
      Tuple(a2)(),
      True,
      Map[Param, (Expr, Expr)](
        a2 -> (2, a2 + a3),
        a3 -> (3, a3 + a4),
        a4 -> (4, a4 * a2 + 2)
      )
    )()
    val actual = StmAccRemovalPass.removeUnusedVars(stm)
    assert(actual == expected)
  }

  test("RemoveUnusedStream") {
    val n = Param("n")()
    val s = Param("s")()
    val a0 = Param("a")()
    val a1 = Param("a")()
    val a2 = Param("a")()
    val original = StmBuild(
      n,
      StmData(a1)(),
      a2 >= n,
      Map[Param, (Expr, Expr)](
        a0 -> (s, a2 < n),
        a1 -> (s, a2 >= n),
        a2 -> (0, a2 + 1)
      )
    )()
    val expected = StmBuild(
      n,
      StmData(a1)(),
      a2 >= n,
      Map[Param, (Expr, Expr)](
        a1 -> (s, a2 >= n),
        a2 -> (0, a2 + 1)
      )
    )()
    assert(StmAccRemovalPass.removeUnusedVars(original) == expected)
  }

  test("RemoveConstantVars:EmptyStream") {
    val s = StmBuild(
      5,
      42,
      True,
      Map[Param, (Expr, Expr)]()
    )()
    assert(StmAccRemovalPass.removeConstantVars(s) == s)
  }

  test("RemoveConstantVars:OneInt") {
    val n = Param("n")()
    val a = Param("a")()
    val b = Param("b")()
    val s = StmBuild(
      n,
      Tuple(a, b)(),
      True,
      Map[Param, (Expr, Expr)](
        a -> (1, Mux(a - 1 === 0, 1, b + 42)()),
        b -> (1, b + 1)
      )
    )()
    // `a` will always be 1, so the optimizer should be able to get rid of it
    val expected = StmBuild(
      n,
      Tuple(1, b)(),
      True,
      Map[Param, (Expr, Expr)](
        b -> (1, b + 1)
      )
    )()
    val actual = StmAccRemovalPass.removeConstantVars(s)
    assert(actual == expected)
  }

  test("RemoveConstantVars:TwoInts") {
    val n = Param("n")()
    val a = Param("a")()
    val b = Param("b")()
    val s = StmBuild(
      n,
      Tuple(a, b)(),
      True,
      Map[Param, (Expr, Expr)](
        a -> (1, Mux(a - 1 === 0 && b + 2 === 4, b - 1, b + 42)()),
        b -> (2, Mux(a - 1 === 0 && b + 2 === 4, a + 1, b + 1)())
      )
    )()
    val expected = StmBuild(
      n,
      Tuple(1, 2)(),
      True,
      Map[Param, (Expr, Expr)]()
    )()
    val actual = StmAccRemovalPass.removeConstantVars(s)
    assert(actual == expected)
  }

  test("RemoveConstantVars:EmptyTuples") {
    val n = Param("n")()
    val a = Param("a")()
    val b = Param("b")()
    val c = Param("c")()
    val d = Param("d")()
    val e = Param("e")()
    val s = StmBuild(
      n,
      a * c * d,
      True,
      Map[Param, (Expr, Expr)](
        a -> (0, a + 1),
        b -> (Tuple()(), b),
        c -> (1, c + 2),
        d -> (2, d + 3),
        e -> (Tuple()(), Tuple()())
      )
    )()
    val expected = StmBuild(
      n,
      a * c * d,
      True,
      Map[Param, (Expr, Expr)](
        a -> (0, a + 1),
        c -> (1, c + 2),
        d -> (2, d + 3)
      )
    )()
    val actual = StmAccRemovalPass.removeConstantVars(s)
    assert(actual == expected)
  }

  test("RemoveDuplicateVars") {
    val n = Param("n")()
    val input = Param("input")()
    val s0 = Param("s")()
    val s1 = Param("s")()
    val i0 = Param("i")()
    val i1 = Param("i")()
    val j = Param("j")()
    val original = StmBuild(
      n,
      Tuple(StmData(s0)(), StmData(s1)(), i0, i1)(),
      True,
      Map[Param, (Expr, Expr)](
        s0 -> (input, i0 < n),
        s1 -> (input, i0 < n),
        i0 -> (0, i0 + 2),
        i1 -> (0, i1 + 2),
        j -> (1, j * 2)
      )
    )()

    val optimized = StmAccRemovalPass.deduplicateVars(original)
    val expected = StmBuild(
      n,
      Tuple(StmData(s0)(), StmData(s0)(), i0, i0)(),
      True,
      Map[Param, (Expr, Expr)](
        s0 -> (input, i0 < n),
        i0 -> (0, i0 + 2),
        j -> (1, j * 2)
      )
    )()
    assert(optimized == expected)
  }
}
