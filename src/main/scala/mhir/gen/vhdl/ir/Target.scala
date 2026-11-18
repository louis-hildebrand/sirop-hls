package mhir.gen.vhdl
package ir

import mhir.ir._

/** The target of a signal or variable assignment.
  *
  * In C terminology, this basically represents an lvalue.
  *
  * @note
  *   The [[typ]] does not have to be convertible to a [[VhdlType]]; it could be
  *   a function type, for example.
  */
sealed trait Target {

  /** The full name of the target, possibly including indexing (e.g.,
    * `my_vec(0)`) and selection (`my_tuple.0`).
    */
  def name: String

  /** The type of the target.
    */
  def typ: Type

  /** The symbol used for assignment (`<=` or `:=`).
    */
  def assign: String

  /** Construct a new [[Target]] by accessing the given index in this target.
    *
    * @note
    *   the type of this target must be [[TyVec]].
    *
    * @param unsignedIndex
    *   the index to take.
    */
  def index(unsignedIndex: String): Target
}

/** A VHDL signal.
  */
sealed class SignalTarget(val name: String, val typ: Type) extends Target {

  override def assign: String = "<="

  def index(unsignedIndex: String): SignalTarget = {
    val TyVec(elemTyp, _) = this.typ
    SignalTarget(s"${this.name}(to_integer($unsignedIndex))", elemTyp)
  }
}

object SignalTarget {
  def apply(name: String, typ: Type) = new SignalTarget(name, typ)
  def apply(x: Param) = SimpleSignalTarget(x.name, x.typ)
}

/** A VHDL variable.
  */
sealed class VariableTarget(val name: String, val typ: Type) extends Target {

  override def assign: String = ":="

  def index(unsignedIndex: String): VariableTarget = {
    val TyVec(elemTyp, _) = this.typ
    VariableTarget(s"${this.name}(to_integer($unsignedIndex))", elemTyp)
  }
}

object VariableTarget {
  def apply(name: String, typ: Type) = new VariableTarget(name, typ)
  def apply(x: Param) = SimpleVariableTarget(x.name, x.typ)
}

/** A target whose name is just an identifier: not an "indexed name," "selected
  * name," etc.
  */
sealed trait SimpleTarget extends Target {

  def toDecl: String
}

case class SimpleSignalTarget(override val name: String, override val typ: Type)
    extends SignalTarget(name, typ)
    with SimpleTarget {

  override def toDecl: String = {
    val identifier = this.name
    val subtypeIndication = VhdlType(this.typ).vhdlName
    // See IEEE Std 1076-2002 section 4.3.1.2
    s"signal $identifier : $subtypeIndication;"
  }
}

case class SimpleVariableTarget(
    override val name: String,
    override val typ: Type
) extends VariableTarget(name, typ)
    with SimpleTarget {

  override def toDecl: String = {
    val identifier = this.name
    val subtypeIndication = VhdlType(this.typ).vhdlName
    // See IEEE Std 1076-2002 section 4.3.1.3
    s"variable $identifier : $subtypeIndication;"
  }
}
