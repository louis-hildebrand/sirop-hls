package ir

import java.util.concurrent.atomic.AtomicLong
import scala.annotation.tailrec

class BadRebuildError(e: Expr, args: Seq[Expr])
    extends IllegalArgumentException(
      s"Wrong arguments passed to rebuild: node $e, args $args"
    )

/** A node in the IR.
  *
  * @param typ
  *   The type of this node. If this node has a type other than
  *   <code>Missing</code>, then all its children must also have types other
  *   than <code>Missing</code>.
  */
sealed abstract class Expr(val children: Expr*)(val typ: Type) {
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
  def unary_! : Not = Not(this)()
  def &&(that: Expr): Expr = And(this, that)()
  def ||(that: Expr): Expr = Or(this, that)()

  // if we use _0, _1, ... for some reasons the Scala compiler gets confused and produces error messages when matching some of the expressions
  def __0: TupleAccess = TupleAccess(this, 0)()
  def __1: TupleAccess = TupleAccess(this, 1)()
  def __2: TupleAccess = TupleAccess(this, 2)()
  def __3: TupleAccess = TupleAccess(this, 3)()
  def __4: TupleAccess = TupleAccess(this, 4)()
  def __5: TupleAccess = TupleAccess(this, 5)()

  /** Type check this expression and annotate this node with its type.
    *
    * @return
    *   A new expression that is equal to this one but has a type annotation
    *   that is not <code>Missing</code>
    */
  def tchk(implicit context: Map[Param, Type] = Map()): Expr = {
    this match {
      case e if e.typ != Missing => e

      case x: Param =>
        context.get(x) match {
          case Some(t) => x.rebuild(t)
          case None    => throw new TypeError(s"Free variable: $x.")
        }
      case Function(x, body) =>
        val t = x.typ match {
          case Missing =>
            throw new TypeError(s"Missing function input type annotation.")
          case t => t
        }
        val newBody = body.tchk(context + (x -> t))
        Function(x, newBody)(TyArrow(t, newBody.typ))
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
            throw new TypeError(s"Term $i of product has type ${t.typ}.")
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
      case mux @ Mux(c, t, f) =>
        val newC = c.tchk
        newC.typ match {
          case TyBool => ()
          case t => throw new TypeError(s"Expected type $TyBool but found $t.")
        }
        val newT = t.tchk
        val newF = f.tchk
        if (newT.typ.isCompatibleWith(newF.typ)) {
          mux.rebuild(newT.typ, Seq(newC, newT, newF))
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
        val newF = f.tchk
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
      case VecLiteral(elems @ _*) =>
        elems match {
          case Seq() =>
            this.rebuild(TyVec(Missing, 0), Seq())
          case _ =>
            val newElems = elems.map(e => e.tchk())
            for ((e, i) <- newElems.zipWithIndex.tail) {
              if (e.typ != newElems.head.typ) {
                throw new TypeError(
                  s"Element 0 of vector has type ${newElems.head.typ} but element $i has type ${e.typ}."
                )
              }
            }
            this.rebuild(TyVec(newElems.head.typ, newElems.length), newElems)
        }

      case s: StmBuild =>
        val newN = s.n.tchk.expectType(TyInt)
        val newSeedByVar = s.seedByVar.map({ case (x, z) => x -> z.tchk })
        val newContext = newSeedByVar.foldLeft(context)({ case (ctx, (x, z)) =>
          ctx + (x -> z.typ)
        })
        val newNextByVar = s.nextByVar.map({ case (x, next) =>
          val initTyp = newSeedByVar(x).typ
          val newNext = next.tchk(newContext)
          val expectedNextTyp = initTyp match {
            case _: TyStm => TyBool
            case t        => initTyp
          }
          if (newNext.typ ~= expectedNextTyp) {
            x -> newNext
          } else {
            throw new TypeError(
              s"Next value for accumulator $x has type ${newNext.typ}. Expected type $expectedNextTyp."
            )
          }
        })
        val newEquations = s.accVars
          .map(x => {
            val z = newSeedByVar(x)
            val newX = x.rebuild(z.typ)
            newX -> (z, newNextByVar(x))
          })
          .toMap
        val newOutput = s.output.tchk(newContext)
        val stmT = newOutput.typ match {
          case TyTuple(t, TyBool) => t
          case t =>
            throw new TypeError(
              s"Output of StmBuild has type $t."
                + " Expected a 2-tuple whose second element is a bool."
            )
        }
        StmBuild(newN, newOutput, newEquations)(TyStm(stmT, newN))
      case sn @ StmNextData(s) =>
        val newS = s.tchk
        newS.typ match {
          case TyStm(t, _) => sn.rebuild(t, Seq(newS))
          case t => throw new TypeError(s"Argument of StmNextData has type $t.")
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
      case StmLiteral(elems @ _*) =>
        val checkedElems = elems.map(e => e.tchk())
        val types = checkedElems.map(e => e.typ).toSet
        if (types.isEmpty) {
          throw new IllegalArgumentException(
            "Cannot type check empty stream literal."
          )
        } else if (types.size == 1) {
          val t = types.head
          this.rebuild(TyStm(t, checkedElems.length), checkedElems)
        } else {
          throw new IllegalArgumentException(
            "Inconsistent element types in stream literal."
          )
        }

      case s: SyntaxSugar => s.typecheck(context)
    }
  }

  def eraseTypes(): Expr = {
    this match {
      case x: Param => x
      case e        => e.mapPreOrder(c => c.eraseTypes())
    }
  }

  def expectTypeCompatibleWith(t: Type): Expr = {
    if (!this.typ.isCompatibleWith(t)) {
      throw new TypeError(s"Expected type $t but found ${this.typ}.")
    }
    this
  }

  def expectType(t: Type): Expr = {
    if (this.typ != t) {
      throw new TypeError(s"Expected type $t but found ${this.typ}.")
    }
    this
  }

  protected def requireType(): Unit = {
    require(
      this.typ != Missing,
      s"${this.getClass.getSimpleName} must be type checked before it can be lowered."
    )
  }

  def hasType: Boolean = this.typ != Missing

  if (this.hasType) {
    // This allows the type checker to completely skip expressions that already
    // have a type
    for ((e, i) <- this.children.zipWithIndex) {
      assert(
        e.hasType,
        s"a typed node must have typed children, but child $i in ${this.getClass.getSimpleName} is untyped"
      )
    }
  }

  def rebuild(typ: Type, newChildren: Seq[Expr]): Expr
  def rebuild(newChildren: Seq[Expr]): Expr = rebuild(Missing, newChildren)
  def rebuild(typ: Type): Expr = rebuild(typ, children)
  def map(f: Expr => Expr): Expr = rebuild(children.map(f))
  def mapPreOrder(f: Expr => Expr): Expr = map(f).map(e => e.mapPreOrder(f))

  /** Remove all syntax sugar from this expression and its children. This is
    * guaranteed to preserve type annotations.
    */
  def lower(): Expr = {
    val desugared = this match {
      case s: SyntaxSugar => s.lowerSyntaxSugar()
      case e => e.rebuild(e.typ.lower, e.children.map(e => e.lower()))
    }
    // This is required because lowering may be syntax-directed (i.e., an
    // expression may need to be typed before it can be lowered) and it is no
    // good if you type check an expression but then the type is removed while
    // lowering its children.
    assert(
      !this.hasType || desugared.typ.isCompatibleWith(this.typ.lower),
      s"lowering must yield an expression whose type is the lowered version of the original type (after attempting to lower ${this.getClass.getSimpleName}, expected ${this.typ.lower} but found ${desugared.typ})"
    )
    assert(
      !desugared.contains(classOf[SyntaxSugar]),
      s"lowering must yield an expression without any syntax sugar (after attempting to lower ${this.getClass.getSimpleName}, which yielded $desugared)"
    )
    desugared
  }

  /** Get rid of curried functions.
    *
    * For example, <code>(x: Int) => (y: Int) => x + y</code> would be replaced
    * by <code>(z: (Int, Int)) => z.0 + z.1</code>.
    */
  def uncurry(): Expr = {
    @tailrec
    def getFuncAndArgs(fc: FunCall, args: Seq[Expr]): (Expr, Seq[Expr]) = {
      fc match {
        case FunCall(f: FunCall, arg) => getFuncAndArgs(f, arg +: args)
        case FunCall(f, arg)          => (f, arg +: args)
      }
    }

    this match {
      // TODO: This is not really a general way of un-currying, is it?
      //       For example, what about (x: Int) => if b then f0 else f1 (where
      //       f0 and f1 are themselves functions)?
      case Function(x, body) =>
        val newX = x.uncurry().asInstanceOf[Param]
        val newBody = body.uncurry()
        newBody.typ match {
          case t: TyArrow =>
            newBody match {
              case Function(y, newestBody) =>
                val z = Param("z")(TyTuple(newX.typ, y.typ))
                val subs = Map[Expr, Expr](newX -> z.__0, y -> z.__1)
                Function(z, newestBody.subPreserveType(subs))().tchk()
              case e =>
                throw new IllegalArgumentException(
                  s"Cannot uncurry function with type $t and body $e."
                )
            }
          case _ => Function(newX, newBody)().tchk()
        }
      case fc: FunCall =>
        val (f, args) = getFuncAndArgs(fc, Seq())
        val argTuple = args.init.foldRight(args.last)({ case (e, acc) =>
          Tuple(e, acc)()
        })
        val uncurried = FunCall(f.uncurry(), argTuple)()
        try {
          uncurried.tchk()
        } catch {
          case _: TypeError =>
            throw new IllegalArgumentException(
              "Uncurried function call is not well-typed."
                + " Are there enough arguments?"
                + " Note that partial application is not supported."
                + s" (Expression: $fc)"
            )
        }
      case e => e.rebuild(e.typ.uncurry, e.children.map(e => e.uncurry()))
    }
  }

  def contains(p: Expr => Boolean): Boolean = {
    p(this) || this.children.exists(e => e.contains(p))
  }
  def contains(e2: Expr): Boolean = this.contains(e => e == e2)
  def contains[T <: Expr](cls: Class[T]): Boolean =
    this.contains(e => cls.isInstance(e))

  /** Perform all the given substitutions on this expression. Substitutions does
    * <i>not</i> necessarily preserve type annotations.
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
              Function(renamed.param, renamed.body.substitute(subs))()
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
            case x: Param =>
              // Don't erase the type annotation for variables
              x
            case e =>
              e.rebuild(Missing, e.children.map(e => e.substitute(subs)))
          }
      }
    }
  }

  /** Perform all the given substitutions on this expression. Substitutions does
    * <i>not</i> necessarily preserve type annotations.
    */
  def substitute(sub: (Expr, Expr)): Expr = substitute(Map(sub))

  /** Perform a substitution while preserving all type annotations.
    */
  def subPreserveType(subs: Map[Expr, Expr]): Expr = {
    val out = if (subs.isEmpty) {
      this
    } else {
      subs.get(this) match {
        case Some(v) =>
          // For convenience, automatically preserve the type if you're
          // replacing with a variable (which seems fairly common)
          v match {
            case x: Param => x.rebuild(this.typ)
            case e        => e
          }
        case None =>
          this match {
            case f: Function =>
              // Rename both
              //   (1) to avoid variable capture and
              //   (2) in case f.param appears free in the old value of a
              //       substitution (i.e., the value to be replaced)
              val renamed = f.renameVar
              Function(renamed.param, renamed.body.subPreserveType(subs))(f.typ)
            case s: StmBuild =>
              // Rename both
              //   (1) to avoid variable capture and
              //   (2) in case an accumulator variable appears free in the old
              //       value of a substitution (i.e., the value to be replaced)
              val renamed = s.renameVars
              StmBuild(
                renamed.n.subPreserveType(subs),
                renamed.output.subPreserveType(subs),
                renamed.equations.map({ case (x, (z, next)) =>
                  x -> (z.subPreserveType(subs), next.subPreserveType(subs))
                })
              )(s.typ)
            case e =>
              e.rebuild(e.typ, e.children.map(e => e.subPreserveType(subs)))
          }
      }
    }
    if (this.hasType) {
      assert(
        out.typ.isCompatibleWith(this.typ),
        s"the type should be preserved after substitution (expected ${this.typ}, found ${out.typ})"
      )
    }
    out
  }

  /** Perform a substitution while preserving all type annotations.
    */
  def subPreserveType(sub: (Expr, Expr)): Expr = subPreserveType(Map(sub))

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
      case _: Mux         => 16
      case _: Tuple       => 17
      case _: Function    => 18
      case _: StmBuild    => 19
      case _: StmNextData => 20
      case _: VecBuild    => 21
      case _: VecAccess   => 22
      case _: VecLiteral  => 23
      case _: StmLiteral  => 24
      case _: StmNextK    => 25
      case _: SyntaxSugar => 26
    }
  }
}

