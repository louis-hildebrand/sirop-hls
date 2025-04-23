package ir

import operations._
import opt.{PartialEvalPass, StmAccRemovalPass}
import org.scalatest.funsuite.AnyFunSuite
import typecheck.Typechecker

class CoreTests extends AnyFunSuite {
  private val lpe: Expr => Expr = e => PartialEvalPass.partialEval(e.lowerAll())

  test("Sum:Flatten") {
    val x = Param("x")
    val y = Param("y")
    val z = Param("z")
    val w = Param("w")

    assert(x + y + z + w == Sum(z, x, y, w)())
    assert(x + y + z + w + x != Sum(z, x, y, w)())
  }

  test("Prod:Flatten") {
    val x = Param("x")
    val y = Param("y")
    val z = Param("z")
    val w = Param("w")

    assert(x * y * z * w == Prod(z, x, y, w)())
    assert(x * y * z * w * x != Prod(z, x, y, w)())
  }

  test("IfThenElse:MakeCondPositive") {
    val c = Param("c")
    val t = Param("t")
    val f = Param("f")

    val e0 = IfThenElse(c, t, f)()
    assert(e0.c == c)
    assert(e0.t == t)
    assert(e0.f == f)

    val e1 = IfThenElse(Not(c)(), f, t)()
    assert(e0 == e1)
  }

  test("Substitute:StmNext") {
    val s = Param("s")
    val v = Param("v")
    val e =
      Typechecker.typecheck(5 + StmNext(s)().__1)(Map(s -> TyStm(TyInt, 2)))
    assert(e.substitute(StmNext(s)().__1 -> v) == 5 + v)
  }

  test("Substitute:Function") {
    val x = Param("x")
    val x2 = Param("x2")
    val y = Param("y")
    val e = Tuple(
      StmNext(y)().__1,
      Function(
        x,
        Missing,
        Tuple(
          StmNext(y)().__1 + 2,
          Function(y, Missing, StmNext(y)().__1 * 3)()
        )()
      )()
    )()
    val actual = e.substitute(StmNext(y)().__1 -> x % 2)
    val expected = Tuple(
      x % 2,
      // (1) Need to rename the variable in the outer function to avoid
      //     variable capture.
      // (2) Must NOT replace the StmNext(y).__1 in the innermost function
      //     because that occurrence of y is referring to the function
      //     parameter, not y in the global scope.
      Function(
        x2,
        Missing,
        Tuple(x % 2 + 2, Function(y, Missing, StmNext(y)().__1 * 3)())()
      )()
    )()
    assert(actual == expected)
  }

  test("Substitute:StmBuild") {
    val x = Param("x")
    val x2 = Param("x2")
    val y = Param("y")
    val y2 = Param("y2")
    val z = Param("z")
    val stm = StmBuild(
      x.__1 + z + 1,
      SSome(Tuple(z, 2 * x.__1 + 1)())(),
      Map[Param, (Expr, Expr)](
        x -> (
          Tuple(True, False)(),
          IfThenElse(
            x.__1,
            Tuple(True, False)(),
            Tuple(False, True)()
          )()
        ),
        y -> (x.__1 / 2 + z, y + 2 + z)
      )
    )()
    val e = Tuple(2 * x.__1 * z, stm)()
    val actual = e.substitute(Map[Expr, Expr](x.__1 -> y, z -> IntCst(99)))
    val expected = Tuple(
      2 * y * 99,
      StmBuild(
        y + IntCst(99) + IntCst(1),
        SSome(Tuple(99, 2 * x2.__1 + 1)())(),
        Map[Param, (Expr, Expr)](
          x2 -> (
            Tuple(True, False)(),
            IfThenElse(
              x2.__1,
              Tuple(True, False)(),
              Tuple(False, True)()
            )()
          ),
          y2 -> (y / 2 + IntCst(99), y2 + 2 + IntCst(99))
        )
      )()
    )()
    assert(actual == expected)
  }

  test("Function:Equals") {
    val f = {
      val x = Param("x")
      Function(x, TyInt, (x + 1) * (x + 2))()
    }
    val g = {
      val y = Param("y")
      Function(y, Missing, (y + 1) * (y + 2))()
    }
    assert(f == g)
    assert(g == f)
    assert(f.hashCode() == g.hashCode())
  }

  test("Function:NotEquals:DifferentBody") {
    val x = Param("x")
    val f = Function(x, TyInt, (x + 1) * (x + 2))()
    val y = Param("y")
    val g = Function(y, TyInt, (y + 1) * (x + 2))()
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
    val n = Param("n")
    val i = Param("i")
    val j = Param("j")
    val z = Param("z")
    val s1 =
      StmBuild(n, SSome(i)(), Map[Param, (Expr, Expr)](i -> (z, i + 1)))()
    val s2 =
      StmBuild(n, SSome(j)(), Map[Param, (Expr, Expr)](j -> (z, j + 1)))()
    assert(s1 == s2)
    assert(s2 == s1)
    assert(s1.hashCode == s2.hashCode)
  }

