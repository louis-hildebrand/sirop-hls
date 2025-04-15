package gen

import org.scalatest.funsuite.AnyFunSuite

import operations._

class VhdlGeneratorTests extends AnyFunSuite {
  // TODO: Support other types of streams (e.g., streams of booleans, tuples, vectors)
  // TODO: Support designs that take inputs?

  test("StmCount(12)") {
    val s = StmCount(12)
    assert(TestRunner.testExpr(s) == TestPassed)
  }

  test("StmRange(10, -2, 3)") {
    val s = StmRange(10, -2, 3)
    assert(TestRunner.testExpr(s) == TestPassed)
  }
}
