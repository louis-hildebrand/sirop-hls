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
      inputs: Seq[TestInput],
      e: Expr,
      dir: Path,
      testNotReady: Boolean = true
  ): Unit = {
    val (out, inputsByVar) = getExpectedOutput(e, inputs)
    makeTestbench(inputsByVar, out, dir, testNotReady = testNotReady)
  }

  def makeTestbench(
      inputs: Seq[TestInput],
      out: TestOutput,
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
      inputsByVar: Map[Param, TestInput],
      out: TestOutput,
      dir: Path,
      testNotReady: Boolean
  ): Unit = {
    val outElemType = VhdlType(out.elems.head.tchk().typ)
    val inputProcesses = inputsByVar
      .map({ case (x, inputs) =>
        val steps = inputs.elems
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
      })
      .mkString("\n\n")
    val testSteps = out.elems.zipWithIndex
      .map({ case (v, i) =>
        val expected = VhdlGenerator.valueToStdLogicVector(v.tchk())
        s"""expected <= $expected;
           |wait until rising_edge(clk) and valid = '1';
           |assert(data = expected) report "Wrong `data` at step $i.";
           |""".stripMargin.stripTrailing
      })
      .mkString("\n\n")
    val assignments = (
      Seq("clk", "data", "valid", "ready")
        ++ inputsByVar.keys.flatMap(x =>
          Seq(s"${x.name}_data", s"${x.name}_valid", s"${x.name}_ready")
        )
    ).map(x => s"$x => $x").mkString(", ")
    val inputStmSignals = inputsByVar.keys
      .flatMap(x => {
        val elemType = x.typ.asInstanceOf[TyStm].t
        val vhdlType = VhdlStdLogicVec(VhdlType(elemType).bitWidth)
        Seq(
          s"signal ${x.name}_data  : ${vhdlType.vhdlName};",
          s"signal ${x.name}_valid : std_logic;",
          s"signal ${x.name}_ready : std_logic := '0';"
        )
      })
      .mkString("\n")
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
    val t0 = if (testNotReady) {
      "-2; -- to account for the two cycles where valid = '1' but ready = '0'"
    } else {
      "0;"
    }
    val expected =
      VhdlConversionGenerator.fromStdLogicVector("expected", outElemType)
    val data = VhdlConversionGenerator.fromStdLogicVector("data", outElemType)
    val str =
      s"""library IEEE;
         |use IEEE.std_logic_1164.all;
         |use IEEE.numeric_std.all;
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
         |    signal   expected   : std_logic_vector(${outElemType.bitWidth - 1} downto 0);
         |    signal   t          : integer := $t0
         |
         |    -- Easier debugging
         |    signal   data_t     : ${outElemType.vhdlName};
         |    signal   expected_t : ${outElemType.vhdlName};
         |
         |    -- Input streams
         |${indent(inputStmSignals)}
         |
         |begin
         |
         |    DUT: entity work.top port map($assignments);
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
         |${indent(inputProcesses)}
         |
         |    data_t <= $data;
         |    expected_t <= $expected;
         |
         |    -- Check outputs
         |    process
         |    begin
         |${indent(notReadyTests, 2)}
         |
         |        ready <= '1';
         |
         |${indent(testSteps, 2)}
         |
         |        wait until falling_edge(clk);
         |        report "LATENCY: " & integer'image(t) & " cycles" severity note;
         |
         |        wait until rising_edge(clk);
         |        assert(valid = '0') report "Wrong `valid` after completion";
         |
         |        test_done <= true;
         |        assert false report "Test done." severity note;
         |        wait;
         |    end process;
         |end tb;
         |""".stripMargin

    val testDir = dir / "test"
    os.makeDir.all(testDir)
    val testbenchFile = testDir / "test_top.vhd"
    if (os.isFile(testbenchFile)) os.remove(testbenchFile)
    os.write(testbenchFile, str)
  }

  /** Find the expected output by passing all the given inputs. Also record
    * which parameter each input corresponds to.
    */
  private def getExpectedOutput(
      e: Expr,
      inputs: Seq[TestInput]
  ): (TestOutput, Map[Param, TestInput]) = {
    val (params, _) = e match {
      case s: StmBuild => (Seq(), s)
      case f: Function => VhdlGenerator.unwrapTopLevelFunction(f)
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
    val outputs = TestOutput(evaluated.elems)
    (outputs, inputByParam)
  }
}
