package mhir.optimize

import mhir.ir._
import mhir.ir.typecheck.TypeCheck

trait LetStmSimplifier {
  def enabled: Boolean

  def simplify(let: LetStm): Expr
  def simplifyAll(expr: Expr): Expr
}

object LetStmSimplifier {
  def apply(enabled: Boolean = true): LetStmSimplifier = {
    if (enabled) EnabledLetStmSimplifier else DisabledLetStmSimplifier
  }
}

object EnabledLetStmSimplifier extends LetStmSimplifier {
  override def enabled: Boolean = true

  def simplify(let: LetStm): Expr = {
    let.tchk().asInstanceOf[LetStm] match {
      case let @ LetStm(_, x, in, out) =>
        val numUses = out.countFreeOccurrences(x)
        if (numUses <= 0) {
          out
        } else if (numUses <= 1) {
          // Add one cycle of latency so as not to introduce or worsen a latency
          // mismatch between this part of the pipeline and another part
          val nop = {
            val TyStm(typ, n) = in.typ
            val s = Param("s")(TyStm(typ, -1))
            StmBuild(
              n,
              StmData(s)(),
              True,
              Map[Param, (Expr, Expr)](s -> (in, True))
            )().tchk()
          }
          out.subPreserveType(x -> nop)
        } else {
          let
        }
    }
  }

  def simplifyAll(expr: Expr): Expr = {
    val result = expr match {
      case LetStm(bufSize, x, in, out) =>
        simplify(LetStm(bufSize, x, simplifyAll(in), simplifyAll(out))())
      case e => e.map(simplifyAll)
    }
    result.tchk()
  }
}

object DisabledLetStmSimplifier extends LetStmSimplifier {
  override def enabled: Boolean = false

  override def simplify(let: LetStm): Expr = let

  override def simplifyAll(expr: Expr): Expr = expr
}