  test("StmBuild:Equals:TwoAccumulatorVars") {
    val n = Param("n")
    val i = Param("i")
    val j = Param("j")
    val z = Param("z")
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
    val n = Param("n")
    val a = Param("a")
    val c = Param("c")
    val d = Param("d")
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
    val i = Param("i")
    val untyped =
      StmBuild(4, SSome(i)(), Map[Param, (Expr, Expr)](i -> (0, i + 1)))()
    val typed = Typechecker.typecheck(untyped)(Map())
    assert(typed.hashCode == untyped.hashCode)
    assert(typed == untyped)
    assert(untyped == typed)
  }

  test("StmBuild:NotEquals:DifferentLengths") {
    val i = Param("i")
    val j = Param("j")
    val z = Param("z")
    val s1 =
      StmBuild(i, SSome(i)(), Map[Param, (Expr, Expr)](i -> (z, i + 1)))()
    val s2 =
      StmBuild(j, SSome(j)(), Map[Param, (Expr, Expr)](j -> (z, j + 1)))()
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuild:NotEquals:DifferentOutputs1") {
    val n = Param("n")
    val i = Param("i")
    val j = Param("j")
    val z = Param("z")
    val s1 =
      StmBuild(n, SSome(i)(), Map[Param, (Expr, Expr)](i -> (z, i + 1)))()
    val s2 =
      StmBuild(n, SSome(i)(), Map[Param, (Expr, Expr)](j -> (z, j + 1)))()
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuild:NotEquals:DifferentOutputs2") {
    val n = Param("n")
    val i = Param("i")
    val j = Param("j")
    val z = Param("z")
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
    val n = Param("n")
    val i = Param("i")
    val j = Param("j")
    val s1 =
      StmBuild(n, SSome(i)(), Map[Param, (Expr, Expr)](i -> (i, i + 1)))()
    val s2 =
      StmBuild(n, SSome(j)(), Map[Param, (Expr, Expr)](j -> (j, j + 1)))()
    assert(s1 != s2)
    assert(s2 != s1)
  }

