package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar.ExprLowering
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

  implicit class StmBuildFusion(stm: StmBuild) {

    /** Fuse a <code>StmBuild</code> with the input stream represented by
      * variable <code>x</code> (which must be one of the accumulator variables
      * in the stream).
      */
    def fuseWith(x: Param): StmBuild = {
      val consumerStm = stm
      require(
        this.stm.hasType,
        "Expression must have been type checked before fusion."
          + s" (Found expression ${this.stm})"
      )
      require(
        !this.stm.hasSyntaxSugar,
        "Expression must be lowered before fusion."
          + s" (Found expression ${this.stm})"
      )
      val fused = consumerStm.seedByVar.get(x) match {
        case Some(e: StmBuild) =>
          // Avoid accumulator name clashes
          val producerStm =
            if (e.accVars.intersect(consumerStm.accVars).nonEmpty) {
              e.renameVars
            } else {
              e
            }
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
              producerStm.equations.map(
                fusedProducerAccumulator(
                  producer = producerStm,
                  ready = readyCond
                )
              )
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
        !fused.accVars.contains(x),
        s"the stream variable ${x.name} should have been removed completely by fusion"
      )
      assert(
        fused.freeVars == stm.freeVars,
        "fusion should not have changed the set of free variables"
      )
      assert(fused.typ ~= stm.typ, "fusion should preserve type annotations")
      assert(
        !fused.hasSyntaxSugar,
        "StmBuild fusion should not introduce any syntax sugar"
      )
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
      val valid = {
        val cvalid =
          consumer.valid.subPreserveType(StmData(x)() -> producer.data)
        val pvalid = producer.valid
        (cvalid && (!ready || pvalid)).tchk()
      }
      val data = {
        // CASE 1: Consumer is ready (i.e., reading from producer).
        //         It doesn't matter whether the producer yielded a valid value:
        //         if it did then fine, if it did not then `valid` will be False
        //         and therefore the `data` doesn't matter.
        // CASE 2: Consumer is not ready (i.e., not reading from producer).
        //         The value of StmData(x) is undefined in this case, so might
        //         as well substitute the same expression.
        consumer.data.subPreserveType(StmData(x)() -> producer.data).tchk()
      }
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
      eqn match {
        case (y, (z, next)) =>
          val canStep = !ready || producer.valid
          y -> (
            z,
            Mux(
              canStep,
              next.subPreserveType(StmData(x)() -> producer.data),
              y.typ match {
                case _: TyStm => False
                case _        => y
              }
            )().tchk().lower
          )
      }
    }

    /** Construct a new recurrence equation after fusion for an accumulator in
      * the producer stream.
      *
      * @param ready
      *   A boolean expression which evaluates to <code>True</code> iff the
      *   consumer is reading from the producer (i.e., the <code>ready</code>
      *   signal is high).
      * @param eqn
      *   The original recurrence equation
      */
    private def fusedProducerAccumulator(
        producer: StmBuild,
        ready: Expr
    )(eqn: (Param, (Expr, Expr))): (Param, (Expr, Expr)) = {
      eqn match {
        case (x, (z, next)) =>
          val canStep = !producer.valid || ready
          x -> (
            z,
            Mux(
              canStep,
              next,
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
