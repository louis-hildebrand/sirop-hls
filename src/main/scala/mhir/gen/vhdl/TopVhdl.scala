package mhir.gen.vhdl

import mhir.ir._
import mhir.ir.typecheck.TypeCheck

import scala.annotation.tailrec

private case class FlatPipeline(
    sbuilds: Seq[(Param, StmBuild)],
    lets: Seq[(Param, Int, Set[Param])],
    inputs: Set[Param],
    unusedInputs: Set[Param],
    sink: Param
)

object TopVhdl {
  def apply(f: Expr): VhdlComponent = {
    val pipe = makeFlatPipeline(f)
    val childComponents = {
      val sbuilds = pipe.sbuilds.zipWithIndex.map({
        case ((x, s: StmBuild), i) =>
          val inputsOfS = s.freeVars
          val component = StmBuildVhdl(s, inputsOfS, name = s"sbuild_${i + 1}")
          val portMap = PortMap(
            Map(
              "clk" -> "clk",
              "reset" -> "reset",
              // Handshake with consumer
              s"data" -> s"${x.name}_data",
              s"valid" -> s"${x.name}_valid",
              s"ready" -> s"${x.name}_ready"
            ) ++ inputsOfS.flatMap({ x =>
              // Handshake with producers
              Map(
                s"${x.name}_data" -> s"${x.name}_data",
                s"${x.name}_valid" -> s"${x.name}_valid",
                s"${x.name}_ready" -> s"${x.name}_ready"
              )
            })
          )
          VhdlEntityInstantiation(component.name, component, portMap)
      })
      val lets = pipe.lets.zipWithIndex.map({ case ((x, bufSize, xs), i) =>
        val component = LetStmVhdl(
          x,
          bufSize,
          xs.toSeq.sortBy(_.name),
          name = s"letstm_${i + 1}"
        )
        val portMap = PortMap(
          Map(
            "clk" -> "clk",
            "reset" -> "reset",
            // Handshake with consumer
            s"${x.name}_data" -> s"${x.name}_data",
            s"${x.name}_valid" -> s"${x.name}_valid",
            s"${x.name}_ready" -> s"${x.name}_ready"
          ) ++ xs.flatMap({ x =>
            // Handshake with producers
            Map(
              s"${x.name}_data" -> s"${x.name}_data",
              s"${x.name}_valid" -> s"${x.name}_valid",
              s"${x.name}_ready" -> s"${x.name}_ready"
            )
          })
        )
        VhdlEntityInstantiation(component.name, component, portMap)
      })
      sbuilds ++ lets
    }
    val signals = {
      val sbuildOutputs = pipe.sbuilds.flatMap({ case (x, _) =>
        Seq(
          Signal(
            category = "",
            name = s"${x.name}_data",
            typ = VhdlType(x.typ.asInstanceOf[TyStm].t).toStdLogicVec
          ),
          Signal(
            category = "",
            name = s"${x.name}_valid",
            typ = VhdlStdLogic
          ),
          Signal(
            category = "",
            name = s"${x.name}_ready",
            typ = VhdlStdLogic,
            assignStmt = if (x == pipe.sink) {
              Some(s"${x.name}_ready <= ready;")
            } else {
              None
            }
          )
        )
      })
      val letOutputs = pipe.lets.flatMap({ case (_, _, xs) =>
        xs.flatMap({ x =>
          Seq(
            Signal(
              category = "",
              name = s"${x.name}_data",
              typ = VhdlType(x.typ.asInstanceOf[TyStm].t).toStdLogicVec
            ),
            Signal(
              category = "",
              name = s"${x.name}_valid",
              typ = VhdlStdLogic
            ),
            Signal(
              category = "",
              name = s"${x.name}_ready",
              typ = VhdlStdLogic,
              assignStmt = if (x == pipe.sink) {
                Some(s"${x.name}_ready <= ready;")
              } else {
                None
              }
            )
          )
        })
      })
      sbuildOutputs ++ letOutputs
    }
    val ports = {
      val TyStm(outElemTyp, _) = pipe.sink.typ
      Seq(
        InPort("clk", VhdlStdLogic),
        InPort("reset", VhdlStdLogic),
        // Handshake with consumer
        OutPort(
          "data",
          VhdlType(outElemTyp).toStdLogicVec,
          assign = Some(s"${pipe.sink.name}_data")
        ),
        OutPort(
          "valid",
          VhdlStdLogic,
          assign = Some(s"${pipe.sink.name}_valid")
        ),
        InPort("ready", VhdlStdLogic)
      ) ++ pipe.inputs.flatMap({ x =>
        val TyStm(inElemTyp, _) = x.typ
        Seq(
          // Handshake with producers
          InPort(s"${x.name}_data", VhdlType(inElemTyp).toStdLogicVec),
          InPort(s"${x.name}_valid", VhdlStdLogic),
          OutPort(
            s"${x.name}_ready",
            VhdlStdLogic,
            assign = if (pipe.unusedInputs.contains(x)) {
              Some(s"${x.name}_valid")
            } else {
              None
            }
          )
        )
      })
    }
    CustomVhdlComponent(
      expr = Some(f),
      name = "top",
      inPorts =
        ports.filter(_.isInstanceOf[InPort]).map(_.asInstanceOf[InPort]),
      outPorts =
        ports.filter(_.isInstanceOf[OutPort]).map(_.asInstanceOf[OutPort]),
      signals = signals,
      functions = Seq(),
      children = childComponents
    )
  }

