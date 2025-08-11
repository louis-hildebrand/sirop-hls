package mhir.ir
package evaluate

import mhir.ir.typecheck.TypeError

import scala.annotation.tailrec

/** A streaming pipeline.
  *
  * @note
  *   this class is mutable so that there can be circular references between the
  *   pipeline and its nodes. The pipeline should not actually be mutated after
  *   construction.
  *
  * @param connections
  *   a directed graph representing the connections between nodes.
  * @param nodes
  *   the current state of each node.
  * @param sinkId
  *   the ID of the unique node in the pipeline with no consumers.
  */
class StmPipeline(
    var connections: DiGraph[StmNodeId],
    var sinkId: StmNodeId,
    var nodes: Map[StmNodeId, StmNode] = Map()
) {

  /** The unique node in the pipeline with no consumers, which gives the output
    * of the entire pipeline.
    */
  def sink: StmNode = this.nodes(sinkId)

  /** Computes the next state of the pipeline.
    */
  def step(): StmPipeline = {
    val newPipe =
      new StmPipeline(connections = this.connections, sinkId = this.sinkId)
    val newNodes = this.nodes.map({ case (id, node) =>
      id -> node.step(newPipe)
    })
    newPipe.nodes = newNodes
    newPipe
  }

  /** Whether this pipeline is empty; i.e., has successfully produced the number
    * of outputs that it was supposed to and no longer has valid output.
    */
  def isEmpty: Boolean = {
    this.sink.isEmpty
  }

  /** The reasons for which this pipeline is stuck, if any (see [[isStuck]]).
    */
  def deadlockReasons: Set[DeadlockReason] = {
    this.sink.deadlockReasons
  }

  /** Whether this pipeline is stuck and will no longer produce any output
    * despite (supposedly) being non-empty.
    */
  def isStuck: Boolean = deadlockReasons.nonEmpty

  /** The type of the stream produced by this pipeline.
    */
  def typ: Type = this.sink.typ

  /** Adds a new node to this pipeline.
    *
    * @param node
    *   the node to add.
    */
  def addNode(node: StmNode): Unit = {
    require(node.pipe == this)
    this.connections = this.connections.addNode(node.id)
    this.nodes = this.nodes + (node.id -> node)
  }

  /** Adds new edges to this pipeline.
    *
    * @param edgesToAdd
    *   the edges to add.
    */
  def addEdges(edgesToAdd: (StmNodeId, StmNodeId)*): Unit = {
    this.connections = this.connections.addEdges(edgesToAdd: _*)
  }
}

/** Companion object for [[StmPipeline]].
  */
object StmPipeline {

  /** Makes a pipeline representing the given stream expression.
    *
    * @param e
    *   an expression representing a stream ([[StmBuild]], [[LetStm]], etc.).
    */
  def apply(e: Expr): StmPipeline = {
    val pipe = new StmPipeline(
      connections = DiGraph(),
      sinkId = StmNodeId(""),
      nodes = Map()
    )
    init(pipe, e, Map())
    pipe
  }

  private def init(
      pipe: StmPipeline,
      e: Expr,
      idByVar: Map[Param, StmNodeId]
  ): StmNodeId = {
    require(
      e.typ != Missing,
      s"Stream must be type checked before it can be converted to a StmNode."
    )
    e match {
      case x: Param if idByVar.contains(x) =>
        val buffer = {
          val typ = e.typ.asInstanceOf[TyStm]
          val id = StmNodeId(Param("buf")().name)
          StmBufferNode(pipe = pipe, id = id, data = None, typ = typ)
        }
        pipe.addNode(buffer)
        pipe.addEdges(idByVar(x) -> buffer.id)
        pipe.sinkId = buffer.id
      case s: StmLiteral =>
        init(pipe, s.toStmBuild, idByVar)
      case s: StmBuild =>
        val newSink = makeStmBuildNode(s, pipe, idByVar)
        pipe.addNode(newSink)
        pipe.addEdges(
          newSink.hw.inputs.map({ case (_, id) => id -> newSink.id }).toSeq: _*
        )
        pipe.sinkId = newSink.id
      case LetStm(x, in, out) =>
        init(pipe, in, idByVar)
        val multiConsumer = {
          val typ = in.typ.asInstanceOf[TyStm]
          StmMultiConsumerNode(
            pipe = pipe,
            id = StmNodeId(Param("multi")().name),
            typ = typ
          )
        }
        pipe.addNode(multiConsumer)
        pipe.addEdges(pipe.sinkId -> multiConsumer.id)
        pipe.sinkId = multiConsumer.id
        init(pipe, out, idByVar + (x -> multiConsumer.id))
      case e =>
        throw new IllegalArgumentException(
          s"Expression cannot be made into a stream pipeline: $e"
        )
    }
    pipe.sinkId
  }

  private def makeStmBuildNode(
      s: StmBuild,
      pipe: StmPipeline,
      idByVar: Map[Param, StmNodeId]
  ): StmBuildNode = {
    val n = eval(s.n) match {
      case IntCst(n) => n
      case e =>
        throw new TypeError(
          s"Stream length evaluated to $e. It must evaluate to an integer."
        )
    }
    val (inputStreams, accumulators) =
      s.equations.partition({ case (x, _) => x.typ.isInstanceOf[TyStm] })
    val readyByInput = inputStreams
      .map({ case (x, (_, ready)) => x -> ready })
    val inputs = inputStreams.map({ case (x, (z, _)) =>
      x -> init(pipe, z, idByVar)
    })
    StmBuildNode(
      pipe = pipe,
      id = StmNodeId(Param("sbuild")().name),
      out = None,
      hw = StmNodeHardware(
        data = s.data,
        valid = s.valid,
        inputs = inputs,
        nextByDataAcc = accumulators.map({ case (x, (_, next)) =>
          x -> next
        }),
        readyByInput = readyByInput,
        typ = s.typ.asInstanceOf[TyStm]
      ),
      n = n,
      acc = accumulators.map({ case (x, (z, _)) =>
        x -> eval(z)
      }),
      invalidSteps = 0
    )
  }
}
