package operations

import ir._

private object Helpers {
  def expandTuple(t: Expr, n: Int): Tuple = {
    Tuple((0 until n).map(i => TupleAccess(t, i)): _*)
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
          StmBuild(
            1,
            SSome(f.body.substitute(f.param -> StmNext(s).__1)),
            Map[Param, (Expr, Expr)](s -> (x, StmNext(s).__0))
          )
        )
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
                z.substitute(f.param -> StmNext(s).__1),
                x
              )
            })
            .foldLeft(Map[Expr, Expr]())(_ + _)
            + (f.param -> IfThenElse(isFirstStep, StmNext(s).__1, y))
        )
        val equationsToAdd = Map[Param, (Expr, Expr)](
          // Input stream
          s -> (input, IfThenElse(isFirstStep, StmNext(s).__0, s)),
          // Whether we still need to read from the input stream
          isFirstStep -> (True, False),
          // Register for the value from the input stream
          y -> (DontCare, IfThenElse(isFirstStep, StmNext(s).__1, y))
        )
        val updatedOldEquations = stm.nextByVar.map({ case (x, next) =>
          x -> (DontCare, next.substitute(subs))
        })
        val newF = Function(
          input /* stream */,
          StmBuild(
            stm.n,
            stm.output.substitute(subs),
            updatedOldEquations ++ equationsToAdd
          )
        )
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
        StmBuild(
          outShape.getOrElse(IntCst(1)),
          SSome(StmNext(s).__1),
          Map[Param, (Expr, Expr)](s -> (x, StmNext(s).__0))
        )
      )
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
      IfThenElse(i === 0, SSome(accExpanded), NNone),
      Map[Param, (Expr, Expr)](
        i -> (n, IfThenElse(i === 0, 0, i - 1)),
        acc -> (z, IfThenElse(i === 0, accExpanded, FunCall(f, accExpanded)))
      )
    )
    StmNext(s).__1
  }
}

object StmCst {
  def apply(n: Expr, c: Expr): Expr /* Stm<Int; n> */ = {
    StmBuild(n, SSome(c), Map[Param, (Expr, Expr)]())
  }
}

object StmCount {
  def apply(n: Expr): Expr /* Stm<Int; n> */ = StmRange(n, 0, 1)
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
  def apply(n: Expr, z: Expr, delta: Expr): Expr = {
    val a = Param("a")
    StmBuild(n, SSome(a), Map(a -> (z, a + delta)))
  }
}

object StmCst2D {
  def apply(n: Expr, m: Expr, c: Expr): Expr /* Stm<Stm<Int; m>; n> */ = {
    StmBuild(n * m, SSome(c), Map[Param, (Expr, Expr)]())
  }
}

