package mhir.optimize.experimental

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.optimize.StreamFuser.StreamFusion
import mhir.optimize.{PartialEvalPass => PE, _}
import mhir.sugar.{ExprLowering, _}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.tagobjects.Slow

/** Examples of how some high-level transformations in, for example, SHIR can be
  * expressed as sequences of smaller transformations in the minimalist IR. For
  * each test case, the existence of the sequence of transformations in the
  * minimalist IR is proven by simply manually writing it (rather than automatic
  * exploration, as with equality saturation).
  */
class ManualOptimizationTests extends AnyFunSuite {

  private val stmBuildSimplifier = StmBuildSimplifier()
  private val simplifier = StmSimplifier(stmBuildSimplifier)
  private val fusionPass = new GreedyStmFusionPass(stmBuildSimplifier)

  /** The optimizer can produce a nice design for a simple map on a 1D stream,
    * even though the lowering pass spits out something a bit gross (since it
    * must handle multi-dimensional streams).
    */
  test("Map:1D-1D") {
    val n = Param("n")(U8)
    val s = Param("s")(TyStm(I32, n))
    val f = Param("f")(TyArrow(I32, I32))
    val original = StmMap(s, I32 ::+ (x => FunCall(f, x)()))()
    val tl = (e: Expr) => e.tchk().lower()
    val optimize = (s: Expr) => {
      val s1 = tl(PE.partialEval(s))
      val s2 = tl(simplifier.simplify(s1)())
      val s3 = tl(fusionPass.fuse(s2))
      val s4 = tl(simplifier.simplify(s3))
      s4
    }
    val optimized = optimize(original.tchk().lower())

    // Correctness
    val nExamples = Seq(0, 1, 4)
    val sExamples = Seq(
      StmCst(n, C(42)(I32))(),
      StmMap(StmCount(n)(), U8 ::+ (x => ReshapeData(x, I32)()))()
    )
    val fExamples =
      Seq(I32 ::+ (x => x), I32 ::+ (_ => C(99)(I32)), I32 ::+ (x => x * x))
    for (nVal <- nExamples) {
      for (sVal <- sExamples) {
        for (fVal <- fExamples) {
          val expected =
            mhir.eval.eval(
              Let(n, C(nVal)(U8), Let(f, fVal, Let(s, sVal, original)())())()
                .tchk()
            )
          val actual =
            mhir.eval.eval(
              Let(n, C(nVal)(U8), Let(f, fVal, Let(s, sVal, optimized)())())()
                .tchk()
            )
          assert(actual == expected)
        }
      }
    }

    // Effective simplification
    val sAcc = Param("s")(TyStm(I32, -1))
    val ideal = StmBuild(
      n,
      FunCall(f, StmData(sAcc)())(),
      True,
      Map[Param, (Expr, Expr)](sAcc -> (s, True))
    )().tchk().lower()
    assert(optimized == ideal)
  }

  /** The optimizer can get rid of the counter in a 1D `StmPrefix`.
    *
    * (The counter is needed in the first place in case the `StmPrefix` is used
    * inside a `StmMap` over a multi-dimensional stream.)
    */
  test("StmPrefix:NoCounter") {
    val n = C(16)(U8)
    val s = Param("s")(TyStm(U8, n))
    val k = C(15)(U8)
    val original = StmPrefix(s, k)().tchk().lower()
    val optimized = simplifier.simplify(original)

    // Correctness
    val sExamples = Seq(
      StmRange(n, C(42)(U8), C(3)(U8))().tchk()
    )
    for (sVal <- sExamples) {
      val originalVal = mhir.eval.eval(original.subPreserveType(s -> sVal))
      val optimizedVal = mhir.eval.eval(optimized.subPreserveType(s -> sVal))
      assert(optimizedVal == originalVal)
    }

    // Effective simplification
    val ideal = StmBuild(
      k,
      StmData(s)(),
      True,
      Map[Param, (Expr, Expr)](
        s -> (s, True)
      )
    )().tchk()
    assert(optimized == ideal)
  }

