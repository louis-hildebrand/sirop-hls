import org.scalatest.funsuite.AnyFunSuite

import scala.runtime.stdLibPatches.Predef.assert

class VectorTests extends AnyFunSuite {

  test("BuildV_and_Access") {
    val cstVec = VecBuild(2, (i:Expr) => IntCst(7))
    assert(ExprInterpreter.partialInterpret(VecAccess(cstVec, 0)) == IntCst(7))
    assert(ExprInterpreter.partialInterpret(VecAccess(cstVec, 1)) == IntCst(7))

    val oneTwoThreeVec = VecBuild(3, (i:Expr) => i+1)
    assert(ExprInterpreter.partialInterpret(VecAccess(oneTwoThreeVec, 0)) == IntCst(1))
    assert(ExprInterpreter.partialInterpret(VecAccess(oneTwoThreeVec, 1)) == IntCst(2))
    assert(ExprInterpreter.partialInterpret(VecAccess(oneTwoThreeVec, 2)) == IntCst(3))
  }

  test("Fold") {
    val oneTwoThreeVec = VecBuild(3, (i:Expr) => i+1)
    assert(ExprInterpreter.partialInterpret(VecFold(oneTwoThreeVec, 7, (e1:Expr) => (e2:Expr) => e1+e2)) == IntCst(13))
  }

  test("Stm2Vec") {
    val cntAst = CounterStream(3)
    val v = Stm2Vec(cntAst)
    assert(ExprInterpreter.partialInterpret(VecAccess(v, 0)) == IntCst(0))
    assert(ExprInterpreter.partialInterpret(VecAccess(v, 1)) == IntCst(1))
    assert(ExprInterpreter.partialInterpret(VecAccess(v, 2)) == IntCst(2))
  }

}
