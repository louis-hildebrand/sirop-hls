package mhir.optimize.cost

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import org.scalatest.funsuite.AnyFunSuite

class SimpleAreaCostModelTests extends AnyFunSuite {
  test("EmptyStmBuild") {
    val s = StmBuild(10, True, True)().tchk()
    val cost = SimpleAreaCostModel.cost(s)
    assert(cost.mem > 0)
  }

  test("EmptyTuple") {
    assert(SimpleAreaCostModel.cost(Tuple()().tchk()) == AreaCost.Zero)
  }
}
