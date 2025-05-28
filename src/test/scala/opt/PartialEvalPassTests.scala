package opt

import org.scalatest.funsuite.AnyFunSuite
import ir._
import operations.{StmCount, StmFold}

class PartialEvalPassTests extends AnyFunSuite {
  private val pe = (e: Expr) => PartialEvalPass.partialEval(e)

  // Used to debug a case where partial evaluator left behind a Not(Not(...))
  test("NotNot") {
    val acc = Param("acc")(TyTuple(TyInt, TyInt, TyInt, TyBool, TyInt))
    val e =
      Mux(
        acc.__3,
        Mux(Not(LessThan(acc.__4, 5)())(), False, True)(),
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
        Mux(y > 42, Function(y, y > 10)(), TyInt ::+ (_ => y > 45))()
      )()
    val actual = PartialEvalPass.partialEval(e)
    val expected = TyInt ::+ (y =>
      Mux(-42 + y > 0, TyInt ::+ (z => -10 + z > 0), TyInt ::+ (_ => False))()
    )
    assert(actual == expected)
  }

  test("ReusedParam:StmAccumulator") {
    val a = Param("a")()
    val s = StmBuild(
      10,
      Tuple(a >= 0, a < 4)(),
      True,
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
        Tuple(True, a < 4)(),
        True,
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
        VecBuild(7, TyInt ::+ (i => Tuple(True, True, 0 < -2 + i)()))(),
        True
      )()
    assert(actual == expected)
  }

  test("MuxTrueBranchSpecialCaseOfFalseBranch") {
    val n = Param("n")()
    val i = Param("i")()
    val acc = Param("acc")()
    val z = Param("z")()
    val delta = Param("delta")()
    val e = Mux(
      i === (n - 1),
      z + acc.__0 * delta,
      z + ((acc.__0 + (i + 1)) - n) * delta
    )()
    val actual = PartialEvalPass.partialEval(e)
    val expected = z + delta * acc.__0 + delta * i + delta - delta * n
    assert(actual == expected)
  }

  test("MuxFalseBranchSpecialCaseOfTrueBranch") {
    val n = Param("n")()
    val i = Param("i")()
    val acc = Param("acc")()
    val z = Param("z")()
    val delta = Param("delta")()
    val e = Mux(
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

  test("ScalarEquality:x+c==k") {
    val x = Param("x")(TyInt)
    assert(pe(x - 1 === 3) == (x === 4))
    assert(pe(5 + x === 2) == (x === -3))
  }

  test("ScalarEquality:x+c<k") {
    val x = Param("x")(TyInt)
    assert(pe(x - 2 < 9) == (x < 11))
    assert(pe(-3 + x < 4) == (x < 7))
  }

  test("ScalarInequality:(x >= c) && (x < c + 1)") {
    val c = 42
    val x = Param("x")(TyInt)
    val e = (x >= c) && (x < (c + 1))
    assert(pe(e) == (x === c))
  }

  test("StmAccumulatorGreaterOrEqualToInitialVal") {
    val n = Param("n")()
    val z = Param("z")()
    val a = Param("a")()
    val s = StmBuild(
      n,
      a,
      a >= z,
      Map[Param, (Expr, Expr)](
        a -> (z, Mux(a >= z, a + 3, a - 1)())
      )
    )()
    val facts = FactSet().range(s, StmAccRangeAnalysis.findAccRanges(s))
    val expected = StmBuild(
      n,
      a,
      True,
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
      a0,
      True,
      Map[Param, (Expr, Expr)](
        a0 -> (z, a0 + a1 + a1),
        a1 -> (0, a1 + a0)
      )
    )().tchk().lower()
    val expected = StmBuild(1, z, True)()
    assert(PartialEvalPass.partialEval(s) == expected)
  }

  test("StmOneElementWithInputs") {
    val n = 5
    val z = Param("z")(TyInt)
    val i = Param("i")(TyInt)
    val a = Param("a")(TyInt)
    val s = Param("s")()
    val sum = StmBuild(
      1,
      a + StmData(s)(),
      i === n - 1,
      Map[Param, (Expr, Expr)](
        i -> (0, i + 1),
        a -> (z, a + StmData(s)()),
        s -> (StmCount(n)(), True)
      )
    )().tchk().lower()
    val expected = StmBuild(1, z + (0 until n).sum, True)().tchk().lower()
    val actual = PartialEvalPass.partialEval(sum).tchk().lower()
    assert(actual == expected)
  }

  test("StmOneElementNotReducible") {
    // I do NOT want this to be simplified to something like
    //   StmCst(1, StmData(s)())
    // because then we're calling StmData(s)() outside a stream
    val s = Param("s")()
    val a = Param("a")()
    val stm = StmBuild(
      1,
      StmData(a)(),
      True,
      Map[Param, (Expr, Expr)](
        a -> (s, True)
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

  test("MuxCondition:x < 10 && x >= 0") {
    val x = Param("x")()
    val e =
      Mux(
        x < 10 && x >= 0,
        Tuple(x < 11, x >= 10, x >= -1, x < 9)(),
        Tuple(x < 9, x >= 10, x >= 11)()
      )()
    val expected =
      Mux(
        x < 10 && x >= 0,
        Tuple(True, False, True, x < 9)(),
        Tuple(x < 9, x >= 10, x >= 11)()
      )()
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
    val e = TyInt ::+ (i => Mux(c0, i + 1, i)())
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

  test("FuseVecAccessOutOfBounds") {
    val e = VecAccess(VecBuild(5, TyInt ::+ (i => i))(), 10)()
    val actual = PartialEvalPass.partialEval(e)
    assert(ir.eval(actual) == ir.eval(e))
  }

  test("NestedMux") {
    val i = Param("i")()
    val n = Param("n")()
    val c0 = Param("c0")()
    val c1 = Param("c1")()
    val c2 = Param("c2")()
    val e = Mux(i === -1 + n, c0, Mux(1 + i < n, c1, c2)())()

    val actual0 = PartialEvalPass.partialEval(e)(FactSet())
    val expected0 = Mux(i === -1 + n, c0, Mux(i < -1 + n, c1, c2)())()
    assert(actual0 == expected0)

    val facts = FactSet().geq(i, 0).lt(i, n)
    val expected = Mux(i === -1 + n, c0, c1)()
    val actual1 = PartialEvalPass.partialEval(e)(facts)
    assert(actual1 == expected)
  }
}
