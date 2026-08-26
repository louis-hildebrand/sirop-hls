package mhir.optimize

import mhir.ir._

sealed trait LatencyNode {

  /** The latency (relative to the global epoch) of the output of this node.
    *
    * For example, suppose the global epoch is 5 cycles after the reset signal
    * is de-asserted, and suppose [[latency]] is 3. This means that the output
    * of this node will become logically valid 8 clock cycles after reset is
    * de-asserted.
    */
  def latency: Option[Int]
}

/** The latency of a stream "source": a parameter, a stream literal, etc.
  */
case class LatencySource(latency: Option[Int]) extends LatencyNode

/** The latency of a [[mhir.ir.StmBuild]] expression.
  *
  * @param latency
  *   the output latency.
  * @param localEpoch
  *   The epoch of this node relative to the global epoch. For example, a stream
  *   operator like `StmZip` might have delay annotations of 0 on each producer.
  *   If those producers both have latencies of 5 (relative to the global
  *   epoch), then the local epoch will be 5. But the delay annotations in
  *   `StmZip` could just as well be 42, in which case the local epoch will be
  *   -37 (5 - 42). In other words, the local epoch in this weird `StmZip` is 37
  *   clock cycles <i>before</i> the global epoch, and the producers become
  *   valid 42 cycles after that.
  * @param producers
  *   the latency of the producers that feed into this node.
  */
case class LatencyStmBuild(
    latency: Option[Int],
    localEpoch: Option[Int],
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
