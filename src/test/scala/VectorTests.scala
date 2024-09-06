import org.scalatest.funsuite.AnyFunSuite

import scala.runtime.stdLibPatches.Predef.assert

object VectorTests {
  def vec2Seq(vec: Expr): Seq[Expr] = {
    val build = ExprEvaluator.partialEval(vec).asInstanceOf[VecBuild]
    val len = build.len.asInstanceOf[IntCst].i
    (0 until len).map(i => ExprEvaluator.partialEval(VecAccess(build, i)))
  }

  def vecVec2SeqSeq(vec: Expr): Seq[Seq[Expr]] =
    vec2Seq(vec).map(e => vec2Seq(e))
}

class VectorTests extends AnyFunSuite {
  def assertVecEqual(actual: Expr, expectedElems: Seq[Expr]): Unit = {
    assert(VectorTests.vec2Seq(actual) == expectedElems)
  }

  def assert2DVecEqual(actual: Expr, expected: Seq[Seq[Expr]]): Unit = {
    assert(VectorTests.vecVec2SeqSeq(actual) == expected)
  }

  test("BuildV_and_Access") {
    val cstVec = VecBuild(2, (i: Expr) => IntCst(7))
    assert(ExprEvaluator.partialEval(VecAccess(cstVec, 0)) == IntCst(7))
    assert(ExprEvaluator.partialEval(VecAccess(cstVec, 1)) == IntCst(7))

    val oneTwoThreeVec = VecBuild(3, (i: Expr) => i + 1)
    assert(ExprEvaluator.partialEval(VecAccess(oneTwoThreeVec, 0)) == IntCst(1))
    assert(ExprEvaluator.partialEval(VecAccess(oneTwoThreeVec, 1)) == IntCst(2))
    assert(ExprEvaluator.partialEval(VecAccess(oneTwoThreeVec, 2)) == IntCst(3))
  }

  test("Map_and_Access") {
    val v0 = VecBuild(3, (i: Expr) => i + 1)
    val v1 = VecMap(v0, (x: Expr) => x * x)
    assert(ExprEvaluator.partialEval(VecAccess(v1, 0)) == IntCst(1))
    assert(ExprEvaluator.partialEval(VecAccess(v1, 1)) == IntCst(4))
    assert(ExprEvaluator.partialEval(VecAccess(v1, 2)) == IntCst(9))
  }

  test("Fold") {
    val oneTwoThreeVec = VecBuild(3, (i: Expr) => i + 1)
    assert(
      ExprEvaluator.partialEval(
        VecFold(oneTwoThreeVec, 7, (e1: Expr) => (e2: Expr) => e1 + e2)
      ) == IntCst(13)
    )
  }

  test("SumRows") {
    val v = VecBuild(3, (i: Expr) => VecBuild(2, (j: Expr) => i + j))
    val v2 =
      VecMap(v, (v: Expr) => VecFold(v, 0, (x: Expr) => (y: Expr) => x + y))
    assert(VectorTests.vec2Seq(v2) == Seq(1, 3, 5).map(n => IntCst(n)))
  }

  test("VecScanInclusive") {
    // [2, 3,  4,  5,  6]
    val v = VecBuild(5, (i: Expr) => i + 2)
    // [2, 7, 18, 41, 88]
    val sum =
      VecScan(v, 0, (x: Expr) => (acc: Expr) => x + 2 * acc, inclusive = true)
    assertVecEqual(sum, Seq(2, 7, 18, 41, 88))
  }

  test("VecScanExclusive") {
    // [2, 3, 4,  5,  6]
    val v = VecBuild(5, (i: Expr) => i + 2)
    // [0, 2, 7, 18, 41]
    val sum =
      VecScan(v, 0, (x: Expr) => (acc: Expr) => x + 2 * acc, inclusive = false)
    assertVecEqual(sum, Seq(0, 2, 7, 18, 41))
  }

  test("Stm2Vec") {
    val v = Stm2Vec(StmCount(3), n = 3)
    assertVecEqual(v, Seq(0, 1, 2))
  }

  test("VecPrepend") {
    val v = VecBuild(3, (i: Expr) => i + 5)
    val e = IntCst(42)
    assertVecEqual(VecPrepend(v, e), Seq(42, 5, 6, 7))
  }

  test("VecAppend") {
    val v = VecBuild(3, (i: Expr) => i + 5)
    val e = IntCst(42)
    assertVecEqual(VecAppend(v, e), Seq(5, 6, 7, 42))
  }

