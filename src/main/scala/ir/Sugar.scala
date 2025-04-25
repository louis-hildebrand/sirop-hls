package ir

case class Let(x: Param, v: Expr, in: Expr)(typ: Type = Missing)
    extends SyntaxSugar(x, v, in)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(x: Param, v, in) => Let(x, v, in)(typ)
      case _                    => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newV = v.tchk(context)
    val newIn = in.tchk(context + (x -> newV.typ))
    val newX = x.rebuild(newV.typ)
    Let(newX, newV, newIn)(newIn.typ)
  }

  override def lowerSyntaxSugar(): Expr = {
    val v = this.v.lower()
    val in = this.in.lower()
    if (this.typ != Missing) {
      FunCall(Function(x, in)(TyArrow(v.typ, in.typ)), v)(in.typ)
    } else {
      FunCall(Function(x, in)(), v)()
    }
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    // Check that the requested type indeed has a default
    Default.getDefault(this.typ)
    this
  }

  override def lowerSyntaxSugar(): Expr = Default.getDefault(this.typ)
}
case object Default {
  def hasDefault(typ: Type): Boolean = {
    getDefaultOpt(typ).isDefined
  }

  private def getDefault(typ: Type): Expr = {
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
      case TyInt            => Some(IntCst(0))
      case TyBool           => Some(False)
      case TyTuple(ts @ _*) => Some(Tuple(ts.map(t => getDefault(t)): _*)(typ))
      case TyVec(t, n) => Some(VecBuild(n, TyInt ::+ (_ => getDefault(t)))(typ))
      case _           => None
    }
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    this
  }

  override def lowerSyntaxSugar(): Expr = {
    Tuple(Default(innerTyp), False)(this.typ.flat)
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

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newE = e.tchk(context)
    rebuild(TyTuple(newE.typ, TyBool), Seq(newE))
  }

  override def lowerSyntaxSugar(): Expr = {
    Tuple(e, True)(this.typ.flat)
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

  override def typecheck(context: Map[Param, Type]): Expr = {
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
    if (sOut.isCompatibleWith(nOut)) {
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
      IfThenElse(
        TupleAccess(e, 1)(TyBool),
        s.body.subPreserveType(s.param -> TupleAccess(e, 0)(innerTyp)),
        n.body.subPreserveType(n.param -> Tuple()(TyTuple()))
      )(this.typ)
    } else {
      IfThenElse(
        e.__1,
        s.body.substitute(s.param -> e.__0),
        n.body.substitute(n.param -> Tuple()())
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

  override def typecheck(context: Map[Param, Type]): Expr = {
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
    TupleAccess(e, 0)(this.typ.flat)
  }
}

case class IsNone(e: Expr)(typ: Type = Missing) extends SyntaxSugar(e)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => IsNone(e)(typ)
      case _      => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(context: Map[Param, Type]): Expr = {
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

  override def typecheck(context: Map[Param, Type]): Expr = {
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
