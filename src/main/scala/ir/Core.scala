package ir

import operations.StmMap

import java.util.concurrent.atomic.AtomicLong
import scala.annotation.tailrec

class BadRebuildError(e: Expr, args: Seq[Expr])
    extends IllegalArgumentException(
      s"Wrong arguments passed to rebuild: node $e, args $args"
    )

/** Scope of a variable within a stream that is parallelized by replication.
  */
private sealed trait AccVarScope

/** Scope of an accumulator variable which depends on the vector index.
  */
private object PrivateScope extends AccVarScope

/** Scope of an accumulator variable which does not depend on the vector index.
  */
private object SharedScope extends AccVarScope

/** A node in the IR.
  *
  * @param typ
  *   The type of this node. If this node has a type other than
  *   <code>Missing</code>, then all its children must also have types other
  *   than <code>Missing</code>.
  */
sealed abstract class Expr(val children: Expr*)(val typ: Type) {

  /** Constructs a function call with the given argument.
    *
    * @param arg
    *   the function argument.
    */
  def apply(arg: Expr): FunCall = FunCall(this, arg)()

  /** See [[SmartSum]].
    */
  def +(that: Expr): Expr = SmartSum(this, that)()

  /** See [[SmartSum]] and [[SmartProd]].
    */
  def -(that: Expr): Expr = this + -1 * that

  /** See [[SmartProd]].
    */
  def *(that: Expr): Expr = SmartProd(this, that)()

  /** See [[SmartDiv]].
    */
  def /(that: Expr): Expr = SmartDiv(this, that)()

  /** See [[SmartMod]].
    */
  def %(that: Expr): Expr = SmartMod(this, that)()

  /** See [[SmartEqual]].
    */
  def ===(that: Expr): Expr = SmartEqual(this, that)()

  /** See [[Equal]].
    */
  def eq(that: Expr): Expr = Equal(this, that)()

  /** See [[SmartEqual]].
    */
  def !==(that: Expr): Expr = !(this === that)

  /** See [[Equal]].
    */
  def neq(that: Expr): Expr = !(this eq that)

  /** See [[SmartLessThan]].
    */
  def <(that: Expr): Expr = SmartLessThan(this, that)()

  /** See [[LessThan]].
    */
  def lt(that: Expr): LessThan = LessThan(this, that)()

  /** See [[SmartLessThan]].
    */
  def <=(that: Expr): Not = !(this > that)

  /** See [[LessThan]]
    */
  def leq(that: Expr): Not = !(this gt that)

  /** See [[SmartLessThan]].
    */
  def >(that: Expr): Expr = that < this

  /** See [[LessThan]].
    */
  def gt(that: Expr): Expr = that lt this

  /** See [[SmartLessThan]].
    */
  def >=(that: Expr): Expr = that <= this

  /** See [[LessThan]].
    */
  def geq(that: Expr): Expr = that leq this

  /** See [[Not]].
    */
  def unary_! : Not = Not(this)()

  /** See [[And]].
    */
  def &&(that: Expr): Expr = And(this, that)()

  /** See [[Or]].
    */
  def ||(that: Expr): Expr = Or(this, that)()

  // if we use _0, _1, ... for some reasons the Scala compiler gets confused and produces error messages when matching some of the expressions
  def __0: TupleAccess = TupleAccess(this, 0)()
  def __1: TupleAccess = TupleAccess(this, 1)()
  def __2: TupleAccess = TupleAccess(this, 2)()
  def __3: TupleAccess = TupleAccess(this, 3)()
  def __4: TupleAccess = TupleAccess(this, 4)()
  def __5: TupleAccess = TupleAccess(this, 5)()

  /** The name of this class. This is useful, for example, for including the
    * class name in an error message without hard-coding it.
    */
  protected def className: String = this.getClass.getSimpleName