  // TODO: Move this somewhere else, since it's used in many places?
  private def unwrapTopLevelFunction(
      f: Expr,
      rename: Boolean
  ): (Seq[Param], Expr) = {
    @tailrec
    def unwrap(e: Expr, inputs: Seq[Param]): (Seq[Param], Expr) = {
      e match {
        case Function(x, e) if x.typ == TyTuple() =>
          unwrap(e, inputs)
        case Function(x, e) =>
          if (rename) {
            val y = Param(s"I${inputs.length}", -1)(x.typ)
            // If this assertion fails, then one of the following is true:
            //  (1) a previous parameter is called the same thing, but that
            //      should not happen.
            //  (2) the expression has a free variable, but that's not allowed.
            assert(x == y || !e.freeVars.contains(y))
            unwrap(e.subPreserveType(x -> y), y +: inputs)
          } else {
            unwrap(e, x +: inputs)
          }
        case e =>
          (inputs, e)
      }
    }

    val (inputSeq, stm) = unwrap(f, Seq())
    if (inputSeq.map(x => x.name).toSet.size != inputSeq.length) {
      val paramList = inputSeq.reverse.map(x => x.name).mkString(", ")
      throw new IllegalArgumentException(
        s"Duplicate parameters in top-level parameter list $paramList."
      )
    }
    (inputSeq.reverse, stm)
  }

  private def validateExpr(e: Expr): Unit = {
    require(
      e.typ != Missing,
      "Expression must be type checked before hardware generation."
    )
    require(
      !e.hasSyntaxSugar,
      "Expression must be lowered before hardware generation."
    )
    require(
      e.freeVars.isEmpty,
      s"Cannot generate hardware for expression with free variables (${e.freeVars})."
    )
    val (inputs, stm) = e match {
      case f: Function => unwrapTopLevelFunction(f, rename = false)
      case e           => (Seq(), e)
    }
    for (x <- inputs) {
      require(
        x.typ.isInstanceOf[TyStm],
        s"Top-level parameter has type ${x.typ}."
          + " All top-level parameters must be streams."
      )
      val numOccurrences = stm.countFreeOccurrences(x)
      require(
        numOccurrences <= 1,
        s"Top-level parameter $x is used $numOccurrences times."
          + " No top-level parameter should be used more than once."
          + " To describe a stream with multiple consumers, consider using LetStm."
      )
    }
  }

