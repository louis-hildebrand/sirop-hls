package mhir.gen.vhdl
package agilex7

import mhir.canonicalize._
import mhir.gen.vhdl.transform.RemoveUnused
import mhir.ir._
import mhir.optimize.{InConsumer, StmOutputScheduler}
import mhir.typecheck.TypeCheck

case class DspSelection(scheduler: StmOutputScheduler) {

  def apply(s: GenStmBuild): GenStmBuild = {
    val s1 = this.moveZeroDelayAfterRegister(s)
    val s2 = this.selectBasic(s1)
    val s3 = this.combineDsps(s2)
    // After combining DSPs, some intermediates might no longer be needed
    val s4 = RemoveUnused.intermediates(s3)
    val s5 = this.enableChainInOut(s4)
    val s6 = this.mergeRegistersIntoDsps(s5)
    // TODO: Also need to ensure arguments in port map are either names or static (i.e., more intermediate insertion)?
    s6
  }

  /** Moves AST nodes with no combinational delay after the `data` output
    * register.
    *
    * For example, suppose the output of this `sbuild` is a multiplication, but
    * truncated and bitshifted and so on. This will hinder the passes that look
    * for multiplications going straight into an accumulator. Instead, the
    * accumulator should be the output of the multiplier and the truncating,
    * bitshifting, etc. should happen after the accumulator.
    */
  private def moveZeroDelayAfterRegister(s: GenStmBuild): GenStmBuild = {
    s.data match {
      case data: Param
          if s.accumulators.contains(data) && onlyUsedForDataOutput(data, s) =>
        s.accumulators.get(data) match {
          case Some(ExprAccumulator(init, ExprIntermediate(next))) =>
            this.scheduler.moveZeroDelayToConsumer(next) match {
              case InConsumer(cData, pData) if pData.size == 1 =>
                val (tmp, newNext) = pData.head
                val newVar = data.freshCopy.rebuild(tmp.typ).asInstanceOf[Param]
                val newAcc = ExprAccumulator(init, ExprIntermediate(newNext))
                val newOut = cData.subPreserveType(tmp -> newVar)
                GenStmBuild(
                  data = newOut,
                  valid = s.valid,
                  accumulators = (s.accumulators - data) + (newVar -> newAcc),
                  producers = s.producers,
                  intermediates = s.intermediates
                )
              case _ => s
            }
          case _ => s
        }
      case _ => s
    }
  }

  private def onlyUsedForDataOutput(x: Param, s: GenStmBuild): Boolean = {
    !(s.valid.freeVars ++
      s.accumulators.flatMap({ case (_, acc) => acc.freeVars }) ++
      s.producers.flatMap({ case (_, (_, ready)) => ready.freeVars }) ++
      s.intermediates.flatMap({ case (_, i) => i.freeVars })).contains(x)
  }

  private def selectBasic(s: GenStmBuild): GenStmBuild = {
    val newIntermediates = s.accumulators.flatMap({
      case (
            acc,
            ExprAccumulator(
              None,
              ExprIntermediate(Sum(chainin, Prod(PadOrIntCst(x), PadTo(y, _))))
            )
          ) =>
        (x.typ, y.typ, chainin.typ) match {
          case (TySInt(wx), TySInt(wy), TySInt(wz))
              if wx <= 18 && wy <= 19 && wz <= 64 =>
            Some(acc -> AgilexMac1(x, y, chainin))
          case (TyUInt(wx), TyUInt(wy), TyUInt(wz))
              if wx <= 18 && wy <= 18 && wz <= 64 =>
            Some(acc -> AgilexMac1(x, y, chainin))
          case _ => None
        }
      case (
            acc,
            ExprAccumulator(
              None,
              ExprIntermediate(Prod(PadOrIntCst(x), PadTo(y, _)))
            )
          ) =>
        (x.typ, y.typ, acc.typ) match {
          case (TySInt(wx), TySInt(wy), TySInt(wz))
              if wx <= 18 && wy <= 19 && wz <= 64 =>
            Some(acc -> AgilexMac1(x, y, C(0)(acc.typ)))
          case (TyUInt(wx), TyUInt(wy), TyUInt(wz))
              if wx <= 18 && wy <= 18 && wz <= 64 =>
            Some(acc -> AgilexMac1(x, y, C(0)(acc.typ)))
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
            case Some(_: AgilexMac1 | _: AgilexMac2) =>
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
              AgilexMac2(
                ax @ VecAccess(axPipe: Param, IntCst(0)),
                ay @ VecAccess(ayPipe: Param, IntCst(0)),
                bx @ VecAccess(bxPipe: Param, IntCst(0)),
                by @ VecAccess(byPipe: Param, IntCst(0)),
                chainin,
                pipeline
              )
            ) =>
          assert(pipeline >= 0)
          assert(pipeline <= 3)
          // How long is the shift register before each input?
          val (axPipeLen: Long, axPipeInput) = s.accumulators
            .get(axPipe)
            .map(axPipe -> _)
            .collect({ case VecShiftLeft(n, e) => (n, e) })
            .getOrElse((0L, Undefined(ax.typ)))
          val (ayPipeLen: Long, ayPipeInput) = s.accumulators
            .get(ayPipe)
            .map(ayPipe -> _)
            .collect({ case VecShiftLeft(n, e) => (n, e) })
            .getOrElse((0L, Undefined(ay.typ)))
          val (bxPipeLen: Long, bxPipeInput) = s.accumulators
            .get(bxPipe)
            .map(bxPipe -> _)
            .collect({ case VecShiftLeft(n, e) => (n, e) })
            .getOrElse((0L, Undefined(bx.typ)))
          val (byPipeLen: Long, byPipeInput) = s.accumulators
            .get(byPipe)
            .map(byPipe -> _)
            .collect({ case VecShiftLeft(n, e) => (n, e) })
            .getOrElse((0L, Undefined(by.typ)))
          // Absorb as many stages as possible into the DSP, subject to the
          // restriction that the DSP does not support more than 3 stages
          val gobble = math
            .min(
              3 - pipeline,
              Seq(axPipeLen, ayPipeLen, bxPipeLen, byPipeLen).max
            )
            .toInt
          // Perform the transformation
          if (gobble <= 0) {
            intermediate
          } else {
            x -> AgilexMac2(
              ax = shortenShiftRegister(axPipe, gobble, axPipeInput),
              ay = shortenShiftRegister(ayPipe, gobble, ayPipeInput),
              bx = shortenShiftRegister(bxPipe, gobble, bxPipeInput),
              by = shortenShiftRegister(byPipe, gobble, byPipeInput),
              chainin,
              pipeline + gobble
            )
          }
        case i => i
      })
    )
  }

  private def shortenShiftRegister(
      pipe: Param,
      skip: Int,
      pipeInput: Expr
  ): Expr = {
    val TyVec(_, IntCst(len)) = pipe.typ
    require(len >= 0)
    require(skip >= 0)
    require(skip <= len)
    if (skip == len) {
      // Bypass the shift register altogether
      pipeInput
    } else {
      VecAccess(pipe, C(skip)())().tchk()
    }
  }
}

private object VecShiftLeft {
  def unapply(arg: (Param, Accumulator)): Option[(Long, Expr)] = {
    arg match {
      case (
            x0,
            // TODO: Use special VecBuildAccumulator for this purpose instead?
            ExprAccumulator(
              None,
              ExprIntermediate(
                VecBuild(
                  IntCst(n),
                  Function(
                    i0,
                    Mux(
                      Equal(i1, IntCst(nMinusOne)),
                      e,
                      VecAccess(x1, Sum(IntCst(1), i2))
                    )
                  )
                )
              )
            )
          ) if x1 == x0 && i1 == i0 && i2 == i0 && nMinusOne == n - 1 =>
        Some((n, e))
      case (
            _,
            ExprAccumulator(
              None,
              ExprIntermediate(VecBuild(IntCst(1), Function(_, e)))
            )
          ) =>
        Some((1, e))
      case _ => None
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
