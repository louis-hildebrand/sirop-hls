package mhir.sugar

import mhir.ir.Lowering.ExprLowering
import org.scalatest.funsuite.AnyFunSuite
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

class ReshapeSeqTests extends AnyFunSuite {
  test("Stm[Int,21+10+11] --> Stm[Int,42]") {
    val s = Param("s")(TyStm(U16, Sum(C(21)(U8), C(10)(U8), C(11)(U8))()))
    val reshaped = ReshapeSeq(s, TyStm(U16, 42))().tchk().lower()
    val sVal = StmLiteral((0 until 42).map(t => C(t)(U16)): _*)().tchk()
    val actual = mhir.ir.eval(reshaped.subPreserveType(s -> sVal))
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
    val actual = mhir.ir.eval(reshaped.subPreserveType(s -> sVal))
    assert(actual == expected)
  }
}
