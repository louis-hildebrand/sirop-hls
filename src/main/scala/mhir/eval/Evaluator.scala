package mhir.eval

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar.ExprLowering
import mhir.typecheck.TypeCheck

import scala.annotation.tailrec
import scala.language.{existentials, implicitConversions}

/** The evaluator.
  *
  * @param maxInvalidSteps
  *   the maximum number of steps without a valid output from a [[StmBuild]]
  *   node. If this is less than or equal to zero, then there is no limit.
  */
class Evaluator(val handshake: Boolean, val maxInvalidSteps: Int) {

  /** Evaluates an expression.
    *
    * @param e
    *   the expression to evaluate.
    * @throws mhir.typecheck.TypeError
    *   if the expression is ill-typed.
    * @throws EvalException
    *   if the evaluator encounters an undefined value <i>and it seems to affect
    *   the final value</i>, or if a stream seems to be deadlocked.
    */
  def eval(
      e: Expr,
      inputs: Map[Param, Expr] = Map(),
      stmData: Map[Param, Option[Expr]] = Map()
  ): Expr = {
    val expr = e.tchk().lower
    expr.typ match {
      case TyData(_) =>
        DataEvaluator.evalBigStep(stmData)(e.tchk().lower)
      case _: TyStm =>
        val pipe =
          StmPipeline(expr, inputs = inputs, handshake = this.handshake)
        evalPipeline(pipe, Seq(), Seq(), invalidSteps = 0)
      case _ =>
        // TODO: decide what to do in this case
        ???
    }
  }

  @tailrec
  private def evalPipeline(
      pipe: StmPipeline,
      physicalOutputsSoFar: Seq[Expr],
      logicalOutputsSoFar: Seq[Expr],
      invalidSteps: Int
  ): Expr = {
    if (pipe.isEmpty) {
      StmLiteral(physicalOutputsSoFar, logicalOutputsSoFar)(pipe.typ)
    } else if (pipe.isStuck) {
      throw new DeadlockError(pipe.deadlockReasons.toSeq)
    } else if (
      this.maxInvalidSteps > 0 && invalidSteps >= this.maxInvalidSteps
    ) {
      throw new DeadlockError(Seq(TooManySteps))
    } else {
      val nextPipe = pipe.step()
      if (nextPipe.reachedFixpoint(pipe)) {
        throw new DeadlockError(Seq(PipelineFixpoint))
      }
      pipe.sink.out(StmNodeId("")) match {
        case LogicalOutput(v) =>
          evalPipeline(
            nextPipe,
            physicalOutputsSoFar,
            logicalOutputsSoFar :+ v,
            invalidSteps = 0
          )
        case PhysicalOutput(v) =>
          evalPipeline(
            nextPipe,
            physicalOutputsSoFar :+ v,
            logicalOutputsSoFar,
            invalidSteps = 0
          )
        case NoOutput =>
          evalPipeline(
            nextPipe,
            physicalOutputsSoFar,
            logicalOutputsSoFar,
            invalidSteps = invalidSteps + 1
          )
      }
    }
  }
}

/** Companion object for [[Evaluator]].
  */
object Evaluator {

  private val DefaultMaxInvalidSteps: Int = 10000

  def apply(
      handshake: Boolean,
      maxInvalidSteps: Option[Int] = None
  ): Evaluator = {
    new Evaluator(
      handshake = handshake,
      maxInvalidSteps = maxInvalidSteps.getOrElse(DefaultMaxInvalidSteps)
    )
  }
}
