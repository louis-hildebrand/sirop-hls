package mhir.optimize

import mhir.canonicalize._
import mhir.eval.{CycleCounter, IllegalBackpressure}
import mhir.ir._
import mhir.sugar._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class LatencyMatcherTests extends AnyFunSuite {

  private val passWithHandshake = {
    LatencyMatcher(new LatencyAnalysis(handshake = true))
  }
  private val passWithoutHandshake = {
    LatencyMatcher(new LatencyAnalysis(handshake = false))
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
          buf,
          i === C(0)(U8),
          Map[Param, (Expr, Expr)](
            s -> (zip, i === C(0)(U8)),
            i -> (
              C(0)(U8),
              Mux(
                i === 0,
                // Even elements get delayed for longer
                Mux(StmData(s)().__0 % 2 === 0, C(2)(U8), C(1)(U8))(),
                ToUnsigned(i - 1)()
              )()
            ),
            buf -> (
              AllZero((U8, U8)).lower,
              Mux(i === C(0)(U8), StmData(s)(), buf)()
            )
          )
        )()
      }
      LetStm(1, s, count, delay)().tchk().lower
    }
    val optimized = passWithHandshake.matchLatencies(original)

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
    val optimized = passWithHandshake.matchLatencies(original)

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

  test("ForkTwice:NoHandshake") {
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
    val optimized = passWithoutHandshake.matchLatencies(original)

    // Correct behaviour
    val expectedVal =
      StmLiteral(
        (0 until n)
          .map(t => Tuple(C(t)(U8), C(t + 5)(U8), C(2 * (t + 5))(U8))()): _*
      )().tchk()
    val actualVal = mhir.eval.eval(optimized, handshake = false)
    assert(actualVal == expectedVal)

    // Effective optimization
    // (Cycle count should be decreased due to improved initiation interval)
    val optimizedCycleCount =
      CycleCounter.count(optimized, handshake = false).get
    assert(optimizedCycleCount == 13)
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
          buf,
          i === 2,
          Map[Param, (Expr, Expr)](
            sAcc -> (s, i === 0),
            i -> (C(0)(U8), Mux(i === 2, C(0)(U8), Sum(C(1)(U8), i)())()),
            buf -> (C(0)(U8), Mux(i === 0, StmData(sAcc)(), buf)())
          )
        )().tchk()
      }
      val zip = SimpleZip(delay, plusOne)
      LetStm(1, s, count, zip)().tchk().lower
    }
    val optimized = passWithHandshake.matchLatencies(original)

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
        i,
        True,
        Map[Param, (Expr, Expr)](
          i -> (C(0)(U8), Sum(C(1)(U8), i)())
        )
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
          acc + StmData(s)(),
          t === C(m - 1)(U8),
          Map[Param, (Expr, Expr)](
            s -> (x, True),
            acc -> (C(0)(U8), acc + StmData(s)()),
            t -> (C(0)(U8), Mux(t === C(m - 1)(U8), C(0)(U8), C(1)(U8) + t)())
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
          VecShiftLeft(acc, StmData(s)())(),
          t === C(m - 1)(U8),
          Map[Param, (Expr, Expr)](
            s -> (x, True),
            acc -> (
              VecBuild(m, U8 ::+ (_ => AllZero(U8)))(),
              VecShiftLeft(acc, StmData(s)())()
            ),
            t -> (C(0)(U8), Mux(t === C(m - 1)(U8), C(0)(U8), C(1)(U8) + t)())
          )
        )().tchk()
      }
      val zipped = SimpleZip(sumPlusFive, stm2Vec)
      LetStm(1, x, count, zipped)().tchk().lower
    }
    val optimized = passWithHandshake.matchLatencies(original)

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
    val optimized = passWithHandshake.matchLatencies(original)

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
      val concat = SimpleConcat(s0, SimpleMap(s0, x => Sum(C(5)(U8), x)()))
      LetStm(n, s0, count, concat)().tchk().lower
    }
    val optimized = passWithHandshake.matchLatencies(original)

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

  test("AcceleratorInputs") {
    val n = 5
    val a = Param("a")(TyStm(U8, n))
    val b = Param("b")(TyStm(I8, n))
    val original = Function(
      a,
      Function(b, SimpleZip(a, SimpleMap(b, x => Sum(x, C(-5)(I8))())))()
    )().tchk()
    val optimized = passWithoutHandshake.matchLatencies(original)

    val inputs = Map[Expr, Expr](
      a -> StmRange(n, C(0)(U8), C(1)(U8))().tchk().lower,
      b -> StmRange(n, C(-2)(I8), C(1)(I8))().tchk().lower
    )
    // Before latency matching, evaluation fails because StmZip will try to
    // apply backpressure
    val originalWithInputs = original
      .asInstanceOf[Function]
      .body
      .asInstanceOf[Function]
      .body
      .subPreserveType(inputs)
    assertThrows[IllegalBackpressure.type](
      mhir.eval.eval(originalWithInputs, handshake = false)
    )
    // After latency matching, evaluation should succeed
    val optimizedWithInputs = optimized
      .asInstanceOf[Function]
      .body
      .asInstanceOf[Function]
      .body
      .subPreserveType(inputs)
    val actual = mhir.eval.eval(optimizedWithInputs, handshake = false)
    val expected =
      StmLiteral(
        (0 until n).map(t => Tuple(C(t)(U8), C(t - 2 - 5)(I8))()): _*
      )().tchk()
    assert(actual == expected)
  }
}
