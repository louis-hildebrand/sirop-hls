import scala.language.implicitConversions

package object ir extends Eval with CommonIntTypes {
  implicit def int2IntCst(i: Int): IntCst = IntCst(i)()

  implicit def long2IntCst(i: Long): IntCst = IntCst(i)()

  // This is dangerous! It is too easy to write e1 == e2 rather than e1 === e2
  @deprecated
  implicit def bool2BoolExpr(b: Boolean): BoolExpr = if (b) True else False

  implicit def typeTuple2(t: (Type, Type)): TyTuple = TyTuple(t._1, t._2)

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
