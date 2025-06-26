package mhir.sugar

import mhir.ir.Lowering.ExprLowering
import mhir.ir.TypeChecker.TypeCheck
import mhir.ir._
import org.scalatest.funsuite.AnyFunSuite

class StreamTests extends AnyFunSuite {
  test("StmCst:Int") {
    val s = StmCst(4, 3)()
    assert(mhir.ir.eval(s.tchk()) == StmLiteral(3, 3, 3, 3)())
  }

  test("StmCst:Tuple") {
    val c = Tuple(False, 99)()
    val s = StmCst(3, c)()
    assert(mhir.ir.eval(s.tchk()) == StmLiteral(c, c, c)())
  }

  test("StmCst:Stream") {
    val c = IntCst(42)()
    val s = StmCst(2, StmCst(3, c)())()
    assert(mhir.ir.eval(s.tchk()) == StmLiteral(c, c, c, c, c, c)())
  }

  test("StmCount") {
    val s = StmCount(5)().tchk()
    assert(mhir.ir.eval(s) == StmLiteral(0, 1, 2, 3, 4)())
  }

  test("StmCountFrom") {
    val s = StmRange(4, IntCst(3)(U8), IntCst(1)(U8))().tchk()
    assert(mhir.ir.eval(s) == StmLiteral(3, 4, 5, 6)())
  }

  test("StmRange(4, 3, 2)") {
    val s = StmRange(4, IntCst(3)(U8), IntCst(2)(U8))()
    assert(mhir.ir.eval(s) == StmLiteral.ints(3, 5, 7, 9))
    assert(mhir.ir.eval(s.tchk()) == StmLiteral.ints(3, 5, 7, 9))
  }

  test("StmRange(3, 2, -3)") {
    val s = StmRange(3, IntCst(2)(I8), IntCst(-3)(I8))()
    assert(mhir.ir.eval(s) == StmLiteral.ints(2, -1, -4))
    assert(mhir.ir.eval(s.tchk()) == StmLiteral.ints(2, -1, -4))
  }

  test("StmCst2D") {
    val s = StmCst2D(3, 4, 42)().tchk()
    val expected = StmLiteral(
      StmLiteral.ints(42, 42, 42, 42),
      StmLiteral.ints(42, 42, 42, 42),
      StmLiteral.ints(42, 42, 42, 42)
    )()
    assert(mhir.ir.eval(s) == expected.flatten)
  }

  test("StmCount2D") {
    val s = StmCount2D(2, 3)().tchk()
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
    assert(mhir.ir.eval(s) == expected.flatten)
  }

  test("Iterate:Square") {
    val x = Param("x")(U32)
    val n = Param("n")()
    val iter = Iterate(n, IntCst(3)(U32), Function(x, x * x)())()
    val e = (nVal: Int) => Let(n, IntCst(nVal)(U8), iter)().tchk()
    assert(mhir.ir.eval(e(0)) == StmLiteral(3)())
    assert(mhir.ir.eval(e(1)) == StmLiteral(9)())
    assert(mhir.ir.eval(e(2)) == StmLiteral(81)())
    assert(mhir.ir.eval(e(3)) == StmLiteral(81 * 81)())
  }

  test("Iterate:PlusTwo") {
    val x = Param("x")(TyTuple(I8))
    val n = Param("n")(U8)
    val iter =
      Iterate(n, Tuple(IntCst(-10)(I8))(), Function(x, Tuple(x.__0 + 2)())())()
    val e =
      (nVal: Int) => Let(n, IntCst(nVal)(U8), iter)().tchk()
    assert(mhir.ir.eval(e(0)) == StmLiteral(Tuple(-10)())())
    assert(mhir.ir.eval(e(1)) == StmLiteral(Tuple(-8)())())
    assert(mhir.ir.eval(e(2)) == StmLiteral(Tuple(-6)())())
    assert(mhir.ir.eval(e(3)) == StmLiteral(Tuple(-4)())())
  }

  test("StmMap:1D-1D:+7") {
    val s = StmMap(StmCount(IntCst(5)(U8))(), U8 ::+ (x => x + 7))().tchk()
    assert(mhir.ir.eval(s) == StmLiteral(7, 8, 9, 10, 11)())
  }

  test("StmMap:1D-1D:Identity") {
    val s = StmMap(StmCount(IntCst(3)(U8))(), U8 ::+ (x => x))()
    assert(mhir.ir.eval(s.tchk()) == StmLiteral(0, 1, 2)())
  }

  test("StmMap:1D-1D:StmSum(StmCount(i))") {
    val n = 5
    val s = StmMap(
      StmCount(IntCst(5)(U8))(),
      U8 ::+ (i => StmFold(StmCount(i + 1)(), C(0)(U8), PlusFunction(U8))())
    )().tchk()
    val expected =
      StmLiteral((0 until n).map(i => IntCst((0 to i).sum)()): _*)()
    val actual = mhir.ir.eval(s)
    assert(actual == expected)
  }

  test("StmMap:1D-1D:DiscardInputReturn42") {
    val s = StmMap(StmCount(IntCst(6)(U8))(), U8 ::+ (_ => 42))()
    assert(mhir.ir.eval(s.tchk()) == StmLiteral(42, 42, 42, 42, 42, 42)())
  }

  test("StmMap:1D-1D:SingleElementStream") {
    val s = StmMap(StmCount(IntCst(1)(U8))(), U8 ::+ (x => x + 5))()
    assert(mhir.ir.eval(s.tchk()) == StmLiteral(5)())
  }

