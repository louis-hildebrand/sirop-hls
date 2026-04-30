package mhir.ir

/** Trait that provides a way of canonicalizing expressions for the sizes of
  * collections.
  */
trait Canonicalizer {

  /** Convert the given expression into a canonical form.
    *
    * This is used in [[TyStm]] and [[TyVec]] to ensure the lengths can be
    * compared directly (e.g., so that `Vec[T, 1+1]` is treated the same as
    * `Vec[T, 2]`).
    */
  def canonicalize(n: Expr): Expr

  /** Conservatively check whether two lengths (e.g., vector sizes) are equal.
    *
    * If the result is <code>true</code> then the lengths are definitely equal,
    * but if the result is <code>False</code> then they may or may not be equal.
    */
  def sameLen(n1: Expr, n2: Expr): Boolean
}

/** A [[Canonicalizer]] that leaves the expression as-is.
  *
  * This is acceptable for cases where the expression is known to already be in
  * canonical form.
  */
object NoOpCanonicalizer extends Canonicalizer {
  override def canonicalize(n: Expr): Expr = n

  override def sameLen(n1: Expr, n2: Expr): Boolean = n1 == n2
}

/** The type of an expression.
  */
sealed trait Type {

  /** Constructs a function whose parameter has this type.
    *
    * @example
    *
    * {{{
    *   // A function whose input has type TyBool
    *   TyBool ::+ (x => x || True)
    * }}}
    *
    * @param f
    *   the body of the function.
    */
  def ::+(f: Param => Expr): Function = {
    val x = Param("x")(this)
    val body = f(x)
    val t =
      if (this != Missing && body.typ != Missing) TyArrow(this, body.typ)
      else Missing
    Function(x, f(x))(t)
  }

  /** Shorthand for [[TyArrow]].
    *
    * @param that
    *   the function input type.
    */
  def ->:(that: Type): Type = {
    // This method is right-associative (since it ends with a colon), and
    // therefore t1 ->: t2 actually means t2.->:(t1)
    TyArrow(that, this)
  }

  /** Performs the given substitutions within this type.
    *
    * @param subs
    *   a map from old expressions (i.e., the ones to be replaced) to new
    *   expressions (i.e., what to replace the old expressions with).
    */
  def substitute(subs: Map[Expr, Expr])(implicit c: Canonicalizer): Type = {
    this match {
      case TyVec(t, n) =>
        TyVec(t.substitute(subs), n.subPreserveType(subs))(c)
      case TyStm(t, n) =>
        TyStm(t.substitute(subs), n.subPreserveType(subs))(c)
      case TyArrow(t1, t2)  => TyArrow(t1.substitute(subs), t2.substitute(subs))
      case TyTuple(ts @ _*) => TyTuple(ts.map(t => t.substitute(subs)): _*)
      case t @ (Missing | TyBool | _: TySInt | _: TyUInt | _: TyFix) => t
    }
  }

  /** Shorthand for [[substitute(subs*]] with just one substitution.
    */
  def substitute(sub: (Expr, Expr))(implicit c: Canonicalizer): Type = {
    substitute(Map(sub))
  }

  /** Finds all the free variables in this type.
    */
  def freeVars: Set[Param] = {
    this match {
      case Missing | TyBool | _: TyAnyInt | _: TyFix => Set()
      case TyArrow(t1, t2) =>
        t1.freeVars ++ t2.freeVars
      case TyTuple(ts @ _*) =>
        ts.foldLeft(Set[Param]())({ case (acc, t) => acc ++ t.freeVars })
      case TyVec(t, n) =>
        t.freeVars ++ n.freeVars
      case TyStm(t, n) =>
        t.freeVars ++ n.freeVars
    }
  }

