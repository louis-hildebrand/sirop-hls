package mhir.eval

/** An error that occurred during evaluation.
  */
sealed abstract class EvalException(msg: String) extends RuntimeException(msg) {
  override def getMessage: String = s"EvalError: $msg"
}

/** The result of evaluation seems to depend on undefined behaviour.
  *
  * @param warnings
  *   the undefined behaviours that the result depends on.
  */
case class UndefinedValException(warnings: Set[EvalWarning])
    extends EvalException(
      "value may rely on undefined behaviours: "
        ++ warnings.map(w => w.display).mkString("", ", ", ". ")
    )

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

object InvalidLetStmBufSize
    extends EvalException(
      "cannot implement letstm with nonzero buffer size when the handshake protocol is disabled"
    )
