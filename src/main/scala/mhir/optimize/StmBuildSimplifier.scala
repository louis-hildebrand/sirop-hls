package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir.Lowering.ExprLowering
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.logging.time
import mhir.optimize.{PartialEvalPass => PE}

import scala.annotation.tailrec

sealed trait StmBuildSimplifier {
  def enabled: Boolean

  def simplify(stm: StmBuild)(facts: FactSet = FactSet()): StmBuild
}

object StmBuildSimplifier {
  def apply(enabled: Boolean = true): StmBuildSimplifier = {
    if (enabled) EnabledStmBuildSimplifier else DisabledStmBuildSimplifier
  }
}

/** Transformation that combines many different simplifications.
  *
  * Simplification should make the expression easier to analyze. In some cases
  * it may make the hardware a bit worse (e.g., by distributing a product over a
  * sum).
  */
object EnabledStmBuildSimplifier extends StmBuildSimplifier {

  private implicit val logger: Logger = Logger(getClass.getName)

  override def enabled: Boolean = true

  /** Perform common-sense simplifications that should pretty much always be
    * beneficial, like partially evaluating and removing unused accumulator
    * elements.
    */
  override def simplify(stm: StmBuild)(facts: FactSet = FactSet()): StmBuild = {
    logger.trace(s"simplifying stream: $stm")
    time("simplifying stream") {
      simplifyUntilFixpoint(tl(stm), i = 0)(facts)
    }
  }

  @tailrec
  private def simplifyUntilFixpoint(s: StmBuild, i: Int)(
      facts: FactSet
  ): StmBuild = {
    val simplified = time(s"iteration $i stream simplification") {
      val s1 = time("partially evaluating stream") {
        implicit val fct: FactSet = facts
        val peStm = StmBuild(
          PE.partialEval(s.n),
          PE.partialEval(s.data),
          PE.partialEval(s.valid),
          s.equations.map({
            case (x, (s, ready)) if x.typ.isInstanceOf[TyStm] =>
              // Don't re-traverse input streams: they've already been
              // simplified
              val newReady = PE.partialEval(ready)
              x -> (s, newReady)
            case (x, (z, next)) =>
              val newZ = PE.partialEval(z)
              val newNext = PE.partialEval(next)
              x -> (newZ, newNext)
          })
        )()
        tl(peStm)
      }
      val s2 = time("removing unused accumulators") {
        tl(StmAccRemovalPass.removeUnusedVars(s1))
      }
      val s3 = time("removing constant accumulators") {
        tl(StmAccRemovalPass.removeConstantVars(s2))
      }
      val s4 = time("deduplicating accumulators") {
        tl(StmAccRemovalPass.deduplicateVars(s3))
      }
      val s5 = time("removing prefix counters") {
        tl(StmAccRemovalPass.removePrefixCounter(s4))
      }
      val s6 = time("removing accumulator trio special case") {
        tl(SpecialCaseStmBuildSimplifier.simplify(s5))
      }
      val s7 = time("shrinking counters") {
        tl(shrinkCounters(s6))
      }
      s7
    }
    val done =
      time("checking whether stream simplification has reached fixpoint") {
        simplified == s
      }
    if (done) {
      logger.trace(s"stream has reached fixpoint after ${i + 1} iterations")
      simplified
    } else {
      logger.trace(
        s"stream has not reached fixpoint yet after ${i + 1} iterations. Continuing simplification"
      )
      // New partial evaluation opportunities may have been revealed by
      // inlining constant-valued accumulator elements
      simplifyUntilFixpoint(simplified, i = i + 1)(facts)
    }
  }

  private def tl(s: Expr): StmBuild = {
    s.tchk().lower().asInstanceOf[StmBuild]
  }

  private def shrinkCounters(s: StmBuild): StmBuild = {
    s.equations
      .filter({ case (x, _) => x.typ.isInstanceOf[TyAnyInt] })
      .foldLeft(s)({
        case (
              acc,
              (
                x,
                (
                  IntCst(0),
                  Mux(Equal(x0, IntCst(lim)), IntCst(0), Sum(IntCst(1), x1))
                )
              )
            ) if x0 == x1 =>
          val oldTyp = x.typ.asInstanceOf[TyAnyInt]
          val newTyp = TyAnyInt.tightest(0, lim).asInstanceOf[TyUInt]
          val canShrink = newTyp.w > 0 && (
            newTyp.maxInt < oldTyp.maxInt && newTyp.minInt >= oldTyp.minInt
              || newTyp.maxInt <= oldTyp.maxInt && newTyp.minInt > oldTyp.minInt
          )
          if (!canShrink) {
            acc
          } else {
            val newX = Param(x.prefix)(newTyp)
            val s1 = acc.addAccumulator(
              newX,
              C(0)(newTyp),
              Mux(
                newX equ C(lim)(newTyp),
                C(0)(newTyp),
                Sum(newX, C(1)(newTyp))()
              )()
            )
            val subs =
              Map[Expr, Expr](x -> ReshapeData(newX, oldTyp)().tchk().lower())
            StmBuild(
              s1.n,
              s1.data.subPreserveType(subs),
              s1.valid.subPreserveType(subs),
              s1.equations
                .filter({ case (y, _) => y != x })
                .map({ case (y, (z, next)) =>
                  y -> (z, next.subPreserveType(subs))
                })
            )().tchk().asInstanceOf[StmBuild]
          }
        case (acc, _) => acc
      })
  }
}

object DisabledStmBuildSimplifier extends StmBuildSimplifier {
  override def enabled: Boolean = false

  override def simplify(stm: StmBuild)(facts: FactSet): StmBuild = stm
}
