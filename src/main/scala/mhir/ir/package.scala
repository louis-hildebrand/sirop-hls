package mhir

import mhir.ir.evaluate.Eval
import mhir.ir.typecheck.{TypeCheck, TypeError}

import scala.annotation.nowarn
import scala.language.implicitConversions

/** The core IR, type checker ([[mhir.ir.typecheck]]), evaluator
  * ([[mhir.ir.evaluate]]), and a few other tidbits.
  *
  * The root class for the IR is [[mhir.ir.Expr]]. There are the typical lambda
  * calculus primitives ([[Param]], [[Function]], [[FunCall]]), some primitives
  * for integer and boolean arithmetic (e.g., [[Sum]], [[And]]), etc. However,
  * the most notable primitives are the ones for sequences. "Vectors" can only
  * be constructed using the general-purpose [[VecBuild]] primitive and
  * "streams" can only be constructed using the general-purpose [[StmBuild]]
  * primitive.
  */
// Use the fully-qualified name for CommonIntTypes; otherwise, Scaladoc fails
// for some reason.
package object ir extends Eval with mhir.ir.typecheck.CommonIntTypes {
  //  TODO: This is a bit dangerous, since it is easy to accidentally discard
  //        an IntCst's type this way. But it is already used in so many places
  //        that it seems wildly impractical to review them all.

  /** Implicitly converts an integer to an [[IntCst]].
    */
  implicit def int2IntCst(i: Int): IntCst = IntCst(i)()

  /** Implicitly converts an integer to an [[ExprOps]] so that shorthands like
    * [[ExprOps.+]] can be used.
    *
    * @example
    *
    * {{{
    *   import mhir.ir.ExprOps
    *   val e1: Expr = ...
    *   val e2: Expr = 1 + e2
    * }}}
    */
  implicit def int2ExprOps(i: Int): ExprOps = new ExprOps(IntCst(i)())

  // WARNING: do not provide an implicit Boolean to BoolCst conversion because
  // it is much too easy to accidentally write e1 == e2 (which compares
  // syntactically and then converts to True or False) rather than e1 === e2
  // (which constructs an expression like Equal(e1, e2) ).

  /** Implicitly converts `()` to an empty [[TyTuple]].
    */
  implicit def typeTuple0(@nowarn t: Unit): TyTuple = TyTuple()

  /** Implicitly converts a tuple of [[Type]] to a [[TyTuple]] with those same
    * types.
    */
  implicit def typeTuple2(t: (Type, Type)): TyTuple = TyTuple(t._1, t._2)

  /** Implicitly converts a tuple of [[Type]] to a [[TyTuple]] with those same
    * types.
    */
  implicit def typeTuple3(t: (Type, Type, Type)): TyTuple =
    TyTuple(t._1, t._2, t._3)

  /** Reset any internal state in this package. For example, the [[Param]] class
    * has an internal counter for generating fresh variables.
    */
  def resetState(): Unit = {
    // StmBuild and Function both have a Param in the companion object.
    // Force initialization of the companion object so that the param is
    // initialized.
    StmBuild.forceInit()
    Function.forceInit()
    Param.resetCounter()
  }

  /** Helper methods and shorthands for common operations related to all
    * [[Expr]]s.
    */
  implicit class ExprOps(expr: Expr) {

    /** Constructs a function call with the given argument.
      *
      * @param arg
      *   the function argument.
      */
    def apply(arg: Expr): FunCall = FunCall(this.expr, arg)()

    /** See [[SmartSum]].
      */
    def +(that: Expr): Expr = SmartSum(this.expr, that)()

    /** See [[SmartSum]] and [[SmartProd]].
      */
    def -(that: Expr): Expr = this.expr + that * -1

    /** See [[SmartProd]].
      */
    def *(that: Expr): Expr = SmartProd(this.expr, that)()

    /** See [[SmartDiv]].
      */
    def /(that: Expr): Expr = SmartDiv(this.expr, that)()

    /** See [[SmartMod]].
      */
    def %(that: Expr): Expr = SmartMod(this.expr, that)()

    /** See [[SmartEqual]].
      */
    def ===(that: Expr): Expr = SmartEqual(this.expr, that)()

    /** See [[Equal]].
      */
    def equ(that: Expr): Expr = Equal(this.expr, that)()

    /** See [[SmartEqual]].
      */
    def !==(that: Expr): Expr = !(this.expr === that)

    /** See [[Equal]].
      */
    def nequ(that: Expr): Expr = !(this.expr equ that)

    /** See [[SmartLessThan]].
      */
    def <(that: Expr): Expr = SmartLessThan(this.expr, that)()

    /** See [[LessThan]].
      */
    def lt(that: Expr): LessThan = LessThan(this.expr, that)()

    /** See [[SmartLessThan]].
      */
    def <=(that: Expr): Not = !(this.expr > that)

    /** See [[LessThan]]
      */
    def leq(that: Expr): Not = !(this.expr gt that)

    /** See [[SmartLessThan]].
      */
    def >(that: Expr): Expr = that < this.expr

    /** See [[LessThan]].
      */
    def gt(that: Expr): Expr = that lt this.expr

    /** See [[SmartLessThan]].
      */
    def >=(that: Expr): Expr = that <= this.expr

    /** See [[LessThan]].
      */
    def geq(that: Expr): Expr = that leq this.expr

    /** See [[Not]].
      */
    def unary_! : Not = Not(this.expr)()

    /** See [[And]].
      */
    def &&(that: Expr): Expr = And(this.expr, that)()

    /** See [[Or]].
      */
    def ||(that: Expr): Expr = Or(this.expr, that)()

    // if we use _0, _1, ... for some reasons the Scala compiler gets confused and produces error messages when matching some of the expressions

    /** Constructs a [[TupleAccess]] with index 0.
      */
    def __0: TupleAccess = TupleAccess(this.expr, 0)()

    /** Constructs a [[TupleAccess]] with index 1.
      */
    def __1: TupleAccess = TupleAccess(this.expr, 1)()

    /** Constructs a [[TupleAccess]] with index 2.
      */
    def __2: TupleAccess = TupleAccess(this.expr, 2)()

    /** Constructs a [[TupleAccess]] with index 3.
      */
    def __3: TupleAccess = TupleAccess(this.expr, 3)()

    /** Reconstruct this expression with new children and erase the type
      * annotation.
      *
      * @param newChildren
      *   the new children.
      */
    def rebuildAndEraseType(newChildren: Seq[Expr]): Expr = {
      this.expr match {
        case _: IntCst | _: Param | _: StmLiteral | _: VecLiteral =>
          // These expressions may carry type information that cannot be derived
          // from the syntax alone, so be careful not to discard it.
          this.expr.rebuild(this.expr.typ, newChildren)
        case _ =>
          this.expr.rebuild(Missing, newChildren)
      }
    }

    /** Reconstruct this expression with a new type annotation but the same
      * children.
      *
      * @param typ
      *   the new type annotation.
      */
    def rebuild(typ: Type): Expr = this.expr.rebuild(typ, this.expr.children)

    /** Rebuild this expression after transforming each of its children.
      *
      * @param f
      *   the function to apply to each child.
      */
    def map(f: Expr => Expr): Expr = {
      this.expr.rebuildAndEraseType(this.expr.children.map(f))
    }

    /** Perform the given substitutions in this expression while erasing the
      * type annotations.
      *
      * @param subs
      *   a map from old expressions (i.e., the ones to be replaced) to new
      *   expressions (i.e., what to replace the old expressions with).
      */
    def subAndEraseType(subs: Map[Expr, Expr]): Expr = {
      if (subs.isEmpty) {
        this.expr
      } else {
        subs.get(this.expr) match {
          case Some(v) =>
            v
          case None =>
            this.expr match {
              case Function(x, body) =>
                // Rename both
                //   (1) to avoid variable capture and
                //   (2) in case f.param appears free in the old value of a
                //       substitution (i.e., the value to be replaced)
                val newX = x.freshCopy
                Function(
                  newX,
                  body.subAndEraseType(x -> newX).subAndEraseType(subs)
                )()
              case s: StmBuild =>
                // Rename both
                //   (1) to avoid variable capture and
                //   (2) in case an accumulator variable appears free in the old
                //       value of a substitution (i.e., the value to be replaced)
                val renamingSubs: Map[Expr, Expr] =
                  s.accVars.map(x => x -> x.freshCopy).toMap
                StmBuild(
                  s.n.subAndEraseType(subs),
                  s.data.subAndEraseType(renamingSubs).subAndEraseType(subs),
                  s.valid.subAndEraseType(renamingSubs).subAndEraseType(subs),
                  s.equations.map({ case (x, (z, next)) =>
                    val newX = renamingSubs(x).asInstanceOf[Param]
                    val newZ = z.subAndEraseType(subs)
                    val newNext =
                      next.subAndEraseType(renamingSubs).subAndEraseType(subs)
                    newX -> (newZ, newNext)
                  })
                )()
              case e: SyntaxSugar =>
                e.sugarSubAndEraseType(subs)
              case e =>
                e.rebuildAndEraseType(
                  e.children.map(e => e.subAndEraseType(subs))
                )
            }
        }
      }
    }

    /** Shorthand for [[subAndEraseType(subs*]] with just one substitution.
      */
    def subAndEraseType(sub: (Expr, Expr)): Expr = {
      subAndEraseType(Map(sub))
    }

    /** Perform the given substitutions in this expression while checking, at
      * each step, that the new type is compatible with the original one.
      *
      * @param subs
      *   a map from old expressions (i.e., the ones to be replaced) to new
      *   expressions (i.e., what to replace the old expressions with).
      */
    def subPreserveType(subs: Map[Expr, Expr]): Expr = {
      val out = if (subs.isEmpty) {
        this.expr
      } else {
        subs.get(this.expr) match {
          case Some(v) => v
          case None =>
            this.expr match {
              case f @ Function(x, body) =>
                // Rename both
                //   (1) to avoid variable capture and
                //   (2) in case f.param appears free in the old value of a
                //       substitution (i.e., the value to be replaced)
                val newX = x.freshCopy
                Function(
                  newX.subPreserveType(subs).asInstanceOf[Param],
                  body.subPreserveType(x -> newX).subPreserveType(subs)
                )(f.typ)
              case s: StmBuild =>
                // Rename both
                //   (1) to avoid variable capture and
                //   (2) in case an accumulator variable appears free in the old
                //       value of a substitution (i.e., the value to be replaced)
                val renamed = s.renameVars
                StmBuild(
                  renamed.n.subPreserveType(subs),
                  renamed.data.subPreserveType(subs),
                  renamed.valid.subPreserveType(subs),
                  renamed.equations.map({ case (x, (z, next)) =>
                    val newX = x.subPreserveType(subs).asInstanceOf[Param]
                    val newZ = z.subPreserveType(subs)
                    val newNext = next.subPreserveType(subs)
                    newX -> (newZ, newNext)
                  })
                )(s.typ)
              case e: SyntaxSugar => e.sugarSubAndKeepType(subs)
              case e =>
                e.rebuild(e.typ, e.children.map(e => e.subPreserveType(subs)))
            }
        }
      }
      if (this.expr.hasType) {
        assert(
          out.typ ~= this.expr.typ,
          s"the type should be preserved after substitution (expected ${this.expr.typ}, found ${out.typ} after substitutions $subs in ${this.expr})"
        )
      }
      // The expressions to replace may occur within the type (e.g., in the
      // length of a vector)
      val newType = out.typ.substitute(subs)
      out.rebuild(newType)
    }

    /** Shorthand for [[subPreserveType(subs*]] with just one substitution.
      */
    def subPreserveType(sub: (Expr, Expr)): Expr = subPreserveType(Map(sub))

    def contains(p: Expr => Boolean): Boolean = {
      p(this.expr) || this.expr.children.exists(e => e.contains(p))
    }
    def contains(e2: Expr): Boolean = this.contains(e => e == e2)
    def contains[T <: Expr](cls: Class[T]): Boolean =
      this.contains(e => cls.isInstance(e))

    def freeVars(): Set[Param] = {
      this.expr match {
        case x: Param       => Set(x)
        case Function(x, e) => e.freeVars() - x
        case stm @ StmBuild(n, data, valid, eqns) =>
          (
            // Free variables in the stream length and seeds are definitely free,
            // even if they are bound by the stream
            n.freeVars()
              ++ eqns.foldLeft(Set[Param]())({ case (fvs, (_, (z, _))) =>
                fvs ++ z.freeVars()
              })
              // There may be bound variables in the output and "next" functions
              ++ (data.freeVars() ++ valid.freeVars()
                ++ eqns.foldLeft(Set[Param]())({ case (fvs, (_, (_, next))) =>
                  fvs ++ next.freeVars()
                })).diff(stm.accVars)
          )
        case e =>
          e.children.foldLeft(Set[Param]())((fvs, e) => fvs ++ e.freeVars())
      }
    }
  }

  /** Helper methods for [[StmBuild]].
    */
  implicit class StreamOps(stm: StmBuild) {

    /** Construct a new <code>StmBuild</code> that is equivalent to this one but
      * where all the accumulator variables have been replaced by fresh
      * variables.
      */
    def renameVars: StmBuild = {
      this.stm.renameVars(this.stm.accVars.map(x => x -> x.freshCopy).toMap)
    }

    /** Construct a new <code>StmBuild</code> that is equivalent to this one but
      * where the accumulator variable <code>x</code> has been replaced by a
      * fresh variable.
      */
    private def renameVar(x: Param): StmBuild = {
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
        replacements.keys.forall(x => this.stm.accVars.contains(x)),
        "all the variables to be replaced must appear in this stream"
      )
      val subs: Map[Expr, Expr] = replacements.toMap
      val newData = this.stm.data.subPreserveType(subs)
      val newValid = this.stm.valid.subPreserveType(subs)
      val newEquations = this.stm.equations.map({ case (x, (z, next)) =>
        val y = replacements.getOrElse(x, x).rebuild(x.typ).asInstanceOf[Param]
        y -> (z, next.subPreserveType(subs))
      })
      StmBuild(this.stm.n, newData, newValid, newEquations)(this.stm.typ)
    }

    def replaceVars(replacements: Map[Param, Expr]): StmBuild = {
      if (replacements.keys.exists(x => !this.stm.accVars.contains(x))) {
        val xs =
          replacements.keys.filter(x => !this.stm.accVars.contains(x)).toSeq
        throw new IllegalArgumentException(
          s"Cannot replace variables $xs because they are not part of the stream."
            + s" The stream is $this."
        )
      } else {
        val subs: Map[Expr, Expr] = replacements.toMap
        StmBuild(
          this.stm.n,
          this.stm.data.subPreserveType(subs),
          this.stm.valid.subPreserveType(subs),
          this.stm.equations
            .filter({ case (x, _) => !replacements.contains(x) })
            .map({ case (x, (z, next)) =>
              x -> (z, next.subPreserveType(subs))
            })
        )()
      }
    }

    def replaceVar(x: Param, e: Expr): StmBuild = replaceVars(Map(x -> e))

    /** Add a new equation to this stream whose value is the number of valid
      * outputs that this stream has <i>previously</i> produced.
      *
      * @param outCtr
      *   The variable to use for the new equation. If the variable already
      *   appears bound in this stream, then the bound variable will be renamed.
      */
    def addOutputCounter(outCtr: Param): StmBuild = {
      this.stm.requireType("adding an output counter")
      outCtr.typ match {
        case Missing =>
          throw new TypeError(
            s"Variable provided for output counter must have a type."
            // ... because every accumulator must have a type, and how would we
            // know what value to choose here?
          )
        case _: TyUInt => ()
        case t =>
          throw new TypeError(
            s"Variable provided for output counter has type $t."
              + " Expected an unsigned integer."
          )
      }
      val s =
        if (this.stm.equations.contains(outCtr))
          this.renameVar(outCtr)
        else
          this.stm
      val z = IntCst(0)(outCtr.typ)
      val next = Mux(s.valid, outCtr + 1, outCtr)().tchk()
      s.addAccumulator(outCtr, z, next)
    }

    /** Add a new equation to this stream whose value is the number of inputs
      * that this stream has <i>previously</i> read from the input stream
      * represented by <code>x</code>.
      *
      * @param x
      *   The input stream.
      * @param inCtr
      *   The variable to use for the new equation. If the variable already
      *   appears bound in this stream, then the bound variable will be renamed.
      */
    def addInputCounter(x: Param, inCtr: Param): StmBuild = {
      this.stm.requireType("adding an input counter")
      inCtr.typ match {
        case Missing =>
          throw new TypeError(
            s"Variable provided for output counter must have a type."
            // ... because every accumulator must have a type, and how would we
            // know what value to choose here?
          )
        case _: TyUInt => ()
        case t =>
          throw new TypeError(
            s"Variable provided for output counter has type $t."
              + " Expected an unsigned integer."
          )
      }
      val s =
        if (this.stm.equations.contains(inCtr))
          this.renameVar(inCtr)
        else
          this.stm
      val stmNextCalled = s.nextByVar(x)
      val next = Mux(stmNextCalled, inCtr + 1, inCtr)().tchk()
      s.addAccumulator(inCtr, C(0)(inCtr.typ), next)
    }

    /** Add a new accumulator variable to this stream. <i>NOTE:</i> the new
      * variable may capture free variables in this stream.
      */
    def addAccumulator(x: Param, z: Expr, next: Expr): StmBuild = {
      val newEquations = this.stm.equations + (x -> (z, next))
      val isTyped = (x.hasType && z.hasType && next.hasType
        && (z.typ ~= x.typ) && (next.typ ~= x.typ))
      val t = if (isTyped) this.stm.typ else Missing
      StmBuild(this.stm.n, this.stm.data, this.stm.valid, newEquations)(t)
    }

    /** Find the direct dependencies between accumulator variables in this
      * stream.
      */
    def accVarDependencies: DiGraph[Param] = {
      val edges = this.stm.nextByVar.toSeq
        .flatMap({ case (x, next) =>
          next.freeVars().intersect(this.stm.accVars).map(y => (x, y))
        })
        .toSet
      DiGraph(nodes = this.stm.accVars, edges = edges)
    }

    /** Find the accumulator variables that the output of this stream depends
      * on.
      */
    def outputDependencies: Set[Param] = {
      this.stm.data
        .freeVars()
        .union(this.stm.valid.freeVars())
        .intersect(this.stm.accVars)
    }

  }
}
