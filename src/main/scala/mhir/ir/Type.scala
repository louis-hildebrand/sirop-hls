package mhir.ir

import mhir.opt.{PartialEvalPass => PE}

sealed trait Type {
  def ::+(f: Param => Expr): Function = {
    val x = Param("x")(this)
    val body = f(x)
    val t =
      if (body.typ != Missing) TyArrow(this, body.typ)
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

  def substitute(subs: Map[Expr, Expr]): Type = {
    this match {
      case TyVec(t, n)     => TyVec(t.substitute(subs), n.subPreserveType(subs))
      case TyStm(t, n)     => TyStm(t.substitute(subs), n.subPreserveType(subs))
      case TyArrow(t1, t2) => TyArrow(t1.substitute(subs), t2.substitute(subs))
      case TyTuple(ts @ _*) => TyTuple(ts.map(t => t.substitute(subs)): _*)
      case TySInt(w)        => TySInt(w)
      case TyUInt(w)        => TyUInt(w)
      case t @ (Missing | TyBool) => t
    }
  }

  def substitute(sub: (Expr, Expr)): Type = substitute(Map(sub))

  def lower: Type = {
    this match {
      case Missing | TyBool => this
      case TySInt(w)        => TySInt(w)
      case TyUInt(w)        => TyUInt(w)
      case TyArrow(t1, t2)  => TyArrow(t1.lower, t2.lower)
      case TyTuple(ts @ _*) => TyTuple(ts.map(t => t.lower): _*)
      case TyVec(t, n) =>
        val newN = n.lower()
        t.lower match {
          case TyStm(t, m) => TyStm(TyVec(t, newN), m)
          case t           => TyVec(t, newN)
        }
      case TyStm(t, n) =>
        t.lower match {
          case TyStm(t, m) => TyStm(t, SafeProd(n, m)().tchk().lower())
          case t           => TyStm(t, n)
        }
    }
  }

  def uncurry: Type = {
    this match {
      case Missing | TyBool => this
      case TySInt(w)        => TySInt(w)
      case TyUInt(w)        => TyUInt(w)
      case TyArrow(tIn, tOut: TyArrow) =>
        tOut.uncurry.asInstanceOf[TyArrow] match {
          case TyArrow(t1, t2) => TyArrow(TyTuple(tIn.uncurry, t1), t2)
        }
      case TyArrow(t1, t2)  => TyArrow(t1.uncurry, t2.uncurry)
      case TyTuple(ts @ _*) => TyTuple(ts.map(t => t.uncurry): _*)
      case TyVec(t, n)      => TyVec(t.uncurry, n)
      case TyStm(t, n)      => TyStm(t.uncurry, n)
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
      case (TyArrow(t1, t2), TyArrow(t3, t4)) =>
        (t1 ~= t3) && (t2 ~= t4)
      case (TyTuple(ts1 @ _*), TyTuple(ts2 @ _*)) =>
        (ts1.length == ts2.length
        && ts1
          .zip(ts2)
          .forall({ case (t1, t2) => t1 ~= t2 }))
      case (TyVec(t1, n1), TyVec(t2, n2)) =>
        // TODO: Improve check for equality of lengths?
        (t1 ~= t2) && Type.sameLen(n1, n2)
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
      case _: TyAnyInt | TyBool            => true
      case TyTuple(ts @ _*)                => ts.forall(t => t.isData)
      case TyVec(t, n)                     => t.isData
      case Missing | _: TyArrow | _: TyStm => false
    }
  }
}

object Type {

  /** Conservatively whether two lengths (e.g., vector sizes) are equal. If the
    * result is <code>true</code> then the lengths are definitely equal, but if
    * the result is <code>False</code> then they may or may not be equal.
    */
  def sameLen(e1: Expr, e2: Expr): Boolean = {
    PE.isEqual(e1.tchk().lower(), e2.tchk().lower())().getOrElse(false)
  }
}

case object Missing extends Type

/** Some custom integer with [[w]] bits.
  *
  * @param w
  *   the bit width.
  */
sealed abstract class TyAnyInt(val w: Int) extends Type {
  require(w >= 0, "Bit width must be non-negative.")

  def +(that: TyAnyInt): TyAnyInt = TSum(this, that)
  def *(that: TyAnyInt): TyAnyInt = TProd(this, that)
  def /(that: TyAnyInt): TyAnyInt = TDiv(this, that)
  def %(that: TyAnyInt): TyAnyInt = TMod(this, that)

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

object TyAnyInt {

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
case class TyVec(t: Type, n: Expr) extends Type {
  require(
    n.hasType,
    s"Length in ${TyVec.getClass.getSimpleName} must have a type."
  )

  override def toString: String = {
    s"Vec[$t, $n]"
  }
}

object TyVec {
  def apply(t: Type, n: Expr): Type = new TyVec(t, n.tchk())
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
case class TyStm(t: Type, n: Expr) extends Type {
  require(
    n.hasType,
    s"Length in ${TyStm.getClass.getSimpleName} must have a type."
  )

  override def toString: String = {
    s"Stm[$t, $n]"
  }
}

object TyStm {
  def apply(t: Type, n: Expr): Type = new TyStm(t, n.tchk())
}

// Syntax sugar for types

case object TyOption {
  def apply(t: Type): Type = TyTuple(t, TyBool)
}
