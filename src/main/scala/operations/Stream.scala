package operations

import ir._
import opt.PartialEvalPass

private object Helpers {
  @deprecated
  def expandTuple(t: Expr, n: Int): Tuple = {
    Tuple((0 until n).map(i => TupleAccess(t, i)()): _*)()
  }

  /** Convert the given function into a function from stream to stream.
    *
    * @param f
    *   The original function, which could be (1) scalar to scalar, (2) scalar
    *   to stream, (3) stream to scalar, or (4) stream to stream.
    * @param inShape
    *   Shape of the input to `f`. `Some(n)` means `f` takes a stream of `n`
    *   scalars. `None` means `f` takes one scalar.
    * @param outShape
    *   Shape of the output of `f`. `Some(n)` means `f` returns a stream of `n`
    *   scalars. `None` means `f` returns one scalar.
    */
  def asStm2Stm(
      f: Function,
      inShape: Option[Expr],
      outShape: Option[Expr]
  ): (Param, StmBuild) = {
    val f1 = (inShape, outShape) match {
      case (None, None) =>
        // scalar -> scalar (e.g., x => x + 1)
        val x = Param("s")()
        val s = Param("s")()
        Function(
          x /* stream */,
          StmBuild(
            1,
            SSome(f.body.substitute(f.param -> StmNext(s)().__1))(),
            Map[Param, (Expr, Expr)](s -> (x, StmNext(s)().__0))
          )()
        )()
      case (None, Some(_)) =>
        // scalar -> stream (e.g., c => StmCst(n, c), c => StmCountFrom(n, c))
        // The scalar input to the original function (`f.param`) can appear in
        // the seed of the stream (as in StmCountFrom), in the `nextF` (as in
        // StmCst), or even both (if you have something weird like a
        // StmBuild that constructs StmCountFrom(n, c) and StmCst(n, c) but
        // zipped together).
        // In the first cycle, whenever `f.param` appears, directly read the
        // value from the stream instead.
        // Also save that value in a new accumulator variable.
        // In subsequent cycles, whenever `f.param` appears, read from the
        // new accumulator variable.
        val stm = f.body.asInstanceOf[StmBuild]
        val input = Param("input")()
        val s = Param("s")()
        val isFirstStep = Param("is_first_step")()
        val y = Param("y")()
        val subs = (
          stm.seedByVar
            .map({ case (x, z) =>
              x -> IfThenElse(
                isFirstStep,
                z.substitute(f.param -> StmNext(s)().__1),
                x
              )()
            })
            .foldLeft(Map[Expr, Expr]())(_ + _)
            + (f.param -> IfThenElse(isFirstStep, StmNext(s)().__1, y)())
        )
        val equationsToAdd = Map[Param, (Expr, Expr)](
          // Input stream
          s -> (input, IfThenElse(isFirstStep, StmNext(s)().__0, s)()),
          // Whether we still need to read from the input stream
          isFirstStep -> (True, False),
          // Register for the value from the input stream
          y -> (Default(???), IfThenElse(isFirstStep, StmNext(s)().__1, y)())
        )
        val updatedOldEquations = stm.nextByVar.map({ case (x, next) =>
          x -> (Default(???), next.substitute(subs))
        })
        val newF = Function(
          input /* stream */,
          StmBuild(
            stm.n,
            stm.output.substitute(subs),
            updatedOldEquations ++ equationsToAdd
          )()
        )()
        assert(!newF.contains(f.param))
        newF
      case (Some(_), None) => {
        throw new IllegalArgumentException(
          "Reducing a stream to a scalar is forbidden."
        )
      }
      case (Some(_), Some(_)) =>
        // stream -> stream (e.g., StmMap, StmPrefix, StmSuffix)
        f
    }
    // We need to be careful about the identity function.
    // We want to be able to reset `f` itself, but *not* the input stream.
    // TODO: does this issue occur in any cases other than the identity function?
    val identity: Function = (x: Expr) => x
    val f2 = if (f1 == identity) {
      val x = Param("s")()
      val s = Param("s")()
      Function(
        x,
        StmBuild(
          outShape.getOrElse(IntCst(1)),
          SSome(StmNext(s)().__1)(),
          Map[Param, (Expr, Expr)](s -> (x, StmNext(s)().__0))
        )()
      )()
    } else {
      f1
    }
    // It is essential to fuse everything.
    // If f is a chain of stream producers, we want to reset them all, not
    // just the last producer.
    (f2.param, f2.body.asInstanceOf[StmBuild].fuseCompletely())
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newN = n.tchk(context).expectType(TyInt)
    val newZ = z.tchk(context)
    val t = newZ.typ
    val newF = f.tchk(context).expectTypeCompatibleWith(TyArrow(t, t))
    this.rebuild(TyStm(t, 1), Seq(newN, newZ, newF))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = this.n.lower()
    val z = this.z.lower()
    val f = this.f.lower()
    val i = Param("i")(TyInt)
    val t = z.typ
    val acc = Param("acc")(t)
    StmBuild(
      1,
      IfThenElse(
        Equal(i, 0)(TyBool),
        SSome(acc)(TyOption(t)),
        NNone(t)
      )(TyOption(t)),
      Map[Param, (Expr, Expr)](
        i -> (n, Sum(i, -1)(TyInt)),
        acc -> (z, FunCall(f, acc)(t))
      )
    )(this.typ.flat).lower()
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newN = n.tchk(context).expectType(TyInt)
    val newC = c.tchk(context)
    this.rebuild(TyStm(newC.typ, newN), Seq(newN, newC))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = this.n.lower()
    val c = this.c.lower()
    c.typ match {
      case TyStm(_, m) => StmRepeat(c, m, n).lower()
      case t           => StmBuild(n, SSome(c)())(TyStm(t, n))
    }
    if (this.typ == Missing) {
      StmBuild(n, SSome(c)())()
    } else {
      StmBuild(n, SSome(c)(TyOption(c.typ)))(this.typ.flat)
    }
  }
}
//object StmCst {
//  def apply(n: Expr, c: Expr): StmBuild /* Stm<Int; n> */ = {
//    StmBuild(n, SSome(c)(), Map[Param, (Expr, Expr)]())()
//  }
//}

