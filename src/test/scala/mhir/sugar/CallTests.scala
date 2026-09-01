package mhir.sugar

import mhir.canonicalize._
import mhir.ir._
import mhir.testing.ParamStore
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

/** These tests mainly check the name resolution logic.
  */
class CallTests extends AnyFunSuite {

  private val x = ParamStore("x")

  private def call(name: String, args: Expr*): Call = {
    Call(Param(name, -1)(Missing), Seq(), args)()
  }

  test("pad7") {
    val u4 = TyUInt(4)
    assert(call("pad7", x(u4)).tchk() == PadTo(x(u4), 7)())
  }

  test("truncate7") {
    assert(call("truncate7", x(U8)).tchk() == TruncateTo(x(U8), 7)())
  }

  test("sign") {
    assert(call("sign", x(U8)).tchk() == ToSigned(x(U8))())
  }

  test("unsign") {
    assert(call("unsign", x(I8)).tchk() == ToUnsigned(x(I8))())
  }

  test("FunCall") {
    val x = Param("x", -1)(Missing)
    val y = Param("y", -1)(Missing)
    val f = Param("f", -1)(Missing)
    val fTyp = U8 ->: (U8, U8) ->: TyTuple() ->: U8
    val original = Call(
      Call(
        Call(f, Seq(), Seq(C(42)(U8)))(),
        Seq(),
        Seq(x, y)
      )(),
      Seq(),
      Seq()
    )()
    val expected = FunCall(
      FunCall(
        FunCall(f.rebuild(fTyp), C(42)(U8))(),
        Tuple(x.rebuild(U8), y.rebuild(U8))()
      )(),
      Tuple()()
    )()
    val context = Map(x -> U8, y -> U8, f -> fTyp)
    val actual = original.tchk(context, Map())
    assert(actual == expected)
  }

  test("bits") {
    assert(call("bits", x(U8)).tchk() == Bits(x(U8))())
  }

  test("interpret_as") {
    val actual = Call(
      Param("interpret_as", -1)(Missing),
      Seq(U8),
      Seq(x(TyVec(TyBool, 8)))
    )().tchk()
    val expected = InterpretAs(x(TyVec(TyBool, 8)), U8)()
    assert(actual == expected)
    assert(actual.typ == expected.targetTyp)
  }

  test("zeros") {
    val actual = Call(Param("zeros", -1)(Missing), Seq(U8), Seq())().tchk()
    val expected = AllZero(U8)
    assert(actual == expected)
    assert(actual.typ == expected.typ)
  }

  test("ones") {
    val actual = Call(Param("ones", -1)(Missing), Seq(U8), Seq())().tchk()
    val expected = AllOne(U8)
    assert(actual == expected)
    assert(actual.typ == expected.typ)
  }

  test("StmRange") {
    val actual = call("StmRange", C(8)(U8), C(7)(U8), C(6)(U8)).tchk()
    val expected = StmRange(C(8)(U8), C(7)(U8), C(6)(U8))()
    assert(actual == expected)
  }
}
