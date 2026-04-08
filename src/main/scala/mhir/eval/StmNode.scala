package mhir.eval

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar.Default

/** A node in a streaming pipeline.
  */
sealed trait StmNode {

  /** The stream pipeline that this node is part of.
    */
  def pipe: StmPipeline

  /** The ID of this node.
    */
  def id: StmNodeId

  /** The output from this node to a given consumer.
    *
    * @param consumerId
    *   the ID of the consumer for which to get the output.
    */
  def out(consumerId: StmNodeId): Option[Expr]

  /** Whether this node has valid output for the given consumer
    *
    * @param consumerId
    *   the ID of the consumer for which to get the `valid` signal.
    */
  def valid(consumerId: StmNodeId): Boolean = this.out(consumerId).nonEmpty

  /** Whether this node is ready to receive data from the given producer.
    *
    * @param producerId
    *   the ID of the producer for which to get the `ready` signal.
    */
  def ready(producerId: StmNodeId): Boolean

  /** Computes the next state of this node.
    */
  def step(newPipe: StmPipeline): StmNode

  /** Check whether this node has the same state as `that`, ignoring the
    * [[StmPipeline]] reference.
    */
  def sameState(that: StmNode): Boolean

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

  /** The IDs of the consumers of this node.
    */
  def consumerIds: Set[StmNodeId] = {
    this.pipe.connections.outNeighbours(this.id)
  }

  /** The consumers of this node.
    */
  def consumers: Set[StmNode] =
    this.consumerIds.map(this.pipe.nodes(_))

  /** The unique consumer of this node.
    *
    * @throws IllegalArgumentException
    *   if the number of consumers is not exactly one.
    */
  protected def consumer: StmNode = {
    val consumers = this.consumers
    if (consumers.size == 1) {
      consumers.head
    } else {
      throw new IllegalArgumentException(
        s"Wrong number of consumers for node ${this.id}:"
          + s" expected one, but found ${consumers.size}:"
          + s" ${consumers.map(_.id).mkString(", ")}"
      )
    }
  }

  /** The IDs of the stream producers that provide input to this node.
    */
  def producerIds: Set[StmNodeId] =
    this.pipe.connections.inNeighbours(this.id)

  /** The stream producers that provide input to this node.
    */
  protected def producers: Set[StmNode] =
    this.producerIds.map(this.pipe.nodes(_))

