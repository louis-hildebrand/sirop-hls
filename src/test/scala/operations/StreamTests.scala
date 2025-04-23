package operations

import ir._
import opt.PartialEvalPass
import org.scalatest.funsuite.AnyFunSuite

object StreamTests {

  /** Use <code>ir.eval</code> directly instead.
    */
  @deprecated
  def stm2Seq(stm: Expr): Seq[Expr] = {
    val n = PartialEvalPass.partialEval(StmLength(stm)()) match {
      case IntCst(n) => n
      case e =>
        throw new IllegalArgumentException(
          s"Stream length $e is not an integer"
        )
    }
    if (n < 0) {
      throw new IllegalArgumentException(s"Stream has negative length ($n)!")
    } else if (n == 0) {
      Seq()
    } else {
      val next = PartialEvalPass.partialEval(StmNext(stm)())
      PartialEvalPass.partialEval(next.__1) +: stm2Seq(next.__0)
    }
  }
}

class StreamTests extends AnyFunSuite {

  @inline
  def assertStreamEqual(stream: Expr, expectedSeq: Seq[Expr]): Unit = {
    val expected = StmLiteral(expectedSeq: _*)()
    assert(ir.eval(stream) == expected)
  }

  test("StmCst:Int") {
    val s = StmCst(4, 3)
    assert(ir.eval(s) == StmLiteral.ints(3, 3, 3, 3))
  }

  test("StmCst:Tuple") {
    val c = Tuple(False, 99)()
    val s = StmCst(3, c)
    assert(ir.eval(s) == StmLiteral(c, c, c)())
  }

  test("StmCount") {
    val s = StmCount(5)
    assert(ir.eval(s) == StmLiteral.ints(0, 1, 2, 3, 4))
  }

  test("StmCountFrom") {
    val s = StmCountFrom(3, 4)
    assert(ir.eval(s) == StmLiteral.ints(3, 4, 5, 6))
  }

  test("StmRange(4, 3, 2)") {
    val s = StmRange(4, 3, 2)
    assert(ir.eval(s) == StmLiteral.ints(3, 5, 7, 9))
  }

  test("StmRange(3, 2, -3)") {
    val s = StmRange(3, 2, -3)
    assert(ir.eval(s) == StmLiteral.ints(2, -1, -4))
  }

  test("StmCst2D") {
    val s = StmCst2D(3, 4, 42)
    val expected = StmLiteral(
      StmLiteral.ints(42, 42, 42, 42),
      StmLiteral.ints(42, 42, 42, 42),
      StmLiteral.ints(42, 42, 42, 42)
    )()
    assert(ir.eval(s) == expected.flatten)
  }

  test("StmCount2D") {
    val s = StmCount2D(2, 3)
    val expected = StmLiteral(
      StmLiteral(
        Tuple(0, 0)(),
        Tuple(0, 1)(),
        Tuple(0, 2)()
      )(),
      StmLiteral(
        Tuple(1, 0)(),
        Tuple(1, 1)(),
        Tuple(1, 2)()
      )()
    )()
    assert(ir.eval(s) == expected.flatten)
  }

  test("Iterate:Square") {
    val e = (n: Int) => Iterate(n, 3, (x: Expr) => x * x, zSize = None)
    assert(ir.eval(e(0)) == IntCst(3))
    assert(ir.eval(e(1)) == IntCst(9))
    assert(ir.eval(e(2)) == IntCst(81))
    assert(ir.eval(e(3)) == IntCst(81 * 81))
  }

