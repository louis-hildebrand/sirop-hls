package mhir.sugar

import com.typesafe.scalalogging.Logger
import mhir.ir.{ExprPrinter => EP, _}
import mhir.logging.time
import mhir.typecheck.{TProd, TypeCheck, TypeChecker, TypeError}

case class PatternFunction(p: Pattern, body: Expr)(typ: Type = Missing)
    extends SyntaxSugar(p, body)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(p: Pattern, body) => PatternFunction(p, body)(typ)
      case _                     => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val p = this.p.tchk(context, constValues)
    val body = this.body.tchk(context ++ makeContext(this.p), constValues)
    val typ = p.typ ->: body.typ
    this.rebuild(typ, Seq(p, body))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val x = this.p.toParam
    val subs = makeSubs(this.p, x)
    Function(x, this.body.subPreserveType(subs))().tchk().lower
  }

  override def precedence: Int = Precedence.Max

  override def displayOneLine(): String = {
    val bodyStr =
      EP.displayOneLine(this.body, parentPrecedence = Precedence.Max)
    s"@${this.p} => $bodyStr"
  }

  override def displayMultiLine(maxWidth: Int): String = {
    val bodyStr = EP.display(
      body,
      maxWidth = maxWidth - EP.Indent.length,
      parentPrecedence = Precedence.Max
    )
    s"@${this.p} =>\n${EP.indent(bodyStr)}"
  }

  override def annotateFuncSyntaxSugar(typ: Type*): Expr = {
    require(typ.nonEmpty)
    val newP = this.p.mergeType(typ.head)
    val newBody = if (typ.tail.isEmpty) {
      this.body
    } else {
      this.body.annotateFunc(typ.tail: _*)
    }
    PatternFunction(newP, newBody)()
  }

  private def makeSubs(p: Pattern, target: Expr): Map[Expr, Expr] = {
    p match {
      case TuplePattern(elems @ _*) =>
        elems.zipWithIndex
          .flatMap({ case (p, i) =>
            val ii = C(i)(TyAnyInt.tightest(0, i))
            makeSubs(p, TupleAccess(target, ii)(p.typ))
          })
          .toMap
      case ParamPattern(x) => Map(x -> target)
    }
  }

  private def makeContext(p: Pattern): Map[Param, Type] = {
    p match {
      case ParamPattern(x)          => Map(x -> x.typ)
      case TuplePattern(elems @ _*) => elems.flatMap(makeContext).toMap
    }
  }

  override lazy val freeVars: Set[Param] = {
    this.body.freeVars.diff(this.p.params.toSet)
  }

  override def sugarSubAndKeepType(
      subs: Map[Expr, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val patParams = this.p.params.toSet
    val wouldCapture = subs.exists({ case (_, rhs) =>
      rhs.freeVars.intersect(patParams).nonEmpty
    })
    val (newP, patSubs) = if (wouldCapture) {
      val patSubs = p.params
        .map({ x =>
          x -> x.freshCopy.rebuild(x.typ.substitute(subs)).asInstanceOf[Param]
        })
        .toMap[Param, Param]
      (p.rename(patSubs), patSubs)
    } else {
      (p, Map[Param, Param]())
    }
    val newSubs =
      subs
        // If a free variable on the LHS is becoming bound, it will never match
        // again and therefore we can discard that substitution
        .filter({ case (lhs, _) => lhs.freeVars.intersect(patParams).isEmpty })
        // Rename the bound variables if necessary
        .++(patSubs)
    PatternFunction(
      newP,
      body.subPreserveType(newSubs)
    )(this.typ.substitute(subs))
  }

  override def sugarSubAndEraseType(
      subs: Map[Expr, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val patParams = this.p.params.toSet
    val wouldCapture = subs.exists({ case (_, rhs) =>
      rhs.freeVars.intersect(patParams).nonEmpty
    })
    val (newP, patSubs) = if (wouldCapture) {
      val patSubs = p.params
        .map({ x =>
          x -> x.freshCopy.rebuild(x.typ.substitute(subs)).asInstanceOf[Param]
        })
        .toMap[Param, Param]
      (p.rename(patSubs), patSubs)
    } else {
      (p, Map[Param, Param]())
    }
    val newSubs =
      subs
        // If a free variable on the LHS is becoming bound, it will never match
        // again and therefore we can discard that substitution
        .filter({ case (lhs, _) => lhs.freeVars.intersect(patParams).isEmpty })
        // Rename the bound variables if necessary
        .++(patSubs)
    PatternFunction(newP, body.subAndEraseType(newSubs))()
  }

  override def equals(x: Any): Boolean = {
    x match {
      case that: PatternFunction if this.p == that.p =>
        // Skip the substitutions, which may be slow
        this.body == that.body
      case that: PatternFunction =>
        implicit val c: Canonicalizer = NoOpCanonicalizer
        PatternFunction.zip(this.p, that.p) match {
          case None => false
          case Some(pairs) =>
            val newParams = pairs.map({ case (x, _) => x.freshCopy })
            val thisSubs =
              pairs.map({ case (x, _) => x }).zip(newParams).toMap[Expr, Expr]
            val thisRenamed = this.body.subAndEraseType(thisSubs)
            val thatSubs =
              pairs.map({ case (_, y) => y }).zip(newParams).toMap[Expr, Expr]
            val thatRenamed = that.body.subAndEraseType(thatSubs)
            thisRenamed == thatRenamed
        }
      case _ => false
    }
  }

  override lazy val hashCode: Int = {
    // This implementation should be correct, but it may cause excessive
    // collisions when dealing with nested functions. For example,
    // x => y => x - y and x => y => y - x will be assigned the same hash code.
    implicit val c: Canonicalizer = NoOpCanonicalizer
    val renamings = this.p.params.zipWithIndex
      .map({ case (x, i) => x -> PatternFunction.hashCodeParam(i, x.typ) })
      .toMap
    (
      this.p.rename(renamings),
      this.body.subAndEraseType(renamings.toMap[Expr, Expr])
    ).hashCode
  }
}

object PatternFunction {

  private def freshenPattern(p: Pattern): (Pattern, Map[Param, Param]) = {
    p match {
      case ParamPattern(x) =>
        val y = x.freshCopy
        (ParamPattern(y), Map(x -> y))
      case TuplePattern(elems @ _*) =>
        val (patterns, renamings) = elems.map(freshenPattern).unzip
        (TuplePattern(patterns: _*), renamings.flatten.toMap)
    }
  }

  private def zip(p1: Pattern, p2: Pattern): Option[Seq[(Param, Param)]] = {
    (p1, p2) match {
      case (ParamPattern(x), ParamPattern(y)) =>
        Some(Seq((x, y)))
      case (TuplePattern(elems1 @ _*), TuplePattern(elems2 @ _*))
          if elems1.length == elems2.length =>
        val zippedElems =
          elems1.zip(elems2).map({ case (p1, p2) => zip(p1, p2) })
        if (zippedElems.contains(None)) {
          None
        } else {
          Some(zippedElems.flatMap(_.get))
        }
      case _ =>
        None
    }
  }

  private def hashCodeParam(i: Int, typ: Type): Param = {
    Param("_PatternFunctionHashCode", i + 1)(typ)
  }
}

/** A let expression.
  *
  * @param x
  *   the variable to declare.
  * @param v
  *   the value to assign to the variable.
  * @param in
  *   an expression that may use the variable.
  */
case class Let(x: Param, v: Expr, in: Expr)(typ: Type = Missing)
    extends SyntaxSugar(x, v, in)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(x: Param, v, in) => Let(x, v, in)(typ)
      case _                    => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val v = this.v.tchk(context, constValues)
    val x = this.x.typ match {
      case Missing => this.x.rebuild(v.typ).asInstanceOf[Param]
      case t =>
        if (t.equalsGivenConstants(v.typ, constValues)) {
          this.x
        } else {
          throw new TypeError(
            s"cannot assign value of type ${v.typ} to variable of type $t",
            TypeChecker.relevantBindings(constValues, v.typ, t)
          )
        }
    }
    val in = this.in.tchk(context + (this.x -> v.typ), constValues)
    Let(x, v, in)(in.typ)
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    time(s"lowering $className (${this.x})") {
      requireType()
      (v.typ.lower, in.typ.lower) match {
        case (_: TyStm, TyStm(_, inLen)) =>
          val x = this.x.lower.asInstanceOf[Param]
          val v = this.v.lower
          val in = this.in.lower
          // Play it safe and buffer the whole input stream.
          // The optimizer may be able to improve this.
          LetStm(inLen, x, v, in)().tchk()
        case (TyData(_), TyData(_)) =>
          val x = this.x.lower.asInstanceOf[Param]
          val v = this.v.lower
          val in = this.in.lower
          Let(x, v, in)().asFunCall().tchk()
        case _ =>
          this.in.subPreserveType(this.x -> this.v).lower
      }
    }(Let.logger)
  }

  override def sugarSubAndKeepType(
      subs: Map[Expr, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val wouldCapture = subs.exists({ case (_, rhs) =>
      rhs.freeVars.contains(this.x)
    })
    val newX = if (wouldCapture) this.x.freshCopy else this.x
    val newSubs =
      subs
        // Substitutions with `x` free on the LHS will never match
        // again, since `x` is now bound.
        .filter({ case (lhs, _) => !lhs.freeVars.contains(this.x) })
        // Rename the bound variable if necessary
        .++(if (this.x == newX) Seq() else Seq(x -> newX))
    Let(
      // There may be substitutions to do within the type annotation
      Param(newX.prefix, newX.id)(newX.typ.substitute(subs)(c)),
      // `x` is not bound here, so use the old subs
      this.v.subPreserveType(subs)(c),
      // `x` is bound here, so use the new subs
      this.in.subPreserveType(newSubs)(c)
    )(this.typ.substitute(subs))
  }

  override def sugarSubAndEraseType(
      subs: Map[Expr, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val wouldCapture = subs.exists({ case (_, rhs) =>
      rhs.freeVars.contains(this.x)
    })
    val newX = if (wouldCapture) this.x.freshCopy else this.x
    val newSubs =
      subs
        // Substitutions with `x` free on the LHS will never match
        // again, since `x` is now bound.
        .filter({ case (lhs, _) => !lhs.freeVars.contains(this.x) })
        // Rename the bound variable if necessary
        .++(if (this.x == newX) Seq() else Seq(x -> newX))
    Let(
      // There may be substitutions to do within the type annotation
      Param(newX.prefix, newX.id)(newX.typ.substitute(subs)(c)),
      // `x` is not bound here, so use the old subs
      this.v.subAndEraseType(subs)(c),
      // `x` is bound here, so use the new subs
      this.in.subAndEraseType(newSubs)(c)
    )()
  }

  override def precedence: Int = Precedence.Max

  override def displayOneLine(): String = {
    val xStr = this.x.typ match {
      case Missing => this.x.name
      case t       => s"${this.x.name}: $t"
    }
    val vStr =
      EP.displayOneLine(this.v, parentPrecedence = this.precedence)
    val inStr =
      EP.displayOneLine(this.in, parentPrecedence = this.precedence)
    s"let $xStr = $vStr in $inStr"
  }

  override def displayMultiLine(maxWidth: Int): String = {
    val xStr = this.x.typ match {
      case Missing => this.x.name
      case t       => s"${this.x.name}: $t"
    }
    val vStr = {
      val str = EP.display(
        this.v,
        maxWidth = maxWidth - EP.Indent.length,
        parentPrecedence = this.precedence
      )
      if (str.contains("\n")) {
        s"(\n${EP.indent(str)}\n)"
      } else {
        str
      }
    }
    val inStr = this.in match {
      case _: Let =>
        EP.displayMultiLine(this.in, maxWidth = maxWidth)
      case _ =>
        EP.display(this.in, maxWidth = maxWidth)
    }
    s"let $xStr = $vStr in\n$inStr"
  }

  private def asFunCall(): FunCall = {
    FunCall(Function(this.x, this.in)(), this.v)()
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: Let => this.asFunCall() == that.asFunCall()
      case _         => false
    }
  }

  override def hashCode(): Int = {
    this.asFunCall().hashCode()
  }
}

