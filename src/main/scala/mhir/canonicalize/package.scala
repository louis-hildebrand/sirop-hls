package mhir

import mhir.ir.{Canonicalizer, Expr}

package object canonicalize {

  implicit val standardCanonicalizer: Canonicalizer = SimplifyingCanonicalizer
}
