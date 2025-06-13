package opt

import ir._

import org.scalatest.funsuite.AnyFunSuite

class StmAccRemovalPassTests extends AnyFunSuite {
  test("RemoveUnusedCounters") {
    val n = Param("n")(U8)
    val a0 = Param("a")(U16)
    val a1 = Param("a")(U16)
    val a2 = Param("a")(U16)
    val a3 = Param("a")(U16)
    val a4 = Param("a")(U16)
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
    val n = Param("n")(U8)
    val s = Param("s")(TyStm(I32, 5))
    val a0 = Param("a")(TyStm(I32, -1))
    val a1 = Param("a")(TyStm(I32, -1))
    val a2 = Param("a")(U8)
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
    val n = Param("n")(U8)
    val a = Param("a")(U8)
    val b = Param("b")(U8)
    val s = StmBuild(
      n,
      Tuple(a, b)(),
      True,
      Map[Param, (Expr, Expr)](
        a -> (C(1), Mux(a - 1 === 0, C(1), b + 42)()),
        b -> (C(1), b + C(1))
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    // `a` will always be 1, so the optimizer should be able to get rid of it
    val expected = StmBuild(
      n,
      Tuple(1, b)(),
      True,
      Map[Param, (Expr, Expr)](
        b -> (1, Sum(C(1), b)())
      )
    )()
    val actual = StmAccRemovalPass.removeConstantVars(s)
    assert(actual == expected)
  }

  test("RemoveConstantVars:TwoInts") {
    val n = Param("n")(U8)
    val a = Param("a")(U8)
    val b = Param("b")(U8)
    val s = StmBuild(
      n,
      Tuple(a, b)(),
      True,
      Map[Param, (Expr, Expr)](
        a -> (
          C(1),
          Mux(a - 1 === 0 && b + 2 === 4, ToUnsigned(b - 1)(), b + 42)()
        ),
        b -> (C(2), Mux(a - 1 === 0 && b + 2 === 4, a + 1, b + 1)())
      )
    )().tchk().lower().asInstanceOf[StmBuild]
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
    val n = Param("n")(U8)
    val a = Param("a")(U8)
    val b = Param("b")(TyTuple())
    val c = Param("c")(U8)
    val d = Param("d")(U8)
    val e = Param("e")(TyTuple())
    val s = StmBuild(
      n,
      Prod(a, c, d)(),
      True,
      Map[Param, (Expr, Expr)](
        a -> (C(0), a + C(1)),
        b -> (Tuple()(), b),
        c -> (C(1), c + C(2)),
        d -> (C(2), d + C(3)),
        e -> (Tuple()(), Tuple()())
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val expected = StmBuild(
      n,
      Prod(a, c, d)(),
      True,
      Map[Param, (Expr, Expr)](
        a -> (C(0), Sum(C(1), a)()),
        c -> (C(1), Sum(C(2), c)()),
        d -> (C(2), Sum(C(3), d)())
      )
    )()
    val actual = StmAccRemovalPass.removeConstantVars(s)
    assert(actual == expected)
  }

  test("RemoveDuplicateVars") {
    val n = Param("n")(U8)
    val input = Param("input")(TyStm(I8, n / 2))
    val s0 = Param("s")(TyStm(I8, -1))
    val s1 = Param("s")(TyStm(I8, -1))
    val i0 = Param("i")(U8)
    val i1 = Param("i")(U8)
    val j = Param("j")(U8)
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
