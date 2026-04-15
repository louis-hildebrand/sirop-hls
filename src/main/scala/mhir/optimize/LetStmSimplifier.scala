package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck.TypeCheck

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
          out.subPreserveType(x -> in)
        } else {
          let
        }
    }
  }

  def simplifyAll(expr: Expr): Expr = {
    val result = expr match {
      case LetStm(_, x, in, out) =>
        // Removing buffers may introduce latency mismatches between different
        // branches, and therefore the new pipeline may need more buffering.
        val TyStm(_, n) = x.typ
        simplify(LetStm(n, x, simplifyAll(in), simplifyAll(out))())
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
