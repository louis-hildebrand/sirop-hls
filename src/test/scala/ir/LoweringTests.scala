package ir

import org.scalatest.funsuite.AnyFunSuite

class LoweringTests extends AnyFunSuite {
  test("LowerParam") {
    val s = Param("s")(TyStm(TyStm(TyInt, 2), 2))
    val actual = s.lower()
    assert(actual.lower() == s)
    assert(actual.typ == TyStm(TyInt, IntCst(2) * IntCst(2)))
  }

  test("UncurryFunction:1arg") {
    val f = (TyInt ::+ (x => x * x + 1)).tchk()
    val actual = f.uncurry()
    assert(actual == f)
    assert(actual.typ == TyArrow(TyInt, TyInt))
  }

  test("UncurryFunction:2args") {
    val f =
      (TyInt ::+ (x => TyInt ::+ (y => x + x * y + y))).tchk()
    val expected = Int2() ::+ (a => a.__0 + a.__0 * a.__1 + a.__1)
    assert(f.uncurry() == expected)
  }

  test("UncurryFunction:3args") {
    val f =
      (TyInt ::+ (x => TyBool ::+ (b => TyInt ::+ (y => Tuple(x + y, b)()))))
        .tchk()
    val expected = TyTuple(TyInt, TyTuple(TyBool, TyInt)) ::+ (z =>
      Tuple(z.__0 + z.__1.__1, z.__1.__0)()
    )
    assert(f.uncurry() == expected)
  }

  test("UncurryFunCall:1arg") {
    val f = Param("f")(TyArrow(TyInt, TyInt))
    val e = FunCall(f, 42)().tchk()
    val actual = e.uncurry()
    assert(actual == e)
    assert(actual.typ == TyInt)
  }

  test("UncurryFunCall:2args") {
    val f = Param("f")(TyArrow(TyInt, TyArrow(TyInt, TyInt)))
    val e = FunCall(FunCall(f, 42)(), 99)().tchk()
    val expected = FunCall(f.lower(), Tuple(42, 99)())()
    val actual = e.uncurry()
    assert(actual == expected)
    assert(e.typ == TyInt)
  }

  test("UncurryFunCall:3args") {
    val int2bool2int2int =
      TyArrow(TyInt, TyArrow(TyBool, TyArrow(TyInt, TyInt)))
    val f = Param("f")(int2bool2int2int)
    val e = FunCall(FunCall(FunCall(f, 42)(), True)(), 99)().tchk()
    val expected = FunCall(f.lower(), Tuple(42, Tuple(True, 99)())())()
    val actual = e.uncurry()
    assert(actual == expected)
    assert(actual.typ == TyInt)
  }

  test("UncurryFunction:Mux") {
    val g0 = TyInt ::+ (_ => 0)
    val g1 = TyInt ::+ (x => 100 / x)
    val x = Param("x")(TyInt)
    val g = Mux(x === 0, g0, g1)()
    val f = Function(x, g)().tchk()
    val exc = intercept[IllegalArgumentException](f.uncurry())
    assert(
      exc.getMessage == s"Cannot uncurry function with type ${TyArrow(TyInt, TyInt)} and body $g."
    )
  }

  test("UncurryFunction:Param") {
    val g = Param("g")(TyArrow(TyInt, TyInt))
    val f = (TyInt ::+ (_ => g)).tchk()
    val exc = intercept[IllegalArgumentException](f.uncurry())
    assert(
      exc.getMessage == s"Cannot uncurry function with type ${TyArrow(TyInt, TyInt)} and body $g."
    )
  }

  test("UncurryFunCall:PartialApplication") {
    val f = Param("f")(TyArrow(TyInt, TyArrow(TyInt, TyInt)))
    val e = FunCall(f, 42)().tchk()
    val exc = intercept[IllegalArgumentException](e.uncurry())
    val expectedMessage =
      s"Uncurried function call is not well-typed. Are there enough arguments? Note that partial application is not supported. (Expression: $e)"
    assert(exc.getMessage == expectedMessage)
  }
}
