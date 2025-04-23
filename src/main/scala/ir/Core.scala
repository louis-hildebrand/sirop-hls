package ir

import java.util.concurrent.atomic.AtomicLong
import scala.annotation.tailrec

class BadRebuildError(e: Expr, args: Seq[Expr])
    extends IllegalArgumentException(
      s"Wrong arguments passed to rebuild: node $e, args $args"
    )

/** A node in the core IR.
  */
sealed trait Expr {
  def +(that: Expr): Expr = Sum(this, that)()
  def -(that: Expr): Expr = Sum(this, Prod(-1, that)())()
  def *(that: Expr): Expr = Prod(this, that)()
  def /(that: Expr): Div = Div(this, that)()
  def %(that: Expr): Mod = Mod(this, that)()
  def ===(that: Expr): Equal = Equal(this, that)()
  def !==(that: Expr): Expr = Not(Equal(this, that)())()
  def <(that: Expr): LessThan = LessThan(this, that)()
  def <=(that: Expr): Not = Not(this > that)()
  def >(that: Expr): LessThan = that < this
  def >=(that: Expr): Not = that <= this
  def &&(that: Expr): And = And(this, that)()
  def ||(that: Expr): Or = Or(this, that)()

  // if we use _0, _1, ... for some reasons the Scala compiler gets confused and produces error messages when matching some of the expressions
  def __0: TupleAccess = TupleAccess(this, 0)()
  def __1: TupleAccess = TupleAccess(this, 1)()
  def __2: TupleAccess = TupleAccess(this, 2)()
  def __3: TupleAccess = TupleAccess(this, 3)()
  def __4: TupleAccess = TupleAccess(this, 4)()
  def __5: TupleAccess = TupleAccess(this, 5)()

  /** The type of this node.
    *
    * If this node has a type other than <code>Missing</code>, then all its
    * children must also have types other than <code>Missing</code>.
    */
  val typ: Type

