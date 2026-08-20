package mhir.eval
package nohandshake

import mhir.canonicalize._
import mhir.ir._

// TODO: Make these methods package-private where possible

sealed trait NoHandshakeStmNode {

  /** Computes the next state of this node.
    */
  private[nohandshake] def step(
      newPipe: StmPipeline
  ): StmNode with NoHandshakeStmNode

  def pipe: StmPipeline

  /** Timestamp of the first logically valid output from this node.
    */
  def absoluteDelayToFirst: Int

  /** Timestamp of the last logically valid output from this node.
    */
  def absoluteDelayToLast: Int

  def data: Expr

  def n: Long = {
    if (this.pipe.time < this.absoluteDelayToFirst) {
      this.absoluteDelayToLast - this.absoluteDelayToFirst + 1
    } else if (this.pipe.time > this.absoluteDelayToLast) {
      0
    } else {
      this.absoluteDelayToLast - this.pipe.time + 1
    }
  }

  def isEmpty: Boolean = {
    this.pipe.time > this.absoluteDelayToLast
  }

  def deadlockReasons: Set[DeadlockReason] = Set()
}

private[nohandshake] case class StmBuildNode(
    pipe: StmPipeline,
    hw: StmNodeHardware,
    data: Expr,
    acc: Map[Param, Expr]
) extends mhir.eval.StmBuildNode
    with NoHandshakeStmNode {

  override def id: StmNodeId = this.hw.id

  override private[nohandshake] def step(
      newPipe: StmPipeline
  ): StmNode with NoHandshakeStmNode = {
    if (newPipe.time > this.absoluteDelayToLast) {
      // This node has no more outputs.
      // Don't take any more steps
      //   (1) to avoid the evaluator complaining about undefined behaviour
      //       (e.g., due to out-of-bounds vector accesses), and
      //   (2) to avoid unnecessary work.
      StmBuildNode(
        pipe = newPipe,
        hw = this.hw,
        data = Undefined(this.data.typ),
        acc = this.acc
      )
    } else {
      val accSubs = this.acc.toMap[Expr, Expr]
      val stmData = this.hw.inputs.map({ case (x, id) =>
        x -> Some(this.pipe.nodes(id).data)
      })
      val newData =
        eval(this.hw.data.subPreserveType(accSubs), stmData = stmData)
      val newAccumulators = this.hw.nextByAccumulator.map({ case (x, next) =>
        this.hw.absoluteDelayByAccumulator.get(x) match {
          case Some(t) if this.pipe.time + 1 < t =>
            // Don't update yet, since we haven't reached this accumulator's
            // delay yet
            x -> this.acc(x)
          case _ =>
            x -> eval(next.subPreserveType(accSubs), stmData = stmData)
        }
      })
      StmBuildNode(
        pipe = newPipe,
        hw = this.hw,
        data = newData,
        acc = newAccumulators
      )
    }
  }

  override def out(consumerId: StmNodeId): StmOutput = {
    if (this.pipe.time < this.absoluteDelayToFirst) {
      PhysicalOutput(this.data)
    } else if (this.pipe.time > this.absoluteDelayToLast) {
      NoOutput
    } else {
      LogicalOutput(this.data)
    }
  }

  override def ready(producerId: StmNodeId): Boolean = true

  override def typ: TyStm = this.hw.typ

  override def loc: StmNodeLocation = this.hw.loc

  override def absoluteDelayToFirst: Int = this.hw.absoluteDelayToFirst

  override def absoluteDelayToLast: Int = this.hw.absoluteDelayToLast
}

private[nohandshake] case class StmLiteralNode(
    pipe: StmPipeline,
    id: StmNodeId,
    physicalElems: Seq[Expr],
    logicalElems: Seq[Expr],
    currentIndex: Int,
    typ: TyStm,
    loc: StmNodeLocation
) extends mhir.eval.StmBuildNode
    with NoHandshakeStmNode {

  override def acc: Map[Param, Expr] = Map()

  override private[nohandshake] def step(
      newPipe: StmPipeline
  ): StmNode with NoHandshakeStmNode = {
    StmLiteralNode(
      pipe = newPipe,
      id = this.id,
      physicalElems = this.physicalElems,
      logicalElems = this.logicalElems,
      currentIndex = this.currentIndex + 1,
      typ = this.typ,
      loc = this.loc
    )
  }

  override def absoluteDelayToFirst: Int = this.physicalElems.length

  override def absoluteDelayToLast: Int = {
    this.physicalElems.length + this.logicalElems.length - 1
  }

  override def data: Expr = {
    this.out(StmNodeId("sink")) match {
      case NoOutput =>
        val TyStm(elemTyp, _) = this.typ
        Undefined(elemTyp)
      case PhysicalOutput(e) => e
      case LogicalOutput(e)  => e
    }
  }

  override def out(consumerId: StmNodeId): StmOutput = {
    assert(
      this.currentIndex >= 0,
      s"${this.getClass.getName}.currentIndex should be non-negative"
    )
    if (this.currentIndex < this.physicalElems.length) {
      PhysicalOutput(this.physicalElems(this.currentIndex))
    } else if (
      this.currentIndex < this.physicalElems.length + this.logicalElems.length
    ) {
      LogicalOutput(
        this.logicalElems(this.currentIndex - this.physicalElems.length)
      )
    } else {
      NoOutput
    }
  }

  override def ready(producerId: StmNodeId): Boolean = true
}
