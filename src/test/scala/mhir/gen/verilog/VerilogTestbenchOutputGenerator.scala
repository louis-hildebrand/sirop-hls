package mhir.gen.verilog

import mhir.debug.indent
import mhir.gen.Undefined
import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import os.Path

import scala.annotation.tailrec

object VerilogTestbenchOutputGenerator {
  def getNames(output: TestOutput): Seq[String] = {
    def names(prefix: String)(typ: Type): Seq[String] = {
      typ match {
        case TyBool | _: TyAnyInt | _: TyFix => Seq(prefix)
        case TyTuple(ts @ _*) =>
          ts.zipWithIndex.flatMap({ case (t, i) => names(s"${prefix}_$i")(t) })
        case TyVec(t, IntCst(n)) =>
          (0 until n.toInt).flatMap(i => names(s"${prefix}_$i")(t))
        case t =>
          throw new IllegalArgumentException(s"Invalid element type: $t")
      }
    }

    names("O")(output.elemTyp)
  }

  def getDirectOutputDecls(output: DirectTestOutput): String = {
    val portWidths = widthByPort(output)
    val portDecls = portWidths
      .map({ case (port, w) => s"wire [${w - 1}:0] $port;" })
      .mkString("\n")
    s"""// Outputs
       |$portDecls
       |""".stripMargin.stripTrailing
  }

  def getFileOutputDecls(out: TestOutputFromFile): String = {
    val portWidths = widthByPort(out)
    val portDecls = portWidths
      .map({ case (port, w) => s"wire [${w - 1}:0] $port;" })
      .mkString("\n")
    val totWidth = portWidths.values.sum
    s"""// Outputs
       |$portDecls
       |reg [${totWidth - 1}:0] output_data_ram [0:${out.len}];
       |reg [${totWidth - 1}:0] output_mask_ram [0:${out.len}];
       |""".stripMargin.stripTrailing
  }

  def getDirectOutputBlock(output: DirectTestOutput): String = {
    val outAtomWidth = getAtomWidth(output.elems.head.typ)
    val outputChecks = outputMap(output)
      .map(valByPort => {
        val checks = valByPort
          .map({ case (port, v) =>
            val validCheck = v match {
              case None =>
                s"// value for $port is undefined"
              case Some(v) =>
                s"check_output($v, $port);"
            }
            val skips =
              s"""// Skip ${output.skip} invalid elements
                 |for (j = 0; j < ${output.skip}; j = j + 1) wait_for_output();
                 |""".stripMargin.stripTrailing
            validCheck + "\n" + skips
          })
          .mkString("\n")
        s"wait_for_output();\n$checks"
      })
      .mkString("\n\n")
    s"""// Output checking
       |
       |task prepare_outputs ();
       |begin
       |    $$display("Nothing to prepare: outputs are hard-coded in testbench source.");
       |end
       |endtask
       |
       |task wait_for_output ();
       |begin
       |    wait(clock == 1 && valid_down);
       |    wait(clock == 0);
       |end
       |endtask
       |
       |task check_output (input [${outAtomWidth - 1}:0] expected, input [${outAtomWidth - 1}:0] actual);
       |begin
       |    $$display("OUTPUT : %d", actual);
       |    if (actual !== expected) begin
       |        $$error("ASSERTION FAILED: expected %d, got %d", expected, actual);
       |    end
       |end
       |endtask
       |
       |initial begin : out_check
       |    integer j;
       |
       |    $$display("Started output checker.");
       |
       |${indent(outputChecks)}
       |
       |    @(negedge clock) begin
       |        $$display("LATENCY: %d cycles", t);
       |    end
       |
       |    $$display("Simulation done.");
       |    $$stop(0);
       |end
       |""".stripMargin.stripTrailing
  }

