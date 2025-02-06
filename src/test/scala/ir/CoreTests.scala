package ir

import org.scalatest.funsuite.AnyFunSuite

class CoreTests extends AnyFunSuite {
  test("FlattenSum") {
    val x = Param()
    val y = Param()
    val z = Param()
    val w = Param()

    assert(x + y + z + w == Sum(z, x, y, w))
    assert(x + y + z + w + x != Sum(z, x, y, w))
  }

  test("FlattenProd") {
    val x = Param()
    val y = Param()
    val z = Param()
    val w = Param()

    assert(x * y * z * w == Prod(z, x, y, w))
    assert(x * y * z * w * x != Prod(z, x, y, w))
  }
}
