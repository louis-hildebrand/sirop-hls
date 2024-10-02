package ir

import opt.PartialEvalPass

import scala.language.implicitConversions

sealed abstract class Expr {
  def +(that: Expr): Add = Add(this, that)
  def -(that: Expr): Sub = Sub(this, that)
  def *(that: Expr): Mul = Mul(this, that)
  def /(that: Expr): Div = Div(this, that)
  def %(that: Expr): Mod = Mod(this, that)
  def ===(that: Expr): Equal = Equal(this, that)
  def !==(that: Expr): NotEqual = NotEqual(this, that)
  def <(that: Expr): LessThan = LessThan(this, that)
  def <=(that: Expr): Not = Not(this > that)
  def >(that: Expr): LessThan = that < this
  def >=(that: Expr): Not = that <= this
  def &&(that: Expr): And = And(this, that)
  def ||(that: Expr): Or = Or(this, that)

  // if we use _0, _1, ... for some reasons the Scala compiler gets confused and produces error messages when matching some of the expressions
  def __0: TupleAccess = TupleAccess(this, 0)
  def __1: TupleAccess = TupleAccess(this, 1)
  def __2: TupleAccess = TupleAccess(this, 2)
  def __3: TupleAccess = TupleAccess(this, 3)
  def __4: TupleAccess = TupleAccess(this, 4)
  def __5: TupleAccess = TupleAccess(this, 5)
}

// Tuples
case class Tuple(elems: Expr*) extends Expr
case class TupleAccess(t: Expr, i: Expr) extends Expr

// Functions
// cannot be a case class as the reference is used to distinguish between Params
class Param() extends Expr
case class Function(param: Param, body: Expr) extends Expr {
  override def equals(x: Any): Boolean = {
    if !x.isInstanceOf[Function] then false
    else {
      val that = x.asInstanceOf[Function]
      val sub = Map[Expr, Expr](this.param -> that.param)
      PartialEvalPass.substitute(this.body)(sub) == that.body
    }
  }
}
case class FunCall(f: Expr, arg: Expr) extends Expr
implicit def scalaUnaryLambdaToFunction(sl: Expr => Expr): Function = {
  val p = Param()
  Function(p, sl(p))
}
implicit def scalaBinaryLambdaToFunction(sl: Expr => Expr => Expr): Function = {
  val p1 = Param()
  val p2 = Param()
  Function(p1, Function(p2, sl(p1)(p2)))
}
object Let {
  def apply(p: Param, v: Expr, in: Expr): Expr = {
    FunCall(Function(p, in), v)
  }
}

// Integer expressions
sealed abstract class IntExpr extends Expr
implicit def int2IntCst(i: Int): IntCst = IntCst(i)
implicit def intCst2Int(ic: IntCst): Int = ic.i
case class IntCst(i: Int) extends IntExpr
case class Add(e1: Expr, e2: Expr) extends IntExpr
case class Sub(e1: Expr, e2: Expr) extends IntExpr
case class Mul(e1: Expr, e2: Expr) extends IntExpr
case class Div(e1: Expr, e2: Expr) extends IntExpr
case class Mod(e1: Expr, e2: Expr) extends IntExpr

// Boolean expressions
sealed abstract class BoolExpr extends Expr
implicit def boolean2BoolExpr(b: Boolean): BoolExpr = if (b) True else False
implicit def boolExpr2Boolean(b: BoolExpr): Boolean = if (b == True) true
else if (b == False) false
else throw new RuntimeException("unexpected boolean value")
object True extends BoolExpr
object False extends BoolExpr
// This is similar to TupleAccess(Tuple(falseE, trueE), cond), as long as
// False is interpreted as 0 and True as 1.
// However, IfThenElse does *not* evaluate the branch that's not taken, which
// is important in cases like calling StmNext() or memory accesses.
case class IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) extends Expr
// Comparison operators
case class Equal(e1: Expr, e2: Expr) extends BoolExpr
case class NotEqual(e1: Expr, e2: Expr) extends BoolExpr
case class LessThan(e1: Expr, e2: Expr) extends BoolExpr
object GreaterThan {
  def apply(e1: Expr, e2: Expr): Expr = LessThan(e2, e1)
}
// Logical operators
case class Not(e: Expr) extends BoolExpr
case class And(e1: Expr, e2: Expr) extends BoolExpr
case class Or(e1: Expr, e2: Expr) extends BoolExpr

// Useful for readability and possibly for optimization
object DontCare extends Expr

// Streams
case class StmBuild(
    length: Expr,
    seed: Expr /*A*/,
    nextF: Function /* A -> (A, B, Bool)*/
) extends Expr
case class StmLength(stream: Expr) extends IntExpr
case class StmNext(stream: Expr /* Stream<A>*/ ) /* (Stream<A>, A) */
    extends Expr // element only available for one clock cycle

// Vectors
case class VecBuild(len: Expr, f: Expr /*Int => Expr*/ ) extends Expr
case class VecAccess(vec: Expr, i: Expr) extends Expr
case class VecLength(vec: Expr) extends IntExpr
