package operations

import ir._
import opt.PartialEvalPass
import org.scalatest.funsuite.AnyFunSuite

class VectorTests extends AnyFunSuite {

  test("BuildV_and_Access") {
    val cstVec = VecBuild(2, (i: Expr) => IntCst(7))()
    assert(PartialEvalPass.partialEval(VecAccess(cstVec, 0)()) == IntCst(7))
    assert(PartialEvalPass.partialEval(VecAccess(cstVec, 1)()) == IntCst(7))

    val oneTwoThreeVec = VecBuild(3, (i: Expr) => i + 1)()
    assert(
      PartialEvalPass.partialEval(VecAccess(oneTwoThreeVec, 0)()) == IntCst(1)
    )
    assert(
      PartialEvalPass.partialEval(VecAccess(oneTwoThreeVec, 1)()) == IntCst(2)
    )
    assert(
      PartialEvalPass.partialEval(VecAccess(oneTwoThreeVec, 2)()) == IntCst(3)
    )
  }

  test("Map_and_Access") {
    val v0 = VecBuild(3, (i: Expr) => i + 1)()
    val v1 = VecMap(v0, (x: Expr) => x * x)
    assert(PartialEvalPass.partialEval(VecAccess(v1, 0)()) == IntCst(1))
    assert(PartialEvalPass.partialEval(VecAccess(v1, 1)()) == IntCst(4))
    assert(PartialEvalPass.partialEval(VecAccess(v1, 2)()) == IntCst(9))
  }

  test("Fold") {
    val oneTwoThreeVec = VecBuild(3, (i: Expr) => i + 1)()
    assert(
      PartialEvalPass.partialEval(
        VecFold(oneTwoThreeVec, 7, (e1: Expr) => (e2: Expr) => e1 + e2)
      ) == IntCst(13)
    )
  }

  test("SumRows") {
    val v = VecBuild(3, (i: Expr) => VecBuild(2, (j: Expr) => i + j)())()
    val v2 =
      VecMap(v, (v: Expr) => VecFold(v, 0, (x: Expr) => (y: Expr) => x + y))
    assert(ir.eval(v2) == VecLiteral(1, 3, 5)())
  }

  test("VecScanInclusive") {
    // [2, 3,  4,  5,  6]
    val v = VecBuild(5, (i: Expr) => i + 2)()
    // [2, 7, 18, 41, 88]
    val sum =
      VecScan(v, 0, (x: Expr) => (acc: Expr) => x + 2 * acc, inclusive = true)
    assert(ir.eval(sum) == VecLiteral(2, 7, 18, 41, 88)())
  }

  test("VecScanExclusive") {
    // [2, 3, 4,  5,  6]
    val v = VecBuild(5, (i: Expr) => i + 2)()
    // [0, 2, 7, 18, 41]
    val sum =
      VecScan(v, 0, (x: Expr) => (acc: Expr) => x + 2 * acc, inclusive = false)
    assert(ir.eval(sum) == VecLiteral(0, 2, 7, 18, 41)())
  }

  test("Stm2Vec") {
    val v = Stm2Vec(StmCount(3), n = 3)
    val expected = StmLiteral(VecLiteral.ints(0, 1, 2))()
    assert(ir.eval(v) == expected)
  }

  test("VecPrepend") {
    val v = VecBuild(3, (i: Expr) => i + 5)()
    val e = IntCst(42)
    assert(ir.eval(VecPrepend(v, e)) == VecLiteral(42, 5, 6, 7)())
  }

  test("VecAppend") {
    val v = VecBuild(3, (i: Expr) => i + 5)()
    val e = IntCst(42)
    assert(ir.eval(VecAppend(v, e)) == VecLiteral(5, 6, 7, 42)())
  }

  test("VecPrefix") {
    val v = VecBuild(3, (i: Expr) => i)()
    assert(ir.eval(VecPrefix(v, 0)) == VecLiteral()())
    assert(ir.eval(VecPrefix(v, 1)) == VecLiteral(0)())
    assert(ir.eval(VecPrefix(v, 2)) == VecLiteral(0, 1)())
    assert(ir.eval(VecPrefix(v, 3)) == VecLiteral(0, 1, 2)())
  }

  test("VecSuffix") {
    val v = VecBuild(3, (i: Expr) => i)()
    assert(ir.eval(VecSuffix(v, 0)) == VecLiteral()())
    assert(ir.eval(VecSuffix(v, 1)) == VecLiteral(2)())
    assert(ir.eval(VecSuffix(v, 2)) == VecLiteral(1, 2)())
    assert(ir.eval(VecSuffix(v, 3)) == VecLiteral(0, 1, 2)())
  }

  test("VecShiftLeft") {
    val v = VecBuild(3, (i: Expr) => i * (i + 2))()
    assert(ir.eval(VecShiftLeft(v, 42)) == VecLiteral(3, 8, 42)())
  }

  test("VecShiftRight") {
    val v = VecBuild(3, (i: Expr) => i * (i + 2))()
    assert(ir.eval(VecShiftRight(v, 42)) == VecLiteral(42, 0, 3)())
  }

