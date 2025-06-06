package ir

import org.scalatest.funsuite.AnyFunSuite

class TypeTests extends AnyFunSuite {
  private val n = Param("n")(U8)
  private val m = Param("m")(U8)
  private val k = Param("k")(U8)

  test("AnnotatedFunction:TypedBody") {
    val f = U8 ::+ (_ => IntCst(-1)(I32))
    val expected = Function(Param("_")(U8), IntCst(-1)(I32))()
    assert(f == expected)
    assert(f.typ == TyArrow(U8, I32))
  }

  test("AnnotatedFunction:UntypedBody") {
    val v = Param("v")()
    val f = U8 ::+ (i => VecAccess(v, i)())
    val expected = {
      val i = Param("i")()
      Function(i, VecAccess(v, i)())()
    }
    assert(f == expected)
  }

  test("U8 -> I8") {
    assert(U8 ->: I8 == TyArrow(U8, I8))
  }

  test("U8 -> U16 -> U32") {
    assert(U8 ->: U16 ->: U32 == TyArrow(U8, TyArrow(U16, U32)))
  }

  test("(I8 -> I16) -> I32") {
    assert((I8 ->: I16) ->: I32 == TyArrow(TyArrow(I8, I16), I32))
  }

  test("IsCompatibleWith:Int") {
    val intTypes = Seq(U8, U16, U32, I8, I16, I32)
    for (t <- intTypes) {
      assert(t ~= t)
    }
    for (i <- intTypes.indices) {
      for (j <- intTypes.indices) {
        if (i != j) {
          assert(!(intTypes(i) ~= intTypes(j)))
        }
      }
    }
  }

  test("IsCompatibleWith:VecLength") {
    val v = Param("v")(TyVec(I16, n))
    val vLen = VecLength(v)(U8)
    assert(TyVec(I16, n) ~= TyVec(I16, vLen))
    assert(TyVec(U32, n) ~= TyVec(U32, vLen))
    assert(TyVec(U32, vLen) ~= TyVec(U32, n))

    val w = Param("w")(TyVec(I16, vLen))
    val wLen = VecLength(w)(U8)
    assert(TyVec(U8, n) ~= TyVec(U8, wLen))
    assert(TyVec(U16, wLen) ~= TyVec(U16, n))
    assert(TyVec(I32, vLen) ~= TyVec(I32, wLen))
    assert(TyVec(I8, wLen) ~= TyVec(I8, vLen))

    val intTypes = Seq(U8, U16, U32, I8, I16, I32)
    for (i <- intTypes.indices) {
      for (j <- intTypes.indices) {
        if (i != j) {
          assert(!(TyVec(intTypes(i), n) ~= TyVec(intTypes(j), n)))
        }
      }
    }
  }

  test("MinInt:SInt") {
    assert(TySInt(0).minInt == 0)
    assert(TySInt(1).minInt == -1)
    assert(TySInt(2).minInt == -2)
    assert(TySInt(3).minInt == -4)
    assert(TySInt(4).minInt == -8)
    assert(TySInt(5).minInt == -16)
  }

  test("MinInt:UInt") {
    for (w <- 0 to 10) {
      assert(TyUInt(w).minInt == 0)
    }
  }

  test("MaxInt:SInt") {
    assert(TySInt(0).maxInt == 0)
    assert(TySInt(1).maxInt == 0)
    assert(TySInt(2).maxInt == 1)
    assert(TySInt(3).maxInt == 3)
    assert(TySInt(4).maxInt == 7)
    assert(TySInt(5).maxInt == 15)
  }

  test("MaxInt:UInt") {
    assert(TyUInt(0).maxInt == 0)
    assert(TyUInt(1).maxInt == 1)
    assert(TyUInt(2).maxInt == 3)
    assert(TyUInt(3).maxInt == 7)
    assert(TyUInt(4).maxInt == 15)
    assert(TyUInt(5).maxInt == 31)
  }

  test("BitWidth:Sum:Empty") {
    val emptySum = ir.eval(Sum()()).asInstanceOf[IntCst].i
    assert(TSum().contains(emptySum))
  }

  test("BitWidth:Sum:OneOperand") {
    assert(TSum(U8) == U8)
  }

  test("BitWidth:Sum:Zeros") {
    val u0 = TyUInt(0)
    assert(TSum(u0, u0, u0, u0) == u0)
  }

  test("BitWidth:Sum:SimpleCase") {
    // [0, 2^8 - 1] + [0, 2^8 - 1] = [0, 2^9 - 2]
    assert(U8 + U8 == TyUInt(9))
    // [-2^7, 2^7 - 1] + [-2^7, 2^7 - 1] = [-2^8, 2^8 - 2]
    assert(I8 + I8 == TySInt(9))
  }