  /** Type check this expression and annotate this node with its type.
    *
    * @return
    *   A new expression that is equal to this one but has a type annotation
    *   that is not <code>Missing</code>
    */
  def tchk(implicit context: Map[Param, Type] = Map()): Expr = {
    if (this.typ != Missing) {
      return this
    }
    this match {
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
            if (newArg.typ ~= t1) {
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

      case cst @ IntCst(n) =>
        assert(cst.typ == Missing)
        cst.rebuild(TyAnyInt.tightest(n, n))
      case s @ Sum(terms @ _*) =>
        val newTerms = terms.map(e => e.tchk)
        for ((t, i) <- newTerms.zipWithIndex) {
          if (!t.typ.isInstanceOf[TyAnyInt]) {
            throw new TypeError(
              s"Term $i of sum has type ${t.typ}."
                + " Expected an integer."
            )
          }
        }
        val elemTypes = newTerms.map(e => e.typ.asInstanceOf[TyAnyInt])
        if (elemTypes.toSet.size > 1) {
          throw new TypeError(
            s"Operands of ${s.className} have different types: ${elemTypes.mkString(", ")}."
          )
        } else {
          s.rebuild(elemTypes.head, newTerms)
        }
      case p @ Prod(factors @ _*) =>
        val newFactors = factors.map(e => e.tchk)
        for ((t, i) <- newFactors.zipWithIndex) {
          if (!t.typ.isInstanceOf[TyAnyInt]) {
            throw new TypeError(
              s"Term $i of product has type ${t.typ}."
                + " Expected an integer."
            )
          }
        }
        val elemTypes = newFactors.map(e => e.typ.asInstanceOf[TyAnyInt])
        if (elemTypes.toSet.size > 1) {
          throw new TypeError(
            s"Operands of ${p.className} have different types: ${elemTypes.mkString(", ")}."
          )
        } else {
          p.rebuild(elemTypes.head, newFactors)
        }
      case d @ Div(e1, e2) =>
        val newLhs = e1.tchk
        val t1 = newLhs.typ match {
          case t: TyAnyInt => t
          case t =>
            throw new TypeError(
              s"Left-hand side of $className has type $t."
                + " Expected an integer"
            )
        }
        val newRhs = e2.tchk
        val t2 = newRhs.typ match {
          case t: TyAnyInt => t
          case t =>
            throw new TypeError(
              s"Right-hand side of $className has type $t."
                + " Expected an integer."
            )
        }
        if (t1 ~= t2) {
          d.rebuild(t1, Seq(newLhs, newRhs))
        } else {
          throw new TypeError(
            s"Left-hand side of $className has type $t1, but right-hand side has type $t2."
          )
        }
      case m @ Mod(e1, e2) =>
        val newLhs = e1.tchk
        val t1 = newLhs.typ match {
          case t: TyAnyInt => t
          case t =>
            throw new TypeError(
              s"Left-hand side of $className has type $t."
                + " Expected an integer"
            )
        }
        val newRhs = e2.tchk
        val t2 = newRhs.typ match {
          case t: TyAnyInt => t
          case t =>
            throw new TypeError(
              s"Right-hand side of $className has type $t."
                + " Expected an integer."
            )
        }
        if (t1 ~= t2) {
          m.rebuild(t1, Seq(newLhs, newRhs))
        } else {
          throw new TypeError(
            s"Left-hand side of $className has type $t1, but right-hand side has type $t2."
          )
        }
      case pad @ PadTo(e, targetWidth) =>
        val newE = e.tchk
        newE.typ match {
          case t @ TyAnyInt(srcWidth) if targetWidth >= srcWidth =>
            pad.rebuild(t.withWidth(targetWidth), Seq(newE))
          case t: TyAnyInt =>
            throw new TypeError(
              s"Argument of ${PadTo.getClass.getSimpleName} has type $t but the target width is $targetWidth."
                + " The target width cannot be smaller than the original width."
            )
          case t =>
            throw new TypeError(
              s"Argument of ${PadTo.getClass.getSimpleName} has type $t."
                + " Expected an integer."
            )
        }
      case trunc @ TruncateTo(e, targetWidth) =>
        val newE = e.tchk
        newE.typ match {
          case t @ TyAnyInt(srcWidth) if targetWidth <= srcWidth =>
            trunc.rebuild(t.withWidth(targetWidth), Seq(newE))
          case t: TyAnyInt =>
            throw new TypeError(
              s"Argument of ${TruncateTo.getClass.getSimpleName} has type $t but the target width is $targetWidth."
                + " The target width cannot be greater than the original width."
            )
          case t =>
            throw new TypeError(
              s"Argument of ${TruncateTo.getClass.getSimpleName} has type $t."
                + " Expected an integer."
            )
        }
      case sgn @ ToSigned(e) =>
        val newE = e.tchk.expectUInt()
        val w = newE.typ.asInstanceOf[TyUInt].w
        // Widen by one bit so that the value is guaranteed to fit
        sgn.rebuild(TySInt(w + 1), Seq(newE))
      case uns @ ToUnsigned(e) =>
        val newE = e.tchk
        newE.typ match {
          case TySInt(w) =>
            // We don't need the sign bit anymore
            assert(w >= 1)
            uns.rebuild(TyUInt(w - 1), Seq(newE))
          case t =>
            throw new TypeError(
              s"Argument of ${ToUnsigned.getClass.getSimpleName} has type $t."
                + " Expected a signed integer."
            )
        }

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
        if (newT.typ ~= newF.typ) {
          mux.rebuild(newT.typ, Seq(newC, newT, newF))
        } else {
          throw new TypeError(
            s"True branch of MUX has type ${newT.typ} but false branch has type ${newF.typ}."
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
          case t if Default.hasDefault(t) => ()
          case t =>
            throw new TypeError(
              s"Left-hand side of $className has non-data type $t."
            )
        }
        val newE2 = e2.tchk
        newE2.typ match {
          case t if Default.hasDefault(t) => ()
          case t =>
            throw new TypeError(
              s"Right-hand side of $className has non-data type $t."
            )
        }
        if (newE1.typ ~= newE2.typ) {
          eq.rebuild(TyBool, Seq(newE1, newE2))
        } else {
          throw new TypeError(
            s"Left-hand side of $className has type ${newE1.typ} but right-hand side has type ${newE2.typ}."
          )
        }
      case lt @ LessThan(e1, e2) =>
        val newLhs = e1.tchk
        newLhs.typ match {
          case _: TyAnyInt => ()
          case t =>
            throw new TypeError(
              s"Left-hand side of $className has type $t."
                + " Expected an integer."
            )
        }
        val newRhs = e2.tchk
        newRhs.typ match {
          case _: TyAnyInt => ()
          case t =>
            throw new TypeError(
              s"Right-hand side of $className has type $t."
                + " Expected an integer."
            )
        }
        if (newLhs.typ ~= newRhs.typ) {
          lt.rebuild(TyBool, Seq(newLhs, newRhs))
        } else {
          throw new TypeError(
            s"Left-hand side of $className has type ${newLhs.typ} but right-hand side has type ${newRhs.typ}."
          )
        }

      case t @ Tuple(elems @ _*) =>
        val newElems = elems.map(e => e.tchk)
        t.rebuild(TyTuple(newElems.map(e => e.typ): _*), newElems)
      case ta @ TupleAccess(t, idx: IntCst) =>
        val newIdx = idx.tchk
        assert(newIdx.typ.isInstanceOf[TyAnyInt])
        val newT = t.tchk
        newT.typ match {
          case TyTuple(ts @ _*) =>
            ta.rebuild(ts(idx.i.toInt), Seq(newT, newIdx))
          case t =>
            throw new TypeError(s"Left-hand side of tuple access has type $t.")
        }

      case vb: VecBuild =>
        vb.typecheckVecBuild(context)
      case va @ VecAccess(v, i) =>
        val newV = v.tchk
        val vecT = newV.typ match {
          case TyVec(t, _) => t
          case t =>
            throw new TypeError(s"Left-hand side of VecAccess has type $t.")
        }
        val newI = i.tchk.expectUInt()
        va.rebuild(vecT, Seq(newV, newI))
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
        val newContext = s.accVars.foldLeft(context)({ case (ctx, x) =>
          x.typ match {
            case Missing =>
              throw new TypeError(
                s"Missing type annotation for accumulator $x."
              )
            case _: TyStm =>
              ctx + (x -> x.typ)
            case t if Default.hasDefault(t) =>
              ctx + (x -> x.typ)
            case t =>
              throw new TypeError(
                s"Invalid type $t for accumulator $x."
                  + s" Accumulators can only be streams or data."
              )
          }
        })
        val newN = s.n.tchk(context).expectUInt()
        val newEquations = s.equations.map({ case (x, (z, next)) =>
          val newZ = z.tchk(context)
          if (!(newZ.typ ~= x.typ)) {
            throw new TypeError(
              s"Seed for accumulator $x has type ${newZ.typ}."
                + s" Expected type ${x.typ}."
            )
          }
          val newNext = next.tchk(newContext)
          val expectedNextTyp = x.typ match {
            case _: TyStm => TyBool
            case _        => x.typ
          }
          if (!(newNext.typ ~= expectedNextTyp)) {
            throw new TypeError(
              s"Next value for accumulator $x has type ${newNext.typ}."
                + s" Expected type $expectedNextTyp."
            )
          }
          x -> (newZ, newNext)
        })
        val newData = s.data.tchk(newContext)
        val newValid = s.valid.tchk(newContext).expectType(TyBool)
        StmBuild(newN, newData, newValid, newEquations)(
          TyStm(newData.typ, newN)
        )
      case sn @ StmData(s) =>
        val newS = s.tchk
        newS.typ match {
          case TyStm(t, _) => sn.rebuild(t, Seq(newS))
          case t =>
            throw new TypeError(
              s"Argument of ${StmData.getClass.getSimpleName} has type $t."
            )
        }
      case sn @ StmNextK(s, k) =>
        val newK = k.tchk
        newK.typ match {
          case _: TyAnyInt => ()
          case t =>
            throw new TypeError(
              s"Index of ${StmNextK.getClass.getSimpleName} has type $t."
                + " Expected an integer."
            )
        }
        val newS = s.tchk
        newS.typ match {
          case TyStm(t, n) =>
            sn.rebuild(TyStm(t, n - k), Seq(newS, newK))
          case t =>
            throw new TypeError(
              s"Stream of ${StmNextK.getClass.getSimpleName} has type $t."
            )
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

  /** Insist that this expression has the type of an unsigned integer.
    *
    * @return
    *   this expression, unchanged.
    * @throws TypeError
    *   if this expression does not have the expected type.
    */
  def expectUInt(): Expr = {
    this.typ match {
      case _: TyUInt => this
      case t =>
        throw new TypeError(s"Expected an unsigned integer but found $t.")
    }
  }

  /** Insist that this expression has the type of an integer, either signed or
    * unsigned.
    *
    * @return
    *   this expression, unchanged.
    * @throws TypeError
    *   if this expression does not have the expected type.
    */
  def expectAnyInt(): Expr = {
    this.typ match {
      case _: TyAnyInt => this
      case t =>
        throw new TypeError(s"Expected an integer but found $t.")
    }
  }

  def expectStream(): Expr = {
    this.typ match {
      case _: TyStm => this
      case t =>
        throw new TypeError(s"Expected a stream but found $t.")
    }
  }

  /** Insist that this expression's type is compatible with the given type (as
    * determined by [[Type.~=]]).
    *
    * @param t
    *   the expected type.
    * @return
    *   this expression, unchanged.
    * @throws TypeError
    *   if this expression does not have the expected type.
    */
  def expectType(t: Type): Expr = {
    if (this.typ != t) {
      throw new TypeError(s"Expected type $t but found ${this.typ}.")
    }
    this
  }

  /** Insist that this expression has been type checked.
    *
    * @param action
    *   the action that must be preceded by type checking.
    */
  protected def requireType(action: String = "lowering"): Unit = {
    require(
      this.typ != Missing,
      s"$className must be type checked before $action."
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

  /** Reconstruct this expression with new children or a new type annotation.
    *
    * @param typ
    *   the new type annotation.
    * @param newChildren
    *   the new children.
    */
  def rebuild(typ: Type, newChildren: Seq[Expr]): Expr

  /** Reconstruct this expression with new children and erase the type
    * annotation.
    *
    * @param newChildren
    *   the new children.
    */
  def rebuild(newChildren: Seq[Expr]): Expr = {
    this match {
      case _: IntCst | _: Param | _: StmLiteral | _: VecLiteral =>
        // These expressions may carry type information that cannot be derived
        // from the syntax alone, so be careful not to discard it.
        this.rebuild(this.typ, newChildren)
      case _ =>
        this.rebuild(Missing, newChildren)
    }
  }

  /** Reconstruct this expression with a new type annotation but the same
    * children.
    *
    * @param typ
    *   the new type annotation.
    */
  def rebuild(typ: Type): Expr = rebuild(typ, children)

  /** Rebuild this expression after applying [[f]] to each of its children.
    *
    * @param f
    *   the function to apply to each child.
    */
  def map(f: Expr => Expr): Expr = rebuild(children.map(f))
  def mapPreOrder(f: Expr => Expr): Expr = map(f).map(e => e.mapPreOrder(f))

  /** Remove all syntax sugar from this expression and its children. This is
    * guaranteed to preserve type annotations.
    */
  def lower(): Expr = {
    val desugared = this match {
      case s: SyntaxSugar => s.lowerSyntaxSugar()
      case vb: VecBuild   => vb.lowerVecBuild()
      case va: VecAccess  => va.lowerVecAccess()
      case e @ (_: IntCst | _: Param | _: StmLiteral | _: VecLiteral) =>
        // These expressions may carry type information that cannot be derived
        // from the syntax alone, so be careful not to discard it.
        e.rebuild(e.typ.lower, e.children.map(e => e.lower()))
      case e =>
        // Re-run the type checker to ensure no type errors crept in during
        // lowering
        val newE = e.rebuild(e.children.map(e => e.lower()))
        if (e.typ != Missing) newE.tchk() else newE
    }
    // This is required because lowering may be syntax-directed (i.e., an
    // expression may need to be typed before it can be lowered) and it is no
    // good if you type check an expression but then the type is removed while
    // lowering its children.
    assert(
      !this.hasType || (desugared.typ ~= this.typ.lower),
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
                val z0 = z.__0.tchk()
                val z1 = z.__1.tchk()
                val subs = Map[Expr, Expr](newX -> z0, y -> z1)
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
  @deprecated
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
                renamed.data.substitute(subs),
                renamed.valid.substitute(subs),
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
  @deprecated
  def substitute(sub: (Expr, Expr)): Expr = substitute(Map(sub))

  /** Perform a substitution while preserving all type annotations.
    */
  def subPreserveType(subs: Map[Expr, Expr]): Expr = {
    val out = if (subs.isEmpty) {
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
                renamed.param.subPreserveType(subs).asInstanceOf[Param],
                renamed.body.subPreserveType(subs)
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
            case e: SyntaxSugar => e.subSyntaxSugar(subs)
            case e =>
              e.rebuild(e.typ, e.children.map(e => e.subPreserveType(subs)))
          }
      }
    }
    if (this.hasType) {
      assert(
        out.typ ~= this.typ,
        s"the type should be preserved after substitution (expected ${this.typ}, found ${out.typ} after substitutions $subs in $this)"
      )
    }
    // The expressions to replace may occur within the type (e.g., in the
    // length of a vector)
    val newType = out.typ.substitute(subs)
    out.rebuild(newType)
  }

  /** Perform a substitution while preserving all type annotations.
    */
  def subPreserveType(sub: (Expr, Expr)): Expr = subPreserveType(Map(sub))

  def freeVars(): Set[Param] = {
    this match {
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
            ++ (
              (data.freeVars() ++ valid.freeVars()
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
      case _: PadTo       => 13
      case _: TruncateTo  => 14
      case _: ToSigned    => 15
      case _: ToUnsigned  => 16
      case _: Param       => 17
      case _: TupleAccess => 18
      case _: FunCall     => 19
      case _: Mux         => 20
      case _: Tuple       => 21
      case _: Function    => 22
      case _: StmBuild    => 23
      case _: StmData     => 24
      case _: VecBuild    => 25
      case _: VecAccess   => 26
      case _: VecLiteral  => 27
      case _: StmLiteral  => 28
      case _: StmNextK    => 29
      case _: SyntaxSugar => 30
    }
  }
}

// Tuples
case class Tuple(elems: Expr*)(typ: Type = Missing)
    extends Expr(elems: _*)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr =
    Tuple(newChildren: _*)(typ)
}

case class TupleAccess(t: Expr, i: IntCst)(typ: Type = Missing)
    extends Expr(t, i)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(t, i: IntCst) => TupleAccess(t, i)(typ)
      case _                 => throw new BadRebuildError(this, newChildren)
    }
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
  private val idCtr = new AtomicLong(0)

  def apply(prefix: String)(typ: Type = Missing): Param = {
    new Param(prefix, idCtr.incrementAndGet())(typ)
  }

  private[ir] def resetCounter(): Unit = {
    idCtr.set(0)
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

  /** Force initialization of this object.
    */
  private[ir] def forceInit(): Unit = {
    // Just calling this method should force initialization of `HashCodeParam`
  }
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

/** An integer constant.
  *
  * @param i
  *   the integer value.
  */
case class IntCst(i: Long)(typ: Type = Missing) extends IntExpr()(typ) {
  typ match {
    case Missing => ()
    case int: TyAnyInt =>
      if (!int.contains(i)) {
        throw OverflowException(i, int)
      }
    case t =>
      throw new TypeError(s"Invalid type $t for integer constant.")
  }

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(typ.isInstanceOf[TyAnyInt] || typ == Missing)
    require(newChildren.isEmpty)
    IntCst(i)(typ)
  }
}

/** Shorthand for [[IntCst]].
  */
object C {

  /** Constructs an [[IntCst]].
    *
    * @param i
    *   the integer value.
    * @param typ
    *   the type annotation.
    */
  def apply(i: Long)(typ: Type = Missing): IntCst = {
    IntCst(i)(typ)
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
      case Seq()  => IntCst(0)(typ)
      case Seq(e) => e
      case terms  => new Sum(terms: _*)(typ)
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
      case Seq()   => IntCst(1)(typ)
      case Seq(e)  => e
      case factors => new Prod(factors: _*)(typ)
    }
  }
}

case class Div(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends IntExpr(e1, e2)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(newChildren.length == 2)
    Div(newChildren.head, newChildren(1))(typ)
  }
}

case class Mod(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends IntExpr(e1, e2)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(newChildren.length == 2)
    Mod(newChildren.head, newChildren(1))(typ)
  }
}

/** Sign extend an integer to be [[w]] bits wide.
  *
  * @param e
  *   the integer to pad.
  * @param w
  *   the new width.
  */
case class PadTo(e: Expr, w: Int)(typ: Type = Missing) extends IntExpr(e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => PadTo(e, w)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }
}

/** Truncate an integer to be [[w]] bits wide by dropping the most significant
  * bits.
  *
  * It is undefined behaviour if the value of the input does not fit within the
  * target type.
  *
  * @param e
  *   the integer to truncate.
  * @param w
  *   the new width.
  */
case class TruncateTo(e: Expr, w: Int)(typ: Type = Missing)
    extends IntExpr(e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => TruncateTo(e, w)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }
}

/** Convert an unsigned integer to a signed integer.
  *
  * @param e
  *   the integer to convert.
  */
case class ToSigned(e: Expr)(typ: Type = Missing) extends IntExpr(e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => ToSigned(e)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }
}

/** Convert a signed integer to an unsigned integer.
  *
  * It is undefined behaviour if the value of the input is negative.
  *
  * @param e
  *   the integer to convert.
  */
case class ToUnsigned(e: Expr)(typ: Type = Missing) extends IntExpr(e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => ToUnsigned(e)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }
}