  /** Type check this expression and annotate this node with its type.
    *
    * @return
    *   A new expression that is equal to this one but has a type annotation
    *   that is not <code>Missing</code>
    */
  def tchk(implicit context: Map[Param, Type]): Expr = {
    this match {
      case e if e.typ != Missing => e

      case x: Param =>
        context.get(x) match {
          case Some(t) => x.rebuild(t)
          case None    => throw new TypeError(s"Free variable: $x.")
        }
      case Function(x, t, body) =>
        val newBody = body.tchk(context + (x -> t))
        val newX = x.rebuild(t).asInstanceOf[Param]
        Function(newX, t, newBody)(TyArrow(t, newBody.typ))
      case fc @ FunCall(f, arg) =>
        val newF = f.tchk
        val newArg = arg.tchk
        newF.typ match {
          case TyArrow(t1, t2) =>
            if (newArg.typ.isCompatibleWith(t1)) {
              fc.rebuild(t2, Seq(newF, newArg))
            } else {
              throw new TypeError(
                s"Left-hand side of function call expects input of type $t1, but ${newArg.typ} was provided."
              )
            }
          case t =>
            throw new TypeError(
              s"Left-hand side of function call has type $t. It should be a function."
            )
        }

      case n: IntCst => n.rebuild(TyInt)
      case s @ Sum(terms @ _*) =>
        val newTerms = terms.map(e => e.tchk)
        for ((t, i) <- newTerms.zipWithIndex) {
          if (t.typ != TyInt) {
            throw new TypeError(s"Term $i of sum has type ${t.typ}.")
          }
        }
        s.rebuild(TyInt, newTerms)
      case p @ Prod(factors @ _*) =>
        val newFactors = factors.map(e => e.tchk)
        for ((t, i) <- newFactors.zipWithIndex) {
          if (t.typ != TyInt) {
            throw new TypeError(s"Term $i of sum has type ${t.typ}.")
          }
        }
        p.rebuild(TyInt, newFactors)
      case d @ Div(e1, e2) =>
        val newLhs = e1.tchk
        newLhs.typ match {
          case TyInt => ()
          case t => throw new TypeError(s"Expected type $TyInt but found $t.")
        }
        val newRhs = e2.tchk
        newRhs.typ match {
          case TyInt => ()
          case t => throw new TypeError(s"Expected type $TyInt but found $t.")
        }
        d.rebuild(TyInt, Seq(newLhs, newRhs))
      case m @ Mod(e1, e2) =>
        val newLhs = e1.tchk
        newLhs.typ match {
          case TyInt => ()
          case t => throw new TypeError(s"Expected type $TyInt but found $t.")
        }
        val newRhs = e2.tchk
        newRhs.typ match {
          case TyInt => ()
          case t => throw new TypeError(s"Expected type $TyInt but found $t.")
        }
        m.rebuild(TyInt, Seq(newLhs, newRhs))

      case True  => True
      case False => False
      case ite @ IfThenElse(c, t, f) =>
        val newC = c.tchk
        newC.typ match {
          case TyBool => ()
          case t => throw new TypeError(s"Expected type $TyBool but found $t.")
        }
        val newT = t.tchk
        val newF = f.tchk
        if (newT.typ.isCompatibleWith(newF.typ)) {
          ite.rebuild(newT.typ, Seq(newC, newT, newF))
        } else {
          throw new TypeError(
            s"True branch of if-then-else has type ${newT.typ} but false branch has type ${newF.typ}."
          )
        }
      case a @ And(terms @ _*) =>
        val newTerms = terms.map(e => e.tchk)
        for ((t, i) <- newTerms.zipWithIndex) {
          if (t.typ != TyBool) {
            throw new TypeError(s"Term $i of AND has type ${t.typ}.")
          }
        }
        a.rebuild(TyBool, newTerms)
      case or @ Or(terms @ _*) =>
        val newTerms = terms.map(e => e.tchk)
        for ((t, i) <- newTerms.zipWithIndex) {
          if (t.typ != TyBool) {
            throw new TypeError(s"Term $i of OR has type ${t.typ}.")
          }
        }
        or.rebuild(TyBool, newTerms)
      case n @ Not(e) =>
        val newE = e.tchk
        newE.typ match {
          case TyBool => ()
          case t => throw new TypeError(s"Expected type $TyBool but found $t.")
        }
        n.rebuild(TyBool, Seq(newE))
      case eq @ Equal(e1, e2) =>
        val newE1 = e1.tchk
        newE1.typ match {
          case _: TyStm | _: TyArrow =>
            throw new TypeError(s"Cannot compare value of type ${newE1.typ}.")
          case _ => ()
        }
        val newE2 = e2.tchk
        newE2.typ match {
          case _: TyStm | _: TyArrow =>
            throw new TypeError(s"Cannot compare value of type ${newE2.typ}.")
          case _ => ()
        }
        if (newE1.typ.isCompatibleWith(newE2.typ)) {
          eq.rebuild(TyBool, Seq(newE1, newE2))
        } else {
          throw new TypeError(
            s"Left-hand side of Equals has type ${newE1.typ} but right-hand side has type ${newE2.typ}."
          )
        }
      case lt @ LessThan(e1, e2) =>
        val newLhs = e1.tchk
        newLhs.typ match {
          case TyInt => ()
          case t => throw new TypeError(s"Expected type $TyInt but found $t.")
        }
        val newRhs = e2.tchk
        newRhs.typ match {
          case TyInt => ()
          case t => throw new TypeError(s"Expected type $TyInt but found $t.")
        }
        lt.rebuild(TyBool, Seq(newLhs, newRhs))

      case t @ Tuple(elems @ _*) =>
        val newElems = elems.map(e => e.tchk)
        t.rebuild(TyTuple(newElems.map(e => e.typ): _*), newElems)
      case ta @ TupleAccess(t, IntCst(i)) =>
        val newT = t.tchk
        newT.typ match {
          case TyTuple(ts @ _*) =>
            ta.rebuild(ts(i), Seq(newT, IntCst(i)))
          case t =>
            throw new TypeError(s"Left-hand side of tuple access has type $t.")
        }

      case vb @ VecBuild(n, f) =>
        val newN = n.tchk
        newN.typ match {
          case TyInt => ()
          case t     => throw new TypeError(s"Length of VecBuild has type $t.")
        }
        val newF = f match {
          case Function(x, Missing, e) => Function(x, TyInt, e)().tchk
          case f                       => f.tchk
        }
        // TODO: Properly enforce restrictions on contents of vector (e.g., no streams, no functions)
        val vecT = newF.typ match {
          case TyArrow(TyInt, t) => t
          case t => throw new TypeError(s"Function of VecBuild has type $t.")
        }
        vb.rebuild(TyVec(vecT, newN), Seq(newN, newF))
      case va @ VecAccess(v, i) =>
        val newV = v.tchk
        val vecT = newV.typ match {
          case TyVec(t, _) => t
          case t =>
            throw new TypeError(s"Left-hand side of VecAccess has type $t.")
        }
        val newI = i.tchk
        newI.typ match {
          case TyInt => ()
          case t =>
            throw new TypeError(s"Right-hand side of VecAccess has type $t.")
        }
        va.rebuild(vecT, Seq(newV, newI))
      case vl @ VecLength(v) =>
        val newV = v.tchk
        newV.typ match {
          case _: TyVec => ()
          case t =>
            throw new TypeError(s"Argument of VecLength has type $t.")
        }
        vl.rebuild(TyInt, Seq(newV))
      case _: VecLiteral => ???

      case s: StmBuild =>
        val newN = s.n.tchk
        newN.typ match {
          case TyInt => ()
          case t     => throw new TypeError(s"Length of StmBuild has type $t.")
        }
        val newSeedByVar = s.seedByVar.map({ case (x, z) => x -> z.tchk })
        val newContext = newSeedByVar.foldLeft(context)({ case (ctx, (x, z)) =>
          ctx + (x -> z.typ)
        })
        val newNextByVar = s.nextByVar.map({ case (x, next) =>
          val newNext = next.tchk(newContext)
          val initTyp = newSeedByVar(x).typ
          if (initTyp.isCompatibleWith(newNext.typ)) {
            x -> newNext
          } else {
            throw new TypeError(
              s"Next value for accumulator $x has type ${newNext.typ} but the initial value has type $initTyp."
            )
          }
        })
        val newEquations = s.accVars
          .map(x => {
            val z = newSeedByVar(x)
            val newX = x.rebuild(z.typ).asInstanceOf[Param]
            newX -> (z, newNextByVar(x))
          })
          .toMap
        val newOutput = s.output.tchk(newContext)
        val stmT = newOutput.typ match {
          case TyTuple(t, TyBool) => t
          case t => throw new TypeError(s"Output of StmBuild has type $t.")
        }
        StmBuild(newN, newOutput, newEquations)(TyStm(stmT, newN))
      case sn @ StmNext(s) =>
        val newS = s.tchk
        newS.typ match {
          case TyStm(t, n) =>
            sn.rebuild(TyTuple(TyStm(t, n - 1), t), Seq(newS))
          case t => throw new TypeError(s"Argument of StmNext has type $t.")
        }
      case sn @ StmNextK(s, k) =>
        val newK = k.tchk
        newK.typ match {
          case TyInt => ()
          case t     => throw new TypeError(s"Index of StmNextK has type $t.")
        }
        val newS = s.tchk
        newS.typ match {
          case TyStm(t, n) =>
            sn.rebuild(TyStm(t, n - k), Seq(newS, newK))
          case t => throw new TypeError(s"Stream of StmNext has type $t.")
        }
      case sl @ StmLength(s) =>
        val newS = s.tchk
        newS.typ match {
          case _: TyStm => sl.rebuild(TyInt, Seq(newS))
          case t => throw new TypeError(s"Argument of StmNext has type $t.")
        }
      case StmLiteral(typ, elems @ _*) => ???

      case s: SyntaxSugar => s.typecheck(context)
    }
  }