  /** The optimizer can turn VecFold into a simple sum *provided* the length is
    * a static constant.
    */
  test("VecFoldSimpleSum") {
    val n = 3
    val v = Param("v")(TyVec(I16, C(n)(U8)))
    val z = Param("z")(I16)
    val original = VecFoldSeq(v, z, PlusFunction(I16))()
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: Expr) => {
      val s0 = tl(s)
      val s1 = tl(s0.fuseCompletely())
      val s2 = tl(stmBuildSimplifier.simplify(s1)())
      val s3 = tl(PE.partialEvalStmBuild(s2))
      s3
    }
    val optimized = optimize(original)

    // Correctness
    val vExamples = Seq(
      VecBuild(C(n)(U8), U8 ::+ (i => ReshapeData(i, I16)()))(),
      VecBuild(C(n)(U8), U8 ::+ (i => ReshapeData(i * (i + 1), I16)()))()
    )
    val zExamples = Seq(C(0)(I16), C(-3)(I16), C(42)(I16))
    for (vVal <- vExamples) {
      for (zVal <- zExamples) {
        val expected =
          mhir.eval.eval(Let(v, vVal, Let(z, zVal, original)())().tchk())
        val actual =
          mhir.eval.eval(Let(v, vVal, Let(z, zVal, optimized)())().tchk())
        assert(actual == expected)
      }
    }

