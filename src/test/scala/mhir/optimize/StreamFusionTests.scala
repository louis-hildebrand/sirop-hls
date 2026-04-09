package mhir.optimize

import mhir.canonicalize._
import mhir.gen.vhdl.VhdlGenerator
import mhir.ir._
import mhir.optimize.StreamFuser.{StmBuildFusion, StreamFusion}
import mhir.sugar._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class StreamFusionTests extends AnyFunSuite {
  private val lpe: Expr => Expr = e =>
    PartialEvalPass.partialEval(e.tchk().lower)

  /** Simplest case of stream fusion: consumer always ready, producer always
    * valid.
    */
  test("StmBuild:Fuse:MapPlusFive") {
    val u2 = TyUInt(2)
    val i = Param("i")(u2)
    val counter =
      StmBuild(
        IntCst(3)(u2),
        i,
        True,
        Map[Param, (Expr, Expr)](i -> (IntCst(0)(u2), i + 1))
      )()
    val original = {
      val s = Param("s")(TyStm(u2, -1))
      StmBuild(
        3,
        PadTo(StmData(s)(), 4)() + 5,
        True,
        Map[Param, (Expr, Expr)](
          s -> (counter, True)
        )
      )().tchk().lower.asInstanceOf[StmBuild]
    }
    val fused = original.fuseCompletely()

    // Correct behaviour
    val expectedElems = StmLiteral.ints(5, 6, 7)
    assert(mhir.eval.eval(original) == expectedElems)
    assert(mhir.eval.eval(fused) == expectedElems)
    // Successful fusion
    val ideal = StmBuild(
      3,
      PadTo(i, 4)() + 5,
      True,
      Map[Param, (Expr, Expr)](
        i -> (IntCst(0)(u2), i + 1)
      )
    )()
    val simplFused = lpe(fused)
    val simplIdeal = lpe(ideal)
    assert(simplFused == simplIdeal)
  }

  /** Stream fusion, but where the producer is not always valid.
    */
  test("StmBuild:Fuse:ZipCounters") {
    val n = 10
    val i = Param("i")(U8)
    val x1 = Param("x1")(TyStm(I16, n))
    val x2 = Param("x2")(TyStm(TyBool, n))
    // Valid every cycle
    val c1 =
      StmBuild(
        n,
        ReshapeData(i + 11, I16)(),
        True,
        Map[Param, (Expr, Expr)](i -> (IntCst(0)(U8), i + 1))
      )().tchk().lower
    // Valid every 2nd cycle
    val c2 =
      StmBuild(
        n,
        i % 3 === 0,
        i % 2 === 0,
        Map[Param, (Expr, Expr)](i -> (IntCst(0)(U8), i + 1))
      )().tchk().lower
    val s = StmBuild(
      n,
      Tuple(StmData(x1)(), StmData(x2)())(),
      True,
      Map[Param, (Expr, Expr)](
        x1 -> (c1, True),
        x2 -> (c2, True)
      )
    )().tchk().lower.asInstanceOf[StmBuild]

    val i1 = Param("i")(U8)
    val i2 = Param("i")(U8)

    // 1) After fusion with x1
    val actual1 = lpe(s.fuseWith(x1))
    // 1a) Correct behaviour
    assert(mhir.eval.eval(actual1) == mhir.eval.eval(s))
    // 1b) Successful fusion
    val ideal1 = lpe(
      StmBuild(
        n,
        Tuple(ReshapeData(i1 + 11, I16)(), StmData(x2)())(),
        True,
        Map[Param, (Expr, Expr)](
          i1 -> (IntCst(0)(U8), i1 + 1),
          x2 -> (c2, True)
        )
      )().tchk()
    )
    assert(actual1 == ideal1)

    // 2) After fusion with x2
    val actual2 = lpe(s.fuseWith(x2)).tchk()
    // 2a) Correct behaviour
    assert(mhir.eval.eval(actual2) == mhir.eval.eval(s))
    // 2b) Successful fusion
    val ideal2 = lpe(
      StmBuild(
        n,
        Tuple(StmData(x1)(), i2 % 3 === 0)(),
        i2 % 2 === 0,
        Map[Param, (Expr, Expr)](
          x1 -> (c1, i2 % 2 === 0),
          i2 -> (IntCst(0)(U8), i2 + 1)
        )
      )().tchk()
    )
    assert(actual2 == ideal2)

    // 3) After two fusions
    val actual3 = lpe(s.fuseCompletely())
    // 3a) Correct behaviour
    assert(mhir.eval.eval(actual2) == mhir.eval.eval(s))
    // 3b) Successful fusion
    val ideal3 = lpe(
      StmBuild(
        n,
        Tuple(ReshapeData(i1 + 11, I16)(), i2 % 3 === 0)(),
        i2 % 2 === 0,
        Map[Param, (Expr, Expr)](
          i1 -> (IntCst(0)(U8), Mux(i2 % 2 === 0, i1 + 1, i1)()),
          i2 -> (IntCst(0)(U8), i2 + 1)
        )
      )().tchk()
    )
    assert(actual3 == ideal3)
  }

  /** Stream fusion where the producer is not always valid <i>and</i> the
    * consumer is not always ready.
    */
  test("StmBuild:Fuse:Interleave") {
    val n = 10
    val i = Param("i")(U8)
    val x1 = Param("x1")(TyStm(U8, n))
    val x2 = Param("x2")(TyStm(U8, n))
    // Valid every 3rd cycle
    val c1 =
      StmBuild(
        n,
        i + 3,
        i % 3 === 0,
        Map[Param, (Expr, Expr)](i -> (IntCst(0)(U8), i + 1))
      )().tchk().lower
    // Valid every 5th cycle
    val c2 =
      StmBuild(
        n,
        i * 5 + 1,
        i % 5 === 0,
        Map[Param, (Expr, Expr)](i -> (IntCst(0)(U8), i + 1))
      )().tchk().lower
    val s = StmBuild(
      n,
      Mux(i % 2 === 0, StmData(x1)(), StmData(x2)())(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (IntCst(0)(U8), i + 1),
        x1 -> (c1, i % 2 === 0),
        x2 -> (c2, i % 2 !== 0)
      )
    )().tchk().lower.asInstanceOf[StmBuild]

    // 1) After fusion with x1
    val actual1 = lpe(s.fuseWith(x1)).asInstanceOf[StmBuild]
    // 1a) Correct behaviour
    assert(mhir.eval.eval(actual1) == mhir.eval.eval(s))
    // 1b) Successful fusion
    assert(!actual1.accVars.contains(x1))
    assert(!actual1.seedByVar.exists({ case (_, z) => z == c1 }))

    // 2) After fusion with x2
    val actual2 = lpe(s.fuseWith(x2)).asInstanceOf[StmBuild]
    // 2a) Correct behaviour
    assert(mhir.eval.eval(actual2) == mhir.eval.eval(s))
    // 2b) Successful fusion
    assert(!actual2.accVars.contains(x2))
    assert(!actual2.seedByVar.exists({ case (_, z) => z == c2 }))

    // 3) After two fusions
    val actual3 = lpe(s.fuseCompletely()).asInstanceOf[StmBuild]
    // 3a) Correct behaviour
    assert(mhir.eval.eval(actual2) == mhir.eval.eval(s))
    // 3b) Successful fusion
    assert(!actual3.accVars.contains(x1))
    assert(!actual3.accVars.contains(x2))
    assert(!actual3.seedByVar.exists({ case (_, z) => z == c1 }))
    assert(!actual3.seedByVar.exists({ case (_, z) => z == c2 }))
  }

  test("LetStm:FuseCompletely:InterleaveSelf") {
    val n = 10
    val input = Param("input")(TyStm(U8, n))
    val stmId = (s: Expr) => {
      val sAcc = Param("s")(TyStm(U8, n))
      StmBuild(
        n,
        StmData(sAcc)(),
        True,
        Map[Param, (Expr, Expr)](sAcc -> (s, True))
      )()
    }
    // If
    //   sA = [a, b, c, ...]
    // and
    //   sB = [z, y, x, ...]
    // Then the interleaved stream will be
    //   interleave(s) = [a, a, a, z, b, b, b, y, c, c, c, x, ...]
    val interleave = (sA: Expr, sB: Expr) => {
      val t = Param("t")(U8)
      val s0 = Param("s0")(TyStm(U8, n))
      val s1 = Param("s1")(TyStm(U8, n))
      val s2 = Param("s2")(TyStm(U8, n))
      val s3 = Param("s3")(TyStm(U8, n))
      StmBuild(
        3 * n,
        Mux(
          t equ C(0)(U8),
          StmData(s0)(),
          Mux(
            t equ C(1)(U8),
            StmData(s1)(),
            Mux(t equ C(2)(U8), StmData(s2)(), StmData(s3)())()
          )()
        )(),
        True,
        Map[Param, (Expr, Expr)](
          t -> (
            C(0)(U8),
            Mux(
              t equ C(3)(U8),
              C(0)(U8),
              Sum(C(1)(U8), t)()
            )()
          ),
          s0 -> (stmId(sA), t equ C(0)(U8)),
          s1 -> (sA, t equ C(1)(U8)),
          s2 -> (sA, t equ C(2)(U8)),
          s3 -> (sB, t equ C(3)(U8))
        )
      )()
    }
    val sA = Param("s_a")()
    val sB = Param("s_b")(TyStm(U8, n))
    val original = LetStm(1, sA, input, interleave(sA, sB))().tchk().lower

    val fused = original.fuseCompletely()

    // Correct behaviour
    val examples = Seq(
      (
        StmLiteral((0 until n).map(x => C(x + 1)(U8)): _*)().tchk(),
        StmLiteral((0 until n).map(x => C(x + 2)(U8)): _*)().tchk()
      ),
      (
        StmLiteral((0 until n).map(t => C(t * t)(U8)): _*)().tchk(),
        StmLiteral((0 until n).map(C(_)(U8)): _*)().tchk()
      ),
      (
        StmLiteral((0 until n).map(t => C(t % 3)(U8)): _*)().tchk(),
        StmLiteral((0 until n).map(C(_)(U8)): _*)().tchk()
      )
    )
    for ((a, b) <- examples) {
      val expected =
        mhir.eval.eval(LetStm(1, input, a, LetStm(1, sB, b, original)())())
      val actual =
        mhir.eval.eval(LetStm(1, input, a, LetStm(1, sB, b, fused)())())
      assert(actual == expected)
    }

    // Successful fusion
    assert(fused.hasType)
    assert(
      fused.seedByVar.forall({ case (x, z) =>
        z == input || z == sB || !x.typ.isInstanceOf[TyStm]
      })
    )

    VhdlGenerator.validateExpr(Function(input, Function(sB, fused)())().tchk())
  }

  /** Suppose the original StmBuild reads once from the producer stream and then
    * use the value many times. The fusion transformation must not make the
    * mistake of pre-emptively fetching the next element from the producer,
    * since this will cause the stream to get stuck if the producer is empty.
    */
  test("LetStm:FuseCompletely:ReadOnceUseRepeatedly") {
    val s = Param("s")(TyStm(U8, 1))
    val original = LetStm(
      1,
      s,
      s, {
        val s0 = Param("s0")(TyStm(U8, -1))
        val s0Buf = Param("s0_buf")(U8)
        val s1 = Param("s1")(TyStm(U8, -1))
        val s1Buf = Param("s1_buf")(U8)
        val isFirstStep = Param("is_first_step")(TyBool)
        StmBuild(
          3,
          Tuple(s0Buf, s1Buf)(),
          !isFirstStep,
          Map[Param, (Expr, Expr)](
            s0 -> (s, isFirstStep),
            s0Buf -> (C(0)(U8), Mux(isFirstStep, StmData(s0)(), s0Buf)()),
            s1 -> (s, isFirstStep),
            s1Buf -> (C(0)(U8), Mux(isFirstStep, StmData(s1)(), s1Buf)()),
            isFirstStep -> (True, False)
          )
        )()
      }
    )().tchk().lower
    val fused = original.fuseCompletely()

    // Correct behaviour
    val examples = Seq(42, 0, 201).map(C(_)(U8)).map(StmLiteral(_)().tchk())
    for (input <- examples) {
      val originalVal = mhir.eval.eval(original.subPreserveType(s -> input))
      val c = input.asInstanceOf[StmLiteral].elems.head
      val expectedVal =
        StmLiteral(Tuple(c, c)(), Tuple(c, c)(), Tuple(c, c)())().tchk()
      assert(originalVal == expectedVal)
      val fusedVal = mhir.eval.eval(fused.subPreserveType(s -> input))
      assert(fusedVal == originalVal)
    }

    // Successful fusion
    assert(fused.hasType)
    assert(
      fused.seedByVar.forall({ case (y, z) =>
        z == s || !y.typ.isInstanceOf[TyStm]
      })
    )

    VhdlGenerator.validateExpr(Function(s, fused)().tchk())
  }
}
