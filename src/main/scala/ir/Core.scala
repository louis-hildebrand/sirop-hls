package ir

import java.util.Objects
import java.util.concurrent.atomic.AtomicLong
import scala.annotation.tailrec

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
  def rebuild(newChildren: Seq[Expr]): Expr

  def contains(p: Expr => Boolean): Boolean = {
    p(this) || this.children.exists(e => e.contains(p))
  }
  def contains(e2: Expr): Boolean = this.contains(e => e == e2)
  def contains[T <: Expr](cls: Class[T]): Boolean =
    this.contains(e => cls.isInstance(e))

  def substitute(subs: Map[Expr, Expr]): Expr = {
    subs.get(this) match {
      case Some(v) => v
      case None =>
        this match {
          case f: Function =>
            // "Rename" to avoid variable capture
            val newParam = Param(f.param.prefix)
            Function(
              newParam,
              f.body.substitute(subs + ((f.param, newParam)))
            )
          case e => e.rebuild(e.children.map(e => e.substitute(subs)))
        }
    }
  }

  def substitute(sub: (Expr, Expr)): Expr = substitute(Map(sub))

  def freeVars(): Set[Param] = {
    this match {
      case x: Param       => Set(x)
      case Function(x, e) => e.freeVars() - x
      case stm @ StmBuild(n, out, eqns) =>
        (
          // Free variables in the stream length and seeds are definitely free,
          // even if they are bound by the stream
          n.freeVars()
            ++ eqns.foldLeft(Set[Param]())({ case (fvs, (_, (z, _))) =>
              fvs ++ z.freeVars()
            })
            // There may be bound variables in the output and "next" functions
            ++ (
              (out.freeVars()
                ++ eqns.foldLeft(Set[Param]())({ case (fvs, (_, (_, next))) =>
                  fvs ++ next.freeVars()
                }))
                .diff(stm.accVars)
            )
        )
      case e =>
        e.children.foldLeft(Set[Param]())((fvs, e) => fvs ++ e.freeVars())
    }
  }
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
      case DontCare       => 0
      case False          => 1
      case True           => 2
      case _: And         => 3
      case _: Or          => 4
      case _: Not         => 5
      case _: Equal       => 6
      case _: LessThan    => 7
      case _: IntCst      => 8
      case _: Sum         => 9
      case _: Prod        => 10
      case _: Div         => 11
      case _: Mod         => 12
      case _: Param       => 13
      case _: TupleAccess => 14
      case _: FunCall     => 15
      case _: IfThenElse  => 16
      case _: Tuple       => 17
      case _: Function    => 18
      case _: StmBuild    => 19
      case _: StmNext     => 20
      case _: StmLength   => 21
      case _: VecBuild    => 22
      case _: VecAccess   => 23
      case _: VecLength   => 24
      case _: VecLiteral  => 25
      case _: StmLiteral  => 26
      case _: StmNextK    => 27
    }
  }
}

// Tuples
case class Tuple(elems: Expr*) extends Expr {
  override def children: Seq[Expr] = elems
  override def rebuild(newChildren: Seq[Expr]): Expr = Tuple(newChildren: _*)
}
case class TupleAccess(t: Expr, i: IntCst) extends Expr {
  override def children: Seq[Expr] = Seq(t, i)
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(t, i: IntCst) => TupleAccess(t, i)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }
}

// Functions
case class Param(prefix: String, id: Long) extends Expr {
  val name: String = s"${prefix}_$id"

  override def children: Seq[Expr] = Seq()
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    require(newChildren.isEmpty)
    this
  }

  def freshCopy: Param = Param(this.prefix)

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
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(x: Param, body: Expr) => Function(x, body)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }

  override def equals(x: Any): Boolean = {
    x match {
      case that: Function =>
        val fresh = Param()
        (this.body.substitute(this.param -> fresh)
          == that.body.substitute(that.param -> fresh))
      case _ => false
    }
  }

  override def hashCode(): Int = {
    // This implementation should be correct, but it may cause excessive
    // collisions when dealing with nested functions. For example,
    // x => y => x - y and x => y => y - x will be assigned the same hash code.
    this.body.substitute(this.param -> Function.HashCodeParam).hashCode
  }
}
object Function {

