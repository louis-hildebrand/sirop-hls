package mhir.optimize

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

/** Transformations for moving [[mhir.ir.Mux]]es around in the AST.
  */
object MuxMover {

  private val logger: Logger = Logger(getClass.getName)
  private var hasLogged: Boolean = false

  /** Move [[mhir.ir.Mux]]es up towards the root of the AST.
    *
    * This may cause the number of nodes in the AST to grow exponentially (as a
    * function of the number of MUXes). However, making a large table of all
    * possible cases may simplify certain analyses.
    *
    * @param expr
    *   the expression in which to move the [[mhir.ir.Mux]]es.
    * @return
    */
  def moveUp(expr: Expr): Expr = {
    val e = expr.tchk()
    val numMuxes = countMuxes(e)
    if (numMuxes > 10) {
      if (!this.hasLogged) {
        this.hasLogged = true
        logger.warn(
          s"skipping moving up MUXes because there are too many ($numMuxes)"
        )
      }
      return e
    }
    val result = e match {
      case Tuple(elems @ _*)         => moveUpMany(elems, xs => Tuple(xs: _*)())
      case TupleAccess(t, i: IntCst) => moveUp1(t, TupleAccess(_, i)())
      case u: Undefined              => u
      case x: Param                  => x
      case f: Function               => f
      case FunCall(f, arg)           => moveUp2(Seq(f, arg), FunCall(_, _)())
      case c: IntCst                 => c
      case Sum(terms @ _*)           => moveUpMany(terms, xs => Sum(xs: _*)())
      case Prod(factors @ _*) => moveUpMany(factors, xs => Prod(xs: _*)())
      case Div(e1, e2)        => moveUp2(Seq(e1, e2), Div(_, _)())
      case Mod(e1, e2)        => moveUp2(Seq(e1, e2), Mod(_, _)())
      case WrappingSum(terms @ _*) =>
        moveUpMany(terms, xs => WrappingSum(xs: _*)())
      case WrappingDiff(e1, e2) =>
        moveUp2(Seq(e1, e2), WrappingDiff(_, _)())
      case WrappingProd(factors @ _*) =>
        moveUpMany(factors, xs => WrappingProd(xs: _*)())
      case PadTo(e, w)        => moveUp1(e, PadTo(_, w)())
      case TruncateTo(e, w)   => moveUp1(e, TruncateTo(_, w)())
      case ToSigned(e)        => moveUp1(e, ToSigned(_)())
      case ToUnsigned(e)      => moveUp1(e, ToUnsigned(_)())
      case LLShift(e1, e2)    => moveUp2(Seq(e1, e2), LLShift(_, _)())
      case LRShift(e1, e2)    => moveUp2(Seq(e1, e2), LRShift(_, _)())
      case c: FixCst          => c
      case IntFixProd(e1, e2) => moveUp2(Seq(e1, e2), IntFixProd(_, _)())
      case True               => True
      case False              => False
      case Equal(e1, e2)      => moveUp2(Seq(e1, e2), Equal(_, _)())
      case LessThan(e1, e2)   => moveUp2(Seq(e1, e2), LessThan(_, _)())
      case Not(e)             => moveUp1(e, Not(_)())
      case And(terms @ _*)    => moveUpMany(terms, xs => And(xs: _*)())
      case Or(terms @ _*)     => moveUpMany(terms, xs => Or(xs: _*)())
      case Mux(c, t, f) =>
        moveUp(c) match {
          case Mux(cc, ct, cf) =>
            Mux(cc, moveUp(Mux(ct, t, f)()), moveUp(Mux(cf, t, f)()))()
          case c =>
            Mux(c, moveUp(t), moveUp(f))()
        }
      case _: StmBuild     => e
      case _: LetStm       => e
      case StmData(s)      => moveUp1(s, StmData(_)())
      case _: VecBuild     => e
      case VecAccess(v, i) => moveUp2(Seq(v, i), VecAccess(_, _)())
      case VecLiteral(elems @ _*) =>
        moveUpMany(elems, xs => VecLiteral(xs: _*)())
      case StmLiteral(elems @ _*) =>
        moveUpMany(elems, xs => StmLiteral(xs: _*)())
      case StmNextK(s, k) => moveUp2(Seq(s, k), StmNextK(_, _)())
      case e: SyntaxSugar =>
        // TODO: emit warning in case there's syntax sugar?
        e.map(moveUp)
    }
    val typedResult = result.tchk()
    assert(
      typedResult.typ ~= e.typ,
      "moving MUXes up should preserve the type"
        + s" (expected ${e.typ}, found ${typedResult.typ})"
    )
    typedResult
  }

  private def countMuxes(e: Expr): Int = {
    val childCount = e.children.map(countMuxes).sum
    e match {
      case _: Mux => childCount + 1
      case _      => childCount
    }
  }

  /** [[moveUp]], but implemented for unary operations.
    *
    * @param arg
    *   the argument to the unary operation.
    * @param wrap
    *   a function which can reconstruct the unary operation, possibly with a
    *   different argument.
    */
  private def moveUp1(arg: Expr, wrap: Expr => Expr): Expr = {
    moveUpMany(
      Seq(arg),
      xs => {
        assert(xs.length == 1)
        wrap(xs.head)
      }
    )
  }

  /** [[moveUp]], but implemented for operations with at least two arguments.
    *
    * @param args
    *   the arguments to the operation.
    * @param op
    *   a function which can reconstruct the operation, possibly with different
    *   arguments.
    */
  private def moveUp2(args: Seq[Expr], op: (Expr, Expr) => Expr): Expr = {
    moveUpMany(
      args,
      xs => {
        assert(xs.length == 2)
        op(xs.head, xs(1))
      }
    )
  }

  /** [[moveUp]], but implemented for operations that take zero or more
    * arguments.
    *
    * @param args
    *   the arguments to the operation.
    * @param op
    *   a function which can reconstruct the operation, possibly with different
    *   arguments.
    */
  private def moveUpMany(args: Seq[Expr], op: Seq[Expr] => Expr): Expr = {
    def reconstruct(wrappedArgs: Seq[Expr], unwrappedArgs: Seq[Expr]): Expr = {
      wrappedArgs match {
        case Seq() =>
          op(unwrappedArgs)
        case Mux(c, t, f) +: xs =>
          Mux(
            c,
            reconstruct(t +: xs, unwrappedArgs),
            reconstruct(f +: xs, unwrappedArgs)
          )()
        case x +: xs =>
          reconstruct(xs, x +: unwrappedArgs)
      }
    }

    reconstruct(args.map(moveUp).reverse, Seq())
  }
}
