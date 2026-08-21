package mhir.eval

import mhir.ir._

// TODO: Document all these traits and methods

sealed trait StmNode {

  /** The ID of this node.
    */
  def id: StmNodeId

  /** The output from this node to a given consumer.
    *
    * @param consumerId
    *   the ID of the consumer for which to get the output.
    */
  def out(consumerId: StmNodeId): StmOutput

  def outMap: Map[StmNodeId, StmOutput] = {
    this.consumerIds
      .map(id => id -> this.out(id))
      .toMap
  }

  /** Whether this node has logically valid output for the given consumer
    *
    * @param consumerId
    *   the ID of the consumer for which to get the `valid` signal.
    */
  private[eval] def valid(consumerId: StmNodeId): Boolean = {
    this.out(consumerId) match {
      case _: LogicalOutput => true
      case _                => false
    }
  }

  /** Whether this node is ready to receive data from the given producer.
    *
    * @param producerId
    *   the ID of the producer for which to get the `ready` signal.
    */
  def ready(producerId: StmNodeId): Boolean

  /** The type of the stream produced by this node.
    */
  def typ: TyStm

  /** Whether this node has successfully produced all the outputs it was
    * supposed to and no longer has valid output.
    */
  def isEmpty: Boolean

  /** Causes for this node being stuck. If empty, then this node is not stuck.
    */
  def deadlockReasons: Set[DeadlockReason]

  /** Whether this node is stuck and will no longer produce any output despite
    * (supposedly) being non-empty.
    */
  def isStuck: Boolean = deadlockReasons.nonEmpty

  /** The IDs of the stream producers that provide input to this node.
    */
  def producerIds: Set[StmNodeId] = {
    this.pipe.connections.inNeighbours(this.id)
  }

  /** The stream producers that provide input to this node.
    */
  protected def producers: Set[StmNode] = {
    this.producerIds.map(this.pipe.nodes(_))
  }

  /** The unique producer for this node.
    *
    * @throws IllegalArgumentException
    *   if the number of producers is not exactly one.
    */
  protected def uniqueProducer: StmNode = {
    val producers = this.producers
    if (producers.size == 1) {
      producers.head
    } else {
      throw new IllegalArgumentException(
        s"Wrong number of inputs to node ${this.id}:"
          + s" expected one, but found ${producers.size}:"
          + s" ${producers.map(_.id).mkString(", ")}"
      )
    }
  }

  /** The IDs of the consumers of this node.
    */
  def consumerIds: Set[StmNodeId] = {
    this.pipe.connections.outNeighbours(this.id)
  }

  /** The consumers of this node.
    */
  def consumers: Set[StmNode] = {
    this.consumerIds.map(this.pipe.nodes(_))
  }

  /** Checks whether all consumers of this node are ready for this node.
    */
  private[eval] def allConsumersReady: Boolean = {
    this.consumers.forall(_.ready(this.id))
  }

  def loc: StmNodeLocation

  /** The stream pipeline that this node is part of.
    */
  private[eval] def pipe: StmPipeline
}

trait StmBuildNode extends StmNode {

  def n: Long

  def acc: Map[Param, Expr]
}

trait LetStmNode extends StmNode {

  def buffer: Array[Expr]
}

trait StmNopNode extends StmNode