object StmCount {
  def apply(n: Expr)(typ: Type = Missing): Expr /* Stm<Int; n> */ =
    StmRange(n, 0, 1)(typ)
}

object StmCountFrom {
  def apply(start: Expr, n: Expr)(typ: Type = Missing): Expr =
    StmRange(n, start, 1)(typ)
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newN = n.tchk(context).expectType(TyInt)
    val newZ = z.tchk(context).expectType(TyInt)
    val newDelta = delta.tchk(context).expectType(TyInt)
    this.rebuild(TyStm(TyInt, newN), Seq(newN, newZ, newDelta))
  }

  override def lowerSyntaxSugar(): Expr = {
    val n = this.n.lower()
    val z = this.z.lower()
    val delta = this.delta.lower()
    val a = Param("a")(TyInt)
    StmBuild(
      n,
      SSome(a)(TyOption(TyInt)),
      Map[Param, (Expr, Expr)](a -> (z, a + delta))
    )(this.typ.flat)
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newN = n.tchk(context).expectType(TyInt)
    val newM = m.tchk(context).expectType(TyInt)
    val newC = c.tchk(context)
    this.rebuild(TyStm(TyStm(newC.typ, newM), newN), Seq(newN, newM, newC))
  }

  override def lowerSyntaxSugar(): Expr = {
    val n = this.n.lower()
    val m = this.m.lower()
    val c = this.c.lower()
    StmBuild(n * m, SSome(c)(c.typ))(this.typ.flat)
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newN = n.tchk(context).expectType(TyInt)
    val newM = m.tchk(context).expectType(TyInt)
    this.rebuild(
      TyStm(TyStm(TyTuple(TyInt, TyInt), newM), newN),
      Seq(newN, newM)
    )
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = this.n.lower()
    val m = this.m.lower()
    val tup = TyTuple(TyInt, TyInt)
    val i = Param("i")(TyInt)
    val j = Param("j")(TyInt)
    StmBuild(
      n * m,
      SSome(Tuple(i, j)(tup))(TyOption(tup)),
      Map[Param, (Expr, Expr)](
        i -> (0, IfThenElse(Equal(j, m - 1)(TyBool), i + 1, i)(TyInt)),
        j -> (0, IfThenElse(Equal(j, m - 1)(TyBool), 0, j + 1)(TyInt))
      )
    )(this.typ.flat)
  }
}

