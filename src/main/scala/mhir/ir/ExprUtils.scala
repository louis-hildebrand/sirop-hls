package mhir.ir

trait ExprUtils {

  /** Helper methods and shorthands for common operations related to all
    * [[Expr]]s.
    */
  implicit class ExprUtilsImplicit(expr: Expr) {

    /** Constructs a function call with the given argument.
      *
      * @param arg
      *   the function argument.
      */
    def apply(arg: Expr): FunCall = FunCall(this.expr, arg)()

    /** See [[WrappingSum]].
      */
    def +%(that: Expr): Expr = WrappingSum(this.expr, that)()

    /** See [[WrappingDiff]].
      */
    def -%(that: Expr): Expr = WrappingDiff(this.expr, that)()

    /** See [[WrappingProd]].
      */
    def *%(that: Expr): Expr = WrappingProd(this.expr, that)()

    /** See [[LLShift]].
      */
    def <<<(that: Expr): Expr = LLShift(this.expr, that)()

    /** See [[LRShift]].
      */
    def >>>(that: Expr): Expr = LRShift(this.expr, that)()

    /** See [[Equal]].
      */
    def equ(that: Expr): Expr = Equal(this.expr, that)()

    /** See [[Equal]].
      */
    def nequ(that: Expr): Expr = !(this.expr equ that)

    /** See [[LessThan]].
      */
    def lt(that: Expr): LessThan = LessThan(this.expr, that)()

    /** See [[LessThan]]
      */
    def leq(that: Expr): Not = !(this.expr gt that)

    /** See [[LessThan]].
      */
    def gt(that: Expr): Expr = that lt this.expr

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
        case _: IntCst | _: Param | StmLiteral() | VecLiteral() |
            _: Undefined =>
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

    def contains(p: Expr => Boolean): Boolean = {
      p(this.expr) || this.expr.children.exists(e => e.contains(p))
    }
    def contains(e2: Expr): Boolean = this.contains(e => e == e2)
    def contains[T <: Expr](cls: Class[T]): Boolean =
      this.contains(e => cls.isInstance(e))

    /** If this expression is a function literal without input type annotations,
      * then recursively annotate it with the given types.
      *
      * @note
      *   the resulting expression will not necessarily be type checked.
      *
      * @param typ
      *   the input types.
      */
    def annotateFunc(typ: Type*): Expr = {
      require(typ.nonEmpty)
      this.expr match {
        case Function(x, body) =>
          val newX =
            if (x.hasType) x else x.rebuild(typ.head).asInstanceOf[Param]
          val newBody = if (typ.tail.isEmpty) {
            body
          } else {
            new ExprUtilsImplicit(body).annotateFunc(typ.tail: _*)
          }
          Function(newX, newBody)()
        case f => f
      }
    }

    /** Count the number of times that `x` occurs free within this expression.
      *
      * @param x
      *   the variable to search for.
      */
    def countFreeOccurrences(x: Param): Int = {
      def count(e: Expr, x: Param): Int = {
        if (e == x) {
          1
        } else {
          e match {
            case Function(y, _) if y == x =>
              0
            case LetStm(bufSize, y, in, _) if y == x =>
              count(bufSize, x) + count(in, x)
            case stm @ StmBuild(n, data, valid, _) =>
              val n0 =
                (count(n, x)
                  + stm.seedByVar.map({ case (_, z) => count(z, x) }).sum)
              if (stm.accVars.contains(x)) {
                n0
              } else {
                (n0
                  + count(data, x)
                  + count(valid, x)
                  + stm.nextByVar.map({ case (_, next) => count(next, x) }).sum)
              }
            case e =>
              e.children.map(e => count(e, x)).sum
          }
        }
      }
      count(this.expr, x)
    }

    /** Convert this expression to a boolean.
      *
      * @return
      *   true if this expression is [[True]] and false if this expression is
      *   [[False]].
      * @throws IllegalArgumentException
      *   if this expression is not a boolean constant.
      */
    def toBool: Boolean = {
      this.expr match {
        case True  => true
        case False => false
        case e =>
          throw new IllegalArgumentException(
            s"Expected a boolean constant but found $e."
          )
      }
    }

    /** Whether this stream operator will have consumed all its inputs by the
      * time it produces its last output.
      *
      * This is useful because it allows the stream resetting transformation to
      * omit input counters.
      *
      * @example
      *   a stream operator which finds the sum of its input stream satisfies
      *   this condition.
      * @example
      *   a stream operator which returns only the first element of its input
      *   stream does <i>not</i> satisfy this condition.
      *
      * @param inputs
      *   the inputs that must be fully consumed.
      * @return
      *   `true` if this expression <i>definitely</i> satisfies the condition,
      *   otherwise `false`.
      */
    def fullyConsumesInputs(inputs: Set[Param]): Boolean = {
      this.expr match {
        case x: Param if inputs.contains(x) => true
        case e if e.typ.isData              =>
          // Combinational expressions definitely satisfy the condition.
          // When converted to a streaming expression (e.g., via the
          // streamifier), the one input element will be consumed in the same
          // cycle as the one output element is produced.
          true
        case e if e.freeVars.intersect(inputs).isEmpty => true
        case s: SyntaxSugar => s.fullyConsumesInputs(inputs)
        case LetStm(_, x, in, out) =>
          in.fullyConsumesInputs(inputs) && out.fullyConsumesInputs(inputs + x)
        case _ => false
      }
    }
  }
}
