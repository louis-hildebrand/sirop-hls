package mhir.ir

import mhir.ir.Lowering.ExprLowering
import mhir.ir.typecheck.TypeCheck
import mhir.sugar.{StmConcat, VecShiftLeft}
import org.scalatest.funsuite.AnyFunSuite

class SubstitutionTests extends AnyFunSuite {
  test("Substitute:StmData") {
    val s = Param("s")()
    val v = Param("v")()

    val original = (5 + StmData(s)()).tchk(Map(s -> TyStm(U16, 2)))
    val subbedSameType =
      original.subPreserveType(StmData(s)() -> v.rebuild(U16))
    assert(subbedSameType == 5 + v)
    assert(subbedSameType.typ == U16)
  }

  test("Substitute:Function") {
    val x = Param("x")(U8)
    val x2 = Param("x2")(U8)
    val y = Param("y")(TyStm(U8, 5))
    val original = Tuple(
      StmData(y)(),
      Function(
        x,
        Tuple(
          StmData(y)() + 2,
          Function(y, StmData(y)() * 3)()
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
      Function(x2, Tuple(x % 2 + 2, Function(y, StmData(y)() * 3)())())()
    )()

    val actual2 = original.subPreserveType(subs)
    assert(actual2 == expected)
    assert(actual2.typ != Missing)
  }

  test("Substitute:StmBuild") {
    val x = Param("x")()
    val x2 = Param("x2")()
    val y = Param("y")(U8)
    val y2 = Param("y2")(U8)
    val z = Param("z")(U8)
    val context = Map(x -> TyTuple(U8, U8), x2 -> TyTuple(U8, U8))
    val stm = StmBuild(
      x.__1 + z + 1,
      Tuple(z, x.__1 && x.__1)(),
      True,
      Map[Param, (Expr, Expr)](
        x.rebuild(TyTuple(TyBool, TyBool)).asInstanceOf[Param] -> (
          Tuple(True, False)(),
          Mux(
            x.__1,
            Tuple(True, False)(),
            Tuple(False, True)()
          )()
        ),
        y -> (x.__1 / 2 + z, y + 2 + z)
      )
    )()
    val original = Tuple(2 * x.__1 * z, stm)().tchk(context)
    val subs = Map[Expr, Expr](x.__1 -> y, z -> IntCst(99)(U8))
    val expected = Tuple(
      2 * y * 99,
      StmBuild(
        y + IntCst(99)() + IntCst(1)(),
        Tuple(99, x2.__1 && x2.__1)(),
        True,
        Map[Param, (Expr, Expr)](
          x2 -> (
            Tuple(True, False)(),
            Mux(
              x2.__1,
              Tuple(True, False)(),
              Tuple(False, True)()
            )()
          ),
          y2 -> (y / 2 + IntCst(99)(), y2 + 2 + IntCst(99)())
        )
      )()
    )()

    val actual2 = original.subPreserveType(subs)
    assert(actual2 == expected)
    assert(actual2.typ != Missing)
  }

  test("Substitute:StmBuildWithShadowing") {
    val n = Param("n")(U8)
    val s = Param("s")(TyStm(U8, n))
    val stm = StmBuild(
      n,
      StmData(s)(),
      True,
      Map[Param, (Expr, Expr)](
        s -> (s, True),
        n -> (C(0)(U8), Sum(C(1)(U8), n)())
      )
    )().tchk()
    val n2 = Param("n2")(U8)
    val s2 = Param("s2")(TyStm(U8, n2))
    val subs = Map[Expr, Expr](n -> n2, s -> s2)
    val expected = {
      val nn = Param("nn")(U8)
      val ss = Param("ss")(TyStm(U8, n))
      StmBuild(
        n2,
        StmData(ss)(),
        True,
        Map[Param, (Expr, Expr)](
          ss -> (s2, True),
          nn -> (C(0)(U8), Sum(C(1)(U8), nn)())
        )
      )()
    }
    val actual1 = stm.subPreserveType(subs)
    assert(actual1 == expected)
    val actual2 = stm.subAndEraseType(subs)
    assert(actual2 == expected)
  }

  test("Substitute:LetStm:VariableCapture") {
    val n = 10
    val x = Param("x")(TyStm(U8, n))
    val y = Param("y")(TyStm(U8, n))
    val e = LetStm(1, x, x, StmConcat(x, y)())()
    val subs = Map[Expr, Expr](y -> x)
    val expected = {
      val z = Param("z")(TyStm(U8, n))
      LetStm(1, z, x, StmConcat(z, x)())()
    }

    val actual0 = e.subPreserveType(subs)
    assert(actual0 == expected)

    val actual1 = e.subAndEraseType(subs)
    assert(actual1 == expected)
  }

  test("Substitute:LetStm:SubBoundVar") {
    val n = 10
    val x = Param("x")(TyStm(U8, n))
    val y = Param("y")(TyStm(U8, n))
    val z = Param("z")(TyStm(U8, n))
    val e = LetStm(1, x, x, StmConcat(x, y)())()
    val subs = Map[Expr, Expr](x -> z)
    val expected = LetStm(1, x, z, StmConcat(x, y)())()

    val actual0 = e.subPreserveType(subs)
    assert(actual0 == expected)

    val actual1 = e.subAndEraseType(subs)
    assert(actual1 == expected)
  }

  test("Substitute:LetStm:Big") {
    val n = 10
    // let x1 = StmMap(x0, +1) in
    // let x2 = StmMap(x1, +1) in
    // ...
    // let x20 = StmMap(x19, +1) in
    // let x21 = StmMap2(x20, a, _ + _) in
    // let x22 = StmMap(x21, +2) in
    // let x23 = StmMap(x22, +2) in
    // ...
    // let x41 = StmMap(x40, +2) in
    // x41
    def before(x: Param, a: Param, numBefore: Int, numAfter: Int): Expr = {
      if (numBefore == 0) {
        val nextX = Param("x")(TyStm(U16, n))
        val s0Var = Param("s0")(TyStm(U16, -1))
        val s1Var = Param("s1")(TyStm(U16, -1))
        val map2 = StmBuild(
          n,
          Sum(StmData(s0Var)(), StmData(s1Var)())(),
          True,
          Map[Param, (Expr, Expr)](
            s0Var -> (x, True),
            s1Var -> (a, True)
          )
        )()
        LetStm(1, nextX, map2, after(nextX, numAfter))()
      } else {
        val nextX = Param("x")(TyStm(U16, n))
        val sVar = Param("s")(TyStm(U16, -1))
        val map = StmBuild(
          n,
          Sum(C(1)(U16), StmData(sVar)())(),
          True,
          Map[Param, (Expr, Expr)](sVar -> (x, True))
        )()
        LetStm(1, nextX, map, before(nextX, a, numBefore - 1, numAfter))()
      }
    }
    def after(x: Param, numAfter: Int): Expr = {
      if (numAfter == 0) {
        x
      } else {
        val nextX = Param("x")(TyStm(U16, n))
        val sVar = Param("s")(TyStm(U16, -1))
        val map = StmBuild(
          n,
          Sum(C(2)(U16), StmData(sVar)())(),
          True,
          Map[Param, (Expr, Expr)](sVar -> (x, True))
        )()
        LetStm(1, nextX, map, after(nextX, numAfter - 1))()
      }
    }
    val a = Param("a")(TyStm(U16, n))
    val b = Param("b")(TyStm(U16, n))
    val x0 = Param("x")(TyStm(U16, n))
    val original = before(x0, a, numBefore = 20, numAfter = 20).tchk().lower()
    // Checking may take a very long time (since StmBuild.equals is slow).
    // The important thing for this test is that substitution should not take a
    // million years.
    val actual0 = original.subPreserveType(a -> b)
    assert(actual0.typ == original.typ)

    original.subAndEraseType(a -> b)
  }

  test("Substitute:LetStm:InBufSize") {
    val n = Param("n")(U8)
    val x = Param("x")(TyStm(U8, n))
    val s0 = Param("s0")(TyStm(U8, n))
    val s1 = Param("s1")(TyStm(U8, n))
    val original = LetStm(n, x, s0, s1)().tchk()
    val expected = LetStm(C(10)(U8), x, s0, s1)().tchk()

    val actual0 = original.subPreserveType(n -> C(10)(U8))
    assert(actual0 == expected)

    val actual1 = original.subAndEraseType(n -> C(10)(U8))
    assert(actual1 == expected)
  }

  test("SubstituteInType0") {
    val n = Param("n")(U8)
    val e = Undefined(TyVec(U8, n))
    val expected = Undefined(TyVec(U8, C(42)(U8)))
    assert(e.subPreserveType(n -> C(42)(U8)) == expected)
    assert(e.subAndEraseType(n -> C(42)(U8)) == expected)
  }

  test("SubstituteInType1") {
    val n = Param("n")(U8)
    val e = Tuple(VecBuild(n * 2, U8 ::+ (i => i))(), n + 1)().tchk()
    val expectedType = TyTuple(TyVec(U8, n * 2), U8)
    assert(e.typ == expectedType)

    val actual = e.subPreserveType(n -> IntCst(42)(U8))

    val expected =
      Tuple(
        VecBuild(IntCst(42)() * IntCst(2)(), U8 ::+ (i => i))(),
        IntCst(42)() + IntCst(1)()
      )()
    assert(actual == expected)
    val expectedTypeAfterSub = TyTuple(TyVec(U8, C(84)()), U8)
    assert(actual.typ == expectedTypeAfterSub)
  }

  test("SubstituteInType2") {
    val n = Param("n")(U8)
    val m = Param("m")(U8)
    val k = Param("k")(U8)
    val v = Param("v")(TyVec(U8, n))
    val e = StmBuild(
      n,
      VecAccess(v, 0)(),
      True,
      Map[Param, (Expr, Expr)](
        v -> (
          VecBuild(n, U8 ::+ (i => i))(),
          VecShiftLeft(v, IntCst(42)(U8))()
        )
      )
    )().tchk()

    val actual = e.subPreserveType(n -> (m + k).tchk())

    val expected = StmBuild(
      m + k,
      VecAccess(v, 0)(),
      True,
      Map[Param, (Expr, Expr)](
        v -> (
          VecBuild(m + k, U8 ::+ (i => i))(),
          VecShiftLeft(v, IntCst(42)(U8))()
        )
      )
    )()
    assert(actual == expected)
    val expectedStmType = TyStm(U8, m + k)
    assert(actual.typ == expectedStmType)
    val expectedVecType = TyVec(U8, m + k)
    val actualVecParam = actual.asInstanceOf[StmBuild].equations.toSeq.head._1
    assert(actualVecParam.typ == expectedVecType)
  }

  test("Substitute:InFunctionTypeAnnotation") {
    val n = Param("n")(U8)
    val v = Param("v")(TyVec(U8, n))
    val f = Function(v, v)().tchk()
    val actual = f.subPreserveType(n -> IntCst(42)(U8)).asInstanceOf[Function]
    val expected = {
      val v = Param("v")(TyVec(U8, 42))
      Function(v, v)()
    }
    assert(actual == expected)
    assert(actual.param.typ == TyVec(U8, 42))
    assert(actual.body.typ == TyVec(U8, 42))
  }

  test("Substitute:ChangeStreamLength") {
    val s1 = Param("s1")(TyStm(U8, 4))
    val s2 = Param("s2")(TyStm(U8, 20))
    val e = {
      val s = Param("s")()
      StmBuild(
        4,
        StmData(s)(),
        True,
        Map[Param, (Expr, Expr)](s -> (s1, True))
      )()
    }
    val actual = e.subPreserveType(s1 -> s2).asInstanceOf[StmBuild]
    val expected = {
      val s = Param("s")()
      StmBuild(
        4,
        StmData(s)(),
        True,
        Map[Param, (Expr, Expr)](s -> (s2, True))
      )()
    }
    assert(actual == expected)
    val actualInputStm = actual.equations.toSeq.head._2._1
    assert(actualInputStm == s2)
    assert(actualInputStm.typ == TyStm(U8, 20))
  }
}
