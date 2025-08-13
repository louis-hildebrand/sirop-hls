package mhir.ir
package evaluate

import mhir.ir.typecheck.TypeCheck
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
  private def assertOverflow(e: Expr, n: Int, typ: TyAnyInt): Unit = {
    val exc = intercept[UndefinedValException](mhir.ir.eval(e))
    assert(exc.warnings == Set(OverflowWarning(n, typ)))
  }

  test("IntCst") {
    assert(mhir.ir.eval(IntCst(3)()) == IntCst(3)())
  }

  test("StmLiteral()") {
    val e = StmLiteral()(TyStm(U8, 0))
    val actual = mhir.ir.eval(e)
    assert(actual == e)
  }

  test("StmLiteral(1, 2, 3)") {
    val e = StmLiteral(C(1)(U8), C(2)(U8), C(3)(U8))().tchk()
    val actual = mhir.ir.eval(e)
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
    val actual = mhir.ir.eval(s)
    assert(actual == expected)
  }

  test("ObviousInfiniteLoop") {
    val s = StmBuild(1, 0, False, Map[Param, (Expr, Expr)]())()
    val exc = intercept[DeadlockError](mhir.ir.eval(s))
    assert(exc.reasons == Seq(TooManySteps))
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
    val exc = intercept[DeadlockError](mhir.ir.eval(s))
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
    val exc = intercept[DeadlockError](mhir.ir.eval(stm))
    assert(exc.reasons == Seq(TooManySteps))
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
    val exc = intercept[DeadlockError](mhir.ir.eval(s))
    assert(exc.reasons == Seq(EmptyStreamRead))
  }

  test("Overflow:Used") {
    assertOverflow(C(255)(U8) + C(1)(U8), 256, U8)
    assertOverflow(Sum(C(32767)(I16), C(1)(I16))(), 32768, I16)
    assertOverflow(C(-127)(I8) + C(-2)(I8), -129, I8)
    assertOverflow(C(128)(U8) * C(2)(U8), 256, U8)
    assertOverflow(C(-64)(I8) * C(3)(I8), -64 * 3, I8)
  }

  test("Overflow:Unused") {
    val f = I9 ::+ (t => Mux(t === 0, C(0)(U8), 2 * ToUnsigned(t - 1)())())
    assert(mhir.ir.eval(f(C(0)(I9))) == C(0)())
    assert(mhir.ir.eval(f(C(1)(I9))) == C(0)())
    assert(mhir.ir.eval(f(C(2)(I9))) == C(2)())
    assert(mhir.ir.eval(f(C(3)(I9))) == C(4)())
  }

  test("DivByZero:Used") {
    def assertDivByZero(e: Expr): Unit = {
      val exc = intercept[UndefinedValException](mhir.ir.eval(e))
      assert(exc.warnings == Set(DivByZeroWarning))
    }

    assertDivByZero(C(42)(U8) / C(0)(U8))
    assertDivByZero(C(42)(U8) % C(0)(U8))
  }

  test("DivByZero:Unused") {
    val f = U8 ::+ (i => Mux(i === 0, C(0)(U8), 10 / i)())
    assert(mhir.ir.eval(f(C(0)(U8))) == C(0)())
    assert(mhir.ir.eval(f(C(1)(U8))) == C(10)())
    assert(mhir.ir.eval(f(C(2)(U8))) == C(5)())
  }

  test("OutOfBoundsVecAccess:Used") {
    def assertOOB(e: Expr, n: Int, i: Int): Unit = {
      val exc = intercept[UndefinedValException](mhir.ir.eval(e))
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
    assert(mhir.ir.eval(f(C(0)(U8))) == C(10)())
    assert(mhir.ir.eval(f(C(1)(U8))) == C(11)())
    assert(mhir.ir.eval(f(C(2)(U8))) == C(12)())
    assert(mhir.ir.eval(f(C(3)(U8))) == C(0)())
    assert(mhir.ir.eval(f(C(4)(U8))) == C(0)())
    assert(mhir.ir.eval(f(C(5)(U8))) == C(0)())
  }

  test("PadTo") {
    for (x <- -3 to 3) {
      for (w <- 3 to 5) {
        assert(mhir.ir.eval(PadTo(x, w)()) == IntCst(x)())
      }
    }
  }

  test("TruncateTo:Valid") {
    assert(mhir.ir.eval(TruncateTo(C(0)(U32), 16)()) == C(0)())
    assert(mhir.ir.eval(TruncateTo(C(0)(U32), 8)()) == C(0)())
    assert(mhir.ir.eval(TruncateTo(C(0)(U32), 0)()) == C(0)())

    assert(mhir.ir.eval(TruncateTo(C(19)(U16), 16)()) == C(19)())
    assert(mhir.ir.eval(TruncateTo(C(19)(U32), 8)()) == C(19)())

    assert(mhir.ir.eval(TruncateTo(C(-7)(I32), 16)()) == C(-7)())
    assert(mhir.ir.eval(TruncateTo(C(-7)(I16), 8)()) == C(-7)())
    assert(mhir.ir.eval(TruncateTo(C(-7)(I8), 4)()) == C(-7)())

    assert(mhir.ir.eval(TruncateTo(C(7)(I32), 16)()) == C(7)())
    assert(mhir.ir.eval(TruncateTo(C(7)(I16), 8)()) == C(7)())
    assert(mhir.ir.eval(TruncateTo(C(7)(I16), 4)()) == C(7)())
  }

  test("TruncateTo:ValueOutOfRange") {
    assertOverflow(TruncateTo(C(-129)(I16), 8)(), -129, I8)
    assertOverflow(TruncateTo(C(-130)(I16), 8)(), -130, I8)
    assertOverflow(TruncateTo(C(128)(I16), 8)(), 128, I8)
    assertOverflow(TruncateTo(C(129)(I16), 8)(), 129, I8)
    assertOverflow(TruncateTo(C(256)(U32), 8)(), 256, U8)
    assertOverflow(TruncateTo(C(257)(U32), 8)(), 257, U8)
    assertOverflow(TruncateTo(C(2049)(U16), 9)(), 2049, TyUInt(9))
  }

  test("ToSigned") {
    for (x <- 0 to 10) {
      assert(mhir.ir.eval(ToSigned(x)()) == IntCst(x)())
    }
  }

  test("ToUnsigned:Valid") {
    val f = I8 ::+ (x => ToUnsigned(x)())

    // No-op if argument is positive
    for (x <- 0 to 10) {
      assert(mhir.ir.eval(FunCall(f, IntCst(x)(I8))()) == IntCst(x)())
    }
  }

  test("ToUnsigned:NegativeInput") {
    assertOverflow(ToUnsigned(C(-5)(I8))(), -5, TyUInt(7))
    assertOverflow(ToUnsigned(C(-1)(I32))(), -1, TyUInt(31))
  }

  test("u0 << u8") {
    assert(mhir.ir.eval(C(0)(U0) << C(0)(U8)) == C(0)(U0))
    assert(mhir.ir.eval(C(0)(U0) << C(5)(U8)) == C(0)(U0))
  }

  test("u8 << u8") {
    // 42 = (00101010)_2
    assert(mhir.ir.eval(C(42)(U8) << C(0)(U8)) == C(42)())
    assert(mhir.ir.eval(C(42)(U8) << C(1)(U8)) == C(84)())
    assert(mhir.ir.eval(C(42)(U8) << C(2)(U8)) == C(168)())
    assert(mhir.ir.eval(C(42)(U8) << C(3)(U8)) == C(80)())
    assert(mhir.ir.eval(C(42)(U8) << C(4)(U8)) == C(160)())
    assert(mhir.ir.eval(C(42)(U8) << C(5)(U8)) == C(64)())
    assert(mhir.ir.eval(C(42)(U8) << C(6)(U8)) == C(128)())
    assert(mhir.ir.eval(C(42)(U8) << C(7)(U8)) == C(0)())
    assert(mhir.ir.eval(C(42)(U8) << C(8)(U8)) == C(0)())
  }

  test("u16 << u8") {
    // 42 = (00000000 00101010)_2
    assert(mhir.ir.eval(C(42)(U16) << C(3)(U8)) == C(336)())
    assert(mhir.ir.eval(C(42)(U16) << C(10)(U8)) == C(43008)())
    assert(mhir.ir.eval(C(42)(U16) << C(11)(U8)) == C(20480)())
  }

  test("i8 << u8") {
    // 99 = (01100011)_2
    assert(mhir.ir.eval(C(99)(I8) << C(0)(U8)) == C(99)())
    assert(mhir.ir.eval(C(99)(I8) << C(1)(U8)) == C(-58)())
    assert(mhir.ir.eval(C(99)(I8) << C(2)(U8)) == C(-116)())
    assert(mhir.ir.eval(C(99)(I8) << C(3)(U8)) == C(24)())
    assert(mhir.ir.eval(C(99)(I8) << C(4)(U8)) == C(48)())
    assert(mhir.ir.eval(C(99)(I8) << C(5)(U8)) == C(96)())
    assert(mhir.ir.eval(C(99)(I8) << C(6)(U8)) == C(-64)())
    assert(mhir.ir.eval(C(99)(I8) << C(7)(U8)) == C(-128)())
    assert(mhir.ir.eval(C(99)(I8) << C(8)(U8)) == C(0)())
  }

  test("i16 << u8") {
    // 99 = (00000000 01100011)_2
    assert(mhir.ir.eval(C(99)(I16) << C(1)(U8)) == C(198)())
    assert(mhir.ir.eval(C(99)(I16) << C(9)(U8)) == C(-14848)())
    assert(mhir.ir.eval(C(99)(I16) << C(10)(U8)) == C(-29696)())
    assert(mhir.ir.eval(C(99)(I16) << C(11)(U8)) == C(6144)())
  }

  test("u0 >> u8") {
    assert(mhir.ir.eval(C(0)(U0) >> C(0)(U8)) == C(0)(U0))
    assert(mhir.ir.eval(C(0)(U0) >> C(5)(U8)) == C(0)(U0))
  }

  test("u8 >> u8") {
    // 168 = (10101000)_2
    assert(mhir.ir.eval(C(168)(U8) >> C(0)(U8)) == C(168)())
    assert(mhir.ir.eval(C(168)(U8) >> C(1)(U8)) == C(84)())
    assert(mhir.ir.eval(C(168)(U8) >> C(2)(U8)) == C(42)())
    assert(mhir.ir.eval(C(168)(U8) >> C(3)(U8)) == C(21)())
    assert(mhir.ir.eval(C(168)(U8) >> C(4)(U8)) == C(10)())
    assert(mhir.ir.eval(C(168)(U8) >> C(5)(U8)) == C(5)())
    assert(mhir.ir.eval(C(168)(U8) >> C(6)(U8)) == C(2)())
    assert(mhir.ir.eval(C(168)(U8) >> C(7)(U8)) == C(1)())
    assert(mhir.ir.eval(C(168)(U8) >> C(8)(U8)) == C(0)())
    assert(mhir.ir.eval(C(168)(U8) >> C(9)(U8)) == C(0)())
  }

  test("u9 >> u8") {
    // 341 = (101010101)_2
    val u9 = TyUInt(9)
    assert(mhir.ir.eval(C(341)(u9) >> C(0)(U8)) == C(341)())
    assert(mhir.ir.eval(C(341)(u9) >> C(1)(U8)) == C(170)())
    assert(mhir.ir.eval(C(341)(u9) >> C(2)(U8)) == C(85)())
    assert(mhir.ir.eval(C(341)(u9) >> C(3)(U8)) == C(42)())
    assert(mhir.ir.eval(C(341)(u9) >> C(4)(U8)) == C(21)())
    assert(mhir.ir.eval(C(341)(u9) >> C(5)(U8)) == C(10)())
    assert(mhir.ir.eval(C(341)(u9) >> C(6)(U8)) == C(5)())
    assert(mhir.ir.eval(C(341)(u9) >> C(7)(U8)) == C(2)())
    assert(mhir.ir.eval(C(341)(u9) >> C(8)(U8)) == C(1)())
    assert(mhir.ir.eval(C(341)(u9) >> C(9)(U8)) == C(0)())
  }

  test("i8 >> u8") {
    // -29 = (11100011)_2
    assert(mhir.ir.eval(C(-29)(I8) >> C(0)(U8)) == C(-29)())
    assert(mhir.ir.eval(C(-29)(I8) >> C(1)(U8)) == C(113)())
    assert(mhir.ir.eval(C(-29)(I8) >> C(2)(U8)) == C(56)())
    assert(mhir.ir.eval(C(-29)(I8) >> C(3)(U8)) == C(28)())
    assert(mhir.ir.eval(C(-29)(I8) >> C(4)(U8)) == C(14)())
    assert(mhir.ir.eval(C(-29)(I8) >> C(5)(U8)) == C(7)())
    assert(mhir.ir.eval(C(-29)(I8) >> C(6)(U8)) == C(3)())
    assert(mhir.ir.eval(C(-29)(I8) >> C(7)(U8)) == C(1)())
    assert(mhir.ir.eval(C(-29)(I8) >> C(8)(U8)) == C(0)())
    assert(mhir.ir.eval(C(-29)(I8) >> C(9)(U8)) == C(0)())
  }

  test("i9 >> u8") {
    // -171 = (101010101)_2
    val i9 = TySInt(9)
    assert(mhir.ir.eval(C(-171)(i9) >> C(0)(U8)) == C(-171)())
    assert(mhir.ir.eval(C(-171)(i9) >> C(1)(U8)) == C(170)())
    assert(mhir.ir.eval(C(-171)(i9) >> C(2)(U8)) == C(85)())
    assert(mhir.ir.eval(C(-171)(i9) >> C(3)(U8)) == C(42)())
    assert(mhir.ir.eval(C(-171)(i9) >> C(4)(U8)) == C(21)())
    assert(mhir.ir.eval(C(-171)(i9) >> C(5)(U8)) == C(10)())
    assert(mhir.ir.eval(C(-171)(i9) >> C(6)(U8)) == C(5)())
    assert(mhir.ir.eval(C(-171)(i9) >> C(7)(U8)) == C(2)())
    assert(mhir.ir.eval(C(-171)(i9) >> C(8)(U8)) == C(1)())
    assert(mhir.ir.eval(C(-171)(i9) >> C(9)(U8)) == C(0)())
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
    val evaluated = mhir.ir.eval(lets)
    val expected = VecLiteral(
      VecLiteral(Tuple(0, 0, 42)(), Tuple(0, 1, 42)())(),
      VecLiteral(Tuple(1, 0, 42)(), Tuple(1, 1, 42)())(),
      VecLiteral(Tuple(2, 0, 42)(), Tuple(2, 1, 42)())()
    )()
    assert(evaluated == expected)
    assert(evaluated.typ == TyVec(TyVec((U32, U32, U32), C(2)(U8)), C(3)(U8)))
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
    val e = LetStm(s, count, zipped)().tchk()
    val expected = StmLiteral(
      Tuple(C(0)(U8), C(0)(U8))(),
      Tuple(C(1)(U8), C(1)(U8))(),
      Tuple(C(2)(U8), C(2)(U8))(),
      Tuple(C(3)(U8), C(3)(U8))(),
      Tuple(C(4)(U8), C(4)(U8))()
    )()
    val actual = mhir.ir.eval(e)
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
          a -> (LetStm(s, count, zipped)(), True)
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
    val actual = mhir.ir.eval(e)
    assert(actual == expected)
  }

  test("LetStm:WrongAccessOrder") {
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
    val e = LetStm(s, count, concat)().tchk()
    val exc = intercept[DeadlockError](mhir.ir.eval(e))
    assert(exc.reasons == Seq(TooManySteps))
  }
}
