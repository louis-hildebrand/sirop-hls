package ir

case class VecLength(v: Expr)(typ: Type = Missing) extends SyntaxSugar(v)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(v) => VecLength(v)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newV = v.tchk
    newV.typ match {
      case _: TyVec => this.rebuild(U32, Seq(newV))
      case t =>
        throw new TypeError(
          s"Vector in VecLength has type $t. Expected a vector."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    ReshapeData(v.typ.asInstanceOf[TyVec].n, U32)().tchk().lower()
  }
}

case class Let(x: Param, v: Expr, in: Expr)(typ: Type = Missing)
    extends SyntaxSugar(x, v, in)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(x: Param, v, in) => Let(x, v, in)(typ)
      case _                    => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val v = this.v.tchk(context)
    val in = this.in.tchk(context + (this.x -> v.typ))
    val x = this.x.typ match {
      case Missing => this.x.rebuild(v.typ)
      case t =>
        if (t ~= v.typ) {
          this.x
        } else {
          throw new TypeError(
            s"Cannot assign value of type ${v.typ} to variable of type $t."
          )
        }
    }
    Let(x, v, in)(in.typ)
  }

  override def lowerSyntaxSugar(): Expr = {
    val x = this.x.lower().asInstanceOf[Param]
    val v = this.v.lower()
    val in = this.in.lower()
    val f = Let(x, v, in)().asFunCall()
    if (this.typ != Missing) f.tchk() else f
  }

  override def sugarSubAndKeepType(subs: Map[Expr, Expr]): Expr = {
    val newX = x.freshCopy
    val newIn = in.subPreserveType(x -> newX)
    Let(newX, v.subPreserveType(subs), newIn.subPreserveType(subs))(this.typ)
  }

  override def sugarSubAndEraseType(subs: Map[Expr, Expr]): Expr = {
    val newX = x.freshCopy
    val newIn = in.subAndEraseType(x -> newX)
    Let(newX, v.subAndEraseType(subs), newIn.subAndEraseType(subs))()
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

// Default value for a given datatype (zero for int, false for bool, tuple of
// defaults for a tuple, etc.).
// Note that this should NOT be used for functions or streams.
case class Default(override val typ: Type) extends SyntaxSugar()(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(newChildren.isEmpty)
    this
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    // Check that the requested type indeed has a default
    Default.getDefault(this.typ)
    this
  }

  override def lowerSyntaxSugar(): Expr =
    Default.getDefault(this.typ).tchk().lower()
}
case object Default {
  private[ir] def getDefault(typ: Type): Expr = {
    getDefaultOpt(typ) match {
      case Some(v) => v
      case None =>
        throw new IllegalArgumentException(
          s"Cannot construct default value for type $typ."
        )
    }
  }

  private def getDefaultOpt(typ: Type): Option[Expr] = {
    typ match {
      case _: TyAnyInt      => Some(IntCst(0)(typ))
      case TyBool           => Some(False)
      case TyTuple(ts @ _*) => Some(Tuple(ts.map(t => getDefault(t)): _*)(typ))
      case TyVec(t, n) =>
        Some(VecBuild(n.tchk(), U32 ::+ (_ => getDefault(t)))(typ))
      case _ => None
    }
  }
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newE = e.tchk
    if (ReshapeData.canReshape(newE.typ, targetType)) {
      this.rebuild(targetType, Seq(newE))
    } else {
      throw new TypeError(
        s"Cannot reshape from type ${newE.typ} to type $targetType."
      )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val e = this.e.lower()
    (e.typ, targetType) match {
      case (t1, t2) if t1 ~= t2 => e
      case (TyUInt(w1), TyUInt(w2)) =>
        assert(w2 > w1)
        PadTo(e, w2)().tchk().lower()
      case (TyUInt(w1), TySInt(w2)) =>
        assert(w2 >= w1 + 1)
        PadTo(ToSigned(e)(), w2)().tchk().lower()
      case (TySInt(0), u: TyUInt) =>
        IntCst(0)(u).tchk().lower()
      case (TySInt(w1), TySInt(w2)) =>
        assert(w2 > w1)
        PadTo(e, w2)().tchk().lower()
      case (_: TyTuple, TyTuple(ts2 @ _*)) =>
        Tuple(
          ts2.zipWithIndex.map({ case (t, i) =>
            ReshapeData(TupleAccess(e, i)(), t)()
          }): _*
        )().tchk().lower()
      case (_: TyVec, TyVec(t2, n)) =>
        VecBuild(n, U32 ::+ (i => ReshapeData(VecAccess(e, i)(), t2)()))()
          .tchk()
          .lower()
      case _ =>
        throw new TypeError(
          s"Cannot reshape from type ${e.typ} to $targetType."
        )
    }
  }
}

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
  def canReshape(t1: Type, t2: Type): Boolean = {
    (t1, t2) match {
      case (TyBool, TyBool)         => true
      case (TyUInt(w1), TyUInt(w2)) => w2 >= w1
      case (TyUInt(w1), TySInt(w2)) => (w2 >= w1 + 1) || (w1 == 0 && w2 == 0)
      case (TySInt(w), _: TyUInt)   => w == 0
      case (TySInt(w1), TySInt(w2)) => w2 >= w1
      case (TyTuple(ts1 @ _*), TyTuple(ts2 @ _*)) =>
        (ts1.length == ts2.length
        && ts1.zip(ts2).forall({ case (t1, t2) => canReshape(t1, t2) }))
      case (TyVec(t1, n1), TyVec(t2, n2)) =>
        canReshape(t1, t2) && Type.sameLen(n1, n2)
      case _ => false
    }
  }

  /** Tries to find the narrowest type such that this type and the given type
    * can both be reshaped to that type.
    */
  def narrowestCommonAncestor(t1: Type, t2: Type): Option[Type] = {
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
          .map({ case (t1, t2) => ReshapeData.narrowestCommonAncestor(t1, t2) })
        if (elemTypeOptions.forall(x => x.isDefined)) {
          Some(TyTuple(elemTypeOptions.map(x => x.get): _*))
        } else {
          None
        }
      case (TyVec(t1, n1), TyVec(t2, n2)) if Type.sameLen(n1, n2) =>
        narrowestCommonAncestor(t1, t2) match {
          case Some(t) => Some(TyVec(t, n1))
          case None    => None
        }
      case _ => None
    }
  }

  def narrowestCommonAncestor(ts: Seq[Type]): Option[Type] = {
    require(ts.nonEmpty)
    ts.tail.foldLeft[Option[Type]](Some(ts.head))({ case (acc, t) =>
      acc match {
        case None      => None
        case Some(acc) => narrowestCommonAncestor(acc, t)
      }
    })
  }
}

