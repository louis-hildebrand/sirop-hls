import org.scalatest.funsuite.AnyFunSuite

class StreamFusionTests extends AnyFunSuite {
  val canon = (e: Expr) => ExprEvaluator.canonicalize(e.asInstanceOf[StmBuild])
  val fuse = ExprEvaluator.fuse

  test("CountFromFive") {
    val s = StmMap(StmCount(3), (x: Expr) => x + 5)
    val ideal =
      StmBuild(
        3,
        Tuple(0),
        (acc: Expr) => Tuple(Tuple(acc.__0 + 1), acc.__0 + 5, True)
      )
    assert(canon(fuse(s)) == ideal)
  }

  test("MapMap") {
    val p = Param()
    val f = Param()
    val g = Param()
    val s = StmMap(StmMap(p, f), g)
    val ideal = StmBuild(
      StmLength(p),
      Tuple(p),
      (acc: Expr) =>
        Tuple(
          Tuple(StmNext(acc.__0).__0),
          FunCall(g, FunCall(f, StmNext(acc.__0).__1)),
          True
        )
    )
    assert(canon(fuse(s)) == ideal)
  }

  test("StmShiftRight") {
    val p = Param()
    val s = StmPrepend(StmPrefix(p, StmLength(p) - 1), 42)
    val ideal = StmBuild(
      StmLength(p),
      Tuple(True, p),
      (acc: Expr) =>
        IfThenElse(
          acc.__0,
          Tuple(Tuple(False, acc.__1), 42, True),
          Tuple(Tuple(False, StmNext(acc.__1).__0), StmNext(acc.__1).__1, True)
        )
    )
    assert(canon(fuse(s)) == ideal)
  }

  test("StmShiftLeft") {
    val p = Param()
    val s = StmAppend(StmSuffix(p, StmLength(p) - 1), 42)
    // TODO: Surely it can be simplified more than this, right?
    val ideal = StmBuild(
      StmLength(p),
      Tuple(StmLength(p) - 1, Tuple(1, p)),
      (acc: Expr) =>
        IfThenElse(
          acc.__0 !== 0, {
            val innerNext = Param()
            Let(
              innerNext,
              IfThenElse(
                acc.__1.__0 === 0,
                Tuple(
                  // TODO: Think this part through
                  Tuple(acc.__1.__0, StmNext(acc.__1.__1).__0),
                  StmNext(acc.__1.__1).__1,
                  True
                ),
                Tuple(
                  // TODO: Think this part through
                  Tuple(acc.__1.__0 - 1, StmNext(acc.__1.__1).__0),
                  StmNext(acc.__1.__1).__1,
                  False
                )
              ),
              IfThenElse(
                innerNext.__2,
                Tuple(
                  // TODO: Think this part through. How will canonicalization
                  //       affect the fact that we're returning this 2-tuple
                  //       directly for innerNext.__0?
                  Tuple(acc.__0 - 1, innerNext.__0),
                  innerNext.__1,
                  True
                ),
                // TODO: Think this part through. How will canonicalization
                //       affect the fact that we're returning this 2-tuple
                //       directly for innerNext.__0?
                Tuple(Tuple(acc.__0, innerNext.__0), innerNext.__1, False)
              )
            )
          },
          Tuple(Tuple(acc.__0, acc.__1), 42, True)
        )
    )
    assert(canon(fuse(s)) == canon(ideal))
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
      Tuple(StmBuild(4, 9, (i: Expr) => Tuple(i + 2, i, True)), 0),
      (acc: Expr) =>
        Tuple(
          Tuple(StmNext(acc.__0).__0, acc.__1 + 1),
          Tuple(acc.__1, StmNext(acc.__0).__1),
          True
        )
    )
    assert(canon(fuse(s)) == ideal0)

    val ideal1 = StmBuild(
      4,
      Tuple(0, 9),
      (acc: Expr) =>
        Tuple(Tuple(acc.__0 + 1, acc.__1 + 2), Tuple(acc.__0, acc.__1), True)
    )
    assert(canon(fuse(fuse(s))) == ideal1)
  }
}
