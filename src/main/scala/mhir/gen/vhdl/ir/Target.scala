package mhir.gen.vhdl.ir

import mhir.ir._

/** The target of a signal or variable assignment.
  *
  * In C terminology, this basically represents an lvalue.
  *
  * @note
  *   The [[expr]] does not have to be a valid identifier. It could also be an
  *   array access or slice like `v(2 downto 0)`, for example.
  * @note
  *   The [[typ]] does not have to be convertible to a [[VhdlType]]; it could be
  *   a function type, for example.
  */
sealed trait Target {
  def expr: String
  def typ: Type
}

object Target {

  def apply(x: Param): SimpleTarget = SimpleTarget(x.name, x.typ)
}

case class SimpleTarget(baseName: String, typ: Type) extends Target {

  // TODO: Implement a more accurate check?
  require(this.baseName.forall({ c =>
    // Digit
    (c >= '0' && c <= '9') ||
    // Letter
    (c >= 'A' && c <= 'Z') ||
    (c >= 'a' && c <= 'z') ||
    // Underscore
    c == '_'
  }))

  def expr: String = this.baseName
}

case class ComplexTarget(expr: String, typ: Type) extends Target