// Tuples
case class Tuple(elems: Expr*)(typ: Type = Missing)
    extends Expr(elems: _*)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr =
    Tuple(newChildren: _*)(typ)
}

case class TupleAccess(t: Expr, i: IntCst)(typ: Type) extends Expr(t, i)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(t, i: IntCst) => TupleAccess(t, i)(typ)
      case _                 => throw new BadRebuildError(this, newChildren)
    }
  }
}
case object TupleAccess {
  def apply(t: Expr, i: IntCst)(typ: Type = Missing): TupleAccess = {
    val newTyp = (typ, t.typ) match {
      case (Missing, TyTuple(ts @ _*)) => ts(i.i)
      case _                           => typ
    }
    new TupleAccess(t, i)(newTyp)
  }
}

// Functions
case class Param(prefix: String, id: Long)(typ: Type) extends Expr()(typ) {
  val name: String = s"${prefix}_$id"

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Param = {
    require(newChildren.isEmpty)
    Param(this.prefix, this.id)(typ)
  }
  override def rebuild(newChildren: Seq[Expr]): Param = {
    // Keep the same type by default
    rebuild(this.typ, newChildren)
  }

  override def rebuild(typ: Type): Param = rebuild(typ, this.children)

  def freshCopy: Param = Param(this.prefix)(this.typ)

  override def toString: String = name
}
case object Param {
  private val idCtr = new AtomicLong()

  def apply(prefix: String)(typ: Type = Missing): Param = {
    new Param(prefix, idCtr.incrementAndGet())(typ)
  }
}

