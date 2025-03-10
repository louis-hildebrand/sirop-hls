package debug

import ir._
import opt.PartialEvalPass

import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets
import sys.process._

sealed trait Scope {
  def equalOrInside(that: Scope): Boolean
}
case class TableScope(id: String, parent: Scope) extends Scope {
  def outermostTableScope: TableScope = {
    parent match {
      case t: TableScope => t.outermostTableScope
      case _             => this
    }
  }

  override def equalOrInside(that: Scope): Boolean = {
    this == that || this.parent.equalOrInside(that)
  }
}
case class FunctionScope(id: String, parent: Scope) extends Scope {
  override def equalOrInside(that: Scope): Boolean = {
    this == that || this.parent.equalOrInside(that)
  }
}
case class StreamScope(id: String, parent: Scope) extends Scope {
  override def equalOrInside(that: Scope): Boolean = {
    this == that || this.parent.equalOrInside(that)
  }
}
case object GlobalScope extends Scope {
  override def equalOrInside(that: Scope): Boolean = {
    this == that
  }
}

sealed trait DotNode {

  /** Which component this expression belongs in (the top level, inside a tuple,
    * inside a function, etc.).
    */
  val scope: Scope

  /** Scope in which this node should be drawn. Usually this will be the same as
    * `scope`, but it may differ for elements that are in a table but not
    * inlined.
    */
  def drawingScope: Scope = {
    // Only table cells belong inside tables
    this match {
      case _: DotTableCell => scope
      case n =>
        n.scope match {
          case ts: TableScope => ts.outermostTableScope.parent
          case s              => s
        }
    }
  }

  // TODO: I should probably distinguish between full ID (table + port) and just the port.
  /** Unique identifier for this node, to be used in the DOT code. If this node
    * is a table cell, it instead represents the port.
    */
  val id: String = DotNode.freshId()

  /** The string to use when referring to this node (e.g., in an edge). This may
    * simply be the `id`, but for table cells it includes the ID of the
    * top-level node as well as the port.
    */
  val fullPath: String

  /** Nodes outside this one that this node is connected to. For example, if
    * this node is an adder, then its dependencies would be its inputs.
    */
  val dependencies: Seq[DotNode]

  /** All of this node's dependencies, including direct and transitive
    * dependencies.
    */
  def allDependencies: Set[DotNode] = {
    dependencies
      .flatMap(n => n.allDependencies + n)
      .toSet
  }

  /** Edges required by this node. Each node generally stores its incoming
    * edges. For example, if this is an adder, it would need to store the edges
    * to each of its inputs. Nodes that contain other nodes (e.g., tuples,
    * functions) must also store the edges inside themselves.
    */
  def edges: Set[DotEdge]

  /** The DOT code defining this node, including nodes inside this one but NOT
    * including its dependencies nor its edges.
    */
  def dot: String

  override def equals(obj: Any): Boolean = {
    obj match {
      case n: DotNode => n.id == this.id
      case _          => false
    }
  }

  override def hashCode(): Int = this.id.hashCode
}
private object DotNode {
  private var nextId: Int = 0
  def freshId(): String = {
    nextId += 1
    s"node$nextId"
  }
}

/** A free variable or a function parameter.
  *
  * @param name
  *   The variable name.
  */
case class DotParam(name: String, scope: Scope) extends DotNode {
  override val fullPath: String = id
  override val dependencies: Seq[DotNode] = Seq()
  override def edges: Set[DotEdge] = Set()
  override val dot: String = s"""$id [label="$name", shape="septagon"];"""
}

/** An expression that evaluates to a scalar. This could be a constant, a sum,
  * etc.
  *
  * @param label
  *   The label for the node.
  * @param labeledChildren
  *   The inputs to this node along with a label for each one. The label is
  *   useful for non-commutative operations, for example.
  */
case class DotScalar(
    label: String,
    labeledChildren: Seq[(String, DotNode)],
    scope: Scope
) extends DotNode {
  def isConst: Boolean = labeledChildren.isEmpty
  override val fullPath: String = id
  override val dependencies: Seq[DotNode] = labeledChildren.map({ case (_, c) =>
    c
  })
  override def edges: Set[DotEdge] =
    labeledChildren
      .map({ case (label, c) => DotEdge.toParent(this, c, label = label) })
      .toSet
  override def dot: String = {
    val shape = if (isConst) "none" else "ellipse"
    s"""$id [label="$label", shape="$shape"];"""
  }
}

