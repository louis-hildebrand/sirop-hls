package mhir.eval
package handshake

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.eval._
import mhir.ir._
import mhir.sugar.{ExprLowering, StmLiteralUtilsImplicit}
import mhir.typecheck.TypeCheck

import scala.annotation.tailrec

/** A streaming pipeline where the handshake protocol is enabled.
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
private[eval] class StmPipeline(
    var connections: DiGraph[StmNodeId],
    var sinkId: StmNodeId,
    var nodes: Map[StmNodeId, StmNode with HandshakeStmNode] = Map()
) extends mhir.eval.StmPipeline {

  def reachedFixpoint(that: mhir.eval.StmPipeline): Boolean = {
    that match {
      case that: StmPipeline =>
        (this.connections == that.connections
        && this.sinkId == that.sinkId
        && this.nodes.keySet == that.nodes.keySet
        && this.nodes.keySet.forall(id =>
          this.nodes(id).sameState(that.nodes(id))
        ))
      case _ => false
    }
  }

  def step(): StmPipeline = {
    val newPipe =
      new StmPipeline(connections = this.connections, sinkId = this.sinkId)
    newPipe.nodes = this.nodes.map({ case (id, node) =>
      id -> node.step(newPipe)
    })
    newPipe
  }

  @tailrec
  private def stepUntilFirstValid(): StmPipeline = {
    if (this.sink.valid(StmNodeId(""))) {
      this
    } else {
      this.step().stepUntilFirstValid()
    }
  }

  /** Adds a new node to this pipeline.
    *
    * @param node
    *   the node to add.
    */
  private def addNode(node: StmNode with HandshakeStmNode): Unit = {
    require(node.pipe == this)
    this.connections = this.connections.addNode(node.id)
    this.nodes = this.nodes + (node.id -> node)
  }

  /** Adds new nodes to this pipeline.
    *
    * @param nodes
    *   the nodes to add.
    */
  private def addNodes(nodes: StmNode with HandshakeStmNode*): Unit = {
    for (v <- nodes) {
      this.addNode(v)
    }
  }

  /** Adds new edges to this pipeline.
    *
    * @param edgesToAdd
    *   the edges to add.
    */
  private def addEdges(edgesToAdd: (StmNodeId, StmNodeId)*): Unit = {
    this.connections = this.connections.addEdges(edgesToAdd: _*)
  }
}

/** Companion object for [[StmPipeline]].
  */
private[eval] object StmPipeline {

  private implicit val logger: Logger = Logger(getClass.getName)

  /** Makes a pipeline representing the given stream expression.
    *
    * @param f
    *   an expression representing a stream ([[StmBuild]], [[LetStm]], etc.).
    */
  def apply(
      f: Expr,
      inputs: Map[Param, Expr],
      initialLoc: StmNodeLocation = InMain
  ): StmPipeline = {
    val pipe = new StmPipeline(
      connections = DiGraph(),
      sinkId = StmNodeId(""),
      nodes = Map()
    )
    val fWithInputs = f.subPreserveType(
      inputs
        .map({ case (x, e) =>
          val loweredX = x.tchk().lower.asInstanceOf[Param]
          // Evaluate the input because we may be in no_handshake mode, but
          // inputs are always evaluated with the handshake protocol (because
          // it's less restrictive for the programmer and I don't want to have
          // to do latency matching for the inputs)
          val evaluatedE = mhir.eval.eval(e)
          loweredX -> TestInput(evaluatedE, x.name)(evaluatedE.typ)
        })
        .toMap[Expr, Expr]
    )
    init(pipe, fWithInputs, Map(), loc = initialLoc)
    // Initialize the flags in each LetStmNode so that they will raise their
    // `ready` signal at the beginning.
    // This can only happen once we actually know who the consumers for the
    // LetStmNodes are.
    pipe.nodes = pipe.nodes.map({
      case (id, s: LetStmNode) => id -> s.withConsumerIds(s.consumerIds)
      case x                   => x
    })
    // TODO: why is this needed again? Why not omit it?
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
      e.hasType,
      s"expression must be type checked before it can be converted to a StmNode"
    )
    e match {
      case x: Param if idByVar.contains(x) =>
        val newNode = {
          val typ = e.typ.asInstanceOf[TyStm]
          val id = StmNodeId(Param("nop")().name)
          StmNopNode(pipe = pipe, id = id, typ = typ, loc = loc)
        }
        pipe.addNode(newNode)
        pipe.addEdges(idByVar(x) -> newNode.id)
        pipe.sinkId = newNode.id
      case s: StmLiteral =>
        // TODO: Add a dedicated StmLiteral node so I don't waste time
        //       converting StmLiteral to StmBuild only to evaluate it back to
        //       a StmLiteral again?
        init(pipe, s.toStmBuild, idByVar, loc)
      case s: StmBuild =>
        val newSink = makeStmBuildNode(s, pipe, idByVar, loc)
        pipe.addNode(newSink)
        pipe.addEdges(
          newSink.hw.inputs.map({ case (_, id) => id -> newSink.id }).toSeq: _*
        )
        pipe.sinkId = newSink.id
      case LetStm(bufSize, x, in, out) =>
        // TODO: what if it turns out to be undefined?
        val IntCst(bufSizeVal) = eval(bufSize)
        init(pipe, in, idByVar, loc)
        val newNode = LetStmNode(
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
        // TODO: Is this needed when the handshake protocol is enabled?
        val tempPipe =
          StmPipeline(e, inputs = Map(), initialLoc = TestStimulus(x))
            .stepUntilFirstValid()
        pipe.addNodes(
          (tempPipe.nodes - tempPipe.sinkId).values
            .map(_.inPipe(pipe))
            .toSeq: _*
        )
        pipe.addEdges(
          tempPipe.connections.edges
            .filter({ case (u, v) =>
              u != tempPipe.sinkId && v != tempPipe.sinkId
            })
            .toSeq: _*
        )
        pipe.sinkId = tempPipe.connections.edges
          .collectFirst({ case (u, v) if v == tempPipe.sinkId => u })
          .get
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
    // TODO: what if it turns out to be undefined?
    val IntCst(n) = eval(s.n)
    val readyByInput = s.producers
      .map({ case (x, (_, ready, _)) => x -> ready })
    val inputs = s.producers.map({ case (x, (z, _, _)) =>
      x -> init(pipe, z, idByVar, loc)
    })
    StmBuildNode(
      pipe = pipe,
      id = StmNodeId(Param("sbuild")().name),
      data = None,
      hw = StmNodeHardware(
        data = s.nextData,
        valid = s.valid,
        inputs = inputs,
        nextByAccumulator = s.accumulators
          .map({ case (x, (_, next, _)) => x -> next }),
        readyByInput = readyByInput,
        typ = s.typ.asInstanceOf[TyStm]
      ),
      n = n,
      acc = s.accumulators.map({
        case (x, (Undefined(typ), _, _)) =>
          logger.debug(
            s"Undefined initial value for accumulator $x will be replaced by default value."
              + " I hope you know what you're doing."
          )
          x -> eval(DefaultVal(typ))
        case (x, (z, _, _)) => x -> eval(z)
      }),
      invalidSteps = 0,
      loc = loc
    )
  }
}
