package mhir.sugar

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import org.scalatest.funsuite.AnyFunSuite

class ArithTests extends AnyFunSuite {
  test("Min") {
    val x = Param("x")()
    val y = Param("y")()
    val e = Min(x, y)()

    val min = (xVal: Int, yVal: Int) =>
      mhir.eval
        .eval(Let(x, C(xVal)(), Let(y, C(yVal)(), e)())())
        .asInstanceOf[IntCst]
        .i
    for (xVal <- -10 to 10) {
      for (yVal <- -10 to 10) {
        assert(min(xVal, yVal) == Math.min(xVal, yVal))
      }
    }
  }

  test("Max") {
    val x = Param("x")()
    val y = Param("y")()
    val e = Max(x, y)()

    val max = (xVal: Int, yVal: Int) =>
      mhir.eval
        .eval(Let(x, C(xVal)(), Let(y, C(yVal)(), e)())())
        .asInstanceOf[IntCst]
        .i
    for (xVal <- -10 to 10) {
      for (yVal <- -10 to 10) {
        assert(max(xVal, yVal) == Math.max(xVal, yVal))
      }
    }
  }

  test("CeilDiv") {
    val x = Param("x")()
    val y = Param("y")()
    val e = CeilDiv(x, y)()

    val ceildiv = (xVal: Int, yVal: Int) =>
      mhir.eval
        .eval(Let(x, C(xVal)(I8), Let(y, C(yVal)(I8), e)())())
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

  test("Cast:u32 to u8") {
    val x = Param("x")(U32)
    val e = Cast(x, U8)().tchk().lower()
    val cast =
      (xVal: Int) =>
        mhir.eval.eval(Let(x, C(xVal)(U32), e)()).asInstanceOf[IntCst].i
    assert(cast(0) == 0)
    assert(cast(1) == 1)
    assert(cast(2) == 2)
    assert(cast(254) == 254)
    assert(cast(255) == 255)
  }

  test("Cast:u32 to i8") {
    val x = Param("x")(U32)
    val e = Cast(x, I8)().tchk().lower()
    val cast =
      (xVal: Int) =>
        mhir.eval.eval(Let(x, C(xVal)(U32), e)()).asInstanceOf[IntCst].i
    assert(cast(0) == 0)
    assert(cast(1) == 1)
    assert(cast(2) == 2)
    assert(cast(126) == 126)
    assert(cast(127) == 127)
  }

  test("Cast:() to ()") {
    val actual = Cast(Tuple()(), TyTuple())().tchk().lower()
    val expected = Tuple()()
    assert(actual == expected)
  }

  test("Cast:(u8, u16, u32) to (u32, u16, u8)") {
    val x = Param("x")((U8, U16, U32))
    val actual = Cast(x, (U32, U16, U8))().tchk().lower()
    val expected = Tuple(PadTo(x.__0, 32)(), x.__1, TruncateTo(x.__2, 8)())()
    assert(actual == expected)
  }

  test("Cast:(i8, i16, i32) to (i32, i16, i8)") {
    val x = Param("x")((I8, I16, I32))
    val actual = Cast(x, (I32, I16, I8))().tchk().lower()
    val expected = Tuple(PadTo(x.__0, 32)(), x.__1, TruncateTo(x.__2, 8)())()
    assert(actual == expected)
  }

  test("Cast:Vec[u8, n] to Vec[u16, n]") {
    val n = Param("n")(U8)
    val x = Param("x")(TyVec(U8, n))
    val actual = Cast(x, TyVec(U16, n))().tchk().lower()
    val expected = VecBuild(n, U8 ::+ (i => PadTo(VecAccess(x, i)(), 16)()))()
    assert(actual == expected)
  }

  test("SafeSum") {
    val u3 = TyUInt(3)
    val u4 = TyUInt(4)
    val e = SafeSum(IntCst(4)(u3), IntCst(4)(u3))().tchk()

    assert(e.typ == u4)
    assert(mhir.eval.eval(e) == IntCst(8)())

    assert(mhir.eval.eval(SafeSum()()) == C(0)())
  }
}