object Let {
  private implicit val logger: Logger = Logger(getClass.getName)
}

/** Like a [[Let]] expression, but can have more than one assignment.
  */
object Lets {
  def apply(assignments: (Param, Expr)*)(body: Expr): Expr = {
    assignments.foldRight(body)({ case ((x, v), acc) => Let(x, v, acc)() })
  }
}

/** The default value for a given data type.
  *
  * For example, the default integer is zero, the default boolean is false, and
  * the default tuple contains the default for each element.
  *
  * The default value only makes sense for "data types" as defined by
  * [[Type.isData]]. In particular, there is no default function and no default
  * stream.
  *
  * @param typ
  *   the data type for which to find the default.
  */
case class Default(override val typ: Type) extends SyntaxSugar()(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(newChildren.isEmpty)
    this
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    // Check that the requested type indeed has a default
    DefaultVal(this.typ)
    this
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    DefaultVal(this.typ).tchk()
  }

  override def sugarSubAndKeepType(
      subs: Map[Expr, Expr]
  )(implicit c: Canonicalizer): Expr = {
    Default(this.typ.substitute(subs))
  }

  override def sugarSubAndEraseType(
      subs: Map[Expr, Expr]
  )(implicit c: Canonicalizer): Expr = {
    Default(this.typ.substitute(subs))
  }

  override def precedence: Int = Precedence.Min

  override def displayOneLine(): String = s"default[${this.typ}]"

  override def displayMultiLine(maxWidth: Int): String = this.displayOneLine()
}

