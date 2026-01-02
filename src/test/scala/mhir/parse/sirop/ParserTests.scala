package mhir.parse
package sirop

import mhir.ir._
import org.scalatest.funsuite.AnyFunSuite

class ParserTests extends AnyFunSuite {

  private val x = Param("x", -1)(Missing)
  private val y = Param("y", -1)(Missing)
  private val z = Param("z", -1)(Missing)

  test("ParenthesizedExpr") {
    assert(Parser.parse("(42)") == C(42)())
    assert(Parser.parse("(true)") == True)
  }

  test("EmptyTuple") {
    assert(Parser.parse("()") == Tuple()())
  }

  test("1-tuple") {
    assert(Parser.parse("(42,)") == Tuple(C(42)())())
  }

  test("2-tuple") {
    assert(Parser.parse("(true, 1_000)") == Tuple(True, C(1000)())())
  }

  test("3-tuple") {
    assert(Parser.parse("(1, false, ())") == Tuple(C(1)(), False, Tuple()())())
  }

  test("Ident") {
    assert(Parser.parse("x") == x)
  }

  test("undefined[()]") {
    assert(Parser.parse("undefined[()]") == Undefined(TyTuple()))
  }

  test("undefined[(u8,)]") {
    assert(Parser.parse("undefined[(u8,)]") == Undefined(TyTuple(U8)))
  }

  test("undefined[(u8)]") {
    assert(Parser.parse("undefined[(u8)]") == Undefined(U8))
  }

  test("undefined[(i16, bool, u8, Vec[u32, 5])]") {
    val src = "undefined[(i16, bool, u8, Vec[u32, 5])]"
    val expected = Undefined(TyTuple(I16, TyBool, U8, TyVec(U32, 5)))
    assert(Parser.parse(src) == expected)
  }

  test("True") {
    assert(Parser.parse("true") == True)
  }

  test("False") {
    assert(Parser.parse("false") == False)
  }

  test("IntCst:NoAnnotation:Basic") {
    assert(Parser.parse("42") == C(42)())
  }

  test("IntCst:NoAnnotation:ThousandsSeparators") {
    assert(Parser.parse("1_123_000") == C(1123000)())
  }

  test("IntCst:NoAnnotation:LeadingPlus") {
    assert(Parser.parse("+10") == C(10)())
  }

  test("IntCst:NoAnnotation:LeadingMinus") {
    assert(Parser.parse("-99") == C(-99)())
  }

  test("IntCst:ValidAnnotation:JustRight") {
    val e1 = Parser.parse("42:u6")
    assert(e1 == C(42)(TyUInt(6)))
    assert(e1.typ == TyUInt(6))
  }

  test("IntCst:ValidAnnotation:Larger") {
    val e2 = Parser.parse("42:u16")
    assert(e2 == C(42)(U16))
    assert(e2.typ == U16)
  }

  test("IntCst:ValidAnnotation:LeadingMinus") {
    val e3 = Parser.parse("-1_000_000:i32")
    assert(e3 == C(-1000000)(I32))
    assert(e3.typ == I32)
  }

  test("IntCst:BitWidthTooSmall") {
    assertThrows[SyntaxError](Parser.parse("4:u2"))
    assertThrows[SyntaxError](Parser.parse("-65:u6"))
  }

  test("IntCst:NonIntType") {
    assertThrows[SyntaxError](Parser.parse("42:bool"))
    assertThrows[SyntaxError](Parser.parse("42:(u32,u32)"))
  }

  test("VecLiteral:Empty") {
    val src = "[]v:Vec[u8,0]"
    val actual = Parser.parse(src)
    assert(actual == VecLiteral()())
    assert(actual.typ == TyVec(U8, 0))
  }

  test("VecLiteral:Empty:MissingTypeAnnotation") {
    val exc = intercept[SyntaxError](Parser.parse("[]v"))
    assert(
      exc.getMessage.contains("missing type annotation for empty Vec literal")
    )
  }

  test("VecLiteral:Empty:WrongLengthInTypeAnnotation") {
    val exc = intercept[SyntaxError](Parser.parse("[]v:Vec[u8, 1]"))
    assert(
      exc.getMessage.contains(
        "wrong length in Vec type annotation: 1:u1 (expected 0)"
      )
    )
  }

  test("VecLiteral:Empty:WrongTypeAnnotation") {
    val exc = intercept[SyntaxError](Parser.parse("[]v:Stm[u8, 0]"))
    assert(exc.getMessage.contains("expected a Vec type"))
  }

