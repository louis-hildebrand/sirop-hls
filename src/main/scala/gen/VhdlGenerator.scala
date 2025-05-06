package gen

import debug.PrettyPrinter
import ir._
import opt.PartialEvalPass

import java.nio.file.{Files, Path}

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
    assignStmt: String,
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
        .map(s => s.assignStmt)
        ++ outPorts.map(p => s"${p.name} <= ${p.assign};")
    )
    val clkStmts = signals
      .filter(s => s.cond.nonEmpty)
      .map(s => s"if (${s.cond.get}) then ${s.assignStmt} end if;")
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
    require(
      s.typ != Missing,
      "Expression must be type checked before hardware generation."
    )
    require(
      !s.contains(classOf[SyntaxSugar]),
      "Expression must be lowered before hardware generation."
    )
    val valid = PartialEvalPass
      .partialEval(IsSome(s.output)().tchk().lower())
      .tchk()
      .lower()
    val data = PartialEvalPass
      .partialEval(OptionUnwrapUnsafe(s.output)().tchk().lower())
      .tchk()
      .lower()
    val bitWidth = getBitWidth(data.typ)

    val (nVhdl, nSignals) = makeVhdlExpr(s.n)
    val (validVhdl, validSignals) = makeVhdlExpr(valid)
    val (dataVhdl, dataSignals) = makeVhdlExpr(data)
    val accSignals = s.equations.flatMap({ case (x, (z, next)) =>
      val typ = x.typ match {
        case Missing =>
          throw new IllegalArgumentException(
            s"Missing type for accumulator element $x."
          )
        case t => t
      }
      val initVhdl = valueToVhdl(z)
      val (nextVhdl, nextSignals) = makeVhdlExpr(next)
      val sig = Signal(
        name = x.name,
        typ = makeVhdlType(typ),
        init = Some(initVhdl),
        assignStmt = s"${x.name} <= $nextVhdl;",
        cond = Some("can_update_acc")
      )
      sig +: nextSignals
    })

    val comment = PrettyPrinter
      .show(s)(Map())
      .split("\n")
      .map(x => s"-- $x")
      .mkString("\n")
    val inPorts =
      Seq(InPort("clk", "std_logic"), InPort("data_ready", "std_logic"))
    val (outPorts, outPortSignals) = {
      val (dataInternalStdLogicVec, sig) =
        toStdLogicVector("data_internal", data.typ, "data'length")
      val ports = Seq(
        OutPort(
          name = "data",
          typ = s"std_logic_vector(${bitWidth - 1} downto 0)",
          assign =
            s"(others => '0') when not transfer_ok else $dataInternalStdLogicVec"
        ),
        OutPort(
          name = "data_valid",
          typ = "std_logic",
          assign = "'1' when data_valid_internal else '0'"
        )
      )
      (ports, sig.toSeq)
    }
    val signals = (
      nSignals ++ validSignals ++ dataSignals ++ accSignals ++ outPortSignals
        ++
        Seq[Signal](
          Signal(
            "num_outputs",
            typ = "integer",
            init = Some("0"),
            assignStmt = "num_outputs <= num_outputs + 1;",
            cond = Some("transfer_ok")
          ),
          Signal(
            "data_valid_internal",
            typ = "boolean",
            init = None,
            assignStmt =
              s"data_valid_internal <= (num_outputs < $nVhdl) and ($validVhdl);",
            cond = None
          ),
          Signal(
            "data_internal",
            typ = makeVhdlType(data.typ),
            init = None,
            assignStmt = s"data_internal <= $dataVhdl;",
            cond = None
          ),
          Signal(
            "transfer_ok",
            typ = "boolean",
            init = None,
            assignStmt =
              "transfer_ok <= data_ready = '1' and data_valid_internal;",
            cond = None
          ),
          Signal(
            "can_update_acc",
            typ = "boolean",
            init = None,
            assignStmt =
              "can_update_acc <= (not data_valid_internal) or transfer_ok;",
            cond = None
          )
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

  private def makeVhdlExpr(e: Expr): (String, Seq[Signal]) = {
    e match {
      case x: Param  => (x.name, Seq())
      case IntCst(n) => (n.toString, Seq())
      case Sum(terms @ _*) =>
        val (vhdlTerms, signals) = terms.map(e => makeVhdlExpr(e)).unzip
        (vhdlTerms.map(x => s"($x)").mkString(" + "), signals.flatten)
      case Prod(factors @ _*) =>
        val (vhdlFactors, signals) =
          factors.map(e => makeVhdlExpr(e)).unzip
        (vhdlFactors.map(x => s"($x)").mkString(" * "), signals.flatten)
      case Div(e1, e2) => ???
      case Mod(e1, e2) => ???
      case True        => ("true", Seq())
      case False       => ("false", Seq())
      case ite @ IfThenElse(c, t, f) =>
        val (cVhdl, cSignals) = makeVhdlExpr(c)
        val (tVhdl, tSignals) = makeVhdlExpr(t)
        val (fVhdl, fSignals) = makeVhdlExpr(f)
        val sigName = Param("ite")().name
        val sig = Signal(
          sigName,
          typ = makeVhdlType(ite.typ),
          init = None,
          assignStmt = s"$sigName <= ($tVhdl) when ($cVhdl) else ($fVhdl);",
          cond = None
        )
        (sigName, sig +: (cSignals ++ tSignals ++ fSignals))
      case Equal(e1, e2) =>
        val (e1Vhdl, e1Signals) = makeVhdlExpr(e1)
        val (e2Vhdl, e2Signals) = makeVhdlExpr(e2)
        (s"($e1Vhdl) = ($e2Vhdl)", e1Signals ++ e2Signals)
      case LessThan(e1, e2) =>
        val (e1Vhdl, e1Signals) = makeVhdlExpr(e1)
        val (e2Vhdl, e2Signals) = makeVhdlExpr(e2)
        (s"($e1Vhdl) < ($e2Vhdl)", e1Signals ++ e2Signals)
      case Not(e) =>
        val (vhdlE, signals) = makeVhdlExpr(e)
        (s"not ($vhdlE)", signals)
      case And(terms @ _*) =>
        val (vhdlTerms, signals) = terms.map(e => makeVhdlExpr(e)).unzip
        (vhdlTerms.map(x => s"($x)").mkString(" and "), signals.flatten)
      case Or(terms @ _*) =>
        val (vhdlTerms, signals) = terms.map(e => makeVhdlExpr(e)).unzip
        (vhdlTerms.map(x => s"($x)").mkString(" or "), signals.flatten)
      case Tuple(elems @ _*) =>
        val (vhdlElems, signals) = elems.map(makeVhdlExpr).unzip
        assert(vhdlElems.length == elems.length)
        val (vhdlElemsStdLogicVec, moreSignals) = vhdlElems
          .zip(elems)
          .map({ case (vhdl, e) =>
            toStdLogicVector(vhdl, e.typ, getBitWidth(e.typ).toString)
          })
          .unzip
        val concat = vhdlElemsStdLogicVec
          .foldLeft("\"\"")({ case (acc, e) => s"$acc & ($e)" })
        (concat, signals.flatten ++ moreSignals.flatten)
      case ta @ TupleAccess(t, IntCst(i)) =>
        val tupTyp = t.typ.asInstanceOf[TyTuple]
        val bitWidth = getBitWidth(tupTyp)
        val bitsBefore = tupTyp.ts.take(i).map(t => getBitWidth(t)).sum
        val bitsInside = getBitWidth(tupTyp.ts(i))
        val msb = bitWidth - 1 - bitsBefore
        val lsb = msb - bitsInside + 1
        val (vhdlTuple, signals) = makeVhdlExpr(t)
        val vhdlTupAccess =
          fromStdLogicVector(s"$vhdlTuple($msb downto $lsb)", ta.typ)
        val sigName = Param("tupaccess")().name
        val sig = Signal(
          name = sigName,
          typ = makeVhdlType(ta.typ),
          // TODO: Always have a default value (to avoid warnings from to_integer)?
          init = Some(valueToVhdl(Default(ta.typ).lower())),
          assignStmt = s"$sigName <= $vhdlTupAccess;",
          cond = None
        )
        (sig.name, sig +: signals)
      case Function(x, e)                 => ???
      case FunCall(f, arg)                => ???
      case StmBuild(n, output, equations) => ???
      case StmNext(stream)                => ???
      case VecLiteral(elems @ _*) =>
        val (elemsVhdl, signals) = elems.map(makeVhdlExpr).unzip
        assert(elemsVhdl.length == elems.length)
        val (elemsStdLogicVec, moreSignals) = elemsVhdl
          .zip(elems)
          .map({ case (vhdl, e) =>
            toStdLogicVector(vhdl, e.typ, getBitWidth(e.typ).toString)
          })
          .unzip
        (
          elemsStdLogicVec.map(x => s"($x)").mkString(" & "),
          signals.flatten ++ moreSignals.flatten
        )
      case VecBuild(IntCst(n), f) =>
        val typ = f.typ.asInstanceOf[TyArrow].t2
        val elems = (0 until n).map(i =>
          PartialEvalPass.partialEval(FunCall(f, i)()).tchk().lower()
        )
        val e = VecLiteral(elems: _*)(TyVec(typ, n))
        makeVhdlExpr(e)
      case VecBuild(n, _) =>
        throw new IllegalArgumentException(
          s"VecBuild with non-constant size ($n) is not supported."
        )
      case va @ VecAccess(v, i) =>
        val (elemBitWidth, n) = v.typ match {
          case TyVec(t, IntCst(n)) if n > 0 => (getBitWidth(t), n)
          case t =>
            throw new IllegalArgumentException(
              s"Invalid type for vector in VecAccess: $t. Only non-empty, constant-size vectors are allowed."
            )
        }
        val totBitWidth = n * elemBitWidth
        val (vhdlVec, vecSignals) = makeVhdlExpr(v)
        val (vhdlIdx, idxSignals) = makeVhdlExpr(i)
        val sigName = Param("vecaccess")().name
        val cases = (0 until n).map(i => {
          val cond = if (i == n - 1) "others" else i.toString
          val msb = totBitWidth - 1 - i * elemBitWidth
          val lsb = msb - elemBitWidth + 1
          val value = s"$vhdlVec($msb downto $lsb)"
          s"($value) when $cond"
        })
        val assign =
          s"with ($vhdlIdx) select $sigName <= ${cases.mkString(", ")};"
        val sig = Signal(
          name = sigName,
          typ = makeVhdlType(va.typ),
          init = None,
          assignStmt = assign,
          cond = None
        )
        (sigName, sig +: (vecSignals ++ idxSignals))
      case StmLiteral(elems @ _*) => ???
      case StmNextK(s, k)         => ???
      case _: SyntaxSugar =>
        throw new IllegalArgumentException(
          s"Syntax sugar must be removed before hardware generation."
        )
    }
  }

  private def valueToVhdl(v: Expr): String = {
    ir.eval(v).tchk() match {
      case False     => "false"
      case True      => "true"
      case IntCst(k) => k.toString
      case _: Tuple | _: VecLiteral =>
        valueToStdLogicVector(v, getBitWidth(v.typ).toString)
      case _ =>
        throw new IllegalArgumentException(
          s"Cannot convert value $v to a VHDL expression. Is it really a value?"
        )
    }
  }

  def valueToStdLogicVector(v: Expr, len: String): String = {
    ir.eval(v).tchk() match {
      case False     => "\"0\""
      case True      => "\"1\""
      case IntCst(k) => s"std_logic_vector(to_signed($k, $len))"
      case Tuple(elems @ _*) =>
        if (elems.isEmpty) {
          "\"\""
        } else {
          elems
            .map(e => valueToStdLogicVector(e, getBitWidth(e.typ).toString))
            .map(x => s"($x)")
            .mkString(" & ")
        }
      case vec: VecLiteral =>
        valueToStdLogicVector(Tuple(vec.elems: _*)(), len)
      case _ =>
        throw new IllegalArgumentException(
          s"Cannot convert value $v to a std_logic_vector. Is it really a value?"
        )
    }
  }

  /** @param e
    *   The VHDL expression to convert to a std_logic_vector
    * @param t
    *   The type of the original expression
    * @param len
    *   The length of the target std_logic_vector
    */
  private def toStdLogicVector(
      e: String,
      t: Type,
      len: String
  ): (String, Option[Signal]) = {
    t match {
      case TyInt => (s"std_logic_vector(to_signed(($e), $len))", None)
      case TyBool =>
        val sigName = Param("slv")().name
        val sig = Signal(
          name = sigName,
          typ = "std_logic_vector(0 downto 0)",
          init = None,
          assignStmt = s"""$sigName <= "1" when ($e) else "0";""",
          cond = None
        )
        (sig.name, Some(sig))
      case _: TyTuple | _: TyVec =>
        // Tuples and vectors are already represented using std_logic_vector
        // TODO: But might it be necessary to resize?
        (e, None)
      case t =>
        throw new IllegalArgumentException(
          s"Cannot convert data of type $t to a std_logic_vector."
        )
    }
  }

  private def fromStdLogicVector(e: String, t: Type): String = {
    t match {
      case TyBool                => s"""($e) = \"1\""""
      case TyInt                 => s"to_integer(signed($e))"
      case _: TyTuple | _: TyVec =>
        // Tuples and vectors are already represented using std_logic_vector
        e
      case t =>
        throw new IllegalArgumentException(
          s"Cannot convert data of type std_logic_vector to $t."
        )
    }
  }

  private def makeVhdlType(t: Type): String = {
    t match {
      case TyInt  => "integer"
      case TyBool => "boolean"
      case _: TyTuple | _: TyVec =>
        val bitWidth = getBitWidth(t)
        s"std_logic_vector(${bitWidth - 1} downto 0)"
      case t =>
        throw new IllegalArgumentException(
          s"Cannot convert type $t to a VHDL type."
        )
    }
  }

  private def getBitWidth(t: Type): Int = {
    t match {
      case TyBool              => 1
      case TyInt               => 32
      case TyTuple(ts @ _*)    => ts.map(t => getBitWidth(t)).sum
      case TyVec(t, IntCst(n)) => getBitWidth(t) * n
      case t =>
        throw new IllegalArgumentException(
          s"Cannot find bit width for type $t."
        )
    }
  }
}