/** Decides whether two values are the same, even ones with slightly different
  * types.
  *
  * @param e1
  *   the first expression to compare.
  * @param e2
  *   the second expression to compare.
  */
case class SmartEqual(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends SyntaxSugar(e1, e2)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e1, e2) => SmartEqual(e1, e2)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newE1 = e1.tchk
    newE1.typ match {
      case t if t.isData => ()
      case t =>
        throw new TypeError(
          s"Left-hand side of $className has non-data type $t."
        )
    }
    val newE2 = e2.tchk
    newE2.typ match {
      case t if t.isData => ()
      case t =>
        throw new TypeError(
          s"Right-hand side of $className has non-data type $t."
        )
    }
    ReshapeData.narrowestCommonAncestor(newE1.typ, newE2.typ) match {
      case Some(_) =>
        this.rebuild(TyBool, Seq(newE1, newE2))
      case None =>
        throw new TypeError(
          s"Left-hand side of $className has type ${newE1.typ}, but right-hand side has type ${newE2.typ}."
        )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val e1 = this.e1.lower()
    val e2 = this.e2.lower()
    val t = ReshapeData.narrowestCommonAncestor(e1.typ, e2.typ).get
    Equal(ReshapeData(e1, t)(), ReshapeData(e2, t)())().tchk().lower()
  }
}

/** Decides whether one value is strictly less than another, even if the values
  * have slightly different types.
  *
  * @param e1
  *   the first expression to compare.
  * @param e2
  *   the second expression to compare.
  */
case class SmartLessThan(e1: Expr, e2: Expr)(typ: Type = Missing)
    extends SyntaxSugar(e1, e2)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e1, e2) => SmartLessThan(e1, e2)(typ)
      case _           => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newLhs = e1.tchk
    newLhs.typ match {
      case _: TyAnyInt => ()
      case t =>
        throw new TypeError(
          s"Left-hand side of $className has type $t."
            + " Expected an integer."
        )
    }
    val newRhs = e2.tchk
    newRhs.typ match {
      case _: TyAnyInt => ()
      case t =>
        throw new TypeError(
          s"Right-hand side of $className has type $t."
            + " Expected an integer."
        )
    }
    this.rebuild(TyBool, Seq(newLhs, newRhs))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val e1 = this.e1.lower()
    val e2 = this.e2.lower()
    val t = ReshapeData.narrowestCommonAncestor(e1.typ, e2.typ).get
    LessThan(ReshapeData(e1, t)(), ReshapeData(e2, t)())().tchk().lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newTerms = terms.zipWithIndex.map({ case (e, i) =>
      val newE = e.tchk(context)
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
      ReshapeData.narrowestCommonAncestor(newTerms.map(e => e.typ)).get
    }
    this.rebuild(typ, newTerms)
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val terms = this.terms.map(e => e.lower())
    if (terms.isEmpty) {
      IntCst(0)(this.typ)
    } else {
      val typ = this.typ.asInstanceOf[TyAnyInt]
      Sum(terms.map(e => ReshapeData(e, typ)()): _*)().tchk().lower()
    }
  }
}

