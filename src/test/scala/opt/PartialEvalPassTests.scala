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

  test("ReusedParam:FreeAndBoundTupleVar") {
    val x = Param("x")
    val e =
      Tuple(
        x.__0 >= 1,
        FunCall(Function(x, x.__0 >= 1), Tuple(x.__1, x.__0)),
        x.__0 >= 1
      )
    val facts = FactSet().geq(x.__0, 1)
    val actual = PartialEvalPass.partialEval(e)(facts)
    val expected = Tuple(True, x.__1 >= 1, True)
    assert(actual == expected)
  }

  test("ReusedParam:NestedScalarFunctions") {
    val y = Param("y")
    val e =
      Function(
        y,
        IfThenElse(y > 42, Function(y, y > 10), (_: Expr) => y > 45)
      )
    val actual = PartialEvalPass.partialEval(e)
    val expected: Function =
      (y: Expr) => IfThenElse(y > 42, (z: Expr) => z > 10, (_: Expr) => False)
    assert(actual == expected)
  }

  test("ReusedParam:StmAccumulator") {
    val a = Param("a")
    val s = StmBuild(
      10,
      SSome(Tuple(a >= 0, a < 4)),
      Map(a -> (IntCst(0), a + 1))
    )
    val e = Tuple(a < 4, s)
    val facts =
      FactSet()
        // Inside the stream, a >= 0
        .range(s, StmAccRange(Map(a -> ScalarRange(Some(0), None))))
        // Outside the stream, a < 4
        .lt(a, 4)
    val actual = PartialEvalPass.partialEval(e)(facts)
    val expected = Tuple(
      True,
      StmBuild(
        10,
        SSome(Tuple(True, a < 4)),
        Map(a -> (IntCst(0), a + 1))
      )
    )
    assert(actual == expected)
  }

  test("ReusedParam:VecIndex") {
    val i = Param("i")
    val e =
      Tuple(i > 1, VecBuild(7, Function(i, Tuple(i >= 0, i < 7, i > 2))), i > 2)
    val facts = FactSet().geq(i, 3)
    val actual = PartialEvalPass.partialEval(e)(facts)
    val expected =
      Tuple(True, VecBuild(7, (i: Expr) => Tuple(True, True, i > 2)), True)
    assert(actual == expected)
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

  test("StmAccumulatorGreaterOrEqualToInitialVal") {
    val n = Param("n")
    val z = Param("z")
    val s = StmBuild(
      n,
      Tuple(z),
      (acc: Expr) =>
        Tuple(
          Tuple(acc.__0 + 3),
          IfThenElse(acc.__0 >= z, SSome(acc.__0), NNone)
        )
    )
    val facts = FactSet().range(s, StmAccRangeAnalysis.findAccRanges(s))
    val expected = StmBuild(
      n,
      Tuple(z),
      (acc: Expr) => Tuple(Tuple(acc.__0 + 3), SSome(acc.__0))
    )
    assert(PartialEvalPass.partialEval(s)(facts) == expected)
  }

  test("StmOneElement") {
    val z = Param("z")
    val s = StmBuild(
      1,
      Tuple(z, 0),
      (acc: Expr) =>
        Tuple(
          Tuple(acc.__0 + acc.__1 + acc.__1, acc.__1 + acc.__0),
          SSome(acc.__0)
        )
    )
    val expected = StmBuild(1, Tuple(), (_: Expr) => Tuple(Tuple(), SSome(z)))
    assert(PartialEvalPass.partialEval(s) == expected)
  }

  test("StmOneElementNotReducible") {
    // I do NOT want this to be simplified to something like
    //   StmCst(1, StmNext(s).__1)
    // because then we're calling StmNext(s).__1 without a corresponding StmNext(s).__0
    val s = Param("s")
    val stm = StmBuild(
      1,
      Tuple(s),
      (acc: Expr) =>
        Tuple(Tuple(StmNext(acc.__0).__0), SSome(StmNext(acc.__0).__1))
    )
    assert(PartialEvalPass.partialEval(stm) == stm)
  }

  test("VecBuildIndexRange") {
    val n = Param("n")
    val v =
      VecBuild(n, (i: Expr) => Tuple(i > -1, i < n + 1, i >= n, i < 0, i > 0))
    val expected =
      VecBuild(n, (i: Expr) => Tuple(True, True, False, False, i > 0))
    assert(PartialEvalPass.partialEval(v) == expected)
  }

  test("IfThenElseCondition:x < 10 && x >= 0") {
    val x = Param("x")
    val e =
      IfThenElse(
        x < 10 && x >= 0,
        Tuple(x < 11, x >= 10, x >= -1, x < 9),
        Tuple(x < 9, x >= 10, x >= 11)
      )
    val expected =
      IfThenElse(
        x < 10 && x >= 0,
        Tuple(True, False, True, x < 9),
        Tuple(x < 9, x >= 10, x >= 11)
      )
    val actual = PartialEvalPass.partialEval(e)
    assert(actual == expected)
  }

  // Used to debug an issue with StmInductionVarRemovalPass
  test("VecBuildIndexRangeAndIfThenElseCondition") {
    val s = Param("s")
    val e = (t: Expr) =>
      IfThenElse(
        t < 7,
        VecBuild(
          7,
          (i: Expr) =>
            StmNext(
              IfThenElse(
                -7 + i + t < 7,
                StmNextK(s, -7 + i + t),
                StmNextK(s, 7)
              )
            ).__1
        ),
        VecBuild(7, (i: Expr) => StmNext(StmNextK(s, i)).__1)
      )
    val expected: Expr = (t: Expr) =>
      IfThenElse(
        t < 7,
        VecBuild(7, (i: Expr) => StmNext(StmNextK(s, -7 + i + t)).__1),
        VecBuild(7, (i: Expr) => StmNext(StmNextK(s, i)).__1)
      )
    val actual = PartialEvalPass.partialEval(e)
    assert(actual == expected)
  }
}
