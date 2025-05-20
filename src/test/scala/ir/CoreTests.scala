package ir

import operations.VecShiftLeft
import opt.PartialEvalPass
import org.scalatest.funsuite.AnyFunSuite

class CoreTests extends AnyFunSuite {
  test("Sum:Flatten") {
    val x = Param("x")()
    val y = Param("y")()
    val z = Param("z")()
    val w = Param("w")()

    assert(x + y + z + w == Sum(z, x, y, w)())
    assert(x + y + z + w + x != Sum(z, x, y, w)())
  }

  test("Prod:Flatten") {
    val x = Param("x")()
    val y = Param("y")()
    val z = Param("z")()
    val w = Param("w")()

    assert(x * y * z * w == Prod(z, x, y, w)())
    assert(x * y * z * w * x != Prod(z, x, y, w)())
  }

  test("Mux:MakeCondPositive") {
    val c = Param("c")()
    val t = Param("t")()
    val f = Param("f")()

    val e0 = Mux(c, t, f)()
    assert(e0.c == c)
    assert(e0.t == t)
    assert(e0.f == f)

    val e1 = Mux(Not(c)(), f, t)()
    assert(e0 == e1)
  }

  test("Substitute:RemoveType") {
    val x = Param("x")(TyInt)
    val y = Param("y")(TyBool)
    val z = Param("z")()
    val e = Tuple(x, y)(TyTuple(TyInt, TyBool))
    val subbed = e.substitute(x -> (y && z))
    assert(subbed == Tuple(y && z, y)())
    assert(subbed.typ != TyTuple(TyInt, TyBool))
  }

  test("Substitute:StmNext") {
    val s = Param("s")()
    val v = Param("v")()

    val untyped = 5 + StmNextData(s)()
    assert(untyped.substitute(StmNextData(s)() -> v) == 5 + v)

    val typed = untyped.tchk(Map(s -> TyStm(TyInt, 2)))
    assert(typed.substitute(StmNextData(s)() -> v) == 5 + v)

    val subbedSameType =
      typed.subPreserveType(StmNextData(s)() -> v.rebuild(TyInt))
    assert(subbedSameType == 5 + v)
    assert(subbedSameType.typ == TyInt)
  }

  test("Substitute:Function") {
    val x = Param("x")(TyInt)
    val x2 = Param("x2")(TyInt)
    val y = Param("y")(TyStm(TyInt, 5))
    val untyped = Tuple(
      StmNextData(y)(),
      Function(
        x,
        Tuple(
          StmNextData(y)() + 2,
          Function(y, StmNextData(y)() * 3)()
        )()
      )()
    )()
    val subs = Map[Expr, Expr](StmNextData(y)() -> Mod(x, 2)(TyInt))
    val expected = Tuple(
      x % 2,
      // (1) Need to rename the variable in the outer function to avoid
      //     variable capture.
      // (2) Must NOT replace the StmNext(y).__1 in the innermost function
      //     because that occurrence of y is referring to the function
      //     parameter, not y in the global scope.
      Function(x2, Tuple(x % 2 + 2, Function(y, StmNextData(y)() * 3)())())()
    )()

    val actual0 = untyped.substitute(subs)
    assert(actual0 == expected)

    val typed = untyped.tchk(Map(x -> x.typ, x2 -> x2.typ, y -> y.typ))
    val actual1 = typed.substitute(subs)
    assert(actual1 == expected)

    val actual2 = typed.subPreserveType(subs)
    assert(actual2 == expected)
    assert(actual2.typ != Missing)
  }

  test("Substitute:StmBuild") {
    val x = Param("x")()
    val x2 = Param("x2")()
    val y = Param("y")()
    val y2 = Param("y2")()
    val z = Param("z")()
    val context = Map(
      x -> TyTuple(TyInt, TyInt),
      x2 -> TyTuple(TyInt, TyInt),
      y -> TyInt,
      y2 -> TyInt,
      z -> TyInt
    )
    val stm = StmBuild(
      x.__1 + z + 1,
      SSome(Tuple(z, x.__1 && x.__1)())(),
      Map[Param, (Expr, Expr)](
        x -> (
          Tuple(True, False)(),
          Mux(
            x.__1,
            Tuple(True, False)(),
            Tuple(False, True)()
          )()
        ),
        y -> (x.__1 / 2 + z, y + 2 + z)
      )
    )()
    val untyped = Tuple(2 * x.__1 * z, stm)()
    val subs = Map[Expr, Expr](x.__1 -> y, z -> IntCst(99))
    val expected = Tuple(
      2 * y * 99,
      StmBuild(
        y + IntCst(99) + IntCst(1),
        SSome(Tuple(99, x2.__1 && x2.__1)())(),
        Map[Param, (Expr, Expr)](
          x2 -> (
            Tuple(True, False)(),
            Mux(
              x2.__1,
              Tuple(True, False)(),
              Tuple(False, True)()
            )()
          ),
          y2 -> (y / 2 + IntCst(99), y2 + 2 + IntCst(99))
        )
      )()
    )()

    val actual0 = untyped.substitute(subs)
    assert(actual0 == expected)

    val typed = untyped.tchk(context)
    val actual1 = typed.substitute(subs)
    assert(actual1 == expected)

    val actual2 = typed.subPreserveType(subs)
    assert(actual2 == expected)
    assert(actual2.typ != Missing)
  }

