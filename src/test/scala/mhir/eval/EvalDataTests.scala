package mhir.eval

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar._
import mhir.typecheck.TypeCheck
import org.scalatest.funsuite.AnyFunSuite

/** Tests for the evaluator.
  */
class EvalDataTests extends AnyFunSuite {

  /** Asserts that evaluating the given expression results in an overflow
    * warning.
    *
    * @param e
    *   the expression to evaluate.
    * @param n
    *   the expected number in the [[OverflowWarning]].
    * @param typ
    *   the expected type in the [[OverflowWarning]].
    */
  private def assertOverflow(
      e: Expr,
      n: Int,
      typ: TyAnyInt,
      op: String
  ): Unit = {
    val exc = intercept[UndefinedValException](mhir.eval.eval(e))
    assert(exc.warnings == Set(OverflowWarning(n, typ, op)))
  }

  test("IntCst") {
    assert(mhir.eval.eval(IntCst(3)()) == IntCst(3)())
  }

  test("WrappingSum(U8, U8)") {
    assert(mhir.eval.eval(WrappingSum(C(10)(U8), C(32)(U8))()) == C(42)(U8))
    assert(mhir.eval.eval(WrappingSum(C(255)(U8), C(3)(U8))()) == C(2)(U8))
    assert(
      mhir.eval.eval(WrappingSum(C(255)(U8), C(255)(U8), C(255)(U8))())
        == C(253)(U8)
    )
  }

  test("WrappingSum(U16, U16)") {
    assert(mhir.eval.eval(WrappingSum(C(10)(U16), C(32)(U16))()) == C(42)(U16))
    assert(mhir.eval.eval(WrappingSum(C(255)(U16), C(3)(U16))()) == C(258)(U16))
    assert(
      mhir.eval.eval(WrappingSum(C(255)(U16), C(65530)(U16))())
        == C(249)(U16)
    )
  }

  test("WrappingSum(I8, I8)") {
    assert(mhir.eval.eval(WrappingSum(C(1)(I8), C(-4)(I8))()) == C(-3)(I8))
    assert(mhir.eval.eval(WrappingSum(C(120)(I8), C(12)(I8))()) == C(-124)(I8))
    assert(mhir.eval.eval(WrappingSum(C(-10)(I8), C(-128)(I8))()) == C(118)(I8))
  }

  test("WrappingDiff(U8, U8)") {
    assert(mhir.eval.eval(WrappingDiff(C(42)(U8), C(3)(U8))()) == C(39)(U8))
    assert(mhir.eval.eval(WrappingDiff(C(42)(U8), C(42)(U8))()) == C(0)(U8))
    assert(mhir.eval.eval(WrappingDiff(C(42)(U8), C(43)(U8))()) == C(255)(U8))
  }

  test("WrappingDiff(U16, U16)") {
    assert(mhir.eval.eval(WrappingDiff(C(42)(U16), C(3)(U16))()) == C(39)(U16))
    assert(mhir.eval.eval(WrappingDiff(C(42)(U16), C(42)(U16))()) == C(0)(U16))
    assert(
      mhir.eval.eval(WrappingDiff(C(42)(U16), C(43)(U16))())
        == C(65535)(U16)
    )
  }

  test("WrappingDiff(I8, I8)") {
    assert(mhir.eval.eval(WrappingDiff(C(42)(I8), C(-1)(I8))()) == C(43)(I8))
    assert(mhir.eval.eval(WrappingDiff(C(42)(I8), C(-125)(I8))()) == C(-89)(I8))
    assert(mhir.eval.eval(WrappingDiff(C(-120)(I8), C(10)(I8))()) == C(126)(I8))
  }

  test("WrappingProd(U8, U8)") {
    assert(mhir.eval.eval(WrappingProd(C(6)(U8), C(7)(U8))()) == C(42)(U8))
    assert(mhir.eval.eval(WrappingProd(C(100)(U8), C(7)(U8))()) == C(188)(U8))
  }

  test("WrappingProd(U16, U16)") {
    assert(mhir.eval.eval(WrappingProd(C(6)(U16), C(7)(U16))()) == C(42)(U16))
    assert(
      mhir.eval.eval(WrappingProd(C(100)(U16), C(7)(U16))()) == C(700)(U16)
    )
    assert(
      mhir.eval.eval(WrappingProd(C(100)(U16), C(700)(U16))())
        == C(4464)(U16)
    )
  }