  test("BitWidth:Sum:MixedSignedness") {
    // [0, 2^8 - 1] + [-2^7, 2^7 - 1] = [-2^7, 2^8 + 2^7 - 2]
    assert(U8 + I8 == TySInt(10))
    // [0, 2^8 - 1] + [-2^6, 2^6 - 1] = [-2^6, 2^8 + 2^6 - 2]
    assert(U8 + TySInt(7) == TySInt(10))
    // [0, 2^8 - 1] + [-2^8, 2^8 - 1] = [-2^8, 2^9 - 2]
    assert(U8 + TySInt(9) == TySInt(10))
    // [0, 2^8 - 1] + [-2^9, 2^9 - 1] = [-2^9, 2^9 + 2^8 - 2]
    assert(U8 + TySInt(10) == TySInt(11))
  }

  test("BitWidth:Sum:BigAndSmall") {
    // [0, 2^8 - 1] + [0, 2^8 - 1] + [0, 1] = [0, 2^9 - 1]
    assert(TSum(U8, U8, TyUInt(1)) == TyUInt(9))
  }

  test("BitWidth:Sum:MoreThanTwoOperands") {
    // 3 * [0, 2^8 - 1] = [0, 3*2^8 - 3] = [0, 2^9 + 2^8 - 3]
    assert(TSum(U8, U8, U8) == TyUInt(10))
    // 4 * [0, 2^8 - 1] = [0, 2^10 - 4]
    assert(TSum(U8, U8, U8, U8) == TyUInt(10))
    // Doing the addition gradually results in more bits due to rounding errors
    assert(((U8 + U8) + U8) + U8 == TyUInt(11))
    // 3 * [-2^7, 2^7 - 1] = [-3 * 2^7, 3 * 2^7 - 3] = [-2^8 - 2^7, 2^8 + 2^7 - 3]
    assert(TSum(I8, I8, I8) == TySInt(10))
    // 4 * [-2^7, 2^7 - 1] = [-2^9, 2^9 - 4]
    assert(TSum(I8, I8, I8, I8) == TySInt(10))
    // [-2^7, 3(2^8 - 1) + 2^7 - 1] = [-2^7, 2^9 + 2^8 + 2^7 - 4]
    assert(TSum(U8, U8, I8, U8) == TySInt(11))
  }

  test("BitWidth:Sum:BruteForce") {
    val testCases = Seq(TyUInt(2), TyUInt(3), TySInt(2), TySInt(3), TySInt(4))
    for (idx1 <- testCases.indices) {
      for (idx2 <- idx1 until testCases.length) {
        val t1 = testCases(idx1)
        val t2 = testCases(idx2)
        val tResult = t1 + t2
        for (x <- t1.minInt to t1.maxInt) {
          for (y <- t2.minInt to t2.maxInt) {
            assert(tResult.contains(x + y), s"(x = $x, y = $y)")
          }
        }
      }
    }
  }

  test("BitWidth:Prod:Empty") {
    val emptyProd = ir.eval(Prod()()).asInstanceOf[IntCst].i
    assert(TProd().contains(emptyProd))
  }

  test("BitWidth:Prod:OneOperand") {
    assert(TProd(U8) == U8)
  }

  test("BitWidth:Prod:Zeros") {
    val u0 = TyUInt(0)
    assert(TProd(u0, u0, u0, u0) == u0)
  }

  test("BitWidth:Prod:SimpleCase") {
    // [0, 2^8 - 1] * [0, 2^8 - 1] = [0, (2^8 - 1)^2] = [0, 2^16 - 2^9 + 1]
    assert(U8 * U8 == TyUInt(16))
    // [-2^7, 2^7 - 1] * [-2^7, 2^7 - 1] = [-2^7 (2^7 - 1), 2^14] = [-2^14 + 2^7, 2^14]
    assert(I8 * I8 == TySInt(16))
  }

  test("BitWidth:Prod:MixedSignedness") {
    // [0, 3] * [-2, 1] = [-6, 3]
    assert(TyUInt(2) * TySInt(2) == TySInt(4))
    // [0, 2^8 - 1] * [-2^7, 2^7 - 1] = [-2^7 (2^8 - 1), (2^8 - 1) (2^7 - 1)] = [-2^15 + 2^7, 2^15 - 2^8 - 2^7 + 1]
    assert(U8 * I8 == TySInt(16))
    assert(U8 * TySInt(7) == TySInt(15))
    assert(U8 * TySInt(9) == TySInt(17))
    assert(U8 * TySInt(10) == TySInt(18))
  }

