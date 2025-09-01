package mhir.ir

import mhir.ir.Lowering.ExprLowering
import mhir.ir.typecheck.TypeCheck
import mhir.sugar.{StmCount, StmMap, StmReduce, StmZip}
import org.scalatest.funsuite.AnyFunSuite

class LetStmMoverTests extends AnyFunSuite {
  test("MoveUp:InsideStmBuild") {
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

  test(
    "MoveUp:let s = (let s = input in let s = s in StmZip(s, s)) in StmZip(s, s)"
  ) {
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

  test("MoveUp:VariableCapture:LetStm") {
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

  test("MoveUp:VariableCapture:StmBuild") {
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

  test("MoveDown:TwoMaps") {
    val n = 15
    val count = StmCount(C(n)(U16))().tchk().lower()
    val zipSelf = (input: Expr) => {
      val plusFive = {
        val s = Param("s")(TyStm(U16, n))
        StmBuild(
          n,
          StmData(s)() + 5,
          True,
          Map[Param, (Expr, Expr)](
            s -> (input, True)
          )
        )().tchk()
      }
      val zip = {
        val s0 = Param("s0")(TyStm(U16, n))
        val s1 = Param("s1")(TyStm(U16, n))
        StmBuild(
          n,
          Tuple(StmData(s0)(), StmData(s1)())(),
          True,
          Map[Param, (Expr, Expr)](
            s0 -> (input, True),
            s1 -> (plusFive, True)
          )
        )().tchk()
      }
      zip
    }
    val dot = (input: Expr) => {
      val mapMul = {
        val s = Param("s")(TyStm((U16, U16), n))
        StmBuild(
          n,
          StmData(s)().__0 * StmData(s)().__1,
          True,
          Map[Param, (Expr, Expr)](
            s -> (input, True)
          )
        )().tchk()
      }
      val reduce = {
        val s = Param("s")(TyStm(U16, n))
        val t = Param("t")(U16)
        val acc = Param("acc")(U16)
        StmBuild(
          1,
          acc + StmData(s)(),
          t === C(n - 1)(U16),
          Map[Param, (Expr, Expr)](
            s -> (mapMul, True),
            t -> (C(0)(U16), C(1)(U16) + t),
            acc -> (C(0)(U16), acc + StmData(s)())
          )
        )().tchk()
      }
      reduce
    }
    val original = {
      val s = Param("s")(TyStm(U16, n))
      LetStm(s, count, dot(zipSelf(s)))().tchk().lower()
    }
    val actual = LetStmMover.moveDown(original)

    // Correctness
    val actualVal = mhir.ir.eval(actual)
    val expectedVal = mhir.ir.eval(original)
    assert(actualVal == expectedVal)

    // Expected value
    val expected = {
      val s = Param("s")(TyStm(U16, n))
      dot(LetStm(s, count, zipSelf(s))()).tchk().lower()
    }
    assert(actual == expected)
  }
}
