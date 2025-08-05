package mhir.ir

import mhir.ir.Lowering.ExprLowering
import mhir.ir.typecheck.TypeCheck
import org.scalatest.funsuite.AnyFunSuite

class LetStmMoverTests extends AnyFunSuite {
  test("InsideStmBuild") {
    val n = 10
    val input = Param("input")(TyStm(U8, n))
    val s = Param("s")(TyStm(U8, n))
    val lets = LetStm(s, input, LetStm(s, s, s)())()
    val top = StmBuild(
      n,
      C(5)(U8) + StmData(s)(),
      True,
      Map[Param, (Expr, Expr)](
        s -> (lets, True)
      )
    )().tchk().lower()
    val actual = LetStmMover.moveUp(top)
    val expected = LetStm(
      s,
      input,
      LetStm(
        s,
        s,
        StmBuild(
          n,
          Sum(C(5)(U8), StmData(s)())(),
          True,
          Map[Param, (Expr, Expr)](
            s -> (s, True)
          )
        )()
      )()
    )().tchk()
    assert(actual == expected)
  }

  test("let s = (let s = input in let s = s in StmZip(s, s)) in StmZip(s, s)") {
    val n = 7
    val s = Param("s")()
    val input = Param("input")(TyStm(TyBool, n))
    val zipped = (t: Type) => {
      val s0 = Param("s0")(TyStm(t, n))
      val s1 = Param("s1")(TyStm(t, n))
      StmBuild(
        n,
        Tuple(StmData(s0)(), StmData(s1)())(),
        True,
        Map[Param, (Expr, Expr)](
          s0 -> (s, True),
          s1 -> (s, True)
        )
      )()
    }
    val e =
      LetStm(
        s,
        LetStm(s, input, LetStm(s, s, zipped(TyBool))())(),
        zipped((TyBool, TyBool))
      )().tchk().lower()
    val actual = LetStmMover.moveUp(e)
    val expected =
      LetStm(
        s,
        input,
        LetStm(s, s, LetStm(s, zipped(TyBool), zipped((TyBool, TyBool)))())()
      )().tchk()
    assert(actual == expected)
  }

  test("VariableCapture:LetStm") {
    val n = 6
    val a = Param("a")(TyStm(U8, n))
    val b = Param("b")(TyStm(U8, n))
    // let a = (let b = ...) in StmZip(a, b) <-- b is free!
    val count = {
      val i = Param("i")(U8)
      StmBuild(
        n,
        i,
        True,
        Map[Param, (Expr, Expr)](
          i -> (C(42)(U8), Sum(C(1)(U8), i)())
        )
      )()
    }
    val zipped = {
      val s0 = Param("s0")(TyStm(U8, n))
      val s1 = Param("s1")(TyStm(U8, n))
      StmBuild(
        n,
        Tuple(StmData(s0)(), StmData(s1)())(),
        True,
        Map[Param, (Expr, Expr)](
          s0 -> (a, True),
          s1 -> (b, True)
        )
      )()
    }
    val original = LetStm(a, LetStm(b, count, b)(), zipped)().tchk().lower()
    val actual = LetStmMover.moveUp(original)
    val expected = {
      // Rename the bound variable, but not the one inside `zipped`
      val b2 = Param("b")(TyStm(U8, n))
      LetStm(b2, count, LetStm(a, b2, zipped)())().tchk()
    }
    assert(actual == expected)
  }

  test("VariableCapture:StmBuild") {
    val n = 5
    val b = Param("b")()
    val s0 = Param("s0")(TyStm(U8, n))
    val s1 = Param("s1")(TyStm(TyBool, n))
    // StmZip(let b = ..., b) <-- b is free!
    val count = {
      val i = Param("i")(U8)
      StmBuild(
        n,
        i,
        True,
        Map[Param, (Expr, Expr)](
          i -> (C(42)(U8), Sum(C(1)(U8), i)())
        )
      )()
    }
    val original = StmBuild(
      n,
      Tuple(StmData(s0)(), StmData(s1)())(),
      True,
      Map[Param, (Expr, Expr)](
        s0 -> (LetStm(b, count, b)(), True),
        s1 -> (b.rebuild(TyStm(TyBool, n)), True)
      )
    )().tchk().lower()
    val actual = LetStmMover.moveUp(original)
    val expected = {
      val b2 = Param("b")()
      LetStm(
        b2,
        count,
        StmBuild(
          n,
          Tuple(StmData(s0)(), StmData(s1)())(),
          True,
          Map[Param, (Expr, Expr)](
            // Rename this occurrence of `b`
            s0 -> (b2, True),
            // Don't rename this occurrence of `b`
            s1 -> (b.rebuild(TyStm(TyBool, n)), True)
          )
        )()
      )().tchk()
    }
    assert(actual == expected)
  }
}