object StmCount2D {
  def apply(n: Expr, m: Expr): Expr /* Stm<Stm<Int; m>; n> */ = {
    val i = Param("i")
    val j = Param("j")
    StmBuild(
      n * m,
      SSome(Tuple(i, j)),
      Map[Param, (Expr, Expr)](
        i -> (0, IfThenElse(j === m - 1, i + 1, i)),
        j -> (0, IfThenElse(j === m - 1, 0, j + 1))
      )
    )
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
      innerStm.freeVars() - s == f.freeVars(),
      "no new free variables should have been introduced by asStm2Stm"
    )
    assert(
      innerStm.seedByVar.count({ case (_, z) => z == s }) <= 1,
      "the input stream should appear no more than once in the inner StmBuild"
    )
    if (!innerStm.seedByVar.values.exists(z => z == s)) {
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
        // TODO: Why these special cases? Is it just to make the resulting stream
        //       easier to simplify? Should I add something similar in the
        //       partial evaluator?
        case IntCst(0) =>
          StmBuild(0, NNone, Map[Param, (Expr, Expr)]())
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
          assert(
            innerWithCtrs.freeVars() - s == f.freeVars(),
            "no new free variables should have been introduced by adding I/O counters"
          )
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
                val nextOrReset = IfThenElse(shouldReset, z, next)
                x -> (z, nextOrReset)
              }
            })
          )
          assert(
            outerStm.freeVars() - s == f.freeVars(),
            "no new free variables should have been introduced by resetting"
          )
          outerStm
      }
      map.substitute(s -> input)
    }
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
      IfThenElse(i === k, SSome(StmNext(s).__1), NNone),
      Map[Param, (Expr, Expr)](
        s -> (stm, StmNext(s).__0),
        i -> (0, IfThenElse(j === perRow - 1, i + 1, i)),
        j -> (0, IfThenElse(j === perRow - 1, 0, j + 1))
      )
    )
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
    val (s, inner) =
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
    assert(inner.n == IntCst(1))
    val usesInputStream = inner.seed
      .asInstanceOf[Tuple]
      .elems
      .contains(s)
    if (!usesInputStream) {
      assert(inner.seed == Tuple())
      val e = inner.nextF.body match {
        case Tuple(Tuple(), Tuple(e, True)) => e
        case _                              => ???
      }
      StmBuild(
        stmShape.head,
        z, {
          val newAcc = Param("acc")
          val x = Param("acc")
          Function(
            newAcc,
            Let(
              x,
              e.substitute(f.param -> newAcc),
              Tuple(x, SSome(x))
            )
          )
        }
      )
    } else {
      val numIn = stmShape.tail.fold(IntCst(1))((x, y) => x * y)
      val scan = StmBuild(
        stmShape.head,
        Tuple(
          inner.seed, /* Accumulator for the inner function */
          z,
          numIn, /* Input counter */
          1 /* Output counter */
        ), {
          val newAcc = Param("acc")
          Function(
            newAcc,
            makeNextFBody(
              oldBody = inner.nextF.body,
              oldAcc = inner.nextF.param,
              newAcc = newAcc,
              oldSeed = inner.seed.asInstanceOf[Tuple],
              numIn = numIn
            ).substitute(f.param -> newAcc.__1)
          )
        }
      )
      scan.substitute(s -> input)
    }
  }

  private def makeNextFBody(
      oldBody: Expr,
      oldAcc: Param,
      newAcc: Param,
      oldSeed: Tuple,
      numIn: Expr
  ): Expr = {
    val e = oldBody match {
      case DontCare => DontCare
      case IfThenElse(cond, trueE, falseE) =>
        IfThenElse(
          cond,
          makeNextFBody(
            oldBody = trueE,
            oldAcc = oldAcc,
            newAcc = newAcc,
            oldSeed = oldSeed,
            numIn = numIn
          ),
          makeNextFBody(
            oldBody = falseE,
            oldAcc = oldAcc,
            newAcc = newAcc,
            oldSeed = oldSeed,
            numIn = numIn
          )
        )
      case Tuple(a, out) =>
        val newInCtr = a match {
          case Tuple(
                TupleAccess(StmNext(TupleAccess(p, IntCst(0))), IntCst(0)),
                _ @_*
              ) if p == oldAcc =>
            // StmNext() called, so decrement the input counter.
            newAcc.__2 - 1
          case Tuple(TupleAccess(p, IntCst(0)), _ @_*) if p == oldAcc =>
            // StmNext() *not* called, so do *not* decrement the input counter.
            newAcc.__2
          case Tuple(x, _ @_*) =>
            throw new IllegalArgumentException(
              s"I can't tell whether StmNext() is being called in ${x} (where oldAcc = ${oldAcc})."
            )
          case e =>
            throw new IllegalArgumentException(
              s"Expected the new accumulator value to be a tuple literal with at least one element, but found $e"
            )
        }
        val newOutCtr = OptionAccess(
          out,
          // Output produced, so decrement the output counter.
          (_: Expr) => newAcc.__3 - 1,
          // No output produced, so do not decrement the output counter.
          (_: Expr) => newAcc.__3
        )
        val output = OptionAccess(out, (e: Expr) => e, (_: Expr) => newAcc.__1)
        val newAccVal =
          IfThenElse(
            (newInCtr === 0) && (newOutCtr === 0),
            // Reset
            Tuple(
              // Never reset the input stream
              Tuple(a.asInstanceOf[Tuple].elems.head +: oldSeed.elems.tail: _*),
              output,
              numIn,
              1
            ),
            // No reset
            Tuple(
              a,
              output,
              newInCtr,
              newOutCtr
            )
          )
        val newValid = (newInCtr === 0) && (newOutCtr === 0)
        Tuple(newAccVal, IfThenElse(newValid, SSome(output), NNone))
      case _: TupleAccess | _: VecAccess | _: StmNext | _: FunCall | _: Param =>
        ???
      case _: IntExpr | _: BoolExpr | _: VecBuild | _: StmBuild | _: Function |
          _: Tuple =>
        throw new IllegalArgumentException(
          "Could not make StmFold body due to an apparent type error."
        )
    }
    e.substitute(oldAcc -> newAcc.__0)
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
      SSome(VecAccess(v, i)),
      Map[Param, (Expr, Expr)](i -> (0, i + 1))
    )
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
      IfThenElse(i < k, SSome(StmNext(s).__1), NNone),
      Map[Param, (Expr, Expr)](
        s -> (stm, StmNext(s).__0),
        i -> (0, IfThenElse(j === perRow - 1, i + 1, i)),
        j -> (0, IfThenElse(j === perRow - 1, 0, j + 1))
      )
    )
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
      IfThenElse(i >= n - k, SSome(StmNext(s).__1), NNone),
      Map[Param, (Expr, Expr)](
        s -> (stm, StmNext(s).__0),
        i -> (0, IfThenElse(j === perRow - 1, i + 1, i)),
        j -> (0, IfThenElse(j === perRow - 1, 0, j + 1))
      )
    )

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
      SSome(IfThenElse(i === n1, StmNext(s1).__1, StmNext(s0).__1)),
      Map[Param, (Expr, Expr)](
        i -> (0, IfThenElse(i === n1, i, i + 1)),
        s0 -> (stm1, IfThenElse(i === n1, s0, StmNext(s0).__0)),
        s1 -> (stm2, IfThenElse(i === n1, StmNext(s1).__0, s1))
      )
    )
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
      StmLength(a),
      SSome(Tuple(StmNext(s0).__1, StmNext(s1).__1)),
      Map[Param, (Expr, Expr)](
        s0 -> (a, StmNext(s0).__0),
        s1 -> (b, StmNext(s1).__0)
      )
    )
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
      StmLength(a),
      SSome(Tuple(StmNext(s0).__1, StmNext(s1).__1)),
      Map(
        s0 -> (a, StmNext(s1).__0),
        s1 -> (b, StmNext(s0).__0)
      )
    )
  }
}

