package opt

import ir._

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
    ???
  }

  private def getRangeByIdx(stm: StmBuild, i: Int): ScalarRange = {
    // TODO: This analysis could be strengthened. For example:
    //       (1) Constructing a dependency graph (possibly containing cycles) for accumulator elements, starting with
    //           elements with fewer dependencies, and using the ranges found for previous elements to help with the
    //           current element.
    //           For example, if seed = (1, 1) and the update function is
    //               (acc: Expr) => Tuple(acc.__0 + 1, acc.__1 + acc.__0)
    //           then `acc.__0 >= 0` (by itself), but also `acc.__1 >= 1` (only because `acc.__0 >= 0`).
    //       (2) Looking for both upper and lower bounds. Maybe this one could even be combined with the function in
    //           `StmCanonPass` which identifies constant accumulator elements.

    val z = PartialEvalPass.partialEval(TupleAccess(stm.seed, i))
    val a = Param("a")
    val delta = TupleAccess(FunCall(stm.nextF, a).__0, i) - TupleAccess(a, i)

    // acc[i] >= z by induction on the step count if:
    //   (Base case) acc[i] = z at first, so acc[i] >= z   (always true)
    //   (Ind. case) acc[i] >= z ==> next(acc[i]) >= z     (to be shown)
    val rLow = ScalarRange(Some(z), None)
    val fLow = FactSet().range(TupleAccess(a, i), rLow)
    val isNonDecreasing =
      PartialEvalPass.isGreaterOrEqual(delta, 0)(fLow).getOrElse(false)
    if (isNonDecreasing) {
      return rLow
    }

    // acc[i] <= z by induction on the step count if:
    //   (Base case) acc[i] = z at first, so acc[i] <= z   (always true)
    //   (Ind. case) acc[i] <= z ==> next(acc[i]) <= z     (to be shown)
    val rHi =
      ScalarRange(
        None,
        Some(ArithSimplifier.simplifyArithmetic(z + 1)(FactSet()))
      )
    val fHi = FactSet().range(TupleAccess(a, i), rHi)
    val isNonIncreasing =
      PartialEvalPass.isSmallerOrEqual(delta, 0)(fHi).getOrElse(false)
    if (isNonIncreasing) {
      rHi
    } else {
      ScalarRange(None, None)
    }
  }
}
