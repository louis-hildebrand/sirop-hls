package mhir.gen.vhdl

import mhir.ir._

import scala.collection.immutable.ListMap

/** An intermediate variable (e.g., a VHDL signal, function, or component
  * instantiation) that does <i>not</i> translate to a register.
  */
sealed trait Intermediate {

  /** Convert this intermediate to a declaration (e.g., a signal, a function)
    * that can be placed directly at the top level of a VHDL architecture (as
    * opposed to within a function).
    *
    * @param target
    *   the name and type of the signal, function, etc. to declare.
    */
  def toVhdlInArchitecture(
      target: Param,
      options: VhdlGeneratorOptions
  ): Seq[Decl]

  def freeVars: Set[Param]
}

/** An [[Intermediate]] that can appear inside a function.
  */
sealed trait AllowedInFunction {

  /** Convert this intermediate to a declaration (e.g., a variable, a function)
    * that can appear within a function.
    *
    * @param target
    *   the name and type of the variable, function, etc. to declare.
    */
  def toVhdlInFunction(target: Param): Seq[Decl]
}

/** An intermediate variable whose value is the data of a producer stream.
  *
  * @param p
  *   the name of the producer stream.
  */
case class StmDataIntermediate(p: Param) extends Intermediate {

  override def toVhdlInArchitecture(
      target: Param,
      options: VhdlGeneratorOptions
  ): Seq[Decl] = {
    val vhdlTyp = VhdlType(target.typ)
    val rawDataConversion = VhdlConversionGenerator.fromStdLogicVector(
      if (options.handshake) s"${p.name}_data" else p.name,
      vhdlTyp
    )
    Seq(
      Signal(
        category = s"Handshake (input producer $p)",
        name = target.name,
        typ = vhdlTyp,
        assignStmt = Some(s"${target.name} <= $rawDataConversion;")
      )
    )
  }

  override def freeVars: Set[Param] = Set(this.p)
}

/** An intermediate variable whose value is constantly calculated from the
  * accumulators, producers, and other intermediates.
  */
case class DataIntermediate(e: Expr)
    extends Intermediate
    with AllowedInFunction {

  override def freeVars: Set[Param] = this.e.freeVars

  override def toVhdlInArchitecture(
      target: Param,
      options: VhdlGeneratorOptions
  ): Seq[Decl] = {
    if (!target.typ.isData) {
      throw new IllegalArgumentException(
        s"invalid type for intermediate: ${target.typ}"
      )
    }
    val rhs = this.e match {
      case Mux(c, t, f) =>
        val cv = VhdlExprGenerator.toVhdl(c)
        val tv = VhdlExprGenerator.toVhdl(t)
        val fv = VhdlExprGenerator.toVhdl(f)
        s"($tv) when ($cv) else ($fv)"
      case e =>
        VhdlExprGenerator.toVhdl(e)
    }
    Seq(
      Signal(
        category = "Intermediate signals",
        name = target.name,
        typ = VhdlType(target.typ),
        init = None,
        assignStmt = Some(s"${target.name} <= $rhs;"),
        cond = None
      )
    )
  }

  override def toVhdlInFunction(target: Param): Seq[Decl] = {
    if (!target.typ.isData) {
      throw new IllegalArgumentException(
        s"invalid type for intermediate: ${target.typ}"
      )
    }
    val stmt = this.e match {
      case Mux(c, t, f) =>
        val cv = VhdlExprGenerator.toVhdl(c)
        val tv = VhdlExprGenerator.toVhdl(t)
        val fv = VhdlExprGenerator.toVhdl(f)
        s"""if ($cv) then
             |    ${target.name} := $tv;
             |else
             |    ${target.name} := $fv;
             |end if;
             |""".stripMargin.stripTrailing
      case e =>
        s"${target.name} := ${VhdlExprGenerator.toVhdl(e)};"
    }
    val newVar = VhdlVariable(
      name = target.name,
      typ = VhdlType(target.typ),
      assignStmt = stmt
    )
    Seq(newVar)
  }
}

/** A function that can be called anywhere within the `sbuild`.
  */
case class FunctionIntermediate(
    params: Seq[Param],
    intermediates: ListMap[Param, Intermediate with AllowedInFunction],
    output: Expr
) extends Intermediate
    with AllowedInFunction {

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

  override def toVhdlInArchitecture(
      target: Param,
      options: VhdlGeneratorOptions
  ): Seq[Decl] = {
    this.toVhdlInFunction(target)
  }

  override def toVhdlInFunction(target: Param): Seq[Decl] = {
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
      .flatMap({ case (x, i) => i.toVhdlInFunction(x) })
      .toSeq
    val outVhdl = VhdlExprGenerator.toVhdl(output)
    Seq(
      VhdlFunction(
        name = target.name,
        args = params.map(x => (x.name, VhdlType(x.typ))),
        returnType = VhdlType(output.typ),
        decls = varDecls,
        ret = outVhdl,
        mode = PureFunction
      )
    )
  }
}

/** An instantiation of an IP block (e.g., the native Agilex DSPs).
  *
  * The inputs to this IP block should all be names (i.e., accumulators or other
  * intermediates), not arbitrary expressions.
  */
trait IpBlockInst extends Intermediate
