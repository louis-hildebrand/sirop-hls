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
      showPhysical: Boolean,
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
          actualPath = actualPath,
          showPhysical = showPhysical
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
      actualPath: Option[Path],
      showPhysical: Boolean
  ): Boolean = {
    logger.debug(s"running test $testIdx ... ")
    val rawExpectedOutput =
      try {
        val result = mhir.eval.eval(a.expectedOutput, handshake = handshake)
        logger.debug(s"raw expected output is $result")
        Some(result)
      } catch {
        case ex: EvalException =>
          logError(expectedPath, ex, testIdx, "raw expected output")
          None
      }
    val ignore = a.ignore match {
      case Some(ignore) =>
        try {
          val result = mhir.eval.eval(ignore, handshake = handshake)
          logger.debug(s"'ignoring' stream is $result")
          Some(result)
        } catch {
          case ex: EvalException =>
            logError(expectedPath, ex, testIdx, "'ignoring' stream")
            None
        }
      case None =>
        val TyStm(t, n) = a.expectedOutput.typ
        Some(mhir.eval.eval(StmCst(n, AllZero(t))(), handshake = handshake))
    }
    val expectedLogical =
      try {
        val result = applyMask(rawExpectedOutput, ignore)
        logResult(
          "expected output",
          expectedPath,
          // The physical prefix is never logged for the expected output sequence
          None,
          result,
          testIdx,
          showPhysical = showPhysical
        )
        result
      } catch {
        case ex: EvalException =>
          logError(expectedPath, ex, testIdx, "expected output")
          None
      }
    val rawActualOutput =
      try {
        val result =
          mhir.eval.eval(body, inputs = a.inputs, handshake = handshake)
        logger.debug(s"raw actual output is $result")
        Some(result)
      } catch {
        case ex: EvalException =>
          logError(actualPath, ex, testIdx, "raw actual output")
          None
      }
    val actualPhysical = rawActualOutput match {
      case Some(StmLiteral(physical, _)) =>
        physical.map({ e =>
          val ok = a.prefixCondition match {
            case Some(f) => mhir.eval.eval(FunCall(f, e)())
            case None    => True
          }
          (e, ok)
        })
      case _ =>
        Seq()
    }
    val actualLogical =
      try {
        val actualLogical = applyMask(rawActualOutput, ignore)
        logResult(
          "actual output",
          actualPath,
          Some(actualPhysical),
          actualLogical,
          testIdx,
          showPhysical = showPhysical
        )
        actualLogical
      } catch {
        case ex: EvalException =>
          logError(actualPath, ex, testIdx, "actual output")
          None
      }
    (expectedLogical, actualLogical) match {
      case (Some(expected), Some(actual)) =>
        val physicalOk = actualPhysical
          .forall({ case (_, ok) => ok == True })
        val logicalOk = actual == expected
        val pass = physicalOk && logicalOk
        if (pass) {
          logger.info(s"test $testIdx: PASSED")
          true
        } else {
          logger.warn(s"test $testIdx: FAILED")
          false
        }
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
  ): Option[Seq[Expr]] = {
    (output, ignore) match {
      case (
            Some(StmLiteral(_, outLogical)),
            Some(ignore @ StmLiteral(_, ignoreElems))
          ) =>
        val TyStm(elemTyp, _) = ignore.typ
        val ones = mhir.eval.eval(AllOne(elemTyp))
        val zeros = mhir.eval.eval(AllZero(elemTyp))
        assert(outLogical.length == ignoreElems.length)
        Some(
          outLogical
            .zip(ignoreElems)
            .map({
              case (out, ignore) if ignore == zeros => out
              case (_, ignore) if ignore == ones    => zeros
              case (out, ignore) =>
                mhir.eval.eval(BitwiseAnd(out, BitwiseNot(ignore)())())
            })
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
  ): Unit = {
    destination match {
      case None =>
        logger.debug(ex.getMessage)
      case Some(p) =>
        os.write.append(p, formatOutput(testIdx, ex.getMessage))
        logger.debug(s"appended error (in $goal) to $p")
    }
  }

  private def logResult(
      goal: String,
      destination: Option[Path],
      physical: Option[Seq[(Expr, Expr)]],
      logical: Option[Seq[Expr]],
      testIdx: Int,
      showPhysical: Boolean
  ): Unit = {
    logical match {
      case None => logMissingResult(destination, testIdx, goal)
      case Some(logical) =>
        destination match {
          case None =>
            val logicalStr = logical.map(_.toString).mkString("[", ", ", "]s")
            val fullStr = physical match {
              case Some(physical) =>
                val physicalStr = physical
                  .map({
                    case (e, True) => e.toString
                    case (e, _) => s"$e /* does not satisfy prefix condition */"
                  })
                  .mkString("[", ", ", "]s")
                s"$physicalStr ++ $logicalStr"
              case None => logicalStr
            }
            logger.debug(s"$goal is $fullStr")
          case Some(dest) =>
            val indent = "  "
            val logicalStr = logical
              .map(_.toString)
              .mkString(s"[\n$indent", s",\n$indent", "\n]s")
            val fullStr = physical match {
              case Some(Seq()) if showPhysical =>
                s"[]s ++ $logicalStr"
              case Some(physical) if showPhysical =>
                val elemStrings = physical.map({ case (e, _) => e.toString })
                val elemWidth = elemStrings.map(_.length).max
                val ok = physical.map({ case (_, ok) => ok == True })
                val physicalStr = elemStrings
                  .zip(ok)
                  .map({
                    case (e, true) => e
                    case (e, false) =>
                      s"${e.padTo(elemWidth, ' ')}  /* does not satisfy prefix condition */"
                  })
                  .mkString(s"[\n$indent", s",\n$indent", "\n]s")
                s"$physicalStr ++ $logicalStr"
              case _ => logicalStr
            }
            val msg = formatOutput(testIdx, fullStr)
            os.write.append(dest, msg)
            logger.debug(s"appended $goal to $dest")
        }
    }
  }

  private def logMissingResult(
      destination: Option[Path],
      testIdx: Int,
      goal: String
  ): Unit = {
    val baseMsg = s"$goal could not be calculated due to previous errors"
    destination match {
      case None =>
        logger.debug(baseMsg)
      case Some(p) =>
        val msg = formatOutput(testIdx, baseMsg)
        os.write.append(p, msg)
        logger.debug(s"$baseMsg; appended note to $p")
    }
  }

  private def formatOutput(testIdx: Int, msg: String): String = {
    ((if (testIdx == 0) "" else "\n")
      + s"/* Test $testIdx */\n"
      + msg
      + "\n")
  }
}
