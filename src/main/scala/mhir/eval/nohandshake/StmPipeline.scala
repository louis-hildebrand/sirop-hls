package mhir.eval
package nohandshake

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.sugar._
import mhir.typecheck._

/** A streaming pipeline where the handshake protocol is disabled.
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
  * @param time
  *   the current time step.
  */
private[eval] class StmPipeline(
    var connections: DiGraph[StmNodeId],
    var sinkId: StmNodeId,
    var nodes: Map[StmNodeId, StmNode with NoHandshakeStmNode] = Map(),
    val time: Int
) extends mhir.eval.StmPipeline {

  def step(): StmPipeline = {
    val newPipe = new StmPipeline(
      connections = this.connections,
      sinkId = this.sinkId,
      time = this.time + 1
    )
    newPipe.nodes = this.nodes.map({ case (id, node) =>
      id -> node.step(newPipe)
    })
    newPipe
  }

  override def sink: StmNode with NoHandshakeStmNode = this.nodes(this.sinkId)

  override def reachedFixpoint(that: mhir.eval.StmPipeline): Boolean = {
    // When the handshake protocol is disabled, we can statically calculate
    // the number of time steps it will take to compute the full result.
    // Therefore, there's no need to worry about reaching a fixpoint and
    // thus falling into an infinite loop.
    false
  }

  /** Adds a new node to this pipeline.
    *
    * @param node
    *   the node to add.
    */
  private def addNode(node: StmNode with NoHandshakeStmNode): Unit = {
    require(node.pipe == this)
    this.connections = this.connections.addNode(node.id)
    this.nodes = this.nodes + (node.id -> node)
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

object StmPipeline {

  private implicit val logger: Logger = Logger(getClass.getName)

  def apply(
      e: Expr,
      inputs: Map[Param, Expr],
      initialLoc: StmNodeLocation = InMain
  ): StmPipeline = {
    val pipe = new StmPipeline(
      connections = DiGraph(),
      sinkId = StmNodeId(""),
      nodes = Map(),
      time = 0
    )
    val eWithInputs = e.subPreserveType(
      inputs
        .map({ case (x, e) =>
          val loweredX = x.tchk().lower.asInstanceOf[Param]
          val loweredE = e.tchk().lower
          loweredX -> TestInput(loweredE, x.name)(loweredE.typ)
        })
        .toMap[Expr, Expr]
    )
    init(pipe, eWithInputs, idByVar = Map(), loc = initialLoc)
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
      case s: StmLiteral =>
        val newSink = StmLiteralNode(
          pipe = pipe,
          id = StmNodeId(Param("sliteral")().name),
          physicalElems = s.physical,
          logicalElems = s.logical,
          currentIndex = 0,
          typ = s.typ.asInstanceOf[TyStm],
          loc = loc
        )
        pipe.addNode(newSink)
        pipe.sinkId = newSink.id
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
        if (bufSizeVal != 0) {
          logger.warn(
            s"cannot implement letstm with nonzero buffer size ($bufSizeVal) when the handshake protocol is disabled." +
              " The buffer size will be ignored, which may lead to the program producing incorrect results."
          )
        }
        init(pipe, in, idByVar, loc)
        init(pipe, out, idByVar + (x -> pipe.sinkId), loc)
      case x: Param if idByVar.contains(x) =>
        pipe.sinkId = idByVar(x)
      case TestInput(e, x) =>
        init(pipe = pipe, e = e, idByVar = Map(), loc = TestStimulus(x))
      case e =>
        throw new IllegalArgumentException(
          s"expression cannot be made into a stream pipeline: $e"
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
    val IntCst(outDelay) = eval(s.delay)
    val inputs = s.producers.map({ case (x, (z, _, _)) =>
      x -> init(pipe, z, idByVar, loc)
    })
    val myEpochByProducer = s.producers
      .map({ case (x, (_, _, delayExpr)) =>
        // TODO: what if it turns out to be undefined?
        val IntCst(relativeDelay) = eval(delayExpr)
        // This producer requires the input stream to have delay
        //     T + relativeDelay
        // for some T.
        // Furthermore, I know the actual, absolute delay:
        //     absoluteDelay = T + relativeDelay
        val absoluteDelay = pipe.nodes(inputs(x)).absoluteDelayToFirst
        x -> (absoluteDelay - relativeDelay.toInt)
      })
    val myEpochs = myEpochByProducer.values.toSet
    val myEpoch = if (myEpochs.isEmpty) {
      // Stream sources (e.g., counters) start running immediately
      0
    } else if (myEpochs.size > 1) {
      val str =
        myEpochByProducer.map({ case (x, d) => s"$x -> $d" }).mkString(", ")
      throw DelayMismatch(s"there is a delay mismatch: $str")
    } else {
      myEpochs.head
    }
    val delayByAccumulator =
      s.accumulators.flatMap({ case (x, (_, _, delayExpr)) =>
        eval(delayExpr) match {
          case Tuple() => None
          case IntCst(relativeDelay) =>
            Some(x -> (myEpoch + relativeDelay.toInt))
          case _ => ???
        }
      })
    StmBuildNode(
      pipe = pipe,
      data = eval(s.initData),
      hw = StmNodeHardware(
        id = StmNodeId(Param("sbuild")().name),
        data = s.nextData,
        inputs = inputs,
        nextByAccumulator = s.accumulators
          .map({ case (x, (_, next, _)) => x -> next }),
        typ = s.typ.asInstanceOf[TyStm],
        loc = loc,
        absoluteDelayToFirst = myEpoch + outDelay.toInt,
        absoluteDelayToLast = (myEpoch + outDelay + n - 1).toInt,
        absoluteDelayByAccumulator = delayByAccumulator
      ),
      acc = s.accumulators.map({ case (x, (z, _, _)) => x -> eval(z) })
    )
  }
}
