package mhir

import mhir.ir.{Canonicalizer, Expr}

package object canonicalize {

  implicit val standardCanonicalizer: Canonicalizer = SimplifyingCanonicalizer

  /** Conservatively check whether two lengths (e.g., vector sizes) are equal.
    *
    * If the result is <code>true</code> then the lengths are definitely equal,
    * but if the result is <code>False</code> then they may or may not be equal.
    */
  def sameLen(n: Expr, m: Expr): Boolean = standardCanonicalizer.sameLen(n, m)
}
