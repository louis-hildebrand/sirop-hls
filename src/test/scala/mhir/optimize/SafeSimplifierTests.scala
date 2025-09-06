package mhir.optimize

import mhir.ir.Lowering.ExprLowering
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.sugar._
import org.scalatest.funsuite.AnyFunSuite

class SafeSimplifierTests extends AnyFunSuite {

  private val simplifier = SafeSimplifier()

  test("LetStm:ZeroUses") {
    val count = simplifier.simplify(StmCount(C(42)(U8))().tchk().lower())
    val e = {
      val s = Param("s")()
      LetStm(s, StmCount(C(10)(U8))(), count)().tchk().lower()
    }
    val actual = simplifier.simplify(e)
    assert(actual == count)
  }

  test("LetStm:OneUse") {
    val count = StmCount(C(10)(U8))().tchk().lower()
    val s = Param("s")(TyStm(U8, 10))
    val map = StmMap(s, U8 ::+ (x => x + 5))().tchk().lower()
    val e = LetStm(s, count, map)().tchk().lower()
    val expected = simplifier.simplify(map.subPreserveType(s -> count))
    val actual = simplifier.simplify(e)
    assert(actual == expected)
  }

  test("LetStm:TwoUses") {
    val s0 = Param("s0")(TyStm(U8, 10))
    val s1 = Param("s1")(TyStm(U8, 10))
    val zipped =
      simplifier.simplify(StmZip(s1, StmMap(s1, U8 ::+ (x => x + 5))())())
    val e = LetStm(s1, s0, zipped)()
    val expected = e
    val actual = simplifier.simplify(e)
    assert(actual == expected)
  }
}
