package opt

import scala.collection.mutable

case class DiGraph[T](nodes: Set[T], edges: Set[(T, T)]) {

  lazy val outNeighbours: Map[T, Set[T]] = {
    val neighboursByNode = edges
      .groupBy({ case (u, _) => u })
      .mapValues(xs => xs.map({ case (_, v) => v }))
    nodes.map(v => v -> neighboursByNode.getOrElse(v, Set())).toMap
  }
  lazy val inNeighbours: Map[T, Set[T]] = {
    val neighboursByNode = edges
      .groupBy({ case (_, v) => v })
      .mapValues(xs => xs.map({ case (u, _) => u }))
    nodes.map(v => v -> neighboursByNode.getOrElse(v, Set())).toMap
  }

  /** Construct the condensation graph of this graph. The nodes of the
    * condensation graph are strongly connected components (i.e., sets of nodes
    * from this graph). The condensation graph is acyclic.
    */
  def condensation(): DiGraph[Set[T]] = {
    // https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm

    val visited: mutable.Map[T, Boolean] =
      mutable.Map(nodes.map(v => v -> false).toSeq: _*)
    var vertices = Seq[T]()
    def visit(u: T): Unit = {
      if (!visited(u)) {
        visited(u) = true
        for (v <- outNeighbours(u)) {
          visit(v)
        }
        vertices = u +: vertices
      }
    }
    for (u <- nodes) {
      visit(u)
    }

    val rootByNode = mutable.Map[T, T]()
    val sccByRoot = mutable.Map[T, Set[T]]()
    def assign(u: T, root: T): Unit = {
      if (!rootByNode.contains(u)) {
        rootByNode(u) = root
        sccByRoot(root) = sccByRoot.getOrElse(root, Set()) + u
        for (v <- inNeighbours(u)) {
          assign(v, root)
        }
      }
    }
    for (u <- vertices) {
      assign(u, u)
    }

    val sccNodes = sccByRoot.values.toSet
    val sccEdges = edges.flatMap({ case (u, v) =>
      val sccu = sccByRoot(rootByNode(u))
      val sccv = sccByRoot(rootByNode(v))
      if (sccu == sccv) None else Some(sccu -> sccv)
    })
    DiGraph(sccNodes, sccEdges)
  }

  def remove(u: T): DiGraph[T] = {
    DiGraph(
      nodes.filter(v => u != v),
      edges.filter({ case (v, w) => u != v && u != w })
    )
  }

  /** Get the nodes of this graph in topological order. That is, if there is an
    * edge <code>(u, v)</code>, then <code>v</code> will appear before
    * <code>u</code>.
    */
  def topologicalOrder(): Seq[T] = {
    if (nodes.isEmpty) {
      Seq()
    } else {
      outNeighbours.toSeq.find({ case (_, deps) => deps.isEmpty }) match {
        case Some((v, _)) => v +: this.remove(v).topologicalOrder()
        case None => throw new IllegalArgumentException(s"graph has a cycle")
      }
    }
  }
}
