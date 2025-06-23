import scala.annotation.nowarn
import scala.language.implicitConversions

package object ir extends Eval with CommonIntTypes {
  //  TODO: This is a bit dangerous, since it is easy to accidentally discard
  //        an IntCst's type this way. But it is already used in so many places
  //        that it seems wildly impractical to review them all.
  implicit def int2IntCst(i: Int): IntCst = IntCst(i)()

  // WARNING: do not provide an implicit Boolean to BoolCst conversion because
  // it is much too easy to accidentally write e1 == e2 (which compares
  // syntactically and then converts to True or False) rather than e1 === e2
  // (which constructs an expression like Equal(e1, e2) ).

  implicit def typeTuple0(@nowarn t: Unit): TyTuple = TyTuple()
  implicit def typeTuple2(t: (Type, Type)): TyTuple = TyTuple(t._1, t._2)
  implicit def typeTuple3(t: (Type, Type, Type)): TyTuple =
    TyTuple(t._1, t._2, t._3)

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
