package mhir.gen.vhdl
package ir

import mhir.canonicalize._
import mhir.debug.indent
import mhir.ir._

import scala.collection.immutable.ListMap

/** Anything whose output can be assigned to an intermediate variable in a
  * concurrent context: an expression, a function, an IP block, etc.
  *
  * The VHDL conversion methods vary by subclass. For the declarative part, see
  * either [[FunctionIntermediate.toDeclarativeItem]] or
  * [[SimpleTarget.toDecl]]. For the statement part, see either
  * [[ConcurrentIntermediate.toConcurrentStatement]] or
  * [[SequentialIntermediate.toSequentialStatement]].
  */
sealed trait Intermediate {

  def freeVars: Set[Param]

  def substitute(subs: Map[Expr, Expr]): Intermediate

  def map(f: Expr => Expr): Intermediate
}

sealed trait ConcurrentIntermediate extends Intermediate {

  // TODO: Get rid of these by using F-bounded polymorphism?
  override def substitute(subs: Map[Expr, Expr]): ConcurrentIntermediate
  override def map(f: Expr => Expr): ConcurrentIntermediate

  /** Convert this to a concurrent statement in VHDL.
    *
    * @param target
    *   where to assign the result of this intermediate.
    * @param enable
    *   clock-enable expression (in VHDL, of type `boolean`). Registers within
    *   this intermediate should only be updated when this evaluates to `true`.
    */
  def toConcurrentStatement(
      target: Target,
      options: VhdlGeneratorOptions,
      enable: String
  ): String
}

sealed trait SequentialIntermediate extends Intermediate {

  // TODO: Get rid of these by using F-bounded polymorphism?
  override def substitute(subs: Map[Expr, Expr]): SequentialIntermediate
  override def map(f: Expr => Expr): SequentialIntermediate

  def toSequentialStatement(
      target: Target,
      options: VhdlGeneratorOptions
  ): String
}

/** An intermediate for a function definition.
  */
case class FunctionIntermediate(
    params: Seq[Param],
    // TODO: Can I use something like BlockIntermediate instead of all this?
    locals: ListMap[Param, SequentialIntermediate],
    output: Expr
) extends ConcurrentIntermediate
    with SequentialIntermediate {

  assert(
    params.toSet.size == params.size,
    "function parameters should be unique"
  )
  for (param <- params) {
    if (!param.typ.isData) {
      throw new IllegalArgumentException(
        s"invalid type for parameter $param in function: ${param.typ}"
      )
    }
  }
  if (!output.typ.isData) {
    throw new IllegalArgumentException(
      s"invalid type for output of function: ${output.typ}"
    )
  }

  def freeVars: Set[Param] = {
    // TODO: Why not just take all the free variables in locals and output
    //       minus the params and locals?
    val paramSet = params.toSet
    val (bound, free) = this.locals
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
    var newIntermediates = ListMap[Param, SequentialIntermediate]()
    for ((x, i) <- this.locals) {
      val newI = i.substitute(subs)
      val (Seq(newX), newSubs) = Substitution.enterBinder(Seq(x), subs)
      subs = newSubs
      newIntermediates += (newX -> newI)
    }
    val newOutput = this.output.subPreserveType(subs)
    FunctionIntermediate(newParams, newIntermediates, newOutput)
  }

  def map(f: Expr => Expr): FunctionIntermediate = {
    FunctionIntermediate(
      this.params.map(_.map(f).asInstanceOf[Param]),
      this.locals.map({ case (x, i) => x -> i.map(f) }),
      this.output.map(f)
    )
  }

  def toDeclarativeItem(
      designator: String,
      options: VhdlGeneratorOptions
  ): String = {
    val subprogramSpecification = {
      // TODO: What if there are no parameters? It doesn't look like formal_parameter_list can be empty
      val formalParameterList = this.params
        .map({ x =>
          val identifier = x.name
          val subtypeIndication = VhdlType(x.typ).vhdlName
          // See IEEE Std 1076-2002 section 4.3.2
          s"$identifier: $subtypeIndication"
        })
        // See IEEE Std 1076-2002 section 4.3.2.1
        .mkString("; ")
      val returnTypeMark = VhdlType(this.output.typ).vhdlTypeMark
      // See IEEE Std 1076-2002 section 2.1
      s"pure function $designator ($formalParameterList) return $returnTypeMark"
    }
    val subprogramDeclarativePart = this.locals
      .map({
        case (x, _: FunctionIntermediate) =>
          throw new AssertionError(
            "nested functions should have been removed in an earlier compilation stage"
              + s" (found function ${x.name} inside $designator)"
          )
        case (x, _) => VariableTarget(x).toDecl
      })
      .mkString("\n")
    val subprogramStatementPart = this.locals
      .map({ case (x, i) =>
        i.toSequentialStatement(VariableTarget(x), options)
      })
      .toSeq
      .:+({
        val expression = VhdlExprGenerator.toVhdl(this.output)
        // See IEEE Std 1076-2002 section 8.12
        s"return $expression;"
      })
      .mkString("\n")
    // See IEEE Std 1076-2002 section 2.2
    s"""$subprogramSpecification is
       |${indent(subprogramDeclarativePart)}
       |begin
       |${indent(subprogramStatementPart)}
       |end function $designator;
       |""".stripMargin.stripTrailing
  }

  override def toConcurrentStatement(
      target: Target,
      options: VhdlGeneratorOptions,
      enable: String
  ): String = ""

  override def toSequentialStatement(
      target: Target,
      options: VhdlGeneratorOptions
  ): String = ""
}