case class Function(param: Param, body: Expr)(typ: Type = Missing)
    extends Expr(param, body)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(x: Param, body: Expr) => Function(x, body)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def lower(): Function = super.lower().asInstanceOf[Function]

  /** Construct a new function in which the bound variable has a fresh name.
    */
  def renameVar: Function = {
    val newParam = this.param.freshCopy
    assert(newParam.typ == this.param.typ)
    Function(newParam, this.body.subPreserveType(this.param -> newParam))(typ)
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

case class FunCall(f: Expr, arg: Expr)(typ: Type = Missing)
    extends Expr(f, arg)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(newChildren.length == 2)
    FunCall(newChildren.head, newChildren(1))(typ)
  }
}

// Integer expressions
// Unfortunately, cannot say that this node always has type Int.
// If we do, then the type checker will not visit the children, which may lead
// to type errors being missed.
sealed abstract class IntExpr(children: Expr*)(typ: Type)
    extends Expr(children: _*)(typ)

case class IntCst(i: Int) extends IntExpr()(TyInt) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(typ == TyInt || typ == Missing)
    require(newChildren.isEmpty)
    this
  }
}

case class Sum(terms: Expr*)(typ: Type) extends IntExpr(terms: _*)(typ) {
  require(terms.nonEmpty, "Sum must have at least one term.")

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    Sum(newChildren: _*)(typ)
  }
}
case object Sum {
  def apply(unsortedTerms: Expr*)(typ: Type = Missing): Expr = {
    val terms = unsortedTerms
      // Flatten nested sums to represent associativity
      .flatMap({
        case s: Sum => s.terms
        case e      => Seq(e)
      })
      // Sort terms to represent commutativity
      .sorted(ExprOrdering)
    terms match {
      case Seq()  => 0
      case Seq(e) => e
      case terms =>
        val newTyp = if (typ == Missing && terms.forall(e => e.typ == TyInt)) {
          TyInt
        } else {
          typ
        }
        new Sum(terms: _*)(newTyp)
    }
  }
}

