package mhir.optimize

import mhir.ir.Lowering.ExprLowering
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.sugar._
import org.scalatest.funsuite.AnyFunSuite

class StmSimplifierTests extends AnyFunSuite {

  private val simplifier = StmSimplifier()

  test("LetStm:ZeroUses") {
    val count = simplifier.simplify(StmCount(C(42)(U8))().tchk().lower())
    val e = {
      val s = Param("s")()
      LetStm(1, s, StmCount(C(10)(U8))(), count)().tchk().lower()
    }
    val actual = simplifier.simplify(e)
    assert(actual == count)
  }

  test("LetStm:OneUse") {
    val count = StmCount(C(10)(U8))().tchk().lower()
    val s = Param("s")(TyStm(U8, 10))
    val map = StmMap(s, U8 ::+ (x => x + 5))().tchk().lower()
    val e = LetStm(1, s, count, map)().tchk().lower()
    val expected = simplifier.simplify(map.subPreserveType(s -> count))
    val actual = simplifier.simplify(e)
    assert(actual == expected)
  }

  test("LetStm:TwoUses") {
    val s0 = Param("s0")(TyStm(U8, 10))
    val s1 = Param("s1")(TyStm(U8, 10))
    val zipped =
      simplifier.simplify(StmZip(s1, StmMap(s1, U8 ::+ (x => x + 5))())())
    val e = LetStm(1, s1, s0, zipped)()
    val expected = e
    val actual = simplifier.simplify(e)
    assert(actual == expected)
  }

  test("StmBuild:IOCounterTrio0") {
    val n = 16
    val original = {
      val inCtr = Param("in_ctr")(U32)
      val firstStep = Param("is_first_step")(TyBool)
      val outCtr = Param("out_ctr")(U32)
      val shouldReset =
        ((!firstStep && (inCtr equ C(1)(U32)) && (outCtr equ C(2)(U32)))
          || (firstStep && (inCtr equ C(0)(U32)) && (outCtr equ C(2)(U32))))
      StmBuild(
        n,
        Tuple(
          firstStep,
          inCtr,
          inCtr equ C(0)(U32),
          inCtr equ C(1)(U32),
          outCtr
        )(),
        True,
        Map[Param, (Expr, Expr)](
          inCtr -> (
            C(0)(U32),
            Mux(
              shouldReset,
              C(0)(U32),
              Mux(firstStep, Sum(C(1)(U32), inCtr)(), inCtr)()
            )()
          ),
          firstStep -> (
            True,
            shouldReset
          ),
          outCtr -> (
            C(0)(U32),
            Mux(shouldReset, C(0)(U32), Sum(C(1)(U32), outCtr)())()
          )
        )
      )().tchk()
    }
    val simplified = simplifier.simplify(original)

    // Correct behaviour
    val originalVal = mhir.ir.eval(original)
    val simplifiedVal = mhir.ir.eval(simplified)
    assert(simplifiedVal == originalVal)

    // Effective simplification
    val expected = {
      val firstStep = Param("is_first_step")(TyBool)
      val u2 = TyUInt(2)
      val outCtr = Param("out_ctr")(u2)
      StmBuild(
        n,
        Tuple(
          firstStep,
          Mux(firstStep, C(0)(U32), C(1)(U32))(),
          firstStep,
          !firstStep,
          PadTo(outCtr, 32)()
        )(),
        True,
        Map[Param, (Expr, Expr)](
          firstStep -> (True, outCtr equ C(2)(u2)),
          outCtr -> (
            C(0)(u2),
            Mux(outCtr equ C(2)(u2), C(0)(u2), Sum(C(1)(u2), outCtr)())()
          )
        )
      )().tchk()
    }
    assert(simplified == expected)
  }
}
