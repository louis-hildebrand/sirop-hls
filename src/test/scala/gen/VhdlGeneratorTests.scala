package gen

import ir._
import org.scalatest.funsuite.AnyFunSuite
import operations._
import opt.StmSimplifier

class VhdlGeneratorTests extends AnyFunSuite {
  // TODO: Support functions inside component (as in a Let expression)?
  // TODO: What happens if an input stream is used multiple times?
  //        (*) Directly in the top-level StmBuild, as in s => StmConcat(s, s)
  //            or s => (x => StmConcat(x, x))(s)?
  //        (*) In a sub-component, as in StmZip(StmPrefix(s, 2), StmSuffix(s, 2))
  // TODO: What if an input stream is unused? Just discard data/valid and set ready <= '0'?

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
        IfThenElse(b, SSome(i)(), NNone(TyInt))(),
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
        SSome(Tuple(x.__0, x.__2, y, x.__3, y)())(),
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
        SSome(Tuple(42, True, v)())(),
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
        SSome(StmNext(s)().__1)(),
        Map[Param, (Expr, Expr)](
          s -> (
            StmCst(n, c)(),
            StmNext(s)().__0
          )
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
        SSome(StmNext(s)().__1 + 42)(),
        Map[Param, (Expr, Expr)](
          s -> (StmCount(n)(), StmNext(s)().__0)
        )
      )().tchk().lower().asInstanceOf[StmBuild]
    }
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmCount |> StmScanInclusive(0, +)") {
    val s = {
      val n = 20
      val s = StmScanInclusive(
        StmCount(n)(),
        0,
        TyInt ::+ (acc => TyInt ::+ (x => acc + x))
      )()
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
      val s = StmScanExclusive(
        StmCount(n)(),
        0,
        TyInt ::+ (acc => TyInt ::+ (x => acc + x))
      )()
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
      val s = StmFold(
        StmCount(n)(),
        0,
        TyInt ::+ (acc => TyInt ::+ (x => acc + x))
      )()
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
}