  private def makeFlatPipeline(f: Expr): FlatPipeline = {
    validateExpr(f)
    val (inputs, stm) = unwrapTopLevelFunction(f, rename = true)
    val unusedInputs = inputs.toSet.diff(stm.freeVars)
    val anfStm = StmAnfConverter.convert(stm)
    val pipe = listChildren(anfStm, inputs.toSet, unusedInputs)
    ensureAtLeastOneBuffer(pipe)
  }

  /** Makes a fresh copy of a given variable for each place it occurs free in
    * the given expression.
    *
    * @example
    *   `StmZip(s, s)` may be replaced with `StmZip(s1, s2)`, where `s`, `s1`,
    *   and `s2` are all different from each other.
    *
    * @param x
    *   the variable to freshen.
    * @param expr
    *   the expression in which to search for the variable.
    * @return
    *   all the fresh copies of the variable, along with the updated expression.
    */
  private def makeVariantsOfFreeVar(
      x: Param,
      expr: Expr
  ): (Set[Param], Expr) = {
    require(expr.hasType)
    val (xs, newE) = expr match {
      case y: Param if y == x =>
        val newX = x.freshCopy
        (Set(newX), newX)
      case Function(y, _) if y == x =>
        (Set[Param](), expr)
      case LetStm(_, y, _, _) if y == x =>
        (Set[Param](), expr)
      case s: StmBuild if s.accVars.contains(x) =>
        (Set[Param](), expr)
      case _ =>
        val (freshVars, newChildren) =
          expr.children.map(e => makeVariantsOfFreeVar(x, e)).unzip
        (freshVars.flatten.toSet, expr.rebuild(expr.typ, newChildren))
    }
    assert(newE.typ == expr.typ)
    (xs, newE)
  }

  private def listChildren(
      stm: Expr,
      inputs: Set[Param],
      unusedInputs: Set[Param]
  ): FlatPipeline = {
    stm match {
      case LetInlineStm(x, in: StmBuild, out) =>
        val rest = listChildren(out, inputs, unusedInputs)
        rest.copy(sbuilds = (x -> in) +: rest.sbuilds)
      case LetInlineStm(_, in, _) =>
        // TODO: Proper error message
        ???
      case LetStm(bufSizeExpr, x, in: Param, out) =>
        val IntCst(bufSize) = mhir.eval.eval(bufSizeExpr)
        val (xs, newOut) = makeVariantsOfFreeVar(x, out)
        val rest = listChildren(newOut, inputs, unusedInputs)
        rest.copy(lets = (in, bufSize.toInt, xs) +: rest.lets)
      case LetStm(_, _, in, _) =>
        // TODO: Proper error message
        ???
      case x: Param =>
        FlatPipeline(
          sbuilds = Seq(),
          lets = Seq(),
          inputs = inputs,
          unusedInputs = unusedInputs,
          sink = x
        )
      case e =>
        throw new IllegalArgumentException(
          s"Expected pipeline output to be a variable, but found ${e.getClass.getSimpleName}: $e"
        )
    }
  }

  private def ensureAtLeastOneBuffer(pipe: FlatPipeline): FlatPipeline = {
    if (pipe.inputs.contains(pipe.sink)) {
      val newSink = Param("s")(pipe.sink.typ)
      val nop = {
        val TyStm(typ, n) = pipe.sink.typ
        val s = Param("s")(TyStm(typ, -1))
        StmBuild(
          n,
          StmData(s)(),
          True,
          Map[Param, (Expr, Expr)](s -> (pipe.sink, True))
        )().tchk().asInstanceOf[StmBuild]
      }
      pipe.copy(sbuilds = pipe.sbuilds :+ (newSink, nop), sink = newSink)
    } else {
      pipe
    }
  }
}