// Smart arithmetic and relational operators
// (automatically resize operands as needed)
// ---------------------------------------------------------------------------------------------------------------------

/** Convert from one data type to a corresponding data type that may have
  * differently-sized integers.
  *
  * Only conversions which are possible without data loss (as determined by
  * [[ReshapeData.canReshape]]) are allowed.
  *
  * @param e
  *   the expression to reshape.
  * @param targetType
  *   the desired type.
  */
case class ReshapeData(e: Expr, targetType: Type)(typ: Type = Missing)
    extends SyntaxSugar(e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => ReshapeData(e, targetType)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val newE = e.tchk(context, constValues)
    if (ReshapeData.canReshape(newE.typ, targetType, constValues)) {
      this.rebuild(targetType, Seq(newE))
    } else {
      throw new TypeError(
        s"Value of type ${newE.typ} might not fit in type $targetType."
      )
    }
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val e = this.e.lower
    (e, e.typ, targetType) match {
      case (IntCst(k), _, target) =>
        // This special case is not really necessary, it's just annoying to
        // constantly see expressions like PadTo(0, w) while debugging
        C(k)(target)
      case (_, t1, t2) if t1 ~= t2 => e
      case (_, TyUInt(w1), TyUInt(w2)) =>
        assert(w2 > w1)
        PadTo(e, w2)().tchk()
      case (_, TyUInt(w1), TySInt(w2)) =>
        assert(w2 >= w1 + 1)
        PadTo(ToSigned(e)(), w2)().tchk()
      case (_, TySInt(0), u: TyUInt) =>
        IntCst(0)(u).tchk()
      case (_, TySInt(w1), TySInt(w2)) =>
        assert(w2 > w1)
        PadTo(e, w2)().tchk()
      case (_, _: TyTuple, TyTuple(ts2 @ _*)) =>
        Tuple(
          ts2.zipWithIndex.map({ case (t, i) =>
            ReshapeData(TupleAccess(e, i)(), t)().tchk().lower
          }): _*
        )().tchk()
      case (_, _: TyVec, TyVec(t2, n)) =>
        VecBuild(
          n,
          U32 ::+ (i => ReshapeData(VecAccess(e, i)(), t2)().tchk().lower)
        )().tchk()
      case _ =>
        throw new TypeError(
          s"Value of type ${e.typ} might not fit in type $targetType."
        )
    }
  }

  override def sugarSubAndKeepType(
      subs: Map[Expr, Expr]
  )(implicit c: Canonicalizer): Expr = {
    ReshapeData(
      this.e.subPreserveType(subs),
      this.targetType.substitute(subs)
    )(this.typ.substitute(subs))
  }

  override def sugarSubAndEraseType(
      subs: Map[Expr, Expr]
  )(implicit c: Canonicalizer): Expr = {
    ReshapeData(
      this.e.subAndEraseType(subs),
      this.targetType.substitute(subs)
    )()
  }
}

