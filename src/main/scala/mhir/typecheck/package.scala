package mhir

/** The typechecker.
  *
  * To type check an expression, use the [[TypeCheck.tchk]] extension method.
  * The implicit class [[TypeCheck]] must be in scope.
  *
  * @example
  *
  * {{{
  *   import mhir.ir.typecheck.TypeCheck
  *   IntCst(42)().tchk()
  * }}}
  */
package object typecheck extends TypeChecker with BitwidthCalculators
