package mhir.ir

import org.scalatest.funsuite.AnyFunSuite

class ExprPrinterTests extends AnyFunSuite {
  test("Unit") {
    val e = Tuple()()
    assert(ExprPrinter.displayOneLine(e) == "()")
  }

  test("1-tuple") {
    val e = Tuple(C(42)(U8))()
    assert(ExprPrinter.displayOneLine(e) == "(42:u8,)")
  }

  test("3-tuple") {
    val x = Param("x")(TyBool)
    val e = Tuple(x, True, False)()
    assert(ExprPrinter.displayOneLine(e) == s"(${x.name}, true, false)")
  }

  test("x.__1") {
    val x = Param("x")(TyTuple(U8, I32))
    val e = x.__1
    assert(ExprPrinter.displayOneLine(e) == s"${x.name}.1")
  }

  test("f(x % y)") {
    val f = Param("f")(U8 ->: U8)
    val x = Param("x")(U8)
    val y = Param("x")(U8)
    val e = FunCall(f, Mod(x, y)())()
    assert(ExprPrinter.displayOneLine(e) == s"${f.name}(${x.name} % ${y.name})")
  }

  test("(x => x + 1)(42)") {
    val f = U32 ::+ (x => Sum(C(1)(U32), x)())
    val x = f.param
    val e = FunCall(f, C(42)(U32))()
    val expected = s"((${x.name} : u32) => 1:u32 + ${x.name})(42:u32)"
    assert(ExprPrinter.displayOneLine(e) == expected)
  }

  test("x * (y + z)") {
    val y = Param("y")(U8)
    val z = Param("z")(U8)
    val e = Prod(C(2)(U8), Sum(y, z)())()
    val expected = s"2:u8 * (${y.name} + ${z.name})"
    assert(ExprPrinter.displayOneLine(e) == expected)
  }

  test("(x / y) / (z / w)") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val z = Param("z")(U8)
    val w = Param("w")(U8)
    val e = Div(Div(x, y)(), Div(z, w)())()
    val expected = s"${x.name} / ${y.name} / (${z.name} / ${w.name})"
    assert(ExprPrinter.displayOneLine(e) == expected)
  }

  test("false || true == (true && false)") {
    val e = False || (True equ (False && True))
    assert(ExprPrinter.displayOneLine(e) == "false || true == (false && true)")
  }

  test("1 != 2") {
    val e = Not(Equal(C(1)(I8), C(2)(I8))())()
    assert(ExprPrinter.displayOneLine(e) == "1:i8 != 2:i8")
  }

  test("42 < 43") {
    val e = C(42)(U16) lt C(43)(U16)
    assert(ExprPrinter.displayOneLine(e) == "42:u16 < 43:u16")
  }

  test("42 >= 43") {
    val e = !(C(42)(U16) lt C(43)(U16))
    assert(ExprPrinter.displayOneLine(e) == "42:u16 >= 43:u16")
  }

  test("!((a || b) && c))") {
    val a = Param("a")(TyBool)
    val b = Param("b")(TyBool)
    val c = Param("c")(TyBool)
    val e = Not(And(Or(a, b)(), c)())()
    val expected = s"!((${a.name} || ${b.name}) && ${c.name})"
    assert(ExprPrinter.displayOneLine(e) == expected)
  }

  test("mux + 1") {
    val e = Sum(C(1)(U8), Mux(True, C(42)(U8), C(99)(U8))())()
    val expected = s"1:u8 + (if (true) then { 42:u8 } else { 99:u8 })"
    assert(ExprPrinter.displayOneLine(e) == expected)
  }

  test("StmBuild") {
    val s = Param("s")(TyStm(U8, C(-1)()))
    val i = Param("i")(U8)
    val j = Param("j")(I9)
    val e = StmBuild(
      C(42)(U8),
      Sum(ToSigned(StmData(s)())(), j)(),
      True,
      Map[Param, (Expr, Expr)](
        j -> (
          C(-10)(I9),
          Sum(C(2)(I9), j)()
        ),
        s -> (
          StmBuild(
            C(42)(U8),
            i,
            True,
            Map[Param, (Expr, Expr)](i -> (C(0)(U8), Sum(C(1)(U8), i)()))
          )(),
          True
        )
      )
    )()
    val expected =
      s"sbuild(42:u8; sgn(data(${s.name})) + ${j.name}; true; (${j.name} : i9) = (-10:i9, 2:i9 + ${j.name}); (${s.name} : Stm[u8, -1:i1]) = (sbuild(42:u8; ${i.name}; true; (${i.name} : u8) = (0:u8, 1:u8 + ${i.name})), true))"
    assert(ExprPrinter.displayOneLine(e) == expected)
  }
}
