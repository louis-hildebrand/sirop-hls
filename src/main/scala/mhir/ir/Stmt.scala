package mhir.ir

/** A statement (for the REPL).
  */
sealed trait Stmt

/** A statement that simply consists of an expression.
  */
case class ExprStmt(e: Expr) extends Stmt

/** The "exit" statement.
  */
object ExitStmt extends Stmt

/** Assign an expression to a variable.
  */
case class SetStmt(x: Param, e: Expr) extends Stmt
