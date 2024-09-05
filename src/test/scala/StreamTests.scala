import org.scalatest.funsuite.AnyFunSuite

object StreamTests {
  def stm2Seq(stm: Expr): Seq[Expr] = {
    val n = ExprEvaluator
      .partialEval(StmLength(stm))
      .asInstanceOf[IntCst]
      .i
    if (n < 0) then {
      throw new IllegalArgumentException(s"Stream has negative length (${n})!")
    } else if (n == 0) {
      Seq()
    } else {
      val next = ExprEvaluator.partialEval(StmNext(stm))
      ExprEvaluator.partialEval(next.__1) +: stm2Seq(next.__0)
    }
  }

  def stmStm2SeqSeq(stm: Expr): Seq[Seq[Expr]] = ???
}

class StreamTests extends AnyFunSuite {

  inline def assertStreamEqual(stream: Expr, expectedSeq: Seq[Expr]): Unit = {
    assert(StreamTests.stm2Seq(stream) == expectedSeq)
  }

  inline def assert2DStreamEqual(
      stream: Expr,
      expectedSeq: Seq[Seq[Expr]]
  ): Unit = {
    assert(StreamTests.stmStm2SeqSeq(stream) == expectedSeq)
  }

  test("IntCst") {
    val intAst = IntCst(3)
    assert(ExprEvaluator.partialEval(intAst) == IntCst(3))
  }

  test("StmCst") {
    val s = StmCst(4, 3)
    assertStreamEqual(s, Seq(3, 3, 3, 3))
  }

  test("StmCount") {
    val s = StmCount(5)
    assertStreamEqual(s, Seq(0, 1, 2, 3, 4))
  }

  test("StmCountFrom") {
    assertStreamEqual(StmCountFrom(3, 4), Seq(3, 4, 5, 6))
  }

  test("StmCst2D") {
    val s = StmCst2D(3, 4, 42)
    val expected = Seq(
      Seq(IntCst(42), IntCst(42), IntCst(42), IntCst(42)),
      Seq(IntCst(42), IntCst(42), IntCst(42), IntCst(42)),
      Seq(IntCst(42), IntCst(42), IntCst(42), IntCst(42))
    )
    assertStreamEqual(s, expected.flatten)
  }

