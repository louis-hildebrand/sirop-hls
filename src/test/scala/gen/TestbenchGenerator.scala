package gen

import ir._

import java.nio.file.{Files, Path}

object TestbenchGenerator {
  def makeTestbench(out: StmLiteral, bitWidth: Int, dir: Path): Unit = {
    val testSteps = out.elems.zipWithIndex
      .map({ case (v, i) =>
        val expected =
          VhdlGenerator.valueToStdLogicVector(v.tchk(), "expected'length")
        s"""    expected <= ${expected};
           |    wait until rising_edge(clk) and data_valid = '1';
           |    assert(data = expected) report "Wrong `data` at step $i.";
           |""".stripMargin
      })
      .mkString("\n\n")
    val str =
      s"""library IEEE;
         |use IEEE.std_logic_1164.all;
         |use IEEE.numeric_std.all;
         |
         |entity testbench is
         |    -- empty
         |end testbench;
         |
         |architecture tb of testbench is
         |
         |    constant CLK_CYCLE  : time := 20 ns;
         |    signal   test_done  : boolean := false;
         |    signal   clk        : std_logic := '0';
         |    signal   data       : std_logic_vector(${bitWidth - 1} downto 0);
         |    signal   data_valid : std_logic;
         |    signal   data_ready : std_logic := '0';
         |    signal   expected   : std_logic_vector(${bitWidth - 1} downto 0);
         |
         |begin
         |
         |    DUT: entity work.top port map(clk => clk, data => data, data_valid => data_valid, data_ready => data_ready);
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
         |    -- Run tests
         |    process
         |    begin
         |        -- What happens if the consumer is not ready?
         |        expected <= (others => '0');
         |        data_ready <= '0';
         |        wait until rising_edge(clk) and data_valid = '1';
         |        assert(data = expected) report "Wrong `data` when data_ready = '0'.";
         |        data_ready <= '1';
         |
         |        $testSteps
         |
         |        expected <= (others => '0');
         |        wait until rising_edge(clk);
         |        assert(data = expected) report "Wrong `data` after completion.";
         |        assert(data_valid = '0') report "Wrong `valid` after completion";
         |
         |        test_done <= true;
         |        assert false report "Test done." severity note;
         |        wait;
         |    end process;
         |end tb;
         |""".stripMargin

    val testDir = dir.resolve("test")
    Files.createDirectory(testDir)
    val testbenchFile = testDir.resolve("test_top.vhd")
    Files.writeString(testbenchFile, str)
  }
}
