import org.scalatest.funsuite.AnyFunSuite

import scala.runtime.stdLibPatches.Predef.assert

class VectorTests extends AnyFunSuite {
  def assertVecEqual(actual: Expr, expectedElems: Seq[Expr]): Unit = {
    val actualBuild = ExprEvaluator.partialEval(actual).asInstanceOf[VecBuild]
    println(actualBuild)
    val actualLen = actualBuild.len.asInstanceOf[IntCst].i
    val actualElems = (0 until actualLen).map(i => ExprEvaluator.partialEval(VecAccess(actualBuild, i)))
    assert(actualElems == expectedElems)
  }

  test("BuildV_and_Access") {
    val cstVec = VecBuild(2, (i:Expr) => IntCst(7))
    assert(ExprEvaluator.partialEval(VecAccess(cstVec, 0)) == IntCst(7))
    assert(ExprEvaluator.partialEval(VecAccess(cstVec, 1)) == IntCst(7))

    val oneTwoThreeVec = VecBuild(3, (i:Expr) => i+1)
    assert(ExprEvaluator.partialEval(VecAccess(oneTwoThreeVec, 0)) == IntCst(1))
    assert(ExprEvaluator.partialEval(VecAccess(oneTwoThreeVec, 1)) == IntCst(2))
    assert(ExprEvaluator.partialEval(VecAccess(oneTwoThreeVec, 2)) == IntCst(3))
  }

  test("Map_and_Access") {
    val v0 = VecBuild(3, (i:Expr) => i+1)
    val v1 = MapV(v0, (x: Expr) => x * x)
    assert(ExprEvaluator.partialEval(VecAccess(v1, 0)) == IntCst(1))
    assert(ExprEvaluator.partialEval(VecAccess(v1, 1)) == IntCst(4))
    assert(ExprEvaluator.partialEval(VecAccess(v1, 2)) == IntCst(9))
  }

  test("Fold") {
    val oneTwoThreeVec = VecBuild(3, (i:Expr) => i+1)
    assert(ExprEvaluator.partialEval(VecFold(oneTwoThreeVec, 7, (e1:Expr) => (e2:Expr) => e1+e2)) == IntCst(13))
  }

  test("Stm2Vec") {
    val cntAst = CounterStream(3)
    val v = Stm2Vec(cntAst)
    assertVecEqual(v, Seq(0, 1, 2))
  }

  test("VecZip") {
    val v0 = VecBuild(3, (i: Expr) => i)
    val v1 = VecBuild(3, (i: Expr) => (i + 1) * 2)
    val zipped = VecZip(v0, v1)
    assertVecEqual(zipped, Seq(Tuple(0, 2), Tuple(1, 4), Tuple(2, 6)))
  }

  test("VecSplit") {
    // [0, 1, 4, 9, 16, 25]
    val v = VecBuild(6, (i: Expr) => i * i)
    // [[ 0,  1,  4],
    //  [ 9, 16, 25]]
    val split = VecSplit(v, 3)
    assert(ExprEvaluator.partialEval(VecAccess(VecAccess(split, 0), 0)) == IntCst(0))
    assert(ExprEvaluator.partialEval(VecAccess(VecAccess(split, 0), 1)) == IntCst(1))
    assert(ExprEvaluator.partialEval(VecAccess(VecAccess(split, 0), 2)) == IntCst(4))
    assert(ExprEvaluator.partialEval(VecAccess(VecAccess(split, 1), 0)) == IntCst(9))
    assert(ExprEvaluator.partialEval(VecAccess(VecAccess(split, 1), 1)) == IntCst(16))
    assert(ExprEvaluator.partialEval(VecAccess(VecAccess(split, 1), 2)) == IntCst(25))
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
}