  /** Turn curried arrow types into equivalent non-curried versions.
    *
    * @example
    *   if a function has type `T1 -> T2 -> T3` (where `T3` is not an arrow
    *   type), then the uncurried function will have type `(T1, T2) -> T3`.
    */
  def uncurry: Type = {
    this match {
      case Missing | TyBool | _: TyAnyInt | _: TyFix => this
      case TyArrow(tIn, tOut: TyArrow) =>
        tOut.uncurry.asInstanceOf[TyArrow] match {
          case TyArrow(t1, t2) => TyArrow(TyTuple(tIn.uncurry, t1), t2)
        }
      case TyArrow(t1, t2)  => TyArrow(t1.uncurry, t2.uncurry)
      case TyTuple(ts @ _*) => TyTuple(ts.map(t => t.uncurry): _*)
      case TyVec(t, n)      => TyVec(t.uncurry, n)(NoOpCanonicalizer)
      case TyStm(t, n)      => TyStm(t.uncurry, n)(NoOpCanonicalizer)
    }
  }

  /** Check whether two types are "compatible," i.e., will have the same shape
    * in hardware.
    */
  def ~=(that: Type): Boolean = {
    (this, that) match {
      case (TyBool, TyBool)         => true
      case (TySInt(w1), TySInt(w2)) => w1 == w2
      case (TyUInt(w1), TyUInt(w2)) => w1 == w2
      case (TyFix(t1, shift1), TyFix(t2, shift2)) =>
        t1 == t2 && shift1 == shift2
      case (TyArrow(t1, t2), TyArrow(t3, t4)) =>
        (t1 ~= t3) && (t2 ~= t4)
      case (TyTuple(ts1 @ _*), TyTuple(ts2 @ _*)) =>
        (ts1.length == ts2.length
        && ts1
          .zip(ts2)
          .forall({ case (t1, t2) => t1 ~= t2 }))
      case (TyVec(t1, n1), TyVec(t2, n2)) =>
        // n1 and n2 are already canonicalized, so we should be able to compare
        // them syntactically
        (t1 ~= t2) && (n1 == n2)
      case (TyStm(t1, _), TyStm(t2, _)) =>
        // Two streams are compatible even if they have different lengths!
        t1 ~= t2
      case _ => false
    }
  }

  /** Like [[~=]], but ignores vector lengths.
    *
    * @param that
    *   the type to compare with.
    */
  def ~~=(that: Type): Boolean = {
    (this, that) match {
      case (TyBool, TyBool)         => true
      case (TyUInt(w1), TyUInt(w2)) => w1 == w2
      case (TySInt(w1), TySInt(w2)) => w1 == w2
      case (TyFix(t1, shift1), TyFix(t2, shift2)) =>
        t1 == t2 && shift1 == shift2
      case (TyTuple(ts1 @ _*), TyTuple(ts2 @ _*)) =>
        (ts1.length == ts2.length
        && ts1.zip(ts2).forall({ case (t1, t2) => t1 ~~= t2 }))
      case (TyVec(t1, _), TyVec(t2, _)) => t1 ~~= t2
      case (TyStm(t1, _), TyStm(t2, _)) => t1 ~~= t2
      case (TyArrow(t11, t12), TyArrow(t21, t22)) =>
        (t11 ~~= t21) && (t12 ~~= t22)
      case _ => false
    }
  }

  /** Decides whether this type is "data."
    *
    * Data types include integers, booleans, tuples whose children are all data,
    * etc. A component in hardware can take data as input and produces data as
    * output. On the other hand, functions and streams are <i>not</i> data.
    */
  def isData: Boolean = {
    this match {
      case _: TyAnyInt | _: TyFix | TyBool => true
      case TyTuple(ts @ _*)                => ts.forall(t => t.isData)
      case TyVec(t, _)                     => t.isData
      case Missing | _: TyArrow | _: TyStm => false
    }
  }

  def equalsGivenConstants(
      that: Type,
      constVals: Map[Param, Expr]
  )(implicit c: Canonicalizer): Boolean = {
    val subs = constVals.toMap[Expr, Expr]
    this.substitute(subs) == that.substitute(subs)
  }
}

/** Placeholder type for an expression which has not been type checked.
  */
case object Missing extends Type

/** Some custom integer with [[w]] bits.
  *
  * @param w
  *   the bit width.
  */
sealed abstract class TyAnyInt(val w: Int) extends Type {
  require(w >= 0, "Bit width must be non-negative.")

