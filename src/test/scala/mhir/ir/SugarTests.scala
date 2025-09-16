package mhir.ir

import mhir.ir.Lowering.ExprLowering
import mhir.ir.typecheck.{TypeCheck, TypeError}
import org.scalatest.funsuite.AnyFunSuite

class SugarTests extends AnyFunSuite {
  test("Substitute:[y / x](let x = x * y in x + y)") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val e = Let(x, x * y, x + y)()
    val expected = Let(x, y * y, x + y)()
    assert(e.subPreserveType(x -> y) == expected)
    assert(e.subAndEraseType(x -> y) == expected)
  }

  test("Substitute:[y / x](let y = x * y in x + y)") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val e = Let(y, x * y, x + y)()
    val expected = {
      val y2 = Param("y2")(U8)
      Let(y2, y * y, y + y2)()
    }
    assert(e.subPreserveType(x -> y) == expected)
    assert(e.subAndEraseType(x -> y) == expected)
  }

  test("Let:Equals") {
    val e1 = {
      val x = Param("x")(U8)
      Let(x, 42, (x + 1) * (x + 2))()
    }
    val e2 = {
      val y = Param("y")()
      Let(y, 42, (y + 1) * (y + 2))()
    }
    assert(e1 == e2)
    assert(e2 == e1)
    assert(e1.hashCode() == e2.hashCode())
  }

  test("Let:NotEquals:DifferentBody") {
    val x = Param("x")(U8)
    val e1 = Let(x, 42, (x + 1) * (x + 2))()
    val y = Param("y")(U8)
    val e2 = Let(y, 42, (y + 1) * (x + 2))()
    assert(e1 != e2)
    assert(e2 != e1)
  }

  test("Let:NotEquals:DifferentArg") {
    val x = Param("x")(U8)
    val e1 = Let(x, 42, (x + 1) * (x + 2))()
    val e2 = Let(x, 43, (x + 1) * (x + 2))()
    assert(e1 != e2)
    assert(e2 != e1)
  }

  test("Let:Display") {
    val x = Param("x", -1)(U8)
    val y = Param("y", -1)(Missing)
    val e = Let(x, C(42)(U8), Let(y, C(-1)(I9), Sum(ToSigned(x)(), y)())())()

    val expectedOneLine = "let x: u8 = 42:u8 in let y = -1:i9 in sgn(x) + y"
    val actualOneLine = ExprPrinter.displayOneLine(e)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      s"""let x: u8 = 42:u8 in
         |let y = -1:i9 in
         |sgn(x) + y
         |""".stripMargin.stripTrailing
    val actualMultiLine = ExprPrinter.display(e, maxWidth = 30)
    assert(actualMultiLine == expectedMultiLine)
  }

  test("Let+1:Display") {
    val x = Param("x", -1)(Missing)
    val e = Sum(C(1)(U8), Let(x, C(42)(U8), x)())()
    assert(ExprPrinter.displayOneLine(e) == "1:u8 + (let x = 42:u8 in x)")
    assert(ExprPrinter.display(e) == "1:u8 + (let x = 42:u8 in x)")
  }

  test("Lets") {
    val x = Param("x")()
    val y = Param("y")()
    val actual = Lets(x -> C(5)(U8), y -> C(-21)(I32))(x + y)
    val expected = Let(x, C(5)(U8), Let(y, C(-21)(I32), x + y)())()
    assert(actual == expected)
  }

  test("LetStm") {
    val n = 10
    val s = Param("s")(TyStm(U8, n))
    val x = Param("x")()
    val zipped = {
      val s1 = Param("s1")(TyStm(U8, -1))
      val s2 = Param("s2")(TyStm(U8, -1))
      StmBuild(
        n,
        Tuple(StmData(s1)(), StmData(s2)())(),
        True,
        Map[Param, (Expr, Expr)](
          s1 -> (x, True),
          s2 -> (x, True)
        )
      )()
    }
    val let = Let(x, s, zipped)()
    val actual = let.tchk().lower()
    val expected = LetStm(1, x, s, zipped)()
    assert(actual == expected)
  }

  test("Default[Bool]:Display") {
    val e = Default(TyBool)
    assert(ExprPrinter.displayOneLine(e) == "default[bool]")
    assert(ExprPrinter.display(e) == "default[bool]")
  }

  test("Default[I16]:Display") {
    val e = Default(I16)
    assert(ExprPrinter.displayOneLine(e) == "default[i16]")
    assert(ExprPrinter.display(e) == "default[i16]")
  }

  test("ReshapeData:Valid") {
    val x = Param("x")()
    val e =
      (t1: Type, t2: Type) => ReshapeData(x.rebuild(t1), t2)().tchk().lower()

    assert(e(U8, U16) == PadTo(x, 16)())
    assert(e(I8, I32) == PadTo(x, 32)())
    assert(e(U8, I16) == PadTo(ToSigned(x)(), 16)())
    assert(
      e(TyTuple(U8, U16), TyTuple(U16, U32))
        == Tuple(PadTo(x.__0, 16)(), PadTo(x.__1, 32)())()
    )
    assert(
      e(TyVec(I8, 5), TyVec(I16, 5))
        == VecBuild(5, U32 ::+ (i => PadTo(VecAccess(x, i)(), 16)()))()
    )
  }

  test("ReshapeData:Invalid") {
    val e = (t1: Type, t2: Type) => ReshapeData(Param("x")(t1), t2)()

    assertThrows[TypeError](e(U16, U8).tchk())
    assertThrows[TypeError](e(U16, TyUInt(15)).tchk())
    assertThrows[TypeError](e(U16, I16).tchk())
    assertThrows[TypeError](e(I8, U32).tchk())
    assertThrows[TypeError](e(TyBool, U32).tchk())
  }

  test("SmartEqual:Bool") {
    assert(mhir.ir.eval(False === False) == True)
    assert(mhir.ir.eval(False === True) == False)
    assert(mhir.ir.eval(True === False) == False)
    assert(mhir.ir.eval(True === True) == True)
  }

  test("SmartEqual:Int") {
    for (t1 <- COMMON_INT_TYPES) {
      for (t2 <- COMMON_INT_TYPES) {
        assert(mhir.ir.eval(IntCst(0)(t1) === IntCst(0)(t2)) == True)
        assert(mhir.ir.eval(IntCst(63)(t1) === IntCst(63)(t2)) == True)
        assert(mhir.ir.eval(IntCst(42)(t1) === IntCst(43)(t2)) == False)
      }
    }
  }

  test("SmartEqual:Vec[(Int, Bool), n]") {
    val v1 = VecBuild(5, U8 ::+ (i => Tuple(i, i === 3)()))()
    val v2 = VecBuild(5, U8 ::+ (i => Tuple(2 * i - i, i + 1 === 4)()))()
    val v3 = VecBuild(5, U8 ::+ (i => Tuple(i + 1, i === 3)()))()
    val v4 = VecBuild(5, U8 ::+ (i => Tuple(i, True)()))()

    assert(mhir.ir.eval(v1 === v2) == True)
    assert(mhir.ir.eval(v2 === v1) == True)
    assert(mhir.ir.eval(v1 === v3) == False)
    assert(mhir.ir.eval(v1 !== v3) == True)
    assert(mhir.ir.eval(v1 === v4) == False)
    assert(mhir.ir.eval(v1 !== v4) == True)
  }

  test("SmartEqual:IncompatibleTypes") {
    val types = Seq(
      U8,
      TyBool,
      TyTuple(U8, TyBool),
      TyTuple(TyBool, U8),
      TyVec(U8, IntCst(5)(U8)),
      TyVec(U8, IntCst(6)(U8)),
      TyVec(TyBool, IntCst(5)(U8))
    )
    for (i <- types.indices) {
      for (j <- types.indices) {
        if (i != j) {
          val x = Param("x")(types(i))
          val y = Param("y")(types(j))
          assertThrows[TypeError](SmartEqual(x, y)().tchk())
        }
      }
    }
  }

  test("SmartLessThan:Valid") {
    for (t1 <- COMMON_INT_TYPES) {
      for (t2 <- COMMON_INT_TYPES) {
        assert(mhir.ir.eval(IntCst(0)(t1) < IntCst(0)(t2)) == False)
        assert(mhir.ir.eval(IntCst(0)(t1) < IntCst(1)(t2)) == True)
        assert(mhir.ir.eval(IntCst(42)(t1) < IntCst(41)(t2)) == False)
        assert(mhir.ir.eval(IntCst(42)(t1) < IntCst(42)(t2)) == False)
        assert(mhir.ir.eval(IntCst(42)(t1) < IntCst(43)(t2)) == True)
      }
    }
  }

  test("SmartLessThan:Invalid") {
    assertThrows[TypeError](SmartLessThan(True, False)().tchk())
  }

  test("SmartSum") {
    assert(
      mhir.ir.eval(
        SmartSum(IntCst(-1)(I8), IntCst(5)(TyUInt(7)), IntCst(-300)(I16))()
      )
        == IntCst(-296)()
    )
    assert(
      mhir.ir.eval(
        SmartSum(IntCst(3)(U8), IntCst(2)(U8), IntCst(1)(TyUInt(1)))()
      )
        == IntCst(3 + 2 + 1)()
    )
    assert(
      mhir.ir.eval(
        SmartSum(
          SmartSum(
            SmartSum(IntCst(1)(U8), IntCst(2)(U8))(),
            IntCst(3)(U8)
          )(),
          IntCst(4)(U8)
        )()
      )
        == IntCst(1 + 2 + 3 + 4)()
    )
    assert(mhir.ir.eval(SmartSum(C(0)(U0), C(0)(U0))()) == IntCst(0)())
    assert(mhir.ir.eval(SmartSum()()) == IntCst(0)())
  }

  test("SmartProd") {
    assert(mhir.ir.eval(IntCst(7)(U8) * IntCst(-6)(I16)) == IntCst(-42)())
    assert(mhir.ir.eval(SmartProd()()) == IntCst(1)())
    assert(mhir.ir.eval(SmartProd(0, 42)()) == C(0)())
  }

  test("SafeProd") {
    val u2 = TyUInt(2)
    val u3 = TyUInt(3)
    val u5 = TyUInt(5)
    val e = SafeProd(IntCst(3)(u2), IntCst(4)(u3))().tchk()

    assert(e.typ == u5)
    assert(mhir.ir.eval(e) == IntCst(12)())

    assert(mhir.ir.eval(SafeProd()()) == C(1)())

    assert(mhir.ir.eval(SafeProd(42, 0)()) == C(0)())
  }

  test("SmartDiv") {
    assert(
      mhir.ir.eval(SmartDiv(IntCst(42)(U16), IntCst(2)(U8))()) == IntCst(21)()
    )
    assert(
      mhir.ir.eval(SmartDiv(IntCst(42)(U8), IntCst(-3)(I8))()) == IntCst(-14)()
    )
  }

  test("SmartMod") {
    assert(
      mhir.ir.eval(SmartMod(IntCst(44)(U8), IntCst(3)(TyUInt(2)))()) == IntCst(
        2
      )()
    )
  }
}
