import scala.language.implicitConversions

sealed abstract class Expr {
  def +(that: Expr): Add = Add(this, that)
  def *(that: Expr): Mul = Mul(this, that)
  def eq(that: Expr): Equal = Equal(this, that)
  def ne(that: Expr): NotEqual = NotEqual(this, that)

  // if we use _0, _1, ... for some reasons the Scala compiler gets confused and produces error messages when matching some of the expressions
  def __0: TupleAccess = TupleAccess(this, 0)
  def __1: TupleAccess = TupleAccess(this, 1)
  def __2: TupleAccess = TupleAccess(this, 2)
}



// Tuples
case class Tuple(elems: Expr*) extends Expr
case class TupleAccess(t: Expr, i: Expr) extends Expr


// Functions
class Param() extends Expr // cannot be a case class as the reference is usd to distinguish between Params
case class Function(param: Param, body: Expr) extends Expr
case class FunCall(f: Expr, arg: Expr) extends Expr
implicit def scalaUnaryLambdaToFunction(sl: Expr => Expr) : Function = {
  val p = Param()
  Function(p, sl(p))
}
implicit def scalaBinaryLambdaToFunction(sl: Expr => Expr => Expr) : Function = {
  val p1 = Param()
  val p2 = Param()
  Function(p1, Function(p2, sl(p1)(p2)))
}
object Let {
  def apply(p: Param, v: Expr, in: Expr) : Expr = {
    FunCall(Function(p, in),v)
  }
}


// Integer expressions
sealed abstract class IntExpr extends Expr
implicit def int2IntCst(i: Int): IntCst = IntCst(i)
implicit def intCst2IntCst(ic: IntCst): IntCst = ic.i
case class IntCst(i: Int) extends IntExpr
case class Add(e1: Expr, e2: Expr) extends IntExpr
case class Mul(e1: Expr, e2: Expr) extends IntExpr

// Boolean expressions
sealed abstract class BoolExpr extends Expr
implicit def boolean2BoolExpr(b: Boolean): BoolExpr = if (b) True else False
implicit def boolExpr2Boolean(b: BoolExpr): Boolean = if (b==True) true else if (b==False) false else throw new RuntimeException("unexpected boolean value")
object True extends BoolExpr
object False extends BoolExpr
case class IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) extends Expr
case class Equal(e1: Expr, e2: Expr) extends BoolExpr
case class NotEqual(e1: Expr, e2: Expr) extends BoolExpr

// High-level function
case class Iterate(n: Expr /* Int */, z: Expr /* A */, f: Function /* A -> A */) extends Expr


// Streams
case class StmBuild(length: Expr /* Int */, seed: Expr /*A*/ , nextF : Function /* A -> (A,B)*/) extends Expr
case class StmLength(stream: Expr) extends IntExpr
case class StmNext(stream: Expr /* Stream<A>*/) /* (A,Steam<A>) */ extends Expr // element only available for one clock cycle
object StmFold {
  def apply(stream: Expr /*Stream<A>*/, z: Expr /*B*/, f: Function /*(A,B) -> B*/) : Expr = {
    Iterate(StmLength(stream), Tuple(z,stream), (acc:Expr) => {
      val next = Param()
      Let(next, StmNext(acc.__1),
        Tuple(
          FunCall(FunCall(f, next.__1), acc.__0),
          next.__0
        )
      )
    }).__0
  }
}


// Vectors
case class VecBuild(len: Expr, f: Function /*Int => Expr*/) extends Expr
case class VecAccess(vec: Expr, i: Expr) extends Expr
case class VecLength(vec: Expr) extends IntExpr
object VecFold {
  def apply(vec: Expr /* Vec<A> */, z: Expr /*B*/ , f: Function/*(A, B) -> B*/) : Expr = {
    Iterate(VecLength(vec), Tuple(z,0), (acc:Expr) => {
      Tuple(FunCall(FunCall(f,VecAccess(vec,acc.__1)),acc.__0),acc.__1+1)
    }).__0
  }
}




object ExprInterpreter {