object StmMap {
  def apply(
      input: Expr /* Stm<A; n> */,
      f: Function /* A -> B */,
      // TODO: Ideally we would get this shape info from the type system
      n: Expr,
      fInShape: Option[Expr],
      fOutShape: Option[Expr]
  ): Expr /* Stm<B; n> */ = {
    // Instantiate `f` as a function from stream to stream
    val (s, innerStm) =
      Helpers.asStm2Stm(f, inShape = fInShape, outShape = fOutShape)
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
      StmRepeat(innerStm, n, innerStm.n)
    } else {
      // How many elements will the inner component read and produce before it must be reset?
      val inputsUntilReset = fInShape.getOrElse(IntCst(1))
      val outputsUntilReset = fOutShape.getOrElse(IntCst(1))
      val map = n match {
        // TODO: Why this special case? Is it just to make the resulting stream
        //       easier to simplify? Should I add something similar in the
        //       partial evaluator?
        case IntCst(1) =>
          // No need to reset
          innerStm
        case n =>
          val inCtr = Param("in_ctr")()
          val outCtr = Param("out_ctr")()
          val innerWithCtrs = innerStm
            .addInputCounter(
              innerStm.seedByVar.find({ case (_, z) => z == s }).get._1,
              inCtr
            )
            .addOutputCounter(outCtr)
          // Want to reset depending on the *next* values of the in/out counters.
          val shouldReset = (
            (innerWithCtrs.nextByVar(inCtr) === inputsUntilReset)
              && (innerWithCtrs.nextByVar(outCtr) === outputsUntilReset)
          )
          val outerStm = StmBuild(
            n * outputsUntilReset,
            innerWithCtrs.output,
            innerWithCtrs.equations.map({ case (x, (z, next)) =>
              if (z == s) {
                // Never reset the input stream
                x -> (z, next)
              } else {
                x -> (z, IfThenElse(shouldReset, z, next)())
              }
            })
          )()
          outerStm
      }
      val ret = map.substitute(s -> input)
      val originalFreeVars =
        (
          input.freeVars()
            ++ f.freeVars()
            ++ n.freeVars()
            ++ fInShape.toSet.flatMap((e: Expr) => e.freeVars())
            ++ fOutShape.toSet.flatMap((e: Expr) => e.freeVars())
        )
      assert(
        ret.freeVars() == originalFreeVars,
        s"the set of free variables should be unchanged by StmMap (expected ${originalFreeVars}, got ${ret.freeVars()})"
      )
      ret
    }
    // Simplification is NOT required, but it makes the tests run much faster
    opt.PartialEvalPass.partialEval(map)
  }
}

case class StmAccess(
    stm: Expr /* Stm<A; n> */,
    k: Expr /* Int */
)(typ: Type = Missing) /* A */
    extends SyntaxSugar(stm, k)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, k) => StmAccess(s, k)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newS = stm.tchk(context)
    val t = newS.typ match {
      case TyStm(t, _) => t
      case t => throw new TypeError(s"Expected a stream but found $t.")
    }
    val newK = k.tchk(context).expectType(TyInt)
    this.rebuild(t, Seq(newS, newK))
  }

  override def lowerSyntaxSugar(): Expr = {
    // TODO: Implement this with StmPrefix and StmSuffix?
    requireType()
    val stm = this.stm.lower()
    val k = this.k.lower()
    val (t, perRow) = this.stm.typ.asInstanceOf[TyStm].t.flat match {
      case TyStm(t, n) => (t, n)
      case t           => (t, IntCst(1))
    }
    val s = Param("s")(stm.typ) // input stream
    val i = Param("i")(TyInt) // index of current row
    val j = Param("j")(TyInt) // index within row
    StmBuild(
      perRow,
      IfThenElse(
        Equal(i, k)(TyBool),
        SSome(StmNext(s)(TyTuple(s.typ, t)).__1)(),
        NNone(t)
      )(TyOption(t)),
      Map[Param, (Expr, Expr)](
        s -> (stm, StmNext(s)(TyTuple(s.typ, t)).__0),
        i -> (0, IfThenElse(Equal(j, perRow - 1)(TyBool), i + 1, i)(TyInt)),
        j -> (0, IfThenElse(Equal(j, perRow - 1)(TyBool), 0, j + 1)(TyInt))
      )
    )(this.typ.flat)
  }
}

