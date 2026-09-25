package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.logging.time
import mhir.matchers.ShiftLeft
import mhir.optimize.cost.SimpleDelayCostModel
import mhir.sugar._
import mhir.typecheck._
import org.slf4j.event.Level

import scala.annotation.tailrec

/** Transformation that shrinks shift registers if parts of them are unused.
  *
  * @note
  *   this transformation may change the delay annotations; it is therefore not
  *   safe to apply it after latency matching.
  */
trait ShiftRegisterShrinker {

  def applyRecursively(e: Expr): Expr

  def applyOnce(stm: StmBuild): Expr
}

object ShiftRegisterShrinker {

  def apply(
      delayCostModel: SimpleDelayCostModel,
      enabled: Boolean,
      handshake: Boolean
  ): ShiftRegisterShrinker = {
    // This pass does not work when the handshake protocol is enabled
    if (enabled && !handshake) {
      new EnabledShiftRegisterShrinker(delayCostModel)
    } else {
      DisabledShiftRegisterShrinker
    }
  }
}

object DisabledShiftRegisterShrinker extends ShiftRegisterShrinker {

  private val logger: Logger = Logger(getClass.getName)
  private var hasLogged: Boolean = false

  override def applyRecursively(e: Expr): Expr = {
    log()
    e
  }

  override def applyOnce(stm: StmBuild): Expr = {
    log()
    stm
  }

  private def log(): Unit = {
    if (!hasLogged) {
      hasLogged = true
      logger.debug("shift register shrinking is disabled")
    }
  }
}

