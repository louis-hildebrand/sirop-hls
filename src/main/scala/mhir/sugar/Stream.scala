package mhir.sugar

import mhir.ir.Lowering.{ExprLowering, TypeLowering}
import mhir.ir.StreamFuser.StreamFusion
import mhir.ir._
import mhir.ir.typecheck.{TypeCheck, TypeError}
import mhir.sugar.Streamifier.Streamify

import scala.annotation.tailrec

object AsFusedStm2Stm {
  def apply(f: Function): (Param, StmBuild) = {
    val f1 = f.lower().streamify().asInstanceOf[Function]
    // It is essential to fuse everything.
    // If f is a chain of stream producers, we want to reset them all, not
    // just the last producer.
    (f1.param, f1.body.asInstanceOf[StmBuild].fuseCompletely())
  }
}

object AsFusedStm2Stm2Stm {
  def apply(f: Function): (Param, Param, StmBuild) = {
    val (x1, x2, stm) = f.lower().streamify() match {
      case Function(x1, Function(x2, stm: StmBuild)) => (x1, x2, stm)
      case _ =>
        ???
    }
    // It is essential to fuse everything.
    // If f is a chain of stream producers, we want to reset them all, not
    // just the last producer.
    (x1, x2, stm.fuseCompletely())
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val n = this.n.tchk.expectUInt()
    val z = this.z.tchk
    val f = this.f.tchk.expectType(z.typ ->: z.typ)
    this.rebuild(TyStm(z.typ, 1), Seq(n, z, f))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = this.n.lower()
    val z = this.z.lower()
    val f = this.f.lower()
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
    )().tchk().lower()
  }
}

case class StmCst(n: Expr, c: Expr)(typ: Type = Missing)
    extends SyntaxSugar(n, c)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n, c) => StmCst(n, c)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newN = n.tchk.expectUInt()
    val newC = c.tchk
    this.rebuild(TyStm(newC.typ, newN), Seq(newN, newC))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = this.n.lower()
    val c = this.c.lower()
    val out = c.typ match {
      case _: TyStm => StmRepeat(c, n)()
      case _        => StmBuild(n, c, True)()
    }
    out.tchk().lower()
  }
}

case class StmCount(n: Expr)(typ: Type = Missing) extends SyntaxSugar(n)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n) => StmCount(n)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newN = n.tchk.expectUInt()
    this.rebuild(TyStm(newN.typ, newN), Seq(newN))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = this.n.lower()
    StmRange(n, IntCst(0)(n.typ), IntCst(1)(n.typ))().tchk().lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newN = n.tchk.expectUInt()
    val newZ = z.tchk.expectAnyInt()
    val newDelta = delta.tchk.expectType(newZ.typ)
    this.rebuild(TyStm(newZ.typ, newN), Seq(newN, newZ, newDelta))
  }

  override def lowerSyntaxSugar(): Expr = {
    val n = this.n.lower()
    val z = this.z.lower()
    val delta = this.delta.lower()
    val a = Param("a")(z.typ)
    StmBuild(
      n,
      a,
      True,
      Map[Param, (Expr, Expr)](a -> (z, a + delta))
    )().tchk().lower()
  }
}

// TODO: What if c is a stream?
case class StmCst2D(
    n: Expr /* Int */,
    m: Expr /* Int */,
    c: Expr /* T */
)(typ: Type = Missing) /* Stm<Stm<T; m>; n> */
    extends SyntaxSugar(n, m, c)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n, m, c) => StmCst2D(n, m, c)(typ)
      case _            => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newN = n.tchk.expectUInt()
    val newM = m.tchk.expectUInt()
    val newC = c.tchk
    this.rebuild(TyStm(TyStm(newC.typ, newM), newN), Seq(newN, newM, newC))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = this.n.lower()
    val m = this.m.lower()
    val c = this.c.lower()
    StmBuild(SafeProd(n, m)(), c, True)().tchk().lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val n = this.n.tchk(context).expectUInt()
    val m = this.m.tchk(context).expectUInt()
    this.rebuild(TyStm(TyStm((n.typ, m.typ), m), n), Seq(n, m))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = this.n.lower()
    val m = this.m.lower()
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
    )().tchk().lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val n1 = this.n1.tchk.expectUInt()
    val n2 = this.n2.tchk.expectUInt()
    val n3 = this.n3.tchk.expectUInt()
    this.rebuild(
      TyStm(TyStm(TyStm((n1.typ, n2.typ, n3.typ), n3), n2), n1),
      Seq(n1, n2, n3)
    )
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n1 = this.n1.lower()
    val n2 = this.n2.lower()
    val n3 = this.n3.lower()
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
          Mux((k === -1 + n3) && (j === -1 + n2), i + 1, i)()
        ),
        j -> (
          C(0)(j.typ),
          Mux(
            (k === -1 + n3) && (j === -1 + n2),
            C(0)(j.typ),
            Mux(k === -1 + n3, j + 1, j)()
          )()
        ),
        k -> (
          C(0)(k.typ),
          Mux(k === -1 + n3, C(0)(k.typ), k + 1)()
        )
      )
    )().tchk().lower()
  }
}

