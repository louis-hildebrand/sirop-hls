package ir

import opt.PartialEvalPass

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
      case t @ (Missing | TyInt | TyBool) => t
    }
  }

  def substitute(sub: (Expr, Expr)): Type = substitute(Map(sub))

  def lower: Type = {
    this match {
      case Missing | TyInt | TyBool => this
      case TySInt(w)                => TySInt(w)
      case TyUInt(w)                => TyUInt(w)
      case TyArrow(t1, t2)          => TyArrow(t1.lower, t2.lower)
      case TyTuple(ts @ _*)         => TyTuple(ts.map(t => t.lower): _*)
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
      case Missing | TyInt | TyBool => this
      case TySInt(w)                => TySInt(w)
      case TyUInt(w)                => TyUInt(w)
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
  @deprecated
  def isCompatibleWith(that: Type): Boolean = {
    (this, that) match {
      case (TyBool, TyBool)         => true
      case (TyInt, TyInt)           => true
      case (TySInt(w1), TySInt(w2)) => w1 == w2
      case (TyUInt(w1), TyUInt(w2)) => w1 == w2
      case (TyArrow(t1, t2), TyArrow(t3, t4)) =>
        t1.isCompatibleWith(t3) && t2.isCompatibleWith(t4)
      case (TyTuple(ts1 @ _*), TyTuple(ts2 @ _*)) =>
        (ts1.length == ts2.length
        && ts1
          .zip(ts2)
          .forall({ case (t1, t2) => t1.isCompatibleWith(t2) }))
      case (TyVec(t1, n1), TyVec(t2, n2)) =>
        // TODO: Improve check for equality of lengths?
        t1.isCompatibleWith(t2) && Type.sameLen(n1, n2)
      case (TyStm(t1, _), TyStm(t2, _)) =>
        // Two streams are compatible even if they have different lengths!
        t1.isCompatibleWith(t2)
      case _ => false
    }
  }

  /** Check whether two types are "compatible" with one another, i.e., will have
    * the same shape in hardware.
    */
  def ~=(that: Type): Boolean = this.isCompatibleWith(that)

  /** Checks whether this type is a subtype of another type—that is, whether an
    * expression of this type can be used in place of an expression of the given
    * type.
    *
    * For data types, this tells you whether this type is "narrower than"
    * another. For example, u8 is narrower than u16 and u8 is also narrower than
    * i16. Likewise, Vec[u8, n] is narrower than Vec[u16, n]. However, bool is
    * not narrower than u8 because they are unrelated types.
    *
    * @param that
    *   the type to compare with.
    */
  def <=(that: Type): Boolean = {
    (this, that) match {
      case (TyBool, TyBool)         => true
      case (TyUInt(w1), TyUInt(w2)) => w1 <= w2
      case (TyUInt(w1), TySInt(w2)) => (w1 < w2) || (w1 == 0 && w2 == 0)
      case (TySInt(w), _: TyUInt)   => w == 0
      case (TySInt(w1), TySInt(w2)) => w1 <= w2
      case (TyTuple(ts1 @ _*), TyTuple(ts2 @ _*)) =>
        // Tuples are covariant
        (ts1.length == ts2.length
        && ts1.zip(ts2).forall({ case (t1, t2) => t1 <= t2 }))
      case (TyVec(t1, n1), TyVec(t2, n2)) =>
        // Vectors are covariant
        // TODO: Actually check that n1 <= n2?
        t1 <= t2 && Type.sameLen(n1, n2)
      case (TyStm(t1, _), TyStm(t2, _)) =>
        // Streams are covariant
        // TODO: Also check that n1 >= n2?
        t1 <= t2
      case (TyArrow(t11, t12), TyArrow(t21, t22)) =>
        // Functions are contravariant in the input and covariant in the output
        t21 <= t11 && t12 <= t22
      case _ => false
    }
  }
}

object Type {

  /** Finds the narrowest common supertype of two types.
    *
    * @return
    *   the narrowest type that is a supertype of both `t1` and `t2`, or
    *   [[None]] if no such type exists.
    */
  def supertype(t1: Type, t2: Type): Option[Type] = {
    (t1, t2) match {
      case (TyBool, TyBool)         => Some(TyBool)
      case (TyUInt(w1), TyUInt(w2)) => Some(TyUInt(math.max(w1, w2)))
      case (u: TyUInt, TySInt(0))   => Some(u)
      case (TySInt(0), u: TyUInt)   => Some(u)
      case (TyUInt(w1), TySInt(w2)) => Some(TySInt(math.max(w1 + 1, w2)))
      case (TySInt(w1), TyUInt(w2)) => Some(TySInt(math.max(w1, w2 + 1)))
      case (TySInt(0), TySInt(0))   => Some(U0)
      case (TySInt(w1), TySInt(w2)) => Some(TySInt(math.max(w1, w2)))
      case (TyTuple(ts1 @ _*), TyTuple(ts2 @ _*)) if ts1.length == ts2.length =>
        val elemTypeOptions = ts1
          .zip(ts2)
          .map({ case (t1, t2) => supertype(t1, t2) })
        if (elemTypeOptions.forall(x => x.isDefined)) {
          Some(TyTuple(elemTypeOptions.map(x => x.get): _*))
        } else {
          None
        }
      case (TyVec(t1, n1), TyVec(t2, n2)) if Type.sameLen(n1, n2) =>
        supertype(t1, t2) match {
          case Some(t) => Some(TyVec(t, n1))
          case None    => None
        }
      case (TyStm(t1, n1), TyStm(t2, _)) =>
        // TODO: What to do about the length here?
        supertype(t1, t2) match {
          case Some(t) => Some(TyStm(t, n1))
          case None    => None
        }
      case (TyArrow(t11, t12), TyArrow(t21, t22)) =>
        (subtype(t11, t21), supertype(t12, t22)) match {
          case (Some(t1), Some(t2)) => Some(t1 ->: t2)
          case _                    => None
        }
      case _ => None
    }
  }

  /** Finds the narrowest common supertype of many types.
    *
    * @return
    *   the narrowest type that is a supertype of all the given types, or
    *   [[None]] if no such type exists.
    */
  def supertype(ts: Seq[Type]): Option[Type] = {
    require(ts.nonEmpty)
    ts.tail.foldLeft[Option[Type]](Some(ts.head))({ case (acc, t) =>
      acc match {
        case None      => None
        case Some(acc) => supertype(acc, t)
      }
    })
  }

  /** Finds the widest common subtype of two types.
    *
    * @return
    *   the widest type that is a subtype of both `t1` and `t2`, or [[None]] if
    *   no such type exists.
    */
  def subtype(t1: Type, t2: Type): Option[Type] = {
    (t1, t2) match {
      case (TyBool, TyBool)         => Some(TyBool)
      case (TyUInt(w1), TyUInt(w2)) => Some(TyUInt(math.min(w1, w2)))
      case (TyUInt(w1), TySInt(w2)) =>
        Some(TyUInt(math.max(0, math.min(w1, w2 - 1))))
      case (TySInt(w1), TyUInt(w2)) =>
        Some(TyUInt(math.max(0, math.min(w1 - 1, w2))))
      case (TySInt(0), TySInt(0))   => Some(U0)
      case (TySInt(w1), TySInt(w2)) => Some(TySInt(math.min(w1, w2)))
      case (TyTuple(ts1 @ _*), TyTuple(ts2 @ _*)) if ts1.length == ts2.length =>
        val elemTypeOptions = ts1
          .zip(ts2)
          .map({ case (t1, t2) => subtype(t1, t2) })
        if (elemTypeOptions.forall(x => x.isDefined)) {
          Some(TyTuple(elemTypeOptions.map(x => x.get): _*))
        } else {
          None
        }
      case (TyVec(t1, n1), TyVec(t2, n2)) if Type.sameLen(n1, n2) =>
        subtype(t1, t2) match {
          case Some(t) => Some(TyVec(t, n1))
          case None    => None
        }
      case (TyStm(t1, n1), TyStm(t2, _)) =>
        // TODO: What to do about the length here?
        subtype(t1, t2) match {
          case Some(t) => Some(TyStm(t, n1))
          case None    => None
        }
      case (TyArrow(t11, t12), TyArrow(t21, t22)) =>
        (supertype(t11, t21), subtype(t12, t22)) match {
          case (Some(t1), Some(t2)) => Some(t1 ->: t2)
          case _                    => None
        }
      case _ => None
    }
  }

  /** Conservatively whether two lengths (e.g., vector sizes) are equal. If the
    * result is <code>true</code> then the lengths are definitely equal, but if
    * the result is <code>False</code> then they may or may not be equal.
    */
  def sameLen(e1: Expr, e2: Expr): Boolean = {
    val e1Normalized = normalizeLen(e1)
    val e2Normalized = normalizeLen(e2)
    e1Normalized == e2Normalized
  }

  private def normalizeLen(e: Expr): Expr = {
    val norm = e match {
      case VecLength(v) =>
        v.typ match {
          case TyVec(_, n) => normalizeLen(n)
          // TODO: It is very sketchy to have the type checker depend on an
          //       optimization pass
          case _ => PartialEvalPass.partialEval(e)
        }
      case e => PartialEvalPass.partialEval(e)
    }
    if (norm == e) {
      e
    } else {
      normalizeLen(norm)
    }
  }
}

case object Missing extends Type
@deprecated
case object TyInt extends Type

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
      case _: TySInt => TySInt(w)
      case _: TyUInt => TyUInt(w)
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

  /** Find the smallest type that fits the given range.
    *
    * @param lowerBound
    *   the lower bound of the desired range (inclusive).
    * @param upperBound
    *   the upper bound of the desired range (inclusive).
    */
  def tightest(lowerBound: BigInt, upperBound: BigInt): TyAnyInt = {
    if (lowerBound >= 0) {
      // Result can be unsigned
      TyUInt(upperBound.bitLength)
    } else {
      // Result must be signed
      val n1 = lowerBound.bitLength
      val n2 = upperBound.bitLength
      TySInt(1 + math.max(n1, n2))
    }
  }
}

