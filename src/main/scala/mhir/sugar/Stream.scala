package mhir.sugar

import com.typesafe.scalalogging.Logger
import mhir.ir.{ExprPrinter => EP, _}
import mhir.parse.SyntaxError
import mhir.sugar.Streamifier.Streamify
import mhir.typecheck._

import scala.annotation.{elidable, tailrec}

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
    extends SyntaxSugar(
      Seq(n, s) ++ inputs.flatMap({ case (x, in) => Seq(x, in) }): _*
    )(typ) {

  private val logger: Logger = Logger(getClass.getName)

  private def inputVars: Set[Param] = this.inputs.keySet

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
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
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val n = this.n.tchk(context).expectUInt()
    val s = this.s.tchk(context).expectStream()
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
      x -> s.tchk(context).expectType(x.typ)
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
        StmBuild(0, Default(t).lower, True)()
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
        val ctrByInput = s.seedByVar
          .flatMap({
            case (x, p) if x.typ.isInstanceOf[TyStm] =>
              val TyStm(_, inLen) = p.typ
              val ctrTyp = inLen match {
                case IntCst(n) => TyAnyInt.tightest(0, n)
                case _         => inLen.typ
              }
              Some(x -> Param("in_ctr")(ctrTyp))
            case _ => None
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
              withInCtrs.data,
              withInCtrs.valid && (outCtr < outputsUntilReset).tchk().lower,
              withInCtrs.equations
            )(annotations = withInCtrs.annotations)
              .tchk()
              .asInstanceOf[StmBuild]
          }
          s1.addOutputCounter(outCtr)
        }
        val shouldReset = if (s.annotations.contains(NoInputsAfterLastOut)) {
          // No need to count inputs: based on the annotation, once we read the
          // last output, we know we can reset immediately
          (withCtrs.nextByVar(outCtr) === outputsUntilReset).tchk().lower
        } else if (s.annotations.contains(NoOutputsAfterLastIn)) {
          // No need to count outputs: based on the annotation, once we read the
          // last inputs, we know we can reset immediately
          val inputsUntilReset: Seq[(Param, Expr)] =
            s.seedByVar
              .flatMap({ case (x, z) =>
                z.typ match {
                  case TyStm(_, n) if ctrByInput.contains(x) =>
                    Some(ctrByInput(x) -> n)
                  case _ => None
                }
              })
              .toSeq
          val shouldReset = inputsUntilReset
            .map({ case (ctr, n) => withCtrs.nextByVar(ctr) === n })
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
            s.seedByVar
              .flatMap({ case (x, z) =>
                z.typ match {
                  case TyStm(_, n) if ctrByInput.contains(x) =>
                    Some(ctrByInput(x) -> n)
                  case _ => None
                }
              })
              .toSeq
          val shouldReset = ((outCtr -> outputsUntilReset) +: inputsUntilReset)
            .map({ case (ctr, n) => withCtrs.nextByVar(ctr) === n })
            .reduce[Expr]({ case (x, y) => x && y })
          shouldReset.tchk().lower
        }
        val result = StmBuild(
          withCtrs.n,
          withCtrs.data,
          withCtrs.valid,
          withCtrs.equations.map({
            case (x, (z: Undefined, next)) =>
              // No need to reset!
              x -> (z, next)
            case (x, (z, next)) if x.typ.isData =>
              val newNext = Mux(shouldReset, z, next)()
              x -> (z, newNext)
            case (x, (s, ready)) if x.typ.isInstanceOf[TyStm] =>
              val newStm = addCountersAndReset(s)
              x -> (newStm, ready)
            case _ =>
              ???
          })
        )(annotations = withCtrs.annotations).tchk()
        result
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
          s.data,
          s.valid,
          s.equations.map({
            case (x, (s, ready)) if x.typ.isInstanceOf[TyStm] =>
              x -> (multiplyLengths(s, inputStreams), ready)
            case eqn => eqn
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
        StmBuild(
          s.n,
          s.data,
          s.valid,
          s.equations.map({
            case (x, (s, ready)) if x.typ.isInstanceOf[TyStm] =>
              x -> (repeatExternalInputs(s, inputStreams), ready)
            case eqn => eqn
          })
        )(annotations = s.annotations)
      case _ =>
        ???
    }
  }
}

case class Iterate(
    n: Expr /* Int */,
    z: Expr /* A */,
    f: Expr /* A -> A */
)(typ: Type = Missing) /* Stm<A; 1> */
    extends SyntaxSugar(n, z, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n, z, f) => Iterate(n, z, f)(typ)
      case _            => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val n = this.n.tchk(context).expectUInt()
    val z = this.z.tchk(context)
    val f = this.f.tchk(context).expectType(z.typ ->: z.typ)
    this.rebuild(TyStm(z.typ, 1), Seq(n, z, f))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val n = this.n.lower
    val z = this.z.lower
    val f = this.f.lower
    val i = Param("i")(n.typ)
    val t = z.typ
    val acc = Param("acc")(t)
    StmBuild(
      1,
      acc,
      i === n,
      Map[Param, (Expr, Expr)](
        i -> (IntCst(0)(n.typ), i + 1),
        acc -> (z, FunCall(f, acc)(t))
      )
    )()
      .annotate(NoInputsAfterLastOut)
      .annotateWithName(this.className)
      .tchk()
      .lower
  }
}

case class StmCst(n: Expr, k: Expr)(typ: Type = Missing)
    extends SyntaxSugar(n, k)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n, c) => StmCst(n, c)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val n = this.n.tchk(context).expectUInt()
    val k = this.k.tchk(context)
    this.rebuild(TyStm(k.typ, n), Seq(n, k))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val n = this.n.lower
    val k = this.k.lower
    val out = k.typ match {
      case _: TyStm => StmRepeat(k, n)()
      case _ =>
        StmBuild(n, k, True)()
          .annotate(NoInputsAfterLastOut)
          .annotateWithName(getClass.getSimpleName)
    }
    out.tchk().lower
  }
}