object StmFold {
  def apply(
      stream: Expr /* Stm<A; n> */,
      z: Expr /* B */,
      f: Function /* B -> A -> B */,
      // Ideally we would get this shape info from the type system
      stmShape: Seq[Expr]
  ): Expr /* Stm<B; 1> */ = {
    StmSuffix(StmScanInclusive(stream, z, f, stmShape = stmShape), 1)()
  }
}

// TODO: Add special cases for n = 0 and n = 1, like in StmMap?
//       Or better yet, define an operator like StmReset that works for both cases?
object StmScanInclusive {
  def apply(
      input: Expr /* Stream<A> */,
      z: Expr /* B */,
      f: Function /* B -> A -> B */,
      // Ideally we would get this shape info from the type system
      stmShape: Seq[Expr]
  ): Expr = {
    // TODO: Enforce the restriction that the accumulator cannot contain any streams?
    val (s, innerStm) =
      Helpers.asStm2Stm(
        // The function has the form (acc) => (elem) => newAcc.
        // Take just the (elem) => newAcc part here, with acc being a free variable.
        // Later, replace that free variable with the appropriate element in the StmScan accumulator.
        f.body.asInstanceOf[Function],
        inShape =
          if (stmShape.tail.isEmpty) None
          else Some(stmShape.tail.fold(IntCst(1))((x, y) => x * y)),
        outShape = if (stmShape.tail.isEmpty) None else Some(1)
      )
    assert(
      innerStm.n == IntCst(1),
      "the function in StmScan should return a scalar or a stream of length 1"
    )
    assert(
      innerStm.seedByVar.count({ case (_, z) => z == s }) <= 1,
      "the input stream should appear at most once in the inner StmBuild"
    )
    val (innerWithCtrs, shouldReset) = {
      val outputsUntilReset = IntCst(1)
      val outCtr = Param("out_ctr")()
      val withOutCtr = innerStm.addOutputCounter(outCtr)
      val usesInputStream = innerStm.seedByVar.exists({ case (_, z) => z == s })
      if (usesInputStream) {
        val inputsUntilReset = stmShape.tail.fold(IntCst(1))((x, y) => x * y)
        val inCtr = Param("in_ctr")()
        val withCtrs = withOutCtr
          .addInputCounter(
            innerStm.seedByVar.find({ case (_, z) => z == s }).get._1,
            inCtr
          )
        // Want to reset depending on the *next* values of the in/out counters.
        val shouldReset = ((withCtrs.nextByVar(inCtr) === inputsUntilReset)
          && (withCtrs.nextByVar(outCtr) === outputsUntilReset))
        (withCtrs, shouldReset)
      } else {
        val shouldReset = withOutCtr.nextByVar(outCtr) === outputsUntilReset
        (withOutCtr, shouldReset)
      }
    }
    val acc = Param("acc")()
    val nextAcc =
      OptionAccess(innerWithCtrs.output, (v: Expr) => v, (_: Expr) => acc)()
    val outerStm = StmBuild(
      stmShape.head,
      IfThenElse(shouldReset, SSome(nextAcc)(), NNone(???))(),
      innerWithCtrs.equations.map({ case (x, (z, next)) =>
        if (z == s) {
          // Never reset the input stream
          x -> (z, next)
        } else {
          x -> (z, IfThenElse(shouldReset, z, next)())
        }
      }) + (acc -> (z, nextAcc))
    )()
    assert(
      !outerStm.n.contains(s)
        && !outerStm.output.contains(s)
        && !outerStm.nextByVar.values.toSeq.exists(nxt => nxt.contains(s)),
      "the input stream must only occur in the seed of the StmBuild"
    )
    // It is safe to do this "naive" substitution because
    //  (1) `s` definitely only occurs in the seed (where it is free), so the
    //      naive substitution behaves like the usual one
    //  (2) we want to replace f.param with the *bound* variable acc, so alpha
    //      renaming is NOT what we want here
    val scan = outerStm.map(e =>
      e.substitute(Map[Expr, Expr](s -> input, f.param -> acc))
    )
    val originalFreeVars = (
      input.freeVars()
        ++ z.freeVars()
        ++ f.freeVars()
        ++ stmShape.flatMap(e => e.freeVars())
    )
    assert(
      scan.freeVars() == originalFreeVars,
      s"the set of free variables should be unchanged by StmScan (expected ${originalFreeVars} but got ${scan.freeVars()})"
    )
    // Simplification is NOT required, but it makes the tests run much faster
    PartialEvalPass.partialEval(scan)
  }
}

