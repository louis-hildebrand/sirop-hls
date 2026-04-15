package mhir.debug

import mhir.eval._
import mhir.ir._
import ujson._
import upickle.default._

object TraceSerialization {
  // See https://docs.scala-lang.org/toolkit/json-serialize.html

  implicit val deadlockReasonWriter: Writer[DeadlockReason] =
    upickle.default
      .writer[String]
      .comap({
        case EmptyStreamRead  => "attempt to read from empty stream"
        case TooManySteps     => "too many steps"
        case PipelineFixpoint => "pipeline reached fixpoint"
      })

  implicit val nodeIdWriter: Writer[StmNodeId] =
    stringKeyW(upickle.default.writer[String].comap(_.id))

  implicit val graphWriter: Writer[DiGraph[StmNodeId]] =
    upickle.default
      .writer[ujson.Value]
      .comap(g => {
        val nodes = g.nodes.toSeq.sortBy(_.id).map(x => Str(x.id))
        val edges = g.edges.toSeq
          .sortBy({ case (u, v) => (u.id, v.id) })
          .map({ case (u, v) =>
            Arr(Str(u.id), Str(v.id))
          })
        Obj(
          "nodes" -> Arr(nodes),
          "edges" -> Arr(edges)
        )
      })

  implicit val exprWriter: Writer[Expr] =
    upickle.default.writer[String].comap(_.toString)

  implicit val evalExceptionWriter: Writer[EvalException] =
    upickle.default.writer[String].comap(_.toString)

  implicit val stmBuildTraceNodeWriter: Writer[StmBuildTraceNode] =
    upickle.default.macroW
  implicit val letStmTraceNodeWriter: Writer[LetStmTraceNode] =
    upickle.default.macroW
  implicit val stmNopTraceNodeWriter: Writer[StmNopTraceNode] =
    upickle.default.macroW
  implicit val terminalTraceNodeWriter: Writer[TerminalTraceNode] =
    upickle.default.macroW
  implicit val traceNodeWriter: Writer[TraceNode] = upickle.default.macroW

  implicit val errorTraceStepWriter: Writer[ErrorTraceStep] =
    upickle.default.macroW
  implicit val validTraceStepWriter: Writer[ValidTraceStep] =
    upickle.default.macroW
  implicit val traceStepWriter: Writer[TraceStep] = upickle.default.macroW

  implicit val traceWriter: Writer[Trace] = upickle.default.macroW
}