case class StmMap(
    input: Expr /* Stm<A; n> */,
    f: Function /* A -> B */
)(typ: Type = Missing) /* Stm<B; n> */
    extends SyntaxSugar(input, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, f: Function) => StmMap(s, f)(typ)
      case _                   => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newS = input.tchk(context)
    val (t1, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t           => throw new TypeError(s"Stream in StmMap has type $t.")
    }
    val newF = {
      val withAnnotatedInput = f.param.typ match {
        case Missing =>
          val x = f.param.rebuild(t1).asInstanceOf[Param]
          Function(x, f.body)()
        case _ => f
      }
      withAnnotatedInput.tchk(context)
    }
    val t2 = newF.typ match {
      case TyArrow(t, t2) if t ~= t1 => t2
      case t =>
        throw new TypeError(
          s"Function in StmMap has type $t. Expected a function whose input type is $t1."
        )
    }
    this.rebuild(TyStm(t2, n), Seq(newS, newF))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val input = this.input.lower()
    val f = this.f.lower().asInstanceOf[Function]
    val n = this.typ.asInstanceOf[TyStm].n
    // Instantiate `f` as a function from stream to stream
    val (s, innerStm) = AsFusedStm2Stm(f)
    assert(innerStm.typ.isInstanceOf[TyStm], "innerStm should be a stream")
    assert(
      innerStm.seedByVar.count({ case (_, z) => z == s }) <= 1,
      "the input stream should appear no more than once in the inner StmBuild"
    )
    val usesInputStream = innerStm.seedByVar.values.exists(z => z == s)
    val map = if (!usesInputStream) {
      // In theory you could have something like
      //     StmMap(StmCount2D(...), _ => StmCst(1, 42))
      // which doesn't actually use the input stream at all.
      // In this case, there's no need for resetting the stream and all that.
      StmRepeat(innerStm, n)()
    } else {
      // How many elements will the inner component read and produce before it must be reset?
      val inputsUntilReset = f.typ.asInstanceOf[TyArrow].t1 match {
        case TyStm(_, n) => n
        case _           => IntCst(1)()
      }
      val outputsUntilReset = f.typ.asInstanceOf[TyArrow].t2 match {
        case TyStm(_, n) => n
        case _           => IntCst(1)()
      }
      val map = n match {
        // TODO: Why this special case? Is it just to make the resulting stream
        //       easier to simplify? Should I add something similar in the
        //       partial evaluator?
        case IntCst(1) =>
          // No need to reset
          innerStm
        case n =>
          val inCtr = Param("in_ctr")(U32)
          val outCtr = Param("out_ctr")(U32)
          val innerWithInCtr = inputsUntilReset match {
            case IntCst(1) =>
              // If there's only one input, then there's no need for an input
              // counter.
              // (The input counter is hard to remove in the case of a function
              // from non-stream to stream.)
              // Either
              //  (1) The input will be read *after* all outputs are produced,
              //      but this means the input stream is actually unused.
              //  (2) All inputs will be read no later than the last output.
              // Add the input counter anyway just to keep the rest of the
              // lowering pass simple, but it should now be trivial for the
              // optimizer to recognize that it is constant.
              innerStm.addAccumulator(inCtr, IntCst(1)(U32), IntCst(1)(U32))
            case _ =>
              val inStmVar =
                innerStm.seedByVar.find({ case (_, z) => z == s }).get._1
              innerStm.addInputCounter(inStmVar, inCtr)
          }
          val innerWithCtrs = innerWithInCtr.addOutputCounter(outCtr)
          // Want to reset depending on the *next* values of the in/out counters.
          val shouldReset = (
            (innerWithCtrs.nextByVar(inCtr) === inputsUntilReset)
              && (innerWithCtrs.nextByVar(outCtr) === outputsUntilReset)
          ).tchk()
          val outerStm = StmBuild(
            SafeProd(n, outputsUntilReset)(),
            innerWithCtrs.data,
            innerWithCtrs.valid,
            innerWithCtrs.equations.map({
              case (x, (z, ready)) if z == s =>
                // Never reset the primary input stream
                x -> (z, ready)
              case (x, (z, ready)) if x.typ.isInstanceOf[TyStm] =>
                // Repeat other input streams to give the illusion that they
                // are being reset
                x -> (StmJoin(StmRepeat(z, n)())(), ready)
              case (x, (z, next)) =>
                // Reset data accumulators
                x -> (z, Mux(shouldReset, z, next)())
            })
          )()
          outerStm
      }
      val ret = map.subPreserveType(s -> input)
      val originalFreeVars =
        input.freeVars() ++ f.freeVars() ++ n.freeVars()
      assert(
        ret.freeVars() == originalFreeVars,
        s"the set of free variables should be unchanged by StmMap (expected $originalFreeVars, got ${ret.freeVars()})"
      )
      ret
    }
    map.tchk().lower()
  }
}