case class StmCount(n: Expr)(typ: Type = Missing) extends SyntaxSugar(n)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n) => StmCount(n)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newN = n.tchk(context).expectUInt()
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
    extends SyntaxSugar(n, z, delta)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n, z, delta) => StmRange(n, z, delta)(typ)
      case _                => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newN = n.tchk(context).expectUInt()
    val newZ = z.tchk(context).expectAnyInt()
    val newDelta = delta.tchk(context).expectType(newZ.typ)
    this.rebuild(TyStm(newZ.typ, newN), Seq(newN, newZ, newDelta))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    val n = this.n.lower
    val z = this.z.lower
    val delta = this.delta.lower
    val a = Param("a")(z.typ)
    StmBuild(
      n,
      a,
      True,
      Map[Param, (Expr, Expr)](
        a -> (z, (a + delta).tchk().lower)
      )
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
) extends SyntaxSugar(n, m, z, delta)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n, m, z, delta) => StmVecRange(n, m, z, delta)(typ)
      case _                   => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val n = this.n.tchk(context).expectUInt()
    val m = this.m.tchk(context).expectUInt()
    val z = this.z.tchk(context).expectAnyInt()
    val delta = this.delta.tchk(context).expectType(z.typ)
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
      v,
      True,
      Map[Param, (Expr, Expr)](
        v -> (
          VecBuild(m, m.typ ::+ (i => z + i * delta))().tchk().lower,
          VecBuild(m, m.typ ::+ (i => VecAccess(v, i)() + m * delta))()
            .tchk()
            .lower
        )
      )
    )().annotate(NoInputsAfterLastOut).annotateWithName(this.className).tchk()
  }
}

// TODO: What if c is a stream?
case class StmCst2D(
    n: Expr /* Int */,
    m: Expr /* Int */,
    k: Expr /* T */
)(typ: Type = Missing) /* Stm<Stm<T; m>; n> */
    extends SyntaxSugar(n, m, k)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n, m, c) => StmCst2D(n, m, c)(typ)
      case _            => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val n = this.n.tchk(context).expectUInt()
    val m = this.m.tchk(context).expectUInt()
    val k = this.k.tchk(context)
    this.rebuild(TyStm(TyStm(k.typ, m), n), Seq(n, m, k))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val n = this.n.lower
    val m = this.m.lower
    val k = this.k.lower
    StmBuild(SafeProd(n, m)().tchk().lower, k, True)()
      .annotate(NoInputsAfterLastOut)
      .annotateWithName(this.className)
      .tchk()
  }
}

case class StmCount2D(n: Expr, m: Expr)(typ: Type = Missing)
    extends SyntaxSugar(n, m)(typ) /* Stm<Stm<(Int, Int); m>; n> */ {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n, m) => StmCount2D(n, m)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val n = this.n.tchk(context).expectUInt()
    val m = this.m.tchk(context).expectUInt()
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
      Tuple(i, j)(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (IntCst(0)(n.typ), Mux(j === m - 1, i + 1, i)()),
        j -> (IntCst(0)(m.typ), Mux(j === m - 1, IntCst(0)(m.typ), j + 1)())
      )
    )()
      .annotate(NoInputsAfterLastOut)
      .annotateWithName(this.className)
      .tchk()
      .lower
  }
}

case class StmCount3D(n1: Expr, n2: Expr, n3: Expr)(typ: Type = Missing)
    extends SyntaxSugar(n1, n2, n3)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n1, n2, n3) => StmCount3D(n1, n2, n3)(typ)
      case _               => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val n1 = this.n1.tchk(context).expectUInt()
    val n2 = this.n2.tchk(context).expectUInt()
    val n3 = this.n3.tchk(context).expectUInt()
    this.rebuild(
      TyStm(TyStm(TyStm((n1.typ, n2.typ, n3.typ), n3), n2), n1),
      Seq(n1, n2, n3)
    )
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val n1 = this.n1.lower
    val n2 = this.n2.lower
    val n3 = this.n3.lower
    val i = Param("i")(n1.typ)
    val j = Param("j")(n2.typ)
    val k = Param("k")(n3.typ)
    StmBuild(
      SafeProd(n1, n2, n3)(),
      Tuple(i, j, k)(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (
          C(0)(i.typ),
          Mux((k === C(-1)() + n3) && (j === C(-1)() + n2), i + 1, i)()
        ),
        j -> (
          C(0)(j.typ),
          Mux(
            (k === C(-1)() + n3) && (j === C(-1)() + n2),
            C(0)(j.typ),
            Mux(k === C(-1)() + n3, j + 1, j)()
          )()
        ),
        k -> (
          C(0)(k.typ),
          Mux(k === C(-1)() + n3, C(0)(k.typ), k + 1)()
        )
      )
    )()
      .annotate(NoInputsAfterLastOut)
      .annotateWithName(this.className)
      .tchk()
      .lower
  }
}

case class StmMap(
    input: Expr /* Stm<A; n> */,
    f: Expr /* A -> B */
)(typ: Type = Missing) /* Stm<B; n> */
    extends SyntaxSugar(input, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, f) => StmMap(s, f)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newS = input.tchk(context)
    val (t1, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t           => throw new TypeError(s"Stream in StmMap has type $t.")
    }
    val newF = f.annotateFunc(t1).tchk(context)
    val t2 = newF.typ match {
      case TyArrow(t, t2) if t ~= t1 => t2
      case t =>
        throw new TypeError(
          s"Function in StmMap has type $t. Expected a function whose input type is $t1."
        )
    }
    this.rebuild(TyStm(t2, n), Seq(newS, newF))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    SL.logger.trace(s"lowering $className: $this")
    requireType()
    val input = this.input.lower
    val f = this.f.lower.asInstanceOf[Function]
    val TyStm(_, n) = this.typ
    val Function(s, innerStm) = f.streamify
    StmReset(n, innerStm, Map(s -> input))().tchk().lower
  }
}