  test("WrappingProd(I8, I8)") {
    assert(mhir.eval.eval(WrappingProd(C(6)(I8), C(7)(I8))()) == C(42)(I8))
    assert(mhir.eval.eval(WrappingProd(C(6)(I8), C(-7)(I8))()) == C(-42)(I8))
    assert(mhir.eval.eval(WrappingProd(C(16)(I8), C(15)(I8))()) == C(-16)(I8))
    assert(mhir.eval.eval(WrappingProd(C(-16)(I8), C(14)(I8))()) == C(32)(I8))
  }

  test("FixCst") {
    assert(mhir.eval.eval(FixCst(8)(TyFix(U8, 7))) == FixCst(8)(TyFix(U8, 7)))
    assert(mhir.eval.eval(FixCst(1)(TyFix(U8, 7))) == FixCst(1)(TyFix(U8, 7)))
    assert(mhir.eval.eval(FixCst(32)(TyFix(U8, 7))) == FixCst(32)(TyFix(U8, 7)))
  }

  test("IntFixProd:Ok:(1/16):fix8_7") {
    val oneOver16 = FixCst(8)(TyFix(U8, 7))
    assert(
      mhir.eval.eval(IntFixProd(C(15)(U8), oneOver16)())
        == C(0)(U8)
    )
    assert(
      mhir.eval.eval(IntFixProd(C(16)(U8), oneOver16)())
        == C(1)(U8)
    )
    assert(
      mhir.eval.eval(IntFixProd(C(17)(U8), oneOver16)())
        == C(1)(U8)
    )
    assert(
      mhir.eval.eval(IntFixProd(C(65)(U8), oneOver16)())
        == C(4)(U8)
    )
  }

  test("IntFixProd:Ok:(1/16):fix8_10") {
    val oneOver16 = FixCst(64)(TyFix(U8, 10))
    assert(
      mhir.eval.eval(IntFixProd(C(15)(U8), oneOver16)())
        == C(0)(U8)
    )
    assert(
      mhir.eval.eval(IntFixProd(C(16)(U8), oneOver16)())
        == C(1)(U8)
    )
    assert(
      mhir.eval.eval(IntFixProd(C(17)(U8), oneOver16)())
        == C(1)(U8)
    )
    assert(
      mhir.eval.eval(IntFixProd(C(65)(U8), oneOver16)())
        == C(4)(U8)
    )
  }

  test("IntFixProd:Ok:5/128") {
    val fiveOver128 = FixCst(5)(TyFix(U8, 7))
    assert(
      mhir.eval.eval(IntFixProd(C(128)(U8), fiveOver128)())
        == C(5)(U8)
    )
    assert(
      mhir.eval.eval(IntFixProd(C(64)(U8), fiveOver128)())
        == C(2)(U8)
    )
    assert(
      mhir.eval.eval(IntFixProd(C(50)(U8), fiveOver128)())
        == C(1)(U8)
    )
  }

  test("IntFixProd:Overflow") {
    assert(
      mhir.eval.eval(IntFixProd(C(255)(U8), FixCst(255)(TyFix(U8, 7)))())
        == C(252)()
    )
  }

  test("StmLiteral()") {
    val e = StmLiteral()(TyStm(U8, 0))
    val actual = mhir.eval.eval(e)
    assert(actual == e)
  }

  test("StmLiteral(1, 2, 3)") {
    val e = StmLiteral(C(1)(U8), C(2)(U8), C(3)(U8))().tchk()
    val actual = mhir.eval.eval(e)
    assert(actual == e)
  }

  test("Overflow:Used:Sum") {
    assertOverflow(C(255)(U8) + C(1)(U8), 256, U8, "255:u8 +` 1:u8")
    assertOverflow(
      Sum(C(32767)(I16), C(1)(I16))(),
      32768,
      I16,
      "32767:i16 +` 1:i16"
    )
    assertOverflow(C(-127)(I8) + C(-2)(I8), -129, I8, "-127:i8 +` -2:i8")
  }

  test("Overflow:Used:Prod") {
    assertOverflow(C(128)(U8) * C(2)(U8), 256, U8, "128:u8 *` 2:u8")
    assertOverflow(C(-64)(I8) * C(3)(I8), -64 * 3, I8, "-64:i8 *` 3:i8")
  }

  test("Overflow:Unused") {
    val f = I9 ::+ (t => Mux(t === 0, C(0)(U8), 2 * ToUnsigned(t - 1)())())
    assert(mhir.eval.eval(f(C(0)(I9))) == C(0)())
    assert(mhir.eval.eval(f(C(1)(I9))) == C(0)())
    assert(mhir.eval.eval(f(C(2)(I9))) == C(2)())
    assert(mhir.eval.eval(f(C(3)(I9))) == C(4)())
  }

