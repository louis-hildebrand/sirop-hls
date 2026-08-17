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
      val fused = consumer.producers.get(x) match {
        case Some((e: StmBuild, _, xDelay)) =>
          val producer = {
            val namesClash =
              e.namesDefinedHere.intersect(consumer.namesDefinedHere).nonEmpty
            val s1 = if (namesClash) e.renameVars else e
            // Need to be careful if the valid expression uses sdata; we need
            // to make sure we don't end up using sdata in the ready expression
            // somewhere
            val s2 = if (s1.valid.contains(e => e.isInstanceOf[StmData])) {
              addOutputRegisters(s1)
            } else {
              s1
            }
            s2
          }
          val (_, consumerReady, _) = consumer.producers(x)
          // IN CONSUMER
          // | consumer ready | producer valid | result                     |
          // | false          | false          | step: data is not needed   |
          // | false          | true           | step: data is not needed   |
          // | true           | false          | no step: need to wait      |
          // | true           | true           | step: successful handshake |
          val consumerCanStep = !consumerReady || producer.valid
          // IN PRODUCER
          // | consumer ready | producer valid | result                        |
          // | false          | false          | no step: may get stuck*       |
          // | false          | true           | no step: need to wait         |
          // | true           | false          | step: current data is invalid |
          // | true           | true           | step: successful handshake    |
          //
          // * Consider StmConcat where the first input is not always valid.
          // Suppose we switch to reading the second input, but the valid expression of the first input evaluates to false.
          // Then we'll try reading from the first input's producers.
          // But these may be empty, so we may get stuck.
          val producerCanStep = consumerReady
          val newData = {
            // CASE 1: Consumer is ready (i.e., reading from producer).
            //         It doesn't matter whether the producer yielded a valid value:
            //         if it did then fine, if it did not then `valid` will be False
            //         and therefore the `data` doesn't matter.
            // CASE 2: Consumer is not ready (i.e., not reading from producer).
            //         The value of StmData(x) is undefined in this case, so might
            //         as well substitute the same expression.
            consumer.nextData
              .subPreserveType(StmData(x)() -> producer.nextData)
              .tchk()
          }
          val newValid = {
            val cvalid =
              consumer.valid.subPreserveType(
                StmData(x)() -> producer.nextData
              )
            (cvalid && consumerCanStep).tchk()
          }
          val newAccumulators =
            producer.accumulators
              .map({ case (y, (init, next, delay)) =>
                y -> (init, Mux(producerCanStep, next, y)().tchk(), delay)
              }) ++
              consumer.accumulators
                .map({ case (y, (init, next, delay)) =>
                  val newNext = Mux(
                    consumerCanStep,
                    next.subPreserveType(StmData(x)() -> producer.nextData),
                    y
                  )().tchk()
                  // TODO: Calculate the new delay properly
                  val newDelay = delay
                  y -> (init, newNext, newDelay)
                })
          val newProducers =
            producer.producers
              .map({ case (y, (stm, ready, delay)) =>
                y -> (stm, (producerCanStep && ready).tchk(), delay)
              }) ++
              (consumer.producers - x)
                .map({ case (y, (stm, ready, delay)) =>
                  // TODO: Calculate the new delay properly
                  val newDelay = delay
                  y -> (stm, (consumerCanStep && ready).tchk(), newDelay)
                })
          StmBuild(
            consumer.n,
            // TODO: Calculate the new delay properly
            consumer.delay,
            consumer.initData,
            newData,
            newValid,
            newAccumulators,
            newProducers
          )().tchk().asInstanceOf[StmBuild]
        case Some((e, _, _)) =>
          throw new IllegalArgumentException(
            s"Expected the initial value of $x to be a StmBuild, but found $e"
          )
        case None =>
          throw new IllegalArgumentException(
            s"StmBuild does not have any producers called $x."
              + s" The stream is $consumer."
          )
      }
      assert(
        !fused.namesDefinedHere.contains(x),
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

  private def addOutputRegisters(s: StmBuild): StmBuild = {
    val data = Param("data")(s.nextData.typ)
    val valid = Param("valid")(TyBool)
    // Adding these output registers leads to a problem at the last time step:
    // we're reading one more element from the input streams than we used to.
    // (We're also updating the other accumulators one time more than we used
    // to, which might cause the evaluator to complain about issues like
    // overflow, although maybe that's more a problem in the evaluator than
    // here.)
    // To address this, we need to freeze the existing accumulators and
    // producers once we reach the last time step.
    val i = Param("i")(s.n.typ)
    val freeze = i equ s.n
    val newProducers = s.producers.map({ case (x, (stm, ready, delay)) =>
      x -> (stm, And(!freeze, ready)().tchk(), delay)
    })
    val newAccumulators = (s.accumulators ++ Map[Param, (Expr, Expr, Expr)](
      data -> (Undefined(data.typ), s.nextData, Tuple()()),
      valid -> (False, s.valid, Tuple()()),
      i -> (C(0)(i.typ), Mux(s.valid, Sum(i, C(1)(i.typ))(), i)(), Tuple()())
    )).map({ case (x, (init, next, delay)) =>
      x -> (init, Mux(freeze, x, next)().tchk(), delay)
    })
    StmBuild(
      s.n,
      Tuple()(),
      Undefined(data.typ),
      data,
      valid,
      newAccumulators,
      newProducers
    )().tchk().asInstanceOf[StmBuild]
  }
}