  test("VecLiteral:OneElem") {
    assert(Parser.parse("[42:u8]v") == VecLiteral(C(42)(U8))())
  }

  test("VecLiteral:OneElem:TypeAnnotation") {
    val exc = intercept[SyntaxError](Parser.parse("[42:u8]v:Vec[u8, 1]"))
    assert(
      exc.getMessage.contains(
        "type annotations are forbidden for non-empty Vec literals"
      )
    )
  }

  test("VecLiteral:ThreeElems") {
    val src = "[false, true, false]v"
    val expected = VecLiteral(False, True, False)()
    assert(Parser.parse(src) == expected)
  }

  test("StmLiteral:Empty") {
    val src = "[]s:Stm[u8,0]"
    val actual = Parser.parse(src)
    assert(actual == StmLiteral()())
    assert(actual.typ == TyStm(U8, 0))
  }

  test("StmLiteral:Empty:MissingTypeAnnotation") {
    val exc = intercept[SyntaxError](Parser.parse("[]s"))
    assert(
      exc.getMessage.contains("missing type annotation for empty Stm literal")
    )
  }

  test("StmLiteral:Empty:WrongLengthInTypeAnnotation") {
    val exc = intercept[SyntaxError](Parser.parse("[]s:Stm[u8, 1]"))
    assert(
      exc.getMessage.contains(
        "wrong length in Stm type annotation: 1:u1 (expected 0)"
      )
    )
  }

  test("StmLiteral:Empty:WrongTypeAnnotation") {
    val exc = intercept[SyntaxError](Parser.parse("[]s:Vec[u8, 0]"))
    assert(exc.getMessage.contains("expected a Stm type"))
  }

  test("StmLiteral:OneElem") {
    assert(Parser.parse("[42:u8]s") == StmLiteral(C(42)(U8))())
  }

  test("StmLiteral:OneElem:TypeAnnotation") {
    val exc = intercept[SyntaxError](Parser.parse("[42:u8]s:Stm[u8, 1]"))
    assert(
      exc.getMessage.contains(
        "type annotations are forbidden for non-empty Stm literals"
      )
    )
  }

  test("StmLiteral:ThreeElems") {
    val src = "[false, true, false]s"
    val expected = StmLiteral(False, True, False)()
    assert(Parser.parse(src) == expected)
  }

  test("Pad") {
    assert(Parser.parse("pad7(x)") == PadTo(x, 7)())
    assert(Parser.parse("pad16(x)") == PadTo(x, 16)())
  }

  test("Truncate") {
    assert(Parser.parse("truncate7(x)") == TruncateTo(x, 7)())
    assert(Parser.parse("truncate16(x)") == TruncateTo(x, 16)())
  }

  test("Sign") {
    assert(Parser.parse("sign(x)") == ToSigned(x)())
  }

  test("Unsign") {
    assert(Parser.parse("unsign(x)") == ToUnsigned(x)())
  }

  test("Sdata") {
    assert(Parser.parse("sdata(s)") == StmData(Param("s", -1)(Missing))())
  }

  test("vbuild") {
    val src = "vbuild(42) { (i: u8) => sign(i) }"
    val expected = VecBuild(42, U8 ::+ (i => ToSigned(i)()))()
    val actual = Parser.parse(src)
    assert(actual == expected)
    assert(actual.asInstanceOf[VecBuild].f.param.typ == expected.f.param.typ)
  }

  // One accumulator, zero producers
  test("sbuild:StmCount") {
    val src =
      "sbuild(42)(i, true) { (i : u8) = { init : 0:u8, next : 1:u8 + i } } {}"
    val expected = {
      val i = Param("i", -1)(U8)
      StmBuild(
        42,
        i,
        True,
        Map[Param, (Expr, Expr)](i -> (C(0)(U8), Sum(C(1)(U8), i)()))
      )()
    }
    assert(Parser.parse(src) == expected)
  }

  // Two accumulators, zero producers
  test("sbuild:StmCountWithBool") {
    val src =
      """sbuild(n)((i, b), true) {
        |    (i : u16) = { init: 0:u16, next: 1:u16 + i },
        |    (b : bool) = { init: true, next: !b}
        |} {}
        |""".stripMargin
    val expected = {
      val n = Param("n", -1)(Missing)
      val i = Param("i", -1)(U16)
      val b = Param("b", -1)(TyBool)
      StmBuild(
        n,
        Tuple(i, b)(),
        True,
        Map[Param, (Expr, Expr)](
          i -> (C(0)(U8), Sum(C(1)(U8), i)()),
          b -> (True, !b)
        )
      )()
    }
    assert(Parser.parse(src) == expected)
  }

