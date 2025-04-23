package opt

import ir._
import operations._
import org.scalatest.funsuite.AnyFunSuite

class OptimizationTests extends AnyFunSuite {

  /** The optimizer can perform map-map fusion.
    */
  test("MapMap") {
    val input = Param("input")
    val f = (x: Expr) => (x + 2) * (x + 3) * (x + 4)
    val g = (x: Expr) => x - 10
    val s =
      StmMap(
        StmMap(input, f, n = 5, fInShape = None, fOutShape = None),
        g,
        n = 5,
        fInShape = None,
        fOutShape = None
      )
    val optimize = (s: StmBuild) => {
      val s1 = s.fuseCompletely()
      StmSimplifier.simplify(s1)()
    }
    val actual = optimize(s.asInstanceOf[StmBuild])

    // Correct behaviour
    // (Using one example input, f, and g)
    val call = (e: Expr) => Let(input, StmCount(5), e)()
    val expectedElems = StmLiteral.ints(14, 50, 110, 200, 326)
    assert(ir.eval(call(s)) == expectedElems)
    assert(ir.eval(call(actual)) == expectedElems)
    // Successful fusion:
    // map(map(s, f), g) should simplify to the same thing as map(s, g . f)
    val ideal = optimize(
      StmMap(
        input,
        (x: Expr) => FunCall(g, FunCall(f, x)())(),
        n = 5,
        fInShape = None,
        fOutShape = None
      ).asInstanceOf[StmBuild]
    )
    assert(actual == ideal)
  }

  /** The optimizer can perform map-fold fusion.
    */
  test("MapFold") {
    val input = Param("input")
    val n = Param("n")
    val f = (x: Expr) => (x + 2) * (x + 3) * (x + 4)
    val z = Param("z")
    val s =
      StmFold(
        StmMap(input, f, n = n, fInShape = None, fOutShape = None),
        z,
        (acc: Expr) => (x: Expr) => acc + x,
        stmShape = Seq(n)
      )
    val optimize = (s: StmBuild) => {
      val s1 = s.fuseCompletely()
      StmSimplifier.simplify(s1)()
    }
    val actual = optimize(s)

    // Correct behaviour
    // (Using one example input, f, g, and z)
    val call =
      (e: Expr) => Let(n, 5, Let(input, StmCount(n), Let(z, 42, e)())())()
    val expected =
      StmLiteral(
        ir.eval(
          42
            + FunCall(f, 0)()
            + FunCall(f, 1)()
            + FunCall(f, 2)()
            + FunCall(f, 3)()
            + FunCall(f, 4)()
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
        (acc: Expr) => (x: Expr) => acc + (x + 2) * (x + 3) * (x + 4),
        stmShape = Seq(n)
      )
    )
    assert(actual == ideal)
  }

  test("FuseStmShiftRight") {
    val input = Param("input")
    val original = StmPrepend(
      StmPrefix(input, StmLength(input)() - 1, shape = Seq(5)),
      42,
      stmShape = Seq(4)
    )
    val fused = original.asInstanceOf[StmBuild].fuseCompletely()

    // Correct behaviour
    // (Using one example input)
    val call = (e: Expr) => Let(input, StmCount(5), e)()
    val expectedElems = StmLiteral.ints(42, 0, 1, 2, 3)
    assert(ir.eval(call(original)) == expectedElems)
    assert(ir.eval(call(fused)) == expectedElems)
    // Successful fusion
    val s = Param("s")
    val i = Param("i")
    val j = Param("j")
    val ideal = StmBuild(
      5,
      IfThenElse(
        i === 1,
        IfThenElse(
          j < -1 + StmLength(input)(),
          SSome(StmNext(s)().__1)(),
          NNone(???)
        ),
        SSome(42)()
      ),
      Map[Param, (Expr, Expr)](
        s -> (input, IfThenElse(i === 1, StmNext(s)().__0, s)),
        i -> (0, IfThenElse(i === 1, i, i + 1)),
        j -> (0, IfThenElse(i === 1, j + 1, j))
      )
    )()
    val fusedAndSimplified =
      PartialEvalPass.partialEval(StmAccRemovalPass.removeConstantVars(fused))
    assert(fusedAndSimplified == ideal)
  }

  test("FuseStmShiftLeft") {
    val input = Param("input")
    val n = 5
    val original =
      StmAppend(StmSuffix(input, n - 1, shape = Seq(5)), 42, stmShape = Seq(4))
    val fused = original.asInstanceOf[StmBuild].fuseCompletely()

    // Correct behaviour
    val call = (e: Expr) => Let(input, StmCount(n), e)()
    val expectedElems = StmLiteral.ints(1, 2, 3, 4, 42)
    assert(ir.eval(call(original)) == expectedElems)
    assert(ir.eval(call(fused)) == expectedElems)
    // Successful fusion
    val s = Param("s")
    val i = Param("i")
    val j = Param("j")
    val ideal =
      StmBuild(
        n,
        IfThenElse(
          i === 4,
          SSome(42)(),
          IfThenElse(j < 1, NNone(???), SSome(StmNext(s)().__1)())
        ),
        Map[Param, (Expr, Expr)](
          s -> (input, IfThenElse(i === 4, s, StmNext(s)().__0)),
          i -> (0, IfThenElse(i === 4, i, IfThenElse(j < 1, i, i + 1))),
          j -> (0, IfThenElse(i === 4, j, j + 1))
        )
      )()
    val fusedAndSimplified =
      PartialEvalPass.partialEval(StmAccRemovalPass.removeConstantVars(fused))
    assert(fusedAndSimplified == ideal)
  }

