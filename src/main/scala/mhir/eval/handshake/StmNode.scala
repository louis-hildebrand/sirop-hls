package mhir.eval
package handshake

import mhir.canonicalize._
import mhir.eval._
import mhir.ir._

private[handshake] sealed trait HandshakeStmNode {

  /** Computes the next state of this node.
    */
  private[handshake] def step(
      newPipe: StmPipeline
  ): StmNode with HandshakeStmNode

  /** Changes the pipeline that this node is part of, but does not update this
    * node's internal state.
    */
  private[handshake] def inPipe(
      newPipe: StmPipeline
  ): StmNode with HandshakeStmNode

  /** Check whether this node has the same state as `that`, ignoring the
    * [[StmPipeline]] reference.
    */
  private[handshake] def sameState(that: StmNode): Boolean
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
private[handshake] case class StmBuildNode(
    pipe: StmPipeline,
    id: StmNodeId,
    hw: StmNodeHardware,
    data: Option[Expr],
    n: Long,
    acc: Map[Param, Expr],
    invalidSteps: Int,
    loc: StmNodeLocation
) extends mhir.eval.StmBuildNode
    with HandshakeStmNode {

  override def out(consumerId: StmNodeId): StmOutput = {
    this.data.map(LogicalOutput(_)).getOrElse(NoOutput)
  }

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
    val ready = this.canUpdateAcc && this.readyInternal(x)
    ready
  }

  override def inPipe(newPipe: StmPipeline): StmBuildNode = {
    StmBuildNode(
      newPipe,
      this.id,
      this.hw,
      this.data,
      this.n,
      this.acc,
      this.invalidSteps,
      this.loc
    )
  }

  override def step(newPipe: StmPipeline): StmBuildNode = {
    if (this.isEmpty) {
      this.inPipe(newPipe)
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
        this.hw.nextByAccumulator.map({ case (x, next) =>
          val evalNext =
            eval(next.subPreserveType(this.accSubs), stmData = this.stmData)
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
        invalidSteps = newInvalidSteps,
        loc = this.loc
      )
    }
  }

  override def typ: TyStm = this.hw.typ

  override def isEmpty: Boolean = this.data.isEmpty && this.n == 0

  override def deadlockReasons: Set[DeadlockReason] = {
    if (this.n == 0) {
      Set()
    } else {
      this.requiredProducers.flatMap(node => {
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
      val ready = eval(readyExpr.subPreserveType(this.accSubs)) match {
        case False        => false
        case True         => true
        case _: Undefined => throw UndefinedReady
        case v            => throw new AssertionError(s"ready evaluated to $v")
      }
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
        this.pipe.nodes(id).out(this.id) match {
          case LogicalOutput(e) => x -> Some(e)
          case _ =>
            throw new AssertionError(
              s"producer data should only be accessed when all producers are valid (attempt to read invalid producer $id)"
            )
        }
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
  * @param internalBuffer
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
private[handshake] case class LetStmNode(
    pipe: StmPipeline,
    id: StmNodeId,
    internalBuffer: Array[Expr],
    tail: Int,
    head: Int,
    readIdx: Map[StmNodeId, Int],
    output: Map[StmNodeId, Option[Expr]],
    typ: TyStm,
    loc: StmNodeLocation
) extends mhir.eval.LetStmNode
    with HandshakeStmNode {

  override def out(consumerId: StmNodeId): StmOutput = {
    output(consumerId).map(LogicalOutput(_)).getOrElse(NoOutput)
  }

  override def ready(producerId: StmNodeId): Boolean = {
    this.readyForProducer
  }

  override def inPipe(newPipe: StmPipeline): StmNode with HandshakeStmNode = {
    LetStmNode(
      pipe = newPipe,
      id = this.id,
      internalBuffer = this.internalBuffer,
      tail = this.tail,
      head = this.head,
      readIdx = this.readIdx,
      output = this.output,
      typ = this.typ,
      loc = this.loc
    )
  }

  override def step(newPipe: StmPipeline): StmNode with HandshakeStmNode = {
    val newBuffer = if (this.willIncrementHead) {
      val LogicalOutput(elem) = this.uniqueProducer.out(this.id)
      this.internalBuffer.updated(this.head, elem)
    } else {
      this.internalBuffer
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
        Some(this.internalBuffer(this.readIdx(cid)))
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
      internalBuffer = newBuffer,
      tail = newTail,
      head = newHead,
      readIdx = newReadIdx,
      output = newOutput,
      typ = this.typ,
      loc = this.loc
    )
  }

  override def isEmpty: Boolean = {
    (this.queueEmpty
    && this.output.forall({ case (_, v) => v.isEmpty })
    && this.uniqueProducer.isEmpty)
  }

  override def deadlockReasons: Set[DeadlockReason] = {
    Set()
  }

  private def nextIdx(i: Int): Int = {
    (i + 1) % internalBuffer.length
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
    this.readyForProducer && this.uniqueProducer.valid(this.id)
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
        && (this.internalBuffer sameElements that.internalBuffer)
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
      internalBuffer = this.internalBuffer,
      tail = this.tail,
      head = this.head,
      readIdx = consumerIds.map(_ -> 0).toMap,
      output = consumerIds.map(_ -> None).toMap,
      typ = this.typ,
      loc = this.loc
    )
  }

  def buffer: Array[Expr] = {
    if (this.head >= this.tail) {
      this.internalBuffer.slice(this.tail, this.head)
    } else {
      this.internalBuffer.slice(this.tail, this.internalBuffer.length) ++
        this.internalBuffer.slice(0, this.head)
    }
  }
}

private[handshake] object LetStmNode {

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
      bufSize: Int,
      loc: StmNodeLocation
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
      internalBuffer = (0 to (bufSize + 1))
        .map(_ => mhir.eval.eval(Undefined(elemTyp)))
        .toArray,
      tail = 0,
      head = 0,
      readIdx = Map(),
      output = Map(),
      typ = inTyp,
      loc = loc
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
private[handshake] case class StmNopNode(
    pipe: StmPipeline,
    id: StmNodeId,
    typ: TyStm,
    loc: StmNodeLocation
) extends mhir.eval.StmNopNode
    with HandshakeStmNode {
  override def out(consumerId: StmNodeId): StmOutput = {
    this.uniqueProducer.out(this.id)
  }

  override def ready(producerId: StmNodeId): Boolean = {
    this.consumers.forall(_.ready(this.id))
  }

  override def inPipe(newPipe: StmPipeline): StmNode with HandshakeStmNode = {
    StmNopNode(
      pipe = newPipe,
      id = this.id,
      typ = this.typ,
      loc = this.loc
    )
  }

  override def step(newPipe: StmPipeline): StmNode with HandshakeStmNode = {
    this.inPipe(newPipe)
  }

  override def isEmpty: Boolean = this.uniqueProducer.isEmpty

  override def deadlockReasons: Set[DeadlockReason] = {
    this.uniqueProducer.deadlockReasons
  }

  override def sameState(that: StmNode): Boolean = {
    that match {
      case that: StmNopNode => this.id == that.id
      case _                => false
    }
  }
}
