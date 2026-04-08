package mhir.optimize

import mhir.sugar.ExprLowering
import org.scalatest.funsuite.AnyFunSuite
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.sugar._

class StaticLetStmBufferShrinkerTests extends AnyFunSuite {

  private val pass: LetStmBufferShrinker =
    new StaticLetStmBufferShrinker(assumeThroughputsMatch = true)

  private def assertAllBufSizesAreOne(e: Expr): Unit = {
    e match {
      case LetStm(bufSize, _, _, _) =>
        assert(bufSize == C(1)())
      case e => e.children.foreach(assertAllBufSizesAreOne)
    }
  }

  /** Simple successful case.
    */
  test("let s = ... in zip(map(s, +5), nop(s))") {
    val n = 16
    val original = {
      val count = SimpleCount(C(n)(U8))
      val s = Param("s")(TyStm(U8, n))
      val plusFive = SimpleMap(s, x => Sum(C(5)(U8), x)())
      val nop = SimpleNop(s)
      val zip = SimpleZip(plusFive, nop)
      Let(s, count, zip)().tchk().lower()
    }
    val optimized = pass.shrinkBuffers(original)

    // Correct behaviour
    val expectedVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == expectedVal)

    // Effective optimization
    assertAllBufSizesAreOne(optimized)
  }

  /** Successful case with nested LetStm.
    */
  test("NestedLets") {
    val n = 10
    val original = {
      val sA = Param("s_a")(TyStm(U8, n))
      val sB = Param("s_b")(TyStm(U8, n))
      val count = SimpleCount(C(n)(U8))
      val plusFive = SimpleMap(sA, x => Sum(C(5)(U8), x)())
      val timesTwo = SimpleMap(sB, x => Prod(C(2)(U8), x)())
      val zip = SimpleZip(SimpleNop(sA, delay = 4), SimpleNop(sB), timesTwo)
      Let(sA, count, Let(sB, plusFive, zip)())().tchk().lower()
    }
    val optimized = pass.shrinkBuffers(original)

    // Correct behaviour
    val expectedVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == expectedVal)

    // Effective optimization
    // (letstm buffers should all be shrunk to 1)
    assertAllBufSizesAreOne(optimized)
  }

  /** Successful case with nested LetStm.
    */
  test("TwoLetsInSeries") {
    val n = 5
    val original = {
      val s0 = Param("s0")(TyStm(U8, n))
      val s1 = Param("s1")(TyStm((U8, U8), n))
      val count = SimpleCount(C(n)(U8))
      val plusFive = SimpleMap(s0, x => Sum(C(5)(U8), x)())
      val zip = SimpleZip(s0, plusFive)
      Let(s1, Let(s0, count, zip)(), s1)().tchk().lower()
    }
    val optimized = pass.shrinkBuffers(original)

    // Correct behaviour
    val originalVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == originalVal)

    // Effective optimization
    // (letstm buffers should all be shrunk to 1)
    assertAllBufSizesAreOne(optimized)
  }

  /** Successful case with reductions.
    */
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
      val zipped = SimpleZip(sum, stm2Vec)
      Let(x, count, zipped)().tchk().lower()
    }
    val optimized = pass.shrinkBuffers(original)

    // Correct behaviour
    val originalVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == originalVal)

    // Effective optimization
    // (letstm buffers should all be shrunk to 1)
    assertAllBufSizesAreOne(optimized)
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
      val count = SimpleCount(C(n * m)(U8))
      val prefix = {
        val s = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          StmData(s)(),
          True,
          Map[Param, (Expr, Expr)](s -> (x, True))
        )().tchk()
      }
      val delayedPrefix = SimpleNop(prefix, delay = m - 1)
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
      val zip = SimpleZip(delayedPrefix, rowSums)
      LetStm(n * m, x, count, zip)().tchk().lower()
    }
    val pass = new StaticLetStmBufferShrinker(assumeThroughputsMatch = false)
    val optimized = pass.shrinkBuffers(original)

    // Correct behaviour
    val expectedVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == expectedVal)
  }

  /** The condition that all branches must have the same latency is necessary
    * for shrinking the buffers in letstm.
    */
  test("SumAndHead") {
    val n = 8
    val m = 4
    val original = {
      // StmCount(n*m)
      val count = SimpleCount(C(n * m)(U8))
      val x = Param("s")(TyStm(U8, n * m))
      val rowSums = {
        val t = Param("t")(U8)
        val acc = Param("acc")(U8)
        val s = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          Sum(StmData(s)(), acc)(),
          t === (m - 1),
          Map[Param, (Expr, Expr)](
            t -> (
              C(0)(U8),
              Mux(t === (m - 1), C(0)(U8), Sum(C(1)(U8), t)())()
            ),
            acc -> (
              C(0)(U8),
              Mux(
                t === (m - 1),
                C(0)(U8),
                Sum(StmData(s)(), acc)()
              )()
            ),
            s -> (x, True)
          )
        )().tchk()
      }
      val rowHeads = {
        val t = Param("t")(U8)
        val s = Param("s")(TyStm(U8, -1))
        StmBuild(
          n,
          StmData(s)(),
          t === 0,
          Map[Param, (Expr, Expr)](
            t -> (
              C(0)(U8),
              Mux(t === (m - 1), C(0)(U8), Sum(C(1)(U8), t)())()
            ),
            s -> (x, True)
          )
        )().tchk()
      }
      val zip = SimpleZip(rowSums, rowHeads)
      LetStm(m, x, count, zip)().tchk()
    }
    val optimized = pass.shrinkBuffers(original)

    // Correct behaviour
    val expectedVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == expectedVal)
  }

  test("StmConcat") {
    val n = 5
    val original = {
      val s0 = Param("s0")(TyStm(U8, n))
      val count = SimpleCount(C(n)(U8))
      val concat = SimpleConcat(s0, s0)
      LetStm(n, s0, count, concat)().tchk().lower()
    }
    val optimized = pass.shrinkBuffers(original)

    // Correct behaviour
    val originalVal = mhir.ir.eval(original)
    val actualVal = mhir.ir.eval(optimized)
    assert(actualVal == originalVal)
  }
}