/** The sum of several values <i>without overflow</i>.
  *
  * The type of this expression will be chosen so as to guarantee that the sum
  * can be computed without overflow.
  *
  * @param terms
  *   the values to add up.
  */
case class SafeSum(terms: Expr*)(typ: Type = Missing)
    extends SyntaxSugar(terms: _*)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    SafeSum(newChildren: _*)(typ)
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val terms = this.terms.map(e => e.tchk.expectAnyInt())
    this.rebuild(
      TSum(terms.map(e => e.typ.asInstanceOf[TyAnyInt]): _*),
      terms
    )
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val terms = this.terms.map(e => e.lower())
    if (terms.isEmpty) {
      IntCst(0)(this.typ)
    } else {
      val typ = this.typ.asInstanceOf[TyAnyInt]
      Sum(terms.map(e => ReshapeData(e, typ)()): _*)().tchk().lower()
    }
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newFactors = factors.zipWithIndex.map({ case (e, i) =>
      val newE = e.tchk(context)
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
      ReshapeData.narrowestCommonAncestor(newFactors.map(e => e.typ)).get
    }
    this.rebuild(typ, newFactors)
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val factors = this.factors.map(e => e.lower())
    if (factors.isEmpty) {
      IntCst(1)(this.typ)
    } else {
      val typ = this.typ.asInstanceOf[TyAnyInt]
      Prod(factors.map(e => ReshapeData(e, typ)()): _*)().tchk().lower()
    }
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val factors = this.factors.map(e => e.tchk.expectAnyInt())
    this.rebuild(
      TProd(factors.map(e => e.typ.asInstanceOf[TyAnyInt]): _*),
      factors
    )
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val factors = this.factors.map(e => e.lower())
    if (factors.isEmpty) {
      IntCst(1)(this.typ)
    } else {
      this.typ.asInstanceOf[TyAnyInt] match {
        case TyUInt(0) =>
          // Need this special case because you can't normally resize to a U0,
          // but we know the product will be zero.
          IntCst(0)(TyUInt(0))
        case typ =>
          Prod(factors.map(e => ReshapeData(e, typ)()): _*)().tchk().lower()
      }
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newLhs = e1.tchk
    val t1 = newLhs.typ match {
      case t: TyAnyInt => t
      case t =>
        throw new TypeError(
          s"Left-hand side of $className has type $t."
            + " Expected an integer"
        )
    }
    val newRhs = e2.tchk
    val t2 = newRhs.typ match {
      case t: TyAnyInt => t
      case t =>
        throw new TypeError(
          s"Right-hand side of $className has type $t."
            + " Expected an integer."
        )
    }
    val typ = ReshapeData.narrowestCommonAncestor(t1, t2).get
    this.rebuild(typ, Seq(newLhs, newRhs))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val typ = this.typ.asInstanceOf[TyAnyInt]
    val e1 = this.e1.lower()
    val e2 = this.e2.lower()
    Div(ReshapeData(e1, typ)(), ReshapeData(e2, typ)())().tchk().lower()
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

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newLhs = e1.tchk
    val t1 = newLhs.typ match {
      case t: TyAnyInt => t
      case t =>
        throw new TypeError(
          s"Left-hand side of $className has type $t."
            + " Expected an integer"
        )
    }
    val newRhs = e2.tchk
    val t2 = newRhs.typ match {
      case t: TyAnyInt => t
      case t =>
        throw new TypeError(
          s"Right-hand side of $className has type $t."
            + " Expected an integer."
        )
    }
    val typ = ReshapeData.narrowestCommonAncestor(t1, t2).get
    this.rebuild(typ, Seq(newLhs, newRhs))
  }

  override def lowerSyntaxSugar(): Expr = {
    requireType()
    val typ = this.typ.asInstanceOf[TyAnyInt]
    val e1 = this.e1.lower()
    val e2 = this.e2.lower()
    Mod(ReshapeData(e1, typ)(), ReshapeData(e2, typ)())().tchk().lower()
  }
}

// Option<T>
// ---------------------------------------------------------------------------------------------------------------------

case class NNone(innerTyp: Type)
    extends SyntaxSugar()(TyTuple(innerTyp, TyBool)) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(typ == Missing || typ == TyTuple(innerTyp, TyBool))
    require(newChildren.isEmpty)
    this
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    this
  }

  override def lowerSyntaxSugar(): Expr = {
    Tuple(Default(innerTyp), False)().tchk().lower()
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: NNone => this.innerTyp == that.innerTyp
      case _           => false
    }
  }
  override def hashCode(): Int = {
    this.innerTyp.hashCode
  }
}

