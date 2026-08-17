package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar._
import mhir.typecheck._
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
      Tuple()(),
      Undefined(Missing),
      Tuple(a2)(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        a0 -> (0, 0, Tuple()()),
        a1 -> (1, a1 + 1, C(10)()),
        a2 -> (2, a2 + a3, C(2)()),
        a3 -> (3, a3 + a4, C(3)()),
        a4 -> (4, a4 * a2 + 2, C(4)())
      ),
      Map()
    )()
    val expected = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Tuple(a2)(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        a2 -> (2, a2 + a3, C(2)()),
        a3 -> (3, a3 + a4, C(3)()),
        a4 -> (4, a4 * a2 + 2, C(4)())
      ),
      Map()
    )()
    val actual = StmAccRemovalPass.removeUnusedVars(stm)
    assert(actual == expected)
  }

  test("RemoveUnusedStream") {
    val n = Param("n")(U8)
    val s = Param("s")(TyStm(I32, 5))
    val p0 = Param("p")(TyStm(I32, -1))
    val p1 = Param("p")(TyStm(I32, -1))
    val a = Param("a")(U8)
    val original = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      StmData(p1)(),
      a >= n,
      Map[Param, (Expr, Expr, Expr)](
        a -> (0, a + 1, Tuple()())
      ),
      Map[Param, (Expr, Expr, Expr)](
        p0 -> (s, a < n, Tuple()()),
        p1 -> (s, a >= n, Tuple()())
      )
    )()
    val expected = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      StmData(p1)(),
      a >= n,
      Map[Param, (Expr, Expr, Expr)](
        a -> (0, a + 1, Tuple()())
      ),
      Map[Param, (Expr, Expr, Expr)](
        p1 -> (s, a >= n, Tuple()())
      )
    )()
    assert(StmAccRemovalPass.removeUnusedVars(original) == expected)
  }

  test("RemoveConstantVars:EmptyStmBuild") {
    val s = StmBuild(
      5,
      Tuple()(),
      Undefined(Missing),
      42,
      True,
      Map(),
      Map()
    )()
    assert(StmAccRemovalPass.removeConstantAccumulators(s) == s)
  }

  test("RemoveConstantVars:OneInt") {
    val n = Param("n")(U8)
    val a = Param("a")(U8)
    val b = Param("b")(U8)
    val s = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Tuple(a, b)(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        a -> (
          C(1)(U8),
          Mux(a - 1 === 0, C(1)(U8), b + 42)(),
          Tuple()()
        ),
        b -> (C(1)(U8), b + C(1)(U8), Tuple()())
      ),
      Map()
    )().tchk().lower.asInstanceOf[StmBuild]
    // `a` will always be 1, so the optimizer should be able to get rid of it
    val expected = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Tuple(1, b)(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        b -> (1, Sum(b, C(1)(U8))(), Tuple()())
      ),
      Map()
    )()
    val actual = StmAccRemovalPass.removeConstantAccumulators(s)
    assert(actual == expected)
  }

  test("RemoveConstantVars:TwoInts") {
    val n = Param("n")(U8)
    val a = Param("a")(U8)
    val b = Param("b")(U8)
    val s = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Tuple(a, b)(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        a -> (
          C(1)(U8),
          Mux(a - 1 === 0 && b + 2 === 4, ToUnsigned(b - 1)(), b + 42)(),
          Tuple()()
        ),
        b -> (
          C(2)(U8),
          Mux(a - 1 === 0 && b + 2 === 4, a + 1, b + 1)(),
          Tuple()()
        )
      ),
      Map()
    )().tchk().lower.asInstanceOf[StmBuild]
    val expected = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Tuple(1, 2)(),
      True,
      Map(),
      Map()
    )()
    val actual = StmAccRemovalPass.removeConstantAccumulators(s)
    assert(actual == expected)
  }

  test("RemoveConstantVars:EmptyTuples") {
    val n = Param("n")(U8)
    val a = Param("a")(U8)
    val b = Param("b")(TyTuple())
    val c = Param("c")(U8)
    val d = Param("d")(U8)
    val e = Param("e")(TyTuple())
    val s = PartialEvalPass
      .partialEval(
        StmBuild(
          n,
          Tuple()(),
          Undefined(Missing),
          Prod(a, c, d)(),
          True,
          Map[Param, (Expr, Expr, Expr)](
            a -> (C(0)(U8), a + C(1)(U8), Tuple()()),
            b -> (Tuple()(), b, Tuple()()),
            c -> (C(1)(U8), c + C(2)(U8), Tuple()()),
            d -> (C(2)(U8), d + C(3)(U8), Tuple()()),
            e -> (Tuple()(), Tuple()(), Tuple()())
          ),
          Map()
        )().tchk().lower
      )
      .asInstanceOf[StmBuild]
    val expected = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Prod(a, c, d)(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        a -> (C(0)(U8), Sum(C(1)(U8), a)(), Tuple()()),
        c -> (C(1)(U8), Sum(C(2)(U8), c)(), Tuple()()),
        d -> (C(2)(U8), Sum(C(3)(U8), d)(), Tuple()())
      ),
      Map()
    )()
    val actual = StmAccRemovalPass.removeConstantAccumulators(s)
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
      Tuple()(),
      Undefined(Missing),
      Tuple(StmData(s0)(), StmData(s1)(), i0, i1)(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        i0 -> (0, i0 + 2, Tuple()()),
        i1 -> (0, i1 + 2, Tuple()()),
        j -> (1, j * 2, Tuple()())
      ),
      Map[Param, (Expr, Expr, Expr)](
        s0 -> (input, i0 < n, Tuple()()),
        s1 -> (input, i0 < n, Tuple()())
      )
    )()

    val optimized = StmAccRemovalPass.deduplicateVars(original)
    val expected = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Tuple(StmData(s0)(), StmData(s0)(), i0, i0)(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        i0 -> (0, i0 + 2, Tuple()()),
        j -> (1, j * 2, Tuple()())
      ),
      Map[Param, (Expr, Expr, Expr)](
        s0 -> (input, i0 < n, Tuple()())
      )
    )()
    assert(optimized == expected)
  }

  test("RemoveDuplicateVars:DifferentDelay") {
    // Be careful not to consider two variables with different delays to be
    // duplicates
    val a = Param("a")(U8)
    val b = Param("b")(U8)
    val original1 = StmBuild(
      C(42)(),
      C(1)(),
      Undefined(Missing),
      Tuple(a, b)(),
      True,
      Map(
        a -> (C(0)(U8), Sum(C(1)(U8), a)(), C(1)()),
        b -> (C(0)(U8), Sum(C(1)(U8), b)(), C(1)())
      ),
      Map()
    )().tchk().asInstanceOf[StmBuild]
    val simplified1 = StmAccRemovalPass.deduplicateVars(original1)
    assert(mhir.eval.eval(simplified1) == mhir.eval.eval(original1))
    assert(simplified1.accumulators.size == 1)

    // Just change the delay of b
    val (bInit, bNext, _) = original1.accumulators(b)
    val original2 = original1
      .copy(accumulators =
        original1.accumulators + (b -> (bInit, bNext, C(2)()))
      )(typ = Missing, annotations = original1.annotations)
      .tchk()
      .asInstanceOf[StmBuild]
    val simplified2 = StmAccRemovalPass.deduplicateVars(original2)
    assert(mhir.eval.eval(simplified2) == mhir.eval.eval(original2))
    assert(simplified2.accumulators.size == 2)
  }

  test("DeduplicateVars:DifferentUnsignedTypes1") {
    val n = Param("n")(U8)
    val a0 = Param("a")(U8)
    val a1 = Param("a")(U16)
    val a2 = Param("a")(U32)
    val s = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Tuple(a0, a1, a2)(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        a1 -> (C(0)(U16), a1 + C(1)(U16), Tuple()()),
        a0 -> (C(0)(U8), a0 + C(1)(U8), Tuple()()),
        a2 -> (C(0)(U32), a2 + C(1)(U32), Tuple()())
      ),
      Map()
    )().tchk().lower.asInstanceOf[StmBuild]
    val t = Param("t")(U8)
    val expected = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Tuple(t, PadTo(t, 16)(), PadTo(t, 32)())(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        t -> (C(0)(U8), Sum(t, C(1)(I8))(), Tuple()())
      ),
      Map()
    )()
    val actual = StmAccRemovalPass.deduplicateVars(s)
    assert(actual == expected)
    assert(actual.namesDefinedHere.head.typ == U8)
  }

  test("DeduplicateVars:DifferentUnsignedTypes2") {
    val n = Param("n")(U8)
    val u2 = TyUInt(2)
    val original = {
      val a0 = Param("a")(u2)
      val a1 = Param("a")(U32)
      StmBuild(
        n,
        Tuple()(),
        Undefined(Missing),
        Tuple(a0, a1)(),
        True,
        Map[Param, (Expr, Expr, Expr)](
          a0 -> (
            C(0)(u2),
            Mux(a1 === C(2)(U32), C(0)(u2), Sum(C(1)(u2), a0)())(),
            Tuple()()
          ),
          a1 -> (
            C(0)(U32),
            Mux(a1 === C(2)(U32), C(0)(U32), Sum(C(1)(U32), a1)())(),
            Tuple()()
          )
        ),
        Map()
      )().tchk().lower.asInstanceOf[StmBuild]
    }
    val expected = {
      val t = Param("t")(u2)
      StmBuild(
        n,
        Tuple()(),
        Undefined(Missing),
        Tuple(t, PadTo(t, 32)())(),
        True,
        Map[Param, (Expr, Expr, Expr)](
          t -> (
            C(0)(u2),
            Mux(PadTo(t, 32)() === C(2)(U32), C(0)(u2), Sum(C(1)(u2), t)())(),
            Tuple()()
          )
        ),
        Map()
      )().tchk().lower
    }
    val actual = StmAccRemovalPass.deduplicateVars(original)
    assert(actual == expected)
    assert(actual.namesDefinedHere.head.typ == u2)
  }

  test("DeduplicateVars:DifferentSignedTypes") {
    val n = Param("n")(U8)
    val a0 = Param("a")(I8)
    val a1 = Param("a")(I16)
    val a2 = Param("a")(I32)
    val s = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Tuple(a0, a1, a2)(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        a1 -> (C(-10)(I16), a1 + C(1)(I16), Tuple()()),
        a2 -> (C(-10)(I32), a2 + C(1)(I32), Tuple()()),
        a0 -> (C(-10)(I8), a0 + C(1)(I8), Tuple()())
      ),
      Map()
    )().tchk().lower.asInstanceOf[StmBuild]
    val t = Param("t")(I8)
    val expected = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Tuple(t, PadTo(t, 16)(), PadTo(t, 32)())(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        t -> (C(-10)(I8), Sum(t, C(1)(I8))(), Tuple()())
      ),
      Map()
    )()
    val actual = StmAccRemovalPass.deduplicateVars(s)
    assert(actual == expected)
    assert(actual.namesDefinedHere.head.typ == I8)
  }

  test("DeduplicateVars:DifferentSignedAndUnsignedTypes") {
    val u9 = TyUInt(9)
    val n = Param("n")(U8)
    val a0 = Param("a")(U8)
    val a1 = Param("a")(I8)
    val a2 = Param("a")(u9)
    val a3 = Param("a")(I9)
    val s = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Tuple(a0, a1, a2, a3)(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        a0 -> (C(0)(U8), a0 + C(1)(U8), Tuple()()),
        a1 -> (C(0)(I8), a1 + C(1)(I8), Tuple()()),
        a2 -> (C(0)(u9), a2 + C(1)(u9), Tuple()()),
        a3 -> (C(0)(I9), a3 + C(1)(I9), Tuple()())
      ),
      Map()
    )().tchk().lower.asInstanceOf[StmBuild]
    val t = Param("t")(U8)
    val expected = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Tuple(
        t,
        TruncateTo(ToSigned(t)(), 8)(),
        PadTo(t, 9)(),
        ToSigned(t)()
      )(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        t -> (C(0)(U8), Sum(t, C(1)(U8))(), Tuple()())
      ),
      Map()
    )()
    val actual = StmAccRemovalPass.deduplicateVars(s)
    assert(actual == expected)
    assert(actual.namesDefinedHere.head.typ == U8)
  }
}