case class StmMap2(s1: Expr, s2: Expr, f: Function)(typ: Type = Missing)
    extends SyntaxSugar(s1, s2, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s1, s2, f: Function) => StmMap2(s1, s2, f)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val s1 = this.s1.tchk
    val (t1, n1) = s1.typ match {
      case TyStm(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"First stream in $className has type $t."
            + " Expected a stream."
        )
    }
    val s2 = this.s2.tchk
    val (t2, n2) = s2.typ match {
      case TyStm(t, n) => (t, n)
      case t =>
        throw new TypeError(
          s"Second stream in $className has type $t."
            + " Expected a stream."
        )
    }
    if (!Type.sameLen(n1, n2)) {
      throw new TypeError(
        s"Stream lengths in $className do not match: $n1 and $n2."
      )
    }
    val f = {
      val withTypeAnnotations = this.f match {
        case Function(x, body) =>
          val newX = if (x.hasType) x else x.rebuild(t1).asInstanceOf[Param]
          val newBody = body match {
            case Function(y, body) =>
              val newY = if (y.hasType) y else y.rebuild(t2).asInstanceOf[Param]
              Function(newY, body)()
            case f => f
          }
          Function(newX, newBody)()
      }
      withTypeAnnotations.tchk.asInstanceOf[Function]
    }
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

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val s1 = this.s1.lower()
    val s2 = this.s2.lower()
    val f = this.f.lower().asInstanceOf[Function]
    val n = this.typ.asInstanceOf[TyStm].n
    // Instantiate `f` as a function from stream to stream
    val (s1Param, s2Param, innerStm) = AsFusedStm2Stm2Stm(f)
    assert(innerStm.typ.isInstanceOf[TyStm], "innerStm should be a stream")
    assert(
      innerStm.seedByVar.count({ case (_, z) => z == s1Param }) <= 1,
      "the first input stream should appear no more than once in the inner StmBuild"
    )
    assert(
      innerStm.seedByVar.count({ case (_, z) => z == s2Param }) <= 1,
      "the second input stream should appear no more than once in the inner StmBuild"
    )
    val map = {
      // How many elements will the inner component read and produce before it must be reset?
      val (t1, t2, t3) = f.typ match {
        case TyArrow(t1, TyArrow(t2, t3)) => (t1, t2, t3)
        case _ =>
          ???
      }
      val inputsFrom1UntilReset = t1 match {
        case TyStm(_, n) => n
        case _           => C(1)().tchk()
      }
      val inputsFrom2UntilReset = t2 match {
        case TyStm(_, n) => n
        case _           => C(1)().tchk()
      }
      val outputsUntilReset = t3 match {
        case TyStm(_, n) => n
        case _           => IntCst(1)()
      }
      val map = {
        val in1Ctr = Param("in1_ctr")(U32)
        val in2Ctr = Param("in2_ctr")(U32)
        val outCtr = Param("out_ctr")(U32)
        val innerWithIn1Ctr = inputsFrom1UntilReset match {
          case IntCst(1) =>
            // If there's only one input, then there's no need for an input
            // counter.
            // (The input counter is hard to remove in the case of a function
            // from non-stream to stream.)
            // Either
            //  (1) The input will be read *after* all outputs are produced,
            //      but this means the input stream is actually unused.
            //  (2) All inputs will be read no later than the last output.
            // Add the input counter anyway just to keep the rest of the
            // lowering pass simple, but it should now be trivial for the
            // optimizer to recognize that it is constant.
            innerStm.addAccumulator(in1Ctr, C(1)(U32), C(1)(U32))
          case _ =>
            innerStm.seedByVar
              .find({ case (_, z) => z == s1Param })
              .map(_._1) match {
              case Some(inStmVar) =>
                innerStm.addInputCounter(inStmVar, in1Ctr)
              case None =>
                // The first input is unused!
                val k = ReshapeData(inputsFrom1UntilReset, U32)().tchk().lower()
                innerStm.addAccumulator(in1Ctr, k, k)
            }
        }
        val innerWithIn2Ctr = inputsFrom2UntilReset match {
          case IntCst(1) =>
            // If there's only one input, then there's no need for an input
            // counter.
            // (The input counter is hard to remove in the case of a function
            // from non-stream to stream.)
            // Either
            //  (1) The input will be read *after* all outputs are produced,
            //      but this means the input stream is actually unused.
            //  (2) All inputs will be read no later than the last output.
            // Add the input counter anyway just to keep the rest of the
            // lowering pass simple, but it should now be trivial for the
            // optimizer to recognize that it is constant.
            innerWithIn1Ctr.addAccumulator(in2Ctr, C(1)(U32), C(1)(U32))
          case _ =>
            innerWithIn1Ctr.seedByVar
              .find({ case (_, z) => z == s2Param })
              .map(_._1) match {
              case Some(inStmVar) =>
                innerWithIn1Ctr.addInputCounter(inStmVar, in2Ctr)
              case None =>
                // The second input is unused!
                val k = ReshapeData(inputsFrom2UntilReset, U32)().tchk().lower()
                innerWithIn1Ctr.addAccumulator(in2Ctr, k, k)
            }
        }
        val innerWithCtrs = innerWithIn2Ctr.addOutputCounter(outCtr)
        // Want to reset depending on the *next* values of the in/out counters.
        val shouldReset = (
          (innerWithCtrs.nextByVar(in1Ctr) === inputsFrom1UntilReset)
            && (innerWithCtrs.nextByVar(in2Ctr) === inputsFrom2UntilReset)
            && (innerWithCtrs.nextByVar(outCtr) === outputsUntilReset)
        ).tchk()
        val outerStm = StmBuild(
          SafeProd(n, outputsUntilReset)(),
          innerWithCtrs.data,
          innerWithCtrs.valid,
          innerWithCtrs.equations.map({
            case (x, (z, ready)) if z == s1Param || z == s2Param =>
              // Never reset the primary input streams
              x -> (z, ready)
            case (x, (z, ready)) if x.typ.isInstanceOf[TyStm] =>
              // Repeat other input streams to give the illusion that they
              // are being reset
              x -> (StmJoin(StmRepeat(z, n)())(), ready)
            case (x, (z, next)) =>
              // Reset data accumulators
              x -> (z, Mux(shouldReset, z, next)())
          })
        )()
        outerStm
      }
      val ret =
        map.subPreserveType(Map[Expr, Expr](s1Param -> s1, s2Param -> s2))
      val originalFreeVars =
        s1.freeVars() ++ s2.freeVars() ++ f.freeVars() ++ n.freeVars()
      assert(
        ret.freeVars() == originalFreeVars,
        s"the set of free variables should be unchanged by $className (expected $originalFreeVars, got ${ret.freeVars()})"
      )
      ret
    }
    map.tchk().lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val s = this.stm.tchk.expectStream()
    val t = s.typ.asInstanceOf[TyStm].t
    val k = this.k.tchk.expectUInt()
    this.rebuild(TyStm(t, 1), Seq(s, k))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val stm = this.stm.lower()
    val k = this.k.lower()
    val perRow = this.stm.typ.asInstanceOf[TyStm].t.lower match {
      case TyStm(_, n) => n
      case _           => IntCst(1)(U32)
    }
    val s = Param("s")(stm.typ) // input stream
    val i = Param("i")(U32) // index of current row
    val j = Param("j")(U32) // index within row
    StmBuild(
      perRow,
      StmData(s)(),
      i === k,
      Map[Param, (Expr, Expr)](
        s -> (stm, True),
        i -> (C(0)(U32), Mux(j + 1 === perRow, i + 1, i)()),
        j -> (C(0)(U32), Mux(j + 1 === perRow, C(0)(U32), j + 1)())
      )
    )().tchk().lower()
  }
}

