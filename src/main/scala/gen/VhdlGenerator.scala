package gen

import debug.PrettyPrinter
import ir._
import opt.PartialEvalPass

import java.nio.file.{Files, Path}

private sealed trait Type
private case object MyInt extends Type
private case object MyBool extends Type

private case class InPort(
    name: String,
    typ: String
)

private case class OutPort(
    name: String,
    typ: String,
    assign: String
)

private case class Signal(
    name: String,
    typ: String,
    init: Option[String],
    assign: String,
    cond: Option[String]
)

/** One component (entity + architecture) in VHDL.
  *
  * @param comment
  *   The expression in the IR from which this component was generated
  * @param name
  *   Name of the component
  * @param inPorts
  *   Input ports
  * @param outPorts
  *   Output ports
  * @param signals
  *   Internal signals
  */
private case class VhdlComponent(
    comment: String,
    name: String,
    inPorts: Seq[InPort],
    outPorts: Seq[OutPort],
    signals: Seq[Signal]
) {
  def vhdl: String = {
    val portDecls = (
      inPorts.map(p => s"${p.name} : in ${p.typ}")
        ++ outPorts.map(p => s"${p.name} : out ${p.typ}")
    )
    val sigDecls = signals.map(s => {
      val str1 = s"signal ${s.name} : ${s.typ}"
      val str2 = s.init match {
        case Some(z) => s"$str1 := $z"
        case None    => str1
      }
      s"$str2;"
    })
    val combStmts = (
      signals
        .filter(s => s.cond.isEmpty)
        .map(s => s"${s.name} <= ${s.assign};")
        ++ outPorts.map(p => s"${p.name} <= ${p.assign};")
    )
    val clkStmts = signals
      .filter(s => s.cond.nonEmpty)
      .map(s => s"if (${s.cond.get}) then ${s.name} <= ${s.assign}; end if;")
    comment + "\n" +
      s"""library IEEE;
         |use IEEE.std_logic_1164.all;
         |use IEEE.numeric_std.all;
         |
         |entity top is
         |port (
         |    ${portDecls.mkString(";\n    ")}
         |);
         |end top;
         |
         |architecture arch of top is
         |    ${sigDecls.mkString("\n    ")}
         |begin
         |    ${combStmts.mkString("\n    ")}
         |
         |    process
         |    begin
         |        wait until rising_edge(clk) and can_update_acc;
         |        ${clkStmts.mkString("\n        ")}
         |    end process ;
         |end arch;
         |""".stripMargin
  }
}

object VhdlGenerator {
  def makeVhdl(s: StmBuild, dir: Path): Int = {
    val valid = PartialEvalPass.partialEval(IsSome(s.output))
    val data = PartialEvalPass.partialEval(OptionUnwrapUnsafe(s.output))
    val (typeByVar, outType) = findTypes(s)
    val bitWidth = getBitWidth(outType)

    val (nVhdl, nSignals) = makeVhdlExpr(s.n, MyInt)
    val (validVhdl, validSignals) = makeVhdlExpr(valid, MyBool)
    val (dataVhdl, dataSignals) = makeVhdlExpr(data, outType)
    val accSignals = s.equations.flatMap({ case (x, (z, next)) =>
      val typ = typeByVar(x)
      val (initVhdl, initSignals) = makeVhdlExpr(z, typ)
      val (nextVhdl, nextSignals) = makeVhdlExpr(next, typ)
      val sig = Signal(
        name = x.name,
        typ = makeVhdlType(typ),
        init = Some(initVhdl),
        assign = nextVhdl,
        cond = Some("can_update_acc")
      )
      sig +: (initSignals ++ nextSignals)
    })

    val comment = PrettyPrinter
      .show(s)(Map())
      .split("\n")
      .map(x => s"-- $x")
      .mkString("\n")
    val signals = (
      nSignals ++ validSignals ++ dataSignals ++ accSignals
        ++
        Seq[Signal](
          Signal(
            "num_outputs",
            typ = "integer",
            init = Some("0"),
            assign = "num_outputs + 1",
            cond = Some("transfer_ok")
          ),
          Signal(
            "data_valid_internal",
            typ = "boolean",
            init = None,
            assign = s"(num_outputs < $nVhdl) and ($validVhdl)",
            cond = None
          ),
          Signal(
            "data_internal",
            typ = makeVhdlType(outType),
            init = None,
            assign = dataVhdl,
            cond = None
          ),
          Signal(
            "transfer_ok",
            typ = "boolean",
            init = None,
            assign = "data_ready = '1' and data_valid_internal",
            cond = None
          ),
          Signal(
            "can_update_acc",
            typ = "boolean",
            init = None,
            assign = "(not data_valid_internal) or transfer_ok",
            cond = None
          )
        )
    )
    val inPorts =
      Seq(InPort("clk", "std_logic"), InPort("data_ready", "std_logic"))
    val outPorts = Seq(
      OutPort(
        name = "data",
        typ = s"std_logic_vector(${bitWidth - 1} downto 0)",
        assign =
          s"(others => '0') when not transfer_ok else ${toStdLogicVector("data_internal", outType, "data'length")}"
      ),
      OutPort(
        name = "data_valid",
        typ = "std_logic",
        assign = "'1' when data_valid_internal else '0'"
      )
    )
    val component = VhdlComponent(
      comment = comment,
      name = "top",
      inPorts = inPorts,
      outPorts = outPorts,
      signals = signals
    )

    val designDir = dir.resolve("design")
    Files.createDirectory(designDir)
    val topFile = designDir.resolve("top.vhd")
    Files.writeString(topFile, component.vhdl)

    bitWidth
  }

