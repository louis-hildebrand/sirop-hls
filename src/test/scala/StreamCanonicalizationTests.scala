import org.scalatest.funsuite.AnyFunSuite

class StreamCanonicalizationTests extends AnyFunSuite {
  test("ScalarAccumulator") {
    val s = StmBuild(6, 0, (i: Expr) => Tuple(i + 1, i, True))
    val canon =
      StmBuild(6, Tuple(0), (i: Expr) => Tuple(Tuple(i.__0 + 1), i.__0, True))
    assert(ExprEvaluator.canonicalize(s) == canon)
  }

  test("VectorAccumulator") {
    val s = StmBuild(
      2,
      VecBuild(3, (i: Expr) => IntCst(0)),
      (v: Expr) => Tuple(VecShiftLeft(v, 42), v, True)
    )
    val canon = StmBuild(
      2,
      Tuple(VecBuild(3, (i: Expr) => IntCst(0))),
      (v: Expr) => Tuple(Tuple(VecShiftLeft(v.__0, 42)), v.__0, True)
    )
    assert(ExprEvaluator.canonicalize(s) == canon)
  }

  test("FlattenAccumulator") {
    val s = StmBuild(
      3,
      Tuple(Tuple(0, 1), Tuple(2, Tuple(3, 4), StmCount(3))),
      (acc: Expr) =>
        Tuple(
          Tuple(
            Tuple(acc.__0.__0 + 1, acc.__0.__1 + 2),
            Tuple(
              acc.__1.__0 + 3,
              Tuple(acc.__1.__1.__0 + 4, acc.__1.__1.__1 + 5),
              StmNext(acc.__1.__2).__0
            )
          ),
          (acc.__0.__0
            + acc.__0.__1
            + acc.__1.__0
            + acc.__1.__1.__0
            + acc.__1.__1.__1
            + StmNext(acc.__1.__2).__1),
          True
        )
    )
    val canon = StmBuild(
      3,
      Tuple(StmCount(3), 0, 1, 2, 3, 4),
      (acc: Expr) =>
        Tuple(
          Tuple(
            StmNext(acc.__0).__0,
            acc.__1 + 1,
            acc.__2 + 2,
            acc.__3 + 3,
            acc.__4 + 4,
            acc.__5 + 5
          ),
          (acc.__1
            + acc.__2
            + acc.__3
            + acc.__4
            + acc.__5
            + StmNext(acc.__0).__1),
          True
        )
    )
    assert(ExprEvaluator.canonicalize(s) == canon)
  }

  test("EmptyAccumulator") {
    val s = StmBuild(
      5,
      Tuple(Tuple()),
      (acc: Expr) => Tuple(Tuple(acc.__0), 42, True)
    )
    val canon = StmBuild(5, Tuple(), (acc: Expr) => Tuple(Tuple(), 42, True))
    assert(ExprEvaluator.canonicalize(s) == canon)
  }

  test("RemoveEmptyTuples") {
    val s = StmBuild(
      4,
      Tuple(0, Tuple(), 1, 2, Tuple()),
      (acc: Expr) =>
        Tuple(
          Tuple(acc.__0 + 1, acc.__1, acc.__2 + 2, acc.__3 + 3, Tuple()),
          acc.__0 * acc.__2 * acc.__3,
          True
        )
    )
    val canon = StmBuild(
      4,
      Tuple(0, 1, 2),
      (acc: Expr) =>
        Tuple(
          Tuple(acc.__0 + 1, acc.__1 + 2, acc.__2 + 3),
          acc.__0 * acc.__1 * acc.__2,
          True
        )
    )
    assert(ExprEvaluator.canonicalize(s) == canon)
  }

  test("MoveStreamsToFront") {
    val s = StmBuild(
      3,
      Tuple(5, StmCount(6), 7, StmCount(8)),
      (acc: Expr) =>
        Tuple(
          Tuple(
            acc.__0 - 1,
            StmNext(acc.__1).__0,
            acc.__2 - 2,
            StmNext(acc.__3).__0
          ),
          acc.__0 + StmNext(acc.__1).__1 + acc.__2 + StmNext(acc.__3).__1,
          True
        )
    )
    val canon = StmBuild(
      3,
      Tuple(StmCount(6), StmCount(8), 5, 7),
      (acc: Expr) =>
        Tuple(
          Tuple(
            StmNext(acc.__0).__0,
            StmNext(acc.__1).__0,
            acc.__2 - 1,
            acc.__3 - 2
          ),
          acc.__2 + StmNext(acc.__0).__1 + acc.__3 + StmNext(acc.__1).__1,
          True
        )
    )
    assert(ExprEvaluator.canonicalize(s) == canon)
  }

  test("MoveIfThenElseOutsideTuple1") {
    val s = StmBuild(
      Tuple(Tuple(2, 2)),
      0,
      (i: Expr) =>
        Tuple(i + 1, IfThenElse(i % 2 === 0, i / 2, (i - 1) / 2), True)
    )
    val canon = StmBuild(
      Tuple(Tuple(2, 2)),
      Tuple(0),
      (i: Expr) =>
        IfThenElse(
          i.__0 % 2 === 0,
          Tuple(Tuple(i.__0 + 1), i.__0 / 2, True),
          Tuple(Tuple(i.__0 + 1), (i.__0 - 1) / 2, True)
        )
    )
    assert(ExprEvaluator.canonicalize(s) == canon)
  }

  test("MoveIfThenElseOutsideTuple2") {
    val s = StmBuild(
      Tuple(Tuple(2, 2), Tuple(3, 3), Tuple(4, 4)),
      Tuple(0, Tuple(), Tuple(0, 0)),
      (acc: Expr) =>
        Tuple(
          IfThenElse(
            acc.__2.__1 === 3,
            IfThenElse(
              acc.__2.__0 === 2,
              Tuple(acc.__0 + 1, Tuple(), Tuple(0, 0)),
              Tuple(acc.__0, Tuple(), Tuple(acc.__2.__0 + 1, 0))
            ),
            Tuple(acc.__0, Tuple(), Tuple(acc.__2.__0, acc.__2.__1 + 1))
          ),
          Tuple(acc.__0, acc.__2.__0, acc.__2.__1),
          True
        )
    )
    val canon = StmBuild(
      Tuple(Tuple(2, 2), Tuple(3, 3), Tuple(4, 4)),
      Tuple(0, 0, 0),
      (acc: Expr) =>
        IfThenElse(
          acc.__2 === 3,
          IfThenElse(
            acc.__1 === 2,
            Tuple(
              Tuple(acc.__0 + 1, 0, 0),
              Tuple(acc.__0, acc.__1, acc.__2),
              True
            ),
            Tuple(
              Tuple(acc.__0, acc.__1 + 1, 0),
              Tuple(acc.__0, acc.__1, acc.__2),
              True
            )
          ),
          Tuple(
            Tuple(acc.__0, acc.__1, acc.__2 + 1),
            Tuple(acc.__0, acc.__1, acc.__2),
            True
          )
        )
    )
    assert(ExprEvaluator.canonicalize(s) == canon)
  }
}