/** The type of a signed integer with [[w]] bits.
  *
  * @param w
  *   the bit width.
  */
case class TySInt(override val w: Int) extends TyAnyInt(w) {
  override def toString: String = s"i$w"
}

/** The type of an unsigned integer with [[w]] bits.
  *
  * @param w
  *   the bit width.
  */
case class TyUInt(override val w: Int) extends TyAnyInt(w) {
  override def toString: String = s"u$w"
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

  /** If this is a stream of type <code>Stm&lt;T; n&gt;</code>, then
    * <code>tOpt</code> is <code>Option&lt;T&gt;</code>. That is,
    * <code>tOpt</code> is the type of the output expression inside the
    * <code>StmBuild</code> itself.
    */
  val tOpt: Type = TyOption(t)

  override def toString: String = {
    s"Stm[$t, $n]"
  }
}

object TyStm {
  def apply(t: Type, n: Expr): Type = new TyStm(t, n.tchk())
}

// Shorthand for common int types

trait CommonIntTypes {

  /** The type of a 0-bit unsigned number (i.e., a number that can only be
    * zero).
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

  /** The type of a 0-bit signed number (i.e., a number that can only be zero).
    */
  val I0: TySInt = TySInt(0)

  /** The type of an 8-bit signed integer.
    */
  val I8: TySInt = TySInt(8)

