package operations

import opt.StmCanonPass
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
      case (None, None) => {
        // scalar -> scalar (e.g., x => x + 1)
        val x = Param("s")
        Function(
          x /* stream */,
          StmBuild(
            1,
            x,
            (acc: Expr) =>
              Tuple(StmNext(acc).__0, SSome(FunCall(f, StmNext(acc).__1)))
          )
        )
      }
      case (None, Some(_)) => {
        // scalar -> stream (e.g., c => StmCst(n, c), c => StmCountFrom(n, c))
        // The scalar input to the original function (`f.param`) can appear in
        // the seed of the stream (as in StmCountFrom), in the `nextF` (as in
        // StmCst), or even both (if you have something weird like a
        // StmBuild that constructs StmCountFrom(n, c) and StmCst(n, c) but
        // zipped together).
        // TODO: Deal with things like IfThenElse as well?
        // Canonicalize mainly to ensure flat accumulator for simplicity
        val s = StmCanonPass.canonicalize(f.body.asInstanceOf[StmBuild])
        val seed = s.seed.asInstanceOf[Tuple]
        val x = Param("s")
        Function(
          x /* stream */,
          StmBuild(
            s.n,
            Tuple(
              // Replace all seed elements that depend on the input scalar
              // (Maybe not strictly necessary in the interpreter since these
              // values are unused anyway, but in the compiler it wouldn't
              // make sense to have free variables hanging around here.)
              Tuple(
                seed.elems.map(e =>
                  if (e.contains(f.param)) DontCare else e
                ): _*
              ),
              x /* input stream */,
              DontCare /* element from input stream (initial value unused) */,
              True /* whether the element is yet to be read from x */
            ),
            (newAcc: Expr) => {
              val innerNext = Param("innerNext")
              val e = Let(
                innerNext,
                FunCall(s.nextF, newAcc.__0),
                IfThenElse(
                  newAcc.__3,
                  // First read from input stream
                  Tuple(
                    Tuple(
                      // Replace all seed elements that depend on the input scalar
                      Tuple(
                        seed.elems.zipWithIndex.map({ case (e, i) =>
                          if (e.contains(f.param))
                            e.substitute(f.param -> StmNext(newAcc.__1).__1)
                          else TupleAccess(newAcc.__0, i)
                        }): _*
                      ),
                      StmNext(newAcc.__1).__0,
                      StmNext(newAcc.__1).__1,
                      False
                    ),
                    NNone
                  ),
                  // Continue as usual
                  Tuple(
                    Tuple(
                      innerNext.__0,
                      newAcc.__1,
                      newAcc.__2,
                      newAcc.__3
                    ),
                    innerNext.__1
                  )
                )
              )
              e.substitute(
                Map[Expr, Expr](
                  s.nextF.param -> newAcc.__0,
                  f.param -> newAcc.__2
                )
              )
            }
          )
        )
      }
      case (Some(_), None) => {
        throw new IllegalArgumentException(
          "Reducing a stream to a scalar is forbidden."
        )
      }
      case (Some(_), Some(_)) => {
        // stream -> stream (e.g., StmMap, StmPrefix, StmSuffix)
        f
      }
    }
    // We need to be careful about the identity function.
    // We want to be able to reset `f` itself, but *not* the input stream.
    // TODO: does this issue occur in any cases other than the identity function?
    val identity: Function = (x: Expr) => x
    val f2 = if (f1 == identity) {
      val f2: Function = (s: Expr) =>
        StmBuild(
          outShape.getOrElse(IntCst(1)),
          s,
          (acc: Expr) => Tuple(StmNext(acc).__0, SSome(StmNext(acc).__1))
        )
      f2
    } else {
      f1
    }
    // It is essential to fuse everything.
    // If f is a chain of stream producers, we want to reset them all, not
    // just the last producer.
    (
      f2.param,
      StmCanonPass.canonicalize(f2.body.asInstanceOf[StmBuild].fuseCompletely())
    )
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
    val (s, inner) =
      Helpers.asStm2Stm(f, inShape = fInShape, outShape = fOutShape)
    // TODO: Deal with multiple input streams?
    // TODO: This check, as well as things like reordering the accumulator, is NOT reliable as currently written (e.g.,
    //       it may fail if `input` is a `Param`). In the real compiler, we should do these things based on type, not
    //       based on syntactic form.
    assert(
      inner.seed
        .asInstanceOf[Tuple]
        .elems
        .drop(1)
        .forall(e => !e.isInstanceOf[StmBuild]),
      "Function in StmMap must not take more than one stream as input."
    )
    val usesInputStream = inner.seed
      .asInstanceOf[Tuple]
      .elems
      .contains(s)
    if (!usesInputStream) {
      // In theory you could have something like StmMap(s, _ => 42) which doesn't actually depend on the input stream.
      // In that case, you can just forget about the input stream.
      StmRepeat(inner, n, inner.n)
    } else {
      // How many elements will the inner component read and produce before it must be reset?
      val numIn = fInShape.getOrElse(IntCst(1))
      val numOut = fOutShape.getOrElse(IntCst(1))
      val map = n match {
        // TODO: Why these special cases? Is it just to make the resulting stream easier to simplify?
        case IntCst(0) =>
          StmBuild(0, Tuple(), (_: Expr) => Tuple(Tuple(), NNone))
        case IntCst(1) =>
          // No need to reset
          inner
        case n =>
          // Build a new stream by repeating the inner one once it's done
          StmBuild(
            n * numOut,
            Tuple(inner.seed, numIn, numOut), {
              val newAcc = Param("acc")
              Function(
                newAcc,
                makeNextFBody(
                  oldBody = inner.nextF.body,
                  oldAcc = inner.nextF.param,
                  newAcc = newAcc,
                  oldSeed = inner.seed.asInstanceOf[Tuple],
                  numIn = numIn,
                  numOut = numOut
                )
              )
            }
          )
      }
      map.substitute(s -> input)
    }
  }

  private def makeNextFBody(
      oldBody: Expr,
      oldAcc: Param,
      newAcc: Param,
      oldSeed: Tuple,
      numIn: Expr,
      numOut: Expr
  ): Expr = {
    // Assume there is exactly one input stream and it can be found at
    // oldAcc.__0
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
            numIn = numIn,
            numOut = numOut
          ),
          makeNextFBody(
            oldBody = falseE,
            oldAcc = oldAcc,
            newAcc = newAcc,
            oldSeed = oldSeed,
            numIn = numIn,
            numOut = numOut
          )
        )
      case _: TupleAccess | _: VecAccess | _: StmNext | _: FunCall | _: Param =>
        ???
      case Tuple(a, out) =>
        val newInCtr = a match {
          case Tuple(
                TupleAccess(StmNext(TupleAccess(p, IntCst(0))), IntCst(0)),
                _ @_*
              ) if p == oldAcc =>
            // StmNext() called, so decrement the input counter.
            newAcc.__1 - 1
          case Tuple(TupleAccess(p, IntCst(0)), _ @_*) if p == oldAcc =>
            // StmNext() *not* called, so do *not* decrement the input counter.
            newAcc.__1
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
          (_: Expr) => newAcc.__2 - 1,
          // No output produced, so do not decrement the output counter.
          (_: Expr) => newAcc.__2
        )
        val newAccVal = IfThenElse(
          (newInCtr === 0) && (newOutCtr === 0),
          // Reset all accumulator elements except the input stream, reset counters
          Tuple(
            Tuple(a.asInstanceOf[Tuple].elems.head +: oldSeed.elems.tail: _*),
            numIn,
            numOut
          ),
          // Don't reset anything
          Tuple(a, newInCtr, newOutCtr)
        )
        Tuple(newAccVal, out)
      case _: IntExpr | _: BoolExpr | _: VecBuild | _: StmBuild | _: Function |
          _: Tuple =>
        throw new IllegalArgumentException(
          "Could not make StmMap body due to an apparent type error."
        )
    }
    e.substitute(oldAcc -> newAcc.__0)
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
      eShape: Seq[Expr]
  ): Expr /* Stm<A; n+1> */ = {
    val eStm = if (eShape.isEmpty) StmCst(1, e) else e
    StmConcat(eStm, stm)
  }
}