  /** The unique producer for this node.
    *
    * @throws IllegalArgumentException
    *   if the number of producers is not exactly one.
    */
  protected def producer: StmNode = {
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

  /** Checks whether all consumers of this node are ready for this node.
    */
  protected def allConsumersReady: Boolean =
    this.consumers.forall(_.ready(this.id))
}

/** A custom stream producer, from [[StmBuild]].
  *
  * @param id
  *   the ID of this node.
  * @param hw
  *   the parts of the stream which do not change during evaluation.
  * @param data
  *   the current output.
  * @param n
  *   the number of remaining outputs, not including the current one.
  * @param acc
  *   the current value of each data accumulator.
  * @param invalidSteps
  *   the number of steps that this node has taken where it was not waiting for
  *   a producer and therefore had the opportunity to produce output, but did
  *   not produce any valid output.
  */
case class StmBuildNode(
    pipe: StmPipeline,
    id: StmNodeId,
    hw: StmNodeHardware,
    data: Option[Expr],
    n: Long,
    acc: Map[Param, Expr],
    invalidSteps: Int
) extends StmNode {
  override def out(consumerId: StmNodeId): Option[Expr] = this.data

  override def ready(producerId: StmNodeId): Boolean = {
    val x = this.hw.inputs
      .find({ case (_, node) => node == producerId })
      .map({ case (x, _) => x }) match {
      case Some(x) =>
        x
      case None =>
        throw new IllegalArgumentException(
          s"Node with ID $producerId is not an input of node with ID ${this.id}."
        )
    }
    this.canUpdateAcc && this.readyInternal(x)
  }

  override def step(newPipe: StmPipeline): StmBuildNode = {
    if (this.isEmpty) {
      StmBuildNode(
        newPipe,
        this.id,
        this.hw,
        this.data,
        this.n,
        this.acc,
        this.invalidSteps
      )
    } else {
      val newData = if (this.transferOk || this.canUpdateAcc) {
        val valid = (
          this.n != 0
            && this.allRequiredProducersValid
            && this.validInternal
        )
        if (valid) {
          val data = eval(
            this.hw.data.subPreserveType(this.accSubs),
            stmData = this.stmData
          )
          Some(data)
        } else {
          None
        }
      } else {
        this.data
      }
      val decrementN = (
        (this.transferOk || this.canUpdateAcc)
          && this.n != 0
          && this.allRequiredProducersValid
          && this.validInternal
      )
      val newN = if (decrementN) this.n - 1 else this.n
      val updateAcc = (
        this.canUpdateAcc
        // Not really necessary, but in software it may save some time.
        // In hardware, it would probably be better to omit this to save
        // resources.
          && this.n != 0
      )
      val newAcc = if (updateAcc) {
        this.hw.nextByDataAcc.map({ case (x, next) =>
          val evalNext = eval(
            next.subPreserveType(this.accSubs),
            stmData = this.stmData,
            // Who cares if the final accumulator values invoke undefined
            // behaviour?
            // They won't be used anyway.
            suppressWarnings = newN == 0
          )
          x -> evalNext
        })
      } else {
        this.acc
      }
      val newInvalidSteps = if (!this.canUpdateAcc) {
        this.invalidSteps
      } else if (newData.nonEmpty) {
        0
      } else {
        this.invalidSteps + 1
      }
      StmBuildNode(
        pipe = newPipe,
        id = this.id,
        hw = this.hw,
        data = newData,
        n = newN,
        acc = newAcc,
        invalidSteps = newInvalidSteps
      )
    }
  }

  override def typ: TyStm = this.hw.typ

  override def isEmpty: Boolean = this.data.isEmpty && this.n == 0

  override def deadlockReasons: Set[DeadlockReason] = {
    if (this.n == 0) {
      Set()
    } else {
      this.requiredProducers.flatMap((node: StmNode) => {
        if (node.isEmpty) {
          Set[DeadlockReason](EmptyStreamRead)
        } else if (node.isStuck) {
          node.deadlockReasons
        } else {
          Set[DeadlockReason]()
        }
      })
    }
  }

  /** The value of the `ready` expression in the [[StmBuild]] for each input
    * stream.
    *
    * @note
    *   the actual `ready` signal (as provided by [[ready]]) may not be the same
    *   as this. For example, when zipping two streams, you must wait until both
    *   streams have valid input before raising the `ready` signal for either of
    *   them.
    */
  private lazy val readyInternal: Map[Param, Boolean] = {
    this.hw.readyByInput.map({ case (x, readyExpr) =>
      if (readyExpr.contains(classOf[StmData])) {
        throw new IllegalArgumentException(
          s"${StmData.getClass.getSimpleName} cannot be used in a ready expression."
        )
      }
      val ready =
        eval(readyExpr.subPreserveType(this.accSubs)).toBool
      x -> ready
    })
  }

  /** The value of the `valid` expression in the [[StmBuild]].
    *
    * @note
    *   the actual `valid` signal (as provided by [[out]]) may not be the same
    *   as this. For example, if not all the required producers have valid
    *   output ([[allRequiredProducersValid]]), then this node must wait.
    * @note
    *   it is an error to access this value unless all required producers have
    *   valid output.
    */
  private lazy val validInternal: Boolean = {
    eval(
      this.hw.valid.subPreserveType(this.accSubs),
      stmData = this.stmData
    ).toBool
  }

  private def accSubs: Map[Expr, Expr] = this.acc.toMap

  /** Maps variables for input producer streams to their current output if the
    * `ready` expression evaluates to `true` or to `None` otherwise.
    *
    * @note
    *   it is an error to access this value unless all required producers have
    *   valid output.
    */
  private def stmData: Map[Param, Option[Expr]] = {
    this.hw.inputs.map({ case (x, id) =>
      if (this.requiredProducerIds.contains(id)) {
        val out = this.pipe.nodes(id).out(this.id)
        assert(
          out.nonEmpty,
          s"producer data should only be accessed when all producers are valid (attempt to read invalid producer $id)"
        )
        x -> Some(out.get)
      } else {
        x -> None
      }
    })
  }

  /** The IDs of all the nodes that must produce valid output before this node
    * can take a step.
    */
  private def requiredProducerIds: Set[StmNodeId] = {
    this.hw.inputs
      .filter({ case (x, _) => this.readyInternal(x) })
      .map({ case (_, id) => id })
      .toSet
  }

  /** All the nodes that must produce valid output before this node can take a
    * step.
    */
  private def requiredProducers: Set[StmNode] = {
    this.requiredProducerIds.map(this.pipe.nodes(_))
  }

  /** Whether all the required producers (see [[requiredProducers]]) have valid
    * output.
    */
  private def allRequiredProducersValid: Boolean = {
    this.requiredProducers.forall(_.valid(this.id))
  }

  /** Whether this node can update its accumulators and output at the next step.
    */
  private lazy val canUpdateAcc: Boolean = {
    (this.data.isEmpty || this.transferOk) && this.allRequiredProducersValid
  }

  /** Whether the output of this node will be sent to its consumer(s) at the
    * next step.
    */
  protected def transferOk: Boolean = {
    this.allConsumersReady && this.data.nonEmpty
  }

  override def sameState(that: StmNode): Boolean = {
    that match {
      case that: StmBuildNode =>
        (this.id == that.id
        && this.hw == that.hw
        && this.data == that.data
        && this.n == that.n
        && this.acc == that.acc)
      case _ => false
    }
  }
}

/** A node in a streaming pipeline representing a [[mhir.ir.LetStm]].
  *
  * @param pipe
  *   the pipeline that this node is part of.
  * @param id
  *   the ID of this node.
  * @param buffer
  *   the buffer of elements from the input stream.
  * @param tail
  *   the index of the beginning of the circular buffer (inclusive).
  * @param head
  *   the index of the end of the circular buffer (exclusive).
  * @param readIdx
  *   for each consumer, this gives the <i>next</i> index that consumer will
  *   read.
  * @param output
  *   the current output for each consumer.
  * @param typ
  *   the type of this node.
  */
case class LetStmNode(
    pipe: StmPipeline,
    id: StmNodeId,
    buffer: Array[Expr],
    tail: Int,
    head: Int,
    readIdx: Map[StmNodeId, Int],
    output: Map[StmNodeId, Option[Expr]],
    typ: TyStm
) extends StmNode {

  override def out(consumerId: StmNodeId): Option[Expr] = {
    output(consumerId)
  }

  override def ready(producerId: StmNodeId): Boolean = {
    // TODO: Check that the given ID matches this node's producer?
    this.readyForProducer
  }

  override def step(newPipe: StmPipeline): StmNode = {
    val newBuffer = if (this.willIncrementHead) {
      val Some(elem) = this.producer.out(this.id)
      this.buffer.updated(this.head, elem)
    } else {
      this.buffer
    }
    val newTail =
      if (this.willIncrementTail) this.nextIdx(this.tail) else this.tail
    val newHead =
      if (this.willIncrementHead) this.nextIdx(this.head) else this.head
    val newReadIdx = this.readIdx.map({ case (cid, i) =>
      val newIdx = if (this.willIncrementReadIdx(cid)) this.nextIdx(i) else i
      cid -> newIdx
    })
    val newOutput = this.output.map({ case (cid, v) =>
      val consumerReady = this.pipe.nodes(cid).ready(this.id)
      val newOut = if (this.willIncrementReadIdx(cid)) {
        Some(this.buffer(this.readIdx(cid)))
      } else if (consumerReady) {
        None
      } else {
        v
      }
      cid -> newOut
    })
    LetStmNode(
      pipe = newPipe,
      id = this.id,
      buffer = newBuffer,
      tail = newTail,
      head = newHead,
      readIdx = newReadIdx,
      output = newOutput,
      typ = this.typ
    )
  }

  override def isEmpty: Boolean = {
    (this.queueEmpty
    && this.output.forall({ case (_, v) => v.isEmpty })
    && this.producer.isEmpty)
  }

  override def deadlockReasons: Set[DeadlockReason] = {
    // TODO
    Set()
  }

  private def nextIdx(i: Int): Int = {
    (i + 1) % buffer.length
  }

  private def queueEmpty: Boolean = {
    this.head == this.tail
  }

  private def queueFull: Boolean = {
    nextIdx(this.head) == this.tail
  }

  /** IDs of the consumers of this node whose read index is still at the tail of
    * the circular buffer.
    */
  private def laggardIds: Set[StmNodeId] = {
    this.readIdx
      .filter({ case (_, i) => i == this.tail })
      .map({ case (id, _) => id })
      .toSet
  }

  /** Whether the head pointer will be incremented at the next step.
    */
  private def willIncrementHead: Boolean = {
    this.readyForProducer && this.producer.valid(this.id)
  }

  /** Whether the read pointer for the given consumer will be updated at the
    * next step.
    */
  private def willIncrementReadIdx(consumerId: StmNodeId): Boolean = {
    val consumerReady = this.pipe.nodes(consumerId).ready(this.id)
    val bufHasData = this.readIdx(consumerId) != this.head
    val outRegIsFree = !this.valid(consumerId) || consumerReady
    bufHasData && outRegIsFree
  }

  /** Whether the tail of the circular buffer will be incremented at the next
    * step.
    */
  private def willIncrementTail: Boolean = {
    this.laggardIds.forall(this.willIncrementReadIdx)
  }

  private def readyForProducer: Boolean = {
    !this.queueFull
  }

  override def sameState(that: StmNode): Boolean = {
    that match {
      case that: LetStmNode =>
        (this.id == that.id
        && (this.buffer sameElements that.buffer)
        && this.tail == that.tail
        && this.head == that.head
        && this.readIdx == that.readIdx
        && this.output == that.output)
      case _ => false
    }
  }

  /** Creates a copy of this node, but with the given consumer IDs.
    */
  def withConsumerIds(consumerIds: Set[StmNodeId]): LetStmNode = {
    new LetStmNode(
      pipe = this.pipe,
      id = this.id,
      buffer = this.buffer,
      tail = this.tail,
      head = this.head,
      readIdx = consumerIds.map(_ -> 0).toMap,
      output = consumerIds.map(_ -> None).toMap,
      typ = this.typ
    )
  }
}

object LetStmNode {

