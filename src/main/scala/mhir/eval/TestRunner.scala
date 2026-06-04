package mhir.eval

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.typecheck.TypeChecker
import os.Path

object TestRunner {

  private implicit val logger: Logger = Logger(getClass.getName)

  /** Run the tests defined in the given program.
    *
    * @param prog
    *   the program to test.
    * @param expectedPath
    *   the path to the file in which to save the expected outputs.
    * @param actualPath
    *   the path to the file in which to save the actual outputs.
    *
    * @throws TestError
    *   if the tests were unsuccessful.
    */
  def run(
      prog: Program,
      expectedPath: Option[Path],
      actualPath: Option[Path],
      overwrite: Boolean
  ): Unit = {
    expectedPath.foreach(checkIfFileExists(_, overwrite))
    actualPath.foreach(checkIfFileExists(_, overwrite))
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
        val ok = run(
          i,
          a,
          body,
          handshake = prog.handshake,
          expectedPath = expectedPath,
          actualPath = actualPath
        )
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

  private def checkIfFileExists(path: Path, overwrite: Boolean): Unit = {
    if (os.isFile(path)) {
      if (overwrite) {
        os.remove(path)
      } else {
        throw FileError(
          s"destination for test outputs ($path) already exists."
            + " Pass the --overwrite command-line flag to overwrite it."
        )
      }
    } else if (os.exists(path)) {
      throw FileError(
        s"destination for test outputs ($path) exists but is not a file"
      )
    }
  }

  private def run(
      testIdx: Int,
      a: Assertion,
      body: Expr,
      handshake: Boolean,
      expectedPath: Option[Path],
      actualPath: Option[Path]
  ): Boolean = {
    logger.debug(s"running test $testIdx ... ")
    try {
      val expectedOutput = mhir.eval.eval(a.expectedOutput)
      expectedPath match {
        case None =>
          logger.debug(s"expected output is $expectedOutput")
        case Some(p) =>
          val msg = (
            (if (testIdx == 0) "" else "\n")
              + s"/* Test $testIdx */\n"
              + ExprPrinter.display(expectedOutput)
              + "\n"
          )
          os.write.append(p, msg)
          logger.debug(s"appended expected output to $p")
      }
      val actualOutput =
        mhir.eval.eval(body, inputs = a.inputs, handshake = handshake)
      actualPath match {
        case None =>
          logger.debug(s"actual output is   $actualOutput")
        case Some(p) =>
          val msg = (
            (if (testIdx == 0) "" else "\n")
              + s"/* Test $testIdx */\n"
              + ExprPrinter.display(actualOutput)
              + "\n"
          )
          os.write.append(p, msg)
          logger.debug(s"appended actual output to $p")
      }
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
