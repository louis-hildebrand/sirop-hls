package mhir.ir

import mhir.ir.Lowering.ExprLowering
import mhir.ir.typecheck.TypeCheck
import mhir.sugar.{StmCount3D, StmFold, StmMap}
import org.scalatest.funsuite.AnyFunSuite

class ExprPrinterTests extends AnyFunSuite {
  test("Unit") {
    val e = Tuple()()
    assert(ExprPrinter.displayOneLine(e) == "()")
    assert(ExprPrinter.display(e) == "()")
  }

  test("1-tuple") {
    val e = Tuple(C(42)(U8))()
    assert(ExprPrinter.displayOneLine(e) == "(42:u8,)")
    assert(ExprPrinter.display(e) == "(42:u8,)")
  }

  test("3-tuple") {
    val x = Param("x", -1)(TyBool)
    val e = Tuple(Tuple(x, True, False)(), False, True)()

    val expectedOneLine = s"((x, true, false), false, true)"
    assert(ExprPrinter.displayOneLine(e) == expectedOneLine)

    val expectedMultiLine =
      s"""(
         |  (
         |    x,
         |    true,
         |    false
         |  ),
         |  false,
         |  true
         |)
         |""".stripMargin.stripTrailing
    assert(ExprPrinter.display(e, maxWidth = 15) == expectedMultiLine)
  }

  test("x.__0 + x.__1") {
    val x = Param("x", -1)(TyTuple(U8, U8))
    val e = Sum(x.__0, x.__1)()
    assert(ExprPrinter.displayOneLine(e) == s"x.0 + x.1")
    assert(ExprPrinter.display(e) == s"x.0 + x.1")
  }

  test("mux.__2") {
    val c = Param("c", -1)(TyBool)
    val t = Param("t", -1)((TyBool, TyTuple(), U8))
    val f = Param("f", -1)((U8, U8, U8))
    val e = Mux(c, t, f)().__2

    val expectedOneLine = s"(if (c) then { t } else { f }).2"
    assert(ExprPrinter.displayOneLine(e) == expectedOneLine)

    val expectedMultiLine =
      s"""(if (c) then {
         |  t
         |} else {
         |  f
         |}).2
         |""".stripMargin.stripTrailing
    assert(ExprPrinter.display(e, maxWidth = 20) == expectedMultiLine)
  }

  test("f(x % y)") {
    val f = Param("f", -1)(U8 ->: U8)
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(U8)
    val e = FunCall(f, Mod(x, y)())()
    assert(ExprPrinter.displayOneLine(e) == s"f(x % y)")
    assert(ExprPrinter.display(e) == s"f(x % y)")
  }

  test("(x => y => x + y + 1)(42)(43)") {
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(U8)
    val f = Function(x, Function(y, Sum(C(1)(U8), x, y)())())()
    val e = f(C(42)(U8))(C(43)(U8))

    val expectedOneLine =
      s"((x : u8) => (y : u8) => 1:u8 + x + y)(42:u8)(43:u8)"
    val actualOneLine = ExprPrinter.displayOneLine(e)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      s"""((x : u8) =>
         |  (y : u8) =>
         |    1:u8 + x + y
         |)(42:u8)(43:u8)
         |""".stripMargin.stripTrailing
    assert(ExprPrinter.display(e, maxWidth = 20) == expectedMultiLine)
  }

  test("f(x + y + z)(x * y * z * w)") {
    val x = Param("x", -1)(U16)
    val y = Param("y", -1)(U16)
    val z = Param("z", -1)(U16)
    val w = Param("w", -1)(U16)
    val f = Param("f", -1)(U16 ->: U16 ->: U16)
    val e = f(Sum(x, y, z)())(Prod(x, y, z, w)())

    val expectedOneLine = s"f(x + y + z)(w * x * y * z)"
    val actualOneLine = ExprPrinter.displayOneLine(e)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      s"""f(
         |  x + y + z
         |)(
         |  w
         |    * x
         |    * y
         |    * z
         |)
         |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.display(e, maxWidth = 12)
    assert(actualMultiLine == expectedMultiLine)
  }

  test("vbuild(100, i => vbuild(100, j => 42 + i + j))[42][43]") {
    val i = Param("i", -1)(U8)
    val j = Param("j", -1)(U8)
    val v = VecBuild(
      C(100)(U8),
      Function(
        i,
        VecBuild(C(100)(U8), Function(j, Sum(C(42)(U8), i, j)())())()
      )()
    )()
    val e = VecAccess(VecAccess(v, C(42)(U8))(), C(43)(U8))()

    val expectedOneLine =
      s"vbuild(100:u8, (i : u8) => vbuild(100:u8, (j : u8) => 42:u8 + i + j))[42:u8][43:u8]"
    val actualOneLine = ExprPrinter.displayOneLine(e)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      s"""vbuild(
         |  100:u8,
         |  (i : u8) =>
         |    vbuild(
         |      100:u8,
         |      (j : u8) =>
         |        42:u8 + i + j
         |    )
         |)[42:u8][43:u8]
         |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.display(e, maxWidth = 25)
    assert(actualMultiLine == expectedMultiLine)
  }

