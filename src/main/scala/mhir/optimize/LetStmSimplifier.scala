package mhir.optimize

import mhir.ir._
import mhir.ir.typecheck.TypeCheck

trait LetStmSimplifier {
  def simplify(let: LetStm): Expr
  def simplifyAll(expr: Expr): Expr
}

object LetStmSimplifier {
  def apply(enabled: Boolean = true): LetStmSimplifier = {
    if (enabled) EnabledLetStmSimplifier else DisabledLetStmSimplifier
  }
}

object EnabledLetStmSimplifier extends LetStmSimplifier {
  def simplify(let: LetStm): Expr = {
    let.tchk().asInstanceOf[LetStm] match {
      case let @ LetStm(x, in, out) =>
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
      case LetStm(x, in, out) =>
        simplify(LetStm(x, simplifyAll(in), simplifyAll(out))())
      case e => e.map(simplifyAll)
    }
    result.tchk()
  }
}

object DisabledLetStmSimplifier extends LetStmSimplifier {
  override def simplify(let: LetStm): Expr = let

  override def simplifyAll(expr: Expr): Expr = expr
}
