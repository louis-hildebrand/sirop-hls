package mhir.debug

import mhir.eval._
import mhir.eval.handshake._
import mhir.ir._

/** One node in a trace, showing the current state of a given node in the stream
  * pipeline.
  */
sealed trait TraceNode {

  /** The current outputs of this node.
    */
  def out: Map[StmNodeId, StmOutput]

  def ready: Set[StmNodeId]

  def loc: StmNodeLocation
}

/** Companion object for [[TraceNode]].
  */
object TraceNode {

  /** Factory for [[TraceNode]].
    *
    * @param s
    *   the stream node to save to a trace node.
    */
  def apply(s: StmNode): Option[TraceNode] = {
    s match {
      case s: StmBuildNode => Some(StmBuildTraceNode(s))
      case s: LetStmNode   => Some(LetStmTraceNode(s))
      case s: StmNopNode   => Some(StmNopTraceNode(s))
    }
  }
}

/** One node in a trace representing a [[StmBuildNode]].
  *
  * @param n
  *   the remaining number of outputs.
  * @param out
  *   the current output of the stream.
  * @param accumulators
  *   the current value of each data accumulator.
  */
case class StmBuildTraceNode(
    n: Long,
    out: Map[StmNodeId, StmOutput],
    accumulators: Map[String, String],
    ready: Set[StmNodeId],
    loc: StmNodeLocation
) extends TraceNode

/** Companion object for [[StmBuildTraceNode]].
  */
object StmBuildTraceNode {

  /** Factory for [[StmBuildTraceNode]].
    *
    * @param s
    *   the stream node to save to a trace node.
    */
  def apply(s: StmBuildNode): StmBuildTraceNode = {
    val acc = s.acc.map({ case (x, v) =>
      x.name -> ExprPrinter.displayOneLine(v)
    })
    StmBuildTraceNode(
      n = s.n,
      out = if (s.consumerIds.isEmpty) {
        Map(StmNodeId("sink") -> s.out(StmNodeId("sink")))
      } else {
        s.consumerIds.map(cid => cid -> s.out(cid)).toMap
      },
      accumulators = acc,
      ready = s.producerIds.filter(s.ready),
      loc = s.loc
    )
  }
}

/** One node in a trace representing a [[mhir.ir.LetStm]].
  *
  * @param buffer
  *   the current state of the buffer.
  */
case class LetStmTraceNode(
    buffer: Array[Expr],
    out: Map[StmNodeId, StmOutput],
    ready: Set[StmNodeId],
    loc: StmNodeLocation
) extends TraceNode

/** Companion object for [[LetStmTraceNode]].
  */
object LetStmTraceNode {

  /** Factory for [[LetStmTraceNode]].
    */
  def apply(s: LetStmNode): LetStmTraceNode = {
    new LetStmTraceNode(
      buffer = s.buffer,
      out = s.outMap,
      ready = s.producerIds.filter(s.ready),
      loc = s.loc
    )
  }
}

/** One node in a trace representing a [[StmNopNode]].
  *
  * @param out
  *   the current output of the stream.
  * @param ready
  *   the set of producers for which this node's `ready` signal is raised.
  */
case class StmNopTraceNode(
    out: Map[StmNodeId, StmOutput],
    ready: Set[StmNodeId],
    loc: StmNodeLocation
) extends TraceNode

/** Companion object for [[StmNopTraceNode]].
  */
object StmNopTraceNode {

  /** Factory for [[StmNopTraceNode]].
    */
  def apply(s: StmNopNode): StmNopTraceNode = {
    StmNopTraceNode(
      out = s.outMap,
      ready = s.producerIds.filter(s.ready),
      loc = s.loc
    )
  }
}