/** One cell in a table (i.e., one element of a tuple or vector).
  */
sealed trait DotTableCell extends DotNode

/** A table cell that directly contains its value.
  */
case class InlinedScalarCell(contents: String, scope: TableScope)
    extends DotTableCell {
  override val fullPath: String = s"${scope.outermostTableScope.id}:$id"
  override val dependencies: Seq[DotNode] = Seq()
  override def edges: Set[DotEdge] = Set()
  override def dot: String =
    s"""<TR><TD PORT="$id" BGCOLOR="white">$contents</TD></TR>"""
}

/** A table cell containing another table.
  */
case class InlinedTableCell(table: DotTuple, outerScope: TableScope)
    extends DotTableCell {
  override val scope: TableScope = outerScope
  override val fullPath: String = s"${outerScope.outermostTableScope.id}:$id"
  override val dependencies: Seq[DotNode] = table.dependencies
  override def edges: Set[DotEdge] = table.edges
  override def dot: String =
    s"""<TR><TD PORT="$id" BGCOLOR="white">${table.dotTable}</TD></TR>"""
}

/** A table cell with an edge to its value.
  */
case class PointerCell(value: DotNode, scope: TableScope) extends DotTableCell {
  override val fullPath: String = s"${scope.outermostTableScope.id}:$id"
  override val dependencies: Seq[DotNode] = Seq(value)
  override def edges: Set[DotEdge] = Set(DotEdge.toParent(this, value))
  override def dot: String =
    s"""<TR><TD PORT="$id" BGCOLOR="white">.</TD></TR>"""
}

/** A node containing other nodes. This could represent a tuple or a vector.
  *
  * @param elems
  *   The elements within this tuple.
  * @param innerScope
  *   The scope within this tuple, that the elements inside will belong to.
  */
case class DotTuple(
    elems: Seq[DotNode],
    innerScope: TableScope
) extends DotNode {
  override val id: String = innerScope.id
  override val fullPath: String = s"${innerScope.outermostTableScope.id}:$id"
  val cells: Seq[DotTableCell] = {
    elems.map({
      // Inline scalar constants and tuples
      case s: DotScalar if s.isConst => InlinedScalarCell(s.label, innerScope)
      case t: DotTuple               => InlinedTableCell(t, innerScope)
      case c                         => PointerCell(c, innerScope)
    })
  }
  override val dependencies: Seq[DotNode] = cells.flatMap(c => c.dependencies)
  override val scope: Scope = innerScope.parent

  override def edges: Set[DotEdge] = cells.flatMap(c => c.edges).toSet
  def dotTable: String = {
    val rows = cells.map(c => c.dot).mkString("\n")
    s"""<TABLE PORT="$id" BGCOLOR="grey">\n${indent(rows)}\n</TABLE>"""
  }
  override def dot: String = {
    s"""$id [shape="none", label=<\n${indent(dotTable)}\n>];"""
  }
}

/** A function, which corresponds to a component in hardware.
  */
case class DotFunction(
    param: DotParam,
    body: DotNode,
    innerScope: FunctionScope
) extends DotNode {
  override val id: String = innerScope.id
  override val fullPath: String = id
  override val dependencies: Seq[DotNode] =
    (param.allDependencies ++ body.allDependencies)
      .filter(n => innerScope.parent.equalOrInside(n.drawingScope))
      .toSeq
  override val scope: Scope = innerScope.parent
  override def edges: Set[DotEdge] =
    // Maybe it would be better to filter out dependencies that aren't in the same scope, but since we're tossing
    // everything into a set it shouldn't be a big deal
    param.edges ++ body.edges ++ body.allDependencies.flatMap(n => n.edges)
  override def dot: String = {
    val nodesToDraw =
      (body.allDependencies + body)
        .filter(n => n.drawingScope == innerScope)
        .map(n => n.dot)
        .mkString("\n")
    s"""subgraph cluster_$id {
       |    color="black";
       |    ${param.id} [label="", shape="septagon"];
       |${indent(nodesToDraw)}
       |}
       |""".stripMargin.stripTrailing
  }
}

/** A call to function `f` with argument `arg`. In other words, instantiate `f`
  * and connect `arg` to the input port of `f`.
  */
