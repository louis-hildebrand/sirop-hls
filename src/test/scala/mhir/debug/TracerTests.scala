package mhir.debug

import mhir.ir.Lowering.ExprLowering
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import org.scalatest.funsuite.AnyFunSuite

class TracerTests extends AnyFunSuite {
  private val TracesDir =
    os.pwd / "src" / "test" / "resources" / "example_traces"

  /** If true, then whatever traces are generated will be saved. This should
    * only ever be temporarily set to true; remember to set it back to false
    * after running the tests. Also, inspect the saved traces to ensure they
    * make sense.
    */
  private val SaveTraces = false

  test("SaveTraces") {
    assert(!SaveTraces)
  }

  private def save(trace: Trace, filename: String): Unit = {
    val p = TracesDir / filename
    if (os.exists(p)) os.remove(p)
    trace.dumpJson(p)
  }

  test("Counter") {
    mhir.ir.reset()
    val n = 3
    val i = Param("i", -1)(U8)
    val j = Param("j", -1)(U8)
    val s = StmBuild(
      n,
      i + 2 * j,
      i % 2 === 0,
      Map[Param, (Expr, Expr)](
        i -> (IntCst(0)(U8), i + 1),
        j -> (IntCst(10)(U8), j + i)
      )
    )().tchk().lower().asInstanceOf[StmBuild]

    val fullTrace = Tracer.traceAll(s)
    if (SaveTraces) {
      save(fullTrace, "trace-all-counter.json")
    } else {
      val expectedFullTrace = os.read(TracesDir / "trace-all-counter.json")
      assert(fullTrace.json == expectedFullTrace)
    }
    assume(!SaveTraces)
  }

  test("Stm2Vec") {
    mhir.ir.reset()
    val n = IntCst(4)(U8)
    val i = Param("i", -1)(U8)
    val input =
      StmBuild(
        n,
        ReshapeData(i, I32)(),
        True,
        Map[Param, (Expr, Expr)](i -> (C(42)(U8), i + 1))
      )()
    val s = Param("s", -1)(TyStm(I32, -1))
    val v = Param("v", -1)(TyVec(I32, n))
    val t = Param("t", -1)(U8)
    val stm2vec = StmBuild(
      1,
      VecBuild(
        n,
        Function(i, Mux((i + 1) === n, StmData(s)(), VecAccess(v, i + 1)())())()
      )(),
      t === n - 1,
      Map[Param, (Expr, Expr)](
        s -> (input, True),
        v -> (
          VecBuild(n, Function(i, Default(I32))())(),
          VecBuild(
            n,
            Function(
              i,
              Mux((i + 1) === n, StmData(s)(), VecAccess(v, i + 1)())()
            )()
          )()
        ),
        t -> (IntCst(0)(U8), t + 1)
      )
    )().tchk().lower().asInstanceOf[StmBuild]

    val fullTrace = Tracer.traceAll(stm2vec)
    if (SaveTraces) {
      save(fullTrace, "trace-all-stm2vec.json")
    } else {
      val expectedFullTrace = os.read(TracesDir / "trace-all-stm2vec.json")
      assert(fullTrace.json == expectedFullTrace)
    }
    assume(!SaveTraces)
  }

  test("Interleave") {
    mhir.ir.reset()
    val stm1 = {
      val i = Param("i", -1)(U8)
      StmBuild(
        2,
        i,
        True,
        Map[Param, (Expr, Expr)](i -> (IntCst(0)(U8), i + 1))
      )()
    }
    val stm2 = {
      val s = Param("s", -1)(TyStm(U8, -1))
      val j = Param("j", -1)(U8)
      StmBuild(
        2,
        StmData(s)(),
        StmData(s)() % 3 === 0,
        Map[Param, (Expr, Expr)](
          s -> (
            StmBuild(
              4,
              j,
              True,
              Map[Param, (Expr, Expr)](j -> (IntCst(42)(U8), j + 1))
            )(),
            True
          )
        )
      )()
    }
    val s = {
      val b = Param("b", -1)(TyBool)
      val s1 = Param("s1", -1)(TyStm(U8, -1))
      val s2 = Param("s2", -1)(TyStm(U8, -1))
      StmBuild(
        4,
        Mux(b, StmData(s1)(), StmData(s2)())(),
        True,
        Map[Param, (Expr, Expr)](
          b -> (True, !b),
          s1 -> (stm1, b),
          s2 -> (stm2, !b)
        )
      )().tchk().lower().asInstanceOf[StmBuild]
    }

    val fullTrace = Tracer.traceAll(s)
    if (SaveTraces) {
      save(fullTrace, "trace-all-interleave.json")
    } else {
      val expectedFullTrace = os.read(TracesDir / "trace-all-interleave.json")
      assert(fullTrace.json == expectedFullTrace)
    }
    assume(!SaveTraces)
  }

  test("LetStm") {
    mhir.ir.reset()
    // StmScan(
    //     let s = StmRange(5, 2, 1) in
    //         StmZip(s, StmMap(x => x + 5)),
    //     (acc : (u8, u8)) => (x : (u8, u8)) =>
    //         (acc.0 + x.0, acc.1 * x.1)
    // )
    val stm = {
      // StmRange(5, 2, 1)
      val count = {
        val i = Param("i")(U16)
        StmBuild(
          5,
          i,
          True,
          Map[Param, (Expr, Expr)](
            i -> (C(2)(U16), Sum(C(1)(U16), i)())
          )
        )()
      }
      val s = Param("s")(TyStm(U16, 5))
      // StmMap(s, x => x + 5)
      val plusFive = {
        val a = Param("a")(TyStm(U16, 5))
        StmBuild(
          5,
          Sum(C(5)(U16), StmData(a)())(),
          True,
          Map[Param, (Expr, Expr)](
            a -> (s, True)
          )
        )()
      }
      // StmZip(s, plusFive)
      val zipped = {
        val s0 = Param("s0")(TyStm(U16, 5))
        val s1 = Param("s1")(TyStm(U16, 5))
        StmBuild(
          5,
          Tuple(StmData(s0)(), StmData(s1)())(),
          True,
          Map[Param, (Expr, Expr)](
            s0 -> (s, True),
            s1 -> (plusFive, True)
          )
        )()
      }
      val scan = {
        val sAcc = Param("s")(TyStm((U16, U16), -1))
        val acc = Param("acc")((U16, U16))
        StmBuild(
          5,
          acc,
          True,
          Map[Param, (Expr, Expr)](
            sAcc -> (LetStm(s, count, zipped)(), True),
            acc -> (
              Tuple(C(0)(U16), C(1)(U16))(),
              Tuple(
                acc.__0 + StmData(sAcc)().__0,
                acc.__1 * StmData(sAcc)().__1
              )()
            )
          )
        )().tchk()
      }
      scan
    }

    val fullTrace = Tracer.traceAll(stm)
    if (SaveTraces) {
      save(fullTrace, "trace-all-let-stm.json")
    } else {
      val expectedFullTrace = os.read(TracesDir / "trace-all-let-stm.json")
      assert(fullTrace.json == expectedFullTrace)
    }
    assume(!SaveTraces)
  }
}
