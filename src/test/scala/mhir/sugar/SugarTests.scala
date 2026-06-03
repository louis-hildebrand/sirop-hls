package mhir.sugar

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._
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

  test("Substitute:[4/n](default[Vec[u8,n]])") {
    val n = Param("n")(U8)
    val e = Default(TyVec(U8, n))
    val actual = e.subPreserveType(n -> C(4)(U8))
    assert(actual == Default(TyVec(U8, 4)))
    assert(actual.typ == TyVec(U8, 4))
  }

  test("PatternFunction:FreeVars") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val z = Param("z")(U8)

    val f = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      Sum(x, y, z)()
    )()
    assert(f.freeVars == Set(z))

    val e = f(Tuple(x, x)())
    assert(e.freeVars == Set(x, z))
  }

  test("PatternFunction:Equals1") {
    val f = {
      val x = Param("x")(U8)
      val y = Param("y")(U8)
      PatternFunction(
        TuplePattern(ParamPattern(x), ParamPattern(y)),
        (x + 1) * (y + 2)
      )()
    }
    val g = {
      val z = Param("z")(U8)
      val w = Param("w")(U8)
      PatternFunction(
        TuplePattern(ParamPattern(z), ParamPattern(w)),
        (z + 1) * (w + 2)
      )()
    }
    assert(f == g)
    assert(g == f)
    assert(f.hashCode == g.hashCode)
  }

  test("PatternFunction:Equals2") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val f = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      (x + 1) * (y + 2)
    )()
    val g = PatternFunction(
      TuplePattern(ParamPattern(y), ParamPattern(x)),
      (y + 1) * (x + 2)
    )()
    assert(f == g)
    assert(g == f)
    assert(f.hashCode == g.hashCode)
  }

  test("PatternFunction:NotEquals:SwappedNames") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val f = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      (x + 1) * (y + 2)
    )()
    val g = PatternFunction(
      TuplePattern(ParamPattern(y), ParamPattern(x)),
      (x + 1) * (y + 2)
    )()
    assert(f != g)
    assert(g != f)
  }

  test("PatternFunction:NotEquals:DifferentShape1") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val f = PatternFunction(
      TuplePattern(ParamPattern(x), TuplePattern(ParamPattern(y))),
      Tuple(x, y)()
    )()
    val g = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      Tuple(x, y)()
    )()
    assert(f != g)
    assert(g != f)
  }

  test("PatternFunction:NotEquals:DifferentShape2") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val f = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y), TuplePattern()),
      Tuple(x, y)()
    )()
    val g = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      Tuple(x, y)()
    )()
    assert(f != g)
    assert(g != f)
  }

  test("PatternFunction:Substitute") {
    val x = Param("x")(U8)
    val x2 = Param("x2")(U8)
    val y = Param("y")(TyStm(U8, 5))
    val z = Param("z")(U8)
    val original = Tuple(
      StmData(y)(),
      PatternFunction(
        TuplePattern(ParamPattern(x)),
        Tuple(
          x,
          StmData(y)() + 2,
          PatternFunction(
            TuplePattern(ParamPattern(z), ParamPattern(y)),
            StmData(y)() * z
          )()
        )()
      )()
    )().tchk()
    val subs = Map[Expr, Expr](StmData(y)() -> (x % 2).tchk())
    val expected = Tuple(
      x % 2,
      // (1) Need to rename the variable in the outer function to avoid
      //     variable capture.
      // (2) Must NOT replace the StmData(y) in the innermost function
      //     because that occurrence of y is referring to the function
      //     parameter, not y in the global scope.
      PatternFunction(
        TuplePattern(ParamPattern(x2)),
        Tuple(
          x2,
          x % 2 + 2,
          PatternFunction(
            TuplePattern(ParamPattern(z), ParamPattern(y)),
            StmData(y)() * z
          )()
        )()
      )()
    )()

    val actual1 = original.subAndEraseType(subs)
    assert(actual1 == expected)

    val actual2 = original.subPreserveType(subs)
    assert(actual2 == expected)
    assert(actual2.typ != Missing)
  }

  test("PatternFunction:Display:(x: u16, y: u16)") {
    val x = Param("x", -1)(U16)
    val y = Param("y", -1)(U16)
    val f = PatternFunction(
      TuplePattern(ParamPattern(x), ParamPattern(y)),
      Sum(x, y)()
    )()

    val expectedOneLine = "@(x: u16, y: u16) => x + y"
    val actualOneLine = ExprPrinter.displayOneLine(f)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiline =
      """@(x: u16, y: u16) =>
        |  x + y
        |""".stripMargin.stripTrailing
    val actualMultiline = ExprPrinter.displayMultiLine(f)
    assert(actualMultiline == expectedMultiline)
  }

  test("PatternFunction:Display:(x,)") {
    val x = Param("x", -1)(Missing)
    val f = PatternFunction(TuplePattern(ParamPattern(x)), x)()

    val expectedOneLine = "@(x,) => x"
    val actualOneLine = ExprPrinter.displayOneLine(f)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiline =
      """@(x,) =>
        |  x
        |""".stripMargin.stripTrailing
    val actualMultiline = ExprPrinter.displayMultiLine(f)
    assert(actualMultiline == expectedMultiline)
  }

  test("PatternFunction:Display:(x: u8,)") {
    val x = Param("x", -1)(U8)
    val f = PatternFunction(TuplePattern(ParamPattern(x)), x)()

    val expectedOneLine = "@(x: u8,) => x"
    val actualOneLine = ExprPrinter.displayOneLine(f)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiline =
      """@(x: u8,) =>
        |  x
        |""".stripMargin.stripTrailing
    val actualMultiline = ExprPrinter.displayMultiLine(f)
    assert(actualMultiline == expectedMultiline)
  }

  test("PatternFunction:Eval:EmptyTuple") {
    val f = PatternFunction(TuplePattern(), C(42)(U8))()
    val e = FunCall(f, Tuple()())().tchk().lower
    assert(mhir.eval.eval(e) == C(42)(U8))
  }

  test("PatternFunction:Eval:NestedTuple") {
    val x = Param("x")((U8, U8))
    val y = Param("y")(U8)
    val z = Param("z")(U8)
    val w = Param("w")(U8)
    val p = TuplePattern(
      TuplePattern(
        TuplePattern(ParamPattern(x), TuplePattern(ParamPattern(y))),
        ParamPattern(z)
      ),
      TuplePattern(),
      ParamPattern(w)
    )
    val f = PatternFunction(p, Tuple(x.__1, x.__0, y, z, w)())()
    val arg = Tuple(
      Tuple(
        Tuple(Tuple(C(0)(U8), C(1)(U8))(), Tuple(C(2)(U8))())(),
        C(3)(U8)
      )(),
      Tuple()(),
      C(4)(U8)
    )()
    val e = f(arg).tchk().lower
    val expected = Tuple(C(1)(U8), C(0)(U8), C(2)(U8), C(3)(U8), C(4)(U8))()
    assert(mhir.eval.eval(e) == expected)
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

    val expectedOneLine = "let x: u8 = 42:u8 in let y = -1:i9 in sign(x) + y"
    val actualOneLine = ExprPrinter.displayOneLine(e)
    assert(actualOneLine == expectedOneLine)

    val expectedMultiLine =
      s"""let x: u8 = 42:u8 in
         |let y = -1:i9 in
         |sign(x) + y
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

  test("Let:Vec[bool, n] <- Vec[bool, 3]") {
    val n = Param("n")(U8)
    val nVal = C(3)(U8)
    val constValues = Map(n -> nVal)
    val x = Param("x")(TyVec(TyBool, n))
    val rhs = VecLiteral(True, False, True)()
    val let = Let(x, rhs, VecBuild(n, U8 ::+ (i => !VecAccess(x, i)()))())()
    val actual = mhir.eval.eval(
      let
        .tchk(Map(), constValues)
        .subPreserveType(constValues.toMap[Expr, Expr])
        .lower
    )
    val expected = VecLiteral(False, True, False)()
    assert(actual == expected)
  }

  test("Let:Vec[bool, 4] <- Vec[bool, n]") {
    val n = Param("n")(U8)
    val nVal = C(4)(U8)
    val constValues = Map(n -> nVal)
    val x = Param("x")(TyVec(TyBool, 4))
    val rhs = VecBuild(n, U8 ::+ (i => i % C(2)(U8) equ C(0)(U8)))()
    val let = Let(x, rhs, VecBuild(n, U8 ::+ (i => !VecAccess(x, i)()))())()
    val actual = mhir.eval.eval(
      let
        .tchk(Map(), constValues)
        .subPreserveType(constValues.toMap[Expr, Expr])
        .lower
    )
    val expected = VecLiteral(False, True, False, True)()
    assert(actual == expected)
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
    val actual = let.tchk().lower
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
      (t1: Type, t2: Type) => ReshapeData(x.rebuild(t1), t2)().tchk().lower

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

  test("ReshapeData:Valid:ConstInType1") {
    val n = Param("n")(U8)
    val nVal = 8
    val constValues = Map(n -> C(nVal)(U8))
    val t2 = TyVec(U16, nVal)
    val v = VecBuild(n, U8 ::+ (i => i))().tchk().lower
    val e = ReshapeData(v, t2)().tchk(Map(), constValues).lower
    val actual =
      mhir.eval.eval(e.subPreserveType(constValues.toMap[Expr, Expr]))
    val expected = VecLiteral((0 until nVal).map(C(_)(U16)): _*)().tchk()
    assert(actual == expected)
  }

  test("ReshapeData:Valid:ConstInType2") {
    val n = Param("n")(U8)
    val nVal = 9
    val constValues = Map(n -> C(nVal)(U8))
    val t2 = TyVec(U16, n)
    val v = VecBuild(nVal, U8 ::+ (i => i))().tchk().lower
    val e = ReshapeData(v, t2)().tchk(Map(), constValues).lower
    val actual =
      mhir.eval.eval(e.subPreserveType(constValues.toMap[Expr, Expr]))
    val expected = VecLiteral((0 until nVal).map(C(_)(U16)): _*)().tchk()
    assert(actual == expected)
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
    assert(mhir.eval.eval(False === False) == True)
    assert(mhir.eval.eval(False === True) == False)
    assert(mhir.eval.eval(True === False) == False)
    assert(mhir.eval.eval(True === True) == True)
  }

  test("SmartEqual:Int") {
    for (t1 <- COMMON_INT_TYPES) {
      for (t2 <- COMMON_INT_TYPES) {
        assert(mhir.eval.eval(IntCst(0)(t1) === IntCst(0)(t2)) == True)
        assert(mhir.eval.eval(IntCst(63)(t1) === IntCst(63)(t2)) == True)
        assert(mhir.eval.eval(IntCst(42)(t1) === IntCst(43)(t2)) == False)
      }
    }
  }

  test("SmartEqual:Vec[(Int, Bool), n]") {
    val v1 = VecBuild(5, U8 ::+ (i => Tuple(i, i === 3)()))()
    val v2 = VecBuild(5, U8 ::+ (i => Tuple(2 * i - i, i + 1 === 4)()))()
    val v3 = VecBuild(5, U8 ::+ (i => Tuple(i + 1, i === 3)()))()
    val v4 = VecBuild(5, U8 ::+ (i => Tuple(i, True)()))()

    assert(mhir.eval.eval(v1 === v2) == True)
    assert(mhir.eval.eval(v2 === v1) == True)
    assert(mhir.eval.eval(v1 === v3) == False)
    assert(mhir.eval.eval(v1 !== v3) == True)
    assert(mhir.eval.eval(v1 === v4) == False)
    assert(mhir.eval.eval(v1 !== v4) == True)
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
        assert(mhir.eval.eval(IntCst(0)(t1) < IntCst(0)(t2)) == False)
        assert(mhir.eval.eval(IntCst(0)(t1) < IntCst(1)(t2)) == True)
        assert(mhir.eval.eval(IntCst(42)(t1) < IntCst(41)(t2)) == False)
        assert(mhir.eval.eval(IntCst(42)(t1) < IntCst(42)(t2)) == False)
        assert(mhir.eval.eval(IntCst(42)(t1) < IntCst(43)(t2)) == True)
      }
    }
  }

  test("SmartLessThan:Invalid") {
    assertThrows[TypeError](SmartLessThan(True, False)().tchk())
  }

  test("SmartSum") {
    assert(
      mhir.eval.eval(
        SmartSum(IntCst(-1)(I8), IntCst(5)(TyUInt(7)), IntCst(-300)(I16))()
      )
        == IntCst(-296)()
    )
    assert(
      mhir.eval.eval(
        SmartSum(IntCst(3)(U8), IntCst(2)(U8), IntCst(1)(TyUInt(1)))()
      )
        == IntCst(3 + 2 + 1)()
    )
    assert(
      mhir.eval.eval(
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
    assert(mhir.eval.eval(SmartSum(C(0)(U0), C(0)(U0))()) == IntCst(0)())
    assert(mhir.eval.eval(SmartSum()()) == IntCst(0)())
  }

  test("SmartProd") {
    assert(mhir.eval.eval(IntCst(7)(U8) * IntCst(-6)(I16)) == IntCst(-42)())
    assert(mhir.eval.eval(SmartProd()()) == IntCst(1)())
    assert(mhir.eval.eval(SmartProd(0, 42)()) == C(0)())
  }

  test("SafeProd") {
    val u2 = TyUInt(2)
    val u3 = TyUInt(3)
    val u5 = TyUInt(5)
    val e = SafeProd(IntCst(3)(u2), IntCst(4)(u3))().tchk()

    assert(e.typ == u5)
    assert(mhir.eval.eval(e) == IntCst(12)())

    assert(mhir.eval.eval(SafeProd()()) == C(1)())

    assert(mhir.eval.eval(SafeProd(42, 0)()) == C(0)())
  }

  test("SmartDiv") {
    assert(
      mhir.eval.eval(SmartDiv(IntCst(42)(U16), IntCst(2)(U8))()) == IntCst(21)()
    )
    assert(
      mhir.eval.eval(SmartDiv(IntCst(42)(U8), IntCst(-3)(I8))()) == IntCst(
        -14
      )()
    )
  }

  test("SmartMod") {
    assert(
      mhir.eval.eval(
        SmartMod(IntCst(44)(U8), IntCst(3)(TyUInt(2)))()
      ) == IntCst(
        2
      )()
    )
  }
}
