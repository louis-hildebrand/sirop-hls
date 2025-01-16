package ir

sealed trait Expr {
  def +(that: Expr): Add = Add(this, that)
  def -(that: Expr): Add = Add(this, Neg(that))
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

  def children: Seq[Expr]
}

// Tuples
case class Tuple(elems: Expr*) extends Expr {
  override def children: Seq[Expr] = elems
}
case class TupleAccess(t: Expr, i: Expr) extends Expr {
  override def children: Seq[Expr] = Seq(t, i)
}

// Functions
case class Param(prefix: String, id: Int) extends Expr {
  val name: String = s"${prefix}_$id"

  override def children: Seq[Expr] = Seq()
}
case object Param {
  private var nextId = 0

  def apply(prefix: String = "p"): Param = {
    val id = nextId
    nextId += 1
    new Param(prefix, id)
  }
}
case class Function(param: Param, body: Expr) extends Expr {
  override def children: Seq[Expr] = Seq(param, body)

  override def equals(x: Any): Boolean = {
    if (!x.isInstanceOf[Function]) false
    else {
      val that = x.asInstanceOf[Function]
      val sub = Map[Expr, Expr](this.param -> that.param)
      substitute(this.body)(sub) == that.body
    }
  }
}
case class FunCall(f: Expr, arg: Expr) extends Expr {
  override def children: Seq[Expr] = Seq(f, arg)
}
case object Let {
  def apply(p: Param, v: Expr, in: Expr): Expr = {
    FunCall(Function(p, in), v)
  }
}

sealed trait BinOp extends Expr {
  val e1: Expr
  val e2: Expr

  override def children: Seq[Expr] = Seq(e1, e2)
}

// Integer expressions
sealed trait IntExpr extends Expr
case class IntCst(i: Int) extends IntExpr {
  override def children: Seq[Expr] = Seq()
}
case class Add(e1: Expr, e2: Expr) extends IntExpr with BinOp
case class Mul(e1: Expr, e2: Expr) extends IntExpr with BinOp
case class Div(e1: Expr, e2: Expr) extends IntExpr with BinOp
case class Mod(e1: Expr, e2: Expr) extends IntExpr with BinOp
case class Neg(e: Expr) extends IntExpr {
  override def children: Seq[Expr] = Seq(e)
}

// Boolean expressions
sealed trait BoolExpr extends Expr
object True extends BoolExpr {
  override def children: Seq[Expr] = Seq()
}
object False extends BoolExpr {
  override def children: Seq[Expr] = Seq()
}
// This is similar to TupleAccess(Tuple(falseE, trueE), cond), as long as
// False is interpreted as 0 and True as 1.
// However, IfThenElse does *not* evaluate the branch that's not taken, which
// is important in cases like calling StmNext() or memory accesses.
case class IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) extends Expr {
  override def children: Seq[Expr] = Seq(cond, trueE, falseE)
}
// Comparison operators
case class Equal(e1: Expr, e2: Expr) extends BoolExpr with BinOp
case class NotEqual(e1: Expr, e2: Expr) extends BoolExpr with BinOp
case class LessThan(e1: Expr, e2: Expr) extends BoolExpr with BinOp
// Logical operators
case class Not(e: Expr) extends BoolExpr {
  override def children: Seq[Expr] = Seq(e)
}
case class And(e1: Expr, e2: Expr) extends BoolExpr with BinOp
case class Or(e1: Expr, e2: Expr) extends BoolExpr with BinOp

// Useful for readability and possibly for optimization
case object DontCare extends Expr {
  override def children: Seq[Expr] = Seq()
}

// Streams
case class StmBuild(
    length: Expr,
    seed: Expr /*A*/,
    // TODO: Use Option<B> instead of (B, Bool) in the final IR
    nextF: Function /* A -> (A, B, Bool)*/
) extends Expr {
  override def children: Seq[Expr] = Seq(length, seed, nextF)
}
case class StmLength(stream: Expr) extends IntExpr {
  override def children: Seq[Expr] = Seq(stream)
}
// element only available for one clock cycle
case class StmNext(stream: Expr /* Stream<A>*/ ) /* (Stream<A>, A) */
    extends Expr {
  override def children: Seq[Expr] = Seq(stream)
}

// Vectors
case class VecBuild(len: Expr, f: Expr /*Int => Expr*/ ) extends Expr {
  override def children: Seq[Expr] = Seq(len, f)
}
case class VecAccess(vec: Expr, i: Expr) extends Expr {
  override def children: Seq[Expr] = Seq(vec, i)
}
case class VecLength(vec: Expr) extends IntExpr {
  override def children: Seq[Expr] = Seq(vec)
}
