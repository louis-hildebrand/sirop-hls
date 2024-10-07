package opt

import org.scalatest.funsuite.AnyFunSuite
import ir.*
import operations.*

import scala.runtime.stdLibPatches.Predef.assert

class PartialEvalPassTests extends AnyFunSuite {

  def assertStreamEqual(stream: Expr, expectedSeq: Seq[Expr]): Unit = {
    var actualSeq = Seq[Expr]()
    var n: Expr = Tuple(stream, 0 /*unused*/ )
    expectedSeq.foreach(exp =>
      n = PartialEvalPass.partialEval(StmNext(n.__0))
      actualSeq = actualSeq :+ PartialEvalPass.partialEval(n.__1)
    )
    assert(actualSeq == expectedSeq)
    assert(PartialEvalPass.partialEval(StmLength(n.__0)) == IntCst(0))
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
    assert(PartialEvalPass.partialEval(e) == expected)
  }

  // Used to debug a case where partial evaluator left behind a Not(Not(...))
  test("NotNot") {
    val acc = Param()
    val e =
      IfThenElse(
        acc.__3,
        IfThenElse(Not(LessThan(acc.__4, 5)), False, True),
        False
      )
    val expected = acc.__3 && LessThan(acc.__4, 5)
    assert(PartialEvalPass.partialEval(e) == expected)
  }

  test("VecScanUnfolded") {
    val a = Param()
    val b = Param()
    val c = Param()
    val z = Param()
    val v =
      VecBuild(
        3,
        (i: Expr) => IfThenElse(i === 0, a, IfThenElse(i === 1, b, c))
      )
    val v2 = VecScan(v, z, (x: Expr) => (a: Expr) => a + x, inclusive = true)
    val pe = PartialEvalPass.partialEval
    assert(pe(VecAccess(v2, 0)) == z + a)
    assert(pe(VecAccess(v2, 1)) == (z + a) + b)
    assert(pe(VecAccess(v2, 2)) == ((z + a) + b) + c)
  }
}
