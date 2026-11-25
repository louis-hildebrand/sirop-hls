package mhir.gen.vhdl
package agilex7

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.gen.vhdl.ir._
import mhir.ir._
import mhir.logging.time
import mhir.matchers.{TyVecs, VecAccesses, VecBuilds}
import mhir.optimize.{InConsumer, PartialEvalPass => PE, StmOutputScheduler}
import mhir.typecheck.TypeCheck
import org.slf4j.event.Level

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
      val s2 = this.recognizeDspBlocks(s1)
      // TODO: Remove unnecessary chainout ports?
      val s3 = DspRegisterPacking.apply(s2)
      s3
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
    s.intermediates
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
        // TODO: This is pretty annoying. Can I get the ClassifyVecAccumulators pass to not do this?
        case (
              x,
              ForGenerateIntermediate(
                loopVars,
                bounds,
                VecShiftLeftAccumulator(1, None, incoming)
              )
            ) if incoming.contains(classOf[Prod]) =>
          val next = VecBuilds(
            bounds.map(_ + 1).:+(1).map(C(_)(U32)),
            loopVars :+ Param("tmp")(U32),
            incoming
          ).tchk()
          assert(next.typ == x.typ)
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
              producers = s.producers.map({ case (x, (p, ready)) =>
                x -> (p, ready.subPreserveType(subs))
              }),
              intermediates = (s.intermediates - x)
                .map({ case (x, i) => x -> i.substitute(subs) })
                .+(newVar -> newAcc)
            )
            updatedS
          case _ => s
        }
      })
  }

  private def recognizeDspBlocks(s: GenStmBuild): GenStmBuild = {
    val dependencyGraph = {
      val nodes = s.intermediates.keySet
      val edges = nodes.flatMap(x =>
        s.intermediates(x).freeVars.intersect(nodes).map(y => x -> y)
      )
      DiGraph(nodes, edges)
    }
    val intermediatesInOrder = dependencyGraph.topologicalIshOrder()
    intermediatesInOrder.foldLeft(s)({ case (s, x) =>
      recognizeSumOf2Dsp(s, x)
        .orElse(recognizeSystolicDsp(s, x))
        .getOrElse(s)
    })
  }

  private def recognizeSumOf2Dsp(
      s: GenStmBuild,
      outVar: Param
  ): Option[GenStmBuild] = {
    val (lengths, loopVars, ax, ay, bx, by, cin) =
      s.intermediates(outVar) match {
        case ExprAccumulator(
              None,
              ExprIntermediate(
                VSumOf2Products(lengths, loopVars, ax, ay, bx, by, cin)
              )
            ) =>
          (lengths, loopVars, ax, ay, bx, by, cin)
        case ForGenerateIntermediate(
              loopVars,
              lengths,
              // TODO: This is pretty annoying. Can I get the ClassifyVecAccumulators pass to not do this?
              VecShiftLeftAccumulator(
                1,
                None,
                SumOf2Products(ax, ay, bx, by, cin)
              )
            ) =>
          (lengths :+ 1, loopVars :+ Param("tmp")(U32), ax, ay, bx, by, cin)
        case _ =>
          return None
      }
    // Need to ensure the following conditions are met:
    //    (C1) The chainin port of an agilex_mac2 must be either
    //          (a) open (i.e., 0), or
    //          (b) connected to the chainout port of another DSP.
    //    (C2) The width of the result cannot exceed 44 bits.
    // ---------------------------------------------------------------------
    // (C2) Check bitwidths
    // TODO! I need to check the output type too.
    //       If the output is small enough (<= 64 bits), I can emit a DSP with both result and chainout.
    //       If the output is too big, I guess I can just skip the chainout port.
    val typesOk = (ax.typ, ay.typ, bx.typ, by.typ, cin.typ) match {
      case (TySInt(ax), TySInt(ay), TySInt(bx), TySInt(by), TySInt(c)) =>
        ax <= 18 && ay <= 19 && bx <= 18 && by <= 19 && c <= 64
      case (TyUInt(ax), TyUInt(ay), TyUInt(bx), TyUInt(by), TyUInt(c)) =>
        ax <= 18 && ay <= 18 && bx <= 18 && by <= 18 && c <= 64
      case _ => false
    }
    if (!typesOk) {
      return None
    }
    // (C1) The chainin port must be connected to another DSP, or else to zero
    getCombinedChainin(s, cin, loopVars, lengths, 64) match {
      case Some(chainin) =>
        val madd = AgilexMadd(ax, ay, bx, by, chainin, pipeline = 0)
        val loopBounds = {
          // e.g., if the vector length is 4 then the loop goes from 0 to 3
          lengths.map(_ - 1)
        }
        val newForGen =
          ForGenerateIntermediate(loopVars, loopBounds, madd)
        // TODO: Deduplicate this code?
        val TyVecs(bElemTyp: TyAnyInt, bLengths) = outVar.typ
        val newTyp = TyVecs(
          // (result, chainout)
          TyTuple(bElemTyp, bElemTyp.withWidth(64)),
          bLengths
        )
        val newOutVar = Param(outVar.prefix)(newTyp)
        val outVarReplacement = VecBuilds(
          bLengths.reverse,
          loopVars,
          VecAccesses(newOutVar, loopVars).__0
        ).tchk()
        val subs = Map[Expr, Expr](outVar -> outVarReplacement)
        val result = GenStmBuild(
          // TODO: Won't partial evaluation slow things down too much?
          data = PE.partialEval(s.data.subPreserveType(subs)),
          valid = PE.partialEval(
            s.valid.subPreserveType(subs)
          ),
          producers = s.producers.map({ case (x, (p, ready)) =>
            val newReady = PE.partialEval(ready.subPreserveType(subs))
            x -> (p, newReady)
          }),
          intermediates = s.intermediates
            .-(outVar)
            .map({ case (x, i) =>
              x -> i.substitute(subs).map(PE.partialEval(_))
            })
            .+(newOutVar -> newForGen)
        )
        assert(
          result.freeVars == s.freeVars,
          s"free variables changed: ${s.freeVars} to ${result.freeVars}"
        )
        Some(result)
      case None => None
    }
  }

  private def recognizeSystolicDsp(
      s: GenStmBuild,
      x: Param
  ): Option[GenStmBuild] = {
    (s, x, s.intermediates(x)) match {
      case (
            s,
            macB,
            ExprAccumulator(
              None,
              ExprIntermediate(
                VBasicMac(
                  lengthsB,
                  loopVarsB,
                  bx,
                  by,
                  chaininB @ VecAccesses(macA: Param, indicesB)
                )
              )
            )
          )
          if indicesB == loopVarsB
            && !bx.freeVars.contains(macA)
            && !by.freeVars.contains(macB) =>
        // Need to ensure the following conditions are met:
        //    (C1) Suppose macB looks like bx*by + macA and macA looks like
        //         ax*ay + c.
        //         Don't merge these into an agilex_mac2 instance if macA is
        //         used elsewhere.
        //         In other words, I want to be able to delete macA immediately
        //         to be sure I don't accidentally increase the number of
        //         multiplications.
        //    (C2) The chainin port of an agilex_mac2 must be either
        //          (a) open (i.e., 0), or
        //          (b) connected to the chainout port of another DSP.
        //    (C3) The width of the result cannot exceed 44 bits.
        //    (C4) The loop bounds must be the same for macA and macB.
        // ---------------------------------------------------------------------
        // (C1) Check if macA is used elsewhere
        // TODO: Create a dependency graph beforehand to avoid this O(n^2) behaviour?
        //       DSP selection doesn't seem to be particularly slow at the
        //       moment, so maybe it's not such a big deal.
        //       The current dependency graph is not quite good enough because
        //       it only includes intermediates---not producers, data, or valid.
        val macAUsedElsewhere = (
          s.data.freeVars.contains(macA) ||
            s.valid.freeVars.contains(macA) ||
            s.producers.exists({ case (_, (_, ready)) =>
              ready.freeVars.contains(macA)
            }) ||
            s.intermediates.exists({ case (y, i) =>
              y != macB && i.freeVars.contains(macA)
            })
        )
        if (macAUsedElsewhere) {
          return None
        }
        s.intermediates.get(macA) match {
          case Some(
                ExprAccumulator(
                  None,
                  ExprIntermediate(
                    VBasicMac(lengthsA, loopVarsA, ax, ay, chaininA)
                  )
                )
              ) =>
            // (C4) Check loop bounds
            if (lengthsA != lengthsB) {
              return None
            }
            // (C3) Check bitwidths
            val typesOk =
              areTypesOkForSystolicDsp(chaininA, ax, ay, chaininB, bx, by, x)
            if (!typesOk) {
              return None
            }
            // (C2) The chainin port must be connected to another DSP, or else to zero
            val chainin =
              getCombinedChainin(s, chaininA, loopVarsA, lengthsA, 44) match {
                case Some(x) => x
                case None    => return None
              }
            val varsAB = loopVarsA.map(_.freshCopy)
            val newForGen = {
              val subsA = loopVarsA.zip(varsAB).toMap[Expr, Expr]
              val subsB = loopVarsB.zip(varsAB).toMap[Expr, Expr]
              val newDsp = AgilexMac2(
                ax.subPreserveType(subsA),
                ay.subPreserveType(subsA),
                bx.subPreserveType(subsB),
                by.subPreserveType(subsB),
                chainin.subPreserveType(subsA),
                pipeline = 0
              )
              val loopBounds = {
                // e.g., if the vector length is 4 then the loop goes from 0 to 3
                lengthsA.map(_ - 1)
              }
              ForGenerateIntermediate(varsAB, loopBounds, newDsp)
            }
            val TyVecs(bElemTyp: TyAnyInt, bLengths) = macB.typ
            val newTyp = TyVecs(
              // (result, chainout)
              TyTuple(bElemTyp, bElemTyp.withWidth(44)),
              bLengths
            )
            val newParam = Param(macB.prefix)(newTyp)
            val macBReplacement = VecBuilds(
              bLengths.reverse,
              varsAB,
              VecAccesses(newParam, varsAB).__0
            ).tchk()
            val subs = Map[Expr, Expr](macB -> macBReplacement)
            val result = GenStmBuild(
              // TODO: Won't partial evaluation slow things down too much?
              data = PE.partialEval(s.data.subPreserveType(subs)),
              valid = PE.partialEval(
                s.valid.subPreserveType(subs)
              ),
              producers = s.producers.map({ case (x, (p, ready)) =>
                val newReady = PE.partialEval(ready.subPreserveType(subs))
                x -> (p, newReady)
              }),
              intermediates = s.intermediates
                .-(macA)
                .-(macB)
                .map({ case (x, i) =>
                  x -> i.substitute(subs).map(PE.partialEval(_))
                })
                .+(newParam -> newForGen)
            )
            assert(
              result.freeVars == s.freeVars,
              s"free variables changed: ${s.freeVars} to ${result.freeVars}"
            )
            Some(result)
          case _ => None
        }
      case _ => None
    }
  }

  private def areTypesOkForSystolicDsp(
      chaininA: Expr,
      ax: Expr,
      ay: Expr,
      chaininB: Expr,
      bx: Expr,
      by: Expr,
      output: Expr
  ): Boolean = {
    // TODO: Also check output?
    (
      ax.typ,
      ay.typ,
      chaininA.typ,
      bx.typ,
      by.typ,
      chaininB.typ,
      output.typ
    ) match {
      // TODO: Make this less ugly by checking one type at a time, instead of
      //       putting them into one big tuple?
      case (
            TySInt(wax),
            TySInt(way),
            TySInt(wac),
            TySInt(wbx),
            TySInt(wby),
            TySInt(wbc),
            TyVecs(TySInt(wout), _)
          ) =>
        wax <= 18 && way <= 19 && wbx <= 18 && wby <= 19 && wac <= 44 && wbc <= 44 && wout <= 44
      case (
            TyUInt(wax),
            TyUInt(way),
            TyUInt(wac),
            TyUInt(wbx),
            TyUInt(wby),
            TyUInt(wbc),
            TyVecs(TyUInt(wout), _)
          ) =>
        wax <= 18 && way <= 18 && wbx <= 18 && wby <= 19 && wac <= 44 && wbc <= 44 && wout <= 44
      case _ => false
    }
  }

  private def getCombinedChainin(
      s: GenStmBuild,
      chainin: Expr,
      loopVars: Seq[Param],
      loopLengths: Seq[Int],
      desiredWidth: Int
  ): Option[Expr] = {
    chainin match {
      case z @ IntCst(0) =>
        val typ = z.typ.asInstanceOf[TyAnyInt]
        Some(C(0)(typ.withWidth(desiredWidth)))
      case TupleAccess(va @ VecAccesses(x: Param, indices), IntCst(0)) =>
        val indicesMatchLoopVars = indices
          .zip(loopVars)
          .zip(loopLengths)
          .forall({
            // If the index is 0 and the loopVar goes from 0 to 0, that's fine too
            case ((IntCst(0), _), 1) => true
            case ((i1, i2), _)       => i1 == i2
          })
        if (!indicesMatchLoopVars) {
          return None
        }
        s.intermediates.get(x) match {
          case Some(
                // TODO: Can you really have a dsp_sumof2 reading from a dsp_systolic, for example?
                ForGenerateIntermediate(loopVars, _, _: AgilexDsp)
              ) if loopVars.length == indices.length =>
            va.typ match {
              case TyTuple(TyAnyInt(w), TyAnyInt(w2))
                  if w <= desiredWidth && w2 == desiredWidth =>
                Some(VecAccesses(x, indices).__1.tchk())
              case _ =>
                throw new AssertionError(
                  s"output from $x has type ${x.typ}; expected a 2-tuple (i{w}, i$desiredWidth) with w <= $desiredWidth"
                )
            }
          case _ => None
        }
      case _ => None
    }
  }
}

