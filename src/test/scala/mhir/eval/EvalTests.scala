package mhir.eval

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar._
import mhir.typecheck.TypeCheck
import org.scalatest.funsuite.AnyFunSuite

/** Tests for the evaluator.
  */
class EvalTests extends AnyFunSuite {

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

  test("StmBuild") {
    val i = Param("i")(U16)
    val s =
      StmBuild(
        5,
        i + 42,
        True,
        Map[Param, (Expr, Expr)](i -> (IntCst(9)(U16), 2 * i + 1))
      )()
    val expected = StmLiteral(9 + 42, 19 + 42, 39 + 42, 79 + 42, 159 + 42)()
    val actual = mhir.eval.eval(s)
    assert(actual == expected)
  }

  test("ObviousInfiniteLoop") {
    val s = StmBuild(1, 0, False, Map[Param, (Expr, Expr)]())()
    val exc = intercept[DeadlockError](mhir.eval.eval(s))
    assert(exc.reasons == Seq(PipelineFixpoint))
  }

  test("LessObviousInfiniteLoop") {
    val a = Param("a")(U32)
    val s = StmBuild(
      1,
      a,
      a % 2 === 1,
      Map[Param, (Expr, Expr)](
        a -> (ReshapeData(0, U32)(), a + 2)
      )
    )().tchk()
    val exc = intercept[DeadlockError](mhir.eval.eval(s))
    assert(exc.reasons == Seq(TooManySteps))
  }

  test("InfiniteLoopInInputStream") {
    val s = Param("s")(TyStm(TyUInt(0), -1))
    val stm = StmBuild(
      1,
      StmData(s)(),
      True,
      Map[Param, (Expr, Expr)](
        s -> (StmBuild(1, 0, False)(), True)
      )
    )()
    val exc = intercept[DeadlockError](mhir.eval.eval(stm))
    assert(exc.reasons == Seq(PipelineFixpoint))
  }