  /** The conversion of a vector with statically-known `f` but unknown `n` to a
    * stream can be optimized (no vector in the final result, just compute the
    * `i`th element directly).
    */
  test("Vec2Stm(VecBuild(n, f))") {
    val f = Param("f")
    val n = Param("n")
    val v = VecBuild(n, (i: Expr) => FunCall(f, i)())()
    val s = Vec2Stm(v, n = VecLength(v)())
    // This optimization is basically free just from partial evaluation.
    val actual = PartialEvalPass.partialEval(s)

    // Correctness
    val n0 = 2
    val f0 = (i: Expr) => i + 5
    val expected0 = StmLiteral.ints(5, 6)
    val actual0 = (s: Expr) => Let(n, n0, Let(f, f0, s)())()
    assert(ir.eval(actual0(s)) == expected0)
    assert(ir.eval(actual0(actual)) == expected0)
    val n1 = 15
    val f1 = (i: Expr) => (i + 1) * (i + 2) * (i + 3)
    val expected1 = StmLiteral(
      (0 until n1).map(i => IntCst((i + 1) * (i + 2) * (i + 3))): _*
    )()
    val actual1 = (s: Expr) => Let(n, n1, Let(f, f1, s)())()
    assert(ir.eval(actual1(s)) == expected1)
    assert(ir.eval(actual1(actual)) == expected1)

    // Effective simplification
    val i = Param("i")
    val ideal = StmBuild(
      n,
      SSome(FunCall(f, i)())(),
      Map[Param, (Expr, Expr)](i -> (0, i + 1))
    )()
    assert(actual == ideal)
  }

  /** The conversion of a constant stream of unknown length into a vector can be
    * optimized (no delay, just return the vector directly).
    */
  test("Stm2Vec(StmCst(n, c))") {
    val n = Param("n")
    val c = Param("c")
    val s = StmCst(n, c)
    val v = {
      val v0 = Stm2Vec(s, n = StmLength(s)()).fuseCompletely()
      val v1 = StmInductionVarRemovalPass().removeInductionVars(v0)
      val v2 = StmSimplifier.simplify(v1)()
      val v3 = StmDelayRemovalPass.skipFirstCycles(v2, n - 1)()
      val v4 = {
        val facts = FactSet().range(v3, StmAccRangeAnalysis.findAccRanges(v3))
        PartialEvalPass.partialEval(v3)(facts).asInstanceOf[StmBuild]
      }
      StmAccRemovalPass.removeUnusedVars(v4)
    }

    // Correctness
    val cExamples: Seq[Expr] = Seq(IntCst(42), IntCst(0), Tuple(False, -99)())
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
    val ideal = StmCst(1, VecBuild(n, (_: Expr) => c)())
    assert(v == ideal)
  }

  /** The conversion of an arbitrary counter of unknown length into a vector can
    * be optimized (no delay, just return the vector directly).
    */
  test("Stm2Vec(StmRange(n, z, delta))") {
    val n = Param("n")
    val z = Param("z")
    val delta = Param("delta")
    val s = StmRange(n, z, delta)
    val v = {
      val v0 = Stm2Vec(s, n = StmLength(s)()).fuseCompletely()
      val v1 = StmInductionVarRemovalPass().removeInductionVars(v0)
      val v2 = StmSimplifier.simplify(v1)()
      val v3 = StmDelayRemovalPass.skipFirstCycles(v2, n - 1)()
      val facts = FactSet().range(v3, StmAccRangeAnalysis.findAccRanges(v3))
      PartialEvalPass.partialEval(v3)(facts).asInstanceOf[StmBuild]
    }

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
    val ideal = StmCst(1, VecBuild(n, (i: Expr) => z + i * delta)())
    assert(v == ideal)
  }