case class StmMap2(s1: Expr, s2: Expr, f: Expr)(typ: Type = Missing)
    extends SyntaxSugar(s1, s2, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s1, s2, f) => StmMap2(s1, s2, f)(typ)
      case _              => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val s1 = this.s1.tchk(context)
    val (t1, n1) = s1.typ match {
      case TyStm(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"First stream in $className has type $t."
            + " Expected a stream."
        )
    }
    val s2 = this.s2.tchk(context)
    val (t2, n2) = s2.typ match {
      case TyStm(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"Second stream in $className has type $t."
            + " Expected a stream."
        )
    }
    if (!c.sameLen(n1, n2)) {
      throw new TypeError(
        s"Stream lengths in $className do not match: $n1 and $n2."
      )
    }
    val f = this.f.annotateFunc(t1, t2).tchk(context)
    val t3 = f.typ match {
      case TyArrow(ft1, TyArrow(ft2, ft3)) if (ft1 ~= t1) && (ft2 ~= t2) =>
        ft3
      case t =>
        throw new TypeError(
          s"Function in $className has type $t."
            + s" Expected a function with input types $t1 and $t2"
        )
    }
    this.rebuild(TyStm(t3, n1), Seq(s1, s2, f))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    SL.logger.trace(s"lowering $className: $this")
    requireType()
    val s1 = this.s1.lower
    val s2 = this.s2.lower
    val f = this.f.lower.asInstanceOf[Function]
    val n = this.typ.asInstanceOf[TyStm].n
    val Function(s1Param, Function(s2Param, innerStm)) = f.streamify
    StmReset(n, innerStm, Map(s1Param -> s1, s2Param -> s2))().tchk().lower
  }
}

case class StmAccess(
    stm: Expr /* Stm<A; n> */,
    k: Expr /* Int */
)(typ: Type = Missing) /* Stm<A; 1> */
    extends SyntaxSugar(stm, k)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, k) => StmAccess(s, k)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val s = this.stm.tchk(context).expectStream()
    val t = s.typ.asInstanceOf[TyStm].t
    val k = this.k.tchk(context).expectUInt()
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
    val s = Param("s")(stm.typ) // input stream
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
      StmData(s)(),
      (i === k).tchk().lower,
      Map[Param, (Expr, Expr)](
        s -> (stm, True),
        i -> (
          C(0)(i.typ),
          Mux(j + 1 === perRow, i + 1, i)().tchk().lower
        ),
        j -> (
          C(0)(j.typ),
          Mux(j + 1 === perRow, C(0)(j.typ), j + 1)().tchk().lower
        )
      )
    )(annotations = annotations).annotateWithName(this.className).tchk()
  }
}

/** Reduction over a stream.
  *
  * This is a bit like [[StmFold]], but the head of the stream is used as the
  * initial value.
  *
  * This is meant to mirror the `reduce_t` primitive from
  * [[https://dl.acm.org/doi/10.1145/3385412.3385983 Aetherling]]. Therefore,
  * strange expressions like `reduce_t (map_s (add I) I) I` must unfortunately
  * be supported.
  *
  * @param s
  *   `Stm[T, n]`. The stream to reduce over.
  * @param f
  *   `(T, T) -> T`. The function to use for reducing.
  */
case class StmReduce(s: Expr, f: Expr)(typ: Type = Missing)
    extends SyntaxSugar(s, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, f) => StmReduce(s, f)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val s = this.s.tchk(context)
    // The type of the accumulator, but possibly wrapped in a bunch of vectors
    // and streams of length 1
    val wrappedTyp = s.typ match {
      case TyStm(t, _) => t
      case t =>
        throw new TypeError(
          s"Stream in $className has type $t. Expected a stream."
        )
    }
    val tupledTyp = tupleElemType(wrappedTyp, this.f)
    val f =
      this.f
        .annotateFunc(tupledTyp)
        .tchk(context)
        .expectType(tupledTyp ->: wrappedTyp)
    this.rebuild(TyStm(wrappedTyp, 1), Seq(s, f))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    SL.logger.trace(s"lowering $className: $this")
    requireType()
    val s = this.s.lower
    val n = this.s.typ.asInstanceOf[TyStm].n
    if (c.sameLen(n, C(1)())) {
      // Reduce over a stream of length 1 is a no-op
      s
    } else {
      val wrappedTyp = this.typ.asInstanceOf[TyStm].t
      val f = unwrapFunc(wrappedTyp, this.f).lower
      val elemTyp = unwrapTyp(wrappedTyp, this.f).lower
      val acc = Param("acc")(elemTyp)
      val t = Param("t")(n.typ)
      val sAcc = Param("s")(s.typ)
      val sData = unwrapElem(wrappedTyp, this.f, StmData(sAcc)())
      val firstStep = Param("first_step")(TyBool)
      StmBuild(
        1,
        wrapResult(wrappedTyp, this.f, f(Tuple(acc, sData)())),
        Sum(C(1)(t.typ), t)() equ n,
        Map[Param, (Expr, Expr)](
          firstStep -> (True, False),
          t -> (C(0)(n.typ), Sum(C(1)(n.typ), t)()),
          sAcc -> (s, True),
          acc -> (
            Default(elemTyp).lower,
            Mux(firstStep, sData, f(Tuple(acc, sData)()))()
          )
        )
      )().annotate(NoInputsAfterLastOut).annotateWithName(this.className).tchk()
    }
  }

  private def tupleElemType(wrappedTyp: Type, f: Expr)(implicit
      c: Canonicalizer
  ): Type = {
    (wrappedTyp, f) match {
      case (TyVec(t, IntCst(1)), Function(v0, VecMap(v1, g))) if v0 == v1 =>
        TyVec(tupleElemType(t, g), 1)
      case (TyStm(t, IntCst(1)), Function(s0, StmMap(s1, g))) if s0 == s1 =>
        TyStm(tupleElemType(t, g), 1)
      case _ =>
        (wrappedTyp, wrappedTyp)
    }
  }

  @tailrec
  private def unwrapTyp(wrappedTyp: Type, f: Expr): Type = {
    (wrappedTyp, f) match {
      case (TyVec(t, IntCst(1)), Function(v0, VecMap(v1, g))) if v0 == v1 =>
        unwrapTyp(t, g)
      case (TyStm(t, IntCst(1)), Function(s0, StmMap(s1, g))) if s0 == s1 =>
        unwrapTyp(t, g)
      case _ =>
        wrappedTyp
    }
  }

  @tailrec
  private def unwrapFunc(wrappedTyp: Type, f: Expr): Expr = {
    (wrappedTyp, f) match {
      case (TyVec(t, IntCst(1)), Function(v0, VecMap(v1, g))) if v0 == v1 =>
        unwrapFunc(t, g)
      case (TyStm(t, IntCst(1)), Function(s0, StmMap(s1, g))) if s0 == s1 =>
        unwrapFunc(t, g)
      case _ =>
        f
    }
  }

  private def unwrapElem(wrappedTyp: Type, f: Expr, x: Expr): Expr = {
    (wrappedTyp, f) match {
      case (TyVec(t, IntCst(1)), Function(v0, VecMap(v1, g))) if v0 == v1 =>
        VecAccess(unwrapElem(t, g, x), 0)()
      case (TyStm(t, IntCst(1)), Function(s0, StmMap(s1, g))) if s0 == s1 =>
        // Streams should be moved to the outside during lowering, so no need
        // to do anything here
        unwrapElem(t, g, x)
      case _ =>
        x
    }
  }

  private def wrapResult(wrappedTyp: Type, f: Expr, x: Expr): Expr = {
    (wrappedTyp, f) match {
      case (TyVec(t, IntCst(1)), Function(v0, VecMap(v1, g))) if v0 == v1 =>
        VecBuild(1, U8 ::+ (_ => wrapResult(t, g, x)))()
      case (TyStm(t, IntCst(1)), Function(s0, StmMap(s1, g))) if s0 == s1 =>
        // Streams should be moved to the outside during lowering, so no need
        // to do anything here
        wrapResult(t, g, x)
      case _ =>
        x
    }
  }
}