  test("v[i / j][i + j + k]") {
    val i = Param("i", -1)(U8)
    val j = Param("j", -1)(U8)
    val k = Param("k", -1)(U8)
    val v = Param("v", -1)(TyVec(TyVec(U8, 100), 100))
    val e = VecAccess(VecAccess(v, Div(i, j)())(), Sum(i, j, k)())()

    val expectedOneLine = s"v[i / j][i + j + k]"
    val actualOneLine = ExprPrinter.displayOneLine(e)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      s"""v[
         |  i / j
         |][
         |  i
         |    + j
         |    + k
         |]
         |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.display(e, maxWidth = 9)
    assert(actualMultiLine == expectedMultiLine)
  }

  test("x => y => (x, y)") {
    val x = Param("x", -1)(TyStm(U32, 999))
    val y = Param("y", -1)(TyVec(I32, 888))
    val e = Function(
      x,
      Function(y, Tuple(x, y, C(1)(U32), C(2)(U32), C(3)(U32))())()
    )()

    val expectedOneLine =
      s"(x : Stm[u32, 999:u10]) => (y : Vec[i32, 888:u10]) => (x, y, 1:u32, 2:u32, 3:u32)"
    assert(ExprPrinter.displayOneLine(e) == expectedOneLine)

    val expectedMultiLine =
      s"""(x : Stm[u32, 999:u10]) =>
         |  (y : Vec[i32, 888:u10]) =>
         |    (x, y, 1:u32, 2:u32, 3:u32)
         |""".stripMargin.stripTrailing
    assert(ExprPrinter.display(e, maxWidth = 35) == expectedMultiLine)
  }

  test("x * (y + z)") {
    val y = Param("y", -1)(U8)
    val z = Param("z", -1)(U8)
    val e = Prod(C(2)(U8), Sum(y, z)())()
    val expected = s"2:u8 * (y + z)"
    assert(ExprPrinter.displayOneLine(e) == expected)
    assert(ExprPrinter.display(e) == expected)
  }

  test("(x / y) / (z / w)") {
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(U8)
    val z = Param("z", -1)(U8)
    val w = Param("w", -1)(U8)
    val e = Div(Div(x, y)(), Div(z, w)())()
    val expected = s"x / y / (z / w)"
    assert(ExprPrinter.displayOneLine(e) == expected)
    assert(ExprPrinter.display(e) == expected)
  }

  test("false || true == (true && false)") {
    val e = False || (True equ (False && True))
    assert(ExprPrinter.displayOneLine(e) == "false || true == (false && true)")
    assert(ExprPrinter.display(e) == "false || true == (false && true)")
  }

  test("1 != 2") {
    val e = Not(Equal(C(1)(I8), C(2)(I8))())()
    assert(ExprPrinter.displayOneLine(e) == "1:i8 != 2:i8")
    assert(ExprPrinter.display(e) == "1:i8 != 2:i8")
  }

  test("42 < 43") {
    val e = C(42)(U16) lt C(43)(U16)
    assert(ExprPrinter.displayOneLine(e) == "42:u16 < 43:u16")
    assert(ExprPrinter.display(e) == "42:u16 < 43:u16")
  }

  test("42 >= 43") {
    val e = !(C(42)(U16) lt C(43)(U16))
    assert(ExprPrinter.displayOneLine(e) == "42:u16 >= 43:u16")
    assert(ExprPrinter.display(e) == "42:u16 >= 43:u16")
  }

  test("!((a || b) && c))") {
    val a = Param("a", -1)(TyBool)
    val b = Param("b", -1)(TyBool)
    val c = Param("c", -1)(TyBool)
    val e = Not(And(Or(a, b)(), c)())()
    val expected = s"!((a || b) && c)"
    assert(ExprPrinter.displayOneLine(e) == expected)
    assert(ExprPrinter.display(e) == expected)
  }

  test("mux + 1") {
    val e = Sum(C(1)(U8), Mux(True, C(42)(U8), C(99)(U8))())()
    val expected = s"1:u8 + (if (true) then { 42:u8 } else { 99:u8 })"
    assert(ExprPrinter.displayOneLine(e) == expected)
    assert(ExprPrinter.display(e) == expected)
  }

  test("pad") {
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(U8)
    val z = Param("z", -1)(U8)
    val e = PadTo(Sum(x, y, z)(), 16)()

    val expectedOneLine = "pad16(x + y + z)"
    assert(ExprPrinter.displayOneLine(e) == expectedOneLine)

    val expectedMultiLine =
      s"""pad16(
         |  x + y + z
         |)
         |""".stripMargin.stripTrailing
    assert(ExprPrinter.display(e, maxWidth = 15) == expectedMultiLine)
  }

  test("StmNextK") {
    val s = Param("s", -1)(TyStm(U8, 42))
    val k = Param("k", -1)(U8)
    val e = StmNextK(s, k)()

    val expectedOneLine = "snextk(s, k)"
    assert(ExprPrinter.displayOneLine(e) == expectedOneLine)

    val expectedMultiLine =
      s"""snextk(
         |  s,
         |  k
         |)
         |""".stripMargin.stripTrailing
    assert(ExprPrinter.display(e, maxWidth = 10) == expectedMultiLine)
  }

  test("if-else-if") {
    val e = Mux(
      True,
      C(0)(U8),
      Mux(True, C(1)(U8), Mux(False, C(2)(U8), C(3)(U8))())()
    )()

    val expectedOneLine =
      s"if (true) then { 0:u8 } else if (true) then { 1:u8 } else if (false) then { 2:u8 } else { 3:u8 }"
    assert(ExprPrinter.displayOneLine(e) == expectedOneLine)

    val expectedMultiLine =
      s"""if (true) then {
         |  0:u8
         |} else if (true) then {
         |  1:u8
         |} else if (false) then {
         |  2:u8
         |} else {
         |  3:u8
         |}
         |""".stripMargin.stripTrailing
    assert(ExprPrinter.display(e) == expectedMultiLine)
  }

  test("StmBuild") {
    val s = Param("s", -1)(TyStm(U8, C(-1)()))
    val i = Param("i", -1)(U8)
    val j = Param("j", -1)(I9)
    val e = StmBuild(
      C(42)(U8),
      Sum(ToSigned(StmData(s)())(), j)(),
      True,
      Map[Param, (Expr, Expr)](
        s -> (
          StmBuild(
            C(42)(U8),
            i,
            True,
            Map[Param, (Expr, Expr)](i -> (C(0)(U8), Sum(C(1)(U8), i)()))
          )(),
          True
        ),
        j -> (
          C(-10)(I9),
          Sum(C(2)(I9), j)()
        )
      )
    )()

    val expectedOneLine =
      s"sbuild(42:u8; sgn(data(s)) + j; true; (j : i9) = (-10:i9, 2:i9 + j); (s : Stm[u8, -1:i1]) = (sbuild(42:u8; i; true; (i : u8) = (0:u8, 1:u8 + i)), true))"
    assert(ExprPrinter.displayOneLine(e) == expectedOneLine)

    val expectedMultiLine =
      s"""sbuild(
         |  42:u8;
         |  sgn(data(s)) + j;
         |  true;
         |  (j : i9) = (
         |    -10:i9,
         |    2:i9 + j
         |  );
         |  (s : Stm[u8, -1:i1]) = (
         |    sbuild(42:u8; i; true; (i : u8) = (0:u8, 1:u8 + i)),
         |    true
         |  );
         |)
         |""".stripMargin.stripTrailing
    assert(ExprPrinter.display(e) == expectedMultiLine)
  }

  test("[[0, 1], [2, 3]]") {
    val e = VecLiteral(
      VecLiteral(C(0)(U8), C(1)(U8))(),
      VecLiteral(C(2)(U8), C(3)(U8))()
    )()

    val expectedOneLine = s"[[0:u8, 1:u8], [2:u8, 3:u8]]"
    assert(ExprPrinter.displayOneLine(e) == expectedOneLine)

    val expectedMultiLine =
      s"""[
         |  [0:u8, 1:u8],
         |  [2:u8, 3:u8]
         |]
         |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.displayMultiLine(e, maxWidth = 80)
    assert(actualMultiLine == expectedMultiLine)
  }

  test("HugeExpression") {
    val e = StmFold(
      StmCount3D(C(2)(U8), C(2)(U8), C(3)(U8))(),
      C(0)(U8),
      U8 ::+ (acc =>
        TyStm(TyStm((U8, U8, U8), 3), 2) ::+ (s =>
          StmMap(
            StmFold(
              s,
              C(0)(U8),
              U8 ::+ (acc =>
                TyStm((U8, U8, U8), 3) ::+ (s =>
                  StmMap(
                    StmFold(
                      s,
                      C(0)(U8),
                      U8 ::+ (acc =>
                        (U8, U8, U8) ::+ (x => acc + x.__0 + x.__1 + x.__2)
                      )
                    )(),
                    U8 ::+ (x => acc + x)
                  )()
                )
              )
            )(),
            U8 ::+ (x => acc + x)
          )()
        )
      )
    )().tchk().lower()
    val start = System.nanoTime()
    val str = ExprPrinter.display(e)
    val duration = (System.nanoTime() - start) / 10000000000L
    // Not a big deal how exactly the code is formatted, as long as the pretty
    // printer doesn't take forever
    assert(str.nonEmpty)
    assert(duration < 10)
  }
}
