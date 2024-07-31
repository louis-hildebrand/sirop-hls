import org.scalatest.funsuite.AnyFunSuite

import scala.runtime.stdLibPatches.Predef.assert

object StreamTests {
  def stm2Seq(stm: Expr): Seq[Expr] = {
    var elements = Seq[Expr]()
    val len = ExprEvaluator.partialEval(StmLength(stm)).asInstanceOf[IntCst].i
    var n: Expr = Tuple(stm, 0 /*unused*/ )
    (0 until len).foreach(i =>
      n = ExprEvaluator.partialEval(StmNext(n.__0))
      val e = ExprEvaluator.partialEval(n.__1)
      elements = elements :+ e
    )
    elements
  }

  def stmStm2SeqSeq(stm: Expr): Seq[Seq[Expr]] =
    stm2Seq(stm).map(e => stm2Seq(e))
}

class StreamTests extends AnyFunSuite {

  def assertStreamEqual(stream: Expr, expectedSeq: Seq[Expr]) = {
    assert(StreamTests.stm2Seq(stream) == expectedSeq)
  }

  def assert2DStreamEqual(stream: Expr, expectedSeq: Seq[Seq[Expr]]) = {
    assert(StreamTests.stmStm2SeqSeq(stream) == expectedSeq)
  }

  test("IntCst") {
    val intAst = IntCst(3)
    assert(ExprEvaluator.partialEval(intAst) == IntCst(3))
  }

  test("CstStream") {
    val cstStream = CstStream(4, 3)
    assertStreamEqual(cstStream, Seq(3, 3, 3, 3))
  }

  test("CounterStream") {
    val cntAst = CounterStream(5)
    assertStreamEqual(cntAst, Seq(0, 1, 2, 3, 4))
  }

