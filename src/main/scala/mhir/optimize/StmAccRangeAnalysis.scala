package mhir.optimize

import mhir.ir.Lowering.ExprLowering
import mhir.ir.typecheck.TypeCheck
import mhir.ir._

object StmAccRangeAnalysis {

  /** Look for ranges on accumulator elements in a stream. For example, if an
    * element starts at two and its update function is non-decreasing, then the
    * range of that element is <code>[2, infinity)</code>.
    *
    * Note that this analysis only considers streams whose accumulators are
    * tuples and will only look for ranges on the elements directly within the
    * tuple, not deeply-nested elements. If the accumulator is not a tuple, then
    * an empty sequence will be returned. It is a good idea to run the
    * canonicalization pass before using this analysis.
    */
  def findAccRanges(stm: StmBuild): StmAccRange = {
    StmAccRange(
      stm.accVars
        .flatMap(x =>
          x.typ match {
            case _: TyAnyInt => Some(x -> getRange(stm, x))
            case _           => None
          }
        )
        .toMap
    )
  }

  private def getRange(stm: StmBuild, x: Param): ScalarRange = {
    // TODO: This analysis could be strengthened. For example:
    //       (1) Constructing a dependency graph (possibly containing cycles) for accumulator elements, starting with
    //           elements with fewer dependencies, and using the ranges found for previous elements to help with the
    //           current element.
    //           For example, if seed = (1, 1) and the update function is
    //               (acc: Expr) => Tuple(acc.__0 + 1, acc.__1 + acc.__0)
    //           then `acc.__0 >= 0` (by itself), but also `acc.__1 >= 1` (only because `acc.__0 >= 0`).
    //       (2) Looking for both upper and lower bounds. Maybe this one could even be combined with the function in
    //           `StmCanonPass` which identifies constant accumulator elements.
    assert(
      x.typ.isInstanceOf[TyAnyInt],
      "the accumulator range analysis should only be used for integer accumulators"
    )

    val z = stm.seedByVar(x)
    val delta = (stm.nextByVar(x) - x).tchk().lower()

    // acc[i] >= z by induction on the step count if:
    //   (Base case) acc[i] = z at first, so acc[i] >= z   (always true)
    //   (Ind. case) acc[i] >= z ==> next(acc[i]) >= z     (to be shown)
    val fLow = FactSet().geq(x, z)
    val isNonDecreasing =
      PartialEvalPass.isGreaterOrEqual(delta, 0)(fLow).getOrElse(false)
    val lower = if (isNonDecreasing) {
      Some(z)
    } else {
      None
    }

    // acc[i] <= z by induction on the step count if:
    //   (Base case) acc[i] = z at first, so acc[i] <= z   (always true)
    //   (Ind. case) acc[i] <= z ==> next(acc[i]) <= z     (to be shown)
    val zPlusOne =
      PartialEvalPass.partialEval((z + 1).tchk().lower())(FactSet())
    val fHi = FactSet().lt(x, zPlusOne)
    val isNonIncreasing =
      PartialEvalPass.isSmallerOrEqual(delta, 0)(fHi).getOrElse(false)
    val upper = if (isNonIncreasing) {
      Some(zPlusOne)
    } else {
      None
    }

    ScalarRange(lower, upper)
  }
}
