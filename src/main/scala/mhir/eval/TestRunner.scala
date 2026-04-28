package mhir.eval

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck.TypeChecker

object TestRunner {

  private implicit val logger: Logger = Logger(getClass.getName)

  /** Run the tests defined in the given program.
    *
    * @throws TestError
    *   if the tests were unsuccessful.
    */
  def run(prog: Program): Unit = {
    val (_, body) = TypeChecker.unwrapTopLevelFunction(prog.accel.body)
    val assertions = prog.test.collect({ case a: Assertion => a })
    if (assertions.isEmpty) {
      println("No tests were found.")
      throw TestError(s"no tests were found")
    } else {
      val numTests = assertions.length
      val testOrTests = if (numTests == 1) "test" else "tests"
      println(s"Running $numTests $testOrTests...")
      println()
      var errors = 0
      for ((a, i) <- assertions.zipWithIndex) {
        val ok = run(i, a, body)
        if (!ok) {
          errors += 1
        }
      }
      println()
      if (errors == 0) {
        println(s"$numTests/$numTests $testOrTests passed!")
      } else {
        throw TestError(s"$errors/$numTests $testOrTests failed.")
      }
    }
  }

  private def run(testIdx: Int, a: Assertion, body: Expr): Boolean = {
    print(s"Test $testIdx ... ")
    logger.whenDebugEnabled {
      // Don't print debug messages on the same line as the "Test i ..." message
      println()
    }
    val bodyWithInputs = body.subPreserveType(a.inputs.toMap[Expr, Expr])
    val actualOutput = mhir.eval.eval(bodyWithInputs)
    val expectedOutput = mhir.eval.eval(a.expectedOutput)
    logger.debug(s"expected output is $expectedOutput")
    logger.debug(s"actual output is   $actualOutput")
    if (actualOutput == expectedOutput) {
      println("OK")
      true
    } else {
      println("FAILED")
      false
    }
  }
}
