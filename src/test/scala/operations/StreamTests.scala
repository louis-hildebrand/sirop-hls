package operations

import ir._
import org.scalatest.funsuite.AnyFunSuite

class StreamTests extends AnyFunSuite {
  test("StmCst:Int") {
    val s = StmCst(4, 3)()
    assert(ir.eval(s.tchk()) == StmLiteral(3, 3, 3, 3)())
  }

  test("StmCst:Tuple") {
    val c = Tuple(False, 99)()
    val s = StmCst(3, c)()
    assert(ir.eval(s.tchk()) == StmLiteral(c, c, c)())
  }

  test("StmCst:Stream") {
    val c = IntCst(42)
    val s = StmCst(2, StmCst(3, c)())()
    assert(ir.eval(s.tchk()) == StmLiteral(c, c, c, c, c, c)())
  }

  test("StmCount") {
    val s = StmCount(5)().tchk()
    assert(ir.eval(s) == StmLiteral(0, 1, 2, 3, 4)())
  }

  test("StmCountFrom") {
    val s = StmRange(4, 3, 1)().tchk()
    assert(ir.eval(s) == StmLiteral(3, 4, 5, 6)())
  }

  test("StmRange(4, 3, 2)") {
    val s = StmRange(4, 3, 2)()
    assert(ir.eval(s) == StmLiteral.ints(3, 5, 7, 9))
    assert(ir.eval(s.tchk()) == StmLiteral.ints(3, 5, 7, 9))
  }

  test("StmRange(3, 2, -3)") {
    val s = StmRange(3, 2, -3)()
    assert(ir.eval(s) == StmLiteral.ints(2, -1, -4))
    assert(ir.eval(s.tchk()) == StmLiteral.ints(2, -1, -4))
  }

  test("StmCst2D") {
    val s = StmCst2D(3, 4, 42)()
    val expected = StmLiteral(
      StmLiteral.ints(42, 42, 42, 42),
      StmLiteral.ints(42, 42, 42, 42),
      StmLiteral.ints(42, 42, 42, 42)
    )()
    assert(ir.eval(s) == expected.flatten)
    assert(ir.eval(s.tchk()) == expected.flatten)
  }

  test("StmCount2D") {
    val s = StmCount2D(2, 3)()
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
    assert(ir.eval(s.tchk()) == expected.flatten)
  }

  test("Iterate:Square") {
    val x = Param("x")(TyInt)
    val n = Param("n")()
    val iter = Iterate(n, 3, Function(x, x * x)())().tchk(Map(n -> TyInt))
    val e = (nVal: Int) => Let(n, nVal, iter)().tchk()
    assert(ir.eval(e(0)) == StmLiteral(3)())
    assert(ir.eval(e(1)) == StmLiteral(9)())
    assert(ir.eval(e(2)) == StmLiteral(81)())
    assert(ir.eval(e(3)) == StmLiteral(81 * 81)())
  }

  test("Iterate:PlusTwo") {
    val x = Param("x")(TyTuple(TyInt))
    val n = Param("n")()
    val iter = Iterate(n, Tuple(-10)(), Function(x, Tuple(x.__0 + 2)())())()
      .tchk(Map(n -> TyInt))
    val e =
      (nVal: Int) => Let(n, nVal, iter)().tchk()
    assert(ir.eval(e(0)) == StmLiteral(Tuple(-10)())())
    assert(ir.eval(e(1)) == StmLiteral(Tuple(-8)())())
    assert(ir.eval(e(2)) == StmLiteral(Tuple(-6)())())
    assert(ir.eval(e(3)) == StmLiteral(Tuple(-4)())())
  }

  test("StmMap:1D-1D:+7") {
    val s = StmMap(StmCount(5)(), TyInt ::+ (x => x + 7))()
    assert(ir.eval(s.tchk()) == StmLiteral(7, 8, 9, 10, 11)())
  }

  test("StmMap:1D-1D:Identity") {
    val s = StmMap(StmCount(3)(), TyInt ::+ (x => x))()
    assert(ir.eval(s.tchk()) == StmLiteral(0, 1, 2)())
  }

  test("StmMap:1D-1D:DiscardInputReturn42") {
    val s = StmMap(StmCount(6)(), TyInt ::+ (_ => 42))()
    assert(ir.eval(s.tchk()) == StmLiteral(42, 42, 42, 42, 42, 42)())
  }

  test("StmMap:1D-1D:SingleElementStream") {
    val s = StmMap(StmCount(1)(), TyInt ::+ (x => x + 5))()
    assert(ir.eval(s.tchk()) == StmLiteral(5)())
  }

