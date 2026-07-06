package mhir.gen
package vhdl

import mhir.canonicalize._
import mhir.gen.TestPassed
import mhir.gen.vhdl.test._
import mhir.ir._
import mhir.optimize.experimental.AnyStreamFuser.StreamFusion
import mhir.optimize.{StmBuildSimplifier, StmSimplifier}
import mhir.sugar.Uncurrier.Uncurry
import mhir.sugar._
import mhir.sugar.experimental.{StmFold, StmScanExclusive, StmScanInclusive}
import mhir.testing.HardwareTest
import mhir.typecheck.TypeCheck
import org.scalatest.funsuite.AnyFunSuite

@HardwareTest
class VhdlGeneratorTests extends AnyFunSuite {

  private val stmBuildSimplifier = StmBuildSimplifier()
  private val stmSimplifier = StmSimplifier(stmBuildSimplifier)

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
    )().tchk().lower
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
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
    )().tchk().lower
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
  }

  test("Bits") {
    val elemTyp = TyTuple(TyVec(U8, 8), TyVec(I8, 8), TyBool)
    val p = Param("p")(TyStm(elemTyp, -1))
    val f = TyStm(elemTyp, 32) ::+ (s =>
      StmBuild(
        32,
        Tuple(Bits(StmData(p)())(), Bits(VecAccess(StmData(p)().__1, 0)())())(),
        True,
        Map[Param, (Expr, Expr)](
          p -> (s, True)
        )
      )().tchk().lower
    )
    val inputs = Seq(
      Seq(
        DirectTestInput(
          (0 until 32).map(t =>
            Some(
              Tuple(
                VecLiteral(
                  (0 until 8).map(i => 8 * t + i).map(C(_)(U8)): _*
                )(),
                VecLiteral(
                  (0 until 8).map(i => -128 + 8 * t + i).map(C(_)(I8)): _*
                )(),
                if (t % 2 == 0) True else False
              )()
            )
          )
        )
      )
    )
    assert(VhdlTestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("InterpretAs") {
    val i = Param("i")(TyVec(TyBool, 8))
    val s = StmBuild(
      255,
      Tuple(
        InterpretAs(VecSlice(i, 5, 1, 1)(), TyBool)(),
        InterpretAs(VecSlice(i, 6, 1, 1)(), TyBool)(),
        InterpretAs(VecSlice(i, 7, 1, 1)(), TyBool)(),
        InterpretAs(i, U8)(),
        InterpretAs(i, I8)(),
        InterpretAs(i, (TyUInt(3), TySInt(5)))(),
        InterpretAs(i, TyVec(TyUInt(2), 4))()
      )(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (
          AllZero(i.typ),
          Bits(WrappingSum(C(1)(U8), InterpretAs(i, U8)())())()
        )
      )
    )().tchk().lower
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
  }

  test("BitwiseShifts") {
    val n = 16
    val u4 = TyUInt(4)
    val i4 = TySInt(4)
    val i = Param("i")(u4)
    val j = Param("j")(i4)
    val s = StmBuild(
      n,
      Tuple(
        Tuple(
          i,
          i << 0,
          i << 1,
          i << 2,
          i << 3,
          i >> 0,
          i >> 1,
          i >> 2,
          i >> 3,
          i >>> 0,
          i >>> 1,
          i >>> 2,
          i >>> 3
        )(),
        Tuple(
          j,
          j << 0,
          j << 1,
          j << 2,
          j << 3,
          j >> 0,
          j >> 1,
          j >> 2,
          j >> 3,
          j >>> 0,
          j >>> 1,
          j >>> 2,
          j >>> 3
        )()
      )(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (C(0)(u4), Sum(C(1)(u4), i)()),
        j -> (C(-8)(i4), Sum(C(1)(i4), j)())
      )
    )().tchk().lower
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
  }

  test("WrappingSum") {
    val n = 10
    val x = Param("x")(U8) // U8, overflow high
    val y = Param("y")(U16) // U16, overflow high
    val z = Param("z")(I16) // I16, overflow high
    val w = Param("w")(I16) // I16, overflow low
    val s = StmBuild(
      n,
      Tuple(x, y, z, w)(),
      True,
      Map[Param, (Expr, Expr)](
        x -> (C(254)(U8), WrappingSum(x, C(1)(U8))()),
        y -> (C(65534)(U16), WrappingSum(y, C(1)(U16))()),
        z -> (C(32765)(I16), WrappingSum(z, C(1)(I16))()),
        w -> (C(-32766)(I16), WrappingSum(w, C(-1)(I16))())
      )
    )().tchk().lower
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
  }

  test("WrappingDiff") {
    val n = 10
    val x = Param("x")(U8) // U8, overflow low
    val y = Param("y")(U16) // U16, overflow low
    val z = Param("z")(I16) // I16, overflow low
    val w = Param("w")(I16) // I16, overflow high
    val s = StmBuild(
      n,
      Tuple(x, y, z, w)(),
      True,
      Map[Param, (Expr, Expr)](
        x -> (C(2)(U8), WrappingDiff(x, C(1)(U8))()),
        y -> (C(2)(U16), WrappingDiff(y, C(1)(U16))()),
        z -> (C(-32766)(I16), WrappingDiff(z, C(1)(I16))()),
        w -> (C(32765)(I16), WrappingDiff(w, C(-1)(I16))())
      )
    )().tchk().lower
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
  }

  test("WrappingProd") {
    val n = 10
    val x = Param("x")(U8) // U8, overflow high
    val y = Param("y")(U16) // U16, overflow high
    val z = Param("z")(I16) // I16, overflow high
    val w = Param("w")(I16) // I16, overflow low
    val s = StmBuild(
      n,
      Tuple(x, y, z, w)(),
      True,
      Map[Param, (Expr, Expr)](
        x -> (C(50)(U8), WrappingProd(x, C(3)(U8))()),
        y -> (C(2000)(U16), WrappingProd(y, C(5)(U16))()),
        z -> (C(1000)(I16), WrappingProd(z, C(5)(I16))()),
        w -> (C(-2000)(I16), WrappingProd(w, C(3)(I16))())
      )
    )().tchk().lower
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
  }

  test("IntFixProd") {
    val n = 256
    val i = Param("i")(U8)
    val s = StmBuild(
      n,
      Tuple(
        i,
        IntFixProd(i, FixCst(8)(TyFix(U8, 7)))(), // 8/128 = 1/16
        IntFixProd(i, FixCst(32)(TyFix(U8, 7)))(), // 32/128 = 1/4
        IntFixProd(i, FixCst(5)(TyFix(U8, 7)))(), // 5/128
        IntFixProd(i, FixCst(64)(TyFix(U8, 10)))() // 64/1024 = 1/16
      )(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (C(0)(U8), Sum(C(1)(U8), i)())
      )
    )().tchk().lower
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
  }

  test("StmRange(10, -2, 3)") {
    val s =
      StmRange(10, C(-2)(I8), C(3)(I8))().tchk().lower.asInstanceOf[StmBuild]
    val inputs = Seq(Seq(), Seq())
    val options = VhdlGeneratorOptions(outName = Some("result"))
    assert(VhdlTestRunner.testExpr(s, inputs, options) == TestPassed)
  }

  test("StmCst(20, True)") {
    val s = StmCst(20, True)().tchk().lower.asInstanceOf[StmBuild]
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
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
      )().tchk().lower.asInstanceOf[StmBuild]
    }
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
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
      )().tchk().lower.asInstanceOf[StmBuild]
    }
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
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
      )().tchk().lower.asInstanceOf[StmBuild]
    }
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
  }

  test("StmBuild:EmptyVec") {
    val s = StmBuild(3, VecBuild(0, U8 ::+ (i => i))(), True)().tchk().lower
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
  }

  test("WonkyRepeat") {
    val n = 16
    val k = 16
    val producer = {
      val a = Param("a")(I16)
      // 2 valid, 4 invalid, 2 valid, 4 invalid, ...
      StmBuild(
        n,
        a,
        a % 6 < 2,
        Map[Param, (Expr, Expr)](
          a -> (C(0)(I16), Sum(C(1)(I16), a)())
        )
      )().tchk().lower
    }
    val repeat = {
      val p = Param("p")(TyStm(I16, n))
      val v = Param("v")(TyVec(I16, n / 2))
      val i = Param("i")(U8)
      val filling1 = Param("filling1")(TyBool)
      val filling2 = Param("filling2")(TyBool)
      StmBuild(
        n * k,
        // Deliberately read the current values from `v` while filling.
        // This is to test the hardware gen for the "single-write vector"
        // pattern.
        VecAccess(v, i)(),
        !filling1,
        Map[Param, (Expr, Expr)](
          p -> (producer, filling2),
          v -> (
            Undefined(v.typ),
            VecBuild(
              n / 2,
              U8 ::+ (j =>
                Mux(filling2 && (i === j), StmData(p)(), VecAccess(v, j)())()
              )
            )()
          ),
          i -> (C(0)(U8), Mux(i === n / 2 - 1, C(0)(U8), Sum(C(1)(U8), i)())()),
          filling1 -> (True, filling1 && (i !== n / 2 - 1)),
          filling2 -> (True, filling1 || (filling2 && (i !== n / 2 - 1)))
        )
      )().tchk().lower
    }
    val consumer = {
      // 2 ready, 6 not ready, 2 ready, 6 not ready, ...
      val p = Param("p")(TyStm(I16, n * k))
      val a = Param("a")(I16)
      StmBuild(
        4 * n * k,
        Mux(a % 8 < 2, StmData(p)(), C(-1)(I16))(),
        True,
        Map[Param, (Expr, Expr)](
          a -> (C(0)(I16), Sum(C(1)(I16), a)()),
          p -> (repeat, a % 8 < 2)
        )
      )().tchk().lower
    }
    assert(VhdlTestRunner.testExpr(consumer) == TestPassed)
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
      )().tchk().lower.asInstanceOf[StmBuild]
    }
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
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
      )().tchk().lower.asInstanceOf[StmBuild]
    }
    val options = VhdlGeneratorOptions(
      topName = "my_counter",
      clock = "clock",
      reset = "reset",
      outName = Some("the_count")
    )
    assert(VhdlTestRunner.testExpr(s, options = options) == TestPassed)
  }

  test("StmCount |> StmScanInclusive(0, +)") {
    val s = {
      val n = 20
      val s =
        StmScanInclusive(StmCount(C(n)(U8))(), C(0)(U8), PlusFunction(U8))()
      s.tchk().lower.asInstanceOf[StmBuild]
    }
    assert(VhdlTestRunner.testExpr(s) == TestPassed)

    val optimized =
      stmBuildSimplifier.simplify(s)().tchk().lower.asInstanceOf[StmBuild]
    assert(VhdlTestRunner.testExpr(optimized) == TestPassed)
  }

  test("StmCount |> StmScanExclusive(0, +)") {
    val s = {
      val n = 20
      val s =
        StmScanExclusive(StmCount(C(n)(U8))(), C(0)(U8), PlusFunction(U8))()
      s.tchk().lower.asInstanceOf[StmBuild]
    }
    assert(VhdlTestRunner.testExpr(s) == TestPassed)

    val optimized =
      stmBuildSimplifier.simplify(s)().tchk().lower.asInstanceOf[StmBuild]
    assert(VhdlTestRunner.testExpr(optimized) == TestPassed)
  }

  test("StmCount |> StmFold(0, +)") {
    val s = {
      val n = 20
      val s = StmFold(StmCount(C(n)(U8))(), C(0)(U8), PlusFunction(U8))()
      s.tchk().lower.asInstanceOf[StmBuild]
    }
    assert(VhdlTestRunner.testExpr(s) == TestPassed)

    val optimized =
      stmBuildSimplifier.simplify(s)().tchk().lower.asInstanceOf[StmBuild]
    assert(VhdlTestRunner.testExpr(optimized) == TestPassed)
  }

  test("s => StmCount(12)") {
    // Input deliberately unused
    val s = Param("s")(TyStm(U8, 1))
    val count = StmCount(C(12)(U8))().tchk().lower.asInstanceOf[StmBuild]
    val f = Function(s, count)().tchk()
    val inputs = Seq(
      Seq(DirectTestInput(Seq(Some(C(42)(U8)))))
    )
    assert(VhdlTestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("s => s |> StmMap") {
    val n = 8
    val s = Param("s")(TyStm(U16, n))
    val map = SimpleMap(s, x => (x + 1) * x + 42).tchk().lower
    val f = Function(s, map)().tchk()
    val inputs = Seq(
      Seq(
        DirectTestInput((0 until n).flatMap(i => Seq(None, Some(C(i)(U16)))))
      ),
      Seq(
        DirectTestInput((0 until n).map(x => C(x * x)(U16)).map(Some(_)))
      )
    )
    assert(VhdlTestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("s => s |> ZipWithIndex") {
    val n = 8
    val t = TyTuple(U8, TyBool, TyVec(I9, 3))
    val s = Param("s")(TyStm(t, n))
    val zip = SimpleZip(SimpleCount(C(n)(U8)), s).tchk().lower
    val f = Function(s, zip)().tchk()
    val inputs = Seq(
      {
        val v =
          (i: Int) =>
            Tuple(
              C(i)(U8),
              i % 2 === 0,
              VecLiteral(C(i - 1)(I9), C(i)(I9), C(i + 1)(I9))()
            )()
        Seq(
          DirectTestInput((0 until n).flatMap(i => Seq(None, Some(v(i)), None)))
        )
      },
      Seq(
        DirectTestInput(
          (0 until n)
            .map(t =>
              Tuple(
                C(t)(U8),
                t % 3 === 0,
                VecLiteral(C(t)(I9), C(t - 42)(I9), C(t * t)(I9))()
              )()
            )
            .map(Some(_))
        )
      )
    )
    assert(VhdlTestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("a => b => c => StmZip(StmZip(a, b), c)") {
    val n = 20
    val a = Param("a")(TyStm(U16, n))
    val b = Param("b")(TyStm(U16, n))
    val c = Param("c")(TyStm(U16, n))
    val zip = StmZip(StmZip(a, b)(), c)().tchk().lower
    val f = Function(a, Function(b, Function(c, zip)())())().tchk()
    val inputs = Seq(
      Seq(
        DirectTestInput((0 until n).flatMap(i => Seq(Some(C(i)(U16))))),
        DirectTestInput(
          (0 until n).flatMap(i => Seq(Some(C(i * 2)(U16)), None))
        ),
        DirectTestInput(
          (0 until n).flatMap(i => Seq(None, Some(C(i * i)(U16)), None))
        )
      )
    )
    assert(VhdlTestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("NoHandshake1") {
    val n = 20
    val latency = 2
    val a = Param("I0", -1)(TyStm(U16, n))
    val b = Param("I1", -1)(TyStm(U16, n))
    val c = Param("I2", -1)(TyStm(U16, n))
    val zip = SimpleZip(SimpleZip(a, b), SimpleNop(c)).tchk().lower
    val f = Function(a, Function(b, Function(c, zip)())())().tchk()
    val io = TestSuiteIO(
      Seq(
        KeywordTestIO(
          Map(
            a -> DirectTestInput((0 until n).map(i => Some(C(i)(U16)))),
            b -> DirectTestInput((0 until n).map(i => Some(C(i * 2)(U16)))),
            c -> DirectTestInput((0 until n).map(i => Some(C(i * i)(U16))))
          ),
          DirectTestOutput(
            (0 until latency).map(_ =>
              Undefined(TyTuple(TyTuple(U16, U16), U16))
            ) ++
              (0 until n).map(i =>
                Tuple(C(i)(U16), C(i * 2)(U16), C(i * i)(U16))()
              ),
            (0 until latency).map(_ => AllOne(TyTuple(TyTuple(U16, U16), U16)))
              ++ (0 until n).map(_ => AllZero(TyTuple(TyTuple(U16, U16), U16)))
          )
        )
      )
    )
    assert(VhdlTestRunner.testWithoutHandshake(f, io) == TestPassed)
  }

  test("NoHandshake2") {
    val n = 10
    val latency = 3
    val s = Param("I0", -1)(TyStm(I16, n))
    val f = Function(
      s,
      LetStm(
        0,
        s,
        s,
        SimpleMap(
          SimpleZip(SimpleMap(s, x => x * x), SimpleMap(s, x => x * C(3)(I16))),
          x => x.__0 + x.__1
        )
      )()
    )().tchk().lower
    val io = TestSuiteIO(
      Seq(
        KeywordTestIO(
          Map(
            s -> DirectTestInput((0 until n).map(C(_)(I16)).map(Some(_)))
          ),
          DirectTestOutput(
            (0 until latency).map(_ => Undefined(I16)) ++
              (0 until n).map(i => C(i * i + 3 * i)(I16)),
            (0 until latency).map(_ => AllOne(I16)) ++
              (0 until n).map(_ => AllZero(I16))
          )
        ),
        KeywordTestIO(
          Map(
            s -> DirectTestInput(
              (0 until n)
                .map(x => if (x % 2 == 0) x else -x)
                .map(C(_)(I16))
                .map(Some(_))
            )
          ),
          DirectTestOutput(
            (0 until latency).map(_ => Undefined(I16)) ++
              (0 until n)
                .map(x => if (x % 2 == 0) x else -x)
                .map(i => C(i * i + 3 * i)(I16)),
            (0 until latency).map(_ => AllOne(I16)) ++
              (0 until n).map(_ => AllZero(I16))
          )
        )
      )
    )
    assert(VhdlTestRunner.testWithoutHandshake(f, io) == TestPassed)
  }

  test("NoHandshake3") {
    val n = 10
    val w = 3
    val latency = 2
    val s = Param("I0", -1)(TyStm(U16, n))
    val f = Function(
      s,
      StmSlideStartingWith(SimpleMap(s, x => x * x), AllZero(TyVec(U16, w)))()
    )().tchk().lower
    val io = TestSuiteIO(
      Seq(
        KeywordTestIO(
          Map(
            s -> DirectTestInput((0 until n).map(C(_)(I16)).map(Some(_)))
          ),
          DirectTestOutput(
            (0 until latency).map(_ => Undefined(TyVec(U16, w + 1))) ++ {
              ((0 until w).map(_ => 0).map(C(_)(U16))
                ++ (0 until n).map(x => x * x).map(C(_)(U16)))
                .sliding(w + 1)
                .map(xs => VecLiteral(xs: _*)())
                .toSeq
            },
            (0 until latency).map(_ => AllOne(TyVec(U16, w + 1))) ++
              (0 until (w + n)).map(_ => AllZero(TyVec(U16, w + 1)))
          )
        ),
        KeywordTestIO(
          Map(
            s -> DirectTestInput((n to 1 by -1).map(C(_)(I16)).map(Some(_)))
          ),
          DirectTestOutput(
            (0 until latency).map(_ => Undefined(TyVec(U16, w + 1))) ++ {
              ((0 until w).map(_ => 0).map(C(_)(U16))
                ++ (n to 1 by -1).map(x => x * x).map(C(_)(U16)))
                .sliding(w + 1)
                .map(xs => VecLiteral(xs: _*)())
                .toSeq
            },
            (0 until latency).map(_ => AllOne(TyVec(U16, w + 1))) ++
              (0 until (w + n)).map(_ => AllZero(TyVec(U16, w + 1)))
          )
        )
      )
    )
    assert(VhdlTestRunner.testWithoutHandshake(f, io) == TestPassed)
  }

  // Producer is a no-op
  test("s => let x = s in StmZip(x, StmZip(x, x))") {
    val s = Param("s")(TyStm(I16, 6))
    val x = Param("x")(TyStm(I16, 6))
    val f = Function(s, LetStm(1, x, s, SimpleZip(x, SimpleZip(x, x)))())()
      .tchk()
      .lower
    val inputs = Seq(
      Seq(
        DirectTestInput(
          Seq(
            Some(C(0)(I16)),
            Some(C(-1)(I16)),
            Some(C(42)(I16)),
            Some(C(99)(I16)),
            Some(C(-100)(I16)),
            Some(C(1)(I16))
          )
        )
      )
    )
    assert(VhdlTestRunner.testExpr(f, inputs) == TestPassed)
  }

  // Consumer is a no-op (using the bound variable)
  test("s => let x = StmMap(s, +5) in x") {
    val s = Param("s")(TyStm(I16, 6))
    val x = Param("x")(TyStm(I16, 6))
    val f =
      Function(
        s,
        LetStm(1, x, StmMap(s, I16 ::+ (y => Sum(C(5)(I16), y)()))(), x)()
      )().tchk().lower
    val inputs = Seq(
      Seq(
        DirectTestInput(
          Seq(
            Some(C(0)(I16)),
            Some(C(-1)(I16)),
            Some(C(42)(I16)),
            Some(C(99)(I16)),
            Some(C(-100)(I16)),
            Some(C(1)(I16))
          )
        )
      )
    )
    assert(VhdlTestRunner.testExpr(f, inputs) == TestPassed)
  }

  // Consumer is a no-op (using an input variable)
  // Bound variable is unused
  test("s => let x = StmCount(10) in s") {
    val s = Param("s")(TyStm(I16, 6))
    val x = Param("x")(TyStm(U32, 10))
    val f =
      Function(s, LetStm(1, x, StmCount(C(10)(U32))(), s)())().tchk().lower
    val inputs = Seq(
      Seq(
        DirectTestInput(
          Seq(
            Some(C(0)(I16)),
            Some(C(-1)(I16)),
            Some(C(42)(I16)),
            Some(C(99)(I16)),
            Some(C(-100)(I16)),
            Some(C(1)(I16))
          )
        )
      )
    )
    assert(VhdlTestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("s => s") {
    val n = 8
    val f = TyStm(U8, n) ::+ (s => s)
    val inputs = Seq(
      Seq(DirectTestInput(Seq((0 until n).map(t => Some(C(t)(U8))): _*)))
    )
    assert(VhdlTestRunner.testExpr(f, inputs) == TestPassed)
  }

  // Input stream is used by the consumer
  test("s => let idx = StmCount(10) in StmZip(idx, s)") {
    val n = 10
    val s = Param("s")(TyStm(U8, n))
    val idx = Param("idx")(TyStm(U8, n))
    val f =
      Function(
        s,
        LetStm(1, idx, SimpleCount(C(n)(U8)), SimpleZip(idx, s))()
      )().tchk().lower
    val inputs = Seq(
      Seq(DirectTestInput((0 until n).map(C(_)(U8)).map(Some(_)))),
      Seq(
        DirectTestInput(
          (0 until n).map(t => t * (t + 1)).map(C(_)(U8)).map(Some(_))
        )
      )
    )
    assert(VhdlTestRunner.testExpr(f, inputs) == TestPassed)
  }

  // Input stream is unused
  // Consumer type is different from producer type
  test("s => let x = StmCount(10) in StmMap(x, x => x % 2 == 0)") {
    val s = Param("s")(TyStm(U8, 10))
    val x = Param("x")(TyStm(U8, 10))
    val f =
      Function(
        s,
        LetStm(
          1,
          x,
          StmCount(C(10)(U8))(),
          StmMap(x, U8 ::+ (y => y % 2 === 0))()
        )()
      )().tchk().lower
    val inputs = Seq(
      Seq(
        DirectTestInput(
          (0 until 10).map(t => t * (t + 1)).map(C(_)(U8)).map(Some(_))
        )
      )
    )
    assert(VhdlTestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("LetStm:More complex example") {
    val n = 5
    val x = Param("x")(TyStm(U8, n))
    val s = Param("s")(TyStm(U8, n))
    val body = SimpleMap(
      LetStm(
        1,
        x,
        s,
        SimpleZip(
          SimpleCount(C(n)(U8)),
          x,
          SimpleMap(x, x => Sum(C(5)(U8), x)())
        )
      )()
        .tchk(),
      x => Tuple(x.__0, x.__1, x.__2, C(3)(U8) * x.__1 + x.__2)()
    )
    val f = Function(s, body)().tchk().lower
    val inputs = Seq(
      Seq(DirectTestInput((0 until n).map(C(_)(U8)).map(Some(_)))),
      Seq(DirectTestInput((0 until n).map(t => C(t * t)(U8)).map(Some(_))))
    )
    assert(VhdlTestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("LetStm:BufSizeMoreThanOne") {
    val n = 8
    val m = 4
    val stm = {
      // StmCount(n*m)
      val count = {
        val i = Param("i")(U8)
        StmBuild(
          n * m,
          i,
          True,
          Map[Param, (Expr, Expr)](
            i -> (C(0)(U8), Sum(C(1)(U8), i)())
          )
        )().tchk()
      }
      val x = Param("s")(TyStm(U8, n * m))
      val rowSums = {
        val t = Param("t")(U8)
        val acc = Param("acc")(U8)
        val s = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          Sum(StmData(s)(), acc)(),
          t === (m - 1),
          Map[Param, (Expr, Expr)](
            t -> (
              C(0)(U8),
              Mux(t === (m - 1), C(0)(U8), Sum(C(1)(U8), t)())()
            ),
            acc -> (
              C(0)(U8),
              Mux(
                t === (m - 1),
                C(0)(U8),
                Sum(StmData(s)(), acc)()
              )()
            ),
            s -> (x, True)
          )
        )().tchk()
      }
      val rowHeads = {
        val t = Param("t")(U8)
        val s = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          StmData(s)(),
          t === 0,
          Map[Param, (Expr, Expr)](
            t -> (
              C(0)(U8),
              Mux(t === (m - 1), C(0)(U8), Sum(C(1)(U8), t)())()
            ),
            s -> (x, True)
          )
        )().tchk()
      }
      val zip = {
        val s0 = Param("s0")(TyStm(U8, -1))
        val s1 = Param("s1")(TyStm(U8, -1))
        StmBuild(
          n,
          Tuple(StmData(s0)(), StmData(s1)())(),
          True,
          Map[Param, (Expr, Expr)](
            s0 -> (rowSums, True),
            s1 -> (rowHeads, True)
          )
        )().tchk()
      }
      LetStm(m, x, count, zip)().tchk().lower
    }
    assert(VhdlTestRunner.testExpr(stm) == TestPassed)
  }

  test("FunctionShadowing") {
    val s = {
      val a = Param("a")(U8)
      val x = Param("x")(U8)
      StmBuild(
        10,
        FunCall(
          Function(x, FunCall(Function(x, Sum(C(1)(U8), x)())(), a)())(),
          C(42)(U8)
        )(),
        True,
        Map[Param, (Expr, Expr)](
          a -> (C(0)(U8), Sum(C(1)(U8), a)())
        )
      )().tchk()
    }
    val inputs = Seq(Seq(), Seq())
    assert(VhdlTestRunner.testExpr(s, inputs) == TestPassed)
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
    )().tchk().lower
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
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
    val s = {
      val s = Param("s")(TyStm(U8, n))
      StmBuild(
        n,
        Tuple(v, a, Let(x, a.__0, Tuple(j, x, StmData(s)())())())(),
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
                  Tuple(
                    StmData(s)() + x + VecAccess(v, 0)(),
                    y + VecAccess(v, 1)()
                  )(),
                  Tuple(
                    StmData(s)() + y + VecAccess(v, 0)(),
                    x + VecAccess(v, 1)()
                  )()
                )()
              )()
            )()
          ),
          v -> (
            VecBuild(n, U8 ::+ (_ => AllZero(U8)))(),
            VecShiftLeft(v, a.__0)()
          ),
          s -> (
            StmRange(n, C(42)(U8), C(3)(U8))(),
            True
          )
        )
      )().tchk().lower
    }
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
  }

  test("VecAccess(VecBuild(...), i)") {
    val s =
      StmBuild(2, VecAccess(VecBuild(3, U8 ::+ (i => 42 + i))(), 1)(), True)()
        .tchk()
        .lower
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
  }

  test("f( (42, True, -1) )") {
    val f = (U8, TyBool, I16) ::+ (x => x.__0)
    val s =
      StmBuild(2, f(Tuple(C(42)(U8), True, C(-1)(I16))()), True)()
        .tchk()
        .lower
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
  }

  test("Tuple(10, 20, 30).__1") {
    val s =
      StmBuild(2, Tuple(C(10)(), C(20)(), C(30)())().__1, True)()
        .tchk()
        .lower
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
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
    )().tchk().lower.uncurry
    assert(VhdlTestRunner.testExpr(s) == TestPassed)
  }

  test("DotProduct") {
    val n = 100
    val a = Param("a")(TyStm(U32, n))
    val b = Param("b")(TyStm(U16, n))
    val s = StmFold(
      StmMap(StmZip(a, b)(), (U32, U16) ::+ (x => x.__0 * x.__1))(),
      C(0)(U32),
      PlusFunction(U32)
    )().tchk().lower.asInstanceOf[StmBuild]
    val f0 = Function(a, Function(b, s)())().tchk()
    val inputs = Seq(
      Seq(
        DirectTestInput((0 until n).map(i => Some(C(i * i)(U32)))),
        DirectTestInput(
          (0 until n).flatMap(i => Seq(Some(C(3 * i)(U16)), None, None))
        )
      )
    )
    assert(VhdlTestRunner.testExpr(f0, inputs) == TestPassed)

    def optimize(s: StmBuild): StmBuild = {
      val s0 =
        stmBuildSimplifier.simplify(s)().tchk().lower.asInstanceOf[StmBuild]
      val s1 = s0.fuseCompletely().tchk().lower.asInstanceOf[StmBuild]
      val s2 =
        stmBuildSimplifier.simplify(s1)().tchk().lower.asInstanceOf[StmBuild]
      s2
    }
    val optimized = optimize(s)
    val fOpt = Function(a, Function(b, optimized)())().tchk()
    assert(VhdlTestRunner.testExpr(fOpt, inputs) == TestPassed)
  }

  test("2DMapFold") {
    val n = 16
    val m = 8
    val s = Param("s")(TyStm(TyStm(U16, m), n))
    val rowSums =
      StmMap(
        s,
        TyStm(U16, m) ::+ (s => StmFold(s, C(0)(U16), PlusFunction(U16))())
      )().tchk().lower.asInstanceOf[StmBuild]

    val inputs = Seq(
      Seq(
        DirectTestInput(
          (0 until n * m).flatMap(i => Seq(None, Some(C(i)(U16))))
        )
      )
    )

    val optimized =
      stmBuildSimplifier
        .simplify(rowSums)()
        .tchk()
        .lower
        .asInstanceOf[StmBuild]
    val f = Function(s.lowerParam, optimized)().tchk()
    assert(VhdlTestRunner.testExpr(f, inputs) == TestPassed)
  }

  test("1DStmSlide") {
    val n = 50
    val m = 3
    val s = Param("s")(TyStm(TyTuple(U8, I8, TyBool), n))
    val slide = StmSlideS(s, m = m)().tchk().lower

    val inputs = Seq(
      Seq(
        DirectTestInput(
          (0 until n).flatMap(i =>
            Seq(
              None,
              Some(Tuple(C(i + 1)(U8), C(i - 10)(I8), i % 3 === 0)()),
              None
            )
          )
        )
      )
    )

    val f0 = Function(s, slide)().tchk().lower
    assert(VhdlTestRunner.testExpr(f0, inputs) == TestPassed)

    val optimized = stmSimplifier.simplify(slide).tchk().lower
    val f1 = Function(s, optimized)().tchk()
    assert(VhdlTestRunner.testExpr(f1, inputs) == TestPassed)
  }

  test("StmShiftRightGarbage") {
    val n = 8
    val shiftAmount = 2
    val s = Param("s")(TyStm(U8, n))
    val stm = {
      val x = Param("x")(TyStm(U8, n))
      Function(
        s,
        StmSuffix(
          LetStm(
            1,
            x,
            s,
            SimpleZip(x, StmShiftRightGarbage(x, shiftAmount)().tchk())
          )(),
          n - shiftAmount
        )()
      )().tchk().lower
    }
    val inputs = Seq(
      Seq(DirectTestInput((0 until n).map(C(_)(U8)).map(Some(_)))),
      Seq(DirectTestInput((0 until n).map(t => C(t * t)(U8)).map(Some(_))))
    )
    assert(VhdlTestRunner.testExpr(stm, inputs) == TestPassed)
  }

  test("Undefined") {
    val e = {
      val i = Param("i")(U8)
      StmBuild(
        5,
        Mux(
          i % 2 === 0,
          Tuple(i, i, i)(),
          Tuple(
            VecAccess(Undefined(TyVec(U8, 2)), 0)(),
            Undefined(TyTuple(U8, I16)).__0,
            Undefined(U8) + 1
          )()
        )(),
        i % 2 === 0,
        Map[Param, (Expr, Expr)](
          i -> (C(0)(U8), i + 1)
        )
      )().tchk().lower
    }
    assert(VhdlTestRunner.testExpr(e) == TestPassed)
  }
}
