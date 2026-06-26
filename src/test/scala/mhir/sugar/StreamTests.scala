package mhir.sugar

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

trait StreamTestHelpers {

  protected def build1D(n: Int, f: Int => Expr): Expr = {
    StmLiteral((0 until n).map(i => f(i)): _*)().tchk()
  }

  /** Build a simple 2D stream.
    *
    * @param n
    *   the number of rows.
    * @param m
    *   the number of columns.
    * @param f
    *   a function from indices `(i, j)` to the expression at that index.
    */
  protected def build2D(n: Int, m: Int, f: Int => Int => Expr): Expr = {
    StmSplit(
      StmLiteral(
        (0 until n).flatMap(i => (0 until m).map(j => f(i)(j))): _*
      )(),
      m
    )().tchk()
  }

  /** Build a simple 3D stream.
    *
    * @param n
    *   the size in the first dimension.
    * @param m
    *   the size in the second dimension.
    * @param k
    *   the size in the final dimension.
    * @param f
    *   a function from indices `(i, j, k)` to the expression at that index.
    */
  protected def build3D(
      n: Int,
      m: Int,
      k: Int,
      f: Int => Int => Int => Expr
  ): Expr = {
    StmSplit(
      StmSplit(
        StmLiteral(
          (0 until n).flatMap(i =>
            (0 until m).flatMap(j => (0 until k).map(k => f(i)(j)(k)))
          ): _*
        )(),
        k
      )(),
      m
    )().tchk()
  }
}

class StreamTests extends AnyFunSuite with StreamTestHelpers {

  test("StmCst:Int") {
    val s = StmCst(4, 3)()
    assert(mhir.eval.eval(s.tchk()) == StmLiteral(3, 3, 3, 3)())
  }

  test("StmCst:Tuple") {
    val c = Tuple(False, 99)()
    val s = StmCst(3, c)()
    assert(mhir.eval.eval(s.tchk()) == StmLiteral(c, c, c)())
  }

  test("StmCst:Stream") {
    val c = IntCst(42)()
    val s = StmCst(2, StmCst(3, c)())()
    assert(mhir.eval.eval(s.tchk()) == StmLiteral(c, c, c, c, c, c)())
  }

  test("StmCount") {
    val s = StmCount(5)().tchk()
    assert(mhir.eval.eval(s) == StmLiteral(0, 1, 2, 3, 4)())
  }

  test("StmCountFrom") {
    val s = StmRange(4, IntCst(3)(U8), IntCst(1)(U8))().tchk()
    assert(mhir.eval.eval(s) == StmLiteral(3, 4, 5, 6)())
  }

  test("StmRange(4, 3, 2)") {
    val s = StmRange(4, IntCst(3)(U8), IntCst(2)(U8))()
    assert(mhir.eval.eval(s) == StmLiteral.ints(3, 5, 7, 9))
    assert(mhir.eval.eval(s.tchk()) == StmLiteral.ints(3, 5, 7, 9))
  }

  test("StmRange(3, 2, -3)") {
    val s = StmRange(3, IntCst(2)(I8), IntCst(-3)(I8))()
    assert(mhir.eval.eval(s) == StmLiteral.ints(2, -1, -4))
    assert(mhir.eval.eval(s.tchk()) == StmLiteral.ints(2, -1, -4))
  }

  test("StmVecRange(4, 3, 1, 5)") {
    val s = StmVecRange(4, 3, C(1)(U8), C(5)(U8))().tchk().lower
    val expected = StmLiteral(
      VecLiteral(C(1)(U8), C(6)(U8), C(11)(U8))(),
      VecLiteral(C(16)(U8), C(21)(U8), C(26)(U8))(),
      VecLiteral(C(31)(U8), C(36)(U8), C(41)(U8))(),
      VecLiteral(C(46)(U8), C(51)(U8), C(56)(U8))()
    )().tchk()
    val actual = mhir.eval.eval(s)
    assert(actual == expected)
  }

  test("StmCst2D") {
    val s = StmCst2D(3, 4, 42)().tchk()
    val expected = StmLiteral(
      StmLiteral.ints(42, 42, 42, 42),
      StmLiteral.ints(42, 42, 42, 42),
      StmLiteral.ints(42, 42, 42, 42)
    )()
    assert(mhir.eval.eval(s) == expected.flatten)
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
    assert(mhir.eval.eval(s) == expected.flatten)
  }

  test("StmCount3D") {
    val s = StmCount3D(2, 3, 4)().tchk().lower
    val expected = StmLiteral(
      (0 until 2).flatMap(i =>
        (0 until 3).flatMap(j => (0 until 4).map(k => Tuple(i, j, k)()))
      ): _*
    )()
    assert(mhir.eval.eval(s) == expected)
  }

  test("Iterate:Square") {
    val x = Param("x")(U32)
    val n = Param("n")()
    val iter = Iterate(n, IntCst(3)(U32), Function(x, x * x)())()
    val e = (nVal: Int) => Let(n, IntCst(nVal)(U8), iter)().tchk()
    assert(mhir.eval.eval(e(0)) == StmLiteral(3)())
    assert(mhir.eval.eval(e(1)) == StmLiteral(9)())
    assert(mhir.eval.eval(e(2)) == StmLiteral(81)())
    assert(mhir.eval.eval(e(3)) == StmLiteral(81 * 81)())
  }

  test("Iterate:PlusTwo") {
    val x = Param("x")(TyTuple(I8))
    val n = Param("n")(U8)
    val iter =
      Iterate(n, Tuple(IntCst(-10)(I8))(), Function(x, Tuple(x.__0 + 2)())())()
    val e =
      (nVal: Int) => Let(n, IntCst(nVal)(U8), iter)().tchk()
    assert(mhir.eval.eval(e(0)) == StmLiteral(Tuple(-10)())())
    assert(mhir.eval.eval(e(1)) == StmLiteral(Tuple(-8)())())
    assert(mhir.eval.eval(e(2)) == StmLiteral(Tuple(-6)())())
    assert(mhir.eval.eval(e(3)) == StmLiteral(Tuple(-4)())())
  }

  test("StmMap:1D-1D:+7") {
    val s = StmMap(StmCount(IntCst(5)(U8))(), U8 ::+ (x => x + 7))().tchk()
    assert(mhir.eval.eval(s) == StmLiteral(7, 8, 9, 10, 11)())
  }

  test("StmMap:1D-1D:Identity") {
    val s = StmMap(StmCount(IntCst(3)(U8))(), Missing ::+ (x => x))()
    assert(mhir.eval.eval(s.tchk()) == StmLiteral(0, 1, 2)())
  }

  test("StmMap:1D-1D:DiscardInputReturn42") {
    val s = StmMap(StmCount(IntCst(6)(U8))(), U8 ::+ (_ => 42))()
    assert(mhir.eval.eval(s.tchk()) == StmLiteral(42, 42, 42, 42, 42, 42)())
  }

  test("StmMap:1D-1D:SingleElementStream") {
    val s = StmMap(StmCount(IntCst(1)(U8))(), U8 ::+ (x => x + 5))()
    assert(mhir.eval.eval(s.tchk()) == StmLiteral(5)())
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
    assert(mhir.eval.eval(s) == expected)
  }