// TODO: Generalize to include pre-adder?
// TODO: Generalize to allow 27-bit systolic mode?
case class MulAddCascaded(s1: Expr, s2: Expr)(typ: Type = Missing)
    extends SyntaxSugar(s1, s2)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s1, s2) => MulAddCascaded(s1, s2)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val s1 = this.s1.tchk(context)
    val (n, m) = s1.typ match {
      case TyStm(TyVec(_: TyAnyInt, m), n) =>
        // TODO: Enforce constraint on bitwidth (18 bits?)
        (n, m)
      case t =>
        throw new TypeError(
          s"First stream in $className has type $t."
            + s" Expected a stream of vectors."
        )
    }
    val s2 = this.s2.tchk(context)
    s2.typ match {
      case TyStm(TyVec(_: TyAnyInt, m2), n2) =>
        // TODO: Enforce constraint on bitwidth (18 bits?)
        if (!c.sameLen(n, n2)) {
          throw new TypeError(
            s"Second stream in $className has length $n2."
              + s" Expected a stream of length $n."
          )
        }
        if (!c.sameLen(m, m2)) {
          throw new TypeError(
            s"Second stream in $className contains vectors of length $m2."
              + s" Expected vectors of length $m."
          )
        }
        ()
      case t =>
        throw new TypeError(
          s"Second stream in $className has type $t." +
            s" Expected a stream of vectors."
        )
    }
    this.rebuild(TyStm(TySInt(44), n), Seq(s1, s2))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val s1 = this.s1.lower
    val s2 = this.s2.lower
    val TyStm(int, n) = this.typ
    val (t1, m) = this.s1.typ match {
      case TyStm(TyVec(t, m), _) =>
        m match {
          case IntCst(0) => ???
          case IntCst(1) => ???
          case IntCst(m) => (t, m.toInt)
          case e         => ???
        }
      case _ => ???
    }
    val t2 = this.s2.typ match {
      case TyStm(TyVec(t, _), _) => t
      case _                     => ???
    }
    val p1 = Param("p1")(TyStm(TyVec(t1, m), -1))
    val p2 = Param("p2")(TyStm(TyVec(t2, m), -1))
    val stageVars = (0 until m).map(i => Param(s"stage$i")(int))
    val stages = stageVars.zipWithIndex
      .map({ case (x, i) =>
        val z = Undefined(int)
        val mul = Prod(
          ReshapeData(VecAccess(StmData(p1)(), i)(), int)(),
          ReshapeData(VecAccess(StmData(p2)(), i)(), int)()
        )().tchk().lower
        val next = if (i == 0) mul else Sum(stageVars(i - 1), mul)()
        x -> (z, next)
      })
      .toMap
    StmBuild(
      n,
      stages.last._2._2,
      True,
      stages.init ++ Map[Param, (Expr, Expr)](
        p1 -> (s1, True),
        p2 -> (s2, True)
      )
    )().tchk()
  }
}

case class StmFold1D(s: Expr, z: Expr, f: Expr)(typ: Type = Missing)
    extends SyntaxSugar(s, z, f)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, z, f) => StmFold1D(s, z, f)(typ)
      case _            => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val s = this.s.tchk(context)
    val t1 = s.typ match {
      case TyStm(TyData(t), _) => t
      case TyStm(_: TyStm, _) =>
        throw new TypeError(s"$className does not accept nested streams.")
      case t =>
        throw new TypeError(
          s"First input to $className has type $t."
            + s" Expected a stream."
        )
    }
    val z = this.z.tchk(context)
    val t2 = z.typ match {
      case TyData(t) => t
      case t =>
        throw new TypeError(
          s"Second input to $className has type $t."
            + s" Expected a data type."
        )
    }
    val f = this.f.tchk(context).expectType((t2, t1) ->: t2)
    this.rebuild(TyStm(z.typ, 1), Seq(s, z, f))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val s = this.s.lower
    val TyStm(t1, n) = s.typ
    val z = this.z.lower
    val t2 = z.typ
    val f = this.f.lower
    val p = Param("p")(TyStm(t1, -1))
    val acc = Param("acc")(t2)
    val i = {
      val typ = n.typ match {
        case TyUInt(0) => TyUInt(1)
        case t         => t
      }
      Param("i")(typ)
    }
    StmBuild(
      1,
      Mux(
        n === 0,
        z,
        f(Tuple(acc, StmData(p)())())
      )().tchk().lower,
      ((n === 0) || Sum(C(1)(i.typ), i)() === n).tchk().lower,
      Map[Param, (Expr, Expr)](
        p -> (s, (n !== 0).tchk().lower),
        acc -> (
          z,
          Mux(n === 0, z, f(Tuple(acc, StmData(p)())()))().tchk().lower
        ),
        i -> (C(0)(i.typ), Sum(C(1)(i.typ), i)())
      )
    )().tchk()
  }
}

