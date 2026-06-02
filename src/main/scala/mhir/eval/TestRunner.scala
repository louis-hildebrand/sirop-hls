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
      throw TestError(s"no tests were found")
    } else {
      val numTests = assertions.length
      val testOrTests = if (numTests == 1) "test" else "tests"
      logger.debug(s"running $numTests $testOrTests...")
      var errors = 0
      for ((a, i) <- assertions.zipWithIndex) {
        val ok = run(i, a, body, handshake = prog.handshake)
        if (!ok) {
          errors += 1
        }
      }
      if (errors == 0) {
        logger.info(s"$numTests/$numTests $testOrTests passed!")
      } else {
        throw TestError(s"$errors/$numTests $testOrTests failed.")
      }
    }
  }

  private def run(
      testIdx: Int,
      a: Assertion,
      body: Expr,
      handshake: Boolean
  ): Boolean = {
    logger.debug(s"running test $testIdx ... ")
    try {
      val expectedOutput = mhir.eval.eval(a.expectedOutput)
      logger.debug(s"expected output is $expectedOutput")
      // Evaluate inputs to avoid errors due to the inputs not having latency
      // matching
      val evaluatedInputs = a.inputs
        .map({ case (x, e) => x -> mhir.eval.eval(e) })
        .toMap[Expr, Expr]
      val bodyWithInputs = body.subPreserveType(evaluatedInputs)
      val actualOutput = mhir.eval.eval(bodyWithInputs, handshake = handshake)
      logger.debug(s"actual output is   $actualOutput")
      if (actualOutput == expectedOutput) {
        logger.info(s"test $testIdx: PASSED")
        true
      } else {
        logger.warn(s"test $testIdx: FAILED")
        false
      }
    } catch {
      case ex: EvalException =>
        logger.debug(ex.getMessage)
        logger.warn(s"test $testIdx: ERROR")
        false
    }
  }
}
