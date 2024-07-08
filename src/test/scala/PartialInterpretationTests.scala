import org.scalatest.funsuite.AnyFunSuite

import scala.runtime.stdLibPatches.Predef.assert

class PartialEvaluationTests extends AnyFunSuite {

  def assertStreamEqual(stream: Expr, expectedSeq: Seq[Expr]) = {
    var actualSeq = Seq[Expr]()
    var n: Expr = Tuple(stream, 0 /*unused*/)
    expectedSeq.foreach(exp =>
      n = ExprEvaluator.partialEval(StmNext(n.__0))
      actualSeq = actualSeq :+ ExprEvaluator.partialEval(n.__1)
    )
    assert(actualSeq == expectedSeq)
    assert(ExprEvaluator.partialEval(StmLength(n.__0)) == IntCst(0))
  }

  test("Stm2Vec2Stm") {
    val cntAst = CounterStream(3)
    val stream = Vec2Stm(Stm2Vec(cntAst))
    assertStreamEqual(stream,Seq(0,1,2))

    val partialEval = ExprEvaluator.partialEval(stream)
    assertStreamEqual(partialEval,Seq(0,1,2))

    // TODO: check that partialEval does not contain any vector!
  }

}
