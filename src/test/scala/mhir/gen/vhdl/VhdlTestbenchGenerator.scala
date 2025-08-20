package mhir.gen
package vhdl

import mhir.debug.indent
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

import os.Path

object VhdlTestbenchGenerator {

  /** Create a testbench for a VHDL design.
    *
    * @param inputs
    *   the inputs to provide to the design.
    * @param e
    *   the expression from which VHDL was generated. This will be used to
    *   determine the expected output.
    * @param dir
    *   the directory of the VHDL project (the whole directory containing the
    *   design, not the `test` subdirectory).
    */
  def makeTestbench(
      // TODO: Make it possible to test multiple values for one input?
      inputs: Seq[DirectTestInput],
      e: Expr,
      dir: Path,
      testNotReady: Boolean = true
  ): Unit = {
    val (out, inputsByVar) = getExpectedOutput(e, inputs)
    makeTestbench(inputsByVar, out, dir, testNotReady = testNotReady)
  }

  def makeTestbench(
      inputs: Seq[DirectTestInput],
      out: DirectTestOutput,
      dir: Path,
      testNotReady: Boolean
  ): Unit = {
    makeTestbench(
      inputsByVar = inputs.zipWithIndex
        .map({ case (in, i) =>
          val typ = TyStm(in.elems.head.get.tchk().typ, -1)
          val x = Param(s"I$i", -1)(typ)
          x -> in
        })
        .toMap,
      out = out,
      dir = dir,
      testNotReady = testNotReady
    )
  }

  /** Creates a testbench for a VHDL design.
    *
    * @param inputsByVar
    *   the inputs to provide to the design.
    * @param out
    *   the expected output.
    * @param dir
    *   the directory of the VHDL project (the whole directory containing the
    *   design, not the `test` subdirectory).
    */
  private def makeTestbench(
      inputsByVar: Map[Param, DirectTestInput],
      out: DirectTestOutput,
      dir: Path,
      testNotReady: Boolean
  ): Unit = {
    val outElemType = VhdlType(out.elemTyp)
    val portMap = getPortMap(inputsByVar.keys.toSeq)
    val inputStreamSignals = inputsByVar
      .map({ case (x, _) => getInputStreamDecls(x) })
      .mkString("\n\n")
    val inputStreamProcesses = inputsByVar
      .map({ case (x, in) => getInputStreamProcess(x, in) })
      .mkString("\n\n")
    val outStreamSignals = getOutputStreamSignals
    val outCheckProcess = getOutputCheckProcess(out, testNotReady)
    val t0 = if (testNotReady) {
      "-2; -- to account for the two cycles where valid = '1' but ready = '0'"
    } else {
      "0;"
    }
    val data = VhdlConversionGenerator.fromStdLogicVector("data", outElemType)
    val str =
      s"""library IEEE;
         |use IEEE.std_logic_1164.all;
         |use IEEE.numeric_std.all;
         |use std.textio.all;
         |use work.typedefs.all;
         |use work.conversions.all;
         |
         |entity testbench is
         |    -- empty
         |end testbench;
         |
         |architecture tb of testbench is
         |
         |    constant CLK_CYCLE  : time := 20 ns;
         |    signal   test_done  : boolean := false;
         |    signal   clk        : std_logic := '1';
         |    signal   data       : std_logic_vector(${outElemType.bitWidth - 1} downto 0);
         |    signal   valid      : std_logic;
         |    signal   ready      : std_logic := '0';
         |    signal   t          : integer := $t0
         |
         |    -- Easier debugging
         |    signal   data_t     : ${outElemType.vhdlName};
         |
         |${indent(inputStreamSignals)}
         |
         |${indent(outStreamSignals)}
         |
         |begin
         |
         |${indent(portMap)}
         |
         |    data_t <= $data;
         |
         |    -- Generate clock signal
         |    process
         |    begin
         |        if (not test_done) then
         |            wait for CLK_CYCLE / 2;
         |            clk <= not clk;
         |        else
         |            wait;
         |        end if;
         |    end process;
         |
         |    -- Keep track of time
         |    process
         |    begin
         |        wait until rising_edge(clk);
         |        t <= t + 1;
         |    end process;
         |
         |${indent(inputStreamProcesses)}
         |
         |${indent(outCheckProcess)}
         |end tb;
         |""".stripMargin

    val testDir = dir / "test"
    os.makeDir.all(testDir)
    val testbenchFile = testDir / "test_top.vhd"
    if (os.isFile(testbenchFile)) os.remove(testbenchFile)
    os.write(testbenchFile, str)
  }

  private def getPortMap(inputVars: Seq[Param]): String = {
    val assignments = (
      Seq("clk", "data", "valid", "ready")
        ++ inputVars.flatMap(x =>
          Seq(s"${x.name}_data", s"${x.name}_valid", s"${x.name}_ready")
        )
    ).map(x => s"$x => $x").mkString(", ")
    s"DUT : entity work.top port map($assignments);"
  }