case class StmAll(s: Expr)(typ: Type = Missing) extends SyntaxSugar(s)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s) => StmAll(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val s = this.s.tchk(context)
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
    StmFold1D(
      s,
      True,
      (TyBool, TyBool) ::+ (x => And(x.__0, x.__1)())
    )().tchk().lower
  }
}

case class Vec2Stm(v: Expr /* Vec<A; n> */ )(
    typ: Type = Missing
) /* Stm<A; n> */
    extends SyntaxSugar(v)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v) => Vec2Stm(v)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newV = v.tchk(context)
    newV.typ match {
      case TyVec(t, n) =>
        this.rebuild(TyStm(t, n), Seq(newV))
      case t => throw new TypeError(s"Vector in Vec2Stm has type $t.")
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val v = this.v.lower
    v.typ match {
      case TyVec(_, n) =>
        val acc = Param("v")(v.typ)
        val TyVec(elemTyp, _) = v.typ
        StmBuild(
          n,
          VecAccess(acc, 0)(),
          True,
          Map[Param, (Expr, Expr)](
            // TODO: It might be useful to use undefined[T] rather than
            //       default[T] here. But it may be necessary to update the
            //       evaluator to allow this use of undefined[T]
            acc -> (v, VecShiftLeft(acc, Default(elemTyp))().tchk().lower)
          )
        )()
          .annotate(NoInputsAfterLastOut)
          .annotateWithName(this.className)
          .tchk()
      case TyStm(tv: TyVec, _) =>
        StmMap(v, tv ::+ (v => Vec2Stm(v)()))().tchk().lower
      case t => throw new TypeError(s"Invalid type for vector in Vec2Stm: $t.")
    }
  }
}

case class Vec2StmOld(v: Expr /* Vec<A; n> */ )(
    typ: Type = Missing
) /* Stm<A; n> */
    extends SyntaxSugar(v)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v) => Vec2StmOld(v)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newV = v.tchk(context)
    newV.typ match {
      case TyVec(t, n) =>
        this.rebuild(TyStm(t, n), Seq(newV))
      case t => throw new TypeError(s"Vector in Vec2Stm has type $t.")
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val v = this.v.lower
    v.typ match {
      case TyVec(_, n) =>
        val i = Param("i")(U32)
        StmBuild(
          n,
          VecAccess(v, i)(),
          True,
          Map[Param, (Expr, Expr)](i -> (C(0)(U32), i + 1))
        )()
          .annotate(NoInputsAfterLastOut)
          .annotateWithName(this.className)
          .tchk()
          .lower
      case TyStm(tv: TyVec, _) =>
        StmMap(v, tv ::+ (v => Vec2StmOld(v)()))().tchk().lower
      case t => throw new TypeError(s"Invalid type for vector in Vec2Stm: $t.")
    }
  }
}

case class StmPrepend(stm: Expr /* Stm<A; n> */, e: Expr /* A */ )(
    typ: Type = Missing
) /* Stm<A; n+1> */
    extends SyntaxSugar(stm, e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, e) => StmPrepend(s, e)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newS = stm.tchk(context)
    val (t, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmPrepend has type $t.")
    }
    val newE = e.tchk(context).expectType(t)
    this.rebuild(TyStm(t, n + 1), Seq(newS, newE))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    StmConcat(StmCst(1, e)(), stm)().tchk().lower.asInstanceOf[StmBuild]
  }
}

case class StmAppend(stm: Expr /* Stm<A; n> */, e: Expr /* A */ )(
    typ: Type = Missing
) /* Stm<A; n+1> */
    extends SyntaxSugar(stm, e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, e) => StmAppend(s, e)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newS = stm.tchk(context)
    val (t, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmAppend has type $t.")
    }
    val newE = e.tchk(context).expectType(t)
    this.rebuild(TyStm(t, n + 1), Seq(newS, newE))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    StmConcat(stm, StmCst(1, e)())().tchk().lower
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
case class StmPrefix(
    stm: Expr /* Stm<A; n> */,
    k: Expr /* Int */
)(typ: Type = Missing) /* Stm<A; k> */
    extends SyntaxSugar(stm, k)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(stm, k) => StmPrefix(stm, k)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val k = this.k.tchk(context).expectUInt()
    val s = this.stm.tchk(context)
    s.typ match {
      case TyStm(t, _) =>
        this.rebuild(TyStm(t, k), Seq(s, k))
      case t => throw new TypeError(s"Stream in StmPrefix has type $t.")
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
    val s = Param("s")(stm.typ) // input stream
    StmBuild(
      SafeProd(k, perRow)().tchk().lower,
      StmData(s)(),
      True,
      Map[Param, (Expr, Expr)](
        s -> (stm, True)
      )
    )().annotateWithName(this.className).tchk()
  }
}

/** Take elements from the end of a stream.
  *
  * NOTE: k must be such that 0 &le; k &le; n.
  *
  * @param stm
  *   The input stream.
  * @param k
  *   The number of elements to extract.
  */
case class StmSuffix(
    stm: Expr /* Stm<A; n> */,
    k: Expr /* Int */
)(typ: Type = Missing) /* Stm<A; k> */
    extends SyntaxSugar(stm, k)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(stm, k) => StmSuffix(stm, k)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newK = k.tchk(context).expectUInt()
    val newS = stm.tchk(context)
    newS.typ match {
      case TyStm(t, _) =>
        this.rebuild(TyStm(t, newK), Seq(newS, newK))
      case t => throw new TypeError(s"Stream in StmPrefix has type $t.")
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val stm = this.stm.lower
    val k = this.k.lower
    val n = this.stm.typ.asInstanceOf[TyStm].n
    val perRow = this.stm.typ.asInstanceOf[TyStm].t.lower match {
      case TyStm(_, n) => n
      case _           => IntCst(1)()
    }
    val s = Param("s")(stm.typ) // input stream
    val i = Param("i")(U32) // index of current row
    val j = Param("j")(U32) // index within row
    StmBuild(
      (k * perRow).tchk().lower,
      StmData(s)(),
      (i >= n - k).tchk().lower,
      Map[Param, (Expr, Expr)](
        s -> (stm, True),
        i -> (C(0)(U32), Mux(j === perRow - 1, i + 1, i)().tchk().lower),
        j -> (
          C(0)(U32),
          Mux(j === perRow - 1, C(0)(U32), j + 1)().tchk().lower
        )
      )
    )().annotate(NoInputsAfterLastOut).annotateWithName(this.className).tchk()
  }
}