case class Prod(factors: Expr*)(typ: Type) extends IntExpr(factors: _*)(typ) {
  require(factors.nonEmpty, "Prod must have at least one factor.")

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    Prod(newChildren: _*)(typ)
  }
}
case object Prod {
  def apply(unsortedFactors: Expr*)(typ: Type = Missing): Expr = {
    val factors: Seq[Expr] =
      unsortedFactors
        // Flatten nested sums to represent associativity
        .flatMap({
          case p: Prod => p.factors
          case e       => Seq(e)
        })
        // Sort terms to represent commutativity
        .sorted(ExprOrdering)
    factors match {
      case Seq()  => IntCst(1)
      case Seq(e) => e
      case factors =>
        val newTyp =
          if (typ == Missing && factors.forall(e => e.typ == TyInt)) {
            TyInt
          } else {
            typ
          }
        new Prod(factors: _*)(newTyp)
    }
  }
}

case class Div(e1: Expr, e2: Expr)(typ: Type) extends IntExpr(e1, e2)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(newChildren.length == 2)
    Div(newChildren.head, newChildren(1))(typ)
  }
}
case object Div {
  def apply(e1: Expr, e2: Expr)(typ: Type = Missing): Div = {
    val newTyp = if (typ == Missing && e1.typ == TyInt && e2.typ == TyInt) {
      TyInt
    } else {
      typ
    }
    new Div(e1, e2)(newTyp)
  }
}

