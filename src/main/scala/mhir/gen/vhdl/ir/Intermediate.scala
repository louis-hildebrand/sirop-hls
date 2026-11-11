package mhir.gen.vhdl
package ir

import mhir.canonicalize._
import mhir.ir._

import scala.collection.immutable.ListMap

/** Anything whose output can be assigned to an intermediate variable in a
  * concurrent context: an expression, a function, an IP block, etc.
  */
sealed trait Intermediate {

  def freeVars: Set[Param]

  def substitute(subs: Map[Expr, Expr]): Intermediate

  def toVhdlDecl(target: Target, options: VhdlGeneratorOptions): Decl
}

/** An [[Intermediate]] which can appear inside a function.
  */
sealed trait IntermediateInFunction extends Intermediate {

  override def substitute(subs: Map[Expr, Expr]): IntermediateInFunction
}

/** A function that can be called anywhere within the `sbuild`.
  */
case class FunctionIntermediate(
    params: Seq[Param],
    intermediates: ListMap[Param, IntermediateInFunction],
    output: Expr
) extends IntermediateInFunction {

  assert(
    params.toSet.size == params.size,
    "function parameters should be unique"
  )

  def freeVars: Set[Param] = {
    val paramSet = params.toSet
    val (bound, free) = this.intermediates
      .foldLeft((paramSet /* bound vars */, Set[Param]() /* free vars */ ))({
        case ((bound, free), (x, i)) =>
          (bound + x, free ++ i.freeVars.diff(bound))
      })
    free ++ this.output.freeVars.diff(bound)
  }

  def substitute(
      substitutions: Map[Expr, Expr]
  ): FunctionIntermediate = {
    var (newParams, subs) = Substitution.enterBinder(this.params, substitutions)
    var newIntermediates = ListMap[Param, IntermediateInFunction]()
    for ((x, i) <- this.intermediates) {
      val newI = i.substitute(subs)
      val (Seq(newX), newSubs) = Substitution.enterBinder(Seq(x), subs)
      subs = newSubs
      newIntermediates += (newX -> newI)
    }
    val newOutput = this.output.subPreserveType(subs)
    FunctionIntermediate(newParams, newIntermediates, newOutput)
  }

  def toVhdlDecl(
      target: Target,
      options: VhdlGeneratorOptions
  ): VhdlFunction = {
    for (param <- params) {
      if (!param.typ.isData) {
        throw new IllegalArgumentException(
          s"invalid type for parameter $param in function $target: ${param.typ}"
        )
      }
    }
    if (!output.typ.isData) {
      throw new IllegalArgumentException(
        s"invalid type for output of function $target: ${output.typ}"
      )
    }
    val varDecls = intermediates
      .map({
        case (x, i: DataIntermediate) =>
          i.toVhdlVariableDecl(Target(x), options)
        case (x, _: FunctionIntermediate) =>
          throw new AssertionError(
            "nested functions should have been removed in an earlier compilation stage"
              + s" (found function ${x.name} inside ${target.name})"
          )
      })
      .toSeq
    val outVhdl = VhdlExprGenerator.toVhdl(output)
    VhdlFunction(
      name = target.name,
      args = params.map(x => (x.name, VhdlType(x.typ))),
      returnType = VhdlType(output.typ),
      decls = varDecls,
      ret = outVhdl
    )
  }
}

