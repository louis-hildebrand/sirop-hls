package opt

import org.scalatest.funsuite.AnyFunSuite
import ir._

class PartialEvalPassTests extends AnyFunSuite {
  private val pe = (e: Expr) => PartialEvalPass.partialEval(e)

  // Used to debug issue with StmFold
  test("FunCall") {
    val ttup = TyTuple(TyInt, TyStm(TyInt, 5))
    val x = Param("x")(ttup)
    val e = FunCall(
      ttup ::+ (y =>
        Tuple(StmNext(y.__1)().__1 + y.__0, StmNext(y.__1)().__0)()
      ),
      x
    )()
    val expected =
      Tuple(StmNext(x.__1)().__1 + x.__0, StmNext(x.__1)().__0)()
    assert(PartialEvalPass.partialEval(e) == expected)
  }

  // Used to debug a case where partial evaluator left behind a Not(Not(...))
  test("NotNot") {
    val acc = Param("acc")(TyTuple(TyInt, TyInt, TyInt, TyBool, TyInt))
    val e =
      IfThenElse(
        acc.__3,
        IfThenElse(Not(LessThan(acc.__4, 5)())(), False, True)(),
        False
      )()
    val expected = acc.__3 && LessThan(acc.__4, 5)()
    assert(PartialEvalPass.partialEval(e) == expected)
  }

  test("ReusedParam:FreeAndBoundTupleVar") {
    val x = Param("x")(TyTuple(TyInt, TyInt))
    val e =
      Tuple(
        x.__0 >= 1,
        FunCall(
          Function(x, x.__0 >= 1)(),
          Tuple(x.__1, x.__0)()
        )(),
        x.__0 >= 1
      )()
    val facts = FactSet().geq(x.__0, 1)
    val actual = PartialEvalPass.partialEval(e)(facts)
    val expected = Tuple(True, x.__1 >= 1, True)()
    assert(actual == expected)
  }

  test("ReusedParam:NestedScalarFunctions") {
    val y = Param("y")(TyInt)
    val e =
      Function(
        y,
        IfThenElse(y > 42, Function(y, y > 10)(), TyInt ::+ (_ => y > 45))()
      )()
    val actual = PartialEvalPass.partialEval(e)
    val expected = TyInt ::+ (y =>
      IfThenElse(y > 42, TyInt ::+ (z => z > 10), TyInt ::+ (_ => False))()
    )
    assert(actual == expected)
  }

