package mhir.gen.vhdl
package agilex7

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.gen.vhdl.ir._
import mhir.ir._
import mhir.optimize.{InConsumer, StmOutputScheduler}
import mhir.typecheck.TypeCheck
import mhir.logging.time
import org.slf4j.event.Level

import scala.annotation.tailrec
import scala.collection.immutable.ListMap

/** Maps multiplications to native Agilex 7 DSP blocks, where possible.
  *
  * @note
  *   intermediate insertion should be run (or re-run) <i>after</i> this pass,
  *   since (1) this pass may construct Sirop expressions that are not valid
  *   VHDL expressions (see [[moveZeroDelayOpsAfterRegister]]) and (2) this pass
  *   does not guarantee that all inputs of the DSP blocks are names, as
  *   required in VHDL.
  */
case class DspSelection(scheduler: StmOutputScheduler) {

  private implicit val logger: Logger = Logger(getClass.getName)

  def apply(s: GenStmBuild): GenStmBuild = {
    time("DSP selection", Level.DEBUG) {
      val s1 = this.moveZeroDelayOpsAfterRegister(s)
      val s2 = this.recognizeBasicDsps(s1)
      val s3 = this.combineDsps(s2)
      // TODO: Remove unnecessary chainout ports?
      val s4 = DspRegisterPacking.apply(s3)
      s4
    }
  }

  /** In cases where the `next` expression in an accumulator is wrapped in
    * zero-delay computations (e.g., truncation, bitwise shifting), move those
    * zero-delay computations <i>after</i> the accumulator.
    *
    * For example, suppose the `next` expression is a multiplication, but
    * truncated and bitshifted and so on. This will hinder the passes that look
    * for multiplications going straight into an accumulator. Instead, the
    * accumulator should be the output of the multiplier and the truncating,
    * bitshifting, etc. should happen whenever this accumulator is <i>used</i>.
    */
  private def moveZeroDelayOpsAfterRegister(s: GenStmBuild): GenStmBuild = {
    s.accumulators
      .collect({
        // Don't bother doing this if it won't help with DSP selection.
        // In particular, I'm only interested in accumulators such that
        //  * there's no initial value (since I don't think it's possible to
        //    provide an arbitrary initial value for the pipeline registers in
        //    the DSPs)
        //  * there is some kind of multiplication happening somewhere in the
        //    'next' expression
        case (x, ExprAccumulator(None, ExprIntermediate(next)))
            if next.contains(classOf[Prod]) =>
          x -> next
      })
      .foldLeft(s)({ case (s, (x, next)) =>
        this.scheduler.moveZeroDelayToConsumer(next) match {
          case InConsumer(cData, pData) if pData.size == 1 =>
            val (tmp, newNext) = pData.head
            val newVar = x.freshCopy.rebuild(tmp.typ).asInstanceOf[Param]
            val newAcc = ExprAccumulator(None, ExprIntermediate(newNext))
            val subs = Map[Expr, Expr](
              x -> cData.subPreserveType(tmp -> newVar)
            )
            val updatedS = GenStmBuild(
              data = s.data.subPreserveType(subs),
              valid = s.valid.subPreserveType(subs),
              accumulators = (s.accumulators - x)
                .map({ case (x, acc) => x -> acc.substitute(subs) })
                .+(newVar -> newAcc),
              producers = s.producers.map({ case (x, (p, ready)) =>
                x -> (p, ready.subPreserveType(subs))
              }),
              intermediates = s.intermediates
                .map({ case (x, i) => x -> i.substitute(subs) })
            )
            updatedS
          case _ => s
        }
      })
  }

  /** Recognize simple cases of multiplication and addition.
    *
    * These basic cases may be combined into larger blocks later (i.e.,
    * [[combineDsps]]).
    *
    * @example
    *   turn `ax * ay + bx * by` into [[AgilexMadd]].
    * @example
    *   turn `ax * ay + b` into [[AgilexMac1]].
    */
  private def recognizeBasicDsps(s: GenStmBuild): GenStmBuild = {
    val newIntermediates = s.accumulators.flatMap({
      case (acc, SumOf2Products(ax, ay, bx, by)) =>
        val typesOk = (ax.typ, ay.typ, bx.typ, by.typ) match {
          case (TySInt(axw), TySInt(ayw), TySInt(bxw), TySInt(byw)) =>
            axw <= 18 && ayw <= 19 && bxw <= 18 && byw <= 19
          case (TyUInt(axw), TyUInt(ayw), TyUInt(bxw), TyUInt(byw)) =>
            axw <= 18 && ayw <= 18 && bxw <= 18 && byw <= 18
          case _ => false
        }
        if (typesOk) {
          val madd = AgilexMadd(ax, ay, bx, by, pipeline = 0)
          val fgi = ForGenerateIntermediate(madd)
          Some(acc -> fgi)
        } else {
          None
        }
      case (acc, BasicMac(x, y, chainin)) =>
        val typesOk = (x.typ, y.typ, chainin.typ) match {
          case (TySInt(wx), TySInt(wy), TySInt(wz)) =>
            wx <= 18 && wy <= 19 && wz <= 64
          case (TyUInt(wx), TyUInt(wy), TyUInt(wz)) =>
            wx <= 18 && wy <= 18 && wz <= 64
          case _ => false
        }
        if (typesOk) {
          val mac = AgilexMac1(x, y, chainin)
          val fgi = ForGenerateIntermediate(mac)
          Some(acc -> fgi)
        } else {
          None
        }
      case _ =>
        None
    })
    GenStmBuild(
      data = s.data,
      valid = s.valid,
      accumulators = s.accumulators -- newIntermediates.keySet,
      producers = s.producers,
      intermediates = s.intermediates ++ newIntermediates
    )
  }

  /** Combine chained [[AgilexMac1]] instances into individual [[AgilexMac2]]
    * instances.
    */
  @tailrec
  private def combineDsps(s: GenStmBuild): GenStmBuild = {
    this.findDspsToCombine(s) match {
      case Some((chainin, macA, macB)) =>
        combineDsps(combineDsps(s, chainin, macA, macB))
      case None =>
        s // nothing left to merge
    }
  }

  private def combineDsps(
      s: GenStmBuild,
      chainin: Expr,
      macA: Param,
      macB: Param
  ): GenStmBuild = {
    (s.intermediates(macA), s.intermediates(macB)) match {
      case (
            ForGenerateIntermediate(
              labelsA,
              varsA,
              boundsA,
              intermediatesA,
              AgilexMac1(ax, ay, _)
            ),
            ForGenerateIntermediate(
              _,
              _,
              boundsB,
              intermediatesB,
              AgilexMac1(bx, by, _)
            )
          )
          // The other lengths should match the length of these lists
          if boundsA == boundsB =>
        val newForGen = {
          val labelsAB = labelsA
          val varsAB = varsA.map(_.freshCopy)
          val newDsp = AgilexMac2(ax, ay, bx, by, chainin, pipeline = 0)
          assert(
            intermediatesA.isEmpty,
            s"intermediatesA should still be empty at this point"
          )
          assert(
            intermediatesB.isEmpty,
            s"intermediatesB should still be empty at this point"
          )
          ForGenerateIntermediate(
            labelsAB,
            varsAB,
            boundsA,
            ListMap.empty,
            newDsp
          )
        }
        val bTyp = macB.typ.asInstanceOf[TyAnyInt]
        val newTyp = TyTuple(bTyp /*result*/, bTyp.withWidth(44) /*chainout*/ )
        val newParam = Param(macB.prefix)(newTyp)
        val subs = Map[Expr, Expr](macB -> newParam.__0.tchk())
        GenStmBuild(
          data = s.data.subPreserveType(subs),
          valid = s.valid.subPreserveType(subs),
          accumulators = s.accumulators.map({ case (x, a) =>
            x -> a.substitute(subs)
          }),
          producers = s.producers.map({ case (x, (p, ready)) =>
            x -> (p, ready.subPreserveType(subs))
          }),
          intermediates = s.intermediates
            .-(macA)
            .-(macB)
            .map({ case (x, i) => x -> i.substitute(subs) })
            .+(newParam -> newForGen)
        )
      case _ =>
        throw new AssertionError(s"cannot combine DSPs $macA and $macB")
    }
  }

  private def findDspsToCombine(
      s: GenStmBuild
  ): Option[(Expr, Param, Param)] = {
    // Need to ensure the following conditions are met:
    //  (C1) Don't merge two instances of agilex_mac1 if the intermediate result
    //       is used elsewhere.
    //       In other words, I want to be able to delete both agilex_mac1 blocks
    //       immediately to be sure I don't accidentally increase the number of
    //       multiplications.
    //  (C2) The chainin port of an agilex_mac2 must be either
    //        (a) open (i.e., 0), or
    //        (b) connected to the chainout port of another agilex_mac1.
    //  (C3) The width of the result cannot exceed 44 bits.
    s.intermediates
      .flatMap({
        case (
              macB,
              ForGenerateIntermediate(
                _,
                _,
                _,
                boundsB,
                AgilexMac1(bx, by, macA: Param)
              )
            )
            if !bx.freeVars.contains(macA) &&
              !by.freeVars.contains(macA) =>
          // (C2) Check chainin port
          val chainin = s.intermediates.get(macA) match {
            case Some(
                  ForGenerateIntermediate(
                    _,
                    _,
                    _,
                    boundsA,
                    AgilexMac1(_, _, chaininA)
                  )
                ) if boundsA == boundsB =>
              chaininA match {
                case z @ IntCst(0) =>
                  val typ = z.typ.asInstanceOf[TyAnyInt]
                  Some(C(0)(typ.withWidth(44)))
                case TupleAccess(x: Param, IntCst(0)) =>
                  s.intermediates.get(x) match {
                    case Some(
                          ForGenerateIntermediate(_, _, _, _, _: AgilexMac2)
                        ) =>
                      x.typ match {
                        case TyTuple(TyAnyInt(w), TyAnyInt(44)) if w <= 44 =>
                          Some(x.__1.tchk())
                        case _ =>
                          throw new AssertionError(
                            s"output from $x has type ${x.typ}"
                          )
                      }
                    case _ => None
                  }
                case _ => None
              }
            case _ => None
          }
          // (C3) Check bitwidths
          val bitwidthsOk = (macA.typ, macB.typ) match {
            case (TyAnyInt(wA), TyAnyInt(wB)) => wA <= 44 && wB <= 44
            case _                            => false
          }
          // (C1) Check if macA is used elsewhere
          // TODO: Create a dependency graph beforehand to avoid this O(n^2) behaviour?
          //       DSP selection doesn't seem to be particularly slow at the
          //       moment, so maybe it's not such a big deal
          val usedElsewhere = (
            s.data.freeVars.contains(macA) ||
              s.valid.freeVars.contains(macA) ||
              s.accumulators.exists({ case (_, a) =>
                a.freeVars.contains(macA)
              }) ||
              s.producers.exists({ case (_, (_, ready)) =>
                ready.freeVars.contains(macA)
              }) ||
              s.intermediates.exists({ case (y, i) =>
                y != macB && i.freeVars.contains(macA)
              })
          )
          if (!usedElsewhere && chainin.nonEmpty && bitwidthsOk) {
            Some((chainin.get, macA, macB))
          } else {
            None
          }
        case _ => None
      })
      .headOption
  }
}

