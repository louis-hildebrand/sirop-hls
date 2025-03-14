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

  test("MakeIfThenElsePositive") {
    val c = Param("c")
    val t = Param("t")
    val f = Param("f")

    val e0 = IfThenElse(c, t, f).asInstanceOf[IfThenElse]
    assert(e0.c == c)
    assert(e0.t == t)
    assert(e0.f == f)

    val e1 = IfThenElse(Not(c), f, t)
    assert(e0 == e1)
  }

  test("FunctionsEqual") {
    val f = {
      val x = Param("x")
      Function(x, (x + 1) * (x + 2))
    }
    val g = {
      val y = Param("y")
      Function(y, (y + 1) * (y + 2))
    }
    assert(f == g)
    assert(g == f)
    assert(f.hashCode() == g.hashCode())
  }

  test("FunctionsNotEqual") {
    val x = Param("x")
    val f = Function(x, (x + 1) * (x + 2))
    val y = Param("y")
    val g = Function(y, (y + 1) * (x + 2))
    assert(f != g)
    assert(g != f)
  }
}
