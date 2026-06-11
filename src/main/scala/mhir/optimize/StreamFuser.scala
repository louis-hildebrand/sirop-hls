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
          val producer = {
            val s1 = if (e.accVars.intersect(consumer.accVars).nonEmpty) {
              e.renameVars
            } else {
              e
            }
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

  private def addOutputRegisters(s: StmBuild): StmBuild = {
    val data = Param("data")(s.data.typ)
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
    val newEquations = (s.equations ++ Map[Param, (Expr, Expr)](
      data -> (Undefined(data.typ), s.data),
      valid -> (False, s.valid),
      i -> (C(0)(i.typ), Mux(s.valid, Sum(i, C(1)(i.typ))(), i)())
    )).map({
      case (x, (stm, ready)) if x.typ.isInstanceOf[TyStm] =>
        x -> (stm, And(!freeze, ready)().tchk())
      case (x, (z, next)) =>
        x -> (z, Mux(freeze, x, next)().tchk())
    })
    StmBuild(
      s.n,
      data,
      valid,
      newEquations
    )().tchk().asInstanceOf[StmBuild]
  }
}
