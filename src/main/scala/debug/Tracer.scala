package debug

import ir._

import os.Path
import scala.annotation.tailrec
import upickle.default.Writer

private object Serialization {
  // See https://docs.scala-lang.org/toolkit/json-serialize.html

  implicit val deadlockReasonWriter: Writer[DeadlockReason] =
    upickle.default
      .writer[String]
      .comap({
        case EmptyStreamRead => "attempt to read from empty stream"
        case TooManySteps    => "too many steps"
      })

  implicit val nodeStateWriter: Writer[NodeState] =
    upickle.default
      .writer[String]
      .comap({
        case Empty    => "empty"
        case Stalled  => "stalled"
        case Invalid  => "invalid"
        case Valid(v) => s"valid (${PrettyPrinter.show(v, evalVec = true)()})"
        case Deadlocked(reasons) => s"deadlocked (${reasons.mkString(", ")})"
      })

  implicit val evalExceptionWriter: Writer[EvalException] =
    upickle.default.writer[String].comap(e => e.toString)

  implicit val summarizedTraceNodeWriter: Writer[SummarizedTraceNode] =
    upickle.default.macroW
  implicit val fullTraceNodeWriter: Writer[FullTraceNode] =
    upickle.default.macroW
  implicit val errorTraceNodeWriter: Writer[ErrorTraceNode] =
    upickle.default.macroW
  implicit val traceNodeWriter: Writer[TraceNode] = upickle.default.macroW
  implicit val traceWriter: Writer[Trace] = upickle.default.macroW
}

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

case class ErrorTraceNode(exc: EvalException) extends TraceNode

/** Provides methods for generating traces showing the state of a pipeline at
  * each cycle.
  */
object Tracer {

  /** Traces execution step-by-step, showing the accumulator values and outputs
    * for all streams.
    *
    * @param s
    *   the stream to trace.
    * @return
    *   a summary of the stream state and output at each step.
    */
  def traceAll(s: StmBuild): Trace = {
    @tailrec
    def trace(
        s: StmNode,
        summaries: Seq[TraceNode]
    ): Seq[TraceNode] = {
      s.state match {
        case Empty | _: Deadlocked =>
          (TraceNode(s, TraceAll) +: summaries).reverse
        case Stalled | Invalid | _: Valid =>
          val newSummaries = TraceNode(s, TraceAll) +: summaries
          val next =
            try {
              s.step()
            } catch {
              case ex: EvalException =>
                return (ErrorTraceNode(ex) +: newSummaries).reverse
            }
          trace(next, newSummaries)
      }
    }

    try {
      Trace(trace(StmNode(s), Seq()))
    } catch {
      case ex: EvalException =>
        Trace(Seq(ErrorTraceNode(ex)))
    }
  }

  /** Traces execution step-by-step, showing the accumulator values and outputs
    * only for the top-level stream.
    *
    * @param s
    *   the stream to trace.
    * @return
    *   a summary of the stream state and output at each step.
    */
  def traceTop(s: StmBuild): Trace = {
    @tailrec
    def trace(
        s: StmNode,
        summaries: Seq[TraceNode]
    ): Seq[TraceNode] = {
      s.state match {
        case Empty | _: Deadlocked =>
          (TraceNode(s, TraceTop) +: summaries).reverse
        case Stalled =>
          trace(s.step(), summaries)
        case Invalid | _: Valid =>
          trace(s.step(), TraceNode(s, TraceTop) +: summaries)
      }
    }

    Trace(trace(StmNode(s), Seq()))
  }
}