case class DotFunCall(f: DotFunction, arg: DotNode, scope: Scope)
    extends DotNode {
  override val fullPath: String = id
  // TODO: It would be nicer to skip the point and just connect the output to whatever's using it.
  //       Maybe add a `value` field which points to the node to connect to.
  override val dependencies: Seq[DotNode] = Seq(f, arg)
  override def edges: Set[DotEdge] =
    Set(DotEdge.toParent(f.param, arg), DotEdge.toParent(this, f.body))
  override def dot: String = s"""$id [shape="point"]"""
}

case class DotRegister(scope: StreamScope) extends DotNode {
  override val fullPath: String = id
  override val dependencies: Seq[DotNode] = Seq()
  override def edges: Set[DotEdge] = Set()
  override def dot: String = s"""$id [label="reg", shape="box"];"""
}

case class DotStream(z: DotNode, f: DotFunction, innerScope: StreamScope)
    extends DotNode {
  private val register = DotRegister(innerScope)
  private val mux = DotScalar("MUX", Seq(("", z), ("", register)), innerScope)
  // TODO: Handle stream that depends on another stream (ready-valid interface)
  override val fullPath: String = id
  override val dependencies: Seq[DotNode] = z.dependencies ++ f.dependencies
  override val scope: Scope = innerScope.parent
  override def edges: Set[DotEdge] = {
    val tupleOut = f.body.asInstanceOf[DotTuple]
    assert(tupleOut.cells.length == 2)
    val nextAccCell = tupleOut.cells.head
    (z.edges
      ++ f.edges
      ++ mux.edges
      + DotEdge.toParent(f.param, mux)
      + DotEdge.toChild(nextAccCell, register))
  }

  override def dot: String = {
    s"""subgraph cluster_$id {
       |    color="black"
       |${indent(z.dot)}
       |${indent(f.dot)}
       |${indent(mux.dot)}
       |${indent(register.dot)}
       |}
       |""".stripMargin
  }
}

case class DotEdge(
    source: DotNode,
    target: DotNode,
    label: String,
    dir: String
) {
  val dot =
    s"""${source.fullPath} -> ${target.fullPath} [label="$label", dir="$dir", arrowhead="vee", arrowtail="vee"];"""
}
object DotEdge {
  def toParent(parent: DotNode, child: DotNode, label: String = ""): DotEdge = {
    DotEdge(
      source = child,
      target = parent,
      label = label,
      dir = "forward"
    )
  }

  def toChild(parent: DotNode, child: DotNode, label: String = ""): DotEdge = {
    DotEdge(source = child, target = parent, label = label, dir = "back")
  }
}

object DotPrinter {
  def save(
      e: Expr,
      path: String,
      keepDotFile: Boolean = false
  ): Unit = {
    val (dotPath, imgPath) = if (path.endsWith(".dot")) {
      (path, path.substring(0, path.length - 4) + ".png")
    } else if (path.endsWith(".png")) {
      (path.substring(0, path.length - 4) + ".dot", path)
    } else {
      ???
    }
    Files.write(
      Paths.get(dotPath),
      makeDotGraph(e).getBytes(StandardCharsets.UTF_8)
    )
    val cmd =
      s"dot -T ${imgPath.substring(imgPath.length - 3, imgPath.length)} $dotPath -o $imgPath"
    cmd.!!
    if (!keepDotFile) {
      Files.delete(Paths.get(dotPath))
    }
  }