  /** Vec2Stm(Stm2Vec(s)) --> s
    */
  test("Vec2Stm(Stm2Vec(s))") {
    val n = Param("n")
    val s = Param("s")
    val original = StmMap(
      Stm2Vec(s, n = n),
      (v: Expr) => Vec2Stm(v, n = n),
      n = 1,
      fInShape = None,
      fOutShape = Some(n)
    ).asInstanceOf[StmBuild]
    val optimized = {
      // TODO: Can I get it to work for n >= 1 rather than n >= 2?
      val facts = FactSet().geq(n, 2)
      val s1 = original.fuseCompletely()
      val s2 = StmSimplifier.simplify(s1)(facts)
      val s3 = StmInductionVarRemovalPass(facts).removeInductionVars(s2)
      val s4 = StmSimplifier.simplify(s3)(facts)
      val s5 = StmDelayRemovalPass.skipFirstCycles(s4, n - 1)(facts)
      val s6 = StmSimplifier.simplify(s5)(facts)
      // Reset `t` to start at zero rather than n - 1
      val s7 = StmInductionVarRemovalPass(facts).removeInductionVars(s6)
      val newFacts = facts.range(s7, StmAccRangeAnalysis.findAccRanges(s7))
      StmSimplifier.simplify(s7)(newFacts)
    }

    // Correctness
    val examples = Seq(
      StmCst(n, 42),
      StmCst(n, 99),
      StmCst(n, -1),
      StmRange(n, 1, 5),
      StmRepeat(StmCount(n), m = 3, n = n)
    )
    for (stm <- examples) {
      for (nVal <- Seq(1, 2, 10)) {
        val expected = Let(n, nVal, Let(s, stm, original)())()
        val actual = Let(n, nVal, Let(s, stm, optimized)())()
        assert(ir.eval(actual) == ir.eval(expected))
      }
    }

    // Effective simplification
    val a = Param("a")
    val identity = StmBuild(
      n,
      SSome(StmNext(a)().__1)(),
      Map[Param, (Expr, Expr)](a -> (s, StmNext(a)().__0))
    )()
    assert(optimized == identity)
  }

  /** Stm2Vec(Vec2Stm(v)) --> StmCst(1, v)
    */
  test("Stm2Vec(Vec2Stm(v))") {
    val n = Param("n")
    val v = Param("v")
    val original = Stm2Vec(Vec2Stm(v, n = n), n = n)
    val optimized = {
      val s1 = original.fuseCompletely()
      val s2 = StmSimplifier.simplify(s1)()
      val s3 = StmInductionVarRemovalPass().removeInductionVars(s2)
      val s4 = StmSimplifier.simplify(s3)()
      val s5 = StmDelayRemovalPass.skipFirstCycles(s4, n - 1)()
      StmSimplifier.simplify(s5)()
    }

    // Correctness
    val examples = Seq(
      VecBuild(n, (i: Expr) => i)(),
      VecBuild(n, (i: Expr) => i * i + 1)()
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
    val ideal = StmCst(1, VecBuild(n, (i: Expr) => VecAccess(v, i)())())
    assert(optimized == ideal)
  }

  test("VecReverse(VecReverse(v))") {
    val v = Param("v")
    val original = VecReverse(VecReverse(v))
    val optimized = PartialEvalPass.partialEval(original)
    assert(optimized == v)
  }

  test("StmReverse(StmReverse(s))") {
    val n = Param("n")
    val s = Param("s")
    val original = StmReverse(StmReverse(s, n = n), n = n)
    val optimized = {
      val facts = FactSet().geq(n, 1)
      val s0 =
        PartialEvalPass.partialEval(original)(facts).asInstanceOf[StmBuild]
      val s1 = s0.fuseCompletely()
      val s2 = StmInductionVarRemovalPass(facts).removeInductionVars(s1)
      // TODO: It should be able to do both in one step
      val s3 = StmAccRemovalPass.removeUnusedVars(s2)
      val s4 = StmAccRemovalPass.removeUnusedVars(s3)
      val s5 = StmDelayRemovalPass.skipFirstCycles(s4, n)(facts)
      val s6 = PartialEvalPass
        .partialEval(s5)(
          facts.range(s5, StmAccRangeAnalysis.findAccRanges(s5))
        )
        .asInstanceOf[StmBuild]
      val s7 = StmAccRemovalPass.removeUnusedVars(s6)
      s7
    }

    // Correctness
    val examples = Seq(
      StmCst(n, 42),
      StmCst(n, 99),
      StmCst(n, -1),
      StmRange(n, 1, 5),
      StmRepeat(StmCount(n), m = 3, n = n)
    )
    for (stm <- examples) {
      for (nVal <- Seq(1, 2, 10)) {
        val expected = Let(n, nVal, Let(s, stm, original)())()
        val actual = Let(n, nVal, Let(s, stm, optimized)())()
        assert(ir.eval(actual) == ir.eval(expected))
      }
    }

    // TODO: Effective simplification
    assume(false)
    val a = Param("a")
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
    val v = Param("v")
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

    val n = Param("n")
    val m = Param("m")
    val s = Param("s")
    val original = {
      val t = StmTranspose(s, n, m)
      val tt = StmTranspose(t, m, n)
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
      StmCst2D(n, m, 42),
      StmCst2D(n, m, -1),
      StmCount2D(n, m)
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

    // TODO: Effective simplification
    assume(false)
    val ideal = s
    assert(optimized == ideal)
  }
}
