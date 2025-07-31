package mhir.ir
package evaluate

/** A node in a streaming pipeline.
  */
sealed trait StmNode {

  /** The stream pipeline that this node is part of.
    */
  def pipe: StmPipeline

  /** The ID of this node.
    */
  def id: StmNodeId

  /** The output of this node.
    */
  def out: Option[Expr]

  /** Whether this node has valid output.
    */
  def valid: Boolean = out.nonEmpty

  /** The `ready` signal from this node to a given producer.
    *
    * @param input
    *   the producer stream for which to get the `ready` signal.
    */
  def ready(input: StmNodeId): Boolean

  /** Computes the next state of this node.
    */
  def step(newPipe: StmPipeline): StmNode

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
  private def consumerIds: Set[StmNodeId] = {
    this.pipe.connections.outNeighbours(this.id)
  }

  /** The consumers of this node.
    */
  private def consumers: Set[StmNode] =
    this.consumerIds.map(this.pipe.nodes(_))

  /** The IDs of the stream producers that provide input to this node.
    */
  protected def producerIds: Set[StmNodeId] =
    this.pipe.connections.inNeighbours(this.id)

  /** The stream producers that provide input to this node.
    */
  protected def producers: Set[StmNode] =
    this.producerIds.map(this.pipe.nodes(_))

  /** Checks whether all consumers of this node are ready for this node.
    */
  protected def allConsumersReady: Boolean =
    this.consumers.forall(_.ready(this.id))

  /** Whether the output of this node will be sent to its consumer(s) at the
    * next step.
    */
  protected def transferOk: Boolean = this.allConsumersReady && this.valid
}

/** A custom stream producer, from [[StmBuild]].
  *
  * @param id
  *   the ID of this node.
  * @param hw
  *   the parts of the stream which do not change during evaluation.
  * @param out
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
    out: Option[Expr],
    n: Long,
    acc: Map[Param, Expr],
    invalidSteps: Int
) extends StmNode {
  override def ready(input: StmNodeId): Boolean = {
    val x = this.hw.inputs
      .find({ case (_, node) => node == input })
      .map({ case (x, _) => x }) match {
      case Some(x) =>
        x
      case None =>
        throw new IllegalArgumentException(
          s"Node with ID $input is not an input of node with ID ${this.id}."
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
        this.out,
        this.n,
        this.acc,
        this.invalidSteps
      )
    } else {
      val newOut = if (this.transferOk || this.canUpdateAcc) {
        val valid = (
          this.n != 0
            && this.allRequiredProducersValid
            && this.validInternal
        )
        if (valid) {
          val data = eval(this.hw.data.subPreserveType(this.accAndInputs))
          Some(data)
        } else {
          None
        }
      } else {
        this.out
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
            next.subPreserveType(this.accAndInputs),
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
      } else if (newOut.nonEmpty) {
        0
      } else {
        this.invalidSteps + 1
      }
      StmBuildNode(
        pipe = newPipe,
        id = this.id,
        hw = this.hw,
        out = newOut,
        n = newN,
        acc = newAcc,
        invalidSteps = newInvalidSteps
      )
    }
  }

  override def typ: TyStm = this.hw.typ

  override def isEmpty: Boolean = !this.valid && this.n == 0

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
        eval(readyExpr.subPreserveType(this.acc.toMap[Expr, Expr])).toBool
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
    eval(this.hw.valid.subPreserveType(this.accAndInputs)).toBool
  }

  /** All substitutions for the variables that are bounds within this stream:
    * both accumulator variables and [[StmData]].
    *
    * @note
    *   it is an error to access this value unless all required producers have
    *   valid output.
    */
  private def accAndInputs: Map[Expr, Expr] = {
    val producerData = this.hw.inputs.map({ case (x, id) =>
      if (this.requiredProducerIds.contains(id)) {
        val out = this.pipe.nodes(id).out
        assert(
          out.nonEmpty,
          s"producer data should only be accessed when all producers are valid (attempt to read invalid producer $id)"
        )
        val v = this.pipe.nodes(id).out.get
        StmData(x)() -> v
      } else {
        val typ = x.typ.asInstanceOf[TyStm].t
        StmData(x)() -> Default(typ)
      }
    })
    this.acc ++ producerData
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
    this.requiredProducers.forall(_.valid)
  }

  /** Whether this node can update its accumulators and output at the next step.
    */
  private lazy val canUpdateAcc: Boolean = {
    ((!this.valid || this.transferOk)
    && this.allRequiredProducersValid)
  }
}

/** A stream producer which buffers one element.
  *
  * @param pipe
  *   the stream pipeline that this node is part of.
  * @param id
  *   the ID of this node.
  * @param data
  *   the data inside the buffer.
  * @param typ
  *   the type of the stream produced by this node.
  */
case class StmBufferNode(
    pipe: StmPipeline,
    id: StmNodeId,
    data: Option[Expr],
    typ: TyStm
) extends StmNode {
  override def out: Option[Expr] = data

  override def ready(input: StmNodeId): Boolean = {
    // TODO: Check that the given node ID is indeed the input to this node?
    this.canUpdate
  }

  override def step(newPipe: StmPipeline): StmNode = {
    val newData = if (this.canUpdate) this.producer.out else this.data
    StmBufferNode(pipe = newPipe, id = this.id, data = newData, typ = this.typ)
  }

  override def isEmpty: Boolean = !this.valid && this.producer.isEmpty

  override def deadlockReasons: Set[DeadlockReason] = {
    this.producer.deadlockReasons
  }

  /** The unique producer for this stream.
    */
  private def producer: StmNode = {
    val producers = this.producers
    if (producers.size == 1) {
      producers.head
    } else {
      throw new IllegalArgumentException(
        s"Wrong number of inputs to node ${this.id}:"
          + s" expected one, but found ${producers.size}:"
          + s" ${producers.mkString(", ")}"
      )
    }
  }

  /** Whether this node can accept new input.
    */
  private def canUpdate: Boolean = {
    !this.valid || this.transferOk
  }
}

/** A stream producer that supports more than one consumer.
  *
  * This simply forwards its input, but sets the `ready` signal to be the
  * logical AND of the `ready` signals from all the consumers. Therefore, the
  * producer will not be able to advance until all consumers are ready.
  *
  * @param pipe
  *   the stream pipeline that this node is part of.
  * @param id
  *   the ID of this node.
  * @param typ
  *   the type of the stream produced by this node.
  */
case class StmMultiConsumerNode(pipe: StmPipeline, id: StmNodeId, typ: TyStm)
    extends StmNode {
  override def out: Option[Expr] = {
    if (!this.allConsumersReady) None else producer.out
  }

  override def ready(input: StmNodeId): Boolean = {
    // TODO: Check that the given node ID is indeed the input to this node?
    this.allConsumersReady
  }

  override def step(newPipe: StmPipeline): StmNode = {
    StmMultiConsumerNode(newPipe, id, typ)
  }

  override def isEmpty: Boolean = !this.valid && this.producer.isEmpty

  override def deadlockReasons: Set[DeadlockReason] = {
    this.producer.deadlockReasons
  }

  /** The unique producer for this stream.
    */
  private def producer: StmNode = {
    val producers = this.producers
    if (producers.size == 1) {
      producers.head
    } else {
      throw new IllegalArgumentException(
        s"Wrong number of inputs to node ${this.id}:"
          + s" expected one, but found ${producers.size}:"
          + s" ${producers.mkString(", ")}"
      )
    }
  }
}