  // TODO: Put this in every constructor
  def checkChildTypes(): Unit = {
    if (this.typ != Missing) {
      assert(
        this.children.forall(e => e.typ != Missing),
        "a typed node must have typed children"
      )
    }
  }

  def children: Seq[Expr]
  def rebuild(typ: Type, newChildren: Seq[Expr]): Expr
  def rebuild(newChildren: Seq[Expr]): Expr = rebuild(Missing, newChildren)
  def rebuild(typ: Type): Expr = rebuild(typ, this.children)
  def map(f: Expr => Expr): Expr = rebuild(children.map(f))

  /** Remove all syntax sugar from this expression and its children.
    */
  def lowerAll(): Expr = {
    val withDesugaredChildren =
      rebuild(this.typ, this.children.map(e => e.lowerAll()))
    val fullyDesugared = withDesugaredChildren match {
      case s: SyntaxSugar => s.lower()
      case e              => e
    }
    // This is required because lowering may be syntax-directed (i.e., an
    // expression may need to be typed before it can be lowered) and it is no
    // good if you type check an expression but then the type is removed while
    // lowering its children.
    assert(fullyDesugared.typ == this.typ, "lowering must preserve type")
    fullyDesugared
  }

  def contains(p: Expr => Boolean): Boolean = {
    p(this) || this.children.exists(e => e.contains(p))
  }
  def contains(e2: Expr): Boolean = this.contains(e => e == e2)
  def contains[T <: Expr](cls: Class[T]): Boolean =
    this.contains(e => cls.isInstance(e))

