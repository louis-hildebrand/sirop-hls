package opt

import ir._
import operations._
import org.scalatest.funsuite.AnyFunSuite

class OptimizationTests extends AnyFunSuite {

  /** The conversion of a vector with statically-known `f` but unknown `n` to a
    * stream can be optimized (no vector in the final result, just compute the
    * `i`th element directly).
    */
  test("Vec2Stm(VecBuild(n, f))") {
    val f = Param()
    val n = Param()
    val v = VecBuild(n, (i: Expr) => FunCall(f, i))
    val s = Vec2Stm(v, n = VecLength(v))
    // This optimization is basically free just from partial evaluation.
    val actual = PartialEvalPass.partialEval(s)

    // Correctness
    val n0 = 2
    val f0 = (i: Expr) => i + 5
    val expected0 = StmLiteral.ints(5, 6)
    val actual0 = (s: Expr) => Let(n, n0, Let(f, f0, s))
    assert(ir.eval(actual0(s)) == expected0)
    assert(ir.eval(actual0(actual)) == expected0)
    val n1 = 15
    val f1 = (i: Expr) => (i + 1) * (i + 2) * (i + 3)
    val expected1 = StmLiteral(
      (0 until n1).map(i => IntCst((i + 1) * (i + 2) * (i + 3))): _*
    )
    val actual1 = (s: Expr) => Let(n, n1, Let(f, f1, s))
    assert(ir.eval(actual1(s)) == expected1)
    assert(ir.eval(actual1(actual)) == expected1)

    // Effective simplification
    val ideal =
      StmBuild(
        n,
        0,
        (i: Expr) => Tuple(1 + i, SSome(FunCall(f, i)))
      )
    assert(actual == ideal)
  }

  /** The conversion of a constant stream of unknown length into a vector can be
    * optimized (no delay, just return the vector directly).
    */
  test("Stm2Vec(StmCst(n, c))") {
    val n = Param()
    val c = Param()
    val s = StmCst(n, c)
    val v = {
      val v0 = StmFusePass.fuseCompletely(Stm2Vec(s, n = StmLength(s)))
      val v1 = StmInductionVarRemovalPass.removeInductionVars(v0)
      val v2 = StmCanonPass.canonicalize(v1)
      val v3 = StmDelayRemovalPass.skipFirstCycles(v2, n - 1)
      val v4 = {
        val facts = FactSet().range(v3, StmAccRangeAnalysis.findAccRanges(v3))
        PartialEvalPass.partialEval(v3)(facts).asInstanceOf[StmBuild]
      }
      StmAccRemovalPass.removeUnusedElems(v4)
    }

    // Correctness
    val cExamples: Seq[Expr] = Seq(IntCst(42), IntCst(0), Tuple(False, -99))
    for (cVal <- cExamples) {
      for (nVal <- Seq(0, 1, 2, 5)) {
        val expected =
          StmLiteral(
            VecLiteral((0 until nVal).map(_ => ir.eval(cVal)): _*)
          )
        val actual = Let(n, nVal, Let(c, cVal, v))
        assert(ir.eval(actual) == expected)
      }
    }

    // Effective simplification
    val ideal = StmBuild(
      1,
      Tuple(),
      (_: Expr) => Tuple(Tuple(), SSome(VecBuild(n, (_: Expr) => c)))
    )
    assert(v == ideal)
  }

