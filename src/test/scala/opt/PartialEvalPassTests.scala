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

  // The partial evaluator can perform algebraic simplification of sums
  test(s"Add") {
    val e1 = VecAccess(Param(), Param()).__1
    val e2 = StmLength(Param())

    assert(pe(IntCst(1) + IntCst(2)) == IntCst(3))
    assert(pe(e1 + IntCst(0)) == e1)
    assert(pe(IntCst(0) + e1) == e1)
    assert(pe(e1 - e1) == IntCst(0))
    assert(pe(e1 - 2 * e1) == -1 * e1)
    assert(pe(3 * e1 + 4 * e1) == 7 * e1)
    assert(pe((e1 + IntCst(9)) + IntCst(3)) == IntCst(12) + e1)
    assert(pe((IntCst(8) + e1) + IntCst(3)) == IntCst(11) + e1)
    assert(pe(IntCst(3) + (e1 + IntCst(4))) == IntCst(7) + e1)
    assert(pe(IntCst(2) + (IntCst(1) + e1)) == IntCst(3) + e1)
    assert(pe((e1 - IntCst(9)) + IntCst(3)) == IntCst(-6) + e1)
    assert(pe((IntCst(8) - e1) + IntCst(3)) == IntCst(11) - e1)
    assert(pe(IntCst(3) + (e1 - IntCst(4))) == IntCst(-1) + e1)
    assert(pe(IntCst(2) + (IntCst(1) - e1)) == IntCst(3) - e1)
    assert(pe(e1 + DontCare) == DontCare)
    assert(pe(DontCare + e1) == DontCare)
    assert(pe(e1 + (e2 - e1)) == e2)
    assert(pe(e2 + (e1 - e2)) == e1)
    assert(pe((e2 - e1) + e1) == e2)
    assert(pe((e1 - e2) + e2) == e1)
    assert(pe((((e1 + IntCst(1)) + e2) - IntCst(1)) - e2) == e1)
  }

  // The non-arithmetic terms within a sum are also simplified
  test("SimplifiableBlackBoxInsideAdd") {
    val i = Param()
    val e3 =
      TupleAccess(Tuple(VecAccess(VecBuild(5, (i: Expr) => i * i), 3), 2, 3), i)
    val e4 = Param()

    val actual = pe((e3 + e4 + IntCst(42)) - e4)
    val expected = IntCst(42) + TupleAccess(Tuple(9, 2, 3), i)
    assert(actual == expected)
  }

  // The partial evaluator can perform algebraic simplification of products
  test("Mul") {
    val e = VecAccess(Param(), Param())

    assert(pe(IntCst(4) * IntCst(9)) == IntCst(36))
    assert(pe(e * IntCst(0)) == IntCst(0))
    assert(pe(IntCst(0) * e) == IntCst(0))
    assert(pe(e * IntCst(1)) == e)
    assert(pe(IntCst(1) * e) == e)
    assert(pe(e * DontCare) == DontCare)
    assert(pe(DontCare * e) == DontCare)
  }

  // The non-arithmetic terms within a product are also simplified
  test("SimplifiableBlackBoxInsideMul") {
    val i = Param()
    val e =
      TupleAccess(Tuple(VecAccess(VecBuild(5, (i: Expr) => i * i), 3), 2, 3), i)

    val actual = pe((IntCst(42) * e) * IntCst(3))
    val expected = IntCst(126) * TupleAccess(Tuple(9, 2, 3), i)
    assert(actual == expected)
  }

  test("Div") {
    val e = StmLength(Param())

    assert(pe(e / DontCare) == DontCare)
    assert(pe(DontCare / e) == DontCare)
    // TODO: What about simplifying x * y / x if x is non-constant? Might need to check that x != 0 first
    assert(pe(e / IntCst(1)) == e)
    assert(pe(e / IntCst(-1)) == -1 * e)
    assert(pe(IntCst(6) * e / IntCst(6)) == e)
    assert(pe((IntCst(6) * e) / IntCst(3)) == 2 * e)
    assert(pe((e * IntCst(15)) / IntCst(5)) == 3 * e)
    assert(pe(IntCst(27) * e / IntCst(6)) == 9 * e / 2)
    assert(pe(e * IntCst(27) / IntCst(6)) == 9 * e / 2)
  }

  test("Mod") {
    val e = VecLength(Param())

    assert(pe(IntCst(17) % IntCst(12)) == IntCst(5))
    assert(pe(DontCare % e) == DontCare)
    assert(pe(e % DontCare) == DontCare)
  }

  test("IfThenElseTrueBranchSpecialCaseOfFalseBranch") {
    val n = Param()
    val i = Param()
    val acc = Param()
    val z = Param()
    val delta = Param()
    val e = IfThenElse(
      i === (n - 1),
      z + acc.__0 * delta,
      z + ((acc.__0 + (i + 1)) - n) * delta
    )
    val actual = PartialEvalPass.partialEval(e)
    val expected = z + ((acc.__0 + (i + 1)) - n) * delta
    assert(actual == expected)
  }

  test("IfThenElseFalseBranchSpecialCaseOfTrueBranch") {
    val n = Param()
    val i = Param()
    val acc = Param()
    val z = Param()
    val delta = Param()
    val e = IfThenElse(
      i !== (n - 1),
      z + ((acc.__0 + (i + 1)) - n) * delta,
      z + acc.__0 * delta
    )
    val expected = z + ((acc.__0 + (i + 1)) - n) * delta
    assert(PartialEvalPass.partialEval(e) == expected)
  }

  test("ScalarInequality:x<x+1") {
    val x = Param()
    assert(PartialEvalPass.partialEval((x - 1) < x) == True)
    assert(PartialEvalPass.partialEval(x < x) == False)
  }

  test("ScalarInequalityWithKnownBounds:x>=0") {
    val x = Param()
    val facts = FactSet().range(x, ScalarRange(0, PosInf))
    assert(PartialEvalPass.partialEval(x >= 0) == x >= 0)
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
