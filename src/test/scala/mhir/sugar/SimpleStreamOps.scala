package mhir.sugar

import mhir.ir.Lowering.ExprLowering
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

import scala.annotation.tailrec

/*

This file contains simple implementations of common streaming primitives.
They are typically more restrictive than the "real" ones: their inputs must
already be type checked and lowered, they may only apply to 1D streams, etc.
This is useful when you want to describe a high-level streaming program but
don't want all the complexity of the "real" implementations.
For example, StmMap is a total mess after lowering because it must handle the
general case where streams may be nested, you may have vectors of streams, etc.

!!!!! IMPORTANT !!!!!

Tests may rely on the exact implementation of these operations.
Don't change them without checking where they are used.
And prioritize simplicity over generality.

 */

object SimpleCount {
  def apply(n: IntCst): Expr = {
    val i = Param("i")(n.typ)
    StmBuild(
      n,
      i,
      True,
      Map[Param, (Expr, Expr)](i -> (C(0)(n.typ), Sum(C(1)(n.typ), i)()))
    )().tchk().lower()
  }
}

object SimpleMap {
  def apply(input: Expr, f: Expr => Expr): Expr = {
    val TyStm(t, n) = input.typ
    val sAcc = Param("s")(TyStm(t, -1))
    StmBuild(
      n,
      f(StmData(sAcc)()),
      True,
      Map[Param, (Expr, Expr)](
        sAcc -> (input, True)
      )
    )().tchk().lower()
  }
}

object SimpleNop {
  @tailrec
  def apply(s: Expr, delay: Int = 1): Expr = {
    if (delay == 0) {
      s
    } else {
      SimpleNop(SimpleMap(s, x => x), delay - 1)
    }
  }
}

object SimpleZip {
  def apply(inputs: Expr*): Expr = {
    val accumulators = inputs.zipWithIndex.map({ case (in, i) =>
      val TyStm(t, _) = in.typ
      Param(s"s$i")(TyStm(t, -1))
    })
    val TyStm(_, n) = inputs.head.typ
    StmBuild(
      n,
      Tuple(accumulators.map(StmData(_)()): _*)(),
      True,
      accumulators
        .zip(inputs)
        .map({ case (acc, in) => acc -> (in, True) })
        .toMap
    )().tchk().lower()
  }
}
