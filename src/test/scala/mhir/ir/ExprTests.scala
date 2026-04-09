package mhir.ir

import mhir.canonicalize._
import mhir.optimize.PartialEvalPass
import mhir.sugar._
import mhir.typecheck.TypeCheck
import org.scalatest.funsuite.AnyFunSuite

class ExprTests extends AnyFunSuite {

  test("FreeVars:Function") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)

    val f = Function(x, Sum(x, y)())()
    assert(f.freeVars == Set(y))

    val e = f(x)
    assert(e.freeVars == Set(x, y))
  }

  test("FreeVars:StmBuild") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val z = Param("z")(U8)
    val c = Param("c")(TyBool)

    // Accumulator variables are bound within the data, valid, and next
    // expressions.
    val s0 = StmBuild(
      5,
      Prod(x, y)(),
      (x % 2 === 0) && c,
      Map[Param, (Expr, Expr)](
        x -> (C(0)(U8), Sum(x, z)())
      )
    )().tchk()
    assert(s0.freeVars == Set(y, z, c))

    // Accumulator variables are not found within the stream length.
    val s1 = StmBuild(
      x,
      Prod(x, y)(),
      (x % 2 === 0) && c,
      Map[Param, (Expr, Expr)](
        x -> (C(0)(U8), Sum(x, z)())
      )
    )().tchk()
    assert(s1.freeVars == Set(x, y, z, c))

    // Accumulator variables are not found within the accumulator seeds.
    val s2 = StmBuild(
      5,
      Prod(x, y)(),
      (x % 2 === 0) && c,
      Map[Param, (Expr, Expr)](
        x -> (x, Sum(x, z)())
      )
    )().tchk()
    assert(s2.freeVars == Set(x, y, z, c))
  }

  test("FreeVars:LetStm") {
    val x = Param("x")(TyStm(U8, 2))
    val s = Param("s")(TyStm(U8, 2))

    val e0 = LetStm(1, x, s, x)()
    assert(e0.freeVars == Set(s))

    val e1 = LetStm(1, x, x, x)()
    assert(e1.freeVars == Set(x))
  }

  test("VecAccess:Equals") {
    val v = Param("v")()
    val e0 = VecAccess(v, C(1)(U8))()
    val e1 = VecAccess(v, C(1)(U32))()
    val e2 = VecAccess(v, C(2)(U32))()
    assert(e0 == e1)
    assert(e1 == e0)
    assert(e0 != e2)
    assert(e2 != e0)
  }

  test("Function:Equals") {
    val f = {
      val x = Param("x")(U8)
      Function(x, (x + 1) * (x + 2))()
    }
    val g = {
      val y = Param("y")()
      Function(y, (y + 1) * (y + 2))()
    }
    assert(f == g)
    assert(g == f)
    assert(f.hashCode == g.hashCode)
  }

  test("Function:NotEquals:DifferentBody") {
    val x = Param("x")(U8)
    val f = Function(x, (x + 1) * (x + 2))()
    val y = Param("y")(U8)
    val g = Function(y, (y + 1) * (x + 2))()
    assert(f != g)
    assert(g != f)
  }

  test("LetStm:Equals") {
    val in = StmBuild(5, C(42)(U16), True)()
    val s0 = Param("s")()
    val s1 = Param("s")()
    val e0 = LetStm(1, s0, in, StmZip(s0, s0)())()
    val e1 = LetStm(1, s1, in, StmZip(s1, s1)())()
    assert(e0 == e1)
    assert(e1 == e0)
    assert(e0.hashCode == e1.hashCode)
  }

  test("LetStm:NotEquals:DifferentInput") {
    val in0 = StmBuild(5, C(42)(I8), True)()
    val in1 = StmBuild(5, C(-1)(I8), True)()
    val s0 = Param("s")()
    val s1 = Param("s")()
    val e0 = LetStm(1, s0, in0, StmZip(s0, s0)())()
    val e1 = LetStm(1, s1, in1, StmZip(s1, s1)())()
    assert(e0 != e1)
    assert(e1 != e0)
  }

  test("LetStm:NotEquals:DifferentOutput") {
    val in = StmBuild(5, C(42)(U16), True)()
    val s0 = Param("s")()
    val s1 = Param("s")()
    val e0 = LetStm(1, s0, in, StmZip(s0, s0)())()
    val e1 = LetStm(1, s1, in, StmZip(s1, s0)())()
    assert(e0 != e1)
    assert(e1 != e0)
  }

  test("StmBuild:Equals:NoAccumulatorVars") {
    val v = Param("v")()
    val s1 =
      StmBuild(3, VecAccess(v, C(1)(U8))(), True, Map[Param, (Expr, Expr)]())()
    val s2 =
      StmBuild(3, VecAccess(v, C(1)(U16))(), True, Map[Param, (Expr, Expr)]())()
    assert(s1 == s2)
    assert(s2 == s1)
    assert(s1.hashCode == s2.hashCode)
  }

  test("StmBuild:Equals:OneAccumulatorVar") {
    val n = Param("n")()
    val i = Param("i")()
    val j = Param("j")()
    val z = Param("z")()
    val s1 =
      StmBuild(n, i, True, Map[Param, (Expr, Expr)](i -> (z, i + 1)))()
    val s2 =
      StmBuild(n, j, True, Map[Param, (Expr, Expr)](j -> (z, j + 1)))()
    assert(s1 == s2)
    assert(s2 == s1)
    assert(s1.hashCode == s2.hashCode)
  }

  test("StmBuild:Equals:TwoAccumulatorVars") {
    val n = Param("n")()
    val i = Param("i")()
    val j = Param("j")()
    val z = Param("z")()
    val s1 =
      StmBuild(
        n,
        j - i,
        True,
        Map[Param, (Expr, Expr)](i -> (z, i + 1), j -> (0, j * 2))
      )()
    val s2 =
      StmBuild(
        n,
        i - j,
        True,
        Map[Param, (Expr, Expr)](j -> (z, j + 1), i -> (0, i * 2))
      )()
    assert(s1 == s2)
    assert(s2 == s1)
    assert(s1.hashCode == s2.hashCode)
  }

  test("StmBuild:Equals:ThreeAccumulatorVars") {
    val n = Param("n")()
    val a = Param("a")()
    val c = Param("c")()
    val d = Param("d")()
    val s1 = StmBuild(
      n,
      a * c * d,
      True,
      Map[Param, (Expr, Expr)](
        a -> (0, a + 1),
        c -> (1, c + 2),
        d -> (2, d + 3)
      )
    )()
    val s2 = StmBuild(
      n,
      a * c * d,
      True,
      Map[Param, (Expr, Expr)](
        a -> (0, a + 1),
        d -> (2, d + 3),
        c -> (1, c + 2)
      )
    )()
    assert(s1 == s2)
    assert(s2 == s1)
    assert(s1.hashCode == s2.hashCode)
  }

  test("StmBuild:Equals:TypedAndUntyped") {
    val i = Param("i")(U8)
    val untyped =
      StmBuild(
        4,
        i,
        True,
        Map[Param, (Expr, Expr)](i -> (IntCst(0)(U8), i + 1))
      )()
    val typed = untyped.tchk()
    assert(typed.hashCode == untyped.hashCode)
    assert(typed == untyped)
    assert(untyped == typed)
  }

  test("StmBuild:Equals:Shadowing") {
    val s = Param("s")()
    val stm1 = {
      val a = Param("a")()
      StmBuild(
        10,
        StmData(a)(),
        True,
        Map[Param, (Expr, Expr)](a -> (s, True))
      )()
    }
    val f1 = Function(s, stm1)()
    val stm2 = StmBuild(
      10,
      StmData(s)(),
      True,
      Map[Param, (Expr, Expr)](s -> (s, True))
    )()
    val f2 = Function(s, stm2)()
    assert(stm1 == stm2)
    assert(f1 == f2)
  }

  test("StmBuild:NotEquals:DifferentLengths") {
    val i = Param("i")()
    val j = Param("j")()
    val z = Param("z")()
    val s1 =
      StmBuild(i, i, True, Map[Param, (Expr, Expr)](i -> (z, i + 1)))()
    val s2 =
      StmBuild(j, j, True, Map[Param, (Expr, Expr)](j -> (z, j + 1)))()
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuild:NotEquals:DifferentOutputs1") {
    val n = Param("n")()
    val i = Param("i")()
    val j = Param("j")()
    val z = Param("z")()
    val s1 =
      StmBuild(n, i, True, Map[Param, (Expr, Expr)](i -> (z, i + 1)))()
    val s2 =
      StmBuild(n, i, True, Map[Param, (Expr, Expr)](j -> (z, j + 1)))()
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuild:NotEquals:DifferentOutputs2") {
    val n = Param("n")()
    val i = Param("i")()
    val j = Param("j")()
    val z = Param("z")()
    val s1 =
      StmBuild(
        n,
        i - j,
        True,
        Map[Param, (Expr, Expr)](i -> (z, i + 1), j -> (0, j * 2))
      )()
    val s2 =
      StmBuild(
        n,
        i - j,
        True,
        Map[Param, (Expr, Expr)](j -> (z, j + 1), i -> (0, i * 2))
      )()
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuild:NotEquals:DifferentSeeds") {
    val n = Param("n")()
    val i = Param("i")()
    val j = Param("j")()
    val s1 =
      StmBuild(n, i, True, Map[Param, (Expr, Expr)](i -> (i, i + 1)))()
    val s2 =
      StmBuild(n, j, True, Map[Param, (Expr, Expr)](j -> (j, j + 1)))()
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuild:NotEquals:DifferentStructures") {
    val n = Param("n")()
    val i = Param("i")()
    val j = Param("j")()
    val z = Param("z")()
    val s1 = StmBuild(
      n,
      i,
      True,
      Map[Param, (Expr, Expr)](
        i -> (z, i + 1),
        j -> (z, j - 1)
      )
    )()
    val s2 = StmBuild(
      n,
      j,
      True,
      Map[Param, (Expr, Expr)](
        i -> (z, j + 1),
        j -> (z, i - 1)
      )
    )()
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuild:RenameVars") {
    val n = Param("n")()
    val z = Param("z")()
    val a = Param("a")()
    val r0 = Param("r0")()
    val r1 = Param("r1")()
    val original = StmBuild(
      n,
      z + a + r0 * r1,
      True,
      Map[Param, (Expr, Expr)](
        a -> (z, a + 1),
        r0 -> (1, a * 2),
        r1 -> (z, a - 1)
      )
    )()
    val renamed = original.renameVars
    assert(original == renamed)
    assert(original.accVars.intersect(renamed.accVars).isEmpty)
  }

  test("StmBuild:AddOutputCounter") {
    val n = Param("n")(U8)
    val data = Param("data")(TyTuple(U8, TyBool))
    val valid = Param("valid")(TyBool)
    val outCtr = Param("out_ctr")(U8)
    val s = StmBuild(
      n,
      data,
      valid,
      Map[Param, (Expr, Expr)](outCtr -> (IntCst(10)(U8), outCtr * 2))
    )().tchk().asInstanceOf[StmBuild]

    val actual = s.addOutputCounter(outCtr)

    // The existing bound variable should be renamed
    val i = Param("i")()
    val expectedOutCtrSeed = IntCst(0)()
    val expectedOutCtrNext = Mux(valid, Sum(C(1)(), outCtr)(), outCtr)()
    val expected = StmBuild(
      n,
      data,
      valid,
      Map[Param, (Expr, Expr)](
        i -> (IntCst(10)(U8), i * 2),
        outCtr -> (expectedOutCtrSeed, expectedOutCtrNext)
      )
    )()
    assert(actual == expected)
    // The new stream must use the exact parameter `outCtr`, not a fresh one
    assert(actual.seedByVar(outCtr) == expectedOutCtrSeed)
    assert(actual.nextByVar(outCtr) == expectedOutCtrNext)
  }

  test("StmBuild:AddInputCounter") {
    val n = Param("n")(U8)
    val f = Param("f")()
    val g = Param("g")()
    val input = Param("input")()
    val i = Param("i")(U8)
    val inCtr = Param("in_ctr")(U8)
    val s = Param("s")(TyStm(U8, n))
    val context = Map(
      f -> TyArrow(U8, TyBool),
      g -> TyArrow(U8, TyBool),
      input -> TyStm(U8, n)
    )
    val original = StmBuild(
      n,
      StmData(s)(),
      FunCall(f, i)(),
      Map[Param, (Expr, Expr)](
        i -> (IntCst(3)(U8), i + 1),
        s -> (
          input,
          FunCall(f, i)() || FunCall(g, inCtr)()
        ),
        inCtr -> (IntCst(1)(U8), inCtr + 2)
      )
    )().tchk(context).asInstanceOf[StmBuild]

    val actual = PartialEvalPass
      .partialEval(original.addInputCounter(s, inCtr))
      .asInstanceOf[StmBuild]

    // The existing bound variable should be renamed
    // I need to find the new parameters representing `i` and `inCtr` so that
    // I can check that the new input counter is updated correctly.
    val freshI =
      actual.seedByVar.find({ case (_, z) => z == IntCst(3)() }).get._1
    // Call this one `j` to avoid confusion
    val j = actual.seedByVar.find({ case (_, z) => z == IntCst(1)() }).get._1
    val expectedInCtrSeed = IntCst(0)()
    val expectedInCtrNext =
      Mux(
        FunCall(f, freshI)() || FunCall(g, j)(),
        Sum(C(1)(), inCtr)(),
        inCtr
      )()
    val expected =
      StmBuild(
        n,
        StmData(s)(),
        FunCall(f, freshI)(),
        Map[Param, (Expr, Expr)](
          freshI -> (3, freshI + 1),
          s -> (
            input,
            FunCall(f, freshI)() || FunCall(g, j)()
          ),
          j -> (1, j + 2),
          inCtr -> (0, expectedInCtrNext)
        )
      )()
    // The new stream must use the exact parameter `inCtr`, not a fresh one
    assert(actual.seedByVar(inCtr) == expectedInCtrSeed)
    assert(actual.nextByVar(inCtr) == expectedInCtrNext)
    assert(actual == expected)
  }

  test("StmBuild:Dependencies") {
    val a = Param("a")()
    val b = Param("b")()
    val c = Param("c")()
    val outside = Param("outside")()
    val s = StmBuild(
      5,
      Tuple(a, b, FunCall(Function(c, c)(), 42)(), outside)(),
      True,
      Map[Param, (Expr, Expr)](
        a -> (b, a + 1 + outside),
        b -> (0, b + c + FunCall(Function(a, a)(), 1)()),
        c -> (1, b * b)
      )
    )()
    val expectedDependencies = {
      DiGraph(
        nodes = Set(a, b, c),
        edges = Set(
          // `a` does NOT depend on `b` because the occurrence of `b` in the seed
          // of `a` is a free variable
          (a, a),
          // `b` does NOT depend on `a` because the occurrence of `a` in
          // `Function(a, a)` is bound
          (b, b),
          (b, c),
          (c, b)
        )
      )
    }
    assert(s.accVarDependencies == expectedDependencies)
  }

  test("StmBuild:OutputDependencies") {
    val a = Param("a")()
    val b = Param("b")()
    val c = Param("c")()
    val outside = Param("outside")()
    val s = StmBuild(
      5,
      Tuple(a, b, FunCall(Function(c, c)(), 42)(), outside)(),
      True,
      Map[Param, (Expr, Expr)](
        a -> (b, a + 1 + outside),
        b -> (0, b + c + FunCall(Function(a, a)(), 1)()),
        c -> (1, b * b)
      )
    )()
    // The output does NOT depend on `c` because the occurrence of `c` in
    // `Function(c, c)` is bound
    val expectedDependencies = Set(a, b)
    assert(s.outputDependencies == expectedDependencies)
  }
}