  // Zero accumulators, one producer
  test("sbuild:StmMap") {
    val src =
      """sbuild(42)(sdata(s) + 5:u8, true) {} {
        |    (s : Stm[u8, 42]) = { stm: s, ready: true }
        |}
        |""".stripMargin
    val expected = {
      val s = Param("s", -1)(TyStm(U8, 42))
      StmBuild(
        42,
        Sum(StmData(s)(), C(5)(U8))(),
        True,
        Map[Param, (Expr, Expr)](s -> (s, True))
      )()
    }
    assert(Parser.parse(src) == expected)
  }

  // Zero accumulators, two producers
  test("sbuild:StmZip") {
    val src =
      """sbuild(42)( (sdata(s1), sdata(s2)), true) {} {
        |    (s1 : Stm[i16, 42]) = { stm: s1, ready: true },
        |    (s2 : Stm[bool, 42]) = { stm: s2, ready: true }
        |}
        |""".stripMargin
    val expected = {
      val s1 = Param("s1", -1)(TyStm(I16, 42))
      val s2 = Param("s2", -1)(TyStm(TyBool, 42))
      StmBuild(
        42,
        Tuple(StmData(s1)(), StmData(s2)())(),
        True,
        Map[Param, (Expr, Expr)](
          s1 -> (s1, True),
          s2 -> (s2, True)
        )
      )()
    }
    assert(Parser.parse(src) == expected)
  }

  test("sdata(s).0.1.2") {
    val src = "sdata(s).0.1.2"
    val expected = StmData(Param("s", -1)(Missing))().__0.__1.__2
    assert(Parser.parse(src) == expected)
  }

  test("sdata(s)[i][j][0]") {
    val src = "sdata(s)[i][j][0]"
    val expected = VecAccess(
      VecAccess(
        VecAccess(
          StmData(Param("s", -1)(Missing))(),
          Param("i", -1)(Missing)
        )(),
        Param("j", -1)(Missing)
      )(),
      0
    )()
    assert(Parser.parse(src) == expected)
  }

  test("f(42)(x, y)()") {
    val src = "f(42)(x, y)()"
    val expected = FunCall(
      FunCall(
        FunCall(Param("f", -1)(Missing), 42)(),
        Tuple(x, y)()
      )(),
      Tuple()()
    )()
    assert(Parser.parse(src) == expected)
  }

  test("!x") {
    assert(Parser.parse("!x") == !x)
  }

  test("x * y * z") {
    val src = "x * y * z"
    val expected = Prod(Prod(x, y)(), z)()
    assert(Parser.parse(src) == expected)
  }

  test("x *% y *% z") {
    val src = "x *% y *% z"
    val expected = WrappingProd(WrappingProd(x, y)(), z)()
    assert(Parser.parse(src) == expected)
  }

  test("x / y / z") {
    val src = "x / y / z"
    val expected = Div(Div(x, y)(), z)()
    assert(Parser.parse(src) == expected)
  }

  test("x % y % z") {
    val src = "x % y % z"
    val expected = Mod(Mod(x, y)(), z)()
    assert(Parser.parse(src) == expected)
  }

  test("x + y + z") {
    val src = "x + y + z"
    val expected = Sum(Sum(x, y)(), z)()
    assert(Parser.parse(src) == expected)
  }

  test("x +% y +% z") {
    val src = "x +% y +% z"
    val expected = WrappingSum(WrappingSum(x, y)(), z)()
    assert(Parser.parse(src) == expected)
  }

  test("x -% y -% z") {
    val src = "x -% y -% z"
    val expected = WrappingDiff(WrappingDiff(x, y)(), z)()
    assert(Parser.parse(src) == expected)
  }

  test("x + y * z") {
    val src = "x + y * z"
    val expected = Sum(x, Prod(y, z)())()
    assert(Parser.parse(src) == expected)
  }

  test("x * y + z") {
    val src = "x * y + z"
    val expected = Sum(Prod(x, y)(), z)()
    assert(Parser.parse(src) == expected)
  }

  test("(x + y) * z") {
    val src = "(x + y) * z"
    val expected = Prod(Sum(x, y)(), z)()
    assert(Parser.parse(src) == expected)
  }

