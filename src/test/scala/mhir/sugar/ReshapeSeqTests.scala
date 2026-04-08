package mhir.sugar

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import org.scalatest.funsuite.AnyFunSuite

class ReshapeSeqTests extends AnyFunSuite {
  test("Stm[Int,21+10+11] --> Stm[Int,42]") {
    val s = Param("s")(TyStm(U16, Sum(C(21)(U8), C(10)(U8), C(11)(U8))()))
    val reshaped = ReshapeSeq(s, TyStm(U16, 42))().tchk().lower()
    val sVal = StmLiteral((0 until 42).map(t => C(t)(U16)): _*)().tchk()
    val actual = mhir.eval.eval(reshaped.subPreserveType(s -> sVal))
    assert(actual == sVal)
  }

  test("Stm[Vec[Int,1],4] --> Stm[Int,4]") {
    val s = Param("s")(TyStm(TyVec(U8, 1), 4))
    val reshaped = ReshapeSeq(s, TyStm(U8, 4))().tchk().lower()
    val sVal = StmLiteral(
      VecLiteral(C(1)(U8))(),
      VecLiteral(C(4)(U8))(),
      VecLiteral(C(9)(U8))(),
      VecLiteral(C(16)(U8))()
    )().tchk()
    val expected = StmLiteral(C(1)(U8), C(4)(U8), C(9)(U8), C(16)(U8))()
    val actual = mhir.eval.eval(reshaped.subPreserveType(s -> sVal))
    assert(actual == expected)
  }

  test("Vec[Vec[Vec[Int, 1], 1], 2] -> Vec[Vec[Int, 1], 2]") {
    val v = Param("v")(TyVec(TyVec(TyVec(U8, 1), 1), 2))
    val reshaped = ReshapeSeq(v, TyVec(TyVec(U8, 1), 2))().tchk().lower()
    val vVal = VecLiteral(
      Seq(42, 99).map(C(_)(U8)).map(VecLiteral(_)()).map(VecLiteral(_)()): _*
    )().tchk()
    val expected = VecLiteral(
      Seq(42, 99).map(C(_)(U8)).map(VecLiteral(_)()): _*
    )().tchk()
    val actual = mhir.eval.eval(reshaped.subPreserveType(v -> vVal))
    assert(actual == expected)
  }

  test("u8 -> Vec[u8, 1]") {
    val x = Param("x")(U8)
    val reshaped = ReshapeSeq(x, TyVec(U8, 1))().tchk().lower()

    val examples = Seq(0, 1, 42).map(C(_)(U8))
    for (xVal <- examples) {
      val expected = VecLiteral(xVal)().tchk()
      val actual = mhir.eval.eval(reshaped.subPreserveType(x -> xVal))
      assert(actual == expected)
    }
  }
}
