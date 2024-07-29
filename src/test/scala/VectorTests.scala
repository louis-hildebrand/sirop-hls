import org.scalatest.funsuite.AnyFunSuite

import scala.runtime.stdLibPatches.Predef.assert

class VectorTests extends AnyFunSuite {

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
    assert(ExprEvaluator.partialEval(VecAccess(v, 0)) == IntCst(0))
    assert(ExprEvaluator.partialEval(VecAccess(v, 1)) == IntCst(1))
    assert(ExprEvaluator.partialEval(VecAccess(v, 2)) == IntCst(2))
  }

}