  /** Perform all the given substitutions on this expression. Substitutions must
    * preserve types (i.e., the old and new expressions must both have the same
    * type).
    */
  def substitute(subs: Map[Expr, Expr]): Expr = {
    if (subs.isEmpty) {
      this
    } else {
      subs.get(this) match {
        case Some(v) => v
        case None =>
          this match {
            case f: Function =>
              // Rename both
              //   (1) to avoid variable capture and
              //   (2) in case f.param appears free in the old value of a
              //       substitution (i.e., the value to be replaced)
              val renamed = f.renameVar
              Function(
                renamed.param,
                f.inputTyp,
                renamed.body.substitute(subs)
              )()
            case s: StmBuild =>
              // Rename both
              //   (1) to avoid variable capture and
              //   (2) in case an accumulator variable appears free in the old
              //       value of a substitution (i.e., the value to be replaced)
              val renamed = s.renameVars
              StmBuild(
                renamed.n.substitute(subs),
                renamed.output.substitute(subs),
                renamed.equations.map({ case (x, (z, next)) =>
                  x -> (z.substitute(subs), next.substitute(subs))
                })
              )()
            case e => e.rebuild(e.typ, e.children.map(e => e.substitute(subs)))
          }
      }
    }
  }

  def substitute(sub: (Expr, Expr)): Expr = substitute(Map(sub))

  def freeVars(): Set[Param] = {
    this match {
      case x: Param          => Set(x)
      case Function(x, _, e) => e.freeVars() - x
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
      case _: SyntaxSugar => 28
    }
  }
}

// Tuples
case class Tuple(elems: Expr*)(val typ: Type = Missing) extends Expr {
  override def children: Seq[Expr] = elems
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr =
    Tuple(newChildren: _*)(typ)
}

case class TupleAccess(t: Expr, i: IntCst)(val typ: Type = Missing)
    extends Expr {
  override def children: Seq[Expr] = Seq(t, i)
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(t, i: IntCst) => TupleAccess(t, i)(typ)
      case _                 => throw new BadRebuildError(this, newChildren)
    }
  }
}