  /** Construct a new type with the same sign as this one but a width of [[w]].
    *
    * @param w
    *   the new bit width.
    */
  def withWidth(w: Int): TyAnyInt = {
    this match {
      case _ if w <= 0 => TyUInt(w)
      case _: TySInt   => TySInt(w)
      case _: TyUInt   => TyUInt(w)
    }
  }

  /** Finds the narrowest type with the same signedness as this that can fit
    * `k`.
    *
    * @param k
    *   the number that must fit in the new range.
    */
  def shrinkToFit(k: BigInt): TyAnyInt = {
    this match {
      case _: TySInt => TySInt.tightest(k, k)
      case _: TyUInt => TyUInt.tightest(k)
    }
  }

  /** Decides whether this type is wide enough to represent the given value.
    *
    * For example, 7 can be represented as a 3-bit unsigned number, as a 4-bit
    * signed number, etc. However, 7 cannot be represented as a 3-bit signed
    * number.
    *
    * @param n
    *   the number to check.
    */
  def contains(n: BigInt): Boolean = {
    n >= this.minInt && n <= this.maxInt
  }

  /** The smallest (i.e., most negative) value that can be represented by this
    * type.
    */
  def minInt: BigInt = {
    this match {
      case TyUInt(_) => 0
      case TySInt(0) => 0
      case TySInt(w) => -BigInt(2).pow(w - 1)
    }
  }

  /** The greatest (i.e., most positive) number that can be represented by this
    * type.
    */
  def maxInt: BigInt = {
    this match {
      case TyUInt(w) => BigInt(2).pow(w) - 1
      case TySInt(0) => 0
      case TySInt(w) => BigInt(2).pow(w - 1) - 1
    }
  }
}

/** Companion object for [[TyAnyInt]].
  */
object TyAnyInt {

  /** Extracts the bit width from this type.
    */
  def unapply(t: TyAnyInt): Option[Int] = Some(t.w)

  /** Finds the smallest type that fits the given range.
    *
    * @param lowerBound
    *   the lower bound of the desired range (inclusive).
    * @param upperBound
    *   the upper bound of the desired range (inclusive).
    */
  def tightest(lowerBound: BigInt, upperBound: BigInt): TyAnyInt = {
    if (lowerBound >= 0) {
      // Result can be unsigned
      TyUInt.tightest(upperBound)
    } else {
      // Result must be signed
      TySInt.tightest(lowerBound, upperBound)
    }
  }
}

/** The type of a signed integer with [[w]] bits.
  *
  * @param w
  *   the bit width.
  */
case class TySInt(override val w: Int) extends TyAnyInt(w) {
  require(w >= 1, s"Invalid width for signed int: $w.")

  override def toString: String = s"i$w"
}

/** Companion object for [[TySInt]].
  */
object TySInt {

  /** Finds the smallest signed type that fits the given range.
    *
    * @param lowerBound
    *   the lower bound of the desired range (inclusive).
    * @param upperBound
    *   the upper bound of the desired range (inclusive).
    */
  private[ir] def tightest(lowerBound: BigInt, upperBound: BigInt): TySInt = {
    val n1 = lowerBound.bitLength
    val n2 = upperBound.bitLength
    TySInt(1 + math.max(n1, n2))
  }
}

/** The type of an unsigned integer with [[w]] bits.
  *
  * @param w
  *   the bit width.
  */
case class TyUInt(override val w: Int) extends TyAnyInt(w) {
  require(w >= 0, s"Invalid width for unsigned int: $w.")

  override def toString: String = s"u$w"
}

/** Companion object for [[TyUInt]].
  */
object TyUInt {

  /** Finds the smallest unsigned type that fits the given range.
    *
    * @param upperBound
    *   the upper bound of the desired range (inclusive).
    */
  private[ir] def tightest(upperBound: BigInt): TyUInt = {
    require(
      upperBound >= 0,
      s"Cannot find unsigned type containing negative value $upperBound."
    )
    TyUInt(upperBound.bitLength)
  }
}