  /** The type of a 9-bit signed integer.
    */
  val I9: TySInt = TySInt(9)

  /** The type of a 16-bit signed integer.
    */
  val I16: TySInt = TySInt(16)

  /** The type of a 32-bit signed integer.
    */
  val I32: TySInt = TySInt(32)

  /** Common integer types.
    */
  val COMMON_INT_TYPES: Seq[TyAnyInt] = Seq(U8, U16, U32, I8, I16, I32)
}

// Bit width computations

// TODO: Delete these?

/** Computes the narrowest type that can represent the result of a sum without
  * overflow.
  */
case object TSum {

  /** Computes the type of the result of a sum.
    *
    * @param ts
    *   the types of the operands.
    */
  def apply(ts: TyAnyInt*): TyAnyInt = {
    val lowerBound = ts.map(t => t.minInt).sum
    val upperBound = ts.map(t => t.maxInt).sum
    TyAnyInt.tightest(lowerBound, upperBound)
  }
}

/** Computes the narrowest type that can represent the result of a product
  * without overflow.
  */
case object TProd {

  /** Computes the type of the result of a product.
    *
    * @param ts
    *   the types of the operands.
    */
  def apply(ts: TyAnyInt*): TyAnyInt = {
    ts.toList match {
      case Seq() => TyUInt(1)
      case t :: ts =>
        val (lowerBound, upperBound) = ts.foldLeft((t.minInt, t.maxInt))({
          case ((lo, hi), t) =>
            assert(lo <= 0)
            assert(hi >= 0)
            assert(t.minInt <= 0)
            assert(t.maxInt >= 0)
            val newLo = (lo * t.maxInt).min(hi * t.minInt)
            val newHi = (lo * t.minInt).max(hi * t.maxInt)
            (newLo, newHi)
        })
        TyAnyInt.tightest(lowerBound, upperBound)
    }
  }
}

