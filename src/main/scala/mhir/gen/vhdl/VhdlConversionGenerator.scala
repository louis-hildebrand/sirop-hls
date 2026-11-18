package mhir.gen.vhdl

import mhir.debug.indent

import scala.annotation.nowarn

object VhdlConversionGenerator {

  def defaultFunctions: Seq[FunctionDeclAndBody] = {
    // See IEEE Std 1076-2002 sections 2.1 and 2.2
    Seq(
      FunctionDeclAndBody(
        s"""pure function bool2sl(b: boolean) return std_logic;""",
        s"""pure function bool2sl(b: boolean) return std_logic is
           |begin
           |    if b then
           |        return '1';
           |    else
           |        return '0';
           |    end if;
           |end function bool2sl;
           |""".stripMargin.stripTrailing
      ),
      FunctionDeclAndBody(
        s"""pure function sl2bool(x: std_logic) return boolean;""",
        s"""pure function sl2bool(x: std_logic) return boolean is
           |begin
           |    return x = '1';
           |end function sl2bool;
           |""".stripMargin.stripTrailing
      ),
      FunctionDeclAndBody(
        s"""pure function pad(x: signed; w: natural) return signed;""",
        s"""pure function pad(x: signed; w: natural) return signed is
           |begin
           |    return resize(x, w);
           |end function pad;
           |""".stripMargin.stripTrailing
      ),
      FunctionDeclAndBody(
        s"""pure function pad(x: unsigned; w: natural) return unsigned;""",
        s"""pure function pad(x: unsigned; w: natural) return unsigned is
           |begin
           |    return resize(x, w);
           |end function pad;
           |""".stripMargin.stripTrailing
      ),
      FunctionDeclAndBody(
        s"""pure function truncate(x: signed; w: natural) return signed;""",
        s"""pure function truncate(x: signed; w: natural) return signed is
           |begin
           |    return x(w-1 downto 0);
           |end function truncate;
           |""".stripMargin.stripTrailing
      ),
      FunctionDeclAndBody(
        s"""pure function truncate(x: unsigned; w: natural) return unsigned;""",
        s"""pure function truncate(x: unsigned; w: natural) return unsigned is
           |begin
           |    return x(w-1 downto 0);
           |end function truncate;
           |""".stripMargin.stripTrailing
      )
    )
  }

  /** @param e
    *   The VHDL expression to convert to a std_logic_vector
    * @param t
    *   The type of the original expression
    */
  def toStdLogicVector(e: String, t: VhdlType): String = {
    t match {
      case VhdlStdLogic                    => s"(0 => $e)"
      case _: VhdlStdLogicVec              => e
      case _: VhdlSigned | _: VhdlUnsigned => s"std_logic_vector($e)"
      case _ =>
        val f = toSlvConverterName(t)
        s"$f($e)"
    }
  }

  private def toSlvConverterName(@nowarn t: VhdlType): String = {
    "to_std_logic_vector"
  }

  /** Define a function that converts the given type to a std_logic_vector.
    *
    * @param t
    *   The desired input type
    */
  def toSlvConverter(t: VhdlType): Option[FunctionDeclAndBody] = {
    t match {
      case VhdlBool           => Some(boolToSlvConverter)
      case _: VhdlSigned      => None
      case _: VhdlUnsigned    => None
      case VhdlStdLogic       => None
      case _: VhdlStdLogicVec => None
      case t: VhdlRecord      => Some(recordToSlvConverter(t))
      case t: VhdlArray       => Some(arrayToSlvConverter(t))
    }
  }

  private def boolToSlvConverter: FunctionDeclAndBody = {
    val designator = toSlvConverterName(VhdlBool)
    // See IEEE Std 1076-2002 sections 2.1 and 2.2
    FunctionDeclAndBody(
      s"""pure function $designator(b: boolean) return std_logic_vector;""",
      s"""pure function $designator(b: boolean) return std_logic_vector is
         |begin
         |    if b then
         |        return "1";
         |    else
         |        return "0";
         |    end if;
         |end function $designator;
         |""".stripMargin.stripTrailing
    )
  }

  private def recordToSlvConverter(tup: VhdlRecord): FunctionDeclAndBody = {
    val designator = toSlvConverterName(tup)
    val expr = if (tup.fieldTypes.isEmpty) {
      "\"\""
    } else {
      tup.fieldTypes.zipWithIndex
        .map({ case (t, i) => toStdLogicVector(s"x.i_$i", t) })
        .mkString(" & ")
    }
    // See IEEE Std 1076-2002 sections 2.1 and 2.2
    FunctionDeclAndBody(
      s"""pure function $designator(x: ${tup.vhdlName}) return std_logic_vector;""",
      s"""pure function $designator(x: ${tup.vhdlName}) return std_logic_vector is
         |begin
         |    return $expr;
         |end function $designator;
         |""".stripMargin.stripTrailing
    )
  }

  private def arrayToSlvConverter(arr: VhdlArray): FunctionDeclAndBody = {
    val designator = toSlvConverterName(arr)
    val statement = if (arr.n <= 0) {
      "-- Nothing to do for an empty vector"
    } else {
      val msb = s"${arr.bitWidth - 1}-${arr.t.bitWidth}*i"
      val lsb = s"${arr.bitWidth - arr.t.bitWidth}-${arr.t.bitWidth}*i"
      val slvElem = toStdLogicVector(s"x(i)", arr.t)
      s"""for i in 0 to ${arr.n - 1} loop
         |    result($msb downto $lsb) := $slvElem;
         |end loop;
         |""".stripMargin.stripTrailing
    }
    // See IEEE Std 1076-2002 sections 2.1 and 2.2
    FunctionDeclAndBody(
      s"""pure function $designator(x: ${arr.vhdlName}) return std_logic_vector;""",
      s"""pure function $designator(x: ${arr.vhdlName}) return std_logic_vector is
         |    variable result: std_logic_vector(${arr.bitWidth - 1} downto 0);
         |begin
         |${indent(statement)}
         |    return result;
         |end function $designator;
         |""".stripMargin.stripTrailing
    )
  }

