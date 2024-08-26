import org.scalatest.funsuite.AnyFunSuite

import scala.runtime.stdLibPatches.Predef.assert

class PartialEvaluationTests extends AnyFunSuite {

  def assertStreamEqual(stream: Expr, expectedSeq: Seq[Expr]): Unit = {
    var actualSeq = Seq[Expr]()
    var n: Expr = Tuple(stream, 0 /*unused*/ )
    expectedSeq.foreach(exp =>
      n = ExprEvaluator.partialEval(StmNext(n.__0))
      actualSeq = actualSeq :+ ExprEvaluator.partialEval(n.__1)
    )
    assert(actualSeq == expectedSeq)
    assert(ExprEvaluator.partialEval(StmLength(n.__0)) == IntCst(0))
  }

  // Used to debug issue with StmFold
  test("FunCall") {
    val x = Param()
    val e = FunCall(
      (y: Expr) => Tuple(StmNext(y.__1).__1 + y.__0, StmNext(y.__1).__0),
      x
    )
    val expected =
      Tuple(StmNext(x.__1).__1 + x.__0, StmNext(x.__1).__0)
    assert(ExprEvaluator.partialEval(e) == expected)
  }

  test("Stm2Vec2Stm") {
    val cntAst = StmCount(3)
    val stream = Vec2Stm(Stm2Vec(cntAst))
    assertStreamEqual(stream, Seq(0, 1, 2))

    val partialEval = ExprEvaluator.partialEval(stream)
    assertStreamEqual(partialEval, Seq(0, 1, 2))

    // TODO: check that partialEval does not contain any vector!
  }

  test("VecScanUnfolded") {
    val a = Param()
    val b = Param()
    val c = Param()
    val z = IntCst(0)
    val v =
      VecBuild(
        3,
        (i: Expr) => IfThenElse(i === 0, a, IfThenElse(i === 1, b, c))
      )
    val v2 = VecScan(v, z, (x: Expr) => (a: Expr) => a + x, inclusive = true)
    val pe = ExprEvaluator.partialEval
    assert(pe(VecAccess(v2, 0)) == z + a)
    assert(pe(VecAccess(v2, 1)) == (z + a) + b)
    assert(pe(VecAccess(v2, 2)) == ((z + a) + b) + c)
  }
}
