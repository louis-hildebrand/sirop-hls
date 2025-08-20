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

  def makeFileBasedTestbench(
      inputs: Seq[DirectTestInput],
      out: DirectTestOutput,
      dir: Path,
      testNotReady: Boolean = false
  ): Unit = {
    val directInputsByVar = toNamedArgs(inputs)
    val fileInputsByVar = directInputsByVar.map({ case (x, in) =>
      val dataFile = dir / "test" / s"${x.name}_data.txt"
      val validFile = dir / "test" / s"${x.name}_valid.txt"
      x -> TestInputFromFiles(
        data = dataFile,
        valid = validFile,
        elemTyp = in.elemTyp,
        len = in.len
      )
    })
    val testDir = dir / "test"
    os.makeDir.all(testDir)
    for ((x, in) <- directInputsByVar) {
      val fIn = fileInputsByVar(x)
      emitTestInputDataFile(fIn.data, in)
      emitTestInputValidFile(fIn.valid, in)
    }
    makeTestbench(
      inputsByVar = fileInputsByVar,
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
      out: DirectTestOutput,
      dir: Path,
      testNotReady: Boolean
  ): Unit = {
    val outElemType = VhdlType(out.elemTyp)
    val portMap = getPortMap(inputsByVar.keys.toSeq)
    val inputStreamSignals = inputsByVar
      .map({
        case (x, _: DirectTestInput)     => getInputStreamDecls(x)
        case (x, in: TestInputFromFiles) => getInputStreamDecls(x, in)
      })
      .mkString("\n\n")
    val inputStreamProcesses = inputsByVar
      .map({
        case (x, in: DirectTestInput)   => getInputStreamProcess(x, in)
        case (x, _: TestInputFromFiles) => getInputStreamProcess(x)
      })
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

  private def toNamedArgs[T <: TestInput](inputs: Seq[T]): Map[Param, T] = {
    inputs.zipWithIndex
      .map({ case (in, i) =>
        val x = Param(s"I$i", -1)(TyStm(in.elemTyp, -1))
        x -> in
      })
      .toMap
  }

  private def emitTestInputDataFile(f: Path, in: DirectTestInput): Unit = {
    for (x <- in.elems) {
      val str = x match {
        case Some(v) => valueToBinary(v)
        case None    => valueToBinary(mhir.ir.eval(Default(in.elemTyp)))
      }
      os.write.append(f, s"$str\n")
    }
  }

  private def valueToBinary(e: Expr): String = {
    e match {
      case False => "0"
      case True  => "1"
      case c: IntCst =>
        val w = c.typ.asInstanceOf[TyAnyInt].w
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
          padded
        }
      case c: FixCst =>
        valueToBinary(C(c.numer)(c.typ.t))
      case Tuple(elems @ _*) =>
        elems.map(valueToBinary).mkString("")
      case VecLiteral(elems @ _*) =>
        elems.map(valueToBinary).mkString("")
      case _ => ???
    }
  }

  private def emitTestInputValidFile(f: Path, in: DirectTestInput): Unit = {
    for (x <- in.elems) {
      val str = x match {
        case Some(_) => "true"
        case None    => "false"
      }
      os.write.append(f, s"$str\n")
    }
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

  private def getInputStreamDecls(x: Param, in: TestInputFromFiles): String = {
    val elemBitWidth = VhdlType(in.elemTyp).bitWidth
    s"""constant ${x.name}_LEN   : natural := ${in.len};
       |constant ${x.name}_WIDTH : natural := $elemBitWidth;
       |type ${x.name}_data_ram_type is array (0 to ${x.name}_LEN-1) of std_logic_vector(${x.name}_WIDTH-1 downto 0);
       |type ${x.name}_valid_ram_type is array (0 to ${x.name}_LEN-1) of std_logic;
       |
       |impure function init_${x.name}_data_ram return ${x.name}_data_ram_type is
       |    file f : text open read_mode is "${in.data}";
       |    variable ok : boolean;
       |    variable current_line : line;
       |    variable ram : ${x.name}_data_ram_type;
       |    variable bv : bit_vector(${x.name}_WIDTH-1 downto 0);
       |begin
       |    for i in 0 to ${x.name}_LEN - 1 loop
       |        readline(f, current_line);
       |        read(current_line, bv, ok);
       |        assert ok report "Failed to read file (line " & integer'image(i) & ").";
       |        ram(i) := to_stdlogicvector(bv);
       |    end loop;
       |    return ram;
       |end function;
       |
       |impure function init_${x.name}_valid_ram return ${x.name}_valid_ram_type is
       |    file f : text open read_mode is "${in.valid}";
       |    variable ok : boolean;
       |    variable current_line : line;
       |    variable ram : ${x.name}_valid_ram_type;
       |    variable b : boolean;
       |begin
       |    for i in 0 to ${x.name}_LEN - 1 loop
       |        readline(f, current_line);
       |        read(current_line, b, ok);
       |        assert ok report "Failed to read file (line " & integer'image(i) & ").";
       |        ram(i) := bool2sl(b);
       |    end loop;
       |    return ram;
       |end function;
       |
       |signal ${x.name}_data_ram : ${x.name}_data_ram_type := init_${x.name}_data_ram;
       |signal ${x.name}_valid_ram : ${x.name}_valid_ram_type := init_${x.name}_valid_ram;
       |signal ${x.name}_data  : std_logic_vector(${x.name}_WIDTH-1 downto 0);
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

  private def getInputStreamProcess(x: Param): String = {
    s"""-- Generate inputs (${x.name})
       |process
       |begin
       |    for i in 0 to ${x.name}_LEN - 1 loop
       |        ${x.name}_valid <= ${x.name}_valid_ram(i);
       |        ${x.name}_data <= ${x.name}_data_ram(i);
       |        wait until rising_edge(clk) and sl2bool(${x.name}_ready);
       |    end loop;
       |
       |    report "Finished giving inputs for ${x.name}." severity note;
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
