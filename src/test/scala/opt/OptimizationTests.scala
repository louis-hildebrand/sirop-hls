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
    val expected0 = Seq(5, 6).map(n => IntCst(n))
    val actual0 = (s: Expr) => Let(n, n0, Let(f, f0, s))
    assert(StreamTests.stm2Seq(actual0(s)) == expected0)
    assert(StreamTests.stm2Seq(actual0(actual)) == expected0)
    val n1 = 15
    val f1 = (i: Expr) => (i + 1) * (i + 2) * (i + 3)
    val expected1 = (0 until n1).map(i => IntCst((i + 1) * (i + 2) * (i + 3)))
    val actual1 = (s: Expr) => Let(n, n1, Let(f, f1, s))
    assert(StreamTests.stm2Seq(actual1(s)) == expected1)
    assert(StreamTests.stm2Seq(actual1(actual)) == expected1)

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
    val v =
      StmCanonPass.canonicalize(
        StmInductionVarRemovalPass.removeInductionVars(
          StmFusePass.fuseCompletely(Stm2Vec(s, n = StmLength(s)))
        )
      )

    // Correctness
    val expected = (n: Int) => Seq((0 until n).map(_ => c))
    val actual = (nVal: Int) =>
      StreamTests.stm2Seq(Let(n, nVal, v)).map(v => VectorTests.vec2Seq(v))
    assert(actual(0) == expected(0))
    assert(actual(1) == expected(1))
    assert(actual(2) == expected(2))
    assert(actual(3) == expected(3))
    assert(actual(15) == expected(15))

    // Effective simplification
    val ideal = StmBuild(
      1,
      Tuple(),
      (acc: Expr) => Tuple(Tuple(), SSome(VecBuild(n, (_: Expr) => c)))
    )
    // TODO: update `valid` expression as well and then remove the unnecessary counter
    // assert(v == ideal)
    assert(
      v.seed.asInstanceOf[Tuple].elems.forall(e => !e.isInstanceOf[VecBuild])
    )
  }

  /** The conversion of an arbitrary counter of unknown length into a vector can
    * be optimized (no delay, just return the vector directly).
    */
  test("Stm2Vec(StmRange(n, z, delta))") {
    val n = Param()
    val z = Param()
    val delta = Param()
    val s = StmRange(n, z, delta)
    val v =
      StmCanonPass.canonicalize(
        StmInductionVarRemovalPass.removeInductionVars(
          StmFusePass.fuseCompletely(Stm2Vec(s, n = StmLength(s)))
        )
      )

    // Correctness
    val expected =
      (n: Int, z: Int, delta: Int) =>
        Seq((0 until n).map(i => IntCst(z + i * delta)))
    val actual = (nVal: Int, zVal: Int, deltaVal: Int) =>
      StreamTests
        .stm2Seq(Let(n, nVal, Let(z, zVal, Let(delta, deltaVal, v))))
        .map(v => VectorTests.vec2Seq(v))
    for (n <- 0 to 10) {
      for (z <- -5 to 5) {
        for (delta <- -5 to 5) {
          assert(actual(n, z, delta) == expected(n, z, delta))
        }
      }
    }

    // Effective simplification
    val ideal = StmBuild(
      1,
      Tuple(),
      (acc: Expr) =>
        Tuple(Tuple(), SSome(VecBuild(n, (i: Expr) => z + i * delta)))
    )
    // TODO: update `valid` expression as well and then remove the unnecessary counter
    // assert(v == ideal)
    assert(
      v.seed.asInstanceOf[Tuple].elems.forall(e => !e.isInstanceOf[VecBuild])
    )
  }
}