/** An intermediate that builds a vector using a `for generate` loop.
  *
  * @param loopVars
  *   loop counter variables.
  * @param loopBoundsInclusive
  *   maximum values of the loop counter variables.
  * @param body
  *   contents of the loop.
  */
case class ForGenerateIntermediate(
    loopVars: Seq[Param],
    // TODO: Allow the loop bounds to be arbitrary expressions?
    //       I would then need to be very careful about things like substitution, intermediate
    //       insertion, etc.
    //       In fact, this flattened loop representation might not work at all, if loop bounds can
    //       depend on earlier loop variables.
    loopBoundsInclusive: Seq[Int],
    body: ConcurrentIntermediate
) extends ConcurrentIntermediate {

  require(
    loopVars.length == loopBoundsInclusive.length,
    s"different number of loop variables and loop bounds: ${loopVars.length} vs ${loopBoundsInclusive.length}"
  )

  override def freeVars: Set[Param] = {
    this.body.freeVars.diff(this.loopVars.toSet)
  }

  override def substitute(subs: Map[Expr, Expr]): ForGenerateIntermediate = {
    val loopVarSet = this.loopVars.toSet
    val newSubs = subs.filterKeys(_.freeVars.intersect(loopVarSet).isEmpty)
    ForGenerateIntermediate(
      this.loopVars,
      this.loopBoundsInclusive,
      this.body.substitute(newSubs)
    )
  }

  override def map(f: Expr => Expr): ForGenerateIntermediate = {
    ForGenerateIntermediate(
      this.loopVars.map(_.map(f).asInstanceOf[Param]),
      this.loopBoundsInclusive,
      this.body.map(f)
    )
  }

  override def toConcurrentStatement(
      target: Target,
      options: VhdlGeneratorOptions,
      enable: String
  ): String = {
    this.toVhdl(
      target,
      this.loopVars,
      this.loopBoundsInclusive,
      options,
      enable
    )
  }

  private def toVhdl(
      target: Target,
      loopVars: Seq[Param],
      loopBounds: Seq[Int],
      options: VhdlGeneratorOptions,
      enable: String
  ): String = {
    (loopVars, loopBounds) match {
      case (Seq(), Seq()) =>
        this.body.toConcurrentStatement(target, options, enable)
      case (Seq(iterVar, otherVars @ _*), Seq(bound, otherBounds @ _*)) =>
        val intIterVar = iterVar.freshCopy
        val iterVarTyp = VhdlType(iterVar.typ).vhdlName
        val TyUInt(bitwidth) = iterVar.typ
        val inside = toVhdl(
          target.index(iterVar.name),
          otherVars,
          otherBounds,
          options,
          enable
        )
        val label = Param("L")().name
        s"""$label : for $intIterVar in 0 to $bound generate
           |    constant $iterVar : $iterVarTyp := to_unsigned($intIterVar, $bitwidth);
           |begin
           |${indent(inside)}
           |end generate $label;
           |""".stripMargin.stripTrailing
      case _ =>
        throw new AssertionError("mismatched list lengths")
    }
  }
}

object ForGenerateIntermediate {

  def apply(body: ConcurrentIntermediate): ForGenerateIntermediate = {
    ForGenerateIntermediate(Seq.empty, Seq.empty, body)
  }
}