    // Effective simplification
    val ideal =
      StmCst(
        1,
        Sum(z, VecAccess(v, 0)(), VecAccess(v, 1)(), VecAccess(v, 2)())()
      )().tchk().lower()
    assert(optimized == ideal)
  }

  /** The optimizer can perform map-map fusion on vectors.
    */
  test("Fuse:VecMapMap") {
    val n = Param("n")(U8)
    val input = Param("input")(TyVec(U32, n))
    val f = U32 ::+ (x => (x + 2) * (x + 3) * (x + 4))
    val g = U32 ::+ (x => x + 10)
    val original = VecMap(VecMap(input, f)(), g)().tchk().lower()
    val optimize = (v: Expr) => {
      val v1 = PE.partialEval(v)
      v1
    }
    val optimized = optimize(original)

    // Correct behaviour
    // (Using one example input, f, and g)
    val call = (e: Expr) =>
      Let(n, C(5)(U8), Let(input, VecBuild(n, U32 ::+ (i => i + 1))(), e)())()
    assert(mhir.eval.eval(call(original)) == mhir.eval.eval(call(optimized)))
    // Successful fusion:
    // map(map(v, f), g) should simplify to the same thing as map(v, g . f)
    val ideal = optimize(
      VecMap(
        input,
        U32 ::+ (x => FunCall(g, FunCall(f, x)())())
      )().tchk().lower()
    )
    assert(optimized == ideal)
  }

  /** The optimizer can perform map-map fusion.
    */
  test("Fuse:StmMapMap") {
    val n = Param("n")(U16)
    val input = Param("input")(TyStm(U16, n))
    val f = U16 ::+ (x => (x + 2) * (x + 3) * (x + 4))
    val g = U16 ::+ (x => x + 7)
    val s = StmMap(StmMap(input, f)(), g)().tchk().lower()
    val optimize = (s: Expr) => {
      val s1 = s.fuseCompletely()
      val s2 = s1.tchk().lower().asInstanceOf[StmBuild]
      stmBuildSimplifier.simplify(s2)()
    }
    val actual = optimize(s)

    // Correct behaviour
    // (Using one example input, f, and g)
    val call = (e: Expr) => Let(n, C(5)(U16), Let(input, StmCount(n)(), e)())()
    val expectedElems = StmLiteral(31, 67, 127, 217, 343)()
    assert(mhir.eval.eval(call(s)) == expectedElems)
    assert(mhir.eval.eval(call(actual)) == expectedElems)
    // Successful fusion:
    // map(map(s, f), g) should simplify to the same thing as map(s, g . f)
    val ideal = optimize(
      StmMap(input, U16 ::+ (x => FunCall(g, FunCall(f, x)())()))()
        .tchk()
        .lower()
    )
    assert(actual == ideal)
  }

  /** The optimizer can perform map-fold fusion.
    */
  test("Fuse:StmMapFold") {
    val n = Param("n")(U16)
    val input = Param("input")(TyStm(U16, n))
    val f = U16 ::+ (x => (x + 2) * (x + 3) * (x + 4))
    val z = Param("z")(U16)
    val s =
      StmFold(StmMap(input, f)(), z, PlusFunction(U16))().tchk().lower()
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: Expr) => {
      val s0 = tl(s)
      val s1 = tl(s0.fuseCompletely())
      val s2 = tl(stmBuildSimplifier.simplify(s1)())
      s2
    }
    val actual = optimize(s)

    // Correct behaviour
    // (Using one example input, f, g, and z)
    val call =
      (e: Expr) =>
        Let(
          n,
          C(3)(U16),
          Let(z, C(42)(U16), Let(input, StmCount(n)(), e)())()
        )()
    val expected =
      StmLiteral(
        mhir.eval.eval(
          C(42)(U16)
            + FunCall(f, C(0)(U16))()
            + FunCall(f, C(1)(U16))()
            + FunCall(f, C(2)(U16))()
        )
      )()
    assert(mhir.eval.eval(call(s)) == expected)
    assert(mhir.eval.eval(call(actual)) == expected)
    // Successful fusion:
    // fold(map(s, f), z, a => x => g(a, x)) should simplify to the same thing
    // as fold(s, z, a => x => g(a, f(x))
    val ideal = optimize(
      StmFold(
        input,
        z,
        U16 ::+ (acc => U16 ::+ (x => acc + FunCall(f, x)()))
      )()
    )
    assert(actual == ideal)
  }

  /** The optimizer can perform map-scan fusion.
    */
  test("Fuse:StmMapScan") {
    val n = Param("n")(U16)
    val input = Param("input")(TyStm(U16, n))
    val f = U16 ::+ (x => (x + 2) * (x + 3) * (x + 4))
    val z = Param("z")(U16)
    val s =
      StmScanInclusive(StmMap(input, f)(), z, PlusFunction(U16))()
        .tchk()
        .lower()
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: Expr) => {
      val s0 = tl(s)
      val s1 = tl(s0.fuseCompletely())
      val s2 = tl(stmBuildSimplifier.simplify(s1)())
      s2
    }
    val actual = optimize(s)

    // Correct behaviour
    // (Using one example input, f, g, and z)
    val call =
      (e: Expr) =>
        Let(
          n,
          C(3)(U16),
          Let(z, C(42)(U16), Let(input, StmCount(n)(), e)())()
        )()
    val expected =
      StmLiteral(
        mhir.eval.eval(42 + f(C(0)(U16))),
        mhir.eval.eval(42 + f(C(0)(U16)) + f(C(1)(U16))),
        mhir.eval.eval(42 + f(C(0)(U16)) + f(C(1)(U16)) + f(C(2)(U16)))
      )()
    assert(mhir.eval.eval(call(s)) == expected)
    assert(mhir.eval.eval(call(actual)) == expected)
    // Successful fusion:
    // fold(map(s, f), z, a => x => g(a, x)) should simplify to the same thing
    // as fold(s, z, a => x => g(a, f(x))
    val ideal = optimize(
      StmScanInclusive(
        input,
        z,
        U16 ::+ (acc => U16 ::+ (x => acc + FunCall(f, x)()))
      )()
    )
    assert(actual == ideal)
  }

  test("Fuse:StmShiftRight") {
    val n = Param("n")(U8)
    val input = Param("input")(TyStm(U8, n + 1))
    val original =
      StmPrepend(StmPrefix(input, n)(), C(42)(U8))()
        .tchk()
        .lower()
        .asInstanceOf[StmBuild]
    val optimize = (s: StmBuild) => {
      val s1 = s.fuseCompletely()
      val s2 = s1.tchk().lower().asInstanceOf[StmBuild]
      val s3 = stmBuildSimplifier.simplify(s2)()
      s3
    }
    val fused = optimize(original)

    // Correct behaviour
    // (Using one example input)
    val call =
      (e: Expr) => Let(n, C(5)(U8), Let(input, StmCount(n + 1)(), e)())()
    val expectedElems = StmLiteral(42, 0, 1, 2, 3, 4)()
    assert(mhir.eval.eval(call(original)) == expectedElems)
    assert(mhir.eval.eval(call(fused)) == expectedElems)
    // Successful fusion
    val s = Param("s")(TyStm(U8, -1))
    val i = Param("i")(U32)
    val ideal = optimize(
      StmBuild(
        Sum(PadTo(n, 9)(), C(1)(TyUInt(9)))(),
        Mux(i === 1, StmData(s)(), C(42)(U8))(),
        True,
        Map[Param, (Expr, Expr)](
          s -> (input, i === 1),
          i -> (C(0)(U32), Mux(i === 1, C(1)(U32), i + 1)())
        )
      )().tchk().lower().asInstanceOf[StmBuild]
    )
    assert(fused == ideal)
  }

  test("Fuse:StmShiftLeft") {
    val n = 5
    val input = Param("input")(TyStm(U8, n))
    val original = StmAppend(StmSuffix(input, n - 1)(), C(42)(U8))()
      .tchk()
      .lower()
      .asInstanceOf[StmBuild]
    val optimize = (s: StmBuild) => {
      val s1 = s.fuseCompletely()
      val s2 = s1.tchk().lower().asInstanceOf[StmBuild]
      val s3 = stmBuildSimplifier.simplify(s2)()
      s3
    }
    val fused = optimize(original)

    // Correct behaviour
    val call = (e: Expr) => Let(input, StmCount(C(n)(U8))(), e)()
    val expectedElems = StmLiteral.ints(1, 2, 3, 4, 42)
    assert(mhir.eval.eval(call(original)) == expectedElems)
    assert(mhir.eval.eval(call(fused)) == expectedElems)
    // Successful fusion
    val s = Param("s")(TyStm(U8, -1))
    val i = Param("i")(U8)
    val j = Param("j")(U8)
    val ideal = optimize(
      StmBuild(
        n,
        Mux(i === 4, C(42)(U8), StmData(s)())(),
        (i === 4) || (j >= 1),
        Map[Param, (Expr, Expr)](
          s -> (input, (i !== 4) || (j < 1)),
          i -> (
            C(0)(U8),
            Mux(
              (i === 4) || (j >= 1),
              Mux(i === 4, C(4)(U8), 1 + i)(),
              i
            )()
          ),
          j -> (C(0)(U8), Mux((i !== 4) || (j < 1), j + 1, j)())
        )
      )().tchk().lower().asInstanceOf[StmBuild]
    )
    assert(fused == ideal)
  }

  /** StmSplit and StmJoin cancel out.
    */
  test("StmSplitJoin") {
    val n = Param("n")(U8)
    val m = Param("m")(U8)
    val s = Param("s")(TyStm(U8, n))
    val original = StmJoin(StmSplit(s, m)())()
    // This is basically free due to the way lowering works
    val optimized = original.tchk().lower()

    // Effective simplification
    val ideal = s
    assert(optimized == ideal)
  }

  /** StmJoin and StmSplit cancel out.
    */
  test("StmJoinSplit") {
    val n = Param("n")(U8)
    val m = Param("m")(U8)
    val s = Param("s")(TyStm(TyStm(U8, m), n))
    val original = StmSplit(StmJoin(s)(), m)()
    // This is basically free due to the way lowering works
    val optimized = original.tchk().lower()

    // Effective simplification
    val ideal = s
    assert(optimized == ideal)
  }

  /** The conversion of a vector with statically-known `f` but unknown `n` to a
    * stream can be optimized (no vector in the final result, just compute the
    * `i`th element directly).
    */
  test("Vec2Stm(VecBuild(n, f))") {
    val f = Param("f")(TyArrow(U16, U16))
    val n = Param("n")(U16)
    val v = VecBuild(n, U16 ::+ (i => FunCall(f, i)()))()
    val s = Vec2StmOld(v)().tchk().lower()
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: Expr) => {
      val s0 = tl(s)
      val s1 = tl(stmBuildSimplifier.simplify(s0)())
      val s2 = tl({
        val facts = FactSet().range(s1, StmAccRangeAnalysis.findAccRanges(s1))
        stmBuildSimplifier.simplify(s1)(facts)
      })
      s2
    }
    val optimized = optimize(s)

    // Correctness
    val n0 = 2
    val f0 = U16 ::+ (i => i + 5)
    val expected0 = StmLiteral.ints(5, 6)
    val actual0 = (s: Expr) => Let(n, C(n0)(U16), Let(f, f0, s)())()
    assert(mhir.eval.eval(actual0(s)) == expected0)
    assert(mhir.eval.eval(actual0(optimized)) == expected0)
    val n1 = 15
    val f1 = U16 ::+ (i => (i + 1) * (i + 2) * (i + 3))
    val expected1 = StmLiteral(
      (0 until n1).map(i => IntCst((i + 1) * (i + 2) * (i + 3))()): _*
    )()
    val actual1 = (s: Expr) => Let(n, C(n1)(U16), Let(f, f1, s)())()
    assert(mhir.eval.eval(actual1(s)) == expected1)
    assert(mhir.eval.eval(actual1(optimized)) == expected1)

    // Effective simplification
    val i = Param("i")(U32)
    val ideal = StmBuild(
      n,
      FunCall(f, TruncateTo(i, 16)())(),
      True,
      Map[Param, (Expr, Expr)](i -> (C(0)(U32), Sum(C(1)(U32), i)()))
    )().tchk().lower()
    assert(optimized == ideal)
  }

  /** The conversion of a constant stream of unknown length into a vector can be
    * optimized (no delay, just return the vector directly).
    *
    * Although this transformation is possible in theory, in practice it is very
    * brittle and I am sick of updating this test each and every time I make
    * some unrelated change to the partial evaluator. Therefore, this test is
    * disabled. Good luck to anyone who wants to re-enable it.
    */
  ignore("Stm2Vec(StmCst(n, c)") {
    val n = Param("n")(U8)
    val c = Param("c")(U8)
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: Expr) => {
      val v0 = tl(s)
      val v1 = tl(v0.fuseCompletely())
      val v2 = tl(StmInductionVarRemovalPass().removeInductionVars(v1))
      val v3 = tl(stmBuildSimplifier.simplify(v2)())
      val v4 =
        tl(StmDelayRemovalPass.skipFirstCycles(v3, (n - 1).tchk().lower())())
      val v5 = tl({
        val facts = FactSet().range(v4, StmAccRangeAnalysis.findAccRanges(v4))
        PE.partialEval(v4)(facts).asInstanceOf[StmBuild]
      })
      tl(StmAccRemovalPass.removeUnusedVars(v5))
    }
    val v = optimize(Stm2Vec(StmCst(n, c)())())

    // Correctness
    val cExamples: Seq[Expr] = Seq(C(42)(U8), C(0)(U8))
    for (cVal <- cExamples) {
      for (nVal <- Seq(1, 2, 5)) {
        val expected =
          StmLiteral(
            VecLiteral((0 until nVal).map(_ => mhir.eval.eval(cVal)): _*)()
          )()
        val actual = Let(n, C(nVal)(U8), Let(c, cVal, v)())()
        val actualVal = mhir.eval.eval(actual)
        assert(actualVal == expected)
      }
    }

    // Effective simplification
    val ideal = optimize(StmCst(1, VecBuild(n, U8 ::+ (_ => c))())())
    assert(v == ideal)
  }

  /** The conversion of an arbitrary counter of unknown length into a vector can
    * be optimized (no delay, just return the vector directly).
    *
    * Although this transformation is possible in theory, in practice it is very
    * brittle and I am sick of updating this test each and every time I make
    * some unrelated change to the partial evaluator. Therefore, this test is
    * disabled. Good luck to anyone who wants to re-enable it.
    */
  ignore("Stm2Vec(StmRange(n, z, delta))", Slow) {
    val n = Param("n")(U16)
    val z = Param("z")(I16)
    val delta = Param("delta")(I16)
    val s = StmRange(n, z, delta)()
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: Expr) => {
      val v0 = tl(s)
      val v1 = tl(v0.fuseCompletely())
      val v2 = tl(StmInductionVarRemovalPass().removeInductionVars(v1))
      val v3 = tl(stmBuildSimplifier.simplify(v2)())
      val v4 =
        tl(StmDelayRemovalPass.skipFirstCycles(v3, (n - 1).tchk().lower())())
      val v5 = tl({
        val facts = FactSet().range(v4, StmAccRangeAnalysis.findAccRanges(v3))
        PE.partialEval(v4)(facts).asInstanceOf[StmBuild]
      })
      tl(StmAccRemovalPass.removeUnusedVars(v5))
    }
    val v = optimize(Stm2Vec(StmRange(n, z, delta)())())

    // Correctness
    for (nVal <- 0 to 10) {
      for (zVal <- -5 to 5) {
        for (deltaVal <- -5 to 5) {
          val expected = {
            val elems =
              mhir.eval
                .eval(
                  Let(
                    n,
                    C(nVal)(U16),
                    Let(z, C(zVal)(I16), Let(delta, C(deltaVal)(I16), s)())()
                  )()
                )
                .asInstanceOf[StmLiteral]
                .elems
            StmLiteral(VecLiteral(elems: _*)())()
          }
          val actual = Let(
            n,
            C(nVal)(U16),
            Let(z, C(zVal)(I16), Let(delta, C(deltaVal)(I16), v)())()
          )()
          assert(mhir.eval.eval(actual) == expected)
        }
      }
    }

    // Effective simplification
    assert(v.valid == True, "(there should be no delay)")
    assert(v.equations.isEmpty, "(there should be no accumulators)")
  }

  /** Vec2Stm(Stm2Vec(s)) --> s
    */
  ignore("Vec2Stm(Stm2Vec(s))") {
    val n = Param("n")(U16)
    val s = Param("s")(TyStm(U16, n))
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: StmBuild) => {
      // TODO: Can I get it to work for n >= 1 rather than n >= 2?
      val facts = FactSet().geq(n, 2)
      val s0 = tl(stmBuildSimplifier.simplify(s)(facts))
      val s1 = tl(s0.fuseCompletely())
      val s2 = tl(stmBuildSimplifier.simplify(s1)(facts))

      // This step in the transformation chain is not working.
      // Each accumulator has a closed form, but replacing some accumulators
      // with their closed form is not working due to invalid access patterns
      // in `StmNextK`.
      // For example, in one case there is an expression like
      //     if ((2:i33 + (-1:i33 * pad_to(to_signed(n_1), 33)) + t_8846) === pad_to(to_signed(n_1), 33)) then {
      //       StmData(StmNextK(s_2, -1:i33 + pad_to(to_signed(n_1), 33)))
      //     } else {
      //       StmData(StmNextK(s_2, 1:i33 + (-1:i33 * pad_to(to_signed(n_1), 33)) + t_8846))
      //     }
      // In the `true` branch, the StmNextK is invalid (it is repeatedly
      // reading index `n - 1`, which is not necessarily the first element.
      // However, we could simplify the expression to the following, since the
      // `true` branch is a special case of the `false` branch.
      //       StmData(StmNextK(s_2, 1:i33 + (-1:i33 * pad_to(to_signed(n_1), 33)) + t_8846))
      // The challenge in actually implementing this transformation is that the
      // condition includes *two* variables: t and n.
      // We need to be careful to not end up in a situation where the range of
      // `n` depends on `t` and the range of `t` depends on `n`.
      // One possible solution would be to declare `n` "static" (i.e., its
      // value must be provided at some point before hardware generation) and
      // say that the range of a non-static variable may depend on static
      // variables.
      // Then the condition boils down to `t == 2*n - 2`, and if this is true
      // then `1 - n + t == n - 1`, and therefore the one branch is a special
      // case of the other.

      val s3 = tl(StmInductionVarRemovalPass(facts).removeInductionVars(s2))
      val s4 = tl(stmBuildSimplifier.simplify(s3)(facts))
      val s5 = tl(
        StmDelayRemovalPass.skipFirstCycles(s4, (n - 1).tchk().lower())(facts)
      )
      val s6 = tl(stmBuildSimplifier.simplify(s5)(facts))
      // Reset `t` to start at zero rather than n - 1
      val s7 = tl(StmInductionVarRemovalPass(facts).removeInductionVars(s6))
      val newFacts = facts.range(s7, StmAccRangeAnalysis.findAccRanges(s7))
      val s8 = tl(stmBuildSimplifier.simplify(s7)(newFacts))
      s8
    }
    val original =
      tl(StmMap(Stm2Vec(s)(), TyVec(U16, n) ::+ (v => Vec2StmOld(v)()))())
    val optimized = optimize(original)

    // Correctness
    val examples = Seq(
      StmCst(n, C(42)(U16))(),
      StmRange(n, C(1)(U16), C(5)(U16))()
    )
    for (stm <- examples) {
      for (nVal <- Seq(2, 3, 10)) {
        val expected = Let(n, C(nVal)(U16), Let(s, stm, original)())().tchk()
        val expectedVal = mhir.eval.eval(expected)
        val actual = Let(n, C(nVal)(U16), Let(s, stm, optimized)())().tchk()
        val actualVal = mhir.eval.eval(actual)
        assert(actualVal == expectedVal)
      }
    }

    // Effective simplification
    val a = Param("a")(TyStm(U16, -1))
    val expected = tl(
      StmBuild(
        n,
        StmData(a)(),
        True,
        Map[Param, (Expr, Expr)](
          a -> (s, True)
        )
      )()
    )
    assert(optimized == expected)
  }

  /** Stm2Vec(Vec2Stm(v)) --> StmCst(1, v)
    */
  test("Stm2Vec(Vec2Stm(v))", Slow) {
    val n = Param("n")(U16)
    val v = Param("v")(TyVec(U16, n))
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: StmBuild) => {
      val s1 = tl(s.fuseCompletely())
      val s2 = tl(simplifier.simplify(s1)())
      val s3 = tl(StmInductionVarRemovalPass().removeInductionVars(s2))
      val s4 = tl(simplifier.simplify(s3)())
      val facts = FactSet().range(s4, StmAccRangeAnalysis.findAccRanges(s4))
      val s5 = tl(simplifier.simplify(s4)(facts))
      val s6 =
        tl(StmDelayRemovalPass.skipFirstCycles(s5, (n - 1).tchk().lower())())
      val s7 = tl(PE.partialEvalStmBuild(s6))
      tl(simplifier.simplify(s7)())
    }
    val original = tl(Stm2Vec(Vec2StmOld(v)())())
    val optimized = optimize(original)

    // Correctness
    val examples = Seq(
      VecBuild(n, U16 ::+ (i => i))().tchk(),
      VecBuild(n, U16 ::+ (i => i * i + 1))().tchk()
    )
    for (vec <- examples) {
      for (nVal <- Seq(1, 2, 10)) {
        val expected =
          original.subPreserveType(v -> vec).subPreserveType(n -> C(nVal)(U16))
        val actual =
          optimized.subPreserveType(v -> vec).subPreserveType(n -> C(nVal)(U16))
        assert(mhir.eval.eval(actual) == mhir.eval.eval(expected))
      }
    }

    // Effective simplification
    assert(optimized.valid == True, "(there should be no delay)")
    assert(optimized.equations.isEmpty, "(there should be no accumulators)")
  }

  test("VecReverse(VecReverse(v))") {
    val n = Param("n")(U8)
    val v = Param("v")(TyVec(U8, n))
    val original = VecReverse(VecReverse(v)).tchk().lower()
    val optimized = PE.partialEval(original)
    val expected = VecBuild(n, U8 ::+ (i => VecAccess(v, i)()))()
    assert(optimized == expected)
  }

  ignore("StmReverse(StmReverse(s))") {
    val n = Param("n")(U8)
    val s = Param("s")(TyStm(U8, n))
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: StmBuild) => {
      val facts = FactSet().geq(n, 1)
      val s0 = tl(PE.partialEval(s)(facts))
      val s1 = tl(s0.fuseCompletely())
      val s2 = tl(StmInductionVarRemovalPass(facts).removeInductionVars(s1))
      // TODO: It should be able to do both in one step
      val s3 = tl(StmAccRemovalPass.removeUnusedVars(s2))
      val s4 = tl(StmAccRemovalPass.removeUnusedVars(s3))
      val s5 = tl(StmDelayRemovalPass.skipFirstCycles(s4, n)(facts))
      val s6 = tl(
        PE
          .partialEval(s5)(
            facts.range(s5, StmAccRangeAnalysis.findAccRanges(s5))
          )
      )
      val s7 = tl(StmAccRemovalPass.removeUnusedVars(s6))
      s7
    }
    val original = tl(StmReverse(StmReverse(s)())())
    val optimized = optimize(original)

    // Correctness
    val examples = Seq(
      StmCst(n, 42)(),
      StmCst(n, 99)(),
      StmCst(n, -1)(),
      StmRange(n, 1, 5)()
    )
    for (stm <- examples) {
      for (nVal <- Seq(1, 2, 10)) {
        val expected = Let(n, C(nVal)(U8), Let(s, stm, original)())().tchk()
        val actual = Let(n, C(nVal)(U8), Let(s, stm, optimized)())().tchk()
        assert(mhir.eval.eval(actual) == mhir.eval.eval(expected))
      }
    }

    // Effective simplification
    val a = Param("a")()
    val identity = StmBuild(
      n,
      StmData(a)(),
      True,
      Map[Param, (Expr, Expr)](a -> (s, True))
    )()
    assert(optimized == identity)
  }

  /** VecTranspose(VecTranspose(v)) --> v
    */
  ignore("VecTranspose(VecTranspose(v))") {
    val n = Param("n")(U8)
    val m = Param("n")(U8)
    val v = Param("v")(TyVec(TyVec(U8, m), n))
    val tt = VecTranspose(VecTranspose(v))
    val optimized = PE.partialEval(tt)
    // TODO: I need some way to essentially eta-reduce a 2D VecBuild
    assert(optimized == v)
  }

  /** StmTranspose(StmTranspose(s)) --> s
    */
  ignore("StmTranspose(StmTranspose(s))") {
    // TODO: Why is this so slow?! It seems like fusion is taking forever
    val n = Param("n")()
    val m = Param("m")()
    val s = Param("s")()
    val original = {
      val t = StmTranspose(s)()
      val tt = StmTranspose(t)()
      tt
    }
    val optimized = {
      val s0 = PE.partialEval(original).asInstanceOf[StmBuild]
      val s1 = s0.fuseCompletely()
      val s2 = StmInductionVarRemovalPass().removeInductionVars(s1)
      PE.partialEval(s2)
    }

    // Correctness
    val examples = Seq(
      StmCst2D(n, m, 42)(),
      StmCst2D(n, m, -1)(),
      StmCount2D(n, m)()
    )
    for (stm <- examples) {
      for (nVal <- Seq(1, 2, 10)) {
        for (mVal <- Seq(1, 2, 10)) {
          val expected = Let(n, nVal, Let(m, mVal, Let(s, stm, original)())())()
          val actual = Let(n, nVal, Let(m, mVal, Let(s, stm, optimized)())())()
          assert(mhir.eval.eval(actual) == mhir.eval.eval(expected))
        }
      }
    }

    // Effective simplification
    val ideal = s
    assert(optimized == ideal)
  }
}
