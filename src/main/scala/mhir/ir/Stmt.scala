package mhir.ir

/** A statement (for the REPL).
  */
sealed trait Stmt

/** Do nothing.
  *
  * This is what the parser emits when the user types a blank line, a line with
  * only a comment, etc.
  */
object NullStmt extends Stmt

/** Evaluate and print an expression.
  */
case class ExprStmt(e: Expr) extends Stmt

/** Exit the REPL.
  */
object ExitStmt extends Stmt

/** Assign an expression to a variable.
  */
case class SetStmt(x: Param, e: Expr) extends Stmt

/** Show the type of the given expression.
  */
case class TypeOfStmt(e: Expr) extends Stmt
