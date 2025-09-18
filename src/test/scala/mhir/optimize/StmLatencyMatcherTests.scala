package mhir.optimize

import mhir.ir.Lowering.ExprLowering
import mhir.ir._
import mhir.ir.evaluate.CycleCounter
import mhir.ir.typecheck.TypeCheck
import mhir.sugar.VecShiftLeft
import org.scalatest.funsuite.AnyFunSuite

class StmLatencyMatcherTests extends AnyFunSuite {

  private val pass = StmLatencyMatcher(assumeThroughputsMatch = true)

  private def assertAllBufSizesAreOne(e: Expr): Unit = {
    e match {
      case LetStm(bufSize, _, _, _) =>
        assert(bufSize == C(1)())
      case e => e.children.foreach(assertAllBufSizesAreOne)
    }
  }

  // TODO: Make a separate file with these implementations?

  private def simpleCount(n: IntCst): Expr = {
    val i = Param("i")(n.typ)
    StmBuild(
      n,
      i,
      True,
      Map[Param, (Expr, Expr)](i -> (C(0)(n.typ), Sum(C(1)(n.typ), i)()))
    )().tchk()
  }

  private def simpleMap(input: Expr, f: Expr => Expr): Expr = {
    val TyStm(t, n) = input.typ
    val sAcc = Param("s")(TyStm(t, -1))
    StmBuild(
      n,
      f(StmData(sAcc)()),
      True,
      Map[Param, (Expr, Expr)](
        sAcc -> (input, True)
      )
    )().tchk()
  }

  private def simpleZip(in0: Expr, in1: Expr): Expr = {
    val TyStm(t0, n) = in0.typ
    val TyStm(t1, _) = in1.typ
    val s0 = Param("s0")(TyStm(t0, -1))
    val s1 = Param("s1")(TyStm(t1, -1))
    StmBuild(
      n,
      Tuple(StmData(s0)(), StmData(s1)())(),
      True,
      Map[Param, (Expr, Expr)](
        s0 -> (in0, True),
        s1 -> (in1, True)
      )
    )().tchk()
  }

  /** The condition that all branches have the same throughput is necessary for
    * shrinking the buffers in letstm. For example, if one branch is just taking
    * the prefix while the other is computing row sums, then a buffer size of 1
    * is insufficient.
    */
  test("MismatchedThroughputs") {
    val n = 8
    val m = 4
    val original = {
      // let x = StmSplit(StmCount(n*m), m) in
      // StmZip(StmPrefix(x, n), StmMap(x, row => StmReduce(row, +)))
      val x = Param("s")(TyStm(U8, n * m))
      val count = simpleCount(C(n * m)(U8))
      val prefix = {
        val s = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          StmData(s)(),
          True,
          Map[Param, (Expr, Expr)](s -> (x, True))
        )().tchk()
      }
      val rowSums = {
        val s = Param("s")(TyStm(U8, -1))
        val i = Param("i")(U8)
        val acc = Param("acc")(U8)
        StmBuild(
          n,
          Sum(StmData(s)(), acc)(),
          i equ C(m - 1)(U8),
          Map[Param, (Expr, Expr)](
            s -> (x, True),
            i -> (
              C(0)(U8),
              Mux(i equ C(m - 1)(U8), C(0)(U8), Sum(C(1)(U8), i)())()
            ),
            acc -> (
              C(0)(U8),
              Mux(i equ C(m - 1)(U8), C(0)(U8), Sum(StmData(s)(), acc)())()
            )
          )
        )().tchk()
      }
      val zip = simpleZip(prefix, rowSums)
      LetStm(n * m, x, count, zip)().tchk().lower()
    }
    val optimized =
      StmLatencyMatcher(assumeThroughputsMatch = false).matchLatencies(original)

