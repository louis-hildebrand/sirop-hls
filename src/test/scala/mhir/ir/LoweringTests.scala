package mhir.ir

import org.scalatest.funsuite.AnyFunSuite

class LoweringTests extends AnyFunSuite {
  test("LowerParam") {
    val s = Param("s")(TyStm(TyStm(U8, 2), 2))
    val actual = s.lower()
    assert(actual.lower() == s)
    assert(actual.typ == TyStm(U8, Prod(PadTo(2, 4)(), PadTo(2, 4)())()))
  }

  test("LowerFunction") {
    val s = Param("s")(TyStm(TyStm(U16, 3), 2))
    val f = Function(s, s)().tchk()
    val actual = f.lower().asInstanceOf[Function]
    assert(actual == f)
    assert(actual.param.typ == TyStm(U16, Prod(PadTo(3, 4)(), PadTo(2, 4)())()))
  }

  test("LowerLet") {
    val s = Param("s")()
    val s1 = Param("s1")(TyStm(TyStm(U32, 2), 2))
    val e = Let(s, s1, s)().tchk()
    val actual = e.lower().asInstanceOf[FunCall]
    assert(actual == FunCall(Function(s, s)(), s1)())
    assert(
      actual.f.asInstanceOf[Function].param.typ
        == TyStm(U32, Prod(PadTo(2, 4)(), PadTo(2, 4)())())
    )
  }

  test("UncurryFunction:1arg") {
    val f = (U8 ::+ (x => x * x + 1)).tchk()
    val actual = f.uncurry()
    assert(actual == f)
    assert(actual.typ == U8 ->: U8)
  }

  test("UncurryFunction:2args") {
    val f =
      (U8 ::+ (x => U8 ::+ (y => x + x * y + y))).tchk()
    val expected = TyTuple(U8, U8) ::+ (a => a.__0 + a.__0 * a.__1 + a.__1)
    assert(f.uncurry() == expected)
  }

  test("UncurryFunction:3args") {
    val f =
      (U8 ::+ (x => TyBool ::+ (b => U8 ::+ (y => Tuple(x + y, b)()))))
        .tchk()
    val expected = TyTuple(U8, TyTuple(TyBool, U8)) ::+ (z =>
      Tuple(z.__0 + z.__1.__1, z.__1.__0)()
    )
    assert(f.uncurry() == expected)
  }

  test("UncurryFunCall:1arg") {
    val f = Param("f")(I8 ->: I8)
    val e = FunCall(f, IntCst(42)(I8))().tchk()
    val actual = e.uncurry()
    assert(actual == e)
    assert(actual.typ == I8)
  }

  test("UncurryFunCall:2args") {
    val f = Param("f")(I16 ->: I16 ->: I16)
    val e = FunCall(FunCall(f, IntCst(42)(I16))(), IntCst(99)(I16))().tchk()
    val expected = FunCall(f.lower(), Tuple(42, 99)())()
    val actual = e.uncurry()
    assert(actual == expected)
    assert(e.typ == I16)
  }

  test("UncurryFunCall:3args") {
    val int2bool2int2int = I32 ->: TyBool ->: I32 ->: I32
    val f = Param("f")(int2bool2int2int)
    val e =
      FunCall(FunCall(FunCall(f, IntCst(42)(I32))(), True)(), IntCst(99)(I32))()
        .tchk()
    val expected = FunCall(f.lower(), Tuple(42, Tuple(True, 99)())())()
    val actual = e.uncurry()
    assert(actual == expected)
    assert(actual.typ == I32)
  }

  test("UncurryFunction:Mux") {
    val g0 = U8 ::+ (_ => IntCst(0)(U8))
    val g1 = U8 ::+ (x => IntCst(100)(U8) / x)
    val x = Param("x")(I8)
    val g = Mux(x === 0, g0, g1)().tchk()
    val f = Function(x, g)().tchk()
    val exc = intercept[IllegalArgumentException](f.uncurry())
    assert(
      exc.getMessage == s"Cannot uncurry function with type ${U8 ->: U8} and body $g."
    )
  }

  test("UncurryFunction:Param") {
    val g = Param("g")(U8 ->: U8)
    val f = (U8 ::+ (_ => g)).tchk()
    val exc = intercept[IllegalArgumentException](f.uncurry())
    assert(
      exc.getMessage == s"Cannot uncurry function with type ${U8 ->: U8} and body $g."
    )
  }

  test("UncurryFunCall:PartialApplication") {
    val f = Param("f")(U8 ->: U8 ->: U8)
    val e = FunCall(f, IntCst(42)(U8))().tchk()
    val exc = intercept[IllegalArgumentException](e.uncurry())
    val expectedMessage =
      s"Uncurried function call is not well-typed. Are there enough arguments? Note that partial application is not supported. (Expression: $e)"
    assert(exc.getMessage == expectedMessage)
  }
}
