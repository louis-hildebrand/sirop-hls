package mhir.gen.verilog

import mhir.debug.indent
import mhir.gen.Undefined
import mhir.ir._
import os.Path

private[verilog] object VerilogTestbenchInputGenerator {
  def getNames(inputs: TestInput): Seq[String] = {
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

    inputs.elemTypes.zipWithIndex.flatMap({ case (t, i) =>
      val prefix = if (inputs.numStreams == 1) "I" else s"I$i"
      names(prefix)(t)
    })
  }

  def getDirectInputDecls(inputs: DirectTestInput): String = {
    val ports = widthByPort(inputs)
      .map({ case (name, w) => s"reg [${w - 1}:0] $name;" })
      .mkString("\n")
    s"""// Inputs
       |$ports
       |""".stripMargin.stripTrailing
  }

  def getFileInputDecls(in: TestInputFromFile): String = {
    val portWidths = widthByPort(in)
    val ports = portWidths
      .map({ case (name, w) => s"reg [${w - 1}:0] $name;" })
      .mkString("\n")
    val totWidth = portWidths.values.sum
    s"""// Inputs
       |$ports
       |reg [${totWidth - 1}:0] input_data_ram [0:${in.len - 1}];
       |""".stripMargin.stripTrailing
  }

  def getDirectInputBlock(inputs: DirectTestInput): String = {
    val ports = getNames(inputs)
    val inputAssignments = inputs.steps
      .map({ elems =>
        val lhs = ports.mkString("{ ", ", ", " }")
        val rhs = toVerilog(elems)
        s"""@(negedge clock) begin
             |    valid_up = 1;
             |    $lhs = $rhs;
             |end
             |""".stripMargin.stripTrailing
      })
      .mkString("\n\n")
    s"""// Input generation
       |
       |task prepare_inputs ();
       |begin
       |    $$display("Nothing to prepare: inputs are hard-coded in testbench source.");
       |end
       |endtask
       |
       |initial begin : in_gen
       |    $$display("Started test stimuli generator.");
       |    valid_up = 0;
       |    initialize();
       |
       |${indent(inputAssignments)}
       |end
       |""".stripMargin.stripTrailing
  }

  def getFileInputBlock(in: TestInputFromFile): String = {
    val portList = getNames(in).mkString("{ ", ", ", " }")
    s"""// Input generation
       |
       |task prepare_inputs ();
       |    integer fd, i, code;
       |begin
       |    $$display("Reading inputs from ${in.f} ...");
       |    fd = $$fopen("${in.f}", "r");
       |    if (fd === 0) begin
       |        $$error("Failed to open input file.");
       |        $$stop(0);
       |    end
       |    for (i = 0; i < ${in.len}; i = i + 1) begin
       |        if ((i & 32'h0000ffff) == 0) begin
       |            $$display("%d%%", (100 * i) / ${in.len});
       |        end
       |        code = $$fscanf(fd, "%b\\n", input_data_ram[i]);
       |        if (code != 1) begin
       |            $$error("An error occurred while reading input file (step %d).", i);
       |            $$fclose(fd);
       |            $$stop(0);
       |        end
       |    end
       |    $$fclose(fd);
       |    $$display("Done reading inputs from file.");
       |end
       |endtask
       |
       |initial begin : in_gen
       |    integer i;
       |
       |    $$display("Started test stimuli generator.");
       |    valid_up = 0;
       |    initialize();
       |
       |    for (i = 0; i < ${in.len}; i = i + 1) begin
       |        @(negedge clock) begin
       |            valid_up = 1;
       |            $portList = input_data_ram[i];
       |        end
       |    end
       |end
       |""".stripMargin.stripTrailing
  }

  def emitInputDataFile(f: Path, in: DirectTestInput): Unit = {
    for (elems <- in.steps) {
      val str = elems.map(valueToBinary).mkString("")
      os.write.append(f, s"$str\n")
    }
  }

  private def widthByPort(inputs: TestInput): Map[String, Int] = {
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

    inputs.elemTypes.zipWithIndex
      .flatMap({ case (typ, i) =>
        val prefix = if (inputs.numStreams == 1) "I" else s"I$i"
        portsToWidths(prefix)(typ)
      })
      .toMap
  }

  private def toVerilog(exprs: Seq[Expr]): String = {
    exprs
      .map({
        case False => "0"
        case True  => "1"
        case c: IntCst =>
          val w = c.typ.asInstanceOf[TyAnyInt].w
          if (c.i < 0) {
            s"-$w'd${-c.i}"
          } else {
            s"$w'd${c.i}"
          }
        case c: FixCst =>
          toVerilog(Seq(C(c.numer)(c.typ.t)))
        case VecLiteral(elems @ _*) =>
          toVerilog(elems)
        case _ =>
          ???
      })
      .mkString("{ ", ", ", " }")
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
}
