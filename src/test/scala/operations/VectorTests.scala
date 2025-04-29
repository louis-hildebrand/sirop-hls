package operations

import ir._
import opt.PartialEvalPass
import org.scalatest.funsuite.AnyFunSuite

class VectorTests extends AnyFunSuite {
  test("BuildV_and_Access") {
    val cstVec = VecBuild(2, TyInt ::+ (_ => 7))()
    assert(PartialEvalPass.partialEval(VecAccess(cstVec, 0)()) == IntCst(7))
    assert(PartialEvalPass.partialEval(VecAccess(cstVec, 1)()) == IntCst(7))

    val oneTwoThreeVec = VecBuild(3, TyInt ::+ (i => i + 1))()
    assert(ir.eval(VecAccess(oneTwoThreeVec, 0)()) == IntCst(1))
    assert(ir.eval(VecAccess(oneTwoThreeVec, 1)()) == IntCst(2))
    assert(ir.eval(VecAccess(oneTwoThreeVec, 2)()) == IntCst(3))
  }

  test("Map_and_Access") {
    val v0 = VecBuild(3, TyInt ::+ (i => i + 1))()
    val v1 = VecMap(v0, TyInt ::+ (x => x * x))().tchk()
    assert(ir.eval(VecAccess(v1, 0)()) == IntCst(1))
    assert(ir.eval(VecAccess(v1, 1)()) == IntCst(4))
    assert(ir.eval(VecAccess(v1, 2)()) == IntCst(9))
  }

  test("Fold") {
    val oneTwoThreeVec = VecBuild(3, TyInt ::+ (i => i + 1))()
    val sum =
      VecFold(oneTwoThreeVec, 7, TyInt ::+ (e1 => TyInt ::+ (e2 => e1 + e2)))()
        .tchk()
    assert(ir.eval(sum) == StmLiteral(IntCst(13))())
  }

  test("SumRows") {
    // TODO: How to handle this case?
    assume(false)
    val v =
      VecBuild(3, TyInt ::+ (i => VecBuild(2, TyInt ::+ (j => i + j))()))()
    val v2 =
      VecMap(
        v,
        TyVec(TyInt, 3) ::+ (v =>
          VecFold(v, 0, TyInt ::+ (x => TyInt ::+ (y => x + y)))()
        )
      )()
    assert(ir.eval(v2) == VecLiteral(1, 3, 5)())
  }

  test("VecScanInclusive") {
    // [2, 3,  4,  5,  6]
    val v = VecBuild(5, TyInt ::+ (i => i + 2))()
    // [2, 7, 18, 41, 88]
    val sum =
      VecScanInclusive(v, 0, TyInt ::+ (acc => TyInt ::+ (x => x + 2 * acc)))
        .tchk()
    assert(ir.eval(sum) == StmLiteral(VecLiteral(2, 7, 18, 41, 88)())())
  }

  test("VecScanExclusive") {
    // [2, 3, 4,  5,  6]
    val v = VecBuild(5, TyInt ::+ (i => i + 2))()
    // [0, 2, 7, 18, 41]
    val sum =
      VecScanExclusive(v, 0, TyInt ::+ (acc => TyInt ::+ (x => x + 2 * acc)))
        .tchk()
    assert(ir.eval(sum) == StmLiteral(VecLiteral(0, 2, 7, 18, 41)())())
  }

  test("Stm2Vec") {
    val v = Stm2Vec(StmCount(3)())().tchk()
    val expected = StmLiteral(VecLiteral.ints(0, 1, 2))()
    assert(ir.eval(v) == expected)
  }

  test("VecPrepend") {
    val v = VecBuild(3, TyInt ::+ (i => i + 5))()
    val e = IntCst(42)
    val actual = VecPrepend(v, e)()
    val expected = VecLiteral(42, 5, 6, 7)()
    assert(ir.eval(actual) == expected)
    assert(ir.eval(actual.tchk()) == expected)
  }

  test("VecAppend") {
    val v = VecBuild(3, TyInt ::+ (i => i + 5))()
    val e = IntCst(42)
    val actual = VecAppend(v, e)()
    val expected = VecLiteral(5, 6, 7, 42)()
    assert(ir.eval(actual) == expected)
    assert(ir.eval(actual.tchk()) == expected)
  }

  test("VecPrefix") {
    val v = VecBuild(3, TyInt ::+ (i => i))()
    assert(ir.eval(VecPrefix(v, 0)) == VecLiteral()())
    assert(ir.eval(VecPrefix(v, 1)) == VecLiteral(0)())
    assert(ir.eval(VecPrefix(v, 2)) == VecLiteral(0, 1)())
    assert(ir.eval(VecPrefix(v, 3)) == VecLiteral(0, 1, 2)())
  }

  test("VecSuffix") {
    val v = VecBuild(3, TyInt ::+ (i => i))()
    assert(ir.eval(VecSuffix(v, 0).tchk()) == VecLiteral()())
    assert(ir.eval(VecSuffix(v, 1).tchk()) == VecLiteral(2)())
    assert(ir.eval(VecSuffix(v, 2).tchk()) == VecLiteral(1, 2)())
    assert(ir.eval(VecSuffix(v, 3).tchk()) == VecLiteral(0, 1, 2)())
  }