  test("VecPrefix") {
    val v = VecBuild(3, (i: Expr) => i)
    assertVecEqual(VecPrefix(v, 0), Seq())
    assertVecEqual(VecPrefix(v, 1), Seq(0))
    assertVecEqual(VecPrefix(v, 2), Seq(0, 1))
    assertVecEqual(VecPrefix(v, 3), Seq(0, 1, 2))
  }

  test("VecSuffix") {
    val v = VecBuild(3, (i: Expr) => i)
    assertVecEqual(VecSuffix(v, 0), Seq())
    assertVecEqual(VecSuffix(v, 1), Seq(2))
    assertVecEqual(VecSuffix(v, 2), Seq(1, 2))
    assertVecEqual(VecSuffix(v, 3), Seq(0, 1, 2))
  }

  test("VecShiftLeft") {
    val v = VecBuild(3, (i: Expr) => i * (i + 2))
    assertVecEqual(VecShiftLeft(v, 42), Seq(3, 8, 42))
  }

  test("VecShiftRight") {
    val v = VecBuild(3, (i: Expr) => i * (i + 2))
    assertVecEqual(VecShiftRight(v, 42), Seq(42, 0, 3))
  }

  test("VecConcat") {
    val v1 = VecBuild(2, (i: Expr) => i)
    val v2 = VecBuild(4, (i: Expr) => i)
    assertVecEqual(VecConcat(v1, v2), Seq(0, 1, 0, 1, 2, 3))
    assertVecEqual(VecConcat(v2, v1), Seq(0, 1, 2, 3, 0, 1))
  }

  test("Vec2Tuple") {
    val v = VecBuild(5, (i: Expr) => i * (i + 1))
    val expected = Tuple(0, 2, 6, 12, 20)
    val actual = ExprEvaluator.partialEval(Vec2Tuple(v))
    assert(actual == expected)
  }

  test("VecZip") {
    val v0 = VecBuild(3, (i: Expr) => i)
    val v1 = VecBuild(3, (i: Expr) => (i + 1) * 2)
    val zipped = VecZip(v0, v1)
    assertVecEqual(zipped, Seq(Tuple(0, 2), Tuple(1, 4), Tuple(2, 6)))
  }

  test("VecZipAlternating") {
    val v0 = VecBuild(4, (i: Expr) => i)
    val v1 = VecBuild(4, (i: Expr) => (i + 1) * 2)
    val zipped = VecZipAlternating(v0, v1)
    assertVecEqual(
      zipped,
      Seq(Tuple(0, 2), Tuple(4, 1), Tuple(2, 6), Tuple(8, 3))
    )
  }

  test("VecRepeat") {
    val v = VecBuild(4, (i: Expr) => (i + 1) * (i + 1))
    val v2 = VecRepeat(v, 2)
    val expected = Seq(
      Seq(IntCst(1), IntCst(4), IntCst(9), IntCst(16)),
      Seq(IntCst(1), IntCst(4), IntCst(9), IntCst(16))
    )
    assert2DVecEqual(v2, expected)
  }

  test("VecSplit") {
    val v = VecBuild(6, (i: Expr) => i * i)
    val split = VecSplit(v, 3)
    val expected = Seq(
      Seq(IntCst(0), IntCst(1), IntCst(4)),
      Seq(IntCst(9), IntCst(16), IntCst(25))
    )
    assert2DVecEqual(split, expected)
  }

  test("VecJoin") {
    // [[0, 1],
    //  [1, 2],
    //  [2, 3]]
    val v = VecBuild(3, (i: Expr) => VecBuild(2, (j: Expr) => i + j))
    // [0, 1, 1, 2, 2, 3]
    val joined = VecJoin(v)
    assertVecEqual(joined, Seq(0, 1, 1, 2, 2, 3))
  }

  test("VecSlide") {
    // [0, 3, 6, 9, 12]
    val v = VecBuild(5, (i: Expr) => i * 3)
    // [[0, 3, 6],
    //  [3, 6, 9],
    //  [6, 9, 12]]
    val actual = VecSlide(v, 3)
    val expected = Seq(
      Seq(IntCst(0), IntCst(3), IntCst(6)),
      Seq(IntCst(3), IntCst(6), IntCst(9)),
      Seq(IntCst(6), IntCst(9), IntCst(12))
    )
    assert2DVecEqual(actual, expected)
  }
}
