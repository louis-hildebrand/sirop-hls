package mhir.gen.vhdl
package transform

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck.TypeCheck
import org.scalatest.funsuite.AnyFunSuite

class BoundsCheckInsertionTests extends AnyFunSuite {

  test("EmptyVector") {
    val v = Param("v")(TyVec(TyBool, 0))
    val i = Param("i")(U8)
    val e = VecAccess(v, i)().tchk()
    val actual = BoundsCheckInsertion(e)
    assert(actual == Undefined(TyBool))
    assert(actual.typ == TyBool)
  }

  test("ConstantInBounds") {
    val v = Param("v")(TyVec(I8, 3))

    val e0 = VecAccess(v, C(0)(U8))().tchk()
    assert(BoundsCheckInsertion(e0) == e0)

    val e1 = VecAccess(v, C(1)(U8))().tchk()
    assert(BoundsCheckInsertion(e1) == e1)

    val e2 = VecAccess(v, C(2)(U8))().tchk()
    assert(BoundsCheckInsertion(e2) == e2)
  }

  test("ConstantOutOfBounds") {
    val v = Param("v")(TyVec(I8, 2))
    val e = VecAccess(v, C(2)(U8))().tchk()
    val actual = BoundsCheckInsertion(e)
    assert(actual == Undefined(I8))
    assert(actual.typ == I8)
  }

  test("PowerOfTwo:1") {
    val v = Param("v")(TyVec(I8, 1))
    val i = Param("i")(U32)
    val e = VecAccess(v, i)()
    val actual = BoundsCheckInsertion(e)
    val expected = VecAccess(v, TruncateTo(i, 0)())()
    assert(actual == expected)
  }

  test("PowerOfTwo:2") {
    val v = Param("v")(TyVec(I8, 2))
    val i = Param("i")(U32)
    val e = VecAccess(v, i)()
    val actual = BoundsCheckInsertion(e)
    val expected = VecAccess(v, TruncateTo(i, 1)())()
    assert(actual == expected)
  }

  test("PowerOfTwo:4") {
    val v = Param("v")(TyVec(I8, 4))
    val i = Param("i")(U32)
    val e = VecAccess(v, i)()
    val actual = BoundsCheckInsertion(e)
    val expected = VecAccess(v, TruncateTo(i, 2)())()
    assert(actual == expected)
  }

  test("PowerOfTwo:128") {
    val v = Param("v")(TyVec(I8, 128))
    val i = Param("i")(U32)
    val e = VecAccess(v, i)()
    val actual = BoundsCheckInsertion(e)
    val expected = VecAccess(v, TruncateTo(i, 7)())()
    assert(actual == expected)
  }

  test("NotPowerOfTwo") {
    val v = Param("v")(TyVec(I8, 100))
    val i = Param("i")(U32)
    val e = VecAccess(v, i)()
    val actual = BoundsCheckInsertion(e)
    val expected = VecAccess(v, Mux(i lt C(100)(U32), i, Undefined(U32))())()
    assert(actual == expected)
  }

  test("UnknownLength") {
    val n = Param("n")(U32)
    val v = Param("v")(TyVec(I8, n))
    val e = VecAccess(v, C(1)(U32))()
    val actual = BoundsCheckInsertion(e)
    val expected =
      VecAccess(v, Mux(C(1)(U32) lt n, C(1)(U32), Undefined(U32))())()
    assert(actual == expected)
  }
}
