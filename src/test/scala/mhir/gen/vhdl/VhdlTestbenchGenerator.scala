package mhir.gen
package vhdl

import com.typesafe.scalalogging.Logger
import mhir.debug.indent
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.logging.time
import mhir.sugar.Default
import org.slf4j.event.Level
import os.Path

object VhdlTestbenchGenerator {

  private implicit val logger: Logger = Logger(getClass.getName)

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
      inputs: Seq[Seq[DirectTestInput]],
      e: Expr,
      dir: Path,
      testNotReady: Boolean = true
  ): Unit = {
    require(inputs.nonEmpty)
    val io = TestSuiteIO(inputs.map({ in =>
      val (out, inputsByVar) = getExpectedOutput(e, in)
      KeywordTestIO(inputsByVar, out)
    }))
    makeTestbench(io, dir, testNotReady = testNotReady)
  }

  /** Make a testbench where all inputs and expected outputs are stored in
    * files.
    *
    * @param io
    *   the expected inputs and outputs. These may or may not already be stored
    *   in files.
    * @param dir
    *   the directory of the VHDL project (the whole directory containing the
    *   design, not the test subdirectory).
    * @param testNotReady
    *   whether to test that the design behaves correctly when the consumer is
    *   not ready. If `false`, the testbench will immediately raise the `ready`
    *   signal and keep it raised the whole time.
    */
  def makeFileBasedTestbench(
      io: PositionalTestIO,
      dir: Path,
      testNotReady: Boolean = false
  ): Unit = {
    val testDir = dir / "test"
    os.makeDir.all(testDir)

    val inputsByVar = toNamedArgs(io.inputs)
    val fileInputsByVar = inputsByVar.map({
      case (x, in: DirectTestInput) =>
        val dataFile = testDir / s"${x.name}_data.bin"
        val validFile = testDir / s"${x.name}_valid.bin"
        val fileInput = TestInputFromFiles(
          data = dataFile,
          valid = validFile,
          elemTyp = in.elemTyp,
          len = in.len
        )
        time("writing input files", Level.DEBUG) {
          emitTestInputFiles(data = fileInput.data, valid = fileInput.valid, in)
        }
        x -> fileInput
      case (x, in: TestInputFromFiles) => x -> in
    })

    val fileOutput = io.expectedOutput match {
      case out: DirectTestOutput =>
        val dataFile = testDir / "out_data.bin"
        val maskFile = testDir / "out_mask.bin"
        val fileOutput = TestOutputFromFile(
          data = dataFile,
          mask = maskFile,
          elemTyp = io.expectedOutput.elemTyp,
          len = io.expectedOutput.len
        )
        time("writing output files", Level.DEBUG) {
          emitTestOutputFiles(
            data = fileOutput.data,
            mask = fileOutput.mask,
            out
          )
        }
        fileOutput
      case out: TestOutputFromFile => out
    }

    makeTestbench(
      io = TestSuiteIO(Seq(KeywordTestIO(fileInputsByVar, fileOutput))),
      dir = dir,
      testNotReady = testNotReady
    )
  }

  def makeDirectTestbench(
      inputs: Seq[DirectTestInput],
      expectedOutput: DirectTestOutput,
      dir: Path,
      testNotReady: Boolean
  ): Unit = {
    val testDir = dir / "test"
    os.makeDir.all(testDir)

    val inputsByVar = toNamedArgs(inputs)

    makeTestbench(
      io = TestSuiteIO(Seq(KeywordTestIO(inputsByVar, expectedOutput))),
      dir = dir,
      testNotReady = testNotReady
    )
  }

  /** Creates a testbench for a VHDL design.
    *
    * @param io
    *   the expected inputs and outputs.
    * @param dir
    *   the directory of the VHDL project (the whole directory containing the
    *   design, not the `test` subdirectory).
    */
  private def makeTestbench(
      io: TestSuiteIO,
      dir: Path,
      testNotReady: Boolean
  ): Unit = {
    val outElemType = VhdlType(io.outElemTyp)
    val portMap = getPortMap(io.params)
    val sharedDecls = getSharedDecls(io)
    val testCaseDecls = io.tests.zipWithIndex
      .map({ case (io, idx) => getTestDecls(io, idx) })
      .mkString("\n\n")
    val testCaseProcedures = io.tests.zipWithIndex
      .map({ case (io, idx) => getTestProcedure(io, idx) })
      .mkString("\n\n")
    val numTests = io.tests.length
    val testCaseProcesses = io.tests.zipWithIndex
      .map({ case (io, idx) =>
        getTestProcesses(
          io,
          idx,
          last = idx == numTests - 1,
          testNotReady = testNotReady
        )
      })
      .mkString("\n\n")
    val testProcedureCalls = io.tests.indices
      .map({ i =>
        s"""    wait until falling_edge(clk);
           |    reset <= '1';
           |    wait until falling_edge(clk);
           |    reset <= '0';
           |
           |    run_test_$i;
           |""".stripMargin.stripTrailing
      })
      .mkString("\n\n")
    val mainTestProcess =
      s"""main : process
         |${indent(testCaseProcedures, 2)}
         |begin
         |$testProcedureCalls
         |
         |    wait until falling_edge(clk);
         |    test_done <= true;
         |    assert false report "Test done." severity note;
         |    wait;
         |end process;
         |""".stripMargin
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
         |    constant CLK_CYCLE   : time := 20 ns;
         |    signal   test_done   : boolean := false;
         |    signal   clk         : std_logic := '1';
         |    signal   reset       : std_logic := '0';
         |    signal   t           : integer := -1;
         |
         |    -- Easier debugging
         |    signal   data_t     : ${outElemType.vhdlName};
         |
         |    -- Binary file I/O
         |    type binary_file is file of character;
         |
         |${indent(sharedDecls)}
         |
         |${indent(testCaseDecls)}
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
         |        wait until test_0_start and rising_edge(clk);
         |        t <= t + 1;
         |    end process;
         |
         |${indent(testCaseProcesses)}
         |
         |${indent(mainTestProcess)}
         |end tb;
         |""".stripMargin

    val testDir = dir / "test"
    os.makeDir.all(testDir)
    val testbenchFile = testDir / "test_top.vhd"
    if (os.isFile(testbenchFile)) os.remove(testbenchFile)
    os.write(testbenchFile, str)
  }

  private def toNamedArgs[T <: TestInput](inputs: Seq[T]): Map[Param, T] = {
    inputs.zipWithIndex
      .map({ case (in, i) =>
        val x = Param(s"I$i", -1)(TyStm(in.elemTyp, -1))
        x -> in
      })
      .toMap
  }

  private val ChunkSize = 1000

  private def emitTestInputFiles(
      data: Path,
      valid: Path,
      in: DirectTestInput
  ): Unit = {
    os.remove(data)
    os.remove(valid)
    for (xs <- in.elements.grouped(ChunkSize)) {
      val binaryData = xs.map({
        case Some(v) => Binary(v)
        case None    => Binary(mhir.ir.eval(Default(in.elemTyp)))
      })
      os.write.append(data, binaryData)

      // TODO: Pack this data even further?
      val binaryValid = xs.map({
        case Some(_) => Array((0 until 8).map(_ => 1.toByte): _*)
        case None    => Array((0 until 8).map(_ => 0.toByte): _*)
      })
      os.write.append(valid, binaryValid)
    }
  }

  private def emitTestOutputFiles(
      data: Path,
      mask: Path,
      out: DirectTestOutput
  ): Unit = {
    os.remove(data)
    os.remove(mask)
    for (xs <- out.elements.grouped(ChunkSize)) {
      val binaryData = xs.map(Binary(_))
      os.write.append(data, binaryData)

      val binaryMask = xs.map(v => Binary.mask(v.tchk()))
      os.write.append(mask, binaryMask)
    }
  }

  private def getPortMap(inputVars: Seq[Param]): String = {
    val assignments = (
      Seq("clk", "reset", "data", "valid", "ready")
        ++ inputVars.flatMap(x =>
          Seq(s"${x.name}_data", s"${x.name}_valid", s"${x.name}_ready")
        )
    ).map(x => s"$x => $x").mkString(", ")
    s"DUT : entity work.top port map($assignments);"
  }

  private def getSharedDecls(io: TestSuiteIO): String = {
    (io.params.map(getSharedInputStreamDecls).mkString("\n\n")
      + getSharedOutputCheckDecls(VhdlType(io.outElemTyp)))
  }

  private def getTestDecls(io: KeywordTestIO, testIdx: Int): String = {
    val inputStreamSignals = io.inputs
      .map({
        case (_, _: DirectTestInput)     => ""
        case (x, in: TestInputFromFiles) => getInputStreamDecls(x, in)
      })
      .mkString("\n\n")
    val outStreamSignals = io.expectedOutput match {
      case _: DirectTestOutput     => ""
      case out: TestOutputFromFile => getOutputCheckDecls(out)
    }
    val inputDoneSignals = io.inputs.keys
      .map(_.name)
      .toSeq
      .sorted
      .map(name =>
        s"signal test_${testIdx}_${name}_inputs_done : boolean := false;"
      )
      .mkString("\n")
    s"""----------------------------------------------------------------------------
       |-- Test case $testIdx
       |----------------------------------------------------------------------------
       |
       |signal test_${testIdx}_start : boolean := false;
       |$inputDoneSignals
       |signal test_${testIdx}_outputs_done : boolean := false;
       |
       |$inputStreamSignals
       |
       |$outStreamSignals
       |""".stripMargin.stripTrailing
  }

  private def getTestProcedure(io: KeywordTestIO, testIdx: Int): String = {
    val allInputsDone = {
      val inputDoneSignals = io.inputs.keys
        .map(_.name)
        .toSeq
        .sorted
        .map(name => s"test_${testIdx}_${name}_inputs_done")
      if (inputDoneSignals.isEmpty) {
        "true"
      } else {
        inputDoneSignals.mkString(" and ")
      }
    }
    s"""procedure run_test_$testIdx is
       |begin
       |    test_${testIdx}_start <= true;
       |    wait until $allInputsDone and test_${testIdx}_outputs_done;
       |    report "Test $testIdx done." severity note;
       |end procedure run_test_$testIdx;
       |""".stripMargin.stripTrailing
  }

  private def getTestProcesses(
      io: KeywordTestIO,
      testIdx: Int,
      last: Boolean,
      testNotReady: Boolean
  ): String = {
    val inputProcesses = io.inputs
      .map({
        case (x, in: DirectTestInput) =>
          getInputStreamProcess(x, in, testIdx, last)
        case (x, _: TestInputFromFiles) =>
          getInputStreamProcess(x, testIdx, last)
      })
      .mkString("\n\n")
    val outCheckProcess = io.expectedOutput match {
      case out: DirectTestOutput =>
        getOutputCheckProcess(out, testIdx, testNotReady)
      case _: TestOutputFromFile =>
        getOutputCheckProcess(testNotReady, testIdx, io.expectedOutput.elemTyp)
    }
    s"""----------------------------------------------------------------------------
       |-- Test case $testIdx
       |----------------------------------------------------------------------------
       |
       |$inputProcesses
       |
       |$outCheckProcess
       |""".stripMargin.stripTrailing
  }

  /** Get signal declarations related to the input streams that are shared
    * across test cases.
    */
  private def getSharedInputStreamDecls(x: Param): String = {
    val elemType = x.typ.asInstanceOf[TyStm].t
    val vhdlType = VhdlStdLogicVec(VhdlType(elemType).bitWidth)
    s"""-- Input stream: ${x.name}
       |signal ${x.name}_data  : ${vhdlType.vhdlName};
       |signal ${x.name}_valid : std_logic;
       |signal ${x.name}_ready : std_logic := '0';
       |""".stripMargin.stripTrailing
  }

  private def getInputStreamDecls(x: Param, in: TestInputFromFiles): String = {
    val elemBitWidth = VhdlType(in.elemTyp).bitWidth
    val bytesPerRow = Binary.paddedBitWidth(in.elemTyp) / 8
    s"""constant ${x.name}_LEN   : natural := ${in.len};
       |constant ${x.name}_WIDTH : natural := $elemBitWidth;
       |
       |type ${x.name}_data_file_type is file of std_logic_vector(${x.name}_WIDTH-1 downto 0);
       |type ${x.name}_valid_file_type is file of std_logic_vector(7 downto 0);
       |type ${x.name}_data_ram_type is array (0 to ${x.name}_LEN-1) of std_logic_vector(${x.name}_WIDTH-1 downto 0);
       |type ${x.name}_valid_ram_type is array (0 to ${x.name}_LEN-1) of std_logic;
       |
       |impure function init_${x.name}_data_ram return ${x.name}_data_ram_type is
       |    file f : binary_file;
       |    variable c : character;
       |    variable row : std_logic_vector(${x.name}_WIDTH-1 downto 0);
       |    variable msb : integer range -1 to ${8 * bytesPerRow - 1};
       |    variable ram : ${x.name}_data_ram_type;
       |begin
       |    file_open(f, "${in.data}");
       |    for i in 0 to ${x.name}_LEN - 1 loop
       |        msb := ${8 * bytesPerRow - 1};
       |        while msb >= 7 loop
       |            read(f, c);
       |            row(msb downto msb-7) := std_logic_vector(to_unsigned(character'pos(c), 8));
       |            msb := msb - 8;
       |        end loop;
       |        ram(i) := row;
       |    end loop;
       |    file_close(f);
       |    return ram;
       |end function;
       |
       |impure function init_${x.name}_valid_ram return ${x.name}_valid_ram_type is
       |    file f : binary_file;
       |    variable c : character;
       |    variable byte : std_logic_vector(7 downto 0);
       |    variable ram : ${x.name}_valid_ram_type;
       |begin
       |    file_open(f, "${in.valid}");
       |    for i in 0 to ${x.name}_LEN - 1 loop
       |        read(f, c);
       |        byte := std_logic_vector(to_unsigned(character'pos(c), 8));
       |        ram(i) := byte(0);
       |    end loop;
       |    file_close(f);
       |    return ram;
       |end function;
       |
       |signal ${x.name}_data_ram : ${x.name}_data_ram_type := init_${x.name}_data_ram;
       |signal ${x.name}_valid_ram : ${x.name}_valid_ram_type := init_${x.name}_valid_ram;
       |""".stripMargin.stripTrailing
  }

  private def getInputStreamProcess(
      x: Param,
      in: DirectTestInput,
      testIdx: Int,
      last: Boolean
  ): String = {
    val steps = in.elements
      .map({
        case None =>
          s"""wait until falling_edge(clk); -- prepare input well before the next rising edge
             |${x.name}_valid <= '0';
             |${x.name}_data <= (others => '0');
             |""".stripMargin.stripTrailing
        case Some(v) =>
          val slv = VhdlGenerator.valueToStdLogicVector(v)
          s"""wait until falling_edge(clk); -- prepare input well before the next rising edge
             |${x.name}_valid <= '1';
             |${x.name}_data <= $slv;
             |wait until rising_edge(clk) and (test_${testIdx}_outputs_done or sl2bool(${x.name}_ready)); -- must wait for the design to accept the input
             |""".stripMargin.stripTrailing
      })
      .mkString("\n\n")
    s"""-- Generate inputs (${x.name})
       |process
       |begin
       |    ${x.name}_valid <= 'Z';
       |    ${x.name}_data <= (others => 'Z');
       |    wait until test_${testIdx}_start;
       |
       |${indent(steps)}
       |
       |    ${x.name}_valid <= '${if (last) "1" else "Z"}';
       |    ${x.name}_data <= (others => 'Z');
       |    test_${testIdx}_${x.name}_inputs_done <= true;
       |    report "Finished giving inputs for test case $testIdx, parameter ${x.name}." severity note;
       |    wait;
       |end process;
       |""".stripMargin.stripTrailing
  }

  private def getInputStreamProcess(
      x: Param,
      testIdx: Int,
      last: Boolean
  ): String = {
    s"""-- Generate inputs (${x.name})
       |process
       |begin
       |    ${x.name}_valid <= 'Z';
       |    ${x.name}_data <= (others => 'Z');
       |    wait until test_${testIdx}_start;
       |
       |    for i in 0 to ${x.name}_LEN - 1 loop
       |        -- Prepare input well before the next rising edge
       |        wait until falling_edge(clk);
       |        if test_${testIdx}_outputs_done then
       |            exit;
       |        end if;
       |        ${x.name}_valid <= ${x.name}_valid_ram(i);
       |        ${x.name}_data <= ${x.name}_data_ram(i);
       |        if ${x.name}_valid_ram(i) = '1' then
       |            -- Must wait until the design has accepted the input
       |            wait until rising_edge(clk) and (test_${testIdx}_outputs_done or sl2bool(${x.name}_ready));
       |        end if;
       |    end loop;
       |
       |    ${x.name}_valid <= '${if (last) "1" else "Z"}';
       |    ${x.name}_data <= (others => 'Z');
       |    test_${testIdx}_${x.name}_inputs_done <= true;
       |    report "Finished giving inputs for test case $testIdx, parameter ${x.name}." severity note;
       |    wait;
       |end process;
       |""".stripMargin.stripTrailing
  }

  private def getSharedOutputCheckDecls(outElemType: VhdlType): String = {
    s"""-- Expected outputs
       |signal data            : std_logic_vector(${outElemType.bitWidth - 1} downto 0);
       |signal valid           : std_logic;
       |signal ready           : std_logic := '0';
       |""".stripMargin.stripTrailing
  }

  private def getOutputCheckDecls(out: TestOutputFromFile): String = {
    val elemBitWidth = VhdlType(out.elemTyp).bitWidth
    val bytesPerRow = Binary.paddedBitWidth(out.elemTyp) / 8
    val initMsb = elemBitWidth - 1
    val initLsb = 8 * bytesPerRow - 8
    s"""-- Expected outputs
       |constant OUT_LEN   : natural := ${out.len};
       |constant OUT_WIDTH : natural := $elemBitWidth;
       |
       |type out_ram_type is array (0 to OUT_LEN-1) of std_logic_vector(OUT_WIDTH-1 downto 0);
       |
       |impure function init_out_data_ram return out_ram_type is
       |    file f : binary_file;
       |    variable c : character;
       |    variable row : std_logic_vector(OUT_WIDTH-1 downto 0);
       |    variable msb : integer range -1 to $initMsb;
       |    variable lsb : integer range -9 to $initLsb;
       |    variable ram : out_ram_type;
       |begin
       |    file_open(f, "${out.data}");
       |    for i in 0 to OUT_LEN - 1 loop
       |        msb := $initMsb;
       |        lsb := $initLsb;
       |        while msb >= 7 loop
       |            read(f, c);
       |            row(msb downto lsb) := std_logic_vector(to_unsigned(character'pos(c), msb - lsb + 1));
       |            msb := lsb - 1;
       |            lsb := msb - 7;
       |        end loop;
       |        ram(i) := row;
       |    end loop;
       |    file_close(f);
       |    return ram;
       |end function;
       |
       |impure function init_out_mask_ram return out_ram_type is
       |    file f : binary_file;
       |    variable c : character;
       |    variable row : std_logic_vector(OUT_WIDTH-1 downto 0);
       |    variable msb : integer range -1 to $initMsb;
       |    variable lsb : integer range -9 to $initLsb;
       |    variable ram : out_ram_type;
       |begin
       |    file_open(f, "${out.mask}");
       |    for i in 0 to OUT_LEN - 1 loop
       |        msb := $initMsb;
       |        lsb := $initLsb;
       |        while msb >= 7 loop
       |            read(f, c);
       |            row(msb downto lsb) := std_logic_vector(to_unsigned(character'pos(c), msb - lsb + 1));
       |            msb := lsb - 1;
       |            lsb := msb - 7;
       |        end loop;
       |        ram(i) := row;
       |    end loop;
       |    file_close(f);
       |    return ram;
       |end function;
       |
       |signal out_data_ram : out_ram_type := init_out_data_ram;
       |signal out_mask_ram : out_ram_type := init_out_mask_ram;
       |""".stripMargin.stripTrailing
  }

  private def getOutputCheckProcess(
      out: DirectTestOutput,
      testIdx: Int,
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
    val testSteps = out.elements
      .map({ v =>
        val checkedV = v.tchk()
        val mask = getMask(checkedV)
        val expected = expectedToVhdl(checkedV)
        s"""-- Wait until falling edge to give output time to settle down (probably not necessary, but just in case)
           |wait until falling_edge(clk) and valid = '1';
           |ready <= '1';
           |mask            := "$mask";
           |expected        := $expected;
           |masked_data     := data and mask;
           |masked_expected := expected and mask;
           |assert(masked_data = masked_expected) report "Wrong data at t = " & integer'image(t) & ".";
           |""".stripMargin.stripTrailing
      })
      .mkString("\n\n")
    val w = VhdlType(out.elemTyp).bitWidth
    s"""-- Check outputs
       |out_check_$testIdx : process
       |    variable mask            : std_logic_vector(${w - 1} downto 0);
       |    variable expected        : std_logic_vector(${w - 1} downto 0);
       |    variable masked_data     : std_logic_vector(${w - 1} downto 0);
       |    variable masked_expected : std_logic_vector(${w - 1} downto 0);
       |begin
       |    ready <= 'Z';
       |    wait until test_${testIdx}_start;
       |
       |${indent(notReadyTests)}
       |
       |${indent(testSteps)}
       |
       |    report "LATENCY: " & integer'image(t) & " cycles" severity note;
       |
       |    -- Accept the last output before moving on to the next test case
       |    wait until rising_edge(clk); wait until falling_edge(clk);
       |    ready <= 'Z';
       |    test_${testIdx}_outputs_done <= true;
       |    report "Finished checking outputs for test case $testIdx." severity note;
       |    wait;
       |end process out_check_$testIdx;
       |""".stripMargin.stripTrailing
  }

  private def getOutputCheckProcess(
      testNotReady: Boolean,
      testIdx: Int,
      elemTyp: Type
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
    val w = VhdlType(elemTyp).bitWidth
    s"""-- Check outputs
       |out_check_$testIdx : process
       |    variable mask            : std_logic_vector(${w - 1} downto 0);
       |    variable expected        : std_logic_vector(${w - 1} downto 0);
       |    variable masked_data     : std_logic_vector(${w - 1} downto 0);
       |    variable masked_expected : std_logic_vector(${w - 1} downto 0);
       |begin
       |    ready <= 'Z';
       |    wait until test_${testIdx}_start;
       |
       |${indent(notReadyTests)}
       |
       |    for i in 0 to OUT_LEN - 1 loop
       |        -- Wait until falling edge to give output time to settle down
       |        wait until falling_edge(clk) and valid = '1';
       |        ready <= '1';
       |        expected        := out_data_ram(i);
       |        mask            := out_mask_ram(i);
       |        masked_data     := data and mask;
       |        masked_expected := expected and mask;
       |        assert(masked_data = masked_expected) report "Wrong data at step " & integer'image(i) & ".";
       |    end loop;
       |
       |    report "LATENCY: " & integer'image(t) & " cycles" severity note;
       |
       |    -- Accept the last output before moving on to the next test case
       |    wait until rising_edge(clk); wait until falling_edge(clk);
       |    ready <= 'Z';
       |    test_${testIdx}_outputs_done <= true;
       |    report "Finished checking outputs for test case $testIdx." severity note;
       |    wait;
       |end process out_check_$testIdx;
       |""".stripMargin.stripTrailing
  }

  private def getMask(expected: Expr): String = {
    require(expected.hasType)
    expected match {
      case VecLiteral(elems @ _*) =>
        elems.map(getMask).mkString("")
      case Tuple(elems @ _*) =>
        elems.map(getMask).mkString("")
      case Undefined(typ) =>
        "0" * VhdlType(typ).bitWidth
      case v =>
        assert(!v.contains(classOf[Undefined]))
        "1" * VhdlType(v.typ).bitWidth
    }
  }

  private def expectedToVhdl(expected: Expr): String = {
    require(expected.hasType)
    if (!expected.contains(classOf[Undefined])) {
      valueToStdLogicVector(expected)
    } else {
      expected match {
        case Undefined(typ) =>
          "\"" + "X" * VhdlType(typ).bitWidth + "\""
        case Tuple(elems @ _*) =>
          assert(elems.nonEmpty)
          elems.map(expectedToVhdl).map(x => s"($x)").mkString(" & ")
        case VecLiteral(elems @ _*) =>
          assert(elems.nonEmpty)
          elems.map(expectedToVhdl).map(x => s"($x)").mkString(" & ")
        case _ =>
          ???
      }
    }
  }

  def valueToStdLogicVector(v: Expr): String = {
    mhir.ir.eval(v).tchk() match {
      case False => "\"0\""
      case True  => "\"1\""
      case c: IntCst =>
        c.typ.asInstanceOf[TyAnyInt] match {
          case TyAnyInt(w) if w > 31 =>
            if (c.i < 0) {
              val bin = c.i.toBinaryString
              assert(bin.head == '1')
              assert(bin.length == 64)
              val truncated = bin.takeRight(w)
              assert(truncated.head == '1')
              truncated
            } else {
              val bin = c.i.toBinaryString
              assert(bin.length <= w)
              val padded = ("0" * (w - bin.length)) + bin
              if (c.typ.isInstanceOf[TySInt]) {
                assert(padded.head == '0')
              }
              s"""\"$padded\""""
            }
          case TyUInt(w) => s"std_logic_vector(to_unsigned(${c.i}, $w))"
          case TySInt(w) => s"std_logic_vector(to_signed(${c.i}, $w))"
        }
      case c: FixCst =>
        valueToStdLogicVector(C(c.numer)(c.typ.t))
      case Tuple(elems @ _*) =>
        if (elems.isEmpty) {
          "\"\""
        } else {
          elems.map(valueToStdLogicVector).map(x => s"($x)").mkString(" & ")
        }
      case vec: VecLiteral =>
        valueToStdLogicVector(Tuple(vec.elems: _*)())
      case _ =>
        throw new IllegalArgumentException(
          s"Cannot convert value $v to a std_logic_vector. Is it really a value?"
        )
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
      case let: LetStm => (Seq(), let)
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
        val arg = StmLiteral(in.elements.flatten.toSeq: _*)()
        FunCall(acc, arg)()
      })
    val evaluated = mhir.ir.eval(substituted).asInstanceOf[StmLiteral]
    val inputByParam = params.zip(inputs).toMap
    val outputs = DirectTestOutput(evaluated.elems)
    (outputs, inputByParam)
  }
}
