// could also make this a primitive in case we want (in the future) to have streams with no length, or if we do not want to use the length information but just the last signal in hardware
object HasNext {
  def apply(stream: Expr): BoolExpr = NotEqual(StmLength(stream), 0)
}

//////////////////////////
// creating streams

object CstStream {
  def apply(n: IntCst, c: IntCst): StmBuild =
    StmBuild(n, c, (seed: Expr) => Tuple(seed, c))
}

object CounterStream {
  def apply(n: IntCst): StmBuild = StmBuild(n, 0, (i: Expr) => Tuple(i + 1, i))
}

// two solutions: one using multi-dim stream, the other using arithmetic and a 1D stream, the latter can be implemented currently with / and %
object Counter2DStream {
  def apply(n: Int, m: Int): StmBuild = {
    StmBuild(
      n,
      0,
      (i: Expr) =>
        Tuple(i + 1, StmBuild(m, 0, (j: Expr) => Tuple(j + 1, Tuple(i, j))))
    )
  }
}

//////////////////////////
// manipulating streams

object StmMap {
  def apply(input: Expr, f: Expr => Expr): StmBuild = {
    StmBuild(
      StmLength(input),
      input,
      (acc: Expr) => {
        val p = Param()
        Let(p, StmNext(acc), Tuple(p.__0, f(p.__1)))
      }
    )
  }
}

//////////////////////////
// reductions
object StmFold {
  def apply(
      stream: Expr /*Stream<A>*/,
      z: Expr /*B*/,
      f: Function /*A -> B -> B*/
  ): Expr = {
    Iterate(
      StmLength(stream),
      Tuple(z, stream),
      (acc: Expr) => {
        val next = Param()
        Let(
          next,
          StmNext(acc.__1),
          Tuple(
            FunCall(FunCall(f, next.__1), acc.__0),
            next.__0
          )
        )
      }
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
            Tuple(Tuple(next.__0, y), if inclusive then y else acc.__1)
          )
        )
    )
  }
}

object Vec2Stm {
  def apply(v: Expr): StmBuild =
    StmBuild(VecLength(v), 0, (i: Expr) => Tuple(i + 1, VecAccess(v, i)))
}

/////////////////////////
// dropping/adding elements
object StmPrepend {
  def apply(input: StmBuild, e: Expr): StmBuild = {
    StmBuild(
      input.length + 1,
      Tuple(True, input),
      (seed: Expr) => {
        IfThenElse(
          seed.__0,
          Tuple(Tuple(False, seed.__1), e), {
            val p = Param()
            Let(p, StmNext(seed.__1), Tuple(Tuple(False, p.__0), p.__1))
          }
        )
      }
    )
  }
}
object StmAppend {
  def apply(input: StmBuild, e: Expr): StmBuild = {
    StmBuild(
      input.length + 1,
      input,
      (seed: Expr) =>
        IfThenElse(
          HasNext(seed), {
            val p = Param()
            Let(p, StmNext(seed), Tuple(p.__0, p.__1))
          },
          Tuple(seed, e)
        )
    )
  }
}

/////////////////////////
// concat
object StmConcat {
  def apply(in1: StmBuild, in2: StmBuild): StmBuild = StmBuild(
    in1.length + in2.length,
    Tuple(in1, in2),
    (seed: Expr) =>
      IfThenElse(
        HasNext(seed.__0), {
          val p = Param()
          Let(p, StmNext(seed.__0), Tuple(Tuple(p.__0, seed.__1), p.__1))
        }, {
          val p = Param()
          Let(p, StmNext(seed.__1), Tuple(Tuple(seed.__0, p.__0), p.__1))
        }
      )
  )
}

////////////////////////
// zip
object StmZip {
  def apply(a: Expr, b: Expr): StmBuild = {
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
            Tuple(Tuple(nextA.__0, nextB.__0), Tuple(nextA.__1, nextB.__1))
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
        (i: Expr) =>
          Tuple(
            0,
            StmBuild(
              VecLength(v),
              0,
              (j: Expr) => Tuple(j + 1, VecAccess(v, j))
            )
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
    val nVal = ExprEvaluator.partialEval(StmLength(stm)).asInstanceOf[IntCst].i
    require(nVal % m == 0)

    val n = StmLength(stm)
    // StmBuild(
    //   n / m,
    //   stm,
    //   (acc: Expr) =>
    //     Tuple(
    //       // How do I write back the stream once the inner stream is done?
    //       ???,
    //       StmBuild(m, acc, (a: Expr) => StmNext(a))
    //     )
    // )
    // TODO: This implementation isn't good
    val v = Param()
    Let(
      v,
      VecSplit(Stm2Vec(stm), m),
      StmBuild(
        n / m,
        0,
        (i: Expr) =>
          Tuple(
            i + 1,
            StmBuild(
              m,
              0,
              (j: Expr) => Tuple(j + 1, VecAccess(VecAccess(v, i), j))
            )
          )
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
              Tuple(Tuple(acc.__0, nextInner.__0), nextInner.__1)
            ),
            // First move to the next element in the outer stream
            // Then return the next element from the inner stream
            Let(
              nextOuter,
              StmNext(acc.__0),
              Let(
                nextInner,
                StmNext(nextOuter.__1),
                Tuple(Tuple(nextOuter.__0, nextInner.__0), nextInner.__1)
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
    val v0 = Param()
    Let(
      v0,
      // Read the first `m - 1` elements into a vector
      // Don't read all `m` because the `StmBuild` will call `StmNext()` once
      // before producing its first element
      Iterate(
        m - 1,
        Tuple(stm, VecBuild(m, (i: Expr) => IntCst(0))),
        (acc: Expr) =>
          Let(
            next,
            StmNext(acc.__0),
            Tuple(
              next.__0,
              // Like a shift register
              VecBuild(
                m,
                (i: Expr) =>
                  IfThenElse(i eq (m - 1), next.__1, VecAccess(acc.__1, i + 1))
              )
            )
          )
      ),
      StmBuild(
        n + -m + 1,
        v0, // (stream, vector)
        (acc: Expr) =>
          Let(
            next,
            StmNext(acc.__0),
            Tuple(
              Tuple(
                next.__0,
                // Like a shift register
                VecBuild(
                  m,
                  (i: Expr) =>
                    IfThenElse(
                      i eq (m - 1),
                      next.__1,
                      VecAccess(acc.__1, i + 1)
                    )
                )
              ),
              // Same as above
              VecBuild(
                m,
                (i: Expr) =>
                  IfThenElse(i eq (m - 1), next.__1, VecAccess(acc.__1, i + 1))
              )
            )
          )
      )
    )
  }
}
