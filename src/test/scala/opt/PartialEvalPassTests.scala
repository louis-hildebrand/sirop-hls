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
}