  test("Iterate:PlusTwo") {
    val e =
      (n: Int) =>
        Iterate(
          n,
          Tuple(-10)(),
          (x: Expr) => Tuple(x.__0 + 2)(),
          zSize = Some(1)
        ).__0
    assert(ir.eval(e(0)) == IntCst(-10))
    assert(ir.eval(e(1)) == IntCst(-8))
    assert(ir.eval(e(2)) == IntCst(-6))
    assert(ir.eval(e(3)) == IntCst(-4))
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

  test("StmMap:1D-1D:SingleElementStream") {
    val s = StmMap(
      StmCount(1),
      (x: Expr) => x + 5,
      n = 1,
      fInShape = None,
      fOutShape = None
    )
    assertStreamEqual(s, Seq(5))
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
      (i: Expr) => {
        val acc = Param("acc")
        StmBuild(
          3,
          SSome(Tuple(i, acc)()),
          Map[Param, (Expr, Expr)](
            acc -> (i, acc + 3)
          )
        )()
      },
      n = 3,
      fInShape = None,
      fOutShape = Some(3)
    )
    val expected = Seq(
      Seq(Tuple(0, 0)(), Tuple(0, 3)(), Tuple(0, 6)()),
      Seq(Tuple(1, 1)(), Tuple(1, 4)(), Tuple(1, 7)()),
      Seq(Tuple(2, 2)(), Tuple(2, 5)(), Tuple(2, 8)())
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

  test("StmMap:1D-2D:SingleElementStream") {
    val s = StmMap(
      StmCst(1, 99),
      (c: Expr) => StmCountFrom(c, 5),
      n = 1,
      fInShape = None,
      fOutShape = Some(5)
    )
    assertStreamEqual(s, Seq(99, 100, 101, 102, 103))
  }

  test("StmMap:2D-1D:StmFold") {
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
      fOutShape = Some(1)
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
      fOutShape = Some(1)
    )
    val expected =
      Seq(Tuple(0, 1)(), Tuple(1, 1)(), Tuple(2, 1)(), Tuple(3, 1)())
    assertStreamEqual(s, expected)
  }

  test("StmMap:2D-1D:Stm2Vec") {
    val s = StmMap(
      StmCount2D(5, 3),
      (s: Expr) => Stm2Vec(s, n = 3),
      n = 5,
      fInShape = Some(3),
      fOutShape = Some(1)
    )
    val expected = StmLiteral(
      VecLiteral(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)())(),
      VecLiteral(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())(),
      VecLiteral(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)())(),
      VecLiteral(Tuple(3, 0)(), Tuple(3, 1)(), Tuple(3, 2)())(),
      VecLiteral(Tuple(4, 0)(), Tuple(4, 1)(), Tuple(4, 2)())()
    )()

    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-1D:DiscardInputReturn42") {
    val s = StmMap(
      StmCount2D(3, 4),
      (_: Expr) => StmCst(1, IntCst(42)),
      n = 3,
      fInShape = Some(4),
      fOutShape = Some(1)
    )
    val expected = Seq(42, 42, 42).map(n => IntCst(n))
    assertStreamEqual(s, expected)
  }

  test("StmMap:2D-1D:SingleElementStream") {
    val s = StmMap(
      StmCount2D(1, 4),
      (s: Expr) =>
        StmFold(
          s,
          10,
          (acc: Expr) => (x: Expr) => acc + x.__1,
          stmShape = Seq(4)
        ),
      n = 1,
      fInShape = Some(4),
      fOutShape = Some(1)
    )
    assertStreamEqual(s, Seq(10 + 0 + 1 + 2 + 3))
  }

  test("StmMap:2D-2D:StmMap") {
    val p = Param("p")
    val s = StmMap(
      p,
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
    assertStreamEqual(Let(p, StmCount2D(4, 3), s), expected.flatten)
  }

  test("StmMap:2D-2D:StmScanInclusive") {
    // [[ 0,  1,  2,  3],
    //  [10, 11, 12, 13],
    //  [20, 21, 22, 23]]
    val s = StmMap(
      StmCount(3),
      (i: Expr) =>
        StmMap(
          StmCount(4),
          (j: Expr) => 10 * i + j,
          n = 4,
          fInShape = None,
          fOutShape = None
        ),
      n = 3,
      fInShape = None,
      fOutShape = Some(4)
    )
    val actual = StmMap(
      s,
      (row: Expr) =>
        StmScanInclusive(
          row,
          0,
          (a: Expr) => (x: Expr) => a + x,
          stmShape = Seq(4)
        ),
      n = 3,
      fInShape = Some(4),
      fOutShape = Some(4)
    )
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 1, 3, 6),
        Seq(10, 21, 33, 46),
        Seq(20, 41, 63, 86)
      ).flatten: _*
    )
    assert(ir.eval(actual) == expected)
  }

  test("StmMap:2D-2D:StmScanExclusive") {
    // [[ 0,  1,  2,  3],
    //  [10, 11, 12, 13],
    //  [20, 21, 22, 23]]
    val s = StmMap(
      StmCount(3),
      (i: Expr) =>
        StmMap(
          StmCount(4),
          (j: Expr) => 10 * i + j,
          n = 4,
          fInShape = None,
          fOutShape = None
        ),
      n = 3,
      fInShape = None,
      fOutShape = Some(4)
    )
    val actual = StmMap(
      s,
      (row: Expr) =>
        StmScanExclusive(
          row,
          0,
          (a: Expr) => (x: Expr) => a + x,
          stmShape = Seq(4)
        ),
      n = 3,
      fInShape = Some(4),
      fOutShape = Some(4)
    )
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 0, 1, 3),
        Seq(0, 10, 21, 33),
        Seq(0, 20, 41, 63)
      ).flatten: _*
    )
    assert(ir.eval(actual) == expected)
  }

  test("StmMap:2D-2D:StmPrepend") {
    val s = StmMap(
      StmCst2D(4, 5, 42),
      (s: Expr) => StmPrepend(s, 43, stmShape = Seq(5)),
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
    val s =
      StmMap(
        StmCount2D(3, 100),
        (s: Expr) => StmPrefix(s, 2, shape = Seq(100)),
        n = 3,
        fInShape = Some(100),
        fOutShape = Some(2)
      )
    val expected = StmLiteral(
      StmLiteral(Tuple(0, 0)(), Tuple(0, 1)())(),
      StmLiteral(Tuple(1, 0)(), Tuple(1, 1)())(),
      StmLiteral(Tuple(2, 0)(), Tuple(2, 1)())()
    )()
    assert(ir.eval(s) == expected.flatten)
  }

  test("StmMap:2D-2D:StmSuffix") {
    val s =
      StmMap(
        StmCount2D(3, 100),
        (s: Expr) => StmSuffix(s, 2, shape = Seq(100)),
        n = 3,
        fInShape = Some(100),
        fOutShape = Some(2)
      )
    val expected = StmLiteral(
      Tuple(0, 98)(),
      Tuple(0, 99)(),
      Tuple(1, 98)(),
      Tuple(1, 99)(),
      Tuple(2, 98)(),
      Tuple(2, 99)()
    )()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmShiftLeft") {
    val s = StmMap(
      StmCount2D(4, 5),
      (s: Expr) => StmShiftLeft(s, Tuple(100, 101)(), stmShape = Seq(5)),
      n = 4,
      fInShape = Some(5),
      fOutShape = Some(5)
    )
    val expected = Seq(
      Seq(
        Tuple(0, 1)(),
        Tuple(0, 2)(),
        Tuple(0, 3)(),
        Tuple(0, 4)(),
        Tuple(100, 101)()
      ),
      Seq(
        Tuple(1, 1)(),
        Tuple(1, 2)(),
        Tuple(1, 3)(),
        Tuple(1, 4)(),
        Tuple(100, 101)()
      ),
      Seq(
        Tuple(2, 1)(),
        Tuple(2, 2)(),
        Tuple(2, 3)(),
        Tuple(2, 4)(),
        Tuple(100, 101)()
      ),
      Seq(
        Tuple(3, 1)(),
        Tuple(3, 2)(),
        Tuple(3, 3)(),
        Tuple(3, 4)(),
        Tuple(100, 101)()
      )
    )
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:2D-2D:StmShiftRight") {
    val s = StmMap(
      StmCount2D(4, 5),
      (s: Expr) => StmShiftRight(s, Tuple(100, 101)(), stmShape = Seq(5)),
      n = 4,
      fInShape = Some(5),
      fOutShape = Some(5)
    )
    val expected = Seq(
      Seq(
        Tuple(100, 101)(),
        Tuple(0, 0)(),
        Tuple(0, 1)(),
        Tuple(0, 2)(),
        Tuple(0, 3)()
      ),
      Seq(
        Tuple(100, 101)(),
        Tuple(1, 0)(),
        Tuple(1, 1)(),
        Tuple(1, 2)(),
        Tuple(1, 3)()
      ),
      Seq(
        Tuple(100, 101)(),
        Tuple(2, 0)(),
        Tuple(2, 1)(),
        Tuple(2, 2)(),
        Tuple(2, 3)()
      ),
      Seq(
        Tuple(100, 101)(),
        Tuple(3, 0)(),
        Tuple(3, 1)(),
        Tuple(3, 2)(),
        Tuple(3, 3)()
      )
    )
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:2D-2D:StmConcatBefore") {
    val s = StmMap(
      StmCst2D(5, 5, 99),
      (s: Expr) =>
        StmConcat(StmCount(3), s, stm1Shape = Seq(3), stm2Shape = Seq(5)),
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
      (s: Expr) =>
        StmConcat(s, StmCount(3), stm1Shape = Seq(5), stm2Shape = Seq(3)),
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
      Seq(
        Tuple(0, Tuple(0, 0)())(),
        Tuple(1, Tuple(0, 1)())(),
        Tuple(2, Tuple(0, 2)())()
      ),
      Seq(
        Tuple(0, Tuple(1, 0)())(),
        Tuple(1, Tuple(1, 1)())(),
        Tuple(2, Tuple(1, 2)())()
      ),
      Seq(
        Tuple(0, Tuple(2, 0)())(),
        Tuple(1, Tuple(2, 1)())(),
        Tuple(2, Tuple(2, 2)())()
      )
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
      Seq(
        Tuple(Tuple(0, 0)(), 0)(),
        Tuple(Tuple(0, 1)(), 1)(),
        Tuple(Tuple(0, 2)(), 2)()
      ),
      Seq(
        Tuple(Tuple(1, 0)(), 0)(),
        Tuple(Tuple(1, 1)(), 1)(),
        Tuple(Tuple(1, 2)(), 2)()
      ),
      Seq(
        Tuple(Tuple(2, 0)(), 0)(),
        Tuple(Tuple(2, 1)(), 1)(),
        Tuple(Tuple(2, 2)(), 2)()
      )
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
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)(), Tuple(0, 3)()),
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)(), Tuple(0, 3)()),
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)(), Tuple(0, 3)())
      ),
      Seq(
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)())
      ),
      Seq(
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)(), Tuple(2, 3)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)(), Tuple(2, 3)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)(), Tuple(2, 3)())
      ),
      Seq(
        Seq(Tuple(3, 0)(), Tuple(3, 1)(), Tuple(3, 2)(), Tuple(3, 3)()),
        Seq(Tuple(3, 0)(), Tuple(3, 1)(), Tuple(3, 2)(), Tuple(3, 3)()),
        Seq(Tuple(3, 0)(), Tuple(3, 1)(), Tuple(3, 2)(), Tuple(3, 3)())
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
      Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
      Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())
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
      fInShape = Some(3),
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

  test("StmMap:2D-2D:StmSlideV") {
    val s = StmCount2D(3, 5)
    val expected = StmLiteral(
      Seq(
        Seq(
          VecLiteral(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)())(),
          VecLiteral(Tuple(0, 1)(), Tuple(0, 2)(), Tuple(0, 3)())(),
          VecLiteral(Tuple(0, 2)(), Tuple(0, 3)(), Tuple(0, 4)())()
        ),
        Seq(
          VecLiteral(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())(),
          VecLiteral(Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)())(),
          VecLiteral(Tuple(1, 2)(), Tuple(1, 3)(), Tuple(1, 4)())()
        ),
        Seq(
          VecLiteral(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)())(),
          VecLiteral(Tuple(2, 1)(), Tuple(2, 2)(), Tuple(2, 3)())(),
          VecLiteral(Tuple(2, 2)(), Tuple(2, 3)(), Tuple(2, 4)())()
        )
      ).flatten: _*
    )()
    val actual = StmMap(
      s,
      (s: Expr) => StmSlideV(s, 3, stmShape = Seq(5)),
      n = 3,
      fInShape = Some(5),
      fOutShape = Some(3)
    )
    assert(ir.eval(actual) == expected)
  }

  test("StmMap:2D-2D:SingleElementOuterStream") {
    val s = StmMap(
      StmCount2D(1, 3),
      (s: Expr) =>
        StmMap(
          s,
          (x: Expr) => x.__0 + x.__1,
          n = 3,
          fInShape = None,
          fOutShape = None
        ),
      n = 1,
      fInShape = Some(3),
      fOutShape = Some(3)
    )
    val expected = Seq(
      Seq(0, 1, 2)
    ).map(xs => xs.map(x => IntCst(x)))
    assertStreamEqual(s, expected.flatten)
  }

  test("StmMap:2D-2D:SingleElementInnerStream") {
    val s = StmMap(
      StmCount2D(4, 1),
      (s: Expr) =>
        StmMap(
          s,
          (x: Expr) => x.__0 + x.__1,
          n = 1,
          fInShape = None,
          fOutShape = None
        ),
      n = 4,
      fInShape = Some(1),
      fOutShape = Some(1)
    )
    val expected = Seq(
      Seq(0),
      Seq(1),
      Seq(2),
      Seq(3)
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
              (k: Expr) => Tuple(i, j, k)(),
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
        Seq(
          Tuple(0, 0, 0)(),
          Tuple(0, 0, 1)(),
          Tuple(0, 0, 2)(),
          Tuple(0, 0, 3)()
        ),
        Seq(
          Tuple(0, 1, 0)(),
          Tuple(0, 1, 1)(),
          Tuple(0, 1, 2)(),
          Tuple(0, 1, 3)()
        ),
        Seq(
          Tuple(0, 2, 0)(),
          Tuple(0, 2, 1)(),
          Tuple(0, 2, 2)(),
          Tuple(0, 2, 3)()
        )
      ),
      Seq(
        Seq(
          Tuple(1, 0, 0)(),
          Tuple(1, 0, 1)(),
          Tuple(1, 0, 2)(),
          Tuple(1, 0, 3)()
        ),
        Seq(
          Tuple(1, 1, 0)(),
          Tuple(1, 1, 1)(),
          Tuple(1, 1, 2)(),
          Tuple(1, 1, 3)()
        ),
        Seq(
          Tuple(1, 2, 0)(),
          Tuple(1, 2, 1)(),
          Tuple(1, 2, 2)(),
          Tuple(1, 2, 3)()
        )
      )
    )
    assertStreamEqual(s, expected.flatten.flatten)
  }

  test("StmMap:2D-3D:StmSlideS") {
    val s = StmCount2D(3, 5)
    val expected = Seq(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
        Seq(Tuple(0, 1)(), Tuple(0, 2)(), Tuple(0, 3)()),
        Seq(Tuple(0, 2)(), Tuple(0, 3)(), Tuple(0, 4)())
      ),
      Seq(
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)()),
        Seq(Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)()),
        Seq(Tuple(1, 2)(), Tuple(1, 3)(), Tuple(1, 4)())
      ),
      Seq(
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)()),
        Seq(Tuple(2, 1)(), Tuple(2, 2)(), Tuple(2, 3)()),
        Seq(Tuple(2, 2)(), Tuple(2, 3)(), Tuple(2, 4)())
      )
    )
    val actual = StmMap(
      s,
      (s: Expr) => StmSlideS(s, 3, stmShape = Seq(5)),
      n = 3,
      fInShape = Some(5),
      fOutShape = Some(9)
    )
    assertStreamEqual(actual, expected.flatten.flatten)
  }

  test("StmMap:3D-3D:StmTranspose") {
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
    val expected = StmLiteral(
      Seq(
        Seq(
          Seq(Tuple(0, 0)(), Tuple(1, 0)(), Tuple(2, 0)()),
          Seq(Tuple(0, 1)(), Tuple(1, 1)(), Tuple(2, 1)()),
          Seq(Tuple(0, 2)(), Tuple(1, 2)(), Tuple(2, 2)()),
          Seq(Tuple(0, 3)(), Tuple(1, 3)(), Tuple(2, 3)())
        ),
        Seq(
          Seq(Tuple(0, 0)(), Tuple(1, 0)(), Tuple(2, 0)()),
          Seq(Tuple(0, 1)(), Tuple(1, 1)(), Tuple(2, 1)()),
          Seq(Tuple(0, 2)(), Tuple(1, 2)(), Tuple(2, 2)()),
          Seq(Tuple(0, 3)(), Tuple(1, 3)(), Tuple(2, 3)())
        )
      ).flatten.flatten: _*
    )()
    val actual = StmMap(
      s,
      (s: Expr) => StmTranspose(s, n = 3, m = 4),
      n = 2,
      fInShape = Some(12),
      fOutShape = Some(12)
    )
    assert(ir.eval(actual) == expected)
  }

  test("StmAccess:1D") {
    val n = 3
    val s = StmRange(n, 5, 2)
    assert(ir.eval(StmAccess(s, 0, shape = Seq(n))) == StmLiteral(5)())
    assert(ir.eval(StmAccess(s, 1, shape = Seq(n))) == StmLiteral(7)())
    assert(ir.eval(StmAccess(s, 2, shape = Seq(n))) == StmLiteral(9)())
  }

  test("StmAccess:2D") {
    val s = StmCount2D(4, 5)

    val row = (i: Int) => StmLiteral((0 until 5).map(j => Tuple(i, j)()): _*)()

    assert(ir.eval(StmAccess(s, 0, shape = Seq(4, 5))) == row(0))
    assert(ir.eval(StmAccess(s, 1, shape = Seq(4, 5))) == row(1))
    assert(ir.eval(StmAccess(s, 2, shape = Seq(4, 5))) == row(2))
    assert(ir.eval(StmAccess(s, 3, shape = Seq(4, 5))) == row(3))
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
              (k: Expr) => Tuple(i, j, k)(),
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
        Seq(
          Tuple(0, 0, 0)(),
          Tuple(0, 0, 1)(),
          Tuple(0, 0, 2)(),
          Tuple(0, 0, 3)()
        ),
        Seq(
          Tuple(0, 1, 0)(),
          Tuple(0, 1, 1)(),
          Tuple(0, 1, 2)(),
          Tuple(0, 1, 3)()
        ),
        Seq(
          Tuple(0, 2, 0)(),
          Tuple(0, 2, 1)(),
          Tuple(0, 2, 2)(),
          Tuple(0, 2, 3)()
        )
      ),
      Seq(
        Seq(
          Tuple(1, 0, 0)(),
          Tuple(1, 0, 1)(),
          Tuple(1, 0, 2)(),
          Tuple(1, 0, 3)()
        ),
        Seq(
          Tuple(1, 1, 0)(),
          Tuple(1, 1, 1)(),
          Tuple(1, 1, 2)(),
          Tuple(1, 1, 3)()
        ),
        Seq(
          Tuple(1, 2, 0)(),
          Tuple(1, 2, 1)(),
          Tuple(1, 2, 2)(),
          Tuple(1, 2, 3)()
        )
      ),
      Seq(
        Seq(
          Tuple(2, 0, 0)(),
          Tuple(2, 0, 1)(),
          Tuple(2, 0, 2)(),
          Tuple(2, 0, 3)()
        ),
        Seq(
          Tuple(2, 1, 0)(),
          Tuple(2, 1, 1)(),
          Tuple(2, 1, 2)(),
          Tuple(2, 1, 3)()
        ),
        Seq(
          Tuple(2, 2, 0)(),
          Tuple(2, 2, 1)(),
          Tuple(2, 2, 2)(),
          Tuple(2, 2, 3)()
        )
      )
    )
    assert(
      ir.eval(StmAccess(s, 0, shape = Seq(3, 3, 4)))
        == StmLiteral(expected.head.flatten: _*)()
    )
    assert(
      ir.eval(StmAccess(s, 1, shape = Seq(3, 3, 4)))
        == StmLiteral(expected(1).flatten: _*)()
    )
    assert(
      ir.eval(StmAccess(s, 2, shape = Seq(3, 3, 4)))
        == StmLiteral(expected(2).flatten: _*)()
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
    assertStreamEqual(sum, Seq(IntCst(18)))
  }

  test("StmFold:1D:Product") {
    val prod =
      StmFold(
        StmCountFrom(1, 5),
        1,
        (acc: Expr) => (x: Expr) => acc * x,
        stmShape = Seq(5)
      )
    assertStreamEqual(prod, Seq(IntCst(120)))
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
    assertStreamEqual(y, Seq(IntCst(88)))
  }

  test("StmFold:1D:DiscardInputAdd42") {
    val x = StmFold(
      StmCount(5),
      2,
      (acc: Expr) => (_: Expr) => acc + 42,
      stmShape = Seq(5)
    )
    assertStreamEqual(x, Seq(IntCst(2 + 5 * 42)))
  }

  test("StmFold:2D:SumWide") {
    // [[0, 1, 2, 3, 4],
    //  [1, 2, 3, 4, 5],
    //  [2, 3, 4, 5, 6]]
    val s = StmMap(
      StmCount(3),
      (i: Expr) =>
        StmMap(
          StmCount(5),
          (j: Expr) => i + j,
          n = 5,
          fInShape = None,
          fOutShape = None
        ),
      n = 3,
      fInShape = None,
      fOutShape = Some(5)
    )
    val sum = StmFold(
      s,
      0,
      (acc: Expr) =>
        (s: Expr) =>
          StmMap(
            StmFold(
              s,
              0,
              (a: Expr) => (x: Expr) => a + x,
              stmShape = Seq(5)
            ),
            (x: Expr) => acc + x,
            n = 1,
            fInShape = None,
            fOutShape = None
          ),
      stmShape = Seq(3, 5)
    )
    assertStreamEqual(sum, Seq(IntCst(45)))
  }

  test("StmFold:2D:SumNarrow") {
    // [[0, 1],
    //  [1, 2],
    //  [2, 3],
    //  [3, 4]]
    val s = StmMap(
      StmCount(4),
      (i: Expr) =>
        StmMap(
          StmCount(2),
          (j: Expr) => i + j,
          n = 2,
          fInShape = None,
          fOutShape = None
        ),
      n = 4,
      fInShape = None,
      fOutShape = Some(2)
    )
    val sum = StmFold(
      s,
      0,
      (acc: Expr) =>
        (s: Expr) =>
          StmMap(
            StmFold(
              s,
              0,
              (a: Expr) => (x: Expr) => a + x,
              stmShape = Seq(2)
            ),
            (x: Expr) => acc + x,
            n = 1,
            fInShape = None,
            fOutShape = None
          ),
      stmShape = Seq(4, 2)
    )
    assertStreamEqual(sum, Seq(IntCst(16)))
  }

  test("StmFold:2D:SumColumn") {
    // [[1, 2, 3, 4],
    //  [2, 3, 4, 5],
    //  [3, 4, 5, 6],
    //  [4, 5, 6, 7]]
    val s = StmMap(
      StmCount(4),
      (i: Expr) =>
        StmMap(
          StmCount(4),
          (j: Expr) => i + j + 1,
          n = 4,
          fInShape = None,
          fOutShape = None
        ),
      n = 4,
      fInShape = None,
      fOutShape = Some(4)
    )
    val x = StmFold(
      s,
      13,
      (acc: Expr) =>
        (s: Expr) =>
          StmMap(
            StmAccess(s, 1, shape = Seq(4)),
            (x: Expr) => acc + x,
            n = 1,
            fInShape = None,
            fOutShape = None
          ),
      stmShape = Seq(4, 4)
    )
    assertStreamEqual(x, Seq(IntCst(13 + 2 + 3 + 4 + 5)))
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
          StmMap(
            StmFold(
              s,
              1,
              (acc: Expr) => (x: Expr) => acc * x,
              stmShape = Seq(3)
            ),
            (x: Expr) => acc * x,
            n = 1,
            fInShape = None,
            fOutShape = None
          ),
      stmShape = Seq(3, 3)
    )
    assertStreamEqual(prod, Seq(IntCst(8640)))
  }

  test("StmFold:2D:DiscardInputAdd42") {
    // [[0, 1, 2, 3, 4],
    //  [1, 2, 3, 4, 5],
    //  [2, 3, 4, 5, 6]]
    val s = StmMap(
      StmCount(3),
      (i: Expr) =>
        StmMap(
          StmCount(5),
          (j: Expr) => i + j,
          n = 5,
          fInShape = None,
          fOutShape = None
        ),
      n = 3,
      fInShape = None,
      fOutShape = Some(5)
    )
    // TODO: What about a weird case where it starts by ignoring input but
    //       then starts using the input after a certain number of rows?
    //       This would require using a tuple accumulator, which is not
    //       currently supported in the interpreter (although it shouldn't be
    //       terribly difficult to implement).
    val sum = StmFold(
      s,
      0,
      (acc: Expr) => (s: Expr) => StmCst(1, acc + 42),
      stmShape = Seq(3, 5)
    )
    assertStreamEqual(sum, Seq(IntCst(3 * 42)))
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
          StmMap(
            StmFold(
              s,
              0,
              (acc: Expr) =>
                (s: Expr) =>
                  StmMap(
                    StmFold(
                      s,
                      0,
                      (a: Expr) => (x: Expr) => a + x,
                      stmShape = Seq(4)
                    ),
                    (x: Expr) => acc + x,
                    n = 1,
                    fInShape = None,
                    fOutShape = None
                  ),
              stmShape = Seq(2, 4)
            ),
            (x: Expr) => acc + x,
            n = 1,
            fInShape = None,
            fOutShape = None
          ),
      stmShape = Seq(3, 2, 4)
    )
    assert(ir.eval(sum) == StmLiteral.ints(72))
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
          StmMap(
            StmFold(
              s,
              1,
              (acc: Expr) =>
                (s: Expr) =>
                  StmMap(
                    StmFold(
                      s,
                      1,
                      (a: Expr) => (x: Expr) => a * x,
                      stmShape = Seq(2)
                    ),
                    (x: Expr) => acc * x,
                    n = 1,
                    fInShape = None,
                    fOutShape = None
                  ),
              stmShape = Seq(2, 2)
            ),
            (x: Expr) => acc * x,
            n = 1,
            fInShape = None,
            fOutShape = None
          ),
      stmShape = Seq(3, 2, 2)
    )
    assert(ir.eval(prod) == StmLiteral(IntCst(207360))())
  }

  test("StmScanInclusive:1D:Sum") {
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
      StmScanInclusive(
        s,
        0,
        (acc: Expr) => (x: Expr) => x + 2 * acc,
        stmShape = Seq(5)
      )
    assertStreamEqual(sum, Seq(2, 7, 18, 41, 88))
  }

  test("StmScanInclusive:2D:SumRowSums") {
    // [[0, 1],
    //  [1, 2],
    //  [2, 3],
    //  [3, 4]]
    val s = StmMap(
      StmCount(4),
      (i: Expr) =>
        StmMap(
          StmCount(2),
          (j: Expr) => i + j,
          n = 2,
          fInShape = None,
          fOutShape = None
        ),
      n = 4,
      fInShape = None,
      fOutShape = Some(2)
    )
    val sums = StmScanInclusive(
      s,
      0,
      (acc: Expr) =>
        (s: Expr) =>
          StmMap(
            StmFold(
              s,
              0,
              (a: Expr) => (x: Expr) => a + x,
              stmShape = Seq(2)
            ),
            (x: Expr) => acc + x,
            n = 1,
            fInShape = None,
            fOutShape = None
          ),
      stmShape = Seq(4, 2)
    )
    // scan([1, 3, 5, 7])
    val expected = Seq(1, 4, 9, 16).map(x => IntCst(x))
    assertStreamEqual(sums, expected)
  }

  test("StmScanInclusive:2D:SumColumn1") {
    // [[1, 2, 3, 4],
    //  [2, 3, 4, 5],
    //  [3, 4, 5, 6],
    //  [4, 5, 6, 7]]
    val s = StmMap(
      StmCount(4),
      (i: Expr) =>
        StmMap(
          StmCount(4),
          (j: Expr) => i + j + 1,
          n = 4,
          fInShape = None,
          fOutShape = None
        ),
      n = 4,
      fInShape = None,
      fOutShape = Some(4)
    )
    val sums = StmScanInclusive(
      s,
      0,
      (acc: Expr) =>
        (s: Expr) =>
          StmMap(
            StmAccess(s, 1, shape = Seq(4)),
            (x: Expr) => acc + x,
            n = 1,
            fInShape = None,
            fOutShape = None
          ),
      stmShape = Seq(4, 4)
    )
    // scan([2, 3, 4, 5])
    val expected = Seq(2, 5, 9, 14).map(x => IntCst(x))
    assertStreamEqual(sums, expected)
  }

  test("StmScanExclusive:1D:Sum") {
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
      StmScanExclusive(
        s,
        0,
        (acc: Expr) => (x: Expr) => x + 2 * acc,
        stmShape = Seq(5)
      )
    assertStreamEqual(sum, Seq(0, 2, 7, 18, 41))
  }

  test("StmScanExclusive:2D:SumRowSums") {
    // [[0, 1],
    //  [1, 2],
    //  [2, 3],
    //  [3, 4]]
    val s = StmMap(
      StmCount(4),
      (i: Expr) =>
        StmMap(
          StmCount(2),
          (j: Expr) => i + j,
          n = 2,
          fInShape = None,
          fOutShape = None
        ),
      n = 4,
      fInShape = None,
      fOutShape = Some(2)
    )
    val sums = StmScanExclusive(
      s,
      0,
      (acc: Expr) =>
        (s: Expr) =>
          StmMap(
            StmFold(
              s,
              0,
              (a: Expr) => (x: Expr) => a + x,
              stmShape = Seq(2)
            ),
            (x: Expr) => acc + x,
            n = 1,
            fInShape = None,
            fOutShape = None
          ),
      stmShape = Seq(4, 2)
    )
    // scan([1, 3, 5, 7])
    val expected = Seq(0, 1, 4, 9).map(x => IntCst(x))
    assertStreamEqual(sums, expected)
  }

  test("StmScanExclusive:2D:SumColumn1") {
    // [[1, 2, 3, 4],
    //  [2, 3, 4, 5],
    //  [3, 4, 5, 6],
    //  [4, 5, 6, 7]]
    val s = StmMap(
      StmCount(4),
      (i: Expr) =>
        StmMap(
          StmCount(4),
          (j: Expr) => i + j + 1,
          n = 4,
          fInShape = None,
          fOutShape = None
        ),
      n = 4,
      fInShape = None,
      fOutShape = Some(4)
    )
    val sums = StmScanExclusive(
      s,
      0,
      (acc: Expr) =>
        (s: Expr) =>
          StmMap(
            StmAccess(s, 1, shape = Seq(4)),
            (x: Expr) => acc + x,
            n = 1,
            fInShape = None,
            fOutShape = None
          ),
      stmShape = Seq(4, 4)
    )
    // scan([2, 3, 4, 5])
    val expected = Seq(0, 2, 5, 9).map(x => IntCst(x))
    assertStreamEqual(sums, expected)
  }

  test("Vec2Stm:1D") {
    val v = VecBuild(5, (i: Expr) => i + 1)()
    assert(ir.eval(Vec2Stm(v, n = 5)) == StmLiteral.ints(1, 2, 3, 4, 5))
  }

  test("Vec2Stm:2D") {
    val v =
      VecBuild(4, (i: Expr) => VecBuild(3, (j: Expr) => Tuple(i, j)())())()
    val s = StmMap(
      Vec2Stm(v, n = 4),
      (v: Expr) => Vec2Stm(v, n = 3),
      n = 4,
      fInShape = None,
      fOutShape = Some(3)
    )

    val expected = StmLiteral(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)()),
        Seq(Tuple(3, 0)(), Tuple(3, 1)(), Tuple(3, 2)())
      ).flatten: _*
    )()
    assert(ir.eval(s) == expected)
  }

  test("Vec2Stm2Vec") {
    val p = Param("p")
    val actual = Stm2Vec(Vec2Stm(p, n = 6), n = 6)

    // Correctness
    val v0 = VecBuild(6, (i: Expr) => i + 2)()
    val expected0 = StmLiteral(VecLiteral(2, 3, 4, 5, 6, 7)())()
    assert(ir.eval(Let(p, v0, actual)) == expected0)
    val v1 = VecBuild(6, (i: Expr) => i * i)()
    val expected1 = StmLiteral(VecLiteral(0, 1, 4, 9, 16, 25)())()
    assert(ir.eval(Let(p, v1, actual)) == expected1)
  }

  test("Stm2Vec2Stm") {
    val p = Param("p")
    val actual = StmMap(
      Stm2Vec(p, n = 6),
      (v: Expr) => Vec2Stm(v, n = 6),
      n = 1,
      fInShape = None,
      fOutShape = Some(6)
    )

    // Correctness
    val s0 = StmCount(6)
    val expected0 = Seq(0, 1, 2, 3, 4, 5).map(n => IntCst(n))
    assertStreamEqual(Let(p, s0, actual), expected0)
    val s1 = StmCst(6, 42)
    val expected1 = Seq(42, 42, 42, 42, 42, 42).map(n => IntCst(n))
    assertStreamEqual(Let(p, s1, actual), expected1)

    // Effective simplification
    // TODO: Check that Sm2Vec and Vec2Stm cancel out
  }

  test("StmPrepend:1D") {
    val s = StmCount(3)
    val actual = StmPrepend(s, 42, stmShape = Seq(3))
    assert(ir.eval(actual) == StmLiteral.ints(42, 0, 1, 2))
  }

  test("StmPrepend:2D") {
    val s0 = StmCount2D(3, 3)
    val s1 = StmCst(3, Tuple(42, 42)())

    val actual = StmPrepend(s0, s1, stmShape = Seq(3, 3))
    val expected = StmLiteral(
      Seq(
        Seq(Tuple(42, 42)(), Tuple(42, 42)(), Tuple(42, 42)()),
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)())
      ).flatten: _*
    )()
    assert(ir.eval(actual) == expected)
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
    assert(
      ir.eval(StmPrepend(s0, s1, stmShape = Seq(2, 2, 2)))
        == StmLiteral(expected.flatten.flatten: _*)()
    )
  }

  test("StmAppend:1D") {
    val s = StmCount(3)
    assert(
      ir.eval(StmAppend(s, 42, stmShape = Seq(3)))
        == StmLiteral.ints(0, 1, 2, 42)
    )
  }

  test("StmAppend:2D") {
    val s0 = StmCount2D(3, 3)
    val s1 = StmCst(3, Tuple(42, 42)())

    val expected = StmLiteral(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)()),
        Seq(Tuple(42, 42)(), Tuple(42, 42)(), Tuple(42, 42)())
      ).flatten: _*
    )()
    assert(ir.eval(StmAppend(s0, s1, stmShape = Seq(3, 3))) == expected)
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
    assert(ir.eval(StmPrefix(s, 0, shape = Seq(3))) == StmLiteral()())
    assert(ir.eval(StmPrefix(s, 1, shape = Seq(3))) == StmLiteral(0)())
    assert(ir.eval(StmPrefix(s, 2, shape = Seq(3))) == StmLiteral(0, 1)())
    assert(ir.eval(StmPrefix(s, 3, shape = Seq(3))) == StmLiteral(0, 1, 2)())
  }

  test("StmPrefix:2D") {
    val s = StmCount2D(2, 3)

    val expected = Seq(
      Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
      Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())
    )
    assert(ir.eval(StmPrefix(s, 0, shape = Seq(2, 3))) == StmLiteral()())
    assert(
      ir.eval(StmPrefix(s, 1, shape = Seq(2, 3)))
        == StmLiteral(expected.slice(0, 1).flatten: _*)()
    )
    assert(
      ir.eval(StmPrefix(s, 2, shape = Seq(2, 3)))
        == StmLiteral(expected.slice(0, 2).flatten: _*)()
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
        Seq(
          Tuple(0, 0)(),
          Tuple(0, 1)(),
          Tuple(0, 2)(),
          Tuple(0, 3)(),
          Tuple(0, 4)()
        ),
        Seq(
          Tuple(1, 0)(),
          Tuple(1, 1)(),
          Tuple(1, 2)(),
          Tuple(1, 3)(),
          Tuple(1, 4)()
        ),
        Seq(
          Tuple(2, 0)(),
          Tuple(2, 1)(),
          Tuple(2, 2)(),
          Tuple(2, 3)(),
          Tuple(2, 4)()
        ),
        Seq(
          Tuple(3, 0)(),
          Tuple(3, 1)(),
          Tuple(3, 2)(),
          Tuple(3, 3)(),
          Tuple(3, 4)()
        )
      ),
      Seq(
        Seq(
          Tuple(0, 0)(),
          Tuple(0, 1)(),
          Tuple(0, 2)(),
          Tuple(0, 3)(),
          Tuple(0, 4)()
        ),
        Seq(
          Tuple(1, 0)(),
          Tuple(1, 1)(),
          Tuple(1, 2)(),
          Tuple(1, 3)(),
          Tuple(1, 4)()
        ),
        Seq(
          Tuple(2, 0)(),
          Tuple(2, 1)(),
          Tuple(2, 2)(),
          Tuple(2, 3)(),
          Tuple(2, 4)()
        ),
        Seq(
          Tuple(3, 0)(),
          Tuple(3, 1)(),
          Tuple(3, 2)(),
          Tuple(3, 3)(),
          Tuple(3, 4)()
        )
      )
    )
    assert(ir.eval(StmPrefix(s, 0, shape = Seq(2, 4, 5))) == StmLiteral()())
    assert(
      ir.eval(StmPrefix(s, 1, shape = Seq(2, 4, 5)))
        == StmLiteral(expected.slice(0, 1).flatten.flatten: _*)()
    )
    assert(
      ir.eval(StmPrefix(s, 2, shape = Seq(2, 4, 5)))
        == StmLiteral(expected.slice(0, 2).flatten.flatten: _*)()
    )
  }

  test("StmSuffix:1D") {
    val s = StmCount(3)
    assert(ir.eval(StmSuffix(s, 0, shape = Seq(3))) == StmLiteral()())
    assert(ir.eval(StmSuffix(s, 1, shape = Seq(3))) == StmLiteral(2)())
    assert(ir.eval(StmSuffix(s, 2, shape = Seq(3))) == StmLiteral(1, 2)())
    assert(ir.eval(StmSuffix(s, 3, shape = Seq(3))) == StmLiteral(0, 1, 2)())
  }

  test("StmSuffix:2D") {
    val s = StmCount2D(3, 2)

    val expected = Seq(
      Seq(Tuple(0, 0)(), Tuple(0, 1)()),
      Seq(Tuple(1, 0)(), Tuple(1, 1)()),
      Seq(Tuple(2, 0)(), Tuple(2, 1)())
    )
    assert(ir.eval(StmSuffix(s, 0, shape = Seq(3, 2))) == StmLiteral()())
    assert(
      ir.eval(StmSuffix(s, 1, shape = Seq(3, 2)))
        == StmLiteral(expected.slice(2, 3).flatten: _*)()
    )
    assert(
      ir.eval(StmSuffix(s, 2, shape = Seq(3, 2)))
        == StmLiteral(expected.slice(1, 3).flatten: _*)()
    )
    assert(
      ir.eval(StmSuffix(s, 3, shape = Seq(3, 2)))
        == StmLiteral(expected.slice(0, 3).flatten: _*)()
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
        Seq(
          Tuple(0, 0)(),
          Tuple(0, 1)(),
          Tuple(0, 2)(),
          Tuple(0, 3)(),
          Tuple(0, 4)()
        ),
        Seq(
          Tuple(1, 0)(),
          Tuple(1, 1)(),
          Tuple(1, 2)(),
          Tuple(1, 3)(),
          Tuple(1, 4)()
        ),
        Seq(
          Tuple(2, 0)(),
          Tuple(2, 1)(),
          Tuple(2, 2)(),
          Tuple(2, 3)(),
          Tuple(2, 4)()
        ),
        Seq(
          Tuple(3, 0)(),
          Tuple(3, 1)(),
          Tuple(3, 2)(),
          Tuple(3, 3)(),
          Tuple(3, 4)()
        )
      ),
      Seq(
        Seq(
          Tuple(0, 0)(),
          Tuple(0, 1)(),
          Tuple(0, 2)(),
          Tuple(0, 3)(),
          Tuple(0, 4)()
        ),
        Seq(
          Tuple(1, 0)(),
          Tuple(1, 1)(),
          Tuple(1, 2)(),
          Tuple(1, 3)(),
          Tuple(1, 4)()
        ),
        Seq(
          Tuple(2, 0)(),
          Tuple(2, 1)(),
          Tuple(2, 2)(),
          Tuple(2, 3)(),
          Tuple(2, 4)()
        ),
        Seq(
          Tuple(3, 0)(),
          Tuple(3, 1)(),
          Tuple(3, 2)(),
          Tuple(3, 3)(),
          Tuple(3, 4)()
        )
      )
    )
    assert(ir.eval(StmSuffix(s, 0, shape = Seq(2, 4, 5))) == StmLiteral()())
    assert(
      ir.eval(StmSuffix(s, 1, shape = Seq(2, 4, 5)))
        == StmLiteral(expected.slice(1, 2).flatten.flatten: _*)()
    )
    assert(
      ir.eval(StmSuffix(s, 2, shape = Seq(2, 4, 5)))
        == StmLiteral(expected.slice(0, 2).flatten.flatten: _*)()
    )
  }

  test("StmShiftLeft:1D") {
    val s = StmCount(3)
    val actual = StmShiftLeft(s, 42, stmShape = Seq(3))
    assert(ir.eval(actual) == StmLiteral(1, 2, 42)())
  }

  test("StmShiftLeft:2D") {
    val s0 = StmCount2D(3, 4)
    val s1 = StmMap(
      StmCount(4),
      (j: Expr) => Tuple(99, j)(),
      n = 4,
      fInShape = None,
      fOutShape = None
    )

    val expected = Seq(
      Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)()),
      Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)(), Tuple(2, 3)()),
      Seq(Tuple(99, 0)(), Tuple(99, 1)(), Tuple(99, 2)(), Tuple(99, 3)())
    )
    assert(
      ir.eval(StmShiftLeft(s0, s1, stmShape = Seq(3, 4)))
        == StmLiteral(expected.flatten: _*)()
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
    assert(
      ir.eval(StmShiftLeft(s0, s1, stmShape = Seq(2, 4, 5)))
        == StmLiteral(expected.flatten.flatten: _*)()
    )
  }

  test("StmShiftRight:1D") {
    val s = StmCount(3)
    assert(
      ir.eval(StmShiftRight(s, 42, stmShape = Seq(3)))
        == StmLiteral(42, 0, 1)()
    )
  }

  test("StmShiftRight:2D") {
    val s0 = StmCount2D(3, 4)
    val s1 = StmMap(
      StmCount(4),
      (j: Expr) => Tuple(99, j)(),
      n = 4,
      fInShape = None,
      fOutShape = None
    )

    val expected = Seq(
      Seq(Tuple(99, 0)(), Tuple(99, 1)(), Tuple(99, 2)(), Tuple(99, 3)()),
      Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)(), Tuple(0, 3)()),
      Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)())
    )
    assert(
      ir.eval(StmShiftRight(s0, s1, stmShape = Seq(3, 4)))
        == StmLiteral(expected.flatten: _*)()
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
    assert(
      ir.eval(StmShiftRight(s0, s1, stmShape = Seq(2, 4, 5)))
        == StmLiteral(expected.flatten.flatten: _*)()
    )
  }

  test("StmConcat:1D") {
    val actual1 =
      StmConcat(
        StmCst(1, 77),
        StmCst(1, 77),
        stm1Shape = Seq(1),
        stm2Shape = Seq(1)
      )
    assert(ir.eval(actual1) == StmLiteral.ints(77, 77))
    val actual2 =
      StmConcat(
        StmCount(3),
        StmCst(4, 77),
        stm1Shape = Seq(3),
        stm2Shape = Seq(4)
      )
    assert(ir.eval(actual2) == StmLiteral.ints(0, 1, 2, 77, 77, 77, 77))
    val actual3 = StmConcat(
      StmConcat(
        StmCount(3),
        StmCst(4, 77),
        stm1Shape = Seq(3),
        stm2Shape = Seq(4)
      ),
      StmCount(2),
      stm1Shape = Seq(7),
      stm2Shape = Seq(2)
    )
    assert(ir.eval(actual3) == StmLiteral.ints(0, 1, 2, 77, 77, 77, 77, 0, 1))
  }

  test("StmConcat:2D") {
    val s0 = StmCst2D(2, 2, Tuple(99, 99)())
    val s1 = StmCount2D(3, 2)
    val actual = StmConcat(s0, s1, stm1Shape = Seq(2, 2), stm2Shape = Seq(3, 2))
    val expected = Seq(
      Seq(Tuple(99, 99)(), Tuple(99, 99)()),
      Seq(Tuple(99, 99)(), Tuple(99, 99)()),
      Seq(Tuple(0, 0)(), Tuple(0, 1)()),
      Seq(Tuple(1, 0)(), Tuple(1, 1)()),
      Seq(Tuple(2, 0)(), Tuple(2, 1)())
    )
    assert(ir.eval(actual) == StmLiteral(expected.flatten: _*)())
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
    val actual =
      StmConcat(s0, s1, stm1Shape = Seq(3, 2, 2), stm2Shape = Seq(2, 2, 2))
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
    assert(ir.eval(actual) == StmLiteral(expected.flatten.flatten: _*)())
  }

  test("StmZip:1D") {
    val a = StmCount(4)
    val b = {
      val b = Param("b")
      StmBuild(4, SSome(b), Map[Param, (Expr, Expr)](b -> (True, Not(b)())))()
    }
    val expected =
      StmLiteral(
        Tuple(0, True)(),
        Tuple(1, False)(),
        Tuple(2, True)(),
        Tuple(3, False)()
      )()
    assert(ir.eval(StmZip(a, b)) == expected)
  }

  test("StmZipAlternating:1D") {
    val a = StmCount(4)
    val b = StmCountFrom(5, 4)
    assert(
      ir.eval(StmZipAlternating(a, b))
        == StmLiteral(
          Tuple(0, 5)(),
          Tuple(6, 1)(),
          Tuple(2, 7)(),
          Tuple(8, 3)()
        )()
    )
  }

  test("StmRepeat:1D") {
    val s = StmCount(3)
    val expected = Seq(IntCst(0), IntCst(1), IntCst(2))

    val actual0 = StmRepeat(s, 0, n = 3)
    assert(ir.eval(actual0) == StmLiteral()())

    val actual1 = StmRepeat(s, 1, n = 3)
    assert(ir.eval(actual1) == StmLiteral(expected: _*)())

    val actual2 = StmRepeat(s, 2, n = 3)
    assert(ir.eval(actual2) == StmLiteral(expected ++ expected: _*)())

    val actual3 = StmRepeat(s, 3, n = 3)
    assert(
      ir.eval(actual3) == StmLiteral(expected ++ expected ++ expected: _*)()
    )
  }

  test("StmRepeat:2D") {
    val s = StmCount2D(2, 3)
    val expected = Seq(
      Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
      Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())
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

  test("StmReverse:1D") {
    val n = 5
    val s = StmCount(n)
    assert(ir.eval(StmReverse(s, n = n)) == StmLiteral(4, 3, 2, 1, 0)())
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
      .map({ case (i, j) => Tuple(i, j)() })
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
      Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)(), Tuple(0, 3)()),
      Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)()),
      Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)(), Tuple(2, 3)()),
      Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)(), Tuple(0, 3)()),
      Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)()),
      Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)(), Tuple(2, 3)())
    )
    assertStreamEqual(StmJoin(s), expected.flatten)
    assertStreamEqual(StmJoin(StmJoin(s)), expected.flatten)
  }

  test("StmSplitJoin") {
    val p = Param("p")
    val s = StmCount(6)
    val elems = (0 until 6).map(x => IntCst(x))

    // Correctness
    assertStreamEqual(Let(p, s, StmJoin(StmSplit(p, 1))), elems)
    assertStreamEqual(Let(p, s, StmJoin(StmSplit(p, 2))), elems)
    assertStreamEqual(Let(p, s, StmJoin(StmSplit(p, 3))), elems)
    assertStreamEqual(Let(p, s, StmJoin(StmSplit(p, 6))), elems)
    // Efficiency
    assert(StmJoin(StmSplit(p, 1)) == p)
    assert(StmJoin(StmSplit(p, 2)) == p)
    assert(StmJoin(StmSplit(p, 3)) == p)
    assert(StmJoin(StmSplit(p, 6)) == p)
  }

  test("StmJoinSplit") {
    val p = Param("p")
    val s = StmCount2D(3, 2)
    val expected = Seq(
      Tuple(0, 0)(),
      Tuple(0, 1)(),
      Tuple(1, 0)(),
      Tuple(1, 1)(),
      Tuple(2, 0)(),
      Tuple(2, 1)()
    )

    // Correctness
    assertStreamEqual(Let(p, s, StmSplit(StmJoin(p), 2)), expected)
    // Efficiency
    assert(StmSplit(StmJoin(p), 2) == p)
  }

  test("StmSlideV:1D:UnitWindow") {
    val s = StmCount(3)
    val actual = StmSlideV(s, 1, stmShape = Seq(3))
    val expected =
      StmLiteral(VecLiteral(0)(), VecLiteral(1)(), VecLiteral(2)())()
    assert(ir.eval(actual) == expected)
  }

  test("StmSlideV:1D:SmallWindow") {
    val s = StmCount(4)
    val actual = StmSlideV(s, 2, stmShape = Seq(4))
    val expected = StmLiteral(
      VecLiteral(IntCst(0), IntCst(1))(),
      VecLiteral(IntCst(1), IntCst(2))(),
      VecLiteral(IntCst(2), IntCst(3))()
    )()
    assert(ir.eval(actual) == expected)
  }

  test("StmSlideV:1D:LargestWindow") {
    val s = StmCount(5)
    val actual = StmSlideV(s, 5, stmShape = Seq(5))
    val expected = StmLiteral(
      VecLiteral(IntCst(0), IntCst(1), IntCst(2), IntCst(3), IntCst(4))()
    )()
    assert(ir.eval(actual) == expected)
  }

  test("StmSlideV:2D") {
    val s = StmCount2D(4, 3)
    val actual = StmSlideV(s, 2, stmShape = Seq(4, 3))
    val expected = StmLiteral(
      VecLiteral(
        Seq(
          Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
          Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())
        ).flatten: _*
      )(),
      VecLiteral(
        Seq(
          Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)()),
          Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)())
        ).flatten: _*
      )(),
      VecLiteral(
        Seq(
          Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)()),
          Seq(Tuple(3, 0)(), Tuple(3, 1)(), Tuple(3, 2)())
        ).flatten: _*
      )()
    )()
    assert(ir.eval(actual) == expected)
  }

  test("StmSlideV:3D") {
    // [[[0, 0, 0, 0],
    //   [0, 0, 0, 0],
    //   [0, 0, 0, 0]],
    //
    //  [[5, 5, 5, 5],
    //   [5, 5, 5, 5],
    //   [5, 5, 5, 5]],
    //
    //  [[10, 10, 10, 10],
    //   [10, 10, 10, 10],
    //   [10, 10, 10, 10]]]
    val s = StmMap(
      StmCount(3),
      (i: Expr) => StmCst2D(3, 4, i * 5),
      n = 3,
      fInShape = None,
      fOutShape = Some(12)
    )
    val actual = StmSlideV(s, 2, stmShape = Seq(3, 3, 4))
    val expected = StmLiteral(
      // Window 0
      VecLiteral(
        Seq(
          // Window 0 element 0
          Seq(
            Seq(IntCst(0), IntCst(0), IntCst(0), IntCst(0)),
            Seq(IntCst(0), IntCst(0), IntCst(0), IntCst(0)),
            Seq(IntCst(0), IntCst(0), IntCst(0), IntCst(0))
          ),
          // Window 0 element 1
          Seq(
            Seq(IntCst(5), IntCst(5), IntCst(5), IntCst(5)),
            Seq(IntCst(5), IntCst(5), IntCst(5), IntCst(5)),
            Seq(IntCst(5), IntCst(5), IntCst(5), IntCst(5))
          )
        ).flatten.flatten: _*
      )(),
      // Window 1
      VecLiteral(
        Seq(
          // Window 1 element 0
          Seq(
            Seq(IntCst(5), IntCst(5), IntCst(5), IntCst(5)),
            Seq(IntCst(5), IntCst(5), IntCst(5), IntCst(5)),
            Seq(IntCst(5), IntCst(5), IntCst(5), IntCst(5))
          ),
          // Window 1 element 1
          Seq(
            Seq(IntCst(10), IntCst(10), IntCst(10), IntCst(10)),
            Seq(IntCst(10), IntCst(10), IntCst(10), IntCst(10)),
            Seq(IntCst(10), IntCst(10), IntCst(10), IntCst(10))
          )
        ).flatten.flatten: _*
      )()
    )()
    assert(ir.eval(actual) == expected)
  }

  test("StmSlideS:1D:UnitWindow") {
    val actual = StmSlideS(StmCount(3), 1, stmShape = Seq(3))
    val expected = StmLiteral(0, 1, 2)()
    assert(ir.eval(actual) == expected)
  }

  test("StmSlideS:1D:SmallWindow") {
    val actual = StmSlideS(StmCount(4), 2, stmShape = Seq(4))

    // Correctness
    val expected = StmLiteral(0, 1, 1, 2, 2, 3)()
    assert(ir.eval(actual) == expected)
    // Performance
    // TODO: Look at how good the hardware is.
    //       It is possible to implement this without any vectors or memory.
  }

  test("StmSlideS:1D:SameSize") {
    val actual = StmSlideS(StmCount(5), 5, stmShape = Seq(5))
    val expected = StmLiteral(0, 1, 2, 3, 4)()
    assert(ir.eval(actual) == expected)
  }

  test("StmSlideS:2D") {
    val actual = StmSlideS(StmCount2D(4, 3), 2, stmShape = Seq(4, 3))
    val expected = StmLiteral(
      Seq(
        Seq(
          Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
          Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())
        ),
        Seq(
          Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)()),
          Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)())
        ),
        Seq(
          Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)()),
          Seq(Tuple(3, 0)(), Tuple(3, 1)(), Tuple(3, 2)())
        )
      ).flatten.flatten: _*
    )()
    assert(ir.eval(actual) == expected)
  }

  test("StmSlideS:3D") {
    // [[[0, 0, 0, 0],
    //   [0, 0, 0, 0],
    //   [0, 0, 0, 0]],
    //
    //  [[5, 5, 5, 5],
    //   [5, 5, 5, 5],
    //   [5, 5, 5, 5]],
    //
    //  [[10, 10, 10, 10],
    //   [10, 10, 10, 10],
    //   [10, 10, 10, 10]]]
    val input = StmMap(
      StmCount(3),
      (i: Expr) => StmCst2D(3, 4, i * 5),
      n = 3,
      fInShape = None,
      fOutShape = Some(12)
    )
    val actual = StmSlideS(input, 2, stmShape = Seq(3, 3, 4))
    val expected = StmLiteral(
      Seq(
        // Window 0
        Seq(
          // Window 0 element 0
          Seq(
            Seq(IntCst(0), IntCst(0), IntCst(0), IntCst(0)),
            Seq(IntCst(0), IntCst(0), IntCst(0), IntCst(0)),
            Seq(IntCst(0), IntCst(0), IntCst(0), IntCst(0))
          ),
          // Window 0 element 1
          Seq(
            Seq(IntCst(5), IntCst(5), IntCst(5), IntCst(5)),
            Seq(IntCst(5), IntCst(5), IntCst(5), IntCst(5)),
            Seq(IntCst(5), IntCst(5), IntCst(5), IntCst(5))
          )
        ).flatten.flatten,
        // Window 1
        Seq(
          // Window 1 element 0
          Seq(
            Seq(IntCst(5), IntCst(5), IntCst(5), IntCst(5)),
            Seq(IntCst(5), IntCst(5), IntCst(5), IntCst(5)),
            Seq(IntCst(5), IntCst(5), IntCst(5), IntCst(5))
          ),
          // Window 1 element 1
          Seq(
            Seq(IntCst(10), IntCst(10), IntCst(10), IntCst(10)),
            Seq(IntCst(10), IntCst(10), IntCst(10), IntCst(10)),
            Seq(IntCst(10), IntCst(10), IntCst(10), IntCst(10))
          )
        ).flatten.flatten
      ).flatten: _*
    )()
    assert(ir.eval(actual) == expected)
  }

  test("StmTranspose") {
    val s = Param("s")
    val n = Param("n")
    val m = Param("m")
    val expected = StmLiteral(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(1, 0)(), Tuple(2, 0)(), Tuple(3, 0)()),
        Seq(Tuple(0, 1)(), Tuple(1, 1)(), Tuple(2, 1)(), Tuple(3, 1)()),
        Seq(Tuple(0, 2)(), Tuple(1, 2)(), Tuple(2, 2)(), Tuple(3, 2)())
      ).flatten: _*
    )()
    val transposed = StmTranspose(s, n = n, m = m)

    // Correctness
    val actual =
      Let(n, 4, Let(m, 3, Let(s, StmCount2D(4, 3), transposed)))
    assert(ir.eval(actual) == expected)
    // Performance
    // TODO: Look at how good the hardware is.
    //       It is possible to implement this without any vectors or memory in
    //       this particular case.
  }

  test("StmTransposeTranspose") {
    val p = Param("p")
    val input = StmCount2D(4, 3)
    val s = StmTranspose(StmTranspose(p, n = 4, m = 3), n = 3, m = 4)
    val expected = StmLiteral(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)()),
        Seq(Tuple(3, 0)(), Tuple(3, 1)(), Tuple(3, 2)())
      ).flatten: _*
    )()

    // Correctness
    assert(ir.eval(Let(p, input, s)) == expected)
    // Performance
    // TODO: Look at how good the hardware is.
    //       It is possible to implement this without any vectors or memory in
    //       this particular case.
  }
}