private object VSumOf2Products {
  def unapply(
      e: Expr
  ): Option[(Seq[Int], Seq[Param], Expr, Expr, Expr, Expr, Expr)] = {
    e match {
      case VecBuilds(lengths, variables, SumOf2Products(ax, ay, bx, by, c)) =>
        Some((lengths, variables, ax, ay, bx, by, c))
      case _ => None
    }
  }
}

private object SumOf2Products {
  def unapply(
      e: Expr
  ): Option[(Expr, Expr, Expr, Expr, Expr)] = {
    e match {
      // TODO: Check similar cases where the terms are in a different order?
      case Sum(
            chainin,
            Prod(PadOrIntCst(ax), PadTo(ay, _)),
            Prod(PadOrIntCst(bx), PadTo(by, _))
          ) =>
        Some((ax, ay, bx, by, chainin))
      case sum @ Sum(
            Prod(PadOrIntCst(ax), PadTo(ay, _)),
            Prod(PadOrIntCst(bx), PadTo(by, _))
          ) =>
        // Use width 1 because i0 is not a valid type
        val zTyp = sum.typ.asInstanceOf[TyAnyInt].withWidth(1)
        Some((ax, ay, bx, by, C(0)(zTyp)))
      case _ =>
        None
    }
  }
}

private object VBasicMac {
  def unapply(e: Expr): Option[(Seq[Int], Seq[Param], Expr, Expr, Expr)] = {
    e match {
      case VecBuilds(lengths, variables, BasicMac(x, y, chainin)) =>
        Some((lengths, variables, x, y, chainin))
      case _ => None
    }
  }
}

private object BasicMac {
  def unapply(e: Expr): Option[(Expr, Expr, Expr)] = {
    e match {
      case Sum(chainin, Prod(PadOrIntCst(x), PadTo(y, _))) =>
        Some((x, y, chainin))
      case Sum(Prod(PadOrIntCst(x), PadTo(y, _)), chainin) =>
        Some((x, y, chainin))
      case prod @ Prod(PadOrIntCst(x), PadTo(y, _)) =>
        // Use width 1 because i0 is not a valid type
        val zTyp = prod.typ.asInstanceOf[TyAnyInt].withWidth(1)
        Some((x, y, C(0)(zTyp)))
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
