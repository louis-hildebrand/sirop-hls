package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.sugar.ExprLowering
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.logging.time
import mhir.sugar.Default
import org.slf4j.event.Level

import scala.annotation.tailrec

/** The stream fusion transformation.
  *
  * Stream fusion combines a consumer stream with its statically-known
  * producers, which eliminates the handshake protocol overhead and may reveal
  * more optimization opportunities.
  *
  * @example
  *   To fuse a stream with <i>all</i> its inputs, use the extension method
  *   [[StreamFuser.StreamFusion.fuseCompletely]]. The implicit class
  *   [[StreamFuser.StreamFusion]] must be in scope.
  *
  * {{{
  *   import mhir.ir.StreamFuser.StreamFusion
  *   stm.fuseCompletely()
  * }}}
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

  private implicit val logger: Logger = Logger(getClass.getName)

  implicit class StreamFusion(stm: Expr) {

    /** Fuse a stream producer with its statically-known stream inputs until it
      * has no more statically-known stream inputs. Note that this requires the
      * stream inputs to each have their own accumulator variable; they cannot
      * be in a tuple.
      *
      * Fusion is guaranteed to preserve type annotations.
      */
    final def fuseCompletely(): StmBuild = {
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
      time("fusing completely", Level.TRACE) {
        logger.trace(s"fusing completely: ${this.stm}")
        val withMovedLets = LetStmMover.moveUp(this.stm)
        logger.trace(s"after moving up lets: $withMovedLets")
        val fused = inlineAndFuse(withMovedLets)
        logger.trace(s"after fusion: $fused")
        val result = deduplicateProducers(fused)
        logger.trace(s"after deduplicating producers: $result")
        result
      }
    }

    @tailrec
    private def inlineAndFuse(stm: Expr): StmBuild = {
      stm match {
        case LetStm(bufSize, x, in, out) =>
          if (bufSize != C(1)()) {
            logger.warn(
              "The fusion pass does not currently support LetStm buffer sizes other than one."
                + " The fused stream may get stuck."
            )
          }
          inlineAndFuse(out.subPreserveType(x -> in))
        case s: StmBuild =>
          s.seedByVar.find({ case (_, e) => e.isInstanceOf[StmBuild] }) match {
            case Some((x, _)) =>
              val fusedOnce = s.fuseWith(x)
              // Partial evaluation is not required, but may make the process
              // faster if you're fusing many times
              val simplified = PartialEvalPass.partialEval(fusedOnce)
              inlineAndFuse(simplified)
            case _ => s
          }
        case _ =>
          ???
      }
    }

    private def deduplicateProducers(stm: StmBuild): StmBuild = {
      assert(stm.hasType)
      assert(stm.seedByVar.forall({ case (x, z) =>
        !x.typ.isInstanceOf[TyStm] || z.isInstanceOf[Param]
      }))
      // Find input streams which appear in multiple recurrences
      val reusedProducers = stm.equations
        .filter({ case (x, _) => x.typ.isInstanceOf[TyStm] })
        .map({ case (x, (z, ready)) => (x, z.asInstanceOf[Param], ready) })
        .groupBy({ case (_, z, _) => z })
        .map({ case (z, xs) =>
          z -> xs.map({ case (x, _, ready) => (x, ready) }).toSet
        })
        .filter({ case (_, xs) => xs.size > 1 })
      reusedProducers.foldLeft(stm)({ case (acc, (z, eqns)) =>
        deduplicateProducer(
          consumer = acc,
          producer = z,
          equations = eqns.toSeq
        )
      })
    }

    private def deduplicateProducer(
        consumer: StmBuild,
        producer: Param,
        equations: Seq[(Param, Expr)]
    ): StmBuild = {
      val producerElemTyp = producer.typ.asInstanceOf[TyStm].t
      // Add one buffer to hold the latest element from the consumer
      val bufData = Param(s"${producer.prefix}_buf_data")(producerElemTyp)
      val bufValid = Param(s"${producer.prefix}_buf_valid")(TyBool)
      // Keep track of whether the consumers have read from the buffer yet
      val flagEqns: Map[Param, (Expr, Expr)] =
        equations
          .map({ case (x, ready) =>
            val didRead = Param(s"${x.prefix}_read")(TyBool)
            val willRead = ready
            // Handle resetting the values later
            didRead -> (False, didRead || willRead)
          })
          .toMap
      val allFlagsWillBeRaised = {
        val nextFlags = flagEqns.map({ case (_, (_, next)) => next })
        nextFlags.tail.foldLeft(nextFlags.head: Expr)({ case (acc, e) =>
          acc && e
        })
      }
      val anyConsumersNeedBuffer = {
        val readyExprs = equations.map({ case (_, ready) => ready })
        readyExprs.tail.foldLeft(readyExprs.head: Expr)({ case (acc, e) =>
          acc || e
        })
      }
      // Only update the buffer when needed! Otherwise, the fused stream may
      // get stuck once the producer is empty.
      val updateBuffer = !bufValid && anyConsumersNeedBuffer
      // Replace StmData(x) with reading from the buffer
      val subs = equations
        .map({ case (x, _) => StmData(x)() -> bufData })
        .toMap[Expr, Expr]
      // Build new StmBuild
      val newN = consumer.n // shouldn't refer to the producer anyway
      val newData = consumer.data.subPreserveType(subs)
      // Stall if the buffer is not valid, like with StmBuild-StmBuild fusion
      val newValid = consumer.valid.subPreserveType(subs) && !updateBuffer
      val newEquations = {
        val varsToRemove = equations.map(_._1).toSet
        val equationsToAdd =
          (flagEqns
            // Drop all the flags once the buffer is invalidated
            .map({ case (x, (z, next)) => x -> (z, bufValid && next) })
            ++ Map(
              producer -> (producer, updateBuffer),
              bufData -> (
                Default(producerElemTyp).tchk().lower(),
                Mux(updateBuffer, StmData(producer)(), bufData)()
              ),
              bufValid -> (
                False,
                // The buffer becomes valid after we update it.
                // If the buffer is currently valid and all flags will be
                // raised, invalidate the buffer because all consumers are done
                // reading the current element.
                updateBuffer || (bufValid && !allFlagsWillBeRaised)
              )
            ))
        val updatedOldEquations = consumer.equations
          .filter({ case (x, _) => !varsToRemove.contains(x) })
          .map({ case (x, (z, next)) =>
            val newZ = z // shouldn't refer to the producer anyway
            // Don't do anything while the buffer is being updated
            val newNext = Mux(
              updateBuffer,
              x.typ match {
                case _: TyStm => False
                case _        => x
              },
              next.subPreserveType(subs)
            )()
            x -> (newZ, newNext)
          })
        updatedOldEquations ++ equationsToAdd
      }
      val result = StmBuild(newN, newData, newValid, newEquations)()
      val checkedResult = result.tchk().asInstanceOf[StmBuild]
      assert(
        !checkedResult.hasSyntaxSugar,
        "no syntax sugar should be introduced by deduplicating StmBuild producers"
      )
      checkedResult
    }
  }

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