/** Computes the narrowest type that can represent the result of a division
  * without overflow.
  */
case object TDiv {

  /** Computes the type of the result of a division.
    *
    * @param t1
    *   the type of the numerator.
    * @param t2
    *   the type of the denominator.
    * @return
    */
  def apply(t1: TyAnyInt, t2: TyAnyInt): TyAnyInt = {
    t2 match {
      case t if t.w == 0 =>
        throw new ArithmeticException(
          s"Denominator of div is guaranteed to be zero since its bit width is zero."
        )
      case _: TySInt =>
        // Cases:
        //  (1) Divide by  0: not allowed
        //  (2) Divide by  1: leaves range unchanged
        //  (3) Divide by -1: flip range
        //  (4) Divide by something with larger magnitude: only shrinks the range
        val lowerBound = t1.minInt.min(-t1.maxInt)
        val upperBound = t1.maxInt.max(-t1.minInt)
        TyAnyInt.tightest(lowerBound, upperBound)
      case _: TyUInt =>
        // Cases:
        //  (1) Divide by zero: not allowed
        //  (2) Divide by one: leaves range unchanged
        //  (3) Divide by something larger: only shrinks the range
        t1
    }
  }
}

/** Computes the narrowest type that can represent the result of the [[Mod]]
  * operation without overflow.
  */
case object TMod {

  /** Computes the type of the result of the [[Mod]] operation.
    *
    * @param t1
    *   the type of the numerator.
    * @param t2
    *   the type of the denominator.
    */
  def apply(t1: TyAnyInt, t2: TyAnyInt): TyAnyInt = {
    if (t2.w == 0) {
      throw new ArithmeticException(
        s"Denominator of mod is guaranteed to be zero since its bit width is zero."
      )
    }
    // The sign is determined by the sign of the numerator
    val lowerBound = t1 match {
      case _: TyUInt =>
        // Numerator is non-negative, so result will be non-negative
        BigInt(0)
      case _: TySInt =>
        // Numerator may be negative, so the result may be negative
        // The magnitude will certainly not exceed the magnitude of the
        // numerator and will be strictly less than that of the denominator
        val magnitude = t1.minInt.abs.min(t2.maxInt.max(t2.minInt.abs) - 1)
        -magnitude
    }
    // Likewise, if the numerator is positive, the result will be no more than
    // the numerator and strictly less than the denominator
    val upperBound = t1.maxInt.min(t2.maxInt.max(t2.minInt.abs) - 1)
    TyAnyInt.tightest(lowerBound, upperBound)
  }
}

// Syntax sugar for types

case object TyOption {
  def apply(t: Type): Type = TyTuple(t, TyBool)
}
@deprecated
case object Int2 {
  def apply(): Type = TyTuple(TyInt, TyInt)
}