/** Companion object for [[ReshapeData]].
  */
object ReshapeData {

  /** Decides whether data of type <code>t1</code> can be reshaped to be
    * compatible with type <code>t2</code> <i>without data loss</i>.
    *
    * For example, <code>u16</code> can be converted to <code>u32</code> or to
    * <code>i32</code>. However, <code>u16</code> cannot be converted to
    * <code>u8</code>, nor to <code>i16</code>, because a 16-bit unsigned
    * integer may not fit into an 8-bit integer or a 16-bit signed integer.
    * Furthermore <code>bool</code> cannot be converted to <code>u8</code>
    * because they are entirely different types.
    */
  def canReshape(
      t1: Type,
      t2: Type,
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Boolean = {
    (t1, t2) match {
      case (TyBool, TyBool)         => true
      case (TyUInt(w1), TyUInt(w2)) => w2 >= w1
      case (TyUInt(w1), TySInt(w2)) => (w2 >= w1 + 1) || (w1 == 0 && w2 == 0)
      case (TySInt(w), _: TyUInt)   => w == 0
      case (TySInt(w1), TySInt(w2)) => w2 >= w1
      case (TyTuple(ts1 @ _*), TyTuple(ts2 @ _*)) =>
        (ts1.length == ts2.length
        && ts1
          .zip(ts2)
          .forall({ case (t1, t2) => canReshape(t1, t2, constValues) }))
      case (TyVec(t1, n1), TyVec(t2, n2)) =>
        canReshape(t1, t2, constValues) && c.sameLen(n1, n2, constValues)
      case _ => false
    }
  }

  /** Tries to find the narrowest type such that this type and the given type
    * can both be reshaped to that type.
    */
  def narrowestCommonAncestor(
      t1: Type,
      t2: Type,
      constValues: Map[Param, Expr] = Map()
  )(implicit c: Canonicalizer): Option[Type] = {
    (t1, t2) match {
      case (TyBool, TyBool)         => Some(TyBool)
      case (TyUInt(w1), TyUInt(w2)) => Some(TyUInt(math.max(w1, w2)))
      case (u: TyUInt, TySInt(0))   => Some(u)
      case (TySInt(0), u: TyUInt)   => Some(u)
      case (TyUInt(w1), TySInt(w2)) => Some(TySInt(math.max(w1 + 1, w2)))
      case (TySInt(w1), TyUInt(w2)) => Some(TySInt(math.max(w1, w2 + 1)))
      case (TySInt(0), TySInt(0))   => Some(TyUInt(0))
      case (TySInt(w1), TySInt(w2)) => Some(TySInt(math.max(w1, w2)))
      case (TyTuple(ts1 @ _*), TyTuple(ts2 @ _*)) if ts1.length == ts2.length =>
        val elemTypeOptions = ts1
          .zip(ts2)
          .map({ case (t1, t2) =>
            ReshapeData.narrowestCommonAncestor(t1, t2, constValues)
          })
        if (elemTypeOptions.forall(x => x.isDefined)) {
          Some(TyTuple(elemTypeOptions.map(x => x.get): _*))
        } else {
          None
        }
      case (TyVec(t1, n1), TyVec(t2, n2)) if c.sameLen(n1, n2, constValues) =>
        narrowestCommonAncestor(t1, t2, constValues) match {
          case Some(t) => Some(TyVec(t, n1))
          case None    => None
        }
      case _ => None
    }
  }

  def narrowestCommonAncestor(
      ts: Seq[Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Option[Type] = {
    require(ts.nonEmpty)
    ts.tail.foldLeft[Option[Type]](Some(ts.head))({ case (acc, t) =>
      acc match {
        case None      => None
        case Some(acc) => narrowestCommonAncestor(acc, t, constValues)
      }
    })
  }
}

/** The sum of multiple values, even ones with slightly different types.
  *
  * Each operand will be reshaped to be compatible with the others. However, the
  * result may still overflow. For example, if you add two values of type
  * <code>u8</code>, the result will remain a <code>u8</code>.
  *
  * @param terms
  *   the terms to add up.
  */
case class SmartSum(terms: Expr*)(typ: Type = Missing)
    extends SyntaxSugar(terms: _*)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    SmartSum(newChildren: _*)(typ)
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val newTerms = terms.zipWithIndex.map({ case (e, i) =>
      val newE = e.tchk(context, constValues)
      newE.typ match {
        case _: TyAnyInt => newE
        case t =>
          throw new TypeError(
            s"Term at index $i in $className has type $t."
              + " Expected an integer."
          )
      }
    })
    val typ = if (newTerms.isEmpty) {
      TyUInt(0)
    } else {
      ReshapeData
        .narrowestCommonAncestor(newTerms.map(e => e.typ), constValues)
        .get
    }
    this.rebuild(typ, newTerms)
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val terms = this.terms.map(e => e.lower)
    MaybeSum(terms.map(e => ReshapeData(e, typ)()): _*)().tchk().lower
  }

  override def precedence: Int = Precedence.Sum

  override def displayOneLine(): String = {
    EP.displayOneLineInfixOp(this.terms, "++", this.precedence)
  }

  override def displayMultiLine(maxWidth: Int): String = {
    EP.displayMultiLineInfixOp(
      this.terms,
      "++",
      maxWidth = maxWidth,
      precedence = this.precedence
    )
  }
}

/** The difference of two values, even ones with slightly different types.
  *
  * @param e1
  *   the minuend.
  * @param e2
  *   the subtrahend.
  */
case class SmartDiff(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends SyntaxSugar(e1, e2)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e1, e2) => SmartDiff(e1, e2)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val newLhs = e1.tchk(context, constValues)
    val t1 = newLhs.typ match {
      case t: TyAnyInt => t
      case t =>
        throw new TypeError(
          s"Left-hand side of $className has type $t."
            + " Expected an integer"
        )
    }
    val newRhs = e2.tchk(context, constValues)
    val t2 = newRhs.typ match {
      case t: TyAnyInt => t
      case t =>
        throw new TypeError(
          s"Right-hand side of $className has type $t."
            + " Expected an integer."
        )
    }
    val typ = ReshapeData.narrowestCommonAncestor(t1, t2, constValues).get
    this.rebuild(typ, Seq(newLhs, newRhs))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val typ = this.typ.asInstanceOf[TyAnyInt]
    val e1 = this.e1.lower
    val e2 = this.e2.lower
    // TODO: Lower this to Diff (with undefined overflow behaviour) when I add Diff?
    WrappingDiff(ReshapeData(e1, typ)(), ReshapeData(e2, typ)())().tchk().lower
  }

  override def precedence: Int = Precedence.Sum

  override def displayOneLine(): String = {
    EP.displayOneLineInfixOp(Seq(this.e1, this.e2), "-!!", this.precedence)
  }

  override def displayMultiLine(maxWidth: Int): String = {
    EP.displayMultiLineInfixOp(
      Seq(this.e1, this.e2),
      "-!!",
      maxWidth = maxWidth,
      precedence = this.precedence
    )
  }
}

