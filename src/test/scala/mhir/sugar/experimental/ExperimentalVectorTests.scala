package mhir.sugar
package experimental

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class ExperimentalVectorTests extends AnyFunSuite {

  test("VecBuild:StmSum") {
    val n = 5
    val m = 3
    val e = VecBuild(
      n,
      U32 ::+ (i => StmFold(StmCst(m, i * i)(), C(0)(U32), PlusFunction(U32))())
    )().tchk().lower
    val expected = StmLiteral(
      VecLiteral(
        (0 until n).map(i => IntCst((0 until m).map(_ => i * i).sum)()): _*
      )()
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("VecMap:VecMap(StmScan)") {
    val n = 5
    val m = 3
    val vs = VecBuild(n, U32 ::+ (i => StmRange(m, i, C(1)(U32))()))()
    val e = VecMap(
      vs,
      TyStm(U32, m) ::+ (s =>
        StmScanInclusive(s, C(0)(U32), PlusFunction(U32))()
      )
    )().tchk().lower
    val expected = StmLiteral(
      (0 until m).map(t =>
        VecLiteral((0 until n).map(i => IntCst((i to i + t).sum)()): _*)()
      ): _*
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("VecFoldSeq:sum") {
    val oneTwoThreeVec = VecBuild(3, U32 ::+ (i => i + 1))()
    val sum =
      VecFoldSeq(oneTwoThreeVec, C(7)(U32), PlusFunction(U32))().tchk()
    assert(mhir.eval.eval(sum) == StmLiteral(IntCst(13)())())
  }

  test("VecFoldSeq:HornersMethod") {
    // [2, 3, 4, 5]
    // i.e., 2x^3 + 3x^2 + 4x + 5
    // i.e., 5 + x*(4 + x*(3 + x*2))
    val v = VecBuild(4, U32 ::+ (i => i + 2))()
    val x = C(10)(U32)
    val result =
      VecFoldSeq(v, C(0)(U32), U32 ::+ (acc => U32 ::+ (a => a + x * acc)))()
        .tchk()
        .lower
    assert(mhir.eval.eval(result) == StmLiteral(C(2345)())())
  }

  test("VecScanInclusive") {
    // [2, 3,  4,  5,  6]
    val v = VecBuild(5, U32 ::+ (i => i + 2))()
    // [2, 7, 18, 41, 88]
    val sum =
      VecScanInclusive(
        v,
        C(0)(U32),
        U32 ::+ (acc => U32 ::+ (x => x + 2 * acc))
      ).tchk()
    assert(mhir.eval.eval(sum) == StmLiteral(VecLiteral(2, 7, 18, 41, 88)())())
  }

  test("VecScanExclusive") {
    // [2, 3, 4,  5,  6]
    val v = VecBuild(5, U8 ::+ (i => i + 2))()
    // [0, 2, 7, 18, 41]
    val sum =
      VecScanExclusive(v, C(0)(U8), U8 ::+ (acc => U8 ::+ (x => x + 2 * acc)))
        .tchk()
    assert(mhir.eval.eval(sum) == StmLiteral(VecLiteral(0, 2, 7, 18, 41)())())
  }
}
