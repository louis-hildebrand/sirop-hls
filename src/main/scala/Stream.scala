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

object MapS {
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

/////////////////////////
// dropping/adding elements
object PadFirst {
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
object PadLast {
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
object Concat {
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

// Join/Split : need to understand how to resprent multi-dim streams
// Stm2Vec, Vec2Stm: need to introduce buildVec, buildArray
// buildArray is given a length, and a list of indices to use to build the array
// buildVec is given a length and the function
// when converting a stream of a vector, we need, sequentially, to write into a an array (or memory) and then once it is written, build the vector
// the memory needs to be readable in one go, which is tricky. Implementation is based on shift register, need perhaps to have register represented as a primitive?

//object Join {
//  def apply(stream: BuildS) : BuildS = BuildS(
//    stream.initLen* Inner(stream).initLen,
//    False, // not used
//    s => s, // not used
//    s => Next(Inner(stream))
//  )
//}

object StmSlide {
  def apply(
      stm: Expr /* Stm<A; n> */,
      m: Int
  ): Expr /* Stm<Vec<A; m>, n-m+1> */ = {
    require(m <= ExprEvaluator.eval(StmLength(stm)).asInstanceOf[IntCst].i)
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