object StmScanExclusive {
  def apply(
      stm: Expr /* Stm<A; n> */,
      z: Expr /* B */,
      f: Expr => Expr => Expr /* B -> A -> B */,
      // Ideally we would get this shape info from the type system
      stmShape: Seq[Expr]
  ): Expr /* Vec<B; n> */ = {
    // Maybe it would be better to first take prefix of input stream, scan,
    // and then prepend
    StmShiftRight(
      StmScanInclusive(stm, z, f, stmShape = stmShape),
      z,
      stmShape = Seq(stmShape.head)
    )
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

  override def typecheck(context: Map[Param, Type]): Expr = {
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
    val i = Param("i")(TyInt)
    val (t, n) = {
      val vt = v.typ.asInstanceOf[TyVec]
      (vt.t, vt.n)
    }
    // Alternatively, you could implement Vec2Stm using a shift register
    StmBuild(
      n,
      SSome(VecAccess(v, i)(t))(),
      Map[Param, (Expr, Expr)](i -> (0, i + 1))
    )(this.typ.flat)
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newS = stm.tchk(context)
    val (t, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmPrepend has type $t.")
    }
    val newE = e.tchk(context).expectTypeCompatibleWith(t)
    this.rebuild(TyStm(t, n + 1), Seq(newS, newE))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val t = this.typ.asInstanceOf[TyStm].t
    StmConcat(StmCst(1, e)(TyStm(t, 1)), stm)(this.typ).lower()
  }
}

object StmAppend {
  def apply(
      stm: Expr /* Stm<A; n> */,
      e: Expr /* A */,
      // Ideally we would get this shape information from the type system
      stmShape: Seq[Expr]
  ): Expr /* Stm<A; n+1> */ = {
    val eShape = stmShape.tail
    val eStm = if (eShape.isEmpty) StmCst(1, e)() else e
    StmConcat(stm, eStm)()
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newK = k.tchk(context).expectType(TyInt)
    val newS = stm.tchk(context)
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
    val (t, perRow) = this.stm.typ.asInstanceOf[TyStm].t.flat match {
      case TyStm(t, n) => (t, n)
      case t           => (t, IntCst(1))
    }
    val s = Param("s")(stm.typ) // input stream
    val i = Param("i")(TyInt) // index of current row
    val j = Param("j")(TyInt) // index within row
    StmBuild(
      k * perRow,
      IfThenElse(
        LessThan(i, k)(TyBool),
        SSome(StmNext(s)(TyTuple(s.typ, t)).__1)(),
        NNone(t)
      )(TyOption(t)),
      Map[Param, (Expr, Expr)](
        s -> (stm, StmNext(s)(TyTuple(s.typ, t)).__0),
        i -> (0, IfThenElse(Equal(j, perRow - 1)(TyBool), i + 1, i)(TyInt)),
        j -> (0, IfThenElse(Equal(j, perRow - 1)(TyBool), 0, j + 1)(TyInt))
      )
    )(this.typ.flat)
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newK = k.tchk(context).expectType(TyInt)
    val newS = stm.tchk(context)
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
    val (t, perRow) = this.stm.typ.asInstanceOf[TyStm].t.flat match {
      case TyStm(t, n) => (t, n)
      case t           => (t, IntCst(1))
    }
    val s = Param("s")(stm.typ) // input stream
    val i = Param("i")(TyInt) // index of current row
    val j = Param("j")(TyInt) // index within row
    StmBuild(
      k * perRow,
      IfThenElse(
        !LessThan(i, n - k)(TyBool),
        SSome(StmNext(s)(TyTuple(s.typ, t)).__1)(),
        NNone(t)
      )(TyOption(t)),
      Map[Param, (Expr, Expr)](
        s -> (stm, StmNext(s)(TyTuple(s.typ, t)).__0),
        i -> (0, IfThenElse(Equal(j, perRow - 1)(TyBool), i + 1, i)(TyInt)),
        j -> (0, IfThenElse(Equal(j, perRow - 1)(TyBool), 0, j + 1)(TyInt))
      )
    )(this.typ.flat)
  }
}

object StmShiftLeft {
  def apply(
      stm: Expr /* Stm<A; n> */,
      e: Expr /* A */,
      // Ideally we would get this shape info from the type system
      stmShape: Seq[Expr]
  ): Expr /* Stm<A; n> */ = {
    val n = stmShape.head
    StmAppend(
      StmSuffix(stm, n - 1)(),
      e,
      stmShape = stmShape.updated(0, n - 1)
    )
  }
}

object StmShiftRight {
  def apply(
      stm: Expr /* Stm<A; n> */,
      e: Expr /* A */,
      // Ideally we would get this shape information from the type system
      stmShape: Seq[Expr]
  ): Expr /* Stm<A; n> */ = {
    val n = stmShape.head
    StmPrepend(StmPrefix(stm, n - 1)(), e)()
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

  override def typecheck(context: Map[Param, Type]): Expr = {
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
      case TyStm(t2, n2) if t2.isCompatibleWith(t1) => n2
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
    val t = this.typ.asInstanceOf[TyStm].t
    val n1 = stm1.typ.asInstanceOf[TyStm].n
    val n2 = stm2.typ.asInstanceOf[TyStm].n
    val s1 = Param("s1")(stm1.typ)
    val s2 = Param("s2")(stm2.typ)
    val i = Param("i")(TyInt)
    StmBuild(
      n1 + n2,
      SSome(
        IfThenElse(
          Equal(i, n1)(TyBool),
          StmNext(s2)(TyTuple(s1.typ, t)).__1,
          StmNext(s1)(TyTuple(s2.typ, t)).__1
        )(t)
      )(),
      Map[Param, (Expr, Expr)](
        i -> (0, IfThenElse(Equal(i, n1)(TyBool), i, i + 1)(TyInt)),
        s1 -> (stm1, IfThenElse(
          Equal(i, n1)(TyBool),
          s1,
          StmNext(s1)(TyTuple(s1.typ, t)).__0
        )(s1.typ)),
        s2 -> (stm2, IfThenElse(
          Equal(i, n1)(TyBool),
          StmNext(s2)(TyTuple(s2.typ, t)).__0,
          s2
        )(s2.typ))
      )
    )(this.typ.flat)
  }
}

// TODO: Disallow applying this to multi-dimensional streams?
object StmZip {
  def apply(
      a: Expr /* Stm<A; n> */,
      b: Expr /* Stm<B; n> */
  ): StmBuild /* Stm<(A, B); n> */ = {
    val s0 = Param("s0")()
    val s1 = Param("s1")()
    StmBuild(
      StmLength(a)(),
      SSome(Tuple(StmNext(s0)().__1, StmNext(s1)().__1)())(),
      Map[Param, (Expr, Expr)](
        s0 -> (a, StmNext(s0)().__0),
        s1 -> (b, StmNext(s1)().__0)
      )
    )()
  }
}

// Not particularly useful, just a strange case to be used to test the compiler
// TODO: Disallow applying this to multi-dimensional streams?
object StmZipAlternating {
  def apply(
      a: Expr /* Stm<A; n> */,
      b: Expr /* Stm<A; n> */
  ): Expr /* Stm<(A, A); n> */ = {
    val s0 = Param("a")()
    val s1 = Param("b")()
    StmBuild(
      StmLength(a)(),
      SSome(Tuple(StmNext(s0)().__1, StmNext(s1)().__1)())(),
      Map(
        s0 -> (a, StmNext(s1)().__0),
        s1 -> (b, StmNext(s0)().__0)
      )
    )()
  }
}

object StmRepeat {
  def apply(
      stm: Expr /* Stm<A; n> */,
      m: Expr,
      // Ideally we would get this shape info from the type system
      n: Expr
  ): Expr /* Stm<Stm<A; n>; m> */ = {
    val v = Stm2Vec(stm)()
    val i = Param("i")()
    StmMap(
      v,
      (v: Expr) =>
        StmBuild(
          n * m,
          SSome(VecAccess(v, i)())(),
          Map[Param, (Expr, Expr)](
            i -> (0, IfThenElse(i + 1 === n, 0, i + 1)())
          )
        )(),
      n = 1,
      fInShape = None,
      fOutShape = Some(n * m)
    )
  }
}

// TODO: How should this work for "multi-dimensional" streams?
object StmReverse {
  def apply(
      stm: Expr /* Stm<A; n> */,
      // Ideally we would get this shape info from the type system
      n: Expr
  ): Expr /* Stm<A; n> */ = {
    val v = Stm2Vec(stm)()
    StmMap(
      v,
      (v: Expr) => Vec2Stm(VecReverse(v))(),
      n = 1,
      fInShape = None,
      fOutShape = Some(n)
    )
  }
}

object StmSplit {
  def apply(
      stm: Expr /* Stm<A; n> */,
      m: Expr
  ): Expr /* Stm<Stm<A; m>; n/m> */ = {
    // Should there be some kind of assertion to check that n is divisible by
    // m? Or will that be handled in the higher-level IR?
    stm
  }
}

object StmJoin {
  def apply(stm: Expr /* Stm<Stm<A; m>; n> */ ): Expr /* Stm<A; m*n> */ = {
    stm
  }
}

object StmSlideV {