  test("Counter2DStream") {
    val s = Counter2DStream(2, 3)
    val expected = Seq(
      Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2)),
      Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2))
    )
    assert2DStreamEqual(s, expected)
  }

  test("Map") {
    val mapAst = StmMap(CounterStream(5), e => Add(e, IntCst(7)))
    assertStreamEqual(mapAst, Seq(7, 8, 9, 10, 11))
  }

  test("Pad") {
    val stmPadFirst = StmPrepend(CounterStream(3), IntCst(33))
    assertStreamEqual(stmPadFirst, Seq(33, 0, 1, 2))

    val stmPadLast = StmAppend(CounterStream(3), IntCst(44))
    assertStreamEqual(stmPadLast, Seq(0, 1, 2, 44))

    val stmFirstLast =
      StmAppend(StmPrepend(CounterStream(3), IntCst(33)), IntCst(44))
    assertStreamEqual(stmFirstLast, Seq(33, 0, 1, 2, 44))

    val stmLastFirst =
      StmPrepend(StmAppend(CounterStream(3), IntCst(44)), IntCst(33))
    assertStreamEqual(stmLastFirst, Seq(33, 0, 1, 2, 44))
  }

  test("Concat") {
    val stmConcatTwice = StmConcat(CstStream(1, 77), CstStream(1, 77))
    assertStreamEqual(stmConcatTwice, Seq(77, 77))

    val stmConcat = StmConcat(CounterStream(3), CstStream(4, 77))
    assertStreamEqual(stmConcat, Seq(0, 1, 2, 77, 77, 77, 77))

    val stmConcat1 = StmConcat(CounterStream(3), CstStream(4, 77))
    val stmConcat2 = StmConcat(stmConcat1, CounterStream(2))
    assertStreamEqual(stmConcat2, Seq(0, 1, 2, 77, 77, 77, 77, 0, 1))
  }

  test("2DTupleStream") {
    val cnt2DAst = Counter2DStream(2, 3)
    val next1 = StmNext(cnt2DAst)
    assertStreamEqual(next1.__1, Seq(Tuple(0, 0), Tuple(0, 1), Tuple(0, 2)))

    val next2 = StmNext(next1.__0)
    assertStreamEqual(next2.__1, Seq(Tuple(1, 0), Tuple(1, 1), Tuple(1, 2)))

    assert(ExprEvaluator.partialEval(StmLength(next2.__0)) == IntCst(0))
  }

  test("MapMapStream") {
    val stream = StmBuild(3, 0, (i: Expr) => Tuple(i + 1, i))
    val mapMapStream = StmMap(StmMap(stream, e => e + 1), e => e * 2)

    assertStreamEqual(mapMapStream, Seq(2, 4, 6))
  }

  test("Map2DStream") {
    val stream2D = StmBuild(
      2,
      0,
      (i: Expr) =>
        Tuple(i + 1, StmBuild(3, 0, (j: Expr) => Tuple(j + 1, i * 3 + j)))
    )

    val mappedStream2D = StmMap(stream2D, e1 => StmMap(e1, e2 => e2 + 1))

    val next1 = StmNext(mappedStream2D)
    assertStreamEqual(next1.__1, Seq(1, 2, 3))

    val next2 = StmNext(next1.__0)
    assertStreamEqual(next2.__1, Seq(4, 5, 6))

    assert(ExprEvaluator.partialEval(StmLength(next2.__0)) == IntCst(0))
  }

  test("StmSum") {
    val sum = StmFold(CounterStream(5), 2, (i: Expr) => (acc: Expr) => i + acc)
    assert(ExprEvaluator.partialEval(sum) == IntCst(12))
  }

  test("StmScanInclusive") {
    // [2, 3,  4,  5,  6]
    val s = StmMap(CounterStream(5), (x: Expr) => x + 2)
    // [2, 7, 18, 41, 88]
    val sum =
      StmScan(s, 0, (x: Expr) => (acc: Expr) => x + 2 * acc, inclusive = true)
    assertStreamEqual(sum, Seq(2, 7, 18, 41, 88))
  }

  test("StmScanExclusive") {
    // [2, 3, 4,  5,  6]
    val s = StmMap(CounterStream(5), (x: Expr) => x + 2)
    // [0, 2, 7, 18, 41]
    val sum =
      StmScan(s, 0, (x: Expr) => (acc: Expr) => x + 2 * acc, inclusive = false)
    assertStreamEqual(sum, Seq(0, 2, 7, 18, 41))
  }

  test("fold2Dstm") {
    val stream2D = StmBuild(
      2,
      0,
      (i: Expr) =>
        Tuple(i + 1, StmBuild(3, 0, (j: Expr) => Tuple(j + 1, i * 3 + j)))
    )

    val mapFold = StmMap(
      stream2D,
      innerStream =>
        StmFold(innerStream, 0, (i: Expr) => (acc: Expr) => i + acc)
    )

    assertStreamEqual(mapFold, Seq(3, 12))
  }

  test("StmZip") {
    val a = CounterStream(3)
    val b = StmMap(CounterStream(3), x => x + 5)
    val zipped = StmZip(a, b)
    assertStreamEqual(zipped, Seq(Tuple(0, 5), Tuple(1, 6), Tuple(2, 7)))
  }

  test("Vec2Stm") {
    val oneTwoThreeVec = VecBuild(3, (i: Expr) => i + 1)
    val stm = Vec2Stm(oneTwoThreeVec)

    assertStreamEqual(stm, Seq(1, 2, 3))
  }

  test("StmRepeat") {
    val s = CounterStream(3)
    val s2 = StmRepeat(s, 4)
    assert2DStreamEqual(
      s2,
      Seq(Seq(0, 1, 2), Seq(0, 1, 2), Seq(0, 1, 2), Seq(0, 1, 2))
    )
  }

  test("StmSplit") {
    val s = CounterStream(6)
    val actual = StmSplit(s, 3)
    val expected = Seq(
      Seq(IntCst(0), IntCst(1), IntCst(2)),
      Seq(IntCst(3), IntCst(4), IntCst(5))
    )
    assert2DStreamEqual(actual, expected)
  }

  test("StmJoin") {
    val s = Counter2DStream(3, 2)
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
    val s = StmRepeat(Counter2DStream(3, 4), 2)
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
    val s = CounterStream(6)
    val elems = Seq(0, 1, 2, 3, 4, 5).map(x => IntCst(x))
    assert(StreamTests.stm2Seq(StmJoin(StmSplit(s, 1))) == elems)
    assert(StreamTests.stm2Seq(StmJoin(StmSplit(s, 2))) == elems)
    assert(StreamTests.stm2Seq(StmJoin(StmSplit(s, 3))) == elems)
    assert(StreamTests.stm2Seq(StmJoin(StmSplit(s, 6))) == elems)
  }

  test("StmSlide") {
    val s = CounterStream(4)
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
    val s = CounterStream(5)
    val actual = StmSlide(s, 5)
    val expected =
      Seq(Seq(IntCst(0), IntCst(1), IntCst(2), IntCst(3), IntCst(4)))

    val actualElements = StreamTests
      .stm2Seq(actual)
      .map(v => VectorTests.vec2Seq(v))
    assert(actualElements == expected)
  }
}
