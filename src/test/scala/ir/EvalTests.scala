package ir

import org.scalatest.funsuite.AnyFunSuite

class EvalTests extends AnyFunSuite {
  test("IntCst") {
    assert(ir.eval(C(3)()) == C(3)())
  }

  test("StmBuild") {
    val i = Param("i")(U16)
    val s =
      StmBuild(
        5,
        i + 42,
        True,
        Map[Param, (Expr, Expr)](i -> (C(9)(U16), 2 * i + 1))
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
    val s = Param("s")(TyStm(U0, -1))
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
      val s = Param("s")(TyStm(U0, 1))
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

  test("Overflow") {
    assertThrows[OverflowError](ir.eval(C(255)(U8) + C(1)(U8)))
    assertThrows[OverflowError](ir.eval(Sum(C(32767)(I16), C(1)(I16))()))
    assertThrows[OverflowError](ir.eval(C(-127)(I8) + C(-2)(I8)))
    assertThrows[OverflowError](ir.eval(C(128)(U8) * C(2)(U8)))
    assertThrows[OverflowError](ir.eval(C(-64)(U8) * C(3)(U8)))
  }

  test("DivByZero") {
    assertThrows[DivByZero.type](ir.eval(C(42)(U8) / C(0)(U8)))
    assertThrows[DivByZero.type](ir.eval(C(42)(U8) % C(0)(U8)))
  }

  test("PadTo") {
    for (x <- -3 to 3) {
      for (w <- 3 to 5) {
        assert(ir.eval(PadTo(x, w)()) == C(x)())
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
    assert(truncations(C(-3)(TySInt(5))) == Seq(-3, -3, -3, 1, -1, 0))
    // -2 = (1110)_2
    assert(truncations(C(-2)(TySInt(4))) == Seq(-2, -2, -2, 0, 0))
    // -1 = (111)_2
    assert(truncations(C(-1)(TySInt(3))) == Seq(-1, -1, -1, 0))
    // 0 = (000)_2
    assert(truncations(C(0)(TySInt(3))) == Seq(0, 0, 0, 0))
    assert(truncations(C(0)(TyUInt(3))) == Seq(0, 0, 0, 0))
    // 1 = (001)_2
    assert(truncations(C(1)(TySInt(3))) == Seq(1, 1, -1, 0))
    assert(truncations(C(1)(TyUInt(3))) == Seq(1, 1, 1, 0))
    // 2 = (0010)_2
    assert(truncations(C(2)(TySInt(4))) == Seq(2, 2, -2, 0, 0))
    assert(truncations(C(2)(TyUInt(4))) == Seq(2, 2, 2, 0, 0))
    // 3 = (0011)_2
    assert(truncations(C(3)(TySInt(4))) == Seq(3, 3, -1, -1, 0))
    assert(truncations(C(3)(TyUInt(4))) == Seq(3, 3, 3, 1, 0))
    // 4 = (00100)_2
    assert(truncations(C(4)(TySInt(5))) == Seq(4, 4, -4, 0, 0, 0))
    assert(truncations(C(4)(TyUInt(5))) == Seq(4, 4, 4, 0, 0, 0))
    // 5 = (00101)_2
    assert(truncations(C(5)(TySInt(5))) == Seq(5, 5, -3, 1, -1, 0))
    assert(truncations(C(5)(TyUInt(5))) == Seq(5, 5, 5, 1, 1, 0))
  }

  test("TruncateSmallNumber") {
    val f = U16 ::+ (x => TruncateTo(x, 15)())
    val e = f(C(1)(TyUInt(1)))
    assert(ir.eval(e) == C(1)())
  }

  test("ToSigned") {
    for (x <- 0 to 10) {
      assert(ir.eval(ToSigned(x)()) == C(x)())
    }
  }

  test("ToUnsigned") {
    val f = I8 ::+ (x => ToUnsigned(x)())

    // No-op if argument is positive
    for (x <- 0 to 10) {
      assert(ir.eval(FunCall(f, C(x)(I8))()) == C(x)())
    }

    // If argument is negative, then drop leading bit and reinterpret as unsigned
    // -6 = (11111010)_2
    assert(ir.eval(FunCall(f, C(-6)(I8))()) == C(122)())
    // -5 = (11111011)_2
    assert(ir.eval(FunCall(f, C(-5)(I8))()) == C(123)())
    // -4 = (11111100)_2
    assert(ir.eval(FunCall(f, C(-4)(I8))()) == C(124)())
    // -3 = (11111101)_2
    assert(ir.eval(FunCall(f, C(-3)(I8))()) == C(125)())
    // -2 = (11111110)_2
    assert(ir.eval(FunCall(f, C(-2)(I8))()) == C(126)())
    // -1 = (11111111)_2
    assert(ir.eval(FunCall(f, C(-1)(I8))()) == C(127)())
  }

  test("UnsignedToUnsigned") {
    val f = I9 ::+ (x => ToUnsigned(x)())
    val e = f(C(2)(TyUInt(2)))
    assert(ir.eval(e) == C(2)())
  }

  test("Equal:Bool") {
    assert(ir.eval(False === False) == True)
    assert(ir.eval(False === True) == False)
    assert(ir.eval(True === False) == False)
    assert(ir.eval(True === True) == True)
  }

  test("Equal:Int") {
    for (t1 <- COMMON_INT_TYPES) {
      for (t2 <- COMMON_INT_TYPES) {
        assert(ir.eval(C(0)(t1) === C(0)(t2)) == True)
        assert(ir.eval(C(63)(t1) === C(63)(t2)) == True)
        assert(ir.eval(C(42)(t1) === C(43)(t2)) == False)
      }
    }
  }

  test("Equal:Vec[(Int, Bool), n]") {
    val v1 = VecBuild(5, U8 ::+ (i => Tuple(i, i === 3)()))()
    val v2 = VecBuild(5, U8 ::+ (i => Tuple(2 * i - i, i + 1 === 4)()))()
    val v3 = VecBuild(5, U8 ::+ (i => Tuple(i + 1, i === 3)()))()
    val v4 = VecBuild(5, U8 ::+ (i => Tuple(i, True)()))()

    assert(ir.eval(v1 === v2) == True)
    assert(ir.eval(v2 === v1) == True)
    assert(ir.eval(v1 === v3) == False)
    assert(ir.eval(v1 !== v3) == True)
    assert(ir.eval(v1 === v4) == False)
    assert(ir.eval(v1 !== v4) == True)
  }

  test("LessThan:Valid") {
    for (t1 <- COMMON_INT_TYPES) {
      for (t2 <- COMMON_INT_TYPES) {
        assert(ir.eval(C(0)(t1) < C(0)(t2)) == False)
        assert(ir.eval(C(0)(t1) < C(1)(t2)) == True)
        assert(ir.eval(C(42)(t1) < C(41)(t2)) == False)
        assert(ir.eval(C(42)(t1) < C(42)(t2)) == False)
        assert(ir.eval(C(42)(t1) < C(43)(t2)) == True)
      }
    }
  }

  test("Sum") {
    assert(ir.eval(C(-1)(I8) + C(5)(TyUInt(7)) + C(-300)(I16)) == C(-296)())
    assert(ir.eval(C(3)(U8) + C(2)(U8) + C(1)(TyUInt(1))) == C(3 + 2 + 1)())
    assert(
      ir.eval(((C(1)(U8) + C(2)(U8)) + C(3)(U8)) + C(4)(U8))
        == C(1 + 2 + 3 + 4)()
    )
    assert(ir.eval(C(0)(U0) + C(0)(TySInt(0))) == C(0)())
    assert(ir.eval(C(0)(TySInt(0)) + C(0)(U0)) == C(0)())
    assert(ir.eval(Sum()()) == C(0)())
  }

  test("Prod") {
    assert(ir.eval(C(7)(U8) * C(-6)(I16)) == C(-42)())
    assert(ir.eval(Prod()()) == C(1)())
    assert(ir.eval(Prod(0, 42)()) == C(0)())
  }

  test("Div") {
    assert(ir.eval(C(42)(U16) / C(2)(U8)) == C(21)())
    assert(ir.eval(C(42)(U8) / C(-3)(I8)) == C(-14)())
  }

  test("Mod") {
    assert(ir.eval(C(44)(U8) % C(3)(TyUInt(2))) == C(2)())
  }
}
