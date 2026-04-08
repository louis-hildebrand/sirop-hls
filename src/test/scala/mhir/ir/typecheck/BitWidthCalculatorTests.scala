package mhir.ir
package typecheck

import org.scalatest.funsuite.AnyFunSuite

class BitWidthCalculatorTests extends AnyFunSuite {
  test("BitWidth:Sum:Empty") {
    val emptySum = mhir.eval.eval(Sum()()).asInstanceOf[IntCst].i
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
    val emptyProd = mhir.eval.eval(Prod()()).asInstanceOf[IntCst].i
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
    val exc = intercept[ArithmeticException](U8 / TyUInt(0))
    assert(exc.getMessage == expectedMessage)
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
    val exc = intercept[ArithmeticException](U8 % TyUInt(0))
    assert(exc.getMessage == expectedMessage)
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
}
