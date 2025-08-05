package mhir.ir

import mhir.gen.vhdl.VhdlGenerator
import mhir.ir.Lowering.ExprLowering
import mhir.ir.StreamFuser.{StmBuildFusion, StreamFusion}
import mhir.ir.typecheck.TypeCheck
import mhir.optimize.PartialEvalPass
import org.scalatest.funsuite.AnyFunSuite

class StreamFusionTests extends AnyFunSuite {
  private val lpe: Expr => Expr = e =>
    PartialEvalPass.partialEval(e.tchk().lower())

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
      )().tchk().lower().asInstanceOf[StmBuild]
    }
    val fused = original.fuseCompletely()

    // Correct behaviour
    val expectedElems = StmLiteral.ints(5, 6, 7)
    assert(mhir.ir.eval(original) == expectedElems)
    assert(mhir.ir.eval(fused) == expectedElems)
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
      )().tchk().lower()
    // Valid every 2nd cycle
    val c2 =
      StmBuild(
        n,
        i % 3 === 0,
        i % 2 === 0,
        Map[Param, (Expr, Expr)](i -> (IntCst(0)(U8), i + 1))
      )().tchk().lower()
    val s = StmBuild(
      n,
      Tuple(StmData(x1)(), StmData(x2)())(),
      True,
      Map[Param, (Expr, Expr)](
        x1 -> (c1, True),
        x2 -> (c2, True)
      )
    )().tchk().lower().asInstanceOf[StmBuild]

    val i1 = Param("i")(U8)
    val i2 = Param("i")(U8)

    // 1) After fusion with x1
    val actual1 = lpe(s.fuseWith(x1))
    // 1a) Correct behaviour
    assert(mhir.ir.eval(actual1) == mhir.ir.eval(s))
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
    assert(mhir.ir.eval(actual2) == mhir.ir.eval(s))
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
    assert(mhir.ir.eval(actual2) == mhir.ir.eval(s))
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
      )().tchk().lower()
    // Valid every 5th cycle
    val c2 =
      StmBuild(
        n,
        i * 5 + 1,
        i % 5 === 0,
        Map[Param, (Expr, Expr)](i -> (IntCst(0)(U8), i + 1))
      )().tchk().lower()
    val s = StmBuild(
      n,
      Mux(i % 2 === 0, StmData(x1)(), StmData(x2)())(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (IntCst(0)(U8), i + 1),
        x1 -> (c1, i % 2 === 0),
        x2 -> (c2, i % 2 !== 0)
      )
    )().tchk().lower().asInstanceOf[StmBuild]

    // 1) After fusion with x1
    val actual1 = lpe(s.fuseWith(x1)).asInstanceOf[StmBuild]
    // 1a) Correct behaviour
    assert(mhir.ir.eval(actual1) == mhir.ir.eval(s))
    // 1b) Successful fusion
    assert(!actual1.accVars.contains(x1))
    assert(!actual1.seedByVar.exists({ case (_, z) => z == c1 }))

    // 2) After fusion with x2
    val actual2 = lpe(s.fuseWith(x2)).asInstanceOf[StmBuild]
    // 2a) Correct behaviour
    assert(mhir.ir.eval(actual2) == mhir.ir.eval(s))
    // 2b) Successful fusion
    assert(!actual2.accVars.contains(x2))
    assert(!actual2.seedByVar.exists({ case (_, z) => z == c2 }))

    // 3) After two fusions
    val actual3 = lpe(s.fuseCompletely()).asInstanceOf[StmBuild]
    // 3a) Correct behaviour
    assert(mhir.ir.eval(actual2) == mhir.ir.eval(s))
    // 3b) Successful fusion
    assert(!actual3.accVars.contains(x1))
    assert(!actual3.accVars.contains(x2))
    assert(!actual3.seedByVar.exists({ case (_, z) => z == c1 }))
    assert(!actual3.seedByVar.exists({ case (_, z) => z == c2 }))
  }

  test("LetStm:FuseCompletely:InterleaveSelf") {
    val n = 10
    val input = Param("input")(TyStm(U8, n))
    val s = Param("s")()
    // If
    //   s = [a, b, c, ...]
    // Then the interleaved stream will be
    //   interleave3(s) = [a, a, a, b, b, b, c, c, c, ...]
    val interleave3 = (s: Expr) => {
      val t = Param("t")(U8)
      val s0 = Param("s0")(TyStm(U8, n))
      val s1 = Param("s1")(TyStm(U8, n))
      val s2 = Param("s2")(TyStm(U8, n))
      StmBuild(
        3 * n,
        Mux(
          t equ C(0)(U8),
          StmData(s0)(),
          Mux(t equ C(1)(U8), StmData(s1)(), StmData(s2)())()
        )(),
        True,
        Map[Param, (Expr, Expr)](
          t -> (C(0)(U8), Mux(t equ C(2)(U8), C(0)(U8), Sum(C(1)(U8), t)())()),
          s0 -> (s, t equ C(0)(U8)),
          s1 -> (s, t equ C(1)(U8)),
          s2 -> (s, t equ C(2)(U8))
        )
      )()
    }
    val original = LetStm(s, input, interleave3(s))().tchk().lower()

    val fused = original.fuseCompletely()

    // Correct behaviour
    val examples = Seq(
      StmLiteral((0 until n).map(t => C(t)(U8)): _*)().tchk(),
      StmLiteral((0 until n).map(t => C(t * t)(U8)): _*)().tchk(),
      StmLiteral((0 until n).map(t => C(t % 3)(U8)): _*)().tchk()
    )
    for (stm <- examples) {
      val expected = mhir.ir.eval(LetStm(input, stm, original)())
      val actual = mhir.ir.eval(LetStm(input, stm, fused)())
      assert(actual == expected)
    }

    // Successful fusion
    assert(fused.hasType)
    assert(
      fused.seedByVar.forall({ case (x, z) =>
        z == input || !x.typ.isInstanceOf[TyStm]
      })
    )

    VhdlGenerator.validateExpr(Function(input, fused)().tchk())
  }
}
