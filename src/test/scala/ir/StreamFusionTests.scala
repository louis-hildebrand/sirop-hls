package ir

import operations._
import opt.PartialEvalPass
import org.scalatest.funsuite.AnyFunSuite

class StreamFusionTests extends AnyFunSuite {
  private val lpe: Expr => Expr = e =>
    PartialEvalPass.partialEval(e.tchk().lower())

  /** Simplest case of stream fusion: consumer always ready, producer always
    * valid.
    */
  test("StmBuild:Fuse:MapPlusFive") {
    val s = Param("s")()
    val original = StmBuild(
      3,
      SSome(StmNextData(s)() + 5)(),
      Map[Param, (Expr, Expr)](
        s -> (StmCount(3)().lower(), True)
      )
    )()
      .tchk(Map(s -> TyStm(TyInt, 3)))
      .asInstanceOf[StmBuild]
    val fused = original.fuseCompletely()

    // Correct behaviour
    val expectedElems = StmLiteral.ints(5, 6, 7)
    assert(ir.eval(original) == expectedElems)
    assert(ir.eval(fused) == expectedElems)
    // Successful fusion
    val i = Param("i")()
    val ideal = StmBuild(
      3,
      SSome(i + 5)(),
      Map[Param, (Expr, Expr)](
        i -> (0, i + 1)
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
    val i = Param("i")()
    val x1 = Param("x1")()
    val x2 = Param("x2")()
    // Valid every cycle
    val c1 =
      StmBuild(n, SSome(i + 11)(), Map[Param, (Expr, Expr)](i -> (0, i + 1)))()
        .tchk()
        .lower()
    // Valid every 2nd cycle
    val c2 =
      StmBuild(
        n,
        Mux(i % 2 === 0, SSome(i % 3 === 0)(), NNone(TyBool))(),
        Map[Param, (Expr, Expr)](i -> (0, i + 1))
      )().tchk().lower()
    val s = StmBuild(
      n,
      SSome(Tuple(StmNextData(x1)(), StmNextData(x2)())())(),
      Map[Param, (Expr, Expr)](
        x1 -> (c1, True),
        x2 -> (c2, True)
      )
    )().tchk().lower().asInstanceOf[StmBuild]

    val i1 = Param("i")()
    val i2 = Param("i")()

    // 1) After fusion with x1
    val actual1 = lpe(s.fuseWith(x1))
    // 1a) Correct behaviour
    assert(ir.eval(actual1) == ir.eval(s))
    // 1b) Successful fusion
    val ideal1 = lpe(
      StmBuild(
        n,
        SSome(Tuple(i1 + 11, StmNextData(x2)())())(),
        Map[Param, (Expr, Expr)](
          i1 -> (0, i1 + 1),
          x2 -> (c2, True)
        )
      )().tchk()
    )
    assert(actual1 == ideal1)

    // 2) After fusion with x2
    val actual2 = lpe(s.fuseWith(x2)).tchk()
    // 2a) Correct behaviour
    assert(ir.eval(actual2) == ir.eval(s))
    // 2b) Successful fusion
    val ideal2 = lpe(
      StmBuild(
        n,
        Mux(
          i2 % 2 === 0,
          SSome(Tuple(StmNextData(x1)(), i2 % 3 === 0)())(),
          NNone(TyTuple(TyInt, TyBool))
        )(),
        Map[Param, (Expr, Expr)](
          x1 -> (c1, i2 % 2 === 0),
          i2 -> (0, i2 + 1)
        )
      )().tchk()
    )
    assert(actual2 == ideal2)

    // 3) After two fusions
    val actual3 = lpe(s.fuseCompletely())
    // 3a) Correct behaviour
    assert(ir.eval(actual2) == ir.eval(s))
    // 3b) Successful fusion
    val ideal3 = lpe(
      StmBuild(
        n,
        Mux(
          i2 % 2 === 0,
          SSome(Tuple(i1 + 11, i2 % 3 === 0)())(),
          NNone(TyTuple(TyInt, TyBool))
        )(),
        Map[Param, (Expr, Expr)](
          i1 -> (0, Mux(i2 % 2 === 0, i1 + 1, i1)()),
          i2 -> (0, i2 + 1)
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
    val i = Param("i")()
    val x1 = Param("x1")()
    val x2 = Param("x2")()
    // Valid every 3rd cycle
    val c1 =
      StmBuild(
        n,
        Mux(i % 3 === 0, SSome(i + 3)(), NNone(TyInt))(),
        Map[Param, (Expr, Expr)](i -> (0, i + 1))
      )().tchk().lower()
    // Valid every 5th cycle
    val c2 =
      StmBuild(
        n,
        Mux(i % 5 === 0, SSome(i * 5 + 1)(), NNone(TyInt))(),
        Map[Param, (Expr, Expr)](i -> (0, i + 1))
      )().tchk().lower()
    val s = StmBuild(
      n,
      SSome(Mux(i % 2 === 0, StmNextData(x1)(), StmNextData(x2)())())(),
      Map[Param, (Expr, Expr)](
        i -> (0, i + 1),
        x1 -> (c1, i % 2 === 0),
        x2 -> (c2, i % 2 !== 0)
      )
    )().tchk().lower().asInstanceOf[StmBuild]

    // 1) After fusion with x1
    val actual1 = lpe(s.fuseWith(x1)).asInstanceOf[StmBuild]
    // 1a) Correct behaviour
    assert(ir.eval(actual1) == ir.eval(s))
    // 1b) Successful fusion
    assert(!actual1.accVars.contains(x1))
    assert(!actual1.seedByVar.exists({ case (_, z) => z == c1 }))

    // 2) After fusion with x2
    val actual2 = lpe(s.fuseWith(x2)).asInstanceOf[StmBuild]
    // 2a) Correct behaviour
    assert(ir.eval(actual2) == ir.eval(s))
    // 2b) Successful fusion
    assert(!actual2.accVars.contains(x2))
    assert(!actual2.seedByVar.exists({ case (_, z) => z == c2 }))

    // 3) After two fusions
    val actual3 = lpe(s.fuseCompletely()).asInstanceOf[StmBuild]
    // 3a) Correct behaviour
    assert(ir.eval(actual2) == ir.eval(s))
    // 3b) Successful fusion
    assert(!actual3.accVars.contains(x1))
    assert(!actual3.accVars.contains(x2))
    assert(!actual3.seedByVar.exists({ case (_, z) => z == c1 }))
    assert(!actual3.seedByVar.exists({ case (_, z) => z == c2 }))
  }
}
