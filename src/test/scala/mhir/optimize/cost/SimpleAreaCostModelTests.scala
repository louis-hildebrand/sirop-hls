package mhir.optimize.cost

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar._
import mhir.typecheck._
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

  test("VecAccessInsideVecBuild") {
    val v = Param("v")(TyVec(U8, 2))
    val f = (x: Expr) => Sum(C(42)(U8), x)()
    val e0 =
      VecBuild(2, U8 ::+ (i => f(VecAccess(v, i)())))().tchk().lower()
    val e1 = Tuple(f(VecAccess(v, 0)()), f(VecAccess(v, 1)()))().tchk().lower()
    // The VecAccess should actually disappear in hardware since the VecBuild
    // index is static at any given position within the vector.
    assert(SimpleAreaCostModel.cost(e0) == SimpleAreaCostModel.cost(e1))
  }

  test("MuxInsideVecBuild") {
    val v = Param("v")(TyVec(U8, 2))
    val f = (x: Expr) => Sum(C(42)(U8), x)()
    val e = VecBuild(
      3,
      U8 ::+ (i => Mux(i === C(0)(U8), C(0)(U8), f(VecAccess(v, i)()))())
    )().tchk().lower()
    // The MUX should actually disappear in hardware since the VecBuild index is
    // static at any given position within the vector.
    // It may be impractical to actually check in general what the cost is for
    // each index individually, since the vector may be large.
    // But the cost should definitely be within these bounds.
    val cost = SimpleAreaCostModel.cost(e)
    val costOneElem = SimpleAreaCostModel.cost(f(VecAccess(v, 0)()).tchk())
    assert(cost >= costOneElem * 2)
    assert(cost <= costOneElem * 3)
  }
}
