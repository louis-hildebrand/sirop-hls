// Helper functions
import ExprEvaluator.substitute

import scala.annotation.tailrec

private def expandTuple(t: Expr, n: Int): Tuple = {
  Tuple((0 until n).map(i => TupleAccess(t, i)): _*)
}

/** Convert the given function into a function from stream to stream.
  *
  * @param f
  *   The original function, which could be (1) scalar to scalar, (2) scalar to
  *   stream, (3) stream to scalar, or (4) stream to stream.
  * @param inShape
  *   Shape of the input to `f`. `Some(n)` means `f` takes a stream of `n`
  *   scalars. `None` means `f` takes one scalar.
  * @param outShape
  *   Shape of the output of `f`. `Some(n)` means `f` returns a stream of `n`
  *   scalars. `None` means `f` returns one scalar.
  */
private def asStm2Stm(
    f: Function,
    inShape: Option[Int],
    outShape: Option[Int]
): Function = {
  val f1 = (inShape, outShape) match {
    case (None, None) => {
      // scalar -> scalar (e.g., x => x + 1)
      val x = Param()
      Function(
        x /* stream */,
        StmBuild(
          1,
          x,
          (acc: Expr) =>
            Tuple(StmNext(acc).__0, FunCall(f, StmNext(acc).__1), True)
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
      val s = ExprEvaluator.canonicalize(f.body.asInstanceOf[StmBuild])
      val seed = s.seed.asInstanceOf[Tuple]
      val x = Param()
      Function(
        x /* stream */,
        StmBuild(
          s.length,
          Tuple(
            // Replace all seed elements that depend on the input scalar
            // (Maybe not strictly necessary in the interpreter since these
            // values are unused anyway, but in the compiler it wouldn't
            // make sense to have free variables hanging around here.)
            Tuple(
              seed.elems.map(e =>
                if ExprEvaluator.contains(e, f.param) then DontCare else e
              ): _*
            ),
            x /* input stream */,
            DontCare /* element from input stream (initial value unused) */,
            True /* whether the element is yet to be read from x */
          ),
          (newAcc: Expr) =>
            substitute({
              val innerNext = Param()
              Let(
                innerNext,
                FunCall(s.nextF, newAcc.__0),
                IfThenElse(
                  newAcc.__3,
                  // First read from input stream
                  Tuple(
                    Tuple(
                      // Replace all seed elements that depend on the input scalar
                      Tuple(
                        seed.elems.zipWithIndex.map((e, i) =>
                          if ExprEvaluator.contains(e, f.param) then
                            substitute(e)(
                              Map(f.param -> StmNext(newAcc.__1).__1)
                            )
                          else TupleAccess(newAcc.__0, i)
                        ): _*
                      ),
                      StmNext(newAcc.__1).__0,
                      StmNext(newAcc.__1).__1,
                      False
                    ),
                    DontCare,
                    False
                  ),
                  // Continue as usual
                  Tuple(
                    Tuple(
                      innerNext.__0,
                      newAcc.__1,
                      newAcc.__2,
                      newAcc.__3
                    ),
                    innerNext.__1,
                    innerNext.__2
                  )
                )
              )
            })(Map(s.nextF.param -> newAcc.__0, f.param -> newAcc.__2))
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
  val f2 = if f1 == identity then {
    val f2: Function = (s: Expr) =>
      StmBuild(
        outShape.getOrElse(1),
        s,
        (acc: Expr) => Tuple(StmNext(acc).__0, StmNext(acc).__1, True)
      )
    f2
  } else {
    f1
  }
  // It is essential to fuse everything.
  // If f is a chain of stream producers, we want to reset them all, not
  // just the last producer.
  val f3 = Function(
    f2.param,
    ExprEvaluator.canonicalize(ExprEvaluator.fuseCompletely(f2.body))
  )
  // StmMap(), StmScanInclusive(), etc. assume the function uses its input.
  val usesInputStream = f3.body
    .asInstanceOf[StmBuild]
    .seed
    .asInstanceOf[Tuple]
    .elems
    .contains(f3.param)
  val f4 = if usesInputStream then {
    f3
  } else {
    Function(
      f3.param,
      ExprEvaluator.fuseCompletely(
        StmConcat(f3.body, StmDrain(f3.param), len1 = outShape.getOrElse(1))
      )
    )
  }
  f4
}

private def findStmNext(e: Expr): Set[Expr] = {
  e match {
    case TupleAccess(StmNext(_), IntCst(1)) => Set(e)
    case _: IntCst                          => Set()
    case Add(x, y)                          => findStmNext(x) ++ findStmNext(y)
    case Sub(x, y)                          => findStmNext(x) ++ findStmNext(y)
    case Mul(x, y)                          => findStmNext(x) ++ findStmNext(y)
    case Div(x, y)                          => findStmNext(x) ++ findStmNext(y)
    case Mod(x, y)                          => findStmNext(x) ++ findStmNext(y)
    case True | False | DontCare            => Set()
    case Equal(x, y)                        => findStmNext(x) ++ findStmNext(y)
    case NotEqual(x, y)                     => findStmNext(x) ++ findStmNext(y)
    case LessThan(x, y)                     => findStmNext(x) ++ findStmNext(y)
    case And(x, y)                          => findStmNext(x) ++ findStmNext(y)
    case Or(x, y)                           => findStmNext(x) ++ findStmNext(y)
    case Not(x)                             => findStmNext(x)
    case IfThenElse(c, t, f) =>
      findStmNext(c) ++ findStmNext(t) ++ findStmNext(f)
    case Tuple(elems: _*) =>
      elems.foldLeft(Set[Expr]())((s, e) => s ++ findStmNext(e))
    case TupleAccess(t, i)     => findStmNext(t) ++ findStmNext(i)
    case _: Param              => Set()
    case Function(p: Param, b) => findStmNext(b)
    case FunCall(f, a)         => findStmNext(f) ++ findStmNext(a)
    // TODO: maybe these should be errors, since I don't expect them to ever happen
    case StmBuild(n, z, f) => findStmNext(n) ++ findStmNext(z) ++ findStmNext(f)
    case StmNext(s)        => findStmNext(s)
    case StmLength(s)      => findStmNext(s)
    case VecBuild(n, f)    => findStmNext(n) ++ findStmNext(f)
    case VecAccess(v, i)   => findStmNext(v) ++ findStmNext(i)
    case VecLength(v)      => findStmNext(v)
  }
}

// This is useful, for example, when StmMap() or StmFold() is called with a
// function that ignores its input. Concatenate the output of that function
// with this to get a new stream producer that has the same effect but empties
// the input.
object StmDrain {
  def apply(stm: Expr /* Stm<A; n> */ ): Expr /* Stm<A; 0> */ = {
    StmBuild(
      0,
      stm,
      (acc: Expr) => Tuple(StmNext(acc).__0, StmNext(acc).__1, False)
    )
  }
}

// High-level function
object Iterate {
  def apply(
      n: Expr /* Int */,
      z: Expr /* A */,
      f: Function /* A -> A */,
      // TODO: Ideally we would get this shape information from the type system,
      zSize: Option[Int]
  ): Expr = {
    val s = StmBuild(
      1,
      Tuple(n, z),
      (acc: Expr) => {
        val acc1Expanded = zSize match {
          case Some(n) => expandTuple(acc.__1, n)
          case None    => acc.__1
        }
        IfThenElse(
          acc.__0 === 0,
          Tuple(
            Tuple(acc.__0, acc1Expanded),
            acc1Expanded,
            True
          ),
          Tuple(
            Tuple(acc.__0 - 1, FunCall(f, acc.__1)),
            acc1Expanded,
            False
          )
        )
      }
    )
    StmNext(s).__1
  }
}

//////////////////////////
// creating streams

object StmCst {
  def apply(n: IntCst, c: Expr): Expr /* Stm<Int; n> */ =
    StmBuild(n, Tuple(), (_: Expr) => Tuple(Tuple(), c, True))
}

object StmCount {
  def apply(n: IntCst): Expr /* Stm<Int; n> */ =
    StmBuild(n, 0, (i: Expr) => Tuple(i + 1, i, True))
}

object StmCountFrom {
  def apply(start: Expr, n: Expr): Expr = {
    StmBuild(n, start, (acc: Expr) => Tuple(acc + 1, acc, True))
  }
}

object StmCst2D {
  def apply(n: Expr, m: Expr, c: Expr): Expr /* Stm<Stm<Int; m>; n> */ = {
    StmBuild(
      n * m,
      Tuple(),
      (_: Expr) => Tuple(Tuple(), c, True)
    )
  }
}

object StmCount2D {
  def apply(n: Int, m: Int): Expr /* Stm<Stm<Int; m>; n> */ = {
    StmBuild(
      n * m,
      Tuple(0, 0),
      (acc: Expr) =>
        Tuple(
          IfThenElse(
            acc.__1 === m - 1,
            Tuple(acc.__0 + 1, 0),
            Tuple(acc.__0, acc.__1 + 1)
          ),
          Tuple(acc.__0, acc.__1),
          True
        )
    )
  }
}

//////////////////////////
// manipulating streams

object StmMap {
  def apply(
      input: Expr /* Stm<A; n> */,
      f: Function /* A -> B */,
      // TODO: Ideally we would get this shape info from the type system
      n: Int,
      fInShape: Option[Int],
      fOutShape: Option[Int]
  ): StmBuild /* Stm<B; n> */ = {
    // Instantiate `f` as a function from stream to stream
    val stmF = asStm2Stm(f, inShape = fInShape, outShape = fOutShape)
    // TODO: Needing to partially evaluate to even define StmMap seems
    //       pretty gross
    val inner =
      ExprEvaluator.canonicalize(
        ExprEvaluator.partialEval(FunCall(stmF, input)).asInstanceOf[StmBuild]
      )
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
    n match {
      case 0 =>
        StmBuild(0, Tuple(), (acc: Expr) => Tuple(Tuple(), DontCare, True))
      case 1 =>
        // No need to reset
        inner
      case n if n > 1 =>
        // How many elements will the inner component read and produce before it
        // must be reset?
        val numIn = fInShape.getOrElse(1)
        val numOut = fOutShape.getOrElse(1)
        // Build a new stream by repeating the inner one once it's done
        StmBuild(
          n * numOut,
          Tuple(inner.seed, numIn, numOut), {
            val newAcc = Param()
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
      case _ =>
        throw new IllegalArgumentException(s"Invalid input to StmMap: n = ${n}")
    }
  }

  private def makeNextFBody(
      oldBody: Expr,
      oldAcc: Param,
      newAcc: Param,
      oldSeed: Tuple,
      numIn: Int,
      numOut: Int
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
      case Tuple(a, e, valid) =>
        val newInCtr = a match {
          case Tuple(
                TupleAccess(StmNext(TupleAccess(p, IntCst(0))), IntCst(0)),
                _: _*
              ) if p == oldAcc =>
            // StmNext() called, so decrement the input counter.
            newAcc.__1 - 1
          case Tuple(TupleAccess(p, IntCst(0)), _: _*) if p == oldAcc =>
            // StmNext() *not* called, so do *not* decrement the input counter.
            newAcc.__1
          case Tuple(x, _: _*) =>
            throw new IllegalArgumentException(
              s"I can't tell whether StmNext() is being called in ${x} (where oldAcc = ${oldAcc})."
            )
          case _ => ???
        }
        val newOutCtr = IfThenElse(
          valid,
          // Output produced, so decrement the output counter.
          newAcc.__2 - 1,
          // No output produced, so do not decrement the output counter.
          newAcc.__2
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
        Tuple(newAccVal, e, valid)
      case _: IntExpr | _: BoolExpr | _: VecBuild | _: StmBuild | _: Function |
          _: Tuple =>
        throw new IllegalArgumentException(
          "Could not make StmMap body due to an apparent type error."
        )
    }
    substitute(e)(Map(oldAcc -> newAcc.__0))
  }
}

//////////////////////////
// reductions
object StmAccess {
  def apply(
      stm: Expr /* Stm<A; n> */,
      i: Expr /* Int */,
      // TODO: Ideally we would get this shape info from the type system
      shape: Seq[Int]
  ): Expr /* A */ = {
    // NOTE: require 0 <= i < n
    val perRow = shape.tail.product
    StmBuild(
      perRow,
      Tuple(stm, 0, perRow),
      (acc: Expr) =>
        Tuple(
          IfThenElse(
            acc.__2 === 1,
            Tuple(StmNext(acc.__0).__0, acc.__1 + 1, perRow),
            Tuple(StmNext(acc.__0).__0, acc.__1, acc.__2 - 1)
          ),
          StmNext(acc.__0).__1,
          acc.__1 === i
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
      stmShape: Seq[Int]
  ): Expr /* Stm<B; 1> */ = {
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
      stream: Expr /* Stream<A> */,
      z: Expr /* B */,
      f: Function /* B -> A -> B */,
      // Ideally we would get this shape info from the type system
      stmShape: Seq[Int]
  ): Expr = {
    // TODO: Enforce the restriction that the accumulator cannot contain any streams?
    val stmF =
      asStm2Stm(
        f.body.asInstanceOf[Function],
        inShape =
          if stmShape.tail.isEmpty then None else Some(stmShape.tail.product),
        outShape = if stmShape.tail.isEmpty then None else Some(1)
      )
    val inner = ExprEvaluator.canonicalize(
      ExprEvaluator.partialEval(FunCall(stmF, stream)).asInstanceOf[StmBuild]
    )
    val numIn = stmShape.tail.product
    StmBuild(
      stmShape.head,
      Tuple(inner.seed, z, numIn, 1), {
        val newAcc = Param()
        Function(
          newAcc,
          substitute(
            makeNextFBody(
              oldBody = inner.nextF.body,
              oldAcc = inner.nextF.param,
              newAcc = newAcc,
              oldSeed = inner.seed.asInstanceOf[Tuple],
              numIn = numIn
            )
          )(Map(f.param -> newAcc.__1))
        )
      }
    )
  }

  private def makeNextFBody(
      oldBody: Expr,
      oldAcc: Param,
      newAcc: Param,
      oldSeed: Tuple,
      numIn: Int
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
      case Tuple(a, e, valid) =>
        val newInCtr = a match {
          case Tuple(
                TupleAccess(StmNext(TupleAccess(p, IntCst(0))), IntCst(0)),
                _: _*
              ) if p == oldAcc =>
            // StmNext() called, so decrement the input counter.
            newAcc.__2 - 1
          case Tuple(TupleAccess(p, IntCst(0)), _: _*) if p == oldAcc =>
            // StmNext() *not* called, so do *not* decrement the input counter.
            newAcc.__2
          case Tuple(x, _: _*) =>
            throw new IllegalArgumentException(
              s"I can't tell whether StmNext() is being called in ${x} (where oldAcc = ${oldAcc})."
            )
          case _ => ???
        }
        val newOutCtr = IfThenElse(
          valid,
          // Output produced, so decrement the output counter.
          newAcc.__3 - 1,
          // No output produced, so do not decrement the output counter.
          newAcc.__3
        )
        val newAccVal =
          IfThenElse(
            (newInCtr === 0) && (newOutCtr === 0),
            // Reset
            Tuple(
              // Never reset the input stream
              Tuple(a.asInstanceOf[Tuple].elems.head +: oldSeed.elems.tail: _*),
              IfThenElse(valid, e, newAcc.__1),
              numIn,
              1
            ),
            // No reset
            Tuple(
              a,
              IfThenElse(valid, e, newAcc.__1),
              newInCtr,
              newOutCtr
            )
          )
        val newValid = (newInCtr === 0) && (newOutCtr === 0)
        Tuple(newAccVal, IfThenElse(valid, e, newAcc.__1), newValid)
      case _: TupleAccess | _: VecAccess | _: StmNext | _: FunCall | _: Param =>
        ???
      case _: IntExpr | _: BoolExpr | _: VecBuild | _: StmBuild | _: Function |
          _: Tuple =>
        throw new IllegalArgumentException(
          "Could not make StmFold body due to an apparent type error."
        )
    }
    substitute(e)(Map(oldAcc -> newAcc.__0))
  }
}

object StmScanExclusive {
  def apply(
      stm: Expr /* Stm<A; n> */,
      z: Expr /* B */,
      f: Expr => Expr => Expr /* B -> A -> B */,
      // Ideally we would get this shape info from the type system
      stmShape: Seq[Int]
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
  def apply(v: Expr /* Vec<A; n> */, n: Int): Expr /* Stm<A; n> */ =
    // TODO: Would it be better to use a shift register for accessing the
    //       input?
    StmBuild(n, 0, (i: Expr) => Tuple(i + 1, VecAccess(v, i), True))
}

/////////////////////////
// dropping/adding elements
object StmPrepend {
  def apply(
      stm: Expr /* Stm<A; n> */,
      e: Expr /* A */,
      // Ideally we would get this shape info from the type system
      eShape: Seq[Int]
  ): Expr /* Stm<A; n+1> */ = {
    val p = Param()
    val eStm = if eShape.isEmpty then {
      StmBuild(1, Tuple(), (_: Expr) => Tuple(Tuple(), e, True))
    } else {
      e
    }
    StmBuild(
      StmLength(stm) + eShape.product,
      Tuple(eStm, stm, eShape.product),
      (acc: Expr) => {
        IfThenElse(
          acc.__2 === 0,
          // Read from stm
          Tuple(
            Tuple(acc.__0, StmNext(acc.__1).__0, acc.__2),
            StmNext(acc.__1).__1,
            True
          ),
          // Read from eStm
          Tuple(
            Tuple(StmNext(acc.__0).__0, acc.__1, acc.__2 - 1),
            StmNext(acc.__0).__1,
            True
          )
        )
      }
    )
  }
}

object StmAppend {
  def apply(
      stm: Expr /* Stm<A; n> */,
      e: Expr /* A */,
      // Ideally we would get this shape information from the type system
      stmShape: Seq[Int]
  ): Expr /* Stm<A; n+1> */ = {
    val eStm = if stmShape.tail.isEmpty then {
      StmBuild(1, Tuple(), (_: Expr) => Tuple(Tuple(), e, True))
    } else {
      e
    }
    StmBuild(
      stmShape.updated(0, stmShape.head + 1).product,
      Tuple(stm, eStm, stmShape.product),
      (acc: Expr) =>
        IfThenElse(
          acc.__2 === 0,
          // Take from eStm
          Tuple(
            Tuple(acc.__0, StmNext(acc.__1).__0, acc.__2),
            StmNext(acc.__1).__1,
            True
          ),
          // Take from stm
          Tuple(
            Tuple(StmNext(acc.__0).__0, acc.__1, acc.__2 - 1),
            StmNext(acc.__0).__1,
            True
          )
        )
    )
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
      shape: Seq[Int]
  ): Expr /* Stm<A; k> */ = {
    val perRow = shape.tail.product
    StmBuild(
      k * perRow,
      Tuple(stm, 0, perRow),
      (acc: Expr) =>
        Tuple(
          IfThenElse(
            acc.__2 === 1,
            Tuple(StmNext(acc.__0).__0, acc.__1 + 1, perRow),
            Tuple(StmNext(acc.__0).__0, acc.__1, acc.__2 - 1)
          ),
          StmNext(acc.__0).__1,
          acc.__1 < k
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
      shape: Seq[Int]
  ): Expr /* Stm<A; k> */ = {
    val perRow = shape.tail.product
    val n = shape.head
    StmBuild(
      k * perRow,
      Tuple(stm, 0, perRow),
      (acc: Expr) =>
        Tuple(
          IfThenElse(
            acc.__2 === 1,
            Tuple(StmNext(acc.__0).__0, acc.__1 + 1, perRow),
            Tuple(StmNext(acc.__0).__0, acc.__1, acc.__2 - 1)
          ),
          StmNext(acc.__0).__1,
          acc.__1 >= n - k
        )
    )
  }
}

object StmShiftLeft {
  def apply(
      stm: Expr /* Stm<A; n> */,
      e: Expr /* A */,
      // Ideally we would get this shape info from the type system
      stmShape: Seq[Int]
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
      stmShape: Seq[Int]
  ): Expr /* Stm<A; n> */ = {
    StmPrepend(
      StmPrefix(stm, stmShape.head - 1, shape = stmShape),
      e,
      eShape = stmShape.tail
    )
  }
}

/////////////////////////
// concat
object StmConcat {
  def apply(
      in1: Expr /* Stm<A; n> */,
      in2: Expr /* Stm<A; m> */,
      len1: Int
  ): Expr /* Stm<A; n+m> */ = {
    val p = Param()
    StmBuild(
      StmLength(in1) + StmLength(in2),
      Tuple(in1, in2, len1),
      (seed: Expr) =>
        IfThenElse(
          GreaterThan(seed.__2, 0),
          Let(
            p,
            StmNext(seed.__0),
            Tuple(Tuple(p.__0, seed.__1, seed.__2 - 1), p.__1, True)
          ),
          Let(
            p,
            StmNext(seed.__1),
            Tuple(Tuple(seed.__0, p.__0, seed.__2), p.__1, True)
          )
        )
    )
  }
}

// TODO: Disallow applying this to multi-dimensional streams?
object StmZip {
  def apply(
      a: Expr /* Stm<A; n> */,
      b: Expr /* Stm<B; n> */
  ): Expr /* Stm<(A, B); n> */ = {
    StmBuild(
      StmLength(a),
      Tuple(a, b),
      (acc: Expr) =>
        Tuple(
          Tuple(StmNext(acc.__0).__0, StmNext(acc.__1).__0),
          Tuple(StmNext(acc.__0).__1, StmNext(acc.__1).__1),
          True
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
    StmBuild(
      StmLength(a),
      Tuple(a, b),
      (acc: Expr) =>
        Tuple(
          Tuple(StmNext(acc.__1).__0, StmNext(acc.__0).__0),
          Tuple(StmNext(acc.__0).__1, StmNext(acc.__1).__1),
          True
        )
    )
  }
}

////////////////////////
// repeat
object StmRepeat {
  def apply(
      stm: Expr /* Stm<A; n> */,
      m: Int,
      // Ideally we would get this shape info from the type system
      n: Int
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
            VecAccess(acc.__1, acc.__2),
            False
          ),
          // Shift register is full
          Tuple(
            Tuple(
              acc.__0,
              acc.__1,
              IfThenElse(acc.__2 === n - 1, 0, acc.__2 + 1),
              False
            ),
            VecAccess(acc.__1, acc.__2),
            True
          )
        )
    )
  }
}

object StmSplit {
  def apply(
      stm: Expr /* Stm<A; n> */,
      m: Int
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
    * @param stm
    *   A stream of length n.
    * @param m
    *   Window size.
    */
  def apply(
      stm: Expr /* Stm<A; n> */,
      m: Int,
      // Ideally we would get this shape info from the type system
      stmShape: Seq[Int]
  ): Expr /* Stm<Vec<A; m>, n-m+1> */ = {
    val n = stmShape.head
    val elemSize = stmShape.tail.product
    val v = Param()
    StmBuild(
      n - m + 1,
      Tuple(
        stm,
        // Number of window elements left to load
        m - 1,
        // How many pieces of data left to load in the current window?
        // This will always be 1 if `stm` is flat, but may be greater than 1
        // if `stm` is multi-dimensional.
        elemSize,
        VecBuild(m * elemSize, (i: Expr) => DontCare)
      ),
      (acc: Expr) =>
        Let(
          v,
          VecShiftLeft(acc.__3, StmNext(acc.__0).__1),
          IfThenElse(
            (acc.__1 === 0) && (acc.__2 === 1),
            // CASE 1: Shift register is full
            Tuple(
              Tuple(StmNext(acc.__0).__0, acc.__1, elemSize, v),
              v,
              True
            ),
            // CASE 2: Shift register is not full yet
            Tuple(
              IfThenElse(
                acc.__1 === 0,
                // CASE 2a: Initial loading is done, just loading the next
                //          element. Note that each element may be a stream,
                //          so this may take multiple cycles.
                Tuple(StmNext(acc.__0).__0, acc.__1, acc.__2 - 1, v),
                // CASE 2b: Initial loading still in progress.
                IfThenElse(
                  acc.__2 === 1,
                  Tuple(StmNext(acc.__0).__0, acc.__1 - 1, elemSize, v),
                  Tuple(StmNext(acc.__0).__0, acc.__1, acc.__2 - 1, v)
                )
              ),
              v,
              False
            )
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
      stmShape: Seq[Int]
  ): Expr /* Stm<Stm<A; m>; n-m+1> */ = {
    // TODO: Optimize this version specifically by producing elements while
    //       the shift register is still filling up?
    val s = StmSlideV(stm, m, stmShape = stmShape)
    StmMap(
      s,
      (v: Expr) => Vec2Stm(v, n = m),
      n = stmShape.head - m + 1,
      fInShape = None,
      fOutShape = Some(m * stmShape.tail.product)
    )
  }
}

object StmTranspose {
  def apply(
      stm: Expr /* Stm<Stm<A; m>; n> */,
      // Ideally we would get this shape info from the type system
      n: Int,
      m: Int
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