  test("StmBuild:NotEquals:DifferentStructures") {
    val n = Param("n")
    val i = Param("i")
    val j = Param("j")
    val z = Param("z")
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
    val n = Param("n")
    val z = Param("z")
    val a = Param("a")
    val r0 = Param("r0")
    val r1 = Param("r1")
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

  test("StmBuild:Fuse:MapPlusFive") {
    val s = Param("s")
    val original = Typechecker
      .typecheck(
        StmBuild(
          3,
          SSome(StmNext(s)().__1 + 5)(),
          Map[Param, (Expr, Expr)](
            s -> (StmCount(3), StmNext(s)().__0)
          )
        )()
      )(Map(s -> TyStm(TyInt, 3)))
      .asInstanceOf[StmBuild]
    val fused = original.fuseCompletely()

    // Correct behaviour
    val expectedElems = StmLiteral.ints(5, 6, 7)
    assert(ir.eval(original) == expectedElems)
    assert(ir.eval(fused) == expectedElems)
    // Successful fusion
    val i = Param("i")
    val ideal = StmBuild(
      3,
      SSome(i + 5)(),
      Map[Param, (Expr, Expr)](
        i -> (0, i + 1)
      )
    )()
    assert(lpe(fused) == lpe(ideal))
  }

  test("StmBuild:Fuse:ZipCounters") {
    val i = Param("i")
    // [0, 1, 2, 3]
    val c1 =
      StmBuild(4, SSome(i)(), Map[Param, (Expr, Expr)](i -> (0, i + 1)))()
    // [9, 11, 13, 15]
    val c2 =
      StmBuild(4, SSome(i)(), Map[Param, (Expr, Expr)](i -> (9, i + 2)))()
    // [(0, 9), (1, 11), (2, 13), (3, 15)]
    val s = Typechecker.typecheck(StmZip(c1, c2))(Map()).asInstanceOf[StmBuild]

    val x1 = s.seedByVar.find({ case (_, z) => z == c1 }).get._1
    val x2 = s.seedByVar.find({ case (_, z) => z == c2 }).get._1

    val i1 = Param("i")
    val i2 = Param("i")

    // 1) After one fusion
    val actual1 = PartialEvalPass.partialEval(s.fuseWith(x1))
    // 1a) Correct behaviour
    assert(ir.eval(actual1) == ir.eval(s))
    // 1b) Successful fusion
    val ideal1 = StmBuild(
      4,
      SSome(Tuple(i1, StmNext(x2)().__1)())(),
      Map[Param, (Expr, Expr)](
        x2 -> s.equations(x2),
        i1 -> (0, i1 + 1)
      )
    )()
    assert(lpe(actual1) == lpe(ideal1))

    // 2) After two fusions
    val actual2 = lpe(s.fuseWith(x1).fuseWith(x2))
    // 2a) Correct behaviour
    val expectedElems =
      StmLiteral(
        Tuple(0, 9)(),
        Tuple(1, 11)(),
        Tuple(2, 13)(),
        Tuple(3, 15)()
      )()
    assert(ir.eval(s) == expectedElems)
    assert(ir.eval(actual2) == expectedElems)
    // 2b) Successful fusion
    val ideal2 = StmBuild(
      4,
      SSome(Tuple(i1, i2)())(),
      Map[Param, (Expr, Expr)](
        i1 -> (0, i1 + 1),
        i2 -> (9, i2 + 2)
      )
    )()
    assert(lpe(actual2) == lpe(ideal2))
  }

  test("StmBuild:AddOutputCounter") {
    val n = Param("n")
    val out = Param("out")
    val outCtr = Param("out_ctr")
    val s = StmBuild(
      n,
      out,
      Map[Param, (Expr, Expr)](outCtr -> (10, outCtr * 2))
    )()

    val actual = s.addOutputCounter(outCtr)

    // The existing bound variable should be renamed
    val i = Param("i")
    val expectedOutCtrSeed = IntCst(0)
    val expectedOutCtrNext = OptionAccess(
      out,
      (_: Expr) => outCtr + 1,
      (_: Expr) => outCtr
    )()
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
    val n = Param("n")
    val f = Param("f")
    val g = Param("g")
    val input = Param("input")
    val i = Param("i")
    val inCtr = Param("in_ctr")
    val s = Param("s")
    val context = Map(
      n -> TyInt,
      f -> TyArrow(TyInt, TyBool),
      g -> TyArrow(TyInt, TyBool),
      input -> TyStm(TyInt, n)
    )
    val original = Typechecker
      .typecheck(
        StmBuild(
          n,
          IfThenElse(
            FunCall(f, i)(),
            SSome(StmNext(s)().__1)(),
            NNone(TyInt)
          )(),
          Map[Param, (Expr, Expr)](
            i -> (3, i + 1),
            s -> (
              input,
              IfThenElse(
                FunCall(f, i)(),
                StmNext(s)().__0,
                IfThenElse(FunCall(g, inCtr)(), StmNext(s)().__0, s)()
              )()
            ),
            inCtr -> (1, inCtr + 2)
          )
        )()
      )(context)
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
      IfThenElse(
        (Not(FunCall(f, freshI)())() && FunCall(g, j)())
          || FunCall(f, freshI)(),
        inCtr + 1,
        inCtr
      )()
    val expected =
      StmBuild(
        n,
        IfThenElse(
          FunCall(f, freshI)(),
          SSome(StmNext(s)().__1)(),
          NNone(TyInt)
        )(),
        Map[Param, (Expr, Expr)](
          freshI -> (3, freshI + 1),
          s -> (
            input,
            IfThenElse(
              FunCall(f, freshI)(),
              StmNext(s)().__0,
              IfThenElse(FunCall(g, j)(), StmNext(s)().__0, s)()
            )()
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
    val a = Param("a")
    val b = Param("b")
    val c = Param("c")
    val outside = Param("outside")
    val s = StmBuild(
      5,
      SSome(Tuple(a, b, FunCall(Function(c, Missing, c)(), 42)(), outside)())(),
      Map[Param, (Expr, Expr)](
        a -> (b, a + 1 + outside),
        b -> (0, b + c + FunCall(Function(a, Missing, a)(), 1)()),
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
    val a = Param("a")
    val b = Param("b")
    val c = Param("c")
    val outside = Param("outside")
    val s = StmBuild(
      5,
      SSome(Tuple(a, b, FunCall(Function(c, Missing, c)(), 42)(), outside)())(),
      Map[Param, (Expr, Expr)](
        a -> (b, a + 1 + outside),
        b -> (0, b + c + FunCall(Function(a, Missing, a)(), 1)()),
        c -> (1, b * b)
      )
    )()
    // The output does NOT depend on `c` because the occurrence of `c` in
    // `Function(c, c)` is bound
    val expectedDependencies = Set(a, b)
    assert(s.outputDependencies == expectedDependencies)
  }
}
