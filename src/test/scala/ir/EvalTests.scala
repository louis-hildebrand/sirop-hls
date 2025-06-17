package ir

import org.scalatest.funsuite.AnyFunSuite

class EvalTests extends AnyFunSuite {
  test("IntCst") {
    assert(ir.eval(IntCst(3)()) == IntCst(3)())
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
    val actual = ir.eval(s)
    assert(actual == expected)
  }

  test("ObviousInfiniteLoop") {
    val s = StmBuild(1, 0, False, Map[Param, (Expr, Expr)]())()
    val exc = intercept[DeadlockError](ir.eval(s))
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
    val exc = intercept[DeadlockError](ir.eval(s))
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
    val exc = intercept[DeadlockError](ir.eval(stm))
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
    val exc = intercept[DeadlockError](ir.eval(s))
    assert(exc.reasons == Seq(EmptyStreamRead))
  }

  test("Overflow:Used") {
    def assertOverflow(e: Expr, n: Int, typ: TyAnyInt): Unit = {
      val exc = intercept[UndefinedValException](ir.eval(e))
      assert(exc.warnings == Set(OverflowWarning(n, typ)))
    }

    assertOverflow(C(255)(U8) + C(1)(U8), 256, U8)
    assertOverflow(Sum(C(32767)(I16), C(1)(I16))(), 32768, I16)
    assertOverflow(C(-127)(I8) + C(-2)(I8), -129, I8)
    assertOverflow(C(128)(U8) * C(2)(U8), 256, U8)
    assertOverflow(C(-64)(I8) * C(3)(I8), -64 * 3, I8)
  }

  test("Overflow:Unused") {
    val f = I9 ::+ (t => Mux(t === 0, C(0)(U8), 2 * ToUnsigned(t - 1)())())
    assert(ir.eval(f(C(0)(I9))) == C(0)())
    assert(ir.eval(f(C(1)(I9))) == C(0)())
    assert(ir.eval(f(C(2)(I9))) == C(2)())
    assert(ir.eval(f(C(3)(I9))) == C(4)())
  }

  test("DivByZero:Used") {
    def assertDivByZero(e: Expr): Unit = {
      val exc = intercept[UndefinedValException](ir.eval(e))
      assert(exc.warnings == Set(DivByZeroWarning))
    }

    assertDivByZero(C(42)(U8) / C(0)(U8))
    assertDivByZero(C(42)(U8) % C(0)(U8))
  }

  test("DivByZero:Unused") {
    val f = U8 ::+ (i => Mux(i === 0, C(0)(U8), 10 / i)())
    assert(ir.eval(f(C(0)(U8))) == C(0)())
    assert(ir.eval(f(C(1)(U8))) == C(10)())
    assert(ir.eval(f(C(2)(U8))) == C(5)())
  }

