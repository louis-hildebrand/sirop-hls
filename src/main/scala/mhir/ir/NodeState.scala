package mhir.ir

/** The state of a node in the pipeline.
  */
sealed trait NodeState

/** The node has no more data to send.
  */
object Empty extends NodeState

/** The node is waiting for some of its input nodes.
  */
object Stalled extends NodeState

/** The node has taken one step but the output was invalid (i.e.,
  * <code>None</code>).
  */
object Invalid extends NodeState

/** The node has a valid output.
  *
  * @param v
  *   The output.
  */
case class Valid(v: Expr) extends NodeState

/** The node is deadlocked and will never produce any more outputs.
  */
case class Deadlocked(reasons: Seq[DeadlockReason]) extends NodeState
