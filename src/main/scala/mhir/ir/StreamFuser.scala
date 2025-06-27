package mhir.ir

import mhir.ir.Lowering.ExprLowering
import mhir.ir.typecheck.TypeCheck

import scala.annotation.tailrec

/** The stream fusion transformation.
  *
  * Stream fusion combines a consumer stream with its statically-known
  * producers, which eliminates the handshake protocol overhead and may reveal
  * more optimization opportunities.
  *
  * To fuse a stream with <i>all</i> its inputs, use the extension method
  * [[StreamFuser.StreamFusion.fuseCompletely]]. To fuse with a specific input,
  * use the extension method [[StreamFuser.StreamFusion.fuseWith]]. In either
  * case, the implicit class [[StreamFuser.StreamFusion]] must be in scope.
  *
  * @example
  *
  * {{{
  *   import mhir.ir.StreamFuser.StreamFusion
  *   stm.fuseCompletely()
  * }}}
  */
object StreamFuser {
  implicit class StreamFusion(stm: StmBuild) {

    /** Fuse a `StmBuild` with its statically-known stream inputs until it has
      * no more statically-known stream inputs. Note that this requires the
      * stream inputs to each have their own accumulator variable; they cannot
      * be in a tuple.
      *
      * Fusion is guaranteed to preserve type annotations.
      */
    @tailrec
    final def fuseCompletely(): StmBuild = {
      stm.seedByVar.find({ case (_, e) => e.isInstanceOf[StmBuild] }) match {
        case Some((x, _)) => stm.fuseWith(x).fuseCompletely()
        case _            => stm
      }
    }

    /** Fuse a <code>StmBuild</code> with the input stream represented by
      * variable <code>x</code> (which must be one of the accumulator variables
      * in the stream).
      */
    def fuseWith(x: Param): StmBuild = {
      val consumerStm = stm
      require(
        consumerStm.typ != Missing,
        "The consumer must have been type checked before fusion."
      )
      val fused = consumerStm.seedByVar.get(x) match {
        case Some(e: StmBuild) =>
          // Rename accumulator variables in case some of them are also being
          // used by the consumer stream
          val producerStm = e.renameVars
          val readyCond = consumerStm.nextByVar(x)
          assert(
            readyCond.typ == TyBool,
            "stream call condition must be a bool"
          )
          val (newData, newValid) = fusedOutput(
            consumer = consumerStm,
            producer = producerStm,
            ready = readyCond,
            x = x
          )
          val newEquations = {
            val newConsumerEquations = (consumerStm.equations - x).map(
              fusedConsumerAccumulator(
                producer = producerStm,
                ready = readyCond,
                x = x
              )
            )
            val newProducerEquations =
              producerStm.equations.map(fusedProducerAccumulator(readyCond))
            newConsumerEquations ++ newProducerEquations
          }
          StmBuild(consumerStm.n, newData, newValid, newEquations)(stm.typ)
        case Some(e) =>
          throw new IllegalArgumentException(
            s"Expected the initial value of $x to be a StmBuild, but found $e"
          )
        case None =>
          throw new IllegalArgumentException(
            s"Stream does not contain accumulator variable $x."
              + s" The stream is $consumerStm."
          )
      }
      assert(
        !fused.contains(x),
        s"the stream variable ${x.name} should have been removed completely by fusion"
      )
      assert(
        fused.freeVars() == stm.freeVars(),
        "fusion should not have changed the set of free variables"
      )
      assert(fused.typ == stm.typ, "fusion should preserve type annotations")
      fused
    }

    /** Construct an expression representing the output of the consumer after
      * fusion.
      *
      * @param consumer
      *   The consumer stream
      * @param producer
      *   The producer stream
      * @param ready
      *   A boolean expression which evaluates to <code>True</code> iff the
      *   consumer is reading from the producer (i.e., the <code>ready</code>
      *   signal is high).
      * @param x
      *   The accumulator variable for the producer
      */
    private def fusedOutput(
        consumer: StmBuild,
        producer: StmBuild,
        ready: Expr,
        x: Param
    ): (Expr, Expr) = {
      val consumerTyp = consumer.typ.asInstanceOf[TyStm]
      val producerTyp = producer.typ.asInstanceOf[TyStm]
      val valid = Mux(
        ready,
        // CASE 1: Consumer is ready (i.e., reading from producer).
        Mux(
          producer.valid,
          // CASE 1a: Producer yielded a valid value. Proceed as usual.
          consumer.valid.subPreserveType(StmData(x)() -> producer.data),
          // CASE 1b: Producer did NOT yield a valid value.
          //          The consumer cannot proceed.
          False
        )(),
        // CASE 2: Consumer is not ready (i.e., not reading from producer).
        //         Proceed as usual, but with the default value for the producer
        //         output (this should not be read).
        consumer.valid.subPreserveType(StmData(x)() -> Default(producerTyp.t))
      )().tchk()
      val data = Mux(
        ready,
        // CASE 1: Consumer is ready (i.e., reading from producer).
        //         It doesn't matter whether the producer yielded a valid value:
        //         if it did then fine, if it did not then `valid` will be False
        //         and therefore the `data` doesn't matter.
        consumer.data.subPreserveType(StmData(x)() -> producer.data),
        // CASE 2: Consumer is not ready (i.e., not reading from producer).
        //         Proceed as usual, but with the default value for the producer
        //         output (this should not be read).
        consumer.data.subPreserveType(StmData(x)() -> Default(producerTyp.t))
      )().tchk()
      (data, valid)
    }

    /** Construct a new recurrence equation after fusion for an accumulator in
      * the consumer stream.
      *
      * @param producer
      *   The producer stream
      * @param ready
      *   A boolean expression which evaluates to <code>True</code> iff the
      *   consumer is reading from the producer (i.e., the <code>ready</code>
      *   signal is high).
      * @param x
      *   The accumulator variable for the producer
      * @param eqn
      *   The original recurrence equation
      */
    private def fusedConsumerAccumulator(
        producer: StmBuild,
        ready: Expr,
        x: Param
    )(
        eqn: (Param, (Expr, Expr))
    ): (Param, (Expr, Expr)) = {
      val producerTyp = producer.typ.asInstanceOf[TyStm]
      eqn match {
        case (y, (z, next)) =>
          y -> (
            z,
            Mux(
              ready,
              // CASE 1: Consumer is reading from producer.
              Mux(
                producer.valid,
                // CASE 1a: Producer yielded a valid value.
                //          Update the accumulators.
                next.subPreserveType(StmData(x)() -> producer.data),
                // CASE 1b: Producer did NOT yield a valid value.
                //          Do not update accumulators until it does.
                y.typ match {
                  case _: TyStm => False
                  case _        => y
                }
              )(),
              // CASE 2: Consumer is not reading from producer.
              //         Update as usual, but with the default value for the
              //         producer output (this should not be read).
              next.subPreserveType(
                StmData(x)() -> Default(producerTyp.t)
              )
            )().tchk().lower()
          )
      }
    }

    /** Construct a new recurrence equation after fusion for an accumulator in
      * the producer stream.
      *
      * @param readyCond
      *   A boolean expression which evaluates to <code>True</code> iff the
      *   consumer is reading from the producer (i.e., the <code>ready</code>
      *   signal is high).
      * @param eqn
      *   The original recurrence equation
      */
    private def fusedProducerAccumulator(
        readyCond: Expr
    )(eqn: (Param, (Expr, Expr))): (Param, (Expr, Expr)) = {
      eqn match {
        case (x, (z, next)) =>
          x -> (
            z,
            Mux(
              readyCond,
              // CASE 1: Consumer is reading from producer.
              //         Update accumulators.
              next,
              // CASE 2: Consumer is not reading from input.
              //         Producer does nothing.
              x.typ match {
                case _: TyStm => False
                case _        => x
              }
            )().tchk()
          )
      }
    }
  }
}
