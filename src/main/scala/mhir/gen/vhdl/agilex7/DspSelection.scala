package mhir.gen.vhdl
package agilex7

import mhir.canonicalize._
import mhir.gen.vhdl.ir._
import mhir.gen.vhdl.transform.RemoveUnused
import mhir.ir._
import mhir.matchers.VecAccesses
import mhir.optimize.{InConsumer, StmOutputScheduler}
import mhir.typecheck.TypeCheck

/** Maps multiplications to native Agilex 7 DSP blocks, where possible.
  *
  * @note
  *   intermediate insertion should be run (or re-run) <i>after</i> this pass,
  *   since (1) this pass may construct Sirop expressions that are not valid
  *   VHDL expressions (see [[moveZeroDelayAfterRegister]]) and (2) this pass
  *   does not guarantee that all inputs of the DSP blocks are names, as
  *   required in VHDL.
  */
case class DspSelection(scheduler: StmOutputScheduler) {

  def apply(s: GenStmBuild): GenStmBuild = {
    val s1 = this.moveZeroDelayAfterRegister(s)
    val s2 = this.selectBasic(s1)
    val s3 = this.combineDsps(s2)
    // After combining DSPs, some intermediates might no longer be needed
    val s4 = RemoveUnused(s3)
    val s5 = this.enableChainInOut(s4)
    val s6 = this.mergeRegistersIntoDsps(s5)
    val s7 = RemoveUnused(s6)
    s7
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
  private def moveZeroDelayAfterRegister(s: GenStmBuild): GenStmBuild = {
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

  private def selectBasic(s: GenStmBuild): GenStmBuild = {
    val newIntermediates = s.accumulators.flatMap({
      case (acc, SumOf2Products(ax, ay, bx, by)) =>
        (ax.typ, ay.typ, bx.typ, by.typ) match {
          case (TySInt(axw), TySInt(ayw), TySInt(bxw), TySInt(byw))
              if axw <= 18 && ayw <= 19 && bxw <= 18 && byw <= 19 =>
            Some(acc -> AgilexMadd(ax, ay, bx, by, pipeline = 0))
          case (TyUInt(axw), TyUInt(ayw), TyUInt(bxw), TyUInt(byw))
              if axw <= 18 && ayw <= 18 && bxw <= 18 && byw <= 18 =>
            Some(acc -> AgilexMadd(ax, ay, bx, by, pipeline = 0))
          case _ => None
        }
      case (acc, BasicMac(x, y, chainin)) =>
        (x.typ, y.typ, chainin.typ) match {
          case (TySInt(wx), TySInt(wy), TySInt(wz))
              if wx <= 18 && wy <= 19 && wz <= 64 =>
            Some(acc -> AgilexMac1(x, y, chainin))
          case (TyUInt(wx), TyUInt(wy), TyUInt(wz))
              if wx <= 18 && wy <= 18 && wz <= 64 =>
            Some(acc -> AgilexMac1(x, y, chainin))
          case _ => None
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

  private def combineDsps(s: GenStmBuild): GenStmBuild = {
    val newIntermediates = s.intermediates.map({
      case i @ (x, AgilexMac1(bx, by, chaininB: Param)) =>
        s.intermediates.get(chaininB) match {
          case Some(AgilexMac1(ax, ay, chaininA)) =>
            x -> AgilexMac2(ax, ay, bx, by, chaininA, pipeline = 0)
          case _ =>
            i
        }
      case i => i
    })
    GenStmBuild(
      data = s.data,
      valid = s.valid,
      accumulators = s.accumulators,
      producers = s.producers,
      intermediates = newIntermediates
    )
  }

  private def enableChainInOut(s: GenStmBuild): GenStmBuild = {
    val renamings = s.intermediates
      .flatMap({
        case (_, AgilexMac2(_, _, _, _, chainin: Param, _)) =>
          s.intermediates.get(chainin) match {
            case Some(_: AgilexMac2) =>
              val chainInOutTyp = chainin.typ.asInstanceOf[TyAnyInt] match {
                case _: TySInt => TySInt(44)
                case _: TyUInt => TyUInt(44)
              }
              val newTarget =
                chainin.freshCopy
                  .rebuild(TyTuple(chainin.typ, chainInOutTyp))
                  .asInstanceOf[Param]
              Some(chainin -> newTarget)
            case _ => None
          }
        case _ => None
      })
    val s1 = enableChainOut(s, renamings)
    val s2 = enableChainIn(s1, renamings)
    s2
  }

  private def enableChainOut(
      s: GenStmBuild,
      renamings: Map[Param, Param]
  ): GenStmBuild = {
    val subs = renamings.mapValues(TupleAccess(_, 0)().tchk()).toMap[Expr, Expr]
    GenStmBuild(
      data = s.data.subPreserveType(subs),
      valid = s.valid.subPreserveType(subs),
      accumulators = s.accumulators.map({ case (x, acc) =>
        x -> acc.substitute(subs)
      }),
      producers = s.producers.map({ case (x, (p, ready)) =>
        x -> (p, ready.subPreserveType(subs))
      }),
      intermediates = s.intermediates.map({
        case (x, i) if renamings.contains(x) =>
          renamings(x) -> i.substitute(subs)
        case (x, i) =>
          x -> i.substitute(subs)
      })
    )
  }

  private def enableChainIn(
      s: GenStmBuild,
      renamings: Map[Param, Param]
  ): GenStmBuild = {
    val newNames = renamings.values.toSet
    GenStmBuild(
      data = s.data,
      valid = s.valid,
      accumulators = s.accumulators,
      producers = s.producers,
      intermediates = s.intermediates.map({
        case (
              x,
              AgilexMac2(
                ax,
                ay,
                bx,
                by,
                TupleAccess(chainin: Param, IntCst(0)),
                pipeline
              )
            ) if newNames.contains(chainin) =>
          x -> AgilexMac2(
            ax,
            ay,
            bx,
            by,
            TupleAccess(chainin, 1)().tchk(),
            pipeline
          )
        case i => i
      })
    )
  }

  private def mergeRegistersIntoDsps(s: GenStmBuild): GenStmBuild = {
    GenStmBuild(
      data = s.data,
      valid = s.valid,
      accumulators = s.accumulators,
      producers = s.producers,
      intermediates = s.intermediates.map({
        case intermediate @ (
              x,
              AgilexMac2(ax, ay, bx, by, chainin, pipeline)
            ) =>
          assert(pipeline >= 0)
          assert(pipeline <= 3)
          // How long is the shift register before each input?
          val gobble = math.min(
            3 - pipeline,
            Seq(ax, ay, bx, by).map(countAvailableRegisters(s, _)).min
          )
          // Perform the transformation
          if (gobble <= 0) {
            intermediate
          } else {
            val newMac = AgilexMac2(
              ax = shortenShiftRegister(s, ax, gobble),
              ay = shortenShiftRegister(s, ay, gobble),
              bx = shortenShiftRegister(s, bx, gobble),
              by = shortenShiftRegister(s, by, gobble),
              chainin,
              pipeline + gobble
            )
            x -> newMac
          }
        case intermediate @ (x, AgilexMadd(ax, ay, bx, by, pipeline)) =>
          assert(pipeline >= 0)
          assert(pipeline <= 0)
          // How long is the shift register before each input?
          // TODO: Delete this
          val availableRegisters =
            Seq(ax, ay, bx, by).map(countAvailableRegisters(s, _))
          val gobble = math.min(
            3 - pipeline,
            Seq(ax, ay, bx, by).map(countAvailableRegisters(s, _)).min
          )
          // Perform the transformation
          if (gobble <= 0) {
            intermediate
          } else {
            val newMadd = AgilexMadd(
              ax = shortenShiftRegister(s, ax, gobble),
              ay = shortenShiftRegister(s, ay, gobble),
              bx = shortenShiftRegister(s, bx, gobble),
              by = shortenShiftRegister(s, by, gobble),
              pipeline + gobble
            )
            x -> newMadd
          }
        case i =>
          i
      })
    )
  }

  private def countAvailableRegisters(s: GenStmBuild, input: Expr): Int = {
    input match {
      case _: IntCst => Int.MaxValue
      case VecAccesses(v: Param, Seq(IntCst(i), _*)) =>
        s.accumulators
          .get(v)
          .collect({ case VecShiftLeftAccumulator(n, None, _) =>
            (n - i).toInt
          })
          .getOrElse(0)
      case _ => 0
    }
  }

  private def shortenShiftRegister(
      s: GenStmBuild,
      input: Expr,
      gobble: Int
  ): Expr = {
    input match {
      case i: IntCst => i
      case VecAccesses(pipe: Param, Seq(IntCst(i), otherIndices @ _*)) =>
        val (len, pipeInput) = s.accumulators
          .get(pipe)
          .collect({ case VecShiftLeftAccumulator(len, None, pipeInput) =>
            (len, pipeInput)
          })
          .get
        val newIndex = i + gobble
        assert(len >= 0)
        assert(newIndex >= 0)
        assert(newIndex <= len)
        val newInput = if (newIndex == len) {
          // Bypass the shift register altogether
          VecAccesses(pipeInput, otherIndices).tchk()
        } else {
          VecAccesses(pipe, C(newIndex)() +: otherIndices).tchk()
        }
        assert(input.hasType, s"missing type for input")
        assert(
          newInput.typ == input.typ,
          s"input started with type ${input.typ} but now has type ${newInput.typ}"
        )
        newInput
      case e =>
        throw new IllegalArgumentException(
          s"invalid argument to shortenShiftRegister: $e"
        )
    }
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
