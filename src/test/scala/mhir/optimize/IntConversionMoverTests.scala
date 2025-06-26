package mhir.optimize

import mhir.ir.Lowering.ExprLowering
import mhir.ir.TypeChecker.TypeCheck
import mhir.ir._
import mhir.testing.ParamStore
import org.scalatest.funsuite.AnyFunSuite

/** Tests for [[IntConversionMover]].
  */
class IntConversionMoverTests extends AnyFunSuite {
  private val x = ParamStore("x")
  private val y = ParamStore("y")
  private val z = ParamStore("z")
  private val c = ParamStore("c")

  // Widen: simple examples --------------------------------------------------------------------------------------------

  // TODO: Also test Mux?

  test("Widen:Pad(x:u8 + y:u8)") {
    val e = PadTo(Sum(x(U8), y(U8))(), 10)().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = Sum(PadTo(x(U8), 10)(), PadTo(y(U8), 10)())()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:Pad(2:i8 * x:i8)") {
    val e = PadTo(Prod(C(2)(I8), x(I8))(), 16)().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = Prod(PadTo(C(2)(I8), 16)(), PadTo(x(I8), 16)())()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:Pad(if Pad(x * y) == 6 then x / y else x % y)") {
    val e = {
      val c = PadTo(Prod(x(U16), y(U16))(), 32)() equ C(6)(U32)
      val t = Div(x(U8), y(U8))()
      val f = Mod(x(U8), y(U8))()
      PadTo(Mux(c, t, f)(), 20)().tchk().lower()
    }
    val actual = IntConversionMover.widen(e)
    val expected = {
      val c = Prod(PadTo(x(U16), 32)(), PadTo(y(U16), 32)())() equ C(6)(U32)
      val t = Div(PadTo(x(U8), 20)(), PadTo(y(U8), 20)())()
      val f = Mod(PadTo(x(U8), 20)(), PadTo(y(U8), 20)())()
      Mux(c, t, f)()
    }
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:Truncate(x:i16) + y:i8 + Truncate(z:i16)") {
    val e = Sum(TruncateTo(x(I16), 8)(), y(I8), TruncateTo(z(I16), 8)())()
      .tchk()
      .lower()
    val actual = IntConversionMover.widen(e)
    val expected = TruncateTo(Sum(x(I16), PadTo(y(I8), 16)(), z(I16))(), 8)()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:x:u8 * Truncate(y:u16)") {
    val e = Prod(x(U8), TruncateTo(y(U16), 8)())().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = TruncateTo(Prod(PadTo(x(U8), 16)(), y(U16))(), 8)()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:ToSigned(x:u8 + y:u8)") {
    val e = ToSigned(Sum(x(U8), y(U8))())().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = Sum(ToSigned(x(U8))(), ToSigned(y(U8))())()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:ToSigned(if ToSigned(x * y) == 6 then x / y else x % y)") {
    val e = {
      val c = ToSigned(Prod(x(U16), y(U16))())() equ C(6)(I17)
      val t = Div(x(U8), y(U8))()
      val f = Mod(x(U8), y(U8))()
      ToSigned(Mux(c, t, f)())().tchk().lower()
    }
    val actual = IntConversionMover.widen(e)
    val expected = {
      val c = Prod(ToSigned(x(U16))(), ToSigned(y(U16))())() equ C(6)(I17)
      val t = Div(ToSigned(x(U8))(), ToSigned(y(U8))())()
      val f = Mod(ToSigned(x(U8))(), ToSigned(y(U8))())()
      Mux(c, t, f)()
    }
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:ToUnsigned(x:i17) + y:u16") {
    val e = Sum(ToUnsigned(x(I17))(), y(U16))().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = ToUnsigned(Sum(x(I17), ToSigned(y(U16))())())()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:x:u8 * ToUnsigned(y:i9) * ToUnsigned(z:i9)") {
    val e =
      Prod(x(U8), ToUnsigned(y(I9))(), ToUnsigned(z(I9))())().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = ToUnsigned(Prod(ToSigned(x(U8))(), y(I9), z(I9))())()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  // Widen: padding/truncating primitives crossing paths ---------------------------------------------------------------

  test("Widen:PadTo(ToUnsigned(TruncateTo(x)))") {
    val e = PadTo(ToUnsigned(TruncateTo(x(I16), 6)())(), 8)().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = ToUnsigned(TruncateTo(x(I16), 9)())()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:Pad(ToSigned(Truncate(x)))") {
    val e = PadTo(ToSigned(TruncateTo(x(U16), 8)())(), 12)().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = TruncateTo(ToSigned(x(U16))(), 12)()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:ToSigned(Truncate(ToUnsigned(x)))") {
    val e = ToSigned(TruncateTo(ToUnsigned(x(I8))(), 5)())().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = TruncateTo(x(I8), 6)()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:ToSigned(Pad(ToUnsigned(x)))") {
    val e = ToSigned(PadTo(ToUnsigned(x(I8))(), 15)())().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = PadTo(x(I8), 16)()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:TruncateTo(PadTo(x:U8, 16), 12)") {
    val e = TruncateTo(PadTo(x(U8), 16)(), 12)().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = PadTo(x(U8), 12)()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:TruncateTo(PadTo(x:U8, 12), 6)") {
    val e = TruncateTo(PadTo(x(U8), 12)(), 6)().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = TruncateTo(x(U8), 6)()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:TruncateTo(PadTo(x:U8, 13), 8)") {
    val e = TruncateTo(PadTo(x(U8), 13)(), 8)().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = x(U8)
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:ToUnsigned(ToSigned(x))") {
    val e = ToUnsigned(ToSigned(x(U8))())().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = x(U8)
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  // Widen: group similar primitives so the partial evaluator has an easier time ---------------------------------------
  // (Push ToUnsigned and ToSigned all the way to the outside.)

  test("Widen:Truncate(ToUnsigned(Truncate(x))") {
    val e =
      TruncateTo(ToUnsigned(TruncateTo(x(I16), 10)())(), 6)().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = ToUnsigned(TruncateTo(x(I16), 7)())()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:PadTo(ToSigned(PadTo(x)))") {
    val e = PadTo(ToSigned(PadTo(x(U8), 16)())(), 32)().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = PadTo(ToSigned(x(U8))(), 32)()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:ToUnsigned(x) + Truncate(y)") {
    val e = Sum(ToUnsigned(x(I9))(), TruncateTo(y(U16), 8)())().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected =
      ToUnsigned(
        TruncateTo(Sum(PadTo(x(I9), 17)(), ToSigned(y(U16))())(), 9)()
      )()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:TruncateTo(x:u16) + TruncateTo(y:u32)") {
    val e =
      Sum(TruncateTo(x(U16), 8)(), TruncateTo(y(U32), 8)())().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = TruncateTo(Sum(PadTo(x(U16), 32)(), y(U32))(), 8)()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:TruncateTo(x:i32) + TruncateTo(y:i16)") {
    val e =
      Sum(TruncateTo(x(I32), 8)(), TruncateTo(y(I16), 8)())().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = TruncateTo(Sum(x(I32), PadTo(y(I16), 32)())(), 8)()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  test("Widen:if c then Truncate(x) else ToUnsigned(y)") {
    val e = Mux(c(TyBool), TruncateTo(x(U16), 8)(), ToUnsigned(y(I9))())()
      .tchk()
      .lower()
    val actual = IntConversionMover.widen(e)
    val expected = ToUnsigned(
      TruncateTo(
        Mux(c(TyBool), ToSigned(x(U16))(), PadTo(y(I9), 17)())(),
        9
      )()
    )()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }

  // Widen: bigger example ---------------------------------------------------------------------------------------------

  test("Widen:ToSigned(Truncate(x) + 1) - ToSigned(Truncate(x))") {
    //     sgn(trunc(x:u16, 8) + 1:u8) + -1:i9 * sgn(trunc(x:u16, 8))
    // --> sgn(trunc(x:u16 + pad(1:u8, 16), 8)) + -1:i9 * sgn(trunc(x:u16, 8))
    // --> trunc(sgn(x:u16 + pad(1:u8, 16)), 9) + -1:i9 * trunc(sgn(x:u16), 9)
    // --> trunc(sgn(x:u16 + pad(1:u8, 16)), 9) + trunc(pad(-1:i9, 17) * sgn(x:u16), 9)
    // --> trunc(sgn(x:u16 + pad(1:u8, 16)) + pad(-1:i9, 17) * sgn(x:u16), 9)
    // --> trunc(sgn(x:u16) + sgn(pad(1:u8, 16)) + pad(-1:i9, 17) * sgn(x:u16), 9)
    // --> trunc(sgn(x:u16) + pad(sgn(1:u8), 17) + pad(-1:i9, 17) * sgn(x:u16), 9)
    //
    // By the way, this is useful because the partial evaluator could simplify that to:
    //     trunc(sgn(x:u16) + 1:i17 + -1:i17 * sgn(x:u16), 9)
    // Which is basically:
    //     trunc(sgn(x) + 1 - sgn(x))
    // And the arithmetic simplifier can then recognize the simplification opportunity.
    val e = Sum(
      ToSigned(Sum(C(1)(U8), TruncateTo(x(U16), 8)())())(),
      Prod(C(-1)(I9), ToSigned(TruncateTo(x(U16), 8)())())()
    )().tchk().lower()
    val actual = IntConversionMover.widen(e)
    val expected = TruncateTo(
      Sum(
        ToSigned(x(U16))(),
        PadTo(ToSigned(C(1)(U8))(), 17)(),
        Prod(PadTo(C(-1)(I9), 17)(), ToSigned(x(U16))())()
      )(),
      9
    )()
    assert(actual == expected)
    assert(actual.typ == e.typ)
  }
}
