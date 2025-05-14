package opt

import ir._
import operations._
import org.scalatest.funsuite.AnyFunSuite

class OptimizationTests extends AnyFunSuite {

  /** The optimizer can produce a nice design for a simple map on a 1D stream,
    * even though the lowering pass spits out something a bit gross (since it
    * must handle multi-dimensional streams).
    */
  test("Map:1D-1D") {
    val n = Param("n")(TyInt)
    val s = Param("s")(TyStm(TyInt, n))
    val f = Param("f")(TyArrow(TyInt, TyInt))
    val original = StmMap(s, TyInt ::+ (x => FunCall(f, x)()))()
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: Expr) => {
      val s1 = tl(s)
      val s2 = tl(StmSimplifier.simplify(s1)())
      s2
    }
    val optimized = optimize(original)

    // Correctness
    val nExamples = Seq(0, 1, 4)
    val sExamples = Seq(StmCst(n, 42)(), StmCount(n)())
    val fExamples =
      Seq(TyInt ::+ (x => x), TyInt ::+ (x => 99), TyInt ::+ (x => x * x))
    for (nVal <- nExamples) {
      for (sVal <- sExamples) {
        for (fVal <- fExamples) {
          val expected =
            ir.eval(
              Let(n, nVal, Let(s, sVal, Let(f, fVal, original)())())().tchk()
            )
          val actual =
            ir.eval(
              Let(n, nVal, Let(s, sVal, Let(f, fVal, optimized)())())().tchk()
            )
          assert(actual == expected)
        }
      }
    }

