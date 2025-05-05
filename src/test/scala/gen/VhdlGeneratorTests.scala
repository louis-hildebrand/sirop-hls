package gen

import ir._
import org.scalatest.funsuite.AnyFunSuite
import operations._

class VhdlGeneratorTests extends AnyFunSuite {
  // TODO: Support other types of streams (streams of tuples, vectors)
  // TODO: Support designs that take inputs (e.g., StmCount . StmMap)?
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
}
