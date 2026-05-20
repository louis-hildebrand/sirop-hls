package mhir.optimize.cost

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar._
import mhir.testing.ParamStore
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class SimpleDelayCostModelTests extends AnyFunSuite {

  private val modelWithMadd = SimpleDelayCostModel(madd = true)
  private val modelWithoutMadd = SimpleDelayCostModel(madd = false)

  private def X = ParamStore("x")
  private def Y = ParamStore("y")
  private def Z = ParamStore("z")
  private def W = ParamStore("w")

  // Two multiplications per cycle is too much ---------------------------------

  test("TwoMultiplications") {
    val e = (X(U8) * Y(U8) * Z(U8)).tchk().lower
    assert(modelWithoutMadd.cost(e) > modelWithoutMadd.FullCycleDelay)
  }

  test("TwoWrappingMultiplications") {
    val e = (X(U8) *% Y(U8) *% Z(U8)).tchk().lower
    assert(modelWithoutMadd.cost(e) > modelWithoutMadd.FullCycleDelay)
  }

  // Multiplication by a power of two is free ----------------------------------

  test("MulBy2") {
    val e = (X(U8) * C(2)(U8)).tchk().lower
    assert(modelWithoutMadd.rawCost(e) == 0)
  }

  test("MulBy32") {
    val e = (X(I16) * C(32)(I16)).tchk().lower
    assert(modelWithoutMadd.rawCost(e) == 0)
  }

  test("WrappingMulBy4") {
    val e = (X(U8) *% C(4)(U8)).tchk().lower
    assert(modelWithoutMadd.rawCost(e) == 0)
  }

  test("WrappingMulBy8") {
    val e = (X(I16) *% C(8)(I16)).tchk().lower
    assert(modelWithoutMadd.rawCost(e) == 0)
  }

  // One multiplication per cycle is acceptable --------------------------------

  test("OneRealMul") {
    val e = (X(U16) * Y(U16) * C(8)(U16)).tchk().lower
    val cost = modelWithoutMadd.rawCost(e)
    assert(cost > 0)
    assert(cost <= modelWithoutMadd.FullCycleDelay)
  }

  test("OneRealWrappingMul") {
    val e = (X(U16) *% Y(U16) *% C(8)(U16)).tchk().lower
    val cost = modelWithoutMadd.rawCost(e)
    assert(cost > 0)
    assert(cost <= modelWithoutMadd.FullCycleDelay)
  }

  // Madd ----------------------------------------------------------------------

  test("MaddDisabled") {
    val e = ((X(I16) *% Y(I16)) +% (Z(I16) *% W(I16))).tchk().lower
    val cost = modelWithoutMadd.cost(e)
    assert(cost > modelWithoutMadd.FullCycleDelay)
  }

  test("MaddEnabled:WrappingSumAndProd") {
    val e = ((X(I16) *% Y(I16)) +% (Z(I16) *% W(I16))).tchk().lower
    val cost = modelWithMadd.cost(e)
    assert(cost > 0)
    assert(cost <= modelWithMadd.FullCycleDelay)
  }

  test("MaddEnabled:RegularSumAndProd") {
    val e = Sum(Prod(X(I16), Y(I16))(), Prod(Z(I16), W(I16))())().tchk().lower
    val cost = modelWithMadd.cost(e)
    assert(cost > 0)
    assert(cost <= modelWithMadd.FullCycleDelay)
  }
}
