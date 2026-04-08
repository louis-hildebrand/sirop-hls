package mhir.main.repl

import mhir.sugar.ExprLowering
import mhir.ir._
import mhir.ir.evaluate.EvalException
import mhir.ir.typecheck.{TypeCheck, TypeError}
import mhir.main.shared.Version
import mhir.parse.SyntaxError
import mhir.parse.sirop.Parser
import org.jline.reader.impl.completer.StringsCompleter
import org.jline.reader.{
  EndOfFileException,
  LineReader,
  LineReaderBuilder,
  UserInterruptException
}
import org.jline.terminal.TerminalBuilder

import java.io.PrintWriter
import scala.annotation.tailrec

/** An interactive read-eval-print loop.
  */
object Repl {

  private val MaxCtrlCCount: Int = 3
  private val GoodbyeMessage: String = "Goodbye\n"

  /** Launches the REPL.
    */
  def run(): Unit = {
    val terminal = TerminalBuilder.builder().dumb(true).build()
    val reader = LineReaderBuilder
      .builder()
      .terminal(terminal)
      .completer(new StringsCompleter("exit"))
      .build()
    val writer = terminal.writer()
    val state = ReplState(ctrlCCount = 0, variables = Map())
    writer.println(s"Welcome to the Sirop REPL (v${Version()})!")
    writer.println(s"Type 'exit' or press Ctrl+D to exit.")
    run(state, reader, writer)
  }

  @tailrec
  private def run(
      state: ReplState,
      reader: LineReader,
      writer: PrintWriter
  ): Unit = {
    val (line, ctrlC, ctrlD) =
      try {
        (reader.readLine("> "), false, false)
      } catch {
        case _: UserInterruptException => ("", true, false)
        case _: EndOfFileException     => ("", false, true)
      }
    if (ctrlD) {
      writer.print(GoodbyeMessage)
      writer.flush()
      // Exit REPL by simply omitting the recursive call to run()
    } else if (ctrlC && state.ctrlCCount + 1 >= MaxCtrlCCount) {
      writer.println("Type 'exit' or press Ctrl+D to exit.")
      run(state.resetCtrlCCount(), reader, writer)
    } else if (ctrlC) {
      run(state.incrementCtrlCCount(), reader, writer)
    } else if (line.strip().isEmpty) {
      run(state.resetCtrlCCount(), reader, writer)
    } else {
      val newState =
        try {
          val s = Parser.parseStmt(line)
          val (newState, exit) = exec(s, state, writer)
          if (exit) {
            writer.print(GoodbyeMessage)
            writer.flush()
            return
          }
          newState
        } catch {
          case ex @ (_: SyntaxError | _: TypeError | _: EvalException) =>
            writer.println(ex.getMessage)
            state
        }
      run(newState, reader, writer)
    }
  }

  /** Executes the given statement.
    *
    * @return
    *   the new state, along with `true` if the REPL should exit and `false`
    *   otherwise.
    */
  private def exec(
      s: Stmt,
      state: ReplState,
      writer: PrintWriter
  ): (ReplState, Boolean) = {
    s match {
      case ExprStmt(e) =>
        writer.println(eval(e, state.variables))
        (state, false)
      case ExitStmt =>
        (state, true)
      case SetStmt(x, e) =>
        val result = eval(e, state.variables)
        val newX = {
          assert(!x.hasType)
          assert(result.hasType)
          x.rebuild(result.typ).asInstanceOf[Param]
        }
        (state.addVar(newX, result), false)
    }
  }

  /** Evaluate the given expression in the given environment.
    *
    * @param e
    *   the expression to evaluate.
    * @param env
    *   the current environment, which maps each variable to its current value.
    */
  private def eval(e: Expr, env: Map[Param, Expr]): Expr = {
    val typingContext = env.map({ case (x, v) => x -> v.typ })
    val subs = env.toMap[Expr, Expr]
    mhir.ir.eval(
      e.tchk(typingContext)
        .subPreserveType(subs)
        .lower()
    )
  }
}