  private def getInputStreamDecls(x: Param): String = {
    val elemType = x.typ.asInstanceOf[TyStm].t
    val vhdlType = VhdlStdLogicVec(VhdlType(elemType).bitWidth)
    s"""-- Input stream: ${x.name}
       |signal ${x.name}_data  : ${vhdlType.vhdlName};
       |signal ${x.name}_valid : std_logic;
       |signal ${x.name}_ready : std_logic := '0';
       |""".stripMargin.stripTrailing
  }

  private def getInputStreamProcess(x: Param, in: DirectTestInput): String = {
    val steps = in.elems
      .map({
        case None =>
          s"""${x.name}_valid <= '0';
             |${x.name}_data <= (others => '0');
             |wait until rising_edge(clk);
             |""".stripMargin.stripTrailing
        case Some(v) =>
          val slv = VhdlGenerator.valueToStdLogicVector(v)
          s"""${x.name}_valid <= '1';
             |${x.name}_data <= $slv;
             |wait until rising_edge(clk) and sl2bool(${x.name}_ready);
             |""".stripMargin.stripTrailing
      })
      .mkString("\n\n")
    s"""-- Generate inputs (${x.name})
       |process
       |begin
       |${indent(steps)}
       |
       |    ${x.name}_valid <= '0';
       |    ${x.name}_data <= (others => '0');
       |    wait;
       |end process;
       |""".stripMargin.stripTrailing
  }

  private def getOutputStreamSignals: String = {
    "-- Expected outputs: hardcoded"
  }

  private def getOutputCheckProcess(
      out: DirectTestOutput,
      testNotReady: Boolean
  ): String = {
    val notReadyTests = if (testNotReady) {
      """-- If the consumer is not ready, the producer must keep working until
        |-- it has a valid value and then it must wait
        |ready <= '0';
        |wait until rising_edge(clk) and valid = '1';
        |wait until rising_edge(clk);
        |assert(valid = '1') report "Design dropped its valid signal.";
        |""".stripMargin.stripTrailing
    } else {
      ""
    }
    val testSteps = out.elems.zipWithIndex
      .map({ case (v, i) =>
        ("wait until rising_edge(clk) and valid = '1';\n"
          + makeAssertions(v.tchk(), t = i))
      })
      .mkString("\n\n")
    s"""-- Check outputs
       |process
       |begin
       |${indent(notReadyTests)}
       |
       |    ready <= '1';
       |
       |${indent(testSteps)}
       |
       |    wait until falling_edge(clk);
       |    report "LATENCY: " & integer'image(t) & " cycles" severity note;
       |
       |    wait until rising_edge(clk);
       |    assert(valid = '0') report "Wrong `valid` after completion";
       |
       |    test_done <= true;
       |    assert false report "Test done." severity note;
       |    wait;
       |end process;
       |""".stripMargin.stripTrailing
  }

  private def makeAssertions(expected: Expr, t: Int): String = {
    require(expected.hasType)
    val fullBitWidth = VhdlType(expected.typ).bitWidth
    makeAssertions(
      actual = "data",
      expected = expected,
      t = t,
      lsb = 0,
      msb = fullBitWidth - 1
    )
  }

  private def makeAssertions(
      actual: String,
      expected: Expr,
      t: Int,
      lsb: Int,
      msb: Int
  ): String = {
    require(expected.hasType)
    (expected.typ, expected) match {
      case (TyVec(typ, _), VecLiteral(elems @ _*)) =>
        val elemBitWidth = VhdlType(typ).bitWidth
        elems.zipWithIndex
          .map({ case (e, i) =>
            val newMsb = msb - i * elemBitWidth
            val newLsb = newMsb - elemBitWidth + 1
            makeAssertions(actual, e, t, lsb = newLsb, msb = newMsb)
          })
          .mkString("\n")
      case (_, _: Undefined) =>
        s"-- $actual($msb downto $lsb) : undefined"
      case (_, v) =>
        val expectedVhdl = VhdlGenerator.valueToStdLogicVector(v)
        val actualSlice = s"$actual($msb downto $lsb)"
        s"""assert ($actualSlice = $expectedVhdl) report "Wrong data at step $t ($msb downto $lsb).";"""
    }
  }

  /** Find the expected output by passing all the given inputs. Also record
    * which parameter each input corresponds to.
    */
  private def getExpectedOutput(
      e: Expr,
      inputs: Seq[DirectTestInput]
  ): (DirectTestOutput, Map[Param, DirectTestInput]) = {
    val (params, _) = e match {
      case s: StmBuild => (Seq(), s)
      case f: Function => VhdlGenerator.unwrapTopLevelFunction(f, rename = true)
      case e =>
        throw new IllegalArgumentException(
          s"I don't know how to find expected output for expression $e."
        )
    }
    if (inputs.length != params.length) {
      throw new IllegalArgumentException(
        s"Wrong number of arguments."
          + s" Expected ${params.length}, got ${inputs.length}."
      )
    }
    val substituted = inputs
      .foldLeft(e)({ case (acc, in) =>
        val arg = StmLiteral(in.elems.flatten: _*)()
        FunCall(acc, arg)()
      })
    val evaluated = mhir.ir.eval(substituted).asInstanceOf[StmLiteral]
    val inputByParam = params.zip(inputs).toMap
    val outputs = DirectTestOutput(evaluated.elems)
    (outputs, inputByParam)
  }
}
