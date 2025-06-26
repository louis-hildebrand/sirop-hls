package mhir.optimize

import mhir.ir.Lowering.ExprLowering
import mhir.ir.TypeChecker.TypeCheck
import mhir.ir._
import mhir.sugar.Cast

import scala.annotation.tailrec

object StmAccRemovalPass {

  /** For each accumulator element that definitely never changes, replace each
    * use of that variable with its constant value and remove the variable from
    * the set of equations for the stream.
    */
  def removeConstantVars(stm: StmBuild): StmBuild = {
    val constantVars = findConstantAccumulatorElems(
      stm,
      // Only remove accumulator elements for which we can definitely perform
      // constant propagation. Replacing all uses with the initial value
      // probably wouldn't work if the element is a stream, for example.
      candidates = stm.seedByVar
        .filter({ case (_, z) =>
          z match {
            case _: IntCst | True | False => true
            case Tuple()                  => true
            case _                        => false
          }
        })
        .map({ case (x, _) => x })
        .toSet
    )
    val replacements = stm.seedByVar
      .filter({ case (x, _) => constantVars.contains(x) })
    stm.replaceVars(replacements)
  }

  // TODO: Make this a method of StmBuild?
  /** Remove unused accumulator elements from the given stream. This pass
    * assumes the stream is already in canonical form (e.g., the accumulator is
    * a flat tuple).
    */
  def removeUnusedVars(stm: StmBuild): StmBuild = {
    val usedElems =
      stm.accVarDependencies.transitiveDependencies(stm.outputDependencies)
    StmBuild(
      stm.n,
      stm.data,
      stm.valid,
      stm.equations.filter({ case (x, _) => usedElems.contains(x) })
    )()
  }

  def deduplicateVars(stm: StmBuild): StmBuild = {
    val varEquivClasses = stm.equations
      .groupBy({ case (x, (z, next)) =>
        // Deliberately capture x because, for example, the following are
        // duplicates:
        //   x: (0, x + 1)
        //   y: (0, y + 1)
        (z, Function(x, next)())
      })
      .values
      .map(m => m.keySet)
      .toSeq
    val replacements: Map[Param, Expr] =
      varEquivClasses
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
            (xs - x).map(y => y -> Cast(x, y.typ)().tchk().lower())
          replacements
        })
        .toMap
    val newStm = stm.replaceVars(replacements)
    // Will this incorrectly leave behind any occurrences of the removed
    // variables?
    // No, because the substitutions should have eliminated all of them
    // without introducing any new occurrences.
    assert(
      newStm.freeVars() == stm.freeVars(),
      "deduplicating accumulator variables should not have changed the set of free variables"
    )
    newStm
  }

  @tailrec
  private def findConstantAccumulatorElems(
      stm: StmBuild,
      candidates: Set[Param]
  ): Set[Param] = {
    // TODO: We could also find constant elements by adapting `StmAccRangeAnalysis` to look for both an upper and lower
    //       bound. An element is constant if its upper and lower bounds coincide. Maybe it would be good to merge the
    //       two somehow.
    if (candidates.isEmpty) {
      Set()
    } else {
      val currentValByVar = stm.seedByVar.map({ case (x, z) =>
        if (candidates.contains(x)) x -> z
        else x -> Param("unknown")(x.typ)
      })
      val nextValByVar = stm.nextByVar.map({ case (x, next) =>
        x -> PartialEvalPass.partialEval(
          next.subPreserveType(currentValByVar.toMap[Expr, Expr])
        )
      })
      val constantVars =
        candidates.filter(x => nextValByVar(x) == currentValByVar(x))
      if (constantVars == candidates) {
        constantVars
      } else {
        findConstantAccumulatorElems(stm, candidates = constantVars)
      }
    }
  }
}