object StmRepeat {
  def apply(
      stm: Expr /* Stm<A; n> */,
      m: Expr,
      // Ideally we would get this shape info from the type system
      n: Expr
  ): Expr /* Stm<Stm<A; n>; m> */ = {
    // TODO: Implement using Stm2Vec instead?
    StmBuild(
      n * m,
      Tuple(stm, VecBuild(n, (_: Expr) => DontCare), 0, True),
      (acc: Expr) =>
        IfThenElse(
          acc.__3,
          // First fill shift register
          Tuple(
            IfThenElse(
              acc.__2 === n - 1,
              Tuple(
                StmNext(acc.__0).__0,
                VecShiftLeft(acc.__1, StmNext(acc.__0).__1),
                0,
                False
              ),
              Tuple(
                StmNext(acc.__0).__0,
                VecShiftLeft(acc.__1, StmNext(acc.__0).__1),
                acc.__2 + 1,
                True
              )
            ),
            NNone
          ),
          // Shift register is full
          Tuple(
            Tuple(
              acc.__0,
              acc.__1,
              IfThenElse(acc.__2 === n - 1, 0, acc.__2 + 1),
              False
            ),
            SSome(VecAccess(acc.__1, acc.__2))
          )
        )
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
        SSome(VecShiftLeft(v, StmNext(s).__1)),
        // CASE 2: Shift register is not full yet.
        //         Wait until it is.
        NNone
      ),
      Map[Param, (Expr, Expr)](
        s -> (input, StmNext(s).__0),
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
              IfThenElse(j === 1, i - 1, i)
            )
          )
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
              IfThenElse(j === 1, elemSize, j - 1)
            )
          )
        ),
        v -> (
          VecBuild(m * elemSize, (_: Expr) => DontCare),
          VecShiftLeft(v, StmNext(s).__1)
        )
      )
    )
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
