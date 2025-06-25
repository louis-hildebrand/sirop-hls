package mhir.opt

import mhir.ir._
import org.scalatest.funsuite.AnyFunSuite

class MuxMoverTests extends AnyFunSuite {
  private val c1 = Param("c1")(TyBool)
  private val c2 = Param("c2")(TyBool)
  private val x = Param("x")(U8)
  private val y = Param("y")(U8)
  private val z = Param("z")(U8)
  private val w = Param("w")(U8)
  private val b1 = Param("b1")(TyBool)
  private val b2 = Param("b2")(TyBool)
  private val b3 = Param("b3")(TyBool)

  test("MoveUp:x / y") {
    val e = Div(x, y)()
    assert(MuxMover.moveUp(e) == e)
  }

  test("MoveUp:x % mux(c, y, z)") {
    val e = Mod(x, Mux(c1, y, z)())()
    val actual = MuxMover.moveUp(e)
    val expected = Mux(c1, Mod(x, y)(), Mod(x, z)())()
    assert(actual == expected)
  }

  test("MoveUp:mux(c, x, y) + z") {
    val e = Sum(Mux(c1, x, y)(), z)()
    val actual = MuxMover.moveUp(e)
    val expected = Mux(c1, Sum(x, z)(), Sum(y, z)())()
    assert(actual == expected)
  }

  test("MoveUp:mux(c1, x, y) * mux(c2, z, w)") {
    val e = Prod(Mux(c1, x, y)(), Mux(c2, z, w)())()
    val actual = MuxMover.moveUp(e)
    val expected = Mux(
      c2,
      Mux(c1, Prod(x, z)(), Prod(y, z)())(),
      Mux(c1, Prod(x, w)(), Prod(y, w)())()
    )()
    assert(actual == expected)
  }

  test("MoveUp:1 + mux(c, x, y) + 2") {
    val e = Sum(C(1)(U8), Mux(c1, x, y)(), C(2)(U8))()
    val actual = MuxMover.moveUp(e)
    val expected = Mux(c1, Sum(1, x, 2)(), Sum(1, y, 2)())()
    assert(actual == expected)
  }

  test("MoveUp:mux(c1, x, mux(c2, y, z)) + 1") {
    val e = Sum(Mux(c1, x, Mux(c2, y, z)())(), C(1)(U8))()
    val actual = MuxMover.moveUp(e)
    val expected = Mux(
      c1,
      Sum(x, C(1)(U8))(),
      Mux(c2, Sum(y, C(1)(U8))(), Sum(z, C(1)(U8))())()
    )()
    assert(actual == expected)
  }

  test("MoveUp:!mux(c1, b1, mux(c2, b2, b3))") {
    val e = !Mux(c1, b1, Mux(c2, b2, b3)())()
    val actual = MuxMover.moveUp(e)
    val expected = Mux(c1, !b1, Mux(c2, !b2, !b3)())()
    assert(actual == expected)
  }

  test("MoveUp:()") {
    assert(MuxMover.moveUp(Tuple()()) == Tuple()())
  }

  test("MoveUp:(mux(c, x, y))") {
    val e = Tuple(Mux(c1, x, y)())()
    val actual = MuxMover.moveUp(e)
    val expected = Mux(c1, Tuple(x)(), Tuple(y)())()
    assert(actual == expected)
  }

  test("MoveUp:((1, 2), mux(c, x, y))") {
    val e = Tuple(Tuple(1, 2)(), Mux(c1, x, y)())()
    val actual = MuxMover.moveUp(e)
    val expected =
      Mux(c1, Tuple(Tuple(1, 2)(), x)(), Tuple(Tuple(1, 2)(), y)())()
    assert(actual == expected)
  }
}
