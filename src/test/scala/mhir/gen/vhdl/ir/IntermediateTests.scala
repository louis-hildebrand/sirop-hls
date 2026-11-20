package mhir.gen.vhdl.ir

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable.ListMap

class IntermediateTests extends AnyFunSuite {

  test("FunctionIntermediate:FreeVars") {
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
      Tuple(x, y, a, b, c)().tchk()
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
          // TODO: On second thought, I think it's silly that a2 is free here.
          //       This would be an error in VHDL (variable used before it's initialized), and it
          //       would probably be simpler to consider a2 bound everywhere inside the function.
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
    val Seq(_, newA2) = actual.locals.keys.toSeq
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

  test("BlockIntermediate:Substitute") {
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(U8)
    val z = Param("z", -1)(U8)
    val block = BlockIntermediate(
      locals = ListMap(
        x -> ExprIntermediate(Sum(C(42)(U8), y /*bound*/ )().tchk()),
        y -> ExprIntermediate(Sum(C(43)(U8), z /*free*/ )().tchk())
      ),
      body = ExprIntermediate(Sum(x, y, z)().tchk())
    )
    val subs = Map[Expr, Expr](
      // NOTE: These substitutions should have no effect, since x and y are local variables
      //       in the block
      x -> C(99)(U8),
      y -> C(100)(U8),
      // NOTE: Watch out for variable capture
      z -> Sum(C(5)(U8), x)().tchk()
    )

    val actual = block.substitute(subs)

    assert(actual.locals.size == 2)
    val renamedX = actual.locals
      .find({ case (x, _) => x.name.startsWith("x") })
      .map({ case (x, _) => x })
      .get
    val renamedY = actual.locals
      .find({ case (y, _) => y.name.startsWith("y") })
      .map({ case (y, _) => y })
      .get
    val expected = BlockIntermediate(
      locals = ListMap(
        renamedX -> ExprIntermediate(Sum(C(42)(U8), renamedY)().tchk()),
        renamedY -> ExprIntermediate(
          Sum(C(43)(U8), Sum(C(5)(U8), x)())().tchk()
        )
      ),
      body =
        ExprIntermediate(Sum(renamedX, renamedY, Sum(C(5)(U8), x)())().tchk())
    )
    // TODO: Simplify all this code by just having an alphaEquals method for Intermediates?
    assert(actual == expected)
  }
}
