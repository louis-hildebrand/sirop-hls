import org.scalatest.funsuite.AnyFunSuite

class StreamFusionTests extends AnyFunSuite {
  val pe = ExprEvaluator.partialEval
  val fuse = ExprEvaluator.fuse

  test("CountFromFive") {
    val s = StmMap(StmCount(3), (x: Expr) => x + 5)
    val ideal =
      StmBuild(
        3,
        Tuple(Tuple(), 0),
        (acc: Expr) => Tuple(Tuple(Tuple(), acc.__1 + 1), acc.__1 + 5, True)
      )
    assert(pe(fuse(s)) == ideal)
  }

  test("MapMap") {
    val p = Param()
    val f = Param()
    val g = Param()
    val s = StmMap(StmMap(p, f), g)
    val ideal = StmBuild(
      StmLength(p),
      Tuple(Tuple(), p),
      (acc: Expr) =>
        Tuple(
          Tuple(Tuple(), StmNext(acc.__1).__0),
          FunCall(g, FunCall(f, StmNext(acc.__1).__1)),
          True
        )
    )
    assert(pe(fuse(s)) == ideal)
  }

  test("StmShiftRight") {
    val p = Param()
    val s = StmPrepend(StmPrefix(p, StmLength(p) - 1), 42)
    val ideal = StmBuild(
      StmLength(p),
      Tuple(Tuple(True, Tuple()), p),
      (acc: Expr) =>
        IfThenElse(
          acc.__0.__0,
          Tuple(Tuple(Tuple(False, Tuple()), acc.__1), 42, True),
          Tuple(
            Tuple(Tuple(False, Tuple()), StmNext(acc.__1).__0),
            StmNext(acc.__1).__1,
            True
          )
        )
    )
    assert(pe(fuse(s)) == ideal)
  }

  test("StmShiftLeft") {
    val p = Param()
    val s = StmAppend(StmSuffix(p, StmLength(p) - 1), 42)
    val ideal = StmBuild(
      StmLength(p),
      Tuple(Tuple(Tuple(), StmLength(p) - 1), Tuple(1, p)),
      (acc: Expr) =>
        IfThenElse(
          acc.__0.__1 !== 0, {
            val innerNext = Param()
            Let(
              innerNext,
              IfThenElse(
                acc.__1.__0 === 0,
                Tuple(
                  Tuple(acc.__1.__0, StmNext(acc.__1.__1).__0),
                  StmNext(acc.__1.__1).__1,
                  True
                ),
                Tuple(
                  Tuple(acc.__1.__0 - 1, StmNext(acc.__1.__1).__0),
                  StmNext(acc.__1.__1).__1,
                  False
                )
              ),
              IfThenElse(
                innerNext.__2,
                Tuple(
                  Tuple(Tuple(Tuple(), acc.__0.__1 - 1), innerNext.__0),
                  innerNext.__1,
                  True
                ),
                Tuple(Tuple(acc.__0, innerNext.__0), innerNext.__1, False)
              )
            )
          },
          Tuple(Tuple(Tuple(Tuple(), acc.__0.__1), acc.__1), 42, True)
        )
    )
    assert(pe(fuse(s)) == pe(ideal))
  }

  test("ZipCounters") {
    // [0, 1, 2, 3]
    val c1 = StmBuild(4, 0, (i: Expr) => Tuple(i + 1, i, True))
    // [9, 11, 13, 15]
    val c2 = StmBuild(4, 9, (i: Expr) => Tuple(i + 2, i, True))
    // [(0, 9), (1, 11), (2, 13), (3, 15)]
    val s = StmZip(c1, c2)

    // After one fusion
    val ideal0 = StmBuild(
      4,
      Tuple(
        Tuple(Tuple(), StmBuild(4, 9, (i: Expr) => Tuple(i + 2, i, True))),
        0
      ),
      (acc: Expr) =>
        Tuple(
          Tuple(Tuple(Tuple(), StmNext(acc.__0.__1).__0), acc.__1 + 1),
          Tuple(acc.__1, StmNext(acc.__0.__1).__1),
          True
        )
    )
    assert(pe(fuse(s)) == ideal0)

    val ideal1 = StmBuild(
      4,
      Tuple(Tuple(Tuple(Tuple(), Tuple()), 0), 9),
      (acc: Expr) =>
        Tuple(
          Tuple(Tuple(Tuple(Tuple(), Tuple()), acc.__0.__1 + 1), acc.__1 + 2),
          Tuple(acc.__0.__1, acc.__1),
          True
        )
    )
    assert(pe(fuse(pe(fuse(s)))) == ideal1)
  }
}
