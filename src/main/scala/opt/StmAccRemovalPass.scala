package opt

import ir._

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
      stm.output,
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
        .flatMap(xs =>
          if (xs.size <= 1) {
            // No duplicates
            Set[(Param, Param)]()
          } else {
            // Choose any variable to be the representative
            val x = xs.head
            (xs - x).map(y => y -> x)
          }
        )
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

  /** @param stm
    *   A stream whose accumulator is a non-nested tuple.
    * @return
    *   The indices within the accumulator tuple of the constant-valued
    *   elements.
    */
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
        else x -> Param("unknown")()
      })
      val nextValByVar = stm.nextByVar.map({ case (x, next) =>
        x -> PartialEvalPass.partialEval(
          next.substitute(currentValByVar.toMap[Expr, Expr])
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
