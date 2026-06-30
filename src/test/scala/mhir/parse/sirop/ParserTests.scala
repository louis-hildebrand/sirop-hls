package mhir.parse
package sirop

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar._
import org.scalatest.funsuite.AnyFunSuite

class ParserTests extends AnyFunSuite {

  private val x = Param("x", -1)(Missing)
  private val y = Param("y", -1)(Missing)
  private val z = Param("z", -1)(Missing)

  test("ParenthesizedExpr") {
    assert(Parser.parse("(42)").body == C(42)())
    assert(Parser.parse("(true)").body == True)
  }

  test("EmptyTuple") {
    assert(Parser.parse("()").body == Tuple()())
  }

  test("1-tuple") {
    assert(Parser.parse("(42,)").body == Tuple(C(42)())())
  }

  test("2-tuple") {
    assert(Parser.parse("(true, 1_000)").body == Tuple(True, C(1000)())())
  }

  test("3-tuple") {
    assert(
      Parser.parse("(1, false, ())").body == Tuple(C(1)(), False, Tuple()())()
    )
  }

  test("Ident") {
    assert(Parser.parse("x").body == x)
  }

  test("undefined[()]") {
    assert(Parser.parse("undefined[()]").body == Undefined(TyTuple()))
  }

  test("undefined[(u8,)]") {
    assert(Parser.parse("undefined[(u8,)]").body == Undefined(TyTuple(U8)))
  }

  test("undefined[(u8)]") {
    assert(Parser.parse("undefined[(u8)]").body == Undefined(U8))
  }

  test("undefined[(i16, bool, u8, Vec[u32, 5])]") {
    val src = "undefined[(i16, bool, u8, Vec[u32, 5])]"
    val expected = Undefined(TyTuple(I16, TyBool, U8, TyVec(U32, 5)))
    assert(Parser.parse(src).body == expected)
  }

  test("True") {
    assert(Parser.parse("true").body == True)
  }

  test("False") {
    assert(Parser.parse("false").body == False)
  }

  test("IntCst:NoAnnotation:Basic") {
    assert(Parser.parse("42").body == C(42)())
  }

  test("IntCst:NoAnnotation:ThousandsSeparators") {
    assert(Parser.parse("1_123_000").body == C(1123000)())
  }

  test("IntCst:NoAnnotation:LeadingPlus") {
    assert(Parser.parse("+10").body == C(10)())
  }

  test("IntCst:NoAnnotation:LeadingMinus") {
    assert(Parser.parse("-99").body == C(-99)())
  }

  test("IntCst:ValidAnnotation:JustRight") {
    val e1 = Parser.parse("42:u6").body
    assert(e1 == C(42)(TyUInt(6)))
    assert(e1.typ == TyUInt(6))
  }

  test("IntCst:ValidAnnotation:Larger") {
    val e2 = Parser.parse("42:u16").body
    assert(e2 == C(42)(U16))
    assert(e2.typ == U16)
  }

  test("IntCst:ValidAnnotation:LeadingMinus") {
    val e3 = Parser.parse("-1_000_000:i32").body
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
    val actual = Parser.parse(src).body
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
    assert(Parser.parse("[42:u8]v").body == VecLiteral(C(42)(U8))())
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
    assert(Parser.parse(src).body == expected)
  }

  test("StmLiteral:Empty") {
    val src = "[]s:Stm[u8,0]"
    val actual = Parser.parse(src).body
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
    assert(Parser.parse("[42:u8]s").body == StmLiteral(C(42)(U8))())
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
    assert(Parser.parse(src).body == expected)
  }

  test("Pad") {
    assert(Parser.parse("pad7(x)").body == PadTo(x, 7)())
    assert(Parser.parse("x.pad16()").body == PadTo(x, 16)())
  }

  test("Truncate") {
    assert(Parser.parse("truncate7(x)").body == TruncateTo(x, 7)())
    assert(Parser.parse("x.truncate16()").body == TruncateTo(x, 16)())
  }

  test("Sign") {
    assert(Parser.parse("sign(x)").body == ToSigned(x)())
    assert(Parser.parse("x.sign()").body == ToSigned(x)())
  }

  test("Unsign") {
    assert(Parser.parse("unsign(x)").body == ToUnsigned(x)())
    assert(Parser.parse("x.unsign()").body == ToUnsigned(x)())
  }

  test("Sdata") {
    assert(Parser.parse("sdata(s)").body == StmData(Param("s", -1)(Missing))())
  }