  test("DivByZero:Used") {
    def assertDivByZero(e: Expr): Unit = {
      val exc = intercept[UndefinedValException](mhir.eval.eval(e))
      assert(exc.warnings == Set(DivByZeroWarning))
    }

    assertDivByZero(C(42)(U8) / C(0)(U8))
    assertDivByZero(C(42)(U8) % C(0)(U8))
  }

  test("DivByZero:Unused") {
    val f = U8 ::+ (i => Mux(i === 0, C(0)(U8), 10 / i)())
    assert(mhir.eval.eval(f(C(0)(U8))) == C(0)())
    assert(mhir.eval.eval(f(C(1)(U8))) == C(10)())
    assert(mhir.eval.eval(f(C(2)(U8))) == C(5)())
  }

  test("OutOfBoundsVecAccess:Used") {
    def assertOOB(e: Expr, n: Int, i: Int): Unit = {
      val exc = intercept[UndefinedValException](mhir.eval.eval(e))
      assert(exc.warnings == Set(VecIndexOutOfBoundsWarning(n, i)))
    }

    val v = VecBuild(4, U8 ::+ (i => i))()
    assertOOB(VecAccess(v, C(4)(U8))(), 4, 4)
    assertOOB(VecAccess(v, C(5)(U8))(), 4, 5)
    assertOOB(VecAccess(v, C(6)(U8))(), 4, 6)
  }

  test("OutOfBoundsVecAccess:Unused") {
    val v = VecBuild(3, U8 ::+ (i => 10 + i))()
    val f = U8 ::+ (i => Mux(i < 3, VecAccess(v, i)(), C(0)(U8))())
    assert(mhir.eval.eval(f(C(0)(U8))) == C(10)())
    assert(mhir.eval.eval(f(C(1)(U8))) == C(11)())
    assert(mhir.eval.eval(f(C(2)(U8))) == C(12)())
    assert(mhir.eval.eval(f(C(3)(U8))) == C(0)())
    assert(mhir.eval.eval(f(C(4)(U8))) == C(0)())
    assert(mhir.eval.eval(f(C(5)(U8))) == C(0)())
  }

  test("PadTo") {
    for (x <- -3 to 3) {
      for (w <- 3 to 5) {
        assert(mhir.eval.eval(PadTo(x, w)()) == IntCst(x)())
      }
    }
  }

  test("TruncateTo:Valid") {
    assert(mhir.eval.eval(TruncateTo(C(0)(U32), 16)()) == C(0)())
    assert(mhir.eval.eval(TruncateTo(C(0)(U32), 8)()) == C(0)())
    assert(mhir.eval.eval(TruncateTo(C(0)(U32), 0)()) == C(0)())

    assert(mhir.eval.eval(TruncateTo(C(19)(U16), 16)()) == C(19)())
    assert(mhir.eval.eval(TruncateTo(C(19)(U32), 8)()) == C(19)())

    assert(mhir.eval.eval(TruncateTo(C(-7)(I32), 16)()) == C(-7)())
    assert(mhir.eval.eval(TruncateTo(C(-7)(I16), 8)()) == C(-7)())
    assert(mhir.eval.eval(TruncateTo(C(-7)(I8), 4)()) == C(-7)())

    assert(mhir.eval.eval(TruncateTo(C(7)(I32), 16)()) == C(7)())
    assert(mhir.eval.eval(TruncateTo(C(7)(I16), 8)()) == C(7)())
    assert(mhir.eval.eval(TruncateTo(C(7)(I16), 4)()) == C(7)())
  }

  test("TruncateTo:ValueOutOfRange") {
    assertOverflow(
      TruncateTo(C(-129)(I16), 8)(),
      -129,
      I8,
      "truncate8(-129:i16)"
    )
    assertOverflow(
      TruncateTo(C(-130)(I16), 8)(),
      -130,
      I8,
      "truncate8(-130:i16)"
    )
    assertOverflow(TruncateTo(C(128)(I16), 8)(), 128, I8, "truncate8(128:i16)")
    assertOverflow(TruncateTo(C(129)(I16), 8)(), 129, I8, "truncate8(129:i16)")
    assertOverflow(TruncateTo(C(256)(U32), 8)(), 256, U8, "truncate8(256:u32)")
    assertOverflow(TruncateTo(C(257)(U32), 8)(), 257, U8, "truncate8(257:u32)")
    assertOverflow(
      TruncateTo(C(2049)(U16), 9)(),
      2049,
      TyUInt(9),
      "truncate9(2049:u16)"
    )
  }

