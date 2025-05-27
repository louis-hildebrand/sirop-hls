package opt

import ir._
import operations._
import org.scalatest.funsuite.AnyFunSuite

class StmInductionVarRemovalPassTests extends AnyFunSuite {
  private def assertEquationsEqual(
      expected: Formula,
      actual: Formula,
      tMin: Int,
      iterations: Int
  ): Unit = {
    require(iterations > 0)
    val expectedVals = expected.tchk().evalSeq(tMin, iterations)
    val actualVals = actual.tchk().evalSeq(tMin, iterations)
    assert(
      expectedVals
        .zip(actualVals)
        .forall({ case (x, y) => x == y }),
      s"Expected values $expectedVals but found $actualVals"
    )
  }

  // Lower and partially evaluate
  private def lpe(e: Expr): Expr = PartialEvalPass.partialEval(e.tchk().lower())

  test("Counters") {
    val n = Param("n")(TyInt)
    val delta = Param("delta")(TyInt)
    val a0 = Param("a")()
    val a1 = Param("a")()
    val a2 = Param("a")()
    val a3 = Param("a")()
    val a4 = Param("a")()
    val a5 = Param("a")()
    val s = StmBuild(
      n,
      Tuple(2 * a0, a1 / 3, a2, a3, a4, a5)(),
      True,
      Map[Param, (Expr, Expr)](
        // up counter (+1)
        a0 -> (3, a0 + 1),
        // up counter (+4)
        a1 -> (10, a1 + 4),
        // down counter (-2)
        a2 -> (19, a2 - 2),
        // up counter (--3)
        a3 -> (0, a3 - IntCst(-3)),
        // down counter (+-6)
        a4 -> (-2, a4 + IntCst(-6)),
        // counter (+ delta + 1)
        a5 -> (1, a5 + delta + 1)
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = PartialEvalPass.partialEval(
      StmInductionVarRemovalPass().removeInductionVars(s)
    )

    // Correctness
    for (nVal <- 0 to 15) {
      for (deltaVal <- Seq(-42, 0, 42)) {
        val expected = Let(n, nVal, Let(delta, deltaVal, s)())()
        val actual = Let(n, nVal, Let(delta, deltaVal, opt)())()
        assert(ir.eval(expected) == ir.eval(actual))
      }
    }

    // Effective simplification
    val t = Param("t")()
    val ideal = lpe(
      StmBuild(
        n,
        Tuple(
          6 + 2 * t,
          3 + (1 + 4 * t) / 3,
          19 + (-2) * t,
          3 * t,
          -2 + (-6) * t,
          1 + delta * t + t
        )(),
        True,
        Map[Param, (Expr, Expr)](t -> (0, t + 1))
      )()
    )
    assert(opt == ideal)
  }

  test("NotCounter:TriangleSum") {
    val n = Param("n")(TyInt)
    val i = Param("i")()
    val sum = Param("a")()
    val t = Param("t")()
    val triangleSum = StmBuild(
      1,
      sum,
      t >= n,
      Map[Param, (Expr, Expr)](
        i -> (0, i + 1),
        sum -> (0, sum + i),
        t -> (0, t + 1)
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val optimized = lpe(
      StmInductionVarRemovalPass().removeInductionVars(triangleSum)
    )

    // Correctness
    for (nVal <- 0 to 10) {
      val actual = Let(n, nVal, optimized)()
      val expected = Let(n, nVal, triangleSum)()
      assert(ir.eval(actual) == ir.eval(expected))
    }

    // One counter can be removed, but not the sum
    val ideal = lpe(
      StmBuild(
        1,
        sum,
        t >= n,
        Map[Param, (Expr, Expr)](
          sum -> (0, sum + t),
          t -> (0, t + 1)
        )
      )()
    )
    assert(optimized == ideal)
  }

  test("VecShiftLeft") {
    val n = Param("n")(TyInt)
    val m = Param("m")(TyInt)
    val i = Param("a")()
    val v = Param("a")()
    val s = StmBuild(
      n,
      VecShiftLeft(v, i)(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (100, i + 2),
        v -> (VecBuild(m, TyInt ::+ (j => j))(), VecShiftLeft(v, i)())
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    val actual =
      (nVal: Int, mVal: Int) => ir.eval(Let(n, nVal, Let(m, mVal, opt)())())
    assert(actual(1, 2) == StmLiteral(VecLiteral.ints(1, 100))())
    assert(
      actual(4, 3) == StmLiteral(
        VecLiteral(1, 2, 100)(),
        VecLiteral(2, 100, 102)(),
        VecLiteral(100, 102, 104)(),
        VecLiteral(102, 104, 106)()
      )()
    )

    // Effective simplification
    // There should only be one accumulator variable left representing t
    assert(opt.equations.size == 1)
    val t = opt.accVars.head
    assert(opt.equations == Map(t -> (IntCst(0), t + 1)))
  }

  test("NextDoesntDependOnCurrent") {
    val n = Param("n")(TyInt)
    val f = Param("f")(TyArrow(TyInt, TyArrow(TyInt, TyInt)))
    val z = Param("z")(TyVec(TyInt, n))
    val a0 = Param("a")()
    val a1 = Param("a1")()
    val s = StmBuild(
      n,
      a1,
      True,
      Map[Param, (Expr, Expr)](
        a0 -> (0, a0 + 1),
        a1 -> (
          z,
          VecBuild(n, TyInt ::+ (j => FunCall(FunCall(f, a0)(), j)()))()
        )
      )
    )().tchk().lower().asInstanceOf[StmBuild]

    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    val nExamples = Seq(1, 2, 5)
    val fExamples: Seq[Function] =
      Seq(
        TyInt ::+ (t => TyInt ::+ (i => i + 1)),
        TyInt ::+ (t => TyInt ::+ (i => 2 * t)),
        TyInt ::+ (t => TyInt ::+ (i => 1 - n + t + i))
      )
    val zExamples = Seq(
      VecBuild(n, TyInt ::+ (_ => Default(TyInt)))(),
      VecBuild(n, TyInt ::+ (i => i * i + 1))()
    )
    for (nVal <- nExamples) {
      for (fVal <- fExamples) {
        for (zVal <- zExamples) {
          val expected = Let(n, nVal, Let(f, fVal, Let(z, zVal, s)())())()
          val actual = Let(n, nVal, Let(f, fVal, Let(z, zVal, opt)())())()
          assert(ir.eval(expected) == ir.eval(actual))
        }
      }
    }

    // Effective simplification
    // There should only be one accumulator variable left representing t
    assert(opt.equations.size == 1)
    val t = opt.accVars.head
    assert(opt.equations == Map(t -> (IntCst(0), t + 1)))
  }

  test("MonotonicBool:SimpleCounter") {
    val n = Param("n")(TyInt)
    val i0 = Param("i0")(TyInt)
    val k0 = Param("k0")(TyInt)
    val k1 = Param("k1")(TyInt)
    // TODO: Run multiple tests for different values of delta?
    val delta = 3
    val i = Param("a")()
    val b0 = Param("a")()
    val b1 = Param("a")()
    val s = StmBuild(
      n,
      Tuple(i, b0, b1)(),
      Not(b1)() && (i % 2 === 0),
      Map[Param, (Expr, Expr)](
        i -> (i0, i + delta),
        b0 -> (True, b0 && i < k0),
        b1 -> (True, b1 && i < k1)
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    for (nVal <- (0 to 2) :+ 5) {
      for (i0Val <- -2 to 2) {
        for (k0Val <- -2 to 2) {
          for (k1Val <- -2 to 2) {
            val expected =
              Let(
                n,
                nVal,
                Let(i0, i0Val, Let(k0, k0Val, Let(k1, k1Val, s)())())()
              )()
            val actual =
              Let(
                n,
                nVal,
                Let(i0, i0Val, Let(k0, k0Val, Let(k1, k1Val, opt)())())()
              )()
            assert(
              ir.eval(actual) == ir.eval(expected),
              s"(for n = $nVal, i0 = $i0Val, k0 = $k0Val, k1 = $k1Val)"
            )
          }
        }
      }
    }

    // Effective simplification
    // There should only be one accumulator variable left representing t
    assert(opt.equations.size == 1)
    val t = opt.accVars.head
    assert(opt.equations == Map(t -> (IntCst(0), t + 1)))
  }

  test("MonotonicBool:BoundedCounter") {
    val n = Param("n")(TyInt)
    val i0 = Param("i0")(TyInt)
    val k = Param("k")(TyInt)
    val delta = 4
    val i = Param("i")()
    val b = Param("b")()
    val s = StmBuild(
      n,
      Tuple(i, b)(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (i0, Mux(b, i + delta, i)()),
        b -> (True, b && i < k)
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    for (nVal <- (0 to 2) :+ 5) {
      for (i0Val <- -2 to 2) {
        for (kVal <- -2 to 2) {
          val expected =
            Let(n, nVal, Let(i0, i0Val, Let(k, kVal, s)())())()
          val actual =
            Let(n, nVal, Let(i0, i0Val, Let(k, kVal, opt)())())()
          assert(
            ir.eval(actual) == ir.eval(expected),
            s"(for n = $nVal, i0 = $i0Val, k = $kVal)"
          )
        }
      }
    }

    // Effective simplification
    // There should only be one accumulator variable left representing t
    assert(opt.equations.size == 1)
    val t = opt.accVars.head
    assert(opt.equations == Map(t -> (IntCst(0), t + 1)))
  }

  // Counters that count up but stop at a certain point
  test("PiecewiseCounter:StopCounting") {
    val n = Param("n")(TyInt)
    val k = Param("k")(TyInt)
    val a0 = Param("a")()
    val a1 = Param("a")()
    val a2 = Param("a")()
    val s = StmBuild(
      n,
      Tuple(a1 + 2, a2)(),
      True,
      Map[Param, (Expr, Expr)](
        a0 -> (0, a0 + 1),
        a1 -> (1, Mux(a0 < k + 1, a1 + 1, a1)()),
        a2 -> (2, Mux(a0 <= -1 + k, a2 + 2, a2)())
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    for (nVal <- Seq(0, 1, 2, 5)) {
      for (kVal <- Seq(-10, -2, -1, 0, 10)) {
        val expected = Let(n, nVal, Let(k, kVal, s)())()
        val actual = Let(n, nVal, Let(k, kVal, opt)())()
        assert(
          ir.eval(actual) == ir.eval(expected),
          s"(for n = $nVal, k = $kVal)"
        )
      }
    }

    // Effective simplification
    // There should only be one accumulator variable left representing t
    assert(opt.equations.size == 1)
    val t = opt.accVars.head
    assert(opt.equations == Map(t -> (IntCst(0), t + 1)))
  }

  // Counters that stay constant at first and only start counting later
  test("PiecewiseCounter:Delayed") {
    val n = Param("n")(TyInt)
    val k = Param("k")(TyInt)
    val a0 = Param("a")()
    val a1 = Param("a")()
    val a2 = Param("a")()
    val s = StmBuild(
      n,
      Tuple(3 * a1, 2 * a2)(),
      True,
      Map[Param, (Expr, Expr)](
        a0 -> (0, a0 + 1),
        a1 -> (2, Mux(a0 >= k + 2, a1 + 1, a1)()),
        a2 -> (1, Mux(a0 > k, a2 + 2, a2)())
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    for (nVal <- Seq(0, 1, 2, 5)) {
      for (kVal <- Seq(-10, -3, -2, -1, 10)) {
        val expected = Let(n, nVal, Let(k, kVal, s)())()
        val actual = Let(n, nVal, Let(k, kVal, opt)())()
        assert(
          ir.eval(actual) == ir.eval(expected),
          s"(for n = $nVal, k = $kVal)"
        )
      }
    }

    // Effective simplification
    // There should only be one accumulator variable left representing t
    assert(opt.equations.size == 1)
    val t = opt.accVars.head
    assert(opt.equations == Map(t -> (IntCst(0), t + 1)))
  }

  // Piecewise functions where each branch is yet another piecewise function
  test("PiecewiseCounter:UpThenDown") {
    val n = Param("n")(TyInt)
    val a0 = Param("a")()
    val a1 = Param("a")()
    val s = StmBuild(
      n,
      2 * a1,
      True,
      Map[Param, (Expr, Expr)](
        a0 -> (10, a0 + 1),
        a1 -> (2, Mux(
          a0 < 20,
          Mux(a0 < 17, a1 + 2, a1)(),
          Mux(a0 < 34, a1 - 3, a1)()
        )())
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    for (nVal <- Seq(0, 1, 2, 5)) {
      val expected = Let(n, nVal, s)()
      val actual = Let(n, nVal, opt)()
      assert(ir.eval(actual) == ir.eval(expected))
    }

    // Effective simplification
    // There should only be one accumulator variable left representing t
    assert(opt.equations.size == 1)
    val t = opt.accVars.head
    assert(opt.equations == Map(t -> (IntCst(0), t + 1)))
  }

  // Shift register that stops at a certain point
  test("PiecewiseVecShiftLeft:StopShifting") {
    val n = Param("n")(TyInt)
    val m = Param("m")(TyInt)
    val i = Param("i")()
    val v = Param("v")()
    val s = StmBuild(
      n,
      v,
      True,
      Map[Param, (Expr, Expr)](
        i -> (2, i + 1),
        v -> (
          VecBuild(m, TyInt ::+ (j => j))(),
          Mux(i < m, VecShiftLeft(v, 2 * i)(), v)()
        )
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    for (nVal <- Seq(0, 1, 2, 5)) {
      for (mVal <- Seq(0, 1, 2, 5)) {
        val expected = Let(n, nVal, Let(m, mVal, s)())().tchk()
        val actual = Let(n, nVal, Let(m, mVal, opt)())().tchk()
        assert(ir.eval(actual) == ir.eval(expected))
      }
    }

    // Effective simplification
    // There should only be one accumulator variable left representing t
    assert(opt.equations.size == 1)
    val t = opt.accVars.head
    assert(opt.equations == Map(t -> (IntCst(0), t + 1)))
  }

  // Shift register that only starts shifting after some delay
  test("PiecewiseVecShiftLeft:Delayed") {
    val n = Param("n")(TyInt)
    val m = Param("m")(TyInt)
    val i = Param("i")()
    val v = Param("v")()
    val s = StmBuild(
      n,
      v,
      True,
      Map[Param, (Expr, Expr)](
        i -> (7, i + 1),
        v -> (
          VecBuild(m, TyInt ::+ (j => j))(),
          Mux(i < m, v, VecShiftLeft(v, 3 * i + 1)())()
        )
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    for (nVal <- Seq(0, 1, 2, 5)) {
      for (mVal <- Seq(0, 1, 2, 5)) {
        val expected = Let(n, nVal, Let(m, mVal, s)())().tchk()
        val actual = Let(n, nVal, Let(m, mVal, opt)())().tchk()
        assert(ir.eval(actual) == ir.eval(expected))
      }
    }

    // Effective simplification
    // There should only be one accumulator variable left representing t
    assert(opt.equations.size == 1)
    val t = opt.accVars.head
    assert(opt.equations == Map(t -> (IntCst(0), t + 1)))
  }

  test("ClosedForm:StmNext") {
    val n = 10
    val s = Param("s")(TyStm(TyInt, n))
    val recEqn =
      TyInt ::+ (t => TyStm(TyInt, n) ::+ (_ => t < n))
    val result = RecurrenceSolver.tryFindClosedForm(0, s, recEqn)

    assert(result.isDefined)
    val f = lpe(result.get)

    val expected =
      lpe(TyInt ::+ (t => StmNextK(s, Mux(t < 10, t, 10)())()))
    assert(f == expected)
  }

  test("ClosedForm:ShiftRegisterWithStmNext") {
    val n = 7
    val t0 = 0
    val s = Param("s")(TyStm(TyInt, n))
    val stmNextRecEqn =
      TyInt ::+ (t => Missing ::+ (_ => t < n))
    val stmNextFun =
      RecurrenceSolver
        .tryFindClosedForm(t0, s, stmNextRecEqn)
        .map(f => PartialEvalPass.partialEval(f))
        .get

    val initialVec =
      VecBuild(n, TyInt ::+ (_ => Default(TyInt)))().tchk().lower()
    val shiftRecEqn = (TyInt ::+ (t =>
      TyVec(TyInt, n) ::+ (x =>
        Mux(
          t < n,
          VecShiftLeft(x, StmData(FunCall(stmNextFun, t)())())(),
          x
        )()
      )
    )).tchk().lower().asInstanceOf[Function]
    val shiftEqn =
      RecurrenceSolver.tryFindClosedForm(0, initialVec, shiftRecEqn)

    assert(shiftEqn.isDefined)
    val f = PartialEvalPass.partialEval(shiftEqn.get)(FactSet())

    // Effective simplification
    val expected = lpe(
      TyInt ::+ (t =>
        Mux(
          t < n,
          VecBuild(
            n,
            TyInt ::+ (i =>
              Mux(
                i + t < 7,
                Default(TyInt),
                StmData(StmNextK(s, -n + t + i)())()
              )()
            )
          )(),
          VecBuild(n, TyInt ::+ (i => StmData(StmNextK(s, i)())()))()
        )()
      )
    )
    val actual = lpe(f)
    assert(actual == expected)
  }

  test("RecursiveForm:StmNextK(s, t)") {
    val n = 3
    val t = Param("t")(TyInt)
    val s = Param("s")(TyStm(TyInt, n))
    val e = StmNextK(s, t)().tchk().asInstanceOf[StmNextK]
    val actual =
      RecurrenceSolver.tryFindRecursiveForm(e, t = t)(FactSet())

    assert(actual.isDefined)
    val (z, f) = actual.get match {
      case (z, Function(t, e)) =>
        (
          PartialEvalPass.partialEval(z),
          Function(t, PartialEvalPass.partialEval(e)(FactSet().geq(t, 0)))()
        )
    }

    // Smoke test
    val stmExamples = Seq(
      StmCst(n, 42)(),
      StmRange(n, 9, 8)()
    )
    for (sVal <- stmExamples) {
      val timeFunc =
        ir.eval(Let(s, sVal, Function(t, e)())().tchk()).asInstanceOf[Function]
      assertEquationsEqual(
        FunctionOfTime(timeFunc),
        StreamTimeRecurrence(Let(s, sVal, z)(), f),
        tMin = 0,
        iterations = n
      )
    }

    assert(z == s)
    val expectedF = TyInt ::+ (_ => TyStm(TyInt, n) ::+ (_ => True))
    assert(f == expectedF)
  }

  test("RecursiveForm:StmNextK(s, Min(-5 + t, 5))") {
    val n = 5
    val t = Param("t")(TyInt)
    val s = Param("s")(TyStm(TyInt, n))
    val e = StmNextK(s, Min(-5 + t, 5))().tchk().lower().asInstanceOf[StmNextK]
    val actual =
      RecurrenceSolver.tryFindRecursiveForm(e, t = t)(FactSet())

    assert(actual.isDefined)
    val (z, f) = actual.get match {
      case (z, Function(t, e)) =>
        (
          PartialEvalPass.partialEval(z),
          Function(t, PartialEvalPass.partialEval(e)(FactSet().geq(t, 0)))()
        )
    }

    // Smoke test
    val stmExamples = Seq(
      StmCst(n, 42)(),
      StmRange(n, 9, 8)()
    )
    for (sVal <- stmExamples) {
      val timeFunc =
        ir.eval(Let(s, sVal, Function(t, e)())().tchk()).asInstanceOf[Function]
      assertEquationsEqual(
        FunctionOfTime(timeFunc),
        StreamTimeRecurrence(Let(s, sVal, z)(), f),
        tMin = 0,
        // Check even a few cycles after everything is done
        iterations = 2 * n + 2
      )
    }

    assert(z == s)
    val expectedF: Function =
      TyInt ::+ (t => Missing ::+ (_ => (t < 10) && (t >= 5)))
    assert(f == expectedF)
  }

  test("RecursiveForm:StmNextK(s, Min(t, n))") {
    val t = Param("t")(TyInt)
    val n = Param("n")(TyInt)
    val s = Param("s")(TyStm(TyInt, n))
    val e = StmNextK(s, Min(t, n))().tchk().lower().asInstanceOf[StmNextK]
    val facts = FactSet().geq(n, 1)
    val actual =
      RecurrenceSolver.tryFindRecursiveForm(e, t = t)(FactSet())

    assert(actual.isDefined)
    val (z, f) = actual.get match {
      case (z, Function(t, e)) =>
        (
          PartialEvalPass.partialEval(z),
          Function(t, PartialEvalPass.partialEval(e)(facts.geq(t, 0)))()
        )
    }

    // Smoke test
    val stmExamples = Seq(
      StmCst(n, 42)(),
      StmRange(n, 9, 8)()
    )
    for (sVal <- stmExamples) {
      for (nVal <- Seq(1, 2, 5)) {
        val timeFunc =
          ir.eval(Let(n, nVal, Let(s, sVal, Function(t, e)())())().tchk())
            .asInstanceOf[Function]
        val recFunc = ir.eval(Let(n, nVal, f)()).asInstanceOf[Function]
        assertEquationsEqual(
          FunctionOfTime(timeFunc),
          StreamTimeRecurrence(Let(n, nVal, Let(s, sVal, z)())(), recFunc),
          tMin = 0,
          // Check even a few cycles after everything is done
          iterations = 2 * nVal + 2
        )
      }
    }

    assert(z == s)
    val expectedF = TyInt ::+ (t => Missing ::+ (_ => t < n))
    assert(f == expectedF)
  }

  test("RecursiveForm:StmNextK(s, t - n)") {
    val t = Param("t")(TyInt)
    val n = Param("n")(TyInt)
    val s = Param("s")(TyStm(TyInt, n))
    val e = StmNextK(s, t - n)().tchk().asInstanceOf[StmNextK]
    val facts = FactSet().geq(n, 1)
    val actual = RecurrenceSolver.tryFindRecursiveForm(e, t = t)(facts)

    assert(actual.isDefined)
    val (z, f) = actual.get match {
      case (z, Function(t, e)) =>
        (
          PartialEvalPass.partialEval(z),
          Function(t, PartialEvalPass.partialEval(e)(facts.geq(t, 0)))()
        )
    }

    // Smoke test
    val stmExamples = Seq(
      StmCst(n, 42)(),
      StmRange(n, 9, 8)()
    )
    for (sVal <- stmExamples) {
      for (nVal <- Seq(1, 2, 5)) {
        val timeFunc = ir
          .eval(Let(n, nVal, Let(s, sVal, Function(t, e)())())().tchk())
          .asInstanceOf[Function]
        val recFunc = ir.eval(Let(n, nVal, f)().tchk()).asInstanceOf[Function]
        assertEquationsEqual(
          FunctionOfTime(timeFunc),
          StreamTimeRecurrence(Let(n, nVal, Let(s, sVal, z)())(), recFunc),
          tMin = 0,
          iterations = 2 * nVal
        )
      }
    }

    assert(z == s)
    val expectedF = TyInt ::+ (t => Missing ::+ (_ => t - n >= 0))
    assert(f == expectedF)
  }

  test("Stm2Vec2Stm") {
    val n = Param("n")(TyInt)
    val input = Param("input")(TyStm(TyInt, n))
    val t = Param("t")()
    val s = Param("s")()
    val v = Param("v")()
    val original = StmBuild(
      n,
      VecAccess(v, t - n)(),
      t >= n,
      Map[Param, (Expr, Expr)](
        t -> (0, t + 1),
        s -> (input, t < n),
        v -> (
          VecBuild(n, TyInt ::+ (_ => Default(TyInt)))(),
          Mux(t < n, VecShiftLeft(v, StmData(s)())(), v)()
        )
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val facts = FactSet().geq(n, 1)
    val opt = lpe(
      StmInductionVarRemovalPass(facts).removeInductionVars(original).tchk()
    )

    // Correctness
    val examples = Seq(
      StmCst(n, 42)(),
      StmCst(n, -1)(),
      StmRange(n, 1, 5)()
    )
    for (exampleStm <- examples) {
      for (nVal <- Seq(0, 1, 2, 6)) {
        val expected = Let(n, nVal, Let(input, exampleStm, original)())().tchk()
        val actual = Let(n, nVal, Let(input, exampleStm, opt)())().tchk()
        assert(ir.eval(actual) == ir.eval(expected))
      }
    }

    // Effective simplification
    val s0 = Param("s")()
    val s1 = Param("s")()
    val expected = lpe(
      StmBuild(
        n,
        Mux(t - n >= 0 && t - n < n, StmData(s1)(), 0)(),
        t >= n,
        Map[Param, (Expr, Expr)](
          s0 -> (input, t < n),
          s1 -> (input, -1 * n + t >= 0),
          t -> (0, t + 1)
        )
      )()
    )
    assert(opt == expected)
  }

  test("Stm2ReversedVec2Stm") {
    val n = Param("n")(TyInt)
    val input = Param("input")(TyStm(TyInt, n))
    val s = Param("s")()
    val v = Param("v")()
    val t = Param("t")()
    val original = StmBuild(
      n,
      VecAccess(v, -1 + 2 * n - t)(),
      t >= n,
      Map[Param, (Expr, Expr)](
        t -> (0, t + 1),
        s -> (input, t < n),
        v -> (
          VecBuild(n, TyInt ::+ (_ => Default(TyInt)))(),
          Mux(t < n, VecShiftLeft(v, StmData(s)())(), v)()
        )
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = PartialEvalPass.partialEval(
      StmInductionVarRemovalPass().removeInductionVars(original)
    )

    // Correctness
    val examples = Seq(
      StmCst(n, 42)(),
      StmCst(n, -1)(),
      StmRange(n, 1, 5)(),
      StmRepeat(StmCount(n)(), m = 3)()
    )
    for (exampleStm <- examples) {
      for (nVal <- Seq(1, 2, 6)) {
        val expected = Let(n, nVal, Let(input, exampleStm, original)())().tchk()
        val actual = Let(n, nVal, Let(input, exampleStm, opt)())().tchk()
        assert(ir.eval(actual) == ir.eval(expected))
      }
    }

    // Cannot remove induction variable here because we're reading from the
    // input stream in reverse
    assert(opt == original)
  }
}
