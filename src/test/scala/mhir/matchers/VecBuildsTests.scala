package mhir.matchers

import org.scalatest.funsuite.AnyFunSuite
import mhir.ir._
import mhir.typecheck._
import mhir.canonicalize._

class VecBuildsTests extends AnyFunSuite {

  test("Vec[Vec[(u8, u8), 3], 4]") {
    val originalLengths = Seq(C(4)(U8), C(3)(U8))
    val i = Param("i")(U8)
    val j = Param("j")(U8)
    val originalIndices = Seq(i, j)
    val originalBody = Tuple(i, j)()
    val vec = VecBuilds(originalLengths, originalIndices, originalBody).tchk()

    // Correct value
    val expected = VecLiteral(
      VecLiteral(
        Tuple(C(0)(U8), C(0)(U8))(),
        Tuple(C(0)(U8), C(1)(U8))(),
        Tuple(C(0)(U8), C(2)(U8))()
      )(),
      VecLiteral(
        Tuple(C(1)(U8), C(0)(U8))(),
        Tuple(C(1)(U8), C(1)(U8))(),
        Tuple(C(1)(U8), C(2)(U8))()
      )(),
      VecLiteral(
        Tuple(C(2)(U8), C(0)(U8))(),
        Tuple(C(2)(U8), C(1)(U8))(),
        Tuple(C(2)(U8), C(2)(U8))()
      )(),
      VecLiteral(
        Tuple(C(3)(U8), C(0)(U8))(),
        Tuple(C(3)(U8), C(1)(U8))(),
        Tuple(C(3)(U8), C(2)(U8))()
      )()
    )().tchk()
    val actual = mhir.eval.eval(vec)
    assert(actual == expected)

    // Unapply works
    vec match {
      case VecBuilds(lengths, indices, body) =>
        assert(lengths == originalLengths.map(_.i))
        assert(indices == originalIndices)
        assert(body == originalBody)
      case _ =>
        fail("unapply did not match")
    }
  }
}
