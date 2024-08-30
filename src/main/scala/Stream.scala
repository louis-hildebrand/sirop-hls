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
@tailrec
private def asStm2Stm(
    f: Function,
    inShape: Option[Int],
    outShape: Option[Int]
): Function = {
  // TODO: what about streams of vectors or streams of tuples?
  (inShape, outShape) match {
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
          // TODO: Here I use 0 as an initial value for whatever the element
          //       type is of x. The type checker will probably not be happy
          //       with that, but we can probably find the element type of x
          //       and then call some method that chooses a "zero" value given
          //       that data type.
          Tuple(
            // Replace all seed elements that depend on the input scalar
            // (Maybe not strictly necessary in the interpreter since these
            // values are unused anyway, but in the compiler it wouldn't
            // make sense to have free variables hanging around here.)
            Tuple(
              seed.elems.map(e =>
                if ExprEvaluator.contains(e, f.param) then 0 else e
              ): _*
            ),
            x /* input stream */,
            0 /* element from input stream (initial value unused) */,
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
                    innerNext.__1 /* same as other branch */,
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
      // stream -> scalar (e.g., StmFold, StmAccess)
      // The only way to collapse a stream s to a scalar is to call
      // StmNext(s).__1.
      // So we find the unique call to StmNext(s).__1, replace that with s,
      // and then rewrite the surrounding scalar expression to a StmMap.
      //
      // EDGE CASES:
      //  * What if there are multiple occurrences of StmNext(...).__1?
      //  * What if the programmer does something like StmNext(StmNext(...).__0).__1?
      //     * Assume it won't happen. There should be no way to get this expression starting from the higher-level IR.
      //  * What if there are more StmNext(...).__1 remaining after substitution (because there were StmNext(...).__1 calls inside the outer StmNext(...).__1)?
      //  * Could it ever happen that StmLength(s) > 1 and therefore we need to add StmPrefix(s, 1)?
      val nextCalls: Set[Expr] = findStmNext(f.body)
      if nextCalls.isEmpty then {
        assert(
          !ExprEvaluator.contains(f.body, f.param),
          "If StmNext() is never called in f, then surely the input stream is unused in f."
          // ... because how else can you convert a stream to a scalar?
        )
        // We can pretend the input is actually a scalar, since it's unused
        asStm2Stm(f, inShape = None, outShape = None)
      } else if nextCalls.size > 1 then {
        throw new IllegalArgumentException(
          "Multiple different calls to StmNext() found."
            + " Does the function take multiple streams as input?"
        )
      } else {
        val nextCall = nextCalls.head
        val stmBeingReduced =
          nextCall.asInstanceOf[TupleAccess].t.asInstanceOf[StmNext].stream
        Function(
          f.param,
          StmMap(
            stmBeingReduced,
            (x: Expr) => substitute(f.body)(Map(nextCall -> x)),
            n = 1,
            fInShape = None,
            fOutShape = None
          )
        )
      }
    }
    case (Some(_), Some(_)) => {
      // stream -> stream (e.g., StmMap, StmPrefix, StmSuffix)
      f
    }
  }
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
    case True | False                       => Set()
    case Equal(x, y)                        => findStmNext(x) ++ findStmNext(y)
    case NotEqual(x, y)                     => findStmNext(x) ++ findStmNext(y)
    case LessThan(x, y)                     => findStmNext(x) ++ findStmNext(y)
    case And(x, y)                          => findStmNext(x) ++ findStmNext(y)
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

// High-level function
object Iterate {
  def apply(
      n: Expr /* Int */,
      z: Expr /* A */,
      f: Function /* A -> A */,
      // TODO: Ideally we would get this shape information from the type system,
      zSize: Int
  ): Expr = {
    val s = StmBuild(
      1,
      Tuple(n, z),
      (acc: Expr) =>
        IfThenElse(
          acc.__0 === 0,
          Tuple(
            Tuple(acc.__0, expandTuple(acc.__1, zSize)),
            expandTuple(acc.__1, zSize),
            True
          ),
          Tuple(
            Tuple(acc.__0 - 1, FunCall(f, acc.__1)),
            expandTuple(acc.__1, zSize),
            False
          )
        )
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
  def apply(n: Int, m: Int, c: Int): Expr /* Stm<Stm<Int; m>; n> */ = {
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
  ): Expr /* Stm<B; n> */ = {
    // Instantiate `f` as a function from stream to stream
    val f1 = asStm2Stm(f, inShape = fInShape, outShape = fOutShape)
    // StmMap shouldn't be messing with the input stream, so we need to be
    // careful with the identity function
    // TODO: does this issue occur in any cases other than the identity function?
    val identity: Function = (x: Expr) => x
    val f2 = if f1 == identity then {
      val f2: Function = (s: Expr) =>
        StmBuild(
          0 /* doesn't matter, right? */,
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
    val f3 = Function(f2.param, ExprEvaluator.fuseCompletely(f2.body))
    // TODO: Needing to partially evaluate to even define StmMap seems
    //       pretty gross
    val inner =
      ExprEvaluator.canonicalize(
        ExprEvaluator.partialEval(FunCall(f3, input)).asInstanceOf[StmBuild]
      )
    val seed = inner.seed.asInstanceOf[Tuple]
    // TODO: Deal with multiple input streams?
    // TODO: This check, as well as things like reordering the accumulator, is NOT reliable as currently written (e.g.,
    //       it may fail if `input` is a `Param`). In the real compiler, we should do these things based on type, not
    //       based on syntactic form.
    assert(
      inner.seed
        .asInstanceOf[Tuple]
        .elems
        .tail
        .forall(e => !e.isInstanceOf[StmBuild]),
      "Function in StmMap must not take more than one stream as input."
    )
    // How many elements will the inner component read and produce before it
    // must be reset?
    val numIn = fInShape.getOrElse(1)
    val numOut = fOutShape.getOrElse(1)
    val innerUsesInputStm =
      seed.elems.head.isInstanceOf[StmBuild] || seed.elems.head == input
    // Build a new stream by repeating the inner one once it's done
    if innerUsesInputStm then {
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
    } else {
      val innerNext = Param()
      StmBuild(
        n * numOut,
        Tuple(inner.seed, numOut),
        (newAcc: Expr) =>
          substitute(
            Let(
              innerNext,
              FunCall(inner.nextF, newAcc.__0),
              IfThenElse(
                newAcc.__1 === 1,
                // Reset
                Tuple(Tuple(inner.seed, numOut), innerNext.__1, innerNext.__2),
                // Don't reset
                Tuple(
                  Tuple(innerNext.__0, newAcc.__1 - 1),
                  innerNext.__1,
                  innerNext.__2
                )
              )
            )
          )(Map(inner.nextF.param -> newAcc.__0))
      )
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
          "Could not fuse function bodies due to an apparent type error."
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
      n: Int
  ): Expr /* A */ = {
    // NOTE: require 0 <= i < n
    StmNext(
      StmBuild(
        1,
        Tuple(stm, 0),
        (acc: Expr) =>
          Tuple(
            Tuple(StmNext(acc.__0).__0, acc.__1 + 1),
            StmNext(acc.__0).__1,
            acc.__1 === i
          )
      )
    ).__1
  }
}

object StmFold {
  def apply(
      stream: Expr /*Stream<A>*/,
      z: Expr /*B*/,
      f: Function /*A -> B -> B*/,
      // TODO: Ideally we would get this shape info from the type system
      n: Int
  ): Expr = {
    Iterate(
      n,
      Tuple(z, stream),
      (acc: Expr) =>
        Tuple(
          FunCall(FunCall(f, StmNext(acc.__1).__1), acc.__0),
          StmNext(acc.__1).__0
        ),
      // TODO: this assumes `z` in `StmFold` is not a tuple (which happens to be the case in all tests so far)
      zSize = 2
    ).__0
  }
}

object StmScan {
  def apply(
      stm: Expr /* Stm<A; n> */,
      z: Expr /* B */,
      f: Expr => Expr => Expr /* A -> B -> B */,
      inclusive: Boolean
  ): Expr /* Vec<B; n> */ = {
    val next = Param()
    val y = Param()
    StmBuild(
      StmLength(stm),
      Tuple(stm, z),
      (acc: Expr) =>
        Let(
          next,
          StmNext(acc.__0),
          Let(
            y,
            f(next.__1)(acc.__1),
            Tuple(Tuple(next.__0, y), if inclusive then y else acc.__1, True)
          )
        )
    )
  }
}

object Vec2Stm {
  def apply(v: Expr /* Vec<A; n> */ ): Expr /* Stm<A; n> */ =
    // TODO: Would it be better to use a shift register for accessing the
    // input?
    StmBuild(
      VecLength(v),
      0,
      (i: Expr) => Tuple(i + 1, VecAccess(v, i), True)
    )
}

/////////////////////////
// dropping/adding elements
object StmPrepend {
  def apply(
      input: Expr /* Stm<A; n> */,
      e: Expr /* A */
  ): Expr /* Stm<A; n+1> */ = {
    val p = Param()
    StmBuild(
      StmLength(input) + 1,
      Tuple(True, input),
      (seed: Expr) => {
        IfThenElse(
          seed.__0,
          Tuple(Tuple(False, seed.__1), e, True),
          Let(p, StmNext(seed.__1), Tuple(Tuple(False, p.__0), p.__1, True))
        )
      }
    )
  }
}

object StmAppend {
  def apply(
      input: Expr /* Stm<A; n> */,
      e: Expr /* A */
  ): Expr /* Stm<A; n+1> */ = {
    val next = Param()
    StmBuild(
      StmLength(input) + 1,
      Tuple(input, StmLength(input)),
      (acc: Expr) =>
        IfThenElse(
          acc.__1 !== 0,
          Let(
            next,
            StmNext(acc.__0),
            Tuple(Tuple(next.__0, acc.__1 - 1), next.__1, True)
          ),
          // In theory we could collapse `Tuple(acc.__0, acc.__1)` to just
          // `acc`.
          // However, the stream fusion function would then be unable to get
          // back to the expanded form without knowing the type of `acc`, which
          // is not available in the interpreter :/
          Tuple(Tuple(acc.__0, acc.__1), e, True)
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
      k: Expr /* Int */
  ): Expr /* Stm<A; k> */ = {
    val next = Param()
    StmBuild(
      k,
      Tuple(stm, k),
      (acc: Expr) =>
        Let(
          next,
          StmNext(acc.__0),
          IfThenElse(
            acc.__1 === 0,
            // Fully drain the input
            Tuple(Tuple(next.__0, acc.__1), next.__1, False),
            Tuple(Tuple(next.__0, acc.__1 - 1), next.__1, True)
          )
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
    * @param n
    *   The number of elements in the input stream.
    * @return
    *   A stream consisting of the last `k` elements from `stm`.
    */
  def apply(
      stm: Expr /* Stm<A; n> */,
      k: Expr /* Int */,
      // TODO: Ideally we would get this shape info from the type system
      n: Int
  ): Expr /* Stm<A; k> */ = {
    val next = Param()
    StmBuild(
      k,
      Tuple(n - k, stm),
      (acc: Expr) =>
        Let(
          next,
          StmNext(acc.__1),
          IfThenElse(
            acc.__0 === 0,
            // keep
            Tuple(Tuple(acc.__0, next.__0), next.__1, True),
            // drop
            Tuple(Tuple(acc.__0 - 1, next.__0), next.__1, False)
          )
        )
    )
  }
}

object StmShiftLeft {
  def apply(
      stm: Expr /* Stm<A; n> */,
      e: Expr /* A */,
      // TODO: Ideally we would get this shape info from the type system
      n: Int
  ): Expr /* Stm<A; n> */ = {
    StmAppend(StmSuffix(stm, n - 1, n), e)
  }
}

object StmShiftRight {
  def apply(
      stm: Expr /* Stm<A; n> */,
      e: Expr /* A */
  ): Expr /* Stm<A; n> */ = {
    StmPrepend(StmPrefix(stm, StmLength(stm) - 1), e)
  }
}

/////////////////////////
// concat
object StmConcat {
  def apply(
      in1: Expr /* Stm<A; n> */,
      in2: Expr /* Stm<A; m> */
  ): Expr /* Stm<A; n+m> */ = {
    val p = Param()
    StmBuild(
      StmLength(in1) + StmLength(in2),
      Tuple(in1, in2, StmLength(in1)),
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

////////////////////////
// zip
object StmZip {
  def apply(
      a: Expr /* Stm<A; n> */,
      b: Expr /* Stm<B; n> */
  ): Expr /* Stm<(A, B); n> */ = {
    val nextA = Param()
    val nextB = Param()
    StmBuild(
      StmLength(a),
      Tuple(a, b),
      (acc: Expr) =>
        Let(
          nextA,
          StmNext(acc.__0),
          Let(
            nextB,
            StmNext(acc.__1),
            Tuple(
              Tuple(nextA.__0, nextB.__0),
              Tuple(nextA.__1, nextB.__1),
              True
            )
          )
        )
    )
  }
}

// Not particularly useful, just a strange case to be used to test the compiler
object StmZipAlternating {
  def apply(
      a: Expr /* Stm<A; n> */,
      b: Expr /* Stm<A; n> */
  ): Expr /* Stm<(A, A); n> */ = {
    val nextA = Param()
    val nextB = Param()
    StmBuild(
      StmLength(a),
      Tuple(a, b),
      (acc: Expr) =>
        Let(
          nextA,
          StmNext(acc.__0),
          Let(
            nextB,
            StmNext(acc.__1),
            Tuple(
              Tuple(nextB.__0, nextA.__0),
              Tuple(nextA.__1, nextB.__1),
              True
            )
          )
        )
    )
  }
}

////////////////////////
// repeat
object StmRepeat {
  def apply(stm: Expr /* Stm<A; n> */, m: Int): Expr /* Stm<Stm<A; n>; m> */ = {
    val v = Param()
    Let(
      v,
      Stm2Vec(stm),
      StmBuild(
        m,
        0 /* unused */,
        (i: Expr) => Tuple(0, Vec2Stm(v), True)
      )
    )
  }
}

object StmSplit {
  def apply(
      stm: Expr /* Stm<A; n> */,
      m: Int
  ): Expr /* Stm<Stm<A; m>; n/m> */ = {
    // TODO: Rewrite this in flat IR
    ???
  }
}

object StmJoin {
  def apply(stm: Expr /* Stm<Stm<A; m>; n> */ ): Expr /* Stm<A; m*n> */ = {
    // TODO: Rewrite this in flat IR
    ???
  }
}

object StmSlide {
  def apply(
      stm: Expr /* Stm<A; n> */,
      m: Int
  ): Expr /* Stm<Vec<A; m>, n-m+1> */ = {
    val nVal = ExprEvaluator.partialEval(StmLength(stm)).asInstanceOf[IntCst].i
    require(m <= nVal)
    require(m >= 1)
    val n = StmLength(stm)
    val next = Param()
    val v = Param()
    StmBuild(
      n - m + 1,
      Tuple(m - 1, stm, VecBuild(m, (i: Expr) => IntCst(0))),
      (acc: Expr) =>
        Let(
          next,
          StmNext(acc.__1),
          Let(
            v,
            VecShiftLeft(acc.__2, next.__1),
            IfThenElse(
              acc.__0 === 0,
              // shift register is full
              Tuple(Tuple(acc.__0, next.__0, v), v, True),
              // shift register is not full yet
              Tuple(Tuple(acc.__0 - 1, next.__0, v), v, False)
            )
          )
        )
    )
  }
}