    // Effective simplification
    val sAcc = Param("s")(TyStm(TyInt, n))
    val ideal = StmBuild(
      n,
      SSome(FunCall(f, StmNext(sAcc)().__1)())(),
      Map[Param, (Expr, Expr)](sAcc -> (s, StmNext(sAcc)().__0))
    )().tchk().lower()
    assert(optimized == ideal)
  }

  /** The optimizer can turn VecFold into a simple sum *provided* the length is
    * a static constant.
    */
  test("VecFoldSimpleSum") {
    val n = 3
    val v = Param("v")(TyVec(TyInt, n))
    val z = Param("z")(TyInt)
    val original = VecFold(v, z, PlusFunction())()
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: Expr) => {
      val s0 = tl(s)
      val s1 = tl(s0.fuseCompletely())
      val s2 = tl(StmSimplifier.simplify(s1)())
      s2
    }
    val optimized = optimize(original)

    // Correctness
    val vExamples = Seq(
      VecBuild(n, TyInt ::+ (i => i))(),
      VecBuild(n, TyInt ::+ (i => i * (i + 1)))()
    )
    val zExamples = Seq(IntCst(0), IntCst(-3), IntCst(42))
    for (vVal <- vExamples) {
      for (zVal <- zExamples) {
        val expected = ir.eval(Let(v, vVal, Let(z, zVal, original)())().tchk())
        val actual = ir.eval(Let(v, vVal, Let(z, zVal, optimized)())().tchk())
        assert(actual == expected)
      }
    }

    // Effective simplification
    val ideal =
      StmCst(1, z + VecAccess(v, 0)() + VecAccess(v, 1)() + VecAccess(v, 2)())()
        .tchk()
        .lower()
    assert(optimized == ideal)
  }

  /** The optimizer can perform map-map fusion.
    */
  test("Fuse:MapMap") {
    val n = Param("n")(TyInt)
    val input = Param("input")(TyStm(TyInt, n))
    val f = TyInt ::+ (x => (x + 2) * (x + 3) * (x + 4))
    val g = TyInt ::+ (x => x - 10)
    val s = StmMap(StmMap(input, f)(), g)().tchk().lower()
    val optimize = (s: StmBuild) => {
      val s1 = s.fuseCompletely()
      val s2 = s1.tchk().lower().asInstanceOf[StmBuild]
      StmSimplifier.simplify(s2)()
    }
    val actual = optimize(s.asInstanceOf[StmBuild])

    // Correct behaviour
    // (Using one example input, f, and g)
    val call = (e: Expr) => Let(n, 5, Let(input, StmCount(n)(), e)())()
    val expectedElems = StmLiteral.ints(14, 50, 110, 200, 326)
    assert(ir.eval(call(s)) == expectedElems)
    assert(ir.eval(call(actual)) == expectedElems)
    // Successful fusion:
    // map(map(s, f), g) should simplify to the same thing as map(s, g . f)
    val ideal = optimize(
      StmMap(input, TyInt ::+ (x => FunCall(g, FunCall(f, x)())()))()
        .tchk()
        .lower()
        .asInstanceOf[StmBuild]
    )
    assert(actual == ideal)
  }

  /** The optimizer can perform map-fold fusion.
    */
  test("Fuse:MapFold") {
    val n = Param("n")(TyInt)
    val input = Param("input")(TyStm(TyInt, n))
    val f = TyInt ::+ (x => (x + 2) * (x + 3) * (x + 4))
    val z = Param("z")(TyInt)
    val s =
      StmFold(StmMap(input, f)(), z, PlusFunction())().tchk().lower()
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: Expr) => {
      val s0 = tl(s)
      val s1 = tl(s0.fuseCompletely())
      val s2 = tl(StmSimplifier.simplify(s1)())
      s2
    }
    val actual = optimize(s)

    // Correct behaviour
    // (Using one example input, f, g, and z)
    val call =
      (e: Expr) => Let(n, 3, Let(input, StmCount(n)(), Let(z, 42, e)())())()
    val expected =
      StmLiteral(
        ir.eval(
          42
            + FunCall(f, 0)()
            + FunCall(f, 1)()
            + FunCall(f, 2)()
        )
      )()
    assert(ir.eval(call(s)) == expected)
    assert(ir.eval(call(actual)) == expected)
    // Successful fusion:
    // fold(map(s, f), z, a => x => g(a, x)) should simplify to the same thing
    // as fold(s, z, a => x => g(a, f(x))
    val ideal = optimize(
      StmFold(
        input,
        z,
        TyInt ::+ (acc => TyInt ::+ (x => acc + FunCall(f, x)()))
      )()
    )
    assert(actual == ideal)
  }

  /** The optimizer can perform map-scan fusion.
    */
  test("Fuse:MapScan") {
    val n = Param("n")(TyInt)
    val input = Param("input")(TyStm(TyInt, n))
    val f = TyInt ::+ (x => (x + 2) * (x + 3) * (x + 4))
    val z = Param("z")(TyInt)
    val s =
      StmScanInclusive(StmMap(input, f)(), z, PlusFunction())().tchk().lower()
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: Expr) => {
      val s0 = tl(s)
      val s1 = tl(s0.fuseCompletely())
      val s2 = tl(StmSimplifier.simplify(s1)())
      s2
    }
    val actual = optimize(s)

    // Correct behaviour
    // (Using one example input, f, g, and z)
    val call =
      (e: Expr) => Let(n, 3, Let(input, StmCount(n)(), Let(z, 42, e)())())()
    val expected =
      StmLiteral(
        ir.eval(42 + FunCall(f, 0)()),
        ir.eval(42 + FunCall(f, 0)() + FunCall(f, 1)()),
        ir.eval(42 + FunCall(f, 0)() + FunCall(f, 1)() + FunCall(f, 2)())
      )()
    assert(ir.eval(call(s)) == expected)
    assert(ir.eval(call(actual)) == expected)
    // Successful fusion:
    // fold(map(s, f), z, a => x => g(a, x)) should simplify to the same thing
    // as fold(s, z, a => x => g(a, f(x))
    val ideal = optimize(
      StmScanInclusive(
        input,
        z,
        TyInt ::+ (acc => TyInt ::+ (x => acc + FunCall(f, x)()))
      )()
    )
    assert(actual == ideal)
  }

  /** StmSplit and StmJoin cancel out.
    */
  test("StmSplitJoin") {
    val n = Param("n")(TyInt)
    val m = Param("m")(TyInt)
    val s = Param("s")(TyStm(TyInt, n))
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
    val n = Param("n")(TyInt)
    val m = Param("m")(TyInt)
    val s = Param("s")(TyStm(TyStm(TyInt, m), n))
    val original = StmSplit(StmJoin(s)(), m)()
    // This is basically free due to the way lowering works
    val optimized = original.tchk().lower()

    // Effective simplification
    val ideal = s
    assert(optimized == ideal)
  }

  test("FuseStmShiftRight") {
    val n = Param("n")(TyInt)
    val input = Param("input")(TyStm(TyInt, n))
    val original = StmPrepend(StmPrefix(input, StmLength(input)() - 1)(), 42)()
      .tchk()
      .lower()
      .asInstanceOf[StmBuild]
    val optimize = (s: StmBuild) => {
      val s1 = s.fuseCompletely()
      val s2 = s1.tchk().lower().asInstanceOf[StmBuild]
      val s3 = StmSimplifier.simplify(s2)()
      s3
    }
    val fused = optimize(original)

    // Correct behaviour
    // (Using one example input)
    val call = (e: Expr) => Let(n, 5, Let(input, StmCount(n)(), e)())()
    val expectedElems = StmLiteral(42, 0, 1, 2, 3)()
    assert(ir.eval(call(original)) == expectedElems)
    assert(ir.eval(call(fused)) == expectedElems)
    // Successful fusion
    val s = Param("s")()
    val i = Param("i")()
    val j = Param("j")()
    val ideal = optimize(
      StmBuild(
        n,
        Mux(
          i === 1,
          Mux(
            j < -1 + StmLength(input)(),
            SSome(StmNext(s)().__1)(),
            NNone(TyInt)
          )(),
          SSome(42)()
        )(),
        Map[Param, (Expr, Expr)](
          s -> (input, Mux(i === 1, StmNext(s)().__0, s)()),
          i -> (0, Mux(i === 1, i, i + 1)()),
          j -> (0, Mux(i === 1, j + 1, j)())
        )
      )().tchk().lower().asInstanceOf[StmBuild]
    )
    assert(fused == ideal)
  }

  test("FuseStmShiftLeft") {
    val n = 5
    val input = Param("input")(TyStm(TyInt, n))
    val original = StmAppend(StmSuffix(input, n - 1)(), 42)()
      .tchk()
      .lower()
      .asInstanceOf[StmBuild]
    val optimize = (s: StmBuild) => {
      val s1 = s.fuseCompletely()
      val s2 = s1.tchk().lower().asInstanceOf[StmBuild]
      val s3 = StmSimplifier.simplify(s2)()
      s3
    }
    val fused = optimize(original)

    // Correct behaviour
    val call = (e: Expr) => Let(input, StmCount(n)(), e)()
    val expectedElems = StmLiteral.ints(1, 2, 3, 4, 42)
    assert(ir.eval(call(original)) == expectedElems)
    assert(ir.eval(call(fused)) == expectedElems)
    // Successful fusion
    val s = Param("s")()
    val i = Param("i")()
    val j = Param("j")()
    val ideal = optimize(
      StmBuild(
        n,
        Mux(
          i === 4,
          SSome(42)(),
          Mux(j < 1, NNone(TyInt), SSome(StmNext(s)().__1)())()
        )(),
        Map[Param, (Expr, Expr)](
          s -> (input, Mux(i === 4, s, StmNext(s)().__0)()),
          i -> (0, Mux(i === 4, i, Mux(j < 1, i, i + 1)())()),
          j -> (0, Mux(i === 4, j, j + 1)())
        )
      )().tchk().lower().asInstanceOf[StmBuild]
    )
    assert(fused == ideal)
  }

  /** The conversion of a vector with statically-known `f` but unknown `n` to a
    * stream can be optimized (no vector in the final result, just compute the
    * `i`th element directly).
    */
  test("Vec2Stm(VecBuild(n, f))") {
    val f = Param("f")(TyArrow(TyInt, TyInt))
    val n = Param("n")(TyInt)
    val v = VecBuild(n, TyInt ::+ (i => FunCall(f, i)()))()
    val s = Vec2Stm(v)().tchk().lower()
    // This optimization is basically free just from partial evaluation.
    val actual = PartialEvalPass.partialEval(s)

    // Correctness
    val n0 = 2
    val f0 = TyInt ::+ (i => i + 5)
    val expected0 = StmLiteral.ints(5, 6)
    val actual0 = (s: Expr) => Let(n, n0, Let(f, f0, s)())()
    assert(ir.eval(actual0(s)) == expected0)
    assert(ir.eval(actual0(actual)) == expected0)
    val n1 = 15
    val f1 = TyInt ::+ (i => (i + 1) * (i + 2) * (i + 3))
    val expected1 = StmLiteral(
      (0 until n1).map(i => IntCst((i + 1) * (i + 2) * (i + 3))): _*
    )()
    val actual1 = (s: Expr) => Let(n, n1, Let(f, f1, s)())()
    assert(ir.eval(actual1(s)) == expected1)
    assert(ir.eval(actual1(actual)) == expected1)

    // Effective simplification
    val i = Param("i")()
    val ideal = StmBuild(
      n,
      SSome(FunCall(f, i)())(),
      Map[Param, (Expr, Expr)](i -> (0, i + 1))
    )().tchk().lower()
    assert(actual == ideal)
  }

  /** The conversion of a constant stream of unknown length into a vector can be
    * optimized (no delay, just return the vector directly).
    */
  test("Stm2Vec(StmCst(n, c)") {
    val n = Param("n")(TyInt)
    val c = Param("c")(TyInt)
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: Expr) => {
      val v0 = tl(s)
      val v1 = tl(v0.fuseCompletely())
      val v2 = tl(StmInductionVarRemovalPass().removeInductionVars(v1))
      val v3 = tl(StmSimplifier.simplify(v2)())
      val v4 = tl(StmDelayRemovalPass.skipFirstCycles(v3, n - 1)())
      val v5 = tl({
        val facts = FactSet().range(v4, StmAccRangeAnalysis.findAccRanges(v3))
        PartialEvalPass.partialEval(v4)(facts).asInstanceOf[StmBuild]
      })
      tl(StmAccRemovalPass.removeUnusedVars(v5))
    }
    val v = optimize(Stm2Vec(StmCst(n, c)())())

    // Correctness
    val cExamples: Seq[Expr] = Seq(IntCst(42), IntCst(0))
    for (cVal <- cExamples) {
      for (nVal <- Seq(0, 1, 2, 5)) {
        val expected =
          StmLiteral(
            VecLiteral((0 until nVal).map(_ => ir.eval(cVal)): _*)()
          )()
        val actual = Let(n, nVal, Let(c, cVal, v)())()
        assert(ir.eval(actual) == expected)
      }
    }

    // Effective simplification
    val ideal = optimize(StmCst(1, VecBuild(n, TyInt ::+ (_ => c))())())
    assert(v == ideal)
  }

  /** The conversion of an arbitrary counter of unknown length into a vector can
    * be optimized (no delay, just return the vector directly).
    */
  test("Stm2Vec(StmRange(n, z, delta))") {
    val n = Param("n")(TyInt)
    val z = Param("z")(TyInt)
    val delta = Param("delta")(TyInt)
    val s = StmRange(n, z, delta)()
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: Expr) => {
      val v0 = tl(s)
      val v1 = tl(v0.fuseCompletely())
      val v2 = tl(StmInductionVarRemovalPass().removeInductionVars(v1))
      val v3 = tl(StmSimplifier.simplify(v2)())
      val v4 = tl(StmDelayRemovalPass.skipFirstCycles(v3, n - 1)())
      val v5 = tl({
        val facts = FactSet().range(v4, StmAccRangeAnalysis.findAccRanges(v3))
        PartialEvalPass.partialEval(v4)(facts).asInstanceOf[StmBuild]
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
              ir.eval(Let(n, nVal, Let(z, zVal, Let(delta, deltaVal, s)())())())
                .asInstanceOf[StmLiteral]
                .elems
            StmLiteral(VecLiteral(elems: _*)())()
          }
          val actual =
            ir.eval(Let(n, nVal, Let(z, zVal, Let(delta, deltaVal, v)())())())
          assert(ir.eval(actual) == expected)
        }
      }
    }

    // Effective simplification
    val ideal =
      optimize(StmCst(1, VecBuild(n, TyInt ::+ (i => z + i * delta))())())
    assert(v == ideal)
  }

  /** Vec2Stm(Stm2Vec(s)) --> s
    */
  test("Vec2Stm(Stm2Vec(s))") {
    val n = Param("n")(TyInt)
    val s = Param("s")(TyStm(TyInt, n))
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: StmBuild) => {
      // TODO: Can I get it to work for n >= 1 rather than n >= 2?
      val facts = FactSet().geq(n, 2)
      val s1 = tl(s.fuseCompletely())
      val s2 = tl(StmSimplifier.simplify(s1)(facts))
      val s3 = tl(StmInductionVarRemovalPass(facts).removeInductionVars(s2))
      val s4 = tl(StmSimplifier.simplify(s3)(facts))
      val s5 = tl(StmDelayRemovalPass.skipFirstCycles(s4, n - 1)(facts))
      val s6 = tl(StmSimplifier.simplify(s5)(facts))
      // Reset `t` to start at zero rather than n - 1
      val s7 = tl(StmInductionVarRemovalPass(facts).removeInductionVars(s6))
      val newFacts = facts.range(s7, StmAccRangeAnalysis.findAccRanges(s7))
      val s8 = tl(StmSimplifier.simplify(s7)(newFacts))
      s8
    }
    val original =
      tl(StmMap(Stm2Vec(s)(), TyVec(TyInt, n) ::+ (v => Vec2Stm(v)()))())
    val optimized = optimize(original)

    // Correctness
    val examples = Seq(
      StmCst(n, 42)(),
      StmCst(n, -1)(),
      StmRange(n, 1, 5)()
    )
    for (stm <- examples) {
      for (nVal <- Seq(1, 2, 10)) {
        val expected = Let(n, nVal, Let(s, stm, original)())().tchk()
        val actual = Let(n, nVal, Let(s, stm, optimized)())().tchk()
        assert(ir.eval(actual) == ir.eval(expected))
      }
    }

    // Effective simplification
    val a = Param("a")()
    val identity = tl(
      StmBuild(
        n,
        SSome(StmNext(a)().__1)(),
        Map[Param, (Expr, Expr)](a -> (s, StmNext(a)().__0))
      )()
    )
    assert(optimized == identity)
  }

  /** Stm2Vec(Vec2Stm(v)) --> StmCst(1, v)
    */
  test("Stm2Vec(Vec2Stm(v))") {
    val n = Param("n")(TyInt)
    val v = Param("v")(TyVec(TyInt, n))
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: StmBuild) => {
      val s1 = tl(s.fuseCompletely())
      val s2 = tl(StmSimplifier.simplify(s1)())
      val s3 = tl(StmInductionVarRemovalPass().removeInductionVars(s2))
      val s4 = tl(StmSimplifier.simplify(s3)())
      val s5 = tl(StmDelayRemovalPass.skipFirstCycles(s4, n - 1)())
      tl(StmSimplifier.simplify(s5)())
    }
    val original = tl(Stm2Vec(Vec2Stm(v)())())
    val optimized = optimize(original)

    // Correctness
    val examples = Seq(
      VecBuild(n, TyInt ::+ (i => i))(),
      VecBuild(n, TyInt ::+ (i => i * i + 1))()
    )
    for (vec <- examples) {
      for (nVal <- Seq(1, 2, 10)) {
        val expected = Let(n, nVal, Let(v, vec, original)())()
        val actual = Let(n, nVal, Let(v, vec, optimized)())()
        assert(ir.eval(actual) == ir.eval(expected))
      }
    }

    // Effective simplification
    // TODO: It would be even better if I could essentially eta-reduce the vector
    val ideal =
      tl(StmCst(1, VecBuild(n, TyInt ::+ (i => VecAccess(v, i)()))())())
    assert(optimized == ideal)
  }

  test("VecReverse(VecReverse(v))") {
    val v = Param("v")()
    val original = VecReverse(VecReverse(v))
    val optimized = PartialEvalPass.partialEval(original)
    assert(optimized == v)
  }

  test("StmReverse(StmReverse(s))") {
    assume(false)

    val n = Param("n")(TyInt)
    val s = Param("s")(TyStm(TyInt, n))
    val tl = (e: Expr) => e.tchk().lower().asInstanceOf[StmBuild]
    val optimize = (s: StmBuild) => {
      val facts = FactSet().geq(n, 1)
      val s0 = tl(PartialEvalPass.partialEval(s)(facts))
      val s1 = tl(s0.fuseCompletely())
      val s2 = tl(StmInductionVarRemovalPass(facts).removeInductionVars(s1))
      // TODO: It should be able to do both in one step
      val s3 = tl(StmAccRemovalPass.removeUnusedVars(s2))
      val s4 = tl(StmAccRemovalPass.removeUnusedVars(s3))
      val s5 = tl(StmDelayRemovalPass.skipFirstCycles(s4, n)(facts))
      val s6 = tl(
        PartialEvalPass
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
        val expected = Let(n, nVal, Let(s, stm, original)())().tchk()
        val actual = Let(n, nVal, Let(s, stm, optimized)())().tchk()
        assert(ir.eval(actual) == ir.eval(expected))
      }
    }

    // Effective simplification
    val a = Param("a")()
    val identity = StmBuild(
      n,
      SSome(StmNext(a)().__1)(),
      Map[Param, (Expr, Expr)](a -> (s, StmNext(a)().__0))
    )()
    assert(optimized == identity)
  }

  /** VecTranspose(VecTranspose(v)) --> v
    */
  test("VecTranspose(VecTranspose(v))") {
    val v = Param("v")()
    val tt = VecTranspose(VecTranspose(v))
    val optimized = PartialEvalPass.partialEval(tt)
    // TODO: I need some way to essentially eta-reduce a 2D VecBuild
    assume(false)
    assert(optimized == v)
  }

  /** StmTranspose(StmTranspose(s)) --> s
    */
  test("StmTranspose(StmTranspose(s))") {
    // TODO: Why is this so slow?! It seems like fusion is taking forever
    assume(false)

    val n = Param("n")()
    val m = Param("m")()
    val s = Param("s")()
    val original = {
      val t = StmTranspose(s)()
      val tt = StmTranspose(t)()
      tt
    }
    val optimized = {
      val s0 = PartialEvalPass.partialEval(original).asInstanceOf[StmBuild]
      val s1 = s0.fuseCompletely()
      val s2 = StmInductionVarRemovalPass().removeInductionVars(s1)
      PartialEvalPass.partialEval(s2)
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
          assert(ir.eval(actual) == ir.eval(expected))
        }
      }
    }

    // Effective simplification
    val ideal = s
    assert(optimized == ideal)
  }
}
