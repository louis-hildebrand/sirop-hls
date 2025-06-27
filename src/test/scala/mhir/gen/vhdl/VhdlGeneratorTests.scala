package mhir.gen.vhdl

import mhir.ir.Lowering.ExprLowering
import mhir.ir.StreamFuser.StreamFusion
import mhir.ir.Uncurrier.Uncurry
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.optimize.StmSimplifier
import mhir.sugar._
import mhir.testing.VhdlTest
import org.scalatest.funsuite.AnyFunSuite

@VhdlTest
class VhdlGeneratorTests extends AnyFunSuite {
  test("Reshaping") {
    val n = 16
    val i = Param("i")(I8)
    val j = Param("j")(U8)
    val s = StmBuild(
      n,
      Tuple(
        Tuple(
          i,
          PadTo(i, 8)(),
          TruncateTo(i, 4)(),
          Mux(i < 0, C(0)(TyUInt(7)), ToUnsigned(i)())()
        )(),
        Tuple(j, PadTo(j, 8)(), TruncateTo(j, 4)(), ToSigned(j)())()
      )(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (C(-8)(I8), TruncateTo(SafeSum(i, 1)(), 8)()),
        j -> (C(0)(U8), TruncateTo(SafeSum(j, 1)(), 8)())
      )
    )().tchk().lower()
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("Arithmetic") {
    val n = 10
    val m = 10
    val i = Param("i")(I8)
    val j = Param("j")(I8)
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
        i -> (C(0)(I8), Mux(j === m, i + 1, i)()),
        j -> (C(1)(I8), Mux(j === m, C(1)(I8), j + 1)())
      )
    )().tchk().lower()
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmRange(10, -2, 3)") {
    val s =
      StmRange(10, C(-2)(I8), C(3)(I8))().tchk().lower().asInstanceOf[StmBuild]
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmCst(20, True)") {
    val s = StmCst(20, True)().tchk().lower().asInstanceOf[StmBuild]
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmBuildWithBoolVars") {
    val s = {
      val b = Param("b")(TyBool)
      val i = Param("i")(U8)
      StmBuild(
        5,
        i,
        b,
        Map[Param, (Expr, Expr)](b -> (True, Not(b)()), i -> (C(0)(U8), i + 1))
      )().tchk().lower().asInstanceOf[StmBuild]
    }
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmBuildWithTupleVars") {
    val s = {
      val x = Param("x")(TyTuple(U8, TyTuple(), (I8, U16), TyBool))
      val y = Param("y")(TyTuple())
      StmBuild(
        4,
        Tuple(x.__0, x.__2, y, x.__3, y)(),
        True,
        Map[Param, (Expr, Expr)](
          x -> (
            Tuple(C(0)(U8), Tuple()(), Tuple(C(1)(I8), C(2)(U16))(), True)(),
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
      val v = Param("v")(TyVec(TyVec((U8, U8), 2), 3))
      val z = VecBuild(
        3,
        U8 ::+ (i => VecBuild(2, U8 ::+ (j => Tuple(i, j)()))())
      )()
      StmBuild(
        5,
        Tuple(42, True, v)(),
        True,
        Map[Param, (Expr, Expr)](
          v -> (z, VecShiftLeft(v, VecAccess(v, 0)())())
        )
      )().tchk().lower().asInstanceOf[StmBuild]
    }
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmCst(10, ((True, 42), (99, False)) |> StmIdentity") {
    // Ensure that the overloaded conversion methods can be resolved even when
    // there are two types---namely, (Bool, Int) and (Int, Bool)---with the
    // same bit width.
    val s = {
      val n = 10
      val c = Tuple(Tuple(True, C(42)(I8))(), Tuple(C(99)(U8), False)())()
      val s = Param("s")(TyStm(((TyBool, I8): Type, (U8, TyBool): Type), -1))
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
      val s = Param("s")(TyStm(U8, -1))
      StmBuild(
        n,
        StmData(s)() + 42,
        True,
        Map[Param, (Expr, Expr)](
          s -> (StmCount(C(n)(U8))(), True)
        )
      )().tchk().lower().asInstanceOf[StmBuild]
    }
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmCount |> StmScanInclusive(0, +)") {
    val s = {
      val n = 20
      val s =
        StmScanInclusive(StmCount(C(n)(U8))(), C(0)(U8), PlusFunction(U8))()
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
      val s =
        StmScanExclusive(StmCount(C(n)(U8))(), C(0)(U8), PlusFunction(U8))()
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
      val s = StmFold(StmCount(C(n)(U8))(), C(0)(U8), PlusFunction(U8))()
      s.tchk().lower().asInstanceOf[StmBuild]
    }
    assert(TestRunner.testExpr(s) == TestPassed)

    val optimized =
      StmSimplifier.simplify(s)().tchk().lower().asInstanceOf[StmBuild]
    assert(TestRunner.testExpr(optimized) == TestPassed)
  }

  test("s => StmCount(12)") {
    // Input deliberately unused
    val s = Param("s")(TyStm(U8, 1))
    val count = StmCount(C(12)(U8))().tchk().lower().asInstanceOf[StmBuild]
    val f = Function(s, count)().tchk()
    val inputs = Seq(
      TestInput(Seq(Some(C(42)(U8))))
    )
    assert(TestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("s => s |> StmMap") {
    val n = 50
    val s = Param("s")(TyStm(U16, n))
    val map = StmMap(s, U16 ::+ (x => (x + 1) * x + 42))().tchk().lower()
    val f = Function(s, map)().tchk()
    val inputs = Seq(
      TestInput((0 until n).flatMap(i => Seq(None, Some(C(i)(U16)))))
    )
    assert(TestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("s => s |> ZipWithIndex") {
    val n = 33
    val t = TyTuple(U8, TyBool, TyVec(I9, 3))
    val s = Param("s")(TyStm(t, n))
    val zip = StmZip(StmCount(n)(), s)().tchk().lower()
    val f = Function(s, zip)().tchk()
    val inputs = {
      val v =
        (i: Int) =>
          Tuple(
            C(i)(U8),
            i % 2 === 0,
            VecLiteral(C(i - 1)(I9), C(i)(I9), C(i + 1)(I9))()
          )()
      Seq(
        TestInput((0 until n).flatMap(i => Seq(None, Some(v(i)), None)))
      )
    }
    assert(TestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("a => b => c => StmZip(StmZip(a, b), c)") {
    val n = 20
    val a = Param("s")(TyStm(U16, n))
    val b = Param("s")(TyStm(U16, n))
    val c = Param("s")(TyStm(U16, n))
    val zip = StmZip(StmZip(a, b)(), c)().tchk().lower()
    val f = Function(a, Function(b, Function(c, zip)())())().tchk()
    val inputs = Seq(
      TestInput((0 until n).flatMap(i => Seq(Some(C(i)(U16))))),
      TestInput((0 until n).flatMap(i => Seq(Some(C(i * 2)(U16)), None))),
      TestInput(
        (0 until n).flatMap(i => Seq(None, Some(C(i * i)(U16)), None))
      )
    )
    assert(TestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("s => StmConcat(s, s)") {
    val n = 5
    val s = Param("s")(TyStm(U8, n))
    val concat = StmConcat(s, s)().tchk().lower()
    val f = Function(s, concat)().tchk().asInstanceOf[Function]
    val exc = intercept[IllegalArgumentException](
      VhdlGenerator.emitVhdl(f, TestRunner.VHDL_TEST_DIR)
    )
    assert(exc.getMessage.startsWith(s"Input $s is used more than once."))
  }

  test("StmZip(StmPrefix(s, 2), StmSuffix(s, 2))") {
    val n = 5
    val s = Param("s")(TyStm(U8, n))
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
    val a = Param("a")(U8)
    val s = StmBuild(
      n,
      Let(x, a * 2, x + x + 1)(),
      True,
      Map[Param, (Expr, Expr)](
        a -> (C(0)(U8), Let(x, a + 1, x * x)())
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
    val j = Param("j")(U8)
    val a = Param("a")((U8, U8))
    val v = Param("v")(TyVec(U8, n))
    val s =
      StmBuild(
        n,
        Tuple(v, a)(),
        True,
        Map[Param, (Expr, Expr)](
          j -> (C(0)(U8), j + 1),
          a -> (
            Tuple(C(42)(U8), C(99)(U8))(),
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
            VecBuild(n, U8 ::+ (_ => Default(U8)))(),
            VecShiftLeft(v, a.__0)()
          )
        )
      )().tchk().lower()
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("CurriedFunCall") {
    val n = 7
    val f = (U32, U32) ::+ (x => U32 ::+ (y => y + x.__0 * y + x.__1))
    val a = Param("a")(U32)
    val s = StmBuild(
      n,
      a,
      True,
      Map[Param, (Expr, Expr)](
        a -> (
          C(0)(U32),
          FunCall(FunCall(f, Tuple(C(1)(U32), C(1)(U32))())(), a)()
        )
      )
    )().tchk().lower().uncurry()
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("DotProduct") {
    val n = 100
    val a = Param("a")(TyStm(U32, n))
    val b = Param("b")(TyStm(U16, n))
    val s = StmFold(
      StmMap(StmZip(a, b)(), (U32, U16) ::+ (x => x.__0 * x.__1))(),
      C(0)(U32),
      PlusFunction(U32)
    )().tchk().lower().asInstanceOf[StmBuild]
    val f0 = Function(a, Function(b, s)())().tchk()
    val inputs = Seq(
      TestInput((0 until n).map(i => Some(C(i * i)(U32)))),
      TestInput(
        (0 until n).flatMap(i => Seq(Some(C(3 * i)(U16)), None, None))
      )
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
    val s = Param("s")(TyStm(TyStm(U16, m), n))
    val rowSums =
      StmMap(
        s,
        TyStm(U16, m) ::+ (s => StmFold(s, C(0)(U16), PlusFunction(U16))())
      )().tchk().lower().asInstanceOf[StmBuild]

    val inputs = Seq(
      TestInput((0 until n * m).flatMap(i => Seq(None, Some(C(i)(U16)))))
    )

    val optimized =
      StmSimplifier.simplify(rowSums)().tchk().lower().asInstanceOf[StmBuild]
    val f = Function(s.lower().asInstanceOf[Param], optimized)().tchk()
    assert(TestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("1DStmSlide") {
    val n = 50
    val m = 3
    val s = Param("s")(TyStm(TyTuple(U8, I8, TyBool), n))
    val slide = StmSlideS(s, m = m)().tchk().lower().asInstanceOf[StmBuild]

    val inputs = Seq(
      TestInput(
        (0 until n).flatMap(i =>
          Seq(
            None,
            Some(Tuple(C(i + 1)(U8), C(i - 10)(I8), i % 3 === 0)()),
            None
          )
        )
      )
    )

    val f0 = Function(s, slide)().tchk().lower()
    assert(TestRunner.testExpr(f0, inputs) == TestPassed)

    val optimized = StmSimplifier.simplify(slide)().tchk().lower()
    val f1 = Function(s, optimized)().tchk()
    assert(TestRunner.testExpr(f1, inputs) == TestPassed)
  }

  test("LetFunction") {
    val n = 5
    val a = Param("a")(U8)
    val f = Param("f")()
    val s = StmBuild(
      n,
      a,
      True,
      Map[Param, (Expr, Expr)](
        a -> (
          C(0)(U8),
          Let(
            f,
            U8 ::+ (x => x * x + 1),
            FunCall(f, a)() + FunCall(f, C(42)(U8))()
          )()
        )
      )
    )().tchk().lower()

    val exc = intercept[NotImplementedError](TestRunner.testExpr(s))
    assert(
      exc.getMessage == s"Cannot generate VHDL function with input type ${U8 ->: U8} and output type $U8."
    )
  }
}
