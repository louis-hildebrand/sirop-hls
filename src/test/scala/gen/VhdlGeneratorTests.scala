package gen

import ir._
import operations._
import opt.StmSimplifier
import org.scalatest.funsuite.AnyFunSuite

class VhdlGeneratorTests extends AnyFunSuite {
  test("Arithmetic") {
    val n = 10
    val m = 10
    val i = Param("i")()
    val j = Param("j")()
    val s = StmBuild(
      n * m,
      Tuple(
        Tuple(i, j)(),
        Tuple(i + j, i + (-1 * j), (-1 * i) + j, (-1 * i) + (-1 * j))(),
        Tuple(i * j, i * (-1 * j), (-1 * i) * j, (-1 * i) * (-1 * j))(),
        Tuple(i / j, i / (-1 * j), (-1 * i) / j, (-1 * i) / (-1 * j))(),
        Tuple(i % j, i % (-1 * j), (-1 * i) % j, (-1 * i) % (-1 * j))()
      )(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (0, Mux(j === m, i + 1, i)()),
        j -> (1, Mux(j === m, 1, j + 1)())
      )
    )().tchk().lower()
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmRange(10, -2, 3)") {
    val s = StmRange(10, -2, 3)().tchk().lower().asInstanceOf[StmBuild]
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmCst(20, True)") {
    val s = StmCst(20, True)().tchk().lower().asInstanceOf[StmBuild]
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmBuildWithBoolVars") {
    val s = {
      val b = Param("b")()
      val i = Param("i")()
      StmBuild(
        5,
        i,
        b,
        Map[Param, (Expr, Expr)](b -> (True, Not(b)()), i -> (0, i + 1))
      )().tchk().lower().asInstanceOf[StmBuild]
    }
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmBuildWithTupleVars") {
    val s = {
      val x = Param("x")()
      val y = Param("y")()
      StmBuild(
        4,
        Tuple(x.__0, x.__2, y, x.__3, y)(),
        True,
        Map[Param, (Expr, Expr)](
          x -> (
            Tuple(0, Tuple()(), Tuple(1, 2)(), True)(),
            Tuple(
              x.__0 + 1,
              x.__1,
              Tuple(x.__2.__0 - 1, x.__2.__1 + 4)(),
              !x.__3
            )()
          ),
          y -> (Tuple()(), Tuple()())
        )
      )().tchk().lower().asInstanceOf[StmBuild]
    }
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmBuildWithVecVars") {
    val s = {
      val v = Param("v")()
      val z = VecBuild(
        3,
        TyInt ::+ (i => VecBuild(2, TyInt ::+ (j => Tuple(i, j)()))())
      )()
      val s = StmBuild(
        5,
        Tuple(42, True, v)(),
        True,
        Map[Param, (Expr, Expr)](
          v -> (z, VecShiftLeft(v, VecAccess(v, 0)()))
        )
      )().tchk().lower().asInstanceOf[StmBuild]
      // TODO: This wouldn't be necessary if VecBuild had IntCst length
      s.eraseTypes().tchk().asInstanceOf[StmBuild]
    }
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmCst(10, ((True, 42), (99, False)) |> StmIdentity") {
    // Ensure that the overloaded conversion methods can be resolved even when
    // there are two types---namely, (Bool, Int) and (Int, Bool)---with the
    // same bit width.
    val s = {
      val n = 10
      val s = Param("s")()
      val c = Tuple(Tuple(True, 42)(), Tuple(99, False)())()
      StmBuild(
        n,
        StmData(s)(),
        True,
        Map[Param, (Expr, Expr)](
          s -> (StmCst(n, c)(), True)
        )
      )().tchk().lower().asInstanceOf[StmBuild]
    }
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmCount |> StmMap(+42)") {
    val s = {
      val n = 4
      val s = Param("s")()
      StmBuild(
        n,
        StmData(s)() + 42,
        True,
        Map[Param, (Expr, Expr)](
          s -> (StmCount(n)(), True)
        )
      )().tchk().lower().asInstanceOf[StmBuild]
    }
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmCount |> StmScanInclusive(0, +)") {
    val s = {
      val n = 20
      val s = StmScanInclusive(StmCount(n)(), 0, PlusFunction())()
      s.tchk().lower().asInstanceOf[StmBuild]
    }
    assert(TestRunner.testExpr(s) == TestPassed)

    val optimized =
      StmSimplifier.simplify(s)().tchk().lower().asInstanceOf[StmBuild]
    assert(TestRunner.testExpr(optimized) == TestPassed)
  }

  test("StmCount |> StmScanExclusive(0, +)") {
    val s = {
      val n = 20
      val s = StmScanExclusive(StmCount(n)(), 0, PlusFunction())()
      s.tchk().lower().asInstanceOf[StmBuild]
    }
    assert(TestRunner.testExpr(s) == TestPassed)

    val optimized =
      StmSimplifier.simplify(s)().tchk().lower().asInstanceOf[StmBuild]
    assert(TestRunner.testExpr(optimized) == TestPassed)
  }

  test("StmCount |> StmFold(0, +)") {
    val s = {
      val n = 20
      val s = StmFold(StmCount(n)(), 0, PlusFunction())()
      s.tchk().lower().asInstanceOf[StmBuild]
    }
    assert(TestRunner.testExpr(s) == TestPassed)

    val optimized =
      StmSimplifier.simplify(s)().tchk().lower().asInstanceOf[StmBuild]
    assert(TestRunner.testExpr(optimized) == TestPassed)
  }

  test("s => StmCount(12)") {
    // Input deliberately unused
    val s = Param("s")(TyStm(TyInt, 1))
    val count = StmCount(12)().tchk().lower().asInstanceOf[StmBuild]
    val f = Function(s, count)().tchk()
    val inputs = Seq(
      TestInput(Seq(Some(42)))
    )
    assert(TestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("s => s |> StmMap") {
    val n = 50
    val s = Param("s")(TyStm(TyInt, n))
    val map = StmMap(s, TyInt ::+ (x => (x + 1) * x + 42))().tchk().lower()
    val f = Function(s, map)().tchk()
    val inputs = Seq(
      TestInput((0 until n).flatMap(i => Seq(None, Some(IntCst(i)))))
    )
    assert(TestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("s => s |> ZipWithIndex") {
    val n = 33
    val t = TyTuple(TyInt, TyBool, TyVec(TyInt, 3))
    val s = Param("s")(TyStm(t, n))
    val zip = StmZip(StmCount(n)(), s)().tchk().lower()
    val f = Function(s, zip)().tchk()
    val inputs = {
      val v = (i: Int) => Tuple(i, i % 2 == 0, VecLiteral(i - 1, i, i + 1)())()
      Seq(
        TestInput((0 until n).flatMap(i => Seq(None, Some(v(i)), None)))
      )
    }
    assert(TestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("a => b => c => StmZip(StmZip(a, b), c)") {
    val n = 20
    val a = Param("s")(TyStm(TyInt, n))
    val b = Param("s")(TyStm(TyInt, n))
    val c = Param("s")(TyStm(TyInt, n))
    val zip = StmZip(StmZip(a, b)(), c)().tchk().lower()
    val f = Function(a, Function(b, Function(c, zip)())())().tchk()
    val inputs = Seq(
      TestInput((0 until n).flatMap(i => Seq(Some(IntCst(i))))),
      TestInput((0 until n).flatMap(i => Seq(Some(IntCst(i * 2)), None))),
      TestInput((0 until n).flatMap(i => Seq(None, Some(IntCst(i * i)), None)))
    )
    assert(TestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("s => StmConcat(s, s)") {
    val n = 5
    val s = Param("s")(TyStm(TyInt, n))
    val concat = StmConcat(s, s)().tchk().lower()
    val f = Function(s, concat)().tchk().asInstanceOf[Function]
    val exc = intercept[IllegalArgumentException](
      VhdlGenerator.emitVhdl(f, TestRunner.VHDL_TEST_DIR)
    )
    assert(exc.getMessage.startsWith(s"Input $s is used more than once."))
  }

  test("StmZip(StmPrefix(s, 2), StmSuffix(s, 2))") {
    val n = 5
    val s = Param("s")(TyStm(TyInt, n))
    val zip = StmZip(StmPrefix(s, 2)(), StmSuffix(s, 2)())().tchk().lower()
    val f = Function(s, zip)().tchk().asInstanceOf[Function]
    val exc = intercept[IllegalArgumentException](
      VhdlGenerator.emitVhdl(f, TestRunner.VHDL_TEST_DIR)
    )
    assert(exc.getMessage.startsWith(s"Input $s is used more than once."))
  }

  test("SimpleLet") {
    val n = 3
    val x = Param("x")()
    val a = Param("a")()
    val s = StmBuild(
      n,
      Let(x, a * 2, x + x + 1)(),
      True,
      Map[Param, (Expr, Expr)](
        a -> (0, Let(x, a + 1, x * x)())
      )
    )().tchk().lower()
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("ComplexLet") {
    // (1) Uses constructs like Mux, VecAccess, TupleAccess that
    //     require intermediate signals (or, in this case, variables)
    // (2) Refers to variables outside the Let
    val n = 5
    val x = Param("x")()
    val y = Param("y")()
    val v = Param("v")()
    val j = Param("j")()
    val a = Param("a")()
    val s =
      StmBuild(
        n,
        Tuple(v, a)(),
        True,
        Map[Param, (Expr, Expr)](
          j -> (0, j + 1),
          a -> (
            Tuple(42, 99)(),
            Let(
              x,
              a.__0,
              Let(
                y,
                a.__1,
                Mux(
                  Tuple(j)().__0 % 2 === 0,
                  Tuple(x + VecAccess(v, 0)(), y + VecAccess(v, 1)())(),
                  Tuple(y + VecAccess(v, 0)(), x + VecAccess(v, 1)())()
                )()
              )()
            )()
          ),
          v -> (
            VecBuild(n, TyInt ::+ (_ => Default(TyInt)))(),
            VecShiftLeft(v, a.__0)
          )
        )
      )().tchk().lower()
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("CurriedFunCall") {
    val n = 7
    val f = Int2() ::+ (x => TyInt ::+ (y => y + x.__0 * y + x.__1))
    val a = Param("a")()
    val s = StmBuild(
      n,
      a,
      True,
      Map[Param, (Expr, Expr)](
        a -> (0, FunCall(FunCall(f, Tuple(42, 99)())(), a)())
      )
    )().tchk().lower().uncurry()
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("DotProduct") {
    val n = 100
    val a = Param("a")(TyStm(TyInt, n))
    val b = Param("b")(TyStm(TyInt, n))
    val s = StmFold(
      StmMap(StmZip(a, b)(), Int2() ::+ (x => x.__0 * x.__1))(),
      0,
      PlusFunction()
    )().tchk().lower().asInstanceOf[StmBuild]
    val f0 = Function(a, Function(b, s)())().tchk()
    val inputs = Seq(
      TestInput((0 until n).map(i => Some(IntCst(i * i * i)))),
      TestInput((0 until n).flatMap(i => Seq(Some(IntCst(3 * i)), None, None)))
    )
    assert(TestRunner.testExpr(f0, inputs) == TestPassed)

    def optimize(s: StmBuild): StmBuild = {
      val s0 = StmSimplifier.simplify(s)().tchk().lower().asInstanceOf[StmBuild]
      val s1 = s0.fuseCompletely().tchk().lower().asInstanceOf[StmBuild]
      val s2 =
        StmSimplifier.simplify(s1)().tchk().lower().asInstanceOf[StmBuild]
      s2
    }
    val optimized = optimize(s)
    val fOpt = Function(a, Function(b, optimized)())().tchk()
    assert(TestRunner.testExpr(fOpt, inputs) == TestPassed)
  }

  test("2DMapFold") {
    val n = 16
    val m = 8
    val s = Param("s")(TyStm(TyStm(TyInt, m), n))
    val rowSums =
      StmMap(s, TyStm(TyInt, m) ::+ (s => StmFold(s, 0, PlusFunction())()))()
        .tchk()
        .lower()
        .asInstanceOf[StmBuild]

    val inputs = Seq(
      TestInput((0 until n * m).flatMap(i => Seq(None, Some(IntCst(i)))))
    )

    val optimized =
      StmSimplifier.simplify(rowSums)().tchk().lower().asInstanceOf[StmBuild]
    val f = Function(s.lower().asInstanceOf[Param], optimized)().tchk()
    assert(TestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("1DStmSlide") {
    val n = 50
    val m = 3
    val s = Param("s")(TyStm(TyTuple(TyInt, TyInt, TyBool), n))
    val slide = StmSlideS(s, m = m)().tchk().lower().asInstanceOf[StmBuild]

    val inputs = Seq(
      TestInput(
        (0 until n).flatMap(i =>
          Seq(None, Some(Tuple(i + 1, i - 10, i % 3 === 0)()), None)
        )
      )
    )

    val f0 = Function(s, slide)().tchk()
    assert(TestRunner.testExpr(f0, inputs) == TestPassed)

    val optimized = StmSimplifier.simplify(slide)().tchk().lower()
    val f1 = Function(s, optimized)().tchk()
    assert(TestRunner.testExpr(f1, inputs) == TestPassed)
  }

  /** The result of accessing a vector out of bounds should be consistent
    * between hardware and software.
    */
  test("OutOfBoundsVecAccess") {
    val m = 5
    val n = 3 * m + 4
    val v = Param("v")()
    val i = Param("i")()
    val s = StmBuild(
      n,
      VecAccess(v, i)(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (-2 - m, i + 1),
        v -> (VecBuild(m, TyInt ::+ (i => 10 * (i + 1)))(), v)
      )
    )().tchk().lower()

    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("LetFunction") {
    val n = 5
    val a = Param("a")()
    val f = Param("f")()
    val s = StmBuild(
      n,
      a,
      True,
      Map[Param, (Expr, Expr)](
        a -> (0, Let(
          f,
          TyInt ::+ (x => x * x + 1),
          FunCall(f, a)() + FunCall(f, 42)()
        )())
      )
    )().tchk().lower()

    val exc = intercept[NotImplementedError](TestRunner.testExpr(s))
    assert(
      exc.getMessage == s"Cannot generate VHDL function with input type ${TyArrow(TyInt, TyInt)} and output type $TyInt."
    )
  }
}
