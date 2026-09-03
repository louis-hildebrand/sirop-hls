package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.optimize.StreamFuser.StmBuildFusion
import mhir.parse.sirop.Parser
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
        Tuple()(),
        Undefined(Missing),
        i,
        True,
        Map[Param, (Expr, Expr, Expr)](i -> (IntCst(0)(u2), i + 1, Tuple()())),
        Map()
      )()
    val s = Param("s")(TyStm(u2, -1))
    val original = StmBuild(
      3,
      Tuple()(),
      Undefined(Missing),
      PadTo(StmData(s)(), 4)() + 5,
      True,
      Map(),
      Map[Param, (Expr, Expr, Expr)](
        s -> (counter, True, Tuple()())
      )
    )().tchk().lower.asInstanceOf[StmBuild]
    val fused = original.fuseWith(s)

    // Correct behaviour
    val expectedElems = StmLiteral.ints(5, 6, 7)
    assert(mhir.eval.eval(original) == expectedElems)
    assert(mhir.eval.eval(fused) == expectedElems)
    // Successful fusion
    val ideal = StmBuild(
      3,
      Tuple()(),
      Undefined(Missing),
      PadTo(i, 4)() + 5,
      True,
      Map[Param, (Expr, Expr, Expr)](
        i -> (IntCst(0)(u2), i + 1, Tuple()())
      ),
      Map()
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
        Tuple()(),
        Undefined(Missing),
        ReshapeData(i + 11, I16)(),
        True,
        Map[Param, (Expr, Expr, Expr)](i -> (IntCst(0)(U8), i + 1, Tuple()())),
        Map()
      )().tchk().lower
    // Valid every 2nd cycle
    val c2 =
      StmBuild(
        n,
        Tuple()(),
        Undefined(Missing),
        i % 3 === 0,
        i % 2 === 0,
        Map[Param, (Expr, Expr, Expr)](i -> (IntCst(0)(U8), i + 1, Tuple()())),
        Map()
      )().tchk().lower
    val s = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Tuple(StmData(x1)(), StmData(x2)())(),
      True,
      Map(),
      Map[Param, (Expr, Expr, Expr)](
        x1 -> (c1, True, Tuple()()),
        x2 -> (c2, True, Tuple()())
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
        Tuple()(),
        Undefined(Missing),
        Tuple(ReshapeData(i1 + 11, I16)(), StmData(x2)())(),
        True,
        Map[Param, (Expr, Expr, Expr)](
          i1 -> (IntCst(0)(U8), i1 + 1, Tuple()())
        ),
        Map[Param, (Expr, Expr, Expr)](
          x2 -> (c2, True, Tuple()())
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
        Tuple()(),
        Undefined(Missing),
        Tuple(StmData(x1)(), i2 % 3 === 0)(),
        i2 % 2 === 0,
        Map[Param, (Expr, Expr, Expr)](
          i2 -> (IntCst(0)(U8), i2 + 1, Tuple()())
        ),
        Map[Param, (Expr, Expr, Expr)](
          x1 -> (c1, i2 % 2 === 0, Tuple()())
        )
      )().tchk()
    )
    assert(actual2 == ideal2)

    // 3) After two fusions
    val actual3 = lpe(s.fuseWith(x1).fuseWith(x2))
    // 3a) Correct behaviour
    assert(mhir.eval.eval(actual2) == mhir.eval.eval(s))
    // 3b) Successful fusion
    val ideal3 = lpe(
      StmBuild(
        n,
        Tuple()(),
        Undefined(Missing),
        Tuple(ReshapeData(i1 + 11, I16)(), i2 % 3 === 0)(),
        i2 % 2 === 0,
        Map[Param, (Expr, Expr, Expr)](
          i1 -> (
            IntCst(0)(U8),
            Mux(i2 % 2 === 0, i1 + 1, i1)(),
            Tuple()()
          ),
          i2 -> (IntCst(0)(U8), i2 + 1, Tuple()())
        ),
        Map()
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
        Tuple()(),
        Undefined(Missing),
        i + 3,
        i % 3 === 0,
        Map[Param, (Expr, Expr, Expr)](i -> (IntCst(0)(U8), i + 1, Tuple()())),
        Map()
      )().tchk().lower
    // Valid every 5th cycle
    val c2 =
      StmBuild(
        n,
        Tuple()(),
        Undefined(Missing),
        i * 5 + 1,
        i % 5 === 0,
        Map[Param, (Expr, Expr, Expr)](i -> (IntCst(0)(U8), i + 1, Tuple()())),
        Map()
      )().tchk().lower
    val s = StmBuild(
      n,
      Tuple()(),
      Undefined(Missing),
      Mux(i % 2 === 0, StmData(x1)(), StmData(x2)())(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        i -> (IntCst(0)(U8), i + 1, Tuple()())
      ),
      Map[Param, (Expr, Expr, Expr)](
        x1 -> (c1, i % 2 === 0, Tuple()()),
        x2 -> (c2, i % 2 !== 0, Tuple()())
      )
    )().tchk().lower.asInstanceOf[StmBuild]

    // 1) After fusion with x1
    val actual1 = lpe(s.fuseWith(x1)).asInstanceOf[StmBuild]
    // 1a) Correct behaviour
    assert(mhir.eval.eval(actual1) == mhir.eval.eval(s))
    // 1b) Successful fusion
    assert(!actual1.namesDefinedHere.contains(x1))
    assert(!actual1.producers.exists({ case (_, (z, _, _)) => z == c1 }))

    // 2) After fusion with x2
    val actual2 = lpe(s.fuseWith(x2)).asInstanceOf[StmBuild]
    // 2a) Correct behaviour
    assert(mhir.eval.eval(actual2) == mhir.eval.eval(s))
    // 2b) Successful fusion
    assert(!actual2.namesDefinedHere.contains(x2))
    assert(!actual2.producers.exists({ case (_, (z, _, _)) => z == c2 }))

    // 3) After two fusions
    val actual3 = lpe(s.fuseWith(x1).fuseWith(x2)).asInstanceOf[StmBuild]
    // 3a) Correct behaviour
    assert(mhir.eval.eval(actual2) == mhir.eval.eval(s))
    // 3b) Successful fusion
    assert(!actual3.namesDefinedHere.contains(x1))
    assert(!actual3.namesDefinedHere.contains(x2))
    assert(!actual3.producers.exists({ case (_, (z, _, _)) => z == c1 }))
    assert(!actual3.producers.exists({ case (_, (z, _, _)) => z == c2 }))
  }

  test("FilterWithOutputRegisters") {
    val in1 = Param("in_stm_1", -1)(TyStm(U8, 8))
    val in2 = Param("in_stm_2", -1)(TyStm(U8, 8))
    val original = Parser
      .parse(
        """/* StmConcat */
          |sbuild(8)(if i == 4:u8 then sdata(p2) else sdata(p1), true) {
          |  (i: u8) = {
          |    init: 0:u8,
          |    next: if i == 4:u8 then 4:u8 else i + 1
          |  }
          |} {
          |  /* Only even inputs, along with the sum of all previous elements */
          |  (p1: Stm[(u8, u8), 4]) = {
          |    stm: sbuild(4)(data, valid) {
          |      (data: (u8, u8)) = {
          |        init: undefined[(u8, u8)],
          |        next: (sum, sdata(p))
          |      },
          |      (valid: bool) = {
          |        init: false,
          |        next: j < 4 && sdata(p) % 2 == 0:u8
          |      },
          |      (j: u8) = {
          |        init: 0:u8,
          |        next: if j < 4 && sdata(p) % 2 == 0:u8 then j + 1 else j
          |      },
          |      (sum: u8) = {
          |        init: 0:u8,
          |        next: sum + (if j < 4 then sdata(p) else 0:u8)
          |      }
          |    } {
          |      (p: Stm[u8, 8]) = {
          |        stm: in_stm_1,
          |        ready: j < 4:u8
          |      }
          |    },
          |    ready: i != 4:u8
          |  },
          |  /* Only odd inputs, along with their index */
          |  (p2: Stm[(u8, u8), 4]) = {
          |    stm: sbuild(4)(data, valid) {
          |      (data: (u8, u8)) = {
          |        init: undefined[(u8, u8)],
          |        next: (i, sdata(p))
          |      },
          |      (valid: bool) = {
          |        init: false,
          |        next: j < 4 && sdata(p) % 2 != 0:u8
          |      },
          |      (j: u8) = {
          |        init: 0:u8,
          |        next: if j < 4 && sdata(p) % 2 != 0:u8 then j + 1 else j
          |      },
          |      (i: u8) = {
          |        init: 0:u8,
          |        next: i + 1
          |      }
          |    } {
          |      (p: Stm[u8, 8]) = {
          |        stm: in_stm_2,
          |        ready: j < 4:u8
          |      }
          |    },
          |    ready: i == 4:u8
          |  }
          |}
          |""".stripMargin
      )
      .body
      .tchk(Map(in1 -> in1.typ, in2 -> in2.typ), Map())
      .lower
      .asInstanceOf[StmBuild]

    // Check that this code works the way I expect
    val in1Val = StmLiteral(
      C(1)(U8),
      C(2)(U8),
      C(3)(U8),
      C(4)(U8),
      C(6)(U8), // deliberately swapped
      C(5)(U8), // deliberately swapped
      C(7)(U8),
      C(8)(U8)
    )().tchk()
    val in2Val = StmLiteral(
      C(1)(U8),
      C(2)(U8),
      C(33)(U8),
      C(4)(U8),
      C(26)(U8),
      C(15)(U8),
      C(7)(U8),
      C(8)(U8)
    )().tchk()
    val expected = StmLiteral(
      Tuple(C(1)(U8), C(2)(U8))(),
      Tuple(C(6)(U8), C(4)(U8))(),
      Tuple(C(10)(U8), C(6)(U8))(),
      Tuple(C(28)(U8), C(8)(U8))(),
      Tuple(C(0)(U8), C(1)(U8))(),
      Tuple(C(2)(U8), C(33)(U8))(),
      Tuple(C(5)(U8), C(15)(U8))(),
      Tuple(C(6)(U8), C(7)(U8))()
    )().tchk()
    val actual0 =
      mhir.eval.eval(original, inputs = Map(in1 -> in1Val, in2 -> in2Val))
    assert(actual0 == expected)

    // Fuse with p1
    {
      val p1 = Param("p1", -1)(TyStm((U8, U8), 4))
      val fused = original.fuseWith(p1)
      // (Correct behaviour)
      val actual =
        mhir.eval.eval(fused, inputs = Map(in1 -> in1Val, in2 -> in2Val))
      assert(actual == expected)
      // (Successful fusion)
      assert(fused.producers.size == 2)
      val in1UsedDirectly = fused.producers
        .exists({ case (_, (z, _, _)) => z == in1 })
      assert(in1UsedDirectly)
    }

    // Fuse with p2
    {
      val p2 = Param("p2", -1)(TyStm((U8, U8), 4))
      val fused = original.fuseWith(p2)
      // (Correct behaviour)
      val actual =
        mhir.eval.eval(fused, inputs = Map(in1 -> in1Val, in2 -> in2Val))
      assert(actual == expected)
      // (Successful fusion)
      assert(fused.producers.size == 2)
      val in2UsedDirectly = fused.producers
        .exists({ case (_, (z, _, _)) => z == in2 })
      assert(in2UsedDirectly)
    }
  }

  test("Filter") {
    val in1 = Param("in_stm_1", -1)(TyStm(U8, 8))
    val in2 = Param("in_stm_2", -1)(TyStm(U8, 8))
    val original = Parser
      .parse(
        """/* StmConcat */
          |sbuild(8)(if i == 4:u8 then sdata(p2) else sdata(p1), true) {
          |  (i: u8) = {
          |    init: 0:u8,
          |    next: if i == 4:u8 then 4:u8 else i + 1
          |  }
          |} {
          |  /* Only even inputs, along with the sum of all previous elements */
          |  (p1: Stm[(u8, u8), 4]) = {
          |    stm: sbuild(4)((sum, sdata(p)), sdata(p) % 2 == 0:u8) {
          |      (sum: u8) = {
          |        init: 0:u8,
          |        next: sum + sdata(p)
          |      }
          |    } {
          |      (p: Stm[u8, 8]) = {
          |        stm: in_stm_1,
          |        ready: true
          |      }
          |    },
          |    ready: i != 4:u8
          |  },
          |  /* Only odd inputs, along with their index */
          |  (p2: Stm[(u8, u8), 4]) = {
          |    stm: sbuild(4)((i, sdata(p)), sdata(p) % 2:u8 != 0:u8) {
          |      (i: u8) = {
          |        init: 0:u8,
          |        next: i + 1
          |      }
          |    } {
          |      (p: Stm[u8, 8]) = {
          |        stm: in_stm_2,
          |        ready: true
          |      }
          |    },
          |    ready: i == 4:u8
          |  }
          |}
          |""".stripMargin
      )
      .body
      .tchk(Map(in1 -> in1.typ, in2 -> in2.typ), Map())
      .lower
      .asInstanceOf[StmBuild]

    // Check that this code works the way I expect
    val in1Val = StmLiteral(
      C(1)(U8),
      C(2)(U8),
      C(3)(U8),
      C(4)(U8),
      C(6)(U8), // deliberately swapped
      C(5)(U8), // deliberately swapped
      C(7)(U8),
      C(8)(U8)
    )().tchk()
    val in2Val = StmLiteral(
      C(1)(U8),
      C(2)(U8),
      C(33)(U8),
      C(4)(U8),
      C(26)(U8),
      C(15)(U8),
      C(7)(U8),
      C(8)(U8)
    )().tchk()
    val expected = StmLiteral(
      Tuple(C(1)(U8), C(2)(U8))(),
      Tuple(C(6)(U8), C(4)(U8))(),
      Tuple(C(10)(U8), C(6)(U8))(),
      Tuple(C(28)(U8), C(8)(U8))(),
      Tuple(C(0)(U8), C(1)(U8))(),
      Tuple(C(2)(U8), C(33)(U8))(),
      Tuple(C(5)(U8), C(15)(U8))(),
      Tuple(C(6)(U8), C(7)(U8))()
    )().tchk()
    val actual0 =
      mhir.eval.eval(original, inputs = Map(in1 -> in1Val, in2 -> in2Val))
    assert(actual0 == expected)

    // Fuse with p1
    {
      val p1 = Param("p1", -1)(TyStm((U8, U8), 4))
      val fused = original.fuseWith(p1)
      // (Correct behaviour)
      val actual =
        mhir.eval.eval(fused, inputs = Map(in1 -> in1Val, in2 -> in2Val))
      assert(actual == expected)
      // (Successful fusion)
      assert(fused.producers.size == 2)
      val in1UsedDirectly = fused.producers
        .exists({ case (_, (z, _, _)) => z == in1 })
      assert(in1UsedDirectly)
    }

    // Fuse with p2
    {
      val p2 = Param("p2", -1)(TyStm((U8, U8), 4))
      val fused = original.fuseWith(p2)
      // (Correct behaviour)
      val actual =
        mhir.eval.eval(fused, inputs = Map(in1 -> in1Val, in2 -> in2Val))
      assert(actual == expected)
      // (Successful fusion)
      assert(fused.producers.size == 2)
      val in2UsedDirectly = fused.producers
        .exists({ case (_, (z, _, _)) => z == in2 })
      assert(in2UsedDirectly)
    }
  }

  test("NoHandshake") {
    def foo(s1: Expr, s2: Expr): StmBuild = {
      val TyStm(elemTyp, IntCst(n)) = s1.typ
      require(n >= 3)
      val p1 = Param("p1")(TyStm(elemTyp, -1))
      val p2 = Param("p2")(TyStm(elemTyp, -1))
      val i = Param("i")(U8)
      StmBuild(
        C(n - 3)(),
        C(4)(),
        Undefined(Missing),
        Tuple(i, StmData(p1)(), StmData(p2)())(),
        True,
        Map(
          i -> (C(0)(i.typ), Sum(C(1)(i.typ), i)(), C(3)())
        ),
        Map(
          p1 -> (s1, True, C(0)()),
          p2 -> (s2, True, C(2)())
        )
      )().tchk().asInstanceOf[StmBuild]
    }
    def delayBy(s: StmLiteral, k: Int): StmLiteral = {
      val TyStm(elemTyp, _) = s.typ
      StmLiteral(
        (0 until k).map(_ => Undefined(elemTyp)) ++ s.physical,
        s.logical
      )(Missing).tchk().asInstanceOf[StmLiteral]
    }
    val n = 10
    val s1 = Param("s1")(TyStm(U8, n))
    val s2 = Param("s2")(TyStm(U8, n))
    val s3 = Param("s3")(TyStm(U8, n))
    val s4 = Param("s4")(TyStm(U8, n))
    val original = foo(foo(s1, s2), foo(s3, s4))
    val Seq(p1, p2) = original.producers.keys.toSeq.sortBy(_.name)
    val inputs = Map(
      s1 -> StmLiteral(
        Seq(),
        (0 until n).map(C(_)(U8))
      )(Missing).tchk().asInstanceOf[StmLiteral],
      s2 -> StmLiteral(
        (0 until 2).map(_ => Undefined(U8)),
        (0 until n).map(t => t * t).map(C(_)(U8))
      )(Missing).tchk().asInstanceOf[StmLiteral],
      s3 -> StmLiteral(
        (0 until 2).map(_ => Undefined(U8)),
        (0 until n).map(42 - _).map(C(_)(U8))
      )(Missing).tchk().asInstanceOf[StmLiteral],
      s4 -> StmLiteral(
        (0 until 4).map(_ => Undefined(U8)),
        (0 until n).map(t => t * (t + 1)).map(C(_)(U8))
      )(Missing).tchk().asInstanceOf[StmLiteral]
    )

    // Check that it works as expected in the first place
    val expected = StmLiteral(
      Seq(),
      //          s1: [ 0,  1,  2,  3,  4,  5,  6,  7,  8,  9]s
      //          s2: [ 0,  1,  4,  9, 16, 25, 36, 49, 64, 81]s
      // foo(s1, s2): [(1, 3, 1), (2, 4, 4), (3, 5, 9), (4, 6, 16), (5, 7, 25), (6, 8, 36), (7, 9, 49)]s
      //          s3: [42, 41, 40, 39, 38, 37, 36, 35, 34, 33]s
      //          s4: [ 0,  2,  6, 12, 20, 30, 42, 56, 72, 90]s
      // foo(s3, s4): [(1, 39, 2), (2, 38, 6), (3, 37, 12), (4, 36, 20), (5, 35, 30), (6, 34, 42), (7, 33, 56)]s
      //      output: [(1, (4, 6, 16), (2, 38, 6)), (2, (5, 7, 25), (3, 37, 12)), (3, (6, 8, 36), (4, 36, 20)), (4, (7, 9, 49), (5, 35, 30))]s
      Seq(
        (1, (4, 6, 16), (2, 38, 6)),
        (2, (5, 7, 25), (3, 37, 12)),
        (3, (6, 8, 36), (4, 36, 20)),
        (4, (7, 9, 49), (5, 35, 30))
      ).map({ case (a, (b1, b2, b3), (c1, c2, c3)) =>
        Tuple(
          C(a)(U8),
          Tuple(C(b1)(U8), C(b2)(U8), C(b3)(U8))(),
          Tuple(C(c1)(U8), C(c2)(U8), C(c3)(U8))()
        )()
      })
    )(Missing).tchk()

    {
      val actual = mhir.eval.eval(original, handshake = false, inputs = inputs)
      assert(actual.dropPhysicalPrefix == expected)
    }

    // Check that it works as expected after fusion with p1
    {
      val fused = original.fuseWith(p1)
      // Need to increase delay in s1 and s2 to compensate for stage removed by fusion
      val updatedInputs = Map(
        s1 -> delayBy(inputs(s1), 1),
        s2 -> delayBy(inputs(s2), 1),
        s3 -> inputs(s3),
        s4 -> inputs(s4)
      )
      val actual =
        mhir.eval.eval(fused, handshake = false, inputs = updatedInputs)
      assert(actual.dropPhysicalPrefix == expected)
    }

    // Check that it works as expected after fusion with p2
    {
      val fused = original.fuseWith(p2)
      // Need to increase delay in s3 and s4 to compensate for stage removed by fusion
      val updatedInputs = Map(
        s1 -> inputs(s1),
        s2 -> inputs(s2),
        s3 -> delayBy(inputs(s3), 1),
        s4 -> delayBy(inputs(s4), 1)
      )
      val actual =
        mhir.eval.eval(fused, handshake = false, inputs = updatedInputs)
      assert(actual.dropPhysicalPrefix == expected)
    }

    // Check that it works as expected after fusion with p1 and p2
    {
      val fused = original.fuseWith(p1).fuseWith(p2)
      val actual = mhir.eval.eval(fused, handshake = false, inputs = inputs)
      assert(actual.dropPhysicalPrefix == expected)
    }
  }
}
