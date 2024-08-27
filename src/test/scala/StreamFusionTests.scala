import org.scalatest.funsuite.AnyFunSuite

class StreamFusionTests extends AnyFunSuite {
  val canon = (e: Expr) => ExprEvaluator.canonicalize(e.asInstanceOf[StmBuild])
  val fuse = ExprEvaluator.fuse
  val stm2Seq = StreamTests.stm2Seq

  test("CountFromFive") {
    val s = StmMap(
      StmCount(3),
      (x: Expr) => x + 5,
      n = 3,
      fInShape = Seq(),
      fOutShape = Seq()
    )
    val actual = canon(fuse(s))

    // Correct behaviour
    val expectedElems = Seq(5, 6, 7).map(n => IntCst(n))
    assert(stm2Seq(s) == expectedElems)
    assert(stm2Seq(actual) == expectedElems)
    // Successful fusion
    val ideal =
      StmBuild(
        3,
        Tuple(0),
        (acc: Expr) => Tuple(Tuple(acc.__0 + 1), acc.__0 + 5, True)
      )
    assert(actual == ideal)
  }

  test("MapMap") {
    val p = Param()
    val f = (x: Expr) => (x + 2) * (x + 3) * (x + 4)
    val g = (x: Expr) => x - 10
    val s =
      StmMap(
        StmMap(p, f, n = 5, fInShape = Seq(), fOutShape = Seq()),
        g,
        n = 5,
        fInShape = Seq(),
        fOutShape = Seq()
      )
    val actual = canon(fuse(s))

    // Correct behaviour
    // (Using one example p, f, and g)
    val call = (e: Expr) => Let(p, StmCount(5), e)
    val expectedElems = Seq(14, 50, 110, 200, 326).map(n => IntCst(n))
    assert(stm2Seq(call(s)) == expectedElems)
    assert(stm2Seq(call(actual)) == expectedElems)
    // Successful fusion
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
    assert(actual == ideal)
  }

  test("StmShiftRight") {
    val p = Param()
    val s = StmPrepend(StmPrefix(p, StmLength(p) - 1), 42)
    val actual = canon(fuse(s))

    // Correct behaviour
    // (Using one example p)
    val call = (e: Expr) => Let(p, StmCount(5), e)
    val expectedElems = Seq(42, 0, 1, 2, 3).map(n => IntCst(n))
    assert(stm2Seq(call(s)) == expectedElems)
    assert(stm2Seq(call(actual)) == expectedElems)
    // Successful fusion
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
    assert(actual == ideal)
  }

  test("StmShiftLeft") {
    val p = Param()
    val n = 5
    val s = StmAppend(StmSuffix(p, n - 1, n), 42)
    val actual = canon(fuse(s))

    // Correct behaviour
    val call = (e: Expr) => Let(p, StmCount(n), e)
    val expectedElems = Seq(1, 2, 3, 4, 42).map(n => IntCst(n))
    assert(stm2Seq(call(s)) == expectedElems)
    assert(stm2Seq(call(actual)) == expectedElems)
    // Successful fusion
    val ideal = canon(
      StmBuild(
        StmLength(p),
        Tuple(StmLength(p) - 1, 1, p),
        (acc: Expr) =>
          IfThenElse(
            acc.__0 !== 0, {
              val innerNext = Param()
              Let(
                innerNext,
                IfThenElse(
                  acc.__1 === 0,
                  Tuple(
                    Tuple(acc.__1, StmNext(acc.__2).__0),
                    StmNext(acc.__2).__1,
                    True
                  ),
                  Tuple(
                    Tuple(acc.__1 - 1, StmNext(acc.__2).__0),
                    StmNext(acc.__2).__1,
                    False
                  )
                ),
                IfThenElse(
                  innerNext.__2,
                  Tuple(
                    Tuple(
                      acc.__0 - 1,
                      innerNext.__0.__0,
                      innerNext.__0.__1
                    ),
                    innerNext.__1,
                    True
                  ),
                  Tuple(
                    Tuple(acc.__0, innerNext.__0.__0, innerNext.__0.__1),
                    innerNext.__1,
                    False
                  )
                )
              )
            },
            Tuple(Tuple(acc.__0, acc.__1, acc.__2), 42, True)
          )
      )
    )
    assert(actual == ideal)
  }

  test("ZipCounters") {
    // [0, 1, 2, 3]
    val c1 = StmBuild(4, 0, (i: Expr) => Tuple(i + 1, i, True))
    // [9, 11, 13, 15]
    val c2 = StmBuild(4, 9, (i: Expr) => Tuple(i + 2, i, True))
    // [(0, 9), (1, 11), (2, 13), (3, 15)]
    val s = StmZip(c1, c2)

    // 1) After one fusion
    val actual1 = canon(fuse(s))
    // 1a) Correct behaviour
    assert(stm2Seq(actual1) == stm2Seq(s))
    // 1b) Successful fusion
    val ideal1 = StmBuild(
      4,
      Tuple(StmBuild(4, 9, (i: Expr) => Tuple(i + 2, i, True)), 0),
      (acc: Expr) =>
        Tuple(
          Tuple(StmNext(acc.__0).__0, acc.__1 + 1),
          Tuple(acc.__1, StmNext(acc.__0).__1),
          True
        )
    )
    assert(actual1 == ideal1)

    // 2) After two fusions
    val actual2 = canon(fuse(fuse(s)))
    // 2a) Correct behaviour
    val expectedElems =
      Seq(Tuple(0, 9), Tuple(1, 11), Tuple(2, 13), Tuple(3, 15))
    assert(stm2Seq(s) == expectedElems)
    assert(stm2Seq(actual2) == expectedElems)
    // 2b) Successful fusion
    val ideal2 = StmBuild(
      4,
      Tuple(0, 9),
      (acc: Expr) =>
        Tuple(Tuple(acc.__0 + 1, acc.__1 + 2), Tuple(acc.__0, acc.__1), True)
    )
    assert(canon(fuse(fuse(s))) == ideal2)
  }
}
