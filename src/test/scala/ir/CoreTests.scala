package ir

import org.scalatest.funsuite.AnyFunSuite

class CoreTests extends AnyFunSuite {
  test("SumEqual") {
    val x = Param()
    val y = Param()
    val z = Param()
    val w = Param()

    assert(x + y + z + w == Sum(Seq(z, x, y, w)))
  }

  test("SumNotEqual") {
    val x = Param()
    val y = Param()
    val z = Param()
    val w = Param()

    assert(x + y + z + w + x != Sum(Seq(z, x, y, w)))
  }
}
