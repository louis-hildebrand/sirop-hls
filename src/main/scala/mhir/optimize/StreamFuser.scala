package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck.TypeCheck

/** The stream fusion transformation.
  *
  * Stream fusion combines a consumer stream with its statically-known
  * producers, which eliminates the handshake protocol overhead and may reveal
  * more optimization opportunities.
  *
  * @example
  *   To fuse with a specific input in [[StmBuild]], use the extension method
  *   [[StreamFuser.StmBuildFusion.fuseWith]]. The implicit class
  *   [[StreamFuser.StmBuildFusion]] must be in scope.
  *
  * {{{
  *   import mhir.ir.StreamFuser.StmBuildFusion
  *   stm.fuseWith(x)
  * }}}
  */
object StreamFuser {

  implicit class StmBuildFusion(consumer: StmBuild) {

    /** Fuse a <code>StmBuild</code> with the input stream represented by
      * variable <code>x</code> (which must be one of the accumulator variables
      * in the stream).
      */
    def fuseWith(x: Param): StmBuild = {
      require(
        consumer.hasType,
        "Expression must have been type checked before fusion."
          + s" (Found expression $consumer)"
      )
      require(
        !consumer.hasSyntaxSugar,
        "Expression must be lowered before fusion."
          + s" (Found expression $consumer)"
      )
      val fused = consumer.seedByVar.get(x) match {
        case Some(e: StmBuild) =>
          // Avoid accumulator name clashes
          val producer =
            if (e.accVars.intersect(consumer.accVars).nonEmpty) {
              e.renameVars
            } else {
              e
            }
          val consumerReady = consumer.nextByVar(x)
          val newData = {
            // CASE 1: Consumer is ready (i.e., reading from producer).
            //         It doesn't matter whether the producer yielded a valid value:
            //         if it did then fine, if it did not then `valid` will be False
            //         and therefore the `data` doesn't matter.
            // CASE 2: Consumer is not ready (i.e., not reading from producer).
            //         The value of StmData(x) is undefined in this case, so might
            //         as well substitute the same expression.
            consumer.data
              .subPreserveType(StmData(x)() -> producer.data)
              .tchk()
          }
          // IN CONSUMER
          // | consumer ready | producer valid | result                     |
          // | false          | false          | step: data is not needed   |
          // | false          | true           | step: data is not needed   |
          // | true           | false          | no step: need to wait      |
          // | true           | true           | step: successful handshake |
          val consumerCanStep = !consumerReady || producer.valid
          val newValid = {
            val cvalid =
              consumer.valid.subPreserveType(
                StmData(x)() -> producer.data
              )
            (cvalid && consumerCanStep).tchk()
          }
          val newEquations = {
            val newConsumerEquations = (consumer.equations - x).map({
              case (y, (stm, ready)) if y.typ.isInstanceOf[TyStm] =>
                y -> (stm, (consumerCanStep && ready).tchk())
              case (y, (z, next)) =>
                y -> (
                  z,
                  Mux(
                    consumerCanStep,
                    next.subPreserveType(StmData(x)() -> producer.data),
                    y
                  )().tchk()
                )
            })
            // IN PRODUCER
            // | consumer ready | producer valid | result                        |
            // | false          | false          | step: current data is invalid |
            // | false          | true           | no step: need to wait         |
            // | true           | false          | step: current data is invalid |
            // | true           | true           | step: successful handshake    |
            val producerCanStep = !producer.valid || consumerReady
            val newProducerEquations =
              producer.equations.map({
                case (x, (stm, ready)) if x.typ.isInstanceOf[TyStm] =>
                  x -> (stm, (producerCanStep && ready).tchk())
                case (x, (z, next)) =>
                  x -> (z, Mux(producerCanStep, next, x)().tchk())
              })
            newConsumerEquations ++ newProducerEquations
          }
          StmBuild(consumer.n, newData, newValid, newEquations)()
            .tchk()
            .asInstanceOf[StmBuild]
        case Some(e) =>
          throw new IllegalArgumentException(
            s"Expected the initial value of $x to be a StmBuild, but found $e"
          )
        case None =>
          throw new IllegalArgumentException(
            s"Stream does not contain accumulator variable $x."
              + s" The stream is $consumer."
          )
      }
      assert(
        !fused.accVars.contains(x),
        s"the stream variable ${x.name} should have been removed completely by fusion"
      )
      assert(
        fused.freeVars == consumer.freeVars,
        "fusion should not have changed the set of free variables"
      )
      assert(
        fused.typ ~= consumer.typ,
        "fusion should preserve type annotations"
      )
      assert(
        !fused.hasSyntaxSugar,
        "fusion should not introduce any syntax sugar"
      )
      fused
    }
  }
}
