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

  inline def assertStreamEqual(stream: Expr, expectedSeq: Seq[Expr]) = {
    assert(StreamTests.stm2Seq(stream) == expectedSeq)
  }

  inline def assert2DStreamEqual(stream: Expr, expectedSeq: Seq[Seq[Expr]]) = {
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
    val e = (n: Int) => Iterate(n, 3, (x: Expr) => x * x)
    assert(ExprEvaluator.partialEval(e(0)) == IntCst(3))
    assert(ExprEvaluator.partialEval(e(1)) == IntCst(9))
    assert(ExprEvaluator.partialEval(e(2)) == IntCst(81))
    assert(ExprEvaluator.partialEval(e(3)) == IntCst(81 * 81))
  }

  test("Iterate:PlusTwo") {
    val e = (n: Int) => Iterate(n, -10, (x: Expr) => x + 2)
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

  test("StmMap:2D-1D:Fold") {
    val s = StmMap(
      StmCount2D(4, 3),
      (s: Expr) =>
        StmFold(s, 0, (x: Expr) => (acc: Expr) => acc + x.__0 + x.__1),
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
      (s: Expr) => StmAccess(s, 1, n = 3),
      n = 4,
      fInShape = Some(3),
      fOutShape = None
    )
    val expected = Seq(Tuple(0, 1), Tuple(1, 1), Tuple(2, 1), Tuple(2, 2))
    assertStreamEqual(s, expected)
  }

  test("StmMap:2D-2D:Map") {
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

  test("StmMap:2D-2D:Prefix") {
    val stm = StmCount2D(3, 1000)
    val actual =
      StmMap(
        stm,
        (s: Expr) => StmPrefix(s, 2),
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

  test("StmMap:2D-2D:Suffix") {
    val stm = StmCount2D(3, 1000)
    val actual =
      StmMap(
        stm,
        (s: Expr) => StmSuffix(s, 2, 1000),
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

  test("StmAccess") {
    val s = StmMap(
      StmCount(3),
      (x: Expr) => x + 5,
      n = 3,
      fInShape = None,
      fOutShape = None
    )
    assert(ExprEvaluator.partialEval(StmAccess(s, 0, 3)) == IntCst(5))
    assert(ExprEvaluator.partialEval(StmAccess(s, 1, 3)) == IntCst(6))
    assert(ExprEvaluator.partialEval(StmAccess(s, 2, 3)) == IntCst(7))
  }

  test("StmFold:1D:Sum") {
    val sum = StmFold(StmCount(6), 3, (x: Expr) => (y: Expr) => x + y)
    val pe = ExprEvaluator.partialEval(sum)
    assert(pe == IntCst(18))
  }

  test("StmFold:2D:Sum") {
    val sum = StmFold(
      StmCount2D(3, 2),
      0,
      (s: Expr) =>
        StmFold(s, 0, (x: Expr) => (acc: Expr) => acc + x.__0 + x.__1)
    )
    assert(ExprEvaluator.partialEval(sum) == IntCst(40))
  }

  test("Pad") {
    val stmPadFirst = StmPrepend(StmCount(3), IntCst(33))
    assertStreamEqual(stmPadFirst, Seq(33, 0, 1, 2))

    val stmPadLast = StmAppend(StmCount(3), IntCst(44))
    assertStreamEqual(stmPadLast, Seq(0, 1, 2, 44))

    val stmFirstLast =
      StmAppend(StmPrepend(StmCount(3), IntCst(33)), IntCst(44))
    assertStreamEqual(stmFirstLast, Seq(33, 0, 1, 2, 44))

    val stmLastFirst =
      StmPrepend(StmAppend(StmCount(3), IntCst(44)), IntCst(33))
    assertStreamEqual(stmLastFirst, Seq(33, 0, 1, 2, 44))
  }

  test("StmPrefix") {
    val s = StmCount(3)
    assertStreamEqual(StmPrefix(s, 0), Seq())
    assertStreamEqual(StmPrefix(s, 1), Seq(0))
    assertStreamEqual(StmPrefix(s, 2), Seq(0, 1))
    assertStreamEqual(StmPrefix(s, 3), Seq(0, 1, 2))
  }

  test("StmSuffix") {
    val s = StmCount(3)
    assertStreamEqual(StmSuffix(s, 0, 3), Seq())
    assertStreamEqual(StmSuffix(s, 1, 3), Seq(2))
    assertStreamEqual(StmSuffix(s, 2, 3), Seq(1, 2))
    assertStreamEqual(StmSuffix(s, 3, 3), Seq(0, 1, 2))
  }

  test("StmShiftLeft") {
    val s = StmCount(3)
    assertStreamEqual(StmShiftLeft(s, 42, 3), Seq(1, 2, 42))
  }

  test("StmShiftRight") {
    val s = StmCount(3)
    assertStreamEqual(StmShiftRight(s, 42), Seq(42, 0, 1))
  }

  test("Concat") {
    val stmConcatTwice = StmConcat(StmCst(1, 77), StmCst(1, 77))
    assertStreamEqual(stmConcatTwice, Seq(77, 77))

    val stmConcat = StmConcat(StmCount(3), StmCst(4, 77))
    assertStreamEqual(stmConcat, Seq(0, 1, 2, 77, 77, 77, 77))

    val stmConcat1 = StmConcat(StmCount(3), StmCst(4, 77))
    val stmConcat2 = StmConcat(stmConcat1, StmCount(2))
    assertStreamEqual(stmConcat2, Seq(0, 1, 2, 77, 77, 77, 77, 0, 1))
  }

  test("2DTupleStream") {
    val cnt2DAst = StmCount2D(2, 3)
    val next1 = StmNext(cnt2DAst)
    assertStreamEqual(next1.__1, Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2)))

    val next2 = StmNext(next1.__0)
    assertStreamEqual(next2.__1, Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2)))

    assert(ExprEvaluator.partialEval(StmLength(next2.__0)) == IntCst(0))
  }

  test("StmSum") {
    val sum = StmFold(StmCount(5), 2, (i: Expr) => (acc: Expr) => i + acc)
    assert(ExprEvaluator.partialEval(sum) == IntCst(12))
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

  test("StmZip") {
    val a = StmCount(3)
    val b = StmMap(
      StmCount(3),
      (x: Expr) => x % 2 === 0,
      n = 3,
      fInShape = None,
      fOutShape = None
    )
    val zipped = StmZip(a, b)
    assertStreamEqual(
      zipped,
      Seq(Tuple(0, True), Tuple(1, False), Tuple(2, True))
    )
  }

  test("StmZipAlternating") {
    val a = StmCount(4)
    val b = StmMap(
      StmCount(4),
      (x: Expr) => x + 5,
      n = 4,
      fInShape = None,
      fOutShape = None
    )
    val zipped = StmZipAlternating(a, b)
    assertStreamEqual(
      zipped,
      Seq(Tuple(0, 5), Tuple(6, 1), Tuple(2, 7), Tuple(8, 3))
    )
  }

  test("Vec2Stm") {
    val oneTwoThreeVec = VecBuild(3, (i: Expr) => i + 1)
    val stm = Vec2Stm(oneTwoThreeVec)

    assertStreamEqual(stm, Seq(1, 2, 3))
  }

  test("StmRepeat") {
    val s = StmCount(3)
    val s2 = StmRepeat(s, 4)
    assert2DStreamEqual(
      s2,
      Seq(Seq(0, 1, 2), Seq(0, 1, 2), Seq(0, 1, 2), Seq(0, 1, 2))
    )
  }

  test("StmSplit") {
    val s = StmCount(6)

    var expected = Seq(
      Seq(IntCst(0)),
      Seq(IntCst(1)),
      Seq(IntCst(2)),
      Seq(IntCst(3)),
      Seq(IntCst(4)),
      Seq(IntCst(5))
    )
    assert2DStreamEqual(StmSplit(s, 1), expected)

    expected = Seq(
      Seq(IntCst(0), IntCst(1)),
      Seq(IntCst(2), IntCst(3)),
      Seq(IntCst(4), IntCst(5))
    )
    assert2DStreamEqual(StmSplit(s, 2), expected)

    expected = Seq(
      Seq(IntCst(0), IntCst(1), IntCst(2)),
      Seq(IntCst(3), IntCst(4), IntCst(5))
    )
    assert2DStreamEqual(StmSplit(s, 3), expected)

    expected = Seq(
      Seq(IntCst(0), IntCst(1), IntCst(2), IntCst(3), IntCst(4), IntCst(5))
    )
    assert2DStreamEqual(StmSplit(s, 6), expected)
  }

  test("StmJoin") {
    val s = StmCount2D(3, 2)
    val actual = StmJoin(s)
    val expected = Seq((0, 0), (0, 1), (1, 0), (1, 1), (2, 0), (2, 1))
      .map((i, j) => Tuple(i, j))
    assertStreamEqual(actual, expected)
  }

  test("StmJoin3D") {
    // [[[(0, 0), (0, 1), (0, 2), (0, 3)],
    //   [(1, 0), (1, 1), (1, 2), (1, 3)],
    //   [(2, 0), (2, 1), (2, 2), (2, 3)]],
    //
    //  [[(0, 0), (0, 1), (0, 2), (0, 3)],
    //   [(1, 0), (1, 1), (1, 2), (1, 3)],
    //   [(2, 0), (2, 1), (2, 2), (2, 3)]]]
    val s = StmRepeat(StmCount2D(3, 4), 2)
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
    assert(StreamTests.stmStm2SeqSeq(StmJoin(s)) == expected)
    assert(StreamTests.stm2Seq(StmJoin(StmJoin(s))) == expected.flatten.toSeq)
  }

  test("StmSplitJoin") {
    val s = StmCount(6)
    val elems = Seq(0, 1, 2, 3, 4, 5).map(x => IntCst(x))
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