case class Mod(e1: Expr, e2: Expr)(typ: Type) extends IntExpr(e1, e2)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(newChildren.length == 2)
    Mod(newChildren.head, newChildren(1))(typ)
  }
}
case object Mod {
  def apply(e1: Expr, e2: Expr)(typ: Type = Missing): Mod = {
    val newTyp = if (typ == Missing && e1.typ == TyInt && e2.typ == TyInt) {
      TyInt
    } else {
      typ
    }
    new Mod(e1, e2)(newTyp)
  }
}

// Boolean expressions
// Unfortunately, cannot say that this node always has type Int.
// If we do, then the type checker will not visit the children, which may lead
// to type errors being missed.
sealed abstract class BoolExpr(children: Expr*)(typ: Type)
    extends Expr(children: _*)(typ)
sealed abstract class BoolCst extends BoolExpr()(TyBool) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(typ == TyBool || typ == Missing)
    require(newChildren.isEmpty)
    this
  }
}
case object True extends BoolCst
case object False extends BoolCst

case class Mux(c: Expr, t: Expr, f: Expr)(typ: Type)
    extends Expr(c, t, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(c, t, f) => Mux(c, t, f)(typ)
      case _            => throw new BadRebuildError(this, newChildren)
    }
  }
}
case object Mux {
  def apply(c: Expr, t: Expr, f: Expr)(
      typ: Type = Missing
  ): Mux = {
    c match {
      case Not(c) => new Mux(c, f, t)(typ)
      case c      => new Mux(c, t, f)(typ)
    }
  }
}

// Comparison operators
case class Equal(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends BoolExpr(e1, e2)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e1, e2) => Equal(e1, e2)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }
}

case class LessThan(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends BoolExpr(e1, e2)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e1, e2) => LessThan(e1, e2)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }
}

// Logical operators
case class Not(e: Expr)(typ: Type = Missing) extends BoolExpr(e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => Not(e)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }
}
case object Not {
  def apply(e: Expr)(typ: Type = Missing): Not = {
    if (typ == Missing && e.typ == TyBool) {
      new Not(e)(TyBool)
    } else {
      new Not(e)(typ)
    }
  }
}

case class And(terms: Expr*)(typ: Type) extends BoolExpr(terms: _*)(typ) {
  require(terms.nonEmpty, "And must have at least one term.")

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
  def apply(unsortedTerms: Expr*)(typ: Type = Missing): Expr = {
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
    terms match {
      case Seq()  => True
      case Seq(e) => e
      case terms =>
        val newTyp = if (typ == Missing && terms.forall(e => e.typ == TyBool)) {
          TyBool
        } else {
          typ
        }
        new And(terms: _*)(newTyp)
    }
  }
}

