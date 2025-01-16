package operations

import ir._
import opt.PartialEvalPass
import org.scalatest.funsuite.AnyFunSuite

class ArithTests extends AnyFunSuite {
  test("Min") {
    val x = Param("x")
    val y = Param("y")
    val e = Min(x, y)

    val min = (xVal: Int, yVal: Int) =>
      PartialEvalPass
        .partialEval(Let(x, xVal, Let(y, yVal, e)))
        .asInstanceOf[IntCst]
        .i
    for (xVal <- -10 to 10) {
      for (yVal <- -10 to 10) {
        assert(min(xVal, yVal) == Math.min(xVal, yVal))
      }
    }
  }

  test("Max") {
    val x = Param("x")
    val y = Param("y")
    val e = Max(x, y)

    val max = (xVal: Int, yVal: Int) =>
      PartialEvalPass
        .partialEval(Let(x, xVal, Let(y, yVal, e)))
        .asInstanceOf[IntCst]
        .i
    for (xVal <- -10 to 10) {
      for (yVal <- -10 to 10) {
        assert(max(xVal, yVal) == Math.max(xVal, yVal))
      }
    }
  }

  test("CeilDiv") {
    val x = Param("x")
    val y = Param("y")
    val e = CeilDiv(x, y)

    val ceildiv = (xVal: Int, yVal: Int) =>
      PartialEvalPass
        .partialEval(Let(x, xVal, Let(y, yVal, e)))
        .asInstanceOf[IntCst]
        .i
    assert(ceildiv(0, 1) == 0)
    assert(ceildiv(0, 2) == 0)
    assert(ceildiv(0, 3) == 0)
    assert(ceildiv(0, 4) == 0)
    assert(ceildiv(1, 1) == 1)
    assert(ceildiv(1, 2) == 1)
    assert(ceildiv(1, 3) == 1)
    assert(ceildiv(1, 4) == 1)
    assert(ceildiv(2, 1) == 2)
    assert(ceildiv(2, 2) == 1)
    assert(ceildiv(2, 3) == 1)
    assert(ceildiv(2, 4) == 1)
    assert(ceildiv(3, 1) == 3)
    assert(ceildiv(3, 2) == 2)
    assert(ceildiv(3, 3) == 1)
    assert(ceildiv(3, 4) == 1)
    assert(ceildiv(4, 1) == 4)
    assert(ceildiv(4, 2) == 2)
    assert(ceildiv(4, 3) == 2)
    assert(ceildiv(4, 4) == 1)
  }
}