  test("OutOfBoundsVecAccess:Used") {
    def assertOOB(e: Expr, n: Int, i: Int): Unit = {
      val exc = intercept[UndefinedValException](ir.eval(e))
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
    assert(ir.eval(f(C(0)(U8))) == C(10)())
    assert(ir.eval(f(C(1)(U8))) == C(11)())
    assert(ir.eval(f(C(2)(U8))) == C(12)())
    assert(ir.eval(f(C(3)(U8))) == C(0)())
    assert(ir.eval(f(C(4)(U8))) == C(0)())
    assert(ir.eval(f(C(5)(U8))) == C(0)())
  }

  test("PadTo") {
    for (x <- -3 to 3) {
      for (w <- 3 to 5) {
        assert(ir.eval(PadTo(x, w)()) == IntCst(x)())
      }
    }
  }

  test("TruncateTo") {
    def truncations(i: IntCst): Seq[Long] = {
      val w0 = i.typ.asInstanceOf[TyAnyInt].w
      (w0 to 0 by -1).map(w =>
        ir.eval(TruncateTo(i, w)()).asInstanceOf[IntCst].i
      )
    }

    // -3 = (11101)_2
    assert(truncations(IntCst(-3)(TySInt(5))) == Seq(-3, -3, -3, 1, -1, 0))
    // -2 = (1110)_2
    assert(truncations(IntCst(-2)(TySInt(4))) == Seq(-2, -2, -2, 0, 0))
    // -1 = (111)_2
    assert(truncations(IntCst(-1)(TySInt(3))) == Seq(-1, -1, -1, 0))
    // 0 = (000)_2
    assert(truncations(IntCst(0)(TySInt(3))) == Seq(0, 0, 0, 0))
    assert(truncations(IntCst(0)(TyUInt(3))) == Seq(0, 0, 0, 0))
    // 1 = (001)_2
    assert(truncations(IntCst(1)(TySInt(3))) == Seq(1, 1, -1, 0))
    assert(truncations(IntCst(1)(TyUInt(3))) == Seq(1, 1, 1, 0))
    // 2 = (0010)_2
    assert(truncations(IntCst(2)(TySInt(4))) == Seq(2, 2, -2, 0, 0))
    assert(truncations(IntCst(2)(TyUInt(4))) == Seq(2, 2, 2, 0, 0))
    // 3 = (0011)_2
    assert(truncations(IntCst(3)(TySInt(4))) == Seq(3, 3, -1, -1, 0))
    assert(truncations(IntCst(3)(TyUInt(4))) == Seq(3, 3, 3, 1, 0))
    // 4 = (00100)_2
    assert(truncations(IntCst(4)(TySInt(5))) == Seq(4, 4, -4, 0, 0, 0))
    assert(truncations(IntCst(4)(TyUInt(5))) == Seq(4, 4, 4, 0, 0, 0))
    // 5 = (00101)_2
    assert(truncations(IntCst(5)(TySInt(5))) == Seq(5, 5, -3, 1, -1, 0))
    assert(truncations(IntCst(5)(TyUInt(5))) == Seq(5, 5, 5, 1, 1, 0))
  }

  test("ToSigned") {
    for (x <- 0 to 10) {
      assert(ir.eval(ToSigned(x)()) == IntCst(x)())
    }
  }

  test("ToUnsigned") {
    val f = I8 ::+ (x => ToUnsigned(x)())

    // No-op if argument is positive
    for (x <- 0 to 10) {
      assert(ir.eval(FunCall(f, IntCst(x)(I8))()) == IntCst(x)())
    }

    // If argument is negative, then drop leading bit and reinterpret as unsigned
    // -6 = (11111010)_2
    assert(ir.eval(FunCall(f, IntCst(-6)(I8))()) == IntCst(122)())
    // -5 = (11111011)_2
    assert(ir.eval(FunCall(f, IntCst(-5)(I8))()) == IntCst(123)())
    // -4 = (11111100)_2
    assert(ir.eval(FunCall(f, IntCst(-4)(I8))()) == IntCst(124)())
    // -3 = (11111101)_2
    assert(ir.eval(FunCall(f, IntCst(-3)(I8))()) == IntCst(125)())
    // -2 = (11111110)_2
    assert(ir.eval(FunCall(f, IntCst(-2)(I8))()) == IntCst(126)())
    // -1 = (11111111)_2
    assert(ir.eval(FunCall(f, IntCst(-1)(I8))()) == IntCst(127)())
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
    val evaluated = ir.eval(lets)
    val expected = VecLiteral(
      VecLiteral(Tuple(0, 0, 42)(), Tuple(0, 1, 42)())(),
      VecLiteral(Tuple(1, 0, 42)(), Tuple(1, 1, 42)())(),
      VecLiteral(Tuple(2, 0, 42)(), Tuple(2, 1, 42)())()
    )()
    assert(evaluated == expected)
    assert(evaluated.typ == TyVec(TyVec((U32, U32, U32), C(2)(U8)), C(3)(U8)))
  }
}
