package mhir.debug

import mhir.ir._
import mhir.ir.evaluate.{
  StmBufferNode,
  StmBuildNode,
  StmMultiConsumerNode,
  StmNode
}

/** One node in a trace, showing the current state of a given node in the stream
  * pipeline.
  */
sealed trait TraceNode

/** Companion object for [[TraceNode]].
  */
object TraceNode {

  /** Factory for [[TraceNode]].
    *
    * @param s
    *   the stream node to save to a trace node.
    */
  def apply(s: StmNode): TraceNode = {
    s match {
      case s: StmBuildNode         => StmBuildTraceNode(s)
      case s: StmBufferNode        => StmBufferTraceNode(s)
      case _: StmMultiConsumerNode => StatelessTraceNode
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
    out: Option[Expr],
    accumulators: Map[String, String]
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
    StmBuildTraceNode(n = s.n, out = s.out, accumulators = acc)
  }
}

/** One node in a trace representing a [[mhir.ir.evaluate.StmBufferNode]].
  *
  * @param data
  *   the current contents of the buffer.
  */
case class StmBufferTraceNode(data: Option[Expr]) extends TraceNode

/** Companion object for [[StmBufferTraceNode]].
  */
object StmBufferTraceNode {

  /** Factory for [[StmBufferTraceNode]].
    *
    * @param s
    *   the stream node to save to a trace node.
    */
  def apply(s: StmBufferNode): StmBufferTraceNode = {
    StmBufferTraceNode(data = s.data)
  }
}

/** A trace node representing a stateless streaming component.
  */
object StatelessTraceNode extends TraceNode
