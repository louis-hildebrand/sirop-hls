package mhir.gen.vhdl

import mhir.ir._

/** The target of a signal or variable assignment.
  *
  * In C terminology, this basically represents an lvalue.
  *
  * @note
  *   The [[name]] does not have to be a valid identifier. It could also be an
  *   array access or slice like `v(2 downto 0)`, for example.
  * @note
  *   The [[typ]] does not have to be convertible to a [[VhdlType]]; it could be
  *   a function type, for example.
  */
case class Target(name: String, typ: Type)

object Target {
  def apply(x: Param): Target = {
    Target(x.name, x.typ)
  }
}
