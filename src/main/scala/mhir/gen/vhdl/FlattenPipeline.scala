package mhir.gen.vhdl

import mhir.canonicalize._
import mhir.gen.CodegenError
import mhir.ir._
import mhir.optimize.{
  LatencyAnalysis,
  LatencyLetStm,
  LatencyNode,
  LatencyStmBuild
}
import mhir.typecheck._

object FlattenPipeline {

  private[vhdl] def apply(
      f: Expr,
      options: VhdlGeneratorOptions
  ): FlatPipeline = {
    validateExpr(f, options)
    val (inputs, stm) = TypeChecker.unwrapTopLevelFunction(f)
    val unusedInputs = inputs.toSet.diff(stm.freeVars)
    val latency =
      new LatencyAnalysis(handshake = options.handshake).actualLatency(f)
    val (sink1, nodes1) = makePipeline(stm, latency)
    val (sink2, nodes2) = deduplicateVars(sink1, nodes1)
    val pipe = FlatPipeline(
      sbuilds = nodes2.collect({ case n: StmBuildNode => n }),
      lets = nodes2.collect({ case n: LetStmNode => n }),
      inputs = inputs.toSet,
      unusedInputs = unusedInputs,
      sink = sink2
    )
    ensureAtLeastOneBuffer(pipe)
  }

  private def validateExpr(e: Expr, options: VhdlGeneratorOptions): Unit = {
    require(
      e.typ != Missing,
      "Expression must be type checked before hardware generation."
    )
    require(
      !e.hasSyntaxSugar,
      "Expression must be lowered before hardware generation."
    )
    require(
      e.freeVars.isEmpty,
      s"Cannot generate hardware for expression with free variables (${e.freeVars})."
    )
    val (inputs, stm) = e match {
      case f: Function => TypeChecker.unwrapTopLevelFunction(f)
      case e           => (Seq(), e)
    }
    for (x <- inputs) {
      require(
        x.typ.isInstanceOf[TyStm],
        s"Top-level parameter has type ${x.typ}."
          + " All top-level parameters must be streams."
      )
      val numOccurrences = stm.countFreeOccurrences(x)
      require(
        numOccurrences <= 1,
        s"Top-level parameter $x is used $numOccurrences times."
          + " No top-level parameter should be used more than once."
          + " To describe a stream with multiple consumers, consider using LetStm."
      )
      if (options.reservedKeywords.contains(x.name)) {
        throw CodegenError(
          s"'${x.name}' cannot be used as an input stream name, since it is a reserved keyword in VHDL"
        )
      }
    }
    options.outName match {
      case None => ()
      case Some(outName) =>
        if (options.reservedKeywords.contains(outName)) {
          throw CodegenError(
            s"'$outName' cannot be used as an output stream name, since it is a reserved keyword in VHDL"
          )
        }
    }
  }

  private def makePipeline(
      e: Expr,
      latency: LatencyNode
  ): (Param, Seq[PipelineNode]) = {
    e match {
      case x: Param =>
        (x, Seq())
      case s: StmBuild =>
        var newEquations = Map[Param, (Expr, Expr)]()
        var newNodes = Seq[PipelineNode]()
        assert(
          latency.isInstanceOf[LatencyStmBuild],
          s"expression $s does not correspond to latency node $latency"
        )
        val lat @ LatencyStmBuild(_, _, producerLatencies) = latency
        for (eqn <- s.equations) {
          eqn match {
            case (x, (p, ready)) if x.typ.isInstanceOf[TyStm] =>
              val (sink, nodes) = makePipeline(p, producerLatencies(x))
              newNodes ++= nodes
              newEquations += x -> (sink, ready)
            case eqn =>
              newEquations += eqn
          }
        }
        val newStm = StmBuild(
          s.n,
          s.data,
          s.valid,
          newEquations
        )(annotations = s.annotations).tchk().asInstanceOf[StmBuild]
        val x = Param("s")(newStm.typ)
        (
          x,
          newNodes :+ StmBuildNode(x, newStm, inputLatency = lat.inputLatency)
        )
      case LetStm(bufSizeExpr, x, in, out) =>
        assert(
          latency.isInstanceOf[LatencyLetStm],
          s"expression $e does not correspond to latency node $latency"
        )
        val LatencyLetStm(_, inLat, outLat) = latency
        val (sinkIn, nodesIn) = makePipeline(in, inLat)
        val (sinkOut, nodesOut) = makePipeline(out, outLat)
        val IntCst(bufSize) = mhir.eval.eval(bufSizeExpr)
        val newNode = LetStmNode(sinkIn, bufSize.toInt, Set(x))
        (sinkOut, nodesIn ++ (newNode +: nodesOut))
      case _ =>
        throw new IllegalArgumentException(
          s"cannot convert non-streaming expression to ANF: $e"
        )
    }
  }

