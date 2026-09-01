package mhir.delay

import mhir.canonicalize._
import mhir.ir._
import mhir.sem.SemanticError
import mhir.sugar.{ExprLowering, SmartDiff}
import mhir.typecheck._

/** Transformation that replaces all accumulator delays by reading from the
  * given "go" stream.
  */
class ReplaceAccumulatorDelaysWithGo(go: Option[Param]) {

  def apply(e: Expr): Expr = {
    e match {
      case s0: StmBuild =>
        val s1 = s0
          .mapProducers({ case (x, (stm, ready, delay)) =>
            x -> (this.apply(stm), ready, delay)
          })
          .tchk()
          .asInstanceOf[StmBuild]
        // TODO: also remove delay for accumulators whose initial value is undefined?
        val delays = s1.accumulators
          .collect({
            case (_, (_, _, delay)) if !delay.typ.isInstanceOf[TyTuple] =>
              delay
          })
          .toSet
        if (delays.isEmpty) {
          s1
        } else {
          this.go match {
            case None =>
              val example = s0.nameAnnotation
                .map(name => s" (e.g., in $name)")
                .getOrElse("")
              // TODO: make this a warning instead of an error?
              throw SemanticError(
                s"the program seems to be latency-sensitive$example, but no 'go' stream is specified."
                  + " Consider adding a stream of booleans as input and adding the 'go' annotation (as in 'accelerator[go=my_go] top = (my_go: Stm[bool, ...]) => ...')."
                  + " The value of this stream must be `[false, false, ...]s ++ [true, true, ...]s`."
              )
            case Some(go) =>
              val producerByDelay =
                delays.map(delay => delay -> go.freshCopy).toMap
              val newProducers =
                s1.producers ++ producerByDelay.map({ case (delay, p) =>
                  // If the accumulator delay is 1, it means that the first update is at time 1.
                  // Therefore, the stream should actually arrive at time 0.
                  val pDelay = SmartDiff(delay, C(1)())().tchk().lower
                  p -> (go, True, pDelay)
                })
              val newAccumulators = s1.accumulators.map({
                case eqn @ (_, (_, _, delay)) if delay.typ == TyTuple() =>
                  eqn
                case (x, (init, next, delay)) =>
                  val go = producerByDelay(delay)
                  val newNext = Mux(StmData(go)(), next, init)()
                  x -> (init, newNext, Tuple()())
              })
              StmBuild(
                s1.n,
                s1.delay,
                s1.initData,
                s1.nextData,
                s1.valid,
                newAccumulators,
                newProducers
              )().tchk()
          }
        }
      case e => e.map(this.apply).tchk()
    }
  }
}