  test("SubstituteInType1") {
    val n = Param("n")(TyInt)
    val e = Tuple(VecBuild(n * 2, TyInt ::+ (i => i))(), n + 1)().tchk()
    val expectedType = TyTuple(TyVec(TyInt, n * 2), TyInt)
    assert(e.typ == expectedType)

    val actual = e.subPreserveType(n -> IntCst(42))

    val expected =
      Tuple(
        VecBuild(IntCst(42) * IntCst(2), TyInt ::+ (i => i))(),
        IntCst(42) + IntCst(1)
      )()
    assert(actual == expected)
    val expectedTypeAfterSub =
      TyTuple(TyVec(TyInt, IntCst(42) * IntCst(2)), TyInt)
    assert(actual.typ == expectedTypeAfterSub)
  }

  test("SubstituteInType2") {
    val n = Param("n")(TyInt)
    val m = Param("m")(TyInt)
    val k = Param("k")(TyInt)
    val v = Param("v")()
    val e = StmBuild(
      n,
      SSome(VecAccess(v, 0)())(),
      Map[Param, (Expr, Expr)](
        v -> (VecBuild(n, TyInt ::+ (i => i))(), VecShiftLeft(v, 42))
      )
    )().tchk()

    val actual = e.subPreserveType(n -> (m + k))

    val expected = StmBuild(
      m + k,
      SSome(VecAccess(v, 0)())(),
      Map[Param, (Expr, Expr)](
        v -> (VecBuild(m + k, TyInt ::+ (i => i))(), VecShiftLeft(v, 42))
      )
    )()
    assert(actual == expected)
    val expectedStmType = TyStm(TyInt, m + k)
    assert(actual.typ == expectedStmType)
    val expectedVecType = TyVec(TyInt, m + k)
    val actualVecParam = actual.asInstanceOf[StmBuild].equations.toSeq.head._1
    assert(actualVecParam.typ == expectedVecType)
  }

  test("Function:Equals") {
    val f = {
      val x = Param("x")(TyInt)
      Function(x, (x + 1) * (x + 2))()
    }
    val g = {
      val y = Param("y")()
      Function(y, (y + 1) * (y + 2))()
    }
    assert(f == g)
    assert(g == f)
    assert(f.hashCode() == g.hashCode())
  }

  test("Function:NotEquals:DifferentBody") {
    val x = Param("x")(TyInt)
    val f = Function(x, (x + 1) * (x + 2))()
    val y = Param("y")(TyInt)
    val g = Function(y, (y + 1) * (x + 2))()
    assert(f != g)
    assert(g != f)
  }

  test("StmBuild:Equals:NoAccumulatorVars") {
    val s1 = StmBuild(3, SSome(True)(), Map[Param, (Expr, Expr)]())()
    val s2 = StmBuild(3, SSome(True)(), Map[Param, (Expr, Expr)]())()
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
      StmBuild(n, SSome(i)(), Map[Param, (Expr, Expr)](i -> (z, i + 1)))()
    val s2 =
      StmBuild(n, SSome(j)(), Map[Param, (Expr, Expr)](j -> (z, j + 1)))()
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
        SSome(j - i)(),
        Map[Param, (Expr, Expr)](i -> (z, i + 1), j -> (0, j * 2))
      )()
    val s2 =
      StmBuild(
        n,
        SSome(i - j)(),
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
      SSome(a * c * d)(),
      Map[Param, (Expr, Expr)](
        a -> (0, a + 1),
        c -> (1, c + 2),
        d -> (2, d + 3)
      )
    )()
    val s2 = StmBuild(
      n,
      SSome(a * c * d)(),
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
    val i = Param("i")()
    val untyped =
      StmBuild(4, SSome(i)(), Map[Param, (Expr, Expr)](i -> (0, i + 1)))()
    val typed = untyped.tchk()
    assert(typed.hashCode == untyped.hashCode)
    assert(typed == untyped)
    assert(untyped == typed)
  }

