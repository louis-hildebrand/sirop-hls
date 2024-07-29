// could also make this a primitive in case we want (in the future) to have streams with no length, or if we do not want to use the length information but just the last signal in hardware
object HasNext {
  def apply(stream: Expr) : BoolExpr = NotEqual(StmLength(stream), 0)
}



//////////////////////////
// creating streams

object CstStream {
  def apply(n: IntCst, c: IntCst) : StmBuild = StmBuild(n, c, (seed:Expr) => Tuple(seed, c))
}
object CounterStream {
  def apply(n: IntCst) : StmBuild  = StmBuild(n, 0, (i:Expr) => Tuple(i+1, i))
}

// two solutions: one using multi-dim stream, the other using arithmetic and a 1D stream, the latter can be implemented currently with / and %
object Counter2DStream {
  def apply(n: Int, m: Int): StmBuild = {
    StmBuild(n, 0, (i: Expr) => Tuple(i+1,
      StmBuild(m, 0, (j: Expr) => Tuple(j+1,
        Tuple(i,j)
      ))
    ))
  }
}


//////////////////////////
// manipulating streams

object MapS {
  def apply(input: Expr, f: Expr => Expr) : StmBuild = {
    StmBuild(StmLength(input), input, (acc: Expr) => {
      val p = Param()
      Let(p, StmNext(acc), Tuple(p.__0, f(p.__1))
      )
    })
  }
}


/////////////////////////
// dropping/adding elements
object PadFirst {
  def apply(input: StmBuild, e: Expr) : StmBuild = { 
    StmBuild(input.length+1, Tuple(True,input),
      (seed: Expr) => {
        IfThenElse(
          seed.__0,
          Tuple(Tuple(False, seed.__1),e),
          {
            val p = Param()
            Let(p, StmNext(seed.__1),
              Tuple(Tuple(False,p.__0),p.__1))
          }
        )
      })
  }
}
object PadLast {
  def apply(input: StmBuild, e: Expr) : StmBuild = {
    StmBuild(input.length+1, input,
      (seed: Expr) =>
        IfThenElse(HasNext(seed), {
          val p = Param()
          Let(p, StmNext(seed), Tuple(p.__0, p.__1))
          },
          Tuple(seed,e)
        )
    )
  }
}


/////////////////////////
// concat
object Concat {
  def apply(in1: StmBuild, in2: StmBuild) : StmBuild = StmBuild(in1.length+in2.length, Tuple(in1, in2), (seed: Expr) => 
    IfThenElse(HasNext(seed.__0),
      {
        val p = Param()
        Let(p, StmNext(seed.__0), Tuple(
          Tuple(p.__0, seed.__1),
          p.__1))
      },
      {
        val p = Param()
        Let(p, StmNext(seed.__1), Tuple(
          Tuple(seed.__0, p.__0),
          p.__1))
      }
    ))
}




////////////////////////
// repeat
// need a buffer and 2D stream!
//object Repeat {
//  def apply(times: IntCst, input: BuildS) : BuildS = BuildS(
//    input.initLen + times /* change + to *!!*/,
//    True,
//    s => s,
//    s => Next(input))
//}

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