// Functions
case class Param(prefix: String, id: Long)(val typ: Type) extends Expr {
  val name: String = s"${prefix}_$id"

  override def children: Seq[Expr] = Seq()
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(newChildren.isEmpty)
    Param(this.prefix, this.id)(typ)
  }

  def freshCopy: Param = Param(this.prefix)(this.typ)

  override def toString: String = name
}
case object Param {
  private val idCtr = new AtomicLong()

  def apply(prefix: String)(typ: Type = Missing): Param = {
    new Param(prefix, idCtr.incrementAndGet())(typ)
  }
}

case class Function(param: Param, inputTyp: Type, body: Expr)(
    val typ: Type = Missing
) extends Expr {
  // NOTE: it doesn't work to have a field called `outputTyp` and let `typ`
  //       always be `TyArrow(inputTyp, outputTyp)` because this may lead to
  //       violations of the constraint that a typed node has typed children.
  if (typ != Missing) {
    assert(
      typ.isInstanceOf[TyArrow],
      "the type of a function must be an arrow type"
    )
    assert(
      typ.asInstanceOf[TyArrow].t1 == inputTyp,
      "the input type of a function must agree with its type annotation"
    )
  }

  override def children: Seq[Expr] = Seq(param, body)
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(x: Param, body: Expr) => Function(x, inputTyp, body)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  /** Construct a new function in which the bound variable has a fresh name.
    */
  def renameVar: Function = {
    val newParam = this.param.freshCopy
    Function(
      newParam,
      inputTyp,
      this.body.substitute(this.param -> newParam)
    )(typ)
  }

  override def equals(x: Any): Boolean = {
    x match {
      case that: Function =>
        val fresh = Param("p")()
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
  private val HashCodeParam = Param("hashCode")()
}

case class FunCall(f: Expr, arg: Expr)(val typ: Type = Missing) extends Expr {
  override def children: Seq[Expr] = Seq(f, arg)
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(newChildren.length == 2)
    FunCall(newChildren.head, newChildren(1))(typ)
  }
}

sealed trait BinOp extends Expr {
  val e1: Expr
  val e2: Expr

  override def children: Seq[Expr] = Seq(e1, e2)
}

// Integer expressions
// Unfortunately, cannot say that this node always has type Int.
// If we do, then the type checker will not visit the children, which may lead
// to type errors being missed.
sealed trait IntExpr extends Expr

case class IntCst(i: Int) extends IntExpr {
  val typ: Type = TyInt
  override def children: Seq[Expr] = Seq()
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(typ == TyInt || typ == Missing)
    require(newChildren.isEmpty)
    this
  }
}

case class Sum(terms: Expr*)(val typ: Type) extends IntExpr {
  override def children: Seq[Expr] = terms

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    Sum(newChildren: _*)(typ)
  }
}
case object Sum {
  def apply(unsortedTerms: Expr*)(typ: Type = Missing): Sum = {
    val terms = unsortedTerms
      // Flatten nested sums to represent associativity
      .flatMap({
        case s: Sum => s.terms
        case e      => Seq(e)
      })
      // Sort terms to represent commutativity
      .sorted(ExprOrdering)
    new Sum(terms: _*)(typ)
  }
}

case class Prod(factors: Expr*)(val typ: Type) extends IntExpr {
  override def children: Seq[Expr] = factors

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    Prod(newChildren: _*)(typ)
  }
}
case object Prod {
  def apply(unsortedFactors: Expr*)(typ: Type = Missing): Prod = {
    val factors: Seq[Expr] =
      unsortedFactors
        // Flatten nested sums to represent associativity
        .flatMap({
          case p: Prod => p.factors
          case e       => Seq(e)
        })
        // Sort terms to represent commutativity
        .sorted(ExprOrdering)
    new Prod(factors: _*)(typ)
  }
}

case class Div(e1: Expr, e2: Expr)(val typ: Type = Missing)
    extends IntExpr
    with BinOp {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(newChildren.length == 2)
    Div(newChildren.head, newChildren(1))(typ)
  }
}

case class Mod(e1: Expr, e2: Expr)(val typ: Type = Missing)
    extends IntExpr
    with BinOp {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(newChildren.length == 2)
    Mod(newChildren.head, newChildren(1))(typ)
  }
}

