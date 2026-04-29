package mhir.optimize

import mhir.ir._

sealed trait LatencyNode {
  def latency: Option[Int]
}

case class LatencyParam(latency: Option[Int]) extends LatencyNode

case class LatencyStmBuild(
    latency: Option[Int],
    selfLatency: Option[Int],
    producers: Map[Param, LatencyNode]
) extends LatencyNode

case class LatencyLetStm(
    latency: Option[Int],
    in: LatencyNode,
    out: LatencyNode
) extends LatencyNode
