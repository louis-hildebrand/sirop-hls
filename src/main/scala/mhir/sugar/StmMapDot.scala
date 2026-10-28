package mhir.sugar

import mhir.ir._
import mhir.sem.SemanticError
import mhir.typecheck._

case class StmMapDot(
    s1: Expr,
    s2: Expr,
    delay: Expr,
    cascade: Expr = True
)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s1, s2, delay, cascade)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): StmMapDot = {
    newChildren match {
      case Seq(s1, s2, delay, cascade) => StmMapDot(s1, s2, delay, cascade)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
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
              + s" Expected a stream of length $n.",
            TypeChecker.relevantBindingsE(constValues, n, n2)
          )
        }
        if (!c.sameLen(m, m2, constValues)) {
          throw new TypeError(
            s"Second stream in $className contains vectors of length $m2."
              + s" Expected vectors of length $m.",
            TypeChecker.relevantBindingsE(constValues, m, m2)
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
    val cascade = this.cascade.tchk(context, constValues).expectBool()
    val outElemTyp = (elemTyp1, elemTyp2) match {
      case (_: TyUInt, _: TyUInt) => U44
      case _                      => I44
      // TODO: What if the operands don't fit in u44/i44?
      //       Or what if we're not targeting an Agilex 7 device and therefore 44 bits is not applicable?
    }
    if (!ReshapeData.canReshape(elemTyp1, outElemTyp, constValues)) {
      throw new TypeError(
        s"Elements of type $elemTyp1 in first stream cannot be reshaped to $outElemTyp.",
        TypeChecker.relevantBindings(constValues, elemTyp1, outElemTyp)
      )
    }
    if (!ReshapeData.canReshape(elemTyp2, outElemTyp, constValues)) {
      throw new TypeError(
        s"Elements of type $elemTyp2 in second stream cannot be reshaped to $outElemTyp.",
        TypeChecker.relevantBindings(constValues, elemTyp2, outElemTyp)
      )
    }
    this.rebuild(TyStm(outElemTyp, n), Seq(s1, s2, delay, cascade))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    val cascadeExpr = if (this.cascade.freeVars.isEmpty) {
      mhir.eval.eval(this.cascade)
    } else {
      this.cascade
    }
    val cascade = cascadeExpr match {
      case False => false
      case True  => true
      case e =>
        throw SemanticError(s"value for parameter 'cascade' is not static: $e")
    }
    if (cascade) {
      StmMapDotCascaded(
        StmCascade(this.s1)(),
        StmCascade(this.s2)(),
        this.delay
      )().tchk().lower
    } else {
      StmMapDotTree(this.s1, this.s2, this.delay)().tchk().lower
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
              + s" Expected a stream of length $n.",
            TypeChecker.relevantBindingsE(constValues, n, n2)
          )
        }
        if (!c.sameLen(m, m2, constValues)) {
          throw new TypeError(
            s"Second stream in $className contains vectors of length $m2."
              + s" Expected vectors of length $m.",
            TypeChecker.relevantBindingsE(constValues, m, m2)
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
        s"Elements of type $elemTyp1 in first stream cannot be reshaped to $outElemTyp.",
        TypeChecker.relevantBindings(constValues, elemTyp1, outElemTyp)
      )
    }
    if (!ReshapeData.canReshape(elemTyp2, outElemTyp, constValues)) {
      throw new TypeError(
        s"Elements of type $elemTyp2 in second stream cannot be reshaped to $outElemTyp.",
        TypeChecker.relevantBindings(constValues, elemTyp2, outElemTyp)
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
        ).tchk()
        val s2Extended = Call(
          Param("StmExtendBy", -1)(Missing),
          Seq(),
          Seq(s2, delay)
        ).tchk()
        // Instruct the optimizer not to delete these shift registers
        val sink = Tuple(
          pipe1Vars.map(v => VecAccess(v, SmartDiff(delay, C(1)())())()) ++
            pipe2Vars.map(v => VecAccess(v, SmartDiff(delay, C(1)())())()): _*
        )().tchk().lower
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
            )()
              .annotateWithName("StmMapDotCascaded")
              .annotate(SinkAnnotation(sink)),
            totDelay
          )
        ).tchk().lower
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

case class StmMapDotTree(
    s1: Expr,
    s2: Expr,
    delay: Expr
)(typ: Type = Missing)
    extends ResolvedSyntaxSugar(s1, s2, delay)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s1, s2, d) => StmMapDotTree(s1, s2, d)(typ)
      case _              => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
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
              + s" Expected a stream of length $n.",
            TypeChecker.relevantBindingsE(constValues, n, n2)
          )
        }
        if (!c.sameLen(m, m2, constValues)) {
          throw new TypeError(
            s"Second stream in $className contains vectors of length $m2."
              + s" Expected vectors of length $m.",
            TypeChecker.relevantBindingsE(constValues, m, m2)
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
        s"Elements of type $elemTyp1 in first stream cannot be reshaped to $outElemTyp.",
        TypeChecker.relevantBindings(constValues, elemTyp1, outElemTyp)
      )
    }
    if (!ReshapeData.canReshape(elemTyp2, outElemTyp, constValues)) {
      throw new TypeError(
        s"Elements of type $elemTyp2 in second stream cannot be reshaped to $outElemTyp.",
        TypeChecker.relevantBindings(constValues, elemTyp2, outElemTyp)
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
    val TyStm(TyVec(int1, m1), _) = this.s1.typ
    val TyStm(TyVec(int2, m2), _) = this.s2.typ
    val TyStm(intOut, _) = this.typ
    Call(
      Param("StmMap2", -1)(Missing),
      Seq(),
      Seq(
        StmDelay(this.s1, this.delay)(),
        StmDelay(this.s2, this.delay)(), {
          val v1 = Param("v1")(TyVec(int1, m1))
          val v2 = Param("v2")(TyVec(int2, m2))
          Function(
            v1,
            Function(
              v2,
              VecFold(
                VecMap2(
                  v1,
                  v2, {
                    val x = Param("x")(int1)
                    val y = Param("y")(int2)
                    Function(
                      x,
                      Function(
                        y,
                        SmartProd(
                          ReshapeData(x, intOut)(),
                          ReshapeData(y, intOut)()
                        )()
                      )()
                    )()
                  }
                )(),
                C(0)(intOut), {
                  val x = Param("x")(TyTuple(intOut, intOut))
                  Function(x, Sum(x.__0, x.__1)())()
                }
              )()
            )()
          )()
        }
      )
    ).tchk().lower
  }
}