/** The product of multiple values, even ones with slightly different types.
  *
  * Each operand will be reshaped to be compatible with the others. However, the
  * result may still overflow. For example, if the operands have type
  * <code>u8</code> and <code>u7</code>, then the result will have type
  * <code>u8</code>.
  *
  * @param factors
  *   the values to multiply.
  */
case class SmartProd(factors: Expr*)(typ: Type = Missing)
    extends SyntaxSugar(factors: _*)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    SmartProd(newChildren: _*)(typ)
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val newFactors = factors.zipWithIndex.map({ case (e, i) =>
      val newE = e.tchk(context, constValues)
      newE.typ match {
        case _: TyAnyInt => newE
        case t =>
          throw new TypeError(
            s"Term at index $i in $className has type $t."
              + " Expected an integer."
          )
      }
    })
    val typ = if (newFactors.isEmpty) {
      TyUInt(1)
    } else {
      ReshapeData
        .narrowestCommonAncestor(newFactors.map(e => e.typ), constValues)
        .get
    }
    this.rebuild(typ, newFactors)
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val factors = this.factors.map(e => e.lower)
    MaybeProd(factors.map(e => ReshapeData(e, typ)()): _*)().tchk().lower
  }

  override def precedence: Int = Precedence.Prod

  override def displayOneLine(): String = {
    EP.displayOneLineInfixOp(this.factors, "**", this.precedence)
  }

  override def displayMultiLine(maxWidth: Int): String = {
    EP.displayMultiLineInfixOp(
      this.factors,
      "**",
      maxWidth = maxWidth,
      precedence = this.precedence
    )
  }
}