  /** Parameter to be used in the definition of <code>hashCode</code> to ensure
    * that the bound variable name doesn't affect the hash code. <i>It MUST NOT
    * be used for anything else</i>.
    */
  private val HashCodeParam = Param("hashCode")
}

case class FunCall(f: Expr, arg: Expr) extends Expr {
  override def children: Seq[Expr] = Seq(f, arg)
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    require(newChildren.length == 2)
    FunCall(newChildren.head, newChildren(1))
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
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    require(newChildren.isEmpty)
    this
  }
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
  override def rebuild(newChildren: Seq[Expr]): Expr = Sum(newChildren: _*)

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
  override def rebuild(newChildren: Seq[Expr]): Expr = Prod(newChildren: _*)

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

case class Div(e1: Expr, e2: Expr) extends IntExpr with BinOp {
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    require(newChildren.length == 2)
    Div(newChildren.head, newChildren(1))
  }
}
case class Mod(e1: Expr, e2: Expr) extends IntExpr with BinOp {
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    require(newChildren.length == 2)
    Mod(newChildren.head, newChildren(1))
  }
}

// Boolean expressions
sealed trait BoolExpr extends Expr
sealed trait BoolCst extends BoolExpr {
  override def children: Seq[Expr] = Seq()
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    require(newChildren.isEmpty)
    this
  }
}
case object True extends BoolCst
case object False extends BoolCst

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
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(c, t, f) => IfThenElse(c, t, f)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }

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
case class Equal(e1: Expr, e2: Expr) extends BoolExpr with BinOp {
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e1, e2) => Equal(e1, e2)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }
}
case class LessThan(e1: Expr, e2: Expr) extends BoolExpr with BinOp {
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e1, e2) => LessThan(e1, e2)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }
}
// Logical operators
case class Not(e: Expr) extends BoolExpr {
  override def children: Seq[Expr] = Seq(e)
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => Not(e)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }
}
final class And(unsortedTerms: Expr*) extends BoolExpr {
  val terms: Seq[Expr] =
    unsortedTerms
      // Flatten nested ANDs to represent associativity
      .flatMap({
        case a: And => a.terms
        case e      => Seq(e)
      })
      // Deduplicate to represent the fact that x && x == x
      .distinct
      // Sort terms to represent commutativity
      .sorted(ExprOrdering)

  def remove(c: Expr): Expr = {
    terms.filter(e => e != c) match {
      case Seq()  => True
      case Seq(e) => e
      case terms  => And(terms: _*)
    }
  }

  override def children: Seq[Expr] = terms
  override def rebuild(newChildren: Seq[Expr]): Expr = And(newChildren: _*)

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: And => this.terms == that.terms
      case _         => false
    }
  }
  override def hashCode(): Int = {
    this.terms.hashCode()
  }

  override def toString: String = {
    s"And(${this.terms.mkString(",")})"
  }
}
object And {
  def apply(terms: Expr*): And = {
    new And(terms: _*)
  }

  def unapplySeq(a: And): Option[Seq[Expr]] = {
    Some(a.terms)
  }
}

final class Or(unsortedTerms: Expr*) extends BoolExpr {
  val terms: Seq[Expr] =
    unsortedTerms
      // Flatten nested ORs to represent associativity
      .flatMap({
        case a: Or => a.terms
        case e     => Seq(e)
      })
      // Deduplicate to represent the fact that x || x == x
      .distinct
      // Sort terms to represent commutativity
      .sorted(ExprOrdering)

  def remove(c: Expr): Expr = {
    terms.filter(e => e != c) match {
      case Seq()  => False
      case Seq(e) => e
      case terms  => Or(terms: _*)
    }
  }

