package mhir.gen.verilog

import mhir.debug.indent
import mhir.ir._

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
    widthByPort(inputs)
      .map({ case (name, w) => s"reg [${w - 1}:0] $name;" })
      .mkString("\n")
  }

  def getBlock(inputs: DirectTestInput): String = {
    val ports = getNames(inputs)
    val inputAssignments = inputs.steps
      .map({ elems =>
        val lhs = ports.mkString("{ ", ", ", " }")
        val rhs = toVerilog(elems)
        s"""@(posedge clock) begin
             |    valid_up = 1;
             |    $lhs = $rhs;
             |end
             |""".stripMargin.stripTrailing
      })
      .mkString("\n\n")
    s"""task prepare_inputs ();
       |begin
       |    $$display("Nothing to prepare: inputs are hard-coded in testbench source.");
       |end
       |endtask
       |
       |initial begin : in_gen
       |    $$display("Started test stimuli generator.");
       |    valid_up = 0;
       |    prepare_inputs();
       |    prepare_outputs();
       |    initialize();
       |
       |${indent(inputAssignments)}
       |
       |    @(posedge clock) begin
       |        valid_up = 0;
       |    end
       |end
       |""".stripMargin.stripTrailing
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
}
