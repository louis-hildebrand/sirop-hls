package mhir.sugar

import com.typesafe.scalalogging.Logger
import mhir.ir.{ExprPrinter => EP, _}
import mhir.optimize.StmAccRemovalPass
import mhir.typecheck._

import scala.annotation.elidable

private object SL {
  val logger: Logger = Logger("StreamSyntaxSugar")
}

/** Reset all internal state in the given stream pipeline after the given number
  * of inputs and outputs have been read.
  *
  * @param n
  *   the total number of repetitions of the stream pipeline.
  * @param s
  *   the stream pipeline.
  * @param inputs
  *   the inputs to the pipeline, which will not be reset.
  */
private[sugar] case class StmReset(
    n: Expr,
    s: Expr,
    inputs: Map[Param, Expr]
)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(
      Seq(n, s) ++ inputs.flatMap({ case (x, in) => Seq(x, in) }): _*
    )(typ) {

  private val logger: Logger = Logger(getClass.getName)

  private def inputVars: Set[Param] = this.inputs.keySet

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmReset = {
    newChildren match {
      case Seq(n, s, xs @ _*) if xs.length % 2 == 0 =>
        val inputs = xs
          .grouped(2)
          .map({
            case Seq(x: Param, s) => x -> s
            case _ => throw new BadRebuildError(this, newChildren)
          })
          .toMap
        StmReset(n, s, inputs)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmReset = {
    val n = this.n.tchk(context, constValues).expectUInt()
    val s = this.s.tchk(context, constValues).expectStream()
    val stmTyp = s.typ match {
      case t: TyStm => t
      case t =>
        throw new TypeError(
          s"Stream pipeline in $className has type $t."
            + "Expected a stream."
        )
    }
    val inputs = this.inputs.map({ case (x, s) =>
      if (!x.hasType) {
        throw new TypeError(
          s"Missing type annotation for variable in $className."
        )
      }
      val newS = s.tchk(context, constValues)
      x.typ match {
        case TyStm(elemTyp, _) =>
          newS.expectStreamOf(elemTyp, constValues)
        case _ =>
          newS.expectType(x.typ, constValues)
      }
      x -> newS
    })
    val typ = TyStm(stmTyp.t, SafeProd(n, stmTyp.n)())
    StmReset(n, s, inputs)(typ)
  }

  override def sugarSubAndKeepType(
      subs: Map[Expr, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val rhsFreeVars = subs.toSeq
      .flatMap({ case (_, rhs) => rhs.freeVars })
      .toSet
    val renamings = this.inputVars
      .flatMap({ x =>
        val wouldCapture = rhsFreeVars.contains(x)
        if (wouldCapture) Some(x -> x.freshCopy) else None
      })
      .toMap
    val newSubs =
      subs
        // Substitutions where an accumulator variable appears
        // free on the left-hand side are no longer needed: that
        // variable is bound now.
        .filter({ case (lhs, _) =>
          lhs.freeVars.intersect(this.inputVars).isEmpty
        })
        .++(renamings)
    StmReset(
      this.n.subPreserveType(subs)(c),
      this.s.subPreserveType(newSubs)(c),
      this.inputs.map({ case (x, stm) =>
        // There may be substitutions to do in the type
        val renamedX = renamings.getOrElse(x, x)
        val newX =
          Param(renamedX.prefix, renamedX.id)(renamedX.typ.substitute(subs)(c))
        val newStm = stm.subPreserveType(subs)(c)
        newX -> newStm
      })
    )(this.typ.substitute(subs))
  }

  override def sugarSubAndEraseType(
      subs: Map[Expr, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val rhsFreeVars = subs.toSeq
      .flatMap({ case (_, rhs) => rhs.freeVars })
      .toSet
    val renamings = this.inputVars
      .flatMap({ x =>
        val wouldCapture = rhsFreeVars.contains(x)
        if (wouldCapture) Some(x -> x.freshCopy) else None
      })
      .toMap
    val newSubs =
      subs
        // Substitutions where an accumulator variable appears
        // free on the left-hand side are no longer needed: that
        // variable is bound now.
        .filter({ case (lhs, _) =>
          lhs.freeVars.intersect(this.inputVars).isEmpty
        })
        .++(renamings)
    StmReset(
      this.n.subAndEraseType(subs)(c),
      this.s.subAndEraseType(newSubs)(c),
      this.inputs.map({ case (x, stm) =>
        // There may be substitutions to do in the type
        val renamedX = renamings.getOrElse(x, x)
        val newX =
          Param(renamedX.prefix, renamedX.id)(renamedX.typ.substitute(subs)(c))
        val newStm = stm.subAndEraseType(subs)(c)
        newX -> newStm
      })
    )()
  }

  override def displayOneLine(): String = {
    val nStr = EP.displayOneLine(this.n, Precedence.Max)
    val pipeStr = EP.displayOneLine(this.s, Precedence.Max)
    val inputsStr = this.inputs.toSeq
      .sortBy({ case (x, _) => x.name })
      .map({ case (x, stm) =>
        val stmStr = EP.displayOneLine(stm, Precedence.Max)
        s"(${x.name} : ${x.typ}) = $stmStr"
      })
      .mkString("; ")
    s"reset ($nStr) { $inputsStr } { $pipeStr }"
  }

  override def displayMultiLine(maxWidth: Int): String = {
    val w1 = maxWidth - EP.Indent.length - ";".length
    val nStr = EP.display(
      this.n,
      maxWidth = w1,
      parentPrecedence = Precedence.Max
    )
    val pipeStr =
      EP.display(this.s, maxWidth = w1, parentPrecedence = Precedence.Max)
    val indentedPipeStr = EP.indent(pipeStr)
    val indentedInputsStr = if (this.inputs.isEmpty) {
      ""
    } else {
      val str = this.inputs.toSeq
        .sortBy({ case (x, _) => x.name })
        .map({ case (x, stm) =>
          val stmStr = EP.display(
            stm,
            maxWidth = maxWidth - 2 * EP.Indent.length - ",".length,
            parentPrecedence = Precedence.Max
          )
          s"(${x.name} : ${x.typ}) = $stmStr"
        })
        .map(str => s"$str;")
        .mkString("\n")
      EP.indent(str)
    }
    // Don't use a multi-line string with .stripMargin here because one of
    // the sub-expressions may have a line starting with '|'.
    // Example:
    //   c1 && c2
    //     || c3 && c4
    s"reset ($nStr) {\n$indentedInputsStr\n} {\n$indentedPipeStr\n}"
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    // Assume each field (n, s, inputs) is already lowered
    // This is to avoid repeatedly traversing large expressions, which slows
    // down compilation
    val loweredPipeline = this.lowerEmptyPipeline
      .orElse(this.lowerForNEqualsOne)
      .getOrElse(this.lowerStandard)
      .tchk()
    val ret = loweredPipeline.subPreserveType(this.inputs.toMap[Expr, Expr])
    assertNoNewFreeVars(ret.freeVars) // Sanity check
    ret
  }

  @elidable(elidable.ASSERTION)
  private def assertNoNewFreeVars(freeVars: Set[Param]): Unit = {
    val originalFreeVars = {
      val boundInputVars = this.inputs.map({ case (x, _) => x }).toSet
      val freeVarsInPipeline = s.freeVars -- boundInputVars
      val freeVarsInInputs = this.inputs
        .flatMap({ case (_, stm) => stm.freeVars })
        .toSet
      (this.n.freeVars
        ++ freeVarsInPipeline
        ++ freeVarsInInputs)
    }
    assert(
      freeVars.subsetOf(originalFreeVars),
      s"the set of free variables should be unchanged by $className"
        + s" (expected $originalFreeVars, got $freeVars)"
    )
  }

  private def lowerEmptyPipeline(implicit c: Canonicalizer): Option[Expr] = {
    if (c.sameLen(this.n, C(0)())) {
      logger.trace(s"lowering $className with n = 0: $this")
      val TyStm(t, _) = s.typ
      Some(
        StmBuild(
          0,
          C(1)(),
          Undefined(t),
          Undefined(t),
          True,
          Map(),
          Map()
        )()
          .annotate(NoInputsAfterLastOut)
          .annotateWithName("Empty")
      )
    } else {
      None
    }
  }

  private def lowerForNEqualsOne(implicit c: Canonicalizer): Option[Expr] = {
    if (c.sameLen(n, C(1)())) {
      logger.trace(s"lowering $className with n = 1: $this")
      Some(s)
    } else {
      None
    }
  }

  private def lowerStandard(implicit c: Canonicalizer): Expr = {
    logger.trace(s"lowering $className the standard way: $this")
    val s0 = addCountersAndReset(this.s)
    val s1 = multiplyLengths(s0, this.inputVars)
    val s2 = repeatExternalInputs(s1, this.inputVars)
    s2
  }

  private def addCountersAndReset(s: Expr)(implicit c: Canonicalizer): Expr = {
    s match {
      case x: Param =>
        x
      case s: StmBuild =>
        val ctrByInput = s.producers
          .map({ case (x, (p, _, _)) =>
            val TyStm(_, inLen) = p.typ
            val ctrTyp = inLen match {
              case IntCst(n) => TyAnyInt.tightest(0, n)
              case _         => inLen.typ
            }
            x -> Param("in_ctr")(ctrTyp)
          })
        val withInCtrs = ctrByInput.foldLeft(s)({ case (acc, (x, ctr)) =>
          acc.addInputCounter(x, ctr)
        })
        val outCtr = {
          val ctrTyp = s.typ match {
            case TyStm(_, IntCst(n)) => TyAnyInt.tightest(0, n)
            case _                   => s.n.typ
          }
          Param("out_ctr")(ctrTyp)
        }
        val outputsUntilReset: Expr = s.n
        val withCtrs = {
          val canOmitLessThan = s.annotations
            .intersect(
              Set(
                // Once we reach the expected number of outputs, we reset.
                // Therefore, the condition outCtr < outputsUntilReset is a
                // tautology.
                NoInputsAfterLastOut,
                // The sbuild already takes care of not producing more than the
                // expected number of outputs.
                SelfControlledOutputs
              )
            )
            .nonEmpty
          val s1 = if (canOmitLessThan) {
            withInCtrs
          } else {
            StmBuild(
              withInCtrs.n,
              withInCtrs.delay,
              withInCtrs.initData,
              withInCtrs.nextData,
              withInCtrs.valid && (outCtr < outputsUntilReset).tchk().lower,
              withInCtrs.accumulators,
              withInCtrs.producers
            )(annotations = withInCtrs.annotations)
              .tchk()
              .asInstanceOf[StmBuild]
          }
          s1.addOutputCounter(outCtr)
        }
        val shouldReset = if (s.annotations.contains(NoInputsAfterLastOut)) {
          // No need to count inputs: based on the annotation, once we read the
          // last output, we know we can reset immediately
          val (_, nextOutCtr, _) = withCtrs.accumulators(outCtr)
          (nextOutCtr === outputsUntilReset).tchk().lower
        } else if (s.annotations.contains(NoOutputsAfterLastIn)) {
          // No need to count outputs: based on the annotation, once we read the
          // last inputs, we know we can reset immediately
          val inputsUntilReset: Seq[(Param, Expr)] =
            s.producers
              .collect({
                case (x, (z, _, _)) if ctrByInput.contains(x) =>
                  val TyStm(_, n) = z.typ
                  ctrByInput(x) -> n
              })
              .toSeq
          val shouldReset = inputsUntilReset
            .map({ case (ctr, n) =>
              val (_, nextCtr, _) = withCtrs.accumulators(ctr)
              nextCtr === n
            })
            .reduce[Expr]({ case (x, y) => x && y })
          shouldReset.tchk().lower
        } else {
          // Need both input and output counters, just in case
          val name = s.nameAnnotation.getOrElse("(unknown name)")
          logger.warn(
            s"StmBuild node $name is neither annotated with $NoInputsAfterLastOut nor $NoOutputsAfterLastIn."
              + " Both input and output counters will be added, which may increase resource usage."
          )
          val inputsUntilReset: Seq[(Param, Expr)] =
            s.producers
              .collect({
                case (x, (z, _, _)) if ctrByInput.contains(x) =>
                  val TyStm(_, n) = z.typ
                  ctrByInput(x) -> n
              })
              .toSeq
          val shouldReset = ((outCtr -> outputsUntilReset) +: inputsUntilReset)
            .map({ case (ctr, n) =>
              val (_, nextCtr, _) = withCtrs.accumulators(ctr)
              nextCtr === n
            })
            .reduce[Expr]({ case (x, y) => x && y })
          shouldReset.tchk().lower
        }
        val result = StmBuild(
          withCtrs.n,
          withCtrs.delay,
          withCtrs.initData,
          withCtrs.nextData,
          withCtrs.valid,
          withCtrs.accumulators.map({
            case (x, (z: Undefined, next, delay)) =>
              // No need to reset!
              x -> (z, next, delay)
            case (x, (z, next, delay)) =>
              val newNext = Mux(shouldReset, z, next)()
              x -> (z, newNext, delay)
          }),
          withCtrs.producers.map({ case (x, (s, ready, delay)) =>
            val newStm = addCountersAndReset(s)
            x -> (newStm, ready, delay)
          })
        )(annotations = withCtrs.annotations).tchk().asInstanceOf[StmBuild]
        val simplifiedResult = StmAccRemovalPass.removeUnusedVars(result)
        simplifiedResult
      case LetStm(bufSize, x, in, out) =>
        LetStm(bufSize, x, addCountersAndReset(in), addCountersAndReset(out))()
          .tchk()
      case _ =>
        ???
    }
  }

  private def multiplyLengths(stm: Expr, inputStreams: Set[Param])(implicit
      c: Canonicalizer
  ): Expr = {
    stm match {
      case x: Param if inputStreams.contains(x) =>
        // Streams that are on the input list become longer.
        val TyStm(t, n) = x.typ
        x.rebuild(TyStm(t, SafeProd(this.n, n)()))
      case x: Param =>
        // Streams that are not on the input list will be repeated (which is
        // handled by a separate method).
        x
      case s: StmBuild =>
        StmBuild(
          SafeProd(this.n, s.n)().tchk().lower,
          s.delay,
          s.initData,
          s.nextData,
          s.valid,
          s.accumulators,
          s.producers.map({ case (x, (s, ready, delay)) =>
            x -> (multiplyLengths(s, inputStreams), ready, delay)
          })
        )(annotations = s.annotations).tchk()
      case LetStm(bufSize, x, in, out) =>
        val TyStm(t, n) = x.typ
        LetStm(
          SafeProd(this.n, bufSize)().tchk().lower,
          x.rebuild(TyStm(t, SafeProd(this.n, n)())).asInstanceOf[Param],
          multiplyLengths(in, inputStreams),
          multiplyLengths(out, inputStreams + x)
        )().tchk()
      case _ =>
        ???
    }
  }

  private def repeatExternalInputs(
      stm: Expr,
      inputStreams: Set[Param]
  )(implicit c: Canonicalizer): Expr = {
    stm match {
      case x: Param if inputStreams.contains(x) =>
        // Streams that are on the input list or were bound by a LetStm are
        // fine: we read them in order.
        x
      case x: Param =>
        // Streams that are not on the input list will be read once *per
        // iteration* of this StmReset.
        // Therefore, they must be repeated.
        StmJoin(StmRepeat(x, n)())().tchk().lower
      case LetStm(bufSize, x, in, out) =>
        LetStm(
          bufSize,
          x,
          repeatExternalInputs(in, inputStreams),
          repeatExternalInputs(out, inputStreams + x)
        )()
      case s: StmBuild =>
        s.mapProducers({ case (x, (s, ready, delay)) =>
          x -> (repeatExternalInputs(s, inputStreams), ready, delay)
        })
      case _ =>
        ???
    }
  }
}

case class StmCst(n: Expr, k: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(n, k)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmCst = {
    newChildren match {
      case Seq(n, c) => StmCst(n, c)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmCst = {
    val n = this.n.tchk(context, constValues).expectUInt()
    val k = this.k.tchk(context, constValues)
    this.rebuild(TyStm(k.typ, n), Seq(n, k))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val n = this.n.lower
    val k = this.k.lower
    val out = k.typ match {
      case _: TyStm => StmRepeat(k, n)()
      case _ =>
        StmBuild(n, C(1)(), k, k, True, Map(), Map())()
          .annotate(NoInputsAfterLastOut)
          .annotateWithName(getClass.getSimpleName)
    }
    out.tchk().lower
  }
}

case class StmCount(n: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(n)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmCount = {
    newChildren match {
      case Seq(n) => StmCount(n)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmCount = {
    val newN = n.tchk(context, constValues).expectUInt()
    this.rebuild(TyStm(newN.typ, newN), Seq(newN))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val n = this.n.lower
    StmRange(n, IntCst(0)(n.typ), IntCst(1)(n.typ))().tchk().lower
  }
}

/** An arbitrary counter, a bit like Python's <code>range()</code>.
  *
  * @param n
  *   Length of the stream.
  * @param z
  *   Initial value of the stream.
  * @param delta
  *   Difference between consecutive elements.
  * @return
  *   The stream of length <code>n</code> with elements <code>[z, z + delta, z +
  *   2 * delta, ...]</code>.
  */
case class StmRange(n: Expr, z: Expr, delta: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(n, z, delta)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmRange = {
    newChildren match {
      case Seq(n, z, delta) => StmRange(n, z, delta)(typ)
      case _                => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmRange = {
    val newN = n.tchk(context, constValues).expectUInt()
    val newZ = z.tchk(context, constValues).expectAnyInt()
    val newDelta = delta
      .tchk(context, constValues)
      .expectType(newZ.typ, constValues)
    this.rebuild(TyStm(newZ.typ, newN), Seq(newN, newZ, newDelta))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    val n = this.n.lower
    val z = this.z.lower
    val delta = this.delta.lower
    val a = Param("a")(z.typ)
    StmBuild(
      n,
      C(1)(),
      Undefined(Missing),
      a,
      True,
      Map[Param, (Expr, Expr, Expr)](
        a -> (z, (a + delta).tchk().lower, C(1)())
      ),
      Map()
    )().annotate(NoInputsAfterLastOut).annotateWithName(this.className).tchk()
  }
}

/** A counter that produces a stream of vectors.
  *
  * This is equivalent to, but possibly more resource-efficient than, the
  * following:
  * {{{
  *   StmRange(n * m, z, delta) |> StmSplit(m) |> StmMap(Stm2Vec)
  * }}}
  *
  * @param n
  *   the length of the stream.
  * @param m
  *   the length of each vector.
  * @param z
  *   the initial value.
  * @param delta
  *   the step size.
  * @note
  *   the stream will have type `Stm[Vec[T, m], n]`, where `T` is the type of
  *   `z` and `delta`.
  */
case class StmVecRange(n: Expr, m: Expr, z: Expr, delta: Expr)(
    typ: Type = Missing
) extends ResolvedSyntaxSugar(n, m, z, delta)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmVecRange = {
    newChildren match {
      case Seq(n, m, z, delta) => StmVecRange(n, m, z, delta)(typ)
      case _                   => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmVecRange = {
    val n = this.n.tchk(context, constValues).expectUInt()
    val m = this.m.tchk(context, constValues).expectUInt()
    val z = this.z.tchk(context, constValues).expectAnyInt()
    val delta = this.delta
      .tchk(context, constValues)
      .expectType(z.typ, constValues)
    this.rebuild(TyStm(TyVec(z.typ, m), n), Seq(n, m, z, delta))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val n = this.n.lower
    val m = this.m.lower
    val z = this.z.lower
    val delta = this.delta.lower
    val v = Param("v")(TyVec(z.typ, m))
    StmBuild(
      n,
      C(1)(),
      Undefined(v.typ),
      v,
      True,
      Map[Param, (Expr, Expr, Expr)](
        v -> (
          VecBuild(m, m.typ ::+ (i => z + i * delta))().tchk().lower,
          VecBuild(m, m.typ ::+ (i => VecAccess(v, i)() + m * delta))()
            .tchk()
            .lower,
          C(1)()
        )
      ),
      Map()
    )().annotate(NoInputsAfterLastOut).annotateWithName(this.className).tchk()
  }
}

case class StmCount2D(n: Expr, m: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(n, m)(typ) /* Stm<Stm<(Int, Int); m>; n> */ {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmCount2D = {
    newChildren match {
      case Seq(n, m) => StmCount2D(n, m)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmCount2D = {
    val n = this.n.tchk(context, constValues).expectUInt()
    val m = this.m.tchk(context, constValues).expectUInt()
    this.rebuild(TyStm(TyStm((n.typ, m.typ), m), n), Seq(n, m))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val n = this.n.lower
    val m = this.m.lower
    val i = Param("i")(n.typ)
    val j = Param("j")(m.typ)
    StmBuild(
      SafeProd(n, m)(),
      C(1)(),
      Undefined(Missing),
      Tuple(i, j)(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        i -> (C(0)(n.typ), Mux(j === m - 1, i + 1, i)(), C(1)()),
        j -> (C(0)(m.typ), Mux(j === m - 1, C(0)(m.typ), j + 1)(), C(1)())
      ),
      Map()
    )()
      .annotate(NoInputsAfterLastOut)
      .annotateWithName(this.className)
      .tchk()
      .lower
  }
}

case class StmMap(s: Expr, f: Expr)(typ: Type = Missing)
    extends SyntaxSugar(s, f)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, f) => StmMap(s, f)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    if (mhir.ir.globalOptions.handshake) {
      mhir.sugar.handshake.StmMap(this.s, this.f)().tchk(context, constValues)
    } else {
      mhir.sugar.nohandshake
        .StmMap(this.s, this.f, Undefined(Missing))()
        .tchk(context, constValues)
    }
  }
}

case class StmAccess(
    stm: Expr /* Stm<A; n> */,
    k: Expr /* Int */
)(typ: Type = Missing) /* Stm<A; 1> */
    extends ResolvedSyntaxSugar(stm, k)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmAccess = {
    newChildren match {
      case Seq(s, k) => StmAccess(s, k)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmAccess = {
    val s = this.stm.tchk(context, constValues).expectStream()
    val t = s.typ.asInstanceOf[TyStm].t
    val k = this.k.tchk(context, constValues).expectUInt()
    this.rebuild(TyStm(t, 1), Seq(s, k))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val stm = this.stm.lower
    val k = this.k.lower
    val TyStm(_, numRows) = this.stm.typ
    val perRow = this.stm.typ.asInstanceOf[TyStm].t.lower match {
      case TyStm(_, n) => n
      case _           => IntCst(1)(U32)
    }
    val TyStm(elemTyp, _) = stm.typ
    val s = Param("s")(TyStm(elemTyp, -1)) // input stream
    val i = { // index of current row
      val typ = numRows match {
        case IntCst(n) => TyAnyInt.tightest(0, n)
        case e         => e.typ
      }
      Param("i")(typ)
    } // index of current row
    val j = { // index within row
      val typ = perRow match {
        case IntCst(n) => TyAnyInt.tightest(0, n)
        case e         => e.typ
      }
      Param("j")(typ)
    }
    val annotations: Set[StmBuildAnnotation] = {
      val basicAnnotations = Set(NoOutputsAfterLastIn, SelfControlledOutputs)
      if (c.sameLen(SafeSum(k, 1)(), numRows)) {
        basicAnnotations + NoInputsAfterLastOut
      } else {
        basicAnnotations
      }
    }
    StmBuild(
      perRow,
      Tuple()(),
      Undefined(elemTyp),
      StmData(s)(),
      (i === k).tchk().lower,
      Map[Param, (Expr, Expr, Expr)](
        i -> (
          C(0)(i.typ),
          Mux(j + 1 === perRow, i + 1, i)().tchk().lower,
          Tuple()()
        ),
        j -> (
          C(0)(j.typ),
          Mux(j + 1 === perRow, C(0)(j.typ), j + 1)().tchk().lower,
          Tuple()()
        )
      ),
      Map[Param, (Expr, Expr, Expr)](
        s -> (stm, True, Tuple()())
      )
    )(annotations = annotations).annotateWithName(this.className).tchk()
  }
}

case class StmCascade(s: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmCascade = {
    newChildren match {
      case Seq(s) => StmCascade(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmCascade = {
    val s = this.s.tchk(context, constValues)
    val (elemTyp, n, m) = s.typ match {
      case TyStm(TyVec(TyData(t), m), n) => (t, n, m)
      case typ =>
        throw new TypeError(
          s"Input to $className has type $typ."
            + s" Expected a stream of vectors."
        )
    }
    // TODO: what if n == m == 0? Then the output stream length will be undefined...
    val outLen = ToUnsigned(SafeSum(n, m, C(-1)())())().tchk()
    this.rebuild(TyStm(TyVec(elemTyp, m), outLen), Seq(s))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val s = this.s.lower
    val TyStm(TyVec(TyData(elemTyp), m), _) = s.typ
    val TyStm(_, outLen) = this.typ
    m match {
      case IntCst(mLong) if mLong > 0 =>
        val m = mLong.toInt
        val sExtended = Call(
          Param("StmExtendBy", -1)(Missing),
          Seq(),
          Seq(s, C(m - 1)())
        )().tchk().lower
        val p = Param("p")(TyStm(TyVec(elemTyp, m), -1))
        val pipeVars = (0 until m - 1).map(i =>
          Param(s"pipe${i + 1}")(TyVec(elemTyp, C(i + 1)()))
        )
        StmBuild(
          outLen,
          C(1)(),
          Undefined(TyVec(elemTyp, m)),
          VecLiteral((0 until m).map({
            case 0 => VecAccess(StmData(p)(), 0)()
            case i => VecAccess(pipeVars(i - 1), 0)()
          }): _*)(),
          True,
          pipeVars.zipWithIndex
            .map({ case (x, i) =>
              x -> (
                Undefined(x.typ),
                VecShiftLeft(x, VecAccess(StmData(p)(), i + 1)())()
                  .tchk()
                  .lower,
                Tuple()()
              )
            })
            .toMap,
          Map[Param, (Expr, Expr, Expr)](
            p -> (sExtended, True, C(0)())
          )
        )().tchk()
      case IntCst(0) =>
        ???
      case e =>
        throw new TypeError(
          s"$className is not applicable when the vectors have length $e."
            + s" Only non-negative, constant lengths are supported."
        )
    }
  }
}

// TODO: Generalize to include pre-adder?
// TODO: Generalize to allow 27-bit systolic mode?
case class StmMapDotCascaded(s1: Expr, s2: Expr, delay: Expr)(
    typ: Type = Missing
) extends ResolvedSyntaxSugar(s1, s2, delay)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmMapDotCascaded = {
    newChildren match {
      case Seq(s1, s2, d) => StmMapDotCascaded(s1, s2, d)(typ)
      case _              => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmMapDotCascaded = {
    val s1 = this.s1.tchk(context, constValues)
    val (elemTyp1, n, m) = s1.typ match {
      case TyStm(TyVec(t: TyAnyInt, m), n) =>
        // TODO: Enforce constraint on bitwidth (18 bits?)
        (t, n, m)
      case t =>
        throw new TypeError(
          s"First stream in $className has type $t."
            + s" Expected a stream of vectors."
        )
    }
    val s2 = this.s2.tchk(context, constValues)
    val elemTyp2 = s2.typ match {
      case TyStm(TyVec(t: TyAnyInt, m2), n2) =>
        // TODO: Enforce constraint on bitwidth (18 bits?)
        if (!c.sameLen(n, n2, constValues)) {
          throw new TypeError(
            s"Second stream in $className has length $n2."
              + s" Expected a stream of length $n."
          )
        }
        if (!c.sameLen(m, m2, constValues)) {
          throw new TypeError(
            s"Second stream in $className contains vectors of length $m2."
              + s" Expected vectors of length $m."
          )
        }
        t
      case t =>
        throw new TypeError(
          s"Second stream in $className has type $t." +
            s" Expected a stream of vectors."
        )
    }
    val delay = this.delay.tchk(context, constValues).expectUInt()
    val outElemTyp = (elemTyp1, elemTyp2) match {
      case (_: TyUInt, _: TyUInt) => U44
      case _                      => I44
      // TODO: What if the operands don't fit in u44/i44?
      //       Or what if we're not targeting an Agilex 7 device and therefore 44 bits is not applicable?
    }
    if (!ReshapeData.canReshape(elemTyp1, outElemTyp, constValues)) {
      throw new TypeError(
        s"Elements of type $elemTyp1 in first stream cannot be reshaped to $outElemTyp."
      )
    }
    if (!ReshapeData.canReshape(elemTyp2, outElemTyp, constValues)) {
      throw new TypeError(
        s"Elements of type $elemTyp2 in second stream cannot be reshaped to $outElemTyp."
      )
    }
    // Note that the output length will be greater than the input length if m == 0.
    // I don't think that's a big deal though, and it makes the length a bit simpler.
    val outLen =
      ToUnsigned(SafeSum(n, SafeProd(C(-1)(), m)(), C(1)())())().tchk()
    this.rebuild(TyStm(outElemTyp, outLen), Seq(s1, s2, delay))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val TyStm(TyVec(t1: TyAnyInt, m), _) = this.s1.typ
    m match {
      case IntCst(mLong) if mLong > 0 =>
        val m = mLong.toInt
        val s1 = this.s1.lower
        val s2 = this.s2.lower
        val delay = this.delay.lower
        val TyStm(int, outLen) = this.typ
        val TyStm(TyVec(t2, _), _) = this.s2.typ
        val p1 = Param("p1")(TyStm(TyVec(t1, m), -1))
        val p2 = Param("p2")(TyStm(TyVec(t2, m), -1))
        val stageVars = (0 until m).map(i => Param(s"stage$i")(int))
        val pipe1Vars =
          (0 until m).map(i => Param(s"stage${i}_x_pipe")(TyVec(t1, delay)))
        val pipe2Vars =
          (0 until m).map(i => Param(s"stage${i}_y_pipe")(TyVec(t2, delay)))
        // IMPORTANT: stages must be an ordered sequence (not a Map!), since I
        // extract the last element later on
        val stages: Seq[(Param, (Expr, Expr, Expr))] = stageVars.zipWithIndex
          .map({ case (x, i) =>
            val factorX = Mux(
              delay === 0,
              VecAccess(StmData(p1)(), i)(),
              VecAccess(pipe1Vars(i), C(0)())()
            )().tchk()
            val factorY = Mux(
              delay === 0,
              VecAccess(StmData(p2)(), i)(),
              VecAccess(pipe2Vars(i), C(0)())()
            )().tchk()
            val mul = Prod(
              ReshapeData(factorX, int)(),
              ReshapeData(factorY, int)()
            )().tchk().lower
            val next = if (i == 0) mul else Sum(stageVars(i - 1), mul)()
            x -> (Undefined(int), next, Tuple()())
          })
        val pipe1 = pipe1Vars.zipWithIndex
          .map({ case (x, i) =>
            val next =
              VecShiftLeft(x, VecAccess(StmData(p1)(), C(i)())())().tchk().lower
            x -> (Undefined(x.typ), next, Tuple()())
          })
          .toMap
        val pipe2 = pipe2Vars.zipWithIndex
          .map({ case (x, i) =>
            val next =
              VecShiftLeft(x, VecAccess(StmData(p2)(), C(i)())())().tchk().lower
            x -> (Undefined(x.typ), next, Tuple()())
          })
          .toMap
        val (_, (_, lastStage, _)) = stages.last
        val totDelay =
          SafeSum(C(math.max(0, stages.length - 1))(), delay)().tchk().lower
        // Extend the input streams so I don't get "attempt to read from an
        // empty stream" errors
        val s1Extended = Call(
          Param("StmExtendBy", -1)(Missing),
          Seq(),
          Seq(s1, delay)
        )().tchk()
        val s2Extended = Call(
          Param("StmExtendBy", -1)(Missing),
          Seq(),
          Seq(s2, delay)
        )().tchk()
        Call(
          Param("StmDrop", -1)(Missing),
          Seq(),
          Seq(
            StmBuild(
              SafeSum(outLen, totDelay)(),
              C(1)(),
              Undefined(int),
              lastStage,
              True,
              pipe1 ++ pipe2 ++ stages.init.toMap,
              Map[Param, (Expr, Expr, Expr)](
                p1 -> (s1Extended, True, C(0)()),
                p2 -> (s2Extended, True, C(0)())
              )
            )(),
            totDelay
          )
        )().tchk().lower
      case IntCst(0) =>
        val TyStm(int, n) = this.typ
        StmBuild(n, C(1)(), Undefined(int), C(0)(int), True, Map(), Map())()
          .tchk()
      case e =>
        throw new TypeError(
          s"$className is not applicable when the vectors have length $e."
            + s" Only non-negative, constant lengths are supported."
        )
    }
  }
}

case class StmMapDot(s1: Expr, s2: Expr, delay: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s1, s2, delay)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmMapDot = {
    newChildren match {
      case Seq(s1, s2, delay) => StmMapDot(s1, s2, delay)(typ)
      case _                  => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmMapDot = {
    val s1 = this.s1.tchk(context, constValues)
    val (elemTyp1, n, m) = s1.typ match {
      case TyStm(TyVec(t: TyAnyInt, m), n) =>
        // TODO: Enforce constraint on bitwidth (18 bits?)
        (t, n, m)
      case t =>
        throw new TypeError(
          s"First stream in $className has type $t."
            + s" Expected a stream of vectors."
        )
    }
    val s2 = this.s2.tchk(context, constValues)
    val elemTyp2 = s2.typ match {
      case TyStm(TyVec(t: TyAnyInt, m2), n2) =>
        // TODO: Enforce constraint on bitwidth (18 bits?)
        if (!c.sameLen(n, n2, constValues)) {
          throw new TypeError(
            s"Second stream in $className has length $n2."
              + s" Expected a stream of length $n."
          )
        }
        if (!c.sameLen(m, m2, constValues)) {
          throw new TypeError(
            s"Second stream in $className contains vectors of length $m2."
              + s" Expected vectors of length $m."
          )
        }
        t
      case t =>
        throw new TypeError(
          s"Second stream in $className has type $t." +
            s" Expected a stream of vectors."
        )
    }
    val delay = this.delay.tchk(context, constValues).expectUInt()
    val outElemTyp = (elemTyp1, elemTyp2) match {
      case (_: TyUInt, _: TyUInt) => U44
      case _                      => I44
      // TODO: What if the operands don't fit in u44/i44?
      //       Or what if we're not targeting an Agilex 7 device and therefore 44 bits is not applicable?
    }
    if (!ReshapeData.canReshape(elemTyp1, outElemTyp, constValues)) {
      throw new TypeError(
        s"Elements of type $elemTyp1 in first stream cannot be reshaped to $outElemTyp."
      )
    }
    if (!ReshapeData.canReshape(elemTyp2, outElemTyp, constValues)) {
      throw new TypeError(
        s"Elements of type $elemTyp2 in second stream cannot be reshaped to $outElemTyp."
      )
    }
    this.rebuild(TyStm(outElemTyp, n), Seq(s1, s2, delay))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    StmMapDotCascaded(
      StmCascade(this.s1)(),
      StmCascade(this.s2)(),
      this.delay
    )()
      .tchk()
      .lower
  }
}

case class StmFold(s: Expr, z: Expr, f: Expr)(typ: Type = Missing)
    extends SyntaxSugar(s, z, f)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmFold = {
    newChildren match {
      case Seq(s, z, f) => StmFold(s, z, f)(typ)
      case _            => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    if (mhir.ir.globalOptions.handshake) {
      mhir.sugar.handshake
        .StmFold(this.s, this.z, this.f)(this.typ)
        .tchk(context, constValues)
    } else {
      mhir.sugar.nohandshake
        .StmFold(this.s, this.z, this.f)(this.typ)
        .tchk(context, constValues)
    }
  }
}

case class StmAll(s: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmAll = {
    newChildren match {
      case Seq(s) => StmAll(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmAll = {
    val s = this.s.tchk(context, constValues)
    s.typ match {
      case TyStm(TyBool, _) => ()
      case t =>
        throw new TypeError(
          s"Input to $className has type $t."
            + s" Expected a stream of booleans."
        )
    }
    this.rebuild(TyStm(TyBool, 1), Seq(s))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    StmFold(
      s,
      True,
      (TyBool, TyBool) ::+ (x => And(x.__0, x.__1)())
    )().tchk().lower
  }
}

case class StmAny(s: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmAny = {
    newChildren match {
      case Seq(s) => StmAny(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmAny = {
    val s = this.s.tchk(context, constValues)
    s.typ match {
      case TyStm(TyBool, _) => ()
      case t =>
        throw new TypeError(
          s"Input to $className has type $t."
            + s" Expected a stream of booleans."
        )
    }
    this.rebuild(TyStm(TyBool, 1), Seq(s))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    StmFold(
      s,
      False,
      (TyBool, TyBool) ::+ (x => Or(x.__0, x.__1)())
    )().tchk().lower
  }
}

case class StmSum(s: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmSum = {
    newChildren match {
      case Seq(s) => StmSum(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmSum = {
    val s = this.s.tchk(context, constValues)
    val typ = s.typ match {
      case TyStm(t: TyAnyInt, _) => t
      case t =>
        throw new TypeError(
          s"Input to $className has type $t."
            + s" Expected a stream of integers."
        )
    }
    this.rebuild(TyStm(typ, 1), Seq(s))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val TyStm(typ, _) = this.s.typ
    StmFold(
      s,
      C(0)(typ),
      (typ, typ) ::+ (x => WrappingSum(x.__0, x.__1)())
    )().tchk().lower
  }
}

case class StmConcat(stm1: Expr, stm2: Expr)
    extends SyntaxSugar(stm1, stm2)(Missing) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(typ == Missing, s"cannot rebuild $className with type $typ")
    newChildren match {
      case Seq(s1, s2) => StmConcat(s1, s2)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    if (mhir.ir.globalOptions.handshake) {
      mhir.sugar.handshake
        .StmConcat(this.stm1, this.stm2)()
        .tchk(context, constValues)
    } else {
      mhir.sugar.nohandshake
        .StmConcat(this.stm1, this.stm2, Undefined(Missing))()
        .tchk(context, constValues)
    }
  }
}

case class StmPrepend(stm: Expr /* Stm<A; n> */, e: Expr /* A */ )(
    typ: Type = Missing
) /* Stm<A; n+1> */
    extends ResolvedSyntaxSugar(stm, e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmPrepend = {
    newChildren match {
      case Seq(s, e) => StmPrepend(s, e)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmPrepend = {
    val newS = stm.tchk(context, constValues)
    val (t, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmPrepend has type $t.")
    }
    val newE = e.tchk(context, constValues).expectType(t, constValues)
    this.rebuild(TyStm(t, n + 1), Seq(newS, newE))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    StmConcat(StmCst(1, e)(), stm).tchk().lower.asInstanceOf[StmBuild]
  }
}

case class StmAppend(stm: Expr /* Stm<A; n> */, e: Expr /* A */ )(
    typ: Type = Missing
) /* Stm<A; n+1> */
    extends ResolvedSyntaxSugar(stm, e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmAppend = {
    newChildren match {
      case Seq(s, e) => StmAppend(s, e)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmAppend = {
    val newS = stm.tchk(context, constValues)
    val (t, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmAppend has type $t.")
    }
    val newE = e.tchk(context, constValues).expectType(t, constValues)
    this.rebuild(TyStm(t, n + 1), Seq(newS, newE))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    StmConcat(stm, StmCst(1, e)()).tchk().lower
  }
}

/** Take elements from the beginning of a stream.
  *
  * NOTE: k must be such that 0 &le; k &le; n.
  *
  * @param stm
  *   The input stream.
  * @param k
  *   The number of elements to extract.
  */
case class StmTake(
    stm: Expr /* Stm<A; n> */,
    k: Expr /* Int */
)(typ: Type = Missing) /* Stm<A; k> */
    extends ResolvedSyntaxSugar(stm, k)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmTake = {
    newChildren match {
      case Seq(stm, k) => StmTake(stm, k)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmTake = {
    val k = this.k.tchk(context, constValues).expectUInt()
    val s = this.stm.tchk(context, constValues)
    s.typ match {
      case TyStm(t, _) => this.rebuild(TyStm(t, k), Seq(s, k))
      case t           => throw new TypeError(s"Stream in StmTake has type $t.")
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val stm = this.stm.lower
    val k = this.k.lower
    val perRow = this.stm.typ.asInstanceOf[TyStm].t.lower match {
      case TyStm(_, n) => n
      case _           => C(1)().tchk()
    }
    val TyStm(elemTyp, _) = stm.typ
    val s = Param("s")(TyStm(elemTyp, -1)) // input stream
    StmBuild(
      SafeProd(k, perRow)().tchk().lower,
      C(1)(),
      Undefined(elemTyp),
      StmData(s)(),
      True,
      Map(),
      Map[Param, (Expr, Expr, Expr)](
        s -> (stm, True, C(0)())
      )
    )().annotateWithName(this.className).tchk()
  }
}

/** Discard the last element of the given stream and insert an undefined value
  * at the beginning.
  *
  * TODO: Replace this with [[StmDelay]]?
  *
  * @param stm
  *   the stream to shift.
  */
case class StmShiftRightGarbage(stm: Expr, shiftAmount: IntCst)(
    typ: Type = Missing
) extends ResolvedSyntaxSugar(stm, shiftAmount)(typ) {
  override def rebuild(
      typ: Type,
      newChildren: Seq[Expr]
  ): StmShiftRightGarbage = {
    newChildren match {
      case Seq(s, m: IntCst) => StmShiftRightGarbage(s, m)(typ)
      case _                 => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmShiftRightGarbage = {
    val newS = stm.tchk(context, constValues)
    val (t, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in $className has type $t.")
    }
    if (!t.isData) {
      throw new TypeError(
        s"Invalid element type $t in input stream of of $className."
      )
    }
    val newShiftAmount =
      this.shiftAmount
        .tchk(context, constValues)
        .expectUInt()
        .asInstanceOf[IntCst]
    if (newShiftAmount.i <= 0) {
      throw new TypeError(
        s"Shift amount in $className must be strictly positive (got $newShiftAmount)."
      )
    }
    this.rebuild(TyStm(t, n), Seq(newS, newShiftAmount))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    SL.logger.trace(s"lowering $className: $this")
    requireType()
    val stm = this.stm.lower
    val shiftAmount = this.shiftAmount.lower.asInstanceOf[IntCst]
    val TyStm(elemTyp, n) = this.stm.typ
    val s = Param("s")(TyStm(elemTyp, -1))
    val buf = Param("buf")(TyVec(elemTyp, shiftAmount))
    StmBuild(
      n,
      C(1)(),
      Undefined(elemTyp),
      VecAccess(buf, C(shiftAmount.i - 1)())(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        buf -> (
          Undefined(TyVec(elemTyp, shiftAmount)),
          VecShiftRight(buf, StmData(s)())().tchk().lower,
          Tuple()()
        )
      ),
      Map[Param, (Expr, Expr, Expr)](
        s -> (stm, True, C(0)())
      )
    )().annotateWithName(this.className).tchk()
  }
}

/** Shift right on a stream of vectors, similar to
  * [[https://dl.acm.org/doi/10.1145/3385412.3385983 Aetherling]]'s `shift_ts`.
  *
  * Note that [[StmVecShiftRightGarbage]] is <i>NOT</i> equivalent to
  * [[StmShiftRightGarbage]]. [[StmVecShiftRightGarbage]] is equivalent to (but
  * more space-efficient than) flattening the sequence, then shifting, and then
  * re-nesting.
  *
  * @example
  *   consider the following stream (of type `Stm[Vec[Int, 2], 2]`):
  *
  * `{{ [0, 1], [2, 3] }}`.
  *
  * Calling [[StmShiftRightGarbage]] on this stream with a shift amount of 1
  * would yield the following stream:
  *
  * `{{ [u, u], [0, 1] }}`.
  *
  * Calling [[StmVecShiftRightGarbage]] with a shift amount of 1 would yield the
  * following stream:
  *
  * `{{ [u, 0], [1, 2] }}`.
  *
  * @param stm
  *   the stream to shift.
  * @param shiftAmount
  *   the amount to shift by.
  */
case class StmVecShiftRightGarbage(stm: Expr, shiftAmount: IntCst)(
    typ: Type = Missing
) extends ResolvedSyntaxSugar(stm, shiftAmount)(typ) {
  override def rebuild(
      typ: Type,
      newChildren: Seq[Expr]
  ): StmVecShiftRightGarbage = {
    newChildren match {
      case Seq(s, m: IntCst) => StmVecShiftRightGarbage(s, m)(typ)
      case _                 => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmVecShiftRightGarbage = {
    val stm = this.stm.tchk(context, constValues)
    stm.typ match {
      case TyStm(_: TyVec, _) => ()
      case t =>
        throw new TypeError(
          s"Stream in $className has type $t."
            + " Expected a stream of vectors."
        )
    }
    val shiftAmount =
      this.shiftAmount
        .tchk(context, constValues)
        .expectUInt()
        .asInstanceOf[IntCst]
    if (shiftAmount.i <= 0) {
      throw new TypeError(
        s"Shift amount in $className must be strictly positive (got $shiftAmount)."
      )
    }
    this.rebuild(stm.typ, Seq(stm, shiftAmount))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    SL.logger.trace(s"lowering $className: $this")
    requireType()
    val stm = this.stm.lower
    val shiftAmount = this.shiftAmount.lower.asInstanceOf[IntCst]
    stm.typ match {
      case TyStm(TyVec(t, IntCst(m)), n) if m > 0 =>
        val buf = Param("buf")(TyVec(t, shiftAmount))
        val s = Param("s")(TyStm(TyVec(t, C(m)()), -1))
        val data = if (shiftAmount.i >= m) {
          // All output data comes from the buffer
          VecPrefix(buf, C(m)())().tchk()
        } else {
          // Some output data comes directly from the input stream
          VecConcat(buf, VecPrefix(StmData(s)(), C(m - shiftAmount.i)())())()
            .tchk()
        }
        val bufNext = if (shiftAmount.i >= m) {
          // All input data goes into the buffer
          VecConcat(VecSuffix(buf, C(shiftAmount.i - m)())(), StmData(s)())()
            .tchk()
        } else {
          // Some input data doesn't go into the buffer
          VecSuffix(StmData(s)(), shiftAmount)().tchk()
        }
        StmBuild(
          n,
          C(1)(),
          Undefined(data.typ),
          data.lower,
          True,
          Map[Param, (Expr, Expr, Expr)](
            buf -> (
              Undefined(TyVec(t, shiftAmount)),
              bufNext.tchk().lower,
              Tuple()()
            )
          ),
          Map[Param, (Expr, Expr, Expr)](
            s -> (stm, True, C(0)())
          )
        )().annotateWithName(this.className).tchk()
      case t =>
        throw new TypeError(
          s"Stream in $className has type $t."
            + " Expected a stream of non-empty, fixed-size vectors."
        )
    }
  }
}

case class StmDelay(stm: Expr, delay: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(stm, delay)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmDelay = {
    newChildren match {
      case Seq(s, d) => StmDelay(s, d)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmDelay = {
    val stm = this.stm.tchk(context, constValues)
    stm.typ match {
      case TyStm(TyData(_), _) => ()
      case typ =>
        throw new TypeError(
          s"Input to $className has type $typ."
            + s" Expected a nno-nested stream."
        )
    }
    val delay = this.delay.tchk(context, constValues).expectUInt()
    this.rebuild(stm.typ, Seq(stm, delay))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val stm = this.stm.lower
    val delay = this.delay.lower
    val TyStm(elemTyp, n) = stm.typ
    val p = Param("p")(TyStm(elemTyp, -1))
    val buf = Param("buf")(TyVec(elemTyp, delay))
    StmBuild(
      n,
      SafeSum(delay, 1)().tchk().lower,
      // TODO: Add optional parameter for initial value
      Undefined(elemTyp),
      Mux(
        delay === C(0)(),
        StmData(p)(),
        VecAccess(buf, C(0)())()
      )().tchk().lower,
      True,
      Map[Param, (Expr, Expr, Expr)](
        buf -> (
          Undefined(buf.typ),
          VecShiftLeft(buf, StmData(p)())().tchk().lower,
          Tuple()()
        )
      ),
      Map[Param, (Expr, Expr, Expr)](
        p -> (stm, True, 0)
      )
    )().tchk()
  }
}

case class StmZip(
    a: Expr,
    b: Expr,
    head: Expr = Undefined(Missing)
)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(a, b, head)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmZip = {
    newChildren match {
      case Seq(a, b, head) => StmZip(a, b, head)(typ)
      case _               => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmZip = {
    val newA = a.tchk(context, constValues)
    val (t1, n1) = newA.typ match {
      case TyStm(TyData(t), n) => (t, n)
      case t =>
        throw new TypeError(
          s"First stream in StmZip has type $t. Expected a non-nested stream."
        )
    }
    val newB = b.tchk(context, constValues)
    val (t2, n2) = newB.typ match {
      case TyStm(TyData(t), n) => (t, n)
      case t =>
        throw new TypeError(
          s"Second stream in StmZip has type $t. Expected a non-nested stream."
        )
    }
    if (!c.sameLen(n1, n2, constValues)) {
      throw new TypeError(
        s"lengths of inputs to $className differ: $n1 and $n2."
      )
    }
    val newHead = this.head match {
      case Undefined(Missing) => Undefined(TyTuple(t1, t2))
      case head =>
        head.tchk(context, constValues).expectType(TyTuple(t1, t2), constValues)
    }
    this.rebuild(TyStm(TyTuple(t1, t2), n1), Seq(newA, newB, newHead))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val a = this.a.lower
    val b = this.b.lower
    val head = this.head.lower
    val TyStm(aElemTyp, n) = a.typ
    val TyStm(bElemTyp, _) = b.typ
    val p1 = Param("p1")(TyStm(aElemTyp, -1))
    val p2 = Param("p2")(TyStm(bElemTyp, -1))
    StmBuild(
      n,
      C(1)(),
      head,
      Tuple(StmData(p1)(), StmData(p2)())(),
      True,
      Map(),
      Map(
        p1 -> (a, True, C(0)()),
        p2 -> (b, True, C(0)())
      )
    )()
      .annotateWithName("StmZip")
      .annotate(NoInputsAfterLastOut)
      .annotate(NoOutputsAfterLastIn)
      .tchk()
  }
}

/** Make `m` copies of a stream by reading the stream into a vector and then
  * repeatedly reading from the vector.
  *
  * @note
  *   the stream must be non-empty.
  *
  * @param stm
  *   the stream to repeat.
  * @param m
  *   the number of times to repeat the stream.
  */
case class StmRepeat(
    stm: Expr /* Stm<A; n> */,
    m: Expr /* Int */
)(typ: Type = Missing) /* Stm<Stm<A; n>; m> */
    extends ResolvedSyntaxSugar(stm, m)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmRepeat = {
    newChildren match {
      case Seq(s, m) => StmRepeat(s, m)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmRepeat = {
    val newM = m.tchk(context, constValues).expectUInt()
    val newS = stm.tchk(context, constValues)
    newS.typ match {
      case TyStm(t, n) =>
        this.rebuild(TyStm(TyStm(t, n), m), Seq(newS, newM))
      case t => throw new TypeError(s"Stream in StmRepeat has type $t.")
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val stm = this.stm.lower
    val m = this.m.lower
    val TyStm(elemTyp, n) = stm.typ
    val s = Param("s")(TyStm(elemTyp, -1))
    val v = Param("v")(TyVec(elemTyp, n))
    val tTyp = n match {
      case IntCst(n) => TyAnyInt.tightest(0, math.max(1, n - 1))
      case _         => n.typ
    }
    val t = Param("t")(tTyp)
    val filling = Param("filling")(TyBool)
    StmBuild(
      SafeProd(n, m)().tchk().lower,
      1,
      Undefined(elemTyp),
      Mux(filling, StmData(s)(), VecAccess(v, t)())(),
      True,
      Map[Param, (Expr, Expr, Expr)](
        v -> (
          Undefined(TyVec(elemTyp, n)),
          // Update the vector in such a way that the synthesis tool can turn
          // it into a BRAM (not a massive shift register)
          VecBuild(
            n,
            U32 ::+ (i =>
              Mux(filling && (i === t), StmData(s)(), VecAccess(v, i)())()
                .tchk()
                .lower
            )
          )(),
          1
        ),
        t -> (
          C(0)(t.typ),
          Mux(
            // Assume n >= 1
            t === ToUnsigned(C(-1)() + n)(),
            C(0)(t.typ),
            t + 1
          )().tchk().lower,
          1
        ),
        filling -> (
          True,
          (filling && (t < ToUnsigned(C(-1)() + n)())).tchk().lower,
          1
        )
      ),
      Map[Param, (Expr, Expr, Expr)](
        s -> (stm, filling, 0)
      )
    )().annotate(NoInputsAfterLastOut).annotateWithName(this.className).tchk()
  }
}

case class StmSplit(stm: Expr /* Stm<A; n> */, m: Expr /* Int */ )(
    typ: Type = Missing
) /* Stm<Stm<A; m>; n/m> */
    extends ResolvedSyntaxSugar(stm, m)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmSplit = {
    newChildren match {
      case Seq(s, m) => StmSplit(s, m)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmSplit = {
    val newM = m.tchk(context, constValues).expectUInt()
    val newS = stm.tchk(context, constValues)
    newS.typ match {
      case TyStm(t, n) =>
        this.rebuild(TyStm(TyStm(t, newM), n / newM), Seq(newS, newM))
      case t => throw new TypeError(s"Stream in StmSplit has type $t.")
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    // Lowering must produce a flat stream, so leave it as-is
    this.stm.lower
  }
}

case class StmJoin(stm: Expr /* Stm<Stm<A; m>; n> */ )(
    typ: Type = Missing
) /* Stm<A; m*n> */
    extends ResolvedSyntaxSugar(stm)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmJoin = {
    newChildren match {
      case Seq(s) => StmJoin(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmJoin = {
    val newS = stm.tchk(context, constValues)
    newS.typ match {
      case TyStm(TyStm(t, m), n) =>
        this.rebuild(TyStm(t, SafeProd(m, n)()), Seq(newS))
      case t =>
        throw new TypeError(
          s"Stream in StmJoin has type $t. Expected a nested stream."
        )
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    // The stream is already flattened during lowering, so there is nothing
    // more to do here
    this.stm.lower
  }
}

/** Like [[StmSlide]], but with a defined initial value for the window and with
  * the output always being valid.
  */
case class StmSlideStartingWith(s: Expr, z: Expr)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s, z)(typ) {

  override def rebuild(
      typ: Type,
      newChildren: Seq[Expr]
  ): StmSlideStartingWith = {
    newChildren match {
      case Seq(s, z) => StmSlideStartingWith(s, z)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmSlideStartingWith = {
    val s = this.s.tchk(context, constValues)
    val (elemTyp, n) = s.typ match {
      case TyStm(TyData(t), n) => (t, n)
      case t =>
        throw new TypeError(
          s"Stream in $className has type $t. Expected a non-nested stream."
        )
    }
    val z = this.z.tchk(context, constValues)
    val m = z.typ match {
      case TyVec(TyData(t1), m) if t1 == elemTyp => m
      case t =>
        throw new TypeError(
          s"Initial window in $className has type $t."
            + s" Expected a vector whose elements have type $elemTyp."
        )
    }
    this.rebuild(TyStm(TyVec(elemTyp, m), n), Seq(s, z))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val s = this.s.lower
    val TyStm(TyData(elemTyp), n) = s.typ
    val z = this.z.lower
    val p = Param("s")(TyStm(elemTyp, -1))
    val buf = Param("buf")(z.typ)
    StmBuild(
      n,
      C(1)(),
      z,
      VecShiftLeft(buf, StmData(p)())().tchk().lower,
      True,
      Map[Param, (Expr, Expr, Expr)](
        buf -> (z, VecShiftLeft(buf, StmData(p)())().tchk().lower, C(1)())
      ),
      Map[Param, (Expr, Expr, Expr)](
        p -> (s, True, 0)
      )
    )()
      .annotate(NoInputsAfterLastOut)
      .annotate(NoOutputsAfterLastIn)
      .annotateWithName("StmSlideStartingWith")
      .tchk()
  }
}

/** Returns a stream of 2-dimensional "windows" from a 2-dimensional stream.
  *
  * This is useful for describing stencil operations.
  *
  * @param stm
  *   the stream to slide over.
  * @param winHeight
  *   the height of each window.
  * @param winWidth
  *   the width of each window.
  */
case class StmSlide2D(stm: Expr, winHeight: Expr, winWidth: Expr)(
    typ: Type = Missing
) extends ResolvedSyntaxSugar(stm, winHeight, winWidth)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmSlide2D = {
    newChildren match {
      case Seq(s, h, w) => StmSlide2D(s, h, w)(typ)
      case _            => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): StmSlide2D = {
    val stm = this.stm.tchk(context, constValues)
    val (t, n, m) = stm.typ match {
      case TyStm(TyStm(TyData(t), m), n) => (t, n, m)
      case t =>
        throw new TypeError(
          s"Stream in $className has type $t. Expected a 2D stream."
        )
    }
    val winHeight = this.winHeight.tchk(context, constValues).expectUInt()
    val winWidth = this.winWidth.tchk(context, constValues).expectUInt()
    val outHeight = ToUnsigned(SafeSum(n, 1, C(-1)() * winHeight)())()
    val outWidth = ToUnsigned(SafeSum(m, 1, C(-1)() * winWidth)())()
    val outTyp =
      TyStm(TyStm(TyVec(TyVec(t, winWidth), winHeight), outWidth), outHeight)
    this.rebuild(outTyp, Seq(stm, winHeight, winWidth))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val stm = this.stm.lower
    val winHeight = this.winHeight.lower
    val winWidth = this.winWidth.lower
    val TyStm(TyStm(TyData(elemTyp), m), n) = this.stm.typ
    // Input stream
    val input = Param("s")(TyStm(elemTyp, -1))
    // Line buffer
    val bufLen =
      ToUnsigned(SafeSum(SafeProd(winHeight - 1, m)(), winWidth - 1)())()
    val buf = Param("buf")(TyVec(elemTyp, bufLen))
    // Shifted and reshaped line buffer, for finding outputs
    val zeros = VecBuild(
      ToUnsigned(SafeSum(m, C(-1)() * winWidth)())(),
      U32 ::+ (_ => AllZero(elemTyp))
    )()
    val buf2d =
      VecSplit(VecConcat(VecAppend(buf, StmData(input)())(), zeros)(), m)()
    // Input counters, to know when buffer is full
    val maxCol = ToUnsigned(SafeSum(m, winWidth, -2)())().tchk().lower
    val maxRow = ToUnsigned(SafeSum(winHeight, -1)())().tchk().lower
    val row = Param("row")(winHeight.typ)
    val col = Param("col")(maxCol.typ)
    val data = VecBuild(
      winHeight,
      winHeight.typ ::+ (i =>
        VecBuild(
          winWidth,
          winWidth.typ ::+ (j => VecAccess(VecAccess(buf2d, i)(), j)())
        )()
      )
    )().tchk().lower
    StmBuild(
      ToUnsigned(
        SafeProd(
          SafeSum(n, C(-1)() * winHeight, 1)(),
          SafeSum(m, C(-1)() * winWidth, 1)()
        )()
      )().tchk().lower,
      Tuple()(),
      Undefined(data.typ),
      data,
      ((row === maxRow) && (col < m)).tchk().lower,
      Map[Param, (Expr, Expr, Expr)](
        buf -> (
          Undefined(buf.typ),
          VecShiftLeft(buf, StmData(input)())().tchk().lower,
          Tuple()()
        ),
        row -> (
          C(0)(row.typ),
          Mux((col === maxCol) && (row !== maxRow), row + 1, row)()
            .tchk()
            .lower,
          Tuple()()
        ),
        col -> (
          C(0)(col.typ),
          Mux(col === maxCol, Cast(winWidth - 1, col.typ)(), col + 1)()
            .tchk()
            .lower,
          Tuple()()
        )
      ),
      Map[Param, (Expr, Expr, Expr)](
        input -> (stm, True, 0)
      )
    )().annotate(NoInputsAfterLastOut).annotateWithName(this.className).tchk()
  }
}

/** Like `FIFON` in
  * [[https://dl.acm.org/doi/10.1145/3385412.3385983 Aetherling]].
  */
object Fifo {
  def apply(x: Expr): Expr = {
    // TODO: Is it really a no-op?
    x
  }
}
