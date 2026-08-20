package mhir.eval

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class EvalNoHandshakeTests extends AnyFunSuite {

  test("DelayMismatch") {
    val n = 5
    val a = StmRange(n, C(0)(U8), C(1)(U8))().tchk().lower
    val b = StmRange(n, C(-2)(I8), C(1)(I8))().tchk().lower
    val original =
      SimpleZip(a, SimpleMap(b, x => Sum(x, C(-5)(I8))()))
        .tchk()
        .lower

    assertThrows[DelayMismatch.type](
      mhir.eval.eval(original, handshake = false)
    )
  }

  test("StmCount") {
    val n = 8
    val original = StmCount(C(n)(U8))()
    val actual = mhir.eval.eval(original, handshake = false)
    val expected = StmLiteral(
      Seq(Undefined(U8)),
      (0 until n).map(t => C(t)(U8))
    )(Missing).tchk()
    assert(actual == expected)
  }

  test("StmMap") {
    val n = 8
    val s = Param("s")(TyStm(U16, n))
    val original = SimpleMap(s, x => Prod(x, x)())
    val inputs = Map(s -> StmRange(n, C(2)(U16), C(3)(U16))())
    val actual = mhir.eval.eval(original, handshake = false, inputs = inputs)
    val expected = StmLiteral(
      Seq(Undefined(U16), Undefined(U16)),
      (0 until n).map(t => 2 + 3 * t).map(t => t * t).map(C(_)(U16))
    )(Missing).tchk()
    assert(actual == expected)
  }

  for (delay <- 0 to 3) {
    test(s"ZipWithIndex:$delay") {
      val n = 5
      val input = Param("input")(TyStm(I32, n))
      val original = SimpleZip(
        SimpleNop(SimpleCount(C(n)(U8)), delay = math.max(1, delay) - 1),
        SimpleNop(input, delay = math.max(1, delay) - delay)
      ).tchk().lower
      val logical = (-3 until n - 3).map(t => t * t * t).map(C(_)(I32))
      val inputs = Map(
        input -> StmLiteral(
          (0 until delay).map(_ => Undefined(I32)),
          logical
        )(Missing).tchk()
      )
      val actual = mhir.eval
        .eval(original, handshake = false, inputs = inputs)
        .asInstanceOf[StmLiteral]
      val expectedLatency = 1 + math.max(1, delay)
      assert(actual.physical.length == expectedLatency)
      val expected =
        (0 until n).map(t => Tuple(C(t)(U8), logical(t))().tchk())
      assert(actual.logical == expected)
    }
  }
}