case class ForGenerateIntermediate(
    // TODO: Get rid of these; choose them at the moment I emit the VHDL string
    loopLabels: Seq[String],
    loopVars: Seq[Param],
    // TODO: Allow the loop bounds to be arbitrary expressions?
    //       I would then need to be very careful about things like substitution, intermediate
    //       insertion, etc.
    //       In fact, this flattened loop representation might not work at all, if loop bounds can
    //       depend on earlier loop variables.
    loopBoundsInclusive: Seq[Int],
    intermediates: ListMap[Param, Intermediate],
    body: IpBlockInst
) extends Intermediate {

  require(
    loopLabels.length == loopVars.length,
    s"different number of labels and variables: ${loopLabels.length} vs ${loopVars.length}"
  )
  require(
    loopLabels.length == loopBoundsInclusive.length,
    s"different number of labels and loop bounds: ${loopLabels.length} vs ${loopBoundsInclusive.length}"
  )

  override def freeVars: Set[Param] = {
    this.body.freeVars.diff(this.loopVars.toSet)
  }

  override def substitute(subs: Map[Expr, Expr]): ForGenerateIntermediate = {
    val loopVarSet = this.loopVars.toSet
    val newSubs = subs.filterKeys(_.freeVars.intersect(loopVarSet).isEmpty)
    ForGenerateIntermediate(
      this.loopLabels,
      this.loopVars,
      this.loopBoundsInclusive,
      this.intermediates.map({ case (x, i) => x -> i.substitute(subs) }),
      this.body.substitute(newSubs)
    )
  }

  def toVhdl(
      target: Target,
      options: VhdlGeneratorOptions,
      enable: String
  ): ForGenerate = {
    val ipBlockTarget = this.loopVars.foldLeft(target)({ case (target, i) =>
      val TyVec(elemTyp, _) = target.typ
      Target(s"${target.name}($i)", elemTyp)
    })
    val decls = this.intermediates
      .map({ case (x, i) =>
        i.toVhdlDecl(Target(x), options)
      })
      .toSeq
    ForGenerate(
      loopLabels = this.loopLabels,
      loopVars = this.loopVars.map(_.name),
      loopBoundsInclusive = this.loopBoundsInclusive.map(_.toString),
      decls = decls,
      body = this.body.toVhdlEntityInst(ipBlockTarget, options, enable = enable)
    )
  }

  override def toVhdlDecl(
      target: Target,
      options: VhdlGeneratorOptions
  ): Decl = {
    Signal(
      category = "Intermediate signals",
      name = target.name,
      typ = VhdlType(target.typ),
      // This signal will be driven by the IP block, so no need for an
      // assignment statement
      assignStmt = None,
      cond = None
    )
  }
}

object ForGenerateIntermediate {

  def apply(body: IpBlockInst): ForGenerateIntermediate = {
    ForGenerateIntermediate(
      Seq.empty,
      Seq.empty,
      Seq.empty,
      ListMap.empty,
      body
    )
  }
}

/** An instantiation of an IP block (e.g., the native Agilex DSPs).
  *
  * This will normally appear inside a [[ForGenerateIntermediate]].
  */
trait IpBlockInst {

  def freeVars: Set[Param]

  def substitute(subs: Map[Expr, Expr]): IpBlockInst

  /** Convert this IP block instantiation to VHDL.
    *
    * @param target
    *   the name and type of the output signal.
    * @param options
    *   general VHDL gen options (e.g., the name of the clock signal).
    * @param enable
    *   a VHDL expression saying whether to enable this IP block. If it
    *   evaluates to `false`, then the IP block should not update its internal
    *   registers.
    */
  def toVhdlEntityInst(
      target: Target,
      options: VhdlGeneratorOptions,
      enable: String
  ): VhdlEntityInstantiation

  def mapInputs(f: Expr => Expr): IpBlockInst
}

/** An intermediate that consists of some expression that is not an IP block and
  * whose type is a "data" type (see [[Type.isData]]).
  */
sealed trait DataIntermediate extends Intermediate with IntermediateInFunction {

  override def toVhdlDecl(
      target: Target,
      options: VhdlGeneratorOptions
  ): Decl = {
    Signal(
      category = "Intermediate signals",
      name = target.name,
      typ = VhdlType(target.typ),
      assignStmt = Some(this.toVhdlConcurrentStmt(target, options)),
      cond = None
    )
  }

  /** Convert this intermediate to a VHDL concurrent statement.
    *
    * @param target
    *   the name and type of the left-hand side.
    */
  def toVhdlConcurrentStmt(
      target: Target,
      options: VhdlGeneratorOptions
  ): String

  /** Create a VHDL sequential statement that assigns this intermediate to a
    * variable.
    *
    * @param target
    *   the name and type of the left-hand side.
    */
  def toVhdlVariableStmt(target: Target, options: VhdlGeneratorOptions): String

  def toVhdlVariableDecl(
      target: Target,
      options: VhdlGeneratorOptions
  ): VhdlVariable = {
    VhdlVariable(
      name = target.name,
      typ = VhdlType(target.typ),
      assignStmt = this.toVhdlVariableStmt(target, options)
    )
  }

  /** Convert this intermediate to a VHDL sequential statement knowing that the
    * target is a signal.
    *
    * @param target
    *   the name and type of the left-hand side.
    */
  def toVhdlSignalStmt(target: Target, options: VhdlGeneratorOptions): String

  override def substitute(subs: Map[Expr, Expr]): DataIntermediate

  def map(f: Expr => Expr): DataIntermediate
}

/** An intermediate variable whose value is the data of a producer stream.
  *
  * @param p
  *   the name of the producer stream.
  */
