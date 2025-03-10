package opt

import ir._
import operations._
import org.scalatest.funsuite.AnyFunSuite

private trait Eqn {
  def eval(tMin: Int, iterations: Int): Seq[Expr]
}
private case class RecEqn(z: Expr, f: Expr) extends Eqn {
  override def eval(tMin: Int, iterations: Int): Seq[Expr] = {
    if (iterations <= 0) {
      Seq()
    } else {
      val tail =
        RecEqn(FunCall(FunCall(f, tMin), z), f).eval(tMin + 1, iterations - 1)
      ir.eval(z) +: tail
    }
  }
}
private case class NonRecEqn(f: Expr) extends Eqn {
  override def eval(tMin: Int, iterations: Int): Seq[Expr] = {
    (tMin until (tMin + iterations)).map(t => ir.eval(FunCall(f, t)))
  }
}

class StmInductionVarRemovalPassTests extends AnyFunSuite {
  private def assertEquationsEqual(
      expected: Eqn,
      actual: Eqn,
      tMin: Int,
      iterations: Int
  ): Unit = {
    require(iterations > 0)
    val expectedVals = expected.eval(tMin, iterations)
    val actualVals = actual.eval(tMin, iterations)
    assert(
      expectedVals
        .zip(actualVals)
        .forall({ case (a, b) => a == DontCare || a == b })
    )
  }