  /** Return a stream of "windows" from a stream. Note that if the input stream
    * is multidimensional, the inner dimensions will be converted to vectors and
    * flattened.
    *
    * NOTE: `m` must be such that 1 &le; m &le; n.
    *
    * @param input
    *   A stream of length n.
    * @param m
    *   Window size.
    */
  def apply(
      input: Expr /* Stm<A; n> */,
      m: Int,
      // Ideally we would get this shape info from the type system
      stmShape: Seq[Expr]
  ): Expr /* Stm<Vec<A; m>, n-m+1> */ = {
    val n = stmShape.head
    val elemSize = stmShape.tail.fold(IntCst(1))(_ * _)
    val s = Param("s")()
    val i = Param("i")()
    val j = Param("j")()
    val v = Param("v")()
    StmBuild(
      n - m + 1,
      IfThenElse(
        i === 0 && j === 1,
        // CASE 1: Shift register is full.
        //         Produce output.
        SSome(VecShiftLeft(v, StmNext(s)().__1))(),
        // CASE 2: Shift register is not full yet.
        //         Wait until it is.
        NNone(???)
      )(),
      Map[Param, (Expr, Expr)](
        s -> (input, StmNext(s)().__0),
        // Number of window elements left to load
        i -> (
          m - 1,
          IfThenElse(
            i === 0 && j === 1,
            // CASE 1: Shift register is full.
            i,
            // CASE 2: Shift register is not full yet.
            IfThenElse(
              i === 0,
              // CASE 2a: Initial loading is done, just loading the next elem
              //          (may take multiple cycles for nested streams).
              i,
              // CASE 2b: Initial loading still in progress.
              IfThenElse(j === 1, i - 1, i)()
            )()
          )()
        ),
        // How many pieces of data left to load in the current window element?
        // This will be 1 if `stm` is flat but may be greater than 1
        // if `stm` is multi-dimensional.
        j -> (
          elemSize,
          IfThenElse(
            i === 0 && j === 1,
            // CASE 1: Shift register is full.
            elemSize,
            // CASE 2: Shift register is not full yet.
            IfThenElse(
              i === 0,
              // CASE 2a: Initial loading is done, just loading the next elem
              //          (may take multiple cycles for nested streams).
              j - 1,
              // CASE 2b: Initial loading still in progress.
              IfThenElse(j === 1, elemSize, j - 1)()
            )()
          )()
        ),
        v -> (
          VecBuild(m * elemSize, (_: Expr) => Default(???))(),
          VecShiftLeft(v, StmNext(s)().__1)
        )
      )
    )()
  }
}

object StmSlideS {
  def apply(
      stm: Expr /* Stm<A; n> */,
      m: Int,
      // Ideally we would get this shape info from the type system
      stmShape: Seq[Expr]
  ): Expr /* Stm<Stm<A; m>; n-m+1> */ = {
    // TODO: Optimize this version specifically by producing elements while
    //       the shift register is still filling up?
    val s = StmSlideV(stm, m, stmShape = stmShape)
    StmMap(
      s,
      (v: Expr) => Vec2Stm(v)(),
      n = stmShape.head - m + 1,
      fInShape = None,
      fOutShape = Some(m * stmShape.tail.fold(IntCst(1))((x, y) => x * y))
    )
  }
}

object StmTranspose {
  def apply(
      stm: Expr /* Stm<Stm<A; m>; n> */,
      // Ideally we would get this shape info from the type system
      n: Expr,
      m: Expr
  ): Expr /* Stm<Stm<A; n>; m> */ = {
    StmMap(
      Stm2Vec(stm)(),
      (v: Expr) => Vec2Stm(VecJoin(VecTranspose(VecSplit(v, m = m))))(),
      n = 1,
      fInShape = None,
      fOutShape = Some(n * m)
    )
  }
}
