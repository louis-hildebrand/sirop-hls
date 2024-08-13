// High-level function
object Iterate {
  def apply(
      n: Expr /* Int */,
      z: Expr /* A */,
      f: Function /* A -> A */
  ): Expr = {
    val s = StmBuild(
      1,
      Tuple(n, z),
      (acc: Expr) =>
        IfThenElse(
          acc.__0 eq 0,
          Tuple(Tuple(acc.__0, acc.__1), acc.__1, True),
          Tuple(Tuple(acc.__0 - 1, FunCall(f, acc.__1)), acc.__1, False)
        )
    )
    StmNext(s).__1
  }
}

// could also make this a primitive in case we want (in the future) to have streams with no length, or if we do not want to use the length information but just the last signal in hardware
object HasNext {
  def apply(stream: Expr): BoolExpr = NotEqual(StmLength(stream), 0)
}

//////////////////////////
// creating streams

object StmCst {
  def apply(n: IntCst, c: IntCst): Expr /* Stm<Int; n> */ =
    StmBuild(n, 0 /* unused */, (_: Expr) => Tuple(0, c, True))
}

object StmCount {
  def apply(n: IntCst): Expr /* Stm<Int; n> */ =
    StmBuild(n, 0, (i: Expr) => Tuple(i + 1, i, True))
}

object StmCst2D {
  def apply(n: Int, m: Int, c: Int): Expr /* Stm<Stm<Int; m>; n> */ = {
    StmBuild(
      n,
      0 /* unused */,
      (_: Expr) =>
        Tuple(
          0,
          StmBuild(m, 0 /* unused */, (_: Expr) => Tuple(0, c, True)),
          True
        )
    )
  }
}

// two solutions: one using multi-dim stream, the other using arithmetic and a 1D stream, the latter can be implemented currently with / and %
object StmCount2D {
  def apply(n: Int, m: Int): Expr /* Stm<Stm<Int; m>; n> */ = {
    StmBuild(
      n,
      0,
      (i: Expr) =>
        Tuple(
          i + 1,
          StmBuild(m, 0, (j: Expr) => Tuple(j + 1, Tuple(i, j), True)),
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
      f: Function /* A -> B */
  ): Expr /* Stm<B; n> */ = {
    val p = Param()
    StmBuild(
      StmLength(input),
      input,
      (acc: Expr) => Let(p, StmNext(acc), Tuple(p.__0, FunCall(f, p.__1), True))
    )
  }
}

//////////////////////////
// reductions
object StmAccess {
  def apply(stm: Expr /* Stm<A; n> */, i: Expr /* Int */ ): Expr /* A */ = {
    val iVal = ExprEvaluator.partialEval(i).asInstanceOf[IntCst].i
    val nVal = ExprEvaluator.partialEval(StmLength(stm)).asInstanceOf[IntCst].i
    require(iVal >= 0)
    require(iVal < nVal)

    val n = StmLength(stm)
    StmNext(StmSuffix(stm, n - i)).__1
  }
}

object StmFold {
  def apply(
      stream: Expr /*Stream<A>*/,
      z: Expr /*B*/,
      f: Function /*A -> B -> B*/
  ): Expr = {
    val next = Param()
    Iterate(
      StmLength(stream),
      Tuple(z, stream),
      (acc: Expr) =>
        Let(
          next,
          StmNext(acc.__1),
          Tuple(
            FunCall(FunCall(f, next.__1), acc.__0),
            next.__0
          )
        )
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
    StmBuild(VecLength(v), 0, (i: Expr) => Tuple(i + 1, VecAccess(v, i), True))
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
      input,
      (seed: Expr) =>
        IfThenElse(
          HasNext(seed),
          Let(next, StmNext(seed), Tuple(next.__0, next.__1, True)),
          Tuple(seed, e, True)
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
      stm,
      (acc: Expr) => Let(next, StmNext(acc), Tuple(next.__0, next.__1, True))
    )
  }
}

object StmSuffix {
  def apply(
      stm: Expr /* Stm<A; n> */,
      k: Expr /* Int */
  ): Expr /* Stm<A; k> */ = {
    val nVal = ExprEvaluator.partialEval(StmLength(stm)).asInstanceOf[IntCst].i
    val kVal = ExprEvaluator.partialEval(k).asInstanceOf[IntCst].i
    require(kVal >= 0)
    require(kVal <= nVal)

    val n = StmLength(stm)
    val next = Param()
    StmBuild(
      k,
      Tuple(n - k, stm),
      (acc: Expr) =>
        Let(
          next,
          StmNext(acc.__1),
          IfThenElse(
            acc.__0 eq 0,
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
      e: Expr /* A */
  ): Expr /* Stm<A; n> */ = {
    StmAppend(StmSuffix(stm, StmLength(stm) - 1), e)
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
      Tuple(in1, in2),
      (seed: Expr) =>
        IfThenElse(
          HasNext(seed.__0),
          Let(p, StmNext(seed.__0), Tuple(Tuple(p.__0, seed.__1), p.__1, True)),
          Let(p, StmNext(seed.__1), Tuple(Tuple(seed.__0, p.__0), p.__1, True))
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
      StmBuild(m, 0 /* unused */, (i: Expr) => Tuple(0, Vec2Stm(v), True))
    )
  }
}

object StmSplit {
  def apply(
      stm: Expr /* Stm<A; n> */,
      m: Int
  ): Expr /* Stm<Stm<A; m>; n/m> */ = {
    val nVal = ExprEvaluator.partialEval(StmLength(stm)).asInstanceOf[IntCst].i
    require(nVal % m == 0)

    val n = StmLength(stm)
    val next = Param()
    val p = Param()
    StmBuild(
      n / m,
      stm,
      (input: Expr) =>
        Let(
          p,
          // Gradually build up the inner stream and return both the inner stream
          // and the new state of the input stream
          // TODO: Rewrite this without using Iterate()?
          Iterate(
            m,
            Tuple(
              input,
              StmBuild(
                0,
                0 /* unused */,
                (_: Expr) => Tuple(0, 0, True)
              )
            ),
            (acc: Expr) =>
              Let(
                next,
                StmNext(acc.__0),
                Tuple(next.__0, StmAppend(acc.__1, next.__1))
              )
          ),
          Tuple(p.__0, p.__1, True)
        )
    )
  }
}

object StmJoin {
  def apply(stm: Expr /* Stm<Stm<A; m>; n> */ ): Expr /* Stm<A; m*n> */ = {
    val n = StmLength(stm)
    val m = StmLength(StmNext(stm).__1)
    val nextInner = Param()
    val nextOuter = Param()
    Let(
      nextOuter,
      StmNext(stm),
      StmBuild(
        n * m,
        nextOuter, /* (Outer stream, inner stream) */
        (acc: Expr) =>
          IfThenElse(
            HasNext(acc.__1),
            // Directly return the next element from the inner stream
            Let(
              nextInner,
              StmNext(acc.__1),
              Tuple(Tuple(acc.__0, nextInner.__0), nextInner.__1, True)
            ),
            // First move to the next element in the outer stream
            // Then return the next element from the inner stream
            Let(
              nextOuter,
              StmNext(acc.__0),
              Let(
                nextInner,
                StmNext(nextOuter.__1),
                Tuple(Tuple(nextOuter.__0, nextInner.__0), nextInner.__1, True)
              )
            )
          )
      )
    )
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
              acc.__0 eq 0,
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