  test("ReusedParam:StmAccumulator") {
    val a = Param("a")()
    val s = StmBuild(
      10,
      SSome(Tuple(a >= 0, a < 4)())(),
      Map(a -> (IntCst(0), a + 1))
    )()
    val e = Tuple(a < 4, s)()
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
        SSome(Tuple(True, a < 4)())(),
        Map(a -> (IntCst(0), a + 1))
      )()
    )()
    assert(actual == expected)
  }

  test("ReusedParam:VecIndex") {
    val i = Param("i")(TyInt)
    val e =
      Tuple(
        i > 1,
        VecBuild(7, Function(i, Tuple(i >= 0, i < 7, i > 2)())())(),
        i > 2
      )()
    val facts = FactSet().geq(i, 3)
    val actual = PartialEvalPass.partialEval(e)(facts)
    val expected =
      Tuple(
        True,
        VecBuild(7, TyInt ::+ (i => Tuple(True, True, i > 2)()))(),
        True
      )()
    assert(actual == expected)
  }

  test("IfThenElseTrueBranchSpecialCaseOfFalseBranch") {
    val n = Param("n")()
    val i = Param("i")()
    val acc = Param("acc")()
    val z = Param("z")()
    val delta = Param("delta")()
    val e = IfThenElse(
      i === (n - 1),
      z + acc.__0 * delta,
      z + ((acc.__0 + (i + 1)) - n) * delta
    )()
    val actual = PartialEvalPass.partialEval(e)
    val expected = z + delta * acc.__0 + delta * i + delta - delta * n
    assert(actual == expected)
  }

  test("IfThenElseFalseBranchSpecialCaseOfTrueBranch") {
    val n = Param("n")()
    val i = Param("i")()
    val acc = Param("acc")()
    val z = Param("z")()
    val delta = Param("delta")()
    val e = IfThenElse(
      i !== (n - 1),
      z + ((acc.__0 + (i + 1)) - n) * delta,
      z + acc.__0 * delta
    )()
    val expected = z + delta * acc.__0 + delta * i + delta - delta * n
    assert(PartialEvalPass.partialEval(e) == expected)
  }

  test("ScalarInequality:x<x+1") {
    val x = Param("x")()
    assert(pe((x - 1) < x) == True)
    assert(pe(x < x) == False)
  }

  test("StmAccumulatorGreaterOrEqualToInitialVal") {
    val n = Param("n")()
    val z = Param("z")()
    val a = Param("a")()
    val s = StmBuild(
      n,
      IfThenElse(a >= z, SSome(a)(), NNone(TyInt))(),
      Map[Param, (Expr, Expr)](
        a -> (z, IfThenElse(a >= z, a + 3, a - 1)())
      )
    )()
    val facts = FactSet().range(s, StmAccRangeAnalysis.findAccRanges(s))
    val expected = StmBuild(
      n,
      SSome(a)(),
      Map[Param, (Expr, Expr)](
        a -> (z, a + 3)
      )
    )()
    assert(PartialEvalPass.partialEval(s)(facts) == expected)
  }

  test("StmOneElement") {
    val z = Param("z")(TyInt)
    val a0 = Param("a")()
    val a1 = Param("a")()
    val s = StmBuild(
      1,
      SSome(a0)(),
      Map[Param, (Expr, Expr)](
        a0 -> (z, a0 + a1 + a1),
        a1 -> (0, a1 + a0)
      )
    )().tchk().lower()
    val expected = StmBuild(1, SSome(z)())()
    assert(PartialEvalPass.partialEval(s) == expected)
  }

  test("StmOneElementNotReducible") {
    // I do NOT want this to be simplified to something like
    //   StmCst(1, StmNext(s)().__1)
    // because then we're calling StmNext(s)().__1 without a corresponding StmNext(s)().__0
    val s = Param("s")()
    val a = Param("a")()
    val stm = StmBuild(
      1,
      SSome(StmNext(a)().__1)(),
      Map[Param, (Expr, Expr)](
        a -> (s, StmNext(a)().__0)
      )
    )()
    assert(PartialEvalPass.partialEval(stm) == stm)
  }

  test("VecBuildIndexRange") {
    val n = Param("n")()
    val v =
      VecBuild(
        n,
        TyInt ::+ (i => Tuple(i > -1, i < n + 1, i >= n, i < 0, i > 0)())
      )()
    val expected =
      VecBuild(n, TyInt ::+ (i => Tuple(True, True, False, False, i > 0)()))()
    assert(PartialEvalPass.partialEval(v) == expected)
  }

  test("IfThenElseCondition:x < 10 && x >= 0") {
    val x = Param("x")()
    val e =
      IfThenElse(
        x < 10 && x >= 0,
        Tuple(x < 11, x >= 10, x >= -1, x < 9)(),
        Tuple(x < 9, x >= 10, x >= 11)()
      )()
    val expected =
      IfThenElse(
        x < 10 && x >= 0,
        Tuple(True, False, True, x < 9)(),
        Tuple(x < 9, x >= 10, x >= 11)()
      )()
    val actual = PartialEvalPass.partialEval(e)
    assert(actual == expected)
  }

  // Used to debug an issue with StmInductionVarRemovalPass
  test("VecBuildIndexRangeAndIfThenElseCondition") {
    val s = Param("s")()
    val e = TyInt ::+ (t =>
      IfThenElse(
        t < 7,
        VecBuild(
          7,
          TyInt ::+ (i =>
            StmNext(
              IfThenElse(
                -7 + i + t < 7,
                StmNextK(s, -7 + i + t)(),
                StmNextK(s, 7)()
              )()
            )().__1
          )
        )(),
        VecBuild(7, TyInt ::+ (i => StmNext(StmNextK(s, i)())().__1))()
      )()
    )
    val expected = TyInt ::+ (t =>
      IfThenElse(
        t < 7,
        VecBuild(
          7,
          TyInt ::+ (i => StmNext(StmNextK(s, -7 + i + t)())().__1)
        )(),
        VecBuild(7, TyInt ::+ (i => StmNext(StmNextK(s, i)())().__1))()
      )()
    )
    val actual = PartialEvalPass.partialEval(e)
    assert(actual == expected)
  }

  test("SmallerOrEqualWithVarRanges") {
    val n = Param("n")(TyInt)
    val i = Param("i")(TyInt)
    val facts = FactSet().geq(n, 2).geq(i, 0).lt(i, n)
    val e = 1 - n + i
    assert(PartialEvalPass.isSmallerOrEqual(e, 0)(facts).contains(true))
    assert(PartialEvalPass.isSmallerOrEqual(e, 0)().isEmpty)
  }

  test("GreaterOrEqualWithVarRanges") {
    val c = Param("c")(TyInt)
    val facts = FactSet().geq(c, 0)
    val delta = 2 - c + 2 * c
    assert(PartialEvalPass.isGreaterOrEqual(delta, 0)(facts).contains(true))
    assert(PartialEvalPass.isGreaterOrEqual(delta, 0)().isEmpty)
  }

  test("ArbitraryConditionIsTrue") {
    val c0 = Param("c0")(TyBool)
    val c1 = Param("c1")(TyBool)
    val facts = FactSet().assumeTrue(c0 && (c0 || c1))
    val e = TyInt ::+ (i => IfThenElse(c0, i + 1, i)())
    val actual = PartialEvalPass.partialEval(e)(facts)
    val expected = TyInt ::+ (i => i + 1)
    assert(actual == expected)
  }

  test("ClearVariableRange") {
    val i = Param("i")(TyInt)
    val facts = FactSet().assumeTrue(i === 0)
    val e = Function(i, i === 0)()
    val actual = PartialEvalPass.partialEval(e)(facts)
    assert(actual == e)
  }
}
