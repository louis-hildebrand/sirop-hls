package opt

import org.scalatest.funsuite.AnyFunSuite
import ir._
import operations._

class PartialEvalPassTests extends AnyFunSuite {
  private val pe = (e: Expr) => PartialEvalPass.partialEval(e)

  // Used to debug issue with StmFold
  test("FunCall") {
    val x = Param()
    val e = FunCall(
      (y: Expr) => Tuple(StmNext(y.__1).__1 + y.__0, StmNext(y.__1).__0),
      x
    )
    val expected =
      Tuple(StmNext(x.__1).__1 + x.__0, StmNext(x.__1).__0)
    assert(PartialEvalPass.partialEval(e) == expected)
  }

  // Used to debug a case where partial evaluator left behind a Not(Not(...))
  test("NotNot") {
    val acc = Param()
    val e =
      IfThenElse(
        acc.__3,
        IfThenElse(Not(LessThan(acc.__4, 5)), False, True),
        False
      )
    val expected = acc.__3 && LessThan(acc.__4, 5)
    assert(PartialEvalPass.partialEval(e) == expected)
  }

  test("VecScanUnfolded") {
    val a = Param()
    val b = Param()
    val c = Param()
    val z = Param()
    val v =
      VecBuild(
        3,
        (i: Expr) => IfThenElse(i === 0, a, IfThenElse(i === 1, b, c))
      )
    val v2 = VecScan(v, z, (x: Expr) => (a: Expr) => a + x, inclusive = true)
    val pe = (e: Expr) => PartialEvalPass.partialEval(e)
    assert(pe(VecAccess(v2, 0)) == z + a)
    assert(pe(VecAccess(v2, 1)) == z + a + b)
    assert(pe(VecAccess(v2, 2)) == z + a + b + c)
  }

  test("Mod") {
    val e = VecLength(Param())

    assert(pe(IntCst(17) % IntCst(12)) == IntCst(5))
    assert(pe(DontCare % e) == DontCare)
    assert(pe(e % DontCare) == DontCare)
  }

  test("IfThenElseTrueBranchSpecialCaseOfFalseBranch") {
    val n = Param("n")
    val i = Param("i")
    val acc = Param("acc")
    val z = Param("z")
    val delta = Param("delta")
    val e = IfThenElse(
      i === (n - 1),
      z + acc.__0 * delta,
      z + ((acc.__0 + (i + 1)) - n) * delta
    )
    val actual = PartialEvalPass.partialEval(e)
    val expected = z + delta * acc.__0 + delta * i + delta - delta * n
    assert(actual == expected)
  }

  test("IfThenElseFalseBranchSpecialCaseOfTrueBranch") {
    val n = Param("n")
    val i = Param("i")
    val acc = Param("acc")
    val z = Param("z")
    val delta = Param("delta")
    val e = IfThenElse(
      i !== (n - 1),
      z + ((acc.__0 + (i + 1)) - n) * delta,
      z + acc.__0 * delta
    )
    val expected = z + delta * acc.__0 + delta * i + delta - delta * n
    assert(PartialEvalPass.partialEval(e) == expected)
  }

  test("ScalarInequality:x<x+1") {
    val x = Param()
    assert(pe((x - 1) < x) == True)
    assert(pe(x < x) == False)
  }

  test("ScalarInequalityWithKnownBounds:x>=0") {
    val x = Param()
    val facts = FactSet().range(x, ScalarRange(0, PosInf))
    assert(pe(x >= 0) == x >= 0)
    assert(PartialEvalPass.partialEval(x >= 1)(facts) == x >= 1)
    assert(PartialEvalPass.partialEval(x >= 0)(facts) == True)
    assert(PartialEvalPass.partialEval(x >= -1)(facts) == True)
  }

  test("ScalarInequalityWithKnownBounds:x>8") {
    val x = Param()
    val facts = FactSet().range(x, ScalarRange(8, PosInf))
    assert(PartialEvalPass.partialEval(x > 8) == x > 8)
    assert(PartialEvalPass.partialEval(x > 8)(facts) == x > 8)
    assert(PartialEvalPass.partialEval(x > 7)(facts) == True)
    assert(PartialEvalPass.partialEval(x > 6)(facts) == True)
  }

  test("ScalarInequalityWithKnownBounds:x<=5") {
    val x = Param()
    val facts = FactSet().range(x, ScalarRange(NegInf, 5))
    assert(PartialEvalPass.partialEval(x <= 5) == x <= 5)
    assert(PartialEvalPass.partialEval(x <= 4)(facts) == x <= 4)
    assert(PartialEvalPass.partialEval(x <= 5)(facts) == True)
    assert(PartialEvalPass.partialEval(x <= 6)(facts) == True)
  }

  test("ScalarInequalityWithKnownBounds:x<3") {
    val x = Param()
    val facts = FactSet().range(x, ScalarRange(NegInf, 3))
    assert(PartialEvalPass.partialEval(x < 3) == x < 3)
    assert(PartialEvalPass.partialEval(x < 3)(facts) == x < 3)
    assert(PartialEvalPass.partialEval(x < 4)(facts) == True)
    assert(PartialEvalPass.partialEval(x < 5)(facts) == True)
  }

  test("TupleInequalityWithKnownBounds:acc.__1>=2") {
    val acc = Param()
    val facts =
      FactSet().range(acc, TupleRange(Seq(None, Some(ScalarRange(2, PosInf)))))

    assert(PartialEvalPass.partialEval(acc.__0 >= 2)(facts) == acc.__0 >= 2)
    assert(PartialEvalPass.partialEval(acc.__0 >= 1)(facts) == acc.__0 >= 1)
    assert(PartialEvalPass.partialEval(acc.__0 >= 0)(facts) == acc.__0 >= 0)

    assert(PartialEvalPass.partialEval(acc.__1 >= 2) == acc.__1 >= 2)
    assert(PartialEvalPass.partialEval(acc.__1 >= 3)(facts) == acc.__1 >= 3)
    assert(PartialEvalPass.partialEval(acc.__1 >= 2)(facts) == True)
    assert(PartialEvalPass.partialEval(acc.__1 >= 1)(facts) == True)
  }
}