private object SumOf2Products {
  def unapply(arg: Accumulator): Option[(Expr, Expr, Expr, Expr)] = {
    arg match {
      case ExprAccumulator(
            None,
            ExprIntermediate(
              Sum(
                Prod(PadOrIntCst(ax), PadTo(ay, _)),
                Prod(PadOrIntCst(bx), PadTo(by, _))
              )
            )
          ) =>
        Some((ax, ay, bx, by))
      case _ =>
        None
    }
  }
}

private object BasicMac {
  def unapply(arg: Accumulator): Option[(Expr, Expr, Expr)] = {
    arg match {
      case ExprAccumulator(
            None,
            ExprIntermediate(Sum(chainin, Prod(PadOrIntCst(x), PadTo(y, _))))
          ) =>
        Some((x, y, chainin))
      case ExprAccumulator(
            None,
            ExprIntermediate(Sum(Prod(PadOrIntCst(x), PadTo(y, _)), chainin))
          ) =>
        Some((x, y, chainin))
      case ExprAccumulator(
            None,
            ExprIntermediate(prod @ Prod(PadOrIntCst(x), PadTo(y, _)))
          ) =>
        Some((x, y, C(0)(prod.typ)))
      case _ =>
        None
    }
  }
}

// TODO: Deduplicate this code
private object PadOrIntCst {
  def unapply(e: Expr): Option[Expr] = {
    e match {
      case PadTo(x, _) => Some(x)
      case IntCst(k) =>
        val originalTyp = e.typ.asInstanceOf[TyAnyInt]
        val typ = originalTyp.shrinkToFit(k)
        if (typ.w < originalTyp.w) {
          Some(IntCst(k)(typ))
        } else {
          None
        }
      case _ => None
    }
  }
}
