package mhir.main.repl

import mhir.ir.Lowering.ExprLowering
import mhir.ir.evaluate.EvalException
import mhir.ir.typecheck.{TypeCheck, TypeError}
import mhir.ir._
import mhir.main.shared.Version
import mhir.parse.SyntaxError
import mhir.parse.sirop.Parser
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

  /** Launches the REPL.
    */
  def run(): Unit = {
    val terminal = TerminalBuilder.builder().build()
    val reader = LineReaderBuilder
      .builder()
      .terminal(terminal)
      .build()
    val writer = terminal.writer()
    val state = ReplState()
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
      // Exit REPL
    } else if (ctrlC && state.ctrlCCount + 1 >= MaxCtrlCCount) {
      writer.println("Type 'exit' or press Ctrl+D to exit.")
      run(state.resetCtrlCCount(), reader, writer)
    } else if (ctrlC) {
      run(state.incrementCtrlCCount(), reader, writer)
    } else if (line.strip().isEmpty) {
      // Wait for next input
      run(state.resetCtrlCCount(), reader, writer)
    } else {
      try {
        val s = Parser.parseStmt(line)
        val exit = exec(s, writer)
        if (exit) {
          return
        }
      } catch {
        case ex @ (_: SyntaxError | _: TypeError | _: EvalException) =>
          writer.println(ex.getMessage)
      }
      run(state.resetCtrlCCount(), reader, writer)
    }
  }

  /** Executes the given statement.
    *
    * @return
    *   `true` if the REPL should exit.
    */
  private def exec(s: Stmt, writer: PrintWriter): Boolean = {
    s match {
      case ExitStmt =>
        return true
      case ExprStmt(e) =>
        val typechecked = e.tchk()
        val lowered = typechecked.lower()
        val result = mhir.ir.eval(lowered)
        writer.println(result)
    }
    false
  }
}
