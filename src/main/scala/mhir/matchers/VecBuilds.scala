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
}