  test("vbuild") {
    val src = "vbuild(42) { (i: u8) => sign(i) }"
    val expected = VecBuild(42, U8 ::+ (i => ToSigned(i)()))()
    val actual = Parser.parse(src).body
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
        Map[Param, (Expr, Expr)](i -> (C(0)(U8), SmartSum(C(1)(U8), i)()))
      )()
    }
    assert(Parser.parse(src).body == expected)
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
          i -> (C(0)(U8), SmartSum(C(1)(U8), i)()),
          b -> (True, !b)
        )
      )()
    }
    assert(Parser.parse(src).body == expected)
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
        SmartSum(StmData(s)(), C(5)(U8))(),
        True,
        Map[Param, (Expr, Expr)](s -> (s, True))
      )()
    }
    assert(Parser.parse(src).body == expected)
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
    assert(Parser.parse(src).body == expected)
  }

  test("sdata(s).0.1.2") {
    val src = "sdata(s).0.1.2"
    val expected = StmData(Param("s", -1)(Missing))().__0.__1.__2
    assert(Parser.parse(src).body == expected)
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
    assert(Parser.parse(src).body == expected)
  }

  test("v[i:j]") {
    val actual = Parser.parse("v[2+1:4*2]").body
    val expected = VecSlice(
      Param("v", -1)(Missing),
      SmartSum(2, 1)(),
      SmartProd(4, 2)(),
      Tuple()()
    )()
    assert(actual == expected)
  }

  test("v[i:]") {
    val actual = Parser.parse("v[i:]").body
    val expected = VecSlice(
      Param("v", -1)(Missing),
      Param("i", -1)(Missing),
      Tuple()(),
      Tuple()()
    )()
    assert(actual == expected)
  }

  test("v[:j]") {
    val actual = Parser.parse("v[:j]").body
    val expected = VecSlice(
      Param("v", -1)(Missing),
      Tuple()(),
      Param("j", -1)(Missing),
      Tuple()()
    )()
    assert(actual == expected)
  }

  test("v[:]") {
    val actual = Parser.parse("v[:]").body
    val expected = VecSlice(
      Param("v", -1)(Missing),
      Tuple()(),
      Tuple()(),
      Tuple()()
    )()
    assert(actual == expected)
  }

  test("v[i:j:k]") {
    val actual = Parser.parse("v[i:j:k]").body
    val expected = VecSlice(
      Param("v", -1)(Missing),
      Param("i", -1)(Missing),
      Param("j", -1)(Missing),
      Param("k", -1)(Missing)
    )()
    assert(actual == expected)
  }

  test("v[i:j:]") {
    val actual = Parser.parse("v[i:j:]").body
    val expected = VecSlice(
      Param("v", -1)(Missing),
      Param("i", -1)(Missing),
      Param("j", -1)(Missing),
      Tuple()()
    )()
    assert(actual == expected)
  }

  test("v[i::k]") {
    val actual = Parser.parse("v[i::k]").body
    val expected = VecSlice(
      Param("v", -1)(Missing),
      Param("i", -1)(Missing),
      Tuple()(),
      Param("k", -1)(Missing)
    )()
    assert(actual == expected)
  }

  test("v[i::]") {
    val actual = Parser.parse("v[i::]").body
    val expected = VecSlice(
      Param("v", -1)(Missing),
      Param("i", -1)(Missing),
      Tuple()(),
      Tuple()()
    )()
    assert(actual == expected)
  }

  test("v[:j:k]") {
    val actual = Parser.parse("v[:j:k]").body
    val expected = VecSlice(
      Param("v", -1)(Missing),
      Tuple()(),
      Param("j", -1)(Missing),
      Param("k", -1)(Missing)
    )()
    assert(actual == expected)
  }

  test("v[:j:]") {
    val actual = Parser.parse("v[:j:]").body
    val expected = VecSlice(
      Param("v", -1)(Missing),
      Tuple()(),
      Param("j", -1)(Missing),
      Tuple()()
    )()
    assert(actual == expected)
  }

  test("v[::k]") {
    val actual = Parser.parse("v[::k]").body
    val expected = VecSlice(
      Param("v", -1)(Missing),
      Tuple()(),
      Tuple()(),
      Param("k", -1)(Missing)
    )()
    assert(actual == expected)
  }

  test("v[::]") {
    val actual = Parser.parse("v[::]").body
    val expected = VecSlice(
      Param("v", -1)(Missing),
      Tuple()(),
      Tuple()(),
      Tuple()()
    )()
    assert(actual == expected)
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
    assert(Parser.parse(src).body == expected)
  }

  test("!x") {
    assert(Parser.parse("!x").body == !x)
  }

  test("(!)") {
    assert(Parser.parse("(!)").body == Function(x, Not(x)())())
  }

  test("x & y & z") {
    val src = "x & y & z"
    val expected = BitwiseAnd(BitwiseAnd(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(&)") {
    val actual = Parser.parse("(&)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      BitwiseAnd(x, y)()
    )()
    assert(actual == expected)
  }

  test("x | y | z") {
    val src = "x | y | z"
    val expected = BitwiseOr(BitwiseOr(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(|)") {
    val actual = Parser.parse("(|)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      BitwiseOr(x, y)()
    )()
    assert(actual == expected)
  }

  test("x & y | z") {
    val src = "x & y | z"
    val expected = BitwiseOr(BitwiseAnd(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("x | y & z") {
    val src = "x | y & z"
    val expected = BitwiseOr(x, BitwiseAnd(y, z)())()
    assert(Parser.parse(src).body == expected)
  }

  test("(x | y) & z") {
    val src = "(x | y) & z"
    val expected = BitwiseAnd(BitwiseOr(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("x * y * z") {
    val src = "x * y * z"
    val expected = SmartProd(SmartProd(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(*)") {
    val actual = Parser.parse("(*)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SmartProd(x, y)()
    )()
    assert(actual == expected)
  }

  test("x *` y *` z") {
    val src = "x *` y *` z"
    val expected = Prod(Prod(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(*`)") {
    val actual = Parser.parse("(*`)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      Prod(x, y)()
    )()
    assert(actual == expected)
  }

  test("x *% y *% z") {
    val src = "x *% y *% z"
    val expected = SmartWrappingProd(SmartWrappingProd(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(*%)") {
    val actual = Parser.parse("(*%)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SmartWrappingProd(x, y)()
    )()
    assert(actual == expected)
  }

  test("x *%` y *%` z") {
    val src = "x *%` y *%` z"
    val expected = WrappingProd(WrappingProd(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(*%`)") {
    val actual = Parser.parse("(*%`)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      WrappingProd(x, y)()
    )()
    assert(actual == expected)
  }

  test("x *^ y *^ z") {
    val src = "x *^ y *^ z"
    val expected = SafeProd(SafeProd(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(*^)") {
    val actual = Parser.parse("(*^)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SafeProd(x, y)()
    )()
    assert(actual == expected)
  }

  test("x / y / z") {
    val src = "x / y / z"
    val expected = SmartDiv(SmartDiv(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(/)") {
    val actual = Parser.parse("(/)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SmartDiv(x, y)()
    )()
    assert(actual == expected)
  }

  test("x /` y /` z") {
    val src = "x /` y /` z"
    val expected = Div(Div(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(/`)") {
    val actual = Parser.parse("(/`)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      Div(x, y)()
    )()
    assert(actual == expected)
  }

  test("x % y % z") {
    val src = "x % y % z"
    val expected = SmartMod(SmartMod(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(%)") {
    val actual = Parser.parse("(%)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SmartMod(x, y)()
    )()
    assert(actual == expected)
  }

  test("x %` y %` z") {
    val src = "x %` y %` z"
    val expected = Mod(Mod(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(%`)") {
    val actual = Parser.parse("(%`)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      Mod(x, y)()
    )()
    assert(actual == expected)
  }

  test("x + y + z") {
    val src = "x + y + z"
    val expected = SmartSum(SmartSum(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(+)") {
    val actual = Parser.parse("(+)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SmartSum(x, y)()
    )()
    assert(actual == expected)
  }

  test("x +` y +` z") {
    val src = "x +` y +` z"
    val expected = Sum(Sum(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(+`)") {
    val actual = Parser.parse("(+`)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      Sum(x, y)()
    )()
    assert(actual == expected)
  }

  test("x +% y +% z") {
    val src = "x +% y +% z"
    val expected = SmartWrappingSum(SmartWrappingSum(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(+%)") {
    val actual = Parser.parse("(+%)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SmartWrappingSum(x, y)()
    )()
    assert(actual == expected)
  }

  test("x +%` y +%` z") {
    val src = "x +%` y +%` z"
    val expected = WrappingSum(WrappingSum(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(+%`)") {
    val actual = Parser.parse("(+%`)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      WrappingSum(x, y)()
    )()
    assert(actual == expected)
  }

  test("x +^ y +^ z") {
    val src = "x +^ y +^ z"
    val expected = SafeSum(SafeSum(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(+^)") {
    val actual = Parser.parse("(+^)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SafeSum(x, y)()
    )()
    assert(actual == expected)
  }

  test("x - y - z") {
    val src = "x - y - z"
    val expected = SmartDiff(SmartDiff(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(-)") {
    val actual = Parser.parse("(-)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SmartDiff(x, y)()
    )()
    assert(actual == expected)
  }

  test("x -% y -% z") {
    val src = "x -% y -% z"
    val expected = SmartWrappingDiff(SmartWrappingDiff(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(-%)") {
    val actual = Parser.parse("(-%)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SmartWrappingDiff(x, y)()
    )()
    assert(actual == expected)
  }

  test("x -%` y -%` z") {
    val src = "x -%` y -%` z"
    val expected = WrappingDiff(WrappingDiff(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(-%`)") {
    val actual = Parser.parse("(-%`)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      WrappingDiff(x, y)()
    )()
    assert(actual == expected)
  }

  test("x -^ y -^ z") {
    val src = "x -^ y -^ z"
    val expected = SafeDiff(SafeDiff(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(-^)") {
    val actual = Parser.parse("(-^)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SafeDiff(x, y)()
    )()
    assert(actual == expected)
  }

  test("x + y * z") {
    val src = "x + y * z"
    val expected = SmartSum(x, SmartProd(y, z)())()
    assert(Parser.parse(src).body == expected)
  }

  test("x * y + z") {
    val src = "x * y + z"
    val expected = SmartSum(SmartProd(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(x + y) * z") {
    val src = "(x + y) * z"
    val expected = SmartProd(SmartSum(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("bits(x)") {
    val src = "bits(x)"
    val expected = Bits(x)()
    assert(Parser.parse(src).body == expected)
  }

  test("x.bits()") {
    val src = "x.bits()"
    val expected = Bits(x)()
    assert(Parser.parse(src).body == expected)
  }

  test("interpret_as:[bool](x)") {
    val src = "interpret_as:[bool](x)"
    val expected = InterpretAs(x, TyBool)()
    assert(Parser.parse(src).body == expected)
  }

  test("x.interpret_as:[(i16, bool)]()") {
    val src = "x.interpret_as:[(i16, bool)]()"
    val expected = InterpretAs(x, (I16, TyBool))()
    assert(Parser.parse(src).body == expected)
  }

  test("zeros:[i16]()") {
    val src = "zeros:[i16]()"
    val expected = AllZero(I16)
    assert(Parser.parse(src).body == expected)
  }

  test("ones:[i16]()") {
    val src = "ones:[i16]()"
    val expected = AllOne(I16)
    assert(Parser.parse(src).body == expected)
  }

  test("~x") {
    val src = "~x"
    val expected = BitwiseNot(x)()
    assert(Parser.parse(src).body == expected)
  }

  test("(~)") {
    assert(Parser.parse("(~)").body == Function(x, BitwiseNot(x)())())
  }

  test("x << y << z") {
    val src = "x << y << z"
    val expected = LShift(LShift(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(<<)") {
    val actual = Parser.parse("(<<)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      LShift(x, y)()
    )()
    assert(actual == expected)
  }

  test("x >> y >> z") {
    val src = "x >> y >> z"
    val expected = ARShift(ARShift(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(>>)") {
    val actual = Parser.parse("(>>)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      ARShift(x, y)()
    )()
    assert(actual == expected)
  }

  test("x >>> y >>> z") {
    val src = "x >>> y >>> z"
    val expected = LRShift(LRShift(x, y)(), z)()
    assert(Parser.parse(src).body == expected)
  }

  test("(>>>)") {
    val actual = Parser.parse("(>>>)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      LRShift(x, y)()
    )()
    assert(actual == expected)
  }

  test("x < y") {
    assert(Parser.parse("x < y").body == SmartLessThan(x, y)())
  }

  test("(<)") {
    val actual = Parser.parse("(<)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SmartLessThan(x, y)()
    )()
    assert(actual == expected)
  }

  test("x <` y") {
    assert(Parser.parse("x <` y").body == LessThan(x, y)())
  }

  test("(<`)") {
    val actual = Parser.parse("(<`)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      LessThan(x, y)()
    )()
    assert(actual == expected)
  }

  test("x > y") {
    assert(Parser.parse("x > y").body == SmartGreaterThan(x, y)())
  }

  test("(>)") {
    val actual = Parser.parse("(>)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SmartGreaterThan(x, y)()
    )()
    assert(actual == expected)
  }

  test("x <= y") {
    assert(Parser.parse("x <= y").body == SmartLessThanOrEqual(x, y)())
  }

  test("(<=)") {
    val actual = Parser.parse("(<=)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SmartLessThanOrEqual(x, y)()
    )()
    assert(actual == expected)
  }

  test("x >= y") {
    assert(Parser.parse("x >= y").body == SmartGreaterThanOrEqual(x, y)())
  }

  test("(>=)") {
    val actual = Parser.parse("(>=)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SmartGreaterThanOrEqual(x, y)()
    )()
    assert(actual == expected)
  }

  test("x == y") {
    assert(Parser.parse("x == y").body == SmartEqual(x, y)())
  }

  test("(==)") {
    val actual = Parser.parse("==").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SmartEqual(x, y)()
    )()
    assert(actual == expected)
  }

  test("x ==` y") {
    assert(Parser.parse("x ==` y").body == Equal(x, y)())
  }

  test("(==`)") {
    val actual = Parser.parse("==`").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      Equal(x, y)()
    )()
    assert(actual == expected)
  }

  test("x != y") {
    assert(Parser.parse("x != y").body == SmartNotEqual(x, y)())
  }

  test("(!=)") {
    val actual = Parser.parse("(!=)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SmartNotEqual(x, y)()
    )()
    assert(actual == expected)
  }

  test("x && y && z") {
    assert(Parser.parse("x && y && z").body == And(And(x, y)(), z)())
  }

  test("(&&)") {
    val actual = Parser.parse("(&&)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      And(x, y)()
    )()
    assert(actual == expected)
  }

  test("x || y || z") {
    assert(Parser.parse("x || y || z").body == Or(Or(x, y)(), z)())
  }

  test("(||)") {
    val actual = Parser.parse("(||)").body
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      Or(x, y)()
    )()
    assert(actual == expected)
  }

  test("x || y && z") {
    assert(Parser.parse("x || y && z").body == Or(x, And(y, z)())())
  }

  test("x && y || z") {
    assert(Parser.parse("x && y || z").body == Or(And(x, y)(), z)())
  }

  test("(x || y) && z") {
    assert(Parser.parse("(x || y) && z").body == And(Or(x, y)(), z)())
  }

  test("if c1 || c2 then x + y else x * y") {
    val c1 = Param("c1", -1)(Missing)
    val c2 = Param("c2", -1)(Missing)
    val src = "if c1 || c2 then x + y else x * y"
    val expected = SmartIf(c1 || c2, SmartSum(x, y)(), SmartProd(x, y)())()
    assert(Parser.parse(src).body == expected)
  }

  test("if (c) then { t } else { f }") {
    val src = "if (c) then { t } else { f }"
    val expected = SmartIf(
      Param("c", -1)(Missing),
      Param("t", -1)(Missing),
      Param("f", -1)(Missing)
    )()
    assert(Parser.parse(src).body == expected)
  }

  test("(_) => true") {
    val src = "(_) => true"
    val expected = Missing ::+ (_ => True)
    val actual = Parser.parse(src).body
    assert(actual == expected)
    assert(actual.asInstanceOf[Function].param.typ == expected.param.typ)
  }

  test("(s : Stm[u8, 42]) => s") {
    val src = "(s : Stm[u8, 42]) => s"
    val expected = TyStm(U8, 42) ::+ (s => s)
    val actual = Parser.parse(src).body
    assert(actual == expected)
    assert(actual.asInstanceOf[Function].param.typ == expected.param.typ)
  }

  test("(f : u32 -> bool) => f") {
    val src = "(f : u32 -> bool) => f"
    val expected = (U32 ->: TyBool) ::+ (f => f)
    val actual = Parser.parse(src).body
    assert(actual == expected)
    assert(actual.asInstanceOf[Function].param.typ == expected.param.typ)
  }

  test("(g : i32 -> i16 -> i8) => g") {
    val src = "(f : i32 -> i16 -> i8) => f"
    val expected = TyArrow(I32, TyArrow(I16, I8)) ::+ (f => f)
    val actual = Parser.parse(src).body
    assert(actual == expected)
    assert(actual.asInstanceOf[Function].param.typ == expected.param.typ)
  }

  test("(h:(i32 -> i16) -> i8) => h") {
    val src = "(h:(i32 -> i16) -> i8) => h"
    val expected = TyArrow(TyArrow(I32, I16), I8) ::+ (f => f)
    val actual = Parser.parse(src).body
    assert(actual == expected)
    assert(actual.asInstanceOf[Function].param.typ == expected.param.typ)
  }

  test("Pattern:() => 42") {
    val src = "@() => 42:u8"
    val expected = PatternFunction(TuplePattern(), C(42)(U8))()
    val actual = Parser.parse(src).body
    assert(actual == expected)
  }

  test("Pattern:(x) => x") {
    val src = "@(x) => x"
    val exc = intercept[SyntaxError](Parser.parse(src))
    assert(
      exc.getMessage.toLowerCase.contains(
        "to match a 1-tuple, add a comma, as in (x,)."
          + " otherwise, omit the parentheses."
      )
    )
  }

  test("Pattern:(x: u16) => x") {
    val src = "@(x: u16) => x"
    val exc = intercept[SyntaxError](Parser.parse(src))
    assert(exc.loc.contains(SourcePoint(1, 2)))
    assert(
      exc.getMessage.toLowerCase.contains(
        "to match a 1-tuple, add a comma, as in (x: u16,)."
          + " otherwise, omit the parentheses."
      )
    )
  }

  test("Pattern:(x,) => x") {
    val src = "@(x,) => x"
    val x = Param("x", -1)(Missing)
    val expected = PatternFunction(TuplePattern(ParamPattern(x)), x)()
    val actual = Parser.parse(src).body
    assert(actual == expected)
  }

  test("Pattern:(x: u32, y: bool) => if y then x + 1 else x") {
    val src = "@(x: u32, y: bool) => if y then x + 1:u32 else x"
    val x = Param("x", -1)(U32)
    val y = Param("y", -1)(TyBool)
    val expected = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      SmartIf(y, SmartSum(x, C(1)(U32))(), x)()
    )()
    val actual = Parser.parse(src).body
    assert(actual == expected)
    assert(actual.asInstanceOf[PatternFunction].p.typ == TyTuple(U32, TyBool))
  }

  test("Pattern:(((x: u8, y), z: bool), (), w) => (x, y, z, w)") {
    val src = "@(((x: u8, y), z: bool), (), w) => (x, y, z, w)"
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(Missing)
    val z = Param("z", -1)(TyBool)
    val w = Param("w", -1)(Missing)
    val p = TuplePattern(
      TuplePattern(
        TuplePattern(ParamPattern(x), ParamPattern(y)),
        ParamPattern(z)
      ),
      TuplePattern(),
      ParamPattern(w)
    )
    val expected = PatternFunction(p, Tuple(x, y, z, w)())()
    val actual = Parser.parse(src).body
    assert(actual == expected)
  }

  test("Pattern:DuplicateNames") {
    val src = "@(a, b, ((y, x), (x, (y,))), z) => x"
    val exc = intercept[SyntaxError](Parser.parse(src))
    assert(exc.loc.contains(SourcePoint(1, 9)))
    assert(exc.getMessage.contains("duplicate parameter(s) in pattern: x, y"))
  }

  test("letstm[42] x = s1 in s2") {
    val src = "letstm[42] x = s1 in s2"
    val expected =
      LetStm(42, x, Param("s1", -1)(Missing), Param("s2", -1)(Missing))()
    val actual = Parser.parse(src).body
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
    val actual = Parser.parse(src).body
    assert(actual == expected)
    assert(actual.asInstanceOf[LetStm].x.typ == expected.x.typ)
  }

  test("let x = 42 in x + 5") {
    val src = "let x = 42 in x + 5"
    val expected = Let(Param("x", -1)(Missing), 42, SmartSum(x, 5)())()
    val actual = Parser.parse(src).body
    assert(actual == expected)
    assert(actual.asInstanceOf[Let].x.typ == expected.x.typ)
  }

  test("let x: u8 = 250:u8 in x * x + 42:u8") {
    val src = "let x: u8 = 250:u8 in x * x + 42:u8"
    val expected = Let(
      Param("x", -1)(U8),
      C(250)(U8),
      SmartSum(SmartProd(x, x)(), C(42)(U8))()
    )()
    val actual = Parser.parse(src).body
    assert(actual == expected)
    assert(actual.asInstanceOf[Let].x.typ == expected.x.typ)
  }

  test("StmRange") {
    val src = "StmRange(100, 1:i32, -3:i32)"
    val expected = StmRange(100, C(1)(I32), C(-3)(I32))()
    assert(Parser.parse(src).body == expected)
  }

  test("StmMap") {
    val src = "StmMap(s, x => x + 5:u8)"
    val s = Param("s", -1)(Missing)
    val x = Param("x", -1)(Missing)
    val expected = StmMap(s, Function(x, SmartSum(x, 5)())())()
    assert(Parser.parse(src).body == expected)
  }

  test("StmReduce") {
    val src = "StmReduce(s, (x) => x.0 - x.1)"
    val s = Param("s", -1)(Missing)
    val x = Param("x", -1)(Missing)
    val expected = StmReduce(s, Function(x, SmartDiff(x.__0, x.__1)())())()
    assert(Parser.parse(src).body == expected)
  }

  test("StmMap2") {
    val src = "StmMap2(s1, s2, (x1) => (x2) => x1 - x2)"
    val s1 = Param("s1", -1)(Missing)
    val s2 = Param("s2", -1)(Missing)
    val x1 = Param("x1", -1)(Missing)
    val x2 = Param("x2", -1)(Missing)
    val expected =
      StmMap2(s1, s2, Function(x1, Function(x2, SmartDiff(x1, x2)())())())()
    assert(Parser.parse(src).body == expected)
  }

  test("StmZip") {
    val src = "StmZip(s1, s2)"
    val s1 = Param("s1", -1)(Missing)
    val s2 = Param("s2", -1)(Missing)
    val expected = StmZip(s1, s2)()
    assert(Parser.parse(src).body == expected)
  }

  test("Dot:MethodStyle") {
    val src =
      "s1.StmZip(s2).StmMap( (x) => x.0 * x.1 ).StmReduce( (x) => x.0 + x.1 )"
    val s1 = Param("s1", -1)(Missing)
    val s2 = Param("s2", -1)(Missing)
    val x = Param("x", -1)(Missing)
    val expected =
      StmReduce(
        StmMap(StmZip(s1, s2)(), Function(x, SmartProd(x.__0, x.__1)())())(),
        Function(x, SmartSum(x.__0, x.__1)())()
      )()
    assert(Parser.parse(src).body == expected)
  }

  test("AcceleratorAnnotation:OutName") {
    val src = "accelerator[out_name=my_name] top = (s: Stm[u8, 10]) => s"
    val prog = Parser.parse(src)
    assert(prog.name == "top")
    assert(prog.accel.annotations("out_name") == Param("my_name", -1)(Missing))
    assert(prog.body == TyStm(U8, 10) ::+ (s => s))
  }

  test("AcceleratorAnnotation:UnknownKey") {
    val src = "accelerator[foo=bar] top = (s: Stm[u8, 10]) => s"
    val ex = intercept[SyntaxError](Parser.parse(src))
    assert(ex.msg == "unknown annotation key: 'foo'")
    assert(ex.loc.contains(SourcePoint(1, 13)))
  }

  test("AcceleratorAnnotation:OutName:MissingValue") {
    val src = "accelerator [out_name] top = (s: Stm[u8, 10]) => s"
    val ex = intercept[SyntaxError](Parser.parse(src))
    assert(
      ex.msg == "missing value for annotation 'out_name'. Expected an identifier."
    )
    assert(ex.loc.contains(SourcePoint(1, 14)))
  }

  test("AcceleratorAnnotation:OutName:BadValue1") {
    val src = "accelerator[out_name=()] top = (s: Stm[u8, 10]) => s"
    val ex = intercept[SyntaxError](Parser.parse(src))
    assert(
      ex.msg == "invalid value for annotation 'out_name'. Expected an identifier."
    )
    assert(ex.loc.contains(SourcePoint(1, 13)))
  }

  test("AcceleratorAnnotation:OutName:BadValue2") {
    val src = "accelerator[out_name=1+1] top = (s: Stm[u8, 10]) => s"
    val ex = intercept[SyntaxError](Parser.parse(src))
    assert(
      ex.msg == "invalid value for annotation 'out_name'. Expected an identifier."
    )
    assert(ex.loc.contains(SourcePoint(1, 13)))
  }

  test("AcceleratorAnnotation:Clock1") {
    val src = "accelerator[clock=clk] top = (s: Stm[u8, 10]) => s"
    val prog = Parser.parse(src)
    assert(prog.clock.contains("clk"))
  }

  test("AcceleratorAnnotation:Clock2") {
    val src = "accelerator[clock=clock] top = (s: Stm[u8, 10]) => s"
    val prog = Parser.parse(src)
    assert(prog.clock.contains("clock"))
  }

  test("AcceleratorAnnotation:Clock:BadValue") {
    val src = "accelerator[clock=175] top = (s: Stm[u8, 10]) => s"
    val ex = intercept[SyntaxError](Parser.parse(src))
    assert(
      ex.msg == "invalid value for annotation 'clock'. Expected an identifier."
    )
    assert(ex.loc.contains(SourcePoint(1, 13)))
  }

  test("AcceleratorAnnotation:Reset1") {
    val src = "accelerator[reset=rst] top = (s: Stm[u8, 10]) => s"
    val prog = Parser.parse(src)
    assert(prog.reset.contains("rst"))
  }

  test("AcceleratorAnnotation:Reset2") {
    val src = "accelerator[reset=reset] top = (s: Stm[u8, 10]) => s"
    val prog = Parser.parse(src)
    assert(prog.reset.contains("reset"))
  }

  test("AcceleratorAnnotation:Reset:BadValue") {
    val src = "accelerator[reset=175] top = (s: Stm[u8, 10]) => s"
    val ex = intercept[SyntaxError](Parser.parse(src))
    assert(
      ex.msg == "invalid value for annotation 'reset'. Expected an identifier."
    )
    assert(ex.loc.contains(SourcePoint(1, 13)))
  }

  test("AcceleratorAnnotation:Handshake") {
    val src = "accelerator top = (s: Stm[u8, 10]) => s"
    val prog = Parser.parse(src)
    assert(prog.handshake)
  }

  test("AcceleratorAnnotation:NoHandshake") {
    val src = "accelerator[no_handshake] top = (s: Stm[u8, 10]) => s"
    val prog = Parser.parse(src)
    assert(!prog.handshake)
  }

  test("AcceleratorAnnotation:NoHandshake:BadValue") {
    val src = "accelerator[no_handshake=foo] top = (s: Stm[u8, 10]) => s"
    val ex = intercept[SyntaxError](Parser.parse(src))
    assert(ex.msg == "unexpected value for annotation 'no_handshake'")
    assert(ex.loc.contains(SourcePoint(1, 13)))
  }

  test("TestSuite:OK1") {
    val src =
      """const N: u32 = 10
        |
        |accelerator top = (s: Stm[u8, N]) => s.StmMap((x) => x + 5)
        |
        |const M: u32 = 5
        |const V: Vec[u8, M] = vbuild(M) { (i: u8) => i }
        |const Z: u8 = V.VecReduce( (x) => x.0 + x.1 )[0]
        |assert { s = StmRange(N, Z, 1:u8) } yields StmRange(N, Z + 5, 1:u8)
        |
        |const Z2: u8 = 9
        |const DELTA2: u8 = 2
        |assert {
        |  s = StmRange(N, Z2, DELTA2)
        |}
        |yields StmRange(N, Z2 + 5, DELTA2)
        |ignoring StmConcat([ones:[u8]()]s, StmCst(9, zeros:[u8]()))
        |""".stripMargin
    val actual = Parser.parse(src)
    val expected = {
      val n = Param("N", -1)(U32)
      val s = Param("s", -1)(TyStm(U8, n))
      val m = Param("M", -1)(U32)
      val v = Param("V", -1)(TyVec(U8, m))
      val z = Param("Z", -1)(U8)
      val z2 = Param("Z2", -1)(U8)
      val delta2 = Param("DELTA2", -1)(U8)
      Program(
        Seq(ConstDecl(n, ReshapeData(C(10)(), U32)())),
        AccelDecl(
          "top",
          Function(s, StmMap(s, Missing ::+ (x => SmartSum(x, C(5)())()))())(),
          Map()
        ),
        Seq(
          ConstDecl(m, ReshapeData(C(5)(), U32)()),
          ConstDecl(
            v,
            ReshapeData(VecBuild(m, U8 ::+ (i => i))(), TyVec(U8, m))()
          ),
          ConstDecl(
            z,
            ReshapeData(
              VecAccess(
                VecReduceComb(v, Missing ::+ (x => SmartSum(x.__0, x.__1)()))(),
                0
              )(),
              U8
            )()
          ),
          Assertion(
            Map(s -> StmRange(n, z, C(1)(U8))()),
            StmRange(n, SmartSum(z, C(5)())(), C(1)(U8))(),
            None
          ),
          ConstDecl(z2, ReshapeData(C(9)(), U8)()),
          ConstDecl(delta2, ReshapeData(C(2)(), U8)()),
          Assertion(
            Map(s -> StmRange(n, z2, delta2)()),
            StmRange(n, SmartSum(z2, C(5)())(), delta2)(),
            Some(
              StmConcat(
                StmLiteral(AllOne(U8))(),
                StmCst(9, AllZero(U8))()
              )()
            )
          )
        )
      )
    }
    assert(actual == expected)
  }

  test("TestSuite:OK2") {
    val src =
      """accelerator top = (a: Stm[u8, 4]) => (b: Stm[bool, 4]) =>
        |  StmZip(a, b)
        |
        |assert {
        |  a = [0:u8, 1:u8, 2:u8, 3:u8]s,
        |  b = [false, true, true, false]s
        |}
        |yields [(0:u8, false), (1:u8, true), (2:u8, true), (3:u8, false)]s
        |""".stripMargin
    val actual = Parser.parse(src)
    val expected = {
      val a = Param("a", -1)(TyStm(U8, 4))
      val b = Param("b", -1)(TyStm(TyBool, 4))
      Program(
        Seq(),
        AccelDecl("top", Function(a, Function(b, StmZip(a, b)())())(), Map()),
        Seq(
          Assertion(
            Map(
              a -> StmLiteral(C(0)(), C(1)(), C(2)(), C(3)())(),
              b -> StmLiteral(False, True, True, False)()
            ),
            StmLiteral(
              Tuple(C(0)(), False)(),
              Tuple(C(1)(), True)(),
              Tuple(C(2)(), True)(),
              Tuple(C(3)(), False)()
            )(),
            None
          )
        )
      )
    }
    assert(actual == expected)
  }

  test("TestSuite:OK3") {
    val src =
      """accelerator top = StmRange(5, -2:i16, 1:i16)
        |
        |assert {} yields [-2:i16, -1:i16, 0:i16, 1:i16, 2:i16]s
        |""".stripMargin
    val actual = Parser.parse(src)
    val expected = Program(
      Seq(),
      AccelDecl("top", StmRange(5, C(-2)(I16), C(1)(I16))(), Map()),
      Seq(Assertion(Map(), StmLiteral((-2 to 2).map(C(_)(I16)): _*)(), None))
    )
    assert(actual == expected)
  }

  test("TestSuite:OK4") {
    val src =
      """accelerator top = StmRange(5, -2:i16, 1:i16)
        |
        |assert yields [-2:i16, -1:i16, 0:i16, 1:i16, 2:i16]s
        |""".stripMargin
    val actual = Parser.parse(src)
    val expected = Program(
      Seq(),
      AccelDecl("top", StmRange(5, C(-2)(I16), C(1)(I16))(), Map()),
      Seq(Assertion(Map(), StmLiteral((-2 to 2).map(C(_)(I16)): _*)(), None))
    )
    assert(actual == expected)
  }

  test("NestedMultiLineComment") {
    val src =
      """x + y / /* z1 +
        |z2 + z2 /* nested */ +
        |z3 + z4 */
        |w
        |""".stripMargin
    val expected = SmartSum(x, SmartDiv(y, Param("w", -1)(Missing))())()
    val actual = Parser.parse(src).body
    assert(actual == expected)
  }

  test("UnclosedMultiLineComment") {
    val src =
      """x + y / /* z1 +
        |z2 + z3
        |""".stripMargin
    val ex = intercept[SyntaxError](Parser.parse(src))
    assert(ex.getMessage.contains("unclosed multiline comment"))
    assert(ex.loc.contains(SourcePoint(3, 1)))
  }

  test("SingleLineComment") {
    val src =
      """x + y / // z1 +
        |w
        |""".stripMargin
    val expected = SmartSum(x, SmartDiv(y, Param("w", -1)(Missing))())()
    val actual = Parser.parse(src).body
    assert(actual == expected)
  }

  // TODO: Forbid leading zeros in int literals?
}
