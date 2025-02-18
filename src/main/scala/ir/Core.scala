package ir

import java.util.concurrent.atomic.AtomicLong

/** A node in the core IR.
  */
sealed trait Expr {
  def +(that: Expr): Expr = Sum(this, that)
  def -(that: Expr): Expr = Sum(this, Prod(-1, that))
  def *(that: Expr): Expr = Prod(this, that)
  def /(that: Expr): Div = Div(this, that)
  def %(that: Expr): Mod = Mod(this, that)
  def ===(that: Expr): Equal = Equal(this, that)
  def !==(that: Expr): Expr = Not(Equal(this, that))
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

// Define a consistent order for expressions to make the tests less brittle.
// Where appropriate, the interpreter should keep things in this order so that a test doesn't break only due to an
// insignificant change in the order of sub-expressions (e.g., for the terms within a sum).
case object ExprOrdering extends Ordering[Expr] {
  override def compare(x: Expr, y: Expr): Int = {
    val c = classScore(x).compareTo(classScore(y))
    if (c != 0) {
      c
    } else {
      // The two objects must be from the same class since their class score
      // is the same
      (x, y) match {
        case (p1: Param, p2: Param) => p1.name.compareTo(p2.name)
        case (IntCst(a), IntCst(b)) => a.compareTo(b)
        case (False, True)          => false.compareTo(true)
        case (True, False)          => true.compareTo(false)
        case _ =>
          val comp = x.children
            .zip(y.children)
            .map({ case (xc, yc) => ExprOrdering.compare(xc, yc) })
            .foldLeft(0)((acc, x) => if (acc != 0) acc else x)
          if (comp != 0) {
            comp
          } else {
            x.children.length.compareTo(y.children.length)
          }
      }
    }
  }

  private def classScore(e: Expr): Int = {
    e match {
      case DontCare          => 0
      case _: IntCst         => 1
      case False             => 2
      case True              => 3
      case _: Param          => 4
      case _: Sum            => 5
      case _: Prod           => 6
      case _: Div            => 7
      case _: Mod            => 8
      case _: And            => 9
      case _: Or             => 10
      case _: Not            => 11
      case _: Equal          => 12
      case _: LessThan       => 13
      case _: TupleAccess    => 14
      case _: FunCall        => 15
      case _: IfThenElse     => 16
      case _: Tuple          => 17
      case _: Function       => 18
      case _: StmBuild       => 19
      case _: StmNext        => 20
      case _: StmLength      => 21
      case _: VecBuild       => 22
      case _: VecAccess      => 23
      case _: VecLength      => 24
      case _: ExtensibleExpr => 25
    }
  }
}

// Tuples
case class Tuple(elems: Expr*) extends Expr {
  override def children: Seq[Expr] = elems
}
case class TupleAccess(t: Expr, i: Expr) extends Expr {
  override def children: Seq[Expr] = Seq(t, i)
}

// Functions
case class Param(prefix: String, id: Long) extends Expr {
  val name: String = s"${prefix}_$id"

  override def children: Seq[Expr] = Seq()

  override def toString: String = name
}
case object Param {
  private val idCtr = new AtomicLong()

  def apply(prefix: String = "p"): Param = {
    new Param(prefix, idCtr.incrementAndGet())
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

final class Sum(unsortedTerms: Seq[Expr]) extends IntExpr {
  val terms: Seq[Expr] =
    unsortedTerms
      // Flatten nested sums to represent associativity
      .flatMap({
        case s: Sum => s.terms
        case e      => Seq(e)
      })
      // Sort terms to represent commutativity
      .sorted(ExprOrdering)

  override def children: Seq[Expr] = terms

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: Sum => this.terms == that.terms
      case _         => false
    }
  }
  override def hashCode(): Int = {
    this.terms.hashCode()
  }

  override def toString: String = {
    s"Sum(${this.terms})"
  }
}
object Sum {
  def apply(terms: Expr*): Sum = {
    new Sum(terms)
  }

  def unapply(s: Sum): Option[Seq[Expr]] = {
    Some(s.terms)
  }
}

final class Prod(unsortedFactors: Seq[Expr]) extends IntExpr {
  val factors: Seq[Expr] =
    unsortedFactors
      // Flatten nested sums to represent associativity
      .flatMap({
        case p: Prod => p.factors
        case e       => Seq(e)
      })
      // Sort terms to represent commutativity
      .sorted(ExprOrdering)

  override def children: Seq[Expr] = factors

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: Prod => this.factors == that.factors
      case _          => false
    }
  }
  override def hashCode(): Int = {
    this.factors.hashCode()
  }

  override def toString: String = {
    s"Prod(${this.factors})"
  }
}
object Prod {
  def apply(factors: Expr*): Prod = {
    new Prod(factors)
  }

  def unapply(p: Prod): Option[Seq[Expr]] = {
    Some(p.factors)
  }
}

case class Div(e1: Expr, e2: Expr) extends IntExpr with BinOp
case class Mod(e1: Expr, e2: Expr) extends IntExpr with BinOp

// Boolean expressions
sealed trait BoolExpr extends Expr
case object True extends BoolExpr {
  override def children: Seq[Expr] = Seq()
}
case object False extends BoolExpr {
  override def children: Seq[Expr] = Seq()
}

// This is similar to TupleAccess(Tuple(falseE, trueE), cond), as long as
// False is interpreted as 0 and True as 1.
// However, IfThenElse does *not* evaluate the branch that's not taken, which
// is important in cases like calling StmNext() or memory accesses.
final class IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) extends Expr {
  val (c, t, f) = cond match {
    case Not(c) => (c, falseE, trueE)
    case c      => (c, trueE, falseE)
  }

  override def children: Seq[Expr] = Seq(c, t, f)

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: IfThenElse =>
        this.c == that.c && this.t == that.t && this.f == that.f
      case _ => false
    }
  }
  override def hashCode(): Int = {
    (this.c, this.t, this.f).hashCode()
  }

  override def toString: String = {
    s"IfThenElse($c, $t, $f)"
  }
}
object IfThenElse {
  def apply(c: Expr, t: Expr, f: Expr): Expr = {
    new IfThenElse(c, t, f)
  }

  def unapply(ite: IfThenElse): Option[(Expr, Expr, Expr)] = {
    Some((ite.c, ite.t, ite.f))
  }
}

// Comparison operators
case class Equal(e1: Expr, e2: Expr) extends BoolExpr with BinOp
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
    nextF: Function /* A -> (A, Option<B>)*/
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

// You can add new nodes to the IR by extending this
trait ExtensibleExpr extends Expr
