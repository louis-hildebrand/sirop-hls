package mhir.debug

import mhir.ir._
import mhir.ir.evaluate._
import upickle.default.Writer

import scala.annotation.tailrec

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
        case Empty               => "empty"
        case Stalled             => "stalled"
        case Invalid             => "invalid"
        case Valid(v)            => s"valid (${ExprPrinter.display(v)})"
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