// Boolean expressions
// Unfortunately, cannot say that this node always has type Int.
// If we do, then the type checker will not visit the children, which may lead
// to type errors being missed.
sealed trait BoolExpr extends Expr
sealed trait BoolCst extends BoolExpr {
  val typ: Type = TyBool
  override def children: Seq[Expr] = Seq()
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(typ == TyBool || typ == Missing)
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
case class IfThenElse(c: Expr, t: Expr, f: Expr)(val typ: Type) extends Expr {
  override def children: Seq[Expr] = Seq(c, t, f)

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(c, t, f) => IfThenElse(c, t, f)(typ)
      case _            => throw new BadRebuildError(this, newChildren)
    }
  }
}
case object IfThenElse {
  def apply(c: Expr, t: Expr, f: Expr)(
      typ: Type = Missing
  ): IfThenElse = {
    c match {
      case Not(c) => new IfThenElse(c, f, t)(typ)
      case c      => new IfThenElse(c, t, f)(typ)
    }
  }
}

// Comparison operators
case class Equal(e1: Expr, e2: Expr)(val typ: Type = Missing)
    extends BoolExpr
    with BinOp {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e1, e2) => Equal(e1, e2)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }
}

case class LessThan(e1: Expr, e2: Expr)(val typ: Type = Missing)
    extends BoolExpr
    with BinOp {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e1, e2) => LessThan(e1, e2)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }
}

// Logical operators
case class Not(e: Expr)(val typ: Type = Missing) extends BoolExpr {
  override def children: Seq[Expr] = Seq(e)
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => Not(e)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }
}

case class And(terms: Expr*)(val typ: Type) extends BoolExpr {
  override def children: Seq[Expr] = terms

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    And(newChildren: _*)(typ)
  }

  def remove(c: Expr): Expr = {
    terms.filter(e => e != c) match {
      case Seq()  => True
      case Seq(e) => e
      case terms  => And(terms: _*)(this.typ)
    }
  }
}
case object And {
  def apply(unsortedTerms: Expr*)(typ: Type = Missing): And = {
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
    new And(terms: _*)(typ)
  }
}

case class Or(terms: Expr*)(val typ: Type) extends BoolExpr {
  override def children: Seq[Expr] = terms

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    Or(newChildren: _*)(typ)
  }

  def remove(c: Expr): Expr = {
    terms.filter(e => e != c) match {
      case Seq()  => False
      case Seq(e) => e
      case terms  => Or(terms: _*)(this.typ)
    }
  }
}
case object Or {
  def apply(unsortedTerms: Expr*)(typ: Type = Missing): Or = {
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
    new Or(terms: _*)(typ)
  }
}

