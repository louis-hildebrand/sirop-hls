package opt

import ir._
import operations._
import org.scalatest.funsuite.AnyFunSuite

class StmFusePassTests extends AnyFunSuite {
  val canon = (e: Expr) => StmCanonPass.canonicalize(e.asInstanceOf[StmBuild])
  val fuse = StmFusePass.fuse
  val fuseCompletely = StmFusePass.fuseCompletely
  val stm2Seq = StreamTests.stm2Seq

  test("CountFromFive") {
    val s = StmMap(
      StmCount(3),
      (x: Expr) => x + 5,
      n = 3,
      fInShape = None,
      fOutShape = None
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
        StmMap(p, f, n = 5, fInShape = None, fOutShape = None),
        g,
        n = 5,
        fInShape = None,
        fOutShape = None
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
      5,
      Tuple(p),
      (acc: Expr) =>
        Tuple(
          Tuple(StmNext(acc.__0).__0), {
            val x = StmNext(acc.__0).__1
            (x + 2) * (x + 3) * (x + 4) - 10
          },
          True
        )
    )
    assert(actual == ideal)
  }

  test("MapFold") {
    val p = Param()
    val n = Param()
    val f = (x: Expr) => (x + 2) * (x + 3) * (x + 4)
    val s =
      StmFold(
        StmMap(p, f, n = n, fInShape = None, fOutShape = None),
        0,
        (acc: Expr) => (x: Expr) => acc + x,
        stmShape = Seq(n)
      )
    val actual = canon(fuseCompletely(s))

    // Correct behaviour
    // (Using one example p, f, and g)
    val call = (e: Expr) => Let(n, 5, Let(p, StmCount(n), e))
    val expected = PartialEvalPass.partialEval(
      FunCall(f, 0)
        + FunCall(f, 1)
        + FunCall(f, 2)
        + FunCall(f, 3)
        + FunCall(f, 4)
    )
    assert(stm2Seq(call(s)) == Seq(expected))
    assert(stm2Seq(call(actual)) == Seq(expected))
    // Successful fusion
    val ideal = canon(
      fuseCompletely(
        StmFold(
          p,
          0,
          (acc: Expr) => (x: Expr) => acc + (x + 2) * (x + 3) * (x + 4),
          stmShape = Seq(n)
        )
      )
    )
    assert(actual == ideal)
  }

  test("StmShiftRight") {
    val p = Param()
    val s = StmPrepend(
      StmPrefix(p, StmLength(p) - 1, shape = Seq(5)),
      42,
      eShape = Seq()
    )
    val actual = canon(fuseCompletely(s))

    // Correct behaviour
    // (Using one example p)
    val call = (e: Expr) => Let(p, StmCount(5), e)
    val expectedElems = Seq(42, 0, 1, 2, 3).map(n => IntCst(n))
    assert(stm2Seq(call(s)) == expectedElems)
    assert(stm2Seq(call(actual)) == expectedElems)
    // Successful fusion
    val ideal = StmBuild(
      StmLength(p),
      Tuple(p, 1, 0),
      (acc: Expr) =>
        IfThenElse(
          acc.__1 === 0,
          IfThenElse(
            acc.__2 < (StmLength(p) - 1),
            Tuple(
              Tuple(StmNext(acc.__0).__0, acc.__1, acc.__2 + 1),
              StmNext(acc.__0).__1,
              True
            ),
            Tuple(
              Tuple(StmNext(acc.__0).__0, acc.__1, acc.__2 + 1),
              DontCare,
              False
            )
          ),
          Tuple(Tuple(acc.__0, acc.__1 - 1, acc.__2), 42, True)
        )
    )
    assert(actual == ideal)
  }

  test("StmShiftLeft") {
    val p = Param()
    val n = 5
    val s =
      StmAppend(StmSuffix(p, n - 1, shape = Seq(5)), 42, stmShape = Seq(4))
    val actual = canon(fuseCompletely(s))

    // Correct behaviour
    val call = (e: Expr) => Let(p, StmCount(n), e)
    val expectedElems = Seq(1, 2, 3, 4, 42).map(n => IntCst(n))
    assert(stm2Seq(call(s)) == expectedElems)
    assert(stm2Seq(call(actual)) == expectedElems)
    // Successful fusion
    val ideal = StmBuild(
      5,
      Tuple(p, 4, 0),
      (acc: Expr) =>
        IfThenElse(
          acc.__1 === 0,
          Tuple(Tuple(acc.__0, acc.__1, acc.__2), 42, True),
          IfThenElse(
            acc.__2 >= 1,
            Tuple(
              Tuple(StmNext(acc.__0).__0, acc.__1 - 1, acc.__2 + 1),
              StmNext(acc.__0).__1,
              True
            ),
            Tuple(
              Tuple(StmNext(acc.__0).__0, acc.__1, acc.__2 + 1),
              DontCare,
              False
            )
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