/** Discard the first element of the given stream and insert the given value at
  * the end.
  *
  * @param stm
  *   the stream to shift.
  * @param e
  *   the element to insert.
  */
case class StmShiftLeft(stm: Expr /* Stm<A; n> */, e: Expr /* A */ )(
    typ: Type = Missing
) /* Stm<A; n> */
    extends SyntaxSugar(stm, e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, e) => StmShiftLeft(s, e)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newS = stm.tchk(context)
    val (t, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmShiftLeft has type $t.")
    }
    val newE = e.tchk(context).expectType(t)
    this.rebuild(TyStm(t, n), Seq(newS, newE))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val n = this.typ.asInstanceOf[TyStm].n
    StmAppend(StmSuffix(stm, ToUnsigned(n - 1)())(), e)().tchk().lower
  }
}

/** Discard the last element of the given stream and insert the given value at
  * the beginning.
  *
  * @param stm
  *   the stream to shift.
  * @param e
  *   the element to insert.
  */
case class StmShiftRight(stm: Expr /* Stm<A; n> */, e: Expr /* A */ )(
    typ: Type = Missing
) /* Stm<A; n> */
    extends SyntaxSugar(stm, e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, e) => StmShiftRight(s, e)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newS = stm.tchk(context)
    val (t, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmShiftRight has type $t.")
    }
    val newE = e.tchk(context).expectType(t)
    this.rebuild(TyStm(t, n), Seq(newS, newE))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val n = this.typ.asInstanceOf[TyStm].n
    StmPrepend(StmPrefix(stm, ToUnsigned(n - 1)())(), e)().tchk().lower
  }
}

/** Discard the last element of the given stream and insert an undefined value
  * at the beginning.
  *
  * @param stm
  *   the stream to shift.
  */
case class StmShiftRightGarbage(stm: Expr, shiftAmount: IntCst)(
    typ: Type = Missing
) extends SyntaxSugar(stm, shiftAmount)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, m: IntCst) => StmShiftRightGarbage(s, m)(typ)
      case _                 => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newS = stm.tchk(context)
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
      this.shiftAmount.tchk(context).expectUInt().asInstanceOf[IntCst]
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
    val TyStm(t, n) = this.stm.typ
    val s = Param("s")(TyStm(t, -1))
    val buf = Param("buf")(TyVec(t, shiftAmount))
    StmBuild(
      n,
      VecAccess(buf, C(shiftAmount.i - 1)())(),
      True,
      Map[Param, (Expr, Expr)](
        s -> (stm, True),
        buf -> (
          Undefined(TyVec(t, shiftAmount)),
          VecShiftRight(buf, StmData(s)())().tchk().lower
        )
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
) extends SyntaxSugar(stm, shiftAmount)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, m: IntCst) => StmVecShiftRightGarbage(s, m)(typ)
      case _                 => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val stm = this.stm.tchk(context)
    stm.typ match {
      case TyStm(_: TyVec, _) => ()
      case t =>
        throw new TypeError(
          s"Stream in $className has type $t."
            + " Expected a stream of vectors."
        )
    }
    val shiftAmount =
      this.shiftAmount.tchk(context).expectUInt().asInstanceOf[IntCst]
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
          VecPrefix(buf, C(m)())()
        } else {
          // Some output data comes directly from the input stream
          VecConcat(buf, VecPrefix(StmData(s)(), C(m - shiftAmount.i)())())()
        }
        val bufNext = if (shiftAmount.i >= m) {
          // All input data goes into the buffer
          VecConcat(VecSuffix(buf, C(shiftAmount.i - m)())(), StmData(s)())()
        } else {
          // Some input data doesn't go into the buffer
          VecSuffix(StmData(s)(), shiftAmount)()
        }
        StmBuild(
          n,
          data.tchk().lower,
          True,
          Map[Param, (Expr, Expr)](
            buf -> (
              Undefined(TyVec(t, shiftAmount)),
              bufNext.tchk().lower
            ),
            s -> (stm, True)
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

case class StmConcat(stm1: Expr /* Stm<A; n1> */, stm2: Expr /* Stm<A; n2> */ )(
    typ: Type = Missing
) /* Stm<A; n1+n2> */
    extends SyntaxSugar(stm1, stm2)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s1, s2) => StmConcat(s1, s2)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newS1 = stm1.tchk(context)
    val (t1, n1) = newS1.typ match {
      case TyStm(t, n1) => (t, n1)
      case t =>
        throw new TypeError(
          s"First input in StmConcat has type $t. Expected a stream."
        )
    }
    val newS2 = stm2.tchk(context)
    val n2 = newS2.typ match {
      case TyStm(t2, n2) if t2 ~= t1 => n2
      case t =>
        throw new TypeError(
          s"Second input in StmConcat has type $t. Expected a stream of $t1."
        )
    }
    this.rebuild(TyStm(t1, SafeSum(n1, n2)()), Seq(newS1, newS2))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val stm1 = this.stm1.lower
    val stm2 = this.stm2.lower
    val n1 = stm1.typ.asInstanceOf[TyStm].n
    val n2 = stm2.typ.asInstanceOf[TyStm].n
    val s1 = Param("s1")(stm1.typ)
    val s2 = Param("s2")(stm2.typ)
    val i = Param("i")(U32)
    StmBuild(
      SafeSum(n1, n2)(),
      Mux(i === n1, StmData(s2)(), StmData(s1)())(),
      True,
      Map[Param, (Expr, Expr)](
        i -> (C(0)(U32), Mux(i === n1, i, i + 1)()),
        s1 -> (stm1, i !== n1),
        s2 -> (stm2, i === n1)
      )
    )()
      .annotate(NoInputsAfterLastOut)
      .annotateWithName(this.className)
      .tchk()
      .lower
  }
}

