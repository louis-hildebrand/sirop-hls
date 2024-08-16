import org.scalatest.funsuite.AnyFunSuite

class StreamCanonicalizationTests extends AnyFunSuite {
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
      Tuple(0, 1, 2, 3, 4, StmCount(3)),
      (acc: Expr) =>
        Tuple(
          Tuple(
            acc.__0 + 1,
            acc.__1 + 2,
            acc.__2 + 3,
            acc.__3 + 4,
            acc.__4 + 5,
            StmNext(acc.__5).__0
          ),
          (acc.__0
            + acc.__1
            + acc.__2
            + acc.__3
            + acc.__4
            + StmNext(acc.__5).__1),
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

  test("MoveStreamToFront") {
    ???
  }
}
