package mhir.ir

import scala.annotation.nowarn
import scala.language.implicitConversions

/** Implicit conversions from Scala objects to [[mhir.ir.Expr]], to make the
  * Scala code more concise.
  */
trait Conversions {

  //  TODO: This is a bit dangerous, since it is easy to accidentally discard
  //        an IntCst's type this way. But it is already used in so many places
  //        that it seems wildly impractical to review them all.

  /** Implicitly converts an integer to an [[IntCst]].
    */
  implicit def int2IntCst(i: Int): IntCst = IntCst(i)()

  /** Implicitly converts an integer to an [[ExprUtilsImplicit]] so that
    * shorthands like [[ExprUtilsImplicit.lt]] can be used.
    *
    * @example
    *
    * {{{
    *   import mhir.ir.ExprOps
    *   val e1: Expr = ...
    *   val e2: Expr = 1 lt e1
    * }}}
    */
  implicit def int2ExprOps(i: Int): ExprUtilsImplicit = {
    new ExprUtilsImplicit(IntCst(i)())
  }

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
}
