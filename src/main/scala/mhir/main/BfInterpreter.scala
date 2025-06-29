package mhir.main

import mhir.ir.Lowering.ExprLowering
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.sugar.{Min, VecShiftLeft}

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
  *   the maximum length of the output.
  */
class BfInterpreter(
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
    val outputStream =
      mhir.ir.eval(this.f(cmdStream)(inStream)).asInstanceOf[StmLiteral]
    outputStream.elems
      .flatMap(e => {
        val tup = e.asInstanceOf[Tuple]
        assert(tup.elems.length == 2)
        val (data, valid) = (tup.elems.head, tup.elems(1))
        assert(data.isInstanceOf[IntCst])
        assert(valid.isInstanceOf[BoolCst])
        (data, valid) match {
          case (IntCst(c), True) => Some(c.toChar)
          case _                 => None
        }
      })
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
    val cmdStreamParam = Param("cmd_stm")(TyStm(U8, this.programLength))
    val inputStreamParam = Param("in_stm")(TyStm(U8, this.inputLength))
    val u2 = TyUInt(2)
    val LANGLE = C('<'.toInt)(U8)
    val RANGLE = C('>'.toInt)(U8)
    val PLUS = C('+'.toInt)(U8)
    val MINUS = C('-'.toInt)(U8)
    val DOT = C('.'.toInt)(U8)
    val COMMA = C(','.toInt)(U8)
    val LSQR = C('['.toInt)(U8)
    val RSQR = C(']'.toInt)(U8)
    val outputStream = {

      /** time */
      val t = Param("t")(U32)

      /** stream of commands */
      val cmdStream = Param("cmd_stm")(TyStm(U8, -1))

      /** array of commands */
      val cmdArray = Param("cmd_arr")(TyVec(U8, this.programLength))

      /** program counter (index within command array) */
      val cmdPtr = Param("cmd_ptr")(U32)

      /** instruction register (value of the command to execute) */
      val cmd = VecAccess(cmdArray, cmdPtr)()

      /** stream of inputs */
      val inStream = Param("in_stm")(TyStm(U8, -1))

      /** data array */
      val tape = Param("tape")(TyVec(U8, this.tapeLength))

      /** data pointer */
      val tapePtr = Param("tape_ptr")(U32)

      val tapeVal = VecAccess(tape, tapePtr)()

      /** how the program counter should move */
      val mode = Param("mode")(u2)
      val LOADING = C(0)(u2)
      val NORMAL = C(1)(u2)
      val SEEK_LEFT = C(2)(u2)
      val SEEK_RIGHT = C(3)(u2)

      /** bracket counter (to be used for seeking left/right) */
      val bracketCtr = Param("bracket_ctr")(U8)

      /** whether the program is done */
      val done = Param("done")(TyBool)

      /** number of inputs remaining */
      val remainingInputs = Param("remaining_inputs")(U32)

      StmBuild(
        this.maxOutputLength,
        Tuple(Mux(done, C(0)(U8), tapeVal)(), !done)(),
        done || (cmd === DOT),
        Map[Param, (Expr, Expr)](
          t -> (
            C(0)(U32),
            1 + t
          ),
          mode -> (
            LOADING,
            Mux(
              // LOADING --> NORMAL
              t === (-1 + this.programLength),
              NORMAL,
              Mux(
                // NORMAL --> SEEK_RIGHT
                (mode === NORMAL) && (cmd === LSQR) && (tapeVal === 0),
                SEEK_RIGHT,
                Mux(
                  // NORMAL --> SEEK_LEFT
                  (mode === NORMAL) && (cmd === RSQR) && (tapeVal !== 0),
                  SEEK_LEFT,
                  Mux(
                    // SEEK_LEFT --> NORMAL
                    // (bracketCtr will be 0 at the next step)
                    (mode === SEEK_LEFT) && (cmd === LSQR) && (bracketCtr === 1),
                    NORMAL,
                    Mux(
                      // SEEK_RIGHT --> NORMAL
                      // (bracketCtr will be 0 at the next step)
                      (mode === SEEK_RIGHT) && (cmd === RSQR) && (bracketCtr === 1),
                      NORMAL,
                      /* default: no transition */
                      mode
                    )()
                  )()
                )()
              )()
            )()
          ),
          done -> (
            False,
            done || (
              (cmdPtr === C(this.programLength - 1)(U32))
                && ((cmd !== RSQR) || (tapeVal === 0))
            )
          ),
          bracketCtr -> (
            C(0)(U8),
            Mux(
              // NORMAL --> SEEK_RIGHT: start at 1
              ((mode === NORMAL) && (cmd === LSQR) && (tapeVal === 0))
              // NORMAL --> SEEK_LEFT: start at 1
                || ((mode === NORMAL) && (cmd === RSQR) && (tapeVal !== 0))
                // Descending deeper into bracket nest
                || ((mode === SEEK_RIGHT) && (cmd === LSQR))
                || ((mode === SEEK_LEFT) && (cmd === RSQR)),
              1 + bracketCtr,
              Mux(
                // Coming out of bracket nest
                ((mode === SEEK_RIGHT) && (cmd === RSQR))
                  || ((mode === SEEK_LEFT) && (cmd === LSQR)),
                ToUnsigned(-1 + bracketCtr)(),
                bracketCtr
              )()
            )()
          ),
          remainingInputs -> (
            C(this.inputLength)(U32),
            Mux(
              (mode === NORMAL) && (cmd === COMMA) && (remainingInputs > 0),
              ToUnsigned(-1 + remainingInputs)(),
              remainingInputs
            )()
          ),
          inStream -> (
            inputStreamParam,
            (mode === NORMAL) && (cmd === COMMA) && (remainingInputs > 0)
          ),
          cmdStream -> (
            cmdStreamParam,
            mode === LOADING
          ),
          cmdArray -> (
            VecBuild(this.programLength, U32 ::+ (_ => C(0)(U8)))(),
            Mux(
              mode === LOADING,
              VecShiftLeft(cmdArray, StmData(cmdStream)())(),
              cmdArray
            )()
          ),
          cmdPtr -> (
            C(0)(U32),
            Mux(
              // LOADING --> LOADING
              // LOADING --> NORMAL
              mode === LOADING,
              cmdPtr,
              Mux(
                // NORMAL --> SEEK_LEFT
                // SEEK_LEFT --> SEEK_LEFT
                ((mode === NORMAL) && (cmd === RSQR) && (tapeVal !== 0))
                  || ((mode === SEEK_LEFT) && ((cmd !== LSQR) || (bracketCtr !== 1))),
                ToUnsigned(-1 + cmdPtr)(),
                // NORMAL --> NORMAL
                // NORMAL --> SEEK_RIGHT
                // SEEK_LEFT --> NORMAL
                // SEEK_RIGHT --> NORMAL
                Min(1 + cmdPtr, C(this.programLength - 1)(U32))()
              )()
            )()
          ),
          tapePtr -> (
            C(0)(U32),
            Mux(
              mode === NORMAL,
              Mux(
                cmd === RANGLE,
                1 + tapePtr,
                Mux(
                  cmd === LANGLE,
                  ToUnsigned(-1 + tapePtr)(),
                  tapePtr
                )()
              )(),
              tapePtr
            )()
          ),
          tape -> (
            VecBuild(this.tapeLength, U32 ::+ (_ => C(0)(U8)))(),
            VecBuild(
              this.tapeLength,
              U32 ::+ (i =>
                Mux(
                  (i !== tapePtr) || (mode !== NORMAL),
                  VecAccess(tape, i)(),
                  Mux(
                    cmd === PLUS,
                    // Wrapping add
                    Mux(
                      tapeVal === C(255)(U8),
                      C(0)(U8),
                      1 + tapeVal
                    )(),
                    Mux(
                      cmd === MINUS,
                      // Wrapping sub
                      Mux(
                        tapeVal === C(0)(U8),
                        C(255)(U8),
                        ToUnsigned(-1 + VecAccess(tape, i)())()
                      )(),
                      Mux(
                        (cmd === COMMA) && (remainingInputs > 0),
                        StmData(inStream)(),
                        VecAccess(tape, i)()
                      )()
                    )()
                  )()
                )()
              )
            )()
          )
        )
      )()
    }
    Function(cmdStreamParam, Function(inputStreamParam, outputStream)())()
      .tchk()
      .lower()
  }
}

/** Companion object for [[BfInterpreter]].
  */
object BfInterpreter {

  /** Factory for [[BfInterpreter]].
    */
  def apply(
      tapeLength: Int = 32768,
      programLength: Int,
      inputLength: Int,
      maxOutputLength: Int
  ): BfInterpreter = {
    new BfInterpreter(tapeLength, programLength, inputLength, maxOutputLength)
  }

  /** Run the given program with the given input and return the output.
    *
    * This is shorthand for initializing a [[BfInterpreter]] and then calling
    * the [[BfInterpreter.run]] method.
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
    val bfi = BfInterpreter(
      tapeLength = tapeLength,
      programLength = program.length,
      inputLength = input.length,
      maxOutputLength = maxOutputLength
    )
    bfi.run(program, input)
  }
}
