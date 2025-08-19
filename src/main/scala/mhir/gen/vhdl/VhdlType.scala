package mhir.gen.vhdl

import mhir.debug.indent
import mhir.ir._

private[vhdl] sealed trait VhdlType {

  /** The VHDL code used to <i>refer to</i> this type, e.g., in signal
    * declarations.
    */
  def vhdlName: String

  /** The type mark for this type. This is used, for instance, in function
    * return types.
    */
  def vhdlTypeMark: String = vhdlName

  /** The VHDL code required to <i>define</i> this type. Will be
    * <code>None</code> for built-in types.
    */
  def vhdlDefinition: Option[String]

  /** All types within this one, recursively.
    */
  def descendants: Set[VhdlType]

  /** The number of bits required by this type.
    */
  def bitWidth: Int

  /** Constructs a `std_logic_vector` type with the same bit width as this type.
    */
  def toStdLogicVec: VhdlType = {
    VhdlStdLogicVec(this.bitWidth)
  }
}

private[vhdl] object VhdlType {
  def apply(t: Type): VhdlType = {
    t match {
      case TyUInt(w)        => VhdlUnsigned(w)
      case TySInt(w)        => VhdlSigned(w)
      case TyBool           => VhdlBool
      case TyTuple(ts @ _*) => VhdlRecord(ts.map(t => VhdlType(t)))
      case TyVec(t, len) if len.freeVars().isEmpty =>
        val n = mhir.ir.eval(len).asInstanceOf[IntCst].i
        VhdlArray(n.toInt, VhdlType(t))
      case t =>
        throw new IllegalArgumentException(
          s"Cannot convert type $t to a VHDL type."
        )
    }
  }
}

/** A VHDL signed integer.
  *
  * @param width
  *   the bit width.
  */
private[vhdl] case class VhdlSigned(width: Int) extends VhdlType {
  override def vhdlName: String = s"signed(${width - 1} downto 0)"
  override def vhdlTypeMark: String = "signed"

  override def vhdlDefinition: Option[String] = None

  override def descendants: Set[VhdlType] = Set()

  override def bitWidth: Int = width
}

/** A VHDL unsigned integer.
  *
  * @param width
  *   the bit width.
  */
private[vhdl] case class VhdlUnsigned(width: Int) extends VhdlType {
  override def vhdlName: String = s"unsigned(${width - 1} downto 0)"
  override def vhdlTypeMark: String = "unsigned"

  override def vhdlDefinition: Option[String] = None

  override def descendants: Set[VhdlType] = Set()

  override def bitWidth: Int = width
}

private[vhdl] case object VhdlBool extends VhdlType {
  override def vhdlName: String = "boolean"

  override def vhdlDefinition: Option[String] = None

  override def descendants: Set[VhdlType] = Set()

  override def bitWidth: Int = 1
}

private[vhdl] case object VhdlStdLogic extends VhdlType {
  override def vhdlName: String = "std_logic"

  override def vhdlDefinition: Option[String] = None

  override def descendants: Set[VhdlType] = Set()

  override def bitWidth: Int = 1
}

private[vhdl] case class VhdlStdLogicVec(n: Int) extends VhdlType {
  override def vhdlName: String = s"std_logic_vector(${n - 1} downto 0)"
  override def vhdlTypeMark: String = "std_logic_vector"

  override def vhdlDefinition: Option[String] = None

  override def descendants: Set[VhdlType] = Set()

  override def bitWidth: Int = n
}

private[vhdl] case class VhdlRecord(fieldTypes: Seq[VhdlType])
    extends VhdlType {
  override def vhdlName: String = {
    val fieldTypeNames = fieldTypes
      .map(t => {
        val name = t.vhdlName
        if (name.startsWith("\\")) {
          assert(name.length >= 2)
          assert(name.endsWith("\\"))
          name.drop(1).dropRight(1)
        } else {
          name
        }
      })
    s"\\(${fieldTypeNames.mkString(", ")})\\"
  }

  override def vhdlDefinition: Option[String] = {
    if (fieldTypes.isEmpty) {
      // VHDL records cannot be empty, so use an empty array instead
      Some(s"type $vhdlName is array (0 downto 1) of character;")
    } else {
      val fieldDecls = fieldTypes.zipWithIndex
        .map({ case (t, i) => s"i_$i : ${t.vhdlName};" })
      val definition =
        s"""type $vhdlName is
           |record
           |    ${fieldDecls.mkString("\n    ")}
           |end record;
           |""".stripMargin.stripTrailing
      Some(definition)
    }
  }

  override def descendants: Set[VhdlType] = {
    fieldTypes.toSet.flatMap((t: VhdlType) => t.descendants + t)
  }

  override def bitWidth: Int = fieldTypes.map(t => t.bitWidth).sum
}

private[vhdl] case class VhdlArray(n: Int, t: VhdlType) extends VhdlType {
  override def vhdlName: String = {
    val elemTypeName = t.vhdlName
    val strippedElemTypeName = if (elemTypeName.startsWith("\\")) {
      assert(elemTypeName.length >= 2)
      assert(elemTypeName.endsWith("\\"))
      elemTypeName.drop(1).dropRight(1)
    } else {
      elemTypeName
    }
    s"\\Vec[$strippedElemTypeName; $n]\\"
  }

  override def vhdlDefinition: Option[String] = {
    val definition =
      s"""type $vhdlName is
         |array (0 to ${n - 1}) of ${t.vhdlName};
         |""".stripMargin.stripTrailing
    Some(definition)
  }

  override def descendants: Set[VhdlType] = {
    t.descendants + t
  }

  override def bitWidth: Int = n * t.bitWidth

  val idxBitWidth: Int = {
    if (this.n <= 0) {
      0
    } else {
      (n - 1).toBinaryString.length
    }
  }

  def vecAccessFunDef: VhdlFunction = {
    val cases =
      ((0 until n).map(i => s"when $i => x := v($i);") :+ "when others =>")
        .mkString("\n")
    assert(
      this.idxBitWidth < 32,
      s"cannot generate vec_access function when index bit width is ${this.idxBitWidth}"
    )
    val body = s"case to_integer(i) is\n${indent(cases)}\nend case;"
    VhdlFunction(
      name = "vec_access",
      args = Seq(("v", this), ("i", VhdlUnsigned(this.idxBitWidth))),
      returnType = t,
      decls = Seq(
        VhdlVariable("x", t, assignStmt = body)
      ),
      ret = "x",
      mode = PureFunction
    )
  }
}
