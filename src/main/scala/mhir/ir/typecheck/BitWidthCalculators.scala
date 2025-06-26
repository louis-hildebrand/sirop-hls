package mhir.ir
package typecheck

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
