package mhir.optimize

import org.scalatest.funsuite.AnyFunSuite
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

class BinOpTreeMakerTests extends AnyFunSuite {
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

  test("x0 + x1 + x2 + x3") {
    val e = Sum(x0, x1, x2, x3)().tchk()
    val expected = Sum(Sum(x0, x1)(), Sum(x2, x3)())()
    val actual = BinOpTreeMaker.makeBinOpTrees(e)
    assert(actual == expected)
  }

  test("x0 + x1 + x2 - x3") {
    val e = Sum(x0, x1, x2, Prod(C(-1)(I8), x3)())().tchk()
    val expected = Sum(Sum(x0, x1)(), Sum(x2, Prod(C(-1)(I8), x3)())())()
    val actual = BinOpTreeMaker.makeBinOpTrees(e)
    assert(actual == expected)
  }

  test("x0 + x1 - x2 - x3") {
    val e = Sum(x0, x1, Prod(C(-1)(I8), x2)(), Prod(C(-1)(I8), x3)())().tchk()
    val expected =
      Sum(Sum(x0, x1)(), Prod(C(-1)(I8), Sum(x2, x3)())())()
    val actual = BinOpTreeMaker.makeBinOpTrees(e)
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
    val actual = BinOpTreeMaker.makeBinOpTrees(e)
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
    val actual = BinOpTreeMaker.makeBinOpTrees(e)
    assert(actual == expected)
  }

  test("x0 * x1 * x3 * x4") {
    val e = Prod(x0, x1, x2, x3)().tchk()
    val expected = Prod(Prod(x0, x1)(), Prod(x2, x3)())()
    val actual = BinOpTreeMaker.makeBinOpTrees(e)
    assert(actual == expected)
  }

  test("b0 && b1 && b2 && b3") {
    val e = And(b0, b1, b2, b3)().tchk()
    val expected = And(And(b0, b1)(), And(b2, b3)())()
    val actual = BinOpTreeMaker.makeBinOpTrees(e)
    assert(actual == expected)
  }

  test("b0 || b1 || b2 || b3") {
    val e = Or(b0, b1, b2, b3)().tchk()
    val expected = Or(Or(b0, b1)(), Or(b2, b3)())()
    val actual = BinOpTreeMaker.makeBinOpTrees(e)
    assert(actual == expected)
  }
}
