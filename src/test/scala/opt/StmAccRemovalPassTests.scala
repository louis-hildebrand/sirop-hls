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
      SSome(Tuple(a2)())(),
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
      SSome(Tuple(a2)())(),
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
      IfThenElse(a2 < n, NNone(TyInt), SSome(StmNext(a1)().__1)())(),
      Map[Param, (Expr, Expr)](
        a0 -> (s, IfThenElse(a2 < n, StmNext(a0)().__0, a0)()),
        a1 -> (s, IfThenElse(a2 < n, a1, StmNext(a1)().__0)()),
        a2 -> (0, a2 + 1)
      )
    )()
    val expected = StmBuild(
      n,
      IfThenElse(a2 < n, NNone(TyInt), SSome(StmNext(a1)().__1)())(),
      Map[Param, (Expr, Expr)](
        a1 -> (s, IfThenElse(a2 < n, a1, StmNext(a1)().__0)()),
        a2 -> (0, a2 + 1)
      )
    )()
    assert(StmAccRemovalPass.removeUnusedVars(original) == expected)
  }

  test("RemoveConstantVars:EmptyStream") {
    val s = StmBuild(
      5,
      SSome(42)(),
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
      SSome(Tuple(a, b)())(),
      Map[Param, (Expr, Expr)](
        a -> (1, IfThenElse(a - 1 === 0, 1, b + 42)()),
        b -> (1, b + 1)
      )
    )()
    // `a` will always be 1, so the optimizer should be able to get rid of it
    val expected = StmBuild(
      n,
      SSome(Tuple(1, b)())(),
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
      SSome(Tuple(a, b)())(),
      Map[Param, (Expr, Expr)](
        a -> (1, IfThenElse(a - 1 === 0 && b + 2 === 4, b - 1, b + 42)()),
        b -> (2, IfThenElse(a - 1 === 0 && b + 2 === 4, a + 1, b + 1)())
      )
    )()
    val expected = StmBuild(
      n,
      SSome(Tuple(1, 2)())(),
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
      SSome(a * c * d)(),
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
      SSome(a * c * d)(),
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
      SSome(Tuple(StmNext(s0)().__1, StmNext(s1)().__1, i0, i1)())(),
      Map[Param, (Expr, Expr)](
        s0 -> (input, IfThenElse(i0 < n, StmNext(s0)().__0, s0)()),
        s1 -> (input, IfThenElse(i0 < n, StmNext(s1)().__0, s1)()),
        i0 -> (0, i0 + 2),
        i1 -> (0, i1 + 2),
        j -> (1, j * 2)
      )
    )()

    val optimized = StmAccRemovalPass.deduplicateVars(original)
    val expected = StmBuild(
      n,
      SSome(Tuple(StmNext(s0)().__1, StmNext(s0)().__1, i0, i0)())(),
      Map[Param, (Expr, Expr)](
        s0 -> (input, IfThenElse(i0 < n, StmNext(s0)().__0, s0)()),
        i0 -> (0, i0 + 2),
        j -> (1, j * 2)
      )
    )()
    assert(optimized == expected)
  }
}
