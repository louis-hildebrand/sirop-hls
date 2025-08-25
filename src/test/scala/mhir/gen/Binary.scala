package mhir.gen

import mhir.ir._

/** Helper methods for converting to binary.
  */
object Binary {

  /** Converts an expression to binary.
    *
    * @note
    *   if padding is required for the length of the binary string to be a
    *   multiple of eight, it will be added on the left (i.e., the most
    *   significant bits), and only after concatenating the binary
    *   representations of all the expressions.
    *
    * @param expressions
    *   the expressions to convert.
    * @return
    *   a sequence of bytes representing the expressions.
    */
  def apply(expressions: Expr*): Array[Byte] = {
    def str(e: Expr): String = {
      e match {
        case Undefined(typ) => str(mhir.ir.eval(Default(typ)))
        case False          => "0"
        case True           => "1"
        case c: IntCst =>
          val w = c.typ.asInstanceOf[TyAnyInt].w
          if (c.i < 0) {
            val bin = c.i.toBinaryString
            assert(bin.head == '1')
            assert(bin.length == 64)
            val truncated = bin.takeRight(w)
            assert(truncated.head == '1')
            truncated
          } else {
            val bin = c.i.toBinaryString
            assert(bin.length <= w)
            val padded = ("0" * (w - bin.length)) + bin
            if (c.typ.isInstanceOf[TySInt]) {
              assert(padded.head == '0')
            }
            padded
          }
        case c: FixCst =>
          str(C(c.numer)(c.typ.t))
        case Tuple(elems @ _*) =>
          elems.map(str).mkString("")
        case VecLiteral(elems @ _*) =>
          elems.map(str).mkString("")
        case _ => ???
      }
    }

    val binaryStr = expressions.map(str).mkString("")
    binaryStringToBytes(binaryStr)
  }

  /** Creates a mask to ignore [[mhir.gen.Undefined]] values.
    *
    * @note
    *   if padding is required for the length of the binary string to be a
    *   multiple of eight, it will be added on the left (i.e., the most
    *   significant bits).
    *
    * @param expected
    *   the expression for which to create a mask, which may contain
    *   [[mhir.gen.Undefined]] elements.
    */
  def mask(expected: Expr): Array[Byte] = {
    def getMask(expected: Expr): String = {
      require(expected.hasType)
      expected match {
        case VecLiteral(elems @ _*) =>
          elems.map(getMask).mkString("")
        case Tuple(elems @ _*) =>
          elems.map(getMask).mkString("")
        case Undefined(typ) =>
          "0" * Binary.bitWidth(typ)
        case v =>
          assert(!v.contains(classOf[Undefined]))
          "1" * Binary.bitWidth(v.typ)
      }
    }

    binaryStringToBytes(getMask(expected))
  }

  /** Converts the given binary string to an array of bytes.
    */
  private def binaryStringToBytes(binaryStr: String): Array[Byte] = {
    val paddedStr = if (binaryStr.length % 8 == 0) {
      binaryStr
    } else {
      val target = 8 * (1 + binaryStr.length / 8)
      assert(target > binaryStr.length)
      ("0" * (target - binaryStr.length)) + binaryStr
    }
    paddedStr.grouped(8).map(s => Integer.parseInt(s, 2).toByte).toArray
  }

  /** Finds the number of bits in the unpadded binary representation of values
    * of the given type.
    */
  def bitWidth(typ: Type): Int = {
    typ match {
      case typ: TyAnyInt       => typ.w
      case typ: TyFix          => typ.t.w
      case TyBool              => 1
      case TyTuple(ts @ _*)    => ts.map(bitWidth).sum
      case TyVec(t, IntCst(n)) => bitWidth(t) * n.toInt
      case typ =>
        throw new IllegalArgumentException(
          s"Cannot get bit width for type $typ."
        )
    }
  }

  /** Finds the number of bits in the binary representation of values of the
    * given types, after padding.
    *
    * In other words, this methods finds the length of the binary string
    * returned by [[apply]].
    */
  def paddedBitWidth(types: Type*): Int = {
    val unpaddedWidth = types.map(bitWidth).sum
    if (unpaddedWidth % 8 == 0) {
      unpaddedWidth
    } else {
      8 * (1 + unpaddedWidth / 8)
    }
  }
}