  test("x <<< y <<< z") {
    val src = "x <<< y <<< z"
    val expected = LLShift(LLShift(x, y)(), z)()
    assert(Parser.parse(src) == expected)
  }

  test("x >>> y >>> z") {
    val src = "x >>> y >>> z"
    val expected = LRShift(LRShift(x, y)(), z)()
    assert(Parser.parse(src) == expected)
  }

  test("x < y") {
    assert(Parser.parse("x < y") == (x lt y))
  }

  test("x > y") {
    assert(Parser.parse("x > y") == (x gt y))
  }

  test("x <= y") {
    assert(Parser.parse("x <= y") == (x leq y))
  }

  test("x >= y") {
    assert(Parser.parse("x >= y") == (x geq y))
  }

  test("x == y") {
    assert(Parser.parse("x == y") == (x equ y))
  }

  test("x != y") {
    assert(Parser.parse("x != y") == (x nequ y))
  }

  test("x && y && z") {
    assert(Parser.parse("x && y && z") == And(And(x, y)(), z)())
  }

  test("x || y || z") {
    assert(Parser.parse("x || y || z") == Or(Or(x, y)(), z)())
  }

  test("x || y && z") {
    assert(Parser.parse("x || y && z") == Or(x, And(y, z)())())
  }

  test("x && y || z") {
    assert(Parser.parse("x && y || z") == Or(And(x, y)(), z)())
  }

  test("(x || y) && z") {
    assert(Parser.parse("(x || y) && z") == And(Or(x, y)(), z)())
  }

  test("if c1 || c2 then x + y else x * y") {
    val c1 = Param("c1", -1)(Missing)
    val c2 = Param("c2", -1)(Missing)
    val src = "if c1 || c2 then x + y else x * y"
    val expected = Mux(c1 || c2, Sum(x, y)(), Prod(x, y)())()
    assert(Parser.parse(src) == expected)
  }

  test("(_) => true") {
    val src = "(_) => true"
    val expected = Missing ::+ (_ => True)
    val actual = Parser.parse(src)
    assert(actual == expected)
    assert(actual.asInstanceOf[Function].param.typ == expected.param.typ)
  }

  test("(s : Stm[u8, 42]) => s") {
    val src = "(s : Stm[u8, 42]) => s"
    val expected = TyStm(U8, 42) ::+ (s => s)
    val actual = Parser.parse(src)
    assert(actual == expected)
    assert(actual.asInstanceOf[Function].param.typ == expected.param.typ)
  }

  test("(f : u32 -> bool) => f") {
    val src = "(f : u32 -> bool) => f"
    val expected = (U32 ->: TyBool) ::+ (f => f)
    val actual = Parser.parse(src)
    assert(actual == expected)
    assert(actual.asInstanceOf[Function].param.typ == expected.param.typ)
  }

  test("(g : i32 -> i16 -> i8) => g") {
    val src = "(f : i32 -> i16 -> i8) => f"
    val expected = TyArrow(I32, TyArrow(I16, I8)) ::+ (f => f)
    val actual = Parser.parse(src)
    assert(actual == expected)
    assert(actual.asInstanceOf[Function].param.typ == expected.param.typ)
  }

  test("(h:(i32 -> i16) -> i8) => h") {
    val src = "(h:(i32 -> i16) -> i8) => h"
    val expected = TyArrow(TyArrow(I32, I16), I8) ::+ (f => f)
    val actual = Parser.parse(src)
    assert(actual == expected)
    assert(actual.asInstanceOf[Function].param.typ == expected.param.typ)
  }

  test("letstm[42] x = s1 in s2") {
    val src = "letstm[42] x = s1 in s2"
    val expected =
      LetStm(42, x, Param("s1", -1)(Missing), Param("s2", -1)(Missing))()
    val actual = Parser.parse(src)
    assert(actual == expected)
    assert(actual.asInstanceOf[LetStm].x.typ == expected.x.typ)
  }

  test("letstm[n] x : Stm[u8, 42] = s1 in s2") {
    val src = "letstm[n] x : Stm[u8, 42] = s1 in s2"
    val expected = LetStm(
      Param("n", -1)(Missing),
      Param("x", -1)(TyStm(U8, 42)),
      Param("s1", -1)(Missing),
      Param("s2", -1)(Missing)
    )()
    val actual = Parser.parse(src)
    assert(actual == expected)
    assert(actual.asInstanceOf[LetStm].x.typ == expected.x.typ)
  }

  // TODO: Forbid leading zeros in int literals?
  // TODO: Test comments
  // TODO: Test unclosed comment
}
