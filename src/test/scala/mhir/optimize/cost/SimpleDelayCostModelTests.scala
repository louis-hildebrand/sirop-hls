package mhir.optimize.cost

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.sugar._
import mhir.testing.ParamStore
import org.scalatest.funsuite.AnyFunSuite

class SimpleDelayCostModelTests extends AnyFunSuite {

  private def X = ParamStore("x")
  private def Y = ParamStore("y")
  private def Z = ParamStore("z")

  // Two multiplications per cycle is too much ---------------------------------

  test("TwoMultiplications") {
    val e = (X(U8) * Y(U8) * Z(U8)).tchk().lower()
    assert(SimpleDelayCostModel.cost(e) > SimpleDelayCostModel.FullCycleDelay)
  }

  test("TwoWrappingMultiplications") {
    val e = (X(U8) *% Y(U8) *% Z(U8)).tchk().lower()
    assert(SimpleDelayCostModel.cost(e) > SimpleDelayCostModel.FullCycleDelay)
  }

  // Multiplication by a power of two is free ----------------------------------

  test("MulBy2") {
    val e = (X(U8) * C(2)(U8)).tchk().lower()
    assert(SimpleDelayCostModel.rawCost(e) == 0)
  }

  test("MulBy32") {
    val e = (X(I16) * C(32)(I16)).tchk().lower()
    assert(SimpleDelayCostModel.rawCost(e) == 0)
  }

  test("WrappingMulBy4") {
    val e = (X(U8) *% C(4)(U8)).tchk().lower()
    assert(SimpleDelayCostModel.rawCost(e) == 0)
  }

  test("WrappingMulBy8") {
    val e = (X(I16) *% C(8)(I16)).tchk().lower()
    assert(SimpleDelayCostModel.rawCost(e) == 0)
  }

  // One multiplication per cycle is acceptable --------------------------------

  test("OneRealMul") {
    val e = (X(U16) * Y(U16) * C(8)(U16)).tchk().lower()
    val cost = SimpleDelayCostModel.rawCost(e)
    assert(cost > 0)
    assert(cost <= SimpleDelayCostModel.FullCycleDelay)
  }

  test("OneRealWrappingMul") {
    val e = (X(U16) *% Y(U16) *% C(8)(U16)).tchk().lower()
    val cost = SimpleDelayCostModel.rawCost(e)
    assert(cost > 0)
    assert(cost <= SimpleDelayCostModel.FullCycleDelay)
  }
}