  override def children: Seq[Expr] = terms
  override def rebuild(newChildren: Seq[Expr]): Expr = Or(newChildren: _*)

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: Or => this.terms == that.terms
      case _        => false
    }
  }
  override def hashCode(): Int = {
    this.terms.hashCode()
  }

  override def toString: String = {
    s"Or(${this.terms.mkString(",")})"
  }
}
object Or {
  def apply(terms: Expr*): Or = {
    new Or(terms: _*)
  }

  def unapplySeq(a: Or): Option[Seq[Expr]] = {
    Some(a.terms)
  }
}

// Useful for readability and possibly for optimization
case object DontCare extends Expr {
  override def children: Seq[Expr] = Seq()
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq() => this
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }
}

// Streams
case class StmBuild(
    n: Expr /* Int */,
    output: Expr /* Option<B> */,
    equations: Map[Param, (Expr, Expr)] /* (A, A) */
) extends Expr {
  override def children: Seq[Expr] = {
    Seq(n, output) ++ equations.flatMap({ case (x, (z, next)) =>
      Seq(x, z, next)
    })
  }
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n, output, eqns @ _*) if eqns.length % 3 == 0 =>
        val equations = (0 until eqns.length / 3)
          .map(i => {
            val x = eqns(3 * i).asInstanceOf[Param]
            val z = eqns(3 * i + 1)
            val next = eqns(3 * i + 2)
            x -> (z, next)
          })
          .toMap
        StmBuild(n, output, equations)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }

  lazy val accVars: Set[Param] = equations.keySet
  lazy val seedByVar: Map[Param, Expr] =
    equations.map({ case (x, (v, _)) => x -> v })
  lazy val nextByVar: Map[Param, Expr] =
    equations.map({ case (x, (_, next)) => x -> next })

  /** Construct a new <code>StmBuild</code> that is equivalent to this one but
    * where all the accumulator variables have been replaced by fresh variables.
    */
  def renameVars: StmBuild = {
    this.renameVars(accVars.map(x => x -> x.freshCopy).toMap)
  }

  /** Rename all the bound variables in this stream using the given
    * substitutions.
    *
    * @param replacements
    *   A map from old variables to new variables.
    */
  private def renameVars(replacements: Map[Param, Param]): StmBuild = {
    val subs: Map[Expr, Expr] = replacements.toMap
    val newOutput = output.substitute(subs)
    val newEquations = replacements.map({ case (x, y) =>
      val oldSeed = seedByVar(x)
      val newNext = nextByVar(x).substitute(subs)
      y -> (oldSeed, newNext)
    })
    StmBuild(n, newOutput, newEquations)
  }

  def replaceVars(replacements: Map[Param, Expr]): StmBuild = {
    if (replacements.keys.exists(x => !accVars.contains(x))) {
      val xs = replacements.keys.filter(x => !accVars.contains(x)).toSeq
      throw new IllegalArgumentException(
        s"Cannot replace variables $xs because they are not part of the stream."
          + s" The stream is $this."
      )
    } else {
      val subs: Map[Expr, Expr] = replacements.toMap
      StmBuild(
        this.n,
        this.output.substitute(subs),
        this.equations
          .filter({ case (x, _) => !replacements.contains(x) })
          .map({ case (x, (z, next)) => x -> (z, next.substitute(subs)) })
      )
    }
  }

  /** Fuse a `StmBuild` with its statically-known stream inputs until it has no
    * more statically-known stream inputs. Note that this requires the stream
    * inputs to each have their own variable in <code>stm</code>; they cannot be
    * in a tuple.
    */
  @tailrec
  final def fuseCompletely(): StmBuild = {
    this.seedByVar.find({ case (_, e) => e.isInstanceOf[StmBuild] }) match {
      case Some((x, _)) => this.fuseWith(x).fuseCompletely()
      case _            => this
    }
  }

  /** Fuse a <code>StmBuild</code> with the input stream represented by variable
    * <code>x</code> (which must be one of the accumulator variables in the
    * stream).
    */
  def fuseWith(x: Param): StmBuild = {
    val consumerStm = this
    val fused = consumerStm.seedByVar.get(x) match {
      case Some(e: StmBuild) =>
        // Rename accumulator variables in case some of them are also being
        // used by the consumer stream
        val producerStm = e.renameVars
        val c = stmNextCallCondition(consumerStm, x)
        val newOutput =
          IfThenElse(
            c,
            // CASE 1: Consumer is reading from producer.
            OptionAccess(
              producerStm.output,
              // CASE 1a: Producer yielded a valid value. Proceed as usual.
              (v: Expr) => consumerStm.output.substitute(StmNext(x).__1 -> v),
              // CASE 1b: Producer did NOT yield a valid value.
              //          The consumer cannot proceed.
              (_: Expr) => NNone
            ),
            // CASE 2: Consumer is not reading from producer.
            //         Proceed as usual.
            //         It's safe to unwrap the Option<T> here because, if it
            //         is being accessed here at all, it means the consumer is
            //         reading a value from the producer without updating the
            //         consumer, which is not allowed.
            consumerStm.output.substitute(
              StmNext(x).__1 -> OptionUnwrapUnsafe(producerStm.output)
            )
          )
        val newEquations = {
          val newConsumerEquations =
            (consumerStm.equations - x)
              .map({ case (y, (z, next)) =>
                y -> (z, IfThenElse(
                  c,
                  // CASE 1: Consumer is reading from producer.
                  OptionAccess(
                    producerStm.output,
                    // CASE 1a: Producer yielded a valid value.
                    //          Update the accumulators in the consumer.
                    (v: Expr) => next.substitute(StmNext(x).__1 -> v),
                    // CASE 1b: Producer did NOT yield a valid value.
                    //          Wait until it does and do not update accumulators.
                    (_: Expr) => y
                  ),
                  // CASE 2: Consumer is not reading from producer.
                  //         Update as usual. It is NOT valid to access the
                  //         value of the producer stream in this case, so
                  //         assume it never happens.
                  next.substitute(StmNext(x).__1 -> DontCare)
                ))
              })
          val newProducerEquations =
            producerStm.equations.map({ case (x, (z, next)) =>
              x -> (z, IfThenElse(
                c,
                // CASE 1: Consumer is reading from producer.
                //         Update accumulators.
                next,
                // CASE 2: Consumer is not reading from input.
                //         Producer does nothing.
                x
              ))
            })
          newConsumerEquations ++ newProducerEquations
        }
        StmBuild(consumerStm.n, newOutput, newEquations)
      case Some(e) =>
        throw new IllegalArgumentException(
          s"Expected the initial value of $x to be a StmBuild, but found $e"
        )
      case None =>
        throw new IllegalArgumentException(
          s"Stream does not contain accumulator variable $x."
            + s" The stream is $consumerStm."
        )
    }
    assert(
      !fused.contains(x),
      s"the stream variable ${x.name} should have been removed completely by fusion"
    )
    assert(
      fused.freeVars() == this.freeVars(),
      "fusion should not have changed the set of free variables"
    )
    fused
  }

  /** Add a new equation to this stream whose value is the number of valid
    * outputs that this stream has <i>previously</i> produced.
    *
    * @param outCtr
    *   The variable to use for the new equation. If the variable already
    *   appears bound in this stream, then the bound variable will be renamed.
    */
  def addOutputCounter(outCtr: Param): StmBuild = {
    val s = if (this.equations.contains(outCtr)) this.renameVars else this
    val z = IntCst(0)
    val next =
      OptionAccess(s.output, (_: Expr) => outCtr + 1, (_: Expr) => outCtr)
    StmBuild(
      s.n,
      s.output,
      s.equations + (outCtr -> (z, next))
    )
  }

  /** Add a new equation to this stream whose value is the number of inputs that
    * this stream has <i>previously</i> read from the input stream represented
    * by <code>x</code>.
    *
    * @param x
    *   The input stream.
    * @param inCtr
    *   The variable to use for the new equation. If the variable already
    *   appears bound in this stream, then the bound variable will be renamed.
    */
  def addInputCounter(x: Param, inCtr: Param): StmBuild = {
    val (renamed, newX) = {
      val replacements = this.accVars.map(x => x -> x.freshCopy).toMap
      val renamed = this.renameVars(replacements)
      val newX = replacements(x)
      (renamed, newX)
    }
    val z = IntCst(0)
    val stmNextCalled = stmNextCallCondition(renamed, newX)
    val next = IfThenElse(stmNextCalled, inCtr + 1, inCtr)
    StmBuild(
      renamed.n,
      renamed.output,
      renamed.equations + (inCtr -> (z, next))
    )
  }

  /** Construct a boolean expression <code>c</code> such that, in
    * <code>stm</code>, the accumulator variable <code>x</code> is updated to
    * <code>StmNext(x).0</code> iff <code>c</code> evaluates to true.
    */
  private def stmNextCallCondition(stm: StmBuild, x: Param): Expr = {
    stmNextCallCondition(stm.nextByVar(x), x)
  }

  private def stmNextCallCondition(e: Expr, x: Param): Expr = {
    e match {
      case TupleAccess(StmNext(y), IntCst(0)) if y == x => True
      case y if y == x                                  => False
      case IfThenElse(c, t, f) =>
        val ct = stmNextCallCondition(t, x)
        val cf = stmNextCallCondition(f, x)
        (c && ct) || (Not(c) && cf)
      case e =>
        throw new IllegalArgumentException(
          s"Illegal update to a stream-valued accumulator element: $e."
        )
    }
  }

  /** Checks for structural equality, ignoring order of equations and names of
    * accumulator variables.
    */
  override def equals(obj: Any): Boolean = {
    obj match {
      case that: StmBuild =>
        if (this eq that) {
          true
        } else if (this.n != that.n) {
          false
        } else if (this.seedByVar.values.toSet != that.seedByVar.values.toSet) {
          false
        } else if (this.equations.size != that.equations.size) {
          false
        } else if (this.hashCode != that.hashCode) {
          false
        } else {
          assert(this.accVars.size == that.accVars.size)
          existsVarRenamingThatMakesEqual(
            domain = this.accVars.toSeq,
            codomain = that.accVars.toSeq,
            map = Map(),
            inverse = Map(),
            that
          )
        }
      case _ => false
    }
  }

  override def hashCode(): Int = {
    // This implementation should be correct, but it may cause excessive
    // collisions since it maps all variables to the same one variable.
    val subs: Map[Expr, Expr] =
      this.accVars.map(x => x -> StmBuild.HashCodeParam).toMap
    val len = this.n
    val out = this.output.substitute(subs)
    val eqns = this.equations.toSeq
      .map({ case (_, (z, next)) =>
        (z, next.substitute(subs))
      })
    val eqnsBag =
      eqns.toSet.map((x: (Expr, Expr)) => x -> eqns.count(y => x == y)).toMap
    // Be careful not to remove equations due to the fact that they'll all use
    // the same param now!
    assert(eqnsBag.values.sum == this.equations.size)
    Objects.hash(len, out, eqnsBag)
  }

  /** Basically just brute-force check through all the possible mappings from
    * one set of variables to the other. This has at least O(n!) runtime (where
    * n is the number of accumulator variables)! D: However, in practice,
    * StmBuilds usually don't have <i>that</i> many accumulators and different
    * accumulators have different seeds, so it shouldn't be too bad.
    */
  private def existsVarRenamingThatMakesEqual(
      domain: Seq[Param],
      codomain: Seq[Param],
      map: Map[Param, Param],
      inverse: Map[Param, Param],
      that: StmBuild
  ): Boolean = {
    if (map.size == domain.size) {
      // We have a full candidate mapping, so check equality
      assert(domain.forall(x => inverse(map(x)) == x))
      assert(codomain.forall(y => map(inverse(y)) == y))
      val freshVarByPair = map.map({ case (x, y) => (x, y) -> Param() })
      // Need to use separate substitutions in case one of the streams refers
      // to a free variable and that same variable happens to be bound in the
      // other stream
      val thisSubs: Map[Expr, Expr] =
        freshVarByPair.map({ case ((x, _), fresh) => x -> fresh })
      val thatSubs: Map[Expr, Expr] =
        freshVarByPair.map({ case ((_, y), fresh) => y -> fresh })
      val eqnsMatch = map.forall({ case (x, y) =>
        (this.nextByVar(x).substitute(thisSubs)
          == that.nextByVar(y).substitute(thatSubs))
      })
      val outputsMatch =
        this.output.substitute(thisSubs) == that.output.substitute(thatSubs)
      eqnsMatch && outputsMatch
    } else {
      // Don't have a full candidate mapping yet, so recurse
      val x = domain(map.size)
      codomain.exists(y => {
        (!inverse.isDefinedAt(y)
        && this.seedByVar(x) == that.seedByVar(y)
        && existsVarRenamingThatMakesEqual(
          domain,
          codomain,
          map + (x -> y),
          inverse + (y -> x),
          that
        ))
      })
    }
  }

  @deprecated
  def seed: Tuple = ???
  @deprecated
  def nextF: Function = ???
}
object StmBuild {
  @deprecated
  def apply(n: Expr, z: Expr, f: Expr): StmBuild = {
    ???
  }

