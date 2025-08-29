package mhir.optimize

import mhir.ir.Lowering.ExprLowering
import mhir.ir._
import mhir.ir.evaluate.CycleCounter
import mhir.ir.typecheck.TypeCheck
import org.scalatest.funsuite.AnyFunSuite

class StmLatencyMatcherTests extends AnyFunSuite {
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
      val zip = {
        val s0 = Param("s0")(TyStm(U8, -1))
        val s1 = Param("s1")(TyStm(U8, -1))
        StmBuild(
          n,
          Tuple(StmData(s0)(), StmData(s1)())(),
          True,
          Map[Param, (Expr, Expr)](
            s0 -> (s, True),
            s1 -> (timesTwo, True)
          )
        )()
      }
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
      LetStm(s, count, delay)().tchk().lower()
    }
    val optimized = StmLatencyMatcher.matchLatencies(original)

    // Correct behaviour
    val expectedVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == expectedVal)

    // Effective optimization
    // (Cycle count should be decreased due to improved initiation interval)
    assert(CycleCounter.count(optimized) < CycleCounter.count(original))
  }

  test("ForkTwice") {
    val n = 10
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
      val sA = Param("s_a")(TyStm(U8, n))
      val plusFive = {
        val sAcc = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          Sum(C(5)(U8), StmData(sAcc)())(),
          True,
          Map[Param, (Expr, Expr)](
            sAcc -> (sA, True)
          )
        )()
      }
      val sB = Param("s_b")(TyStm(U8, n))
      val timesTwo = {
        val sAcc = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          Prod(C(2)(U8), StmData(sAcc)())(),
          True,
          Map[Param, (Expr, Expr)](
            sAcc -> (sB, True)
          )
        )()
      }
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
      LetStm(sA, count, LetStm(sB, plusFive, zip)())().tchk().lower()
    }
    val optimized = StmLatencyMatcher.matchLatencies(original)

    // Correct behaviour
    val expectedVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == expectedVal)

    // Effective optimization
    // (Cycle count should be decreased due to improved initiation interval)
    assert(CycleCounter.count(original) == 6 + (n - 1) * 4)
    assert(CycleCounter.count(optimized) == 6 + (n - 1) * 1)
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
      LetStm(s, count, zip)().tchk().lower()
    }
    val optimized = StmLatencyMatcher.matchLatencies(original)

    // Correct behaviour
    val expectedVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == expectedVal)

    // Non-pessimization
    // (Cycle count should not be increased)
    assert(CycleCounter.count(optimized) == CycleCounter.count(original))
  }
}
