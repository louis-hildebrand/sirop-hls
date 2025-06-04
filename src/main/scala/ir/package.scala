import scala.language.implicitConversions

package object ir extends Eval {
  implicit def int2IntCst(i: Int): IntCst = IntCst(i)

  implicit def bool2BoolExpr(b: Boolean): BoolExpr = if (b) True else False

  /** Reset any internal state in this package. For example, the [[Param]] class
    * has an internal counter for generating fresh variables.
    */
  def resetState(): Unit = {
    // StmBuild and Function both have a Param in the companion object.
    // Force initialization of the companion object so that the param is
    // initialized.
    StmBuild.forceInit()
    Function.forceInit()
    Param.resetCounter()
  }
}
