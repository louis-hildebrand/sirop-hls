package mhir.gen.vhdl.ir

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

  test("FunctionIntermediate:Substitute") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val z = Param("z")(U8)
    val a1 = Param("a1")(U8)
    val a2 = Param("a2")(U8)
    val original = FunctionIntermediate(
      Seq(x, y),
      ListMap(
        a1 -> ExprIntermediate(
          Sum(x, y, a1 /* free */, a2 /* free */ )().tchk()
        ),
        a2 -> ExprIntermediate(
          Sum(x, y, a1 /* bound */, a2 /* free */ )().tchk()
        )
      ),
      Sum(x, y, a1 /* bound */, a2 /* bound */, z /* free*/ )().tchk()
    )
    val subs = Map[Expr, Expr](
      // This substitution will never apply, since x is a parameter of the function
      x -> C(42)(U8),
      // This substitution will sometimes apply
      // Also notice how it refers to free variable x, so need to be careful to avoid capture
      a1 -> Prod(C(3)(U8), x)().tchk(),
      // This substitution will sometimes apply
      a2 -> C(15)(U8),
      // Notice how this substitution refers to free variable a2, so need to be careful to avoid capture
      z -> Prod(C(5)(U8), a2)().tchk()
    )
    val actual = original.substitute(subs)

    // Some variables need to be renamed to avoid capture
    val Seq(newX, _) = actual.params
    assert(newX != x)
    val Seq(_, newA2) = actual.intermediates.keys.toSeq
    assert(newA2 != a2)
    // Don't rename needlessly
    val newY = y
    val newA1 = a1
    val expected = FunctionIntermediate(
      Seq(newX, newY),
      ListMap(
        newA1 -> ExprIntermediate(
          Sum(newX, newY, Prod(C(3)(U8), x)(), C(15)(U8))().tchk()
        ),
        newA2 -> ExprIntermediate(
          Sum(newX, newY, newA1, C(15)(U8))().tchk()
        )
      ),
      Sum(newX, newY, newA1, newA2, Prod(C(5)(U8), a2)())().tchk()
    )
    assert(actual == expected)
  }
}