  /** Convert a std_logic_vector to the given type.
    *
    * @param e
    *   The VHDL expression to convert
    * @param t
    *   The desired output type
    */
  def fromStdLogicVector(e: String, t: VhdlType): String = {
    t match {
      case _: VhdlStdLogicVec => e
      case _: VhdlSigned      => s"signed($e)"
      case _: VhdlUnsigned    => s"unsigned($e)"
      case _ =>
        val f = fromSlvConverterName(t)
        s"$f($e)"
    }
  }

  private def fromSlvConverterName(@nowarn t: VhdlType): String = {
    // It seems like the VHDL compiler can tell which overload to use based on
    // the return type (since the result of this conversion is always assigned
    // to a signal or return value, not converted back to a std_logic_vector).
    // If at some point that is no longer true, just change this name to
    // include the target type.
    "from_std_logic_vector"
  }

  /** Define a function that converts a std_logic_vector to the given type.
    *
    * @param t
    *   The desired output type
    */
  def fromSlvConverter(t: VhdlType): Option[FunctionDeclAndBody] = {
    t match {
      case VhdlStdLogic       => Some(stdLogicFromSlvConverter)
      case VhdlBool           => Some(boolFromSlvConverter)
      case _: VhdlSigned      => None
      case _: VhdlUnsigned    => None
      case _: VhdlStdLogicVec => None
      case t: VhdlRecord      => Some(recordFromSlvConverter(t))
      case t: VhdlArray       => Some(arrayFromSlvConverter(t))
    }
  }

  private def stdLogicFromSlvConverter: FunctionDeclAndBody = {
    val designator = fromSlvConverterName(VhdlStdLogic)
    // See IEEE Std 1076-2002 sections 2.1 and 2.2
    FunctionDeclAndBody(
      s"""pure function $designator(v: std_logic_vector(0 downto 0)) return std_logic;""",
      s"""pure function $designator(v: std_logic_vector(0 downto 0)) return std_logic is
         |begin
         |    return v(0);
         |end function $designator;
         |""".stripMargin.stripTrailing
    )
  }

  private def boolFromSlvConverter: FunctionDeclAndBody = {
    val designator = fromSlvConverterName(VhdlBool)
    // See IEEE Std 1076-2002 sections 2.1 and 2.2
    FunctionDeclAndBody(
      s"""pure function $designator(v: std_logic_vector(0 downto 0)) return boolean;""",
      s"""pure function $designator(v: std_logic_vector(0 downto 0)) return boolean is
         |begin
         |    return v = "1";
         |end function $designator;
         |""".stripMargin.stripTrailing
    )
  }

  private def recordFromSlvConverter(tup: VhdlRecord): FunctionDeclAndBody = {
    val designator = fromSlvConverterName(tup)
    val expr = if (tup.fieldTypes.isEmpty) {
      "\"\""
    } else {
      val elems = tup.fieldTypes.zipWithIndex
        .map({ case (t, i) =>
          val bitsBefore = tup.fieldTypes.take(i).map(t => t.bitWidth).sum
          val bitsInside = t.bitWidth
          val msb = tup.bitWidth - 1 - bitsBefore
          val lsb = msb - bitsInside + 1
          fromStdLogicVector(s"v($msb downto $lsb)", t)
        })
      val assignments = elems.zipWithIndex
        .map({ case (v, i) => s"i_$i => $v" })
        .mkString(", ")
      s"($assignments)"
    }
    // See IEEE Std 1076-2002 sections 2.1 and 2.2
    FunctionDeclAndBody(
      s"""pure function $designator(v: ${tup.toStdLogicVec.vhdlName}) return ${tup.vhdlTypeMark};""",
      s"""pure function $designator(v: ${tup.toStdLogicVec.vhdlName}) return ${tup.vhdlTypeMark} is
         |begin
         |    return $expr;
         |end function $designator;
         |""".stripMargin.stripTrailing
    )
  }

  private def arrayFromSlvConverter(arr: VhdlArray): FunctionDeclAndBody = {
    val designator = fromSlvConverterName(arr)
    val slv = arr.toStdLogicVec
    val statement = if (arr.n <= 0) {
      "-- Nothing to do for empty vector"
    } else {
      val msb = s"${arr.bitWidth - 1}-${arr.t.bitWidth}*i"
      val lsb = s"${arr.bitWidth - arr.t.bitWidth}-${arr.t.bitWidth}*i"
      val elem = fromStdLogicVector(s"v($msb downto $lsb)", arr.t)
      s"""for i in 0 to ${arr.n - 1} loop
         |    result(i) := $elem;
         |end loop;
         |""".stripMargin.stripTrailing
    }
    // See IEEE Std 1076-2002 sections 2.1 and 2.2
    FunctionDeclAndBody(
      s"""pure function $designator(v: ${slv.vhdlName}) return ${arr.vhdlTypeMark};""",
      s"""pure function $designator(v: ${slv.vhdlName}) return ${arr.vhdlTypeMark} is
         |    variable result: ${arr.vhdlName};
         |begin
         |${indent(statement)}
         |    return result;
         |end function $designator;
         |""".stripMargin.stripTrailing
    )
  }
}
