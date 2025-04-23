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
        val x = Param("s")
        val s = Param("s")
        Function(
          x /* stream */,
          Missing,
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
        val input = Param("input")
        val s = Param("s")
        val isFirstStep = Param("is_first_step")
        val y = Param("y")
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
          Missing,
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
      val x = Param("s")
      val s = Param("s")
      Function(
        x,
        Missing,
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

object Iterate {
  def apply(
      n: Expr /* Int */,
      z: Expr /* A */,
      f: Function /* A -> A */,
      // TODO: Ideally we would get this shape information from the type system,
      zSize: Option[Int]
  ): Expr = {
    val i = Param("i")
    val acc = Param("acc")
    val accExpanded = zSize match {
      case Some(n) => Helpers.expandTuple(acc, n)
      case None    => acc
    }
    val s = StmBuild(
      1,
      IfThenElse(i === 0, SSome(accExpanded)(), NNone(???))(),
      Map[Param, (Expr, Expr)](
        i -> (n, IfThenElse(i === 0, 0, i - 1)()),
        acc -> (z, IfThenElse(
          i === 0,
          accExpanded,
          FunCall(f, accExpanded)()
        )())
      )
    )()
    StmNext(s)().__1
  }
}

object StmCst {
  def apply(n: Expr, c: Expr): StmBuild /* Stm<Int; n> */ = {
    StmBuild(n, SSome(c)(), Map[Param, (Expr, Expr)]())()
  }
}

object StmCount {
  def apply(n: Expr): StmBuild /* Stm<Int; n> */ = StmRange(n, 0, 1)
}

object StmCountFrom {
  def apply(start: Expr, n: Expr): Expr = StmRange(n, start, 1)
}

object StmRange {

  /** An arbitrary counter, a bit like Python's <code>range()</code>.
    *
    * @param n
    *   Length of the stream.
    * @param z
    *   Initial value of the stream.
    * @param delta
    *   Difference between consecutive elements.
    * @return
    *   The stream of length <code>n</code> with elements <code>[z, z + delta, z
    *   + 2 * delta, ...]</code>.
    */
  def apply(n: Expr, z: Expr, delta: Expr): StmBuild = {
    val a = Param("a")
    StmBuild(n, SSome(a)(), Map(a -> (z, a + delta)))()
  }
}

object StmCst2D {
  def apply(n: Expr, m: Expr, c: Expr): Expr /* Stm<Stm<Int; m>; n> */ = {
    StmBuild(n * m, SSome(c)(), Map[Param, (Expr, Expr)]())()
  }
}

object StmCount2D {
  def apply(n: Expr, m: Expr): Expr /* Stm<Stm<Int; m>; n> */ = {
    val i = Param("i")
    val j = Param("j")
    StmBuild(
      n * m,
      SSome(Tuple(i, j)())(),
      Map[Param, (Expr, Expr)](
        i -> (0, IfThenElse(j === m - 1, i + 1, i)()),
        j -> (0, IfThenElse(j === m - 1, 0, j + 1)())
      )
    )()
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
          val inCtr = Param("in_ctr")
          val outCtr = Param("out_ctr")
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

object StmAccess {
  def apply(
      stm: Expr /* Stm<A; n> */,
      k: Expr /* Int */,
      // TODO: Ideally we would get this shape info from the type system
      shape: Seq[Expr]
  ): Expr /* A */ = {
    // NOTE: require 0 <= k < n
    val perRow = shape.tail.fold(IntCst(1))((x, y) => x * y)
    val s = Param("s") // input stream
    val i = Param("i") // index of current row
    val j = Param("j") // index within row
    StmBuild(
      perRow,
      IfThenElse(i === k, SSome(StmNext(s)().__1)(), NNone(???))(),
      Map[Param, (Expr, Expr)](
        s -> (stm, StmNext(s)().__0),
        i -> (0, IfThenElse(j === perRow - 1, i + 1, i)()),
        j -> (0, IfThenElse(j === perRow - 1, 0, j + 1)())
      )
    )()
  }
}

object StmFold {
  def apply(
      stream: Expr /* Stm<A; n> */,
      z: Expr /* B */,
      f: Function /* B -> A -> B */,
      // Ideally we would get this shape info from the type system
      stmShape: Seq[Expr]
  ): StmBuild /* Stm<B; 1> */ = {
    StmSuffix(
      StmScanInclusive(stream, z, f, stmShape = stmShape),
      1,
      shape = Seq(stmShape.head)
    )
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
      val outCtr = Param("out_ctr")
      val withOutCtr = innerStm.addOutputCounter(outCtr)
      val usesInputStream = innerStm.seedByVar.exists({ case (_, z) => z == s })
      if (usesInputStream) {
        val inputsUntilReset = stmShape.tail.fold(IntCst(1))((x, y) => x * y)
        val inCtr = Param("in_ctr")
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
    val acc = Param("acc")
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

object Vec2Stm {
  def apply(v: Expr /* Vec<A; n> */, n: Expr): StmBuild /* Stm<A; n> */ = {
    // Alternatively, you could implement Vec2Stm using a shift register
    val i = Param("i")
    StmBuild(
      n,
      SSome(VecAccess(v, i)())(),
      Map[Param, (Expr, Expr)](i -> (0, i + 1))
    )()
  }
}

object StmPrepend {
  def apply(
      stm: Expr /* Stm<A; n> */,
      e: Expr /* A */,
      // Ideally we would get this shape info from the type system
      stmShape: Seq[Expr]
  ): Expr /* Stm<A; n+1> */ = {
    val eShape = stmShape.tail
    val eStm = if (eShape.isEmpty) StmCst(1, e) else e
    StmConcat(eStm, stm, stm1Shape = IntCst(1) +: eShape, stm2Shape = stmShape)
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
    val eStm = if (eShape.isEmpty) StmCst(1, e) else e
    StmConcat(stm, eStm, stm1Shape = stmShape, stm2Shape = IntCst(1) +: eShape)
  }
}

object StmPrefix {

  /** Take elements from the beginning of a stream.
    *
    * NOTE: k must be such that 0 &le; k &le; n.
    *
    * @param stm
    *   The input stream.
    * @param k
    *   The number of elements to extract.
    * @return
    *   A stream consisting of the first `k` elements from `stm`.
    */
  def apply(
      stm: Expr /* Stm<A, n> */,
      k: Expr /* Int */,
      // Ideally we would get this shape information from the type system
      shape: Seq[Expr]
  ): Expr /* Stm<A; k> */ = {
    val perRow = shape.tail.fold(IntCst(1))(_ * _)
    val s = Param("s")
    val i = Param("i")
    val j = Param("j")
    StmBuild(
      k * perRow,
      IfThenElse(i < k, SSome(StmNext(s)().__1)(), NNone(???))(),
      Map[Param, (Expr, Expr)](
        s -> (stm, StmNext(s)().__0),
        i -> (0, IfThenElse(j === perRow - 1, i + 1, i)()),
        j -> (0, IfThenElse(j === perRow - 1, 0, j + 1)())
      )
    )()
  }
}

object StmSuffix {

  /** Take elements from the end of a stream.
    *
    * NOTE: k must be such that 0 &le; k &le; n.
    *
    * @param stm
    *   The input stream.
    * @param k
    *   The number of elements to extract.
    * @return
    *   A stream consisting of the last `k` elements from `stm`.
    */
  def apply(
      stm: Expr /* Stm<A; n> */,
      k: Expr /* Int */,
      // Ideally we would get this shape info from the type system
      shape: Seq[Expr]
  ): StmBuild /* Stm<A; k> */ = {
    val n = shape.head
    val perRow = shape.tail.fold(IntCst(1))(_ * _)
    val s = Param("s")
    val i = Param("i")
    val j = Param("j")
    StmBuild(
      k * perRow,
      IfThenElse(i >= n - k, SSome(StmNext(s)().__1)(), NNone(???))(),
      Map[Param, (Expr, Expr)](
        s -> (stm, StmNext(s)().__0),
        i -> (0, IfThenElse(j === perRow - 1, i + 1, i)()),
        j -> (0, IfThenElse(j === perRow - 1, 0, j + 1)())
      )
    )()
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
      StmSuffix(stm, n - 1, shape = stmShape),
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
    StmPrepend(
      StmPrefix(stm, n - 1, shape = stmShape),
      e,
      stmShape = stmShape.updated(0, n - 1)
    )
  }
}

object StmConcat {
  def apply(
      stm1: Expr /* Stm<A; n1> */,
      stm2: Expr /* Stm<A; n2> */,
      // Ideally we would get this shape information from the type system
      stm1Shape: Seq[Expr],
      stm2Shape: Seq[Expr]
  ): Expr /* Stm<A; n+m> */ = {
    require(
      stm1Shape.length == stm2Shape.length,
      "the streams being concatenated should each have the same number of dimensions"
    )
    require(
      stm1Shape.tail == stm2Shape.tail,
      "the streams being concatenated should have the same size in each dimension except the first"
    )
    val n1 = stm1Shape.fold(IntCst(1))(_ * _)
    val n2 = stm2Shape.fold(IntCst(1))(_ * _)
    val s0 = Param("s0")
    val s1 = Param("s1")
    val i = Param("i")
    StmBuild(
      n1 + n2,
      SSome(IfThenElse(i === n1, StmNext(s1)().__1, StmNext(s0)().__1)())(),
      Map[Param, (Expr, Expr)](
        i -> (0, IfThenElse(i === n1, i, i + 1)()),
        s0 -> (stm1, IfThenElse(i === n1, s0, StmNext(s0)().__0)()),
        s1 -> (stm2, IfThenElse(i === n1, StmNext(s1)().__0, s1)())
      )
    )()
  }
}

// TODO: Disallow applying this to multi-dimensional streams?
object StmZip {
  def apply(
      a: Expr /* Stm<A; n> */,
      b: Expr /* Stm<B; n> */
  ): StmBuild /* Stm<(A, B); n> */ = {
    val s0 = Param("s0")
    val s1 = Param("s1")
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
    val s0 = Param("a")
    val s1 = Param("b")
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
    val v = Stm2Vec(stm, n = n)
    val i = Param("i")
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
    val v = Stm2Vec(stm, n = n)
    StmMap(
      v,
      (v: Expr) => Vec2Stm(VecReverse(v), n = n),
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
    val s = Param("s")
    val i = Param("i")
    val j = Param("j")
    val v = Param("v")
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
      (v: Expr) => Vec2Stm(v, n = m),
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
      Stm2Vec(stm, n = n * m),
      (v: Expr) =>
        Vec2Stm(VecJoin(VecTranspose(VecSplit(v, m = m))), n = n * m),
      n = 1,
      fInShape = None,
      fOutShape = Some(n * m)
    )
  }
}