  test("VecConcat") {
    val v1 = VecBuild(2, (i: Expr) => i)()
    val v2 = VecBuild(4, (i: Expr) => i)()
    assert(ir.eval(VecConcat(v1, v2)) == VecLiteral(0, 1, 0, 1, 2, 3)())
    assert(ir.eval(VecConcat(v2, v1)) == VecLiteral(0, 1, 2, 3, 0, 1)())
  }

  test("Vec2Tuple") {
    val v = VecBuild(5, (i: Expr) => i * (i + 1))()
    val expected = Tuple(0, 2, 6, 12, 20)()
    val actual = PartialEvalPass.partialEval(Vec2Tuple(v))
    assert(actual == expected)
  }

  test("VecZip") {
    val v0 = VecBuild(3, (i: Expr) => i)()
    val v1 = VecBuild(3, (i: Expr) => (i + 1) * 2)()
    val zipped = VecZip(v0, v1)
    assert(
      ir.eval(zipped) == VecLiteral(
        Tuple(0, 2)(),
        Tuple(1, 4)(),
        Tuple(2, 6)()
      )()
    )
  }

  test("VecZipAlternating") {
    val v0 = VecBuild(4, (i: Expr) => i)()
    val v1 = VecBuild(4, (i: Expr) => (i + 1) * 2)()
    val zipped = VecZipAlternating(v0, v1)
    assert(
      ir.eval(zipped)
        == VecLiteral(
          Tuple(0, 2)(),
          Tuple(4, 1)(),
          Tuple(2, 6)(),
          Tuple(8, 3)()
        )()
    )
  }

  test("VecRepeat") {
    val v = VecBuild(4, (i: Expr) => (i + 1) * (i + 1))()
    val v2 = VecRepeat(v, 2)
    val expected = VecLiteral(
      VecLiteral(IntCst(1), IntCst(4), IntCst(9), IntCst(16))(),
      VecLiteral(IntCst(1), IntCst(4), IntCst(9), IntCst(16))()
    )()
    assert(ir.eval(v2) == expected)
  }

  test("VecReverse") {
    val v = VecBuild(4, (i: Expr) => (i + 1) * (i + 1))()
    assert(ir.eval(VecReverse(v)) == VecLiteral(16, 9, 4, 1)())
  }

  test("VecSplit") {
    val v = VecBuild(6, (i: Expr) => i * i)()
    val split = VecSplit(v, 3)
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
    val v = VecBuild(3, (i: Expr) => VecBuild(2, (j: Expr) => i + j)())()
    // [0, 1, 1, 2, 2, 3]
    val joined = VecJoin(v)
    assert(ir.eval(joined) == VecLiteral(0, 1, 1, 2, 2, 3)())
  }

  test("VecSlide") {
    // [0, 3, 6, 9, 12]
    val v = VecBuild(5, (i: Expr) => i * 3)()
    // [[0, 3, 6],
    //  [3, 6, 9],
    //  [6, 9, 12]]
    val actual = VecSlide(v, 3)
    val expected = VecLiteral(
      VecLiteral(IntCst(0), IntCst(3), IntCst(6))(),
      VecLiteral(IntCst(3), IntCst(6), IntCst(9))(),
      VecLiteral(IntCst(6), IntCst(9), IntCst(12))()
    )()
    assert(ir.eval(actual) == expected)
  }

  test("VecTranspose") {
    val v =
      VecBuild(3, (i: Expr) => VecBuild(4, (j: Expr) => Tuple(i, j)())())()
    val expected = VecLiteral(
      VecLiteral(Tuple(0, 0)(), Tuple(1, 0)(), Tuple(2, 0)())(),
      VecLiteral(Tuple(0, 1)(), Tuple(1, 1)(), Tuple(2, 1)())(),
      VecLiteral(Tuple(0, 2)(), Tuple(1, 2)(), Tuple(2, 2)())(),
      VecLiteral(Tuple(0, 3)(), Tuple(1, 3)(), Tuple(2, 3)())()
    )()
    assert(ir.eval(VecTranspose(v)) == expected)
  }

  test("VecTransposeTranspose") {
    val x = Param("x")
    val v =
      VecBuild(4, (i: Expr) => VecBuild(3, (j: Expr) => Tuple(i, j)())())()
    val expected = VecLiteral(
      VecLiteral(Tuple(0, 0)(), Tuple(0, 1)(), Tuple(0, 2)())(),
      VecLiteral(Tuple(1, 0)(), Tuple(1, 1)(), Tuple(1, 2)())(),
      VecLiteral(Tuple(2, 0)(), Tuple(2, 1)(), Tuple(2, 2)())(),
      VecLiteral(Tuple(3, 0)(), Tuple(3, 1)(), Tuple(3, 2)())()
    )()
    val actual = VecTranspose(VecTranspose(x))

    // Correctness
    assert(ir.eval(Let(x, v, actual)()) == expected)
    // Effective simplification
    for (i <- (0 until 4)) {
      for (j <- (0 until 3)) {
        assert(
          PartialEvalPass.partialEval(
            VecAccess(VecAccess(actual, i)(), j)()
          ) == VecAccess(VecAccess(x, i)(), j)()
        )
      }
    }
  }
}
