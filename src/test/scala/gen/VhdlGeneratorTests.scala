package gen

import ir._
import org.scalatest.funsuite.AnyFunSuite
import operations._

class VhdlGeneratorTests extends AnyFunSuite {
  // TODO: Support other types of streams (streams of tuples, vectors)
  // TODO: Support designs that take inputs (e.g., StmCount . StmMap)?
  // TODO: Support designs that take external inputs (e.g., s => StmMap(s, ...))?

  test("StmCount(12)") {
    val s = StmCount(12)
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmRange(10, -2, 3)") {
    val s = StmRange(10, -2, 3)
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmCst(20, True)") {
    val s = StmCst(20, True)
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmBuildWithBoolVars") {
    val s = {
      val b = Param("b")
      val i = Param("i")
      StmBuild(
        5,
        IfThenElse(b, SSome(i), NNone(TyInt)),
        Map[Param, (Expr, Expr)](b -> (True, Not(b)()), i -> (0, i + 1))
      )().lowerAll().asInstanceOf[StmBuild]
    }
    assert(TestRunner.testExpr(s) == TestPassed)
  }
}