  /** Parameter to be used in the definition of <code>hashCode</code> to ensure
    * that bound variable names don't affect the hash code. <i>It MUST NOT be
    * used for anything else</i>.
    */
  private val HashCodeParam = Param("hashCode")
}

case class StmLength(stream: Expr) extends IntExpr {
  override def children: Seq[Expr] = Seq(stream)
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s) => StmLength(s)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }
}
// element only available for one clock cycle
case class StmNext(stream: Expr /* Stream<A>*/ ) /* (Stream<A>, A) */
    extends Expr {
  override def children: Seq[Expr] = Seq(stream)
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s) => StmNext(s)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }
}

// Vectors
case class VecBuild(len: Expr, f: Function /*Int => Expr*/ ) extends Expr {
  override def children: Seq[Expr] = Seq(len, f)
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n, f) => VecBuild(n, f.asInstanceOf[Function])
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }
}
case class VecAccess(vec: Expr, i: Expr) extends Expr {
  override def children: Seq[Expr] = Seq(vec, i)
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, i) => VecAccess(v, i)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }
}
case class VecLength(vec: Expr) extends IntExpr {
  override def children: Seq[Expr] = Seq(vec)
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v) => VecLength(v)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }
}

// ---------------------------------------------------------------------------------------------------------------------
// Extra, non-synthesizable nodes
// (Useful for evaluation and optimization)

case class VecLiteral(elems: Expr*) extends Expr {
  override def children: Seq[Expr] = elems
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    VecLiteral(newChildren: _*)
  }
}
object VecLiteral {
  def ints(elems: Int*): VecLiteral = {
    VecLiteral(elems.map(n => IntCst(n)): _*)
  }
}

case class StmLiteral(elems: Expr*) extends Expr {
  override def children: Seq[Expr] = elems
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    StmLiteral(newChildren: _*)
  }
  def flatten: StmLiteral = {
    require(elems.forall(e => e.isInstanceOf[StmLiteral]))
    StmLiteral(elems.flatMap(e => e.asInstanceOf[StmLiteral].elems): _*)
  }
}
object StmLiteral {
  def ints(elems: Int*): StmLiteral = {
    StmLiteral(elems.map(n => IntCst(n)): _*)
  }

  val nil: StmLiteral = StmLiteral(Seq[Expr](): _*)
}

case class StmNextK(s: Expr /* Stm<A; n> */, k: Expr /* Int */ ) extends Expr {
  override def children: Seq[Expr] = Seq(s, k)
  override def rebuild(newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, i) => StmNextK(s, i)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }
}