  test("VecShiftLeft") {
    val v = VecBuild(3, TyInt ::+ (i => i * (i + 2)))()
    assert(ir.eval(VecShiftLeft(v, 42).tchk()) == VecLiteral(3, 8, 42)())
  }

  test("VecShiftRight") {
    val v = VecBuild(3, TyInt ::+ (i => i * (i + 2)))()
    assert(ir.eval(VecShiftRight(v, 42).tchk()) == VecLiteral(42, 0, 3)())
  }

  test("VecConcat") {
    val v1 = VecBuild(2, TyInt ::+ (i => i))()
    val v2 = VecBuild(4, TyInt ::+ (i => i))()
    assert(ir.eval(VecConcat(v1, v2).tchk()) == VecLiteral(0, 1, 0, 1, 2, 3)())
    assert(ir.eval(VecConcat(v2, v1).tchk()) == VecLiteral(0, 1, 2, 3, 0, 1)())
  }

  test("Vec2Tuple") {
    val v = VecBuild(5, TyInt ::+ (i => i * (i + 1)))()
    val expected = Tuple(0, 2, 6, 12, 20)()
    val actual = PartialEvalPass.partialEval(Vec2Tuple(v))
    assert(actual == expected)
  }

  test("VecZip") {
    val v0 = VecBuild(3, TyInt ::+ (i => i))()
    val v1 = VecBuild(3, TyInt ::+ (i => (i + 1) * 2))()
    val zipped = VecZip(v0, v1).tchk()
    val expected = VecLiteral(
      Tuple(0, 2)(),
      Tuple(1, 4)(),
      Tuple(2, 6)()
    )()
    assert(ir.eval(zipped) == expected)
  }

  test("VecRepeat") {
    val v = VecBuild(4, TyInt ::+ (i => (i + 1) * (i + 1)))()
    val v2 = VecRepeat(v, 2)
    val expected = VecLiteral(
      VecLiteral(IntCst(1), IntCst(4), IntCst(9), IntCst(16))(),
      VecLiteral(IntCst(1), IntCst(4), IntCst(9), IntCst(16))()
    )()
    assert(ir.eval(v2) == expected)
  }

  test("VecReverse") {
    val v = VecBuild(4, TyInt ::+ (i => (i + 1) * (i + 1)))()
    assert(ir.eval(VecReverse(v).tchk()) == VecLiteral(16, 9, 4, 1)())
  }

  test("VecSplit") {
    val v = VecBuild(6, TyInt ::+ (i => i * i))()
    val split = VecSplit(v, 3).tchk()
    val expected = VecLiteral(
      VecLiteral(IntCst(0), IntCst(1), IntCst(4))(),
      VecLiteral(IntCst(9), IntCst(16), IntCst(25))()
    )()
    assert(ir.eval(split) == expected)
  }

  test("VecJoin") {
    // [[0, 1],
    //  [1, 2],
    //  [2, 3]]
    val v =
      VecBuild(3, TyInt ::+ (i => VecBuild(2, TyInt ::+ (j => i + j))()))()
    // [0, 1, 1, 2, 2, 3]
    val joined = VecJoin(v)().tchk()
    assert(ir.eval(joined) == VecLiteral(0, 1, 1, 2, 2, 3)())
  }

  test("VecSlide") {
    // [0, 3, 6, 9, 12]
    val v = VecBuild(5, TyInt ::+ (i => i * 3))()
    // [[0, 3, 6],
    //  [3, 6, 9],
    //  [6, 9, 12]]
    val actual = VecSlide(v, 3).tchk()
    val expected = VecLiteral(
      VecLiteral(IntCst(0), IntCst(3), IntCst(6))(),
      VecLiteral(IntCst(3), IntCst(6), IntCst(9))(),
      VecLiteral(IntCst(6), IntCst(9), IntCst(12))()
    )()
    assert(ir.eval(actual) == expected)
  }

  test("VecTranspose") {
    val v = VecTranspose(
      VecBuild(
        3,
        TyInt ::+ (i => VecBuild(4, TyInt ::+ (j => Tuple(i, j)()))())
      )()
    ).tchk()
    val expected = VecLiteral(
      VecLiteral(Tuple(0, 0)(), Tuple(1, 0)(), Tuple(2, 0)())(),
      VecLiteral(Tuple(0, 1)(), Tuple(1, 1)(), Tuple(2, 1)())(),
      VecLiteral(Tuple(0, 2)(), Tuple(1, 2)(), Tuple(2, 2)())(),
      VecLiteral(Tuple(0, 3)(), Tuple(1, 3)(), Tuple(2, 3)())()
    )()
    assert(ir.eval(v) == expected)
  }

  test("VecTransposeTranspose") {
    val x = Param("x")()
    val v =
      VecBuild(
        4,
        TyInt ::+ (i => VecBuild(3, TyInt ::+ (j => Tuple(i, j)()))())
      )()
    val expected = VecLiteral(
      VecLiteral(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)())(),
      VecLiteral(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())(),
      VecLiteral(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)())(),
      VecLiteral(Tuple(3, 0)(), Tuple(3, 1)(), Tuple(3, 2)())()
    )()
    val actual = Let(x, v, VecTranspose(VecTranspose(x)))().tchk()

    assert(ir.eval(actual) == expected)
  }
}
