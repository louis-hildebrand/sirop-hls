package mhir.ir

import java.util.concurrent.atomic.AtomicLong

/** A node in the IR.
  *
  * ==Important Note About [[equals]] and [[hashCode]]==
  * The [[equals]] and [[hashCode]] methods of an [[Expr]] must NOT depend on
  * arithmetic simplification via the [[lift.arithmetic]] library, even
  * indirectly. In particular, this means they must NOT use methods like
  * [[Type.~=]] which use the arithmetic expression library to compare vector
  * lengths. This restriction is due to the fact that the library may invoke
  * [[equals]] or [[hashCode]] while arithmetic simplification is temporarily
  * disabled.
  *
  * @param typ
  *   The type of this node. If this node has a type other than
  *   <code>Missing</code>, then all its children must also have types other
  *   than <code>Missing</code>.
  */
sealed abstract class Expr(val children: Expr*)(val typ: Type) {

  /** The name of this class. This is useful, for example, for including the
    * class name in an error message without hard-coding it.
    */
  def className: String = this.getClass.getSimpleName

  /** Insist that this expression has been type checked.
    *
    * @param action
    *   the action that must be preceded by type checking.
    */
  def requireType(action: String = "lowering"): Unit = {
    require(
      this.typ != Missing,
      s"$className must be type checked before $action."
    )
  }

  /** Whether this expression has a type that is not [[Missing]].
    */
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

  override def toString: String = ExprPrinter.displayOneLine(this)
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

  override def equals(x: Any): Boolean = {
    x match {
      case that: Function =>
        val fresh = Param("p")()
        val thisRenamed =
          this.body.subAndEraseType(this.param -> fresh.rebuild(this.param.typ))
        val thatRenamed =
          that.body.subAndEraseType(that.param -> fresh.rebuild(that.param.typ))
        thisRenamed == thatRenamed
      case _ => false
    }
  }
  override def hashCode(): Int = {
    // This implementation should be correct, but it may cause excessive
    // collisions when dealing with nested functions. For example,
    // x => y => x - y and x => y => y - x will be assigned the same hash code.
    this.body
      .subAndEraseType(
        this.param -> Function.HashCodeParam.rebuild(this.param.typ)
      )
      .hashCode
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
    val data = this.data.subAndEraseType(subs)
    val valid = this.valid.subAndEraseType(subs)
    val eqns = this.equations.toSeq
      .map({ case (_, (z, next)) =>
        (z, next.subAndEraseType(subs))
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
        freshVarByPair.map({ case ((x, _), fresh) =>
          x -> fresh.rebuild(x.typ)
        })
      val thatSubs: Map[Expr, Expr] =
        freshVarByPair.map({ case ((_, y), fresh) =>
          y -> fresh.rebuild(y.typ)
        })
      val eqnsMatch = map.forall({ case (x, y) =>
        (this.nextByVar(x).subAndEraseType(thisSubs)
          == that.nextByVar(y).subAndEraseType(thatSubs))
      })
      val thisOutput =
        (
          this.data.subAndEraseType(thisSubs),
          this.valid.subAndEraseType(thisSubs)
        )
      val thatOutput =
        (
          that.data.subAndEraseType(thatSubs),
          that.valid.subAndEraseType(thatSubs)
        )
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

  /** The precedence of this expression. See [[Precedence]].
    */
  def precedence: Int = Precedence.FunCall

  /** See [[ExprPrinter.displayOneLine]].
    *
    * @note
    *   there is no need to wrap the final result in parentheses; that will be
    *   handled outside this method.
    */
  def displayOneLine(): String = {
    ExprPrinter.displayFunCallOneLine(this.className, this.children)
  }

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

  def sugarSubAndKeepType(subs: Map[Expr, Expr]): Expr = {
    this.rebuild(this.typ, this.children.map(e => e.subPreserveType(subs)))
  }

  def sugarSubAndEraseType(subs: Map[Expr, Expr]): Expr = {
    this.rebuildAndEraseType(this.children.map(e => e.subPreserveType(subs)))
  }
}