// Boolean expressions
// Unfortunately, cannot say that this node always has type Bool.
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
    data: Expr /* B */,
    valid: Expr /* Bool */,
    equations: Map[Param, (Expr, Expr)] = Map() /* (A, A) */
)(typ: Type = Missing)
    extends Expr(
      Seq(n, data, valid) ++ equations.flatMap({ case (x, (z, next)) =>
        Seq(x, z, next)
      }): _*
    )(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(n, data, valid, eqns @ _*) if eqns.length % 3 == 0 =>
        val equations = (0 until eqns.length / 3)
          .map(i => {
            val x = eqns(3 * i).asInstanceOf[Param]
            val z = eqns(3 * i + 1)
            val next = eqns(3 * i + 2)
            x -> (z, next)
          })
          .toMap
        StmBuild(n, data, valid, equations)(typ)
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
    val newData = data.subPreserveType(subs)
    val newValid = valid.subPreserveType(subs)
    val newEquations = equations.map({ case (x, (z, next)) =>
      val y = replacements.getOrElse(x, x).rebuild(x.typ)
      y -> (z, next.subPreserveType(subs))
    })
    StmBuild(n, newData, newValid, newEquations)(typ)
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
        this.data.subPreserveType(subs),
        this.valid.subPreserveType(subs),
        this.equations
          .filter({ case (x, _) => !replacements.contains(x) })
          .map({ case (x, (z, next)) => x -> (z, next.subPreserveType(subs)) })
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
        val readyCond = consumerStm.nextByVar(x)
        assert(readyCond.typ == TyBool, "stream call condition must be a bool")
        val (newData, newValid) = fusedOutput(
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
        StmBuild(consumerStm.n, newData, newValid, newEquations)(typ)
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
  ): (Expr, Expr) = {
    val consumerTyp = consumer.typ.asInstanceOf[TyStm]
    val producerTyp = producer.typ.asInstanceOf[TyStm]
    val valid = Mux(
      ready,
      // CASE 1: Consumer is ready (i.e., reading from producer).
      Mux(
        producer.valid,
        // CASE 1a: Producer yielded a valid value. Proceed as usual.
        consumer.valid.subPreserveType(StmData(x)() -> producer.data),
        // CASE 1b: Producer did NOT yield a valid value.
        //          The consumer cannot proceed.
        False
      )(),
      // CASE 2: Consumer is not ready (i.e., not reading from producer).
      //         Proceed as usual, but with the default value for the producer
      //         output (this should not be read).
      consumer.valid.subPreserveType(StmData(x)() -> Default(producerTyp.t))
    )().tchk()
    val data = Mux(
      ready,
      // CASE 1: Consumer is ready (i.e., reading from producer).
      //         It doesn't matter whether the producer yielded a valid value:
      //         if it did then fine, if it did not then `valid` will be False
      //         and therefore the `data` doesn't matter.
      consumer.data.subPreserveType(StmData(x)() -> producer.data),
      // CASE 2: Consumer is not ready (i.e., not reading from producer).
      //         Proceed as usual, but with the default value for the producer
      //         output (this should not be read).
      consumer.data.subPreserveType(StmData(x)() -> Default(producerTyp.t))
    )().tchk()
    (data, valid)
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
            Mux(
              producer.valid,
              // CASE 1a: Producer yielded a valid value.
              //          Update the accumulators.
              next.subPreserveType(StmData(x)() -> producer.data),
              // CASE 1b: Producer did NOT yield a valid value.
              //          Do not update accumulators until it does.
              y.typ match {
                case _: TyStm => False
                case _        => y
              }
            )(),
            // CASE 2: Consumer is not reading from producer.
            //         Update as usual, but with the default value for the
            //         producer output (this should not be read).
            next.subPreserveType(
              StmData(x)() -> Default(producerTyp.t)
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
  def addOutputCounter(outCtr: Param): StmBuild = {
    requireType("adding an output counter")
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
      if (this.equations.contains(outCtr))
        this.renameVar(outCtr)
      else
        this
    val z = IntCst(0)(outCtr.typ)
    val next = Mux(s.valid, outCtr + 1, outCtr)().tchk()
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
    requireType("adding an input counter")
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
      if (this.equations.contains(inCtr))
        this.renameVar(inCtr)
      else
        this
    val stmNextCalled = s.nextByVar(x)
    val next = Mux(stmNextCalled, inCtr + 1, inCtr)().tchk()
    s.addAccumulator(inCtr, C(0)(inCtr.typ), next)
  }

  /** Add a new accumulator variable to this stream. <i>NOTE:</i> the new
    * variable may capture free variables in this stream.
    */
  def addAccumulator(x: Param, z: Expr, next: Expr): StmBuild = {
    val newEquations = this.equations + (x -> (z, next))
    val isTyped = (x.hasType && z.hasType && next.hasType
      && (z.typ ~= x.typ) && (next.typ ~= x.typ))
    val t = if (isTyped) this.typ else Missing
    StmBuild(this.n, this.data, this.valid, newEquations)(t)
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
    this.data.freeVars().union(this.valid.freeVars()).intersect(this.accVars)
  }

  /** Parallelize this stream by duplicating its body <code>m</code> times.
    *
    * @param m
    *   The number of instances of the body to create.
    * @param i
    *   The index within the vector.
    * @param varsToReplicate
    *   Input stream variables that must also be replicated.
    * @return
    *   A stream of vectors of length <code>m</code>.
    */
  def replicate(
      m: Expr,
      i: Param,
      varsToReplicate: Set[Param]
  ): StmBuild = {
    if (this.n.freeVars().contains(i)) {
      throw new IllegalArgumentException(
        "Stream length must not vary with vector index."
          + s" (Found vector index $i and stream length $n.)"
      )
    }
    // Some variables depend on the vector index i and must therefore be turned
    // into vectors.
    // Some variables do not depend on the vector index i and can therefore be
    // shared.
    val scopes = this.findScopeByVar(i, varsToReplicate)
    // If the type of the variables may change, then it seems like a good idea
    // to just introduce a whole new variable.
    // This way I can check that no uses of the original variable were
    // accidentally left behind and there aren't multiple variables with the
    // same name but different types floating around.
    val oldToNewVar = scopes
      .map({
        case (x, PrivateScope) =>
          x -> x.freshCopy.rebuild(TyVec(x.typ, m).lower)
        case (x, SharedScope) => x -> x
      })
    val subs: Map[Expr, Expr] = scopes.flatMap({
      case (x, PrivateScope) =>
        val newX = oldToNewVar(x)
        x.typ match {
          case TyStm(t, _) =>
            Some(StmData(x)() -> VecAccess(StmData(newX)(TyVec(t, m)), i)(t))
          case t if Default.hasDefault(t) =>
            Some(x -> VecAccess(newX, i)(x.typ))
          case t =>
            throw new IllegalArgumentException(s"Invalid accumulator type $t.")
        }
      case (_, SharedScope) => None
    })
    val newEquations = this.equations.map({ case (x, (z, next)) =>
      val newX = oldToNewVar(x)
      x.typ match {
        case TyStm(t, k) =>
          if (next.freeVars().contains(i)) {
            throw new IllegalArgumentException(
              "Input stream `ready` cannot depend on the vector index."
                + s" (Found vector index $i and `ready` expression $next.)"
            )
          }
          val newZ = scopes(x) match {
            case PrivateScope =>
              z match {
                case z: StmBuild => z.replicate(m, i, varsToReplicate)
                case z: Param    => z.rebuild(TyStm(TyVec(t, m), k))
                case z =>
                  throw new IllegalArgumentException(
                    s"Invalid initial value for input stream: $z."
                  )
              }
            case SharedScope => z
          }
          newX -> (newZ, next)
        case t if Default.hasDefault(t) =>
          scopes(x) match {
            case SharedScope => newX -> (z, next.subPreserveType(subs))
            case PrivateScope =>
              newX -> (
                VecBuild(m, Function(i, z)())(),
                VecBuild(m, Function(i, next.subPreserveType(subs))())()
              )
          }
        case t =>
          throw new IllegalArgumentException(s"Invalid accumulator type $t.")
      }
    })
    val newData = {
      val validDependsOnI = valid
        .freeVars()
        .exists(x => x == i || scopes.get(x).contains(PrivateScope))
      if (validDependsOnI) {
        throw new IllegalArgumentException(
          "Stream `valid` must not depend on the vector index."
            + s" (Found vector index $i and stream `valid` expression $valid.)"
        )
      }
      VecBuild(m, Function(i, data.subPreserveType(subs))())()
    }
    val s = StmBuild(this.n, newData, valid, newEquations)()
    assert(
      !s.freeVars().contains(i),
      "there should be no more free occurrences of the vector index i"
    )
    val expectedFreeVars = (this.freeVars() ++ m.freeVars()) - i
    assert(
      s.freeVars().subsetOf(expectedFreeVars),
      "replication should not introduce any new free variables"
        + s" (expected $expectedFreeVars, found ${s.freeVars()})"
    )
    s.tchk().asInstanceOf[StmBuild]
  }

  private def findScopeByVar(
      i: Param,
      varsToReplicate: Set[Param]
  ): Map[Param, AccVarScope] = {
    // If an accumulator variable depends on private variables, then it must also be private
    @tailrec
    def propagateScopes(
        scopeByVar: Map[Param, AccVarScope],
        dependencies: Map[Param, Set[Param]],
        i: Param
    ): Map[Param, AccVarScope] = {

      require(scopeByVar.keySet == dependencies.keySet)
      val newScopeByVar = dependencies.map({ case (x, deps) =>
        scopeByVar(x) match {
          case PrivateScope => x -> PrivateScope
          case SharedScope =>
            val anyDepsPrivate = deps.exists(y => scopeByVar(y) == PrivateScope)
            x -> (if (anyDepsPrivate) PrivateScope else SharedScope)
        }
      })
      if (newScopeByVar == scopeByVar) {
        scopeByVar
      } else {
        propagateScopes(newScopeByVar, dependencies, i)
      }
    }

    val dependencies = this.equations.map({ case (x, (z, next)) =>
      x -> next.freeVars().intersect(this.accVars)
    })
    propagateScopes(
      scopeByVar = this.equations
        .map({ case (x, (z, next)) =>
          val dependsOnI = next.freeVars().contains(i) || z
            .freeVars()
            .intersect(varsToReplicate + i)
            .nonEmpty
          x -> (if (dependsOnI) PrivateScope else SharedScope)
        }),
      dependencies = dependencies,
      i = i
    )
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
    val data = this.data.substitute(subs)
    val valid = this.valid.substitute(subs)
    val eqns = this.equations.toSeq
      .map({ case (_, (z, next)) =>
        (z, next.substitute(subs))
      })
    val eqnsBag =
      eqns.toSet.map((x: (Expr, Expr)) => x -> eqns.count(y => x == y)).toMap
    // Be careful not to remove equations due to the fact that they'll all use
    // the same param now!
    assert(eqnsBag.values.sum == this.equations.size)
    (len, data, valid, eqnsBag).hashCode
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
      val thisOutput =
        (this.data.substitute(thisSubs), this.valid.substitute(thisSubs))
      val thatOutput =
        (that.data.substitute(thatSubs), that.valid.substitute(thatSubs))
      eqnsMatch && thisOutput == thatOutput
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

  /** Force initialization of this object.
    */
  private[ir] def forceInit(): Unit = {
    // Just calling this method should force initialization of `HashCodeParam`
  }
}

case class StmData(s: Expr)(typ: Type = Missing) extends Expr(s)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s) => StmData(s)(typ)
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

  def typecheckVecBuild(implicit context: Map[Param, Type]): Expr = {
    val newN = len.tchk.expectUInt()
    val newF = f.tchk
    val vecT = newF.typ match {
      case TyArrow(_: TyUInt, t) => t
      case t => throw new TypeError(s"Function of VecBuild has type $t.")
    }
    this.rebuild(TyVec(vecT, newN), Seq(newN, newF))
  }

  def lowerVecBuild(): Expr = {
    requireType()
    val n = this.len.lower()
    val f = this.f.lower()
    this.typ.asInstanceOf[TyVec].t.lower match {
      case _: TyStm =>
        val (i, s) = f match {
          case Function(i, s: StmBuild) => (i, s)
          case Function(_, body) =>
            throw new IllegalArgumentException(
              s"Cannot lower VecBuild whose function has body $body. Expected StmBuild."
            )
        }
        s.replicate(n, i, Set())
      case t if Default.hasDefault(t) => VecBuild(n, f)().tchk()
      case t =>
        throw new IllegalArgumentException(
          s"Cannot lower VecBuild containing elements of type $t."
        )
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

  def lowerVecAccess(): Expr = {
    requireType()
    val v = this.vec.lower()
    val i = this.i.lower()
    v.typ match {
      case _: TyVec => VecAccess(v, i)().tchk()
      case TyStm(tv: TyVec, _) =>
        StmMap(v, tv ::+ (v => VecAccess(v, i)()))().tchk().lower()
      case t =>
        throw new TypeError(
          s"Cannot lower ${VecAccess.getClass.getSimpleName} whose first argument has type $t."
        )
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
    VecLiteral(elems.map(n => IntCst(n)()): _*)()
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
    StmLiteral(elems.map(n => IntCst(n)()): _*)()
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

  def typecheck(implicit context: Map[Param, Type]): Expr

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

  def subSyntaxSugar(subs: Map[Expr, Expr]): Expr = {
    this.rebuild(this.typ, this.children.map(e => e.subPreserveType(subs)))
  }
}

@deprecated
case class VecLength(v: Expr)(typ: Type = Missing) extends SyntaxSugar(v)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v) => VecLength(v)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newV = v.tchk
    newV.typ match {
      case _: TyVec => this.rebuild(U32, Seq(newV))
      case t =>
        throw new TypeError(
          s"Vector in VecLength has type $t. Expected a vector."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    ReshapeData(v.typ.asInstanceOf[TyVec].n, U32)().tchk().lower()
  }
}