// Streams
case class StmBuild(
    n: Expr /* Int */,
    output: Expr /* Option<B> */,
    equations: Map[Param, (Expr, Expr)] = Map() /* (A, A) */
)(val typ: Type = Missing)
    extends Expr {
  override def children: Seq[Expr] = {
    Seq(n, output) ++ equations.flatMap({ case (x, (z, next)) =>
      Seq(x, z, next)
    })
  }
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
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
        StmBuild(n, output, equations)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
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

  /** Construct a new <code>StmBuild</code> that is equivalent to this one but
    * where the accumulator variable <code>x</code> has been replaced by a fresh
    * variable.
    */
  def renameVar(x: Param): StmBuild = {
    renameVars(Map(x -> x.freshCopy))
  }

  /** Rename all the bound variables in this stream using the given
    * substitutions.
    *
    * @param replacements
    *   A map from old variables to new variables.
    */
  private def renameVars(replacements: Map[Param, Param]): StmBuild = {
    require(
      replacements.keys.forall(x => this.accVars.contains(x)),
      "all the variables to be replaced must appear in this stream"
    )
    val subs: Map[Expr, Expr] = replacements.toMap
    val newOutput = output.substitute(subs)
    val newEquations = equations.map({ case (x, (z, next)) =>
      val y = replacements.getOrElse(x, x)
      y -> (z, next.substitute(subs))
    })
    StmBuild(n, newOutput, newEquations)(typ)
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
      )(typ)
    }
  }

  def replaceVar(x: Param, e: Expr): StmBuild = replaceVars(Map(x -> e))

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
    val consumerTyp = {
      assert(
        consumerStm.typ != Missing,
        "the consumer must have been type checked before fusion"
      )
      assert(
        consumerStm.typ.isInstanceOf[TyStm],
        "the consumer must be a stream"
      )
      consumerStm.typ.asInstanceOf[TyStm]
    }
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
              (v: Expr) => consumerStm.output.substitute(StmNext(x)().__1 -> v),
              // CASE 1b: Producer did NOT yield a valid value.
              //          The consumer cannot proceed.
              (_: Expr) => NNone(consumerTyp.t)
            )(),
            // CASE 2: Consumer is not reading from producer.
            //         Proceed as usual.
            //         It's safe to unwrap the Option<T> here because, if it
            //         is being accessed here at all, it means the consumer is
            //         reading a value from the producer without updating the
            //         consumer, which is not allowed.
            consumerStm.output.substitute(
              StmNext(x)().__1 -> OptionUnwrapUnsafe(producerStm.output)()
            )
          )()
        val producerTyp = {
          assert(
            producerStm.typ != Missing,
            "the producer must have been type checked before fusion"
          )
          assert(
            producerStm.typ.isInstanceOf[TyStm],
            "the producer be a stream"
          )
          producerStm.typ.asInstanceOf[TyStm]
        }
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
                    (v: Expr) => next.substitute(StmNext(x)().__1 -> v),
                    // CASE 1b: Producer did NOT yield a valid value.
                    //          Wait until it does and do not update accumulators.
                    (_: Expr) => y
                  )()
                    // Need to immediately lower in case this is a stream-valued
                    // accumulator: don't want to introduce an invalid stream
                    // update expression
                    // TODO: it's kind of ugly that this is necessary. Can we
                    //       define StmBuild in such a way that it's not (e.g.,
                    //       by letting the update expression for a stream
                    //       input be a bool)?
                    .lowerAll(),
                  // CASE 2: Consumer is not reading from producer.
                  //         Update as usual.
                  //         If the consumer *does* try to get output from the
                  //         producer at this point, return the default value.
                  next.substitute(StmNext(x)().__1 -> Default(producerTyp.t))
                )())
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
              )())
            })
          newConsumerEquations ++ newProducerEquations
        }
        StmBuild(consumerStm.n, newOutput, newEquations)(typ)
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
    val s =
      if (this.equations.contains(outCtr))
        this.renameVar(outCtr)
      else
        this
    val z = IntCst(0)
    val next =
      OptionAccess(s.output, (_: Expr) => outCtr + 1, (_: Expr) => outCtr)()
    s.addAccumulator(outCtr, z, next)
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
    val s =
      if (this.equations.contains(inCtr))
        this.renameVar(inCtr)
      else
        this
    val z = IntCst(0)
    val stmNextCalled = stmNextCallCondition(s, x)
    val next = IfThenElse(stmNextCalled, inCtr + 1, inCtr)()
    s.addAccumulator(inCtr, z, next)
  }

  /** Add a new accumulator variable to this stream. <i>NOTE:</i> the new
    * variable may capture free variables in this stream.
    */
  def addAccumulator(x: Param, z: Expr, next: Expr): StmBuild = {
    StmBuild(
      this.n,
      this.output,
      this.equations + (x -> (z, next))
    )(typ)
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
        (c && ct) || (Not(c)() && cf)
      case e =>
        throw new IllegalArgumentException(
          s"Illegal update to a stream-valued accumulator element: $e."
        )
    }
  }

  /** Find the direct dependencies between accumulator variables in this stream.
    */
  def accVarDependencies: DiGraph[Param] = {
    val edges = this.nextByVar.toSeq
      .flatMap({ case (x, next) =>
        next.freeVars().intersect(this.accVars).map(y => (x, y))
      })
      .toSet
    DiGraph(nodes = this.accVars, edges = edges)
  }

  /** Find the accumulator variables that the output of this stream depends on.
    */
  def outputDependencies: Set[Param] = {
    this.output.freeVars().intersect(this.accVars)
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
    (len, out, eqnsBag).hashCode
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
      val freshVarByPair = map.map({ case (x, y) => (x, y) -> Param("p")() })
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
}
object StmBuild {

