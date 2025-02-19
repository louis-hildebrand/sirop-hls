package operations

import ir._
import org.scalatest.funsuite.AnyFunSuite

class ArithTests extends AnyFunSuite {
  test("Min") {
    val x = Param("x")
    val y = Param("y")
    val e = Min(x, y)

    val min = (xVal: Int, yVal: Int) =>
      ir.eval(Let(x, xVal, Let(y, yVal, e)))
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
      ir.eval(Let(x, xVal, Let(y, yVal, e)))
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
      ir.eval(Let(x, xVal, Let(y, yVal, e)))
        .asInstanceOf[IntCst]
        .i
    for (x <- -4 to 4) {
      for (y <- (-4 to 4).diff(Seq(0))) {
        val expected = (x.toDouble / y.toDouble).ceil.toInt
        val actual = ceildiv(x, y)
        assert(actual == expected, s"(for x = $x, y = $y)")
      }
    }
  }
}