class EnabledShiftRegisterShrinker(delayCostModel: SimpleDelayCostModel)
    extends ShiftRegisterShrinker {

  private implicit val logger: Logger = Logger(getClass.getName)

  def applyRecursively(e: Expr): Expr = {
    def rec(e0: Expr): Expr = {
      val e1 = e0.map(rec).tchk()
      e1 match {
        case s: StmBuild => this.applyOnce(s)
        case e           => e
      }
    }
    time("shrinking shift registers", Level.DEBUG) {
      val result = rec(e)
      assert(
        result.freeVars.subsetOf(e.freeVars),
        "no new free variables should have been introduced by shift register shrinking"
      )
      result
    }
  }

  def applyOnce(stm: StmBuild): Expr = {
    val shrinker = new Shrinker()
    val newStm = shrinker.runAndMutateBindings(stm)
    shrinker.bindings.foldRight[Expr](newStm)({ case ((x, e), acc) =>
      val TyStm(_, n) = e.typ
      LetStm(n, x, e, acc)().tchk()
    })
  }

  private class Shrinker {

    // Note that the order of the bindings matters!
    var bindings: Seq[(Param, Expr)] = Seq()

    def runAndMutateBindings(stm: StmBuild): StmBuild = {
      val shiftRegisters = stm.accumulators
        .collect({ case ShiftLeft(x, shift) => x -> shift })
        .toSet
      val maxIndexByVar = {
        val candidates = shiftRegisters.map({ case (x, _) => x })
        val finder = new VecAccessFinder(candidates)
        finder.run(stm.nextData)
        finder.run(stm.valid)
        stm.producers
          .map({ case (_, (_, ready, _)) => ready })
          .foreach(finder.run(_))
        stm.accumulators
          .map({ case (x, (_, next, _)) => x -> next })
          .foreach({ case (x, e) => finder.run(e, ignore = x) })
        finder.maxIndexByVar
      }
      val maxIndexInSinkByVar = {
        val candidates = maxIndexByVar.keySet
        val finder = new VecAccessFinder(candidates)
        stm.sinkAnnotation.foreach(sink => finder.run(sink))
        finder.maxIndexByVar
      }
      // TODO: do this in a single pass, rather than one pass per variable
      val shiftRegisterUses = shiftRegisters
        .flatMap({ case (v, ShiftLeft(len, input)) =>
          (maxIndexByVar.get(v), maxIndexInSinkByVar.get(v)) match {
            case (Some(maxIndexInBody), Some(maxIndexInSink)) =>
              val maxIndex = math.max(maxIndexInBody, maxIndexInSink)
              val delayCost = delayCostModel.rawCost(
                input,
                varCosts = stm.namesDefinedHere.map(_ -> 0L).toMap
              )
              val canOmitOneMore = (
                // Don't omit one more if it will increase the combinational delay
                delayCost == 0
                // Don't omit one more if the last index is specified as a
                // sink; the sink annotation is meant to signal that the
                // given index of the shift register should be preserved.
                  && maxIndex != maxIndexInSink
              )
              val isConstant = input.freeVars.isEmpty
              // We have a situation like
              //                     |
              //                     v
              //   +---+---+---+---+---+
              //   | 0 | 1 | 2 | 3 | 4 |
              //   +---+---+---+---+---+
              //     |   |   |
              //     v   v   v
              val newLen = if (isConstant) {
                // If the input is constant, why bother with the shift register
                // at all?
                0
              } else if (!canOmitOneMore) {
                // We could change this to the following:
                //             |
                //             v
                //   +---+---+---+
                //   | 0 | 1 | 2 |
                //   +---+---+---+
                //     |   |   |
                //     v   v   v
                maxIndex + 1
              } else {
                // But if the input has no combinational delay, why not go even further?
                //              |
                //              |
                //   +---+---+  |
                //   | 0 | 1 |<-+
                //   +---+---+  |
                //     |   |    |
                //     v   v    v
                maxIndex
              }
              if (newLen < len) {
                Some(v -> newLen)
              } else {
                None
              }
            case _ => None
          }
        })
        .toMap
      shiftRegisterUses
        .foldLeft(stm)({ case (stm, (v, newLen)) =>
          shrinkShiftRegister(stm, v, newLen)
        })
    }

    @tailrec
    private def shrinkShiftRegister(
        stm: StmBuild,
        v: Param,
        newLen: Long
    ): StmBuild = {
      val dependencies = stm.internalDependencies
      val vDependencies = dependencies
        .transitiveDependencies(Set(v))
        // Don't worry about the fact that v depends on itself
        .-(v)
      // We'll need to change the delay of any producers feeding into the shift
      // register.
      // If a producer is used in the shift register and also elsewhere, then
      // make a copy so we can change the delay going into the shift register
      // but preserve the delay elsewhere.
      val producersToCopy = vDependencies
        .filter(stm.producers.contains)
        .filter({ x =>
          stm.nextData.freeVars.contains(x) ||
          stm.valid.freeVars.contains(x) ||
          dependencies.inNeighbours(x).-(v).nonEmpty
        })
      val ShiftLeft(_, ShiftLeft(oldLen, input)) = v -> stm.accumulators(v)
      val (oldInit, _, oldDelay) = stm.accumulators(v)
      val deltaDelay = oldLen - newLen
      val outDelay = stm.delay match {
        case IntCst(delay) => Some(delay)
        case _             => None
      }
      // Watch out! We don't want the out delay annotation to be less
      // than or equal to the producer delay annotation.
      // If this happens and the producer data arrives immediately
      // after reset, it means the first valid output should be
      // *before* reset, which makes no sense.
      val updatedDelaysWillBeValid = outDelay.isEmpty || vDependencies
        .filter(stm.producers.contains)
        .forall({ x =>
          val (_, _, xDelay) = stm.producers(x)
          xDelay match {
            case IntCst(xDelay) => xDelay + deltaDelay < outDelay.get
            case _              => false
          }
        })
      // TODO: use lazy vals to avoid unnecessary evaluation of the above code?
      val ok = !input.freeVars.contains(v) &&
        // TODO: also support cases where some other accumulator is feeding into the shift register?
        //       Would need to be careful about which producers' delays to update and which to preserve.
        //       Would also need to carefully consider whether it's worth it:
        //       it might require duplicating the accumulator (similarly to duplicating the producers).
        !vDependencies.exists(stm.accumulators.contains) &&
        // TODO: also support some cases where the initial value is not undefined?
        //       Maybe if all the elements equal some constant and the heads of the relevant producers are
        //       such that the shift register input will be that same constant...
        oldInit.isInstanceOf[Undefined] &&
        // TODO: in this case, try with a larger value of newLen rather than giving up?
        updatedDelaysWillBeValid
      if (!ok) {
        stm
      } else if (producersToCopy.nonEmpty) {
        shrinkShiftRegister(
          this.copyProducers(stm, v, producersToCopy),
          v,
          newLen
        )
      } else {
        val elemTyp = input.typ
        val newV = Param(v.prefix)(TyVec(elemTyp, C(newLen)()))
        // Append the next input, in case we decided to omit the last part of
        // the shift register
        val newVPlusOne = if (input.freeVars.isEmpty) {
          VecCst(C(oldLen)(), input)().tchk().lower
        } else {
          PartialEvalPass.partialEval(
            VecAppend(newV, input)().tchk().lower
          )
        }
        val subs = Map[Expr, Expr](v -> newVPlusOne)
        val newInit = PartialEvalPass.partialEval(
          VecTake(oldInit, C(newLen)())().tchk().lower
        )
        val newNext = PartialEvalPass.partialEval(
          VecShiftLeft(newV, input)().tchk().lower
        )
        val newDelay = {
          assert(
            oldInit.isInstanceOf[Undefined] || oldInit.typ == TyTuple(),
            s"since the initial value of the shift register is $oldInit (not undefined), the delay annotation needs to be updated"
            // while you're at it, don't forget to add a unit test
          )
          oldDelay
        }
        StmBuild(
          n = stm.n,
          delay = stm.delay,
          initData = stm.initData,
          nextData = stm.nextData.subAndEraseType(subs),
          valid = stm.valid.subPreserveType(subs),
          accumulators = stm.accumulators
            .-(v)
            .map({ case (x, (init, next, delay)) =>
              assert(
                !vDependencies.contains(x),
                s"since the shift register depends on accumulator $x, the delay annotation of $x may need to be updated and $x may need to be duplicated"
              )
              x -> (init, next.subAndEraseType(subs), delay)
            })
            .+(newV -> (newInit, newNext, newDelay)),
          producers = stm.producers
            .map({ case (x, (stm1, ready, delay)) =>
              val newDelay = if (vDependencies.contains(x)) {
                delay.updateDelayIfPresent { delay =>
                  PartialEvalPass.partialEval(
                    SafeSum(delay, C(deltaDelay)())().tchk().lower
                  )
                }
              } else {
                delay
              }
              val newReady = ready.subAndEraseType(subs)
              x -> (stm1, newReady, newDelay)
            })
        )(annotations = stm.annotations).tchk().asInstanceOf[StmBuild]
      }
    }

    private def copyProducers(
        stm: StmBuild,
        shift: Param,
        producersToCopy: Set[Param]
    ): StmBuild = {
      require(stm.accumulators.contains(shift))
      require(producersToCopy.forall(stm.producers.contains))
      val copies = producersToCopy.map(x => x -> x.freshCopy)
      val producersToAdd = copies.flatMap({ case (xOld, xNew) =>
        val (s, ready, delay) = stm.producers(xOld)
        val x = s match {
          case s: Param => s.freshCopy
          case _        => Param("s")(s.typ)
        }
        this.bindings = this.bindings :+ (x -> s)
        Seq(
          xOld -> (x, ready, delay),
          xNew -> (x, ready, delay)
        )
      })
      val subs = copies.toMap[Expr, Expr]
      StmBuild(
        stm.n,
        stm.delay,
        stm.initData,
        stm.nextData,
        stm.valid,
        stm.accumulators.map({
          case (y, (init, next, ready)) if y == shift =>
            val newNext = next.subPreserveType(subs)
            y -> (init, newNext, ready)
          case eqn => eqn
        }),
        (stm.producers -- producersToCopy) ++ producersToAdd
      )(annotations = stm.annotations).tchk().asInstanceOf[StmBuild]
    }
  }

  private object VecAccessFinder {
    private val dummy: Param = Param("dummy")()
  }

  private class VecAccessFinder(candidates: Iterable[Param]) {

    /** Maximum index accessed in each vector-valued variable.
      *
      * If the index is negative, it means no uses have been found yet. If a
      * variable is removed from the map, it means the whole vector is needed.
      */
    val maxIndexByVar: scala.collection.mutable.Map[Param, Long] =
      scala.collection.mutable.Map(candidates.map(_ -> -1L).toSeq: _*)

    def run(e: Expr, ignore: Param = VecAccessFinder.dummy): Unit = {
      if (this.maxIndexByVar.isEmpty) {
        return
      }
      e match {
        case VecAccess(v: Param, IntCst(i)) if v != ignore =>
          maxIndexByVar.get(v) match {
            case Some(j) => maxIndexByVar(v) = math.max(i, j)
            case None    => ()
          }
        case v: Param if v != ignore => maxIndexByVar.remove(v)
        case e                       => e.children.foreach(this.run(_, ignore))
      }
    }
  }
}
