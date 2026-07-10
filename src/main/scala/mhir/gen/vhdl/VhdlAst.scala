package mhir.gen.vhdl

import mhir.debug.indent

private[vhdl] sealed trait Decl {
  def vhdlDecl: String
}

private[vhdl] sealed trait VhdlFunctionMode
private[vhdl] object PureFunction extends VhdlFunctionMode
private[vhdl] object ImpureFunction extends VhdlFunctionMode

private[vhdl] case class VhdlFunction(
    name: String,
    args: Seq[(String, VhdlType)],
    returnType: VhdlType,
    decls: Seq[Decl],
    ret: String,
    mode: VhdlFunctionMode = PureFunction
) extends Decl {
  require(
    decls.forall(d => !d.isInstanceOf[Signal]),
    "Signals are not allowed in VHDL functions."
  )

  private def signature: String = {
    val pureOrImpure = mode match {
      case PureFunction   => "pure"
      case ImpureFunction => "impure"
    }
    val argList = args
      .map({ case (x, t) =>
        val tStr = t match {
          case VhdlStdLogicVec(n, _) =>
            if (n < 0) t.vhdlTypeMark else t.vhdlName
          case VhdlArray(n, _) => if (n < 0) t.vhdlTypeMark else t.vhdlName
          case VhdlSigned(n)   => if (n < 0) t.vhdlTypeMark else t.vhdlName
          case VhdlUnsigned(n) => if (n < 0) t.vhdlTypeMark else t.vhdlName
          case _               => t.vhdlName
        }
        s"$x : in $tStr"
      })
      .mkString("; ")
    s"$pureOrImpure function $name ($argList) return ${returnType.vhdlTypeMark}"
  }

  def vhdlSignature: String = s"$signature;"

  def vhdlImpl: String = {
    val declsStr = if (decls.isEmpty) {
      "-- No variables needed"
    } else {
      decls.map(x => x.vhdlDecl).mkString("\n")
    }
    val varAssignments = decls
      .flatMap({
        case x: VhdlVariable             => Some(x.assignStmt)
        case _: VhdlFunction | _: Signal => None
      })
      .mkString("\n")
    val body = s"$varAssignments\nreturn $ret;"
    s"""$signature is
       |${indent(declsStr)}
       |begin
       |${indent(body)}
       |end;
       |""".stripMargin.stripTrailing
  }

  override def vhdlDecl: String = vhdlImpl
}

private[vhdl] trait Port
private[vhdl] case class InPort(
    name: String,
    typ: VhdlType
) extends Port

private[vhdl] case class OutPort(
    name: String,
    typ: VhdlType,
    assign: Option[String]
) extends Port

private[vhdl] sealed trait VarOrSigDecl extends Decl {
  val name: String
  val typ: VhdlType
}

private[vhdl] case class VhdlVariable(
    name: String,
    typ: VhdlType,
    assignStmt: String
) extends VarOrSigDecl {
  override def vhdlDecl: String = s"variable $name : ${typ.vhdlName};"
}

/** A signal within a VHDL design
  *
  * @param category
  *   a user-readable category for this signal. This simply allows the signals
  *   to be grouped in a readable way in the VHDL code; it does not change the
  *   behaviour of the signal.
  * @param name
  *   the name of the signal.
  * @param typ
  *   the type of the signal.
  * @param assignStmt
  *   the statement which updates the signal (see also: [[cond]]).
  * @param cond
  *   the condition under which the signal is updated. If [[None]], then
  *   [[assignStmt]] will be interpreted as a combinational statement. If
  *   [[Some]], then [[assignStmt]] will be interpreted as a sequential
  *   statement which is run if [[cond]] is true.
  */
private[vhdl] case class Signal(
    category: String,
    name: String,
    typ: VhdlType,
    assignStmt: Option[String] = None,
    cond: Option[String] = None
) extends VarOrSigDecl {
  override def vhdlDecl: String = s"signal $name : ${typ.vhdlName};"
}

/** The arguments to use for a VHDL `port map`.
  *
  * @param map
  *   a map from port names to signal names.
  */
private[vhdl] case class PortMap(map: Map[String, String])

/** An instantiation of a VHDL entity.
  *
  * @param name
  *   the name to use for this instance.
  * @param c
  *   the component to instantiate.
  * @param args
  *   the arguments to pass to the component.
  */
private[vhdl] case class VhdlEntityInstantiation(
    name: String,
    c: VhdlComponent,
    args: PortMap
)
