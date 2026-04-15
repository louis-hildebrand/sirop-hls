package mhir.sugar
package experimental

import mhir.ir._
import mhir.typecheck._

/** Sequential fold over a vector.
  *
  * This produces a stream of length one containing the result, since it may
  * take multiple steps.
  *
  * @param v
  *   the vector to fold over.
  * @param z
  *   the initial value.
  * @param f
  *   the function to use for folding.
  */
case class VecFoldSeq(
    v: Expr /* Vec<T1; n> */,
    z: Expr /* T2 */,
    f: Function /* T2 -> T1 -> T2 */
)(typ: Type = Missing) /* Stm<T2; 1> */
    extends SyntaxSugar(v, z, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v, z, f: Function) => VecFoldSeq(v, z, f)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newV = v.tchk(context)
    val t1 = newV.typ match {
      case TyVec(t, _) => t
      case t           => throw new TypeError(s"Vector in VecFold has type $t.")
    }
    val newZ = z.tchk(context)
    val t2 = newZ.typ
    val newF = f.tchk(context)
    newF.typ match {
      case TyArrow(t3, TyArrow(t4, t5))
          if (t3 ~= t1) && (t4 ~= t2) && (t5 ~= t2) =>
        ()
      case t =>
        throw new TypeError(
          s"Function in VecFold has type $t. Expected ${TyArrow(t1, TyArrow(t2, t2))}."
        )
    }
    this.rebuild(TyStm(t2, 1), Seq(newV, newZ, newF))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    StmFold(Vec2Stm(v)(), z, f)().tchk().lower
  }
}

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
