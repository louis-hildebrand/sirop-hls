package mhir.optimize

import mhir.canonicalize._
import mhir.eval.{CycleCounter, DelayMismatch}
import mhir.ir._
import mhir.sugar._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class LatencyMatcherTests extends AnyFunSuite {

  private val passWithHandshake = {
    LatencyMatcher(new LatencyAnalysis(handshake = true), handshake = true)
  }
  private val passWithoutHandshake = {
    LatencyMatcher(new LatencyAnalysis(handshake = false), handshake = false)
  }

  test("let s = ... in Dynamic(StmZip(s, s |> StmMap(+5) |> StmMap(*2)))") {
    val n = 16
    val original = {
      val count = SimpleCount(C(n)(U8))
      val s = Param("s")(TyStm(U8, n))
      val mapOnce = SimpleMap(s, x => Sum(C(1)(U8), x)())
      val mapTwice = SimpleMap(mapOnce, x => Prod(C(2)(U8), x)())
      val mapThrice = SimpleMap(mapTwice, x => Sum(C(1)(U8), x)())
      val mapFourTimes = SimpleMap(mapThrice, x => Sum(C(2)(U8), x)())
      val mapFiveTimes = SimpleMap(mapFourTimes, x => Sum(C(3)(U8), x)())
      val zip = SimpleZip(s, mapFiveTimes)
      val delay = {
        // The latency through this node cannot be predicted statically, since
        // it depends on the inputs.
        // Nevertheless, this should not hinder latency matching because this
        // delaying comes after the join.
        val s = Param("s")(TyStm((U8, U8), -1))
        val i = Param("i")(U8)
        val buf = Param("buf")((U8, U8))
        StmBuild(
          n,
          Tuple()(),
          Undefined(Missing),
          buf,
          i === C(0)(U8),
          Map[Param, (Expr, Expr, Expr)](
            i -> (
              C(0)(U8),
              Mux(
                i === 0,
                // Even elements get delayed for longer
                Mux(StmData(s)().__0 % 2 === 0, C(2)(U8), C(1)(U8))(),
                ToUnsigned(i - 1)()
              )(),
              Tuple()()
            ),
            buf -> (
              AllZero((U8, U8)).lower,
              Mux(i === C(0)(U8), StmData(s)(), buf)(),
              Tuple()()
            )
          ),
          Map[Param, (Expr, Expr, Expr)](
            s -> (zip, i === C(0)(U8), Tuple()())
          )
        )()
      }
      LetStm(1, s, count, delay)().tchk().lower
    }
    val optimized =
      passWithHandshake.matchLatencies(original, headByVar = Map())

    // Correct behaviour
    val expectedVal = mhir.eval.eval(original)
    val actualVal = mhir.eval.eval(optimized)
    assert(actualVal == expectedVal)

    // Effective optimization
    // (Cycle count should be decreased due to improved initiation interval)
    val originalCount = CycleCounter.count(original, handshake = true).get
    val optimizedCount = CycleCounter.count(optimized, handshake = true).get
    assert(optimizedCount < originalCount)
  }

  test("ForkTwice") {
    val n = 10
    val original = {
      val sA = Param("s_a")(TyStm(U8, n))
      val sB = Param("s_b")(TyStm(U8, n))
      val count = SimpleCount(C(n)(U8))
      val plusFive = SimpleMap(sA, x => Sum(C(5)(U8), x)())
      val timesTwo = SimpleMap(sB, x => Prod(C(2)(U8), x)())
      val zip = SimpleZip(sA, sB, timesTwo)
      LetStm(1, sA, count, LetStm(1, sB, plusFive, zip)())().tchk().lower
    }
    val optimized =
      passWithHandshake.matchLatencies(original, headByVar = Map())

    // Correct behaviour
    val expectedVal = mhir.eval.eval(original)
    val actualVal = mhir.eval.eval(optimized)
    assert(actualVal == expectedVal)

    // Effective optimization
    // (Cycle count should be decreased due to improved initiation interval)
    val originalCycleCount = CycleCounter.count(original, handshake = true).get
    val optimizedCycleCount =
      CycleCounter.count(optimized, handshake = true).get
    assert(optimizedCycleCount < originalCycleCount)
    assert(optimizedCycleCount == 17)
  }

  /** Suppose that one branch has a sequence of three [[mhir.ir.StmBuild]]s,
    * each with an initiation interval of 1, and the other branch has one
    * [[mhir.ir.StmBuild]] with an initiation interval of 3. I don't think
    * adding extra registers on the latter branch will help (the
    * [[mhir.ir.StmBuild]] with the large initiation interval is the bottleneck)
    * and it will needlessly increase the resource usage.
    */
  test("AlreadyMatchingLatency") {
    val n = 7
    val original = {
      val count = SimpleCount(C(n)(U8))
      val s = Param("s")(TyStm(U8, n))
      val plusFive = SimpleMap(s, x => Sum(C(5)(U8), x)())
      val timesTwo = SimpleMap(plusFive, x => Prod(C(2)(U8), x)())
      val plusOne = SimpleMap(timesTwo, x => Sum(C(1)(U8), x)())
      val delay = {
        val i = Param("i")(U8)
        val sAcc = Param("s")(TyStm(U8, -1))
        val buf = Param("buf")(U8)
        StmBuild(
          n,
          Tuple()(),
          Undefined(Missing),
          buf,
          i === 2,
          Map[Param, (Expr, Expr, Expr)](
            i -> (
              C(0)(U8),
              Mux(i === 2, C(0)(U8), Sum(C(1)(U8), i)())(),
              Tuple()()
            ),
            buf -> (C(0)(U8), Mux(i === 0, StmData(sAcc)(), buf)(), Tuple()())
          ),
          Map[Param, (Expr, Expr, Expr)](
            sAcc -> (s, i === 0, Tuple()())
          )
        )().tchk()
      }
      val zip = SimpleZip(delay, plusOne)
      LetStm(1, s, count, zip)().tchk().lower
    }
    val optimized =
      passWithHandshake.matchLatencies(original, headByVar = Map())

    // Correct behaviour
    val expectedVal = mhir.eval.eval(original)
    val actualVal = mhir.eval.eval(optimized)
    assert(actualVal == expectedVal)

    // Non-pessimization
    // (Cycle count should not be increased)
    assert(
      CycleCounter.count(optimized, handshake = true)
        == CycleCounter.count(original, handshake = true)
    )
  }

  test("Reduction") {
    val n = 4
    val m = 3
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
      )()
    }
    val original = {
      val x = Param("x")(TyStm(U8, n * m))
      val sum = {
        val s = Param("s")(TyStm(U8, -1))
        val acc = Param("acc")(U8)
        val t = Param("t")(U8)
        StmBuild(
          n,
          Tuple()(),
          Undefined(Missing),
          acc + StmData(s)(),
          t === C(m - 1)(U8),
          Map[Param, (Expr, Expr, Expr)](
            acc -> (C(0)(U8), acc + StmData(s)(), Tuple()()),
            t -> (
              C(0)(U8),
              Mux(t === C(m - 1)(U8), C(0)(U8), C(1)(U8) + t)(),
              Tuple()()
            )
          ),
          Map[Param, (Expr, Expr, Expr)](
            s -> (x, True, Tuple()())
          )
        )().tchk()
      }
      val sumPlusFive = SimpleMap(sum, x => Sum(C(5)(U8), x)())
      val stm2Vec = {
        val s = Param("s")(TyStm(U8, -1))
        val acc = Param("acc")(TyVec(U8, m))
        val t = Param("t")(U8)
        StmBuild(
          n,
          Tuple()(),
          Undefined(Missing),
          VecShiftLeft(acc, StmData(s)())(),
          t === C(m - 1)(U8),
          Map[Param, (Expr, Expr, Expr)](
            acc -> (
              VecBuild(m, U8 ::+ (_ => AllZero(U8)))(),
              VecShiftLeft(acc, StmData(s)())(),
              Tuple()()
            ),
            t -> (
              C(0)(U8),
              Mux(t === C(m - 1)(U8), C(0)(U8), C(1)(U8) + t)(),
              Tuple()()
            )
          ),
          Map[Param, (Expr, Expr, Expr)](
            s -> (x, True, Tuple()())
          )
        )().tchk()
      }
      val zipped = SimpleZip(sumPlusFive, stm2Vec)
      LetStm(1, x, count, zipped)().tchk().lower
    }
    val optimized =
      passWithHandshake.matchLatencies(original, headByVar = Map())

    // Correct behaviour
    val originalVal = mhir.eval.eval(original)
    val actualVal = mhir.eval.eval(optimized)
    assert(actualVal == originalVal)

    // Effective optimization
    // (Cycle count should be decreased due to improved initiation interval)
    val originalCount = CycleCounter.count(original, handshake = true).get
    val optimizedCount = CycleCounter.count(optimized, handshake = true).get
    assert(optimizedCount < originalCount)
  }

  test("NestedLetStm") {
    val n = 5
    val original = {
      val s0 = Param("s0")(TyStm(U8, n))
      val s1 = Param("s1")(TyStm((U8, U8), n))
      val count = SimpleCount(C(n)(U8))
      val plusFive = SimpleMap(s0, x => Sum(C(5)(U8), x)())
      val zip = SimpleZip(s0, plusFive)
      LetStm(1, s1, LetStm(1, s0, count, zip)(), s1)().tchk().lower
    }
    val optimized =
      passWithHandshake.matchLatencies(original, headByVar = Map())

    // Correct behaviour
    val originalVal = mhir.eval.eval(original)
    val actualVal = mhir.eval.eval(optimized)
    assert(actualVal == originalVal)

    // Effective optimization
    // (Cycle count should be decreased due to improved initiation interval)
    val originalCount = CycleCounter.count(original, handshake = true).get
    val optimizedCount = CycleCounter.count(optimized, handshake = true).get
    assert(optimizedCount < originalCount)
  }

  test("StmConcat") {
    val n = 5
    val original = {
      val s0 = Param("s0")(TyStm(U8, n))
      val count = SimpleCount(C(n)(U8))
      val concat =
        SimpleConcatHandshake(s0, SimpleMap(s0, x => Sum(C(5)(U8), x)()))
      LetStm(n, s0, count, concat)().tchk().lower
    }
    val optimized =
      passWithHandshake.matchLatencies(original, headByVar = Map())

    // Correct behaviour
    val originalVal = mhir.eval.eval(original)
    val actualVal = mhir.eval.eval(optimized)
    assert(actualVal == originalVal)

    // Non-pessimization
    // (Cycle count should not get worse)
    val originalCount = CycleCounter.count(original, handshake = true).get
    val optimizedCount = CycleCounter.count(optimized, handshake = true).get
    assert(optimizedCount <= originalCount)
  }

  test("NoHandshake:ForkTwice") {
    val n = 10
    val sA = Param("s_a")(TyStm(U8, n))
    val sB = Param("s_b")(TyStm(U8, n))
    val original = {
      val count = SimpleCount(C(n)(U8))
      val plusFive = SimpleMap(sA, x => Sum(C(5)(U8), x)())
      val timesTwo = SimpleMap(sB, x => Prod(C(2)(U8), x)())
      val zip = SimpleZip(sA, sB, timesTwo)
      LetStm(1, sA, count, LetStm(1, sB, plusFive, zip)())().tchk().lower
    }
    val optimized = passWithoutHandshake.matchLatencies(
      original,
      headByVar = Map(sA -> Undefined(Missing), sB -> Undefined(Missing))
    )

    // Correct behaviour
    val expectedVal = StmLiteral(
      (0 until n)
        .map(t => Tuple(C(t)(U8), C(t + 5)(U8), C(2 * (t + 5))(U8))()): _*
    )(Missing).tchk()
    val actualVal =
      mhir.eval.eval(optimized, handshake = false).asInstanceOf[StmLiteral]
    assert(actualVal.dropPhysical(4) == expectedVal)

    // Effective optimization
    // (Cycle count should be decreased due to improved initiation interval)
    val optimizedCycleCount =
      CycleCounter.count(optimized, handshake = false).get
    assert(optimizedCycleCount == 13)
  }

  test("NoHandshake:TakeAndDrop") {
    val n = 8
    val k = 3
    // EXAMPLE: [u]s ++ [42, 43, 44, 45, 46, 47, 48, 49]s
    val input = Param("input")(TyStm(U8, n))
    val original = {
      val take = {
        // EXAMPLE: [u, u]s ++ [42, 43, 44, 45, 46]s
        val p = Param("p")(TyStm(U8, -1))
        StmBuild(
          n - k,
          C(1)(),
          Undefined(Missing),
          StmData(p)(),
          True,
          Map(),
          Map(p -> (input, True, C(0)()))
        )().tchk()
      }
      val drop = {
        // EXAMPLE: [u, u, 42, 43, 44]s ++ [45, 46, 47, 48, 49]s
        val p = Param("p")(TyStm(U8, -1))
        StmBuild(
          n - k,
          C(1 + k)(),
          Undefined(Missing),
          StmData(p)(),
          True,
          Map(),
          Map(p -> (input, True, C(0)()))
        )().tchk()
      }
      val zip = {
        // EXAMPLE (with latency matching):
        // take: [u, u,  u,  u,  u]s ++ [42, 43, 44, 45, 46]s
        // drop: [u, u, 42, 43, 44]s ++ [45, 46, 47, 48, 49]s
        //  zip: [u, (u, u), (u, u), (u, 42), (u, 43), (u, 44)]s ++ [(42, 45), (43, 46), (44, 47), (45, 48), (46, 49)]s
        val p1 = Param("p1")(TyStm(U8, -1))
        val p2 = Param("p2")(TyStm(U8, -1))
        StmBuild(
          n - k,
          C(1)(),
          Undefined(Missing),
          Tuple(StmData(p1)(), StmData(p2)())(),
          True,
          Map(),
          Map(
            p1 -> (take, True, C(0)()),
            p2 -> (drop, True, C(0)())
          )
        )().tchk()
      }
      Function(input, zip)().tchk()
    }
    val actual =
      passWithoutHandshake.matchLatencies(original, headByVar = Map())

    val inputs = Map(
      input -> StmRange(n, C(42)(U8), C(1)(U8))().tchk().lower
    )

    // There should be a latency mismatch at first
    assertThrows[DelayMismatch](
      mhir.eval.eval(
        original.asInstanceOf[Function].body,
        handshake = false,
        inputs = inputs
      )
    )

    // There should NOT be a latency mismatch afterwards
    val expectedVal = StmLiteral(
      Seq(
        Undefined(TyTuple(U8, U8)),
        Tuple(Undefined(U8), Undefined(U8))(),
        Tuple(Undefined(U8), Undefined(U8))(),
        Tuple(Undefined(U8), C(42)(U8))(),
        Tuple(Undefined(U8), C(43)(U8))(),
        Tuple(Undefined(U8), C(44)(U8))()
      ),
      Seq(
        Tuple(C(42)(U8), C(45)(U8))(),
        Tuple(C(43)(U8), C(46)(U8))(),
        Tuple(C(44)(U8), C(47)(U8))(),
        Tuple(C(45)(U8), C(48)(U8))(),
        Tuple(C(46)(U8), C(49)(U8))()
      )
    )(Missing).tchk()
    val actualVal = mhir.eval.eval(
      actual.asInstanceOf[Function].body,
      handshake = false,
      inputs = inputs
    )
    assert(actualVal == expectedVal)
  }

  test("PreserveInitData:FromStmBuild") {
    val n = 5
    val original @ Function(input1, Function(input2, originalBody)) = {
      val input1 = Param("input1")(TyStm(U16, n))
      val input2 = Param("input1")(TyStm(TyBool, n))
      val power4 = SimpleMap(
        SimpleMap(input1, x => Prod(x, x)()).tchk(),
        x => Prod(x, x)()
      ).tchk()
      val nop = {
        val p = Param("p")(TyStm(TyBool, -1))
        StmBuild(
          n,
          C(1)(),
          False,
          StmData(p)(),
          True,
          Map(),
          Map(p -> (input2, True, C(0)()))
        )().tchk()
      }
      val zip = {
        val p1 = Param("p1")(TyStm(U16, -1))
        val p2 = Param("p2")(TyStm(TyBool, -1))
        StmBuild(
          n,
          C(1)(),
          Tuple(Undefined(U16), False)(),
          Tuple(StmData(p1)(), StmData(p2)())(),
          True,
          Map(),
          Map(
            p1 -> (power4, True, C(0)()),
            p2 -> (nop, True, C(0)())
          )
        )().tchk()
      }
      Function(input1, Function(input2, zip)())().tchk()
    }
    val Function(_, Function(_, actualBody)) =
      passWithoutHandshake.matchLatencies(original, headByVar = Map())

    val inputs = Map(
      input1 -> StmRange(n, C(1)(U16), C(1)(U16))().tchk().lower,
      input2 -> StmLiteral(
        Seq(False),
        (0 until n).map(_ % 2 == 0).map(if (_) True else False)
      )(Missing).tchk()
    )

    // There should be a latency mismatch at first
    assertThrows[DelayMismatch](
      mhir.eval.eval(originalBody, handshake = false, inputs = inputs)
    )

    // There should NOT be a latency mismatch afterwards
    val expectedVal = StmLiteral(
      Seq(
        Tuple(Undefined(U16), False)(),
        Tuple(Undefined(U16), False)(),
        Tuple(Undefined(U16), False)(),
        Tuple(Undefined(U16), False)()
      ),
      Seq(
        Tuple(C(1)(U16), True)(),
        Tuple(C(16)(U16), False)(),
        Tuple(C(81)(U16), True)(),
        Tuple(C(256)(U16), False)(),
        Tuple(C(625)(U16), True)()
      )
    )(Missing).tchk()
    val actualVal =
      mhir.eval.eval(actualBody, handshake = false, inputs = inputs)
    assert(actualVal == expectedVal)
  }

  test("PreserveInitData:FromInput") {
    val n = 5
    val original @ Function(input1, Function(input2, originalBody)) = {
      val input1 = Param("input1")(TyStm(U16, n))
      val input2 = Param("input1")(TyStm(TyBool, n))
      val power4 = SimpleMap(
        SimpleMap(input1, x => Prod(x, x)()).tchk(),
        x => Prod(x, x)()
      ).tchk()
      val zip = {
        val p1 = Param("p1")(TyStm(U16, -1))
        val p2 = Param("p2")(TyStm(TyBool, -1))
        StmBuild(
          n,
          C(1)(),
          Tuple(Undefined(U16), False)(),
          Tuple(StmData(p1)(), StmData(p2)())(),
          True,
          Map(),
          Map(
            p1 -> (power4, True, C(0)()),
            p2 -> (input2, True, C(0)())
          )
        )().tchk()
      }
      Function(input1, Function(input2, zip)())().tchk()
    }
    val Function(_, Function(_, actualBody)) =
      passWithoutHandshake.matchLatencies(
        original,
        headByVar = Map(input2 -> False)
      )

    val inputs = Map(
      input1 -> StmRange(n, C(1)(U16), C(1)(U16))().tchk().lower,
      input2 -> StmLiteral(
        Seq(False),
        (0 until n).map(_ % 2 == 0).map(if (_) True else False)
      )(Missing).tchk()
    )

    // There should be a latency mismatch at first
    assertThrows[DelayMismatch](
      mhir.eval.eval(originalBody, handshake = false, inputs = inputs)
    )

    // There should NOT be a latency mismatch afterwards
    val expectedVal = StmLiteral(
      Seq(
        Tuple(Undefined(U16), False)(),
        Tuple(Undefined(U16), False)(),
        Tuple(Undefined(U16), False)(),
        Tuple(Undefined(U16), False)()
      ),
      Seq(
        Tuple(C(1)(U16), True)(),
        Tuple(C(16)(U16), False)(),
        Tuple(C(81)(U16), True)(),
        Tuple(C(256)(U16), False)(),
        Tuple(C(625)(U16), True)()
      )
    )(Missing).tchk()
    val actualVal =
      mhir.eval.eval(actualBody, handshake = false, inputs = inputs)
    assert(actualVal == expectedVal)
  }
}
