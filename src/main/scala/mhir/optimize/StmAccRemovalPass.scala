package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.optimize.{PartialEvalPass => PE}
import mhir.sugar.{Cast, ExprLowering}
import mhir.typecheck.TypeCheck

import scala.annotation.tailrec

/** Simple transformations for removing unnecessary accumulators within a
  * [[mhir.ir.StmBuild]].
  */
object StmAccRemovalPass {

  /** For each accumulator element that definitely never changes, replace each
    * use of that variable with its constant value and remove the variable from
    * the set of accumulators for the stream.
    */
  def removeConstantAccumulators(stm: StmBuild): StmBuild = {
    val constantVars = findConstantAccumulators(
      stm,
      // Replacing all uses with the initial value probably wouldn't work if
      // the element is a stream
      candidates = stm.accumulators.map({ case (x, _) => x }).toSet
    )
    val replacements = stm.accumulators
      .collect({ case (x, (z, _)) if constantVars.contains(x) => x -> z })
    stm.replaceVars(replacements)
  }

  /** Remove unused accumulator elements from the given stream.
    */
  def removeUnusedVars(stm: StmBuild): StmBuild = {
    val usedElems =
      stm.internalDependencies.transitiveDependencies(stm.outputDependencies)
    StmBuild(
      stm.n,
      stm.data,
      stm.valid,
      stm.accumulators.filter({ case (x, _) => usedElems.contains(x) }),
      stm.producers.filter({ case (x, _) => usedElems.contains(x) })
    )()
  }

  def deduplicateVars(stm: StmBuild): StmBuild = {
    val equivClasses =
      findDuplicateAccumulators(stm) ++ findDuplicateInputs(stm)
    val replacements: Map[Param, Expr] =
      equivClasses
        .flatMap(xs => {
          // Accumulators with different integer types may end up in the same
          // equivalence class.
          // Keep the "smallest" type (unsigned if possible and with the
          // smallest bit width) out of the available types.
          // In theory, in some cases you could go even narrower than any of the
          // existing accumulators.
          // For example, if you have a u8 and an i8, you should be able to fit
          // the value into a u7 (assuming the original program had no overflow).
          // However, then you would need to do some reshaping to turn the u8
          // accumulator into a u7 accumulator, which seems non-trivial in
          // general.
          // Simply choosing one of the existing accumulators to keep seems
          // much easier.
          val typ = xs
            .map(x => x.typ)
            .toSeq
            .minBy({
              case TyUInt(w) => (0, w)
              case TySInt(w) => (1, w)
              case _         => (2, 0)
            })
          val x = xs.find(x => x.typ == typ).get
          val replacements =
            (xs - x).map(y => y -> Cast(x, y.typ)().tchk().lower)
          replacements
        })
        .toMap
    val newStm = stm.replaceVars(replacements)
    // Will this incorrectly leave behind any occurrences of the removed
    // variables?
    // No, because the substitutions should have eliminated all of them
    // without introducing any new occurrences.
    assert(
      newStm.freeVars == stm.freeVars,
      "deduplicating accumulator variables should not have changed the set of free variables"
    )
    newStm
  }

  @tailrec
  private def findConstantAccumulators(
      stm: StmBuild,
      candidates: Set[Param]
  ): Set[Param] = {
    // TODO: We could also find constant elements by adapting `StmAccRangeAnalysis` to look for both an upper and lower
    //       bound. An element is constant if its upper and lower bounds coincide. Maybe it would be good to merge the
    //       two somehow.
    if (candidates.isEmpty) {
      Set()
    } else {
      val initByAccumulator =
        stm.accumulators.map({ case (x, (init, _)) =>
          if (candidates.contains(x)) {
            x -> init
          } else {
            x -> Param("unknown")(x.typ)
          }
        })
      val subs = initByAccumulator.toMap[Expr, Expr]
      val nextByAccumulator = stm.accumulators.map({ case (x, (_, next)) =>
        x -> PartialEvalPass.partialEval(next.subPreserveType(subs))
      })
      val constantVars =
        candidates.filter(x => nextByAccumulator(x) == initByAccumulator(x))
      // TODO: Speed this up by comparing sizes instead of contents?
      if (constantVars == candidates) {
        constantVars
      } else {
        findConstantAccumulators(stm, candidates = constantVars)
      }
    }
  }

  /** Finds sets of accumulators in the given [[mhir.ir.StmBuild]] which will
    * always have the same value.
    *
    * @return
    *   equivalence classes of variables. Each equivalence class will have at
    *   least two elements. Each element of an equivalence class is an
    *   accumulator in the given stream.
    * @note
    *   the return value does not necessarily contain all the accumulators in
    *   the [[mhir.ir.StmBuild]].
    */
  private def findDuplicateAccumulators(stm: StmBuild): Set[Set[Param]] = {
    // This method proves that each equivalence class contains equivalent
    // accumulators by induction.
    //  * Base case. group into initial equivalence classes based on seeds.
    //  * Step case. using the assumption that the variables in a given
    //    class are all equal to one another, find the next value and split
    //    into possibly smaller equivalence classes. If all variables in a given
    //    class also have the same next value, then they are all equivalent.

    def split(cls: Set[Param]): Set[Set[Param]] = {
      val testAcc = Param("test_acc")()
      val subs = cls
        .map(x => x -> testAcc.rebuild(x.typ))
        .toMap[Expr, Expr]
      val nextByVar = cls
        .map({ x =>
          val next = stm.nextOrReady(x)
          val nextWithSub = next.subPreserveType(subs)
          val simplifiedNext =
            try {
              PE.partialEval(nextWithSub)
            } catch {
              case _: OverflowException => Param("error")()
            }
          x -> simplifiedNext
        })
      val splitClasses = nextByVar
        .groupBy({ case (_, next) => next })
        .map({ case (_, eqns) => eqns.map({ case (x, _) => x }) })
        .toSet
      splitClasses
    }
    @tailrec
    def fix(
        maybeEquiv: Set[Set[Param]],
        confirmedEquiv: Set[Set[Param]]
    ): Set[Set[Param]] = {
      val nonTrivialEquivClasses = maybeEquiv.filter(_.size > 1)
      if (nonTrivialEquivClasses.isEmpty) {
        confirmedEquiv
      } else {
        var newMaybe = Set[Set[Param]]()
        var newConfirmed = confirmedEquiv
        for (cls <- nonTrivialEquivClasses) {
          val splitCls = split(cls)
          if (splitCls.size == 1) {
            assert(splitCls.head == cls)
            newConfirmed = newConfirmed + cls
          } else {
            newMaybe = newMaybe ++ splitCls
          }
        }
        fix(newMaybe, newConfirmed)
      }
    }

    val initialEquivClasses =
      stm.accumulators
        .map({ case (x, (init, _)) => x -> init })
        .groupBy({ case (_, z) => z })
        .map({ case (_, eqns) => eqns.map({ case (x, _) => x }).toSet })
        .toSet
    fix(initialEquivClasses, Set[Set[Param]]())
  }

  private def findDuplicateInputs(stm: StmBuild): Set[Set[Param]] = {
    stm.producers
      .groupBy({ case (_, (s, ready)) => (s, ready) })
      .map({ case (_, eqns) => eqns.map({ case (x, _) => x }).toSet })
      .toSet
  }
}
