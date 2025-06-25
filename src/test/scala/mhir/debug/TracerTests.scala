package mhir.debug

import mhir.ir._
import mhir.sugar.VecShiftLeft
import org.scalatest.funsuite.AnyFunSuite

class TracerTests extends AnyFunSuite {
  private val TracesDir =
    os.pwd / "src" / "test" / "scala" / "mhir" / "debug" / "traces"

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
    trace.dump(p)
  }

  test("Counter") {
    mhir.ir.resetState()
    val n = 3
    val i = Param("i")(U8)
    val j = Param("j")(U8)
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
    val topTrace = Tracer.traceTop(s)
    if (SaveTraces) {
      save(fullTrace, "trace-all-counter.json")
      save(topTrace, "trace-top-counter.json")
    } else {
      val expectedFullTrace = os.read(TracesDir / "trace-all-counter.json")
      assert(fullTrace.json == expectedFullTrace)
      val expectedTopTrace = os.read(TracesDir / "trace-top-counter.json")
      assert(topTrace.json == expectedTopTrace)
    }
    assume(!SaveTraces)
  }

  test("Stm2Vec") {
    mhir.ir.resetState()
    val n = IntCst(4)(U8)
    val i = Param("i")(U8)
    val input =
      StmBuild(
        n,
        ReshapeData(i, I32)(),
        True,
        Map[Param, (Expr, Expr)](i -> (IntCst(42)(U8), i + 1))
      )()
    val s = Param("s")(TyStm(I32, -1))
    val v = Param("v")(TyVec(I32, n))
    val t = Param("t")(U8)
    val stm2vec = StmBuild(
      1,
      VecShiftLeft(v, StmData(s)())(),
      t === n - 1,
      Map[Param, (Expr, Expr)](
        s -> (input, True),
        v -> (
          VecBuild(n, U8 ::+ (_ => Default(I32)))(),
          VecShiftLeft(v, StmData(s)())()
        ),
        t -> (IntCst(0)(U8), t + 1)
      )
    )().tchk().lower().asInstanceOf[StmBuild]

    val fullTrace = Tracer.traceAll(stm2vec)
    val topTrace = Tracer.traceTop(stm2vec)
    if (SaveTraces) {
      save(fullTrace, "trace-all-stm2vec.json")
      save(topTrace, "trace-top-stm2vec.json")
    } else {
      val expectedFullTrace = os.read(TracesDir / "trace-all-stm2vec.json")
      assert(fullTrace.json == expectedFullTrace)
      val expectedTopTrace = os.read(TracesDir / "trace-top-stm2vec.json")
      assert(topTrace.json == expectedTopTrace)
    }
    assume(!SaveTraces)
  }

  test("Interleave") {
    mhir.ir.resetState()
    val stm1 = {
      val i = Param("i")(U8)
      StmBuild(
        2,
        i,
        True,
        Map[Param, (Expr, Expr)](i -> (IntCst(0)(U8), i + 1))
      )()
    }
    val stm2 = {
      val s = Param("s")(TyStm(U8, -1))
      val j = Param("j")(U8)
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
      val b = Param("b")(TyBool)
      val s1 = Param("s1")(TyStm(U8, -1))
      val s2 = Param("s2")(TyStm(U8, -1))
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
    val topTrace = Tracer.traceTop(s)
    if (SaveTraces) {
      save(fullTrace, "trace-all-interleave.json")
      save(topTrace, "trace-top-interleave.json")
    } else {
      val expectedFullTrace = os.read(TracesDir / "trace-all-interleave.json")
      assert(fullTrace.json == expectedFullTrace)
      val expectedTopTrace = os.read(TracesDir / "trace-top-interleave.json")
      assert(topTrace.json == expectedTopTrace)
    }
    assume(!SaveTraces)
  }
}
