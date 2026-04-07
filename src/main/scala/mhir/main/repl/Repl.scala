package mhir.main.repl

import mhir.ir.Lowering.ExprLowering
import mhir.ir.typecheck.TypeCheck
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

  def run(): Unit = {
    val terminal = TerminalBuilder.builder().build()
    val reader = LineReaderBuilder
      .builder()
      .terminal(terminal)
      .build()
    val writer = terminal.writer()
    run(reader, writer)
  }

  @tailrec
  private def run(reader: LineReader, writer: PrintWriter): Unit = {
    val line =
      try {
        reader.readLine("> ")
      } catch {
        case _: UserInterruptException => ""
        case _: EndOfFileException     => null
      }
    if (line == null) {
      // Exit REPL
    } else if (line.strip().isEmpty) {
      // Wait for next input
      run(reader, writer)
    } else {
      val e = Parser.parse(line)
      val typechecked = e.tchk()
      val lowered = typechecked.lower()
      val result = mhir.ir.eval(lowered)
      writer.println(result)
      run(reader, writer)
    }
  }
}
