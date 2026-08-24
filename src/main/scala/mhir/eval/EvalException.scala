package mhir.eval

/** An error that occurred during evaluation.
  */
sealed abstract class EvalException(msg: String) extends RuntimeException(msg) {
  override def getMessage: String = s"EvalError: $msg"
}

/** The stream became deadlocked.
  *
  * @param reasons
  *   the cause(s) of the deadlock.
  */
class DeadlockError(val reasons: Seq[DeadlockReason])
    extends EvalException(s"stuck (${reasons.map(_.name).mkString(", ")}).")

/** A cause for a deadlock.
  */
sealed trait DeadlockReason {

  /** A short, developer-friendly explanation of this reason.
    */
  def name: String
}

/** The stream is definitely deadlocked because it tried to read from an empty
  * stream.
  */
object EmptyStreamRead extends DeadlockReason {
  override def name: String = "attempt to read from an empty stream"
}

object UndefinedReady extends EvalException("ready evaluated to undefined")

/** The stream <i>appears</i> to be deadlocked because it took too many steps
  * without producing any valid outputs.
  */
object TooManySteps extends DeadlockReason {
  override def name: String = "too many steps"
}

/** The stream is definitely stuck because it has reached a fixpoint.
  */
object PipelineFixpoint extends DeadlockReason {
  override def name: String = "pipeline reached fixpoint"
}

case class DelayMismatch(msg: String) extends EvalException(msg)