  test("BitWidth:Prod:MoreThanTwoOperands") {
    // [0, (2^8 - 1)^3]
    assert(TProd(U8, U8, U8) == TyUInt(24))
    // [0, (2^8 - 1)^4]
    assert(TProd(U8, U8, U8, U8) == TyUInt(32))
    // [(-2^7)^3, (-2^7)^2 (2^7 - 1)] = [-2^21, 2^21 - 2^14]
    assert(TProd(I8, I8, I8) == TySInt(22))
    // [(-2^7)^3 (2^7 - 1), (-2^7)^4] = [-2^28 + 2^21, 2^28]
    assert(TProd(I8, I8, I8, I8) == TySInt(30))
    // [-2^7 (2^8 - 1)^3, (2^8 - 1)^3 (2^7 - 1)]
    assert(TProd(U8, U8, I8, U8) == TySInt(32))
    // [-2^7 (2^8 - 1) (2^7 - 1), (-2^7)^2 (2^8 - 1)^2]
    assert(TProd(I8, U8, I8, U8) == TySInt(31))
  }

  test("BitWidth:Prod:BruteForce") {
    val testCases = Seq(TyUInt(2), TyUInt(3), TySInt(2), TySInt(3), TySInt(4))
    for (idx1 <- testCases.indices) {
      for (idx2 <- idx1 until testCases.length) {
        val t1 = testCases(idx1)
        val t2 = testCases(idx2)
        val tResult = t1 * t2
        for (x <- t1.minInt to t1.maxInt) {
          for (y <- t2.minInt to t2.maxInt) {
            assert(
              tResult.contains(x * y),
              s"(t1 = $t1, t2 = $t2, x = $x, y = $y)"
            )
          }
        }
      }
    }
  }

  test("BitWidth:Div:ZeroDenominator") {
    val expectedMessage =
      "Denominator of div is guaranteed to be zero since its bit width is zero."
    val exc1 = intercept[ArithmeticException](U8 / TyUInt(0))
    assert(exc1.getMessage == expectedMessage)
    val exc2 = intercept[ArithmeticException](U8 / TySInt(0))
    assert(exc2.getMessage == expectedMessage)
  }

  test("BitWidth:Div") {
    assert(U8 / U8 == U8)
    assert(U8 / U32 == U8)
    assert(U16 / U8 == U16)

    // [-2^7, 2^7 - 1] / [0, 2^8 - 1] = [-2^7, 2^7 - 1]
    assert(I8 / U8 == I8)

    // [0, 2^8 - 1] / [-2^7, 2^7 - 1] = [-2^8 + 1, 2^8 - 1]
    assert(U8 / I8 == TySInt(9))

    // [-2^7, 2^7 - 1] / [-2^7, 2^7 - 1] = [-2^7, 2^7]
    // Just one too many T-T
    assert(I8 / I8 == TySInt(9))
    assert(I8 / I32 == TySInt(9))
    assert(I16 / I8 == TySInt(17))
  }

  test("BitWidth:Div:BruteForce") {
    val testCases = Seq(TyUInt(2), TyUInt(3), TySInt(2), TySInt(3), TySInt(4))
    for (idx1 <- testCases.indices) {
      for (idx2 <- idx1 until testCases.length) {
        val t1 = testCases(idx1)
        val t2 = testCases(idx2)
        val tResult = t1 / t2
        for (x <- t1.minInt to t1.maxInt) {
          for (y <- t2.minInt to t2.maxInt) {
            if (y != 0) {
              assert(tResult.contains(x / y), s"(x = $x, y = $y)")
            }
          }
        }
      }
    }
  }

  test("BitWidth:Mod") {
    assert(U8 % U8 == U8)
    assert(U32 % U8 == U8)
    assert(U8 % U32 == U8)

    // [-2^6, 2^6 - 1] % [0, 2^8 - 1] = [-2^6, 2^6 - 1]
    assert(TySInt(7) % U8 == TySInt(7))
    // [-2^7, 2^7 - 1] % [0, 2^8 - 1] = [-2^7, 2^7 - 1]
    assert(I8 % U8 == I8)
    // [-2^8, 2^8 - 1] % [0, 2^8 - 1] = [-2^8 + 2, 2^8 - 2]
    assert(TySInt(9) % U8 == TySInt(9))
    // [-2^9, 2^9 - 1] % [0, 2^8 - 1] = [-2^8 + 2, 2^8 - 2]
    assert(TySInt(10) % U8 == TySInt(9))

    // [0, 2^8 - 1] % [-2^7, 2^7 - 1] = [0, 2^7 - 2]
    assert(U8 % I8 == TyUInt(7))
    // [0, 2^8 - 1] % [-2^8, 2^8 - 1] = [0, 2^8 - 2]
    assert(U8 % TySInt(9) == TyUInt(8))
    assert(U8 % TySInt(10) == TyUInt(8))

    // [-2^7, 2^7 - 1] % [-2^7, 2^7 - 1] = [-2^7 + 1, 2^7 - 1]
    assert(I8 % I8 == TySInt(8))
    // [-2^7, 2^7 - 1] % [-2^6, 2^6 - 1] = [-2^6 + 1, 2^6 - 1]
    assert(I8 % TySInt(7) == TySInt(7))
    // [-2^7, 2^7 - 1] % [-2^8, 2^8 - 1] = [-2^7, 2^7 - 1]
    assert(I8 % TySInt(9) == TySInt(8))
  }

