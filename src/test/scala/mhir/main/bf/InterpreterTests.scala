package mhir.main.bf

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.tags.Slow

/** Tests for [[mhir.main.bf.Interpreter]].
  */
@Slow
class InterpreterTests extends AnyFunSuite {
  test("H") {
    val program = "Should print 'H': +++++++++[>++++++++<-]>."
    val input = ""
    val expectedOutput = "H"
    val actualOutput = Interpreter.run(
      tapeLength = 2,
      maxOutputLength = expectedOutput.length + 2,
      program = program,
      input = input
    )
    assert(actualOutput == expectedOutput)
  }

  test("Hello World!") {
    val program =
      "++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++."
    val input = ""
    val expectedOutput = "Hello World!"
    val actualOutput = Interpreter.run(
      tapeLength = 32,
      maxOutputLength = expectedOutput.length,
      program = program,
      input = input
    )
    assert(actualOutput == expectedOutput)
  }

  test("[]") {
    val program = "[]."
    val input = ""
    val expectedOutput = "\u0000"
    val actualOutput = Interpreter.run(
      tapeLength = 1,
      maxOutputLength = expectedOutput.length,
      program = program,
      input = input
    )
    assert(actualOutput == expectedOutput)
  }

  test("WrapOn+") {
    val program = "+++++ +++++[>+++++ +++++ +++++ +++++ +++++ +<-]>."
    val input = ""
    val expectedOutput = Seq(4.toChar).mkString("")
    val actualOutput = Interpreter.run(
      tapeLength = 2,
      maxOutputLength = expectedOutput.length,
      program = program,
      input = input
    )
    assert(actualOutput == expectedOutput)
  }

  test("WrapOn-") {
    val program = "-."
    val input = ""
    val expectedOutput = Seq(255.toChar).mkString("")
    val actualOutput = Interpreter.run(
      tapeLength = 1,
      maxOutputLength = expectedOutput.length,
      program = program,
      input = input
    )
    assert(actualOutput == expectedOutput)
  }

  test("brainfuck.org:test1") {
    val program =
      ">,>+++++++++,>+++++++++++[<++++++<++++++<+>>>-]<<.>.<<-.>.>.<<."
    val input = "\n"
    val expectedOutput = "LK"
    val actualOutput = Interpreter.run(
      tapeLength = 8,
      maxOutputLength = expectedOutput.length,
      program = program,
      input = input
    )
    assert(actualOutput == expectedOutput)
  }

  test("brainfuck.org:test4") {
    val program =
      "[]++++++++++[>>+>+>++++++[<<+<+++>>>-]<<<<-]\n\"A*$\";?@![#>>+<<]>[>>]<<<<[>++<[-]]>.>."
    val input = ""
    val expectedOutput = "H"
    val actualOutput = Interpreter.run(
      tapeLength = 8,
      maxOutputLength = expectedOutput.length,
      program = program,
      input = input
    )
    assert(actualOutput == expectedOutput)
  }
}