case class BlockIntermediate(
    locals: ListMap[Param, ConcurrentIntermediate],
    body: ConcurrentIntermediate
) extends ConcurrentIntermediate {

  override def freeVars: Set[Param] = {
    this.locals
      .flatMap({ case (_, i) => i.freeVars })
      .++(this.body.freeVars)
      .toSet
      .diff(this.locals.keySet)
  }

  override def substitute(subs: Map[Expr, Expr]): BlockIntermediate = {
    val willCapture = subs.exists({ case (_, e) =>
      e.freeVars.intersect(this.locals.keySet).nonEmpty
    })
    if (willCapture) {
      // TODO: Handle variable capture
      ???
    }
    BlockIntermediate(
      this.locals.map({ case (x, i) => x -> i.substitute(subs) }),
      this.body.substitute(subs)
    )
  }

  override def map(f: Expr => Expr): BlockIntermediate = {
    BlockIntermediate(
      this.locals.map({ case (x, i) => x -> i.map(f) }),
      this.body.map(f)
    )
  }

  override def toConcurrentStatement(
      target: Target,
      options: VhdlGeneratorOptions,
      enable: String
  ): String = {
    val blockDeclarativePart = this.locals
      .map({
        // TODO: Deduplicate this code somehow?
        case (f, i: FunctionIntermediate) =>
          i.toDeclarativeItem(f.name, options)
        case (x, _) => SignalTarget(x).toDecl
      })
      .mkString("\n")
    val blockStatementPart = this.locals
      .map({ case (x, i) =>
        i.toConcurrentStatement(SignalTarget(x), options, enable)
      })
      .toSeq
      .+(this.body.toConcurrentStatement(target, options, enable))
      .mkString("\n")
    val label = Param("B")().name
    // TODO: This code looks a lot like the code in CustomVhdlComponent. Can I deduplicate it somehow?
    // See IEEE Std 1076-2002 section 9.1
    s"""$label: block is
       |${indent(blockDeclarativePart)}
       |begin
       |${indent(blockStatementPart)}
       |end block $label;
       |""".stripMargin.stripTrailing
  }
}

/** An instantiation of an IP block (e.g., the native Agilex DSPs).
  */
trait IpBlockInst extends ConcurrentIntermediate {

  // TODO: Get rid of these by using F-bounded polymorphism?
  override def substitute(subs: Map[Expr, Expr]): IpBlockInst
  override def map(f: Expr => Expr): IpBlockInst
}

/** An intermediate that consists of some expression that is not an IP block and
  * whose type is a "data" type (see [[Type.isData]]).
  */
sealed trait SimpleIntermediate
    extends ConcurrentIntermediate
    with SequentialIntermediate {

  // TODO: Get rid of these by using F-bounded polymorphism?
  override def substitute(subs: Map[Expr, Expr]): SimpleIntermediate
  override def map(f: Expr => Expr): SimpleIntermediate
}

/** An intermediate variable whose value is the data of a producer stream.
  *
  * @param p
  *   the name of the producer stream.
  */
case class StmDataIntermediate(p: Param) extends SimpleIntermediate {

  override def freeVars: Set[Param] = Set(this.p)

  override def substitute(subs: Map[Expr, Expr]): StmDataIntermediate = {
    StmDataIntermediate(this.p.subPreserveType(subs).asInstanceOf[Param])
  }

  override def map(f: Expr => Expr): StmDataIntermediate = {
    StmDataIntermediate(f(p).asInstanceOf[Param])
  }

  override def toConcurrentStatement(
      target: Target,
      options: VhdlGeneratorOptions,
      enable: String
  ): String = {
    val waveform = this.rhs(target, options)
    // See IEEE Std 1076-2002 section 9.5.1
    s"${target.name} <= $waveform;"
  }

  override def toSequentialStatement(
      target: Target,
      options: VhdlGeneratorOptions
  ): String = {
    val waveform = this.rhs(target, options)
    // See IEEE Std 1076-2002 sections 8.4 and 8.5
    s"${target.name} ${target.assign} $waveform;"
  }

  private def rhs(target: Target, options: VhdlGeneratorOptions): String = {
    val vhdlTyp = VhdlType(target.typ)
    VhdlConversionGenerator.fromStdLogicVector(
      if (options.handshake) s"${p.name}_data" else p.name,
      vhdlTyp
    )
  }
}

/** An intermediate variable whose value is an expression.
  */
