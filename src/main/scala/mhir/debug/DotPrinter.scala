package mhir.debug

import mhir.ir._
import mhir.ir.evaluate._
import os.Path

/** Provides functions for visualizing a [[Trace]] using
  * [[https://graphviz.org/ Graphviz]].
  */
object DotPrinter {

  /** Save this trace to a sequence of .dot files which can be converted to
    * images by [[https://graphviz.org/ Graphviz]].
    *
    * @note
    *   the `dot` program must be installed on your computer and in your `PATH`.
    *
    * @param dir
    *   the directory in which to save the .dot files.
    * @param overwrite
    *   whether existing files in the directory should be deleted.
    */
  def dumpDot(
      trace: Trace,
      dir: Path = os.pwd / "traces",
      overwrite: Boolean = false
  ): Unit = {
    val alreadyExists =
      os.exists(dir) || (os.isDir(dir) && os.list(dir).nonEmpty)
    if (alreadyExists) {
      if (overwrite) {
        os.remove.all(dir)
      } else {
        throw new IllegalArgumentException(s"$dir already exists.")
      }
    }
    os.makeDir.all(dir)
    for ((step, i) <- trace.steps.zipWithIndex) {
      step match {
        case step: ValidTraceStep =>
          os.write(
            dir / s"step_$i.dot",
            toDot(step, trace.structure, trace.sink)
          )
        case _: ErrorTraceStep =>
          ???
      }
    }
    os.proc(os.pwd / "src" / "main" / "sh" / "dots_to_svg.sh", dir.toString())
      .call(cwd = os.pwd)
  }

  /** Converts this step to the
    * [[https://graphviz.org/doc/info/lang.html DOT language]].
    *
    * @param step
    *   the step to convert.
    * @param g
    *   the immutable structure of the stream pipeline.
    */
  def toDot(
      step: ValidTraceStep,
      g: DiGraph[StmNodeId],
      sinkId: StmNodeId
  ): String = {
    val nodes = nodesToDot(step)
    val edges = edgesToDot(step, g, sinkId)
    ("digraph g {"
      + "\n    rankdir=\"TB\";"
      + "\n    // Nodes"
      + s"\n${indent(nodes)}"
      + "\n"
      + "\n    // Edges"
      + s"\n${indent(edges)}"
      + "\n}\n")
  }

  private def nodesToDot(step: ValidTraceStep): String = {
    val nodes =
      step.nodes.map({ case (id, node) => nodeToDot(id, node) }).mkString("\n")
    s"""$nodes\nsink [shape="point"];"""
  }

  private def nodeToDot(id: StmNodeId, node: TraceNode): String = {
    node match {
      case node: StmBuildTraceNode  => stmBuildNodeToDot(id, node)
      case node: StmBufferTraceNode => stmBufferNodeToDot(id, node)
      case _: StatelessTraceNode    => s"$id;"
    }
  }

  private def stmBuildNodeToDot(
      id: StmNodeId,
      node: StmBuildTraceNode
  ): String = {
    val acc =
      (s"{n|${node.n}}" +: node.accumulators
        .map({ case (x, v) => s"{$x|$v}" })
        .toSeq)
        .mkString("|")
    val label = s"$id|$acc"
    s"""$id [shape="record", label="$label"];"""
  }

  private def stmBufferNodeToDot(
      id: StmNodeId,
      node: StmBufferTraceNode
  ): String = {
    val data = node.data.map(_.toString()).getOrElse("-")
    s"""$id [shape="Mrecord", label="$id|$data"];"""
  }

  private def edgesToDot(
      step: ValidTraceStep,
      g: DiGraph[StmNodeId],
      sinkId: StmNodeId
  ): String = {
    (g.edges.toSeq.map({ case (u, v) => u -> Some(v) }) :+ (sinkId -> None))
      .sortBy({ case (u, _) => u.id })
      .map({ case (u, v) => edgeToDot(step, from = u, to = v) })
      .mkString("\n")
  }

  private def edgeToDot(
      step: ValidTraceStep,
      from: StmNodeId,
      to: Option[StmNodeId]
  ): String = {
    val out = step.nodes.get(from).flatMap(_.out) match {
      case Some(e) => e.toString
      case None    => ""
    }
    val ready = to.exists(id =>
      step.nodes.get(id).exists(producer => producer.ready.contains(from))
    )
    val transferOk = out.nonEmpty && (ready || to.isEmpty)
    val color = if (transferOk) "green" else "black"
    val dir =
      if (transferOk) "both"
      else if (ready) "back"
      else "forward"
    val style =
      if (transferOk) "solid"
      else "dashed"
    val toId = to match {
      case Some(id) => id.id
      case None     => "sink"
    }
    s"""${from.id} -> $toId [label="$out", dir="$dir", color="$color", style="$style"];"""
  }
}