case class StmZip(a: Expr /* Stm<A; n> */, b: Expr /* Stm<B; n> */ )(
    typ: Type = Missing
) /* Stm<(A, B); n> */
    extends SyntaxSugar(a, b)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(a, b) => StmZip(a, b)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newA = a.tchk(context)
    val (t1, n) = newA.typ match {
      case TyStm(t1, n) if t1.isData => (t1, n)
      case t =>
        throw new TypeError(
          s"First stream in StmZip has type $t. Expected a non-nested stream."
        )
    }
    val newB = b.tchk(context)
    val t2 = newB.typ match {
      case TyStm(t2, _) if t1.isData => t2
      case t =>
        throw new TypeError(
          s"Second stream in StmZip has type $t. Expected a non-nested stream."
        )
    }
    this.rebuild(TyStm(TyTuple(t1, t2), n), Seq(newA, newB))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val t1 = this.a.typ.asInstanceOf[TyStm].t
    val t2 = this.b.typ.asInstanceOf[TyStm].t
    StmMap2(this.a, this.b, t1 ::+ (x => t2 ::+ (y => Tuple(x, y)())))()
      .tchk()
      .lower
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
    extends SyntaxSugar(stm, m)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, m) => StmRepeat(s, m)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newM = m.tchk(context).expectUInt()
    val newS = stm.tchk(context)
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
    val TyStm(typ, n) = stm.typ
    val s = Param("s")(TyStm(typ, -1))
    val v = Param("v")(TyVec(typ, n))
    val tTyp = n match {
      case IntCst(n) => TyAnyInt.tightest(0, math.max(1, n - 1))
      case _         => n.typ
    }
    val t = Param("t")(tTyp)
    val filling = Param("filling")(TyBool)
    StmBuild(
      SafeProd(n, m)().tchk().lower,
      Mux(filling, StmData(s)(), VecAccess(v, t)())(),
      True,
      Map[Param, (Expr, Expr)](
        s -> (stm, filling),
        v -> (
          Undefined(TyVec(typ, n)),
          // Update the vector in such a way that the synthesis tool can turn
          // it into a BRAM (not a massive shift register)
          VecBuild(
            n,
            U32 ::+ (i =>
              Mux(filling && (i === t), StmData(s)(), VecAccess(v, i)())()
                .tchk()
                .lower
            )
          )()
        ),
        t -> (
          C(0)(t.typ),
          Mux(
            // Assume n >= 1
            t === ToUnsigned(C(-1)() + n)(),
            C(0)(t.typ),
            t + 1
          )().tchk().lower
        ),
        filling -> (
          True,
          (filling && (t < ToUnsigned(C(-1)() + n)())).tchk().lower
        )
      )
    )().annotate(NoInputsAfterLastOut).annotateWithName(this.className).tchk()
  }
}

case class StmReverse(stm: Expr /* Stm<A; n> */ )(
    typ: Type = Missing
) /* Stm<A; n> */
    extends SyntaxSugar(stm)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s) => StmReverse(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newS = stm.tchk(context)
    newS.typ match {
      case TyStm(t, n) =>
        this.rebuild(TyStm(t, n), Seq(newS))
      case t =>
        throw new TypeError(
          s"Stream in StmReverse has type $t. Expected a stream."
        )
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    val stm = this.stm.lower // flat stream
    val t = stm.typ.asInstanceOf[TyStm].t
    val n = stm.typ.asInstanceOf[TyStm].n
    val elemSize = this.stm.typ.asInstanceOf[TyStm].t.lower match {
      case TyStm(_, n) => n
      case _           => IntCst(1)()
    }
    StmMap(
      Stm2Vec(stm)() /* flat vector */,
      TyVec(t, n) ::+ (v =>
        Vec2Stm(VecJoin(VecReverse(VecSplit(v, elemSize)()))())()
      )
    )().tchk().lower
  }
}

case class StmSplit(stm: Expr /* Stm<A; n> */, m: Expr /* Int */ )(
    typ: Type = Missing
) /* Stm<Stm<A; m>; n/m> */
    extends SyntaxSugar(stm, m)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, m) => StmSplit(s, m)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newM = m.tchk(context).expectUInt()
    val newS = stm.tchk(context)
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
    extends SyntaxSugar(stm)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s) => StmJoin(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newS = stm.tchk(context)
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

/** Return a stream of "windows" from a stream. Note that if the input stream is
  * multidimensional, the inner dimensions will be converted to vectors and
  * flattened.
  *
  * NOTE: `m` must be such that 1 &le; m &le; n.
  *
  * @param input
  *   (`Stm[A, n]`) a stream of length n.
  * @param winSize
  *   (`Int`) window size.
  * @param stride
  *   (`Int`) how much to move the window per step.
  */
case class StmSlide(
    input: Expr /* Stm<A; n> */,
    winSize: Expr /* Int */,
    stride: Expr = C(1)() /* Int */
)(typ: Type = Missing) /* Stm<Vec<A; m>; n-m+1> */
    extends SyntaxSugar(input, winSize, stride)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, winSize, stride) => StmSlide(s, winSize, stride)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newWinSize = this.winSize.tchk(context).expectUInt()
    val newStride = this.stride.tchk(context).expectUInt()
    val newInput = this.input.tchk(context)
    newInput.typ match {
      case TyStm(t, n) if t.isData =>
        // First window start index (inclusive): 0
        // Last window start index (inclusive): n - winSize
        // We want to know how many multiples of `stride` there are in the
        // range [0, n-winSize].
        // In general, that is ceil( (n - winSize + 1) / stride )
        val newLen =
          CeilDiv(
            ToUnsigned(SafeSum(n, C(-1)() * newWinSize, 1)())(),
            newStride
          )()
            .tchk()
            .lower
        this.rebuild(
          TyStm(TyVec(t, newWinSize), newLen),
          Seq(newInput, newWinSize, newStride)
        )
      case t =>
        throw new TypeError(
          s"Stream in StmSlideV has type $t. Expected a non-nested stream."
        )
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val input = this.input.lower
    val winSize = this.winSize.lower
    val stride = this.stride.lower
    val TyStm(_, myLen) = this.typ
    val TyStm(t, _) = input.typ
    val s = Param("s")(TyStm(t, -1))
    val i = {
      val typ = TyAnyInt.tightest(
        1 - stride.typ.asInstanceOf[TyAnyInt].maxInt,
        myLen.typ.asInstanceOf[TyAnyInt].maxInt
      )
      Param("i")(typ)
    }
    val v = Param("v")(TyVec(t, winSize))
    val lowered = StmBuild(
      myLen,
      VecShiftLeft(v, StmData(s)())().tchk().lower,
      (i + 1 === winSize).tchk().lower,
      Map[Param, (Expr, Expr)](
        s -> (input, True),
        // Number of elements loaded so far
        i -> (
          C(0)(i.typ),
          Mux(
            i + 1 === winSize,
            Cast(i + 1 - stride, i.typ)().tchk().lower,
            i + 1
          )().tchk().lower
        ),
        // Vector for the window
        v -> (
          Undefined(TyVec(t, winSize)),
          VecShiftLeft(v, StmData(s)())().tchk().lower
        )
      )
    )().annotate(NoInputsAfterLastOut).annotateWithName(this.className)
    lowered.tchk()
  }
}

