package opt

import ir.*
import org.scalatest.funsuite.AnyFunSuite
import operations.StreamTests

class StmAccRemovePassTests extends AnyFunSuite {
  private val MonotonicBoolTestCases = Seq(
    // TODO: generalize to these cases?
//    (
//      "MonotonicBool:F-T:LessThan",
//      Tuple(10, False),
//      (acc: Expr) =>
//        Tuple(
//          Tuple(acc.__0 - 1, acc.__1 || (acc.__0 < 3)),
//          acc.__0,
//          (acc.__1) && (acc.__0 % 2 === 0)
//        ),
//      Tuple(10),
//      (acc: Expr) =>
//        Tuple(Tuple(acc.__0 - 1), acc.__0, (acc.__0 < 2) && (acc.__0 % 2 === 0))
//    ),
//    (
//      "MonotonicBool:F-T:LessOrEqual",
//      Tuple(10, False),
//      (acc: Expr) =>
//        Tuple(
//          Tuple(acc.__0 - 1, acc.__1 || (acc.__0 <= 3)),
//          acc.__0,
//          (acc.__1) && (acc.__0 % 2 === 0)
//        ),
//      Tuple(10),
//      (acc: Expr) =>
//        Tuple(
//          Tuple(acc.__0 - 1),
//          acc.__0,
//          (acc.__0 <= 2) && (acc.__0 % 2 === 0)
//        )
//    ),
//    (
//      "MonotonicBool:F-T:GreaterThan",
//      Tuple(0, False),
//      (acc: Expr) =>
//        Tuple(
//          Tuple(acc.__0 + 1, acc.__1 || (acc.__0 > 3)),
//          acc.__0,
//          (acc.__1) && (acc.__0 % 2 === 0)
//        ),
//      Tuple(0),
//      (acc: Expr) =>
//        Tuple(Tuple(acc.__0 + 1), acc.__0, (acc.__0 > 4) && (acc.__0 % 2 === 0))
//    ),
//    (
//      "MonotonicBool:F-T:GreaterOrEqual",
//      Tuple(0, False),
//      (acc: Expr) =>
//        Tuple(
//          Tuple(acc.__0 + 1, acc.__1 || (acc.__0 >= 3)),
//          acc.__0,
//          (acc.__1) && (acc.__0 % 2 === 0)
//        ),
//      Tuple(0),
//      (acc: Expr) =>
//        Tuple(
//          Tuple(acc.__0 + 1),
//          acc.__0,
//          (acc.__0 >= 4) && (acc.__0 % 2 === 0)
//        )
//    ),
//    (
//      "MonotonicBool:T-F:GreaterOrEqual",
//      Tuple(10, True),
//      (acc: Expr) =>
//        Tuple(
//          Tuple(acc.__0 - 1, acc.__1 && (acc.__0 >= 3)),
//          acc.__0,
//          Not(acc.__1) && (acc.__0 % 2 === 0)
//        ),
//      Tuple(10),
//      (acc: Expr) =>
//        Tuple(Tuple(acc.__0 - 1), acc.__0, (acc.__0 < 2) && (acc.__0 % 2 === 0))
//    ),
//    (
//      "MonotonicBool:T-F:GreaterThan",
//      Tuple(10, True),
//      (acc: Expr) =>
//        Tuple(
//          Tuple(acc.__0 - 1, acc.__1 && (acc.__0 > 3)),
//          acc.__0,
//          Not(acc.__1) && (acc.__0 % 2 === 0)
//        ),
//      Tuple(10),
//      (acc: Expr) =>
//        Tuple(
//          Tuple(acc.__0 - 1),
//          acc.__0,
//          (acc.__0 <= 2) && (acc.__0 % 2 === 0)
//        )
//    ),
//    (
//      "MonotonicBool:T-F:LessOrEqual",
//      Tuple(0, True),
//      (acc: Expr) =>
//        Tuple(
//          Tuple(acc.__0 + 1, acc.__1 && (acc.__0 <= 3)),
//          acc.__0,
//          Not(acc.__1) && (acc.__0 % 2 === 0)
//        ),
//      Tuple(0),
//      (acc: Expr) =>
//        Tuple(Tuple(acc.__0 + 1), acc.__0, (acc.__0 > 4) && (acc.__0 % 2 === 0))
//    ),
    (
      "MonotonicBool:T-F:LessThan:i0=0:delta=1:k=3",
      Tuple(0, True),
      (acc: Expr) =>
        Tuple(
          Tuple(acc.__0 + 1, acc.__1 && (acc.__0 < 3)),
          acc.__0,
          Not(acc.__1) && (acc.__0 % 2 === 0)
        ),
      Tuple(0),
      (acc: Expr) =>
        Tuple(
          Tuple(acc.__0 + 1),
          acc.__0,
          (acc.__0 >= 4) && (acc.__0 % 2 === 0)
        )
    ),
    (
      "MonotonicBool:T-F:LessThan:i0=2:delta=3:k=10",
      Tuple(2, True),
      (acc: Expr) =>
        Tuple(
          Tuple(
            IfThenElse(acc.__1, acc.__0 + 3, acc.__0),
            acc.__1 && (acc.__0 < 10)
          ),
          acc.__0,
          Not(acc.__1)
        ),
      Tuple(2),
      (acc: Expr) =>
        Tuple(
          Tuple(IfThenElse(acc.__0 < 14, acc.__0 + 3, acc.__0)),
          acc.__0,
          (acc.__0 >= 14)
        )
    ),
    (
      "MonotonicBool:T-F:LessThan:i0=10:delta=1:k=3",
      Tuple(10, True),
      (acc: Expr) =>
        Tuple(
          Tuple(
            IfThenElse(acc.__1, acc.__0 + 1, acc.__0),
            acc.__1 && (acc.__0 < 3)
          ),
          acc.__0,
          Not(acc.__1)
        ),
      Tuple(10),
      (acc: Expr) =>
        Tuple(
          Tuple(IfThenElse(acc.__0 < 11, acc.__0 + 1, acc.__0)),
          acc.__0,
          (acc.__0 >= 11)
        )
    )
    // TODO: Add negative test cases as well?
    // TODO: what if the counter is not *strictly* monotonic (e.g., increases every two cycles?)
    // TODO: what if bool is used elsewhere?
  )

  MonotonicBoolTestCases.foreach({ case (name, seed0, nextF0, seed1, nextF1) =>
    test(name) {
      val n = Param()
      val s = StmBuild(n, seed0, nextF0)
      val actual = StmAccRemovePass.removeUnnecessaryAccumulators(s)

      // TODO
      import debug.*

      // Correct behaviour
      val nVal = 15
      assert(
        StreamTests.stm2Seq(Let(n, nVal, actual)) == StreamTests.stm2Seq(
          Let(n, nVal, s)
        )
      )
      // Effective simplification
      val ideal = StmBuild(n, seed1, nextF1)
      assert(
        StmCanonPass.canonicalize(actual) == StmCanonPass.canonicalize(ideal)
      )
    }
  })
}
