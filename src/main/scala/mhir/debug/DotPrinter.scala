package mhir.debug

import mhir.eval._
import mhir.ir._
import os.Path

/** Provides functions for visualizing a [[Trace]] using
  * [[https://graphviz.org/ Graphviz]].
  */
object DotPrinter {

  private case class Node(id: StmNodeId, isNop: Boolean)

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
      dir: Path = os.pwd / "traces" / "default",
      overwrite: Boolean = false,
      topName: String = "main"
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
      val dot = step match {
        case step: ValidTraceStep =>
          toDot(step, trace.structure, topName)
        case step: ErrorTraceStep =>
          val label = step.err.toString
          s"""digraph g {
             |    v [shape="plain", label="$label"];
             |}
             |""".stripMargin
      }
      os.write(dir / s"step_$i.dot", dot)
    }
    os.proc(os.pwd / "src" / "main" / "sh" / "dots_to_svg.sh", dir.toString())
      .call(cwd = os.pwd)
    for (f <- os.list(dir)) {
      if (f.ext == "dot") {
        os.remove(f)
      }
    }
  }

  /** Converts this step to the
    * [[https://graphviz.org/doc/info/lang.html DOT language]].
    *
    * @param step
    *   the step to convert.
    * @param g
    *   the immutable structure of the stream pipeline.
    */
  private def toDot(
      step: ValidTraceStep,
      g: DiGraph[StmNodeId],
      topName: String
  ): String = {
    val nodes = nodesToDot(step, topName)
    val edges = edgesToDot(
      step,
      g = g.mapNodes(id => {
        val isNop = step.nodes(id).isInstanceOf[StmNopTraceNode]
        Node(id, isNop)
      })
    )
    ("digraph g {"
      + "\n    rankdir=\"TB\";"
      + "\n    // Nodes"
      + s"\n${indent(nodes)}"
      + "\n"
      + "\n    // Edges"
      + s"\n${indent(edges)}"
      + "\n}\n")
  }

  private def nodesToDot(step: ValidTraceStep, topName: String): String = {
    val mainNodes = step.nodes
      .filter({ case (_, node) => node.loc == InMain })
      .map({ case (id, node) => nodeToDot(id, node) })
      .mkString("\n")
    val otherNodes = step.nodes
      .flatMap({ case (id, node) =>
        node.loc match {
          case InMain          => None
          case _: TestStimulus => Some(testStimulusNodeToDot(id, node))
          case Sink            => Some(nodeToDot(id, node))
        }
      })
      .mkString("\n")
    s"""subgraph cluster_main {
       |    label = "$topName";
       |    style = "dotted";
       |
       |${indent(mainNodes)}
       |}
       |
       |$otherNodes
       |""".stripMargin.stripTrailing
  }

  private def testStimulusNodeToDot(id: StmNodeId, node: TraceNode): String = {
    val TestStimulus(x) = node.loc
    s"""$id [shape="ellipse", label="$x", style="dotted", fontcolor="gray"];"""
  }

  private def nodeToDot(id: StmNodeId, node: TraceNode): String = {
    node match {
      case node: StmBuildTraceNode => stmBuildNodeToDot(id, node)
      case node: LetStmTraceNode   => letStmNodeToDot(id, node)
      case _: StmNopTraceNode      => nopNodeToDot(id)
      case _: TerminalTraceNode    => terminalNodeToDot(id)
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
    s"""$id [shape="record", label="$acc"];"""
  }

  private def letStmNodeToDot(
      id: StmNodeId,
      node: LetStmTraceNode
  ): String = {
    val data = node.buffer.map(_.toString()).mkString("|")
    s"""$id [shape="Mrecord", label="$data"];"""
  }

  private def nopNodeToDot(id: StmNodeId): String = {
    s"""$id [shape="point", style="invis"];"""
  }

  private def terminalNodeToDot(id: StmNodeId): String = {
    s"""$id [shape="point"];"""
  }

  private def edgesToDot(
      step: ValidTraceStep,
      g: DiGraph[Node]
  ): String = {
    g.edges.toSeq
      .map({ case (u, v) => u -> v })
      .sortBy({ case (u, _) => u.id.id })
      .map({ case (u, v) => edgeToDot(step, from = u, to = v) })
      .mkString("\n")
  }

  private def edgeToDot(
      step: ValidTraceStep,
      from: Node,
      to: Node
  ): String = {
    // TODO: Also show `read` flags in LetStm
    val out =
      step.nodes.get(from.id).flatMap(_.out.get(to.id)) match {
        case Some(e) => e.toString
        case None    => ""
      }
    val ready =
      step.nodes.get(to.id).exists(producer => producer.ready.contains(from.id))
    val transferOk = out.nonEmpty && ready
    val color = if (transferOk) "green" else "black"
    val style = if (transferOk) "solid" else "dashed"
    val dir = {
      val tail = ready && !from.isNop
      val head = out.nonEmpty && !to.isNop
      (tail, head) match {
        case (false, false) => "none"
        case (false, true)  => "forward"
        case (true, false)  => "back"
        case (true, true)   => "both"
      }
    }
    val label = if (from.isNop) "" else out
    s"""${from.id} -> ${to.id} [label="$label", dir="$dir", color="$color", style="$style"];"""
  }
}