  /** The conversion of an arbitrary counter of unknown length into a vector can
    * be optimized (no delay, just return the vector directly).
    */
  test("Stm2Vec(StmRange(n, z, delta))") {
    val n = Param()
    val z = Param()
    val delta = Param()
    val s = StmRange(n, z, delta)
    val v = {
      val v0 = StmFusePass.fuseCompletely(Stm2Vec(s, n = StmLength(s)))
      val v1 = StmInductionVarRemovalPass.removeInductionVars(v0)
      val v2 = StmCanonPass.canonicalize(v1)
      val v3 = StmDelayRemovalPass.skipFirstCycles(v2, n - 1)
      val facts = FactSet().range(v3, StmAccRangeAnalysis.findAccRanges(v3))
      PartialEvalPass.partialEval(v3)(facts).asInstanceOf[StmBuild]
    }

    // Correctness
    for (nVal <- 0 to 10) {
      for (zVal <- -5 to 5) {
        for (deltaVal <- -5 to 5) {
          val expected = {
            val elems =
              ir.eval(Let(n, nVal, Let(z, zVal, Let(delta, deltaVal, s))))
                .asInstanceOf[StmLiteral]
                .elems
            StmLiteral(VecLiteral(elems: _*))
          }
          val actual =
            ir.eval(Let(n, nVal, Let(z, zVal, Let(delta, deltaVal, v))))
          assert(ir.eval(actual) == expected)
        }
      }
    }

    // Effective simplification
    val ideal = StmBuild(
      1,
      Tuple(),
      (_: Expr) =>
        Tuple(Tuple(), SSome(VecBuild(n, (i: Expr) => z + i * delta)))
    )
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
    )
    val optimized = {
      val facts = FactSet().range(n, ScalarRange(Some(1), None))
      val s0 = PartialEvalPass.partialEval(original)(facts)
      val s1 = StmFusePass.fuseCompletely(s0)
      val s2 = StmInductionVarRemovalPass.removeInductionVars(s1)
      PartialEvalPass.partialEval(s2)(facts)
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
        val expected = Let(n, nVal, Let(s, stm, original))
        val actual = Let(n, nVal, Let(s, stm, optimized))
        assert(ir.eval(actual) == ir.eval(expected))
      }
    }

    // TODO: Effective simplification
    assume(false)
    val ideal = s
    assert(optimized == ideal)
  }

  /** Stm2Vec(Vec2Stm(v)) --> StmCst(1, v)
    */
  test("Stm2Vec(Vec2Stm(v))") {
    val n = Param("n")
    val v = Param("v")
    val original = Stm2Vec(Vec2Stm(v, n = n), n = n)
    val optimized = {
      val s1 = StmFusePass.fuseCompletely(original)
      val s2 = StmInductionVarRemovalPass.removeInductionVars(s1)
      val s3 = StmDelayRemovalPass.skipFirstCycles(s2, n - 1)
      StmInductionVarRemovalPass.removeInductionVars(s3)
    }

    // Correctness
    val examples = Seq(
      VecBuild(n, (i: Expr) => i),
      VecBuild(n, (i: Expr) => i * i + 1)
    )
    for (vec <- examples) {
      for (nVal <- Seq(1, 2, 10)) {
        val expected = Let(n, nVal, Let(v, vec, original))
        val actual = Let(n, nVal, Let(v, vec, optimized))
        assert(ir.eval(actual) == ir.eval(expected))
      }
    }

    // Effective simplification
    // TODO: It would be even better if I could essentially eta-reduce the vector
    val ideal = StmBuild(
      1,
      Tuple(),
      (_: Expr) =>
        Tuple(Tuple(), SSome(VecBuild(n, (i: Expr) => VecAccess(v, i))))
    )
    assert(optimized == ideal)
  }

  /** VecTranspose(VecTranspose(v)) --> v
    */
  test("VecTranspose(VecTranspose(v))") {
    val v = Param("v")
    val tt = VecTranspose(VecTranspose(v))
    val optimized = PartialEvalPass.partialEval(tt)
    // TODO: I need some way to essentially eta-reduce a VecBuild
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
      val s1 = StmFusePass.fuseCompletely(s0)
      val s2 = StmInductionVarRemovalPass.removeInductionVars(s1)
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
          val expected = Let(n, nVal, Let(m, mVal, Let(s, stm, original)))
          val actual = Let(n, nVal, Let(m, mVal, Let(s, stm, optimized)))
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
