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
    this.reset(this.globalOptions)
  }

  def reset(globalOptions: GlobalOptions): Unit = {
    Function.forceInit()
    StmBuild.forceInit()
    Param.reset()
    this.globalOptions = globalOptions
  }

  /** Compiler options that should be accessible from anywhere.
    *
    * Some options (e.g., whether the handshake protocol is enabled) need to be
    * accessed during lowering. However, the lower() method is called in
    * literally hundreds of places (including places where this information
    * shouldn't normally be needed, like lowering stream lengths). It would be a
    * massive pain to pass this information via method parameters, even implicit
    * ones (I've tried!). Therefore, just set them here and read them during
    * lowering.
    */
  var globalOptions = GlobalOptions()
}