/** The product of several factors <i>without overflow</i>.
  *
  * The type of this expression will be chosen so as to guarantee that the
  * product can be computed without overflow.
  *
  * @param factors
  *   the values to multiply.
  */
case class SafeProd(factors: Expr*)(typ: Type = Missing)
    extends SyntaxSugar(factors: _*)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    SafeProd(newChildren: _*)(typ)
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val factors =
      this.factors.map(e => e.tchk(context, constValues).expectAnyInt())
    val factorTypes = factors.map(_.typ.asInstanceOf[TyAnyInt])
    val minTyp = TProd(factorTypes: _*)
    val typ = ReshapeData
      .narrowestCommonAncestor(minTyp +: factorTypes, constValues)
      .get
    this.rebuild(typ, factors)
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val factors = this.factors.map(e => e.lower)
    this.typ.asInstanceOf[TyAnyInt] match {
      case TyUInt(0) =>
        // Need this special case because you can't normally resize to a U0,
        // but we know the product will be zero.
        IntCst(0)(TyUInt(0))
      case typ =>
        MaybeProd(factors.map(e => ReshapeData(e, typ)()): _*)().tchk().lower
    }
  }
}

/** The quotient of two values, even ones with slightly different types.
  *
  * @param e1
  *   the numerator.
  * @param e2
  *   the denominator.
  */
