package mhir.ir

import org.scalatest.funsuite.AnyFunSuite

class DiGraphTests extends AnyFunSuite {

  test("TopologicalIshOrder:Loop") {
    val g = DiGraph(
      nodes = Set("c", "b", "a"),
      edges = Set(("a", "b"), ("b", "a"), ("c", "a"), ("c", "b"))
    )
    val order = g.topologicalIshOrder()
    assert(order.indexOf("a") < order.indexOf("c"))
    assert(order.indexOf("b") < order.indexOf("c"))
    // Indices of "a" and "b" are unknown
  }
}
