package mhir.gen.vhdl.ir

import mhir.ir._

import scala.collection.immutable.ListMap

/** A variant of [[mhir.ir.StmBuild]] that's more suitable for VHDL generation.
  *
  * @param data
  *   the `data` output expression.
  * @param valid
  *   the `valid` output expression.
  * @param accumulators
  *   the set of accumulators, each of which will generate a register. Each
  *   accumulator has a name, initial value, and an expression to calculate the
  *   next value.
  * @param producers
  *   the producer streams that feed into this component. Each producer has a
  *   name and a `ready` expression. The same name is used outside this `sbuild`
  *   (in the top-level component) and inside this sbuild (as an argument for
  *   `sdata`).
  * @param intermediates
  *   intermediate signals defined in this component. Each one has a name and an
  *   expression that calculates the value of the intermediate using the
  *   accumulators, producers, and other intermediates in this component.
  */
case class GenStmBuild(
    data: Expr,
    valid: Expr,
    accumulators: Map[Param, Accumulator],
    producers: Map[Param, Expr],
    intermediates: ListMap[Param, Intermediate]
)
