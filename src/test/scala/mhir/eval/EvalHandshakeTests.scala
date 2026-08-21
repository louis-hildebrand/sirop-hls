package mhir.eval

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class EvalHandshakeTests extends AnyFunSuite {

  test("StmBuild") {
    val i = Param("i")(U16)
    val s =
      StmBuild(
        5,
        Tuple()(),
        Undefined(Missing),
        i + 42,
        True,
        Map[Param, (Expr, Expr, Expr)](
          i -> (IntCst(9)(U16), 2 * i + 1, Tuple()())
        ),
        Map()
      )()
    val expected = StmLiteral(9 + 42, 19 + 42, 39 + 42, 79 + 42, 159 + 42)()
    val actual = mhir.eval.eval(s)
    assert(actual == expected)
  }

  test("ObviousInfiniteLoop") {
    val s = StmBuild(1, Tuple()(), Undefined(Missing), 0, False, Map(), Map())()
    val exc = intercept[DeadlockError](mhir.eval.eval(s))
    assert(exc.reasons == Seq(PipelineFixpoint))
  }

  test("LessObviousInfiniteLoop") {
    val a = Param("a")(U32)
    val s = StmBuild(
      1,
      Tuple()(),
      Undefined(Missing),
      a,
      a % 2 === 1,
      Map[Param, (Expr, Expr, Expr)](
        a -> (ReshapeData(0, U32)(), a + 2, Tuple()())
      ),
      Map()
    )().tchk()
    val exc = intercept[DeadlockError](mhir.eval.eval(s))
    assert(exc.reasons == Seq(TooManySteps))
  }

  test("InfiniteLoopInInputStream") {
    val s = Param("s")(TyStm(TyUInt(0), -1))
    val stm = StmBuild(
      1,
      Tuple()(),
      Undefined(Missing),
      StmData(s)(),
      True,
      Map(),
      Map[Param, (Expr, Expr, Expr)](
        s -> (
          StmBuild(1, Tuple()(), Undefined(Missing), 0, False, Map(), Map())(),
          True,
          Tuple()()
        )
      )
    )().tchk()
    val exc = intercept[DeadlockError](mhir.eval.eval(stm))
    assert(exc.reasons == Seq(PipelineFixpoint))
  }

  test("ReadFromEmptyStream") {
    val s = {
      val s = Param("s")(TyStm(TyUInt(0), 1))
      StmBuild(
        2,
        Tuple()(),
        Undefined(Missing),
        StmData(s)(),
        True,
        Map(),
        Map[Param, (Expr, Expr, Expr)](
          s -> (
            StmBuild(1, Tuple()(), Undefined(Missing), 0, True, Map(), Map())(),
            True,
            Tuple()()
          )
        )
      )()
    }
    val exc = intercept[DeadlockError](mhir.eval.eval(s))
    assert(exc.reasons == Seq(EmptyStreamRead))
  }

  test("LetStm:ZipWithSelf") {
    // StmCount(5)
    val count = {
      val i = Param("i")(U8)
      StmBuild(
        5,
        Tuple()(),
        Undefined(Missing),
        i,
        True,
        Map[Param, (Expr, Expr, Expr)](
          i -> (C(0)(U8), Sum(C(1)(U8), i)(), Tuple()())
        ),
        Map()
      )()
    }
    val s = Param("s")(TyStm(U8, 5))
    // StmZip(s, s)
    val zipped = {
      val s0 = Param("s0")(TyStm(U8, 5))
      val s1 = Param("s1")(TyStm(U8, 5))
      StmBuild(
        5,
        Tuple()(),
        Undefined(Missing),
        Tuple(StmData(s0)(), StmData(s1)())(),
        True,
        Map(),
        Map[Param, (Expr, Expr, Expr)](
          s0 -> (s, True, Tuple()()),
          s1 -> (s, True, Tuple()())
        )
      )()
    }
    val e = LetStm(1, s, count, zipped)().tchk()
    val expected = StmLiteral(
      Tuple(C(0)(U8), C(0)(U8))(),
      Tuple(C(1)(U8), C(1)(U8))(),
      Tuple(C(2)(U8), C(2)(U8))(),
      Tuple(C(3)(U8), C(3)(U8))(),
      Tuple(C(4)(U8), C(4)(U8))()
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("LetStm:ZipWithPlusFive") {
    // StmCount(5)
    val count = {
      val i = Param("i")(U8)
      StmBuild(
        5,
        Tuple()(),
        Undefined(Missing),
        i,
        True,
        Map[Param, (Expr, Expr, Expr)](
          i -> (C(0)(U8), Sum(C(1)(U8), i)(), Tuple()())
        ),
        Map()
      )()
    }
    val s = Param("s")(TyStm(U8, 5))
    // StmMap(s, x => x + 5)
    val plusFive = {
      val a = Param("a")(TyStm(U8, 5))
      StmBuild(
        5,
        Tuple()(),
        Undefined(Missing),
        Sum(C(5)(U8), StmData(a)())(),
        True,
        Map(),
        Map[Param, (Expr, Expr, Expr)](
          a -> (s, True, Tuple()())
        )
      )()
    }
    // StmZip(s, plusFive)
    val zipped = {
      val s0 = Param("s0")(TyStm(U8, 5))
      val s1 = Param("s1")(TyStm(U8, 5))
      StmBuild(
        5,
        Tuple()(),
        Undefined(Missing),
        Tuple(StmData(s0)(), StmData(s1)())(),
        True,
        Map(),
        Map[Param, (Expr, Expr, Expr)](
          s0 -> (s, True, Tuple()()),
          s1 -> (plusFive, True, Tuple()())
        )
      )()
    }
    // StmMap(let stm s = count in zipped, (x, y) => (x, y, 3 * x + y))
    val e = {
      val a = Param("a")(TyStm((U8, U8), 5))
      StmBuild(
        5,
        Tuple()(),
        Undefined(Missing),
        Tuple(
          StmData(a)().__0,
          StmData(a)().__1,
          Sum(Prod(C(3)(U8), StmData(a)().__0)(), StmData(a)().__1)()
        )(),
        True,
        Map(),
        Map[Param, (Expr, Expr, Expr)](
          a -> (LetStm(1, s, count, zipped)(), True, Tuple()())
        )
      )().tchk()
    }
    val expected = StmLiteral(
      Tuple(C(0)(U8), C(5)(U8), C(5)(U8))(),
      Tuple(C(1)(U8), C(6)(U8), C(9)(U8))(),
      Tuple(C(2)(U8), C(7)(U8), C(13)(U8))(),
      Tuple(C(3)(U8), C(8)(U8), C(17)(U8))(),
      Tuple(C(4)(U8), C(9)(U8), C(21)(U8))()
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("LetStm:StmConcat:NotEnoughBuffering") {
    // StmCount(5)
    val count = {
      val i = Param("i")(U8)
      StmBuild(
        5,
        Tuple()(),
        Undefined(Missing),
        i,
        True,
        Map[Param, (Expr, Expr, Expr)](
          i -> (C(0)(U8), Sum(C(1)(U8), i)(), Tuple()())
        ),
        Map()
      )()
    }
    val s = Param("s")(TyStm(U8, 5))
    // StmConcat(s, s)
    val concat = {
      val t = Param("t")(U8)
      val s0 = Param("s0")(TyStm(U8, 5))
      val s1 = Param("s1")(TyStm(U8, 5))
      StmBuild(
        5,
        Tuple()(),
        Undefined(Missing),
        Mux(t lt C(5)(U8), StmData(s0)(), StmData(s1)())(),
        True,
        Map[Param, (Expr, Expr, Expr)](
          t -> (C(0)(U8), Sum(C(1)(U8), t)(), Tuple()())
        ),
        Map[Param, (Expr, Expr, Expr)](
          s0 -> (s, t lt C(5)(U8), Tuple()()),
          s1 -> (s, t geq C(5)(U8), Tuple()())
        )
      )()
    }
    val e = LetStm(1, s, count, concat)().tchk()
    val exc = intercept[DeadlockError](mhir.eval.eval(e))
    assert(exc.reasons == Seq(PipelineFixpoint))
  }

  test("LetStm:StmConcat:Valid") {
    val n = 5
    // StmCount(5)
    val count = {
      val i = Param("i")(U8)
      StmBuild(
        n,
        Tuple()(),
        Undefined(Missing),
        i,
        True,
        Map[Param, (Expr, Expr, Expr)](
          i -> (C(0)(U8), Sum(C(1)(U8), i)(), Tuple()())
        ),
        Map()
      )()
    }
    val s = Param("s")(TyStm(U8, n))
    // StmConcat(s, s)
    val concat = {
      val t = Param("t")(U8)
      val s0 = Param("s0")(TyStm(U8, -1))
      val s1 = Param("s1")(TyStm(U8, -1))
      StmBuild(
        2 * n,
        Tuple()(),
        Undefined(Missing),
        Mux(t lt C(n)(U8), StmData(s0)(), StmData(s1)())(),
        True,
        Map[Param, (Expr, Expr, Expr)](
          t -> (C(0)(U8), Sum(C(1)(U8), t)(), Tuple()())
        ),
        Map[Param, (Expr, Expr, Expr)](
          s0 -> (s, t lt C(n)(U8), Tuple()()),
          s1 -> (s, t geq C(n)(U8), Tuple()())
        )
      )()
    }
    val e = LetStm(n, s, count, concat)().tchk()
    val expected = StmLiteral(
      (0 until n).map(C(_)(U8))
        ++ (0 until n).map(C(_)(U8)): _*
    )()
    val actual = mhir.eval.eval(e)
    assert(actual == expected)
  }

  test("LetStm:SumAndHead") {
    val n = 8
    val m = 4
    val original = {
      // StmCount(n*m)
      val count = {
        val i = Param("i")(U8)
        StmBuild(
          n * m,
          Tuple()(),
          Undefined(Missing),
          i,
          True,
          Map[Param, (Expr, Expr, Expr)](
            i -> (C(0)(U8), Sum(C(1)(U8), i)(), Tuple()())
          ),
          Map()
        )().tchk()
      }
      val x = Param("s")(TyStm(U8, n * m))
      val rowSums = {
        val t = Param("t")(U8)
        val acc = Param("acc")(U8)
        val s = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          Tuple()(),
          Undefined(Missing),
          Sum(StmData(s)(), acc)(),
          t === (m - 1),
          Map[Param, (Expr, Expr, Expr)](
            t -> (
              C(0)(U8),
              Mux(t === (m - 1), C(0)(U8), Sum(C(1)(U8), t)())(),
              Tuple()()
            ),
            acc -> (
              C(0)(U8),
              Mux(
                t === (m - 1),
                C(0)(U8),
                Sum(StmData(s)(), acc)()
              )(),
              Tuple()()
            )
          ),
          Map(
            s -> (x, True, Tuple()())
          )
        )().tchk()
      }
      val rowHeads = {
        val t = Param("t")(U8)
        val s = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          Tuple()(),
          Undefined(Missing),
          StmData(s)(),
          t === 0,
          Map[Param, (Expr, Expr, Expr)](
            t -> (
              C(0)(U8),
              Mux(t === (m - 1), C(0)(U8), Sum(C(1)(U8), t)())(),
              Tuple()()
            )
          ),
          Map[Param, (Expr, Expr, Expr)](
            s -> (x, True, Tuple()())
          )
        )().tchk()
      }
      val zip = {
        val s0 = Param("s0")(TyStm(U8, -1))
        val s1 = Param("s1")(TyStm(U8, -1))
        StmBuild(
          n,
          Tuple()(),
          Undefined(Missing),
          Tuple(StmData(s0)(), StmData(s1)())(),
          True,
          Map(),
          Map[Param, (Expr, Expr, Expr)](
            s0 -> (rowSums, True, Tuple()()),
            s1 -> (rowHeads, True, Tuple()())
          )
        )().tchk()
      }
      LetStm(m, x, count, zip)().tchk()
    }
    val expected = StmLiteral(
      (0 until (n * m))
        .grouped(m)
        .map(xs => Tuple(C(xs.sum)(U8), C(xs.head)(U8))())
        .toSeq: _*
    )().tchk()
    val actual = mhir.eval.eval(original)
    assert(actual == expected)
  }

  test("StmBuild:StmDataWithoutReady") {
    val n = 10
    val s = Param("s")(TyStm(U8, n))
    val e = {
      val count = {
        val a = Param("a")(U8)
        StmBuild(
          n,
          Tuple()(),
          Undefined(Missing),
          a,
          True,
          Map[Param, (Expr, Expr, Expr)](
            a -> (C(0)(U8), Sum(C(1)(U8), a)(), Tuple()())
          ),
          Map()
        )().tchk()
      }
      StmBuild(
        n,
        Tuple()(),
        Undefined(Missing),
        StmData(s)(),
        True,
        Map(),
        Map[Param, (Expr, Expr, Expr)](s -> (count, False, Tuple()()))
      )().tchk()
    }
    val actual = mhir.eval.eval(e)
    val expected = StmLiteral((0 until n).map(_ => Undefined(U8)): _*)().tchk()
    assert(actual == expected)
  }
}