case class StmFold(
    stream: Expr /* Stm<A; n> */,
    z: Expr /* B */,
    f: Function /* B -> A -> B */
)(typ: Type = Missing) /* Stm<B; 1> */
    extends SyntaxSugar(stream, z, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, z, f: Function) => StmFold(s, z, f)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newS = stream.tchk(context)
    val t1 = newS.typ match {
      case TyStm(t, _) => t
      case t           => throw new TypeError(s"Stream in StmFold has type $t.")
    }
    val newZ = z.tchk(context)
    val t2 = newZ.typ
    val newF = f.tchk(context)
    newF.typ match {
      case TyArrow(t3, TyArrow(t4, t5))
          if (t3 ~= t2) && (t4 ~= t1) && (t5 ~= t2) =>
        ()
      case TyArrow(t3, TyArrow(t4, TyStm(t5, IntCst(1))))
          if (t3 ~= t2) && (t4 ~= t1) && (t5 ~= t2) =>
        ()
      case t =>
        throw new TypeError(
          s"Function in $className has type $t. Expected ${t2 ->: t1 ->: t2}."
        )
    }
    this.rebuild(TyStm(t2, 1), Seq(newS, newZ, newF))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    StmSuffix(StmScanInclusive(stream, z, f)(), 1)().tchk().lower()
  }
}

