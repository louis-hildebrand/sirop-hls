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
) extends LatencyNode {

  def inputLatency: Option[Int] = {
    this.latency
      .zip(this.selfLatency)
      .map({ case (out, self) => out - self })
      .headOption
  }
}

case class LatencyLetStm(
    latency: Option[Int],
    in: LatencyNode,
    out: LatencyNode
) extends LatencyNode