  test("StmBuild:NotEquals:DifferentLengths") {
    val i = Param("i")()
    val j = Param("j")()
    val z = Param("z")()
    val s1 =
      StmBuild(i, SSome(i)(), Map[Param, (Expr, Expr)](i -> (z, i + 1)))()
    val s2 =
      StmBuild(j, SSome(j)(), Map[Param, (Expr, Expr)](j -> (z, j + 1)))()
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuild:NotEquals:DifferentOutputs1") {
    val n = Param("n")()
    val i = Param("i")()
    val j = Param("j")()
    val z = Param("z")()
    val s1 =
      StmBuild(n, SSome(i)(), Map[Param, (Expr, Expr)](i -> (z, i + 1)))()
    val s2 =
      StmBuild(n, SSome(i)(), Map[Param, (Expr, Expr)](j -> (z, j + 1)))()
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
        SSome(i - j)(),
        Map[Param, (Expr, Expr)](i -> (z, i + 1), j -> (0, j * 2))
      )()
    val s2 =
      StmBuild(
        n,
        SSome(i - j)(),
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
      StmBuild(n, SSome(i)(), Map[Param, (Expr, Expr)](i -> (i, i + 1)))()
    val s2 =
      StmBuild(n, SSome(j)(), Map[Param, (Expr, Expr)](j -> (j, j + 1)))()
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
      SSome(i)(),
      Map[Param, (Expr, Expr)](
        i -> (z, i + 1),
        j -> (z, j - 1)
      )
    )()
    val s2 = StmBuild(
      n,
      SSome(j)(),
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
    val n = Param("n")(TyInt)
    val out = Param("out")(TyTuple(TyTuple(TyInt, TyBool), TyBool))
    val outCtr = Param("out_ctr")(TyInt)
    val s = StmBuild(
      n,
      out,
      Map[Param, (Expr, Expr)](outCtr -> (10, outCtr * 2))
    )().tchk().asInstanceOf[StmBuild]

    val actual = s.addOutputCounter(outCtr)

    // The existing bound variable should be renamed
    val i = Param("i")()
    val expectedOutCtrSeed = IntCst(0)
    val expectedOutCtrNext = Mux(IsSome(out)(), outCtr + 1, outCtr)()
    val expected = StmBuild(
      n,
      out,
      Map[Param, (Expr, Expr)](
        i -> (10, i * 2),
        outCtr -> (expectedOutCtrSeed, expectedOutCtrNext)
      )
    )()
    assert(actual == expected)
    // The new stream must use the exact parameter `outCtr`, not a fresh one
    assert(actual.seedByVar(outCtr) == expectedOutCtrSeed)
    assert(actual.nextByVar(outCtr) == expectedOutCtrNext)
  }

  test("StmBuild:AddInputCounter") {
    val n = Param("n")()
    val f = Param("f")()
    val g = Param("g")()
    val input = Param("input")()
    val i = Param("i")()
    val inCtr = Param("in_ctr")()
    val s = Param("s")()
    val context = Map(
      n -> TyInt,
      f -> TyArrow(TyInt, TyBool),
      g -> TyArrow(TyInt, TyBool),
      input -> TyStm(TyInt, n)
    )
    val original = StmBuild(
      n,
      Mux(
        FunCall(f, i)(),
        SSome(StmNextData(s)())(),
        NNone(TyInt)
      )(),
      Map[Param, (Expr, Expr)](
        i -> (3, i + 1),
        s -> (
          input,
          FunCall(f, i)() || FunCall(g, inCtr)()
        ),
        inCtr -> (1, inCtr + 2)
      )
    )()
      .tchk(context)
      .asInstanceOf[StmBuild]

    val actual = PartialEvalPass
      .partialEval(original.addInputCounter(s, inCtr))
      .asInstanceOf[StmBuild]

    // The existing bound variable should be renamed
    // I need to find the new parameters representing `i` and `inCtr` so that
    // I can check that the new input counter is updated correctly.
    val freshI = actual.seedByVar.find({ case (_, z) => z == IntCst(3) }).get._1
    // Call this one `j` to avoid confusion
    val j = actual.seedByVar.find({ case (_, z) => z == IntCst(1) }).get._1
    val expectedInCtrSeed = IntCst(0)
    val expectedInCtrNext =
      Mux(FunCall(f, freshI)() || FunCall(g, j)(), inCtr + 1, inCtr)()
    val expected =
      StmBuild(
        n,
        Mux(
          FunCall(f, freshI)(),
          SSome(StmNextData(s)())(),
          NNone(TyInt)
        )(),
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
      SSome(Tuple(a, b, FunCall(Function(c, c)(), 42)(), outside)())(),
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
      SSome(Tuple(a, b, FunCall(Function(c, c)(), 42)(), outside)())(),
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
