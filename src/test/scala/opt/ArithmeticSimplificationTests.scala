package opt

import ir._
import lift.{arithmetic => ae}
import opt.ArithSimplifier.BlackBox
import org.scalatest.funsuite.AnyFunSuite

class ArithmeticSimplificationTests extends AnyFunSuite {
  private val pe = (e: Expr) => PartialEvalPass.partialEval(e)

  test(s"Sum") {
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
    assert(pe(IntCst(6) * e / IntCst(6)) == e)
    assert(pe((IntCst(6) * e) / IntCst(3)) == 2 * e)
    assert(pe((e * IntCst(15)) / IntCst(5)) == 3 * e)
    assert(pe(IntCst(27) * e / IntCst(6)) == 9 * e / 2)
    assert(pe(e * IntCst(27) / IntCst(6)) == 9 * e / 2)
  }

  test("IfThenElseWithBoundedVariable") {
    val x = Param("x")
    val y = Param("y")
    val z = Param("z")
    val a = Param("a")
    val e = IfThenElse(x.__1 >= -2 + a, y, z)

    val facts0 = FactSet()
    assert(PartialEvalPass.partialEval(e)(facts0) == e)
    val facts1 = FactSet().range(x.__1, ScalarRange(Some(a - 1), None))
    assert(PartialEvalPass.partialEval(e)(facts1) == y)
    val facts2 = FactSet().range(x.__1, ScalarRange(None, Some(a - 2)))
    assert(PartialEvalPass.partialEval(e)(facts2) == z)
  }

  test("LessThanWithBoundedVariable") {
    val a = Param("a")
    val n = Param("n")
    val e = a.__0 >= -1 + n

    val facts0 = FactSet()
    assert(PartialEvalPass.partialEval(e)(facts0) == e)
    val facts1 = FactSet().range(a.__0, ScalarRange(Some(n - 1), None))
    assert(PartialEvalPass.partialEval(e)(facts1) == True)
    val facts2 = FactSet().range(a.__0, ScalarRange(None, Some(n - 1)))
    assert(PartialEvalPass.partialEval(e)(facts2) == False)
  }

  test("SimplifyBranchesOfIfThenElse") {
    val i = IntCst(0)
    val e = IfThenElse(
      (i % 2) === 0,
      Tuple(i, 2 + (2 * i)),
      Tuple(2 + (2 * i), i)
    )
    assert(PartialEvalPass.partialEval(e) == Tuple(0, 2))
  }
}
