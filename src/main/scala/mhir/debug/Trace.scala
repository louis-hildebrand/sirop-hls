package mhir.debug

import mhir.ir.evaluate._
import os.Path

/** A step-by-step account of the execution of a pipeline.
  *
  * @param steps
  *   the state of the pipeline at each step.
  */
case class Trace(steps: Seq[TraceNode]) {

  /** Converts this trace to JSON.
    */
  def json: String = {
    upickle.default.write(this, indent = 4)(Serialization.traceWriter)
  }

  /** Saves this trace to a file.
    *
    * @param path
    *   the file in which to save the trace.
    */
  def dump(path: Path): Unit = {
    os.write(path, this.json)
  }

  /** Saves this trace to a file in the current working directory.
    *
    * @param path
    *   the name of the file in which to save the trace.
    */
  def dump(path: String): Unit = this.dump(os.pwd / path)
}

private sealed trait TraceType
private object TraceAll extends TraceType
private object TraceTop extends TraceType

/** One node in a trace.
  */
sealed trait TraceNode

/** Factory for [[TraceNode]].
  */
object TraceNode {
  def apply(s: StmNode, mode: TraceType): TraceNode = {
    mode match {
      case TraceAll => FullTraceNode(s)
      case TraceTop => SummarizedTraceNode(s)
    }
  }
}

/** One node in a trace containing accumulator values for a stream <i>and</i>
  * its inputs.
  *
  * @param n
  *   the current stream length. If the stream has a valid output, that output
  *   is included in the length.
  * @param state
  *   the current state of the stream.
  * @param accumulators
  *   the current value of each data accumulator.
  * @param inputs
  *   the input streams.
  */
case class FullTraceNode(
    n: Long,
    state: NodeState,
    accumulators: Map[String, String],
    inputs: Map[String, TraceNode]
) extends TraceNode

/** Factory for [[FullTraceNode]].
  */
object FullTraceNode {
  def apply(s: StmNode): FullTraceNode = {
    FullTraceNode(
      n = s.n,
      state = s.state,
      accumulators = s.currentValByDataAcc.map({ case (x, v) =>
        x.name -> PrettyPrinter.show(v, evalVec = true)()
      }),
      inputs = s.inputs.map({ case (x, s) => x.name -> FullTraceNode(s) })
    )
  }
}

/** One node in a trace containing accumulator values for a stream but not its
  * inputs.
  *
  * @param n
  *   the current stream length. If the stream has a valid output, that output
  *   is included in the length.
  * @param state
  *   the current state of the stream.
  * @param accumulators
  *   the current value of each data accumulator.
  * @param inputLengths
  *   the current length of each input stream.
  */
case class SummarizedTraceNode(
    n: Long,
    state: NodeState,
    accumulators: Map[String, String],
    inputLengths: Map[String, Long]
) extends TraceNode

/** Factory for [[SummarizedTraceNode]].
  */
object SummarizedTraceNode {
  def apply(s: StmNode): SummarizedTraceNode = {
    SummarizedTraceNode(
      n = s.n,
      state = s.state,
      accumulators = s.currentValByDataAcc.map({ case (x, v) =>
        x.name -> PrettyPrinter.show(v, evalVec = true)()
      }),
      inputLengths = s.inputs.map({ case (x, s) => x.name -> s.n })
    )
  }
}

/** One node in a trace showing that an exception has occurred.
  *
  * @param exc
  *   the exception that interrupted evaluation.
  */
case class ErrorTraceNode(exc: EvalException) extends TraceNode
