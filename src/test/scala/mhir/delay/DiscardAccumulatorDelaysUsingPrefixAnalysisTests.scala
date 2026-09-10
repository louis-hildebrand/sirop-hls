package mhir.delay

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class DiscardAccumulatorDelaysUsingPrefixAnalysisTests extends AnyFunSuite {

  private def countAccumulatorsWithDelay(e: Expr): Int = {
    e match {
      case s: StmBuild =>
        val here = s.accumulators.count({ case (_, (_, _, delay)) =>
          delay.typ.isInstanceOf[TyAnyInt]
        })
        val inProducers = s.producers
          .map({ case (_, (stm, _, _)) => countAccumulatorsWithDelay(stm) })
          .sum
        here + inProducers
      case e => e.children.map(countAccumulatorsWithDelay).sum
    }
  }

  test("StmSlideStartingWith:OK") {
    val n = 8
    val w = 3
    val input = Param("input")(TyStm(TyBool, n))
    val original = Function(
      input,
      StmSlideStartingWith(input, AllZero(TyVec(TyBool, w)))()
    )().tchk().lower
    assert(countAccumulatorsWithDelay(original) > 0)

    val actual = DiscardAccumulatorDelaysUsingPrefixAnalysis.apply(
      original,
      headByParam = Map(input -> False)
    )
    assert(countAccumulatorsWithDelay(actual) == 0)
  }

  test("StmSlideStartingWith:MissingInputHead") {
    val n = 8
    val w = 3
    val input = Param("input")(TyStm(TyBool, n))
    val original = Function(
      input,
      StmSlideStartingWith(input, AllZero(TyVec(TyBool, w)))()
    )().tchk().lower
    val originalCount = countAccumulatorsWithDelay(original)
    assert(originalCount > 0)

    val actual = DiscardAccumulatorDelaysUsingPrefixAnalysis.apply(
      original,
      headByParam = Map(input -> Undefined(TyBool))
    )
    val actualCount = countAccumulatorsWithDelay(actual)
    assert(actualCount == originalCount)
  }
}