  test("Counters") {
    val n = Param("n")
    val delta = Param("delta")
    val s = StmBuild(
      n,
      Tuple(
        3 /* up counter (+1) */, 10 /* up counter (+4) */,
        19 /* down counter (-2) */, 0 /* up counter (--3) */,
        -2 /* down counter (+-6) */, 1 /* counter (+ delta + 1) */
      ),
      (acc: Expr) =>
        Tuple(
          Tuple(
            acc.__0 + 1,
            acc.__1 + 4,
            acc.__2 - 2,
            acc.__3 - IntCst(-3),
            acc.__4 + (-6),
            acc.__5 + delta + 1
          ),
          SSome(
            Tuple(2 * acc.__0, acc.__1 / 3, acc.__2, acc.__3, acc.__4, acc.__5)
          )
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // Correctness
    for (nVal <- 0 to 15) {
      for (deltaVal <- Seq(-42, 0, 42)) {
        val expected = Let(n, nVal, Let(delta, deltaVal, s))
        val actual = Let(n, nVal, Let(delta, deltaVal, opt))
        assert(ir.eval(expected) == ir.eval(actual))
      }
    }

    // Effective simplification
    val ideal =
      StmBuild(
        n,
        Tuple(0),
        (acc: Expr) =>
          Tuple(
            Tuple(acc.__0 + 1),
            SSome(
              Tuple(
                6 + 2 * acc.__0,
                3 + (1 + acc.__0 * 4) / 3,
                19 + (-2) * acc.__0,
                acc.__0 * 3,
                -2 + -6 * acc.__0,
                1 + delta * acc.__0 + acc.__0
              )
            )
          )
      )
    assert(opt == ideal)
  }

  test("NotCounter:TriangleSum") {
    val n = Param("n")
    val triangleSum = StmBuild(
      1,
      Tuple(0 /* i */, 0 /* sum of i's */, 0 /* t */ ),
      (acc: Expr) =>
        Tuple(
          Tuple(acc.__0 + 1, acc.__0 + acc.__1, acc.__2 + 1),
          IfThenElse(acc.__2 >= n, SSome(acc.__1), NNone)
        )
    )
    val optimized = StmInductionVarRemovalPass.removeInductionVars(triangleSum)

    // Correctness
    for (nVal <- 0 to 10) {
      val actual = Let(n, nVal, optimized)
      val expected = Let(n, nVal, triangleSum)
      assert(ir.eval(actual) == ir.eval(expected))
    }

    // One counter can be removed, but not the sum
    val ideal = StmCanonPass.canonicalize(
      StmBuild(
        1,
        Tuple(0, 0),
        (acc: Expr) =>
          Tuple(
            Tuple(acc.__0 + acc.__1, acc.__1 + 1),
            IfThenElse(acc.__1 >= n, SSome(acc.__0), NNone)
          ),
      )
    )
    assert(optimized == ideal)
  }

  test("VecShiftLeft") {
    val n = Param()
    val m = Param()
    val s = StmBuild(
      n,
      Tuple(100, VecBuild(m, (i: Expr) => i)),
      (acc: Expr) =>
        Tuple(
          Tuple(acc.__0 + 2, VecShiftLeft(acc.__1, acc.__0)),
          SSome(VecShiftLeft(acc.__1, acc.__0))
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // Correctness
    val actual =
      (nVal: Int, mVal: Int) => ir.eval(Let(n, nVal, Let(m, mVal, opt)))
    assert(actual(1, 2) == StmLiteral(VecLiteral.ints(1, 100)))
    assert(
      actual(4, 3) == StmLiteral(
        VecLiteral(1, 2, 100),
        VecLiteral(2, 100, 102),
        VecLiteral(100, 102, 104),
        VecLiteral(102, 104, 106)
      )
    )

    // Effective simplification
    val ideal =
      StmBuild(
        n,
        Tuple(0),
        (acc: Expr) =>
          Tuple(
            Tuple(acc.__0 + 1),
            SSome(
              PartialEvalPass.partialEval(
                VecShiftLeft(
                  VecBuild(
                    m,
                    (i: Expr) =>
                      IfThenElse(
                        acc.__0 + i < m,
                        acc.__0 + i,
                        100 + (acc.__0 + i - m) * 2
                      )
                  ),
                  100 + acc.__0 * 2
                )
              )
            )
          )
      )
    assert(opt == ideal)
  }

  test("MonotonicBool:SimpleCounter") {
    val n = Param("n")
    val i0 = Param("i0")
    val k0 = Param("k0")
    val k1 = Param("k1")
    // TODO: Run multiple tests for different values of delta?
    val delta = 3
    val s = StmBuild(
      n,
      Tuple(i0, True, True),
      (acc: Expr) =>
        Tuple(
          Tuple(
            acc.__0 + delta,
            acc.__1 && (acc.__0 < k0),
            acc.__2 && (acc.__0 < k1)
          ),
          IfThenElse(
            Not(acc.__1) && (acc.__0 % 2 === 0),
            SSome(Tuple(acc.__0, acc.__1, acc.__2)),
            NNone
          )
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // Correctness
    for (nVal <- (0 to 2) :+ 5) {
      for (i0Val <- -2 to 2) {
        for (k0Val <- -2 to 2) {
          for (k1Val <- -2 to 2) {
            val expected =
              Let(n, nVal, Let(i0, i0Val, Let(k0, k0Val, Let(k1, k1Val, s))))
            val actual =
              Let(n, nVal, Let(i0, i0Val, Let(k0, k0Val, Let(k1, k1Val, opt))))
            assert(
              ir.eval(actual) == ir.eval(expected),
              s"(for n = ${nVal}, i0 = ${i0Val}, k0 = ${k0Val}, k1 = ${k1Val})"
            )
          }
        }
      }
    }

    // Effective simplification
    // There should only be one accumulator left representing t
    assert(opt.seed == Tuple(0))
    val expectedNextAcc: Expr = (acc: Expr) => acc.__0 + 1
    val actualNextAcc = Function(
      opt.nextF.param,
      PartialEvalPass.partialEval(opt.nextF.body.__0.__0)
    )
    assert(actualNextAcc == expectedNextAcc)
  }

  test("MonotonicBool:BoundedCounter") {
    val n = Param("n")
    val i0 = Param("i0")
    val k = Param("k")
    val delta = 4
    val s = StmBuild(
      n,
      Tuple(i0, True),
      (acc: Expr) =>
        Tuple(
          Tuple(
            IfThenElse(acc.__1, acc.__0 + delta, acc.__0),
            acc.__1 && (acc.__0 < k)
          ),
          SSome(Tuple(acc.__0, acc.__1))
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // Correctness
    for (nVal <- (0 to 2) :+ 5) {
      for (i0Val <- -2 to 2) {
        for (kVal <- -2 to 2) {
          val expected =
            Let(n, nVal, Let(i0, i0Val, Let(k, kVal, s)))
          val actual =
            Let(n, nVal, Let(i0, i0Val, Let(k, kVal, opt)))
          assert(
            ir.eval(actual) == ir.eval(expected),
            s"(for n = ${nVal}, i0 = ${i0Val}, k = ${kVal})"
          )
        }
      }
    }

    // Effective simplification
    // There should only be one accumulator left representing t
    assert(opt.seed == Tuple(0))
    val expectedNextAcc: Expr = (acc: Expr) => acc.__0 + 1
    val actualNextAcc = Function(
      opt.nextF.param,
      PartialEvalPass.partialEval(opt.nextF.body.__0.__0)
    )
    assert(actualNextAcc == expectedNextAcc)
  }

  // Counters that count up but stop at a certain point
  test("PiecewiseCounter:StopCounting") {
    val n = Param("n")
    val k = Param("k")
    val s = StmBuild(
      n,
      Tuple(0, 1, 2),
      (acc: Expr) =>
        Tuple(
          Tuple(
            acc.__0 + 1,
            IfThenElse(acc.__0 < k + 1, acc.__1 + 1, acc.__1),
            IfThenElse(acc.__0 <= -1 + k, acc.__2 + 2, acc.__2)
          ),
          SSome(Tuple(acc.__1 + 2, acc.__2))
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // Correctness
    for (nVal <- Seq(0, 1, 2, 5)) {
      for (kVal <- Seq(-10, -2, -1, 0, 10)) {
        val expected = Let(n, nVal, Let(k, kVal, s))
        val actual = Let(n, nVal, Let(k, kVal, opt))
        assert(
          ir.eval(actual) == ir.eval(expected),
          s"(for n = $nVal, k = $kVal)"
        )
      }
    }

    // Effective simplification
    // There should only be one accumulator left representing t
    assert(opt.seed == Tuple(0))
    val expectedNextAcc: Expr = (acc: Expr) => acc.__0 + 1
    val actualNextAcc = Function(
      opt.nextF.param,
      PartialEvalPass.partialEval(opt.nextF.body.__0.__0)
    )
    assert(actualNextAcc == expectedNextAcc)
  }

  // Counters that stay constant at first and only start counting later
  test("PiecewiseCounter:Delayed") {
    val n = Param("n")
    val k = Param("k")
    val s = StmBuild(
      n,
      Tuple(0, 2, 1),
      (acc: Expr) =>
        Tuple(
          Tuple(
            acc.__0 + 1,
            IfThenElse(acc.__0 >= k + 2, acc.__1 + 1, acc.__1),
            IfThenElse(acc.__0 > k, acc.__2 + 2, acc.__2)
          ),
          SSome(Tuple(3 * acc.__1, 2 * acc.__2))
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // Correctness
    for (nVal <- Seq(0, 1, 2, 5)) {
      for (kVal <- Seq(-10, -3, -2, -1, 10)) {
        val expected = Let(n, nVal, Let(k, kVal, s))
        val actual = Let(n, nVal, Let(k, kVal, opt))
        assert(
          ir.eval(actual) == ir.eval(expected),
          s"(for n = $nVal, k = $kVal)"
        )
      }
    }

    // Effective simplification
    // There should only be one accumulator left representing t
    assert(opt.seed == Tuple(0))
    val expectedNextAcc: Expr = (acc: Expr) => acc.__0 + 1
    val actualNextAcc = Function(
      opt.nextF.param,
      PartialEvalPass.partialEval(opt.nextF.body.__0.__0)
    )
    assert(actualNextAcc == expectedNextAcc)
  }

  // Piecewise functions where each branch is yet another piecewise function
  test("PiecewiseCounter:UpThenDown") {
    val n = Param("n")
    val s = StmBuild(
      n,
      Tuple(10, 2),
      (acc: Expr) =>
        Tuple(
          Tuple(
            acc.__0 + 1,
            IfThenElse(
              acc.__0 < 20,
              IfThenElse(acc.__0 < 17, acc.__1 + 2, acc.__1),
              IfThenElse(acc.__0 < 34, acc.__1 - 3, acc.__1)
            )
          ),
          SSome(2 * acc.__1)
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // Correctness
    for (nVal <- Seq(0, 1, 2, 5)) {
      val expected = Let(n, nVal, s)
      val actual = Let(n, nVal, opt)
      assert(ir.eval(actual) == ir.eval(expected))
    }

    // Effective simplification
    // There should only be one accumulator left representing t
    assert(opt.seed == Tuple(0))
    val expectedNextAcc: Expr = (acc: Expr) => acc.__0 + 1
    val actualNextAcc = Function(
      opt.nextF.param,
      PartialEvalPass.partialEval(opt.nextF.body.__0.__0)
    )
    assert(actualNextAcc == expectedNextAcc)
  }

  // Shift register that stops at a certain point
  test("PiecewiseVecShiftLeft:StopShifting") {
    val n = Param("n")
    val m = Param("m")
    val s = StmBuild(
      n,
      Tuple(2, VecBuild(m, (i: Expr) => i)),
      (acc: Expr) =>
        Tuple(
          Tuple(
            acc.__0 + 1,
            IfThenElse(acc.__0 < m, VecShiftLeft(acc.__1, 2 * acc.__0), acc.__1)
          ),
          SSome(acc.__1)
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // Correctness
    for (nVal <- Seq(0, 1, 2, 5)) {
      for (mVal <- Seq(0, 1, 2, 5)) {
        val expected = Let(n, nVal, Let(m, mVal, s))
        val actual = Let(n, nVal, Let(m, mVal, opt))
        assert(ir.eval(actual) == ir.eval(expected))
      }
    }

    // Effective simplification
    // There should only be one accumulator left representing t
    assert(opt.seed == Tuple(0))
    val expectedNextAcc: Expr = (acc: Expr) => acc.__0 + 1
    val actualNextAcc = Function(
      opt.nextF.param,
      PartialEvalPass.partialEval(opt.nextF.body.__0.__0)
    )
    assert(actualNextAcc == expectedNextAcc)
  }

  // Shift register that only starts shifting after some delay
  test("PiecewiseVecShiftLeft:Delayed") {
    val n = Param("n")
    val m = Param("m")
    val s = StmBuild(
      n,
      Tuple(7, VecBuild(m, (i: Expr) => i)),
      (acc: Expr) =>
        Tuple(
          Tuple(
            acc.__0 + 1,
            IfThenElse(acc.__0 < m, acc.__1, VecShiftLeft(acc.__1, 2 * acc.__0))
          ),
          SSome(acc.__1)
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // Correctness
    for (nVal <- Seq(0, 1, 2, 5)) {
      for (mVal <- Seq(0, 1, 2, 5)) {
        val expected = Let(n, nVal, Let(m, mVal, s))
        val actual = Let(n, nVal, Let(m, mVal, opt))
        assert(ir.eval(actual) == ir.eval(expected))
      }
    }

    // Effective simplification
    // There should only be one accumulator left representing t
    assert(opt.seed == Tuple(0))
    val expectedNextAcc: Expr = (acc: Expr) => acc.__0 + 1
    val actualNextAcc = Function(
      opt.nextF.param,
      PartialEvalPass.partialEval(opt.nextF.body.__0.__0)
    )
    assert(actualNextAcc == expectedNextAcc)
  }

  test("ClosedForm:StmNext") {
    val s = Param("s")
    val t0 = 0
    val n = 10
    val recEqn =
      (t: Expr) => (x: Expr) => IfThenElse(t < n, StmNext(x).__0, x)
    val result = StmInductionVarRemovalPass.tryFindClosedForm(t0, s, recEqn)

    assert(result.isDefined)
    val f = result.get

    // Smoke test
    val stmExamples = Seq(
      StmCst(n, Tuple(True, False)),
      StmRange(n, 9, 8)
    )
    for (sVal <- stmExamples) {
      assertEquationsEqual(
        RecEqn(sVal, recEqn),
        NonRecEqn(Let(s, sVal, f)),
        t0,
        // Check a few steps after we stop reading from the input stream
        n + 2
      )
    }

    // Effective simplification
    val expected: Function = (t: Expr) => StmNextK(s, IfThenElse(t < 10, t, 10))
    assert(PartialEvalPass.partialEval(f) == expected)
  }

  test("ClosedForm:ShiftRegisterWithStmNext") {
    val n = 7
    val t0 = 0
    val s = Param("s")
    val stmNextRecEqn =
      (t: Expr) => (x: Expr) => IfThenElse(t < n, StmNext(x).__0, x)
    val stmNextFun =
      StmInductionVarRemovalPass
        .tryFindClosedForm(t0, s, stmNextRecEqn)
        .map(f => PartialEvalPass.partialEval(f))
        .get

    val initialVec = VecBuild(n, (_: Expr) => DontCare)
    val shiftRecEqn = (t: Expr) =>
      (x: Expr) =>
        IfThenElse(
          t < n,
          VecShiftLeft(x, StmNext(FunCall(stmNextFun, t)).__1),
          x
        )
    val shiftEqn =
      StmInductionVarRemovalPass.tryFindClosedForm(0, initialVec, shiftRecEqn)

    assert(shiftEqn.isDefined)
    val f = PartialEvalPass.partialEval(shiftEqn.get)(FactSet())

    // Smoke test
    val stmExamples = Seq(
      StmCst(n, Tuple(True, False)),
      StmRange(n, 9, 8)
    )
    for (sVal <- stmExamples) {
      assertEquationsEqual(
        RecEqn(initialVec, Let(s, sVal, shiftRecEqn)),
        NonRecEqn(Let(s, sVal, f)),
        t0,
        // Check a few steps after the shift register freezes
        n + 2
      )
    }

    // Effective simplification
    val expected: Function = (t: Expr) =>
      IfThenElse(
        t < n,
        VecBuild(n, (i: Expr) => StmNext(StmNextK(s, -n + t + i)).__1),
        VecBuild(n, (i: Expr) => StmNext(StmNextK(s, i)).__1)
      )
    assert(f == expected)
  }

  test("RecursiveForm:StmNextK(s, t)") {
    val t = Param("t")
    val s = Param("s")
    val e = StmNextK(s, t)
    val actual =
      StmInductionVarRemovalPass.tryFindRecursiveForm(e, t = t)

    assert(actual.isDefined)
    val (z, f) = actual.get match {
      case (z, Function(t, e)) =>
        (
          PartialEvalPass.partialEval(z),
          Function(
            t,
            PartialEvalPass.partialEval(e)(
              FactSet().range(t, ScalarRange(Some(0), None))
            )
          )
        )
    }

    // Smoke test
    val n = 3
    val stmExamples = Seq(
      StmCst(n, Tuple(True, False)),
      StmRange(n, 9, 8)
    )
    for (sVal <- stmExamples) {
      assertEquationsEqual(
        NonRecEqn(Let(s, sVal, Function(t, e))),
        RecEqn(Let(s, sVal, z), f),
        tMin = 0,
        iterations = n
      )
    }

    assert(z == s)
    val expectedF: Function = (_: Expr) => (x: Expr) => StmNext(x).__0
    assert(f == expectedF)
  }

  test("RecursiveForm:StmNextK(s, Min(-5 + t, 5))") {
    val t = Param("t")
    val s = Param("s")
    val e = StmNextK(s, Min(-5 + t, 5))
    val actual =
      StmInductionVarRemovalPass.tryFindRecursiveForm(e, t = t)

    assert(actual.isDefined)
    val (z, f) = actual.get match {
      case (z, Function(t, e)) =>
        (
          PartialEvalPass.partialEval(z),
          Function(
            t,
            PartialEvalPass.partialEval(e)(
              FactSet().range(t, ScalarRange(Some(0), None))
            )
          )
        )
    }

    // Smoke test
    val n = 5
    val stmExamples = Seq(
      StmCst(n, Tuple(True, False)),
      StmRange(n, 9, 8)
    )
    for (sVal <- stmExamples) {
      assertEquationsEqual(
        NonRecEqn(Let(s, sVal, Function(t, e))),
        RecEqn(Let(s, sVal, z), f),
        tMin = 0,
        // Check even a few cycles after everything is done
        iterations = 2 * n + 2
      )
    }

    assert(z == s)
    val expectedF: Function =
      (t: Expr) =>
        (x: Expr) => IfThenElse(-5 + t >= 5 || -5 + t < 0, x, StmNext(x).__0)
    assert(f == expectedF)
  }

  test("Stm2Vec2Stm") {
    val n = Param("n")
    val input = Param("s")
    val s = StmBuild(
      n,
      Tuple(input, VecBuild(n, (_: Expr) => DontCare), 0),
      (acc: Expr) =>
        IfThenElse(
          acc.__2 < n,
          Tuple(
            Tuple(
              StmNext(acc.__0).__0,
              VecShiftLeft(acc.__1, StmNext(acc.__0).__1),
              1 + acc.__2
            ),
            NNone
          ),
          Tuple(
            Tuple(acc.__0, acc.__1, 1 + acc.__2),
            SSome(VecAccess(acc.__1, acc.__2 - n))
          )
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // Correctness
    val examples = Seq(
      StmCst(n, 42),
      StmCst(n, Tuple(99, True)),
      StmCst(n, -1),
      StmRange(n, 1, 5),
      StmRepeat(StmCount(n), m = 3, n = n)
    )
    for (exampleStm <- examples) {
      for (nVal <- Seq(0, 1, 2, 6)) {
        val expected = Let(n, nVal, Let(input, exampleStm, s))
        val actual = Let(n, nVal, Let(input, exampleStm, opt))
        assert(ir.eval(actual) == ir.eval(expected))
      }
    }

    // Effective simplification
    assume(false)
    val expected = StmBuild(
      n,
      Tuple(input),
      (acc: Expr) =>
        Tuple(Tuple(StmNext(acc.__0).__0), SSome(StmNext(acc.__0).__1))
    )
    assert(opt == expected)
  }

  test("Stm2ReversedVec2Stm") {
    val n = Param("n")
    val input = Param("s")
    val s = StmBuild(
      n,
      Tuple(input, VecBuild(n, (_: Expr) => DontCare), 0),
      (acc: Expr) =>
        IfThenElse(
          acc.__2 < n,
          Tuple(
            Tuple(
              StmNext(acc.__0).__0,
              VecShiftLeft(acc.__1, StmNext(acc.__0).__1),
              1 + acc.__2
            ),
            NNone
          ),
          Tuple(
            Tuple(acc.__0, acc.__1, 1 + acc.__2),
            SSome(VecAccess(acc.__1, -1 + 2 * n - acc.__2))
          )
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // Correctness
    val examples = Seq(
      StmCst(n, 42),
      StmCst(n, Tuple(99, True)),
      StmCst(n, -1),
      StmRange(n, 1, 5),
      StmRepeat(StmCount(n), m = 3, n = n)
    )
    for (exampleStm <- examples) {
      for (nVal <- Seq(1, 2, 6)) {
        val expected = Let(n, nVal, Let(input, exampleStm, s))
        val actual = Let(n, nVal, Let(input, exampleStm, opt))
        assert(ir.eval(actual) == ir.eval(expected))
      }
    }

    // Effective simplification
    assert(opt == s)
  }

  test("RemoveAllInductionVarsSuccessfully") {
    val n = Param("n")
    val delta = Param("delta")
    val s = StmBuild(
      n,
      Tuple(
        3 /* up counter (+1) */, 10 /* up counter (+4) */,
        19 /* down counter (-2) */, 0 /* up counter (--3) */,
        -2 /* down counter (+-6) */, 1 /* counter (+ delta + 1) */
      ),
      (acc: Expr) =>
        Tuple(
          Tuple(
            acc.__0 + 1,
            acc.__1 + 4,
            acc.__2 - 2,
            acc.__3 - IntCst(-3),
            acc.__4 + (-6),
            acc.__5 + delta + 1
          ),
          SSome(
            Tuple(2 * acc.__0, acc.__1 / 3, acc.__2, acc.__3, acc.__4, acc.__5)
          )
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // tryRemoveAllInductionVars should return the same thing as removeInductionVars in this case
    assert(
      StmInductionVarRemovalPass.tryRemoveAllInductionVars(s).contains(opt)
    )
  }

  test("RemoveAllInductionVarsUnsuccessfully") {
    val s = Param("s")
    val n = Param("n")
    val stm = StmBuild(
      n,
      Tuple(3, s),
      (acc: Expr) =>
        Tuple(
          Tuple(acc.__0 + 2, StmNext(acc.__1).__0),
          SSome(StmNext(acc.__1).__1)
        )
    )
    assert(StmInductionVarRemovalPass.tryRemoveAllInductionVars(stm).isEmpty)
  }
}