  // The scalar input to the inner function is used only in the `nextF`.
  test("StmMap:1D-2D:StmCst") {
    val s = StmMap(StmCount(4)(), TyInt ::+ (c => StmCst(3, c)()))().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 0, 0),
        Seq(1, 1, 1),
        Seq(2, 2, 2),
        Seq(3, 3, 3)
      ).flatten: _*
    )
    assert(ir.eval(s) == expected)
  }

  // The scalar input to the inner function is used only in the seed.
  test("StmMap:1D-2D:StmCountFrom") {
    val s =
      StmMap(StmCount(3)(), TyInt ::+ (n => StmRange(4, n, 1)()))().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 1, 2, 3),
        Seq(1, 2, 3, 4),
        Seq(2, 3, 4, 5)
      ).flatten: _*
    )
    assert(ir.eval(s) == expected)
  }

  // The scalar input to the inner function is used both in the `nextF` and in
  // the seed.
  test("StmMap:1D-2D:StmCstAndCountFrom") {
    val s = StmMap(
      StmCount(3)(),
      TyInt ::+ (i => {
        val acc = Param("acc")()
        StmBuild(
          3,
          SSome(Tuple(i, acc)())(),
          Map[Param, (Expr, Expr)](
            acc -> (i, acc + 3)
          )
        )()
      })
    )().tchk()
    val expected = StmLiteral(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(0, 3)(), Tuple(0, 6)()),
        Seq(Tuple(1, 1)(), Tuple(1, 4)(), Tuple(1, 7)()),
        Seq(Tuple(2, 2)(), Tuple(2, 5)(), Tuple(2, 8)())
      ).flatten: _*
    )()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:1D-2D:DiscardInputReturnStmCount") {
    val s = StmMap(StmCount(4)(), TyInt ::+ (_ => StmCount(3)()))().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 1, 2),
        Seq(0, 1, 2),
        Seq(0, 1, 2),
        Seq(0, 1, 2)
      ).flatten: _*
    )
    assert(ir.eval(s) == expected)
  }

  test("StmMap:1D-2D:SingleElementStream") {
    val s =
      StmMap(StmCst(1, 99)(), TyInt ::+ (c => StmRange(5, c, 1)()))().tchk()
    val expected = StmLiteral(99, 100, 101, 102, 103)()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-1D:StmFold") {
    val s = StmMap(
      StmCount2D(4, 3)(),
      TyStm(Int2(), 3) ::+ (s =>
        StmFold(
          s,
          0,
          TyInt ::+ (acc => Int2() ::+ (x => acc + x.__0 + x.__1))
        )()
      )
    )().tchk()
    val expected = StmLiteral(3, 6, 9, 12)()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-1D:Access") {
    val s = StmMap(
      StmCount2D(4, 3)(),
      TyStm(Int2(), 3) ::+ (s => StmAccess(s, 1)())
    )().tchk()
    val expected =
      StmLiteral(Tuple(0, 1)(), Tuple(1, 1)(), Tuple(2, 1)(), Tuple(3, 1)())()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-1D:Stm2Vec") {
    val s =
      StmMap(StmCount2D(5, 3)(), TyStm(Int2(), 3) ::+ (s => Stm2Vec(s)()))()
        .tchk()
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
      StmCount2D(3, 4)(),
      TyStm(Int2(), 4) ::+ (_ => StmCst(1, IntCst(42))())
    )().tchk()
    val expected = StmLiteral(42, 42, 42)()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-1D:SingleElementStream") {
    val s = StmMap(
      StmCount2D(1, 4)(),
      TyStm(Int2(), 4) ::+ (s =>
        StmFold(
          s,
          10,
          TyInt ::+ (acc => Int2() ::+ (x => acc + x.__1))
        )()
      )
    )().tchk()
    val expected = StmLiteral(10 + 0 + 1 + 2 + 3)()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmMap") {
    val ttup = TyTuple(TyInt, TyInt)
    val s = StmMap(
      StmCount2D(4, 3)(),
      TyStm(ttup, 3) ::+ (s => StmMap(s, ttup ::+ (x => x.__0 + x.__1))())
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 1, 2),
        Seq(1, 2, 3),
        Seq(2, 3, 4),
        Seq(3, 4, 5)
      ).flatten: _*
    )
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmScanInclusive") {
    // [[ 0,  1,  2,  3],
    //  [10, 11, 12, 13],
    //  [20, 21, 22, 23]]
    val input = StmMap(
      StmCount(3)(),
      TyInt ::+ (i => StmMap(StmCount(4)(), TyInt ::+ (j => 10 * i + j))())
    )()
    val s = StmMap(
      input,
      TyStm(TyInt, 4) ::+ (row =>
        StmScanInclusive(row, 0, TyInt ::+ (a => TyInt ::+ (x => a + x)))()
      )
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 1, 3, 6),
        Seq(10, 21, 33, 46),
        Seq(20, 41, 63, 86)
      ).flatten: _*
    )
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmScanExclusive") {
    // [[ 0,  1,  2,  3],
    //  [10, 11, 12, 13],
    //  [20, 21, 22, 23]]
    val input = StmMap(
      StmCount(3)(),
      TyInt ::+ (i => StmMap(StmCount(4)(), TyInt ::+ (j => 10 * i + j))())
    )()
    val s = StmMap(
      input,
      TyStm(TyInt, 4) ::+ (row =>
        StmScanExclusive(
          row,
          0,
          TyInt ::+ (a => TyInt ::+ (x => a + x))
        )()
      )
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 0, 1, 3),
        Seq(0, 10, 21, 33),
        Seq(0, 20, 41, 63)
      ).flatten: _*
    )
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmPrepend") {
    val s = StmMap(
      StmCst2D(4, 5, 42)(),
      TyStm(TyInt, 5) ::+ (s => StmPrepend(s, 43)())
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(43, 42, 42, 42, 42, 42),
        Seq(43, 42, 42, 42, 42, 42),
        Seq(43, 42, 42, 42, 42, 42),
        Seq(43, 42, 42, 42, 42, 42)
      ).flatten: _*
    )
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmAppend") {
    val s = StmMap(
      StmCst2D(4, 5, 42)(),
      TyStm(TyInt, 5) ::+ (s => StmAppend(s, 43)())
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(42, 42, 42, 42, 42, 43),
        Seq(42, 42, 42, 42, 42, 43),
        Seq(42, 42, 42, 42, 42, 43),
        Seq(42, 42, 42, 42, 42, 43)
      ).flatten: _*
    )
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmPrefix") {
    val s = StmMap(
      StmCount2D(3, 100)(),
      TyStm(TyTuple(TyInt, TyInt), 100) ::+ (s => StmPrefix(s, 2)())
    )().tchk()
    val expected = StmLiteral(
      StmLiteral(Tuple(0, 0)(), Tuple(0, 1)())(),
      StmLiteral(Tuple(1, 0)(), Tuple(1, 1)())(),
      StmLiteral(Tuple(2, 0)(), Tuple(2, 1)())()
    )()
    assert(ir.eval(s) == expected.flatten)
  }

  test("StmMap:2D-2D:StmSuffix") {
    val s = StmMap(
      StmCount2D(3, 100)(),
      TyStm(TyTuple(TyInt, TyInt), 100) ::+ (s => StmSuffix(s, 2)())
    )().tchk()
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
      StmCount2D(2, 3)(),
      TyStm(Int2(), 3) ::+ (s => StmShiftLeft(s, Tuple(100, 101)())())
    )().tchk()
    val expected = StmLiteral(
      Seq(
        Seq(
          Tuple(0, 1)(),
          Tuple(0, 2)(),
          Tuple(100, 101)()
        ),
        Seq(
          Tuple(1, 1)(),
          Tuple(1, 2)(),
          Tuple(100, 101)()
        )
      ).flatten: _*
    )()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmShiftRight") {
    val s = StmMap(
      StmCount2D(4, 5)(),
      TyStm(TyTuple(TyInt, TyInt), 5) ::+ (s =>
        StmShiftRight(s, Tuple(100, 101)())()
      )
    )().tchk()
    val expected = StmLiteral(
      Seq(
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
      ).flatten: _*
    )()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmConcatBefore") {
    val s = StmMap(
      StmCst2D(5, 5, 99)(),
      TyStm(TyInt, 5) ::+ (s => StmConcat(StmCount(3)(), s)())
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 1, 2, 99, 99, 99, 99, 99),
        Seq(0, 1, 2, 99, 99, 99, 99, 99),
        Seq(0, 1, 2, 99, 99, 99, 99, 99),
        Seq(0, 1, 2, 99, 99, 99, 99, 99),
        Seq(0, 1, 2, 99, 99, 99, 99, 99)
      ).flatten: _*
    )
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmConcatAfter") {
    val s = StmMap(
      StmCst2D(5, 5, 99)(),
      TyStm(TyInt, 5) ::+ (s => StmConcat(s, StmCount(3)())())
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(99, 99, 99, 99, 99, 0, 1, 2),
        Seq(99, 99, 99, 99, 99, 0, 1, 2),
        Seq(99, 99, 99, 99, 99, 0, 1, 2),
        Seq(99, 99, 99, 99, 99, 0, 1, 2),
        Seq(99, 99, 99, 99, 99, 0, 1, 2)
      ).flatten: _*
    )
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmZipWithIndexBefore") {
    val s = StmMap(
      StmCount2D(3, 3)(),
      TyStm(TyTuple(TyInt, TyInt), 3) ::+ (s => StmZip(StmCount(3)(), s)())
    )().tchk()
    val expected = StmLiteral(
      Seq(
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
      ).flatten: _*
    )()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmZipWithIndexAfter") {
    val s = StmMap(
      StmCount2D(3, 3)(),
      TyStm(TyTuple(TyInt, TyInt), 3) ::+ (s => StmZip(s, StmCount(3)())())
    )().tchk()
    val expected = StmLiteral(
      Seq(
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
      ).flatten: _*
    )()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmRepeat") {
    val s = StmMap(
      StmCount2D(4, 4)(),
      TyStm(TyTuple(TyInt, TyInt), 4) ::+ (s => StmRepeat(s, 3)())
    )().tchk()
    val expected = StmLiteral(
      Seq(
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
      ).flatten.flatten: _*
    )()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:Identity") {
    val s = StmMap(
      StmCount2D(2, 3)(),
      TyStm(TyTuple(TyInt, TyInt), 3) ::+ (s => s)
    )().tchk()
    val expected = StmLiteral(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())
      ).flatten: _*
    )()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:DiscardInputReturnStmCount") {
    val s = StmMap(
      StmCount2D(4, 3)(),
      TyStm(TyTuple(TyInt, TyInt), 3) ::+ (_ => StmCount(5)())
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 1, 2, 3, 4),
        Seq(0, 1, 2, 3, 4),
        Seq(0, 1, 2, 3, 4),
        Seq(0, 1, 2, 3, 4)
      ).flatten: _*
    )
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmSlideV") {
    val s = StmMap(
      StmCount2D(3, 5)(),
      TyStm(TyTuple(TyInt, TyInt), 5) ::+ (s => StmSlideV(s, 3)())
    )().tchk()
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
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:SingleElementOuterStream") {
    val s = StmMap(
      StmCount2D(1, 3)(),
      TyStm(Int2(), 3) ::+ (s => StmMap(s, Int2() ::+ (x => x.__0 + x.__1))())
    )().tchk()
    val expected = StmLiteral(0, 1, 2)()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:SingleElementInnerStream") {
    val s = StmMap(
      StmCount2D(4, 1)(),
      TyStm(Int2(), 1) ::+ (s => StmMap(s, Int2() ::+ (x => x.__0 + x.__1))())
    )().tchk()
    val expected = StmLiteral(0, 1, 2, 3)()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:1D-3D:MapCount") {
    val s = StmMap(
      StmCount(2)(),
      TyInt ::+ (i =>
        StmMap(
          StmCount(3)(),
          TyInt ::+ (j =>
            StmMap(StmCount(4)(), TyInt ::+ (k => Tuple(i, j, k)()))()
          )
        )()
      )
    )().tchk()
    val expected = StmLiteral(
      Seq(
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
      ).flatten.flatten: _*
    )()
    assert(ir.eval(s) == expected)
  }

  test("StmMap:2D-3D:StmSlideS") {
    val s = StmCount2D(3, 5)()
    val expected = StmLiteral(
      Seq(
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
      ).flatten.flatten: _*
    )()
    val actual =
      StmMap(s, TyStm(Int2(), 5) ::+ (s => StmSlideS(s, 3)()))().tchk()
    assert(ir.eval(actual) == expected)
  }

  test("StmMap:3D-3D:StmTranspose") {
    // TODO: Why is this so slow?!
    assume(false)
    // [[[(0, 0), (0, 1), (0, 2), (0, 3)],
    //   [(1, 0), (1, 1), (1, 2), (1, 3)],
    //   [(2, 0), (2, 1), (2, 2), (2, 3)]],
    //
    //  [[(0, 0), (0, 1), (0, 2), (0, 3)],
    //   [(1, 0), (1, 1), (1, 2), (1, 3)],
    //   [(2, 0), (2, 1), (2, 2), (2, 3)]]]
    val s = StmMap(StmCount(2)(), TyInt ::+ (_ => StmCount2D(3, 4)()))()
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
    val actual =
      StmMap(s, TyStm(TyStm(Int2(), 4), 3) ::+ (s => StmTranspose(s)()))()
        .tchk()
    assert(ir.eval(actual) == expected)
  }

  test("StmMap:MultiDimStreamFromCounters") {
    val s = StmMap(
      StmCount(3)(),
      TyInt ::+ (i => StmMap(StmRange(3, i, 1)(), TyInt ::+ (j => i + j * j))())
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 1, 4),
        Seq(2, 5, 10),
        Seq(6, 11, 18)
      ).flatten: _*
    )
    assert(ir.eval(s) == expected)
  }

  test("StmAccess:1D") {
    val n = 3
    val i = Param("i")(TyInt)
    val s = StmRange(n, 5, 2)()
    val access = StmAccess(s, i)().tchk(Map(i -> TyInt)).lower()

    assert(ir.eval(Let(i, 0, access)()) == StmLiteral(5)())
    assert(ir.eval(Let(i, 1, access)()) == StmLiteral(7)())
    assert(ir.eval(Let(i, 2, access)()) == StmLiteral(9)())
  }

  test("StmAccess:2D") {
    val s = StmCount2D(4, 5)()
    val i = Param("i")(TyInt)
    val access = StmAccess(s, i)().tchk(Map(i -> TyInt)).lower()

    val row = (i: Int) => StmLiteral((0 until 5).map(j => Tuple(i, j)()): _*)()

    assert(ir.eval(Let(i, 0, access)()) == row(0))
    assert(ir.eval(Let(i, 1, access)()) == row(1))
    assert(ir.eval(Let(i, 2, access)()) == row(2))
    assert(ir.eval(Let(i, 3, access)()) == row(3))
  }

  test("StmAccess:3D") {
    val s = StmMap(
      StmCount(3)(),
      TyInt ::+ (i =>
        StmMap(
          StmCount(3)(),
          TyInt ::+ (j =>
            StmMap(StmCount(4)(), TyInt ::+ (k => Tuple(i, j, k)()))()
          )
        )()
      )
    )()
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
      ir.eval(StmAccess(s, 0)().tchk())
        == StmLiteral(expected.head.flatten: _*)()
    )
    assert(
      ir.eval(StmAccess(s, 1)().tchk())
        == StmLiteral(expected(1).flatten: _*)()
    )
    assert(
      ir.eval(StmAccess(s, 2)().tchk())
        == StmLiteral(expected(2).flatten: _*)()
    )
  }

  test("StmFold:1D:Sum") {
    val sum =
      StmFold(StmCount(6)(), 3, TyInt ::+ (acc => TyInt ::+ (x => acc + x)))()
        .tchk()
    assert(ir.eval(sum) == StmLiteral(18)())
  }

  test("StmFold:1D:Product") {
    val prod = StmFold(
      StmRange(5, 1, 1)(),
      1,
      TyInt ::+ (acc => TyInt ::+ (x => acc * x))
    )().tchk()
    assert(ir.eval(prod) == StmLiteral(120)())
  }

  test("StmFold:1D:HornerMethod") {
    // Non-commutative, non-associative update function
    // Evaluate y = 2x^4 + 3x^3 + 4x^2 + 5x + 6 at x = 2
    val coefficients = StmRange(5, 2, 1)()
    val x = 2
    val y = StmFold(
      coefficients,
      0,
      TyInt ::+ (acc => TyInt ::+ (a => a + x * acc))
    )().tchk()
    assert(ir.eval(y) == StmLiteral(88)())
  }

  test("StmFold:1D:DiscardInputAdd42") {
    val x = StmFold(
      StmCount(5)(),
      2,
      TyInt ::+ (acc => TyInt ::+ (_ => acc + 42))
    )().tchk()
    assert(ir.eval(x) == StmLiteral(2 + 5 * 42)())
  }

  test("StmFold:2D:SumWide") {
    // [[0, 1, 2, 3, 4],
    //  [1, 2, 3, 4, 5],
    //  [2, 3, 4, 5, 6]]
    val s = StmMap(
      StmCount(3)(),
      TyInt ::+ (i => StmMap(StmCount(5)(), TyInt ::+ (j => i + j))())
    )()
    val sum = StmFold(
      s,
      0,
      TyInt ::+ (acc =>
        TyStm(TyInt, 5) ::+ (s =>
          StmMap(
            StmFold(
              s,
              0,
              TyInt ::+ (a => TyInt ::+ (x => a + x))
            )(),
            TyInt ::+ (x => acc + x)
          )()
        )
      )
    )().tchk()
    assert(ir.eval(sum) == StmLiteral(45)())
  }

  test("StmFold:2D:SumNarrow") {
    // [[0, 1],
    //  [1, 2],
    //  [2, 3],
    //  [3, 4]]
    val s = StmMap(
      StmCount(4)(),
      TyInt ::+ (i => StmMap(StmCount(2)(), TyInt ::+ (j => i + j))())
    )()
    val sum = StmFold(
      s,
      0,
      TyInt ::+ (acc =>
        TyStm(TyInt, 2) ::+ (s =>
          StmMap(
            StmFold(
              s,
              0,
              TyInt ::+ (a => TyInt ::+ (x => a + x))
            )(),
            TyInt ::+ (x => acc + x)
          )()
        )
      )
    )().tchk()
    assert(ir.eval(sum) == StmLiteral(16)())
  }

  test("StmFold:2D:SumColumn") {
    // [[1, 2, 3, 4],
    //  [2, 3, 4, 5],
    //  [3, 4, 5, 6],
    //  [4, 5, 6, 7]]
    val s = StmMap(
      StmCount(4)(),
      TyInt ::+ (i => StmMap(StmCount(4)(), TyInt ::+ (j => i + j + 1))())
    )()
    val x = StmFold(
      s,
      13,
      TyInt ::+ (acc =>
        TyStm(TyInt, 4) ::+ (s =>
          StmMap(StmAccess(s, 1)(), TyInt ::+ (x => acc + x))()
        )
      )
    )().tchk()
    assert(ir.eval(x) == StmLiteral(13 + 2 + 3 + 4 + 5)())
  }

  test("StmFold:2D:Product") {
    // [[1, 2, 3],
    //  [2, 3, 4],
    //  [3, 4, 5]]
    val s = StmMap(
      StmCount(3)(),
      TyInt ::+ (i => StmMap(StmCount(3)(), TyInt ::+ (j => i + j + 1))())
    )()
    val prod = StmFold(
      s,
      1,
      TyInt ::+ (acc =>
        TyStm(TyInt, 3) ::+ (s =>
          StmMap(
            StmFold(
              s,
              1,
              TyInt ::+ (acc => TyInt ::+ (x => acc * x))
            )(),
            TyInt ::+ (x => acc * x)
          )()
        )
      )
    )().tchk()
    assert(ir.eval(prod) == StmLiteral(8640)())
  }

  test("StmFold:2D:DiscardInputAdd42") {
    // [[0, 1, 2, 3, 4],
    //  [1, 2, 3, 4, 5],
    //  [2, 3, 4, 5, 6]]
    val s = StmMap(
      StmCount(3)(),
      TyInt ::+ (i => StmMap(StmCount(5)(), TyInt ::+ (j => i + j))())
    )()
    val sum = StmFold(
      s,
      0,
      TyInt ::+ (acc => TyStm(TyInt, 5) ::+ (_ => StmCst(1, acc + 42)()))
    )().tchk()
    assert(ir.eval(sum) == StmLiteral(3 * 42)())
  }

  test("StmFold:3D:Sum") {
    // TODO: Why is this so slow?!
    assume(false)
    // [[[0, 1, 2, 3],
    //   [1, 2, 3, 4]],
    //  [[1, 2, 3, 4],
    //   [2, 3, 4, 5]],
    //  [[2, 3, 4, 5],
    //   [3, 4, 5, 6]]]
    val s = StmMap(
      StmCount(3)(),
      TyInt ::+ (i =>
        StmMap(
          StmCount(2)(),
          TyInt ::+ (j => StmMap(StmCount(4)(), TyInt ::+ (k => i + j + k))())
        )()
      )
    )()
    val sum = StmFold(
      s,
      0,
      TyInt ::+ (acc =>
        TyStm(TyStm(TyInt, 4), 2) ::+ (s =>
          StmMap(
            StmFold(
              s,
              0,
              TyInt ::+ (acc =>
                TyStm(TyInt, 4) ::+ (s =>
                  StmMap(
                    StmFold(
                      s,
                      0,
                      TyInt ::+ (a => TyInt ::+ (x => a + x))
                    )(),
                    TyInt ::+ (x => acc + x)
                  )()
                )
              )
            )(),
            TyInt ::+ (x => acc + x)
          )()
        )
      )
    )().tchk()
    assert(ir.eval(sum) == StmLiteral(72)())
  }

  test("StmFold:3D:Product") {
    // TODO: Why is this so slow?!
    assume(false)
    // [[[1, 2],
    //   [2, 3]],
    //  [[2, 3],
    //   [3, 4]],
    //  [[3, 4],
    //   [4, 5]]]
    val s = StmMap(
      StmCount(3)(),
      TyInt ::+ (i =>
        StmMap(
          StmCount(2)(),
          TyInt ::+ (j =>
            StmMap(StmCount(2)(), TyInt ::+ (k => i + j + k + 1))()
          )
        )()
      )
    )()
    val prod = StmFold(
      s,
      1,
      TyInt ::+ (acc =>
        TyStm(TyStm(TyInt, 2), 2) ::+ (s =>
          StmMap(
            StmFold(
              s,
              1,
              TyInt ::+ (acc =>
                TyStm(TyInt, 2) ::+ (s =>
                  StmMap(
                    StmFold(
                      s,
                      1,
                      TyInt ::+ (a => TyInt ::+ (x => a * x))
                    )(),
                    TyInt ::+ (x => acc * x)
                  )()
                )
              )
            )(),
            TyInt ::+ (x => acc * x)
          )()
        )
      )
    )().tchk()
    assert(ir.eval(prod) == StmLiteral(IntCst(207360))())
  }

  test("StmScanInclusive:1D:Sum") {
    // [2, 3,  4,  5,  6]
    val s = StmMap(StmCount(5)(), TyInt ::+ (x => x + 2))()
    // [2, 7, 18, 41, 88]
    val sum =
      StmScanInclusive(s, 0, TyInt ::+ (acc => TyInt ::+ (x => x + 2 * acc)))()
        .tchk()
    val expected = StmLiteral(2, 7, 18, 41, 88)()
    assert(ir.eval(sum) == expected)
  }

  test("StmScanInclusive:2D:SumRowSums") {
    // [[0, 1],
    //  [2, 3],
    //  [4, 5],
    //  [6, 7]]
    val s = StmSplit(StmCount(8)(), 2)()
    val sums = StmScanInclusive(
      s,
      0,
      TyInt ::+ (acc =>
        TyStm(TyInt, 2) ::+ (s =>
          StmMap(
            StmFold(
              s,
              0,
              TyInt ::+ (a => TyInt ::+ (x => a + x))
            )(),
            TyInt ::+ (x => acc + x)
          )()
        )
      )
    )().tchk()
    // scan([1, 5, 9, 13])
    val expected = StmLiteral(1, 6, 15, 28)()
    assert(ir.eval(sums) == expected)
  }

  test("StmScanInclusive:2D:SumColumn1") {
    // [[1, 2, 3, 4],
    //  [2, 3, 4, 5],
    //  [3, 4, 5, 6],
    //  [4, 5, 6, 7]]
    val s = StmMap(
      StmCount(4)(),
      TyInt ::+ (i => StmMap(StmCount(4)(), TyInt ::+ (j => i + j + 1))())
    )()
    val sums = StmScanInclusive(
      s,
      0,
      TyInt ::+ (acc =>
        TyStm(TyInt, 4) ::+ (s =>
          StmMap(StmAccess(s, 1)(), TyInt ::+ (x => acc + x))()
        )
      )
    )().tchk()
    // scan([2, 3, 4, 5])
    val expected = StmLiteral(2, 5, 9, 14)()
    assert(ir.eval(sums) == expected)
  }

  test("StmScanExclusive:1D:Sum") {
    // [2, 3, 4,  5,  6]
    val s = StmMap(StmCount(5)(), TyInt ::+ (x => x + 2))()
    // [0, 2, 7, 18, 41]
    val sum =
      StmScanExclusive(
        s,
        0,
        TyInt ::+ (acc => TyInt ::+ (x => x + 2 * acc))
      )().tchk()
    assert(ir.eval(sum) == StmLiteral(0, 2, 7, 18, 41)())
  }

  test("StmScanExclusive:2D:SumRowSums") {
    // [[0, 1],
    //  [1, 2],
    //  [2, 3],
    //  [3, 4]]
    val s = StmMap(
      StmCount(4)(),
      TyInt ::+ (i => StmMap(StmCount(2)(), TyInt ::+ (j => i + j))())
    )()
    val sums = StmScanExclusive(
      s,
      0,
      TyInt ::+ (acc =>
        TyStm(TyInt, 2) ::+ (s =>
          StmMap(
            StmFold(
              s,
              0,
              TyInt ::+ (a => TyInt ::+ (x => a + x))
            )(),
            TyInt ::+ (x => acc + x)
          )()
        )
      )
    )().tchk()
    // scan([1, 3, 5, 7])
    val expected = StmLiteral(0, 1, 4, 9)()
    assert(ir.eval(sums) == expected)
  }

  test("StmScanExclusive:2D:SumColumn1") {
    // [[1, 2, 3, 4],
    //  [2, 3, 4, 5],
    //  [3, 4, 5, 6],
    //  [4, 5, 6, 7]]
    val s = StmMap(
      StmCount(4)(),
      TyInt ::+ (i => StmMap(StmCount(4)(), TyInt ::+ (j => i + j + 1))())
    )()
    val sums = StmScanExclusive(
      s,
      0,
      TyInt ::+ (acc =>
        TyStm(TyInt, 4) ::+ (s =>
          StmMap(StmAccess(s, 1)(), TyInt ::+ (x => acc + x))()
        )
      )
    )()
    // scan([2, 3, 4, 5])
    val expected = StmLiteral(0, 2, 5, 9)()
    assert(ir.eval(sums) == expected)
  }

  test("Vec2Stm:1D") {
    val v = VecBuild(5, TyInt ::+ (i => i + 1))()
    assert(ir.eval(Vec2Stm(v)().tchk()) == StmLiteral.ints(1, 2, 3, 4, 5))
  }

  test("Vec2Stm:2D") {
    val v =
      VecBuild(
        4,
        TyInt ::+ (i => VecBuild(3, TyInt ::+ (j => Tuple(i, j)()))())
      )()
    val s = StmMap(Vec2Stm(v)(), TyVec(Int2(), 3) ::+ (v => Vec2Stm(v)()))()

    val expected = StmLiteral(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)()),
        Seq(Tuple(3, 0)(), Tuple(3, 1)(), Tuple(3, 2)())
      ).flatten: _*
    )()
    assert(ir.eval(s.tchk()) == expected)
  }

  test("Vec2Stm2Vec") {
    val p = Param("p")()
    val actual = Stm2Vec(Vec2Stm(p)())()

    val v0 = VecBuild(6, TyInt ::+ (i => i + 2))()
    val expected0 = StmLiteral(VecLiteral(2, 3, 4, 5, 6, 7)())()
    assert(ir.eval(Let(p, v0, actual)().tchk()) == expected0)

    val v1 = VecBuild(6, TyInt ::+ (i => i * i))()
    val expected1 = StmLiteral(VecLiteral(0, 1, 4, 9, 16, 25)())()
    assert(ir.eval(Let(p, v1, actual)().tchk()) == expected1)
  }

  test("Stm2Vec2Stm") {
    val p = Param("p")()
    val actual = StmMap(Stm2Vec(p)(), TyVec(TyInt, 6) ::+ (v => Vec2Stm(v)()))()

    val s0 = StmCount(6)()
    val expected0 = StmLiteral(0, 1, 2, 3, 4, 5)()
    assert(ir.eval(Let(p, s0, actual)().tchk()) == expected0)

    val s1 = StmCst(6, 42)()
    val expected1 = StmLiteral(42, 42, 42, 42, 42, 42)()
    assert(ir.eval(Let(p, s1, actual)().tchk()) == expected1)
  }

  test("StmPrepend:1D") {
    val s = StmCount(3)()
    val actual = StmPrepend(s, 42)().tchk()
    assert(ir.eval(actual) == StmLiteral.ints(42, 0, 1, 2))
  }

  test("StmPrepend:2D") {
    val s0 = StmCount2D(3, 3)()
    val s1 = StmCst(3, Tuple(42, 42)())()

    val actual = StmPrepend(s0, s1)().tchk()
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
    val s0 = StmMap(StmCount(2)(), TyInt ::+ (_ => StmCst2D(2, 2, 42)()))()
    val s1 = StmCst2D(2, 2, 99)()
    val actual = StmPrepend(s0, s1)().tchk()

    val expected = StmLiteral.ints(
      Seq(
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
      ).flatten.flatten: _*
    )
    assert(ir.eval(actual) == expected)
  }

  test("StmAppend:1D") {
    val s = StmCount(3)()
    val actual = StmAppend(s, 42)().tchk()
    assert(ir.eval(actual) == StmLiteral.ints(0, 1, 2, 42))
  }

  test("StmAppend:2D") {
    val s0 = StmCount2D(3, 3)()
    val s1 = StmCst(3, Tuple(42, 42)())()

    val actual = StmAppend(s0, s1)().tchk()
    val expected = StmLiteral(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)()),
        Seq(Tuple(42, 42)(), Tuple(42, 42)(), Tuple(42, 42)())
      ).flatten: _*
    )()
    assert(ir.eval(actual) == expected)
  }

  test("StmAppend:3D") {
    val s0 = StmMap(StmCount(2)(), TyInt ::+ (_ => StmCst2D(2, 2, 42)()))()
    val s1 = StmCst2D(2, 2, 99)()
    val actual = StmAppend(s0, s1)().tchk()

    val expected = StmLiteral.ints(
      Seq(
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
      ).flatten.flatten: _*
    )
    assert(ir.eval(actual) == expected)
  }

  test("StmPrefix:1D") {
    val s = StmCount(3)()
    val k = Param("k")(TyInt)
    val prefix = StmPrefix(s, k)().tchk(Map(k -> TyInt))

    assert(ir.eval(Let(k, 0, prefix)()) == StmLiteral()())
    assert(ir.eval(Let(k, 1, prefix)()) == StmLiteral(0)())
    assert(ir.eval(Let(k, 2, prefix)()) == StmLiteral(0, 1)())
    assert(ir.eval(Let(k, 3, prefix)()) == StmLiteral(0, 1, 2)())
  }

  test("StmPrefix:2D") {
    val s = StmCount2D(2, 3)()
    val k = Param("k")(TyInt)
    val prefix = StmPrefix(s, k)().tchk(Map(k -> TyInt))

    val expectedValues = Seq(
      Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
      Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())
    )
    val expected =
      (k: Int) => StmLiteral(expectedValues.slice(0, k).flatten: _*)()
    assert(ir.eval(Let(k, 0, prefix)()) == expected(0))
    assert(ir.eval(Let(k, 1, prefix)()) == expected(1))
    assert(ir.eval(Let(k, 2, prefix)()) == expected(2))
  }

  test("StmPrefix:3D") {
    val s = StmMap(StmCount(2)(), TyInt ::+ (_ => StmCount2D(4, 5)()))()

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
    assert(ir.eval(StmPrefix(s, 0)().tchk()) == StmLiteral()())
    assert(
      ir.eval(StmPrefix(s, 1)().tchk())
        == StmLiteral(expected.slice(0, 1).flatten.flatten: _*)()
    )
    assert(
      ir.eval(StmPrefix(s, 2)().tchk())
        == StmLiteral(expected.slice(0, 2).flatten.flatten: _*)()
    )
  }

  test("StmSuffix:1D") {
    val s = StmCount(3)()
    val k = Param("k")(TyInt)
    val suffix = StmSuffix(s, k)().tchk(Map(k -> TyInt))
    assert(ir.eval(Let(k, 0, suffix)()) == StmLiteral()())
    assert(ir.eval(Let(k, 1, suffix)()) == StmLiteral(2)())
    assert(ir.eval(Let(k, 2, suffix)()) == StmLiteral(1, 2)())
    assert(ir.eval(Let(k, 3, suffix)()) == StmLiteral(0, 1, 2)())
  }

  test("StmSuffix:2D") {
    val s = StmCount2D(3, 2)()
    val k = Param("k")(TyInt)
    val suffix = StmSuffix(s, k)().tchk(Map(k -> TyInt))

    val expectedValues = Seq(
      Seq(Tuple(0, 0)(), Tuple(0, 1)()),
      Seq(Tuple(1, 0)(), Tuple(1, 1)()),
      Seq(Tuple(2, 0)(), Tuple(2, 1)())
    )
    val expected =
      (k: Int) => StmLiteral(expectedValues.slice(3 - k, 3).flatten: _*)()
    assert(ir.eval(Let(k, 0, suffix)()) == expected(0))
    assert(ir.eval(Let(k, 1, suffix)()) == expected(1))
    assert(ir.eval(Let(k, 2, suffix)()) == expected(2))
    assert(ir.eval(Let(k, 3, suffix)()) == expected(3))
  }

  test("StmSuffix:3D") {
    val s = StmMap(StmCount(2)(), TyInt ::+ (_ => StmCount2D(4, 5)()))()

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
    assert(ir.eval(StmSuffix(s, 0)().tchk()) == StmLiteral()())
    assert(
      ir.eval(StmSuffix(s, 1)().tchk())
        == StmLiteral(expected.slice(1, 2).flatten.flatten: _*)()
    )
    assert(
      ir.eval(StmSuffix(s, 2)().tchk())
        == StmLiteral(expected.slice(0, 2).flatten.flatten: _*)()
    )
  }

  test("StmShiftLeft:1D") {
    val s = StmShiftLeft(StmCount(3)(), 42)().tchk()
    assert(ir.eval(s) == StmLiteral(1, 2, 42)())
  }

  test("StmShiftLeft:2D") {
    val s0 = StmCount2D(3, 4)()
    val s1 = StmMap(StmCount(4)(), TyInt ::+ (j => Tuple(99, j)()))()
    val s = StmShiftLeft(s0, s1)().tchk()

    val expected = StmLiteral(
      Seq(
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)(), Tuple(2, 3)()),
        Seq(Tuple(99, 0)(), Tuple(99, 1)(), Tuple(99, 2)(), Tuple(99, 3)())
      ).flatten: _*
    )()
    assert(ir.eval(s) == expected)
  }

  test("StmShiftLeft:3D") {
    val s0 = StmMap(StmCount(2)(), TyInt ::+ (i => StmCst2D(4, 5, i)()))()
    val s1 = StmCst2D(4, 5, 42)()
    val actual = StmShiftLeft(s0, s1)().tchk()
    val expected = StmLiteral.ints(
      Seq(
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
      ).flatten.flatten: _*
    )
    assert(ir.eval(actual) == expected)
  }

  test("StmShiftRight:1D") {
    val s = StmShiftRight(StmCount(3)(), 42)().tchk()
    val expected = StmLiteral(42, 0, 1)()
    assert(ir.eval(s) == expected)
  }

  test("StmShiftRight:2D") {
    val s0 = StmCount2D(3, 4)()
    val s1 = StmMap(StmCount(4)(), TyInt ::+ (j => Tuple(99, j)()))()
    val s = StmShiftRight(s0, s1)().tchk()

    val expected = StmLiteral(
      Seq(
        Seq(Tuple(99, 0)(), Tuple(99, 1)(), Tuple(99, 2)(), Tuple(99, 3)()),
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)(), Tuple(0, 3)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)())
      ).flatten: _*
    )()
    assert(ir.eval(s) == expected)
  }

  test("StmShiftRight:3D") {
    val s0 = StmMap(StmCount(2)(), TyInt ::+ (i => StmCst2D(4, 5, i)()))()
    val s1 = StmCst2D(4, 5, 42)()
    val actual = StmShiftRight(s0, s1)().tchk()
    val expected = StmLiteral.ints(
      Seq(
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
      ).flatten.flatten: _*
    )
    assert(ir.eval(actual) == expected)
  }

  test("StmConcat:1D") {
    val actual1 = StmConcat(StmCst(1, 77)(), StmCst(1, 77)())().tchk()
    assert(ir.eval(actual1) == StmLiteral.ints(77, 77))
    val actual2 = StmConcat(StmCount(3)(), StmCst(4, 77)())().tchk()
    assert(ir.eval(actual2) == StmLiteral.ints(0, 1, 2, 77, 77, 77, 77))
    val actual3 = StmConcat(
      StmConcat(StmCount(3)(), StmCst(4, 77)())(),
      StmCount(2)()
    )().tchk()
    assert(ir.eval(actual3) == StmLiteral.ints(0, 1, 2, 77, 77, 77, 77, 0, 1))
  }

  test("StmConcat:2D") {
    val s0 = StmCst2D(2, 2, Tuple(99, 99)())()
    val s1 = StmCount2D(3, 2)()
    val actual = StmConcat(s0, s1)().tchk()
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
    val s0 = StmMap(StmCount(3)(), TyInt ::+ (_ => StmCst2D(2, 2, 100)()))()
    val s1 = StmMap(StmCount(2)(), TyInt ::+ (i => StmCst2D(2, 2, i)()))()
    val actual = StmConcat(s0, s1)().tchk()
    val expected = StmLiteral.ints(
      Seq(
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
      ).flatten.flatten: _*
    )
    assert(ir.eval(actual) == expected)
  }

  test("StmZip:1D") {
    val a = StmCount(4)()
    val b = {
      val b = Param("b")()
      StmBuild(4, SSome(b)(), Map[Param, (Expr, Expr)](b -> (True, Not(b)())))()
    }
    val s = StmZip(a, b)().tchk()
    val expected =
      StmLiteral(
        Tuple(0, True)(),
        Tuple(1, False)(),
        Tuple(2, True)(),
        Tuple(3, False)()
      )()
    assert(ir.eval(s) == expected)
  }

  test("StmRepeat:1D") {
    val s = StmCount(3)()
    val expected = Seq(IntCst(0), IntCst(1), IntCst(2))

    val actual0 = StmRepeat(s, 0)().tchk()
    assert(ir.eval(actual0) == StmLiteral()())

    val actual1 = StmRepeat(s, 1)().tchk()
    assert(ir.eval(actual1) == StmLiteral(expected: _*)())

    val actual2 = StmRepeat(s, 2)().tchk()
    assert(ir.eval(actual2) == StmLiteral(expected ++ expected: _*)())

    val actual3 = StmRepeat(s, 3)().tchk()
    assert(
      ir.eval(actual3) == StmLiteral(expected ++ expected ++ expected: _*)()
    )
  }

  test("StmRepeat:2D") {
    val s = StmCount2D(2, 3)()
    val expected = Seq(
      Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
      Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())
    ).flatten
    assert(ir.eval(StmRepeat(s, 0)().tchk()) == StmLiteral()())
    assert(ir.eval(StmRepeat(s, 1)().tchk()) == StmLiteral(expected: _*)())
    assert(
      ir.eval(StmRepeat(s, 2)().tchk())
        == StmLiteral(expected ++ expected: _*)()
    )
    assert(
      ir.eval(StmRepeat(s, 3)().tchk())
        == StmLiteral(expected ++ expected ++ expected: _*)()
    )
  }

  test("StmRepeat:3D") {
    val s = StmMap(StmCount(2)(), TyInt ::+ (i => StmCst2D(3, 4, i)()))()
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
    assert(ir.eval(StmRepeat(s, 0)().tchk()) == StmLiteral()())
    assert(ir.eval(StmRepeat(s, 1)().tchk()) == StmLiteral(expected: _*)())
    assert(
      ir.eval(StmRepeat(s, 2)().tchk())
        == StmLiteral(expected ++ expected: _*)()
    )
    assert(
      ir.eval(StmRepeat(s, 3)().tchk())
        == StmLiteral(expected ++ expected ++ expected: _*)()
    )
  }

  test("StmReverse:1D") {
    val s = StmReverse(StmCount(5)())().tchk()
    val expected = StmLiteral(4, 3, 2, 1, 0)()
    assert(ir.eval(s) == expected)
  }

  test("StmReverse:2D") {
    val s = StmReverse(StmCount2D(3, 3)())().tchk()
    val expected = StmLiteral(
      Tuple(2, 0)(),
      Tuple(2, 1)(),
      Tuple(2, 2)(),
      Tuple(1, 0)(),
      Tuple(1, 1)(),
      Tuple(1, 2)(),
      Tuple(0, 0)(),
      Tuple(0, 1)(),
      Tuple(0, 2)()
    )()
    assert(ir.eval(s) == expected)
  }

  test("StmSplit:1D-2D") {
    val s = StmCount(6)()

    val expected = StmLiteral(0, 1, 2, 3, 4, 5)()
    assert(ir.eval(StmSplit(s, 1)().tchk()) == expected)
    assert(ir.eval(StmSplit(s, 2)().tchk()) == expected)
    assert(ir.eval(StmSplit(s, 3)().tchk()) == expected)
    assert(ir.eval(StmSplit(s, 6)().tchk()) == expected)
  }

  test("StmJoin:2D-1D") {
    val s = StmJoin(StmCount2D(3, 2)())().tchk()
    val expected = StmLiteral(
      Tuple(0, 0)(),
      Tuple(0, 1)(),
      Tuple(1, 0)(),
      Tuple(1, 1)(),
      Tuple(2, 0)(),
      Tuple(2, 1)()
    )()
    assert(ir.eval(s) == expected)
  }

  test("StmJoin:3D-2D-1D") {
    // [[[(0, 0), (0, 1), (0, 2), (0, 3)],
    //   [(1, 0), (1, 1), (1, 2), (1, 3)],
    //   [(2, 0), (2, 1), (2, 2), (2, 3)]],
    //
    //  [[(0, 0), (0, 1), (0, 2), (0, 3)],
    //   [(1, 0), (1, 1), (1, 2), (1, 3)],
    //   [(2, 0), (2, 1), (2, 2), (2, 3)]]]
    val s = StmMap(StmCount(2)(), TyInt ::+ (_ => StmCount2D(3, 4)()))()
    val expected = StmLiteral(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)(), Tuple(0, 3)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)(), Tuple(2, 3)()),
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)(), Tuple(0, 3)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)(), Tuple(2, 3)())
      ).flatten: _*
    )()
    assert(ir.eval(StmJoin(s)().tchk()) == expected)
    assert(ir.eval(StmJoin(StmJoin(s)())().tchk()) == expected)
  }

  test("StmSplitJoin") {
    val p = Param("p")()
    val s = StmCount(6)()
    val expected = StmLiteral(0, 1, 2, 3, 4, 5)()

    assert(ir.eval(Let(p, s, StmJoin(StmSplit(p, 1)())())().tchk()) == expected)
    assert(ir.eval(Let(p, s, StmJoin(StmSplit(p, 2)())())().tchk()) == expected)
    assert(ir.eval(Let(p, s, StmJoin(StmSplit(p, 3)())())().tchk()) == expected)
    assert(ir.eval(Let(p, s, StmJoin(StmSplit(p, 6)())())().tchk()) == expected)
  }

  test("StmJoinSplit") {
    val p = Param("p")()
    val s = StmCount2D(3, 2)()
    val actual = Let(p, s, StmSplit(StmJoin(p)(), 2)())().tchk()
    val expected = StmLiteral(
      Tuple(0, 0)(),
      Tuple(0, 1)(),
      Tuple(1, 0)(),
      Tuple(1, 1)(),
      Tuple(2, 0)(),
      Tuple(2, 1)()
    )()
    assert(ir.eval(actual) == expected)
  }

  test("StmSlideV:1D:UnitWindow") {
    val actual = StmSlideV(StmCount(3)(), 1)().tchk()
    val expected =
      StmLiteral(VecLiteral(0)(), VecLiteral(1)(), VecLiteral(2)())()
    assert(ir.eval(actual) == expected)
  }

  test("StmSlideV:1D:SmallWindow") {
    val actual = StmSlideV(StmCount(4)(), 2)().tchk()
    val expected = StmLiteral(
      VecLiteral(IntCst(0), IntCst(1))(),
      VecLiteral(IntCst(1), IntCst(2))(),
      VecLiteral(IntCst(2), IntCst(3))()
    )()
    assert(ir.eval(actual) == expected)
  }

  test("StmSlideV:1D:LargestWindow") {
    val actual = StmSlideV(StmCount(5)(), 5)().tchk()
    val expected = StmLiteral(
      VecLiteral(IntCst(0), IntCst(1), IntCst(2), IntCst(3), IntCst(4))()
    )()
    assert(ir.eval(actual) == expected)
  }

  test("StmSlideS:1D:UnitWindow") {
    val actual = StmSlideS(StmCount(3)(), 1)().tchk()
    val expected = StmLiteral(0, 1, 2)()
    assert(ir.eval(actual) == expected)
  }

  test("StmSlideS:1D:SmallWindow") {
    val actual = StmSlideS(StmCount(4)(), 2)().tchk()
    val expected = StmLiteral(0, 1, 1, 2, 2, 3)()
    assert(ir.eval(actual) == expected)
  }

  test("StmSlideS:1D:SameSize") {
    val actual = StmSlideS(StmCount(5)(), 5)().tchk()
    val expected = StmLiteral(0, 1, 2, 3, 4)()
    assert(ir.eval(actual) == expected)
  }

  test("StmTranspose") {
    val s = Param("s")()
    val n = Param("n")()
    val m = Param("m")()
    val expected = StmLiteral(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(1, 0)()),
        Seq(Tuple(0, 1)(), Tuple(1, 1)())
      ).flatten: _*
    )()
    val transposed = StmTranspose(s)()

    val actual =
      Let(n, 2, Let(m, 2, Let(s, StmCount2D(2, 2)(), transposed)())())().tchk()
    assert(ir.eval(actual) == expected)
  }

  test("StmTransposeTranspose") {
    // TODO: Why is this so slow?!
    assume(false)
    val s = Param("s")()
    val actual =
      Let(s, StmCount2D(2, 2)(), StmTranspose(StmTranspose(s)())())().tchk()
    val expected = StmLiteral(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(0, 1)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)())
      ).flatten: _*
    )()
    assert(ir.eval(actual) == expected)
  }
}
