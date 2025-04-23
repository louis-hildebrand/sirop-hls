package ir

case class Let(x: Param, v: Expr, in: Expr)(val typ: Type = Missing)
    extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(x, v, in)

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(x: Param, v, in) => Let(x, v, in)(typ)
      case _                    => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(context: Map[Param, Type]): Expr = {
    val newV = v.tchk(context)
    val newIn = in.tchk(context + (x -> newV.typ))
    val newX = x.rebuild(newV.typ).asInstanceOf[Param]
    Let(newX, newV, newIn)(newIn.typ)
  }

  override def lower(): Expr = {
    FunCall(Function(x, v.typ, in)(TyArrow(v.typ, in.typ)), v)(in.typ)
  }
}

// Default value for a given datatype (zero for int, false for bool, tuple of
// defaults for a tuple, etc.).
// Note that this should NOT be used for functions or streams.
case class Default(typ: Type) extends SyntaxSugar {
  override def children: Seq[Expr] = Seq()

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(newChildren.isEmpty)
    this
  }

  override def typecheck(context: Map[Param, Type]): Expr = {
    // Check that the requested type indeed has a default
    Default.getDefault(this.typ)
    this
  }

  override def lower(): Expr = Default.getDefault(this.typ)
}
case object Default {
  private def getDefault(typ: Type): Expr = {
    typ match {
      case TyInt            => IntCst(0)
      case TyBool           => False
      case TyTuple(ts @ _*) => Tuple(ts.map(t => getDefault(t)): _*)(typ)
      case TyVec(t, n)      => VecBuild(n, (_: Expr) => getDefault(t))(typ)
      case t =>
        throw new IllegalArgumentException(
          s"Cannot construct default value for type $t."
        )
    }
  }
}

// Option<T>
// ---------------------------------------------------------------------------------------------------------------------

case class NNone(innerTyp: Type) extends SyntaxSugar {
  val typ: Type = TyTuple(innerTyp, TyBool)

  override def children: Seq[Expr] = Seq()

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    require(typ == Missing || typ == TyTuple(innerTyp, TyBool))
    require(newChildren.isEmpty)
    this
  }

  override def typecheck(context: Map[Param, Type]): Expr = {
    this
  }

  override def lower(): Expr = {
    Tuple(Default(innerTyp), False)(this.typ)
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

case class SSome(e: Expr /* T */ )(val typ: Type = Missing) /* Option<T> */
    extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(e)

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

  override def lower(): Expr = {
    Tuple(e, True)(this.typ)
  }
}

case class OptionAccess(
    e: Expr /* Option<T> */,
    s: Function /* T -> V */,
    n: Function /* () -> V */
)(val typ: Type = Missing) /* V */
    extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(e, s, n)

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
    val newS = Function(s.param, innerTyp, s.body)().tchk(context)
    val sOut = newS.typ match {
      case TyArrow(_, t2) => t2
      case _ =>
        throw new TypeError(s"`Some` branch of OptionAccess is not a function.")
    }
    val newN = Function(n.param, TyTuple(), n.body)().tchk(context)
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

  override def lower(): Expr = {
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
        s.body.substitute(s.param -> TupleAccess(e, 0)(innerTyp)),
        n.body.substitute(n.param -> Tuple()(TyTuple()))
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

case class OptionUnwrapUnsafe(e: Expr)(val typ: Type = Missing)
    extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(e)

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

  override def lower(): Expr = {
    if (this.typ != Missing) {
      val innerTyp = e.typ match {
        case TyTuple(t, TyBool) => t
        case t =>
          throw new IllegalArgumentException(
            s"Target of OptionAccess has type $t."
          )
      }
      TupleAccess(e, 0)(innerTyp)
    } else {
      e.__0
    }
  }
}

case class IsNone(e: Expr)(val typ: Type = Missing) extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(e)

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

  override def lower(): Expr = {
    Not(TupleAccess(e, 1)(TyBool))(TyBool)
  }
}

case class IsSome(e: Expr)(val typ: Type = Missing) extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(e)

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

  override def lower(): Expr = {
    TupleAccess(e, 1)(TyBool)
  }
}
