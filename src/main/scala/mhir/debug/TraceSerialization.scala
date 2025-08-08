package mhir.debug

import mhir.ir._
import mhir.ir.evaluate._
import upickle.default._
import ujson._

object TraceSerialization {
  // See https://docs.scala-lang.org/toolkit/json-serialize.html

  implicit val deadlockReasonWriter: Writer[DeadlockReason] =
    upickle.default
      .writer[String]
      .comap({
        case EmptyStreamRead => "attempt to read from empty stream"
        case TooManySteps    => "too many steps"
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

  implicit val statelessTraceNodeWriter: Writer[StatelessTraceNode] =
    upickle.default.macroW
  implicit val stmBufferTraceNodeWriter: Writer[StmBufferTraceNode] =
    upickle.default.macroW
  implicit val stmBuildTraceNodeWriter: Writer[StmBuildTraceNode] =
    upickle.default.macroW
  // TODO: Write a custom serializer that omits accumulators field if there are
  //       none, includes `ready` signals, etc.?
  implicit val traceNodeWriter: Writer[TraceNode] = upickle.default.macroW

  implicit val errorTraceStepWriter: Writer[ErrorTraceStep] =
    upickle.default.macroW
  implicit val validTraceStepWriter: Writer[ValidTraceStep] =
    upickle.default.macroW
  implicit val traceStepWriter: Writer[TraceStep] = upickle.default.macroW

  implicit val traceWriter: Writer[Trace] = upickle.default.macroW
}
