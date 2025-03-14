package opt

import ir._
import scala.annotation.tailrec

object StmFusePass {

  /** Fuse a `StmBuild` with its statically-known stream inputs until it has no
    * more statically-known stream inputs. Note that this requires the stream
    * inputs to each have their own variable in <code>stm</code>; they cannot be
    * in a tuple.
    */
  @tailrec
  def fuseCompletely(
      stm: StmBuild /* Stm<A; n> */
  ): StmBuild /* Stm<A; n> */ = {
    stm.seedByVar.find({ case (_, e) => e.isInstanceOf[StmBuild] }) match {
      case Some((x, _)) => fuseCompletely(fuseWith(stm, x))
      case _            => stm
    }
  }

  /** Fuse a <code>StmBuild</code> with the input stream represented by variable
    * <code>x</code> (which must be one of the accumulator variables in the
    * stream).
    */
  def fuseWith(
      consumerStm: StmBuild /* Stm<A; n> */,
      x: Param
  ): StmBuild /* Stm<A; n> */ = {
    consumerStm.seedByVar.get(x) match {
      case Some(e: StmBuild) =>
        // Rename accumulator variables in case some of them are also being
        // used by the consumer stream
        val producerStm = e.renameAccVars
        val c = stmNextCallCondition(consumerStm, x)
        val newOutput =
          IfThenElse(
            c,
            // CASE 1: Consumer is reading from producer.
            OptionAccess(
              producerStm.output,
              // CASE 1a: Producer yielded a valid value. Proceed as usual.
              (v: Expr) => consumerStm.output.substitute(StmNext(x).__1 -> v),
              // CASE 1b: Producer did NOT yield a valid value.
              //          The consumer cannot proceed.
              (_: Expr) => NNone
            ),
            // CASE 2: Consumer is not reading from producer.
            //         Proceed as usual.
            //         It's safe to unwrap the Option<T> here because, if it
            //         is being accessed here at all, it means the consumer is
            //         reading a value from the producer without updating the
            //         consumer, which is not allowed.
            consumerStm.output.substitute(
              StmNext(x).__1 -> OptionUnwrapUnsafe(producerStm.output)
            )
          )
        val newEquations = {
          val newConsumerEquations =
            (consumerStm.equations - x)
              .map({ case (x, (z, next)) =>
                x -> (z, IfThenElse(
                  c,
                  // CASE 1: Consumer is reading from producer.
                  OptionAccess(
                    producerStm.output,
                    // CASE 1a: Producer yielded a valid value.
                    //          Update the accumulators in the consumer.
                    (v: Expr) => next.substitute(StmNext(x).__1 -> v),
                    // CASE 1b: Producer did NOT yield a valid value.
                    //          Wait until it does and do not update accumulators.
                    (_: Expr) => x
                  ),
                  // CASE 2: Consumer is not reading from producer.
                  //         Update as usual.
                  next
                ))
              })
          val newProducerEquations =
            producerStm.equations.map({ case (x, (z, next)) =>
              x -> (z, IfThenElse(
                c,
                // CASE 1: Consumer is reading from producer.
                //         Update accumulators.
                next,
                // CASE 2: Consumer is not reading from input.
                //         Producer does nothing.
                x
              ))
            })
          newConsumerEquations ++ newProducerEquations
        }
        StmBuild(consumerStm.n, newOutput, newEquations)
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
  }

  /** Construct a boolean expression <code>c</code> such that, in
    * <code>stm</code>, the accumulator variable <code>x</code> is updated to
    * <code>StmNext(x).0</code> iff <code>c</code> evaluates to true.
    */
  private def stmNextCallCondition(stm: StmBuild, x: Param): Expr = {
    stmNextCallCondition(stm.nextByVar(x), x)
  }

  private def stmNextCallCondition(e: Expr, x: Param): Expr = {
    e match {
      case TupleAccess(StmNext(y), IntCst(0)) if y == x => True
      case y if y == x                                  => False
      case IfThenElse(c, t, f) =>
        val ct = stmNextCallCondition(t, x)
        val cf = stmNextCallCondition(f, x)
        (c && ct) || (Not(c) && cf)
      case e =>
        throw new IllegalArgumentException(
          s"Illegal update to a stream-valued accumulator element: $e."
        )
    }
  }
}
