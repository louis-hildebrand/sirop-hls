package operations

import ir._
import org.scalatest.funsuite.AnyFunSuite

class ArithTests extends AnyFunSuite {
  test("Min") {
    val x = Param("x")()
    val y = Param("y")()
    val e = Min(x, y)

    val min = (xVal: Int, yVal: Int) =>
      ir.eval(Let(x, IntCst(xVal)(I8), Let(y, IntCst(yVal)(I8), e)())())
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
    val e = Max(x, y)

    val max = (xVal: Int, yVal: Int) =>
      ir.eval(Let(x, IntCst(xVal)(I8), Let(y, IntCst(yVal)(I8), e)())())
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
    val e = CeilDiv(x, y)

    val ceildiv = (xVal: Int, yVal: Int) =>
      ir.eval(Let(x, IntCst(xVal)(I8), Let(y, IntCst(yVal)(I8), e)())())
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
      (xVal: Int) => ir.eval(Let(x, C(xVal)(U32), e)()).asInstanceOf[IntCst].i
    for (xVal <- Seq(0, 1, 2, 255, 256, 257)) {
      assert(cast(xVal) == xVal % 256)
    }
  }

  test("Cast:u32 to i8") {
    val x = Param("x")(U32)
    val e = Cast(x, I8)().tchk().lower()
    val cast =
      (xVal: Int) => ir.eval(Let(x, C(xVal)(U32), e)()).asInstanceOf[IntCst].i
    assert(cast(0) == 0)
    assert(cast(1) == 1)
    assert(cast(2) == 2)
    assert(cast(127) == 127)
    assert(cast(128) == -128)
    assert(cast(129) == -127)
    assert(cast(255) == -1)
    assert(cast(256) == 0)
    assert(cast(257) == 1)
  }

  test("Cast:() to ()") {
    val actual = Cast(Tuple()(), ())().tchk().lower()
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
}
