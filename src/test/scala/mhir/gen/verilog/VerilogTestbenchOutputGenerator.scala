package mhir.gen.verilog

import mhir.debug.indent
import mhir.gen.Undefined
import mhir.ir._

import scala.annotation.tailrec

object VerilogTestbenchOutputGenerator {
  def getNames(output: DirectTestOutput): Seq[String] = {
    widthByPort(output).keys.toSeq
  }

  def getDecls(output: DirectTestOutput): String = {
    val outAtomWidth = getAtomWidth(output.elems.head.typ)
    s"wire [${outAtomWidth - 1}:0] ${getNames(output).mkString(", ")};"
  }

  def getBlock(output: DirectTestOutput): String = {
    val outAtomWidth = getAtomWidth(output.elems.head.typ)
    val outputChecks = outputMap(output)
      .map(valByPort => {
        val checks = valByPort
          .map({ case (port, v) =>
            v match {
              case None =>
                s"// value for $port is undefined"
              case Some(v) =>
                s"check_output($v, $port);"
            }
          })
          .mkString("\n")
        s"wait_for_output();\n$checks"
      })
      .mkString("\n\n")
    s"""task prepare_outputs ();
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

  private def widthByPort(output: DirectTestOutput): Map[String, Int] = {
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
}
