package mhir.ir

/** An error that occurred during evaluation.
  */
sealed abstract class EvalException(msg: String) extends RuntimeException(msg)

/** The result of evaluation seems to depend on undefined behaviour.
  *
  * @param warnings
  *   the undefined behaviours that the result depends on.
  */
case class UndefinedValException(warnings: Set[EvalWarning])
    extends EvalException(
      "The result of evaluation might rely on one or more undefined behaviours: "
        ++ warnings.map(w => w.display).mkString("", ", ", ". ")
    )

/** The stream became deadlocked.
  *
  * @param reasons
  *   the cause(s) of the deadlock.
  */
class DeadlockError(val reasons: Seq[DeadlockReason])
    extends EvalException(s"Deadlock (${reasons.mkString(", ")}).")

/** A cause for a deadlock.
  */
sealed trait DeadlockReason

/** The stream is definitely deadlocked because it tried to read from an empty
  * stream.
  */
object EmptyStreamRead extends DeadlockReason

/** The stream <i>appears</i> to be deadlocked because it took too many steps
  * without producing any valid outputs.
  */
object TooManySteps extends DeadlockReason

case class OverflowException(n: Long, typ: Type)
    extends IllegalArgumentException(s"Value $n does not fit within type $typ.")