  test("ToSigned") {
    for (x <- 0 to 10) {
      assert(mhir.eval.eval(ToSigned(x)()) == IntCst(x)())
    }
  }

  test("ToUnsigned:Valid") {
    val f = I8 ::+ (x => ToUnsigned(x)())

    // No-op if argument is positive
    for (x <- 0 to 10) {
      assert(mhir.eval.eval(FunCall(f, IntCst(x)(I8))()) == IntCst(x)())
    }
  }

  test("ToUnsigned:NegativeInput") {
    assertOverflow(ToUnsigned(C(-5)(I8))(), -5, TyUInt(7), "unsign(-5:i8)")
    assertOverflow(ToUnsigned(C(-1)(I32))(), -1, TyUInt(31), "unsign(-1:i32)")
  }

  test("bits(bool)") {
    assert(mhir.eval.eval(Bits(False)()) == VecLiteral(False)())
    assert(mhir.eval.eval(Bits(True)()) == VecLiteral(True)())
  }

  test("bits(u3)") {
    val f = False
    val t = True
    val u3 = TyUInt(3)
    assert(mhir.eval.eval(Bits(C(0)(u3))()) == VecLiteral(f, f, f)())
    assert(mhir.eval.eval(Bits(C(1)(u3))()) == VecLiteral(f, f, t)())
    assert(mhir.eval.eval(Bits(C(2)(u3))()) == VecLiteral(f, t, f)())
    assert(mhir.eval.eval(Bits(C(3)(u3))()) == VecLiteral(f, t, t)())
    assert(mhir.eval.eval(Bits(C(4)(u3))()) == VecLiteral(t, f, f)())
    assert(mhir.eval.eval(Bits(C(5)(u3))()) == VecLiteral(t, f, t)())
    assert(mhir.eval.eval(Bits(C(6)(u3))()) == VecLiteral(t, t, f)())
    assert(mhir.eval.eval(Bits(C(7)(u3))()) == VecLiteral(t, t, t)())
  }

  test("bits(i3)") {
    val f = False
    val t = True
    val i3 = TySInt(3)
    assert(mhir.eval.eval(Bits(C(-4)(i3))()) == VecLiteral(t, f, f)())
    assert(mhir.eval.eval(Bits(C(-3)(i3))()) == VecLiteral(t, f, t)())
    assert(mhir.eval.eval(Bits(C(-2)(i3))()) == VecLiteral(t, t, f)())
    assert(mhir.eval.eval(Bits(C(-1)(i3))()) == VecLiteral(t, t, t)())
    assert(mhir.eval.eval(Bits(C(0)(i3))()) == VecLiteral(f, f, f)())
    assert(mhir.eval.eval(Bits(C(1)(i3))()) == VecLiteral(f, f, t)())
    assert(mhir.eval.eval(Bits(C(2)(i3))()) == VecLiteral(f, t, f)())
    assert(mhir.eval.eval(Bits(C(3)(i3))()) == VecLiteral(f, t, t)())
  }