  // The scalar input to the inner function is used only in the `nextF`.
  test("StmMap:1D-2D:StmCst") {
    val s =
      StmMap(StmCount(IntCst(4)(U8))(), U8 ::+ (c => StmCst(3, c)()))().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 0, 0),
        Seq(1, 1, 1),
        Seq(2, 2, 2),
        Seq(3, 3, 3)
      ).flatten: _*
    )
    assert(mhir.ir.eval(s) == expected)
  }

  // The scalar input to the inner function is used only in the seed.
  test("StmMap:1D-2D:StmCountFrom") {
    val s = StmMap(
      StmCount(IntCst(3)(U8))(),
      U8 ::+ (n => StmRange(4, n, IntCst(1)(U8))())
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 1, 2, 3),
        Seq(1, 2, 3, 4),
        Seq(2, 3, 4, 5)
      ).flatten: _*
    )
    assert(mhir.ir.eval(s) == expected)
  }

  // The scalar input to the inner function is used both in the `nextF` and in
  // the seed.
  test("StmMap:1D-2D:StmCstAndCountFrom") {
    val s = StmMap(
      StmCount(IntCst(3)(U8))(),
      U8 ::+ (i => {
        val acc = Param("acc")(U8)
        StmBuild(
          3,
          Tuple(i, acc)(),
          True,
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
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:1D-2D:DiscardInputReturnStmCount") {
    val s =
      StmMap(StmCount(IntCst(4)(U8))(), U8 ::+ (_ => StmCount(3)()))().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 1, 2),
        Seq(0, 1, 2),
        Seq(0, 1, 2),
        Seq(0, 1, 2)
      ).flatten: _*
    )
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:1D-2D:SingleElementStream") {
    val s = StmMap(
      StmCst(1, IntCst(99)(U8))(),
      U8 ::+ (c => StmRange(5, c, IntCst(1)(U8))())
    )().tchk()
    val expected = StmLiteral(99, 100, 101, 102, 103)()
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-1D:StmFold") {
    val s = StmMap(
      StmCount2D(C(4)(U8), C(3)(U8))(),
      TyStm((U8, U8), 3) ::+ (s =>
        StmFold(
          s,
          C(0)(U8),
          U8 ::+ (acc => (U8, U8) ::+ (x => acc + x.__0 + x.__1))
        )()
      )
    )().tchk().lower()
    val expected = StmLiteral(3, 6, 9, 12)()
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-1D:Access") {
    val s = StmMap(
      StmCount2D(C(4)(U8), C(3)(U8))(),
      TyStm((U8, U8), 3) ::+ (s => StmAccess(s, 1)())
    )().tchk()
    val expected =
      StmLiteral(Tuple(0, 1)(), Tuple(1, 1)(), Tuple(2, 1)(), Tuple(3, 1)())()
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-1D:Stm2Vec") {
    val s =
      StmMap(
        StmCount2D(C(5)(U8), C(3)(U8))(),
        TyStm((U8, U8), 3) ::+ (s => Stm2Vec(s)())
      )().tchk()
    val expected = StmLiteral(
      VecLiteral(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)())(),
      VecLiteral(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())(),
      VecLiteral(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)())(),
      VecLiteral(Tuple(3, 0)(), Tuple(3, 1)(), Tuple(3, 2)())(),
      VecLiteral(Tuple(4, 0)(), Tuple(4, 1)(), Tuple(4, 2)())()
    )()
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-1D:DiscardInputReturn42") {
    val s = StmMap(
      StmCount2D(C(3)(U8), C(4)(U8))(),
      TyStm((U8, U8), 4) ::+ (_ => StmCst(1, IntCst(42)())())
    )().tchk()
    val expected = StmLiteral(42, 42, 42)()
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-1D:SingleElementStream") {
    val s = StmMap(
      StmCount2D(C(1)(U8), C(4)(U8))(),
      TyStm((U8, U8), 4) ::+ (s =>
        StmFold(
          s,
          C(10)(U8),
          U8 ::+ (acc => (U8, U8) ::+ (x => acc + x.__1))
        )()
      )
    )().tchk()
    val expected = StmLiteral(10 + 0 + 1 + 2 + 3)()
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmMap") {
    val s = StmMap(
      StmCount2D(C(4)(U8), C(3)(U8))(),
      TyStm((U8, U8), 3) ::+ (s =>
        StmMap(s, (U8, U8) ::+ (x => x.__0 + x.__1))()
      )
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 1, 2),
        Seq(1, 2, 3),
        Seq(2, 3, 4),
        Seq(3, 4, 5)
      ).flatten: _*
    )
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmScanInclusive") {
    // [[ 0,  1,  2,  3],
    //  [10, 11, 12, 13],
    //  [20, 21, 22, 23]]
    val input = StmMap(
      StmCount(C(3)(U8))(),
      U8 ::+ (i => StmMap(StmCount(C(4)(U8))(), U8 ::+ (j => 10 * i + j))())
    )()
    val s = StmMap(
      input,
      TyStm(U8, 4) ::+ (row =>
        StmScanInclusive(row, C(0)(U8), PlusFunction(U8))()
      )
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 1, 3, 6),
        Seq(10, 21, 33, 46),
        Seq(20, 41, 63, 86)
      ).flatten: _*
    )
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmScanExclusive") {
    // [[ 0,  1,  2,  3],
    //  [10, 11, 12, 13],
    //  [20, 21, 22, 23]]
    val input = StmMap(
      StmCount(C(3)(U8))(),
      U8 ::+ (i => StmMap(StmCount(C(4)(U8))(), U8 ::+ (j => 10 * i + j))())
    )()
    val s = StmMap(
      input,
      TyStm(U8, 4) ::+ (row =>
        StmScanExclusive(row, C(0)(U8), PlusFunction(U8))()
      )
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 0, 1, 3),
        Seq(0, 10, 21, 33),
        Seq(0, 20, 41, 63)
      ).flatten: _*
    )
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmPrepend") {
    val s = StmMap(
      StmCst2D(4, 5, C(42)(U8))(),
      TyStm(U8, 5) ::+ (s => StmPrepend(s, C(43)(U8))())
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(43, 42, 42, 42, 42, 42),
        Seq(43, 42, 42, 42, 42, 42),
        Seq(43, 42, 42, 42, 42, 42),
        Seq(43, 42, 42, 42, 42, 42)
      ).flatten: _*
    )
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmAppend") {
    val s = StmMap(
      StmCst2D(4, 5, C(42)(U8))(),
      TyStm(U8, 5) ::+ (s => StmAppend(s, C(43)(U8))())
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(42, 42, 42, 42, 42, 43),
        Seq(42, 42, 42, 42, 42, 43),
        Seq(42, 42, 42, 42, 42, 43),
        Seq(42, 42, 42, 42, 42, 43)
      ).flatten: _*
    )
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmPrefix") {
    val s = StmMap(
      StmCount2D(C(3)(U8), C(100)(U8))(),
      TyStm((U8, U8), 100) ::+ (s => StmPrefix(s, 2)())
    )().tchk()
    val expected = StmLiteral(
      StmLiteral(Tuple(0, 0)(), Tuple(0, 1)())(),
      StmLiteral(Tuple(1, 0)(), Tuple(1, 1)())(),
      StmLiteral(Tuple(2, 0)(), Tuple(2, 1)())()
    )()
    assert(mhir.ir.eval(s) == expected.flatten)
  }

  test("StmMap:2D-2D:StmSuffix") {
    val s = StmMap(
      StmCount2D(C(3)(U8), C(100)(U8))(),
      TyStm((U8, U8), 100) ::+ (s => StmSuffix(s, 2)())
    )().tchk()
    val expected = StmLiteral(
      Tuple(0, 98)(),
      Tuple(0, 99)(),
      Tuple(1, 98)(),
      Tuple(1, 99)(),
      Tuple(2, 98)(),
      Tuple(2, 99)()
    )()
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmShiftLeft") {
    val s = StmMap(
      StmCount2D(C(2)(U8), C(3)(U8))(),
      TyStm((U8, U8), 3) ::+ (s =>
        StmShiftLeft(s, Tuple(C(100)(U8), C(101)(U8))())()
      )
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
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmShiftRight") {
    val s = StmMap(
      StmCount2D(C(4)(U8), C(5)(U8))(),
      TyStm((U8, U8), 5) ::+ (s =>
        StmShiftRight(s, Tuple(C(100)(U8), C(101)(U8))())()
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
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmConcatBefore") {
    val s = StmMap(
      StmCst2D(5, 5, C(99)(U8))(),
      TyStm(U8, 5) ::+ (s => StmConcat(StmCount(C(3)(U8))(), s)())
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
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmConcatAfter") {
    val s = StmMap(
      StmCst2D(5, 5, C(99)(U8))(),
      TyStm(U8, 5) ::+ (s => StmConcat(s, StmCount(C(3)(U8))())())
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
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmZipWithIndexBefore") {
    val s = StmMap(
      StmCount2D(C(3)(U8), C(3)(U8))(),
      TyStm((U8, U8), 3) ::+ (s => StmZip(StmCount(3)(), s)())
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
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmZipWithIndexAfter") {
    val s = StmMap(
      StmCount2D(C(3)(U8), C(3)(U8))(),
      TyStm((U8, U8), 3) ::+ (s => StmZip(s, StmCount(3)())())
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
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmRepeat") {
    val s = StmMap(
      StmCount2D(C(4)(U8), C(4)(U8))(),
      TyStm((U8, U8), 4) ::+ (s => StmRepeat(s, 3)())
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
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:Identity") {
    val s = StmMap(
      StmCount2D(C(2)(U8), C(3)(U8))(),
      TyStm((U8, U8), 3) ::+ (s => s)
    )().tchk()
    val expected = StmLiteral(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())
      ).flatten: _*
    )()
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:DiscardInputReturnStmCount") {
    val s = StmMap(
      StmCount2D(C(4)(U8), C(3)(U8))(),
      TyStm((U8, U8), 3) ::+ (_ => StmCount(5)())
    )().tchk().lower()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 1, 2, 3, 4),
        Seq(0, 1, 2, 3, 4),
        Seq(0, 1, 2, 3, 4),
        Seq(0, 1, 2, 3, 4)
      ).flatten: _*
    )
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmSlideV") {
    val s = StmMap(
      StmCount2D(C(3)(U8), C(5)(U8))(),
      TyStm((U8, U8), 5) ::+ (s => StmSlideV(s, 3)())
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
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:SingleElementOuterStream") {
    val s = StmMap(
      StmCount2D(C(1)(U8), C(3)(U8))(),
      TyStm((U8, U8), 3) ::+ (s =>
        StmMap(s, (U8, U8) ::+ (x => x.__0 + x.__1))()
      )
    )().tchk()
    val expected = StmLiteral(0, 1, 2)()
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-2D:SingleElementInnerStream") {
    val s = StmMap(
      StmCount2D(C(4)(U8), C(1)(U8))(),
      TyStm((U8, U8), 1) ::+ (s =>
        StmMap(s, (U8, U8) ::+ (x => x.__0 + x.__1))()
      )
    )().tchk()
    val expected = StmLiteral(0, 1, 2, 3)()
    assert(mhir.ir.eval(s) == expected)
  }

  /** The lowering pass for StmMap may elide the input counter if the number of
    * inputs per "row" is one. But what if the inner stream somehow first
    * produces all its outputs and then reads the input? (This is probably not
    * useful, but the result should at least be correct).
    */
  test("StmMap:2D-2D:ReadInputAfterOutputs") {
    val n = 4
    // m needs to be 1, otherwise it defeats the purpose of this test
    val m = 1
    val k = 3
    val input = StmCount2D(C(n)(U8), C(m)(U8))()
    val actual =
      StmMap(
        input,
        TyStm((U8, U8), m) ::+ (sIn => {
          val i = Param("i")(U8)
          val s = Param("s")(TyStm((U8, U8), -1))
          StmBuild(
            k,
            i,
            i < k,
            Map[Param, (Expr, Expr)](
              i -> (C(0)(U8), i + 1),
              s -> (sIn, i === k)
            )
          )()
        })
      )().tchk()
    val expected =
      StmLiteral(
        (0 until n).flatMap(_ => (0 until k).map(j => IntCst(j)())): _*
      )()
    assert(mhir.ir.eval(actual) == expected)
  }

  test("StmMap:1D-3D:MapCount") {
    val s = StmMap(
      StmCount(C(2)(U8))(),
      U8 ::+ (i =>
        StmMap(
          StmCount(C(3)(U8))(),
          U8 ::+ (j =>
            StmMap(StmCount(C(4)(U8))(), U8 ::+ (k => Tuple(i, j, k)()))()
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
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmMap:2D-3D:StmSlideS") {
    val s = StmCount2D(C(3)(U8), C(5)(U8))()
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
      StmMap(s, TyStm((U8, U8), 5) ::+ (s => StmSlideS(s, 3)()))().tchk()
    assert(mhir.ir.eval(actual) == expected)
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
    val s = StmMap(
      StmCount(C(2)(U8))(),
      U8 ::+ (_ => StmCount2D(C(3)(U8), C(4)(U8))())
    )()
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
      StmMap(s, TyStm(TyStm((U8, U8), 4), 3) ::+ (s => StmTranspose(s)()))()
        .tchk()
    assert(mhir.ir.eval(actual) == expected)
  }

  test("StmMap:RepeatedData1") {
    val n = 3
    val m = 4
    val f =
      (TyStm(TyStm(U8, m), n) ::+ (a =>
        TyStm(I32, m) ::+ (b =>
          StmMap(a, TyStm(U8, m) ::+ (rowA => StmZip(rowA, b)()))()
        )
      )).tchk().lower()
    val a = StmMap(
      StmCount2D(C(n)(U8), C(m)(U8))(),
      TyStm((U8, U8), m) ::+ (s =>
        StmMap(s, (U8, U8) ::+ (x => x.__0 + 2 * x.__1))()
      )
    )().tchk().lower()
    val b = StmRange(m, C(-1)(I32), C(2)(I32))().tchk().lower()
    val expected = {
      val aVals = (0 until n).map(i => (0 until m).map(j => i + 2 * j))
      val bVals = (0 until m).map(t => -1 + 2 * t)
      val expectedVals = aVals.map(rowA => rowA.zip(bVals))
      StmLiteral(
        expectedVals.flatten.map({ case (x, y) => Tuple(x, y)() }): _*
      )()
    }
    val actual = mhir.ir.eval(FunCall(FunCall(f, a)(), b)())
    assert(actual == expected)
  }

  test("StmMap:RepeatedData2") {
    val n = 2
    val m = 3
    val k = 4
    val a = Param("a")(TyStm(TyStm((U8, U8), k), n))
    val b = Param("b")(TyStm(TyStm((U8, U8), k), m))
    val map = {
      val rowA = Param("row_a")(TyStm((U8, U8), k))
      val rowB = Param("row_b")(TyStm((U8, U8), k))
      val map = StmMap(
        a,
        Function(rowA, StmMap(b, Function(rowB, StmZip(rowA, rowB)())())())()
      )()
      map.tchk().lower()
    }
    val subs: Map[Expr, Expr] = Map(
      a -> StmCount2D(C(n)(U8), C(k)(U8))().tchk().lower(),
      b -> StmCount2D(C(m)(U8), C(k)(U8))().tchk().lower()
    )
    val expected = {
      val aVals = (0 until n).map(i => (0 until k).map(j => Tuple(i, j)()))
      val bVals = (0 until m).map(i => (0 until k).map(j => Tuple(i, j)()))
      val expectedVals = aVals.map(rowA => bVals.map(rowB => rowA.zip(rowB)))
      StmLiteral(
        expectedVals.flatten.flatten.map({ case (x, y) => Tuple(x, y)() }): _*
      )()
    }
    val actual = mhir.ir.eval(map.subPreserveType(subs))
    assert(actual == expected)
  }

  test("StmFold:RepeatedData") {
    val n = 4
    val f =
      (TyStm(TyStm(U16, n), n) ::+ (a =>
        TyStm(U16, n) ::+ (b =>
          StmMap(
            a,
            TyStm(U16, n) ::+ (rowA => {
              val zipped = StmZip(rowA, b)()
              val products =
                StmMap(zipped, (U16, U16) ::+ (x => x.__0 * x.__1))()
              val dotProd = StmFold(products, C(0)(U16), PlusFunction(U16))()
              dotProd
            })
          )()
        )
      )).tchk().lower()
    val a = StmMap(
      StmCount2D(C(n)(U16), C(n)(U16))(),
      TyStm((U16, U16), n) ::+ (s =>
        StmMap(s, (U16, U16) ::+ (x => x.__0 + 2 * x.__1))()
      )
    )().tchk().lower()
    val b = StmRange(n, C(10)(U16), C(3)(U16))().tchk().lower()
    val expected = {
      val aVals = (0 until n).map(i => (0 until n).map(j => i + 2 * j))
      val bVals = (0 until n).map(t => 10 + 3 * t)
      val expectedVals =
        aVals.map(rowA => rowA.zip(bVals).map({ case (x, y) => x * y }).sum)
      StmLiteral.ints(expectedVals: _*)
    }
    val actual = mhir.ir.eval(FunCall(FunCall(f, a)(), b)())
    assert(actual == expected)
  }

  test("StmAccess:1D") {
    val n = 3
    val i = Param("i")(U8)
    val s = StmRange(n, C(5)(U8), C(2)(U8))()
    val access = StmAccess(s, i)().tchk().lower()

    assert(mhir.ir.eval(Let(i, C(0)(U8), access)()) == StmLiteral(5)())
    assert(mhir.ir.eval(Let(i, C(1)(U8), access)()) == StmLiteral(7)())
    assert(mhir.ir.eval(Let(i, C(2)(U8), access)()) == StmLiteral(9)())
  }

  test("StmAccess:2D") {
    val s = StmCount2D(4, 5)()
    val i = Param("i")(U8)
    val access = StmAccess(s, i)().tchk().lower()

    val row = (i: Int) => StmLiteral((0 until 5).map(j => Tuple(i, j)()): _*)()

    assert(mhir.ir.eval(Let(i, C(0)(U8), access)()) == row(0))
    assert(mhir.ir.eval(Let(i, C(1)(U8), access)()) == row(1))
    assert(mhir.ir.eval(Let(i, C(2)(U8), access)()) == row(2))
    assert(mhir.ir.eval(Let(i, C(3)(U8), access)()) == row(3))
  }

  test("StmAccess:3D") {
    val s = StmMap(
      StmCount(C(3)(U8))(),
      U8 ::+ (i =>
        StmMap(
          StmCount(C(3)(U8))(),
          U8 ::+ (j =>
            StmMap(StmCount(C(4)(U8))(), U8 ::+ (k => Tuple(i, j, k)()))()
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
      mhir.ir.eval(StmAccess(s, 0)().tchk())
        == StmLiteral(expected.head.flatten: _*)()
    )
    assert(
      mhir.ir.eval(StmAccess(s, 1)().tchk())
        == StmLiteral(expected(1).flatten: _*)()
    )
    assert(
      mhir.ir.eval(StmAccess(s, 2)().tchk())
        == StmLiteral(expected(2).flatten: _*)()
    )
  }

  test("StmFold:1D:Sum") {
    val sum =
      StmFold(StmCount(C(6)(U8))(), C(3)(U8), PlusFunction(U8))()
        .tchk()
    assert(mhir.ir.eval(sum) == StmLiteral(18)())
  }

  test("StmFold:1D:Product") {
    val prod =
      StmFold(StmRange(5, C(1)(U8), C(1)(U8))(), C(1)(U8), TimesFunction(U8))()
        .tchk()
    assert(mhir.ir.eval(prod) == StmLiteral(120)())
  }

  test("StmFold:1D:HornerMethod") {
    // Non-commutative, non-associative update function
    // Evaluate y = 2x^4 + 3x^3 + 4x^2 + 5x + 6 at x = 2
    val coefficients = StmRange(5, C(2)(U8), C(1)(U8))()
    val x = 2
    val y = StmFold(
      coefficients,
      C(0)(U8),
      U8 ::+ (acc => U8 ::+ (a => a + x * acc))
    )().tchk()
    assert(mhir.ir.eval(y) == StmLiteral(88)())
  }

  test("StmFold:1D:DiscardInputAdd42") {
    val x = StmFold(
      StmCount(C(5)(U8))(),
      C(2)(U8),
      U8 ::+ (acc => U8 ::+ (_ => acc + 42))
    )().tchk()
    assert(mhir.ir.eval(x) == StmLiteral(2 + 5 * 42)())
  }

  test("StmFold:2D:SumWide") {
    // [[0, 1, 2, 3, 4],
    //  [1, 2, 3, 4, 5],
    //  [2, 3, 4, 5, 6]]
    val s = StmMap(
      StmCount(C(3)(U8))(),
      U8 ::+ (i => StmMap(StmCount(C(5)(U8))(), U8 ::+ (j => i + j))())
    )()
    val sum = StmFold(
      s,
      C(0)(U8),
      U8 ::+ (acc =>
        TyStm(U8, 5) ::+ (s =>
          StmMap(
            StmFold(s, C(0)(U8), PlusFunction(U8))(),
            U8 ::+ (x => acc + x)
          )()
        )
      )
    )().tchk()
    assert(mhir.ir.eval(sum) == StmLiteral(45)())
  }

  test("StmFold:2D:SumNarrow") {
    // [[0, 1],
    //  [1, 2],
    //  [2, 3],
    //  [3, 4]]
    val s = StmMap(
      StmCount(C(4)(U8))(),
      U8 ::+ (i => StmMap(StmCount(C(2)(U8))(), U8 ::+ (j => i + j))())
    )()
    val sum = StmFold(
      s,
      C(0)(U8),
      U8 ::+ (acc =>
        TyStm(U8, 2) ::+ (s =>
          StmMap(
            StmFold(s, C(0)(U8), PlusFunction(U8))(),
            U8 ::+ (x => acc + x)
          )()
        )
      )
    )().tchk()
    assert(mhir.ir.eval(sum) == StmLiteral(16)())
  }

  test("StmFold:2D:SumColumn") {
    // [[1, 2, 3, 4],
    //  [2, 3, 4, 5],
    //  [3, 4, 5, 6],
    //  [4, 5, 6, 7]]
    val s = StmMap(
      StmCount(C(4)(U8))(),
      U8 ::+ (i => StmMap(StmCount(C(4)(U8))(), U8 ::+ (j => i + j + 1))())
    )()
    val x = StmFold(
      s,
      C(13)(U8),
      U8 ::+ (acc =>
        TyStm(U8, 4) ::+ (s =>
          StmMap(StmAccess(s, 1)(), U8 ::+ (x => acc + x))()
        )
      )
    )().tchk()
    assert(mhir.ir.eval(x) == StmLiteral(13 + 2 + 3 + 4 + 5)())
  }

  test("StmFold:2D:Product") {
    // [[1, 2, 3],
    //  [2, 3, 4],
    //  [3, 4, 5]]
    val s = StmMap(
      StmCount(C(3)(U8))(),
      U8 ::+ (i => StmMap(StmCount(C(3)(U8))(), U8 ::+ (j => i + j + 1))())
    )()
    val prod = StmFold(
      s,
      C(1)(U16),
      U16 ::+ (acc =>
        TyStm(U8, 3) ::+ (s =>
          StmMap(
            StmFold(
              s,
              C(1)(U16),
              U16 ::+ (acc => U8 ::+ (x => acc * x))
            )(),
            U16 ::+ (x => acc * x)
          )()
        )
      )
    )().tchk()
    assert(mhir.ir.eval(prod) == StmLiteral(8640)())
  }

  test("StmFold:2D:DiscardInputAdd42") {
    // [[0, 1, 2, 3, 4],
    //  [1, 2, 3, 4, 5],
    //  [2, 3, 4, 5, 6]]
    val s = StmMap(
      StmCount(C(3)(U8))(),
      U8 ::+ (i => StmMap(StmCount(C(5)(U8))(), U8 ::+ (j => i + j))())
    )()
    val sum = StmFold(
      s,
      C(0)(U8),
      U8 ::+ (acc => TyStm(U8, 5) ::+ (_ => StmCst(1, acc + 42)()))
    )().tchk()
    assert(mhir.ir.eval(sum) == StmLiteral(3 * 42)())
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
      StmCount(C(3)(U8))(),
      U8 ::+ (i =>
        StmMap(
          StmCount(C(2)(U8))(),
          U8 ::+ (j => StmMap(StmCount(C(4)(U8))(), U8 ::+ (k => i + j + k))())
        )()
      )
    )()
    val sum = StmFold(
      s,
      C(0)(U8),
      U8 ::+ (acc =>
        TyStm(TyStm(U8, 4), 2) ::+ (s =>
          StmMap(
            StmFold(
              s,
              C(0)(U8),
              U8 ::+ (acc =>
                TyStm(U8, 4) ::+ (s =>
                  StmMap(
                    StmFold(s, C(0)(U8), PlusFunction(U8))(),
                    U8 ::+ (x => acc + x)
                  )()
                )
              )
            )(),
            U8 ::+ (x => acc + x)
          )()
        )
      )
    )().tchk()
    assert(mhir.ir.eval(sum) == StmLiteral(72)())
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
      StmCount(C(3)(U8))(),
      U8 ::+ (i =>
        StmMap(
          StmCount(C(2)(U8))(),
          U8 ::+ (j =>
            StmMap(StmCount(C(2)(U8))(), U8 ::+ (k => i + j + k + 1))()
          )
        )()
      )
    )()
    val prod = StmFold(
      s,
      C(1)(U32),
      U32 ::+ (acc =>
        TyStm(TyStm(U8, 2), 2) ::+ (s =>
          StmMap(
            StmFold(
              s,
              C(1)(U32),
              U32 ::+ (acc =>
                TyStm(U8, 2) ::+ (s =>
                  StmMap(
                    StmFold(
                      s,
                      C(1)(U32),
                      U32 ::+ (a => U8 ::+ (x => a * x))
                    )(),
                    U32 ::+ (x => acc * x)
                  )()
                )
              )
            )(),
            U32 ::+ (x => acc * x)
          )()
        )
      )
    )().tchk()
    assert(mhir.ir.eval(prod) == StmLiteral(IntCst(207360)())())
  }

  test("StmScanInclusive:1D:Sum") {
    // [2, 3,  4,  5,  6]
    val s = StmMap(StmCount(C(5)(U8))(), U8 ::+ (x => x + 2))()
    // [2, 7, 18, 41, 88]
    val sum =
      StmScanInclusive(s, C(0)(U8), U8 ::+ (acc => U8 ::+ (x => x + 2 * acc)))()
        .tchk()
    val expected = StmLiteral(2, 7, 18, 41, 88)()
    assert(mhir.ir.eval(sum) == expected)
  }

  test("StmScanInclusive:2D:SumRowSums") {
    // [[0, 1],
    //  [2, 3],
    //  [4, 5],
    //  [6, 7]]
    val s = StmSplit(StmCount(C(8)(U8))(), 2)()
    val sums = StmScanInclusive(
      s,
      C(0)(U8),
      U8 ::+ (acc =>
        TyStm(U8, 2) ::+ (s =>
          StmMap(
            StmFold(s, C(0)(U8), PlusFunction(U8))(),
            U8 ::+ (x => acc + x)
          )()
        )
      )
    )().tchk()
    // scan([1, 5, 9, 13])
    val expected = StmLiteral(1, 6, 15, 28)()
    assert(mhir.ir.eval(sums) == expected)
  }

  test("StmScanInclusive:2D:SumColumn1") {
    // [[1, 2, 3, 4],
    //  [2, 3, 4, 5],
    //  [3, 4, 5, 6],
    //  [4, 5, 6, 7]]
    val s = StmMap(
      StmCount(C(4)(U8))(),
      U8 ::+ (i => StmMap(StmCount(C(4)(U8))(), U8 ::+ (j => i + j + 1))())
    )()
    val sums = StmScanInclusive(
      s,
      C(0)(U8),
      U8 ::+ (acc =>
        TyStm(U8, 4) ::+ (s =>
          StmMap(StmAccess(s, 1)(), U8 ::+ (x => acc + x))()
        )
      )
    )().tchk()
    // scan([2, 3, 4, 5])
    val expected = StmLiteral(2, 5, 9, 14)()
    assert(mhir.ir.eval(sums) == expected)
  }

  test("StmScanExclusive:1D:Sum") {
    // [2, 3, 4,  5,  6]
    val s = StmMap(StmCount(C(5)(U8))(), U8 ::+ (x => x + 2))()
    // [0, 2, 7, 18, 41]
    val sum =
      StmScanExclusive(
        s,
        C(0)(U8),
        U8 ::+ (acc => U8 ::+ (x => x + 2 * acc))
      )().tchk()
    assert(mhir.ir.eval(sum) == StmLiteral(0, 2, 7, 18, 41)())
  }

  test("StmScanExclusive:2D:SumRowSums") {
    // [[0, 1],
    //  [1, 2],
    //  [2, 3],
    //  [3, 4]]
    val s = StmMap(
      StmCount(C(4)(U8))(),
      U8 ::+ (i => StmMap(StmCount(C(2)(U8))(), U8 ::+ (j => i + j))())
    )()
    val sums = StmScanExclusive(
      s,
      C(0)(U8),
      U8 ::+ (acc =>
        TyStm(U8, 2) ::+ (s =>
          StmMap(
            StmFold(s, C(0)(U8), PlusFunction(U8))(),
            U8 ::+ (x => acc + x)
          )()
        )
      )
    )().tchk()
    // scan([1, 3, 5, 7])
    val expected = StmLiteral(0, 1, 4, 9)()
    assert(mhir.ir.eval(sums) == expected)
  }

  test("StmScanExclusive:2D:SumColumn1") {
    // [[1, 2, 3, 4],
    //  [2, 3, 4, 5],
    //  [3, 4, 5, 6],
    //  [4, 5, 6, 7]]
    val s = StmMap(
      StmCount(C(4)(U8))(),
      U8 ::+ (i => StmMap(StmCount(C(4)(U8))(), U8 ::+ (j => i + j + 1))())
    )()
    val sums = StmScanExclusive(
      s,
      C(0)(U8),
      U8 ::+ (acc =>
        TyStm(U8, 4) ::+ (s =>
          StmMap(StmAccess(s, 1)(), U8 ::+ (x => acc + x))()
        )
      )
    )()
    // scan([2, 3, 4, 5])
    val expected = StmLiteral(0, 2, 5, 9)()
    assert(mhir.ir.eval(sums) == expected)
  }

  test("Vec2Stm:1D") {
    val v = VecBuild(5, U32 ::+ (i => i + 1))()
    assert(mhir.ir.eval(Vec2Stm(v)().tchk()) == StmLiteral.ints(1, 2, 3, 4, 5))
  }

  test("Vec2Stm:2D") {
    val v =
      VecBuild(
        C(4)(U32),
        U32 ::+ (i => VecBuild(C(3)(U32), U32 ::+ (j => Tuple(i, j)()))())
      )()
    val s = StmMap(
      Vec2Stm(v)(),
      TyVec((U32, U32), C(3)(U32)) ::+ (v => Vec2Stm(v)())
    )().tchk()

    val expected = StmLiteral(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)()),
        Seq(Tuple(3, 0)(), Tuple(3, 1)(), Tuple(3, 2)())
      ).flatten: _*
    )()
    assert(mhir.ir.eval(s) == expected)
  }

  test("Vec2Stm:Vec[Stm[Int]]") {
    val n = 4
    val m = 3
    val vs =
      VecBuild(C(n)(U32), U32 ::+ (i => StmRange(C(m)(U32), C(42)(U32), i)()))()
    val e = Vec2Stm(vs)().tchk().lower()
    val expected =
      StmLiteral(
        (0 until m).flatMap(i => (0 until n).map(t => IntCst(42 + i * t)())): _*
      )()
    val actual = mhir.ir.eval(e)
    assert(actual == expected)
  }

  test("Vec2Stm2Vec") {
    val p = Param("p")()
    val actual = Stm2Vec(Vec2Stm(p)())()

    val v0 = VecBuild(6, U32 ::+ (i => i + 2))()
    val expected0 = StmLiteral(VecLiteral(2, 3, 4, 5, 6, 7)())()
    assert(mhir.ir.eval(Let(p, v0, actual)().tchk()) == expected0)

    val v1 = VecBuild(6, U32 ::+ (i => i * i))()
    val expected1 = StmLiteral(VecLiteral(0, 1, 4, 9, 16, 25)())()
    assert(mhir.ir.eval(Let(p, v1, actual)().tchk()) == expected1)
  }

  test("Stm2Vec2Stm") {
    val s = Param("s")(TyStm(U32, C(6)(U8)))
    val actual =
      StmMap(Stm2Vec(s)(), TyVec(U32, C(6)(U8)) ::+ (v => Vec2Stm(v)()))()

    val s0 = StmCount(C(6)(U32))()
    val expected0 = StmLiteral(0, 1, 2, 3, 4, 5)()
    assert(mhir.ir.eval(Let(s, s0, actual)().tchk()) == expected0)

    val s1 = StmCst(6, C(42)(U32))()
    val expected1 = StmLiteral(42, 42, 42, 42, 42, 42)()
    assert(mhir.ir.eval(Let(s, s1, actual)().tchk()) == expected1)
  }

  test("StmConcat:1D") {
    val u7 = TyUInt(7)
    val s1 = StmCst(4, C(-77)(I8))()
    val s2 = StmMap(StmCount(C(2)(u7))(), u7 ::+ (x => ReshapeData(x, I8)()))()
    val s3 = StmMap(StmCount(C(3)(u7))(), u7 ::+ (x => ReshapeData(x, I8)()))()

    val actual1 = StmConcat(s1, s1)().tchk().lower()
    val expected1 = StmLiteral((0 until 8).map(_ => C(-77)(I8)): _*)()
    assert(mhir.ir.eval(actual1) == expected1)

    val actual2 = StmConcat(s3, s1)().tchk()
    val expected2 = StmLiteral(0, 1, 2, -77, -77, -77, -77)()
    assert(mhir.ir.eval(actual2) == expected2)

    val actual3 = StmConcat(StmConcat(s3, s1)(), s2)().tchk()
    val expected3 = StmLiteral(0, 1, 2, -77, -77, -77, -77, 0, 1)()
    assert(mhir.ir.eval(actual3) == expected3)
  }

  test("StmConcat:2D") {
    val s0 = StmCst2D(2, 2, Tuple(C(99)(U8), C(99)(U8))())()
    val s1 = StmCount2D(C(3)(U8), C(2)(U8))()
    val actual = StmConcat(s0, s1)().tchk()
    val expected = Seq(
      Seq(Tuple(99, 99)(), Tuple(99, 99)()),
      Seq(Tuple(99, 99)(), Tuple(99, 99)()),
      Seq(Tuple(0, 0)(), Tuple(0, 1)()),
      Seq(Tuple(1, 0)(), Tuple(1, 1)()),
      Seq(Tuple(2, 0)(), Tuple(2, 1)())
    )
    assert(mhir.ir.eval(actual) == StmLiteral(expected.flatten: _*)())
  }

  test("StmConcat:3D") {
    val s0 =
      StmMap(StmCount(C(3)(U8))(), U8 ::+ (_ => StmCst2D(2, 2, C(100)(U8))()))()
    val s1 = StmMap(StmCount(C(2)(U8))(), U8 ::+ (i => StmCst2D(2, 2, i)()))()
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
    assert(mhir.ir.eval(actual) == expected)
  }

  test("StmPrepend:1D") {
    val s = StmCount(C(3)(U8))()
    val actual = StmPrepend(s, C(42)(U8))().tchk()
    assert(mhir.ir.eval(actual) == StmLiteral(42, 0, 1, 2)())
  }

  test("StmPrepend:2D") {
    val s0 = StmCount2D(C(3)(U8), C(3)(U8))()
    val s1 = StmCst(3, Tuple(C(42)(U8), C(42)(U8))())()

    val actual = StmPrepend(s0, s1)().tchk().lower()
    val expected = StmLiteral(
      Seq(
        Seq(Tuple(42, 42)(), Tuple(42, 42)(), Tuple(42, 42)()),
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)())
      ).flatten: _*
    )()
    assert(mhir.ir.eval(actual) == expected)
  }

  test("StmPrepend:3D") {
    val s0 =
      StmMap(StmCount(C(2)(U8))(), U8 ::+ (_ => StmCst2D(2, 2, C(42)(U8))()))()
    val s1 = StmCst2D(2, 2, C(99)(U8))()
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
    assert(mhir.ir.eval(actual) == expected)
  }

  test("StmAppend:1D") {
    val s = StmCount(C(3)(U8))()
    val actual = StmAppend(s, C(42)(U8))().tchk()
    assert(mhir.ir.eval(actual) == StmLiteral(0, 1, 2, 42)())
  }

  test("StmAppend:2D") {
    val s0 = StmCount2D(C(3)(U8), C(3)(U8))()
    val s1 = StmCst(3, Tuple(C(42)(U8), C(42)(U8))())()

    val actual = StmAppend(s0, s1)().tchk()
    val expected = StmLiteral(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)()),
        Seq(Tuple(42, 42)(), Tuple(42, 42)(), Tuple(42, 42)())
      ).flatten: _*
    )()
    assert(mhir.ir.eval(actual) == expected)
  }

  test("StmAppend:3D") {
    val s0 =
      StmMap(StmCount(C(2)(U8))(), U8 ::+ (_ => StmCst2D(2, 2, C(42)(U8))()))()
    val s1 = StmCst2D(2, 2, C(99)(U8))()
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
    assert(mhir.ir.eval(actual) == expected)
  }

  test("StmPrefix:1D") {
    val s = StmCount(3)()
    val k = Param("k")(U8)
    val prefix = StmPrefix(s, k)().tchk()

    assert(mhir.ir.eval(Let(k, C(0)(U8), prefix)()) == StmLiteral()())
    assert(mhir.ir.eval(Let(k, C(1)(U8), prefix)()) == StmLiteral(0)())
    assert(mhir.ir.eval(Let(k, C(2)(U8), prefix)()) == StmLiteral(0, 1)())
    assert(mhir.ir.eval(Let(k, C(3)(U8), prefix)()) == StmLiteral(0, 1, 2)())
  }

  test("StmPrefix:2D") {
    val s = StmCount2D(C(2)(U8), C(3)(U8))()
    val k = Param("k")(U8)
    val prefix = StmPrefix(s, k)().tchk()

    val expectedValues = Seq(
      Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
      Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())
    )
    val expected =
      (k: Int) => StmLiteral(expectedValues.slice(0, k).flatten: _*)()
    assert(mhir.ir.eval(Let(k, C(0)(U8), prefix)()) == expected(0))
    assert(mhir.ir.eval(Let(k, C(1)(U8), prefix)()) == expected(1))
    assert(mhir.ir.eval(Let(k, C(2)(U8), prefix)()) == expected(2))
  }

  test("StmPrefix:3D") {
    val s = StmMap(StmCount(C(2)(U8))(), U8 ::+ (_ => StmCount2D(4, 5)()))()

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
    assert(mhir.ir.eval(StmPrefix(s, 0)().tchk()) == StmLiteral()())
    assert(
      mhir.ir.eval(StmPrefix(s, 1)().tchk())
        == StmLiteral(expected.slice(0, 1).flatten.flatten: _*)()
    )
    assert(
      mhir.ir.eval(StmPrefix(s, 2)().tchk())
        == StmLiteral(expected.slice(0, 2).flatten.flatten: _*)()
    )
  }

  test("StmSuffix:1D") {
    val s = StmCount(3)()
    val k = Param("k")(U8)
    val suffix = StmSuffix(s, k)().tchk()
    assert(mhir.ir.eval(Let(k, C(0)(U8), suffix)()) == StmLiteral()())
    assert(mhir.ir.eval(Let(k, C(1)(U8), suffix)()) == StmLiteral(2)())
    assert(mhir.ir.eval(Let(k, C(2)(U8), suffix)()) == StmLiteral(1, 2)())
    assert(mhir.ir.eval(Let(k, C(3)(U8), suffix)()) == StmLiteral(0, 1, 2)())
  }

  test("StmSuffix:2D") {
    val s = StmCount2D(C(3)(U8), C(2)(U8))()
    val k = Param("k")(U8)
    val suffix = StmSuffix(s, k)().tchk()

    val expectedValues = Seq(
      Seq(Tuple(0, 0)(), Tuple(0, 1)()),
      Seq(Tuple(1, 0)(), Tuple(1, 1)()),
      Seq(Tuple(2, 0)(), Tuple(2, 1)())
    )
    val expected =
      (k: Int) => StmLiteral(expectedValues.slice(3 - k, 3).flatten: _*)()
    assert(mhir.ir.eval(Let(k, C(0)(U8), suffix)()) == expected(0))
    assert(mhir.ir.eval(Let(k, C(1)(U8), suffix)()) == expected(1))
    assert(mhir.ir.eval(Let(k, C(2)(U8), suffix)()) == expected(2))
    assert(mhir.ir.eval(Let(k, C(3)(U8), suffix)()) == expected(3))
  }

  test("StmSuffix:3D") {
    val s = StmMap(StmCount(C(2)(U8))(), U8 ::+ (_ => StmCount2D(4, 5)()))()

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
    assert(mhir.ir.eval(StmSuffix(s, 0)().tchk()) == StmLiteral()())
    assert(
      mhir.ir.eval(StmSuffix(s, 1)().tchk())
        == StmLiteral(expected.slice(1, 2).flatten.flatten: _*)()
    )
    assert(
      mhir.ir.eval(StmSuffix(s, 2)().tchk())
        == StmLiteral(expected.slice(0, 2).flatten.flatten: _*)()
    )
  }

  test("StmShiftLeft:1D") {
    val s = StmShiftLeft(StmCount(C(3)(U8))(), C(42)(U8))().tchk()
    assert(mhir.ir.eval(s) == StmLiteral(1, 2, 42)())
  }

  test("StmShiftLeft:2D") {
    val s0 = StmCount2D(C(3)(U8), C(4)(U8))()
    val s1 = StmMap(StmCount(C(4)(U8))(), U8 ::+ (j => Tuple(C(99)(U8), j)()))()
    val s = StmShiftLeft(s0, s1)().tchk()

    val expected = StmLiteral(
      Seq(
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)(), Tuple(2, 3)()),
        Seq(Tuple(99, 0)(), Tuple(99, 1)(), Tuple(99, 2)(), Tuple(99, 3)())
      ).flatten: _*
    )()
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmShiftLeft:3D") {
    val s0 = StmMap(StmCount(C(2)(U8))(), U8 ::+ (i => StmCst2D(4, 5, i)()))()
    val s1 = StmCst2D(4, 5, C(42)(U8))()
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
    assert(mhir.ir.eval(actual) == expected)
  }

  test("StmShiftRight:1D") {
    val s = StmShiftRight(StmCount(C(3)(U8))(), C(42)(U8))().tchk()
    val expected = StmLiteral(42, 0, 1)()
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmShiftRight:2D") {
    val s0 = StmCount2D(C(3)(U8), C(4)(U8))()
    val s1 = StmMap(StmCount(C(4)(U8))(), U8 ::+ (j => Tuple(C(99)(U8), j)()))()
    val s = StmShiftRight(s0, s1)().tchk()

    val expected = StmLiteral(
      Seq(
        Seq(Tuple(99, 0)(), Tuple(99, 1)(), Tuple(99, 2)(), Tuple(99, 3)()),
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)(), Tuple(0, 3)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)())
      ).flatten: _*
    )()
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmShiftRight:3D") {
    val s0 = StmMap(StmCount(C(2)(U8))(), U8 ::+ (i => StmCst2D(4, 5, i)()))()
    val s1 = StmCst2D(4, 5, C(42)(U8))()
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
    assert(mhir.ir.eval(actual) == expected)
  }

  test("StmZip:1D") {
    val a = StmCount(4)()
    val b = {
      val b = Param("b")(TyBool)
      StmBuild(4, b, True, Map[Param, (Expr, Expr)](b -> (True, !b)))()
    }
    val s = StmZip(a, b)().tchk()
    val expected =
      StmLiteral(
        Tuple(0, True)(),
        Tuple(1, False)(),
        Tuple(2, True)(),
        Tuple(3, False)()
      )()
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmRepeat:1D") {
    val s = StmCount(C(3)(U8))()
    val expected = Seq(IntCst(0)(), IntCst(1)(), IntCst(2)())

    val actual0 = StmRepeat(s, 0)().tchk().lower()
    assert(mhir.ir.eval(actual0) == StmLiteral()())

    val actual1 = StmRepeat(s, 1)().tchk().lower()
    assert(mhir.ir.eval(actual1) == StmLiteral(expected: _*)())

    val actual2 = StmRepeat(s, 2)().tchk().lower()
    assert(mhir.ir.eval(actual2) == StmLiteral(expected ++ expected: _*)())

    val actual3 = StmRepeat(s, 3)().tchk().lower()
    assert(
      mhir.ir.eval(actual3) == StmLiteral(
        expected ++ expected ++ expected: _*
      )()
    )
  }

  test("StmRepeat:2D") {
    val s = StmCount2D(C(2)(U8), C(3)(U8))()
    val expected = Seq(
      Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
      Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())
    ).flatten
    assert(mhir.ir.eval(StmRepeat(s, 0)().tchk()) == StmLiteral()())
    assert(mhir.ir.eval(StmRepeat(s, 1)().tchk()) == StmLiteral(expected: _*)())
    assert(
      mhir.ir.eval(StmRepeat(s, 2)().tchk())
        == StmLiteral(expected ++ expected: _*)()
    )
    assert(
      mhir.ir.eval(StmRepeat(s, 3)().tchk())
        == StmLiteral(expected ++ expected ++ expected: _*)()
    )
  }

  test("StmRepeat:3D") {
    val s = StmMap(StmCount(C(2)(U8))(), U8 ::+ (i => StmCst2D(3, 4, i)()))()
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
    ).flatMap(xss => xss.flatMap(xs => xs.map(x => IntCst(x)())))
    assert(mhir.ir.eval(StmRepeat(s, 0)().tchk()) == StmLiteral()())
    assert(mhir.ir.eval(StmRepeat(s, 1)().tchk()) == StmLiteral(expected: _*)())
    assert(
      mhir.ir.eval(StmRepeat(s, 2)().tchk())
        == StmLiteral(expected ++ expected: _*)()
    )
    assert(
      mhir.ir.eval(StmRepeat(s, 3)().tchk())
        == StmLiteral(expected ++ expected ++ expected: _*)()
    )
  }

  test("StmReverse:1D") {
    val s = StmReverse(StmCount(C(5)(U8))())().tchk()
    val expected = StmLiteral(4, 3, 2, 1, 0)()
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmReverse:2D") {
    val s = StmReverse(StmCount2D(C(3)(U8), C(3)(U8))())().tchk()
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
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmSplit:1D-2D") {
    val s = StmCount(6)()

    val expected = StmLiteral(0, 1, 2, 3, 4, 5)()
    assert(mhir.ir.eval(StmSplit(s, 1)().tchk()) == expected)
    assert(mhir.ir.eval(StmSplit(s, 2)().tchk()) == expected)
    assert(mhir.ir.eval(StmSplit(s, 3)().tchk()) == expected)
    assert(mhir.ir.eval(StmSplit(s, 6)().tchk()) == expected)
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
    assert(mhir.ir.eval(s) == expected)
  }

  test("StmJoin:3D-2D-1D") {
    // [[[(0, 0), (0, 1), (0, 2), (0, 3)],
    //   [(1, 0), (1, 1), (1, 2), (1, 3)],
    //   [(2, 0), (2, 1), (2, 2), (2, 3)]],
    //
    //  [[(0, 0), (0, 1), (0, 2), (0, 3)],
    //   [(1, 0), (1, 1), (1, 2), (1, 3)],
    //   [(2, 0), (2, 1), (2, 2), (2, 3)]]]
    val s = StmMap(StmCount(C(2)(U8))(), U8 ::+ (_ => StmCount2D(3, 4)()))()
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
    assert(mhir.ir.eval(StmJoin(s)().tchk()) == expected)
    assert(mhir.ir.eval(StmJoin(StmJoin(s)())().tchk()) == expected)
  }

  test("StmSplitJoin") {
    val p = Param("p")()
    val s = StmCount(6)()
    val expected = StmLiteral(0, 1, 2, 3, 4, 5)()

    assert(
      mhir.ir.eval(Let(p, s, StmJoin(StmSplit(p, 1)())())().tchk()) == expected
    )
    assert(
      mhir.ir.eval(Let(p, s, StmJoin(StmSplit(p, 2)())())().tchk()) == expected
    )
    assert(
      mhir.ir.eval(Let(p, s, StmJoin(StmSplit(p, 3)())())().tchk()) == expected
    )
    assert(
      mhir.ir.eval(Let(p, s, StmJoin(StmSplit(p, 6)())())().tchk()) == expected
    )
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
    assert(mhir.ir.eval(actual) == expected)
  }

  test("StmSlideV:1D:UnitWindow") {
    val actual = StmSlideV(StmCount(3)(), 1)().tchk()
    val expected =
      StmLiteral(VecLiteral(0)(), VecLiteral(1)(), VecLiteral(2)())()
    assert(mhir.ir.eval(actual) == expected)
  }

  test("StmSlideV:1D:SmallWindow") {
    val actual = StmSlideV(StmCount(4)(), 2)().tchk().lower()
    val expected = StmLiteral(
      VecLiteral(IntCst(0)(), IntCst(1)())(),
      VecLiteral(IntCst(1)(), IntCst(2)())(),
      VecLiteral(IntCst(2)(), IntCst(3)())()
    )()
    assert(mhir.ir.eval(actual) == expected)
  }

  test("StmSlideV:1D:LargestWindow") {
    val actual = StmSlideV(StmCount(5)(), 5)().tchk()
    val expected = StmLiteral(
      VecLiteral(
        IntCst(0)(),
        IntCst(1)(),
        IntCst(2)(),
        IntCst(3)(),
        IntCst(4)()
      )()
    )()
    assert(mhir.ir.eval(actual) == expected)
  }

  test("StmSlideS:1D:UnitWindow") {
    val actual = StmSlideS(StmCount(3)(), 1)().tchk()
    val expected = StmLiteral(0, 1, 2)()
    assert(mhir.ir.eval(actual) == expected)
  }

  test("StmSlideS:1D:SmallWindow") {
    val actual = StmSlideS(StmCount(4)(), 2)().tchk()
    val expected = StmLiteral(0, 1, 1, 2, 2, 3)()
    assert(mhir.ir.eval(actual) == expected)
  }

  test("StmSlideS:1D:SameSize") {
    val actual = StmSlideS(StmCount(C(5)(U8))(), 5)().tchk()
    val expected = StmLiteral(0, 1, 2, 3, 4)()
    assert(mhir.ir.eval(actual) == expected)
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
      Let(
        n,
        2,
        Let(m, 2, Let(s, StmCount2D(C(2)(U8), C(2)(U8))(), transposed)())()
      )().tchk()
    assert(mhir.ir.eval(actual) == expected)
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
    assert(mhir.ir.eval(actual) == expected)
  }
}
