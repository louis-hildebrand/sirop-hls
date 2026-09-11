package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.optimize.{PartialEvalPass => PE}
import mhir.sugar.{
  Cast,
  ExprLowering,
  SafeSum,
  VecConcat,
  VecDrop,
  VecShiftLeft,
  VecTake,
  VecTakeRight
}
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
      .collect({ case (x, (z, _, _)) if constantVars.contains(x) => x -> z })
    stm.replaceVars(replacements)
  }

  /** Remove unused accumulator elements from the given stream.
    */
  def removeUnusedVars(stm: StmBuild): StmBuild = {
    val usedElems =
      stm.internalDependencies.transitiveDependencies(stm.outputDependencies)
    stm.removeVarsExcept(usedElems)
  }

  def deduplicateVars(stm: StmBuild): StmBuild = {
    val stm1 = mergeFullyEquivalentVars(stm)
    val stm2 = mergeChainedShiftRegisters(stm1)
    val stm3 = mergeShiftRegistersWithSameInput(stm2)
    stm3
  }

  private def mergeFullyEquivalentVars(stm: StmBuild): StmBuild = {
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

  private def mergeChainedShiftRegisters(original: StmBuild): StmBuild = {
    @tailrec
    def merge(stm: StmBuild, visited: Set[Param]): StmBuild = {
      val childCandidate = stm.accumulators.keySet.diff(visited).headOption
      childCandidate match {
        case None => stm
        case Some(child) =>
          (child, stm.accumulators(child)) match {
            // TODO: this match syntax is kind of gross. Can I simplify?
            case ShiftLeft(
                  _,
                  ShiftLeft(childLen, VecAccess(parent: Param, IntCst(src)))
                ) if stm.accumulators.contains(parent) && parent != child =>
              (parent, stm.accumulators(parent)) match {
                case ShiftLeft(_, ShiftLeft(parentLen, parentInput)) =>
                  // TODO: can I combine these two branches?
                  if (src >= childLen) {
                    // child is completely inside parent
                    val newStm = mergeChainedShiftRegisterInside(
                      stm,
                      child,
                      childLen,
                      parent,
                      src
                    )
                    newStm match {
                      case Some(s) => merge(s, visited)
                      case None    => merge(stm, visited + child)
                    }
                  } else {
                    // child extends past index 0 of parent; make one big shift register to replace both
                    val newStm = mergeChainedShiftRegistersOutside(
                      stm,
                      child,
                      childLen,
                      parent,
                      parentLen,
                      parentInput,
                      src
                    )
                    newStm match {
                      case Some(s) => merge(s, visited)
                      case None    => merge(stm, visited + child)
                    }
                  }
                case _ => merge(stm, visited + child)
              }
            case _ => merge(stm, visited + child)
          }
      }
    }
    merge(
      original,
      visited =
        original.accumulators.keySet.filterNot(_.typ.isInstanceOf[TyVec])
    )
  }

  private def mergeChainedShiftRegisterInside(
      stm: StmBuild,
      child: Param,
      childLen: Long,
      parent: Param,
      src: Long
  ): Option[StmBuild] = {
    val (childInit, _, childDelay) = stm.accumulators(child)
    val (parentInit, _, parentDelay) = stm.accumulators(parent)
    // The part of the parent's initial value that the child's initial value should match
    val overlapInitFromParent = PartialEvalPass.partialEval(
      // Discard from the left until the length matches childLen
      VecTakeRight(
        // Discard everything after and including index `src`
        VecTake(parentInit, C(src)())(),
        C(childLen)()
      )().tchk().lower
    )
    assert(overlapInitFromParent.typ == childInit.typ)
    val sameInit = (
      childInit.isInstanceOf[Undefined]
        || parentInit.isInstanceOf[Undefined]
        || (
          overlapInitFromParent == childInit
            && parentDelay == childDelay
        )
    )
    if (sameInit) {
      val i = Param("i")(TyAnyInt.tightest(0, childLen - 1))
      val newChild = PartialEvalPass.partialEval(
        VecBuild(
          C(childLen)(),
          Function(
            i,
            // child[childLen-1] is a duplicate of parent[src-1]
            // child[childLen-2] is a duplicate of parent[src-2]
            // ...
            // child[childLen-childLen] is a duplicate of parent[src-childLen]
            VecAccess(parent, SafeSum(i, C(src - childLen)())())()
          )()
        )().tchk().lower
      )
      Some(
        stm
          .replaceVars(Map(child -> newChild))
          .tchk()
          .asInstanceOf[StmBuild]
      )
    } else {
      None
    }
  }

  private def mergeChainedShiftRegistersOutside(
      stm: StmBuild,
      child: Param,
      childLen: Long,
      parent: Param,
      parentLen: Long,
      parentInput: Expr,
      src: Long
  ): Option[StmBuild] = {
    val TyVec(elemTyp, _) = parent.typ
    val (childInit, _, childDelay) = stm.accumulators(child)
    val (parentInit, _, parentDelay) = stm.accumulators(parent)
    // Initial value in the overlapping region
    // TODO: skip this calculation if one of them are undefined or src == 0? Maybe move this to a method?
    val overlapInitFromChild = PartialEvalPass.partialEval(
      VecTakeRight(childInit, C(src)())().tchk().lower
    )
    val overlapInitFromParent = PartialEvalPass.partialEval(
      VecTake(parentInit, C(src)())().tchk().lower
    )
    val sameInitInOverlap = (
      // TODO: is simplification possible if childInit is undefined but parentInit is a concrete value, or vice-versa?
      //       Need to think carefully about the semantics of a missing delay annotation.
      //       Maybe the programmer has assumed the input latency is exactly a certain value;
      //       in that case, shift register merging might change the behaviour.
      //       If it turns out to be important for performance to merge in this case,
      //       maybe I can add a compiler flag or accelerator annotation to let the compiler assume strict latency insensitivity.
      src == 0 // no overlap at all
        || (childInit.isInstanceOf[Undefined]
          && parentInit.isInstanceOf[Undefined])
        || (overlapInitFromParent == overlapInitFromChild
          && childDelay == parentDelay)
    )
    if (sameInitInOverlap) {
      val combinedLen = {
        // src == 0: combinedLen = childLen + parentLen
        // src == 1: combinedLen = childLen + parentLen - 1
        // etc.
        childLen + parentLen - src
      }
      val combined = parent.freshCopy
        .rebuild(TyVec(elemTyp, C(combinedLen)()))
        .asInstanceOf[Param]
      val combinedDelay = parentInit match {
        case _: Undefined => childDelay
        case _            => parentDelay
      }
      val combinedInit = PartialEvalPass.partialEval(
        VecConcat(childInit, VecDrop(parentInit, C(src)())())()
          .tchk()
          .lower
      )
      val combinedNext = PartialEvalPass.partialEval(
        VecShiftLeft(combined, parentInput)().tchk().lower
      )
      val newChild = PartialEvalPass.partialEval(
        VecTake(combined, C(childLen)())().tchk().lower
      )
      val newParent = PartialEvalPass.partialEval(
        VecTakeRight(combined, C(parentLen)())().tchk().lower
      )
      val result = stm
        .addAccumulator(
          combined,
          combinedInit,
          combinedNext,
          combinedDelay
        )
        .replaceVars(
          Map(
            child -> newChild,
            parent -> newParent
          )
        )
      Some(result)
    } else {
      None
    }
  }

  private def mergeShiftRegistersWithSameInput(original: StmBuild): StmBuild = {
    val shiftRegisters = original.accumulators
      .collect({ case ShiftLeft(x, shift) => x -> shift })
    val groups = shiftRegisters
      .map({ case (x, ShiftLeft(_, input)) => x -> input })
      .groupBy({ case (x, input) =>
        val (_, _, delay) = original.accumulators(x)
        (input, delay)
      })
      .map({ case (_, map) => map.keySet })
      .toSet
    // Length of the register needed for each input
    val replacements = groups
      .flatMap({ xs =>
        val maxLength = xs.map(shiftRegisters(_).length).max
        // Keep the longest shift register
        val representative = xs.find(shiftRegisters(_).length == maxLength).get
        val (representativeInit, _, _) = original.accumulators(representative)
        xs
          .filterNot(_ == representative)
          .map({ x =>
            val ShiftLeft(xLen, _) = shiftRegisters(x)
            (x, xLen)
          })
          .filter({ case (x, xLen) =>
            // Check that the initial value matches the representative.
            // Discard if not.
            // TODO: try to make a separate group if the initial value doesn't match, instead of discarding?
            val (xInit, _, _) = original.accumulators(x)
            val overlapInitFromRep = PartialEvalPass.partialEval(
              VecTakeRight(representativeInit, C(xLen)())().tchk().lower
            )
            val overlapInitFromX = PartialEvalPass.partialEval(
              VecTakeRight(xInit, C(xLen)())().tchk().lower
            )
            overlapInitFromRep == overlapInitFromX
          })
          .map({ case (x, xLen) =>
            x -> VecTakeRight(representative, C(xLen)())().tchk().lower
          })
      })
      .toMap
    original.replaceVars(replacements)
  }

  @tailrec
  private def findConstantAccumulators(
      stm: StmBuild,
      candidates: Set[Param]
  ): Set[Param] = {
    if (candidates.isEmpty) {
      Set()
    } else {
      val initByAccumulator =
        stm.accumulators.map({ case (x, (init, _, _)) =>
          if (candidates.contains(x)) {
            x -> init
          } else {
            x -> Param("unknown")(x.typ)
          }
        })
      val subs = initByAccumulator.toMap[Expr, Expr]
      val nextByAccumulator = stm.accumulators.map({ case (x, (_, next, _)) =>
        x -> PartialEvalPass.partialEval(next.subPreserveType(subs))
      })
      val constantVars =
        candidates.filter(x => nextByAccumulator(x) == initByAccumulator(x))
      if (constantVars.size == candidates.size) {
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
        .groupBy({ case (_, (init, _, delay)) => (init, delay) })
        .map({ case (_, eqns) => eqns.map({ case (x, _) => x }).toSet })
        .toSet
    fix(initialEquivClasses, Set[Set[Param]]())
  }

  private def findDuplicateInputs(stm: StmBuild): Set[Set[Param]] = {
    stm.producers
      .groupBy({ case (_, (s, ready, delay)) => (s, ready, delay) })
      .map({ case (_, eqns) => eqns.map({ case (x, _) => x }).toSet })
      .toSet
  }
}

// TODO: generalize to non-static lengths
private case class ShiftLeft(length: Long, input: Expr)

// TODO: this is very similar to code in mhir.gen (ClassifyVecAccumulators); try to deduplicate this code
private object ShiftLeft {

  def unapply(acc: (Param, (Expr, Expr, Expr))): Option[(Param, ShiftLeft)] = {
    acc match {
      case (
            x0,
            (
              _,
              VecBuild(
                IntCst(n),
                Function(
                  i0,
                  Mux(
                    Equal(i1, IntCst(nMinusOne)),
                    input,
                    VecAccess(x1, Sum(IntCst(1), i2))
                  )
                )
              ),
              _
            )
          ) if i1 == i0 && i2 == i0 && x1 == x0 && nMinusOne == n - 1 =>
        Some(x0 -> ShiftLeft(n, input))
      case (x, (_, VecBuild(IntCst(1), Function(i, input)), _))
          if !input.freeVars.contains(i) =>
        Some(x -> ShiftLeft(1, input))
      case _ => None
    }
  }
}
