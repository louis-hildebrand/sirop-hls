package mhir.eval

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.sugar.StmLiteralUtilsImplicit
import mhir.typecheck.TypeError

/** A streaming pipeline.
  *
  * @note
  *   this class is mutable so that there can be circular references between the
  *   pipeline and its nodes. The pipeline should not actually be mutated after
  *   construction.
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

  def sameState(that: StmPipeline): Boolean = {
    (this.connections == that.connections
    && this.sinkId == that.sinkId
    && this.nodes.keySet == that.nodes.keySet
    && this.nodes.keySet.forall(id => this.nodes(id).sameState(that.nodes(id))))
  }

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

  private implicit val logger: Logger = Logger(getClass.getName)

  /** Makes a pipeline representing the given stream expression.
    *
    * @param f
    *   an expression representing a stream ([[StmBuild]], [[LetStm]], etc.) or
    *   a function on streams.
    */
  def apply(f: Expr, inputs: Map[Param, Expr]): StmPipeline = {
    val pipe = new StmPipeline(
      connections = DiGraph(),
      sinkId = StmNodeId(""),
      nodes = Map()
    )
    val fWithInputs = f.subPreserveType(
      inputs
        .map({ case (x, e) => x -> TestInput(e, x.name)(e.typ) })
        .toMap[Expr, Expr]
    )
    init(pipe, fWithInputs, Map(), loc = InMain)
    // Initialize the flags in each LetStmNode so that they will raise their
    // `ready` signal at the beginning.
    // This can only happen once we actually know who the consumers for the
    // LetStmNodes are.
    pipe.nodes = pipe.nodes.map({
      case (id, s: LetStmNode) => id -> s.withConsumerIds(s.consumerIds)
      case x                   => x
    })
    // Add terminal node
    val term = TerminalNode(pipe, id = StmNodeId("sink"), typ = pipe.sink.typ)
    pipe.addEdges(pipe.sinkId -> term.id)
    pipe.addNode(term)
    pipe.sinkId = term.id
    pipe
  }

  private def init(
      pipe: StmPipeline,
      e: Expr,
      idByVar: Map[Param, StmNodeId],
      loc: StmNodeLocation
  ): StmNodeId = {
    require(
      e.typ != Missing,
      s"Stream must be type checked before it can be converted to a StmNode."
    )
    e match {
      case x: Param if idByVar.contains(x) =>
        val newNode = {
          val typ = e.typ.asInstanceOf[TyStm]
          val id = StmNodeId(Param("nop")().name)
          StmNopNode(pipe = pipe, id = id, typ = typ, loc)
        }
        pipe.addNode(newNode)
        pipe.addEdges(idByVar(x) -> newNode.id)
        pipe.sinkId = newNode.id
      case s: StmLiteral =>
        init(pipe, s.toStmBuild, idByVar, loc)
      case s: StmBuild =>
        val newSink = makeStmBuildNode(s, pipe, idByVar, loc)
        pipe.addNode(newSink)
        pipe.addEdges(
          newSink.hw.inputs.map({ case (_, id) => id -> newSink.id }).toSeq: _*
        )
        pipe.sinkId = newSink.id
      case LetStm(bufSize, x, in, out) =>
        val bufSizeVal = eval(bufSize) match {
          case IntCst(n) => n
          case e =>
            throw new TypeError(
              s"Buffer size in LetStm evaluated to $e."
                + "It must evaluate to an integer."
            )
        }
        init(pipe, in, idByVar, loc)
        val newNode =
          LetStmNode(
            pipe = pipe,
            id = StmNodeId(Param("let")().name),
            inTyp = in.typ.asInstanceOf[TyStm],
            bufSize = bufSizeVal.toInt,
            loc = loc
          )
        pipe.addNode(newNode)
        pipe.addEdges(pipe.sinkId -> newNode.id)
        pipe.sinkId = newNode.id
        init(pipe, out, idByVar + (x -> newNode.id), loc)
      case TestInput(e, x) =>
        init(pipe, e, idByVar, loc = TestStimulus(x))
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
      idByVar: Map[Param, StmNodeId],
      loc: StmNodeLocation
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
      x -> init(pipe, z, idByVar, loc)
    })
    StmBuildNode(
      pipe = pipe,
      id = StmNodeId(Param("sbuild")().name),
      data = None,
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
      acc = accumulators.map({
        case (x, (Undefined(typ), _)) =>
          logger.debug(
            s"Undefined initial value for accumulator $x will be replaced by default value."
              + " I hope you know what you're doing."
          )
          x -> eval(DefaultVal(typ))
        case (x, (z, _)) => x -> eval(z)
      }),
      invalidSteps = 0,
      loc = loc
    )
  }
}
