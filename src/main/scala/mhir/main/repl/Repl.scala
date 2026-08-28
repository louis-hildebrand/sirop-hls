package mhir.main.repl

import mhir.canonicalize._
import mhir.eval.EvalException
import mhir.ir._
import mhir.main.shared.Version
import mhir.optimize.{
  EnabledLatencyMatcher,
  LatencyAnalysis,
  StaticLetStmBufferShrinker
}
import mhir.parse.SyntaxError
import mhir.parse.sirop.Parser
import mhir.sugar.ExprLowering
import mhir.typecheck.{NameError, TypeCheck, TypeError}
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
    val state = ReplState(
      handshake = true,
      showPhysical = false,
      ctrlCCount = 0,
      variables = Map()
    )
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
          val s = Parser.parseStmt(line, state.typingContext)
          val (newState, exit) = exec(s, state, writer)
          if (exit) {
            writer.print(GoodbyeMessage)
            writer.flush()
            return
          }
          newState
        } catch {
          case ex @ (_: SyntaxError | _: TypeError | _: EvalException |
              _: NameError) =>
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
        val result = eval(e, state)
        writer.println(ExprPrinter.display(result))
        (state, false)
      case ExitStmt =>
        (state, true)
      case SetStmt(x, e) if x.name.startsWith("__") =>
        (updateSetting(state, x.name, e), false)
      case SetStmt(x, e) =>
        val result = eval(e, state)
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
    */
  private def eval(e: Expr, state: ReplState): Expr = {
    val checked = e.tchk(state.typingContext, state.env)
    val subs = state.env.toMap[Expr, Expr]
    val lowered = checked.subPreserveType(subs).lower
    val isStream = lowered.typ.isInstanceOf[TyStm]
    val optimized = if (!state.handshake && isStream) {
      // Certain transformations are needed when the handshake protocol is
      // disabled, namely latency matching and letstm buffer shrinking
      val latencyAnalysis = new LatencyAnalysis(handshake = state.handshake)
      val latencyMatcher =
        new EnabledLatencyMatcher(latencyAnalysis, handshake = state.handshake)
      val headByParam = state.env
        .filter({ case (x, _) => x.typ.isInstanceOf[TyStm] })
        .map({
          case (x, StmLiteral(Seq(head, _*), _)) => x -> head
          case (x, _)                            => x -> Undefined(Missing)
        })
      val afterLatencyMatching =
        latencyMatcher.matchLatencies(lowered, headByParam)
      val letBufShrinker = new StaticLetStmBufferShrinker(
        latencyAnalysis,
        handshake = state.handshake,
        assumeThroughputsMatch = false
      )
      letBufShrinker.shrinkBuffers(afterLatencyMatching)
    } else {
      lowered
    }
    val result = mhir.eval.eval(optimized, handshake = state.handshake)
    if (state.showPhysical) {
      result
    } else {
      result.dropPhysicalPrefix
    }
  }

  private def updateSetting(
      state: ReplState,
      setting: String,
      newValue: Expr
  ): ReplState = {
    setting match {
      case "__handshake" =>
        eval(newValue, state) match {
          case True =>
            mhir.ir.globalOptions = mhir.ir.globalOptions.copy(handshake = true)
            state.copy(handshake = true)
          case False =>
            mhir.ir.globalOptions =
              mhir.ir.globalOptions.copy(handshake = false)
            state.copy(handshake = false)
          case v =>
            throw new TypeError(
              s"value of __handshake evaluated to $v." +
                s" Expected true or false."
            )
        }
      case "__show_physical" =>
        eval(newValue, state) match {
          case True  => state.copy(showPhysical = true)
          case False => state.copy(showPhysical = false)
          case v =>
            throw new TypeError(
              s"value of __show_physical evaluated to $v." +
                s" Expected true or false."
            )
        }
      case setting =>
        throw NameError(
          s"unknown setting '$setting'."
            + " (Note that names starting with two underscores are reserved for REPL settings.)"
        )
    }
  }
}