case class SSome(e: Expr /* T */ )(typ: Type) /* Option<T> */
    extends SyntaxSugar(e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => SSome(e)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newE = e.tchk(context)
    rebuild(TyTuple(newE.typ, TyBool), Seq(newE))
  }

  override def lowerSyntaxSugar(): Expr = {
    val e = this.e.lower()
    Tuple(e, True)().tchk().lower()
  }
}
case object SSome {
  def apply(e: Expr)(typ: Type = Missing): SSome = {
    val newTyp = if (typ == Missing && e.typ != Missing) {
      TyOption(e.typ)
    } else {
      typ
    }
    new SSome(e)(newTyp)
  }
}

case class OptionAccess(
    e: Expr /* Option<T> */,
    s: Function /* T -> V */,
    n: Function /* () -> V */
)(typ: Type = Missing) /* V */
    extends SyntaxSugar(e, s, n)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e, s: Function, n: Function) =>
        OptionAccess(e, s, n)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newE = e.tchk(context)
    val innerTyp = newE.typ match {
      case TyTuple(t, TyBool) => t
      case t => throw new TypeError(s"Target of OptionAccess has type $t.")
    }
    val newS = Function(s.param.rebuild(innerTyp), s.body)().tchk(context)
    val sOut = newS.typ match {
      case TyArrow(_, t2) => t2
      case _ =>
        throw new TypeError(s"`Some` branch of OptionAccess is not a function.")
    }
    val newN = Function(n.param.rebuild(TyTuple()), n.body)().tchk(context)
    val nOut = newN.typ match {
      case TyArrow(_, t2) => t2
      case _ =>
        throw new TypeError(s"`None` branch of OptionAccess is not a function.")
    }
    if (sOut ~= nOut) {
      rebuild(sOut, Seq(newE, newS, newN))
    } else {
      throw new TypeError(
        s"The `Some` branch of OptionAccess produces type $sOut but the `None` branch produces type $nOut."
      )
    }
  }

  override def lowerSyntaxSugar(): Expr = {
    val e = this.e.lower()
    val s = this.s.lower()
    val n = this.n.lower()
    if (this.typ != Missing) {
      val innerTyp = e.typ match {
        case TyTuple(t, TyBool) => t
        case t =>
          throw new IllegalArgumentException(
            s"Target of OptionAccess has type $t."
          )
      }
      assert(e.typ.isInstanceOf[TyTuple])
      Mux(
        TupleAccess(e, 1)(TyBool),
        s.body.subPreserveType(s.param -> TupleAccess(e, 0)(innerTyp)),
        n.body.subPreserveType(n.param -> Tuple()(TyTuple()))
      )(this.typ)
    } else {
      Mux(
        e.__1,
        s.body.subPreserveType(s.param -> e.__0),
        n.body.subPreserveType(n.param -> Tuple()())
      )()
    }
  }
}

case class OptionUnwrapUnsafe(e: Expr)(typ: Type = Missing)
    extends SyntaxSugar(e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => OptionUnwrapUnsafe(e)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newE = e.tchk(context)
    val t = newE.typ match {
      case TyTuple(t, TyBool) => t
      case t =>
        throw new TypeError(s"Target of OptionUnwrapUnsafe has type $t.")
    }
    rebuild(t, Seq(newE))
  }

  override def lowerSyntaxSugar(): Expr = {
    val e = this.e.lower()
    TupleAccess(e, 0)().tchk().lower()
  }
}

case class IsNone(e: Expr)(typ: Type = Missing) extends SyntaxSugar(e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => IsNone(e)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newE = e.tchk(context)
    newE.typ match {
      case TyTuple(_, TyBool) => ()
      case t => throw new TypeError(s"Target of IsNone has type $t.")
    }
    this.rebuild(TyBool, Seq(newE))
  }

  override def lowerSyntaxSugar(): Expr = {
    val e = this.e.lower()
    val t = if (e.hasType) TyBool else Missing
    Not(TupleAccess(e, 1)(t))(t)
  }
}

case class IsSome(e: Expr)(typ: Type = Missing) extends SyntaxSugar(e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => IsSome(e)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(implicit context: Map[Param, Type]): Expr = {
    val newE = e.tchk(context)
    newE.typ match {
      case TyTuple(_, TyBool) => ()
      case t => throw new TypeError(s"Target of IsSome has type $t.")
    }
    this.rebuild(TyBool, Seq(newE))
  }

  override def lowerSyntaxSugar(): Expr = {
    val e = this.e.lower()
    val t = if (e.hasType) TyBool else Missing
    TupleAccess(e, 1)(t)
  }
}
