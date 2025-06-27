package mhir.ir
package typecheck

/** Shorthands for common integer types.
  */
trait CommonIntTypes {

  /** The type of a 0-bit unsigned number—i.e., a number which can only be zero.
    */
  val U0: TyUInt = TyUInt(0)

  /** The type of an 8-bit unsigned integer.
    */
  val U8: TyUInt = TyUInt(8)

  /** The type of a 16-bit unsigned integer.
    */
  val U16: TyUInt = TyUInt(16)

  /** The type of a 32-bit unsigned integer.
    */
  val U32: TyUInt = TyUInt(32)

  /** The type of an 8-bit signed integer.
    */
  val I8: TySInt = TySInt(8)

  /** The type of a 9-bit signed integer. (This is the type of [[ToSigned]] when
    * the input has type [[U8]].)
    */
  val I9: TySInt = TySInt(9)

  /** The type of a 16-bit signed integer.
    */
  val I16: TySInt = TySInt(16)

  /** The type of a 17-bit signed integer. (This is the type of [[ToSigned]]
    * when the input has type [[U16]].)
    */
  val I17: TySInt = TySInt(17)

  /** The type of a 32-bit signed integer.
    */
  val I32: TySInt = TySInt(32)

  /** The type of a 33-bit signed integer. (This is the type of [[ToSigned]]
    * when the input has type [[U32]].)
    */
  val I33: TySInt = TySInt(33)

  /** Common integer types.
    */
  val COMMON_INT_TYPES: Seq[TyAnyInt] = Seq(U8, U16, U32, I8, I16, I32)
}
