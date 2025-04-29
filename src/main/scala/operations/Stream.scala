package operations

import ir._

private object Helpers {

  /** Convert the given function into a function from stream to stream.
    *
    * @param f
    *   The original function, which could be (1) scalar to scalar, (2) scalar
    *   to stream, or (3) stream to stream.
    */
  def asStm2Stm(f: Function): (Param, StmBuild) = {
    val f0 = f.lower()
    val f1 = f0.typ match {
      case TyArrow(TyStm(t1, n1), _: TyStm) =>
        // stream -> stream (e.g., StmMap, StmPrefix, StmSuffix)
        // We need to be careful about the identity function.
        // We want to be able to reset `f` itself, but *not* the input stream.
        val identity: Function = (x: Expr) => x
        if (f0 == identity) {
          val s2 = Param("s2")(TyStm(t1, n1))
          TyStm(t1, n1) ::+ (s1 =>
            StmBuild(
              n1,
              SSome(StmNext(s2)(TyTuple(s1.typ, t1)).__1)(),
              Map[Param, (Expr, Expr)](
                s2 -> (s1, StmNext(s2)(TyTuple(s1.typ, t1)).__0)
              )
            )(s1.typ)
          )
        } else {
          assert(
            f0.body.isInstanceOf[StmBuild],
            "the function in AsStm2Stm should return a StmBuild"
          )
          f0
        }
      case TyArrow(t1, TyStm(t2, n)) =>
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
        // Existing accumulators whose *seed* depends on `f.param` can only be
        // initialized after the first cycle.
        // If *those* accumulators are needed in the first cycle, inline the
        // seed (but with `f.param` being read from the stream).
        // This does not make sense for input streams: Default[Stm[...]] is
        // not defined, it doesn't make sense to inline a stream in this way,
        // and it doesn't make sense to conjure up a stream out of nowhere
        // after the first cycle).
        // Try to get rid of input streams by fusion.
        val stm = f0.body.asInstanceOf[StmBuild].fuseCompletely()
        for (x <- stm.accVars) {
          if (!Default.hasDefault(x.typ)) {
            throw new IllegalArgumentException(
              s"Accumulator $x with type ${x.typ} has no default."
            )
          }
        }
        val input = Param("input")(TyStm(t1, 1))
        val s = Param("s")(TyStm(t1, 1))
        val isFirstStep = Param("is_first_step")(TyBool)
        val y = Param("y")(t1)
        val next = StmNext(s)(TyTuple(TyStm(t1, 0), t1))
        val yFromStmOrReg = IfThenElse(isFirstStep, next.__1, y)(t1)
        val subs = (
          stm.seedByVar
            .map({ case (x, z) =>
              x -> IfThenElse(
                isFirstStep,
                z.subPreserveType(f0.param -> next.__1),
                x
              )(x.typ)
            })
            .foldLeft(Map[Expr, Expr]())(_ + _)
            + (f0.param -> yFromStmOrReg)
        )
        val newOutput = stm.output.subPreserveType(subs)
        val newEquations = {
          val equationsToAdd = Map[Param, (Expr, Expr)](
            // Input stream
            s -> (input, IfThenElse(isFirstStep, next.__0, s)(s.typ)),
            // Whether we still need to read from the input stream
            isFirstStep -> (True, False),
            // Register for the value from the input stream
            y -> (Default(t1), yFromStmOrReg)
          )
          val updatedOldEquations = stm.nextByVar.map({ case (x, next) =>
            assert(
              !x.typ.isInstanceOf[TyStm],
              "the stream must not take any other streams as input"
            )
            x -> (Default(x.typ), next.subPreserveType(subs))
          })
          updatedOldEquations ++ equationsToAdd
        }
        val newF = Function(
          input,
          StmBuild(stm.n, newOutput, newEquations)(TyStm(t2, n))
        )(TyArrow(TyStm(t1, 1), TyStm(t2, n)))
        assert(!newF.contains(f0.param))
        newF
      case TyArrow(_: TyStm, _) =>
        throw new IllegalArgumentException(
          "Reducing a stream to a scalar is forbidden."
        )
      case TyArrow(t1, t2) =>
        // scalar -> scalar (e.g., x => x + 1)
        val s1 = Param("s1")(TyStm(t1, 1))
        val s2 = Param("s2")(TyStm(t1, 1))
        val next = StmNext(s2)(TyTuple(TyStm(t1, 0), t1))
        Function(
          s1,
          StmBuild(
            1,
            SSome(f0.body.subPreserveType(f0.param -> next.__1))(),
            Map[Param, (Expr, Expr)](s2 -> (s1, next.__0))
          )(TyStm(t2, 1))
        )(TyArrow(TyStm(t1, 1), TyStm(t2, 1)))
      case t =>
        throw new IllegalArgumentException(
          s"Function in AsStm2Stm has type $t."
        )
    }
    // It is essential to fuse everything.
    // If f is a chain of stream producers, we want to reset them all, not
    // just the last producer.
    (f1.param, f1.body.asInstanceOf[StmBuild].fuseCompletely())
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
      IfThenElse(i === 0, SSome(acc)(), NNone(t))(),
      Map[Param, (Expr, Expr)](
        i -> (n, Sum(i, -1)()),
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newN = n.tchk(context).expectType(TyInt)
    val newC = c.tchk(context)
    this.rebuild(TyStm(newC.typ, newN), Seq(newN, newC))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = this.n.lower()
    val c = this.c.lower()
    val out = c.typ match {
      case _: TyStm => StmRepeat(c, n)()
      case _        => StmBuild(n, SSome(c)())()
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newN = n.tchk(context).expectType(TyInt)
    this.rebuild(TyStm(TyInt, newN), Seq(newN))
  }

  override def lowerSyntaxSugar(): Expr = {
    StmRange(n, 0, 1)().tchk().lower()
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
      SSome(a)(),
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
    StmBuild(n * m, SSome(c)())().tchk().lower()
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
    val i = Param("i")(TyInt)
    val j = Param("j")(TyInt)
    StmBuild(
      n * m,
      SSome(Tuple(i, j)())(),
      Map[Param, (Expr, Expr)](
        i -> (0, IfThenElse(j === m - 1, i + 1, i)()),
        j -> (0, IfThenElse(j === m - 1, 0, j + 1)())
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newS = input.tchk(context)
    val (t1, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t           => throw new TypeError(s"Stream in StmMap has type $t.")
    }
    val newF = f.tchk(context)
    val t2 = newF.typ match {
      case TyArrow(t, t2) if t.isCompatibleWith(t1) => t2
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
    // TODO: Doesn't asStm2Stm already take care of this?
    val f = this.f.lower()
    val n = this.typ.asInstanceOf[TyStm].n
    // Instantiate `f` as a function from stream to stream
    val (s, innerStm) = Helpers.asStm2Stm(f)
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
        case _           => IntCst(1)
      }
      val outputsUntilReset = f.typ.asInstanceOf[TyArrow].t2 match {
        case TyStm(_, n) => n
        case _           => IntCst(1)
      }
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
            Equal(innerWithCtrs.nextByVar(inCtr), inputsUntilReset)(TyBool)
              && Equal(innerWithCtrs.nextByVar(outCtr), outputsUntilReset)(
                TyBool
              )
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newS = stm.tchk(context)
    val t = newS.typ match {
      case TyStm(t, _) => t
      case t => throw new TypeError(s"Expected a stream but found $t.")
    }
    val newK = k.tchk(context).expectType(TyInt)
    this.rebuild(TyStm(t, 1), Seq(newS, newK))
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
      IfThenElse(i === k, SSome(StmNext(s)().__1)(), NNone(t))(),
      Map[Param, (Expr, Expr)](
        s -> (stm, StmNext(s)().__0),
        i -> (0, IfThenElse(j === perRow - 1, i + 1, i)()),
        j -> (0, IfThenElse(j === perRow - 1, 0, j + 1)())
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

  override def typecheck(context: Map[Param, Type]): Expr = {
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
          s"Function in StmScan has type $t. Expected ${TyArrow(t2, TyArrow(t1, t2))}."
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

  override def typecheck(context: Map[Param, Type]): Expr = {
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
    // IMPORTANT: use the original input here, not the flattened version
    val n = this.input.typ.asInstanceOf[TyStm].n
    val inputsUntilReset = this.input.typ.asInstanceOf[TyStm].t match {
      case TyStm(_, n) => n
      case _           => IntCst(1)
    }
    val input = this.input.lower()
    val z = this.z.lower()
    val elemType = z.typ
    // TODO: Need to lower f as well? Or does asStm2Stm already take care of it?
    // TODO: Enforce the restriction that the accumulator cannot contain any streams?
    val (s, innerStm) =
      Helpers.asStm2Stm(
        // The function has the form (acc) => (elem) => newAcc.
        // Take just the (elem) => newAcc part here, with acc being a free variable.
        // Later, replace that free variable with the appropriate element in the StmScan accumulator.
        f.body.asInstanceOf[Function]
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
      val outCtr = Param("out_ctr")(TyInt)
      val withOutCtr = innerStm.addOutputCounter(outCtr)
      val usesInputStream = innerStm.seedByVar.exists({ case (_, z) => z == s })
      if (usesInputStream) {
        val inCtr = Param("in_ctr")(TyInt)
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
    val nextAcc =
      OptionAccess(
        innerWithCtrs.output,
        elemType ::+ (v => v),
        TyTuple() ::+ (_ => acc)
      )()
    val outerStm = StmBuild(
      n,
      IfThenElse(shouldReset, SSome(nextAcc)(), NNone(elemType))(),
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
    val scan = outerStm.rebuild(
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

  override def typecheck(context: Map[Param, Type]): Expr = {
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
    )().tchk().lower()
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newS = stm.tchk(context)
    val (t, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmAppend has type $t.")
    }
    val newE = e.tchk(context).expectTypeCompatibleWith(t)
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
      IfThenElse(i < k, SSome(StmNext(s)().__1)(), NNone(t))(),
      Map[Param, (Expr, Expr)](
        s -> (stm, StmNext(s)().__0),
        i -> (0, IfThenElse(j === perRow - 1, i + 1, i)()),
        j -> (0, IfThenElse(j === perRow - 1, 0, j + 1)())
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
      IfThenElse(i >= n - k, SSome(StmNext(s)().__1)(), NNone(t))(),
      Map[Param, (Expr, Expr)](
        s -> (stm, StmNext(s)().__0),
        i -> (0, IfThenElse(j === perRow - 1, i + 1, i)()),
        j -> (0, IfThenElse(j === perRow - 1, 0, j + 1)())
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newS = stm.tchk(context)
    val (t, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmShiftLeft has type $t.")
    }
    val newE = e.tchk(context).expectTypeCompatibleWith(t)
    this.rebuild(TyStm(t, n), Seq(newS, newE))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = this.typ.asInstanceOf[TyStm].n
    StmAppend(StmSuffix(stm, n - 1)(), e)().tchk().lower()
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newS = stm.tchk(context)
    val (t, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmShiftRight has type $t.")
    }
    val newE = e.tchk(context).expectTypeCompatibleWith(t)
    this.rebuild(TyStm(t, n), Seq(newS, newE))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val n = this.typ.asInstanceOf[TyStm].n
    StmPrepend(StmPrefix(stm, n - 1)(), e)().tchk().lower()
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
    val n1 = stm1.typ.asInstanceOf[TyStm].n
    val n2 = stm2.typ.asInstanceOf[TyStm].n
    val s1 = Param("s1")(stm1.typ)
    val s2 = Param("s2")(stm2.typ)
    val i = Param("i")(TyInt)
    StmBuild(
      n1 + n2,
      SSome(IfThenElse(i === n1, StmNext(s2)().__1, StmNext(s1)().__1)())(),
      Map[Param, (Expr, Expr)](
        i -> (0, IfThenElse(i === n1, i, i + 1)()),
        s1 -> (stm1, IfThenElse(i === n1, s1, StmNext(s1)().__0)()),
        s2 -> (stm2, IfThenElse(i === n1, StmNext(s2)().__0, s2)())
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newA = a.tchk(context)
    val (t1, n) = newA.typ match {
      case TyStm(t1, n) if Default.hasDefault(t1) => (t1, n)
      case t =>
        throw new TypeError(
          s"First stream in StmZip has type $t. Expected a non-nested stream."
        )
    }
    val newB = b.tchk(context)
    val t2 = newB.typ match {
      case TyStm(t2, _) if Default.hasDefault(t2) => t2
      case t =>
        throw new TypeError(
          s"Second stream in StmZip has type $t. Expected a non-nested stream."
        )
    }
    this.rebuild(TyStm(TyTuple(t1, t2), n), Seq(newA, newB))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val a = this.a.lower()
    val b = this.b.lower()
    val s0 = Param("s0")(a.typ)
    val s1 = Param("s1")(b.typ)
    StmBuild(
      StmLength(a)(),
      SSome(Tuple(StmNext(s0)().__1, StmNext(s1)().__1)())(),
      Map[Param, (Expr, Expr)](
        s0 -> (a, StmNext(s0)().__0),
        s1 -> (b, StmNext(s1)().__0)
      )
    )().tchk().lower()
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newM = m.tchk(context).expectType(TyInt)
    val newS = stm.tchk(context)
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
    val i = Param("i")(TyInt)
    StmMap(
      Stm2Vec(stm)(),
      TyVec(t, n) ::+ (v =>
        StmBuild(
          n * m,
          SSome(VecAccess(v, i)())(),
          Map[Param, (Expr, Expr)](
            i -> (0, IfThenElse(i + 1 === n, 0, i + 1)())
          )
        )()
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
      case Seq(s) => StmReverse(s)()
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(context: Map[Param, Type]): Expr = {
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
    val elemSize = this.stm.typ.asInstanceOf[TyStm].t.flat match {
      case TyStm(_, n) => n
      case _           => IntCst(1)
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newM = m.tchk(context).expectType(TyInt)
    val newS = stm.tchk(context)
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

  override def typecheck(context: Map[Param, Type]): Expr = {
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newM = m.tchk(context).expectType(TyInt)
    val newS = input.tchk(context)
    newS.typ match {
      case TyStm(t, n) if Default.hasDefault(t) =>
        this.rebuild(TyStm(TyVec(t, newM), n - newM + 1), Seq(newS, newM))
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
    val (t, elemSize) = this.input.typ.asInstanceOf[TyStm].t.flat match {
      case TyStm(t, n) => (t, n)
      case t           => (t, IntCst(1))
    }
    val s = Param("s")()
    val i = Param("i")()
    val j = Param("j")()
    val v = Param("v")()
    val lowered = StmBuild(
      n - m + 1,
      IfThenElse(
        i === 0 && j === 1,
        // CASE 1: Shift register is full.
        //         Produce output.
        SSome(VecShiftLeft(v, StmNext(s)().__1))(),
        // CASE 2: Shift register is not full yet.
        //         Wait until it is.
        NNone(TyVec(t, m * elemSize))
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
          VecBuild(m * elemSize, (_: Expr) => Default(t))(),
          VecShiftLeft(v, StmNext(s)().__1)
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newM = m.tchk(context).expectType(TyInt)
    val newS = stm.tchk(context)
    newS.typ match {
      case TyStm(t, n) if Default.hasDefault(t) =>
        this.rebuild(TyStm(TyStm(t, newM), n - newM + 1), Seq(newS, newM))
      case t =>
        throw new TypeError(
          s"Stream in StmSlideS has typ $t. Expected a stream."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    // TODO: Optimize this version specifically by producing elements while
    //       the shift register is still filling up?
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newS = stm.tchk(context)
    newS.typ match {
      case TyStm(TyStm(t, m), n) if Default.hasDefault(t) =>
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
