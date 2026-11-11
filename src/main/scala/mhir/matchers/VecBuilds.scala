package mhir.matchers

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._

object VecBuilds {

  def apply(lengths: Seq[Expr], indices: Seq[Param], body: Expr): Expr = {
    require(
      lengths.length == indices.length,
      s"length and index lists differ in size: ${lengths.length} vs ${indices.length}"
    )
    lengths
      .zip(indices)
      .foldRight(body)({ case ((n, i), e) => VecBuild(n, Function(i, e)())() })
      .tchk()
  }

  def unapply(e: Expr): Option[(Seq[Int], Seq[Param], Expr)] = {
    e match {
      case VecBuild(
            IntCst(n),
            Function(i, VecBuilds(otherLengths, otherVars, body))
          ) =>
        Some((n.toInt +: otherLengths, i +: otherVars, body))
      case e =>
        Some((Seq.empty, Seq.empty, e))
    }
  }
}
