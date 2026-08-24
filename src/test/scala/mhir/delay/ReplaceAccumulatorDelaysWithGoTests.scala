package mhir.delay

import mhir.canonicalize._
import mhir.ir._
import mhir.sem.SemanticError
import mhir.sugar.{SimpleCount, SimpleMap, SimpleZip}
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class ReplaceAccumulatorDelaysWithGoTests extends AnyFunSuite {

  private def assertNoAccumulatorDelays(e: Expr): Unit = {
    e match {
      case s: StmBuild =>
        s.accumulators.foreach({ case (x, (_, _, delay)) =>
          assert(
            delay.typ == TyTuple(),
            s"accumulator $x should have no delay annotation"
          )
        })
      case e => e.children.foreach(assertNoAccumulatorDelays)
    }
  }

  test("StmZipWithIndex") {
    val n = 8
    val input = Param("input")(TyStm(I16, n))
    val original = {
      val a = Param("a")(U8)
      val p = Param("p")(TyStm(I16, -1))
      StmBuild(
        n,
        C(1)(),
        Undefined(Missing),
        Tuple(a, StmData(p)())(),
        True,
        Map(a -> (C(0)(U8), Sum(C(1)(U8), a)(), C(1)())),
        Map(p -> (input, True, C(0)()))
      )().tchk()
    }
    val go = Param("go")(TyStm(TyBool, n))
    val actual = new ReplaceAccumulatorDelaysWithGo(Some(go)).apply(original)

    // Correctness
    val inputs = Map(
      input -> StmLiteral(
        Seq(Undefined(I16)),
        (0 until n).map(t => t * (t + 1)).map(C(_)(I16))
      )(Missing).tchk(),
      go -> StmLiteral(Seq(False), (0 until n).map(_ => True))(Missing).tchk()
    )
    val expectedVal = mhir.eval
      .eval(original, handshake = false, inputs = inputs)
      .asInstanceOf[StmLiteral]
    val actualVal = mhir.eval
      .eval(actual, handshake = false, inputs = inputs)
      .asInstanceOf[StmLiteral]
    assert(actualVal.physical.length == expectedVal.physical.length)
    assert(actualVal.logical == expectedVal.logical)

    // Transformation
    assertNoAccumulatorDelays(actual)
    assert(actual.freeVars == Set(input, go))
  }

  test("NoGoParam:OK") {
    val n = 8
    val input1 = Param("input")(TyStm(U8, n))
    val input2 = Param("input")(TyStm(U8, n))
    val original = SimpleZip(
      SimpleMap(input1, x => Sum(C(5)(U8), x)()),
      SimpleMap(input2, x => Sum(C(1)(U8), x)())
    )
    // Just check that there's no error
    new ReplaceAccumulatorDelaysWithGo(None).apply(original)
  }

  test("NoGoParam:Error") {
    val n = 8
    val original = SimpleMap(SimpleCount(C(n)(U8)), x => x)

    // Error if no "go" stream
    val ex = intercept[SemanticError](
      new ReplaceAccumulatorDelaysWithGo(None).apply(original)
    )
    assert(
      ex.msg.contains(
        "the program seems to be latency-sensitive (e.g., in SimpleCount), but no 'go' stream is specified"
      )
    )

    // No error if "go" stream is provided
    val go = Param("go")(TyStm(TyBool, n))
    val actual = new ReplaceAccumulatorDelaysWithGo(Some(go)).apply(original)

    // Correctness
    val inputs = Map(
      go -> StmLiteral(Seq(), (0 until n).map(_ => True))(Missing).tchk()
    )
    val expectedVal = mhir.eval
      .eval(original, handshake = false, inputs = inputs)
      .asInstanceOf[StmLiteral]
    val actualVal = mhir.eval
      .eval(actual, handshake = false, inputs = inputs)
      .asInstanceOf[StmLiteral]
    assert(actualVal.physical.length == expectedVal.physical.length)
    assert(actualVal.logical == expectedVal.logical)

    // Transformation
    assertNoAccumulatorDelays(actual)
    assert(actual.freeVars == Set(go))
  }
}