  /** Parameter to be used in the definition of <code>hashCode</code> to ensure
    * that bound variable names don't affect the hash code. <i>It MUST NOT be
    * used for anything else</i>.
    */
  private val HashCodeParam = Param("hashCode")()
}

// TODO: Make this syntax sugar?
case class StmLength(stream: Expr)(val typ: Type = Missing) extends IntExpr {
  override def children: Seq[Expr] = Seq(stream)
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s) => StmLength(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }
}

// element only available for one clock cycle
case class StmNext(stream: Expr /* Stream<A>*/ )(
    val typ: Type = Missing
) /* (Stream<A>, A) */
    extends Expr {
  override def children: Seq[Expr] = Seq(stream)
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s) => StmNext(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }
}

// Vectors
case class VecBuild(len: Expr, f: Function /*Int => Expr*/ )(
    val typ: Type = Missing
) extends Expr {
  override def children: Seq[Expr] = Seq(len, f)
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n, f) => VecBuild(n, f.asInstanceOf[Function])(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }
}

case class VecAccess(vec: Expr, i: Expr)(val typ: Type = Missing) extends Expr {
  override def children: Seq[Expr] = Seq(vec, i)
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, i) => VecAccess(v, i)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }
}

// TODO: Make this syntax sugar?
case class VecLength(vec: Expr)(val typ: Type = Missing) extends IntExpr {
  override def children: Seq[Expr] = Seq(vec)
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v) => VecLength(v)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }
}

// ---------------------------------------------------------------------------------------------------------------------
// Extra, non-synthesizable nodes
// (Useful for evaluation and optimization)

case class VecLiteral(elems: Expr*)(val typ: Type = Missing) extends Expr {
  override def children: Seq[Expr] = elems
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    VecLiteral(newChildren: _*)(typ)
  }
}
object VecLiteral {
  def ints(elems: Int*): VecLiteral = {
    VecLiteral(elems.map(n => IntCst(n)): _*)()
  }
}

case class StmLiteral(elems: Expr*)(val typ: Type = Missing) extends Expr {
  override def children: Seq[Expr] = elems
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    StmLiteral(newChildren: _*)(typ)
  }
  def flatten: StmLiteral = {
    require(elems.forall(e => e.isInstanceOf[StmLiteral]))
    StmLiteral(elems.flatMap(e => e.asInstanceOf[StmLiteral].elems): _*)()
  }
}
object StmLiteral {
  def ints(elems: Int*): StmLiteral = {
    StmLiteral(elems.map(n => IntCst(n)): _*)()
  }

  val nil: StmLiteral = StmLiteral(Seq[Expr](): _*)()
}

case class StmNextK(s: Expr /* Stm<A; n> */, k: Expr /* Int */ )(
    val typ: Type = Missing
) extends Expr {
  override def children: Seq[Expr] = Seq(s, k)
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, i) => StmNextK(s, i)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }
}

trait SyntaxSugar extends Expr {

  def typecheck(context: Map[Param, Type]): Expr

  /** Desugar this node assuming its children have already been desugared.
    *
    * If this expression has already been type checked, this method <i>MUST</i>
    * preserve the type. This method <i>MAY</i> assume that the expression has
    * already been type checked, but it is acceptable to gracefully handle the
    * case where it has not yet been type checked. (This would make it easier to
    * test expressions where lowering does not require the type.)
    */
  def lower(): Expr
}
