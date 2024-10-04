package opt

import ir.*
import operations.{StreamTests, VecShiftLeft, VectorTests}
import org.scalatest.funsuite.AnyFunSuite

class StmInductionVarRemovalPassTests extends AnyFunSuite {
  test("Counters") {
    val n = Param()
    val s = StmBuild(
      n,
      Tuple(
        3 /* up counter (+1) */, 10 /* up counter (+4) */,
        19 /* down counter (-2) */, 0 /* up counter (--3) */,
        -2 /* down counter (+-6) */
      ),
      (acc: Expr) =>
        Tuple(
          Tuple(
            acc.__0 + 1,
            acc.__1 + 4,
            acc.__2 - 2,
            acc.__3 - IntCst(-3),
            acc.__4 + (-6)
          ),
          Tuple(2 * acc.__0, acc.__1 / 3, acc.__2, acc.__3, acc.__4),
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
          IntCst(-2 - 6 * i)
        )
      )
    val actual = (nVal: Int) => StreamTests.stm2Seq(Let(n, nVal, opt))
    assert(actual(0) == expected(0))
    assert(actual(1) == expected(1))
    assert(actual(2) == expected(2))
    assert(actual(3) == expected(3))
    assert(actual(4) == expected(4))
    assert(actual(15) == expected(15))

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
              19 - acc.__0 * 2,
              0 - acc.__0 * (-3),
              -2 - acc.__0 * 6
            ),
            True
          )
      )
    assert(opt == ideal)
  }

  test("ShiftRegister") {
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
    val c = IntCst.apply
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
}
