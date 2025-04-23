package opt

import ir._
import lift.{arithmetic => ae}
import operations.Min
import opt.ArithSimplifier.BlackBox
import org.scalatest.funsuite.AnyFunSuite

class ArithmeticSimplificationTests extends AnyFunSuite {
  private val pe = (e: Expr) => PartialEvalPass.partialEval(e)

  test(s"Sum") {
    val e1 = VecAccess(Param("p"), Param("p"))().__1
    val e2 = StmLength(Param("p"))()

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
    assert(pe(e1 + (e2 - e1)) == e2)
    assert(pe(e2 + (e1 - e2)) == e1)
    assert(pe((e2 - e1) + e1) == e2)
    assert(pe((e1 - e2) + e2) == e1)
    assert(pe((((e1 + IntCst(1)) + e2) - IntCst(1)) - e2) == e1)
  }

  // The non-arithmetic terms within a sum are also simplified
  test("SimplifiableBlackBoxInsideAdd") {
    val i = Param("i")
    val f = Param("f")
    val e3 = VecAccess(FunCall(f, Tuple(0, 10, 20)().__1)(), i)()
    val e4 = Param("p")

    val actual = pe((e3 + e4 + IntCst(42)) - e4)
    val expected = IntCst(42) + VecAccess(FunCall(f, 10)(), i)()
    assert(actual == expected)
  }

  test("Mul") {
    val e = VecAccess(Param("p"), Param("p"))()

    assert(pe(IntCst(4) * IntCst(9)) == IntCst(36))
    assert(pe(e * IntCst(0)) == IntCst(0))
    assert(pe(IntCst(0) * e) == IntCst(0))
    assert(pe(e * IntCst(1)) == e)
    assert(pe(IntCst(1) * e) == e)
  }

  // The non-arithmetic terms within a product are also simplified
  test("SimplifiableBlackBoxInsideMul") {
    val i = Param("i")
    val f = Param("f")
    val e = VecAccess(FunCall(f, Tuple(0, 10, 20)().__1)(), i)()

    val actual = pe((IntCst(42) * e) * IntCst(3))
    val expected = IntCst(126) * VecAccess(FunCall(f, 10)(), i)()
    assert(actual == expected)
  }

  test("Div") {
    val e = StmLength(Param("p"))()

    // TODO: What about simplifying x * y / x if x is non-constant? Might need to check that x != 0 first
    assert(pe(e / IntCst(1)) == e)
    assert(pe(IntCst(6) * e / IntCst(6)) == e)
    assert(pe((IntCst(6) * e) / IntCst(3)) == 2 * e)
    assert(pe((e * IntCst(15)) / IntCst(5)) == 3 * e)
    assert(pe(IntCst(27) * e / IntCst(6)) == 9 * e / 2)
    assert(pe(e * IntCst(27) / IntCst(6)) == 9 * e / 2)
  }

  test("Mod") {
    val e = VecLength(Param("p"))()

    assert(pe(IntCst(17) % IntCst(12)) == IntCst(5))
    assert(pe(IntCst(0) % e) == IntCst(0))
  }

  test("IfThenElseWithBoundedVariable") {
    val x = Param("x")
    val y = Param("y")
    val z = Param("z")
    val a = Param("a")
    val e = IfThenElse(x.__1 >= -2 + a, y, z)()

    val facts0 = FactSet()
    assert(PartialEvalPass.partialEval(e)(facts0) == e)
    val facts1 = FactSet().geq(x.__1, a - 1)
    assert(PartialEvalPass.partialEval(e)(facts1) == y)
    val facts2 = FactSet().lt(x.__1, a - 2)
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
      Tuple(i, 2 + (2 * i))(),
      Tuple(2 + (2 * i), i)()
    )()
    assert(PartialEvalPass.partialEval(e) == Tuple(0, 2)())
  }

  /** The partial evaluator may check whether one branch is a special case of
    * the other. But in some cases, this can lead to division by zero.
    */
  test("PossibleDivByZeroInFalseBranch") {
    val x = Param("x")

    val e0 = IfThenElse(x === 0, 1, 2 / x)()
    assert(PartialEvalPass.partialEval(e0) == e0)

    val e1 = IfThenElse(x === 1, 1, 3 / (1 - x))()
    assert(PartialEvalPass.partialEval(e1) == e1)

    val e2 = IfThenElse(x > 0, 10 / x, 0)()
    assert(PartialEvalPass.partialEval(e2) == e2)
  }

  test("PossibleDivByZeroInTrueBranch") {
    val x = Param("x")

    val e0 = IfThenElse(x !== 0, 2 / x, 1)()
    assert(PartialEvalPass.partialEval(e0) == e0)

    val e1 = IfThenElse(x !== 1, 3 / (1 - x), 1)()
    assert(PartialEvalPass.partialEval(e1) == e1)

    val e2 = IfThenElse(x <= 0, 0, 10 / x)()
    assert(PartialEvalPass.partialEval(e2) == e2)
  }

  test("MinLessThanOrEqualToMin") {
    val t = Param("t")
    val e = Min(-5 + t, 5) <= Min(-4 + t, 5)
    val actual = PartialEvalPass.partialEval(e)
    assert(actual == True)
  }

  test("MinMinusMinGreaterOrEqualToZero") {
    val t = Param("t")
    val e = (Min(-4 + t, 5) - Min(-5 + t, 5)) >= 0
    assert(PartialEvalPass.partialEval(e) == True)
  }

  test("MinMinusMinLessOrEqualToOne") {
    val t = Param("t")
    val e = (Min(-4 + t, 5) - Min(-5 + t, 5)) <= 1
    assert(PartialEvalPass.partialEval(e) == True)
  }

  test("IfThenElse(a < b, True, False) === False") {
    val a = Param("a")
    val b = Param("b")
    val e = IfThenElse(a < b, True, False)() === False
    val actual = pe(e)
    val expected = a >= b
    assert(actual == expected)
  }

  test("1 < 2") {
    assert(pe(1 < 2) == True)
    assert(pe(1 < 1) == False)
  }

  test("ParamMinusOneLessThan") {
    val n = Param("n")
    val b = Param("b")
    val facts = FactSet().geq(n, 1)
    val e = ((-1 + n) >= 0) && b
    val actual = PartialEvalPass.partialEval(e)(facts)
    assert(actual == b)
  }

  test("ParamMinusOneEqual") {
    val n = Param("n")
    val b = Param("b")
    val facts = FactSet().geq(n, 2)
    val e = ((-1 + n) === 0) || b
    val actual = PartialEvalPass.partialEval(e)(facts)
    assert(actual == b)
  }
}
