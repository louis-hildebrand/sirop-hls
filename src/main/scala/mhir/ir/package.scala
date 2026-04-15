package mhir

import scala.language.implicitConversions

/** The core IR and a few helper methods.
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
package object ir
    extends mhir.ir.ExprUtils
    with mhir.ir.StmBuildUtils
    with mhir.ir.Conversions
    with mhir.ir.CommonIntTypes
    with mhir.ir.Substitution {

  /** Reset all global mutable state in this package.
    */
  def reset(): Unit = {
    Function.forceInit()
    StmBuild.forceInit()
    Param.reset()
  }
}
