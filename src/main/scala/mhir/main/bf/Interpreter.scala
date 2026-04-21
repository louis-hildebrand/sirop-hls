package mhir.main.bf

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar.ExprLowering
import mhir.typecheck.TypeCheck

/** An interpreter for the Brainfuck programming language
  * (https://brainfuck.org/).
  *
  * @param tapeLength
  *   the number of cells in the data array.
  * @param programLength
  *   the number of characters in the Brainfuck program.
  * @param inputLength
  *   the number of characters in the input.
  * @param maxOutputLength
  *   the maximum length of the output. The program <i>must</i> produce at least
  *   this many characters of output.
  */
class Interpreter(
    val tapeLength: Int,
    val programLength: Int,
    val inputLength: Int,
    val maxOutputLength: Int
) {

  private val f = this.makeCpuExpr()

  /** Run the given program with the given input and return the output.
    *
    * @param program
    *   the Brainfuck program to run. This must only contain ASCII characters.
    * @param input
    *   the input to the program. This must only contain ASCII characters.
    * @return
    *   all output produced by the program, up to the maximum output length.
    */
  def run(program: String, input: String): String = {
    require(
      program.toCharArray.forall(c => c.toInt <= 127),
      "Program must only use ASCII characters."
    )
    require(
      input.toCharArray.forall(c => c.toInt <= 127),
      "Input must only use ASCII characters."
    )
    // TODO: Automatically pad if the actual program/input length is less than
    //       the provided number?
    require(
      program.length == this.programLength,
      s"Expected program of length ${this.programLength}, got program of length ${program.length}."
    )
    require(
      input.length == this.inputLength,
      s"Expected input of length ${this.inputLength}, got input of length ${input.length}."
    )
    val cmdLen = program.length
    val cmdStream =
      StmLiteral(program.toCharArray.map(n => C(n)(U8)): _*)(TyStm(U8, cmdLen))
    val inLen = input.length
    val inStream =
      StmLiteral(input.toCharArray.map(n => C(n)(U8)): _*)(TyStm(U8, inLen))
    val outputStreamSimplified =
      mhir.optimize.PartialEvalPass.partialEval(this.f(cmdStream)(inStream))
    val outputStream =
      mhir.eval.eval(outputStreamSimplified).asInstanceOf[StmLiteral]
    outputStream.elems
      .map(_.asInstanceOf[IntCst])
      .map({ case IntCst(c) => c.toChar })
      .mkString("")
  }

  // TODO: use memory instead of a vector
  //        * It seems unreasonable to have 30,000 registers on an actual FPGA
  // TODO: use unbounded streams rather than fixed-length streams
  //        * This would remove the need for the maxOutputLength
  //        * This would make it possible to split the stream into a control
  //          stream (which produces a stream of commands not including '[' or
  //          ']') and an output stream (which updates the data array and
  //          produces output).
  // TODO: add syntax sugar for switch statement or else if?
  private def makeCpuExpr(): Expr = {
    val src = os.read(
      os.pwd / "src" / "main" / "scala" / "mhir" / "main" / "bf" / "interpreter.sirop"
    )
    val parsed =
      mhir.parse.sirop.Parser
        .parse(
          src
            .replace("TAPE_LEN", s"$tapeLength:u32")
            .replace("PROG_LEN", s"$programLength:u32")
            .replace("INPUT_LEN", s"$inputLength:u32")
            .replace("OUTPUT_LEN", s"$maxOutputLength:u32")
        )
        .e
    val typeChecked = parsed.tchk()
    val lowered = typeChecked.lower
    val simplified = mhir.optimize.PartialEvalPass.partialEval(lowered)
    simplified
  }
}

/** Companion object for [[Interpreter]].
  */
object Interpreter {

  /** Factory for [[Interpreter]].
    */
  def apply(
      tapeLength: Int = 32768,
      programLength: Int,
      inputLength: Int,
      maxOutputLength: Int
  ): Interpreter = {
    new Interpreter(tapeLength, programLength, inputLength, maxOutputLength)
  }

  /** Run the given program with the given input and return the output.
    *
    * This is shorthand for initializing an [[Interpreter]] and then calling the
    * [[Interpreter.run]] method.
    *
    * @param tapeLength
    *   the number of cells in the data array.
    * @param maxOutputLength
    *   the maximum length of the output.
    * @param program
    *   the Brainfuck program to run. This must only contain ASCII characters.
    * @param input
    *   the input to the program. This must only contain ASCII characters.
    * @return
    *   all output produced by this program, up to the maximum output length.
    */
  def run(
      tapeLength: Int = 32768,
      maxOutputLength: Int,
      program: String,
      input: String
  ): String = {
    val bfi = Interpreter(
      tapeLength = tapeLength,
      programLength = program.length,
      inputLength = input.length,
      maxOutputLength = maxOutputLength
    )
    bfi.run(program, input)
  }
}