  /** Create a [[LetStmNode]] in its initial state.
    *
    * @param pipe
    *   the pipeline that this node is part of.
    * @param id
    *   the ID of this node.
    * @param inTyp
    *   the type of the input producer stream.
    * @param bufSize
    *   the desired number of elements to buffer.
    */
  def apply(
      pipe: StmPipeline,
      id: StmNodeId,
      inTyp: TyStm,
      bufSize: Int
  ): LetStmNode = {
    val TyStm(elemTyp, _) = inTyp
    new LetStmNode(
      pipe = pipe,
      id = id,
      // Need the buffer to be one element bigger than `bufSize` to be able to
      // represent the state in which the circular buffer is full.
      // And add a second extra slot to improve throughput.
      // For example, if `bufSize = 1`, this node would only request new data
      // from the producer every other cycle (because it only requests data
      // when the buffer is not full).
      // With the extra slot, the consumer can read from the one slot while the
      // next slot is being filled.
      buffer =
        (0 to (bufSize + 1)).map(_ => mhir.eval.eval(Default(elemTyp))).toArray,
      tail = 0,
      head = 0,
      readIdx = Map(),
      output = Map(),
      typ = inTyp
    )
  }
}

/** A node that simply passes its input to its output with no modification.
  *
  * @param pipe
  *   the pipeline that this node is part of.
  * @param id
  *   the ID of this node.
  * @param typ
  *   the type of the stream produced by this node.
  */
case class StmNopNode(pipe: StmPipeline, id: StmNodeId, typ: TyStm)
    extends StmNode {
  override def out(consumerId: StmNodeId): Option[Expr] = {
    // TODO: Check that the given ID indeed belongs to a consumer of this node?
    this.producer.out(this.id)
  }

  override def ready(producerId: StmNodeId): Boolean = {
    // TODO: Check that the given ID indeed belongs to a producer of this node?
    this.consumer.ready(this.id)
  }

  override def step(newPipe: StmPipeline): StmNode = {
    StmNopNode(pipe = newPipe, id = this.id, typ = this.typ)
  }

  override def isEmpty: Boolean = this.producer.isEmpty

  override def deadlockReasons: Set[DeadlockReason] = {
    this.producer.deadlockReasons
  }

  override def sameState(that: StmNode): Boolean = {
    that match {
      case that: StmNopNode => this.id == that.id
      case _                => false
    }
  }
}

/** A node that serves as the sink of the entire pipeline.
  *
  * @param pipe
  *   the pipeline that this node is part of.
  * @param id
  *   the ID of this node.
  * @param typ
  *   the type of the stream produced by this node.
  */
case class TerminalNode(pipe: StmPipeline, id: StmNodeId, typ: TyStm)
    extends StmNode {
  override def out(consumerId: StmNodeId): Option[Expr] = {
    this.producer.out(this.id)
  }

  override def ready(producerId: StmNodeId): Boolean = true

  override def step(newPipe: StmPipeline): StmNode = {
    TerminalNode(pipe = newPipe, id = this.id, typ = this.typ)
  }

  override def isEmpty: Boolean = this.producer.isEmpty

  override def deadlockReasons: Set[DeadlockReason] = {
    this.producer.deadlockReasons
  }

  override def sameState(that: StmNode): Boolean = {
    that match {
      case that: TerminalNode => this.id == that.id
      case _                  => false
    }
  }
}
