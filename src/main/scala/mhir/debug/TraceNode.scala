package mhir.debug

import mhir.ir._
import mhir.ir.evaluate._

/** One node in a trace, showing the current state of a given node in the stream
  * pipeline.
  */
sealed trait TraceNode {

  /** The current outputs of this node.
    *
    * If an output is invalid (i.e., no output), then it should simply be
    * omitted from the `Map`.
    */
  def out: Map[StmNodeId, Expr]

  def ready: Set[StmNodeId]
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
      case s: TerminalNode => Some(TerminalTraceNode(s))
      case s: StmNopNode   => Some(StmNopTraceNode(s))
    }
  }
}

/** One node in a trace representing a [[mhir.ir.evaluate.StmBuildNode]].
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
    out: Map[StmNodeId, Expr],
    accumulators: Map[String, String],
    ready: Set[StmNodeId]
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
    val consumers = s.consumers
    assert(consumers.size == 1)
    val consumer = consumers.head
    StmBuildTraceNode(
      n = s.n,
      out = s.out(consumer.id) match {
        case Some(v) => Map(consumer.id -> v)
        case None    => Map()
      },
      accumulators = acc,
      ready = s.producerIds.filter(s.ready)
    )
  }
}

/** One node in a trace representing a [[mhir.ir.LetStm]].
  *
  * @param data
  *   the current value in the buffer.
  */
case class LetStmTraceNode(
    data: Option[Expr],
    out: Map[StmNodeId, Expr],
    ready: Set[StmNodeId]
) extends TraceNode

/** Companion object for [[LetStmTraceNode]].
  */
object LetStmTraceNode {

  /** Factory for [[LetStmTraceNode]].
    */
  def apply(s: LetStmNode): LetStmTraceNode = {
    new LetStmTraceNode(
      data = s.data,
      out = s.consumerIds
        .flatMap(id =>
          s.out(id) match {
            case Some(x) => Some(id -> x)
            case None    => None
          }
        )
        .toMap,
      ready = s.producerIds.filter(s.ready)
    )
  }
}

/** One node in a trace representing a [[mhir.ir.evaluate.StmNopNode]].
  *
  * @param out
  *   the current output of the stream.
  * @param ready
  *   the set of producers for which this node's `ready` signal is raised.
  */
case class StmNopTraceNode(out: Map[StmNodeId, Expr], ready: Set[StmNodeId])
    extends TraceNode

/** Companion object for [[StmNopTraceNode]].
  */
object StmNopTraceNode {

  /** Factory for [[StmNopTraceNode]].
    */
  def apply(s: StmNopNode): StmNopTraceNode = {
    StmNopTraceNode(
      out = s.consumerIds
        .flatMap(id =>
          s.out(id) match {
            case Some(x) => Some(id -> x)
            case None    => None
          }
        )
        .toMap,
      ready = s.producerIds.filter(s.ready)
    )
  }
}

case class TerminalTraceNode(out: Map[StmNodeId, Expr], ready: Set[StmNodeId])
    extends TraceNode

/** Companion object for [[TerminalTraceNode]].
  */
object TerminalTraceNode {

  /** Factory for [[TerminalTraceNode]].
    */
  def apply(s: TerminalNode): TerminalTraceNode = {
    TerminalTraceNode(
      out = s.out(StmNodeId("")).map(StmNodeId("") -> _).toMap,
      ready = s.producerIds.filter(s.ready)
    )
  }
}
