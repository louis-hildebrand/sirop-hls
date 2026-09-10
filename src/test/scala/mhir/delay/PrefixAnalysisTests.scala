package mhir.delay

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar.{AllZero, ExprLowering, VecShiftLeft}
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class PrefixAnalysisTests extends AnyFunSuite {

  test("[]s ++ [1, 2, 3, 4, 5]s") {
    val e =
      StmLiteral(C(1)(U8), C(2)(U8), C(3)(U8), C(4)(U8), C(5)(U8))().tchk()
    val actual = PrefixAnalysis.findPrefixPatterns(e)
    val expected = StmLiteralPrefixPattern(None, U8, "[1:u8, 2:u8, 3:u8, ...]s")
    assert(actual == expected)
  }

  test("[false, false, false]s ++ [true, false, true, false, true]s") {
    val e = StmLiteral(
      Seq(False, False, False),
      Seq(True, False, True, False, True)
    )(Missing).tchk()
    val actual = PrefixAnalysis.findPrefixPatterns(e)
    val expected =
      StmLiteralPrefixPattern(Some(False), TyBool, "[true, false, true, ...]s")
    assert(actual == expected)
  }

  test("[true, true, true]s ++ [true, false, true, false, true]s") {
    val e = StmLiteral(
      Seq(True, True, True),
      Seq(True, False, True, False, True)
    )(Missing).tchk()
    val actual = PrefixAnalysis.findPrefixPatterns(e)
    val expected =
      StmLiteralPrefixPattern(Some(True), TyBool, "[true, false, true, ...]s")
    assert(actual == expected)
  }

  test("[false, false, true]s ++ [true, false, true, false, true]s") {
    val e = StmLiteral(
      Seq(False, False, True),
      Seq(True, False, True, False, True)
    )(Missing).tchk()
    val actual = PrefixAnalysis.findPrefixPatterns(e)
    val expected = StmLiteralPrefixPattern(
      Some(Undefined(TyBool)),
      TyBool,
      "[true, false, true, ...]s"
    )
    assert(actual == expected)
  }

  test("[true, false, false]s ++ [true, false, true, false, true]s") {
    val e = StmLiteral(
      Seq(True, False, False),
      Seq(True, False, True, False, True)
    )(Missing).tchk()
    val actual = PrefixAnalysis.findPrefixPatterns(e)
    val expected = StmLiteralPrefixPattern(
      Some(Undefined(TyBool)),
      TyBool,
      "[true, false, true, ...]s"
    )
    assert(actual == expected)
  }

  test("[0:u8, 0:u8, 0:u8, 0:u8]s ++ [41:u8, 42:u8]s") {
    val e = StmLiteral(
      Seq(C(0)(U8), C(0)(U8), C(0)(U8), C(0)(U8)),
      Seq(C(41)(U8), C(42)(U8))
    )(Missing).tchk()
    val actual = PrefixAnalysis.findPrefixPatterns(e)
    val expected = StmLiteralPrefixPattern(
      Some(C(0)(U8)),
      U8,
      "[41:u8, 42:u8]s"
    )
    assert(actual == expected)
  }

  test("[0:u8, 0:u8, 1:u8, 0:u8]s ++ [41:u8, 42:u8]s") {
    val e = StmLiteral(
      Seq(C(0)(U8), C(0)(U8), C(1)(U8), C(0)(U8)),
      Seq(C(41)(U8), C(42)(U8))
    )(Missing).tchk()
    val actual = PrefixAnalysis.findPrefixPatterns(e)
    val expected = StmLiteralPrefixPattern(
      Some(Undefined(U8)),
      U8,
      "[41:u8, 42:u8]s"
    )
    assert(actual == expected)
  }

  test("[(0, false), (1, false)]s ++ [(2, true), (3, true)]s") {
    val e = StmLiteral(
      Seq(Tuple(C(0)(U8), False)(), Tuple(C(1)(U8), False)()),
      Seq(Tuple(C(2)(U8), True)(), Tuple(C(3)(U8), True)())
    )(Missing).tchk()
    val actual = PrefixAnalysis.findPrefixPatterns(e)
    val expected = StmLiteralPrefixPattern(
      Some(Tuple(Undefined(U8), False)()),
      (U8, TyBool),
      "[(2:u8, true), (3:u8, true)]s"
    )
    assert(actual == expected)
  }

  test("StmMap:SameHead") {
    val n = 8
    val input = Param("input")(TyStm(U16, n))
    val p = Param("p")(TyStm(U16, -1))
    val stmMap = StmBuild(
      n,
      C(1)(),
      C(42)(U16),
      Sum(StmData(p)(), C(42)(U16))(),
      True,
      Map(),
      Map(
        p -> (input, True, C(0)())
      )
    )().tchk()
    val inputPattern = ParamPrefixPattern(Some(C(0)(U16)), input)
    val actual = PrefixAnalysis.findPrefixPatterns(
      stmMap,
      patternByParam = Map(input -> inputPattern)
    )
    val expected =
      StmBuildPrefixPattern(Some(C(42)(U16)), U16, Map(p -> inputPattern))
    assert(actual == expected)
  }

  test("StmMap:DifferentHead") {
    val n = 8
    val input = Param("input")(TyStm(U16, n))
    val p = Param("p")(TyStm(U16, -1))
    val stmMap = StmBuild(
      n,
      C(1)(),
      C(42)(U16),
      Sum(StmData(p)(), C(42)(U16))(),
      True,
      Map(),
      Map(
        p -> (input, True, C(0)())
      )
    )().tchk()
    val inputPattern = ParamPrefixPattern(Some(C(1)(U16)), input)
    val actual = PrefixAnalysis.findPrefixPatterns(
      stmMap,
      patternByParam = Map(input -> inputPattern)
    )
    val expected =
      StmBuildPrefixPattern(Some(Undefined(U16)), U16, Map(p -> inputPattern))
    assert(actual == expected)
  }

  test("StmSlideStartingWith") {
    val n = 16
    val w = 3
    val input = Param("input")(TyStm(U16, n))
    val p = Param("p")(TyStm(U16, -1))
    val buf = Param("buf")(TyVec(U16, w))
    val stmSlide = StmBuild(
      n,
      C(1)(),
      VecBuild(w, U8 ::+ (_ => C(0)(U16)))(),
      VecShiftLeft(buf, StmData(p)())(),
      True,
      Map(
        buf -> (
          AllZero(buf.typ),
          VecShiftLeft(buf, StmData(p)())(),
          C(1)()
        )
      ),
      Map(
        p -> (input, True, C(0)())
      )
    )().tchk().lower
    val inputPattern = ParamPrefixPattern(Some(C(0)(U16)), input)
    val actual = PrefixAnalysis.findPrefixPatterns(
      stmSlide,
      patternByParam = Map(input -> inputPattern)
    )
    val expected = StmBuildPrefixPattern(
      Some(VecLiteral((0 until w).map(_ => C(0)(U16)): _*)()),
      TyVec(U16, w),
      Map(p -> inputPattern)
    )
    assert(actual == expected)
  }

  test("StmDrop") {
    val n = 8
    val input = Param("input")(TyStm(U16, n))
    val p = Param("p")(TyStm(U16, -1))
    val stmDrop = StmBuild(
      n,
      C(2)(),
      C(0)(U16),
      StmData(p)(),
      True,
      Map(),
      Map(
        p -> (input, True, C(0)())
      )
    )().tchk()
    val inputPattern = ParamPrefixPattern(Some(C(0)(U16)), input)
    val actual = PrefixAnalysis.findPrefixPatterns(
      stmDrop,
      patternByParam = Map(input -> inputPattern)
    )
    val expected =
      StmBuildPrefixPattern(Some(Undefined(U16)), U16, Map(p -> inputPattern))
    assert(actual == expected)
  }

  test("StmZipWithIndex:OK") {
    val n = 6
    val input = Param("input")(TyStm(U16, n))
    val p = Param("p")(TyStm(U16, -1))
    val i = Param("i")(U8)
    val stmMap = StmBuild(
      n,
      C(1)(),
      Tuple(C(0)(U8), Undefined(U16))(),
      Tuple(i, StmData(p)())(),
      True,
      Map(
        i -> (C(0)(U8), Sum(C(1)(U8), i)(), C(1)())
      ),
      Map(
        p -> (input, True, C(0)())
      )
    )().tchk()
    val inputPattern = ParamPrefixPattern(None, input)
    val actual = PrefixAnalysis.findPrefixPatterns(
      stmMap,
      patternByParam = Map(input -> inputPattern)
    )
    val expected = StmBuildPrefixPattern(
      Some(Tuple(C(0)(U8), Undefined(U16))().tchk()),
      TyTuple(U8, U16),
      Map(p -> inputPattern)
    )
    assert(actual == expected)
  }

  test("StmZipWithIndex:EarlyStart") {
    val n = 6
    val input = Param("input")(TyStm(U16, n))
    val p = Param("p")(TyStm(U16, -1))
    val i = Param("i")(U8)
    val stmMap = StmBuild(
      n,
      C(1)(),
      Tuple(C(0)(U8), Undefined(U16))(),
      Tuple(i, StmData(p)())(),
      True,
      Map(
        // NOTICE: delay annotation is 0 rather than 1,
        // so at time 0 we'll have i == 1, not i == 0
        i -> (C(0)(U8), Sum(C(1)(U8), i)(), C(0)())
      ),
      Map(
        p -> (input, True, C(0)())
      )
    )().tchk()
    val inputPattern = ParamPrefixPattern(None, input)
    val actual = PrefixAnalysis.findPrefixPatterns(
      stmMap,
      patternByParam = Map(input -> inputPattern)
    )
    val expected = StmBuildPrefixPattern(
      Some(Undefined(TyTuple(U8, U16))),
      TyTuple(U8, U16),
      Map(p -> inputPattern)
    )
    assert(actual == expected)
  }

  test("LetStm") {
    val n = 8
    val input = Param("input")(TyStm(U16, n))
    val p1 = Param("p1")(TyStm(U16, -1))
    val p2 = Param("p2")(TyStm(U16, -1))
    val x = Param("x")(TyStm(U16, -1))
    val plusFive = StmBuild(
      n,
      C(1)(),
      C(5)(U16),
      Sum(StmData(p1)(), C(5)(U16))(),
      True,
      Map(),
      Map(
        p1 -> (input, True, C(0)())
      )
    )().tchk()
    val timesTwo = StmBuild(
      n,
      C(1)(),
      C(10)(U16),
      Prod(StmData(p2)(), C(2)(U16))(),
      True,
      Map(),
      Map(
        p2 -> (x, True, C(0)())
      )
    )().tchk()
    val original = LetStm(C(0)(), x, plusFive, timesTwo)().tchk()

    val inputPattern = ParamPrefixPattern(Some(C(0)(U16)), input)
    val actual = PrefixAnalysis.findPrefixPatterns(
      original,
      patternByParam = Map(input -> inputPattern)
    )
    val expected = LetStmPrefixPattern(
      StmBuildPrefixPattern(Some(C(5)(U16)), U16, Map(p1 -> inputPattern)),
      StmBuildPrefixPattern(
        Some(C(10)(U16)),
        U16,
        Map(p2 -> ParamPrefixPattern(Some(C(5)(U16)), x))
      )
    )
    assert(actual == expected)
    assert(actual.pattern.contains(C(10)(U16)))
  }
}