  test("StmCount2D") {
    val s = StmCount2D(2, 3)
    val expected = Seq(
      Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2)),
      Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2))
    )
    assertStreamEqual(s, expected.flatten)
  }

  test("Iterate:Square") {
    val e = (n: Int) => Iterate(n, 3, (x: Expr) => x * x, zSize = 1)
    assert(ExprEvaluator.partialEval(e(0)) == IntCst(3))
    assert(ExprEvaluator.partialEval(e(1)) == IntCst(9))
    assert(ExprEvaluator.partialEval(e(2)) == IntCst(81))
    assert(ExprEvaluator.partialEval(e(3)) == IntCst(81 * 81))
  }

  test("Iterate:PlusTwo") {
    val e = (n: Int) => Iterate(n, -10, (x: Expr) => x + 2, zSize = 1)
    assert(ExprEvaluator.partialEval(e(0)) == IntCst(-10))
    assert(ExprEvaluator.partialEval(e(1)) == IntCst(-8))
    assert(ExprEvaluator.partialEval(e(2)) == IntCst(-6))
    assert(ExprEvaluator.partialEval(e(3)) == IntCst(-4))
  }

  test("StmMap:1D-1D:+7") {
    val s = StmMap(
      StmCount(5),
      (x: Expr) => x + 7,
      n = 5,
      fInShape = None,
      fOutShape = None
    )
    assertStreamEqual(s, Seq(7, 8, 9, 10, 11))
  }

  test("StmMap:1D-1D:Identity") {
    val s = StmMap(
      StmCount(3),
      (x: Expr) => x,
      n = 3,
      fInShape = None,
      fOutShape = None
    )
    assertStreamEqual(s, Seq(0, 1, 2))
  }

  test("StmMap:1D-1D:DiscardInputReturn42") {
    val s = StmMap(
      StmCount(6),
      (_: Expr) => IntCst(42),
      n = 6,
      fInShape = None,
      fOutShape = None
    )
    assertStreamEqual(s, Seq(42, 42, 42, 42, 42, 42))
  }

  // The scalar input to the inner function is used only in the `nextF`.
  test("StmMap:1D-2D:StmCst") {
    val s = StmMap(
      StmCount(4),
      (c: Expr) => StmCst(3, c),
      n = 4,
      fInShape = None,
      fOutShape = Some(3)
    )
    val expected = Seq(
      Seq(0, 0, 0),
      Seq(1, 1, 1),
      Seq(2, 2, 2),
      Seq(3, 3, 3)
    ).map(xs => xs.map(x => IntCst(x)))
    assertStreamEqual(s, expected.flatten)
  }

  // The scalar input to the inner function is used only in the seed.
  test("StmMap:1D-2D:StmCountFrom") {
    val s =
      StmMap(
        StmCount(3),
        (n: Expr) => StmCountFrom(n, 4),
        n = 3,
        fInShape = None,
        fOutShape = Some(4)
      )
    val expected = Seq(
      Seq(0, 1, 2, 3),
      Seq(1, 2, 3, 4),
      Seq(2, 3, 4, 5)
    ).map(ns => ns.map(n => IntCst(n)))
    assertStreamEqual(s, expected.flatten)
  }

  // The scalar input to the inner function is used both in the `nextF` and in
  // the seed.
  test("StmMap:1D-2D:StmCstAndCountFrom") {
    val s = StmMap(
      StmCount(3),
      (i: Expr) =>
        StmBuild(
          Tuple(Tuple(3, 3)),
          i,
          (acc: Expr) => Tuple(acc + 1, Tuple(i, acc), True)
        ),
      n = 3,
      fInShape = None,
      fOutShape = Some(3)
    )
    val expected = Seq(
      Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2)),
      Seq(Tuple(1, 1), Tuple(1, 2), Tuple(1, 3)),
      Seq(Tuple(2, 2), Tuple(2, 3), Tuple(2, 4))
    )
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:1D-2D:DiscardInputReturnStmCount") {
    val s = StmMap(
      StmCount(4),
      (_: Expr) => StmCount(3),
      n = 4,
      fInShape = None,
      fOutShape = Some(3)
    )
    val expected = Seq(
      Seq(0, 1, 2),
      Seq(0, 1, 2),
      Seq(0, 1, 2),
      Seq(0, 1, 2)
    ).map(xs => xs.map(x => IntCst(x)))
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:2D-1D:Fold") {
    val s = StmMap(
      StmCount2D(4, 3),
      (s: Expr) =>
        StmFold(
          s,
          0,
          (acc: Expr) => (x: Expr) => acc + x.__0 + x.__1,
          stmShape = Seq(3)
        ),
      n = 4,
      fInShape = Some(3),
      fOutShape = None
    )
    val expected = Seq(3, 6, 9, 12).map(n => IntCst(n))
    assertStreamEqual(s, expected)
  }

  test("StmMap:2D-1D:Access") {
    val s = StmMap(
      StmCount2D(4, 3),
      (s: Expr) => StmAccess(s, 1, shape = Seq(3)),
      n = 4,
      fInShape = Some(3),
      fOutShape = None
    )
    val expected = Seq(Tuple(0, 1), Tuple(1, 1), Tuple(2, 1), Tuple(3, 1))
    assertStreamEqual(s, expected)
  }

  test("StmMap:2D-1D:DiscardInputReturn42") {
    val s = StmMap(
      StmCount2D(3, 4),
      (_: Expr) => IntCst(42),
      n = 3,
      fInShape = Some(4),
      fOutShape = None
    )
    val expected = Seq(42, 42, 42).map(n => IntCst(n))
    assertStreamEqual(s, expected)
  }

  test("StmMap:2D-2D:StmMap") {
    val s = StmMap(
      StmCount2D(4, 3),
      (s: Expr) =>
        StmMap(
          s,
          (x: Expr) => x.__0 + x.__1,
          n = 3,
          fInShape = None,
          fOutShape = None
        ),
      n = 4,
      fInShape = Some(3),
      fOutShape = Some(3)
    )
    val expected = Seq(
      Seq(0, 1, 2),
      Seq(1, 2, 3),
      Seq(2, 3, 4),
      Seq(3, 4, 5)
    ).map(xs => xs.map(x => IntCst(x)))
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:2D-2D:StmPrepend") {
    val s = StmMap(
      StmCst2D(4, 5, 42),
      (s: Expr) => StmPrepend(s, 43, eShape = Seq()),
      n = 4,
      fInShape = Some(5),
      fOutShape = Some(6)
    )

    val expected = Seq(
      Seq(43, 42, 42, 42, 42, 42),
      Seq(43, 42, 42, 42, 42, 42),
      Seq(43, 42, 42, 42, 42, 42),
      Seq(43, 42, 42, 42, 42, 42)
    ).map(xs => xs.map(x => IntCst(x)))
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:2D-2D:StmAppend") {
    val s = StmMap(
      StmCst2D(4, 5, 42),
      (s: Expr) => StmAppend(s, 43, stmShape = Seq(5)),
      n = 4,
      fInShape = Some(5),
      fOutShape = Some(6)
    )

    val expected = Seq(
      Seq(42, 42, 42, 42, 42, 43),
      Seq(42, 42, 42, 42, 42, 43),
      Seq(42, 42, 42, 42, 42, 43),
      Seq(42, 42, 42, 42, 42, 43)
    ).map(xs => xs.map(x => IntCst(x)))
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:2D-2D:StmPrefix") {
    val stm = StmCount2D(3, 1000)
    val actual =
      StmMap(
        stm,
        (s: Expr) => StmPrefix(s, 2, shape = Seq(1000)),
        n = 3,
        fInShape = Some(1000),
        fOutShape = Some(2)
      )
    val expected = Seq(
      Seq(Tuple(0, 0), Tuple(0, 1)),
      Seq(Tuple(1, 0), Tuple(1, 1)),
      Seq(Tuple(2, 0), Tuple(2, 1))
    )
    assertStreamEqual(actual, expected.flatten)
  }

  test("StmMap:2D-2D:StmSuffix") {
    val stm = StmCount2D(3, 1000)
    val actual =
      StmMap(
        stm,
        (s: Expr) => StmSuffix(s, 2, shape = Seq(1000)),
        n = 3,
        fInShape = Some(1000),
        fOutShape = Some(2)
      )
    val expected = Seq(
      Seq(Tuple(0, 998), Tuple(0, 999)),
      Seq(Tuple(1, 998), Tuple(1, 999)),
      Seq(Tuple(2, 998), Tuple(2, 999))
    )
    assertStreamEqual(actual, expected.flatten)
  }

  test("StmMap:2D-2D:StmShiftLeft") {
    val s = StmMap(
      StmCount2D(4, 5),
      (s: Expr) => StmShiftLeft(s, Tuple(100, 101), stmShape = Seq(5)),
      n = 4,
      fInShape = Some(5),
      fOutShape = Some(5)
    )
    val expected = Seq(
      Seq(Tuple(0, 1), Tuple(0, 2), Tuple(0, 3), Tuple(0, 4), Tuple(100, 101)),
      Seq(Tuple(1, 1), Tuple(1, 2), Tuple(1, 3), Tuple(1, 4), Tuple(100, 101)),
      Seq(Tuple(2, 1), Tuple(2, 2), Tuple(2, 3), Tuple(2, 4), Tuple(100, 101)),
      Seq(Tuple(3, 1), Tuple(3, 2), Tuple(3, 3), Tuple(3, 4), Tuple(100, 101))
    )
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:2D-2D:StmShiftRight") {
    val s = StmMap(
      StmCount2D(4, 5),
      (s: Expr) => StmShiftRight(s, Tuple(100, 101), stmShape = Seq(5)),
      n = 4,
      fInShape = Some(5),
      fOutShape = Some(5)
    )
    val expected = Seq(
      Seq(Tuple(100, 101), Tuple(0, 0), Tuple(0, 1), Tuple(0, 2), Tuple(0, 3)),
      Seq(Tuple(100, 101), Tuple(1, 0), Tuple(1, 1), Tuple(1, 2), Tuple(1, 3)),
      Seq(Tuple(100, 101), Tuple(2, 0), Tuple(2, 1), Tuple(2, 2), Tuple(2, 3)),
      Seq(Tuple(100, 101), Tuple(3, 0), Tuple(3, 1), Tuple(3, 2), Tuple(3, 3))
    )
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:2D-2D:StmConcatBefore") {
    val s = StmMap(
      StmCst2D(5, 5, 99),
      (s: Expr) => StmConcat(StmCount(3), s, len1 = 3),
      n = 5,
      fInShape = Some(5),
      fOutShape = Some(8)
    )
    val expected = Seq(
      Seq(0, 1, 2, 99, 99, 99, 99, 99),
      Seq(0, 1, 2, 99, 99, 99, 99, 99),
      Seq(0, 1, 2, 99, 99, 99, 99, 99),
      Seq(0, 1, 2, 99, 99, 99, 99, 99),
      Seq(0, 1, 2, 99, 99, 99, 99, 99)
    ).map(xs => xs.map(x => IntCst(x)))
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:2D-2D:StmConcatAfter") {
    val s = StmMap(
      StmCst2D(5, 5, 99),
      (s: Expr) => StmConcat(s, StmCount(3), len1 = 5),
      n = 5,
      fInShape = Some(5),
      fOutShape = Some(8)
    )
    val expected = Seq(
      Seq(99, 99, 99, 99, 99, 0, 1, 2),
      Seq(99, 99, 99, 99, 99, 0, 1, 2),
      Seq(99, 99, 99, 99, 99, 0, 1, 2),
      Seq(99, 99, 99, 99, 99, 0, 1, 2),
      Seq(99, 99, 99, 99, 99, 0, 1, 2)
    ).map(xs => xs.map(x => IntCst(x)))
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:2D-2D:StmZipWithIndexBefore") {
    val s = StmMap(
      StmCount2D(3, 3),
      (s: Expr) => StmZip(StmCount(3), s),
      n = 3,
      fInShape = Some(3),
      fOutShape = Some(3)
    )
    val expected = Seq(
      Seq(Tuple(0, Tuple(0, 0)), Tuple(1, Tuple(0, 1)), Tuple(2, Tuple(0, 2))),
      Seq(Tuple(0, Tuple(1, 0)), Tuple(1, Tuple(1, 1)), Tuple(2, Tuple(1, 2))),
      Seq(Tuple(0, Tuple(2, 0)), Tuple(1, Tuple(2, 1)), Tuple(2, Tuple(2, 2)))
    )
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:2D-2D:StmZipWithIndexAfter") {
    val s = StmMap(
      StmCount2D(3, 3),
      (s: Expr) => StmZip(s, StmCount(3)),
      n = 3,
      fInShape = Some(3),
      fOutShape = Some(3)
    )
    val expected = Seq(
      Seq(Tuple(Tuple(0, 0), 0), Tuple(Tuple(0, 1), 1), Tuple(Tuple(0, 2), 2)),
      Seq(Tuple(Tuple(1, 0), 0), Tuple(Tuple(1, 1), 1), Tuple(Tuple(1, 2), 2)),
      Seq(Tuple(Tuple(2, 0), 0), Tuple(Tuple(2, 1), 1), Tuple(Tuple(2, 2), 2))
    )
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:2D-2D:StmRepeat") {
    val s = StmMap(
      StmCount2D(4, 4),
      (s: Expr) => StmRepeat(s, 3, n = 4),
      n = 4,
      fInShape = Some(4),
      fOutShape = Some(12)
    )
    val expected = Seq(
      Seq(
        Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2), Tuple(0, 3)),
        Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2), Tuple(0, 3)),
        Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2), Tuple(0, 3))
      ),
      Seq(
        Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2), Tuple(1, 3)),
        Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2), Tuple(1, 3)),
        Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2), Tuple(1, 3))
      ),
      Seq(
        Seq(Tuple(2, 0), Tuple(2, 1), Tuple(2, 2), Tuple(2, 3)),
        Seq(Tuple(2, 0), Tuple(2, 1), Tuple(2, 2), Tuple(2, 3)),
        Seq(Tuple(2, 0), Tuple(2, 1), Tuple(2, 2), Tuple(2, 3))
      ),
      Seq(
        Seq(Tuple(3, 0), Tuple(3, 1), Tuple(3, 2), Tuple(3, 3)),
        Seq(Tuple(3, 0), Tuple(3, 1), Tuple(3, 2), Tuple(3, 3)),
        Seq(Tuple(3, 0), Tuple(3, 1), Tuple(3, 2), Tuple(3, 3))
      )
    )
    assertStreamEqual(s, expected.flatten.flatten)
  }

  test("StmMap:2D-2D:Identity") {
    val s = StmMap(
      StmCount2D(2, 3),
      (s: Expr) => s,
      n = 2,
      fInShape = Some(3),
      fOutShape = Some(3)
    )
    val expected = Seq(
      Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2)),
      Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2))
    )
    // TODO: this is failing because StmMap is restarting the StmCount2D rather than the identity function!
    // But the test passes if we replace (s: Expr) => s by
    // (s: Expr) => StmMap(s, (x: Expr) => x, n = 3, fInShape = None, fOutShape = None)
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:2D-2D:DiscardInputReturnStmCount") {
    val s = StmMap(
      StmCount2D(4, 3),
      (_: Expr) => StmCount(5),
      n = 4,
      fInShape = Some(5),
      fOutShape = Some(5)
    )
    val expected = Seq(
      Seq(0, 1, 2, 3, 4),
      Seq(0, 1, 2, 3, 4),
      Seq(0, 1, 2, 3, 4),
      Seq(0, 1, 2, 3, 4)
    ).map(xs => xs.map(x => IntCst(x)))
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:1D-3D:MapCount") {
    val s = StmMap(
      StmCount(2),
      (i: Expr) =>
        StmMap(
          StmCount(3),
          (j: Expr) =>
            StmMap(
              StmCount(4),
              (k: Expr) => Tuple(i, j, k),
              n = 4,
              fInShape = None,
              fOutShape = None
            ),
          n = 3,
          fInShape = None,
          fOutShape = Some(4)
        ),
      n = 2,
      fInShape = None,
      fOutShape = Some(12)
    )
    val expected = Seq(
      Seq(
        Seq(Tuple(0, 0, 0), Tuple(0, 0, 1), Tuple(0, 0, 2), Tuple(0, 0, 3)),
        Seq(Tuple(0, 1, 0), Tuple(0, 1, 1), Tuple(0, 1, 2), Tuple(0, 1, 3)),
        Seq(Tuple(0, 2, 0), Tuple(0, 2, 1), Tuple(0, 2, 2), Tuple(0, 2, 3))
      ),
      Seq(
        Seq(Tuple(1, 0, 0), Tuple(1, 0, 1), Tuple(1, 0, 2), Tuple(1, 0, 3)),
        Seq(Tuple(1, 1, 0), Tuple(1, 1, 1), Tuple(1, 1, 2), Tuple(1, 1, 3)),
        Seq(Tuple(1, 2, 0), Tuple(1, 2, 1), Tuple(1, 2, 2), Tuple(1, 2, 3))
      )
    )
    assertStreamEqual(s, expected.flatten.flatten)
  }

  test("StmAccess:1D") {
    val s = StmMap(
      StmCount(3),
      (x: Expr) => x + 5,
      n = 3,
      fInShape = None,
      fOutShape = None
    )

    val pe = ExprEvaluator.partialEval
    assert(pe(StmAccess(s, 0, shape = Seq(3))) == IntCst(5))
    assert(pe(StmAccess(s, 1, shape = Seq(3))) == IntCst(6))
    assert(pe(StmAccess(s, 2, shape = Seq(3))) == IntCst(7))
  }

  test("StmAccess:2D") {
    val s = StmCount2D(4, 5)

    val row = (i: Int) => (0 until 5).map(j => Tuple(i, j))

    assertStreamEqual(StmAccess(s, 0, shape = Seq(4, 5)), row(0))
    assertStreamEqual(StmAccess(s, 1, shape = Seq(4, 5)), row(1))
    assertStreamEqual(StmAccess(s, 2, shape = Seq(4, 5)), row(2))
    assertStreamEqual(StmAccess(s, 3, shape = Seq(4, 5)), row(3))
  }

  test("StmAccess:3D") {
    val s = StmMap(
      StmCount(3),
      (i: Expr) =>
        StmMap(
          StmCount(3),
          (j: Expr) =>
            StmMap(
              StmCount(4),
              (k: Expr) => Tuple(i, j, k),
              n = 4,
              fInShape = None,
              fOutShape = None
            ),
          n = 3,
          fInShape = None,
          fOutShape = Some(4)
        ),
      n = 3,
      fInShape = None,
      fOutShape = Some(12)
    )
    val expected = Seq(
      Seq(
        Seq(Tuple(0, 0, 0), Tuple(0, 0, 1), Tuple(0, 0, 2), Tuple(0, 0, 3)),
        Seq(Tuple(0, 1, 0), Tuple(0, 1, 1), Tuple(0, 1, 2), Tuple(0, 1, 3)),
        Seq(Tuple(0, 2, 0), Tuple(0, 2, 1), Tuple(0, 2, 2), Tuple(0, 2, 3))
      ),
      Seq(
        Seq(Tuple(1, 0, 0), Tuple(1, 0, 1), Tuple(1, 0, 2), Tuple(1, 0, 3)),
        Seq(Tuple(1, 1, 0), Tuple(1, 1, 1), Tuple(1, 1, 2), Tuple(1, 1, 3)),
        Seq(Tuple(1, 2, 0), Tuple(1, 2, 1), Tuple(1, 2, 2), Tuple(1, 2, 3))
      ),
      Seq(
        Seq(Tuple(2, 0, 0), Tuple(2, 0, 1), Tuple(2, 0, 2), Tuple(2, 0, 3)),
        Seq(Tuple(2, 1, 0), Tuple(2, 1, 1), Tuple(2, 1, 2), Tuple(2, 1, 3)),
        Seq(Tuple(2, 2, 0), Tuple(2, 2, 1), Tuple(2, 2, 2), Tuple(2, 2, 3))
      )
    )
    assertStreamEqual(
      StmAccess(s, 0, shape = Seq(3, 3, 4)),
      expected.head.flatten
    )
    assertStreamEqual(
      StmAccess(s, 1, shape = Seq(3, 3, 4)),
      expected(1).flatten
    )
    assertStreamEqual(
      StmAccess(s, 2, shape = Seq(3, 3, 4)),
      expected(2).flatten
    )
  }

  test("StmFold:1D:Sum") {
    val sum =
      StmFold(
        StmCount(6),
        3,
        (acc: Expr) => (x: Expr) => acc + x,
        stmShape = Seq(6)
      )
    val pe = ExprEvaluator.partialEval(sum)
    assert(pe == IntCst(18))
  }

  test("StmFold:1D:Product") {
    val prod =
      StmFold(
        StmCountFrom(1, 5),
        1,
        (acc: Expr) => (x: Expr) => acc * x,
        stmShape = Seq(5)
      )
    assert(ExprEvaluator.partialEval(prod) == IntCst(120))
  }

  test("StmFold:1D:HornerMethod") {
    // Non-commutative, non-associative update function
    // Evaluate y = 2x^4 + 3x^3 + 4x^2 + 5x + 6 at x = 2
    val coefficients = StmCountFrom(2, 5)
    val x = 2
    val y =
      StmFold(
        coefficients,
        0,
        (acc: Expr) => (a: Expr) => a + x * acc,
        stmShape = Seq(5)
      )
    assert(ExprEvaluator.partialEval(y) == IntCst(88))
  }

  test("StmFold:1D:IgnoreInput") {
    fail("TODO")
  }

  test("StmFold:2D:Sum") {
    // [[0, 1, 2, 3],
    //  [1, 2, 3, 4],
    //  [2, 3, 4, 5]]
    val s = StmMap(
      StmCount(3),
      (i: Expr) =>
        StmMap(
          StmCount(4),
          (j: Expr) => i + j,
          n = 4,
          fInShape = None,
          fOutShape = None
        ),
      n = 3,
      fInShape = None,
      fOutShape = Some(4)
    )
    val sum = StmFold(
      s,
      0,
      (acc: Expr) =>
        (s: Expr) =>
          acc + StmFold(
            s,
            0,
            (a: Expr) => (x: Expr) => a + x,
            stmShape = Seq(4)
          ),
      stmShape = Seq(3, 4)
    )
    assert(ExprEvaluator.partialEval(sum) == IntCst(30))
  }

  test("StmFold:2D:SumColumn") {
    fail("TODO")
  }

  test("StmFold:2D:Product") {
    // [[1, 2, 3],
    //  [2, 3, 4],
    //  [3, 4, 5]]
    val s = StmMap(
      StmCount(3),
      (i: Expr) =>
        StmMap(
          StmCount(3),
          (j: Expr) => i + j + 1,
          n = 3,
          fInShape = None,
          fOutShape = None
        ),
      n = 3,
      fInShape = None,
      fOutShape = Some(3)
    )
    val prod = StmFold(
      s,
      1,
      (acc: Expr) =>
        (s: Expr) =>
          acc * StmFold(
            s,
            1,
            (acc: Expr) => (x: Expr) => acc * x,
            stmShape = Seq(3)
          ),
      stmShape = Seq(3, 3)
    )
    assert(ExprEvaluator.partialEval(prod) == IntCst(8640))
  }

  test("StmFold:3D:Sum") {
    // [[[0, 1, 2, 3],
    //   [1, 2, 3, 4]],
    //  [[1, 2, 3, 4],
    //   [2, 3, 4, 5]],
    //  [[2, 3, 4, 5],
    //   [3, 4, 5, 6]]]
    val s = StmMap(
      StmCount(3),
      (i: Expr) =>
        StmMap(
          StmCount(2),
          (j: Expr) =>
            StmMap(
              StmCount(4),
              (k: Expr) => i + j + k,
              n = 4,
              fInShape = None,
              fOutShape = None
            ),
          n = 2,
          fInShape = None,
          fOutShape = Some(4)
        ),
      n = 3,
      fInShape = None,
      fOutShape = Some(8)
    )
    val sum = StmFold(
      s,
      0,
      (acc: Expr) =>
        (s: Expr) =>
          acc + StmFold(
            s,
            0,
            (acc: Expr) =>
              (s: Expr) =>
                acc + StmFold(
                  s,
                  0,
                  (a: Expr) => (x: Expr) => a + x,
                  stmShape = Seq(4)
                ),
            stmShape = Seq(2, 4)
          ),
      stmShape = Seq(3, 2, 4)
    )
    assert(ExprEvaluator.partialEval(sum) == IntCst(72))
  }

  test("StmFold:3D:Product") {
    // [[[1, 2],
    //   [2, 3]],
    //  [[2, 3],
    //   [3, 4]],
    //  [[3, 4],
    //   [4, 5]]]
    val s = StmMap(
      StmCount(3),
      (i: Expr) =>
        StmMap(
          StmCount(2),
          (j: Expr) =>
            StmMap(
              StmCount(2),
              (k: Expr) => i + j + k + 1,
              n = 2,
              fInShape = None,
              fOutShape = None
            ),
          n = 2,
          fInShape = None,
          fOutShape = Some(2)
        ),
      n = 3,
      fInShape = None,
      fOutShape = Some(4)
    )
    val prod = StmFold(
      s,
      1,
      (acc: Expr) =>
        (s: Expr) =>
          acc * StmFold(
            s,
            1,
            (acc: Expr) =>
              (s: Expr) =>
                acc * StmFold(
                  s,
                  1,
                  (a: Expr) => (x: Expr) => a * x,
                  stmShape = Seq(2)
                ),
            stmShape = Seq(2, 2)
          ),
      stmShape = Seq(3, 2, 2)
    )
    assert(ExprEvaluator.partialEval(prod) == IntCst(207360))
  }

  test("StmScanInclusive") {
    // [2, 3,  4,  5,  6]
    val s = StmMap(
      StmCount(5),
      (x: Expr) => x + 2,
      n = 5,
      fInShape = None,
      fOutShape = None
    )
    // [2, 7, 18, 41, 88]
    val sum =
      StmScan(s, 0, (x: Expr) => (acc: Expr) => x + 2 * acc, inclusive = true)
    assertStreamEqual(sum, Seq(2, 7, 18, 41, 88))
  }

  test("StmScanExclusive") {
    // [2, 3, 4,  5,  6]
    val s = StmMap(
      StmCount(5),
      (x: Expr) => x + 2,
      n = 5,
      fInShape = None,
      fOutShape = None
    )
    // [0, 2, 7, 18, 41]
    val sum =
      StmScan(s, 0, (x: Expr) => (acc: Expr) => x + 2 * acc, inclusive = false)
    assertStreamEqual(sum, Seq(0, 2, 7, 18, 41))
  }

  test("Vec2Stm:1D") {
    val v = VecBuild(5, (i: Expr) => i + 1)

    assertStreamEqual(Vec2Stm(v), Seq(1, 2, 3, 4, 5))
  }

  test("Vec2Stm:2D") {
    val v = VecBuild(4, (i: Expr) => VecBuild(3, (j: Expr) => Tuple(i, j)))
    val s = StmMap(
      Vec2Stm(v),
      (v: Expr) => Vec2Stm(v),
      n = 4,
      fInShape = None,
      fOutShape = Some(3)
    )

    val expected = Seq(
      Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2)),
      Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2)),
      Seq(Tuple(2, 0), Tuple(2, 1), Tuple(2, 2)),
      Seq(Tuple(3, 0), Tuple(3, 1), Tuple(3, 2))
    )
    assertStreamEqual(s, expected.flatten)
  }

  test("StmPrepend:1D") {
    val s = StmCount(3)
    assertStreamEqual(
      StmPrepend(s, 42, eShape = Seq()),
      Seq(42, 0, 1, 2).map(n => IntCst(n))
    )
  }

  test("StmPrepend:2D") {
    val s0 = StmMap(
      StmCount2D(3, 3),
      (s: Expr) =>
        StmMap(s, (x: Expr) => x.__1, n = 3, fInShape = None, fOutShape = None),
      n = 3,
      fInShape = Some(3),
      fOutShape = Some(3)
    )
    val s1 = StmCst(3, 42)

    val expected = Seq(
      Seq(42, 42, 42),
      Seq(0, 1, 2),
      Seq(0, 1, 2),
      Seq(0, 1, 2)
    ).map(xs => xs.map(x => IntCst(x)))
    assertStreamEqual(
      StmPrepend(s0, s1, eShape = Seq(3)),
      expected.flatten
    )
  }

  test("StmPrepend:3D") {
    val s0 = StmMap(
      StmCount(2),
      (_: Expr) => StmCst2D(2, 2, 42),
      n = 2,
      fInShape = None,
      fOutShape = Some(4)
    )
    val s1 = StmCst2D(2, 2, 99)

    val expected = Seq(
      Seq(
        Seq(99, 99),
        Seq(99, 99)
      ),
      Seq(
        Seq(42, 42),
        Seq(42, 42)
      ),
      Seq(
        Seq(42, 42),
        Seq(42, 42)
      )
    ).map(xss => xss.map(xs => xs.map(x => IntCst(x))))
    assertStreamEqual(
      StmPrepend(s0, s1, eShape = Seq(2, 2)),
      expected.flatten.flatten
    )
  }

  test("StmAppend:1D") {
    val s = StmCount(3)
    assertStreamEqual(
      StmAppend(s, 42, stmShape = Seq(3)),
      Seq(0, 1, 2, 42).map(n => IntCst(n))
    )
  }

  test("StmAppend:2D") {
    val s0 = StmMap(
      StmCount2D(3, 3),
      (s: Expr) =>
        StmMap(s, (x: Expr) => x.__1, n = 3, fInShape = None, fOutShape = None),
      n = 3,
      fInShape = Some(3),
      fOutShape = Some(3)
    )
    val s1 = StmCst(3, 42)

    val expected = Seq(
      Seq(0, 1, 2),
      Seq(0, 1, 2),
      Seq(0, 1, 2),
      Seq(42, 42, 42)
    ).map(xs => xs.map(x => IntCst(x)))
    assertStreamEqual(StmAppend(s0, s1, stmShape = Seq(3, 3)), expected.flatten)
  }

  test("StmAppend:3D") {
    val s0 = StmMap(
      StmCount(2),
      (_: Expr) => StmCst2D(2, 2, 42),
      n = 2,
      fInShape = None,
      fOutShape = Some(4)
    )
    val s1 = StmCst2D(2, 2, 99)

    val expected = Seq(
      Seq(
        Seq(42, 42),
        Seq(42, 42)
      ),
      Seq(
        Seq(42, 42),
        Seq(42, 42)
      ),
      Seq(
        Seq(99, 99),
        Seq(99, 99)
      )
    ).map(xss => xss.map(xs => xs.map(x => IntCst(x))))
    assertStreamEqual(
      StmAppend(s0, s1, stmShape = Seq(2, 2, 2)),
      expected.flatten.flatten
    )
  }

  test("StmPrefix:1D") {
    val s = StmCount(3)
    assertStreamEqual(StmPrefix(s, 0, shape = Seq(3)), Seq())
    assertStreamEqual(StmPrefix(s, 1, shape = Seq(3)), Seq(0))
    assertStreamEqual(StmPrefix(s, 2, shape = Seq(3)), Seq(0, 1))
    assertStreamEqual(StmPrefix(s, 3, shape = Seq(3)), Seq(0, 1, 2))
  }

  test("StmPrefix:2D") {
    val s = StmCount2D(2, 3)

    val expected = Seq(
      Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2)),
      Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2))
    )
    assertStreamEqual(StmPrefix(s, 0, shape = Seq(2, 3)), Seq())
    assertStreamEqual(
      StmPrefix(s, 1, shape = Seq(2, 3)),
      expected.slice(0, 1).flatten
    )
    assertStreamEqual(
      StmPrefix(s, 2, shape = Seq(2, 3)),
      expected.slice(0, 2).flatten
    )
  }

  test("StmPrefix:3D") {
    val s = StmMap(
      StmCount(2),
      (_: Expr) => StmCount2D(4, 5),
      n = 2,
      fInShape = None,
      fOutShape = Some(20)
    )

    val expected = Seq(
      Seq(
        Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2), Tuple(0, 3), Tuple(0, 4)),
        Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2), Tuple(1, 3), Tuple(1, 4)),
        Seq(Tuple(2, 0), Tuple(2, 1), Tuple(2, 2), Tuple(2, 3), Tuple(2, 4)),
        Seq(Tuple(3, 0), Tuple(3, 1), Tuple(3, 2), Tuple(3, 3), Tuple(3, 4))
      ),
      Seq(
        Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2), Tuple(0, 3), Tuple(0, 4)),
        Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2), Tuple(1, 3), Tuple(1, 4)),
        Seq(Tuple(2, 0), Tuple(2, 1), Tuple(2, 2), Tuple(2, 3), Tuple(2, 4)),
        Seq(Tuple(3, 0), Tuple(3, 1), Tuple(3, 2), Tuple(3, 3), Tuple(3, 4))
      )
    )
    assertStreamEqual(StmPrefix(s, 0, shape = Seq(2, 4, 5)), Seq())
    assertStreamEqual(
      StmPrefix(s, 1, shape = Seq(2, 4, 5)),
      expected.slice(0, 1).flatten.flatten
    )
    assertStreamEqual(
      StmPrefix(s, 2, shape = Seq(2, 4, 5)),
      expected.slice(0, 2).flatten.flatten
    )
  }

  test("StmSuffix:1D") {
    val s = StmCount(3)
    assertStreamEqual(StmSuffix(s, 0, shape = Seq(3)), Seq())
    assertStreamEqual(StmSuffix(s, 1, shape = Seq(3)), Seq(2))
    assertStreamEqual(StmSuffix(s, 2, shape = Seq(3)), Seq(1, 2))
    assertStreamEqual(StmSuffix(s, 3, shape = Seq(3)), Seq(0, 1, 2))
  }

  test("StmSuffix:2D") {
    val s = StmCount2D(3, 2)

    val expected = Seq(
      Seq(Tuple(0, 0), Tuple(0, 1)),
      Seq(Tuple(1, 0), Tuple(1, 1)),
      Seq(Tuple(2, 0), Tuple(2, 1))
    )
    assertStreamEqual(StmSuffix(s, 0, shape = Seq(3, 2)), Seq())
    assertStreamEqual(
      StmSuffix(s, 1, shape = Seq(3, 2)),
      expected.slice(2, 3).flatten
    )
    assertStreamEqual(
      StmSuffix(s, 2, shape = Seq(3, 2)),
      expected.slice(1, 3).flatten
    )
    assertStreamEqual(
      StmSuffix(s, 3, shape = Seq(3, 2)),
      expected.slice(0, 3).flatten
    )
  }

  test("StmSuffix:3D") {
    val s = StmMap(
      StmCount(2),
      (_: Expr) => StmCount2D(4, 5),
      n = 2,
      fInShape = None,
      fOutShape = Some(20)
    )

    val expected = Seq(
      Seq(
        Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2), Tuple(0, 3), Tuple(0, 4)),
        Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2), Tuple(1, 3), Tuple(1, 4)),
        Seq(Tuple(2, 0), Tuple(2, 1), Tuple(2, 2), Tuple(2, 3), Tuple(2, 4)),
        Seq(Tuple(3, 0), Tuple(3, 1), Tuple(3, 2), Tuple(3, 3), Tuple(3, 4))
      ),
      Seq(
        Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2), Tuple(0, 3), Tuple(0, 4)),
        Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2), Tuple(1, 3), Tuple(1, 4)),
        Seq(Tuple(2, 0), Tuple(2, 1), Tuple(2, 2), Tuple(2, 3), Tuple(2, 4)),
        Seq(Tuple(3, 0), Tuple(3, 1), Tuple(3, 2), Tuple(3, 3), Tuple(3, 4))
      )
    )
    assertStreamEqual(StmSuffix(s, 0, shape = Seq(2, 4, 5)), Seq())
    assertStreamEqual(
      StmSuffix(s, 1, shape = Seq(2, 4, 5)),
      expected.slice(1, 2).flatten.flatten
    )
    assertStreamEqual(
      StmSuffix(s, 2, shape = Seq(2, 4, 5)),
      expected.slice(0, 2).flatten.flatten
    )
  }

  test("StmShiftLeft:1D") {
    val s = StmCount(3)
    assertStreamEqual(StmShiftLeft(s, 42, stmShape = Seq(3)), Seq(1, 2, 42))
  }

  test("StmShiftLeft:2D") {
    val s0 = StmCount2D(3, 4)
    val s1 = StmMap(
      StmCount(4),
      (j: Expr) => Tuple(99, j),
      n = 4,
      fInShape = None,
      fOutShape = None
    )

    val expected = Seq(
      Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2), Tuple(1, 3)),
      Seq(Tuple(2, 0), Tuple(2, 1), Tuple(2, 2), Tuple(2, 3)),
      Seq(Tuple(99, 0), Tuple(99, 1), Tuple(99, 2), Tuple(99, 3))
    )
    assertStreamEqual(
      StmShiftLeft(s0, s1, stmShape = Seq(3, 4)),
      expected.flatten
    )
  }

  test("StmShiftLeft:3D") {
    val s0 = StmMap(
      StmCount(2),
      (i: Expr) => StmCst2D(4, 5, i),
      n = 2,
      fInShape = None,
      fOutShape = Some(20)
    )
    val s1 = StmCst2D(4, 5, 42)

    val expected = Seq(
      Seq(
        Seq(1, 1, 1, 1, 1),
        Seq(1, 1, 1, 1, 1),
        Seq(1, 1, 1, 1, 1),
        Seq(1, 1, 1, 1, 1)
      ),
      Seq(
        Seq(42, 42, 42, 42, 42),
        Seq(42, 42, 42, 42, 42),
        Seq(42, 42, 42, 42, 42),
        Seq(42, 42, 42, 42, 42)
      )
    ).map(xss => xss.map(xs => xs.map(x => IntCst(x))))
    assertStreamEqual(
      StmShiftLeft(s0, s1, stmShape = Seq(2, 4, 5)),
      expected.flatten.flatten
    )
  }

  test("StmShiftRight:1D") {
    val s = StmCount(3)
    assertStreamEqual(StmShiftRight(s, 42, stmShape = Seq(3)), Seq(42, 0, 1))
  }

  test("StmShiftRight:2D") {
    val s0 = StmCount2D(3, 4)
    val s1 = StmMap(
      StmCount(4),
      (j: Expr) => Tuple(99, j),
      n = 4,
      fInShape = None,
      fOutShape = None
    )

    val expected = Seq(
      Seq(Tuple(99, 0), Tuple(99, 1), Tuple(99, 2), Tuple(99, 3)),
      Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2), Tuple(0, 3)),
      Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2), Tuple(1, 3))
    )
    assertStreamEqual(
      StmShiftRight(s0, s1, stmShape = Seq(3, 4)),
      expected.flatten
    )
  }

  test("StmShiftRight:3D") {
    val s0 = StmMap(
      StmCount(2),
      (i: Expr) => StmCst2D(4, 5, i),
      n = 2,
      fInShape = None,
      fOutShape = Some(20)
    )
    val s1 = StmCst2D(4, 5, 42)

    val expected = Seq(
      Seq(
        Seq(42, 42, 42, 42, 42),
        Seq(42, 42, 42, 42, 42),
        Seq(42, 42, 42, 42, 42),
        Seq(42, 42, 42, 42, 42)
      ),
      Seq(
        Seq(0, 0, 0, 0, 0),
        Seq(0, 0, 0, 0, 0),
        Seq(0, 0, 0, 0, 0),
        Seq(0, 0, 0, 0, 0)
      )
    ).map(xss => xss.map(xs => xs.map(x => IntCst(x))))
    assertStreamEqual(
      StmShiftRight(s0, s1, stmShape = Seq(2, 4, 5)),
      expected.flatten.flatten
    )
  }

  test("StmConcat:1D") {
    assertStreamEqual(
      StmConcat(StmCst(1, 77), StmCst(1, 77), len1 = 1),
      Seq(77, 77)
    )
    assertStreamEqual(
      StmConcat(StmCount(3), StmCst(4, 77), len1 = 3),
      Seq(0, 1, 2, 77, 77, 77, 77)
    )
    assertStreamEqual(
      StmConcat(
        StmConcat(StmCount(3), StmCst(4, 77), len1 = 3),
        StmCount(2),
        len1 = 7
      ),
      Seq(0, 1, 2, 77, 77, 77, 77, 0, 1)
    )
  }

  test("StmConcat:2D") {
    val s0 = StmCst2D(2, 2, 42)
    val s1 = StmMap(
      StmCount(3),
      (i: Expr) =>
        StmMap(
          StmCount(2),
          (j: Expr) => i + j,
          n = 2,
          fInShape = None,
          fOutShape = None
        ),
      n = 3,
      fInShape = None,
      fOutShape = Some(2)
    )
    val expected = Seq(
      Seq(42, 42),
      Seq(42, 42),
      Seq(0, 1),
      Seq(1, 2),
      Seq(2, 3)
    ).map(xs => xs.map(x => IntCst(x)))
    assertStreamEqual(StmConcat(s0, s1, len1 = 4), expected.flatten)
  }

  test("StmConcat:3D") {
    val s0 = StmMap(
      StmCount(3),
      (_: Expr) => StmCst2D(2, 2, 100),
      n = 3,
      fInShape = None,
      fOutShape = Some(4)
    )
    val s1 = StmMap(
      StmCount(2),
      (i: Expr) => StmCst2D(2, 2, i),
      n = 2,
      fInShape = None,
      fOutShape = Some(4)
    )
    val expected = Seq(
      Seq(
        Seq(100, 100),
        Seq(100, 100)
      ),
      Seq(
        Seq(100, 100),
        Seq(100, 100)
      ),
      Seq(
        Seq(100, 100),
        Seq(100, 100)
      ),
      Seq(
        Seq(0, 0),
        Seq(0, 0)
      ),
      Seq(
        Seq(1, 1),
        Seq(1, 1)
      )
    ).map(xss => xss.map(xs => xs.map(x => IntCst(x))))
    assertStreamEqual(StmConcat(s0, s1, len1 = 12), expected.flatten.flatten)
  }

  test("StmZip:1D") {
    val a = StmCount(4)
    val b = StmMap(
      StmCount(4),
      (x: Expr) => x % 2 === 0,
      n = 4,
      fInShape = None,
      fOutShape = None
    )
    assertStreamEqual(
      StmZip(a, b),
      Seq(Tuple(0, True), Tuple(1, False), Tuple(2, True), Tuple(3, False))
    )
  }

  test("StmZipAlternating:1D") {
    val a = StmCount(4)
    val b = StmMap(
      StmCount(4),
      (x: Expr) => x + 5,
      n = 4,
      fInShape = None,
      fOutShape = None
    )
    assertStreamEqual(
      StmZipAlternating(a, b),
      Seq(Tuple(0, 5), Tuple(6, 1), Tuple(2, 7), Tuple(8, 3))
    )
  }

  test("StmRepeat:1D") {
    val s = StmCount(3)
    val expected = Seq(IntCst(0), IntCst(1), IntCst(2))
    assertStreamEqual(StmRepeat(s, 0, n = 3), Seq())
    assertStreamEqual(StmRepeat(s, 1, n = 3), expected)
    assertStreamEqual(StmRepeat(s, 2, n = 3), expected ++ expected)
    assertStreamEqual(StmRepeat(s, 3, n = 3), expected ++ expected ++ expected)
  }

  test("StmRepeat:2D") {
    val s = StmCount2D(2, 3)
    val expected = Seq(
      Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2)),
      Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2))
    ).flatten
    assertStreamEqual(StmRepeat(s, 0, n = 6), Seq())
    assertStreamEqual(StmRepeat(s, 1, n = 6), expected)
    assertStreamEqual(StmRepeat(s, 2, n = 6), expected ++ expected)
    assertStreamEqual(StmRepeat(s, 3, n = 6), expected ++ expected ++ expected)
  }

  test("StmRepeat:3D") {
    val s = StmMap(
      StmCount(2),
      (i: Expr) => StmCst2D(3, 4, i),
      n = 2,
      fInShape = None,
      fOutShape = Some(12)
    )
    val expected = Seq(
      Seq(
        Seq(0, 0, 0, 0),
        Seq(0, 0, 0, 0),
        Seq(0, 0, 0, 0)
      ),
      Seq(
        Seq(1, 1, 1, 1),
        Seq(1, 1, 1, 1),
        Seq(1, 1, 1, 1)
      )
    ).flatMap(xss => xss.flatMap(xs => xs.map(x => IntCst(x))))
    assertStreamEqual(StmRepeat(s, 0, n = 24), Seq())
    assertStreamEqual(StmRepeat(s, 1, n = 24), expected)
    assertStreamEqual(StmRepeat(s, 2, n = 24), expected ++ expected)
    assertStreamEqual(StmRepeat(s, 3, n = 24), expected ++ expected ++ expected)
  }

  test("StmSplit:1D-2D") {
    val s = StmCount(6)

    val expected = (0 until 6).map(n => IntCst(n))
    assertStreamEqual(StmSplit(s, 1), expected)
    assertStreamEqual(StmSplit(s, 2), expected)
    assertStreamEqual(StmSplit(s, 3), expected)
    assertStreamEqual(StmSplit(s, 6), expected)
  }

  test("StmJoin:2D-1D") {
    val s = StmCount2D(3, 2)

    val expected = Seq((0, 0), (0, 1), (1, 0), (1, 1), (2, 0), (2, 1))
      .map((i, j) => Tuple(i, j))
    assertStreamEqual(StmJoin(s), expected)
  }

  test("StmJoin:3D-2D-1D") {
    // [[[(0, 0), (0, 1), (0, 2), (0, 3)],
    //   [(1, 0), (1, 1), (1, 2), (1, 3)],
    //   [(2, 0), (2, 1), (2, 2), (2, 3)]],
    //
    //  [[(0, 0), (0, 1), (0, 2), (0, 3)],
    //   [(1, 0), (1, 1), (1, 2), (1, 3)],
    //   [(2, 0), (2, 1), (2, 2), (2, 3)]]]
    val s = StmMap(
      StmCount(2),
      (_: Expr) => StmCount2D(3, 4),
      n = 2,
      fInShape = None,
      fOutShape = Some(12)
    )
    val expected = Seq(
      Seq(
        Tuple(0, 0),
        Tuple(0, 1),
        Tuple(0, 2),
        Tuple(0, 3)
      ),
      Seq(
        Tuple(1, 0),
        Tuple(1, 1),
        Tuple(1, 2),
        Tuple(1, 3)
      ),
      Seq(
        Tuple(2, 0),
        Tuple(2, 1),
        Tuple(2, 2),
        Tuple(2, 3)
      ),
      Seq(
        Tuple(0, 0),
        Tuple(0, 1),
        Tuple(0, 2),
        Tuple(0, 3)
      ),
      Seq(
        Tuple(1, 0),
        Tuple(1, 1),
        Tuple(1, 2),
        Tuple(1, 3)
      ),
      Seq(
        Tuple(2, 0),
        Tuple(2, 1),
        Tuple(2, 2),
        Tuple(2, 3)
      )
    )
    assertStreamEqual(StmJoin(s), expected.flatten)
    assertStreamEqual(StmJoin(StmJoin(s)), expected.flatten)
  }

  test("StmSplitJoin") {
    val s = StmCount(6)
    val elems = (0 until 6).map(x => IntCst(x))
    assert(StreamTests.stm2Seq(StmJoin(StmSplit(s, 1))) == elems)
    assert(StreamTests.stm2Seq(StmJoin(StmSplit(s, 2))) == elems)
    assert(StreamTests.stm2Seq(StmJoin(StmSplit(s, 3))) == elems)
    assert(StreamTests.stm2Seq(StmJoin(StmSplit(s, 6))) == elems)
  }

  test("StmSlide") {
    val s = StmCount(4)
    val actual = StmSlide(s, 2)
    val expected = Seq(
      Seq(IntCst(0), IntCst(1)),
      Seq(IntCst(1), IntCst(2)),
      Seq(IntCst(2), IntCst(3))
    )

    val actualElements = StreamTests
      .stm2Seq(actual)
      .map(v => VectorTests.vec2Seq(v))
    assert(actualElements == expected)
  }

  test("StmSlideSameSize") {
    val s = StmCount(5)
    val actual = StmSlide(s, 5)
    val expected =
      Seq(Seq(IntCst(0), IntCst(1), IntCst(2), IntCst(3), IntCst(4)))

    val actualElements = StreamTests
      .stm2Seq(actual)
      .map(v => VectorTests.vec2Seq(v))
    assert(actualElements == expected)
  }
}
