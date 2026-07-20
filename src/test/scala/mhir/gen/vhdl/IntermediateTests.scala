package mhir.gen.vhdl

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable.ListMap

class IntermediateTests extends AnyFunSuite {

  test("FreeVars") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val a = Param("a")(U8)
    val b = Param("b")(U8)
    val c = Param("c")(U8)
    val intermediate = FunctionIntermediate(
      Seq(x, y),
      ListMap(
        a -> ExprIntermediate(Sum(x, b)().tchk()), // b is free here
        b -> ExprIntermediate(Sum(y, a)().tchk()) // a is bound here
      ),
      Tuple(x, y, a, b, c)()
    )
    val actual = intermediate.freeVars
    val expected = Set(b, c)
    assert(actual == expected)
  }
}
