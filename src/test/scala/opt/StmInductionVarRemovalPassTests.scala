package opt

import ir._
import operations._
import org.scalatest.funsuite.AnyFunSuite

class StmInductionVarRemovalPassTests extends AnyFunSuite {
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
          Tuple(2 * acc.__0, acc.__1 / 3, acc.__2, acc.__3, acc.__4, acc.__5),
          True
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // Correctness
    val expected = (n: Int) =>
      (0 until n).map(i =>
        Tuple(
          IntCst(2 * (i + 3)),
          IntCst((10 + 4 * i) / 3),
          IntCst(19 - 2 * i),
          IntCst(3 * i),
          IntCst(-2 - 6 * i),
          PartialEvalPass.partialEval(1 + (delta + 1) * i)
        )
      )
    val actual = (nVal: Int) =>
      StreamTests
        .stm2Seq(Let(n, nVal, opt))
        .map(e => PartialEvalPass.partialEval(e))
    for (nVal <- 0 to 15) {
      assert(actual(nVal) == expected(nVal))
    }

    // Effective simplification
    val ideal =
      StmBuild(
        n,
        Tuple(0),
        (acc: Expr) =>
          Tuple(
            Tuple(acc.__0 + 1),
            Tuple(
              2 * (3 + acc.__0),
              (10 + acc.__0 * 4) / 3,
              19 + -2 * acc.__0,
              acc.__0 * 3,
              -2 + -6 * acc.__0,
              1 + (delta + 1) * acc.__0
            ),
            True
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
          acc.__1,
          acc.__2 >= n
        )
    )
    val optimized = StmInductionVarRemovalPass.removeInductionVars(triangleSum)

    // Correctness
    for (nVal <- 0 to 10) {
      val actual = StreamTests.stm2Seq(Let(n, nVal, optimized))
      val expected = StreamTests.stm2Seq(Let(n, nVal, triangleSum))
      assert(actual == expected)
    }

    // One counter can be removed, but not the sum
    val ideal = StmBuild(
      1,
      Tuple(0, 0),
      (acc: Expr) =>
        Tuple(
          Tuple(acc.__0 + acc.__1, acc.__1 + 1),
          acc.__0,
          acc.__1 >= n
        ),
    )
    assert(optimized == ideal)
  }

  test("NotCounter:Scan") {
    val n = Param("n")
    val s = Param("s")
    val scan = StmBuild(
      n,
      Tuple(s, 0),
      (acc: Expr) =>
        Tuple(
          Tuple(StmNext(acc.__0).__0, acc.__1 + StmNext(acc.__0).__1),
          acc.__1,
          True
        )
    )
    val optimized = StmInductionVarRemovalPass.removeInductionVars(scan)

    // Correctness
    for (nVal <- 0 to 10) {
      val s0 = StmRange(n, 1, 2)
      val expected0 = StreamTests.stm2Seq(Let(n, nVal, Let(s, s0, scan)))
      val actual0 = StreamTests.stm2Seq(Let(n, nVal, Let(s, s0, optimized)))
      assert(actual0 == expected0)

      val s1 =
        StmBuild(
          n,
          0,
          (i: Expr) => Tuple(i + 1, IfThenElse(i % 2 === 0, i, 1), True)
        )
      val expected1 = StreamTests.stm2Seq(Let(n, nVal, Let(s, s1, scan)))
      val actual1 = StreamTests.stm2Seq(Let(n, nVal, Let(s, s1, optimized)))
      assert(actual1 == expected1)
    }

    // acc.__0 kind of looks like a counter, but the `delta` is not constant
    assert(optimized == scan)
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
          VecShiftLeft(acc.__1, acc.__0),
          True
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // Correctness
    val actual = (nVal: Int, mVal: Int) =>
      StreamTests
        .stm2Seq(Let(n, nVal, Let(m, mVal, opt)))
        .map(v => VectorTests.vec2Seq(v))
    val c = (n: Int) => IntCst(n)
    assert(actual(1, 2) == Seq(Seq(c(1), c(100))))
    assert(
      actual(4, 3) == Seq(
        Seq(c(1), c(2), c(100)),
        Seq(c(2), c(100), c(102)),
        Seq(c(100), c(102), c(104)),
        Seq(c(102), c(104), c(106))
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
            ),
            True
          )
      )
    assert(opt == ideal)
  }

  test("MonotonicBoolSimpleCounter") {
    val n = Param()
    val i0 = Param()
    val k0 = Param()
    val k1 = Param()
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
          Tuple(acc.__0, acc.__1, acc.__2),
          Not(acc.__1) && (acc.__0 % 2 === 0)
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // Correctness
    for (nVal <- 0 to 10) {
      for (i0Val <- -2 to 2) {
        for (k0Val <- -2 to 2) {
          for (k1Val <- -2 to 2) {
            val expected =
              Let(n, nVal, Let(i0, i0Val, Let(k0, k0Val, Let(k1, k1Val, s))))
            val actual =
              Let(n, nVal, Let(i0, i0Val, Let(k0, k0Val, Let(k1, k1Val, opt))))
            assert(
              StreamTests.stm2Seq(actual) == StreamTests.stm2Seq(expected),
              s"(for n = ${nVal}, i0 = ${i0Val}, k0 = ${k0Val}, k1 = ${k1Val})"
            )
          }
        }
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
            Tuple(
              i0 + acc.__0 * 3,
              acc.__0 < (CeilDiv(Max(0, k0 - i0), delta) + 1),
              acc.__0 < (CeilDiv(Max(0, k1 - i0), delta) + 1)
            ),
            Not(acc.__0 < (CeilDiv(Max(0, k0 - i0), delta) + 1))
              && ((i0 + acc.__0 * 3) % 2 === 0)
          )
      )
    assert(opt == ideal)
  }

  test("MonotonicBoolBoundedCounter") {
    val n = Param()
    val i0 = Param()
    val k = Param()
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
          Tuple(acc.__0, acc.__1),
          True
        )
    )
    val opt = StmInductionVarRemovalPass.removeInductionVars(s)

    // Correctness
    for (nVal <- 0 to 10) {
      for (i0Val <- -2 to 2) {
        for (kVal <- -2 to 2) {
          val expected =
            Let(n, nVal, Let(i0, i0Val, Let(k, kVal, s)))
          val actual =
            Let(n, nVal, Let(i0, i0Val, Let(k, kVal, opt)))
          assert(
            StreamTests.stm2Seq(actual) == StreamTests.stm2Seq(expected),
            s"(for n = ${nVal}, i0 = ${i0Val}, k = ${kVal})"
          )
        }
      }
    }

    // Effective simplification
    // TODO: we should be able to get rid of the bounded counter as well
    assert(opt.seed == Tuple(i0, 0))
  }
}