  test("ReadFromEmptyStream") {
    val s = {
      val s = Param("s")(TyStm(TyUInt(0), 1))
      StmBuild(
        2,
        StmData(s)(),
        True,
        Map[Param, (Expr, Expr)](
          s -> (StmBuild(1, 0, True)(), True)
        )
      )()
    }
    val exc = intercept[DeadlockError](mhir.eval.eval(s))
    assert(exc.reasons == Seq(EmptyStreamRead))
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
    assertOverflow(C(128)(U8) * C(2)(U8), 256, U8, "128:u8 * 2:u8")
    assertOverflow(C(-64)(I8) * C(3)(I8), -64 * 3, I8, "-64:i8 * 3:i8")
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

  test("LetStm:ZipWithSelf") {
    // StmCount(5)
    val count = {
      val i = Param("i")(U8)
      StmBuild(
        5,
        i,
        True,
        Map[Param, (Expr, Expr)](
          i -> (C(0)(U8), Sum(C(1)(U8), i)())
        )
      )()
    }
    val s = Param("s")(TyStm(U8, 5))
    // StmZip(s, s)
    val zipped = {
      val s0 = Param("s0")(TyStm(U8, 5))
      val s1 = Param("s1")(TyStm(U8, 5))
      StmBuild(
        5,
        Tuple(StmData(s0)(), StmData(s1)())(),
        True,
        Map[Param, (Expr, Expr)](
          s0 -> (s, True),
          s1 -> (s, True)
        )
      )()
    }
    val e = LetStm(1, s, count, zipped)().tchk()
    val expected = StmLiteral(
      Tuple(C(0)(U8), C(0)(U8))(),
      Tuple(C(1)(U8), C(1)(U8))(),
      Tuple(C(2)(U8), C(2)(U8))(),
      Tuple(C(3)(U8), C(3)(U8))(),
      Tuple(C(4)(U8), C(4)(U8))()
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("LetStm:ZipWithPlusFive") {
    // StmCount(5)
    val count = {
      val i = Param("i")(U8)
      StmBuild(
        5,
        i,
        True,
        Map[Param, (Expr, Expr)](
          i -> (C(0)(U8), Sum(C(1)(U8), i)())
        )
      )()
    }
    val s = Param("s")(TyStm(U8, 5))
    // StmMap(s, x => x + 5)
    val plusFive = {
      val a = Param("a")(TyStm(U8, 5))
      StmBuild(
        5,
        Sum(C(5)(U8), StmData(a)())(),
        True,
        Map[Param, (Expr, Expr)](
          a -> (s, True)
        )
      )()
    }
    // StmZip(s, plusFive)
    val zipped = {
      val s0 = Param("s0")(TyStm(U8, 5))
      val s1 = Param("s1")(TyStm(U8, 5))
      StmBuild(
        5,
        Tuple(StmData(s0)(), StmData(s1)())(),
        True,
        Map[Param, (Expr, Expr)](
          s0 -> (s, True),
          s1 -> (plusFive, True)
        )
      )()
    }
    // StmMap(let stm s = count in zipped, (x, y) => (x, y, 3 * x + y))
    val e = {
      val a = Param("a")(TyStm((U8, U8), 5))
      StmBuild(
        5,
        Tuple(
          StmData(a)().__0,
          StmData(a)().__1,
          Sum(Prod(C(3)(U8), StmData(a)().__0)(), StmData(a)().__1)()
        )(),
        True,
        Map[Param, (Expr, Expr)](
          a -> (LetStm(1, s, count, zipped)(), True)
        )
      )().tchk()
    }
    val expected = StmLiteral(
      Tuple(C(0)(U8), C(5)(U8), C(5)(U8))(),
      Tuple(C(1)(U8), C(6)(U8), C(9)(U8))(),
      Tuple(C(2)(U8), C(7)(U8), C(13)(U8))(),
      Tuple(C(3)(U8), C(8)(U8), C(17)(U8))(),
      Tuple(C(4)(U8), C(9)(U8), C(21)(U8))()
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("LetStm:StmConcat:NotEnoughBuffering") {
    // StmCount(5)
    val count = {
      val i = Param("i")(U8)
      StmBuild(
        5,
        i,
        True,
        Map[Param, (Expr, Expr)](
          i -> (C(0)(U8), Sum(C(1)(U8), i)())
        )
      )()
    }
    val s = Param("s")(TyStm(U8, 5))
    // StmConcat(s, s)
    val concat = {
      val t = Param("t")(U8)
      val s0 = Param("s0")(TyStm(U8, 5))
      val s1 = Param("s1")(TyStm(U8, 5))
      StmBuild(
        5,
        Mux(t lt C(5)(U8), StmData(s0)(), StmData(s1)())(),
        True,
        Map[Param, (Expr, Expr)](
          t -> (C(0)(U8), Sum(C(1)(U8), t)()),
          s0 -> (s, t lt C(5)(U8)),
          s1 -> (s, t geq C(5)(U8))
        )
      )()
    }
    val e = LetStm(1, s, count, concat)().tchk()
    val exc = intercept[DeadlockError](mhir.eval.eval(e))
    assert(exc.reasons == Seq(PipelineFixpoint))
  }

  test("LetStm:StmConcat:Valid") {
    val n = 5
    // StmCount(5)
    val count = {
      val i = Param("i")(U8)
      StmBuild(
        n,
        i,
        True,
        Map[Param, (Expr, Expr)](
          i -> (C(0)(U8), Sum(C(1)(U8), i)())
        )
      )()
    }
    val s = Param("s")(TyStm(U8, n))
    // StmConcat(s, s)
    val concat = {
      val t = Param("t")(U8)
      val s0 = Param("s0")(TyStm(U8, -1))
      val s1 = Param("s1")(TyStm(U8, -1))
      StmBuild(
        2 * n,
        Mux(t lt C(n)(U8), StmData(s0)(), StmData(s1)())(),
        True,
        Map[Param, (Expr, Expr)](
          t -> (C(0)(U8), Sum(C(1)(U8), t)()),
          s0 -> (s, t lt C(n)(U8)),
          s1 -> (s, t geq C(n)(U8))
        )
      )()
    }
    val e = LetStm(n, s, count, concat)().tchk()
    val expected = StmLiteral(
      (0 until n).map(C(_)(U8))
        ++ (0 until n).map(C(_)(U8)): _*
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("LetStm:SumAndHead") {
    val n = 8
    val m = 4
    val original = {
      // StmCount(n*m)
      val count = {
        val i = Param("i")(U8)
        StmBuild(
          n * m,
          i,
          True,
          Map[Param, (Expr, Expr)](
            i -> (C(0)(U8), Sum(C(1)(U8), i)())
          )
        )().tchk()
      }
      val x = Param("s")(TyStm(U8, n * m))
      val rowSums = {
        val t = Param("t")(U8)
        val acc = Param("acc")(U8)
        val s = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          Sum(StmData(s)(), acc)(),
          t === (m - 1),
          Map[Param, (Expr, Expr)](
            t -> (
              C(0)(U8),
              Mux(t === (m - 1), C(0)(U8), Sum(C(1)(U8), t)())()
            ),
            acc -> (
              C(0)(U8),
              Mux(
                t === (m - 1),
                C(0)(U8),
                Sum(StmData(s)(), acc)()
              )()
            ),
            s -> (x, True)
          )
        )().tchk()
      }
      val rowHeads = {
        val t = Param("t")(U8)
        val s = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          StmData(s)(),
          t === 0,
          Map[Param, (Expr, Expr)](
            t -> (
              C(0)(U8),
              Mux(t === (m - 1), C(0)(U8), Sum(C(1)(U8), t)())()
            ),
            s -> (x, True)
          )
        )().tchk()
      }
      val zip = {
        val s0 = Param("s0")(TyStm(U8, -1))
        val s1 = Param("s1")(TyStm(U8, -1))
        StmBuild(
          n,
          Tuple(StmData(s0)(), StmData(s1)())(),
          True,
          Map[Param, (Expr, Expr)](
            s0 -> (rowSums, True),
            s1 -> (rowHeads, True)
          )
        )().tchk()
      }
      LetStm(m, x, count, zip)().tchk()
    }
    val expected = StmLiteral(
      (0 until (n * m))
        .grouped(m)
        .map(xs => Tuple(C(xs.sum)(U8), C(xs.head)(U8))())
        .toSeq: _*
    )().tchk()
    val actual = mhir.eval.eval(original)
    assert(actual == expected)
  }

  test("StmBuild:StmDataWithoutReady") {
    val n = 10
    val s = Param("s")(TyStm(U8, n))
    val e = {
      val count = {
        val a = Param("a")(U8)
        StmBuild(
          n,
          a,
          True,
          Map[Param, (Expr, Expr)](a -> (C(0)(U8), Sum(C(1)(U8), a)()))
        )().tchk()
      }
      StmBuild(
        n,
        StmData(s)(),
        True,
        Map[Param, (Expr, Expr)](s -> (count, False))
      )().tchk()
    }
    val exc = intercept[UndefinedValException](mhir.eval.eval(e))
    assert(exc.warnings.contains(StmDataWithoutReady(s)))
  }

  test("NoHandshake:IllegalBackpressure") {
    val n = 5
    val a = StmRange(n, C(0)(U8), C(1)(U8))().tchk().lower
    val b = StmRange(n, C(-2)(I8), C(1)(I8))().tchk().lower
    val original =
      SimpleZip(a, SimpleMap(b, x => Sum(x, C(-5)(I8))()))
        .tchk()
        .lower

    assertThrows[IllegalBackpressure.type](
      mhir.eval.eval(original, handshake = false)
    )
  }

  test("NoHandshake:OK") {
    val n = 5
    val a = StmRange(n, C(0)(U8), C(1)(U8))().tchk().lower
    val b = StmRange(n, C(-2)(I8), C(1)(I8))().tchk().lower
    val c = Param("c")(TyStm(I32, n))
    val original = SimpleZip(
      SimpleMap(a, x => x),
      SimpleMap(b, x => Sum(x, C(-5)(I8))()),
      SimpleNop(SimpleNop(c))
    ).tchk().lower

    val actual = mhir.eval.eval(
      original,
      handshake = false,
      inputs = Map(
        c -> SimpleMap(
          SimpleMap(
            SimpleCount(C(n)(U8)),
            x => PadTo(ToSigned(Prod(x, x)())(), 32)()
          ),
          x => Sum(x, C(-10)(I32))()
        )
      )
    )
    val expected = StmLiteral(
      (0 until n).map(t =>
        Tuple(C(t)(U8), C(t - 2 - 5)(I8), C(t * t - 10)(I32))()
      ): _*
    )().tchk()
    assert(actual == expected)
  }
}
