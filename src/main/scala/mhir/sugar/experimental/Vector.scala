package mhir.sugar
package experimental

import mhir.ir._
import mhir.typecheck._

/** Sequential, inclusive scan over a vector.
  */
object VecScanInclusive {
  def apply(
      v: Expr /* Vec<A; n> */,
      z: Expr /* B */,
      f: Function /* B -> A -> B */
  ): Expr /* Stm<Vec<B; n>; 1> */ = {
    Stm2Vec(StmScanInclusive(Vec2Stm(v)(), z, f)())()
  }
}

/** Sequential, exclusive scan over a vector.
  */
object VecScanExclusive {
  def apply(
      v: Expr /* Vec<A; n> */,
      z: Expr /* B */,
      f: Function /* B -> A -> B */
  ): Expr /* Stm<Vec<B; n>; 1> */ = {
    Stm2Vec(StmScanExclusive(Vec2Stm(v)(), z, f)())()
  }
}
