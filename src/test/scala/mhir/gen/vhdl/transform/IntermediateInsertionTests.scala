package mhir.gen.vhdl
package transform

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable.ListMap

class IntermediateInsertionTests extends AnyFunSuite {

  /* The VHDL expression generator translates "if-then-else" expressions to
   * "concurrent conditional signal assignment" statements. These are not
   * expressions, so an intermediate variable is needed.
   */
  test("IfElse") {
    val c = Param("c")(TyBool)
    val e = Sum(C(1)(U8), Mux(c, C(42)(U8), C(0)(U8))())()
    val (actualExpr, actualIntermediates) = IntermediateInsertion(e)

    assert(actualIntermediates.size == 1)
    val (tmp, DataIntermediate(actualIntermediate)) = actualIntermediates.head
    assert(actualIntermediate == Mux(c, C(42)(U8), C(0)(U8))())
    assert(actualIntermediate.typ == U8)
    val expectedExpr = Sum(C(1)(U8), tmp)()
    assert(actualExpr == expectedExpr)
    assert(actualExpr.typ == U8)
  }

  test("MuxAndStmDataInsideFunction") {
    val s = Param("s")(TyStm(I16, 16))
    val x = Param("x")(I16)
    val fImpl = Function(
      x,
      Sum(
        Mux(
          x gt C(0)(I16),
          x,
          Mux(x gt C(-10)(I16), C(-10)(I16), C(0)(I16))()
        )(),
        Mux(x lt C(42)(I16), x, C(42)(I16))(),
        StmData(s)()
      )()
    )().tchk()
    val (actualExpr, actualIntermediates) = IntermediateInsertion(fImpl)

    assert(actualExpr.isInstanceOf[Param])
    val actualExprParam = actualExpr.asInstanceOf[Param]

    assert(actualIntermediates.size == 2)
    val FunctionIntermediate(Seq(actualX), actualFunIntermediates, actualBody) =
      actualIntermediates(actualExprParam)
    assert(actualX == x)
    val (sdata, StmDataIntermediate(actualS)) =
      (actualIntermediates - actualExprParam).head
    assert(actualS == s)

    val (ite1, ite2, ite3) = actualBody match {
      case Sum(ite2: Param, ite3: Param, actualSdata) =>
        assert(actualSdata == sdata)
        actualFunIntermediates(ite2) match {
          case DataIntermediate(Mux(_, _, ite1: Param)) =>
            (ite1, ite2, ite3)
          case e =>
            fail(s"wrong RHS for second if-then-else: $e")
        }
      case body =>
        fail(s"wrong body: $body")
    }

    val expectedIntermediates = ListMap(
      ite1 -> DataIntermediate(
        Mux(x gt C(-10)(I16), C(-10)(I16), C(0)(I16))().tchk()
      ),
      ite2 -> DataIntermediate(Mux(x gt C(0)(I16), x, ite1)().tchk()),
      ite3 -> DataIntermediate(Mux(x lt C(42)(I16), x, C(42)(I16))().tchk())
    )
    assert(actualFunIntermediates == expectedIntermediates)
  }

  /* Undefined initial values for the accumulators should stay as-is so that
   * later passes can easily recognize it (e.g., to know that there's no need
   * to emit reset logic).
   */
  test("UndefinedInitialValue") {
    val acc = Param("acc")(U8)
    val p = Param("p")(TyStm(U8, 16))
    val original = GenStmBuild(
      data = acc,
      valid = True,
      accumulators = Map(
        acc -> (Undefined(U8), StmData(p)().tchk())
      ),
      producers = Map(
        p -> (p, True)
      ),
      intermediates = ListMap()
    )
    val actual = IntermediateInsertion(original)
    assert(actual.accumulators.size == 1)
    assert(actual.accumulators.head._2._1.isInstanceOf[Undefined])
  }
}
