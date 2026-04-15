package mhir.sugar
package experimental

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class ExperimentalStreamTests extends AnyFunSuite with StreamTestHelpers {

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
    )().tchk().lower
    val expected = StmLiteral(3, 6, 9, 12)()
    assert(mhir.eval.eval(s) == expected)
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
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmScanInclusive") {
    // [[ 0,  1,  2,  3],
    //  [10, 11, 12, 13],
    //  [20, 21, 22, 23]]
    val input = build2D(3, 4, i => j => C(10 * i + j)(U8))
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
    assert(mhir.eval.eval(s) == expected)
  }

  test("StmMap:2D-2D:StmScanExclusive") {
    // [[ 0,  1,  2,  3],
    //  [10, 11, 12, 13],
    //  [20, 21, 22, 23]]
    val input = build2D(3, 4, i => j => C(10 * i + j)(U8))
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
    assert(mhir.eval.eval(s) == expected)
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
      )).tchk().lower
    val a = build2D(n, n, i => j => C(i + 2 * j)(U16)).tchk().lower
    val b = StmRange(n, C(10)(U16), C(3)(U16))().tchk().lower
    val expected = {
      val aVals = (0 until n).map(i => (0 until n).map(j => i + 2 * j))
      val bVals = (0 until n).map(t => 10 + 3 * t)
      val expectedVals =
        aVals.map(rowA => rowA.zip(bVals).map({ case (x, y) => x * y }).sum)
      StmLiteral.ints(expectedVals: _*)
    }
    val actual = mhir.eval.eval(FunCall(FunCall(f, a)(), b)())
    assert(actual == expected)
  }

  test("StmFold:1D:Sum") {
    val sum =
      StmFold(StmCount(C(6)(U8))(), C(3)(U8), PlusFunction(U8))()
        .tchk()
    assert(mhir.eval.eval(sum) == StmLiteral(18)())
  }

  test("StmFold:1D:Product") {
    val prod =
      StmFold(StmRange(5, C(1)(U8), C(1)(U8))(), C(1)(U8), TimesFunction(U8))()
        .tchk()
    assert(mhir.eval.eval(prod) == StmLiteral(120)())
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
    assert(mhir.eval.eval(y) == StmLiteral(88)())
  }

  test("StmFold:1D:DiscardInputAdd42") {
    val x = StmFold(
      StmCount(C(5)(U8))(),
      C(2)(U8),
      U8 ::+ (acc => U8 ::+ (_ => acc + 42))
    )().tchk()
    assert(mhir.eval.eval(x) == StmLiteral(2 + 5 * 42)())
  }

  test("StmFold:2D:SumWide") {
    // [[0, 1, 2, 3, 4],
    //  [1, 2, 3, 4, 5],
    //  [2, 3, 4, 5, 6]]
    val s = build2D(3, 5, i => j => C(i + j)(U8))
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
    )().tchk().lower
    assert(mhir.eval.eval(sum) == StmLiteral(45)())
  }

  test("StmFold:2D:SumNarrow") {
    // [[0, 1],
    //  [1, 2],
    //  [2, 3],
    //  [3, 4]]
    val s = build2D(4, 2, i => j => C(i + j)(U8))
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
    assert(mhir.eval.eval(sum) == StmLiteral(16)())
  }

  test("StmFold:2D:SumColumn") {
    // [[1, 2, 3, 4],
    //  [2, 3, 4, 5],
    //  [3, 4, 5, 6],
    //  [4, 5, 6, 7]]
    val s = build2D(4, 4, i => j => C(i + j + 1)(U8))
    val x = StmFold(
      s,
      C(13)(U8),
      U8 ::+ (acc =>
        TyStm(U8, 4) ::+ (s =>
          StmMap(StmAccess(s, 1)(), U8 ::+ (x => acc + x))()
        )
      )
    )().tchk()
    assert(mhir.eval.eval(x) == StmLiteral(13 + 2 + 3 + 4 + 5)())
  }

  test("StmFold:2D:Product") {
    // [[1, 2, 3],
    //  [2, 3, 4],
    //  [3, 4, 5]]
    val s = build2D(3, 3, i => j => C(i + j + 1)(U8))
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
    assert(mhir.eval.eval(prod) == StmLiteral(8640)())
  }

  test("StmFold:2D:DiscardInputAdd42") {
    // [[0, 1, 2, 3, 4],
    //  [1, 2, 3, 4, 5],
    //  [2, 3, 4, 5, 6]]
    val s = build2D(3, 5, i => j => C(i + j)(U8))
    val sum = StmFold(
      s,
      C(0)(U8),
      U8 ::+ (acc => TyStm(U8, 5) ::+ (_ => StmCst(1, acc + 42)()))
    )().tchk()
    assert(mhir.eval.eval(sum) == StmLiteral(3 * 42)())
  }

  test("StmFold:3D:Sum") {
    val s =
      build3D(2, 2, 3, i => j => k => Tuple(C(i)(U8), C(j)(U8), C(k)(U8))())
    val sum = StmFold(
      s,
      C(0)(U8),
      U8 ::+ (acc =>
        TyStm(TyStm((U8, U8, U8), 3), 2) ::+ (s =>
          StmMap(
            StmFold(
              s,
              C(0)(U8),
              U8 ::+ (acc =>
                TyStm((U8, U8, U8), 3) ::+ (s =>
                  StmMap(
                    StmFold(
                      s,
                      C(0)(U8),
                      U8 ::+ (acc =>
                        (U8, U8, U8) ::+ (x => acc + x.__0 + x.__1 + x.__2)
                      )
                    )(),
                    U8 ::+ (x => acc + x)
                  )()
                )
              )
            )(),
            U8 ::+ (x => acc + x)
          )()
        )
      )
    )().tchk().lower
    val expected = StmLiteral(
      (0 until 2)
        .flatMap(i => (0 until 2).flatMap(j => (0 until 3).map(k => i + j + k)))
        .sum
    )()
    assert(mhir.eval.eval(sum) == expected)
  }

  test("StmFold:3D:Product") {
    val s =
      build3D(3, 2, 2, i => j => k => Tuple(C(i)(U8), C(j)(U8), C(k)(U8))())
    val prod = StmFold(
      s,
      C(1)(U32),
      U32 ::+ (acc =>
        TyStm(TyStm((U8, U8, U8), 2), 2) ::+ (s =>
          StmMap(
            StmFold(
              s,
              C(1)(U32),
              U32 ::+ (acc =>
                TyStm((U8, U8, U8), 2) ::+ (s =>
                  StmMap(
                    StmFold(
                      s,
                      C(1)(U32),
                      U32 ::+ (a =>
                        (U8, U8, U8) ::+ (x => a * (x.__0 + x.__1 + x.__2 + 1))
                      )
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
    )().tchk().lower
    val expected = {
      val s = (0 until 3).map(i =>
        (0 until 2).map(j => (0 until 2).map(k => (i, j, k)))
      )
      val prod = s.foldLeft(1)({ case (acc, s) =>
        val x = s.foldLeft(1)({ case (acc, s) =>
          val x = s.foldLeft(1)({ case (acc, x) =>
            acc * (x._1 + x._2 + x._3 + 1)
          })
          acc * x
        })
        acc * x
      })
      StmLiteral(prod)()
    }
    assert(mhir.eval.eval(prod) == expected)
  }

  test("StmScanInclusive:1D:Sum") {
    // [2, 3,  4,  5,  6]
    val s = build1D(5, i => C(i + 2)(U8))
    // [2, 7, 18, 41, 88]
    val sum =
      StmScanInclusive(s, C(0)(U8), U8 ::+ (acc => U8 ::+ (x => x + 2 * acc)))()
        .tchk()
    val expected = StmLiteral(2, 7, 18, 41, 88)()
    assert(mhir.eval.eval(sum) == expected)
  }

  test("StmScanInclusive:2D:SumRowSums") {
    // [[0, 1],
    //  [2, 3],
    //  [4, 5],
    //  [6, 7]]
    val s = build2D(4, 2, i => j => C(2 * i + j)(U8))
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
    assert(mhir.eval.eval(sums) == expected)
  }

  test("StmScanInclusive:2D:SumColumn1") {
    // [[1, 2, 3, 4],
    //  [2, 3, 4, 5],
    //  [3, 4, 5, 6],
    //  [4, 5, 6, 7]]
    val s = build2D(4, 4, i => j => C(i + j + 1)(U8))
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
    assert(mhir.eval.eval(sums) == expected)
  }

  test("StmScanExclusive:1D:Sum") {
    // [2, 3, 4,  5,  6]
    val s = build1D(5, i => C(i + 2)(U8))
    // [0, 2, 7, 18, 41]
    val sum =
      StmScanExclusive(
        s,
        C(0)(U8),
        U8 ::+ (acc => U8 ::+ (x => x + 2 * acc))
      )().tchk()
    assert(mhir.eval.eval(sum) == StmLiteral(0, 2, 7, 18, 41)())
  }

  test("StmScanExclusive:2D:SumRowSums") {
    // [[0, 1],
    //  [1, 2],
    //  [2, 3],
    //  [3, 4]]
    val s = build2D(4, 2, i => j => C(i + j)(U8))
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
    )().tchk().lower
    // scan([1, 3, 5, 7])
    val expected = StmLiteral(0, 1, 4, 9)()
    assert(mhir.eval.eval(sums) == expected)
  }

  test("StmScanExclusive:2D:SumColumn1") {
    // [[1, 2, 3, 4],
    //  [2, 3, 4, 5],
    //  [3, 4, 5, 6],
    //  [4, 5, 6, 7]]
    val s = build2D(4, 4, i => j => C(i + j + 1)(U8))
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
    assert(mhir.eval.eval(sums) == expected)
  }
}
