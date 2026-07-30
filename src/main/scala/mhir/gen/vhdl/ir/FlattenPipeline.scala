package mhir.gen.vhdl
package ir

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

import scala.collection.immutable.ListMap

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
    val pipe1 = FlatPipeline(
      sbuilds = nodes2.collect({ case n: StmBuildNode => n }),
      lets = nodes2.collect({ case n: LetStmNode => n }),
      inputs = inputs.toSet,
      unusedInputs = unusedInputs,
      sink = sink2
    )
    val pipe2 = ensureAtLeastOneBuffer(pipe1)
    val pipe3 = cleanUpSbuilds(pipe2)
    pipe3
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
        var renamings = Map[Expr, Expr]()
        var newProducers = Map[Param, Expr]()
        var newNodes = Seq[PipelineNode]()
        assert(
          latency.isInstanceOf[LatencyStmBuild],
          s"expression $s does not correspond to latency node $latency"
        )
        val lat @ LatencyStmBuild(_, _, producerLatencies) = latency
        // Translate producers and keep track of what will need to be renamed
        for ((x, (p, ready)) <- s.producers) {
          val (sink, nodes) = makePipeline(p, producerLatencies(x))
          newNodes ++= nodes
          // Need to rename `x` so it's the same as `sink`
          renamings += (x -> sink)
          newProducers += sink -> ready
        }
        // Translate accumulators and rename producer variables as needed
        var newAccumulators = Map[Param, Accumulator]()
        for ((x, (init, next)) <- s.accumulators) {
          assert(x.typ.isData)
          val acc = ExprAccumulator(
            init match {
              case _: Undefined => None
              case init         => Some(ExprIntermediate(init))
            },
            ExprIntermediate(next.subPreserveType(renamings))
          )
          newAccumulators += (x -> acc)
        }
        val genSbuild = GenStmBuild(
          data = s.data.subPreserveType(renamings),
          valid = s.valid.subPreserveType(renamings),
          accumulators = newAccumulators,
          producers = newProducers,
          intermediates = ListMap()
        )
        val x = Param("s")(s.typ)
        val finalNode =
          StmBuildNode(x, genSbuild, inputLatency = lat.inputLatency)
        (x, newNodes :+ finalNode)
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
          var subsInsideS = Map[Expr, Expr]()
          var newProducers = Map[Param, Expr]()
          for (eqn <- s.producers) {
            eqn match {
              case (x, ready) if varsToRename.contains(x) =>
                val newX = x.freshCopy
                renamings += (x -> (renamings.getOrElse(x, Set()) + newX))
                subsInsideS += (x -> newX)
                newProducers += newX -> ready
              case eqn =>
                newProducers += eqn
            }
          }
          val newSbuild = GenStmBuild(
            data = s.data.subPreserveType(subsInsideS),
            valid = s.valid.subPreserveType(subsInsideS),
            accumulators = s.accumulators.map({ case (x, acc) =>
              x -> acc.substitute(subsInsideS)
            }),
            // No substitutions to do here, since the `ready` expressions are
            // not allowed to use sdata
            producers = newProducers,
            intermediates = s.intermediates.map({ case (x, i) =>
              x -> i.substitute(subsInsideS)
            })
          )
          val newNode = StmBuildNode(x, newSbuild, latency)
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

  /** Apply a few final transformations on the new representation of sbuild.
    */
  private def cleanUpSbuilds(pipe: FlatPipeline): FlatPipeline = {
    FlatPipeline(
      sbuilds = pipe.sbuilds.map({ case StmBuildNode(out, s, inputLatency) =>
        StmBuildNode(out, makeDataRegisterExplicit(s), inputLatency)
      }),
      lets = pipe.lets,
      inputs = pipe.inputs,
      unusedInputs = pipe.unusedInputs,
      sink = pipe.sink
    )
  }

  private def makeDataRegisterExplicit(s: GenStmBuild): GenStmBuild = {
    val (newData, newAccumulators) = SplitTupleIntoAccumulators(s.data, "data")
    GenStmBuild(
      data = newData,
      valid = s.valid,
      accumulators = s.accumulators ++ newAccumulators,
      producers = s.producers,
      intermediates = s.intermediates
    )
  }

  private def ensureAtLeastOneBuffer(pipe: FlatPipeline): FlatPipeline = {
    if (pipe.inputs.contains(pipe.sink)) {
      val newSink = Param("s")(pipe.sink.typ)
      val nop = GenStmBuild(
        data = StmData(pipe.sink)().tchk(),
        valid = True,
        accumulators = Map(),
        producers = Map(pipe.sink -> True),
        intermediates = ListMap()
      )
      val newNode = StmBuildNode(newSink, nop, Some(0))
      pipe.copy(sbuilds = pipe.sbuilds :+ newNode, sink = newSink)
    } else {
      pipe
    }
  }
}

/** Turns the `data` expression of [[StmBuild]] into a bunch of accumulators
  * (one for each tuple element, if it's a tuple).
  *
  * Splitting up a tuple into multiple arguments makes later passes easier. For
  * example, DSP selection looks for accumulators whose `next` expression is
  * basically a multiplication. This is harder to recognize if the
  * multiplication is buried somewhere in a tuple.
  */
private object SplitTupleIntoAccumulators {
  def apply(e: Expr, prefix: String): (Expr, Map[Param, Accumulator]) = {
    val splitter = new SplitTupleIntoAccumulators(Map())
    val newE = splitter.runAndMutateAccumulators(e, prefix)
    (newE, splitter.accumulators)
  }
}

private class SplitTupleIntoAccumulators(
    var accumulators: Map[Param, Accumulator]
) {

  private def runAndMutateAccumulators(e: Expr, prefix: String): Expr = {
    e match {
      case Tuple(elems @ _*) =>
        val newElems = elems.zipWithIndex
          .map({ case (e, i) =>
            this.runAndMutateAccumulators(e, s"${prefix}_$i")
          })
        Tuple(newElems: _*)().tchk()
      case e =>
        val x = Param(prefix)(e.typ)
        this.accumulators += (x -> ExprAccumulator(None, ExprIntermediate(e)))
        x
    }
  }
}
