package mhir.eval

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.sugar.{AllOne, AllZero, BitwiseAnd, BitwiseNot, StmCst}
import mhir.typecheck._
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
    val rawExpectedOutput =
      try {
        val result = mhir.eval.eval(a.expectedOutput)
        logResult(None, result, testIdx, "raw expected output")
      } catch {
        case ex: EvalException =>
          logError(expectedPath, ex, testIdx, "raw expected output")
      }
    val ignore = a.ignore match {
      case Some(ignore) =>
        try {
          val result = mhir.eval.eval(ignore)
          logResult(None, result, testIdx, "'ignoring' stream")
        } catch {
          case ex: EvalException =>
            logError(expectedPath, ex, testIdx, "'ignoring' stream")
        }
      case None =>
        val TyStm(t, n) = a.expectedOutput.typ
        Some(mhir.eval.eval(StmCst(n, AllZero(t))()))
    }
    val expectedOutput =
      try {
        val result = applyMask(rawExpectedOutput, ignore)
        logResult(expectedPath, result, testIdx, "expected output")
      } catch {
        case ex: EvalException =>
          logError(expectedPath, ex, testIdx, "expected output")
      }
    val rawActualOutput =
      try {
        val result =
          mhir.eval.eval(body, inputs = a.inputs, handshake = handshake)
        logResult(None, result, testIdx, "raw actual output")
      } catch {
        case ex: EvalException =>
          logError(actualPath, ex, testIdx, "raw actual output")
      }
    val actualOutput =
      try {
        val result = applyMask(rawActualOutput, ignore)
        logResult(actualPath, result, testIdx, "actual output")
      } catch {
        case ex: EvalException =>
          logError(actualPath, ex, testIdx, "actual output")
      }
    (expectedOutput, actualOutput) match {
      case (Some(expected), Some(actual)) if actual == expected =>
        logger.info(s"test $testIdx: PASSED")
        true
      case (Some(_), Some(_)) =>
        logger.warn(s"test $testIdx: FAILED")
        false
      case (None, Some(_)) =>
        logger.warn(s"test $testIdx: ERROR (when evaluating expected output)")
        false
      case (Some(_), None) =>
        logger.warn(s"test $testIdx: ERROR (when evaluating actual output)")
        false
      case (None, None) =>
        logger.warn(
          s"test $testIdx: ERROR (when evaluating expected and actual outputs)"
        )
        false
    }
  }

  private def applyMask(
      output: Option[Expr],
      ignore: Option[Expr]
  ): Option[Expr] = {
    (output, ignore) match {
      case (Some(output), Some(ignore)) =>
        val StmLiteral(outElems @ _*) = output
        val StmLiteral(ignoreElems @ _*) = ignore
        val TyStm(elemTyp, _) = ignore.typ
        val ones = mhir.eval.eval(AllOne(elemTyp))
        val zeros = mhir.eval.eval(AllZero(elemTyp))
        assert(outElems.length == ignoreElems.length)
        Some(
          StmLiteral(
            outElems
              .zip(ignoreElems)
              .map({
                case (out, ignore) if ignore == zeros => out
                case (_, ignore) if ignore == ones    => zeros
                case (out, ignore) =>
                  mhir.eval.eval(BitwiseAnd(out, BitwiseNot(ignore)())())
              }): _*
          )()
        )
      case _ =>
        None
    }
  }

  private def logError(
      destination: Option[Path],
      ex: EvalException,
      testIdx: Int,
      goal: String
  ): None.type = {
    destination match {
      case None =>
        logger.debug(ex.getMessage)
      case Some(p) =>
        os.write.append(p, formatOutput(testIdx, ex.getMessage))
        logger.debug(s"appended error (in $goal) to $p")
    }
    None
  }

  private def logResult(
      destination: Option[Path],
      result: Option[Expr],
      testIdx: Int,
      goal: String
  ): Option[Expr] = {
    result match {
      case Some(result) => logResult(destination, result, testIdx, goal)
      case None         => logMissingResult(destination, testIdx, goal)
    }
  }

  private def logResult(
      destination: Option[Path],
      result: Expr,
      testIdx: Int,
      goal: String
  ): Option[Expr] = {
    destination match {
      case None =>
        logger.debug(s"$goal is $result")
      case Some(p) =>
        val msg = formatOutput(testIdx, ExprPrinter.display(result))
        os.write.append(p, msg)
        logger.debug(s"appended $goal to $p")
    }
    Some(result)
  }

  private def logMissingResult(
      destination: Option[Path],
      testIdx: Int,
      goal: String
  ): Option[Expr] = {
    val baseMsg = s"$goal could not be calculated due to previous errors"
    destination match {
      case None =>
        logger.debug(baseMsg)
      case Some(p) =>
        val msg = formatOutput(testIdx, baseMsg)
        os.write.append(p, msg)
        logger.debug(s"$baseMsg; appended note to $p")
    }
    None
  }

  private def formatOutput(testIdx: Int, msg: String): String = {
    ((if (testIdx == 0) "" else "\n")
      + s"/* Test $testIdx */\n"
      + msg
      + "\n")
  }
}
