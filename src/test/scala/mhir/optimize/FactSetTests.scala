package mhir.optimize

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.optimize.{PartialEvalPass => PE}
import mhir.sugar._
import org.scalatest.funsuite.AnyFunSuite

class FactSetTests extends AnyFunSuite {
  test("GetRange:ToSigned") {
    val t = Param("t")(U8)
    val u8Range = ScalarRange(Some(0), Some(256))
    val range = ScalarRange(None, Some(10))
    val combinedRange = ScalarRange(Some(0), Some(10))
    val facts = FactSet().range(t, range)
    assert(FactSet().getRange(ToSigned(t)()).contains(u8Range))
    assert(facts.getRange(ToSigned(t)()).contains(combinedRange))
  }

  test("GetRange:PadTo") {
    val t = Param("t")(I16)
    val i16Range = ScalarRange(Some(-32768), Some(32768))
    val range = ScalarRange(Some(0), None)
    val combinedRange = ScalarRange(Some(0), Some(32768))
    val facts = FactSet().range(t, range)
    assert(FactSet().getRange(PadTo(t, 32)()).contains(i16Range))
    assert(facts.getRange(PadTo(t, 32)()).contains(combinedRange))
  }

  test("AssumeTrue:-5 + t < 5") {
    val t = Param("t")(U8)
    val cond = PE.partialEval((-5 + t < 5).tchk().lower())
    val facts = FactSet().assumeTrue(cond)
    val expectedRange = ScalarRange(Some(0), Some(10))
    val actualRange = facts.getRange(t)
    assert(actualRange.contains(expectedRange))
  }

  test("AssumeTrue:10 < x") {
    val x = Param("x")(U16)
    val cond = PE.partialEval((10 < x).tchk().lower())
    val facts = FactSet().assumeTrue(cond)
    val expectedRange = ScalarRange(Some(11), Some(65536))
    val actualRange = facts.getRange(x)
    assert(actualRange.contains(expectedRange))
  }

  test("AssumeFalse:-5 + t < 5") {
    val t = Param("t")(U8)
    val cond = PE.partialEval((-5 + t < 5).tchk().lower())
    val facts = FactSet().assumeFalse(cond)
    val expectedRange = ScalarRange(Some(10), Some(256))
    val actualRange = facts.getRange(t)
    assert(actualRange.contains(expectedRange))
  }

  test("AssumeFalse:10 < x") {
    val x = Param("x")(U16)
    val cond = PE.partialEval((10 < x).tchk().lower())
    val facts = FactSet().assumeFalse(cond)
    val expectedRange = ScalarRange(Some(0), Some(11))
    val actualRange = facts.getRange(x)
    assert(actualRange.contains(expectedRange))
  }

  test("AssumeTrue:x == -1") {
    val x = Param("x")(I8)
    val cond = PE.partialEval((x === -1).tchk().lower())
    val facts = FactSet().assumeTrue(cond)
    val expectedRange = ScalarRange(Some(-1), Some(0))
    val actualRange = facts.getRange(x)
    assert(actualRange.contains(expectedRange))
  }

  test("AssumeTrue:42 == x") {
    val x = Param("x")(I8)
    val cond = PE.partialEval((42 === x).tchk().lower())
    val facts = FactSet().assumeTrue(cond)
    val expectedRange = ScalarRange(Some(42), Some(43))
    val actualRange = facts.getRange(x)
    assert(actualRange.contains(expectedRange))
  }

  test("AssumeTrue:NonInt == NonInt") {
    val x = Param("x")(TyBool)
    val y = Param("y")(TyBool)
    val facts = FactSet().assumeTrue(Equal(x, y)().tchk())
    assert(facts.getRange(x).isEmpty)
    assert(facts.getRange(y).isEmpty)
  }
}
