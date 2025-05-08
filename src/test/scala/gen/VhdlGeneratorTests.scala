package gen

import ir._
import org.scalatest.funsuite.AnyFunSuite
import operations._
import opt.StmSimplifier

class VhdlGeneratorTests extends AnyFunSuite {
  // TODO: Support designs that take external inputs (e.g., s => StmMap(s, ...))?

  test("StmCount(12)") {
    val s = StmCount(12)().tchk().lower().asInstanceOf[StmBuild]
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
}