  private def makeDotGraph(e: Expr): String = {
    // TODO: Look for functions that are never called and add lines going in and coming out for clarity?
    val params =
      findFreeVars(e)(Set()).map(p => p -> DotParam(p.name, GlobalScope)).toMap
    val resultNode = toDot(e, GlobalScope)(params)
    val topLevelNodes = resultNode.allDependencies + resultNode
    val nodesDot =
      topLevelNodes.toSeq.sortBy(n => n.id).map(n => n.dot).mkString("\n")
    val edges = topLevelNodes.flatMap(n => n.edges)
    val edgesDot = edges.toSeq
      .sortBy(e => (e.source.id, e.target.id))
      .map(e => e.dot)
      .mkString("\n")
    s"digraph {\n${indent("""rankdir="LR"""")}\n${indent(nodesDot)}\n${indent(edgesDot)}\n}\n"
  }

  private def findFreeVars(
      e: Expr
  )(implicit boundVars: Set[Param]): Set[Param] = {
    e match {
      case p: Param              => if (boundVars.contains(p)) Set() else Set(p)
      case Function(param, body) => findFreeVars(body)(boundVars + param)
      case e =>
        e.children.foldLeft(Set[Param]())((s, e) => s.union(findFreeVars(e)))
    }
  }

  private def toDot(
      e: Expr,
      scope: Scope
  )(implicit params: Map[Param, DotParam]): DotNode = {
    e match {
      case IntCst(n) =>
        DotScalar(n.toString, Seq(), scope)
      case True =>
        DotScalar("T", Seq(), scope)
      case False =>
        DotScalar("F", Seq(), scope)
      case DontCare =>
        DotScalar("???", Seq(), scope)
      case p: Param if params.contains(p) =>
        // TODO: What if this parameter is for a non-scalar?
        params(p)
      case p: Param =>
        throw new IllegalArgumentException(
          s"Missing name for free variable $p."
        )
      case Sum(terms) =>
        val labeledTerms = terms.map(e => ("", toDot(e, scope)))
        DotScalar("+", labeledTerms, scope)
      case Prod(factors) =>
        val labeledFactors = factors.map(e => ("", toDot(e, scope)))
        DotScalar("*", labeledFactors, scope)
      case e: BinOp =>
        val left = toDot(e.e1, scope)
        val right = toDot(e.e2, scope)
        DotScalar(labelBinOp(e), Seq(("L", left), ("R", right)), scope)
      case And(terms @ _*) =>
        val labeledTerms = terms.map(e => ("", toDot(e, scope)))
        DotScalar("&&", labeledTerms, scope)
      case Not(Equal(e1, e2)) =>
        val left = toDot(e1, scope)
        val right = toDot(e2, scope)
        DotScalar("!=", Seq(("", left), ("", right)), scope)
      case Not(e) =>
        val child = toDot(e, scope)
        DotScalar("!", Seq(("", child)), scope)
      case IfThenElse(c, t, f) =>
        val cond = toDot(c, scope)
        val trueVal = toDot(t, scope)
        val falseVal = toDot(f, scope)
        // TODO: Shape this like a MUX
        DotScalar(
          "if",
          Seq(("c", cond), ("t", trueVal), ("f", falseVal)),
          scope
        )
      case Tuple(elems @ _*) =>
        val tupScope = TableScope(DotNode.freshId(), parent = scope)
        DotTuple(
          elems.map(e => toDot(e, tupScope)),
          innerScope = tupScope
        )
      case VecBuild(n, f) =>
        PartialEvalPass.partialEval(n) match {
          case IntCst(n) =>
            val vecScope = TableScope(DotNode.freshId(), parent = scope)
            val children = (0 until n)
              .map(i => PartialEvalPass.partialEval(FunCall(f, i)))
              .map(e => toDot(e, vecScope))
            DotTuple(children, innerScope = vecScope)
          case _ =>
            throw new IllegalArgumentException(
              "Only VecBuild with a constant length is supported."
            )
        }
      case VecAccess(v, i) =>
        // TODO: Convert vector to tuple ahead of time to avoid code duplication?
        val vDot = toDot(v, scope)
        val dot = vDot match {
          case t: DotTuple =>
            PartialEvalPass.partialEval(i) match {
              case IntCst(i) => Some(t.cells(i))
              case _         => None
            }
          case _ =>
            None
        }
        // TODO: Handle accesses to nested vectors as well by creating a new vector of the same size filled with some
        //       kind of "unknown" value?
        dot match {
          case Some(dot) => dot
          case None =>
            DotScalar("v[]", Seq(("v", vDot), ("i", toDot(i, scope))), scope)
        }
      case Function(p, body) =>
        val funcScope = FunctionScope(DotNode.freshId(), parent = scope)
        val pDot = DotParam("", funcScope)
        DotFunction(
          pDot,
          toDot(body, funcScope)(params + (p -> pDot)),
          innerScope = funcScope
        )
      case FunCall(f, a) =>
        DotFunCall(
          toDot(f, scope).asInstanceOf[DotFunction],
          toDot(a, scope),
          scope
        )
      case StmBuild(n, z, f) =>
        val stmScope = StreamScope(DotNode.freshId(), parent = scope)
        val zDot = toDot(z, stmScope)
        val fDot = toDot(f, stmScope).asInstanceOf[DotFunction]
        DotStream(zDot, fDot, stmScope)
    }
  }

  private def labelBinOp(e: BinOp): String = {
    e match {
      case _: Div      => "/"
      case _: Mod      => "%"
      case _: Or       => "||"
      case _: LessThan => "<"
      case _: Equal    => "=="
    }
  }
}