  test("BitWidth:Mod:ZeroDenominator") {
    val expectedMessage =
      "Denominator of mod is guaranteed to be zero since its bit width is zero."
    val exc1 = intercept[ArithmeticException](U8 % TyUInt(0))
    assert(exc1.getMessage == expectedMessage)
    val exc2 = intercept[ArithmeticException](U8 % TySInt(0))
    assert(exc2.getMessage == expectedMessage)
  }

  test("BitWidth:Mod:BruteForce") {
    val testCases = Seq(TyUInt(2), TyUInt(3), TySInt(2), TySInt(3), TySInt(4))
    for (idx1 <- testCases.indices) {
      for (idx2 <- idx1 until testCases.length) {
        val t1 = testCases(idx1)
        val t2 = testCases(idx2)
        val tResult = t1 % t2
        for (x <- t1.minInt to t1.maxInt) {
          for (y <- t2.minInt to t2.maxInt) {
            if (y != 0) {
              assert(tResult.contains(x % y), s"(x = $x, y = $y)")
            }
          }
        }
      }
    }
  }

  test("LowerType:NonStreams") {
    val t = TyTuple(
      Missing,
      U8,
      I16,
      TyBool,
      TyArrow(I8, I32),
      TyVec(TyVec(U32, 5), 4)
    )
    assert(t.lower == t)
  }

  test("LowerType:Stm[Int]") {
    val t = TyStm(U8, n)
    assert(t.lower == t)
  }

  test("LowerType:Vec[Int]") {
    val t = TyVec(U8, n)
    assert(t.lower == t)
  }

  test("LowerType:Stm[Stm[Int]]") {
    val t = TyStm(TyStm(TyBool, m), n)
    assert(t.lower == TyStm(TyBool, n * m))
  }

  test("LowerType:Stm[Vec[Int]]") {
    val t = TyStm(TyVec(U8, m), n)
    assert(t.lower == t)
  }

  test("LowerType:Vec[Stm[Int]]") {
    val t = TyVec(TyStm(U16, m), n)
    assert(t.lower == TyStm(TyVec(U16, n), m))
  }

  test("LowerType:Vec[Vec[Int]]") {
    val t = TyVec(TyVec(U32, m), n)
    assert(t.lower == t)
  }

  test("LowerType:Stm[Stm[Stm[Int]]]") {
    val t = TyStm(TyStm(TyStm(TyBool, k), m), n)
    assert(t.lower == TyStm(TyBool, n * m * k))
  }

  test("LowerType:Stm[Stm[Vec[Int]]]") {
    val t = TyStm(TyStm(TyVec(I8, k), m), n)
    assert(t.lower == TyStm(TyVec(I8, k), n * m))
  }

  test("LowerType:Stm[Vec[Stm[Int]]]") {
    val t = TyStm(TyVec(TyStm(I16, k), m), n)
    assert(t.lower == TyStm(TyVec(I16, m), n * k))
  }

  test("LowerType:Stm[Vec[Vec[Int]]]") {
    val t = TyStm(TyVec(TyVec(I32, k), m), n)
    assert(t.lower == t)
  }

  test("LowerType:Vec[Stm[Stm[Int]]]") {
    val t = TyVec(TyStm(TyStm(U8, k), m), n)
    assert(t.lower == TyStm(TyVec(U8, n), m * k))
  }

  test("LowerType:Vec[Stm[Vec[Int]]]") {
    val t = TyVec(TyStm(TyVec(U16, k), m), n)
    assert(t.lower == TyStm(TyVec(TyVec(U16, k), n), m))
  }

  test("LowerType:Vec[Vec[Stm[Int]]]") {
    val t = TyVec(TyVec(TyStm(U32, k), m), n)
    assert(t.lower == TyStm(TyVec(TyVec(U32, m), n), k))
  }

  test("LowerType:Vec[Vec[Vec[Int]]]") {
    val t = TyVec(TyVec(TyVec(U8, k), m), n)
    assert(t.lower == t)
  }
}