case class StmDataIntermediate(p: Param) extends DataIntermediate {

  override def toVhdlConcurrentStmt(
      target: Target,
      options: VhdlGeneratorOptions
  ): String = {
    s"${target.name} <= ${this.rhs(target, options)};"
  }

  override def toVhdlVariableStmt(
      target: Target,
      options: VhdlGeneratorOptions
  ): String = {
    s"${target.name} := ${this.rhs(target, options)};"
  }

  override def toVhdlSignalStmt(
      target: Target,
      options: VhdlGeneratorOptions
  ): String = {
    s"${target.name} <= ${this.rhs(target, options)};"
  }

  private def rhs(target: Target, options: VhdlGeneratorOptions): String = {
    val vhdlTyp = VhdlType(target.typ)
    VhdlConversionGenerator.fromStdLogicVector(
      if (options.handshake) s"${p.name}_data" else p.name,
      vhdlTyp
    )
  }

  override def freeVars: Set[Param] = Set(this.p)

  override def substitute(subs: Map[Expr, Expr]): DataIntermediate = {
    StmDataIntermediate(this.p.subPreserveType(subs).asInstanceOf[Param])
  }

  override def map(f: Expr => Expr): DataIntermediate = {
    StmDataIntermediate(f(p).asInstanceOf[Param])
  }
}

/** An intermediate variable whose value is constantly calculated from the
  * accumulators, producers, and other intermediates.
  */
case class ExprIntermediate(e: Expr) extends DataIntermediate {

  override def freeVars: Set[Param] = this.e.freeVars

  override def toVhdlConcurrentStmt(
      target: Target,
      options: VhdlGeneratorOptions
  ): String = {
    s"${target.name} <= ${VhdlExprGenerator.toVhdl(this.e)};"
  }

  override def toVhdlVariableStmt(
      target: Target,
      options: VhdlGeneratorOptions
  ): String = {
    s"${target.name} := ${VhdlExprGenerator.toVhdl(this.e)};"
  }

  override def toVhdlSignalStmt(
      target: Target,
      options: VhdlGeneratorOptions
  ): String = {
    s"${target.name} <= ${VhdlExprGenerator.toVhdl(this.e)};"
  }

  override def substitute(subs: Map[Expr, Expr]): DataIntermediate = {
    ExprIntermediate(this.e.subPreserveType(subs))
  }

  override def map(f: Expr => Expr): DataIntermediate = {
    ExprIntermediate(f(e))
  }
}

/** An if-then-else expression.
  *
  * @param c
  *   the condition.
  * @param t
  *   the value when [[c]] evaluates to `true`.
  * @param f
  *   the value when [[c]] evaluates to `false`.
  */
case class MuxIntermediate(c: Expr, t: Expr, f: Expr) extends DataIntermediate {

  override def substitute(subs: Map[Expr, Expr]): DataIntermediate = {
    MuxIntermediate(
      this.c.subPreserveType(subs),
      this.t.subPreserveType(subs),
      this.f.subPreserveType(subs)
    )
  }

  override def map(f: Expr => Expr): DataIntermediate = {
    MuxIntermediate(f(this.c), f(this.t), f(this.f))
  }

  override def freeVars: Set[Param] = {
    c.freeVars ++ t.freeVars ++ f.freeVars
  }

  override def toVhdlConcurrentStmt(
      target: Target,
      options: VhdlGeneratorOptions
  ): String = {
    val c = VhdlExprGenerator.toVhdl(this.c)
    val t = VhdlExprGenerator.toVhdl(this.t)
    val f = VhdlExprGenerator.toVhdl(this.f)
    s"${target.name} <= ($t) when ($c) else ($f);"
  }

  override def toVhdlVariableStmt(
      target: Target,
      options: VhdlGeneratorOptions
  ): String = {
    s"""if (${VhdlExprGenerator.toVhdl(this.c)}) then
       |    ${target.name} := ${VhdlExprGenerator.toVhdl(this.t)};
       |else
       |    ${target.name} := ${VhdlExprGenerator.toVhdl(this.f)};
       |end if;
       |""".stripMargin.stripTrailing
  }

  override def toVhdlSignalStmt(
      target: Target,
      options: VhdlGeneratorOptions
  ): String = {
    s"""if (${VhdlExprGenerator.toVhdl(this.c)}) then
       |    ${target.name} <= ${VhdlExprGenerator.toVhdl(this.t)};
       |else
       |    ${target.name} <= ${VhdlExprGenerator.toVhdl(this.f)};
       |end if;
       |""".stripMargin.stripTrailing
  }
}
