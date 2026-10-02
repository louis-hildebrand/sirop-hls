package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.matchers.{MapShiftLeft, ShiftLeftSelf, VecAccesses, NestedVecCst}
import mhir.optimize.{PartialEvalPass => PE}
import mhir.sugar._
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
          val (_, childNext, _) = stm.accumulators(child)
          childNext match {
            // CONDITION: The "child" vector must be a shift register.
            case MapShiftLeft(childParams, childLen, vec, input)
                if vec.forall(_ == child) =>
              // CONDITION: The "child" must be reading from some other vector (the "parent").
              input match {
                case VecAccesses(parent: Param, indices)
                    if stm.accumulators.contains(parent) && parent != child =>
                  // CONDITION: The "parent" vector must be a shift register.
                  val (_, parentNext, _) = stm.accumulators(parent)
                  parentNext match {
                    case MapShiftLeft(parentParams, parentLen, vec, parentInput)
                        if vec.forall(_ == parent) &&
                          indices.length > parentParams.length =>
                      // CONDITION: the "src" index must be an integer literal
                      // TODO: Lift this restriction on the "src" index?
                      val indicesBeforeSrc = indices.take(parentParams.length)
                      val src = indices(parentParams.length)
                      val indicesAfterSrc =
                        indices.drop(parentParams.length + 1)
                      src match {
                        case IntCst(src) =>
                          val newStm = mergeChainedShiftRegisters(
                            stm,
                            child,
                            childLen,
                            childParams,
                            parent,
                            parentLen,
                            parentParams,
                            parentInput,
                            indicesBeforeSrc,
                            src,
                            indicesAfterSrc
                          )
                          newStm match {
                            case Some(newStm) => merge(newStm, visited)
                            case _            => merge(stm, visited + child)
                          }
                        case _ => merge(stm, visited + child)
                      }
                    case _ => merge(stm, visited + child)
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

  private def combineInitAndDelay(
      childInit: Expr,
      childDelay: Expr,
      parentInit: Expr,
      parentDelay: Expr,
      combinedTyp: Type
  ): Option[(Expr, Expr)] = {
    val bothUndefined = childInit.isInstanceOf[Undefined] &&
      parentInit.isInstanceOf[Undefined]
    if (bothUndefined) {
      return Some((Undefined(Missing), Tuple()()))
    }

    (childInit, parentInit) match {
      case (NestedVecCst(k1), NestedVecCst(k2))
          if k1 == k2 && childDelay == parentDelay =>
        return Some((makeNestedVecCst(k1, combinedTyp), childDelay))
      case _ => ()
    }

    None
  }

  private def makeNestedVecCst(k: Expr, targetTyp: Type): Expr = {
    targetTyp match {
      case t if t == k.typ => k
      case TyVec(elemTyp, len) =>
        val i = Param("i")(U32)
        VecBuild(len, Function(i, makeNestedVecCst(k, elemTyp))())()
      case t =>
        throw new AssertionError(
          s"wrong type in makeNestedVecCst: $t" +
            " (expected a possibly nested vector with element type ${k.typ})"
        )
    }
  }

  private def mergeChainedShiftRegisters(
      stm: StmBuild,
      child: Param,
      childLen: Long,
      childParams: Seq[Param],
      parent: Param,
      parentLen: Long,
      parentParams: Seq[Param],
      parentInput: Expr,
      indicesBeforeSrc: Seq[Expr],
      src: Long,
      indicesAfterSrc: Seq[Expr]
  ): Option[StmBuild] = {
    val overlapLen = math.min(childLen, src)
    val deltaLen = childLen - overlapLen
    val combinedLen = parentLen + deltaLen
    def combineTyp(parentTyp: Type, dimensionsToUnwrap: Int): Type = {
      if (dimensionsToUnwrap <= 0) {
        val TyVec(elemTyp, _) = parentTyp
        TyVec(elemTyp, C(combinedLen)())
      } else {
        val TyVec(innerTyp, len) = parentTyp
        val updatedInnerTyp = combineTyp(innerTyp, dimensionsToUnwrap - 1)
        TyVec(updatedInnerTyp, len)
      }
    }
    val combinedTyp =
      combineTyp(parent.typ, dimensionsToUnwrap = parentParams.length)
    val combinedParam = Param(parent.prefix)(combinedTyp)
    // CONDITION: The initial values of the "child" and "parent" vectors
    //            must be the same in the overlapping region.
    val (childInit, _, childDelay) =
      stm.accumulators(child)
    val (parentInit, _, parentDelay) =
      stm.accumulators(parent)
    val combinedInitAndDelay = combineInitAndDelay(
      childInit,
      childDelay,
      parentInit,
      parentDelay,
      combinedTyp
    )
    combinedInitAndDelay match {
      case None => None
      case Some((combinedInit, combinedDelay)) =>
        @tailrec
        def getVecLengths(
            typ: Type,
            n: Int,
            acc: Seq[Expr] = Seq()
        ): Seq[Expr] = {
          assert(n >= 0)
          n match {
            case 0 => acc
            case n =>
              val TyVec(elemTyp, len) = typ
              getVecLengths(elemTyp, n - 1, len +: acc)
          }
        }
        // TODO: deduplicate this code
        val parentLengths = getVecLengths(parent.typ, parentParams.length)
        val combinedNext = {
          val inputOfShiftLeft = parentParams.foldLeft[Expr](combinedParam)({
            case (acc, i) => VecAccess(acc, i)().tchk()
          })
          val combinedShift = PartialEvalPass.partialEval(
            VecShiftLeft(inputOfShiftLeft, parentInput)().tchk().lower
          )
          // IMPORTANT: `parentInput` may refer to variables in `parentParams`.
          // Therefore, we need to use the same params; we cannot just add the required number of
          // VecMap calls.
          assert(parentParams.length == parentLengths.length)
          parentParams.reverse
            .zip(parentLengths)
            .foldLeft(combinedShift)({ case (v, (i, len)) =>
              VecBuild(len, Function(i, v)())().tchk()
            })
        }
        val newParent = {
          val inputOfTake = parentParams.foldLeft[Expr](combinedParam)({
            case (acc, i) => VecAccess(acc, i)().tchk()
          })
          val takeRight = PartialEvalPass.partialEval(
            VecTakeRight(inputOfTake, C(parentLen)())().tchk().lower
          )
          // IMPORTANT: `parentInput` may refer to variables in `parentParams`.
          // Therefore, we need to use the same params; we cannot just add the required number of
          // VecMap calls.
          assert(parentParams.length == parentLengths.length)
          parentParams.reverse
            .zip(parentLengths)
            .foldLeft(takeRight)({ case (v, (i, len)) =>
              VecBuild(len, Function(i, v)())().tchk()
            })
        }
        val newChild = {
          val innermostParam = Param("i")(U32)
          val childStartIndex = src + deltaLen - childLen
          // val inputOfTake = indicesToParent.foldLeft[Expr](combinedParam)({ case (acc, i) =>
          //   VecAccess(acc, i)().tchk()
          // })
          // val take = PartialEvalPass.partialEval(
          //   VecTakeRight(VecTake(inputOfTake, C(src + deltaLen)())(), C(childLen)())().tchk().lower
          // )
          var childBody: Expr = combinedParam
          childBody = indicesBeforeSrc.foldLeft[Expr](childBody)({
            case (acc, i) =>
              VecAccess(acc, i)().tchk()
          })
          childBody = VecAccess(
            childBody,
            Sum(C(childStartIndex)(innermostParam.typ), innermostParam)()
          )().tchk()
          childBody = indicesAfterSrc.foldLeft(childBody)({ case (acc, i) =>
            VecAccess(acc, i)().tchk()
          })
          val innermostVec =
            VecBuild(C(childLen)(), Function(innermostParam, childBody)())()
              .tchk()
          val childLengths = getVecLengths(child.typ, childParams.length)
          childParams.reverse
            .zip(childLengths)
            .foldLeft(innermostVec)({ case (v, (i, len)) =>
              VecBuild(len, Function(i, v)())().tchk()
            })
        }
        // TODO: Specifically apply the (vbuild(n) { f })[i] ~~> f[i] rule (just one pass)
        //       rather than doing full partial evaluation (with fixpoint iteration and everything)?
        Some(
          PartialEvalPass
            .partialEval(
              stm
                .addAccumulator(
                  combinedParam,
                  combinedInit,
                  combinedNext,
                  combinedDelay
                )
                .replaceVars(Map(child -> newChild, parent -> newParent))
            )
            .asInstanceOf[StmBuild]
        )
    }
  }

  private def mergeShiftRegistersWithSameInput(original: StmBuild): StmBuild = {
    val shiftRegisters = original.accumulators
      .collect({ case ShiftLeftSelf(x, shift) => x -> shift })
    val groups = shiftRegisters
      .map({ case (x, ShiftLeftSelf(_, input)) => x -> input })
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
            val ShiftLeftSelf(xLen, _) = shiftRegisters(x)
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
            // It's fine to use alphaEquals here because we know we're dealing
            // with vectors, not sbuild
            overlapInitFromRep alphaEquals overlapInitFromX
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