case class Or(terms: Expr*)(typ: Type) extends BoolExpr(terms: _*)(typ) {
  require(terms.nonEmpty, "Or must have at least one term.")

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
  def apply(unsortedTerms: Expr*)(typ: Type = Missing): Expr = {
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
    terms match {
      case Seq()  => False
      case Seq(e) => e
      case terms =>
        val newTyp = if (typ == Missing && terms.forall(e => e.typ == TyBool)) {
          TyBool
        } else {
          typ
        }
        new Or(terms: _*)(newTyp)
    }
  }
}

// Streams
case class StmBuild(
    n: Expr /* Int */,
    output: Expr /* Option<B> */,
    equations: Map[Param, (Expr, Expr)] = Map() /* (A, A) */
)(typ: Type = Missing)
    extends Expr(
      Seq(n, output) ++ equations.flatMap({ case (x, (z, next)) =>
        Seq(x, z, next)
      }): _*
    )(typ) {
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
  def renameVars(replacements: Map[Param, Param]): StmBuild = {
    require(
      replacements.keys.forall(x => this.accVars.contains(x)),
      "all the variables to be replaced must appear in this stream"
    )
    val subs: Map[Expr, Expr] = replacements.toMap
    val newOutput = output.subPreserveType(subs)
    val newEquations = equations.map({ case (x, (z, next)) =>
      val y = replacements.getOrElse(x, x).rebuild(x.typ)
      y -> (z, next.subPreserveType(subs))
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
      )()
    }
  }

  def replaceVar(x: Param, e: Expr): StmBuild = replaceVars(Map(x -> e))

  /** Fuse a `StmBuild` with its statically-known stream inputs until it has no
    * more statically-known stream inputs. Note that this requires the stream
    * inputs to each have their own variable in <code>stm</code>; they cannot be
    * in a tuple.
    *
    * Fusion is guaranteed to preserve type annotations.
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
    require(
      consumerStm.typ != Missing,
      "The consumer must have been type checked before fusion."
    )
    val fused = consumerStm.seedByVar.get(x) match {
      case Some(e: StmBuild) =>
        // Rename accumulator variables in case some of them are also being
        // used by the consumer stream
        val producerStm = e.renameVars
        val readyCond = stmNextCallCondition(consumerStm, x)
        assert(readyCond.typ == TyBool, "stream call condition must be a bool")
        val newOutput = fusedOutput(
          consumer = consumerStm,
          producer = producerStm,
          ready = readyCond,
          x = x
        )
        val newEquations = {
          val newConsumerEquations = (consumerStm.equations - x).map(
            fusedConsumerAccumulator(
              producer = producerStm,
              ready = readyCond,
              x = x
            )
          )
          val newProducerEquations =
            producerStm.equations.map(fusedProducerAccumulator(readyCond))
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
    assert(fused.typ == this.typ, "fusion should preserve type annotations")
    fused
  }

  /** Construct an expression representing the output of the consumer after
    * fusion.
    *
    * @param consumer
    *   The consumer stream
    * @param producer
    *   The producer stream
    * @param ready
    *   A boolean expression which evaluates to <code>True</code> iff the
    *   consumer is reading from the producer (i.e., the <code>ready</code>
    *   signal is high).
    * @param x
    *   The accumulator variable for the producer
    */
  private def fusedOutput(
      consumer: StmBuild,
      producer: StmBuild,
      ready: Expr,
      x: Param
  ): Expr = {
    val consumerTyp = consumer.typ.asInstanceOf[TyStm]
    val producerTyp = producer.typ.asInstanceOf[TyStm]
    Mux(
      ready,
      // CASE 1: Consumer is ready (i.e., reading from producer).
      OptionAccess(
        producer.output,
        // CASE 1a: Producer yielded a valid value. Proceed as usual.
        producerTyp.t ::+ (v =>
          consumer.output.subPreserveType(StmNextData(x)() -> v)
        ),
        // CASE 1b: Producer did NOT yield a valid value.
        //          The consumer cannot proceed.
        TyTuple() ::+ (_ => NNone(consumerTyp.t))
      )(),
      // CASE 2: Consumer is not ready (i.e., not reading from producer).
      //         Proceed as usual, but with the default value for the producer
      //         output (this should not be read).
      consumer.output.subPreserveType(
        StmNextData(x)() -> Default(producerTyp.t)
      )
    )().tchk()
  }

  /** Construct a new recurrence equation after fusion for an accumulator in the
    * consumer stream.
    *
    * @param producer
    *   The producer stream
    * @param ready
    *   A boolean expression which evaluates to <code>True</code> iff the
    *   consumer is reading from the producer (i.e., the <code>ready</code>
    *   signal is high).
    * @param x
    *   The accumulator variable for the producer
    * @param eqn
    *   The original recurrence equation
    */
  private def fusedConsumerAccumulator(
      producer: StmBuild,
      ready: Expr,
      x: Param
  )(
      eqn: (Param, (Expr, Expr))
  ): (Param, (Expr, Expr)) = {
    val producerTyp = producer.typ.asInstanceOf[TyStm]
    eqn match {
      case (y, (z, next)) =>
        y -> (
          z,
          Mux(
            ready,
            // CASE 1: Consumer is reading from producer.
            OptionAccess(
              producer.output,
              // CASE 1a: Producer yielded a valid value.
              //          Update the accumulators.
              producerTyp.t ::+ (v =>
                next.subPreserveType(StmNextData(x)() -> v)
              ),
              // CASE 1b: Producer did NOT yield a valid value.
              //          Do not update accumulators until it does.
              y.typ match {
                case _: TyStm => TyTuple() ::+ (_ => False)
                case _        => TyTuple() ::+ (_ => y)
              }
            )(),
            // CASE 2: Consumer is not reading from producer.
            //         Update as usual, but with the default value for the
            //         producer output (this should not be read).
            next.subPreserveType(
              StmNextData(x)() -> Default(producerTyp.t)
            )
          )().tchk().lower()
        )
    }
  }

  /** Construct a new recurrence equation after fusion for an accumulator in the
    * producer stream.
    *
    * @param readyCond
    *   A boolean expression which evaluates to <code>True</code> iff the
    *   consumer is reading from the producer (i.e., the <code>ready</code>
    *   signal is high).
    * @param eqn
    *   The original recurrence equation
    */
  private def fusedProducerAccumulator(
      readyCond: Expr
  )(eqn: (Param, (Expr, Expr))): (Param, (Expr, Expr)) = {
    eqn match {
      case (x, (z, next)) =>
        x -> (
          z,
          Mux(
            readyCond,
            // CASE 1: Consumer is reading from producer.
            //         Update accumulators.
            next,
            // CASE 2: Consumer is not reading from input.
            //         Producer does nothing.
            x.typ match {
              case _: TyStm => False
              case _        => x
            }
          )().tchk()
        )
    }
  }

  /** Add a new equation to this stream whose value is the number of valid
    * outputs that this stream has <i>previously</i> produced.
    *
    * @param outCtr
    *   The variable to use for the new equation. If the variable already
    *   appears bound in this stream, then the bound variable will be renamed.
    */
  def addOutputCounter(p: Param): StmBuild = {
    requireType()
    val outCtr = p.rebuild(TyInt)
    val s =
      if (this.equations.contains(outCtr))
        this.renameVar(outCtr)
      else
        this
    val z = IntCst(0)
    val next = Mux(IsSome(s.output)(TyBool), outCtr + 1, outCtr)(TyInt)
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
  def addInputCounter(x: Param, p: Param): StmBuild = {
    requireType()
    val inCtr = p.rebuild(TyInt)
    val s =
      if (this.equations.contains(inCtr))
        this.renameVar(inCtr)
      else
        this
    val z = IntCst(0)
    val stmNextCalled = stmNextCallCondition(s, x)
    val next = Mux(stmNextCalled, inCtr + 1, inCtr)(TyInt)
    s.addAccumulator(inCtr, z, next)
  }

  /** Add a new accumulator variable to this stream. <i>NOTE:</i> the new
    * variable may capture free variables in this stream.
    */
  def addAccumulator(x: Param, z: Expr, next: Expr): StmBuild = {
    val newEquations = this.equations + (x -> (z, next))
    val isTyped = (x.hasType && z.hasType && next.hasType
      && z.typ.isCompatibleWith(x.typ) && next.typ.isCompatibleWith(x.typ))
    val t = if (isTyped) this.typ else Missing
    StmBuild(this.n, this.output, newEquations)(t)
  }

  /** Construct a boolean expression <code>c</code> such that, in
    * <code>stm</code>, the accumulator variable <code>x</code> is updated to
    * <code>StmNext(x).0</code> iff <code>c</code> evaluates to true.
    */
  private def stmNextCallCondition(stm: StmBuild, x: Param): Expr = {
    StmBuild.stmNextCallCondition(stm.nextByVar(x), x)
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
      this.accVars.map(x => x -> StmBuild.HashCodeParam.rebuild(x.typ)).toMap
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
      val freshVarByPair = map.map({ case (x, y) =>
        (x, y) -> (if (x.typ != Missing) Param("p")(x.typ)
                   else Param("p")(y.typ))
      })
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

  @deprecated
  def stmNextCallCondition(e: Expr, x: Param): Expr = {
    e
  }
}

case class StmNextData(s: Expr)(typ: Type = Missing) extends Expr(s)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s) => StmNextData(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }
}