/** Similar to [[StmSlide]], but produces a nested stream rather than a stream
  * of vectors.
  */
case class StmSlideS(stm: Expr /* Stm<A; n> */, m: Expr /* Int */ )(
    typ: Type = Missing
) /* Stm<Stm<A; m>; n-m+1> */
    extends SyntaxSugar(stm, m)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(stm, m) => StmSlideS(stm, m)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newM = m.tchk(context).expectUInt()
    val newS = stm.tchk(context)
    newS.typ match {
      case TyStm(t, n) if t.isData =>
        this.rebuild(
          TyStm(TyStm(t, newM), SafeSum(n, C(-1)() * newM, 1)()),
          Seq(newS, newM)
        )
      case t =>
        throw new TypeError(
          s"Stream in StmSlideS has typ $t. Expected a stream."
        )
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    // It may be possible to optimize this version specifically by producing
    // elements while the shift register is still filling up
    requireType()
    val t = this.stm.typ.asInstanceOf[TyStm].t
    StmMap(StmSlide(stm, m)(), TyVec(t, m) ::+ (v => Vec2Stm(v)()))()
      .tchk()
      .lower
  }
}

/** Like [[StmSlide]], but with a defined initial value for the window and with
  * the output always being valid.
  */
case class StmSlideInit(s: Expr, z: Expr)(typ: Type = Missing)
    extends SyntaxSugar(s, z)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, z) => StmSlideInit(s, z)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val s = this.s.tchk(context)
    val (elemTyp, n) = s.typ match {
      case TyStm(TyData(t), n) => (t, n)
      case t =>
        throw new TypeError(
          s"Stream in $className has type $t. Expected a non-nested stream."
        )
    }
    val z = this.z.tchk(context)
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
      buf,
      True,
      Map[Param, (Expr, Expr)](
        p -> (s, True),
        buf -> (z, VecShiftLeft(buf, StmData(p)())().tchk().lower)
      )
    )().tchk()
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
) extends SyntaxSugar(stm, winHeight, winWidth)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, h, w) => StmSlide2D(s, h, w)(typ)
      case _            => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val stm = this.stm.tchk(context)
    val (t, n, m) = stm.typ match {
      case TyStm(TyStm(TyData(t), m), n) => (t, n, m)
      case t =>
        throw new TypeError(
          s"Stream in $className has type $t. Expected a 2D stream."
        )
    }
    val winHeight = this.winHeight.tchk(context).expectUInt()
    val winWidth = this.winWidth.tchk(context).expectUInt()
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
      U32 ::+ (_ => Default(elemTyp))
    )()
    val buf2d =
      VecSplit(VecConcat(VecAppend(buf, StmData(input)())(), zeros)(), m)()
    // Input counters, to know when buffer is full
    val maxCol = ToUnsigned(SafeSum(m, winWidth, -2)())().tchk().lower
    val maxRow = ToUnsigned(SafeSum(winHeight, -1)())().tchk().lower
    val row = Param("row")(winHeight.typ)
    val col = Param("col")(maxCol.typ)
    StmBuild(
      ToUnsigned(
        SafeProd(
          SafeSum(n, C(-1)() * winHeight, 1)(),
          SafeSum(m, C(-1)() * winWidth, 1)()
        )()
      )().tchk().lower,
      VecBuild(
        winHeight,
        winHeight.typ ::+ (i =>
          VecBuild(
            winWidth,
            winWidth.typ ::+ (j => VecAccess(VecAccess(buf2d, i)(), j)())
          )()
        )
      )().tchk().lower,
      ((row === maxRow) && (col < m)).tchk().lower,
      Map[Param, (Expr, Expr)](
        input -> (stm, True),
        buf -> (
          Undefined(buf.typ),
          VecShiftLeft(buf, StmData(input)())().tchk().lower
        ),
        row -> (
          C(0)(row.typ),
          Mux((col === maxCol) && (row !== maxRow), row + 1, row)()
            .tchk()
            .lower
        ),
        col -> (
          C(0)(col.typ),
          Mux(col === maxCol, Cast(winWidth - 1, col.typ)(), col + 1)()
            .tchk()
            .lower
        )
      )
    )().annotate(NoInputsAfterLastOut).annotateWithName(this.className).tchk()
  }
}

case class StmTranspose(stm: Expr /* Stm<Stm<A; m>; n> */ )(
    typ: Type = Missing
) /* Stm<Stm<A; n>; m> */
    extends SyntaxSugar(stm)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s) => StmTranspose(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newS = stm.tchk(context)
    newS.typ match {
      case TyStm(TyStm(t, m), n) if t.isData =>
        this.rebuild(TyStm(TyStm(t, n), m), Seq(newS))
      case t =>
        throw new TypeError(
          s"Stream in StmTranspose has type $t. Expected a two-dimensional stream."
        )
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val m = this.stm.typ.asInstanceOf[TyStm].t.asInstanceOf[TyStm].n
    val stm = this.stm.lower // flat stream
    val t = stm.typ.asInstanceOf[TyStm].t
    val n = stm.typ.asInstanceOf[TyStm].n
    StmMap(
      Stm2Vec(stm)(), // flat vector
      TyVec(t, n) ::+ (v =>
        Vec2Stm(VecJoin(VecTranspose(VecSplit(v, m)())())())()
      )
    )().tchk().lower
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
