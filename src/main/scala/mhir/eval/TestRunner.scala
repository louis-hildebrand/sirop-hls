package mhir.eval

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.sugar.{AllOne, AllZero, BitwiseAnd, BitwiseNot, StmCst}
import mhir.typecheck._
import os.Path

private case class TestCaseFailed(msg: String) extends Exception(msg)

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
        try {
          val runner = new TestRunner(
            testIdx = i,
            handshake = prog.handshake,
            showPhysical = showPhysical
          )
          runner.run(
            a,
            body,
            expectedPath = expectedPath,
            actualPath = actualPath,
            headByParam = prog.headByParam
          )
          logger.info(s"test $i: PASSED")
        } catch {
          case TestCaseFailed(msg) =>
            errors += 1
            logger.warn(s"test $i: $msg")
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
}

class TestRunner(testIdx: Int, handshake: Boolean, showPhysical: Boolean) {

  private implicit val logger: Logger = Logger(getClass.getName)

  private def run(
      a: Assertion,
      body: Expr,
      expectedPath: Option[Path],
      actualPath: Option[Path],
      headByParam: Map[Param, Expr]
  ): Unit = {
    logger.debug(s"running test $testIdx ... ")
    val rawExpectedOutput = eval("expected output", expectedPath) {
      a.expectedOutput
    }
    val ignore = eval("'ignoring' stream", expectedPath) {
      a.ignore.getOrElse({
        val TyStm(t, n) = a.expectedOutput.typ
        // TODO: bypass evaluation in this case and just make a stream literal directly?
        StmCst(n, AllZero(t))()
      })
    }
    val expectedLogical =
      try {
        val result = applyMask(rawExpectedOutput, ignore)
        logResult(
          "expected output",
          expectedPath,
          // The physical prefix is never logged for the expected output sequence
          None,
          result
        )
        result
      } catch {
        case ex: EvalException =>
          logError(expectedPath, ex, "expected output")
          throw TestCaseFailed(
            "ERROR (when combining expected output and 'ignoring' stream)"
          )
      }
    if (expectedLogical.exists(_.isInstanceOf[Undefined])) {
      logger.warn(
        s"expected output for test $testIdx contains undefined elements that are not ignored"
      )
    }
    val inputs = a.inputs.map({ case (input, rhs) =>
      val TyStm(elemTyp, _) = rhs.typ
      val result = mhir.eval.eval(rhs, handshake = handshake)
      val head = headByParam
        .get(input)
        .map(mhir.eval.eval(_))
        .getOrElse(Undefined(elemTyp))
      result match {
        case StmLiteral(physical, _) =>
          checkInputPhysicalPrefix(input, physical, head)
        case _ => ()
      }
      input -> result
    })
    val inputLatencies = inputs.map({
      case (_, StmLiteral(physical, _)) => physical.length
      case _                            => 0
    })
    if (inputLatencies.toSet.size > 1) {
      throw TestCaseFailed("INPUT LATENCY MISMATCH")
    }
    val rawActualOutput = eval("actual output", actualPath, inputs = inputs) {
      body
    }
    if (this.handshake) {
      a.prefixCondition.foreach(_ =>
        logger.warn(s"prefix condition for test case $testIdx will be ignored")
      )
    }
    val actualPhysical = rawActualOutput match {
      case StmLiteral(physical, _) =>
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
          actualLogical
        )
        actualLogical
      } catch {
        case ex: EvalException =>
          logError(actualPath, ex, "actual output")
          throw TestCaseFailed(
            "ERROR (when combining actual output and 'ignoring' stream)"
          )
      }
    val logicalOk = actualLogical == expectedLogical
    if (!logicalOk) {
      throw TestCaseFailed("WRONG OUTPUT")
    }
    val physicalOk = actualPhysical
      .forall({ case (_, ok) => ok == True })
    if (!physicalOk) {
      throw TestCaseFailed("WRONG PHYSICAL PREFIX")
    }
  }

  private def eval(
      name: String,
      dest: Option[Path],
      inputs: Map[Param, Expr] = Map()
  )(body: => Expr): Expr = {
    try {
      val result =
        mhir.eval.eval(body, handshake = this.handshake, inputs = inputs)
      logger.debug(s"$name is $result")
      result
    } catch {
      case ex: EvalException =>
        logError(dest, ex, name)
        throw TestCaseFailed(s"ERROR (when evaluating $name)")
    }
  }

  private def applyMask(output: Expr, ignore: Expr): Seq[Expr] = {
    (output, ignore) match {
      case (StmLiteral(_, outLogical), ignore @ StmLiteral(_, ignoreElems)) =>
        val TyStm(elemTyp, _) = ignore.typ
        val ones = mhir.eval.eval(AllOne(elemTyp))
        val zeros = mhir.eval.eval(AllZero(elemTyp))
        assert(outLogical.length == ignoreElems.length)
        outLogical
          .zip(ignoreElems)
          .map({
            case (out, ignore) if ignore == zeros => out
            case (_, ignore) if ignore == ones    => zeros
            case (out, ignore) =>
              mhir.eval.eval(BitwiseAnd(out, BitwiseNot(ignore)())())
          })
      case (out, _: StmLiteral) =>
        throw new AssertionError(
          s"output should evaluate to a stream, but found $out"
        )
      case (_, ignore) =>
        throw new AssertionError(
          s"'ignore' should evaluate to a stream, but found $ignore"
        )
    }
  }

  private def checkInputPhysicalPrefix(
      input: Param,
      physical: Seq[Expr],
      head: Expr
  ): Unit = {
    val ok = physical.forall(isConsistentWithHead(_, head))
    if (!ok) {
      logger.warn(
        s"physical prefix of input $input in test case $testIdx is not consistent with annotation head($input)"
      )
    }
  }

  private def isConsistentWithHead(e: Expr, head: Expr): Boolean = {
    (e, head) match {
      case (a, b) if a == b  => true
      case (_, _: Undefined) => true
      case (Tuple(aElems @ _*), Tuple(bElems @ _*)) =>
        aElems.zip(bElems).forall({ case (a, b) => isConsistentWithHead(a, b) })
      case (VecLiteral(aElems @ _*), VecLiteral(bElems @ _*)) =>
        aElems.zip(bElems).forall({ case (a, b) => isConsistentWithHead(a, b) })
      case _ => false
    }
  }

  private def logError(
      destination: Option[Path],
      ex: EvalException,
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
      logical: Seq[Expr]
  ): Unit = {
    destination match {
      case None =>
        val logicalStr = logical.map(_.toString).mkString("[", ", ", "]s")
        val fullStr = physical match {
          case Some(physical) =>
            val physicalStr = physical
              .map({
                case (e, True) => e.toString
                case (e, _)    => s"$e /* does not satisfy prefix condition */"
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
          case Some(Seq()) if this.showPhysical =>
            s"[]s ++ $logicalStr"
          case Some(physical) if this.showPhysical =>
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

  private def formatOutput(testIdx: Int, msg: String): String = {
    ((if (testIdx == 0) "" else "\n")
      + s"/* Test $testIdx */\n"
      + msg
      + "\n")
  }
}
