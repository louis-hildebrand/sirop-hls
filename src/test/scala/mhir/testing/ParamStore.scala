package mhir.testing

import mhir.ir._

/** Utility class that conjures up variables with a given name and type.
  *
  * Suppose you want to write a bunch of tests using dummy variables `x` and
  * `y`, possibly of different types in each test case. You could manually
  * create a [[Param]] in each test case (or maybe create a bunch of variables
  * for the whole test class), but this adds some clutter. Instead, you can
  * create a [[ParamStore]] for each desired variable name and use them to
  * conjure up an instance of [[Param]] with that name for whatever type you
  * want. The [[ParamStore]] will store previously-created variables so that, if
  * you ask again for a variable with the same type, you'll get the same
  * variable as earlier.
  *
  * Example usage:
  *
  * {{{
  *   val x = ParamStore("x)
  *   val y = ParamStore("y")
  *
  *   assert(x(U8) + y(U8) == Sum(x(U8), y(U8))())
  * }}}
  *
  * @param name
  *   the name to use for all variables.
  */
class ParamStore(name: String) {
  private var paramByType: Map[Type, Param] = Map()

  def apply(typ: Type): Param = {
    if (!paramByType.contains(typ)) {
      paramByType += (typ -> Param(name)(typ))
    }
    paramByType(typ)
  }
}

object ParamStore {
  def apply(name: String): ParamStore = new ParamStore(name)
}
