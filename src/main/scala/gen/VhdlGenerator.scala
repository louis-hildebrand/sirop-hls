package gen

import debug.PrettyPrinter
import ir._
import opt.PartialEvalPass

import java.nio.file.{Files, Path}

object VhdlGenerator {
  def makeVhdl(s: StmBuild, dir: Path): Unit = {
    // TODO: Implement this properly
    val comment =
      PrettyPrinter.show(s)(Map()).split("\n").map(x => s"-- $x").mkString("\n")
    val valid = PartialEvalPass.partialEval(IsSome(s.output))
    val data = PartialEvalPass.partialEval(OptionUnwrapUnsafe(s.output))
    val accSignals = s.seedByVar
      .map({
        case (x, IntCst(n)) => s"    signal $x : integer := $n;"
        case _              => ???
      })
      .mkString("\n")
    val accSignalUpdates = s.nextByVar
      .map({ case (x, nxt) => s"            $x <= ${makeVhdlExpr(nxt)};" })
      .mkString("\n")
    val str = comment +
      s"""
         |library IEEE ;
         |use IEEE.std_logic_1164.all;
         |use IEEE.numeric_std.all;
         |
         |entity top is
         |port (
         |    clk        : in  std_logic;
         |    data       : out std_logic_vector(31 downto 0);
         |    data_valid : out std_logic;
         |    data_ready : in  std_logic);
         |end top;
         |
         |architecture arch of top is
         |    $accSignals
         |    signal num_outputs : Integer := 0;
         |    signal data_valid_internal : boolean;
         |    signal data_internal : integer := 0;
         |    signal transfer_ok : boolean;
         |begin
         |    data_valid_internal <=
         |        (num_outputs < ${makeVhdlExpr(s.n)})
         |        and (${makeVhdlExpr(valid)});
         |    data_valid <= '1' when data_valid_internal else '0';
         |
         |    transfer_ok <= data_ready = '1' and data_valid_internal;
         |
         |    data_internal <= ${makeVhdlExpr(data)} when transfer_ok else 0;
         |    data <= std_logic_vector(to_signed(data_internal, data'length));
         |
         |	process
         |	begin
         |        wait until rising_edge(clk);
         |        if transfer_ok then
         |            num_outputs <= num_outputs + 1;
         |            $accSignalUpdates
         |        end if;
         |	end process ;
         |end arch;
         |""".stripMargin

    val designDir = dir.resolve("design")
    Files.createDirectory(designDir)
    val topFile = designDir.resolve("top.vhd")
    Files.writeString(topFile, str)
  }

  private def makeVhdlExpr(e: Expr): String = {
    e match {
      case x: Param   => x.name
      case IntCst(n)  => n.toString
      case Sum(terms) => terms.map(e => s"(${makeVhdlExpr(e)})").mkString(" + ")
      case Prod(factors) =>
        factors.map(e => s"(${makeVhdlExpr(e)})").mkString(" * ")
      case Div(e1, e2)         => ???
      case Mod(e1, e2)         => ???
      case True                => "true"
      case False               => "false"
      case IfThenElse(c, t, f) => ???
      case Equal(e1, e2)    => s"(${makeVhdlExpr(e1)}) = (${makeVhdlExpr(e2)})"
      case LessThan(e1, e2) => s"(${makeVhdlExpr(e1)}) < (${makeVhdlExpr(e2)})"
      case Not(e)           => s"not (${makeVhdlExpr(e)})"
      case And(terms @ _*) =>
        terms.map(e => s"(${makeVhdlExpr(e)})").mkString(" and ")
      case Or(terms @ _*) =>
        terms.map(e => s"(${makeVhdlExpr(e)})").mkString(" or ")
      case Tuple(elems @ _*)              => ???
      case TupleAccess(t, i)              => ???
      case Function(param, body)          => ???
      case FunCall(f, arg)                => ???
      case Default                        => ???
      case StmBuild(n, output, equations) => ???
      case StmNext(stream)                => ???
      case VecBuild(len, f)               => ???
      case VecAccess(vec, i)              => ???
      case VecLiteral(elems @ _*)         => ???
      case StmLiteral(elems @ _*)         => ???
      case StmNextK(s, k)                 => ???
    }
  }
}