// TODO: Add special cases for n = 0 and n = 1, like in StmMap?
//       Or better yet, define an operator like StmReset that works for both cases?
case class StmScanInclusive(
    input: Expr /* Stm<A; n> */,
    z: Expr /* B */,
    f: Function /* B -> A -> B */
)(typ: Type = Missing) /* Stm<B; n> */
    extends SyntaxSugar(input, z, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, z, f: Function) => StmScanInclusive(s, z, f)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newS = input.tchk(context)
    val (t1, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmScanInclusive has type $t.")
    }
    val newZ = z.tchk(context)
    val t2 = newZ.typ
    val newF = f.tchk(context)
    newF.typ match {
      case TyArrow(t3, TyArrow(t4, t5))
          if (t3 ~= t2) && (t4 ~= t1) && (t5 ~= t2) =>
        ()
      case TyArrow(t3, TyArrow(t4, TyStm(t5, IntCst(1))))
          if (t3 ~= t2) && (t4 ~= t1) && (t5 ~= t2) =>
        ()
      case t =>
        throw new TypeError(
          s"Function in StmScanInclusive has type $t. Expected ${TyArrow(t2, TyArrow(t1, t2))}."
        )
    }
    this.rebuild(TyStm(t2, n), Seq(newS, newZ, newF))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val input = this.input.lower()
    val z = this.z.lower()
    // IMPORTANT: use the original input here, not the flattened version.
    // If we're scanning over a Stm[Stm[T, m], n], we will produce a stream of
    // length n, not a stream of length n * m.
    val n = this.input.typ.asInstanceOf[TyStm].n
    // IMPORTANT: use the original (possibly nested) stream here, but flatten
    // the inner dimensions.
    // If we're scanning over a Stm[Stm[Stm[T, k], m], n], then it takes k * m
    // steps per scan output, not just m.
    val inputsUntilReset = this.input.typ.asInstanceOf[TyStm].t.lower match {
      case TyStm(_, n) => n
      case _           => IntCst(1)()
    }
    val elemType = z.typ
    // TODO: Enforce the restriction that the accumulator cannot contain any streams?
    val (s, innerStm) =
      AsFusedStm2Stm(
        // The function has the form (acc) => (elem) => newAcc.
        // Take just the (elem) => newAcc part here, with acc being a free variable.
        // Later, replace that free variable with the appropriate element in the StmScan accumulator.
        f.body.asInstanceOf[Function]
      )
    assert(
      innerStm.n == IntCst(1)(),
      "the function in StmScan should return a scalar or a stream of length 1"
    )
    assert(
      innerStm.seedByVar.count({ case (_, z) => z == s }) <= 1,
      "the input stream should appear at most once in the inner StmBuild"
    )
    val (innerWithCtrs, shouldReset) = {
      val outputsUntilReset = IntCst(1)()
      val outCtr = Param("out_ctr")(U32)
      val withOutCtr = innerStm.addOutputCounter(outCtr)
      val usesInputStream = innerStm.seedByVar.exists({ case (_, z) => z == s })
      if (usesInputStream) {
        val inCtr = Param("in_ctr")(U32)
        val withCtrs = withOutCtr
          .addInputCounter(
            innerStm.seedByVar.find({ case (_, z) => z == s }).get._1,
            inCtr
          )
        // Want to reset depending on the *next* values of the in/out counters.
        val shouldReset =
          (withCtrs.nextByVar(inCtr) === inputsUntilReset
            && withCtrs.nextByVar(outCtr) === outputsUntilReset)
        (withCtrs, shouldReset)
      } else {
        val shouldReset = withOutCtr.nextByVar(outCtr) === outputsUntilReset
        (withOutCtr, shouldReset)
      }
    }
    val acc = Param("acc")(elemType)
    val nextAcc = Mux(innerWithCtrs.valid, innerWithCtrs.data, acc)()
    val outerStm = StmBuild(
      n,
      nextAcc,
      shouldReset,
      innerWithCtrs.equations.map({
        case (x, (z, ready)) if z == s =>
          // Never reset the primary input stream
          x -> (z, ready)
        case (x, (z, ready)) if x.typ.isInstanceOf[TyStm] =>
          // Repeat other input streams to give the illusion that they
          // are being reset
          x -> (StmJoin(StmRepeat(z, n)())(), ready)
        case (x, (z, next)) =>
          // Reset data accumulators
          x -> (z, Mux(shouldReset, z, next)())
      }) + (acc -> (z, nextAcc))
    )()
    assert(
      !outerStm.n.contains(s)
        && !outerStm.data.contains(s)
        && !outerStm.valid.contains(s)
        && !outerStm.nextByVar.values.toSeq.exists(nxt => nxt.contains(s)),
      "the input stream must only occur in the seed of the StmBuild"
    )
    // It is safe to do this "naive" substitution because
    //  (1) `s` definitely only occurs in the seed (where it is free), so the
    //      naive substitution behaves like the usual one
    //  (2) we want to replace f.param with the *bound* variable acc, so alpha
    //      renaming is NOT what we want here
    val scan = outerStm.rebuildAndEraseType(
      outerStm.children.map(e =>
        e.subPreserveType(Map[Expr, Expr](s -> input, f.param -> acc))
      )
    )
    val originalFreeVars =
      input.freeVars() ++ z.freeVars() ++ f.freeVars() ++ n.freeVars()
    assert(
      scan.freeVars() == originalFreeVars,
      s"the set of free variables should be unchanged by StmScan (expected $originalFreeVars but got ${scan.freeVars()})"
    )
    scan.tchk().lower()
  }
}

