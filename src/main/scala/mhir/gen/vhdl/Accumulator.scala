package mhir.gen.vhdl

import mhir.canonicalize._
import mhir.debug.indent
import mhir.ir._

sealed trait Accumulator {

  def substitute(subs: Map[Expr, Expr]): Accumulator

  def freeVars: Set[Param]

  def map(f: Expr => Expr): Accumulator

  def toVhdl(
      target: Target,
      enable: String,
      options: VhdlGeneratorOptions
  ): Signal
}

case class ExprAccumulator(
    init: Option[DataIntermediate],
    next: DataIntermediate
) extends Accumulator {

  override def substitute(subs: Map[Expr, Expr]): Accumulator = {
    ExprAccumulator(init.map(_.substitute(subs)), next.substitute(subs))
  }

  override def freeVars: Set[Param] = {
    next.freeVars ++
      init.toSet.flatMap((i: DataIntermediate) => i.freeVars)
  }

  override def map(f: Expr => Expr): Accumulator = {
    ExprAccumulator(init.map(i => i.map(f)), next.map(f))
  }

  override def toVhdl(
      target: Target,
      enable: String,
      options: VhdlGeneratorOptions
  ): Signal = {
    val initVhdl = this.init.map(init => init.toVhdlSignalStmt(target, options))
    val nextVhdl = this.next.toVhdlSignalStmt(target, options)
    Signal(
      category = "Registers",
      name = target.name,
      typ = VhdlType(target.typ),
      assignStmt = Some({
        val update =
          s"""if $enable then
               |${indent(nextVhdl)}
               |end if;
               |""".stripMargin.stripTrailing
        initVhdl match {
          case None => update
          case Some(initVhdl) =>
            s"""if sl2bool(${options.reset}) then
                 |${indent(initVhdl)}
                 |els$update
                 |""".stripMargin.stripTrailing
        }
      }),
      cond = Some("true")
    )
  }
}

/** An accumulator which is a vector which is updated in at most one location
  * per time step.
  *
  * I want to handle this specially because Quartus won't infer a BRAM from the
  * usual VHDL for a vector accumulator, which always updates every index (but
  * possibly using the old value).
  *
  * @param cond
  *   only perform the update if this condition evaluates to `true`.
  * @param index
  *   the index to update.
  * @param value
  *   what to write at that index.
  */
case class VecWriteAccumulator(cond: Expr, index: Expr, value: Expr)
    extends Accumulator {

  assert(this.cond.typ == TyBool)
  assert(this.index.typ.isInstanceOf[TyUInt])

  override def substitute(subs: Map[Expr, Expr]): Accumulator = {
    VecWriteAccumulator(
      cond.subPreserveType(subs),
      index.subPreserveType(subs),
      value.subPreserveType(subs)
    )
  }

  override def freeVars: Set[Param] = {
    cond.freeVars ++ index.freeVars ++ value.freeVars
  }

  def map(f: Expr => Expr): Accumulator = {
    VecWriteAccumulator(f(cond), f(index), f(value))
  }

  def toVhdl(
      target: Target,
      enable: String,
      options: VhdlGeneratorOptions
  ): Signal = {
    val condVhdl = VhdlExprGenerator.toVhdl(this.cond)
    // TODO: Deduplicate this code (introduce method like VhdlExprGenerator.toIntegerVhdl)
    val idxVhdl = this.index match {
      case IntCst(i) => i.toString
      case i         => s"to_integer(${VhdlExprGenerator.toVhdl(i)})"
    }
    val writeVhdl = VhdlExprGenerator.toVhdl(this.value)
    Signal(
      category = "Registers",
      name = target.name,
      typ = VhdlType(target.typ),
      assignStmt = Some(s"${target.name}($idxVhdl) <= $writeVhdl;"),
      cond = Some(s"$enable and ($condVhdl)")
    )
  }
}