    // Correct behaviour
    val expectedVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == expectedVal)
  }

  test("let s = ... in Dynamic(StmZip(s, s |> StmMap(+5) |> StmMap(*2)))") {
    val n = 16
    val original = {
      val count = {
        val i = Param("i")(U8)
        StmBuild(
          n,
          i,
          True,
          Map[Param, (Expr, Expr)](
            i -> (C(0)(U8), Sum(C(1)(U8), i)())
          )
        )()
      }
      val s = Param("s")(TyStm(U8, n))
      val mapOnce = simpleMap(s, x => Sum(C(1)(U8), x)())
      val mapTwice = simpleMap(mapOnce, x => Prod(C(2)(U8), x)())
      val mapThrice = simpleMap(mapTwice, x => Sum(C(1)(U8), x)())
      val mapFourTimes = simpleMap(mapThrice, x => Sum(C(2)(U8), x)())
      val mapFiveTimes = simpleMap(mapFourTimes, x => Sum(C(3)(U8), x)())
      val zip = simpleZip(s, mapFiveTimes)
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
              Default((U8, U8)).lower(),
              Mux(i === C(0)(U8), StmData(s)(), buf)()
            )
          )
        )()
      }
      LetStm(1, s, count, delay)().tchk().lower()
    }
    val optimized = pass.matchLatencies(original)

    // Correct behaviour
    val expectedVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == expectedVal)

    // Effective optimization
    // (Cycle count should be decreased due to improved initiation interval)
    val originalCount = CycleCounter.count(original).get
    val optimizedCount = CycleCounter.count(optimized).get
    assert(optimizedCount < originalCount)

    // Effective optimization
    // (letstm buffers should all be shrunk to 1)
    assertAllBufSizesAreOne(optimized)
  }

  test("ForkTwice") {
    val n = 10
    val original = {
      val count = simpleCount(C(n)(U8))
      val sA = Param("s_a")(TyStm(U8, n))
      val plusFive = simpleMap(sA, x => Sum(C(5)(U8), x)())
      val sB = Param("s_b")(TyStm(U8, n))
      val timesTwo = simpleMap(sB, x => Prod(C(2)(U8), x)())
      val zip = {
        val s0 = Param("s0")(TyStm(U8, -1))
        val s1 = Param("s1")(TyStm(U8, -1))
        val s2 = Param("s2")(TyStm(U8, -1))
        StmBuild(
          n,
          Tuple(StmData(s0)(), StmData(s1)(), StmData(s2)())(),
          True,
          Map[Param, (Expr, Expr)](
            s0 -> (sA, True),
            s1 -> (sB, True),
            s2 -> (timesTwo, True)
          )
        )()
      }
      LetStm(1, sA, count, LetStm(1, sB, plusFive, zip)())().tchk().lower()
    }
    val optimized = pass.matchLatencies(original)

    // Correct behaviour
    val expectedVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == expectedVal)

    // Effective optimization
    // (Cycle count should be decreased due to improved initiation interval)
    val originalCycleCount = CycleCounter.count(original).get
    val optimizedCycleCount = CycleCounter.count(optimized).get
    assert(optimizedCycleCount < originalCycleCount)
    assert(optimizedCycleCount == 17)

    // Effective optimization
    // (letstm buffers should all be shrunk to 1)
    assertAllBufSizesAreOne(optimized)
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
      val count = {
        val i = Param("i")(U8)
        StmBuild(
          n,
          i,
          True,
          Map[Param, (Expr, Expr)](
            i -> (C(0)(U8), Sum(C(1)(U8), i)())
          )
        )()
      }
      val s = Param("s")(TyStm(U8, n))
      val plusFive = {
        val sAcc = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          Sum(C(5)(U8), StmData(sAcc)())(),
          True,
          Map[Param, (Expr, Expr)](
            sAcc -> (s, True)
          )
        )()
      }
      val timesTwo = {
        val sAcc = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          Prod(C(2)(U8), StmData(sAcc)())(),
          True,
          Map[Param, (Expr, Expr)](
            sAcc -> (plusFive, True)
          )
        )()
      }
      val plusOne = {
        val sAcc = Param("s")(TyStm(U8, n))
        StmBuild(
          n,
          Sum(C(1)(U8), StmData(sAcc)())(),
          True,
          Map[Param, (Expr, Expr)](
            sAcc -> (timesTwo, True)
          )
        )()
      }
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
        )()
      }
      val zip = {
        val s0 = Param("s0")(TyStm(U8, -1))
        val s1 = Param("s1")(TyStm(U8, -1))
        StmBuild(
          n,
          Tuple(StmData(s0)(), StmData(s1)())(),
          True,
          Map[Param, (Expr, Expr)](
            s0 -> (delay, True),
            s1 -> (plusOne, True)
          )
        )()
      }
      LetStm(1, s, count, zip)().tchk().lower()
    }
    val optimized = pass.matchLatencies(original)

    // Correct behaviour
    val expectedVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == expectedVal)

    // Non-pessimization
    // (Cycle count should not be increased)
    assert(CycleCounter.count(optimized) == CycleCounter.count(original))
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
      val sumPlusFive = {
        val s = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          C(5)(U8) + StmData(s)(),
          True,
          Map[Param, (Expr, Expr)](
            s -> (sum, True)
          )
        )().tchk()
      }
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
              VecBuild(m, U8 ::+ (_ => Default(U8)))(),
              VecShiftLeft(acc, StmData(s)())()
            ),
            t -> (C(0)(U8), Mux(t === C(m - 1)(U8), C(0)(U8), C(1)(U8) + t)())
          )
        )().tchk()
      }
      val zipped = {
        val s0 = Param("s0")(TyStm(TyVec(U8, m), -1))
        val s1 = Param("s1")(TyStm(U8, -1))
        StmBuild(
          n,
          Tuple(StmData(s0)(), StmData(s1)())(),
          True,
          Map[Param, (Expr, Expr)](
            s0 -> (stm2Vec, True),
            s1 -> (sumPlusFive, True)
          )
        )().tchk()
      }
      LetStm(1, x, count, zipped)().tchk().lower()
    }
    val optimized = pass.matchLatencies(original)

    // Correct behaviour
    val originalVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == originalVal)

    // Effective optimization
    // (Cycle count should be decreased due to improved initiation interval)
    val originalCount = CycleCounter.count(original).get
    val optimizedCount = CycleCounter.count(optimized).get
    assert(optimizedCount < originalCount)

    // Effective optimization
    // (letstm buffers should all be shrunk to 1)
    assertAllBufSizesAreOne(optimized)
  }

  test("NestedLetStm") {
    val n = 5
    val original = {
      val s0 = Param("s0")(TyStm(U8, n))
      val s1 = Param("s1")(TyStm((U8, U8), n))
      val count = {
        val i = Param("i")(U8)
        StmBuild(
          n,
          i,
          True,
          Map[Param, (Expr, Expr)](
            i -> (C(0)(U8), Sum(C(1)(U8), i)())
          )
        )()
      }
      val plusFive = {
        val s = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          C(5)(U8) + StmData(s)(),
          True,
          Map[Param, (Expr, Expr)](
            s -> (s0, True)
          )
        )().tchk()
      }
      val zip = {
        val s0Acc = Param("s0")(TyStm(U8, -1))
        val s1Acc = Param("s1")(TyStm(U8, -1))
        StmBuild(
          n,
          Tuple(StmData(s0Acc)(), StmData(s1Acc)())(),
          True,
          Map[Param, (Expr, Expr)](
            s0Acc -> (s0, True),
            s1Acc -> (plusFive, True)
          )
        )().tchk()
      }
      LetStm(1, s1, LetStm(1, s0, count, zip)(), s1)().tchk().lower()
    }
    val optimized = pass.matchLatencies(original)

    // Correct behaviour
    val originalVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == originalVal)

    // Effective optimization
    // (Cycle count should be decreased due to improved initiation interval)
    val originalCount = CycleCounter.count(original).get
    val optimizedCount = CycleCounter.count(optimized).get
    assert(optimizedCount < originalCount)

    // Effective optimization
    // (letstm buffers should all be shrunk to 1)
    assertAllBufSizesAreOne(optimized)
  }
}