case class ExprIntermediate(e: Expr) extends SimpleIntermediate {

  override def freeVars: Set[Param] = this.e.freeVars

  override def substitute(subs: Map[Expr, Expr]): ExprIntermediate = {
    ExprIntermediate(this.e.subPreserveType(subs))
  }

  override def map(f: Expr => Expr): ExprIntermediate = {
    ExprIntermediate(f(e))
  }

  override def toConcurrentStatement(
      target: Target,
      options: VhdlGeneratorOptions,
      enable: String
  ): String = {
    val waveform = VhdlExprGenerator.toVhdl(this.e)
    // See IEEE Std 1076-2002 section 9.5.1
    s"${target.name} <= $waveform;"
  }

  override def toSequentialStatement(
      target: Target,
      options: VhdlGeneratorOptions
  ): String = {
    val waveform = VhdlExprGenerator.toVhdl(this.e)
    // See IEEE Std 1076-2002 sections 8.4 and 8.5
    s"${target.name} ${target.assign} $waveform;"
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
case class MuxIntermediate(c: Expr, t: Expr, f: Expr)
    extends SimpleIntermediate {

  override def substitute(subs: Map[Expr, Expr]): MuxIntermediate = {
    MuxIntermediate(
      this.c.subPreserveType(subs),
      this.t.subPreserveType(subs),
      this.f.subPreserveType(subs)
    )
  }

  override def map(f: Expr => Expr): MuxIntermediate = {
    MuxIntermediate(f(this.c), f(this.t), f(this.f))
  }

  override def freeVars: Set[Param] = {
    c.freeVars ++ t.freeVars ++ f.freeVars
  }

  def toConcurrentStatement(
      target: Target,
      options: VhdlGeneratorOptions,
      enable: String
  ): String = {
    val c = VhdlExprGenerator.toVhdl(this.c)
    val t = VhdlExprGenerator.toVhdl(this.t)
    val f = VhdlExprGenerator.toVhdl(this.f)
    // See IEEE Std 1076-2002 section 9.5.1
    s"${target.name} <= ($t) when ($c) else ($f);"
  }

  def toSequentialStatement(
      target: Target,
      options: VhdlGeneratorOptions
  ): String = {
    val c = VhdlExprGenerator.toVhdl(this.c)
    val sequenceOfStatementsIfTrue = {
      val waveform = VhdlExprGenerator.toVhdl(this.t)
      // See IEEE Std 1076-2002 sections 8.4 and 8.5
      s"${target.name} ${target.assign} $waveform;"
    }
    val sequenceOfStatementsIfFalse = {
      val waveform = VhdlExprGenerator.toVhdl(this.f)
      // See IEEE Std 1076-2002 sections 8.4 and 8.5
      s"${target.name} ${target.assign} $waveform;"
    }
    // See IEEE Std 1076-2002 section 8.7
    s"""if ($c) then
       |$sequenceOfStatementsIfTrue
       |else
       |$sequenceOfStatementsIfFalse
       |end if;
       |""".stripMargin.stripTrailing
  }
}

// -------------------------------------------------------------------------------------------------

sealed trait Accumulator extends ConcurrentIntermediate {

  private val AlwaysEnabled: String = "true"

  // TODO: Get rid of these by using F-bounded polymorphism?
  override def substitute(subs: Map[Expr, Expr]): Accumulator
  override def map(f: Expr => Expr): Accumulator

  protected def makeProcess(
      name: String,
      initStmt: Option[String],
      nextStmt: String,
      options: VhdlGeneratorOptions,
      enable: String
  ): String = {
    val processStatementPart = {
      (initStmt, enable) match {
        case (None, AlwaysEnabled) =>
          nextStmt
        case (None, enable) =>
          s"""if ($enable) then
             |${indent(nextStmt)}
             |end if;
             |""".stripMargin.stripTrailing
        case (Some(resetStmt), AlwaysEnabled) =>
          s"""if sl2bool(${options.reset}) then
             |${indent(resetStmt)}
             |else
             |${indent(nextStmt)}
             |end if;
             |""".stripMargin.stripTrailing
        case (Some(resetStmt), enable) =>
          s"""if sl2bool(${options.reset}) then
             |${indent(resetStmt)}
             |elsif ($enable) then
             |${indent(nextStmt)}
             |end if;
             |""".stripMargin.stripTrailing
      }
    }
    val waitStatement = if (this.freeVars.isEmpty) {
      s"wait;"
    } else {
      // See IEEE Std 1076-2002 section 8.1
      val sensitivityList =
        this.freeVars.map(_.name).toSeq.sorted.mkString(", ")
      s"wait on $sensitivityList;"
    }
    val label = Param("P")().name
    // See IEEE Std 1076-2002 section 9.2
    s"""-- Register $name
       |$label : process
       |begin
       |${indent(processStatementPart)}
       |$waitStatement
       |end process $label;
       |""".stripMargin.stripTrailing
  }
}

case class ExprAccumulator(
    init: Option[SimpleIntermediate],
    next: SimpleIntermediate
) extends Accumulator {

  override def freeVars: Set[Param] = {
    next.freeVars ++
      init.toSet.flatMap((i: SimpleIntermediate) => i.freeVars)
  }

  override def substitute(subs: Map[Expr, Expr]): ExprAccumulator = {
    ExprAccumulator(init.map(_.substitute(subs)), next.substitute(subs))
  }

  override def map(f: Expr => Expr): ExprAccumulator = {
    ExprAccumulator(init.map(i => i.map(f)), next.map(f))
  }

  def toConcurrentStatement(
      target: Target,
      options: VhdlGeneratorOptions,
      enable: String
  ): String = {
    this.makeProcess(
      name = target.name,
      initStmt = this.init.map(_.toSequentialStatement(target, options)),
      nextStmt = this.next.toSequentialStatement(target, options),
      options = options,
      enable = enable
    )
  }
}

/** An accumulator which is a vector that is updated in at most one location per
  * time step.
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

  override def freeVars: Set[Param] = {
    cond.freeVars ++ index.freeVars ++ value.freeVars
  }

  override def substitute(subs: Map[Expr, Expr]): VecWriteAccumulator = {
    VecWriteAccumulator(
      cond.subPreserveType(subs),
      index.subPreserveType(subs),
      value.subPreserveType(subs)
    )
  }

  override def map(f: Expr => Expr): VecWriteAccumulator = {
    VecWriteAccumulator(f(cond), f(index), f(value))
  }

  def toConcurrentStatement(
      target: Target,
      options: VhdlGeneratorOptions,
      enable: String
  ): String = {
    val condVhdl = VhdlExprGenerator.toVhdl(this.cond)
    val idxVhdl = VhdlExprGenerator.toVhdlInteger(this.index)
    val writeVhdl = VhdlExprGenerator.toVhdl(this.value)
    val nextStatement = {
      // See IEEE Std 1076-2002 section 9.5.1
      s"${target.name}($idxVhdl) <= $writeVhdl;"
    }
    this.makeProcess(
      name = target.name,
      initStmt = None,
      nextStmt = nextStatement,
      options = options,
      enable = s"$enable and ($condVhdl)"
    )
  }
}

/** An accumulator which is a vector that gets shifted left once per time step.
  *
  * I want to handle this specially because this allows me to merge registers
  * into DSP blocks, for example.
  *
  * @param incoming
  *   the value written to the right-most index at each step.
  */
case class VecShiftLeftAccumulator(
    len: Long,
    init: Option[SimpleIntermediate],
    incoming: Expr
) extends Accumulator {

  require(this.len >= 1, "shift register accumulator cannot be empty")
  require(
    this.incoming.hasType,
    s"incoming expression must have a type (${this.incoming} is missing a type)"
  )

  override def freeVars: Set[Param] = {
    this.incoming.freeVars ++
      this.init.toSet.flatMap((i: SimpleIntermediate) => i.freeVars)
  }

  override def substitute(subs: Map[Expr, Expr]): Accumulator = {
    VecShiftLeftAccumulator(
      this.len,
      this.init.map(_.substitute(subs)),
      this.incoming.subPreserveType(subs)
    )
  }

  override def map(f: Expr => Expr): Accumulator = {
    VecShiftLeftAccumulator(
      this.len,
      this.init.map(_.map(f)),
      f(this.incoming)
    )
  }

  def toConcurrentStatement(
      target: Target,
      options: VhdlGeneratorOptions,
      enable: String
  ): String = {
    val initStmt = this.init.map(_.toSequentialStatement(target, options))
    val nextStmt = {
      val shift = if (this.len <= 1) {
        ""
      } else {
        s"${target.name}(0 to ${this.len - 2}) <= ${target.name}(1 to ${this.len - 1});"
      }
      val incomingVhdl = VhdlExprGenerator.toVhdl(this.incoming)
      val saveIncoming =
        s"${target.name}(${this.len - 1}) <= $incomingVhdl;"
      s"$shift$saveIncoming"
    }
    this.makeProcess(
      name = target.name,
      initStmt = initStmt,
      nextStmt = nextStmt,
      options = options,
      enable = enable
    )
  }
}