case class SmartDiv(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends SyntaxSugar(e1, e2)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e1, e2) => SmartDiv(e1, e2)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val newLhs = e1.tchk(context, constValues)
    val t1 = newLhs.typ match {
      case t: TyAnyInt => t
      case t =>
        throw new TypeError(
          s"Left-hand side of $className has type $t."
            + " Expected an integer"
        )
    }
    val newRhs = e2.tchk(context, constValues)
    val t2 = newRhs.typ match {
      case t: TyAnyInt => t
      case t =>
        throw new TypeError(
          s"Right-hand side of $className has type $t."
            + " Expected an integer."
        )
    }
    val typ = ReshapeData.narrowestCommonAncestor(t1, t2, constValues).get
    this.rebuild(typ, Seq(newLhs, newRhs))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val typ = this.typ.asInstanceOf[TyAnyInt]
    val e1 = this.e1.lower
    val e2 = this.e2.lower
    Div(ReshapeData(e1, typ)(), ReshapeData(e2, typ)())().tchk().lower
  }

  override def precedence: Int = Precedence.Div

  override def displayOneLine(): String = {
    EP.displayOneLineInfixOp(Seq(this.e1, this.e2), "//", this.precedence)
  }

  override def displayMultiLine(maxWidth: Int): String = {
    EP.displayMultiLineInfixOp(
      Seq(this.e1, this.e2),
      "//",
      maxWidth = maxWidth,
      precedence = this.precedence
    )
  }
}

/** Computes [[Mod]] of two values, even ones with slightly different types.
  *
  * @param e1
  *   the numerator.
  * @param e2
  *   the denominator.
  */
case class SmartMod(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends SyntaxSugar(e1, e2)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e1, e2) => SmartMod(e1, e2)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val newLhs = e1.tchk(context, constValues)
    val t1 = newLhs.typ match {
      case t: TyAnyInt => t
      case t =>
        throw new TypeError(
          s"Left-hand side of $className has type $t."
            + " Expected an integer"
        )
    }
    val newRhs = e2.tchk(context, constValues)
    val t2 = newRhs.typ match {
      case t: TyAnyInt => t
      case t =>
        throw new TypeError(
          s"Right-hand side of $className has type $t."
            + " Expected an integer."
        )
    }
    val typ = ReshapeData.narrowestCommonAncestor(t1, t2, constValues).get
    this.rebuild(typ, Seq(newLhs, newRhs))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val typ = this.typ.asInstanceOf[TyAnyInt]
    val e1 = this.e1.lower
    val e2 = this.e2.lower
    Mod(ReshapeData(e1, typ)(), ReshapeData(e2, typ)())().tchk().lower
  }

  override def precedence: Int = Precedence.Mod

  override def displayOneLine(): String = {
    EP.displayOneLineInfixOp(Seq(this.e1, this.e2), "%%", this.precedence)
  }

  override def displayMultiLine(maxWidth: Int): String = {
    EP.displayMultiLineInfixOp(
      Seq(this.e1, this.e2),
      "%%",
      maxWidth = maxWidth,
      precedence = this.precedence
    )
  }
}

case class EnsureUnsigned(e: Expr)(typ: Type = Missing)
    extends SyntaxSugar(e)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => EnsureUnsigned(e)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val e = this.e.tchk().expectAnyInt()
    val outTyp = e.typ.asInstanceOf[TyAnyInt] match {
      case typ: TyUInt => typ
      case TySInt(w)   => TyUInt(w - 1)
    }
    this.rebuild(outTyp, Seq(e))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    this.e.typ.asInstanceOf[TyAnyInt] match {
      case _: TyUInt => e.lower
      case _: TySInt => ToUnsigned(e)().tchk().lower
    }
  }
}