  // The scalar input to the inner function is used only in the seed.
  test("StmMap:1D-2D:StmCountFrom") {
    val s = StmMap(
      StmCount(IntCst(3)(U8))(),
      Missing ::+ (n => StmRange(4, n, IntCst(1)(U8))())
    )().tchk()
    val expected = StmLiteral(
      Seq(
        Seq(0, 1, 2, 3),
        Seq(1, 2, 3, 4),
        Seq(2, 3, 4, 5)
      ).flatten.map(C(_)(U8)): _*
    )().tchk()
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmMap:1D-2D:SingleElementStream") {
    val s = StmMap(
      StmCst(1, IntCst(99)(U8))(),
      U8 ::+ (c => StmRange(5, c, IntCst(1)(U8))())
    )().tchk()
    val expected = StmLiteral(99, 100, 101, 102, 103)()
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmMap:2D-1D:StmReduce") {
    val s = StmCount2D(C(3)(U8), C(3)(U8))().tchk()
    val actual =
      StmMap(
        s,
        Missing ::+ (row =>
          StmReduce(
            row,
            Missing ::+ (x =>
              Tuple(x.__0.__0 + x.__1.__0, x.__0.__1 * x.__1.__1)()
            )
          )()
        )
      )().tchk().lower
    val expected = StmLiteral({
      val s: Seq[Seq[(Int, Int)]] =
        (0 until 3).map(i => (0 until 3).map(j => (i, j)))
      s.map(row =>
        row.reduce[(Int, Int)]({ case (acc, a) =>
          (acc._1 + a._1, acc._2 * a._2)
        })
      ).map({ case (x, y) => Tuple(x, y)() })
    }: _*)()
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmMap:2D-1D:StmReduce(StmPrefix)") {
    // [[3, 4, 5, 6, 7],
    //  [4, 5, 6, 7, 8],
    //  [5, 6, 7, 8, 9]]
    val s = build2D(n = 3, m = 5, i => j => C(3 + i + j)(U8))
    val actual = StmMap(
      s,
      Missing ::+ (row =>
        StmReduce(StmPrefix(row, 2)(), (U8, U8) ::+ (x => x.__0 + x.__1))()
      )
    )().tchk().lower
    val expected = StmLiteral(C(3 + 4)(U8), C(4 + 5)(U8), C(5 + 6)(U8))()
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmMap:2D-1D:StmAccess(Middle)") {
    val s = StmMap(
      StmCount2D(C(4)(U8), C(3)(U8))(),
      Missing ::+ (s => StmAccess(s, 1)())
    )().tchk()
    val expected =
      StmLiteral(Tuple(0, 1)(), Tuple(1, 1)(), Tuple(2, 1)(), Tuple(3, 1)())()
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmMap:2D-1D:StmAccess(Last)") {
    val s = StmMap(
      StmCount2D(C(4)(U8), C(3)(U8))(),
      Missing ::+ (s => StmAccess(s, 2)())
    )().tchk()
    val expected =
      StmLiteral(Tuple(0, 2)(), Tuple(1, 2)(), Tuple(2, 2)(), Tuple(3, 2)())()
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmMap:2D-1D:DiscardInputReturn42") {
    val s = StmMap(
      StmCount2D(C(3)(U8), C(4)(U8))(),
      TyStm((U8, U8), 4) ::+ (_ => StmCst(1, IntCst(42)())())
    )().tchk()
    val expected = StmLiteral(42, 42, 42)()
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmMap") {
    val s = StmMap(
      StmCount2D(C(4)(U8), C(3)(U8))(),
      Missing ::+ (s => StmMap(s, (U8, U8) ::+ (x => x.__0 + x.__1))())
    )().tchk()
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 1, 2),
        Seq(1, 2, 3),
        Seq(2, 3, 4),
        Seq(3, 4, 5)
      ).flatten: _*
    )
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmPrefix") {
    val s = StmMap(
      StmCount2D(C(3)(U8), C(100)(U8))(),
      TyStm((U8, U8), 100) ::+ (s => StmPrefix(s, 2)())
    )().tchk().lower
    val expected = StmLiteral(
      StmLiteral(Tuple(0, 0)(), Tuple(0, 1)())(),
      StmLiteral(Tuple(1, 0)(), Tuple(1, 1)())(),
      StmLiteral(Tuple(2, 0)(), Tuple(2, 1)())()
    )()
    assert(mhir.eval.eval(s) == expected.flatten)
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
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmMap:2D-2D:DiscardInputReturnStmCount") {
    val s = StmMap(
      StmCount2D(C(4)(U8), C(3)(U8))(),
      TyStm((U8, U8), 3) ::+ (_ => StmCount(5)())
    )().tchk().lower
    val expected = StmLiteral.ints(
      Seq(
        Seq(0, 1, 2, 3, 4),
        Seq(0, 1, 2, 3, 4),
        Seq(0, 1, 2, 3, 4),
        Seq(0, 1, 2, 3, 4)
      ).flatten: _*
    )
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmSlideV") {
    val s = StmMap(
      StmCount2D(C(3)(U8), C(5)(U8))(),
      TyStm((U8, U8), 5) ::+ (s => StmSlide(s, 3)())
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
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmMap:2D-2D:SingleElementOuterStream") {
    val s = StmMap(
      StmCount2D(C(1)(U8), C(3)(U8))(),
      TyStm((U8, U8), 3) ::+ (s =>
        StmMap(s, (U8, U8) ::+ (x => x.__0 + x.__1))()
      )
    )().tchk()
    val expected = StmLiteral(0, 1, 2)()
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmMap:2D-2D:SingleElementInnerStream") {
    val s = StmMap(
      StmCount2D(C(4)(U8), C(1)(U8))(),
      TyStm((U8, U8), 1) ::+ (s =>
        StmMap(s, (U8, U8) ::+ (x => x.__0 + x.__1))()
      )
    )().tchk()
    val expected = StmLiteral(0, 1, 2, 3)()
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmMap:-2D:MapCount") {
    val s = StmMap(
      StmCount(C(2)(U8))(),
      U8 ::+ (i => StmMap(StmCount(C(3)(U8))(), U8 ::+ (j => Tuple(i, j)()))())
    )().tchk().lower
    val expected = StmLiteral(
      Seq(
        Seq((0, 0), (0, 1), (0, 2)),
        Seq((1, 0), (1, 1), (1, 2))
      ).flatten.map({ case (i, j) => Tuple(C(i)(U8), C(j)(U8))() }): _*
    )().tchk()
    val actual = mhir.eval.eval(s)
    assert(actual == expected)
  }

  test("StmMap:-3D:MapCount") {
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
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmMap:3D-3D:StmTranspose") {
    val s = StmCount3D(C(2)(U8), C(2)(U8), C(2)(U8))()
    val expected = StmLiteral(
      (0 until 2).flatMap(i =>
        (0 until 2).flatMap(j => (0 until 2).map(k => Tuple(i, k, j)()))
      ): _*
    )()
    val actual =
      StmMap(s, TyStm(TyStm((U8, U8, U8), 2), 2) ::+ (s => StmTranspose(s)()))()
        .tchk()
        .lower
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmMap:RepeatedData1") {
    val n = 3
    val m = 4
    val f =
      (TyStm(TyStm(U8, m), n) ::+ (a =>
        TyStm(I32, m) ::+ (b =>
          StmMap(a, TyStm(U8, m) ::+ (rowA => StmZip(rowA, b)()))()
        )
      )).tchk().lower
    val a = build2D(n, m, i => j => C(i + 2 * j)(U8)).tchk().lower
    val b = StmRange(m, C(-1)(I32), C(2)(I32))().tchk().lower
    val expected = {
      val aVals = (0 until n).map(i => (0 until m).map(j => i + 2 * j))
      val bVals = (0 until m).map(t => -1 + 2 * t)
      val expectedVals = aVals.map(rowA => rowA.zip(bVals))
      StmLiteral(
        expectedVals.flatten.map({ case (x, y) => Tuple(x, y)() }): _*
      )()
    }
    val actual = mhir.eval.eval(FunCall(FunCall(f, a)(), b)())
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
      map.tchk().lower
    }
    val subs: Map[Expr, Expr] = Map(
      a -> StmCount2D(C(n)(U8), C(k)(U8))().tchk().lower,
      b -> StmCount2D(C(m)(U8), C(k)(U8))().tchk().lower
    )
    val expected = {
      val aVals =
        (0 until n).map(i => (0 until k).map(j => Tuple(C(i)(U8), C(j)(U8))()))
      val bVals =
        (0 until m).map(i => (0 until k).map(j => Tuple(C(i)(U8), C(j)(U8))()))
      val expectedVals = aVals.map(rowA => bVals.map(rowB => rowA.zip(rowB)))
      StmLiteral(
        expectedVals.flatten.flatten.map({ case (x, y) => Tuple(x, y)() }): _*
      )().tchk()
    }
    val actual = map.subPreserveType(subs)
    val actualVal = mhir.eval.eval(actual)
    assert(actualVal == expected)
  }

  test("StmMap2:Prod") {
    val n = 6
    val s1 = StmCount(C(n)(U8))()
    val s2 = StmRange(n, C(10)(U8), C(1)(U8))()
    val combined = StmMap2(s1, s2, TimesFunction(U8))()
    val expected = StmLiteral((0 until n).map(i => C(i * (10 + i))(U8)): _*)()
    val actual = mhir.eval.eval(combined)
    assert(actual == expected)
  }

  test("StmMap2:StmConcat") {
    val n = 3
    val m1 = 4
    val m2 = 5
    val s1 = StmCount2D(C(n)(U8), C(m1)(U8))()
    val s2 = StmCount2D(C(n)(U8), C(m2)(U8))()
    val combined = StmMap2(
      s1,
      s2,
      Missing ::+ (s1 => Missing ::+ (s2 => StmConcat(s1, s2)()))
    )()
    val expected = StmLiteral(
      (0 until n).flatMap(i => {
        val s1Elems =
          (0 until m1).map(j => Tuple(C(i)(U8), C(j)(U8))())
        val s2Elems =
          (0 until m2).map(j => Tuple(C(i)(U8), C(j)(U8))())
        s1Elems ++ s2Elems
      }): _*
    )()
    val actual = mhir.eval.eval(combined)
    assert(actual == expected)
  }

  test("StmMap2:StmPrepend") {
    val n = 3
    val m = 4
    val s1 = StmCount(C(n)(U8))().tchk()
    val s2 = StmSplit(StmRange(n * m, C(10)(U8), C(1)(U8))(), m)().tchk()
    val combined = StmMap2(
      s1,
      s2,
      Missing ::+ (i => Missing ::+ (s => StmPrepend(s, i)()))
    )().tchk().lower
    val expected = StmLiteral(
      (0 until n * m)
        .map(_ + 10)
        .grouped(m)
        .zipWithIndex
        .flatMap({ case (xs, i) => i +: xs })
        .map(C(_)(U8))
        .toSeq: _*
    )()
    val actual = mhir.eval.eval(combined)
    assert(actual == expected)
  }

  test("StmMap2:StmAppend") {
    val n = 3
    val m = 4
    val s1 = StmSplit(StmRange(n * m, C(10)(U8), C(1)(U8))(), m)().tchk()
    val s2 = StmCount(C(n)(U8))().tchk()
    val combined = StmMap2(
      s1,
      s2,
      Missing ::+ (s => Missing ::+ (i => StmAppend(s, i)()))
    )()
    val expected = StmLiteral(
      (0 until n * m)
        .map(_ + 10)
        .grouped(m)
        .zipWithIndex
        .flatMap({ case (xs, i) => xs :+ i })
        .map(C(_)(U8))
        .toSeq: _*
    )()
    val actual = mhir.eval.eval(combined)
    assert(actual == expected)
  }

  test("StmMap2:IgnoreFirstInput") {
    val n = 3
    val m1 = 4
    val m2 = 7
    val s1 = StmCount2D(C(n)(U8), C(m1)(U8))()
    val s2 = StmCount2D(C(n)(U8), C(m2)(U8))()
    val combined = StmMap2(
      s1,
      s2,
      Missing ::+ (_ => Missing ::+ (s2 => StmSuffix(s2, 2)()))
    )()
    val expected = StmLiteral(
      (0 until n).flatMap(i =>
        (0 until m2).map(j => Tuple(C(i)(U8), C(j)(U8))()).takeRight(2)
      ): _*
    )()
    val actual = mhir.eval.eval(combined)
    assert(actual == expected)
  }

  test("StmMap2:IgnoreSecondInput") {
    val n = 3
    val m1 = 4
    val m2 = 7
    val s1 = StmCount2D(C(n)(U8), C(m1)(U8))()
    val s2 = StmCount2D(C(n)(U8), C(m2)(U8))()
    val combined = StmMap2(
      s1,
      s2,
      Missing ::+ (s1 => Missing ::+ (_ => StmSuffix(s1, 2)()))
    )()
    val expected = StmLiteral(
      (0 until n).flatMap(i =>
        (0 until m1).map(j => Tuple(C(i)(U8), C(j)(U8))()).takeRight(2)
      ): _*
    )()
    val actual = mhir.eval.eval(combined)
    assert(actual == expected)
  }

  test("StmMap2:IgnoreBothInputs") {
    val n = 3
    val m1 = 4
    val m2 = 7
    val s1 = StmCount2D(C(n)(U8), C(m1)(U8))()
    val s2 = StmCount2D(C(n)(U8), C(m2)(U8))()
    val combined = StmMap2(
      s1,
      s2,
      Missing ::+ (_ => Missing ::+ (_ => StmCst(1, Tuple(42, True)())()))
    )()
    val expected = StmLiteral((0 until n).map(_ => Tuple(42, True)()): _*)()
    val actual = mhir.eval.eval(combined)
    assert(actual == expected)
  }

  test("StmAccess:1D") {
    val n = 3
    val i = Param("i")(U8)
    val s = StmRange(n, C(5)(U8), C(2)(U8))()
    val access = StmAccess(s, i)().tchk().lower

    assert(mhir.eval.eval(Let(i, C(0)(U8), access)()) == StmLiteral(5)())
    assert(mhir.eval.eval(Let(i, C(1)(U8), access)()) == StmLiteral(7)())
    assert(mhir.eval.eval(Let(i, C(2)(U8), access)()) == StmLiteral(9)())
  }

  test("StmAccess:2D") {
    val s = StmCount2D(4, 5)()
    val i = Param("i")(U8)
    val access = StmAccess(s, i)().tchk().lower

    val row = (i: Int) => StmLiteral((0 until 5).map(j => Tuple(i, j)()): _*)()

    assert(mhir.eval.eval(Let(i, C(0)(U8), access)()) == row(0))
    assert(mhir.eval.eval(Let(i, C(1)(U8), access)()) == row(1))
    assert(mhir.eval.eval(Let(i, C(2)(U8), access)()) == row(2))
    assert(mhir.eval.eval(Let(i, C(3)(U8), access)()) == row(3))
  }

  test("StmAccess:3D") {
    val s =
      build3D(3, 3, 4, i => j => k => Tuple(C(i)(U8), C(j)(U8), C(k)(U8))())
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
      mhir.eval.eval(StmAccess(s, 0)().tchk())
        == StmLiteral(expected.head.flatten: _*)()
    )
    assert(
      mhir.eval.eval(StmAccess(s, 1)().tchk())
        == StmLiteral(expected(1).flatten: _*)()
    )
    assert(
      mhir.eval.eval(StmAccess(s, 2)().tchk())
        == StmLiteral(expected(2).flatten: _*)()
    )
  }

  test("StmReduce:Stm[Int,4]:Sum") {
    val s = StmCount(C(4)(U8))()
    val f = {
      val x = Param("x")()
      val y = Param("y")()
      PatternFunction(TuplePattern(ParamPattern(x), ParamPattern(y)), x + y)()
    }
    val sum = StmReduce(s, f)().tchk().lower
    val expected = StmLiteral(C(6)())()
    val actual = mhir.eval.eval(sum)
    assert(actual == expected)
  }

  test("StmReduce:Stm[Int,4]:HornersMethod") {
    // [2, 3, 4, 5]
    // i.e., 2x^3 + 3x^2 + 4x + 5
    // i.e., 5 + x*(4 + x*(3 + x*2))
    val s = StmRange(C(4)(U16), C(2)(U16), C(1)(U16))()
    val x = C(10)(U16)
    val sum =
      StmReduce(s, Missing ::+ (a => a.__1 + x * a.__0))().tchk().lower
    val expected = StmLiteral(C(2345)())()
    val actual = mhir.eval.eval(sum)
    assert(actual == expected)
  }

  test("StmReduce:Stm[Vec[Int,1],4]:Sum") {
    val s = Param("s")(TyStm(TyVec(U8, 1), 4))
    val sum = StmReduce(
      s,
      Missing ::+ (v => VecMap(v, Missing ::+ (x => x.__0 + x.__1))())
    )().tchk().lower
    val sVal =
      StmLiteral((0 until 4).map(t => VecLiteral(C(t)(U8))()): _*)().tchk()
    val expected = StmLiteral(VecLiteral(C(6)())())()
    val actual = mhir.eval.eval(sum.subPreserveType(s -> sVal))
    assert(actual == expected)
  }

  test("StmReduce:Stm[Stm[Int,1],4]:Sum") {
    val s = Param("s")(TyStm(TyStm(U8, 1), 4))
    val sum = StmReduce(
      s,
      Missing ::+ (s => StmMap(s, Missing ::+ (x => x.__0 + x.__1))())
    )().tchk().lower
    val sVal = StmLiteral(C(1)(U8), C(11)(U8), C(21)(U8), C(31)(U8))().tchk()
    val expected = StmLiteral(C(64)())()
    val actual = mhir.eval.eval(sum.subPreserveType(s -> sVal))
    assert(actual == expected)
  }

  test("StmReduce:Stm[Vec[Stm[Stm[Vec[Int,1],1],1],1],4]") {
    val s = Param("s")(TyStm(TyVec(TyStm(TyStm(TyVec(U8, 1), 1), 1), 1), 4))
    val sum = StmReduce(
      s,
      Missing ::+ (v =>
        VecMap(
          v,
          Missing ::+ (s =>
            StmMap(
              s,
              Missing ::+ (s =>
                StmMap(
                  s,
                  Missing ::+ (v =>
                    VecMap(v, Missing ::+ (x => x.__0 + x.__1))()
                  )
                )()
              )
            )()
          )
        )()
      )
    )().tchk().lower
    val sVal = StmLiteral(
      Seq(1, 2, 3, 4).map(t => VecLiteral(VecLiteral(C(t)(U8))())()): _*
    )().tchk()
    val expected = StmLiteral(VecLiteral(VecLiteral(C(10)())())())()
    val actual = mhir.eval.eval(sum.subPreserveType(s -> sVal))
    assert(actual == expected)
  }

  test("MulAddCascaded:0") {
    val tv = TyVec(U8, 0)
    val s1 = StmLiteral(VecLiteral()(tv), VecLiteral()(tv), VecLiteral()(tv))()
    val s2 = StmLiteral(VecLiteral()(tv), VecLiteral()(tv), VecLiteral()(tv))()
    val n = 3
    assert(s1.elems.length == n)
    assert(s2.elems.length == n)
    val actualExpr = MulAddCascaded(s1, s2)().tchk().lower
    val actual = mhir.eval.eval(actualExpr)
    val expected = StmLiteral(C(0)(U16), C(0)(U16), C(0)(U16))().tchk()
    assert(actual == expected)
  }

  test("MulAddCascaded:1") {
    val s1 = StmLiteral(
      VecLiteral(C(1)(U16))(),
      VecLiteral(C(2)(U16))(),
      VecLiteral(C(3)(U16))(),
      VecLiteral(C(4)(U16))(),
      VecLiteral(C(5)(U16))(),
      VecLiteral(C(6)(U16))(),
      VecLiteral(C(7)(U16))()
    )()
    val s2 = StmLiteral(
      VecLiteral(C(7)(U16))(),
      VecLiteral(C(6)(U16))(),
      VecLiteral(C(5)(U16))(),
      VecLiteral(C(4)(U16))(),
      VecLiteral(C(3)(U16))(),
      VecLiteral(C(2)(U16))(),
      VecLiteral(C(1)(U16))()
    )()
    val n = 7
    assert(s1.elems.length == n)
    assert(s2.elems.length == n)
    val actualExpr = MulAddCascaded(s1, s2)().tchk().lower
    val actual = mhir.eval.eval(actualExpr)
    val expected = StmLiteral(
      C(1 * 7)(U16),
      C(2 * 6)(U16),
      C(3 * 5)(U16),
      C(4 * 4)(U16),
      C(5 * 3)(U16),
      C(6 * 2)(U16),
      C(7 * 1)(U16)
    )().tchk()
    assert(actual == expected)
  }

  test("MulAddCascaded:4") {
    val s1 = StmLiteral(
      VecLiteral(C(1)(U16), C(2)(U16), C(3)(U16), C(4)(U16))(),
      VecLiteral(C(5)(U16), C(6)(U16), C(7)(U16), C(8)(U16))(),
      VecLiteral(C(9)(U16), C(10)(U16), C(11)(U16), C(12)(U16))(),
      VecLiteral(C(13)(U16), C(14)(U16), C(15)(U16), C(16)(U16))(),
      VecLiteral(C(17)(U16), C(18)(U16), C(19)(U16), C(20)(U16))(),
      VecLiteral(C(21)(U16), C(22)(U16), C(23)(U16), C(24)(U16))(),
      VecLiteral(C(25)(U16), C(26)(U16), C(27)(U16), C(28)(U16))()
    )()
    val s2 = StmLiteral(
      VecLiteral(C(3)(U8), C(3)(U8), C(3)(U8), C(3)(U8))(),
      VecLiteral(C(3)(U8), C(3)(U8), C(3)(U8), C(3)(U8))(),
      VecLiteral(C(3)(U8), C(3)(U8), C(3)(U8), C(3)(U8))(),
      VecLiteral(C(3)(U8), C(3)(U8), C(3)(U8), C(3)(U8))(),
      VecLiteral(C(3)(U8), C(3)(U8), C(3)(U8), C(3)(U8))(),
      VecLiteral(C(3)(U8), C(3)(U8), C(3)(U8), C(3)(U8))(),
      VecLiteral(C(3)(U8), C(3)(U8), C(3)(U8), C(3)(U8))()
    )()
    val n = 7
    assert(s1.elems.length == n)
    assert(s2.elems.length == n)
    // TODO: Automatically drop the first few elements?
    val actualExpr = StmSuffix(MulAddCascaded(s1, s2)(), n - 3)().tchk().lower
    val actual = mhir.eval.eval(actualExpr)
    val expected = StmLiteral(
      C(3 * (1 + 6 + 11 + 16))(U16),
      C(3 * (5 + 10 + 15 + 20))(U16),
      C(3 * (9 + 14 + 19 + 24))(U16),
      C(3 * (13 + 18 + 23 + 28))(U16)
    )().tchk()
    assert(actual == expected)
  }

  for (n <- 0 until 10) {
    test(s"StmSum:$n") {
      val input = StmCount(C(n)(U8))()
      val e = StmSum(input)().tchk().lower
      val actual = mhir.eval.eval(e)
      val expected = StmLiteral(C((0 until n).sum)(U8))().tchk()
      assert(actual == expected)
    }
  }

  test("StmAll:Empty") {
    val e = StmAll(StmLiteral()(TyStm(TyBool, 0)))().tchk().lower
    assert(mhir.eval.eval(e) == StmLiteral(True)())
  }

  test("StmAll:AllTrue") {
    val input = StmLiteral((0 until 8).map(_ => True): _*)().tchk().lower
    val e = StmAll(input)().tchk().lower
    val actual = mhir.eval.eval(e)
    assert(actual == StmLiteral(True)())
  }

  test("StmAll:OneFalse") {
    val input = StmLiteral((0 until 9).map({
      case 1 => False
      case _ => True
    }): _*)().tchk().lower
    val e = StmAll(input)().tchk().lower
    val actual = mhir.eval.eval(e)
    assert(actual == StmLiteral(False)())
  }

  test("StmAny:Empty") {
    val e = StmAny(StmLiteral()(TyStm(TyBool, 0)))().tchk().lower
    assert(mhir.eval.eval(e) == StmLiteral(False)())
  }

  test("StmAny:AllFalse") {
    val input = StmLiteral((0 until 8).map(_ => False): _*)().tchk().lower
    val e = StmAny(input)().tchk().lower
    val actual = mhir.eval.eval(e)
    assert(actual == StmLiteral(False)())
  }

  test("StmAll:OneTrue") {
    val input = StmLiteral((0 until 9).map({
      case 0 => True
      case _ => False
    }): _*)().tchk().lower
    val e = StmAny(input)().tchk().lower
    val actual = mhir.eval.eval(e)
    assert(actual == StmLiteral(True)())
  }

  test("Vec2Stm:1D") {
    val v = VecBuild(5, U32 ::+ (i => i + 1))()
    assert(
      mhir.eval.eval(Vec2Stm(v)().tchk()) == StmLiteral.ints(1, 2, 3, 4, 5)
    )
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
    assert(mhir.eval.eval(s) == expected)
  }

  test("Vec2Stm:Vec[Stm[Int]]") {
    val n = 4
    val m = 3
    val vs =
      VecBuild(C(n)(U32), U32 ::+ (i => StmRange(C(m)(U32), C(42)(U32), i)()))()
    val e = Vec2Stm(vs)().tchk().lower
    val expected =
      StmLiteral(
        (0 until m).flatMap(i => (0 until n).map(t => IntCst(42 + i * t)())): _*
      )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("Vec2Stm2Vec") {
    val p = Param("p")()
    val actual = Stm2Vec(Vec2Stm(p)())()

    val v0 = VecBuild(6, U32 ::+ (i => i + 2))()
    val expected0 = StmLiteral(VecLiteral(2, 3, 4, 5, 6, 7)())()
    assert(mhir.eval.eval(Let(p, v0, actual)().tchk()) == expected0)

    val v1 = VecBuild(6, U32 ::+ (i => i * i))()
    val expected1 = StmLiteral(VecLiteral(0, 1, 4, 9, 16, 25)())()
    assert(mhir.eval.eval(Let(p, v1, actual)().tchk()) == expected1)
  }

  test("Stm2Vec2Stm") {
    val s = Param("s")(TyStm(U32, C(6)(U8)))
    val actual =
      StmMap(Stm2Vec(s)(), TyVec(U32, C(6)(U8)) ::+ (v => Vec2Stm(v)()))()

    val s0 = StmCount(C(6)(U32))()
    val expected0 = StmLiteral(0, 1, 2, 3, 4, 5)()
    assert(mhir.eval.eval(Let(s, s0, actual)().tchk()) == expected0)

    val s1 = StmCst(6, C(42)(U32))()
    val expected1 = StmLiteral(42, 42, 42, 42, 42, 42)()
    assert(mhir.eval.eval(Let(s, s1, actual)().tchk()) == expected1)
  }

  test("StmConcat:1D") {
    val u7 = TyUInt(7)
    val s1 = StmCst(4, C(-77)(I8))()
    val s2 = StmMap(StmCount(C(2)(u7))(), u7 ::+ (x => ReshapeData(x, I8)()))()
    val s3 = StmMap(StmCount(C(3)(u7))(), u7 ::+ (x => ReshapeData(x, I8)()))()

    val actual1 = StmConcat(s1, s1)().tchk().lower
    val expected1 = StmLiteral((0 until 8).map(_ => C(-77)(I8)): _*)()
    assert(mhir.eval.eval(actual1) == expected1)

    val actual2 = StmConcat(s3, s1)().tchk()
    val expected2 = StmLiteral(0, 1, 2, -77, -77, -77, -77)()
    assert(mhir.eval.eval(actual2) == expected2)

    val actual3 = StmConcat(StmConcat(s3, s1)(), s2)().tchk()
    val expected3 = StmLiteral(0, 1, 2, -77, -77, -77, -77, 0, 1)()
    assert(mhir.eval.eval(actual3) == expected3)
  }

  test("StmConcat:1D:Stm[Vec]") {
    val v = VecLiteral(C(-1)(I16), C(0)(I16), C(1)(I16))()
    val s1 = StmCst(3, v)()
    val n = Param("n")(U8)
    val nVal = C(3)(U8)
    val constValues = Map(n -> nVal)
    val s2 = StmMap(
      StmCount(C(4)(U8))(),
      U8 ::+ (t =>
        VecBuild(n, U8 ::+ (i => ReshapeData(Sum(i, Prod(t, t)())(), I16)()))()
      )
    )()
    val actual = StmConcat(s1, s2)()
      .tchk(Map(), constValues)
      .subPreserveType(n -> nVal)
      .lower
    val actualVal = mhir.eval.eval(actual)
    val expected = StmLiteral(
      v,
      v,
      v,
      VecLiteral(C(0)(I16), C(1)(I16), C(2)(I16))(),
      VecLiteral(C(1)(I16), C(2)(I16), C(3)(I16))(),
      VecLiteral(C(4)(I16), C(5)(I16), C(6)(I16))(),
      VecLiteral(C(9)(I16), C(10)(I16), C(11)(I16))()
    )().tchk()
    assert(actualVal == expected)
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
    assert(mhir.eval.eval(actual) == StmLiteral(expected.flatten: _*)())
  }

  test("StmConcat:3D") {
    val s0 = build3D(3, 2, 2, _ => _ => _ => C(100)(U8))
    val s1 = build3D(2, 2, 2, i => _ => _ => C(i)(U8))
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
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmPrepend:1D") {
    val s = StmCount(C(3)(U8))()
    val actual = StmPrepend(s, C(42)(U8))().tchk()
    assert(mhir.eval.eval(actual) == StmLiteral(42, 0, 1, 2)())
  }

  test("StmPrepend:2D") {
    val s0 = StmCount2D(C(3)(U8), C(3)(U8))()
    val s1 = StmCst(3, Tuple(C(42)(U8), C(42)(U8))())()

    val actual = StmPrepend(s0, s1)().tchk().lower
    val expected = StmLiteral(
      Seq(
        Seq(Tuple(42, 42)(), Tuple(42, 42)(), Tuple(42, 42)()),
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)())
      ).flatten: _*
    )()
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmPrepend:3D") {
    val s0 = build3D(2, 2, 2, _ => _ => _ => C(42)(U8))
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
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmAppend:1D") {
    val s = StmCount(C(3)(U8))()
    val actual = StmAppend(s, C(42)(U8))().tchk()
    assert(mhir.eval.eval(actual) == StmLiteral(0, 1, 2, 42)())
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
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmAppend:3D") {
    val s0 = build3D(2, 2, 2, _ => _ => _ => C(42)(U8))
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
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmPrefix:1D") {
    val s = StmCount(3)()
    val k = Param("k")(U8)
    val prefix = StmPrefix(s, k)().tchk()

    assert(mhir.eval.eval(Let(k, C(0)(U8), prefix)()) == StmLiteral()())
    assert(mhir.eval.eval(Let(k, C(1)(U8), prefix)()) == StmLiteral(0)())
    assert(mhir.eval.eval(Let(k, C(2)(U8), prefix)()) == StmLiteral(0, 1)())
    assert(mhir.eval.eval(Let(k, C(3)(U8), prefix)()) == StmLiteral(0, 1, 2)())
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
    assert(mhir.eval.eval(Let(k, C(0)(U8), prefix)()) == expected(0))
    assert(mhir.eval.eval(Let(k, C(1)(U8), prefix)()) == expected(1))
    assert(mhir.eval.eval(Let(k, C(2)(U8), prefix)()) == expected(2))
  }

  test("StmPrefix:3D") {
    val s = build3D(2, 4, 5, _ => j => k => Tuple(C(j)(U8), C(k)(U8))())

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
    assert(mhir.eval.eval(StmPrefix(s, 0)().tchk()) == StmLiteral()())
    assert(
      mhir.eval.eval(StmPrefix(s, 1)().tchk())
        == StmLiteral(expected.slice(0, 1).flatten.flatten: _*)()
    )
    assert(
      mhir.eval.eval(StmPrefix(s, 2)().tchk())
        == StmLiteral(expected.slice(0, 2).flatten.flatten: _*)()
    )
  }

  test("StmSuffix:1D") {
    val s = StmCount(3)()
    val k = Param("k")(U8)
    val suffix = StmSuffix(s, k)().tchk()
    assert(mhir.eval.eval(Let(k, C(0)(U8), suffix)()) == StmLiteral()())
    assert(mhir.eval.eval(Let(k, C(1)(U8), suffix)()) == StmLiteral(2)())
    assert(mhir.eval.eval(Let(k, C(2)(U8), suffix)()) == StmLiteral(1, 2)())
    assert(mhir.eval.eval(Let(k, C(3)(U8), suffix)()) == StmLiteral(0, 1, 2)())
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
    assert(mhir.eval.eval(Let(k, C(0)(U8), suffix)()) == expected(0))
    assert(mhir.eval.eval(Let(k, C(1)(U8), suffix)()) == expected(1))
    assert(mhir.eval.eval(Let(k, C(2)(U8), suffix)()) == expected(2))
    assert(mhir.eval.eval(Let(k, C(3)(U8), suffix)()) == expected(3))
  }

  test("StmSuffix:3D") {
    val s = build3D(2, 4, 5, _ => j => k => Tuple(C(j)(U8), C(k)(U8))())

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
    assert(mhir.eval.eval(StmSuffix(s, 0)().tchk()) == StmLiteral()())
    assert(
      mhir.eval.eval(StmSuffix(s, 1)().tchk())
        == StmLiteral(expected.slice(1, 2).flatten.flatten: _*)()
    )
    assert(
      mhir.eval.eval(StmSuffix(s, 2)().tchk())
        == StmLiteral(expected.slice(0, 2).flatten.flatten: _*)()
    )
  }

  test("StmShiftLeft:1D") {
    val s = StmShiftLeft(StmCount(C(3)(U8))(), C(42)(U8))().tchk()
    assert(mhir.eval.eval(s) == StmLiteral(1, 2, 42)())
  }

  test("StmShiftLeft:2D") {
    val s0 = StmCount2D(C(3)(U8), C(4)(U8))()
    val s1 = build1D(4, j => Tuple(C(99)(U8), C(j)(U8))())
    val s = StmShiftLeft(s0, s1)().tchk()

    val expected = StmLiteral(
      Seq(
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)()),
        Seq(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)(), Tuple(2, 3)()),
        Seq(Tuple(99, 0)(), Tuple(99, 1)(), Tuple(99, 2)(), Tuple(99, 3)())
      ).flatten: _*
    )()
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmShiftLeft:3D") {
    val s0 = build3D(2, 4, 5, i => _ => _ => C(i)(U8))
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
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmShiftRight:1D") {
    val s = StmShiftRight(StmCount(C(3)(U8))(), C(42)(U8))().tchk()
    val expected = StmLiteral(42, 0, 1)()
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmShiftRight:2D") {
    val s0 = StmCount2D(C(3)(U8), C(4)(U8))()
    val s1 = build1D(4, j => Tuple(C(99)(U8), C(j)(U8))())
    val s = StmShiftRight(s0, s1)().tchk()

    val expected = StmLiteral(
      Seq(
        Seq(Tuple(99, 0)(), Tuple(99, 1)(), Tuple(99, 2)(), Tuple(99, 3)()),
        Seq(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)(), Tuple(0, 3)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)(), Tuple(1, 3)())
      ).flatten: _*
    )()
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmShiftRight:3D") {
    val s0 = build3D(2, 4, 5, i => _ => _ => C(i)(U8))
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
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmVecShiftRightGarbage:Stm[Vec[u8,3],4]") {
    def extractFlat(e: Expr): Seq[Expr] = {
      e.asInstanceOf[StmLiteral].elems.flatMap(_.asInstanceOf[VecLiteral].elems)
    }

    val input = StmLiteral(
      VecLiteral(C(0)(U8), C(1)(U8), C(2)(U8))(),
      VecLiteral(C(3)(U8), C(4)(U8), C(5)(U8))(),
      VecLiteral(C(6)(U8), C(7)(U8), C(8)(U8))(),
      VecLiteral(C(9)(U8), C(10)(U8), C(11)(U8))()
    )()
    val expected = (0 until 12).map(C(_)(U8))

    val s1 = StmVecShiftRightGarbage(input, 1)().tchk().lower
    val actual1 = extractFlat(mhir.eval.eval(s1)).drop(1)
    assert(actual1 == expected.dropRight(1))

    val s2 = StmVecShiftRightGarbage(input, 2)().tchk().lower
    val actual2 = extractFlat(mhir.eval.eval(s2)).drop(2)
    assert(actual2 == expected.dropRight(2))

    val s3 = StmVecShiftRightGarbage(input, 3)().tchk().lower
    val actual3 = extractFlat(mhir.eval.eval(s3)).drop(3)
    assert(actual3 == expected.dropRight(3))

    val s4 = StmVecShiftRightGarbage(input, 4)().tchk().lower
    val actual4 = extractFlat(mhir.eval.eval(s4)).drop(4)
    assert(actual4 == expected.dropRight(4))

    val s5 = StmVecShiftRightGarbage(input, 5)().tchk().lower
    val actual5 = extractFlat(mhir.eval.eval(s5)).drop(5)
    assert(actual5 == expected.dropRight(5))
  }

  test("StmShiftRightGarbage:Stm[u8,8]") {
    def extract(e: Expr): Seq[Expr] = {
      e.asInstanceOf[StmLiteral].elems
    }

    val input = StmLiteral((0 until 8).map(C(_)(U8)): _*)()
    val expected = (0 until 8).map(C(_)(U8))

    val s1 = StmShiftRightGarbage(input, 1)().tchk().lower
    val actual1 = extract(mhir.eval.eval(s1)).drop(1)
    assert(actual1 == expected.dropRight(1))

    val s2 = StmShiftRightGarbage(input, 2)().tchk().lower
    val actual2 = extract(mhir.eval.eval(s2)).drop(2)
    assert(actual2 == expected.dropRight(2))

    val s3 = StmShiftRightGarbage(input, 3)().tchk().lower
    val actual3 = extract(mhir.eval.eval(s3)).drop(3)
    assert(actual3 == expected.dropRight(3))

    val s4 = StmShiftRightGarbage(input, 4)().tchk().lower
    val actual4 = extract(mhir.eval.eval(s4)).drop(4)
    assert(actual4 == expected.dropRight(4))

    val s5 = StmShiftRightGarbage(input, 5)().tchk().lower
    val actual5 = extract(mhir.eval.eval(s5)).drop(5)
    assert(actual5 == expected.dropRight(5))
  }

  test("StmZip:1D") {
    val n = Param("n")(U8)
    val nVal = 4
    val constValues = Map(n -> C(nVal)(U8))
    val a = StmCount(4)()
    val b = {
      val b = Param("b")(TyBool)
      StmBuild(n, b, True, Map[Param, (Expr, Expr)](b -> (True, !b)))()
    }
    val s = StmZip(a, b)()
      .tchk(Map(), constValues)
      .subPreserveType(constValues.toMap[Expr, Expr])
    val expected =
      StmLiteral(
        Tuple(0, True)(),
        Tuple(1, False)(),
        Tuple(2, True)(),
        Tuple(3, False)()
      )()
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmRepeat:1D") {
    val s = StmCount(C(4)(U8))()
    val expected = Seq(C(0)(), C(1)(), C(2)(), C(3)())

    val actual0 = StmRepeat(s, 0)().tchk().lower
    assert(mhir.eval.eval(actual0) == StmLiteral()())

    val actual1 = StmRepeat(s, 1)().tchk().lower
    assert(mhir.eval.eval(actual1) == StmLiteral(expected: _*)())

    val actual2 = StmRepeat(s, 2)().tchk().lower
    assert(mhir.eval.eval(actual2) == StmLiteral(expected ++ expected: _*)())

    val actual3 = StmRepeat(s, 3)().tchk().lower
    assert(
      mhir.eval.eval(actual3) == StmLiteral(
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
    assert(mhir.eval.eval(StmRepeat(s, 0)().tchk()) == StmLiteral()())
    assert(
      mhir.eval.eval(StmRepeat(s, 1)().tchk()) == StmLiteral(expected: _*)()
    )
    assert(
      mhir.eval.eval(StmRepeat(s, 2)().tchk())
        == StmLiteral(expected ++ expected: _*)()
    )
    assert(
      mhir.eval.eval(StmRepeat(s, 3)().tchk())
        == StmLiteral(expected ++ expected ++ expected: _*)()
    )
  }

  test("StmRepeat:3D") {
    val s = build3D(2, 3, 4, i => _ => _ => C(i)(U8))
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
    assert(mhir.eval.eval(StmRepeat(s, 0)().tchk()) == StmLiteral()())
    assert(
      mhir.eval.eval(StmRepeat(s, 1)().tchk()) == StmLiteral(expected: _*)()
    )
    assert(
      mhir.eval.eval(StmRepeat(s, 2)().tchk())
        == StmLiteral(expected ++ expected: _*)()
    )
    assert(
      mhir.eval.eval(StmRepeat(s, 3)().tchk())
        == StmLiteral(expected ++ expected ++ expected: _*)()
    )
  }

  test("StmReverse:1D") {
    val s = StmReverse(StmCount(C(5)(U8))())().tchk()
    val expected = StmLiteral(4, 3, 2, 1, 0)()
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmSplit:1D-2D") {
    val s = StmCount(6)()

    val expected = StmLiteral(0, 1, 2, 3, 4, 5)()
    assert(mhir.eval.eval(StmSplit(s, 1)().tchk()) == expected)
    assert(mhir.eval.eval(StmSplit(s, 2)().tchk()) == expected)
    assert(mhir.eval.eval(StmSplit(s, 3)().tchk()) == expected)
    assert(mhir.eval.eval(StmSplit(s, 6)().tchk()) == expected)
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
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmJoin:3D-2D-1D") {
    // [[[(0, 0), (0, 1), (0, 2), (0, 3)],
    //   [(1, 0), (1, 1), (1, 2), (1, 3)],
    //   [(2, 0), (2, 1), (2, 2), (2, 3)]],
    //
    //  [[(0, 0), (0, 1), (0, 2), (0, 3)],
    //   [(1, 0), (1, 1), (1, 2), (1, 3)],
    //   [(2, 0), (2, 1), (2, 2), (2, 3)]]]
    val s = build3D(2, 3, 4, _ => j => k => Tuple(C(j)(U8), C(k)(U8))())
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
    assert(mhir.eval.eval(StmJoin(s)().tchk()) == expected)
    assert(mhir.eval.eval(StmJoin(StmJoin(s)())().tchk()) == expected)
  }

  test("StmSplitJoin") {
    val p = Param("p")()
    val s = StmCount(6)()
    val expected = StmLiteral(0, 1, 2, 3, 4, 5)()

    assert(
      mhir.eval.eval(Let(p, s, StmJoin(StmSplit(p, 1)())())().tchk())
        == expected
    )
    assert(
      mhir.eval.eval(Let(p, s, StmJoin(StmSplit(p, 2)())())().tchk())
        == expected
    )
    assert(
      mhir.eval.eval(Let(p, s, StmJoin(StmSplit(p, 3)())())().tchk())
        == expected
    )
    assert(
      mhir.eval.eval(Let(p, s, StmJoin(StmSplit(p, 6)())())().tchk())
        == expected
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
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmSlideV:1D:UnitWindow") {
    val actual1 = StmSlide(StmCount(C(3)(U8))(), winSize = 1)().tchk()
    val expected1 = StmLiteral(
      VecLiteral(C(0)(U8))(),
      VecLiteral(C(1)(U8))(),
      VecLiteral(C(2)(U8))()
    )().tchk()
    assert(actual1.typ == expected1.typ)
    assert(mhir.eval.eval(actual1) == expected1)

    val actual2 =
      StmSlide(StmCount(C(3)(U8))(), winSize = 1, stride = 2)().tchk()
    val expected2 = StmLiteral(
      VecLiteral(C(0)(U8))(),
      VecLiteral(C(2)(U8))()
    )().tchk()
    assert(actual2.typ == expected2.typ)
    assert(mhir.eval.eval(actual2) == expected2)

    val actual3 =
      StmSlide(StmCount(C(3)(U8))(), winSize = 1, stride = 3)().tchk()
    val expected3 = StmLiteral(
      VecLiteral(C(0)(U8))()
    )().tchk()
    assert(actual3.typ == expected3.typ)
    assert(mhir.eval.eval(actual3) == expected3)

    val actual4 =
      StmSlide(StmCount(C(3)(U8))(), winSize = 1, stride = 4)().tchk()
    val expected4 = StmLiteral(
      VecLiteral(C(0)(U8))()
    )().tchk()
    assert(actual4.typ == expected4.typ)
    assert(mhir.eval.eval(actual4) == expected4)
  }

  test("StmSlideV:1D:SmallWindow") {
    val actual1 = StmSlide(StmCount(C(4)(U8))(), winSize = 2)().tchk().lower
    val expected1 = StmLiteral(
      VecLiteral(C(0)(U8), C(1)(U8))(),
      VecLiteral(C(1)(U8), C(2)(U8))(),
      VecLiteral(C(2)(U8), C(3)(U8))()
    )().tchk()
    assert(actual1.typ == expected1.typ)
    assert(mhir.eval.eval(actual1) == expected1)

    val actual2 =
      StmSlide(StmCount(C(4)(U8))(), winSize = 2, stride = 2)().tchk().lower
    val expected2 = StmLiteral(
      VecLiteral(C(0)(U8), C(1)(U8))(),
      VecLiteral(C(2)(U8), C(3)(U8))()
    )().tchk()
    assert(actual2.typ == expected2.typ)
    assert(mhir.eval.eval(actual2) == expected2)

    val actual3 =
      StmSlide(StmCount(C(4)(U8))(), winSize = 2, stride = 3)().tchk().lower
    val expected3 = StmLiteral(
      VecLiteral(C(0)(U8), C(1)(U8))()
    )().tchk()
    assert(actual3.typ == expected3.typ)
    assert(mhir.eval.eval(actual3) == expected3)
  }

  test("StmSlideV:1D:LargestWindow") {
    val actual = StmSlide(StmCount(5)(), 5)().tchk()
    val expected = StmLiteral(
      VecLiteral(
        IntCst(0)(),
        IntCst(1)(),
        IntCst(2)(),
        IntCst(3)(),
        IntCst(4)()
      )()
    )()
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmSlideStartingWith:1D:SmallWindow") {
    val actual1 = StmSlideStartingWith(
      // [1, 4, 9, 16, 25]
      build1D(5, i => C((i + 1) * (i + 1))(U8)),
      AllZero(TyVec(U8, 2))
    )().tchk().lower
    val expected1 = StmLiteral(
      VecLiteral(C(0)(U8), C(0)(U8), C(1)(U8))(),
      VecLiteral(C(0)(U8), C(1)(U8), C(4)(U8))(),
      VecLiteral(C(1)(U8), C(4)(U8), C(9)(U8))(),
      VecLiteral(C(4)(U8), C(9)(U8), C(16)(U8))(),
      VecLiteral(C(9)(U8), C(16)(U8), C(25)(U8))()
    )().tchk()
    assert(actual1.typ == expected1.typ)
    assert(mhir.eval.eval(actual1) == expected1)
  }

  test("StmSlideS:1D:UnitWindow") {
    val actual = StmSlideS(StmCount(3)(), 1)().tchk()
    val expected = StmLiteral(0, 1, 2)()
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmSlideS:1D:SmallWindow") {
    val actual = StmSlideS(StmCount(4)(), 2)().tchk()
    val expected = StmLiteral(0, 1, 1, 2, 2, 3)()
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmSlideS:1D:SameSize") {
    val actual = StmSlideS(StmCount(C(5)(U8))(), 5)().tchk()
    val expected = StmLiteral(0, 1, 2, 3, 4)()
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmSlide2D:3x3") {
    // [[ 0,  1,  2,  3,  4],
    //  [ 5,  6,  7,  8,  9],
    //  [10, 11, 12, 13, 14],
    //  [15, 16, 17, 18, 19]]
    val s = StmSplit(StmCount(C(20)(U8))(), 5)()
    // Partial evaluation speeds up evaluation in this case
    val actual = mhir.optimize.PartialEvalPass.partialEval(
      StmSlide2D(s, 3, 3)().tchk().lower
    )
    val expected = StmLiteral(
      Seq(
        Seq(Seq(0, 1, 2), Seq(5, 6, 7), Seq(10, 11, 12)),
        Seq(Seq(1, 2, 3), Seq(6, 7, 8), Seq(11, 12, 13)),
        Seq(Seq(2, 3, 4), Seq(7, 8, 9), Seq(12, 13, 14)),
        Seq(Seq(5, 6, 7), Seq(10, 11, 12), Seq(15, 16, 17)),
        Seq(Seq(6, 7, 8), Seq(11, 12, 13), Seq(16, 17, 18)),
        Seq(Seq(7, 8, 9), Seq(12, 13, 14), Seq(17, 18, 19))
      ).map(xxs =>
        VecLiteral(xxs.map(xs => VecLiteral(xs.map(C(_)(U8)): _*)()): _*)()
      ): _*
    )()
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmSlide2D:4x2") {
    // [[ 0,  1,  2,  3],
    //  [ 4,  5,  6,  7],
    //  [ 8,  9, 10, 11],
    //  [12, 13, 14, 15]]
    val s = StmSplit(StmCount(C(16)(U8))(), 4)()
    val actual = mhir.optimize.PartialEvalPass.partialEval(
      StmSlide2D(s, 4, 2)().tchk().lower
    )
    val expected = StmLiteral(
      Seq(
        Seq(Seq(0, 1), Seq(4, 5), Seq(8, 9), Seq(12, 13)),
        Seq(Seq(1, 2), Seq(5, 6), Seq(9, 10), Seq(13, 14)),
        Seq(Seq(2, 3), Seq(6, 7), Seq(10, 11), Seq(14, 15))
      ).map(xxs =>
        VecLiteral(xxs.map(xs => VecLiteral(xs.map(C(_)(U8)): _*)()): _*)()
      ): _*
    )()
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmSlide2D:1x1") {
    val s = StmSplit(StmCount(C(4)(U8))(), 2)()
    val actual = StmSlide2D(s, 1, 1)().tchk().lower
    val expected =
      StmLiteral(Seq(0, 1, 2, 3).map(x => VecLiteral(VecLiteral(x)())()): _*)()
    assert(mhir.eval.eval(actual) == expected)
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
    assert(mhir.eval.eval(actual) == expected)
  }

  test("StmTransposeTranspose") {
    val s = Param("s")()
    val actual =
      Let(s, StmCount2D(2, 2)(), StmTranspose(StmTranspose(s)())())()
        .tchk()
        .lower
    val expected = StmLiteral(
      Seq(
        Seq(Tuple(0, 0)(), Tuple(0, 1)()),
        Seq(Tuple(1, 0)(), Tuple(1, 1)())
      ).flatten: _*
    )()
    assert(mhir.eval.eval(actual) == expected)
  }
}
