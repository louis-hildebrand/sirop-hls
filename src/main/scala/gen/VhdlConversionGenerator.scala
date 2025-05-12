package gen

object VhdlConversionGenerator {

  /** @param e
    *   The VHDL expression to convert to a std_logic_vector
    * @param t
    *   The type of the original expression
    */
  def toStdLogicVector(e: String, t: VhdlType): String = {
    t match {
      case VhdlStdLogic       => s"(0 => $e)"
      case _: VhdlStdLogicVec => e
      case _ =>
        val f = toSlvConverterName(t)
        s"$f($e)"
    }
  }

  private def toSlvConverterName(t: VhdlType): String = {
    "to_std_logic_vector"
  }

  /** Define a function that converts the given type to a std_logic_vector.
    *
    * @param t
    *   The desired input type
    */
  def toSlvConverter(t: VhdlType): Option[VhdlFunction] = {
    t match {
      case VhdlBool           => Some(boolToSlvConverter)
      case VhdlInt            => Some(intToSlvConverter)
      case VhdlStdLogic       => None
      case _: VhdlStdLogicVec => None
      case t: VhdlRecord      => Some(recordToSlvConverter(t))
      case t: VhdlArray       => Some(arrayToSlvConverter(t))
    }
  }

  private def boolToSlvConverter: VhdlFunction = {
    VhdlFunction(
      name = toSlvConverterName(VhdlBool),
      args = Seq(("b", VhdlBool)),
      returnType = VhdlStdLogicVec(1),
      decls =
        Seq(Variable("x", VhdlStdLogicVec(1), "\"1\" when (b) else \"0\"")),
      ret = "x"
    )
  }

  private def intToSlvConverter: VhdlFunction = {
    VhdlFunction(
      name = toSlvConverterName(VhdlInt),
      args = Seq(("n", VhdlInt)),
      returnType = VhdlStdLogicVec(VhdlInt.bitWidth),
      decls = Seq(),
      ret = s"std_logic_vector(to_signed(n, ${VhdlInt.bitWidth}))"
    )
  }

  private def recordToSlvConverter(tup: VhdlRecord): VhdlFunction = {
    VhdlFunction(
      name = toSlvConverterName(tup),
      args = Seq(("x", tup)),
      returnType = VhdlStdLogicVec(tup.bitWidth),
      decls = Seq(),
      ret = if (tup.fieldTypes.isEmpty) {
        "\"\""
      } else {
        tup.fieldTypes.zipWithIndex
          .map({ case (t, i) => toStdLogicVector(s"x.i_$i", t) })
          .mkString(" & ")
      }
    )
  }

  private def arrayToSlvConverter(arr: VhdlArray): VhdlFunction = {
    VhdlFunction(
      name = toSlvConverterName(arr),
      args = Seq(("x", arr)),
      returnType = VhdlStdLogicVec(arr.bitWidth),
      decls = Seq(),
      ret = (0 until arr.n)
        .map(i => toStdLogicVector(s"x($i)", arr.t))
        .mkString(" & ")
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
      case _ =>
        val f = fromSlvConverterName(t)
        s"$f($e)"
    }
  }

  private def fromSlvConverterName(t: VhdlType): String = {
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
  def fromSlvConverter(t: VhdlType): Option[VhdlFunction] = {
    t match {
      case VhdlStdLogic       => Some(stdLogicFromSlvConverter)
      case VhdlBool           => Some(boolFromSlvConverter)
      case VhdlInt            => Some(intFromSlvConverter)
      case _: VhdlStdLogicVec => None
      case t: VhdlRecord      => Some(recordFromSlvConverter(t))
      case t: VhdlArray       => Some(arrayFromSlvConverter(t))
    }
  }

  private def stdLogicFromSlvConverter: VhdlFunction = {
    VhdlFunction(
      name = fromSlvConverterName(VhdlStdLogic),
      args = Seq(("v", VhdlStdLogicVec(1))),
      returnType = VhdlStdLogic,
      decls = Seq(),
      ret = "v(0)"
    )
  }

  private def boolFromSlvConverter: VhdlFunction = {
    VhdlFunction(
      name = fromSlvConverterName(VhdlBool),
      args = Seq(("v", VhdlStdLogicVec(1))),
      returnType = VhdlBool,
      decls = Seq(),
      ret = "v = \"1\""
    )
  }

  private def intFromSlvConverter: VhdlFunction = {
    VhdlFunction(
      name = fromSlvConverterName(VhdlInt),
      args = Seq(("v", VhdlStdLogicVec(VhdlInt.bitWidth))),
      returnType = VhdlInt,
      decls = Seq(),
      ret = "to_integer(signed(v))"
    )
  }

  private def recordFromSlvConverter(tup: VhdlRecord): VhdlFunction = {
    VhdlFunction(
      name = fromSlvConverterName(tup),
      args = Seq(("v", VhdlStdLogicVec(tup.bitWidth))),
      returnType = tup,
      decls = Seq(),
      ret = if (tup.fieldTypes.isEmpty) {
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
    )
  }

  private def arrayFromSlvConverter(vec: VhdlArray): VhdlFunction = {
    VhdlFunction(
      name = fromSlvConverterName(vec),
      args = Seq(("v", VhdlStdLogicVec(vec.bitWidth))),
      returnType = vec,
      decls = Seq(),
      ret = {
        val elems = (0 until vec.n).map(i => {
          val msb = vec.bitWidth - 1 - i * vec.t.bitWidth
          val lsb = msb - vec.t.bitWidth + 1
          fromStdLogicVector(s"v($msb downto $lsb)", vec.t)
        })
        val assignments = elems.zipWithIndex
          .map({ case (v, i) => s"$i => $v" })
          .mkString(", ")
        s"($assignments)"
      }
    )
  }
}
