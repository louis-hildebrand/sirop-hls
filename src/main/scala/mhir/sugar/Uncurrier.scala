package mhir.sugar

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck.{TypeCheck, TypeError}

import scala.annotation.tailrec

/** The uncurrying transformation.
  *
  * Uncurrying transforms a curried function like `x => y => x + y` into an
  * equivalent non-curried function like `(x, y) => x + y`.
  *
  * To perform the transformation, use the extension method
  * [[Uncurrier.Uncurry.uncurry]]. The implicit class [[Uncurrier.Uncurry]] must
  * be in scope.
  *
  * @example
  *
  * {{{
  *   import mhir.ir.Uncurrier.Uncurry
  *   f.uncurry()
  * }}}
  */
object Uncurrier {
  implicit class Uncurry(expr: Expr) {

    /** Get rid of curried functions.
      *
      * For example, <code>(x: Int) => (y: Int) => x + y</code> would be
      * replaced by <code>(z: (Int, Int)) => z.0 + z.1</code>.
      */
    def uncurry(): Expr = {
      @tailrec
      def getFuncAndArgs(fc: FunCall, args: Seq[Expr]): (Expr, Seq[Expr]) = {
        fc match {
          case FunCall(f: FunCall, arg) => getFuncAndArgs(f, arg +: args)
          case FunCall(f, arg)          => (f, arg +: args)
        }
      }

      this.expr match {
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
          val uncurriedArgs = args.map(_.uncurry())
          val argTuple = uncurriedArgs.init.foldRight(uncurriedArgs.last)({
            case (e, acc) => Tuple(e, acc)()
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
  }
}
