package mhir.ir

import mhir.ir.Lowering.ExprLowering
import mhir.ir.typecheck.TypeCheck
import mhir.sugar.{StmCount, StmCount3D, StmCst, StmFold, StmMap, StmZip}
import org.scalatest.funsuite.AnyFunSuite

class ExprPrinterTests extends AnyFunSuite {
  test("undefined[u8]") {
    val e = Undefined(U8)
    assert(ExprPrinter.displayOneLine(e) == "undefined[u8]")
    assert(ExprPrinter.displayMultiLine(e, maxWidth = 120) == "undefined[u8]")
  }

  test("undefined[Vec[i16, 42:u6]]") {
    val e = Undefined(TyVec(I16, 42))
    val expected = "undefined[Vec[i16, 42:u6]]"
    assert(ExprPrinter.displayOneLine(e) == expected)
    assert(ExprPrinter.displayMultiLine(e, maxWidth = 120) == expected)
  }

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

  test("WrappingSum") {
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(U8)
    val z = Param("z", -1)(U8)
    val e = WrappingSum(x, y, z)()

    val expectedOneLine = "x +% y +% z"
    val actualOneLine = ExprPrinter.displayOneLine(e)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      """x
        |  +% y
        |  +% z
        |""".stripMargin.stripTrailing()
    val actualMultiLine = ExprPrinter.displayMultiLine(e)
    assert(actualMultiLine == expectedMultiLine)
  }

