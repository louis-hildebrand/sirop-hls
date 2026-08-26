package mhir.optimize

import mhir.ir._

sealed trait LatencyNode {
  def latency: Option[Int]
}

/** The latency of a stream "source": a parameter, a stream literal, etc.
  */
case class LatencySource(latency: Option[Int]) extends LatencyNode

/** The latency of a [[mhir.ir.StmBuild]] expression.
  *
  * @param latency
  *   the output latency.
  * @param selfLatency
  *   the delay added by this node.
  * @param producers
  *   the latency of the producers that feed into this node.
  */
case class LatencyStmBuild(
    latency: Option[Int],
    selfLatency: Option[Int],
    producers: Map[Param, LatencyNode]
) extends LatencyNode

/** The latency of a [[mhir.ir.LetStm]] expression.
  *
  * @param latency
  *   the output latency.
  * @param in
  *   the latency of the input stream.
  * @param out
  *   the latency of the output stream.
  */
case class LatencyLetStm(
    latency: Option[Int],
    in: LatencyNode,
    out: LatencyNode
) extends LatencyNode
