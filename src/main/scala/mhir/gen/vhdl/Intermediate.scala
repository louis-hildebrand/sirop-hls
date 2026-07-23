package mhir.gen.vhdl

import mhir.canonicalize._
import mhir.ir._

import scala.collection.immutable.ListMap

// TODO: Move this stuff to package mhir.gen.vhdl.ir?

/** Anything whose output can be assigned to an intermediate variable in a
  * concurrent context: an expression, a function, an IP block, etc.
  */
sealed trait Intermediate {

  def freeVars: Set[Param]

  def substitute(subs: Map[Expr, Expr]): Intermediate

  // TODO: Delete this?
//  def map(f: Expr => Expr): Intermediate

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
      subs: Map[Expr, Expr]
  ): IntermediateInFunction = {
    val varsBoundHere = this.params.toSet ++ this.intermediates.keySet
    val wouldCapture =
      subs.values.exists(e => e.freeVars.intersect(varsBoundHere).nonEmpty)
    if (wouldCapture) {
      // TODO: Implement this properly
      ???
    } else {
      FunctionIntermediate(
        this.params,
        this.intermediates.map({ case (x, i) => x -> i.substitute(subs) }),
        output.subPreserveType(subs)
      )
    }
  }

  // TODO: Delete this?
//  override def map(f: Expr => Expr): SequentialIntermediate = {
//    val newParams = params.map(x => f(x).asInstanceOf[Param])
//    val newIntermediates = intermediates.map({ case (x, i) => x -> i.map(f) })
//    val newOutput = output.map(f)
//    FunctionIntermediate(newParams, newIntermediates, newOutput)
//  }

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
        case (x, i: FunctionIntermediate) =>
          // TODO: This should never happen, right?
          ???
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

/** An instantiation of an IP block (e.g., the native Agilex DSPs).
  */
trait IpBlockInst extends Intermediate {

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
      target: Param,
      options: VhdlGeneratorOptions,
      enable: String
  ): VhdlEntityInstantiation

  override def toVhdlDecl(
      target: Target,
      options: VhdlGeneratorOptions
  ): Signal = {
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
