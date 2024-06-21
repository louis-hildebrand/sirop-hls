import org.scalatest.funsuite.AnyFunSuite

import scala.runtime.stdLibPatches.Predef.assert

class PartialInterpretationTests extends AnyFunSuite {

  def assertStreamEqual(stream: Expr, expectedSeq: Seq[Expr]) = {
    var actualSeq = Seq[Expr]()
    var n: Expr = Tuple(stream, 0 /*unused*/)
    expectedSeq.foreach(exp =>
      n = ExprInterpreter.partialInterpret(StmNext(n.__0))
      actualSeq = actualSeq :+ ExprInterpreter.partialInterpret(n.__1)
    )
    assert(actualSeq == expectedSeq)
    assert(ExprInterpreter.partialInterpret(StmLength(n.__0)) == IntCst(0))
  }

  test("Stm2Vec2Stm") {
    val cntAst = CounterStream(3)
    val stream = Vec2Stm(Stm2Vec(cntAst))
    assertStreamEqual(stream,Seq(0,1,2))

    val partialEval = ExprInterpreter.partialInterpret(stream)
    assertStreamEqual(partialEval,Seq(0,1,2))

    // TODO: check that partialEval does not contain any vector!
  }

}