  private def substitute(e: Expr)(implicit substitutions: Map[Param, Expr]) : Expr = {
    e match {
      case t: Tuple => Tuple(t.elems.toSeq.map(substitute(_)):_*)
      case TupleAccess(t: Expr, i: Expr) => TupleAccess(substitute(t), substitute(i))

      case p: Param => substitutions.get(p) match {
        case Some(v) => v
        case None => p
      }
      case f: Function => {
        val newParam = Param()
        Function(newParam, substitute(f.body)(substitutions + ((f.param, newParam))))
        // when substituting the body, this might be come a new function if anything is susbstituted, therefore, we create a new Param
      }
      case FunCall(f: Expr, arg: Expr) => FunCall(substitute(f), substitute(arg))

      case Add(e1: Expr, e2: Expr) => Add(substitute(e1),substitute(e2))
      case Mul(e1: Expr, e2: Expr) => Mul(substitute(e1),substitute(e2))
      case IntCst(_) => e

      case True  => True
      case False => False
      case IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) => IfThenElse(substitute(cond), substitute(trueE), substitute(falseE))
      case NotEqual(e1: Expr, e2: Expr) => NotEqual(substitute(e1), substitute(e2))
      case Equal(e1: Expr, e2: Expr) => Equal(substitute(e1), substitute(e2))

      case Iterate(n: Expr, z: Expr, f: Function) => Iterate(substitute(n), substitute(z), substitute(f).asInstanceOf[Function])

      case StmBuild(length, seed, f) => StmBuild(substitute(length), substitute(seed), substitute(f).asInstanceOf[Function])
      case StmLength(s) => StmLength (substitute(s))
      case StmNext(e: Expr) => StmNext(substitute(e))

      case VecBuild(len: Expr, f: Function) => VecBuild(substitute(len), substitute(f).asInstanceOf[Function])
      case VecAccess(vec: Expr, i: Expr) => VecAccess(substitute(vec), substitute(i))
      case VecLength(vec: Expr) => VecLength(substitute(vec))
    }
  }


  def interpret(e: Expr)(implicit substitutions: Map[Param, Expr] = Map()): Expr = {
    e match {

      case t: Tuple => Tuple(t.elems.toSeq.map(interpret(_)):_*)
      case TupleAccess(t: Expr, i: Expr) =>
        val tuple = interpret(t).asInstanceOf[Tuple]
        val index = interpret(i).asInstanceOf[IntCst]
        interpret(tuple.elems(index.i))

      case p: Param => substitutions.get(p) match {
        case Some(v) => v
        case None => throw new RuntimeException("Param not found in substititions map")
      }
      case f: Function => substitute(f).asInstanceOf[Function]
      case FunCall(f: Expr, arg: Expr) =>
        val fun = interpret(f).asInstanceOf[Function]
        interpret(fun.body)(substitutions+((fun.param, interpret(arg))))

      case Add(e1: Expr, e2: Expr) => interpret(e1).asInstanceOf[IntCst].i + interpret(e2).asInstanceOf[IntCst].i
      case Mul(e1: Expr, e2: Expr) => interpret(e1).asInstanceOf[IntCst].i * interpret(e2).asInstanceOf[IntCst].i
      case IntCst(_) => e

      case True  => True
      case False => False
      case IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) => if (interpret(cond).asInstanceOf[BoolExpr]) interpret(trueE) else interpret(falseE)
      case NotEqual(e1: Expr, e2: Expr) => interpret(e1).asInstanceOf[IntCst].i != interpret(e2).asInstanceOf[IntCst].i
      case Equal(e1: Expr, e2: Expr) => interpret(e1).asInstanceOf[IntCst].i == interpret(e2).asInstanceOf[IntCst].i

      case Iterate(n: Expr, z: Expr, f: Function) => {
        val n_int : Int = interpret(n).asInstanceOf[IntCst].i
        assert(n_int > -1)

        val z_interpreted = interpret(z)
        if (n_int == 0)
          z_interpreted
        else
          interpret(Iterate(n_int-1, FunCall(f, z_interpreted), f))
      }

      case StmBuild(length, seed, f) =>
        StmBuild(interpret(length), interpret(seed), interpret(f).asInstanceOf[Function] /* ensures any free Param in f gets substituted */)

      case StmLength(s) =>
        val stream = interpret(s).asInstanceOf[StmBuild]
        interpret(stream.length)

      case StmNext(e: Expr) => // need to ensure that from now on, cannot access e: can either make Next primitive take a new body for what to do next a bit like a function
        val stream: StmBuild = interpret(e).asInstanceOf[StmBuild]
        assert(interpret(stream.length).asInstanceOf[IntCst].i > 0)

        val next: Tuple = interpret(FunCall(stream.nextF, stream.seed)).asInstanceOf[Tuple]

        // return the new stream and the next element
        Tuple(
          StmBuild(interpret(stream.length + -1), interpret(next.__0), interpret(stream.nextF).asInstanceOf[Function] /*this function may have free parameters*/),
          interpret(next.__1))

      case VecBuild(len: Expr, f: Function) => VecBuild(interpret(len), interpret(f).asInstanceOf[Function] /* ensures any free Param in f gets substituted */)
      case VecAccess(vec: Expr, i: Expr) => interpret(FunCall(interpret(vec).asInstanceOf[VecBuild].f,interpret(i)))
      case VecLength(vec: Expr) => interpret(interpret(vec).asInstanceOf[VecBuild].len)
    }
  }

  def partialInterpret(e: Expr)(implicit substitutions: Map[Param, Expr] = Map()): Expr = {
    e match {

      case t: Tuple => Tuple(t.elems.toSeq.map(partialInterpret(_)): _*)
      case TupleAccess(t: Expr, i: Expr) =>
        (partialInterpret(t), partialInterpret(i)) match {
          case (tuple: Tuple, index: IntCst) => partialInterpret(tuple.elems(index.i))
          case (tuple@_, index@_) => TupleAccess(tuple, index)
        }

      case p: Param => substitutions.get(p) match {
        case Some(v) => v
        case None => p
      }
      case f: Function =>
        val newF = substitute(f).asInstanceOf[Function]
        Function(newF.param, partialInterpret(newF.body))
      case FunCall(f: Expr, arg: Expr) =>
        partialInterpret(f) match {
          case fun: Function => partialInterpret(fun.body)(substitutions + ((fun.param, partialInterpret(arg))))
          case fun@_ => FunCall(fun, partialInterpret(arg))
        }

      case Add(e1: Expr, e2: Expr) =>
        (partialInterpret(e1),partialInterpret(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i + e2.i
          case (e1@_, e2@_) => Add(e1, e2)
        }
      case Mul(e1: Expr, e2: Expr) =>
        (partialInterpret(e1),partialInterpret(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i * e2.i
          case (e1@_, e2@_) => Mul(e1, e2)
        }
      case IntCst(_) => e

      case True => True
      case False => False
      case IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) =>
        partialInterpret(cond) match {
          case True  => partialInterpret(trueE)
          case False => partialInterpret(falseE)
          case cond@_ =>
            if (trueE == falseE)
              trueE
            else
              IfThenElse(cond, partialInterpret(trueE), partialInterpret(falseE))
        }
      case NotEqual(e1: Expr, e2: Expr) =>
        (partialInterpret(e1),partialInterpret(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i != e2.i
          case (e1@_, e2@_) => NotEqual(e1, e2)
        }
      case Equal(e1: Expr, e2: Expr) =>
        (partialInterpret(e1),partialInterpret(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i == e2.i
          case (e1@_, e2@_) => Equal(e1, e2)
        }

      case Iterate(n: Expr, z: Expr, f: Function) => {
        partialInterpret(n) match {
          case n: IntCst => {
            val n_int: Int = n.i
            assert(n_int > -1)

            val z_interpreted = partialInterpret(z)
            if (n_int == 0)
              z_interpreted
            else
              partialInterpret(Iterate(n_int - 1, FunCall(f, z_interpreted), f))
          }
          case n@_ =>  Iterate(n, partialInterpret(z), f)
        }
      }

      case StmBuild(length, seed, f) =>
        StmBuild(partialInterpret(length), partialInterpret(seed), partialInterpret(f).asInstanceOf[Function] /* ensures any free Param in f gets substituted */)

      case StmLength(s) =>
        partialInterpret(s) match {
          case s: StmBuild => partialInterpret(s.length)
          case s@_ => StmLength(s)
        }

      case StmNext(s: Expr) =>
        partialInterpret(s) match {
          case s: StmBuild =>
            partialInterpret(s.length) match {
              case len: IntCst => {
                assert(len.i > 0)
                partialInterpret(FunCall(s.nextF, s.seed)) match {
                  case next: Tuple => {
                    // return the new stream and the next element
                    Tuple(
                      StmBuild(partialInterpret(s.length + -1), partialInterpret(next.__0), partialInterpret(s.nextF).asInstanceOf[Function] /*this function may have free parameters*/),
                      partialInterpret(next.__1))
                  }
                  case next@_ => StmNext(s)
                }
              }
              case len@_ => StmNext(s)
            }
          case s@_ => StmNext(s)
        }


      case VecBuild(len: Expr, f: Function) => VecBuild(partialInterpret(len), partialInterpret(f).asInstanceOf[Function] /* ensures any free Param in f gets substituted */)
      case VecAccess(vec: Expr, i: Expr) =>
        partialInterpret(vec) match {
          case vec: VecBuild => partialInterpret(FunCall(vec.f, partialInterpret(i)))
          case vec@_ => VecAccess(vec, partialInterpret(i))
        }
      case VecLength(vec: Expr) =>
        partialInterpret(vec) match {
          case vec: VecBuild => partialInterpret(vec.len)
          case vec@_ => VecLength(vec)
        }
    }
  }

}