  test("bits((Vec[u8, 4], Vec[u8, 4]))") {
    val e = Bits(
      Tuple(
        VecLiteral(C(0)(U8), C(42)(U8), C(128)(U8), C(255)(U8))(),
        VecLiteral(C(-128)(I8), C(-1)(I8), C(42)(I8), C(127)(I8))()
      )()
    )().tchk()
    val f = False
    val t = True
    val expected = VecLiteral(
      Seq(
        Seq(f, f, f, f, f, f, f, f), //    0:u8 = 0b00000000
        Seq(f, f, t, f, t, f, t, f), //   42:u8 = 0b00101010
        Seq(t, f, f, f, f, f, f, f), //  128:u8 = 0b10000000
        Seq(t, t, t, t, t, t, t, t), //  255:u8 = 0b11111111
        Seq(t, f, f, f, f, f, f, f), // -128:i8 = 0b10000000
        Seq(t, t, t, t, t, t, t, t), //   -1:i8 = 0b11111111
        Seq(f, f, t, f, t, f, t, f), //   42:i8 = 0b00101010
        Seq(f, t, t, t, t, t, t, t) ///  127:i8 = 0b01111111
      ).flatten: _*
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("interpret_as:[()]") {
    assert(
      mhir.eval.eval(InterpretAs(VecLiteral()(TyVec(TyBool, 0)), TyTuple())())
        == Tuple()()
    )
  }

  test("interpret_as:[bool]") {
    assert(mhir.eval.eval(InterpretAs(VecLiteral(False)(), TyBool)()) == False)
    assert(mhir.eval.eval(InterpretAs(VecLiteral(False)(), TyBool)()) == False)
  }

  test("interpret_as:[u3]") {
    val f = False
    val t = True
    val eval = (e: Expr) => mhir.eval.eval(e)
    val u3 = TyUInt(3)
    assert(eval(InterpretAs(VecLiteral(f, f, f)(), u3)()) == C(0)())
    assert(eval(InterpretAs(VecLiteral(f, f, t)(), u3)()) == C(1)())
    assert(eval(InterpretAs(VecLiteral(f, t, f)(), u3)()) == C(2)())
    assert(eval(InterpretAs(VecLiteral(f, t, t)(), u3)()) == C(3)())
    assert(eval(InterpretAs(VecLiteral(t, f, f)(), u3)()) == C(4)())
    assert(eval(InterpretAs(VecLiteral(t, f, t)(), u3)()) == C(5)())
    assert(eval(InterpretAs(VecLiteral(t, t, f)(), u3)()) == C(6)())
    assert(eval(InterpretAs(VecLiteral(t, t, t)(), u3)()) == C(7)())
  }

  test("interpret_as:[i3]") {
    val f = False
    val t = True
    val eval = (e: Expr) => mhir.eval.eval(e)
    val i3 = TySInt(3)
    assert(eval(InterpretAs(VecLiteral(f, f, f)(), i3)()) == C(0)())
    assert(eval(InterpretAs(VecLiteral(f, f, t)(), i3)()) == C(1)())
    assert(eval(InterpretAs(VecLiteral(f, t, f)(), i3)()) == C(2)())
    assert(eval(InterpretAs(VecLiteral(f, t, t)(), i3)()) == C(3)())
    assert(eval(InterpretAs(VecLiteral(t, f, f)(), i3)()) == C(-4)())
    assert(eval(InterpretAs(VecLiteral(t, f, t)(), i3)()) == C(-3)())
    assert(eval(InterpretAs(VecLiteral(t, t, f)(), i3)()) == C(-2)())
    assert(eval(InterpretAs(VecLiteral(t, t, t)(), i3)()) == C(-1)())
  }

  test("interpret_as:[(i8, bool, (bool, u4))]") {
    val bits = VecLiteral("00101010101101".map({
      case '0' => False
      case '1' => True
    }): _*)()
    val u4 = TyUInt(4)
    val expected = Tuple(C(42)(I8), True, Tuple(False, C(13)(u4))())().tchk()
    val targetTyp = TyTuple(I8, TyBool, TyTuple(TyBool, u4))
    val actual = mhir.eval.eval(InterpretAs(bits, targetTyp)())
    assert(actual == expected)
  }

  test("interpret_as:[Vec[i4, 4]]") {
    val bits = VecLiteral("1110011100111001".map({
      case '0' => False
      case '1' => True
    }): _*)()
    val i4 = TySInt(4)
    val expected = VecLiteral(C(-2)(i4), C(7)(i4), C(3)(i4), C(-7)(i4))().tchk()
    val actual = mhir.eval.eval(InterpretAs(bits, TyVec(i4, 4))())
    assert(actual == expected)
  }

  test("u0 << u8") {
    assert(mhir.eval.eval(C(0)(U0) << C(0)(U8)) == C(0)(U0))
    assert(mhir.eval.eval(C(0)(U0) << C(5)(U8)) == C(0)(U0))
  }

  test("u8 << u8") {
    // 42 = (00101010)_2
    assert(mhir.eval.eval(C(42)(U8) << C(0)(U8)) == C(42)())
    assert(mhir.eval.eval(C(42)(U8) << C(1)(U8)) == C(84)())
    assert(mhir.eval.eval(C(42)(U8) << C(2)(U8)) == C(168)())
    assert(mhir.eval.eval(C(42)(U8) << C(3)(U8)) == C(80)())
    assert(mhir.eval.eval(C(42)(U8) << C(4)(U8)) == C(160)())
    assert(mhir.eval.eval(C(42)(U8) << C(5)(U8)) == C(64)())
    assert(mhir.eval.eval(C(42)(U8) << C(6)(U8)) == C(128)())
    assert(mhir.eval.eval(C(42)(U8) << C(7)(U8)) == C(0)())
    assert(mhir.eval.eval(C(42)(U8) << C(8)(U8)) == C(0)())
  }

  test("u16 << u8") {
    // 42 = (00000000 00101010)_2
    assert(mhir.eval.eval(C(42)(U16) << C(3)(U8)) == C(336)())
    assert(mhir.eval.eval(C(42)(U16) << C(10)(U8)) == C(43008)())
    assert(mhir.eval.eval(C(42)(U16) << C(11)(U8)) == C(20480)())
  }

  test("i8 << u8") {
    // 99 = (01100011)_2
    assert(mhir.eval.eval(C(99)(I8) << C(0)(U8)) == C(99)())
    assert(mhir.eval.eval(C(99)(I8) << C(1)(U8)) == C(-58)())
    assert(mhir.eval.eval(C(99)(I8) << C(2)(U8)) == C(-116)())
    assert(mhir.eval.eval(C(99)(I8) << C(3)(U8)) == C(24)())
    assert(mhir.eval.eval(C(99)(I8) << C(4)(U8)) == C(48)())
    assert(mhir.eval.eval(C(99)(I8) << C(5)(U8)) == C(96)())
    assert(mhir.eval.eval(C(99)(I8) << C(6)(U8)) == C(-64)())
    assert(mhir.eval.eval(C(99)(I8) << C(7)(U8)) == C(-128)())
    assert(mhir.eval.eval(C(99)(I8) << C(8)(U8)) == C(0)())
  }

  test("i16 << u8") {
    // 99 = (00000000 01100011)_2
    assert(mhir.eval.eval(C(99)(I16) << C(1)(U8)) == C(198)())
    assert(mhir.eval.eval(C(99)(I16) << C(9)(U8)) == C(-14848)())
    assert(mhir.eval.eval(C(99)(I16) << C(10)(U8)) == C(-29696)())
    assert(mhir.eval.eval(C(99)(I16) << C(11)(U8)) == C(6144)())
  }

  test("u0 >> u8") {
    assert(mhir.eval.eval(C(0)(U0) >> C(0)(U8)) == C(0)(U0))
    assert(mhir.eval.eval(C(0)(U0) >> C(5)(U8)) == C(0)(U0))
  }

  test("u8 >> u8") {
    // 168 = (10101000)_2
    assert(mhir.eval.eval(C(168)(U8) >> C(0)(U8)) == C(168)())
    assert(mhir.eval.eval(C(168)(U8) >> C(1)(U8)) == C(84)())
    assert(mhir.eval.eval(C(168)(U8) >> C(2)(U8)) == C(42)())
    assert(mhir.eval.eval(C(168)(U8) >> C(3)(U8)) == C(21)())
    assert(mhir.eval.eval(C(168)(U8) >> C(4)(U8)) == C(10)())
    assert(mhir.eval.eval(C(168)(U8) >> C(5)(U8)) == C(5)())
    assert(mhir.eval.eval(C(168)(U8) >> C(6)(U8)) == C(2)())
    assert(mhir.eval.eval(C(168)(U8) >> C(7)(U8)) == C(1)())
    assert(mhir.eval.eval(C(168)(U8) >> C(8)(U8)) == C(0)())
    assert(mhir.eval.eval(C(168)(U8) >> C(9)(U8)) == C(0)())
  }

  test("u9 >> u8") {
    // 341 = (101010101)_2
    val u9 = TyUInt(9)
    assert(mhir.eval.eval(C(341)(u9) >> C(0)(U8)) == C(341)())
    assert(mhir.eval.eval(C(341)(u9) >> C(1)(U8)) == C(170)())
    assert(mhir.eval.eval(C(341)(u9) >> C(2)(U8)) == C(85)())
    assert(mhir.eval.eval(C(341)(u9) >> C(3)(U8)) == C(42)())
    assert(mhir.eval.eval(C(341)(u9) >> C(4)(U8)) == C(21)())
    assert(mhir.eval.eval(C(341)(u9) >> C(5)(U8)) == C(10)())
    assert(mhir.eval.eval(C(341)(u9) >> C(6)(U8)) == C(5)())
    assert(mhir.eval.eval(C(341)(u9) >> C(7)(U8)) == C(2)())
    assert(mhir.eval.eval(C(341)(u9) >> C(8)(U8)) == C(1)())
    assert(mhir.eval.eval(C(341)(u9) >> C(9)(U8)) == C(0)())
  }

  test("i8 >> u8") {
    // -29 = (11100011)_2
    assert(mhir.eval.eval(C(-29)(I8) >> C(0)(U8)) == C(-29)())
    assert(mhir.eval.eval(C(-29)(I8) >> C(1)(U8)) == C(-15)())
    assert(mhir.eval.eval(C(-29)(I8) >> C(2)(U8)) == C(-8)())
    assert(mhir.eval.eval(C(-29)(I8) >> C(3)(U8)) == C(-4)())
    assert(mhir.eval.eval(C(-29)(I8) >> C(4)(U8)) == C(-2)())
    assert(mhir.eval.eval(C(-29)(I8) >> C(5)(U8)) == C(-1)())
    assert(mhir.eval.eval(C(-29)(I8) >> C(6)(U8)) == C(-1)())
    assert(mhir.eval.eval(C(-29)(I8) >> C(7)(U8)) == C(-1)())
    assert(mhir.eval.eval(C(-29)(I8) >> C(8)(U8)) == C(-1)())
    assert(mhir.eval.eval(C(-29)(I8) >> C(9)(U8)) == C(-1)())
  }

  test("i9 >> u8") {
    // -171 = (101010101)_2
    val i9 = TySInt(9)
    assert(mhir.eval.eval(C(-171)(i9) >> C(0)(U8)) == C(-171)())
    assert(mhir.eval.eval(C(-171)(i9) >> C(1)(U8)) == C(-86)())
    assert(mhir.eval.eval(C(-171)(i9) >> C(2)(U8)) == C(-43)())
    assert(mhir.eval.eval(C(-171)(i9) >> C(3)(U8)) == C(-22)())
    assert(mhir.eval.eval(C(-171)(i9) >> C(4)(U8)) == C(-11)())
    assert(mhir.eval.eval(C(-171)(i9) >> C(5)(U8)) == C(-6)())
    assert(mhir.eval.eval(C(-171)(i9) >> C(6)(U8)) == C(-3)())
    assert(mhir.eval.eval(C(-171)(i9) >> C(7)(U8)) == C(-2)())
    assert(mhir.eval.eval(C(-171)(i9) >> C(8)(U8)) == C(-1)())
    assert(mhir.eval.eval(C(-171)(i9) >> C(9)(U8)) == C(-1)())
  }

  test("u0 >>> u8") {
    assert(mhir.eval.eval(C(0)(U0) >>> C(0)(U8)) == C(0)(U0))
    assert(mhir.eval.eval(C(0)(U0) >>> C(5)(U8)) == C(0)(U0))
  }

  test("u8 >>> u8") {
    // 168 = (10101000)_2
    assert(mhir.eval.eval(C(168)(U8) >>> C(0)(U8)) == C(168)())
    assert(mhir.eval.eval(C(168)(U8) >>> C(1)(U8)) == C(84)())
    assert(mhir.eval.eval(C(168)(U8) >>> C(2)(U8)) == C(42)())
    assert(mhir.eval.eval(C(168)(U8) >>> C(3)(U8)) == C(21)())
    assert(mhir.eval.eval(C(168)(U8) >>> C(4)(U8)) == C(10)())
    assert(mhir.eval.eval(C(168)(U8) >>> C(5)(U8)) == C(5)())
    assert(mhir.eval.eval(C(168)(U8) >>> C(6)(U8)) == C(2)())
    assert(mhir.eval.eval(C(168)(U8) >>> C(7)(U8)) == C(1)())
    assert(mhir.eval.eval(C(168)(U8) >>> C(8)(U8)) == C(0)())
    assert(mhir.eval.eval(C(168)(U8) >>> C(9)(U8)) == C(0)())
  }

  test("u9 >>> u8") {
    // 341 = (101010101)_2
    val u9 = TyUInt(9)
    assert(mhir.eval.eval(C(341)(u9) >>> C(0)(U8)) == C(341)())
    assert(mhir.eval.eval(C(341)(u9) >>> C(1)(U8)) == C(170)())
    assert(mhir.eval.eval(C(341)(u9) >>> C(2)(U8)) == C(85)())
    assert(mhir.eval.eval(C(341)(u9) >>> C(3)(U8)) == C(42)())
    assert(mhir.eval.eval(C(341)(u9) >>> C(4)(U8)) == C(21)())
    assert(mhir.eval.eval(C(341)(u9) >>> C(5)(U8)) == C(10)())
    assert(mhir.eval.eval(C(341)(u9) >>> C(6)(U8)) == C(5)())
    assert(mhir.eval.eval(C(341)(u9) >>> C(7)(U8)) == C(2)())
    assert(mhir.eval.eval(C(341)(u9) >>> C(8)(U8)) == C(1)())
    assert(mhir.eval.eval(C(341)(u9) >>> C(9)(U8)) == C(0)())
  }

  test("i8 >>> u8") {
    // -29 = (11100011)_2
    assert(mhir.eval.eval(C(-29)(I8) >>> C(0)(U8)) == C(-29)())
    assert(mhir.eval.eval(C(-29)(I8) >>> C(1)(U8)) == C(113)())
    assert(mhir.eval.eval(C(-29)(I8) >>> C(2)(U8)) == C(56)())
    assert(mhir.eval.eval(C(-29)(I8) >>> C(3)(U8)) == C(28)())
    assert(mhir.eval.eval(C(-29)(I8) >>> C(4)(U8)) == C(14)())
    assert(mhir.eval.eval(C(-29)(I8) >>> C(5)(U8)) == C(7)())
    assert(mhir.eval.eval(C(-29)(I8) >>> C(6)(U8)) == C(3)())
    assert(mhir.eval.eval(C(-29)(I8) >>> C(7)(U8)) == C(1)())
    assert(mhir.eval.eval(C(-29)(I8) >>> C(8)(U8)) == C(0)())
    assert(mhir.eval.eval(C(-29)(I8) >>> C(9)(U8)) == C(0)())
  }

  test("i9 >>> u8") {
    // -171 = (101010101)_2
    val i9 = TySInt(9)
    assert(mhir.eval.eval(C(-171)(i9) >>> C(0)(U8)) == C(-171)())
    assert(mhir.eval.eval(C(-171)(i9) >>> C(1)(U8)) == C(170)())
    assert(mhir.eval.eval(C(-171)(i9) >>> C(2)(U8)) == C(85)())
    assert(mhir.eval.eval(C(-171)(i9) >>> C(3)(U8)) == C(42)())
    assert(mhir.eval.eval(C(-171)(i9) >>> C(4)(U8)) == C(21)())
    assert(mhir.eval.eval(C(-171)(i9) >>> C(5)(U8)) == C(10)())
    assert(mhir.eval.eval(C(-171)(i9) >>> C(6)(U8)) == C(5)())
    assert(mhir.eval.eval(C(-171)(i9) >>> C(7)(U8)) == C(2)())
    assert(mhir.eval.eval(C(-171)(i9) >>> C(8)(U8)) == C(1)())
    assert(mhir.eval.eval(C(-171)(i9) >>> C(9)(U8)) == C(0)())
  }

  test("NestedLet") {
    val n = Param("n")(U8)
    val m = Param("m")(U8)
    val f = U32 ::+ (k =>
      VecBuild(
        n,
        U32 ::+ (i => VecBuild(m, U32 ::+ (j => Tuple(i, j, k)()))())
      )()
    )
    val lets = Let(n, C(3)(U8), Let(m, C(2)(U8), FunCall(f, C(42)(U32))())())()
    val evaluated = mhir.eval.eval(lets)
    val expected = VecLiteral(
      VecLiteral(Tuple(0, 0, 42)(), Tuple(0, 1, 42)())(),
      VecLiteral(Tuple(1, 0, 42)(), Tuple(1, 1, 42)())(),
      VecLiteral(Tuple(2, 0, 42)(), Tuple(2, 1, 42)())()
    )()
    assert(evaluated == expected)
    assert(evaluated.typ == TyVec(TyVec((U32, U32, U32), C(2)(U8)), C(3)(U8)))
  }

  test("(x => x - x)(undefined)") {
    val f = U8 ::+ (x => x - x)
    val e = f(Undefined(U8))
    assert(mhir.eval.eval(e, suppressWarnings = true) == C(0)(U8))
  }

  test("(x => x == x)(undefined)") {
    val f = U8 ::+ (x => x === x)
    val e = f(Undefined(U8))
    assert(mhir.eval.eval(e, suppressWarnings = true) == True)
  }

  test("(v => v[0] == v[0])(undefined)") {
    val f = TyVec(I16, 2) ::+ (v => VecAccess(v, 0)() === VecAccess(v, 0)())
    val e = f(Undefined(TyVec(I16, 2)))
    assert(mhir.eval.eval(e, suppressWarnings = true) == True)
  }

  test("warning:undefined") {
    val typ = TyVec((U8, I16), 42)
    val e = Undefined(typ)
    val exc = intercept[UndefinedValException](mhir.eval.eval(e))
    assert(exc.warnings == Set(UndefinedPrimitive(typ)))
  }

  test("warning:undefined + 1") {
    val e = Undefined(U8) + 1
    val exc = intercept[UndefinedValException](mhir.eval.eval(e))
    assert(exc.warnings == Set(UndefinedPrimitive(U8)))
  }

  test("warning:undefined[0]") {
    val e = Undefined(TyVec(U8, 5))
    val exc = intercept[UndefinedValException](mhir.eval.eval(e))
    assert(exc.warnings == Set(UndefinedPrimitive(TyVec(U8, 5))))
  }

}