/** The type of a fixed-point number with numerator of type [[t]] and
  * denominator [[2^shift]].
  *
  * @example
  *   Values of type `TyFix(U8, 7)` are represented as unsigned 8-bit integers,
  *   but implicitly divided by `2^7` (i.e., 128). The smallest nonzero number
  *   that can be represented by this type is `1/128` and the largest number
  *   that can be represented by this type is `255/128`.
  *
  * @param t
  *   the type of the numerator.
  * @param shift
  *   the amount by which values of this type are implicitly shifted. In other
  *   words, this is the number of bits to the right of the radix point.
  */
case class TyFix(t: TyUInt, shift: Int) extends Type {
  override def toString: String = s"fix${t.w}_${this.shift}"
}

/** The type of a boolean.
  */
case object TyBool extends Type {
  override def toString: String = "bool"
}

/** The type of a function.
  *
  * @param t1
  *   the input type.
  * @param t2
  *   the output type.
  */
case class TyArrow(t1: Type, t2: Type) extends Type {
  override def toString: String = {
    val shouldParenthesizeInput = t1 match {
      case _: TyArrow => true
      case _          => false
    }
    if (shouldParenthesizeInput) {
      s"($t1) -> $t2"
    } else {
      s"$t1 -> $t2"
    }
  }
}

/** The type of a tuple.
  *
  * @param ts
  *   the element types.
  */
case class TyTuple(ts: Type*) extends Type {
  override def toString: String = {
    ts.map(t => t.toString).mkString("(", ", ", ")")
  }
}

/** The type of a vector.
  *
  * @param t
  *   the type of the elements in the vector.
  * @param n
  *   the length of the vector.
  */
class TyVec(val t: Type, val n: Expr) extends Type {
  require(t != Missing, s"Type in TyVec must not be Missing.")
  require(
    n.hasType,
    s"Length in ${TyVec.getClass.getSimpleName} must have a type."
  )

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: TyVec => this.t == that.t && this.n == that.n
      case _           => false
    }
  }

  override def hashCode(): Int = {
    (this.t, this.n).hashCode()
  }

  override def toString: String = {
    s"Vec[$t, $n]"
  }
}

/** Companion object for [[TyVec]].
  */
object TyVec {

  /** Factory for [[TyVec]].
    */
  def apply(t: Type, n: Expr)(implicit c: Canonicalizer): TyVec = {
    new TyVec(t, c.canonicalize(n))
  }

  def unapply(t: TyVec): Option[(Type, Expr)] = Some(t.t, t.n)
}

/** The type of a stream.
  *
  * @param t
  *   the type of the <i>unwrapped</i> elements produced by the stream. In other
  *   words, this is the type seen by consumers of the stream (usually
  *   <i>not</i> an <code>Option</code> type).
  * @param n
  *   the length of the stream.
  */
class TyStm(val t: Type, val n: Expr) extends Type {
  require(
    n.hasType,
    s"Length in ${TyStm.getClass.getSimpleName} must have a type."
  )

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: TyStm => this.t == that.t && this.n == that.n
      case _           => false
    }
  }

  override def hashCode(): Int = {
    (this.t, this.n).hashCode()
  }

  override def toString: String = {
    s"Stm[$t, $n]"
  }
}

/** Companion object for [[TyStm]].
  */
object TyStm {

  /** Factory for [[TyStm]].
    */
  def apply(t: Type, n: Expr)(implicit c: Canonicalizer): TyStm = {
    new TyStm(t, c.canonicalize(n))
  }

  def unapply(t: TyStm): Option[(Type, Expr)] = Some(t.t, t.n)
}

/** The type of an option (like Scala's [[scala.Option]]).
  */
object TyOption {
  def apply(t: Type): Type = TyTuple(t, TyBool)
}

/** Destructor for data types (as defined by [[Type.isData]]).
  */
object TyData {

  /** Checks whether the given type is data (as defined by [[Type.isData]]).
    */
  def unapply(t: Type): Option[Type] = {
    if (t.isData) {
      Some(t)
    } else {
      None
    }
  }
}

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
