package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.optimize.cost.SimpleDelayCostModel
import mhir.sugar._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class StmFissionPassTests extends AnyFunSuite {

  private val pass = EnabledStmFissionPass(
    StmOutputScheduler(EnabledBinOpTreeBalancingPass)
  )

  test("SharpenOne") {
    val n = 9
    val uint = U32
    val inputA = StmRange(n, C(96)(uint), C(1)(uint))().tchk().lower()
    val inputB = StmRange(n, C(80)(uint), C(5)(uint))().tchk().lower()
    val original = {
      val sA = Param("s_a")(TyStm(uint, n))
      val sB = Param("s_b")(TyStm(uint, n))
      val threshold = 15
      val a = StmData(sA)()
      val b = StmData(sB)()
      val passedThreshold = ((a -% b) > threshold) || ((b -% a) > threshold)
      val alphaH =
        Mux(passedThreshold, (b -% a) >>> 2, C(0)(uint))()
      val sharp = b +% alphaH
      StmBuild(
        n,
        sharp,
        True,
        Map[Param, (Expr, Expr)](
          sA -> (inputA, True),
          sB -> (inputB, True)
        )
      )().tchk().lower()
    }
    val optimized = pass.fission(original)

    // Correct behaviour
    val originalVal = mhir.eval.eval(original)
    val optimizedVal = mhir.eval.eval(optimized)
    assert(optimizedVal == originalVal)

    // Effective optimization
    val originalDelayCost = SimpleDelayCostModel.cost(original)
    assert(originalDelayCost > SimpleDelayCostModel.FullCycleDelay)
    val optimizedDelayCost = SimpleDelayCostModel.cost(optimized)
    assert(optimizedDelayCost <= SimpleDelayCostModel.FullCycleDelay)
  }

  test("ProdReductionTree:Nested") {
    val n = 8
    val m = 5
    val uint = U32
    val input = StmLiteral(
      (0 until n).map(t =>
        VecLiteral((0 until m).map(i => C(t + i)(uint)): _*)()
      ): _*
    )().tchk().lower()
    val original = {
      val s = Param("s")(TyStm(TyVec(uint, m), n))
      StmBuild(
        n,
        (VecAccess(StmData(s)(), 0)() *% VecAccess(StmData(s)(), 1)()) *%
          (VecAccess(StmData(s)(), 2)() *% (VecAccess(StmData(s)(), 3)() *%
            VecAccess(StmData(s)(), 4)())),
        True,
        Map[Param, (Expr, Expr)](
          s -> (input, True)
        )
      )().tchk().lower()
    }
    val optimized = pass.fission(original)

    // Correct behaviour
    val originalVal = mhir.eval.eval(original)
    val optimizedVal = mhir.eval.eval(optimized)
    assert(optimizedVal == originalVal)

    // Effective optimization
    val originalDelayCost = SimpleDelayCostModel.cost(original)
    assert(originalDelayCost > SimpleDelayCostModel.FullCycleDelay)
    val optimizedDelayCost = SimpleDelayCostModel.cost(optimized)
    assert(optimizedDelayCost <= SimpleDelayCostModel.FullCycleDelay)
  }

  /** Avoid infinite loops when an expression doesn't fit anywhere even when all
    * its inputs have no delay.
    */
  test("ProdReductionTree:Flat") {
    val n = 8
    val m = 5
    val uint = U32
    val input = StmLiteral(
      (0 until n).map(t =>
        VecLiteral((0 until m).map(i => C(t + i)(uint)): _*)()
      ): _*
    )().tchk().lower()
    val original = {
      val s = Param("s")(TyStm(TyVec(uint, m), n))
      StmBuild(
        n,
        WrappingProd(
          VecAccess(StmData(s)(), 0)(),
          VecAccess(StmData(s)(), 1)(),
          VecAccess(StmData(s)(), 2)(),
          VecAccess(StmData(s)(), 3)(),
          VecAccess(StmData(s)(), 4)()
        )(),
        True,
        Map[Param, (Expr, Expr)](
          s -> (input, True)
        )
      )().tchk().lower()
    }
    val optimized = pass.fission(original)

    // Correct behaviour
    val originalVal = mhir.eval.eval(original)
    val optimizedVal = mhir.eval.eval(optimized)
    assert(optimizedVal == originalVal)

    // Effective optimization
    val originalDelayCost = SimpleDelayCostModel.cost(original)
    assert(originalDelayCost > SimpleDelayCostModel.FullCycleDelay)
    val optimizedDelayCost = SimpleDelayCostModel.cost(optimized)
    assert(optimizedDelayCost <= SimpleDelayCostModel.FullCycleDelay)
  }

  test("ProdReductionTree:VecReduceComb") {
    val n = 8
    val m = 5
    val uint = U32
    val input = StmLiteral(
      (0 until n).map(t =>
        VecLiteral((0 until m).map(i => C(t + i)(uint)): _*)()
      ): _*
    )().tchk().lower()
    val original = {
      val s = Param("s")(TyStm(TyVec(uint, m), n))
      StmBuild(
        n,
        VecReduceComb(StmData(s)(), (uint, uint) ::+ (x => x.__0 * x.__1))(),
        True,
        Map[Param, (Expr, Expr)](
          s -> (input, True)
        )
      )().tchk().lower()
    }
    val optimized = pass.fission(original)

    // Correct behaviour
    val originalVal = mhir.eval.eval(original)
    val optimizedVal = mhir.eval.eval(optimized)
    assert(optimizedVal == originalVal)

    // Effective optimization
    val originalDelayCost = SimpleDelayCostModel.cost(original)
    assert(originalDelayCost > SimpleDelayCostModel.FullCycleDelay)
    val optimizedDelayCost = SimpleDelayCostModel.cost(optimized)
    assert(optimizedDelayCost <= SimpleDelayCostModel.FullCycleDelay)
  }
}