// Vectors
case class VecBuild(len: Expr, f: Function /* Int => Expr */ )(
    typ: Type = Missing
) extends Expr(len, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n, f) => VecBuild(n, f.asInstanceOf[Function])(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }
}

case class VecAccess(vec: Expr, i: Expr)(typ: Type = Missing)
    extends Expr(vec, i)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, i) => VecAccess(v, i)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }
}

// ---------------------------------------------------------------------------------------------------------------------
// Extra, non-synthesizable nodes
// (Useful for evaluation and optimization)

case class VecLiteral(elems: Expr*)(typ: Type = Missing)
    extends Expr(elems: _*)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    VecLiteral(newChildren: _*)(typ)
  }
}
object VecLiteral {
  def ints(elems: Int*): VecLiteral = {
    VecLiteral(elems.map(n => IntCst(n)): _*)()
  }
}

case class StmLiteral(elems: Expr*)(typ: Type = Missing)
    extends Expr(elems: _*)(typ) {
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
}

case class StmNextK(s: Expr /* Stm<A; n> */, k: Expr /* Int */ )(
    typ: Type = Missing
) extends Expr(s, k)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, i) => StmNextK(s, i)(typ)
      case _         => throw new BadRebuildError(this, newChildren)
    }
  }
}

abstract class SyntaxSugar(children: Expr*)(typ: Type)
    extends Expr(children: _*)(typ) {

  def typecheck(context: Map[Param, Type]): Expr

  /** Remove syntax sugar from this node and its children.
    *
    * If this expression has already been type checked, this method <i>MUST</i>
    * return the flattened version of that same type. This method <i>MAY</i>
    * assume that the expression has already been type checked, but it is
    * acceptable to gracefully handle the case where it has not yet been type
    * checked. (This would make it easier to test expressions where lowering
    * does not require the type.)
    */
  def lowerSyntaxSugar(): Expr
}

case class StmLength(s: Expr)(typ: Type = Missing) extends SyntaxSugar(s)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s) => StmLength(s)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newS = s.tchk(context)
    newS.typ match {
      case _: TyStm => this.rebuild(TyInt, Seq(newS))
      case t =>
        throw new TypeError(
          s"Stream in StmLength has type $t. Expected a stream."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    s.typ.asInstanceOf[TyStm].n.lower()
  }
}

case class VecLength(v: Expr)(typ: Type = Missing) extends SyntaxSugar(v)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v) => VecLength(v)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newV = v.tchk(context)
    newV.typ match {
      case _: TyVec => this.rebuild(TyInt, Seq(newV))
      case t =>
        throw new TypeError(
          s"Vector in VecLength has type $t. Expected a stream."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    v.typ.asInstanceOf[TyVec].n.lower()
  }
}