  def getFileOutputBlock(out: TestOutputFromFile): String = {
    val totWidth = widthByPort(out).values.sum
    val outPortList = getNames(out).mkString("{ ", ", ", " }")
    s"""// Output checking
       |
       |task read_output_data ();
       |    integer fd, i, code;
       |begin
       |    $$display("Reading output data from ${out.data}...");
       |    fd = $$fopen("${out.data}", "r");
       |    if (fd === 0) begin
       |        $$error("Failed to open output data file.");
       |        $$stop(0);
       |    end
       |    for (i = 0; i < ${out.len}; i = i + 1) begin
       |        code = $$fscanf(fd, "%b\\n", output_data_ram[i]);
       |        if (code != 1) begin
       |            $$error("An error occurred while reading output data file (step %d).", i);
       |            $$fclose(fd);
       |            $$stop(0);
       |        end
       |    end
       |    $$fclose(fd);
       |    $$display("Done reading output data from file.");
       |end
       |endtask
       |
       |task read_output_masks ();
       |    integer fd, i, code;
       |begin
       |    $$display("Reading output masks from ${out.mask}...");
       |    fd = $$fopen("${out.mask}", "r");
       |    if (fd === 0) begin
       |        $$error("Failed to open output mask file.");
       |        $$stop(0);
       |    end
       |    for (i = 0; i < ${out.len}; i = i + 1) begin
       |        code = $$fscanf(fd, "%b\\n", output_mask_ram[i]);
       |        if (code != 1) begin
       |            $$error("An error occurred while reading output mask file (step %d).", i);
       |            $$fclose(fd);
       |            $$stop(0);
       |        end
       |    end
       |    $$fclose(fd);
       |    $$display("Done reading output mask from file.");
       |end
       |endtask
       |
       |task prepare_outputs ();
       |begin
       |    read_output_data();
       |    read_output_masks();
       |end
       |endtask
       |
       |task wait_for_output ();
       |begin
       |    wait(clock == 1 && valid_down);
       |    wait(clock == 0);
       |end
       |endtask
       |
       |initial begin : out_check
       |    integer i, j;
       |    reg [${totWidth - 1}:0] data;
       |    reg [${totWidth - 1}:0] expected;
       |    reg [${totWidth - 1}:0] mask;
       |    reg [${totWidth - 1}:0] masked_data;
       |    reg [${totWidth - 1}:0] masked_expected;
       |
       |    $$display("Started output checker.");
       |
       |    for (i = 0; i < ${out.len}; i = i + 1) begin
       |        wait_for_output();
       |        data = $outPortList;
       |        expected = output_data_ram[i];
       |        mask = output_mask_ram[i];
       |        masked_data = data & mask;
       |        masked_expected = expected & mask;
       |        $$display("OUTPUT : %h", data);
       |        if (masked_data !== masked_expected) begin
       |            $$error("ASSERTION FAILED: expected %h, got %h", masked_expected, masked_data);
       |        end
       |
       |        // Skip ${out.skip} invalids
       |        for (j = 0; j < ${out.skip}; j = j + 1) begin
       |            wait_for_output();
       |            expected = 'x;
       |            mask = 'x;
       |            masked_data = 'x;
       |            masked_expected = 'x;
       |        end
       |    end
       |
       |    @(negedge clock) begin
       |        $$display("LATENCY: %d cycles", t);
       |    end
       |
       |    $$display("Simulation done.");
       |    $$stop(0);
       |end
       |""".stripMargin.stripTrailing
  }

  def emitOutputDataFile(f: Path, out: DirectTestOutput): Unit = {
    for (v <- out.elems) {
      val str = valueToBinary(v)
      os.write.append(f, s"$str\n")
    }
  }

  def emitOutputMaskFile(f: Path, out: DirectTestOutput): Unit = {
    for (v <- out.elems) {
      val str = getMask(v.tchk())
      os.write.append(f, s"$str\n")
    }
  }

  private def widthByPort(output: TestOutput): Map[String, Int] = {
    def portsToWidths(prefix: String)(typ: Type): Map[String, Int] = {
      typ match {
        case TyBool      => Map(prefix -> 1)
        case t: TyAnyInt => Map(prefix -> t.w)
        case t: TyFix    => Map(prefix -> t.t.w)
        case TyVec(t, IntCst(n)) =>
          (0 until n.toInt)
            .flatMap(i => portsToWidths(s"${prefix}_$i")(t))
            .toMap
        case _ =>
          ???
      }
    }

    portsToWidths("O")(output.elemTyp)
  }

  private def outputMap(
      output: DirectTestOutput
  ): Seq[Map[String, Option[String]]] = {
    def portsToValues(prefix: String)(e: Expr): Map[String, Option[String]] = {
      e match {
        case _: Undefined => Map(prefix -> None)
        case False        => Map(prefix -> Some("0"))
        case True         => Map(prefix -> Some("1"))
        case c: IntCst =>
          val w = c.typ.asInstanceOf[TyAnyInt].w
          if (c.i < 0) {
            Map(prefix -> Some(s"-$w'd${-c.i}"))
          } else {
            Map(prefix -> Some(s"$w'd${c.i}"))
          }
        case VecLiteral(elems @ _*) =>
          elems.zipWithIndex
            .flatMap({ case (e, i) =>
              portsToValues(s"${prefix}_$i")(e)
            })
            .toMap
        case _ =>
          ???
      }
    }

    output.elems.map(portsToValues("O"))
  }

  @tailrec
  private def getAtomWidth(t: Type): Int = {
    t match {
      case Missing =>
        throw new IllegalArgumentException(
          s"Cannot find bit width for type $Missing."
        )
      case TyBool      => 1
      case TyAnyInt(w) => w
      case TyVec(t, _) => getAtomWidth(t)
      case _ =>
        ???
    }
  }

  // TODO: Merge this with VhdlGenerator.valueToStdLogicVector?
  private def valueToBinary(e: Expr): String = {
    e match {
      case Undefined(typ) => valueToBinary(mhir.ir.eval(Default(typ)))
      case False          => "0"
      case True           => "1"
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

  private def bitWidth(typ: Type): Int = {
    typ match {
      case typ: TyAnyInt       => typ.w
      case typ: TyFix          => typ.t.w
      case TyBool              => 1
      case TyTuple(ts @ _*)    => ts.map(bitWidth).sum
      case TyVec(t, IntCst(n)) => bitWidth(t) * n.toInt
      case typ =>
        throw new IllegalArgumentException(
          s"Cannot get bit width for type $typ."
        )
    }
  }

  private def getMask(expected: Expr): String = {
    require(expected.hasType)
    expected match {
      case VecLiteral(elems @ _*) =>
        elems.map(getMask).mkString("")
      case Tuple(elems @ _*) =>
        elems.map(getMask).mkString("")
      case Undefined(typ) =>
        "0" * bitWidth(typ)
      case v =>
        assert(!v.contains(classOf[Undefined]))
        "1" * bitWidth(v.typ)
    }
  }
}