  /** Make separate copies of the output variables in [[LetStmNode]], such that
    * each one is used only once.
    *
    * After the initial pipeline construction, you might end up with a pipeline
    * like
    * {{{
    *   Seq(
    *     LetStmNode(..., out = Set(x)),
    *     StmBuildNode(s = StmMap(x, ...), out = y),
    *     StmBuildNode(s = StmZip(x, y), ...)
    *   )
    * }}}
    * (of course, `StmMap` and `StmZip` would actually be lowered to `sbuild`).
    * Notice how the output `x` from the [[LetStmNode]] is used in two separate
    * places. We need to replace the pipeline above with something more like
    * this:
    * {{{
    *   Seq(
    *     LetStmNode(..., out = Set(x_1, x_2)),
    *     StmBuildNode(s = StmMap(x_1, ...), out = y),
    *     StmBuildNode(s = StmZip(x_2, y), ...)
    *   )
    * }}}
    */
  private def deduplicateVars(
      sink: Param,
      nodes: Seq[PipelineNode]
  ): (Param, Seq[PipelineNode]) = {
    def deduplicateVars(
        nodes: Seq[PipelineNode],
        varsToRename: Set[Param]
    ): (Map[Param, Set[Param]], Param, Seq[PipelineNode]) = {
      nodes match {
        case Seq() =>
          if (varsToRename.contains(sink)) {
            val newSink = sink.freshCopy
            (Map(sink -> Set(newSink)), newSink, Seq())
          } else {
            (Map(), sink, Seq())
          }
        case Seq(StmBuildNode(x, s, latency), rest @ _*) =>
          var (renamings, newSink, newRest) =
            deduplicateVars(rest, varsToRename)
          var newEquations = Map[Param, (Expr, Expr)]()
          for (eqn <- s.equations) {
            eqn match {
              case (x, (p: Param, ready))
                  if x.typ.isInstanceOf[TyStm] && varsToRename.contains(p) =>
                val newP = p.freshCopy
                renamings = renamings +
                  (p -> (renamings.getOrElse(p, Set()) + newP))
                newEquations += x -> (newP, ready)
              case eqn =>
                newEquations += eqn
            }
          }
          val newStm = StmBuild(
            s.n,
            s.data,
            s.valid,
            newEquations
          )(annotations = s.annotations).tchk().asInstanceOf[StmBuild]
          val newNode = StmBuildNode(x, newStm, latency)
          (renamings, newSink, newNode +: newRest)
        case Seq(LetStmNode(in, bufSize, out), rest @ _*) =>
          val (renamings, newSink, newRest) =
            deduplicateVars(rest, varsToRename ++ out)
          val newOut = out.flatMap(x => renamings.getOrElse(x, Set()))
          val (newRenamings, newNode) = if (varsToRename.contains(in)) {
            val newIn = in.freshCopy
            (
              renamings + (in -> (renamings.getOrElse(in, Set()) + newIn)),
              LetStmNode(newIn, bufSize, newOut)
            )
          } else {
            (renamings, LetStmNode(in, bufSize, newOut))
          }
          // The variable inside `out` is no longer needed in the renamings
          // set; it shouldn't appear in any of the nodes that may have come
          // before this one
          (newRenamings -- out, newSink, newNode +: newRest)
      }
    }
    val (renamings, newSink, newNodes) = deduplicateVars(nodes, Set())
    assert(renamings.isEmpty)
    (newSink, newNodes)
  }

  private def ensureAtLeastOneBuffer(pipe: FlatPipeline): FlatPipeline = {
    if (pipe.inputs.contains(pipe.sink)) {
      val newSink = Param("s")(pipe.sink.typ)
      val nop = {
        val TyStm(typ, n) = pipe.sink.typ
        val s = Param("s")(TyStm(typ, -1))
        StmBuild(
          n,
          StmData(s)(),
          True,
          Map[Param, (Expr, Expr)](s -> (pipe.sink, True))
        )().tchk().asInstanceOf[StmBuild]
      }
      val newNode = StmBuildNode(newSink, nop, Some(0))
      pipe.copy(sbuilds = pipe.sbuilds :+ newNode, sink = newSink)
    } else {
      pipe
    }
  }
}