case class StmScanExclusive(
    stm: Expr /* Stm<A; n> */,
    z: Expr /* B */,
    f: Function /* B -> A -> B */
)(typ: Type = Missing) /* Stm<B; n> */
    extends SyntaxSugar(stm, z, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, z, f: Function) => StmScanExclusive(s, z, f)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newS = stm.tchk(context)
    val (t1, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmScanExclusive has type $t.")
    }
    val newZ = z.tchk(context)
    val t2 = newZ.typ
    val newF = f.tchk(context)
    newF.typ match {
      case TyArrow(t3, TyArrow(t4, t5))
          if (t3 ~= t2) && (t4 ~= t1) && (t5 ~= t2) =>
        ()
      case TyArrow(t3, TyArrow(t4, TyStm(t5, IntCst(1))))
          if (t3 ~= t2) && (t4 ~= t1) && (t5 ~= t2) =>
        ()
      case t =>
        throw new TypeError(
          s"Function in StmScanExclusive has type $t. Expected ${TyArrow(t2, TyArrow(t1, t2))}."
        )
    }
    this.rebuild(TyStm(t2, n), Seq(newS, newZ, newF))
  }

  override def lowerSyntaxSugar(): Expr = {
    // Maybe it would be better to first take prefix of input stream, scan,
    // and then prepend
    StmShiftRight(StmScanInclusive(stm, z, f)(), z)().tchk().lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val s = this.s.tchk
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
    val annotatedF = this.f match {
      case Function(x, body) if !x.hasType =>
        val newX = x.rebuild(tupledTyp).asInstanceOf[Param]
        Function(newX, body)()
      case f =>
        f
    }
    val f = annotatedF.tchk.expectType(tupledTyp ->: wrappedTyp)
    this.rebuild(TyStm(wrappedTyp, 1), Seq(s, f))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val s = this.s.lower()
    val wrappedTyp = this.typ.asInstanceOf[TyStm].t
    val f = unwrapFunc(wrappedTyp, this.f).lower()
    val elemTyp = unwrapTyp(wrappedTyp, this.f).lower
    val n = this.s.typ.asInstanceOf[TyStm].n
    val acc = Param("acc")(elemTyp)
    val t = Param("t")(U32)
    val sAcc = Param("s")(s.typ)
    val sData = unwrapElem(wrappedTyp, this.f, StmData(sAcc)())
    val firstStep = Param("first_step")(TyBool)
    StmBuild(
      1,
      wrapResult(wrappedTyp, this.f, f(Tuple(acc, sData)())),
      t + 1 === n,
      Map[Param, (Expr, Expr)](
        firstStep -> (True, False),
        t -> (C(0)(U32), C(1)(U32) + t),
        sAcc -> (s, True),
        acc -> (
          Default(elemTyp),
          Mux(firstStep, sData, f(Tuple(acc, sData)()))()
        )
      )
    )().tchk().lower()
  }

  private def tupleElemType(wrappedTyp: Type, f: Expr): Type = {
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newV = v.tchk(context)
    newV.typ match {
      case TyVec(t, n) =>
        this.rebuild(TyStm(t, n), Seq(newV))
      case t => throw new TypeError(s"Vector in Vec2Stm has type $t.")
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val v = this.v.lower()
    v.typ match {
      case TyVec(_, n) =>
        // Alternatively, you could implement Vec2Stm using a shift register
        val i = Param("i")(U32)
        StmBuild(
          n,
          VecAccess(v, i)(),
          True,
          Map[Param, (Expr, Expr)](i -> (C(0)(U32), i + 1))
        )().tchk().lower()
      case TyStm(tv: TyVec, _) =>
        StmMap(v, tv ::+ (v => Vec2Stm(v)()))().tchk().lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newS = stm.tchk(context)
    val (t, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmPrepend has type $t.")
    }
    val newE = e.tchk(context).expectType(t)
    this.rebuild(TyStm(t, n + 1), Seq(newS, newE))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    StmConcat(StmCst(1, e)(), stm)().tchk().lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newS = stm.tchk(context)
    val (t, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmAppend has type $t.")
    }
    val newE = e.tchk(context).expectType(t)
    this.rebuild(TyStm(t, n + 1), Seq(newS, newE))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    StmConcat(stm, StmCst(1, e)())().tchk().lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val k = this.k.tchk.expectUInt()
    val s = this.stm.tchk
    s.typ match {
      case TyStm(t, _) =>
        this.rebuild(TyStm(t, k), Seq(s, k))
      case t => throw new TypeError(s"Stream in StmPrefix has type $t.")
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val stm = this.stm.lower()
    val k = this.k.lower()
    val perRow = this.stm.typ.asInstanceOf[TyStm].t.lower match {
      case TyStm(_, n) => n
      case _           => IntCst(1)()
    }
    val s = Param("s")(stm.typ) // input stream
    val i = Param("i")(U32) // index of current row
    val j = Param("j")(U32) // index within row
    StmBuild(
      SafeProd(k, perRow)(),
      StmData(s)(),
      i < k,
      Map[Param, (Expr, Expr)](
        s -> (stm, True),
        i -> (C(0)(U32), Mux(j === perRow - 1, i + 1, i)()),
        j -> (C(0)(U32), Mux(j === perRow - 1, C(0)(U32), j + 1)())
      )
    )().tchk().lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newK = k.tchk.expectUInt()
    val newS = stm.tchk
    newS.typ match {
      case TyStm(t, _) =>
        this.rebuild(TyStm(t, newK), Seq(newS, newK))
      case t => throw new TypeError(s"Stream in StmPrefix has type $t.")
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val stm = this.stm.lower()
    val k = this.k.lower()
    val n = this.stm.typ.asInstanceOf[TyStm].n
    val perRow = this.stm.typ.asInstanceOf[TyStm].t.lower match {
      case TyStm(_, n) => n
      case _           => IntCst(1)()
    }
    val s = Param("s")(stm.typ) // input stream
    val i = Param("i")(U32) // index of current row
    val j = Param("j")(U32) // index within row
    StmBuild(
      k * perRow,
      StmData(s)(),
      i >= n - k,
      Map[Param, (Expr, Expr)](
        s -> (stm, True),
        i -> (C(0)(U32), Mux(j === perRow - 1, i + 1, i)()),
        j -> (C(0)(U32), Mux(j === perRow - 1, C(0)(U32), j + 1)())
      )
    )().tchk().lower()
  }
}

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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newS = stm.tchk(context)
    val (t, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmShiftLeft has type $t.")
    }
    val newE = e.tchk(context).expectType(t)
    this.rebuild(TyStm(t, n), Seq(newS, newE))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = this.typ.asInstanceOf[TyStm].n
    StmAppend(StmSuffix(stm, ToUnsigned(n - 1)())(), e)().tchk().lower()
  }
}

case class StmShiftRight(stm: Expr /* Stm<A; n> */, e: Expr /* A */ )(
    typ: Type = Missing
) /* Stm<A; n> */
    extends SyntaxSugar(stm, e)(typ) {
  def apply(
      stm: Expr /* Stm<A; n> */,
      e: Expr /* A */,
      // Ideally we would get this shape information from the type system
      stmShape: Seq[Expr]
  ): Expr /* Stm<A; n> */ = {
    val n = stmShape.head
    StmPrepend(StmPrefix(stm, n - 1)(), e)()
  }

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, e) => StmShiftRight(s, e)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newS = stm.tchk(context)
    val (t, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmShiftRight has type $t.")
    }
    val newE = e.tchk(context).expectType(t)
    this.rebuild(TyStm(t, n), Seq(newS, newE))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = this.typ.asInstanceOf[TyStm].n
    StmPrepend(StmPrefix(stm, ToUnsigned(n - 1)())(), e)().tchk().lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newS1 = stm1.tchk
    val (t1, n1) = newS1.typ match {
      case TyStm(t, n1) => (t, n1)
      case t =>
        throw new TypeError(
          s"First input in StmConcat has type $t. Expected a stream."
        )
    }
    val newS2 = stm2.tchk
    val n2 = newS2.typ match {
      case TyStm(t2, n2) if t2 ~= t1 => n2
      case t =>
        throw new TypeError(
          s"Second input in StmConcat has type $t. Expected a stream of $t1."
        )
    }
    this.rebuild(TyStm(t1, n1 + n2), Seq(newS1, newS2))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val stm1 = this.stm1.lower()
    val stm2 = this.stm2.lower()
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
    )().tchk().lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
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

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val t1 = this.a.typ.asInstanceOf[TyStm].t
    val t2 = this.b.typ.asInstanceOf[TyStm].t
    StmMap2(this.a, this.b, t1 ::+ (x => t2 ::+ (y => Tuple(x, y)())))()
      .tchk()
      .lower()
  }
}

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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newM = m.tchk.expectUInt()
    val newS = stm.tchk
    newS.typ match {
      case TyStm(t, n) =>
        this.rebuild(TyStm(TyStm(t, n), m), Seq(newS, newM))
      case t => throw new TypeError(s"Stream in StmRepeat has type $t.")
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val stm = this.stm.lower()
    val m = this.m.lower()
    val t = stm.typ.asInstanceOf[TyStm].t
    val n = stm.typ.asInstanceOf[TyStm].n
    val s = Param("s")(TyStm(t, -1))
    val v = Param("v")(TyVec(t, n))
    val i = Param("i")(U32)
    val filling = Param("filling")(TyBool)
    // TODO: It may be possible to shave off one cycle by outputting valid data
    //       during the last filling cycle, but this would make the expression
    //       more complicated.
    // NOTE: You could also implement this using Vec2Stm and then reading the
    //       vector repeatedly, but the resulting expression is pretty gross
    StmBuild(
      SafeProd(n, m)(),
      VecAccess(v, i)(),
      !filling,
      Map[Param, (Expr, Expr)](
        s -> (stm, filling),
        v -> (
          VecBuild(n, U32 ::+ (_ => Default(t)))(),
          Mux(filling, VecShiftLeft(v, StmData(s)())(), v)()
        ),
        i -> (C(0)(U32), Mux(i + 1 === n, C(0)(U32), i + 1)()),
        filling -> (True, filling && (i + 1 < n))
      )
    )().tchk().lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
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

  override def lowerSyntaxSugar(): Expr = {
    val stm = this.stm.lower() // flat stream
    val t = stm.typ.asInstanceOf[TyStm].t
    val n = stm.typ.asInstanceOf[TyStm].n
    val elemSize = this.stm.typ.asInstanceOf[TyStm].t.lower match {
      case TyStm(_, n) => n
      case _           => IntCst(1)()
    }
    StmMap(
      Stm2Vec(stm)() /* flat vector */,
      TyVec(t, n) ::+ (v =>
        Vec2Stm(VecJoin(VecReverse(VecSplit(v, elemSize)))())()
      )
    )().tchk().lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newM = m.tchk.expectUInt()
    val newS = stm.tchk
    newS.typ match {
      case TyStm(t, n) =>
        this.rebuild(TyStm(TyStm(t, newM), n / newM), Seq(newS, newM))
      case t => throw new TypeError(s"Stream in StmSplit has type $t.")
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    // Lowering must produce a flat stream, so leave it as-is
    this.stm.lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newS = stm.tchk(context)
    newS.typ match {
      case TyStm(TyStm(t, m), n) =>
        this.rebuild(TyStm(t, m * n), Seq(newS))
      case t =>
        throw new TypeError(
          s"Stream in StmJoin has type $t. Expected a nested stream."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    // The stream is already flattened during lowering, so there is nothing
    // more to do here
    this.stm.lower()
  }
}

/** Return a stream of "windows" from a stream. Note that if the input stream is
  * multidimensional, the inner dimensions will be converted to vectors and
  * flattened.
  *
  * NOTE: `m` must be such that 1 &le; m &le; n.
  *
  * @param input
  *   A stream of length n.
  * @param m
  *   Window size.
  */
case class StmSlideV(input: Expr /* Stm<A; n> */, m: Expr /* Int */ )(
    typ: Type = Missing
) /* Stm<Vec<A; m>; n-m+1> */
    extends SyntaxSugar(input, m)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, m) => StmSlideV(s, m)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newM = m.tchk.expectUInt()
    val newS = input.tchk
    newS.typ match {
      case TyStm(t, n) if t.isData =>
        this.rebuild(
          TyStm(TyVec(t, newM), ToUnsigned(n - newM + 1)().tchk()),
          Seq(newS, newM)
        )
      case t =>
        throw new TypeError(
          s"Stream in StmSlideV has type $t. Expected a non-nested stream."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val input = this.input.lower()
    val n = this.input.typ.asInstanceOf[TyStm].n
    val (t, elemSize) = this.input.typ.asInstanceOf[TyStm].t.lower match {
      case TyStm(t, n) => (t, n)
      case t           => (t, IntCst(1)())
    }
    val s = Param("s")(TyStm(t, -1))
    val i = Param("i")(U32)
    val j = Param("j")(U32)
    val stmLen = ToUnsigned(n - m + 1)()
    val vecLen = SafeProd(m, elemSize)().tchk().lower()
    val v = Param("v")(TyVec(t, vecLen))
    val lowered = StmBuild(
      stmLen,
      VecShiftLeft(v, StmData(s)())(),
      i + 1 === m && j + 1 === elemSize,
      Map[Param, (Expr, Expr)](
        s -> (input, True),
        // Number of window elements loaded so far
        i -> (
          C(0)(U32),
          Mux((i + 1 === m) || (j + 1 !== elemSize), i, i + 1)()
        ),
        // Number of pieces of data loaded so far in the current window element
        j -> (
          C(0)(U32),
          Mux(j + 1 === elemSize, C(0)(U32), j + 1)()
        ),
        v -> (
          VecBuild(vecLen, U32 ::+ (_ => Default(t)))(),
          VecShiftLeft(v, StmData(s)())()
        )
      )
    )()
    lowered.tchk().lower()
  }
}

/** Similar to <code>StmSlideS</code>, but produces a nested stream rather than
  * a stream of vectors.
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newM = m.tchk.expectUInt()
    val newS = stm.tchk(context)
    newS.typ match {
      case TyStm(t, n) if t.isData =>
        this.rebuild(TyStm(TyStm(t, newM), n - newM + 1), Seq(newS, newM))
      case t =>
        throw new TypeError(
          s"Stream in StmSlideS has typ $t. Expected a stream."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    // It may be possible to optimize this version specifically by producing
    // elements while the shift register is still filling up
    requireType()
    val t = this.stm.typ.asInstanceOf[TyStm].t
    StmMap(StmSlideV(stm, m)(), TyVec(t, m) ::+ (v => Vec2Stm(v)()))()
      .tchk()
      .lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
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

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val m = this.stm.typ.asInstanceOf[TyStm].t.asInstanceOf[TyStm].n
    val stm = this.stm.lower() // flat stream
    val t = stm.typ.asInstanceOf[TyStm].t
    val n = stm.typ.asInstanceOf[TyStm].n
    StmMap(
      Stm2Vec(stm)(), // flat vector
      TyVec(t, n) ::+ (v => Vec2Stm(VecJoin(VecTranspose(VecSplit(v, m)))())())
    )().tchk().lower()
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
