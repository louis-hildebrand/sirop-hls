package mhir.sugar

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class BitwiseTests extends AnyFunSuite {

  test("~(42:u8, true, 42:i8, false)") {
    val arg = Tuple(C(42)(U8), True, C(42)(I8), False)()
    val e = BitwiseNot(arg)().tchk().lower
    val expected = Tuple(C(213)(U8), False, C(-43)(I8), True)()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("(12:u4, -6:i4) & (10:u4, -4:i4)") {
    val u4 = TyUInt(4)
    val i4 = TySInt(4)
    val arg1 = Tuple(C(12)(u4), C(-6)(i4))()
    val arg2 = Tuple(C(10)(u4), C(-4)(i4))()
    val e = BitwiseAnd(arg1, arg2)().tchk().lower
    val expected = Tuple(C(8)(u4), C(-8)(i4))()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("(12:u4, -6:i4) | (10:u4, -4:i4)") {
    val u4 = TyUInt(4)
    val i4 = TySInt(4)
    val arg1 = Tuple(C(12)(u4), C(-6)(i4))()
    val arg2 = Tuple(C(10)(u4), C(-4)(i4))()
    val e = BitwiseOr(arg1, arg2)().tchk().lower
    val expected = Tuple(C(14)(u4), C(-2)(i4))()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }
}
