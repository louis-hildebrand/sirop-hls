package debug

import ir.*
import opt.PartialEvalPass

import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets
import sys.process.*

sealed trait DotExpr {
  val id: String = DotExpr.freshId()
  val dot: String
  val children: Seq[DotExpr]

  def descendants: Seq[DotExpr] = {
    children.flatMap({
      case dt: DotTuple => dt +: dt.descendants
      case c            => Seq(c)
    })
  }
}
private object DotExpr {
  private var nextId: Int = 0
  def freshId(): String = {
    nextId += 1
    s"node$nextId"
  }
}
// Emit each parameter only once but, for simplicity, treat *references* to parameters more like other types of nodes
// (i.e., a DotScalar with a DotParamRef as a child would print out that param ref).
case class DotParam(name: String) extends DotExpr {
  val dot: String = s"$id [label=\"$name\", shape=\"septagon\"];"
  val children: Seq[DotExpr] = Seq()
}
case class DotParamRef(p: DotParam) extends DotExpr {
  val dot: String =
    s"""$id [label=\"\", shape=\"point\"];
       |${p.id} -> $id [arrowhead=\"none\"];
       |""".stripMargin.stripTrailing
  val children: Seq[DotExpr] = Seq()
}
case class DotScalar(
    label: String,
    labeledChildren: Seq[(String, DotExpr)]
) extends DotExpr {
  def isConst: Boolean = labeledChildren.isEmpty
  val dot: String = {
    val shape = if isConst then "none" else "ellipse"
    val node = s"$id [label=\"$label\", shape=\"$shape\"];"
    val edges =
      labeledChildren.map((lab, c) => s"${c.id} -> $id [label=\"$lab\"];")
    s"${labeledChildren.map((_, c) => c.dot).mkString("\n")}\n$node\n${edges.mkString("\n")}"
  }
  val children: Seq[DotExpr] = labeledChildren.map((_, c) => c)
}
case class DotTuple(children: Seq[DotExpr]) extends DotExpr {
  val dot: String = {
    val nonInlinedDescendants = descendants.filter({
      case s: DotScalar if s.isConst => false
      case _: DotTuple               => false
      case _                         => true
    })
    val descendantsStr = nonInlinedDescendants.map(d => d.dot).mkString("\n")
    val thisNode = s"$id [shape=\"none\", label=<\n${indent(table)}\n>];"
    val edgesStr = edges(id).mkString("\n")
    s"$descendantsStr\n$thisNode\n$edgesStr"
  }

  private lazy val cellIds = children.map(_ => DotExpr.freshId())
  private lazy val table: String = {
    val rows: Seq[String] = children
      .map({
        // Inline scalar constants and tuples
        case s: DotScalar if s.isConst => s.label
        case t: DotTuple               => s"\n${indent(t.table)}\n"
        case c                         => "."
      })
      .zip(cellIds)
      .map((lab, cid) =>
        s"<TR><TD BGCOLOR=\"white\" PORT=\"$cid\">$lab</TD></TR>"
      )
    val indentedRows = rows.map(r => indent(r))
    s"<TABLE BGCOLOR=\"darkgrey\">\n${indentedRows.mkString("\n")}\n</TABLE>"
  }
  private def edges(outermostTableId: String): Seq[String] = {
    children
      .zip(cellIds)
      .flatMap((c, cellId) =>
        c match {
          case s: DotScalar if s.isConst => Seq()
          case dt: DotTuple              => dt.edges(outermostTableId)
          // It would be nice to make the edge go to the center of the cell.
          // Unfortunately, headclip=false doesn't seem to have any effect.
          case c => Seq(s"${c.id} -> $outermostTableId:$cellId;")
        }
      )
  }
}
//case class DotStream(dot: String, outId: String) extends DotExpr

object DotPrinter {
  def save(
      e: Expr,
      path: String,
      keepDotFile: Boolean = false,
      nameByVar: Map[Param, String] = Map()
  ): Unit = {
    val (dotPath, imgPath) = if path.endsWith(".dot") then {
      (path, path.substring(0, path.length - 4) + ".png")
    } else if path.endsWith(".png") then {
      (path.substring(0, path.length - 4) + ".dot", path)
    } else {
      ???
    }
    Files.write(
      Paths.get(dotPath),
      makeDotGraph(e, nameByVar).getBytes(StandardCharsets.UTF_8)
    )
    val cmd =
      s"dot -T ${imgPath.substring(imgPath.length - 3, imgPath.length)} $dotPath -o $imgPath"
    cmd.!!
    if !keepDotFile then {
      Files.delete(Paths.get(dotPath))
    }
  }

  private def makeDotGraph(e: Expr, nameByVar: Map[Param, String]): String = {
    // TODO: Emit params separately?
    val params = nameByVar.map((p, name) => p -> DotParam(name))
    val paramDots = params.values.map(p => p.dot).mkString("\n")
    s"""digraph {
       |    rankdir="LR"
       |${indent(paramDots)}
       |${indent(toDot(e)(params).dot)}
       |}
       |""".stripMargin
  }

  private def toDot(
      e: Expr
  )(implicit params: Map[Param, DotParam]): DotExpr = {
    e match {
      case IntCst(n) =>
        DotScalar(n.toString, Seq())
      case True =>
        DotScalar("T", Seq())
      case False =>
        DotScalar("F", Seq())
      case DontCare =>
        DotScalar("???", Seq())
      case p: Param if params.contains(p) =>
        // TODO: What if this parameter is for a non-scalar?
        DotParamRef(params(p))
      case p: Param =>
        throw new IllegalArgumentException(
          s"Missing name for free variable $p."
        )
      case e: BinOp =>
        val left = toDot(e.e1)
        val right = toDot(e.e2)
        DotScalar(labelBinOp(e), Seq(("L", left), ("R", right)))
      case Not(e) =>
        val child = toDot(e)
        DotScalar("!", Seq(("", child)))
      case IfThenElse(c, t, f) =>
        val cond = toDot(c)
        val trueVal = toDot(t)
        val falseVal = toDot(f)
        // TODO: Shape this like a MUX
        DotScalar("if", Seq(("c", cond), ("t", trueVal), ("f", falseVal)))
      case VecBuild(IntCst(n), f) =>
        // TODO: Somehow inline constants, tuples, vectors
        // TODO: Convert vector to tuple to avoid code duplication?
        val children = (0 until n)
          .map(i => PartialEvalPass.partialEval(FunCall(f, i)))
          .map(e => toDot(e))
        DotTuple(children)
      case _: VecBuild =>
        throw new IllegalArgumentException(
          "Only VecBuild with a constant length is supported."
        )
    }
  }

  private def labelBinOp(e: BinOp): String = {
    e match {
      case _: Add      => "+"
      case _: Sub      => "-"
      case _: Mul      => "*"
      case _: Div      => "/"
      case _: Mod      => "%"
      case _: And      => "&&"
      case _: Or       => "||"
      case _: LessThan => "<"
      case _: Equal    => "=="
      case _: NotEqual => "!="
    }
  }
}

def indent(s: String): String =
  s.split('\n').map(ln => s"    $ln").mkString("\n")