object StmAppend {
  def apply(
      stm: Expr /* Stm<A; n> */,
      e: Expr /* A */,
      // Ideally we would get this shape information from the type system
      eShape: Seq[Expr]
  ): Expr /* Stm<A; n+1> */ = {
    val eStm = if (eShape.isEmpty) StmCst(1, e) else e
    StmConcat(stm, eStm)
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
      // Assume shape of e matches shape of elements of stream (type system
      // should probably check this)
      eShape = stmShape.tail
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
    StmPrepend(
      StmPrefix(stm, stmShape.head - 1, shape = stmShape),
      e,
      eShape = stmShape.tail
    )
  }
}

object StmConcat {
  def apply(
      in1: Expr /* Stm<A; n> */,
      in2: Expr /* Stm<A; m> */
  ): Expr /* Stm<A; n+m> */ = {
    val n = StmLength(in1)
    val m = StmLength(in2)
    val s0 = Param("s0")
    val s1 = Param("s1")
    val i = Param("i")
    StmBuild(
      n + m,
      SSome(IfThenElse(i === n, StmNext(s1).__1, StmNext(s0).__1)),
      Map[Param, (Expr, Expr)](
        i -> (0, IfThenElse(i === n, i, i + 1)),
        s0 -> (in1, IfThenElse(i === n, s0, StmNext(s0).__0)),
        s1 -> (in2, IfThenElse(i === n, StmNext(s1).__0, s1))
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
