package ir

import org.scalatest.funsuite.AnyFunSuite

class SugarTests extends AnyFunSuite {
  test("Substitute:[y / x](let x = x * y in x + y)") {
    val x = Param("x")(TyInt)
    val y = Param("y")(TyInt)
    val e = Let(x, x * y, x + y)()
    val actual = e.subPreserveType(x -> y)
    val expected = Let(x, y * y, x + y)()
    assert(actual == expected)
  }

  test("Substitute:[y / x](let y = x * y in x + y)") {
    val x = Param("x")(TyInt)
    val y = Param("y")(TyInt)
    val e = Let(y, x * y, x + y)()
    val actual = e.subPreserveType(x -> y)
    val y2 = Param("y2")(TyInt)
    val expected = Let(y2, y * y, y + y2)()
    assert(actual == expected)
  }

  test("Let:Equals") {
    val e1 = {
      val x = Param("x")(TyInt)
      Let(x, 42, (x + 1) * (x + 2))()
    }
    val e2 = {
      val y = Param("y")()
      Let(y, 42, (y + 1) * (y + 2))()
    }
    assert(e1 == e2)
    assert(e2 == e1)
    assert(e1.hashCode() == e2.hashCode())
  }

  test("Let:NotEquals:DifferentBody") {
    val x = Param("x")(TyInt)
    val e1 = Let(x, 42, (x + 1) * (x + 2))()
    val y = Param("y")(TyInt)
    val e2 = Let(y, 42, (y + 1) * (x + 2))()
    assert(e1 != e2)
    assert(e2 != e1)
  }

  test("Let:NotEquals:DifferentArg") {
    val x = Param("x")(TyInt)
    val e1 = Let(x, 42, (x + 1) * (x + 2))()
    val e2 = Let(x, 43, (x + 1) * (x + 2))()
    assert(e1 != e2)
    assert(e2 != e1)
  }
}