  test("WrappingDiff") {
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(U8)
    val e = WrappingDiff(x, y)()

    val expectedOneLine = "x -% y"
    val actualOneLine = ExprPrinter.displayOneLine(e)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      """x
        |  -% y
        |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.displayMultiLine(e)
    assert(actualMultiLine == expectedMultiLine)
  }

  test("WrappingProd") {
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(U8)
    val z = Param("z", -1)(U8)
    val e = WrappingProd(x, y, z)()

    val expectedOneLine = "x *% y *% z"
    val actualOneLine = ExprPrinter.displayOneLine(e)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      """x
        |  *% y
        |  *% z
        |""".stripMargin.stripTrailing()
    val actualMultiLine = ExprPrinter.displayMultiLine(e)
    assert(actualMultiLine == expectedMultiLine)
  }

  test("FixCst") {
    val c = FixCst(8)(TyFix(U8, 7))
    val expected = "(8/2e7):fix8_7"
    val actual = ExprPrinter.display(c)
    assert(actual == expected)
  }

  test("FixCst:Tiny") {
    val c = FixCst(1)(TyFix(U32, 62))
    val expected = "(1/2e62):fix32_62"
    val actual = ExprPrinter.display(c)
    assert(actual == expected)
  }

  test("IntFixProd") {
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(TyFix(U8, 7))
    val e = IntFixProd(x, y)()

    val expectedOneLine = "x *_ y"
    val actualOneLine = ExprPrinter.displayOneLine(e)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      """x
        |  *_ y
        |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.displayMultiLine(e)
    assert(actualMultiLine == expectedMultiLine)
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

  test("2 * (y + z)") {
    val y = Param("y", -1)(U8)
    val z = Param("z", -1)(U8)
    val e = Prod(C(2)(U8), Sum(y, z)())()
    val expected = s"2:u8 * (y + z)"
    assert(ExprPrinter.displayOneLine(e) == expected)
    assert(ExprPrinter.display(e) == expected)
  }

  test("NestedSum") {
    val w = Param("w", -1)(U8)
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(U8)
    val z = Param("z", -1)(U8)
    val e = Sum(Sum(w, x)(), Sum(y, z)())()

    // Show nesting because, although the value is the same, the AST is
    // different
    val expectedOneLine = "(w + x) + (y + z)"
    val actualOneLine = ExprPrinter.displayOneLine(e)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      """(w + x)
        |  + (y + z)
        |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.displayMultiLine(e)
    assert(actualMultiLine == expectedMultiLine)
  }

  test("NestedProd") {
    val w = Param("w", -1)(U8)
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(U8)
    val z = Param("z", -1)(U8)
    val e = Prod(Prod(w, x)(), Prod(y, z)())()

    // Show nesting because, although the value is the same, the AST is
    // different
    val expectedOneLine = "(w * x) * (y * z)"
    val actualOneLine = ExprPrinter.displayOneLine(e)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      """(w * x)
        |  * (y * z)
        |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.displayMultiLine(e)
    assert(actualMultiLine == expectedMultiLine)
  }

  test("NestedDiv") {
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(U8)
    val z = Param("z", -1)(U8)
    val w = Param("w", -1)(U8)
    val e = Div(Div(x, y)(), Div(z, w)())()
    // No need for parentheses in this case: the AST is unambiguous
    val expected = "x / y / (z / w)"
    assert(ExprPrinter.displayOneLine(e) == expected)
    assert(ExprPrinter.display(e) == expected)
  }

  test("NestedAnd") {
    val w = Param("w", -1)(U8)
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(U8)
    val z = Param("z", -1)(U8)
    val e = And(And(w, x)(), And(y, z)())()

    // Show nesting because, although the value is the same, the AST is
    // different
    val expectedOneLine = "(w && x) && (y && z)"
    val actualOneLine = ExprPrinter.displayOneLine(e)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      """(w && x)
        |  && (y && z)
        |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.displayMultiLine(e)
    assert(actualMultiLine == expectedMultiLine)
  }

  test("NestedOr") {
    val w = Param("w", -1)(U8)
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(U8)
    val z = Param("z", -1)(U8)
    val e = Or(Or(w, x)(), Or(y, z)())()

    // Show nesting because, although the value is the same, the AST is
    // different
    val expectedOneLine = "(w || x) || (y || z)"
    val actualOneLine = ExprPrinter.displayOneLine(e)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      """(w || x)
        |  || (y || z)
        |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.displayMultiLine(e)
    assert(actualMultiLine == expectedMultiLine)
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

  test("a <<< b") {
    val a = Param("a", -1)(U8)
    val b = Param("b", -1)(U8)
    val e = LLShift(a, b)()

    val expectedOneLine = "a <<< b"
    assert(ExprPrinter.displayOneLine(e) == expectedOneLine)

    val expectedMultiLine =
      s"""a
         |  <<< b
         |""".stripMargin.stripTrailing
    assert(ExprPrinter.displayMultiLine(e) == expectedMultiLine)
  }

  test("a >>> b") {
    val a = Param("a", -1)(U8)
    val b = Param("b", -1)(U8)
    val e = LRShift(a, b)()

    val expectedOneLine = "a >>> b"
    assert(ExprPrinter.displayOneLine(e) == expectedOneLine)

    val expectedMultiLine =
      s"""a
         |  >>> b
         |""".stripMargin.stripTrailing
    assert(ExprPrinter.displayMultiLine(e) == expectedMultiLine)
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

  test("mux with long condition") {
    val c1 = Param("c1", -1)(TyBool)
    val c2 = Param("c2", -1)(TyBool)
    val c3 = Param("c3", -1)(TyBool)
    val c4 = Param("c4", -1)(TyBool)
    val c = (c1 && c2) || (c3 && c4)
    val e = Function(c1, Mux(c, C(1)(U8), C(0)(U8))())()

    val expectedOneLine =
      s"(c1 : bool) => if (c1 && c2 || c3 && c4) then { 1:u8 } else { 0:u8 }"
    val actualOneLine = ExprPrinter.displayOneLine(e)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      """(c1 : bool) =>
         |  if (
         |    c1 && c2
         |      || c3 && c4
         |  ) then {
         |    1:u8
         |  } else {
         |    0:u8
         |  }
         |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.display(e, maxWidth = 25)
    assert(actualMultiLine == expectedMultiLine)
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
      s"sbuild(42:u8)(sgn(data(s)) + j, true) { (j : i9) = { init: -10:i9, next: 2:i9 + j } } { (s : Stm[u8, -1:i1]) = { stm: sbuild(42:u8)(i, true) { (i : u8) = { init: 0:u8, next: 1:u8 + i } } {}, ready: true } }"
    assert(ExprPrinter.displayOneLine(e) == expectedOneLine)

    val expectedMultiLine =
      s"""sbuild(42:u8)(sgn(data(s)) + j, true) {
         |  (j : i9) = {
         |    init: -10:i9,
         |    next: 2:i9 + j
         |  }
         |} {
         |  (s : Stm[u8, -1:i1]) = {
         |    stm: sbuild(42:u8)(i, true) { (i : u8) = { init: 0:u8, next: 1:u8 + i } } {},
         |    ready: true
         |  }
         |}
         |""".stripMargin.stripTrailing
    assert(ExprPrinter.display(e) == expectedMultiLine)
  }

  test("StmBuild with multi-line or") {
    val c1 = Param("c1", -1)(TyBool)
    val c2 = Param("c2", -1)(TyBool)
    val c3 = Param("c3", -1)(TyBool)
    val c4 = Param("c4", -1)(TyBool)
    val s = StmBuild(C(10)(U8), (c1 && c2) || (c3 && c4), True)()

    val expectedOneLine = "sbuild(10:u8)(c1 && c2 || c3 && c4, true) {} {}"
    val actualOneLine = ExprPrinter.displayOneLine(s)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      s"""sbuild(10:u8)(
         |  c1 && c2
         |    || c3 && c4,
         |  true
         |) {} {}
         |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.displayMultiLine(s, maxWidth = 20)
    assert(actualMultiLine == expectedMultiLine)
  }

  test("LetStm") {
    val n = 6
    val s1 = Param("s1", -1)(Missing)
    val s1Val = StmCount(C(n)(U8))()
    val s2 = Param("s2", -1)(Missing)
    val s2Val = StmCst(C(n)(U8), True)()
    val let =
      LetStm(
        1,
        s1,
        s1Val,
        LetStm(1, s2, s2Val, StmZip(StmZip(s1, s2)(), StmZip(s2, s1)())())()
      )()

    val expectedOneLine =
      s"letstm[1] s1 = StmCount($n:u8) in letstm[1] s2 = StmCst($n:u8, true) in StmZip(StmZip(s1, s2), StmZip(s2, s1))"
    val actualOneLine = ExprPrinter.displayOneLine(let)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      s"""letstm[1] s1 = StmCount($n:u8) in
         |letstm[1] s2 = StmCst($n:u8, true) in
         |StmZip(StmZip(s1, s2), StmZip(s2, s1))
         |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.displayMultiLine(let)
    assert(actualMultiLine == expectedMultiLine)
  }

  test("[[0, 1]v, [2, 3]v]v") {
    val e = VecLiteral(
      VecLiteral(C(0)(U8), C(1)(U8))(),
      VecLiteral(C(2)(U8), C(3)(U8))()
    )()

    val expectedOneLine = s"[[0:u8, 1:u8]v, [2:u8, 3:u8]v]v"
    assert(ExprPrinter.displayOneLine(e) == expectedOneLine)

    val expectedMultiLine =
      s"""[
         |  [0:u8, 1:u8]v,
         |  [2:u8, 3:u8]v
         |]v
         |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.displayMultiLine(e)
    assert(actualMultiLine == expectedMultiLine)
  }

  test("[]v") {
    val e = VecLiteral()(TyVec((U8, TyBool), 0))
    val expected = "[]v:Vec[(u8, bool), 0:u0]"
    assert(ExprPrinter.displayOneLine(e) == expected)
    assert(ExprPrinter.displayMultiLine(e) == expected)
  }

  test("[[0, 1]s, [2, 3]s]s") {
    val e = StmLiteral(
      StmLiteral(C(0)(U8), C(1)(U8))(),
      StmLiteral(C(2)(U8), C(3)(U8))()
    )()

    val expectedOneLine = s"[[0:u8, 1:u8]s, [2:u8, 3:u8]s]s"
    assert(ExprPrinter.displayOneLine(e) == expectedOneLine)

    val expectedMultiLine =
      s"""[
         |  [0:u8, 1:u8]s,
         |  [2:u8, 3:u8]s
         |]s
         |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.displayMultiLine(e)
    assert(actualMultiLine == expectedMultiLine)
  }

  test("[]s") {
    val e = StmLiteral()(TyStm((U8, TyBool), 0))
    val expected = "[]s:Stm[(u8, bool), 0:u0]"
    assert(ExprPrinter.displayOneLine(e) == expected)
    assert(ExprPrinter.displayMultiLine(e) == expected)
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
