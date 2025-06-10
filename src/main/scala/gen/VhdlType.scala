package gen

import debug.indent
import ir._

private[gen] sealed trait VhdlType {

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

  /** The default value for this type, consistent with Default(T) from the IR.
    */
  def defaultVal: String
}

private[gen] object VhdlType {
  def apply(t: Type): VhdlType = {
    t match {
      case TyInt            => VhdlInt
      case TyBool           => VhdlBool
      case TyTuple(ts @ _*) => VhdlRecord(ts.map(t => VhdlType(t)))
      case TyVec(t, len) if len.freeVars().isEmpty =>
        val n = ir.eval(len).asInstanceOf[IntCst].i
        VhdlArray(n.toInt, VhdlType(t))
      case t =>
        throw new IllegalArgumentException(
          s"Cannot convert type $t to a VHDL type."
        )
    }
  }
}

private[gen] case object VhdlInt extends VhdlType {
  override def vhdlName: String = "integer"

  override def vhdlDefinition: Option[String] = None

  override def descendants: Set[VhdlType] = Set()

  override def bitWidth: Int = 32

  override def defaultVal: String = "0"
}

private[gen] case object VhdlBool extends VhdlType {
  override def vhdlName: String = "boolean"

  override def vhdlDefinition: Option[String] = None

  override def descendants: Set[VhdlType] = Set()

  override def bitWidth: Int = 1

  override def defaultVal: String = "false"
}

private[gen] case object VhdlStdLogic extends VhdlType {
  override def vhdlName: String = "std_logic"

  override def vhdlDefinition: Option[String] = None

  override def descendants: Set[VhdlType] = Set()

  override def bitWidth: Int = 1

  override def defaultVal: String = "'0'"
}

private[gen] case class VhdlStdLogicVec(n: Int) extends VhdlType {
  override def vhdlName: String = s"std_logic_vector(${n - 1} downto 0)"
  override def vhdlTypeMark: String = "std_logic_vector"

  override def vhdlDefinition: Option[String] = None

  override def descendants: Set[VhdlType] = Set()

  override def bitWidth: Int = n

  override def defaultVal: String = "(others => '0')"
}

private[gen] case class VhdlRecord(fieldTypes: Seq[VhdlType]) extends VhdlType {
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

  override def defaultVal: String = {
    val assignments = fieldTypes.zipWithIndex
      .map({ case (t, i) => s"i_$i => ${t.defaultVal}" })
      .mkString(",")
    s"($assignments)"
  }
}

private[gen] case class VhdlArray(n: Int, t: VhdlType) extends VhdlType {
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

  override def defaultVal: String = s"(others => ${t.defaultVal})"

  def vecAccessFunDef: VhdlFunction = {
    val defaultVal = t.defaultVal
    val cases =
      ((0 until n).map(i => s"v($i) when $i") ++ Seq(
        s"$defaultVal when others"
      ))
        .mkString(",\n")
    val body = s"with i select x :=\n${indent(cases)};"
    VhdlFunction(
      name = "vec_access",
      args = Seq(("v", this), ("i", VhdlInt)),
      returnType = t,
      decls = Seq(VhdlVariable("x", t, assignStmt = body)),
      ret = "x",
      mode = PureFunction
    )
  }
}
