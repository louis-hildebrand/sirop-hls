package mhir.optimize

import mhir.ir.Lowering.ExprLowering
import mhir.ir.typecheck.TypeCheck
import mhir.ir._
import mhir.optimize.{PartialEvalPass => PE}
import mhir.sugar.{StmCst, StmRange, VecShiftLeft}
import org.scalatest.funsuite.AnyFunSuite

class StmInductionVarRemovalPassTests extends AnyFunSuite {
  private def assertEquationsEqual(
      expected: Formula,
      actual: Formula,
      iterations: Int,
      tMin: Int = 0
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

  /** Asserts that the given stream has only one accumulator and that the
    * accumulator represents time (i.e., starts at zero and counts up).
    *
    * @param s
    *   the stream to check.
    */
  private def assertOneAccumulator(s: StmBuild): Unit = {
    assert(s.equations.size == 1, "(there should be only one accumulator)")
    val t = s.accVars.head
    assert(s.seedByVar(t) == C(0)())
    assert(s.nextByVar(t) == Sum(C(1)(), t)())
  }

  // Lower and partially evaluate
  private def lpe(e: Expr): Expr = PartialEvalPass.partialEval(e.tchk().lower())

  /** Consider the accumulator
    *
    * {{{
    *   (x : i8) -> (-128, x + 32)
    * }}}
    *
    * This produces the sequence -128, -96, -64, -32, 0, 32, 64, 96, which does
    * not have overflow. However, if you carelessly let its closed form be
    * -128:i8 + 32:i8 * t, then 32:i8 * t will overflow when t = 4.
    */
  test("Counter:WatchOutForOverflow") {
    val n = 6
    val a = Param("a")(I8)
    val s = StmBuild(
      C(n)(),
      a,
      True,
      Map[Param, (Expr, Expr)](
        a -> (C(-128)(I8), a + C(32)(I8))
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    assert(mhir.ir.eval(s) == mhir.ir.eval(opt))

    // Effective simplification
    assertOneAccumulator(opt)
  }

  test("Counters") {
    val n = Param("n")(U8)
    val delta = Param("delta")(I16)
    val a0 = Param("a")(U8)
    val a1 = Param("a")(U8)
    val a2 = Param("a")(I8)
    val a3 = Param("a")(I8)
    val a4 = Param("a")(I8)
    val a5 = Param("a")(I16)
    val s = StmBuild(
      n,
      Tuple(2 * a0, a1 / 3, a2, a3, a4, a5)(),
      True,
      Map[Param, (Expr, Expr)](
        // up counter (+1)
        a0 -> (C(3)(U8), a0 + 1),
        // up counter (+4)
        a1 -> (C(10)(U8), a1 + 4),
        // down counter (-2)
        a2 -> (C(19)(I8), a2 - 2),
        // up counter (--3)
        a3 -> (C(0)(I8), a3 - IntCst(-3)()),
        // down counter (+-6)
        a4 -> (C(-2)(I8), a4 + IntCst(-6)()),
        // counter (+ delta + 1)
        a5 -> (C(1)(I16), a5 + delta + 1)
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = PartialEvalPass
      .partialEval(StmInductionVarRemovalPass().removeInductionVars(s))
      .asInstanceOf[StmBuild]

    // Correctness
    for (nVal <- 0 to 15) {
      for (deltaVal <- Seq(-42, 0, 42)) {
        val expected = Let(n, C(nVal)(U8), Let(delta, C(deltaVal)(I16), s)())()
        val actual = Let(n, C(nVal)(U8), Let(delta, C(deltaVal)(I16), opt)())()
        assert(mhir.ir.eval(expected) == mhir.ir.eval(actual))
      }
    }

    // Effective simplification
    assertOneAccumulator(opt)
  }

  test("NotCounter:TriangleSum") {
    val n = Param("n")(U8)
    val i = Param("i")(U8)
    val sum = Param("a")(U8)
    val t = Param("t")(U8)
    val triangleSum = StmBuild(
      1,
      sum,
      t >= n,
      Map[Param, (Expr, Expr)](
        i -> (C(0)(U8), i + 1),
        sum -> (C(0)(U8), sum + i),
        t -> (C(0)(U8), t + 1)
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val optimized = lpe(
      StmInductionVarRemovalPass().removeInductionVars(triangleSum)
    ).asInstanceOf[StmBuild]

    // Correctness
    for (nVal <- 0 to 10) {
      val actual = Let(n, C(nVal)(U8), optimized)()
      val expected = Let(n, C(nVal)(U8), triangleSum)()
      assert(mhir.ir.eval(actual) == mhir.ir.eval(expected))
    }

    // One counter can be removed, but not the sum
    val ideal = lpe({
      val t = Param("t")(I33)
      StmBuild(
        1,
        sum,
        TruncateTo(ToUnsigned(t)(), 8)() >= n,
        Map[Param, (Expr, Expr)](
          sum -> (C(0)(U8), sum + TruncateTo(ToUnsigned(t)(), 8)()),
          t -> (C(0)(I33), t + 1)
        )
      )()
    })
    assert(optimized == ideal)
  }

  test("VecShiftLeft") {
    val n = Param("n")(U8)
    val m = Param("m")(U8)
    val i = Param("i")(U8)
    val v = Param("v")(TyVec(U8, m))
    val s = StmBuild(
      n,
      VecShiftLeft(v, i)(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (C(100)(U8), i + 2),
        v -> (VecBuild(m, U8 ::+ (j => j))(), VecShiftLeft(v, i)())
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    val actual = (nVal: Int, mVal: Int) =>
      mhir.ir.eval(Let(n, C(nVal)(U8), Let(m, C(mVal)(U8), opt)())())
    assert(actual(1, 2) == StmLiteral(VecLiteral(1, 100)())())
    assert(
      actual(4, 3) == StmLiteral(
        VecLiteral(1, 2, 100)(),
        VecLiteral(2, 100, 102)(),
        VecLiteral(100, 102, 104)(),
        VecLiteral(102, 104, 106)()
      )()
    )

    // Effective simplification
    assertOneAccumulator(opt)
  }

  test("NextDoesntDependOnCurrent") {
    val n = Param("n")(U8)
    val f = Param("f")(TyArrow(U8, TyArrow(U8, I9)))
    val z = Param("z")(TyVec(I9, n))
    val a0 = Param("a")(U8)
    val a1 = Param("a1")(TyVec(I9, n))
    val s = StmBuild(
      n,
      a1,
      True,
      Map[Param, (Expr, Expr)](
        a0 -> (C(0)(U8), a0 + 1),
        a1 -> (
          z,
          VecBuild(n, U8 ::+ (j => FunCall(FunCall(f, a0)(), j)()))()
        )
      )
    )().tchk().lower().asInstanceOf[StmBuild]

    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    val nExamples = Seq(1, 2, 5)
    val fExamples: Seq[Function] =
      Seq(
        U8 ::+ (_ => U8 ::+ (i => ToSigned(i + 1)())),
        U8 ::+ (t => U8 ::+ (_ => ToSigned(2 * t)())),
        U8 ::+ (t => U8 ::+ (i => 1 - n + t + i))
      )
    val zExamples = Seq(
      VecBuild(n, U8 ::+ (_ => Default(I9)))(),
      VecBuild(n, U8 ::+ (i => ToSigned(i * i + 1)()))()
    )
    for (nVal <- nExamples) {
      for (fVal <- fExamples) {
        for (zVal <- zExamples) {
          val expected =
            Let(n, C(nVal)(U8), Let(f, fVal, Let(z, zVal, s)())())()
          val expectedVal = mhir.ir.eval(expected)
          val actual =
            Let(n, C(nVal)(U8), Let(f, fVal, Let(z, zVal, opt)())())()
          val actualVal = mhir.ir.eval(actual)
          assert(actualVal == expectedVal)
        }
      }
    }

    // Effective simplification
    assertOneAccumulator(opt)
  }

  test("MonotonicBool:SimpleCounter") {
    val n = Param("n")(U8)
    val i0 = Param("i0")(I8)
    val k0 = Param("k0")(I8)
    val k1 = Param("k1")(I8)
    val delta = 3
    val i = Param("i")(I8)
    val b0 = Param("b0")(TyBool)
    val b1 = Param("b1")(TyBool)
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
    for (nVal <- Seq(0, 1, 5)) {
      for (i0Val <- Seq(-3, 0, 2)) {
        for (k0Val <- Seq(-3, 0, 2)) {
          for (k1Val <- Seq(-3, 0, 2)) {
            val expected =
              Let(
                n,
                C(nVal)(U8),
                Let(
                  i0,
                  C(i0Val)(I8),
                  Let(k0, C(k0Val)(I8), Let(k1, C(k1Val)(I8), s)())()
                )()
              )()
            val actual =
              Let(
                n,
                C(nVal)(U8),
                Let(
                  i0,
                  C(i0Val)(I8),
                  Let(k0, C(k0Val)(I8), Let(k1, C(k1Val)(I8), opt)())()
                )()
              )()
            assert(
              mhir.ir.eval(actual) == mhir.ir.eval(expected),
              s"(for n = $nVal, i0 = $i0Val, k0 = $k0Val, k1 = $k1Val)"
            )
          }
        }
      }
    }

    // Effective simplification
    assertOneAccumulator(opt)
  }

  test("MonotonicBool:BoundedCounter:i8") {
    val n = Param("n")(U8)
    val i0 = Param("i0")(I8)
    val k = Param("k")(I8)
    val delta = C(4)(I8)
    val i = Param("i")(I8)
    val b = Param("b")(TyBool)
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
    for (nVal <- Seq(0, 1, 5)) {
      for (i0Val <- Seq(-3, 0, 2)) {
        for (kVal <- Seq(-3, 0, 2)) {
          val expected =
            Let(
              n,
              C(nVal)(U8),
              Let(i0, C(i0Val)(I8), Let(k, C(kVal)(I8), s)())()
            )()
          val actual =
            Let(
              n,
              C(nVal)(U8),
              Let(i0, C(i0Val)(I8), Let(k, C(kVal)(I8), opt)())()
            )()
          assert(
            mhir.ir.eval(actual) == mhir.ir.eval(expected),
            s"(for n = $nVal, i0 = $i0Val, k = $kVal)"
          )
        }
      }
    }

    // Effective simplification
    assertOneAccumulator(opt)
  }

  test("MonotonicBool:BoundedCounter:u32") {
    val n = Param("n")(U8)
    val i0 = Param("i0")(U32)
    val k = Param("k")(U32)
    val delta = C(4)(U32)
    val i = Param("i")(U32)
    val b = Param("b")(TyBool)
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
    for (nVal <- Seq(0, 1, 5)) {
      for (i0Val <- Seq(0, 1, 2)) {
        for (kVal <- Seq(0, 1, 2)) {
          val expected =
            Let(
              n,
              C(nVal)(U8),
              Let(i0, C(i0Val)(U32), Let(k, C(kVal)(U32), s)())()
            )()
          val actual =
            Let(
              n,
              C(nVal)(U8),
              Let(i0, C(i0Val)(U32), Let(k, C(kVal)(U32), opt)())()
            )()
          assert(
            mhir.ir.eval(actual) == mhir.ir.eval(expected),
            s"(for n = $nVal, i0 = $i0Val, k = $kVal)"
          )
        }
      }
    }

    // Effective simplification
    assertOneAccumulator(opt)
  }

  // Counters that count up but stop at a certain point
  test("PiecewiseCounter:StopCounting") {
    val n = Param("n")(U8)
    val k = Param("k")(I8)
    val a0 = Param("a")(U8)
    val a1 = Param("a")(U8)
    val a2 = Param("a")(U8)
    val s = StmBuild(
      n,
      Tuple(a1 + 2, a2)(),
      True,
      Map[Param, (Expr, Expr)](
        a0 -> (C(0)(U8), a0 + 1),
        a1 -> (C(1)(U8), Mux(a0 < k + 1, a1 + 1, a1)()),
        a2 -> (C(2)(U8), Mux(a0 <= -1 + k, a2 + 2, a2)())
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    for (nVal <- Seq(0, 1, 2, 5)) {
      for (kVal <- Seq(-10, -2, -1, 0, 10)) {
        val expected = Let(n, C(nVal)(U8), Let(k, C(kVal)(I8), s)())()
        val actual = Let(n, C(nVal)(U8), Let(k, C(kVal)(I8), opt)())()
        assert(
          mhir.ir.eval(actual) == mhir.ir.eval(expected),
          s"(for n = $nVal, k = $kVal)"
        )
      }
    }

    // Effective simplification
    assertOneAccumulator(opt)
  }

  // Counters that stay constant at first and only start counting later
  test("PiecewiseCounter:Delayed") {
    val n = Param("n")(U8)
    val k = Param("k")(I8)
    val a0 = Param("a")(U8)
    val a1 = Param("a")(U8)
    val a2 = Param("a")(U8)
    val s = StmBuild(
      n,
      Tuple(3 * a1, 2 * a2)(),
      True,
      Map[Param, (Expr, Expr)](
        a0 -> (C(0)(U8), a0 + 1),
        a1 -> (C(2)(U8), Mux(a0 >= k + 2, a1 + 1, a1)()),
        a2 -> (C(1)(U8), Mux(a0 > k, a2 + 2, a2)())
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    for (nVal <- Seq(0, 1, 2, 5)) {
      for (kVal <- Seq(-10, -3, -2, -1, 10)) {
        val expected = Let(n, C(nVal)(U8), Let(k, C(kVal)(I8), s)())()
        val actual = Let(n, C(nVal)(U8), Let(k, C(kVal)(I8), opt)())()
        assert(
          mhir.ir.eval(actual) == mhir.ir.eval(expected),
          s"(for n = $nVal, k = $kVal)"
        )
      }
    }

    // Effective simplification
    assertOneAccumulator(opt)
  }

  // Piecewise functions where each branch is yet another piecewise function
  test("PiecewiseCounter:UpThenDown") {
    val n = Param("n")(U8)
    val a0 = Param("a")(U8)
    val a1 = Param("a")(I8)
    val s = StmBuild(
      n,
      2 * a1,
      True,
      Map[Param, (Expr, Expr)](
        a0 -> (C(10)(U8), a0 + 1),
        a1 -> (
          C(2)(I8),
          Mux(
            a0 < 20,
            Mux(a0 < 17, a1 + 2, a1)(),
            Mux(a0 < 34, a1 - 3, a1)()
          )()
        )
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    for (nVal <- Seq(0, 1, 2, 5)) {
      val expected = Let(n, C(nVal)(U8), s)()
      val actual = Let(n, C(nVal)(U8), opt)()
      assert(mhir.ir.eval(actual) == mhir.ir.eval(expected))
    }

    // Effective simplification
    assertOneAccumulator(opt)
  }

  // Shift register that stops at a certain point
  test("PiecewiseVecShiftLeft:StopShifting") {
    val n = Param("n")(U8)
    val m = Param("m")(U8)
    val i = Param("i")(U8)
    val v = Param("v")(TyVec(U8, m))
    val s = StmBuild(
      n,
      v,
      True,
      Map[Param, (Expr, Expr)](
        i -> (C(2)(U8), i + 1),
        v -> (
          VecBuild(m, U8 ::+ (j => j))(),
          Mux(i < m, VecShiftLeft(v, 2 * i)(), v)()
        )
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    for (nVal <- Seq(0, 1, 2, 5)) {
      for (mVal <- Seq(0, 1, 2, 5)) {
        val expected = Let(n, C(nVal)(U8), Let(m, C(mVal)(U8), s)())().tchk()
        val actual = Let(n, C(nVal)(U8), Let(m, C(mVal)(U8), opt)())().tchk()
        assert(mhir.ir.eval(actual) == mhir.ir.eval(expected))
      }
    }

    // Effective simplification
    assertOneAccumulator(opt)
  }

  // Shift register that only starts shifting after some delay
  test("PiecewiseVecShiftLeft:Delayed") {
    val n = Param("n")(U8)
    val m = Param("m")(U8)
    val i = Param("i")(U8)
    val v = Param("v")(TyVec(U8, m))
    val s = StmBuild(
      n,
      v,
      True,
      Map[Param, (Expr, Expr)](
        i -> (C(7)(U8), i + 1),
        v -> (
          VecBuild(m, U8 ::+ (j => j))(),
          Mux(i < m, v, VecShiftLeft(v, 3 * i + 1)())()
        )
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = StmInductionVarRemovalPass().removeInductionVars(s)

    // Correctness
    for (nVal <- Seq(0, 1, 2, 5)) {
      for (mVal <- Seq(0, 1, 2, 5)) {
        val expected = Let(n, C(nVal)(U8), Let(m, C(mVal)(U8), s)())().tchk()
        val actual = Let(n, C(nVal)(U8), Let(m, C(mVal)(U8), opt)())().tchk()
        assert(mhir.ir.eval(actual) == mhir.ir.eval(expected))
      }
    }

    // Effective simplification
    assertOneAccumulator(opt)
  }

  test("ClosedForm:StmNext") {
    val n = C(10)(U8)
    val s = Param("s")(TyStm((U8, U8), n))
    val recEqn =
      (I33 ::+ (t => TyStm((U8, U8), n) ::+ (_ => t < n)))
        .tchk()
        .lower()
        .asInstanceOf[Function]
    val result = RecurrenceSolver.tryFindClosedForm(0, s, recEqn)

    assert(result.isDefined)
    val f = lpe(result.get)

    val expected =
      lpe(I33 ::+ (t => StmNextK(s, Mux(t < 10, t, C(10)(I33))())()))
    assert(f == expected)
  }

  test("ClosedForm:ShiftRegisterWithStmNext") {
    val n = C(7)(U8)
    val t0 = 0
    val s = Param("s")(TyStm(I16, n))
    val stmNextRecEqn =
      (I33 ::+ (t => TyStm(I16, n) ::+ (_ => t < n)))
        .tchk()
        .lower()
        .asInstanceOf[Function]
    val stmNextFun =
      RecurrenceSolver
        .tryFindClosedForm(t0, s, stmNextRecEqn)
        .map(f => PartialEvalPass.partialEval(f))
        .get

    val initialVec =
      VecBuild(n, U32 ::+ (_ => Default(I16)))().tchk().lower()
    val shiftRecEqn = (I33 ::+ (t =>
      TyVec(I16, n) ::+ (x =>
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
      I33 ::+ (t =>
        Mux(
          t < n,
          VecBuild(
            n,
            U32 ::+ (i =>
              Mux(
                i + t < 7,
                Default(I16),
                StmData(StmNextK(s, -1 * n + t + ToSigned(i)())())()
              )()
            )
          )(),
          VecBuild(
            n,
            U32 ::+ (i => StmData(StmNextK(s, ToSigned(i)())())())
          )()
        )()
      )
    )
    val actual = lpe(f)
    assert(actual == expected)
  }

  test("RecursiveForm:StmNextK(s, t)") {
    val n = 3
    val t = Param("t")(I33)
    val s = Param("s")(TyStm(I16, n))
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
      StmCst(n, C(42)(I16))(),
      StmRange(n, C(9)(I16), C(8)(I16))()
    )
    for (sVal <- stmExamples) {
      val timeFunc =
        mhir.ir
          .eval(Let(s, sVal, Function(t, e)())().tchk())
          .asInstanceOf[Function]
      assertEquationsEqual(
        FunctionOfTime(timeFunc),
        StreamTimeRecurrence(Let(s, sVal, z)(), f),
        iterations = n
      )
    }

    assert(z == s)
    val expectedF = I33 ::+ (_ => TyStm(I16, n) ::+ (_ => True))
    assert(f == expectedF)
  }

  test("RecursiveForm:StmNextK(s, t - n)") {
    val t = Param("t")(I33)
    val n = Param("n")(U8)
    val s = Param("s")(TyStm(I16, n))
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
      StmCst(n, C(42)(I16))(),
      StmRange(n, C(9)(I16), C(8)(I16))()
    )
    for (sVal <- stmExamples) {
      for (nVal <- Seq(1, 2, 5)) {
        val timeFunc = mhir.ir
          .eval(Let(n, C(nVal)(U8), Let(s, sVal, Function(t, e)())())().tchk())
          .asInstanceOf[Function]
        val recFunc =
          mhir.ir.eval(Let(n, C(nVal)(U8), f)().tchk()).asInstanceOf[Function]
        assertEquationsEqual(
          FunctionOfTime(timeFunc),
          StreamTimeRecurrence(
            Let(n, C(nVal)(U8), Let(s, sVal, z)())(),
            recFunc
          ),
          iterations = 2 * nVal
        )
      }
    }

    assert(z == s)
    val expectedF =
      U32 ::+ (t =>
        TyStm(I16, n) ::+ (_ =>
          Sum(t, Prod(-1, PadTo(ToSigned(n)(), 33)())())() geq 0
        )
      )
    assert(f == expectedF)
  }

  test("Stm2Vec2Stm") {
    val n = Param("n")(U8)
    val input = Param("input")(TyStm(I16, n))
    val t = Param("t")(I33)
    val s = Param("s")(TyStm(I16, -1))
    val v = Param("v")(TyVec(I16, n))
    val original = StmBuild(
      n,
      VecAccess(v, ToUnsigned(t - n)())(),
      t >= n,
      Map[Param, (Expr, Expr)](
        t -> (C(0)(I33), t + 1),
        s -> (input, t < n),
        v -> (
          VecBuild(n, U32 ::+ (_ => Default(I16)))(),
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
      StmCst(n, C(42)(I16))(),
      StmCst(n, C(-1)(I16))(),
      StmRange(n, C(1)(I16), C(5)(I16))()
    )
    for (exampleStm <- examples) {
      for (nVal <- Seq(0, 1, 2, 6)) {
        val expected =
          Let(n, C(nVal)(U8), Let(input, exampleStm, original)())().tchk()
        val actual = Let(n, C(nVal)(U8), Let(input, exampleStm, opt)())().tchk()
        assert(mhir.ir.eval(actual) == mhir.ir.eval(expected))
      }
    }

    // Effective simplification
    val s0 = Param("s")(TyStm(I16, -1))
    val s1 = Param("s")(TyStm(I16, -1))
    val expected = lpe(
      StmBuild(
        n,
        StmData(s1)(),
        t >= n,
        Map[Param, (Expr, Expr)](
          s0 -> (input, t < n),
          s1 -> (
            input,
            PadTo(t, 33)() - PadTo(ToSigned(n)(), 33)() >= 0
          ),
          t -> (C(0)(I33), t + 1)
        )
      )()
    )
    assert(opt == expected)
  }

  test("Stm2ReversedVec2Stm") {
    val n = Param("n")(U8)
    val input = Param("input")(TyStm(I16, n))
    val s = Param("s")(TyStm(I16, -1))
    val v = Param("v")(TyVec(I16, n))
    val t = Param("t")(I33)
    val original = StmBuild(
      n,
      VecAccess(v, ToUnsigned(-1 + 2 * n - t)())(),
      t >= n,
      Map[Param, (Expr, Expr)](
        t -> (C(0)(I33), t + 1),
        s -> (input, t < n),
        v -> (
          VecBuild(n, U32 ::+ (_ => Default(I16)))(),
          Mux(t < n, VecShiftLeft(v, StmData(s)())(), v)()
        )
      )
    )().tchk().lower().asInstanceOf[StmBuild]
    val opt = PartialEvalPass.partialEval(
      StmInductionVarRemovalPass().removeInductionVars(original)
    )

    // Correctness
    val examples = Seq(
      StmCst(n, C(42)(I16))(),
      StmCst(n, C(-1)(I16))(),
      StmRange(n, C(1)(I16), C(5)(I16))()
    )
    for (exampleStm <- examples) {
      for (nVal <- Seq(1, 2, 6)) {
        val expected =
          Let(n, C(nVal)(U8), Let(input, exampleStm, original)())().tchk()
        val actual = Let(n, C(nVal)(U8), Let(input, exampleStm, opt)())().tchk()
        assert(mhir.ir.eval(actual) == mhir.ir.eval(expected))
      }
    }

    // Cannot remove induction variable here because we're reading from the
    // input stream in reverse
    val expected = PE.partialEval(original)
    assert(opt == expected)
  }
}