  private def makeVhdlExpr(e: Expr, typ: Type): (String, Seq[Signal]) = {
    e match {
      case x: Param  => (x.name, Seq())
      case IntCst(n) => (n.toString, Seq())
      case Sum(terms) =>
        val (vhdlTerms, signals) = terms.map(e => makeVhdlExpr(e, typ)).unzip
        (vhdlTerms.map(x => s"($x)").mkString(" + "), signals.flatten)
      case Prod(factors) =>
        val (vhdlFactors, signals) =
          factors.map(e => makeVhdlExpr(e, typ)).unzip
        (vhdlFactors.map(x => s"($x)").mkString(" * "), signals.flatten)
      case Div(e1, e2) => ???
      case Mod(e1, e2) => ???
      case True        => ("true", Seq())
      case False       => ("false", Seq())
      case IfThenElse(c, t, f) =>
        val (cVhdl, cSignals) = makeVhdlExpr(c, MyBool)
        val (tVhdl, tSignals) = makeVhdlExpr(t, typ)
        val (fVhdl, fSignals) = makeVhdlExpr(f, typ)
        val sigName = Param("ite").name
        val sig = Signal(
          sigName,
          typ = makeVhdlType(typ),
          init = None,
          assign = s"($tVhdl) when ($cVhdl) else ($fVhdl)",
          cond = None
        )
        (sigName, sig +: (cSignals ++ tSignals ++ fSignals))
      case Equal(e1, e2) =>
        val (e1Vhdl, e1Signals) = makeVhdlExpr(e1, MyInt)
        val (e2Vhdl, e2Signals) = makeVhdlExpr(e2, MyInt)
        (s"($e1Vhdl) = ($e2Vhdl)", e1Signals ++ e2Signals)
      case LessThan(e1, e2) =>
        val (e1Vhdl, e1Signals) = makeVhdlExpr(e1, MyInt)
        val (e2Vhdl, e2Signals) = makeVhdlExpr(e2, MyInt)
        (s"($e1Vhdl) < ($e2Vhdl)", e1Signals ++ e2Signals)
      case Not(e) =>
        val (vhdlE, signals) = makeVhdlExpr(e, typ)
        (s"not ($vhdlE)", signals)
      case And(terms @ _*) =>
        val (vhdlTerms, signals) = terms.map(e => makeVhdlExpr(e, typ)).unzip
        (vhdlTerms.map(x => s"($x)").mkString(" and "), signals.flatten)
      case Or(terms @ _*) =>
        val (vhdlTerms, signals) = terms.map(e => makeVhdlExpr(e, typ)).unzip
        (vhdlTerms.map(x => s"($x)").mkString(" or "), signals.flatten)
      case Tuple(elems @ _*)     => ???
      case TupleAccess(t, i)     => ???
      case Function(param, body) => ???
      case FunCall(f, arg)       => ???
      case Default =>
        typ match {
          case MyInt  => ("0", Seq())
          case MyBool => ("false", Seq())
        }
      case StmBuild(n, output, equations) => ???
      case StmNext(stream)                => ???
      case VecBuild(len, f)               => ???
      case VecAccess(vec, i)              => ???
      case VecLiteral(elems @ _*)         => ???
      case StmLiteral(elems @ _*)         => ???
      case StmNextK(s, k)                 => ???
    }
  }

  private def makeVhdlType(t: Type): String = {
    t match {
      case MyInt  => "integer"
      case MyBool => "boolean"
    }
  }

  private def toStdLogicVector(e: String, t: Type, len: String): String = {
    t match {
      case MyInt  => s"std_logic_vector(to_signed(($e), $len))"
      case MyBool => s""" "1" when ($e) else "0" """.strip
    }
  }

  private def getBitWidth(t: Type): Int = {
    t match {
      case MyBool => 1
      case MyInt  => 32
    }
  }

  private def findTypes(s: StmBuild): (Map[Param, Type], Type) = {
    val typeByVar = s.seedByVar.flatMap({ case (x, e) =>
      findType(e, Map()) match {
        case Some(t) => Some(x -> t)
        case None =>
          throw new IllegalArgumentException(
            s"Unable to determine type of accumulator variable $x."
          )
      }
    })
    val data = PartialEvalPass.partialEval(OptionUnwrapUnsafe(s.output))
    val outType = findType(data, typeByVar) match {
      case Some(t) => t
      case None =>
        throw new IllegalArgumentException("Failed to find output type.")
    }
    (typeByVar, outType)
  }

  private def findType(
      e: Expr,
      typeByVar: Map[Param, Type]
  ): Option[Type] = {
    e match {
      case _: IntExpr  => Some(MyInt)
      case _: BoolExpr => Some(MyBool)
      case IfThenElse(_, t, f) =>
        findType(t, typeByVar).orElse(findType(f, typeByVar))
      case x: Param => typeByVar.get(x)
      case _        => None
    }
  }
}
