package mhir.gen.verilog

import mhir.debug.indent
import mhir.gen.Binary
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
          ts.zipWithIndex
            .flatMap({ case (t, i) => names(s"${prefix}_t${i}b")(t) })
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
    val outAtomWidth = getAtomWidth(output.elemTyp)
    val outMap = outputMap(output).toSeq
    val outputChecks = outMap.zipWithIndex
      .map({ case (valByPort, t) =>
        val checks = valByPort
          .map({ case (port, v) =>
            val validCheck = v match {
              case None    => s"// value for $port is undefined"
              case Some(v) => s"check_output($v, $port);"
            }
            val skips = if (t == outMap.size - 1 || output.skip <= 0) {
              "// Ignore trailing invalids"
            } else {
              s"""// Skip ${output.skip} invalid elements
                 |for (j = 0; j < ${output.skip}; j = j + 1) wait_for_output();
                 |""".stripMargin.stripTrailing
            }
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
    val bitsPerRow = Binary.paddedBitWidth(out.elemTyp)
    s"""// Output checking
       |
       |task read_output_data ();
       |    integer fd, i, msb;
       |begin
       |    $$display("Reading output data from ${out.data}...");
       |    fd = $$fopen("${out.data}", "r");
       |    if (fd === 0) begin
       |        $$error("Failed to open output data file.");
       |        $$stop(0);
       |    end
       |    for (i = 0; i < ${out.len}; i = i + 1) begin
       |        if ((i & 32'h0000ffff) == 0) begin
       |            $$display("%d%%", (100 * i) / ${out.len});
       |        end
       |        for (msb = ${bitsPerRow - 1}; msb >= 7; msb = msb - 8) begin
       |            output_data_ram[i][msb -: 8] = $$fgetc(fd);
       |        end
       |    end
       |    $$fclose(fd);
       |    $$display("Done reading output data from file.");
       |end
       |endtask
       |
       |task read_output_masks ();
       |    integer fd, i, msb;
       |begin
       |    $$display("Reading output masks from ${out.mask}...");
       |    fd = $$fopen("${out.mask}", "r");
       |    if (fd === 0) begin
       |        $$error("Failed to open output mask file.");
       |        $$stop(0);
       |    end
       |    for (i = 0; i < ${out.len}; i = i + 1) begin
       |        if ((i & 32'h0000ffff) == 0) begin
       |            $$display("%d%%", (100 * i) / ${out.len});
       |        end
       |        for (msb = ${bitsPerRow - 1}; msb >= 7; msb = msb - 8) begin
       |            output_mask_ram[i][msb -: 8] = $$fgetc(fd);
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
       |        if ((i & 32'h0000ffff) == 0) begin
       |            $$display("%d%%", (100 * i) / ${out.len});
       |        end
       |        wait_for_output();
       |        data = $outPortList;
       |        expected = output_data_ram[i];
       |        mask = output_mask_ram[i];
       |        masked_data = data & mask;
       |        masked_expected = expected & mask;
       |        if (masked_data !== masked_expected) begin
       |            $$error("ASSERTION FAILED: expected %h, got %h", masked_expected, masked_data);
       |        end
       |
       |        if (i != ${out.len - 1}) begin
       |            // Skip ${out.skip} invalids
       |            for (j = 0; j < ${out.skip}; j = j + 1) begin
       |                wait_for_output();
       |                expected = 'x;
       |                mask = 'x;
       |                masked_data = 'x;
       |                masked_expected = 'x;
       |            end
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

  def emitOutputFiles(data: Path, mask: Path, out: DirectTestOutput): Unit = {
    for (xs <- out.elements.grouped(1000)) {
      val binaryData = xs.map(Binary(_))
      os.write.append(data, binaryData)

      val binaryMask = xs.map(v => Binary.mask(v.tchk()))
      os.write.append(mask, binaryMask)
    }
  }

  private def widthByPort(output: TestOutput): Map[String, Int] = {
    def portsToWidths(prefix: String)(typ: Type): Map[String, Int] = {
      typ match {
        case TyBool      => Map(prefix -> 1)
        case t: TyAnyInt => Map(prefix -> t.w)
        case t: TyFix    => Map(prefix -> t.t.w)
        case TyTuple(ts @ _*) =>
          ts.zipWithIndex
            .flatMap({ case (t, i) => portsToWidths(s"${prefix}_t${i}b")(t) })
            .toMap
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
  ): Iterator[Map[String, Option[String]]] = {
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
        case Tuple(elems @ _*) =>
          elems.zipWithIndex
            .flatMap({ case (e, i) => portsToValues(s"${prefix}_t${i}b")(e) })
            .toMap
        case VecLiteral(elems @ _*) =>
          elems.zipWithIndex
            .flatMap({ case (e, i) => portsToValues(s"${prefix}_$i")(e) })
            .toMap
        case _ =>
          ???
      }
    }

    output.elements.map(portsToValues("O"))
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
}
