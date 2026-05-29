package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck.TypeCheck
import org.scalatest.funsuite.AnyFunSuite

class BinOpTreeBalancingPassTests extends AnyFunSuite {
  private val x0 = Param("x0", -1)(I8)
  private val x1 = Param("x1", -1)(I8)
  private val x2 = Param("x2", -1)(I8)
  private val x3 = Param("x3", -1)(I8)
  private val x4 = Param("x4", -1)(I8)
  private val x5 = Param("x5", -1)(I8)
  private val x6 = Param("x6", -1)(I8)
  private val x7 = Param("x7", -1)(I8)
  private val b0 = Param("b0", -1)(TyBool)
  private val b1 = Param("b1", -1)(TyBool)
  private val b2 = Param("b2", -1)(TyBool)
  private val b3 = Param("b3", -1)(TyBool)
  private val y0 = Param("y0", -1)(I8)
  private val y1 = Param("y1", -1)(I8)
  private val y2 = Param("y2", -1)(I8)
  private val y3 = Param("y3", -1)(I8)
  private val y4 = Param("y4", -1)(I8)
  private val y5 = Param("y5", -1)(I8)

  private val pass: BinOpTreeBalancingPass = BinOpTreeBalancingPass()

  test("x0 + x1 + x2 + x3") {
    val e = Sum(x0, x1, x2, x3)().tchk()
    val expected = Sum(Sum(x0, x1)(), Sum(x2, x3)())()
    val actual = pass.balance(e)
    assert(actual == expected)
  }

  test("x0 + x1 + x2 - x3") {
    val e = Sum(x0, x1, x2, Prod(C(-1)(I8), x3)())().tchk()
    val expected = Sum(Sum(x0, x1)(), Sum(x2, Prod(C(-1)(I8), x3)())())()
    val actual = pass.balance(e)
    assert(actual == expected)
  }

  test("x0 + x1 - x2 - x3") {
    val e = Sum(x0, x1, Prod(C(-1)(I8), x2)(), Prod(C(-1)(I8), x3)())().tchk()
    val expected =
      Sum(Sum(x0, x1)(), Prod(C(-1)(I8), Sum(x2, x3)())())()
    val actual = pass.balance(e)
    assert(actual == expected)
  }

  test("x0 - x1 - x2 - x3") {
    val e = Sum(
      x0,
      Prod(C(-1)(I8), x1)(),
      Prod(C(-1)(I8), x2)(),
      Prod(C(-1)(I8), x3)()
    )().tchk()
    val expected =
      Sum(Sum(x0, Prod(C(-1)(I8), x1)())(), Prod(C(-1)(I8), Sum(x2, x3)())())()
    val actual = pass.balance(e)
    assert(actual == expected)
  }

  test("x0 + x1 + x2 - x3 - x4 - x5 - x6 - x7") {
    val e = Sum(
      x0,
      x1,
      x2,
      Prod(C(-1)(I8), x3)(),
      Prod(C(-1)(I8), x4)(),
      Prod(C(-1)(I8), x5)(),
      Prod(C(-1)(I8), x6)(),
      Prod(C(-1)(I8), x7)()
    )().tchk()
    val expected = Sum(
      Sum(
        Sum(x0, x1)(),
        Sum(x2, Prod(C(-1)(I8), x3)())()
      )(),
      Prod(
        C(-1)(I8),
        Sum(
          Sum(x4, x5)(),
          Sum(x6, x7)()
        )()
      )()
    )()
    val actual = pass.balance(e)
    assert(actual == expected)
  }

  test("x0 +% x1 +% x2 +% x3") {
    val e = WrappingSum(x0, x1, x2, x3)().tchk()
    val expected = WrappingSum(WrappingSum(x0, x1)(), WrappingSum(x2, x3)())()
    val actual = pass.balance(e)
    assert(actual == expected)
  }

  test("FIR6") {
    val e = WrappingSum(
      x0 *% y0,
      x1 *% y1,
      x2 *% y2,
      x3 *% y3,
      x4 *% y4,
      x5 *% y5
    )().tchk()
    val expected = WrappingSum(
      WrappingSum(x0 *% y0, x1 *% y1)(),
      WrappingSum(
        WrappingSum(x2 *% y2, x3 *% y3)(),
        WrappingSum(x4 *% y4, x5 *% y5)()
      )()
    )().tchk()
    val actual = pass.balance(e)
    assert(actual == expected)
  }

  /* Split in such a way that binary ops are as close as possible to the leaves.
   * Thus, the number of mul-add operations is maximized.
   */
  {
    def countMadd(e: Expr): Int = {
      e match {
        case Sum(Prod(_, _), Prod(_, _)) => 1
        case e                           => e.children.map(countMadd).sum
      }
    }
    for (n <- 2 until 20) {
      test(s"MaxMulAdd:$n") {
        val original = Sum(
          (0 until n).map(i =>
            Prod(Param(s"x$i", -1)(I16), Param(s"y$i", -1)(I16))()
          ): _*
        )().tchk()
        val balanced = pass.balance(original)
        val expectedMadd = n / 2
        val actualMadd = countMadd(balanced)
        assert(actualMadd == expectedMadd)
      }
    }
  }

  test("x0 * x1 * x3 * x4") {
    val e = Prod(x0, x1, x2, x3)().tchk()
    val expected = Prod(Prod(x0, x1)(), Prod(x2, x3)())()
    val actual = pass.balance(e)
    assert(actual == expected)
  }

  test("x0 *% x1 *% x2 *% x3") {
    val e = WrappingProd(x0, x1, x2, x3)().tchk()
    val expected =
      WrappingProd(WrappingProd(x0, x1)(), WrappingProd(x2, x3)())()
    val actual = pass.balance(e)
    assert(actual == expected)
  }

  test("b0 && b1 && b2 && b3") {
    val e = And(b0, b1, b2, b3)().tchk()
    val expected = And(And(b0, b1)(), And(b2, b3)())()
    val actual = pass.balance(e)
    assert(actual == expected)
  }

  test("b0 || b1 || b2 || b3") {
    val e = Or(b0, b1, b2, b3)().tchk()
    val expected = Or(Or(b0, b1)(), Or(b2, b3)())()
    val actual = pass.balance(e)
    assert(actual == expected)
  }
}
