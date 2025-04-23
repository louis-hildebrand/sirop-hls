package ir

case class Let(typ: Type, x: Param, v: Expr, in: Expr) extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(x, v, in)

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(x: Param, v, in) => Let(typ, x, v, in)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      tc: Expr => Map[Param, Type] => Expr,
      err: String => Nothing
  ): Expr = {
    val newV = tc(v)(context)
    val newIn = tc(in)(context + (x -> newV.typ))
    val newX = x.rebuild(newV.typ).asInstanceOf[Param]
    Let(newIn.typ, newX, newV, newIn)
  }

  override def lower(): Expr = {
    FunCall(Function(x, v.typ, in)(TyArrow(v.typ, in.typ)), v)(in.typ)
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: Let => (this.x, this.v, this.in) == (that.x, that.v, that.in)
      case _         => false
    }
  }
  override def hashCode(): Int = {
    (this.x, this.v, this.in).hashCode
  }
}
case object Let {
  def apply(x: Param, v: Expr, in: Expr): Let = Let(Missing, x, v, in)
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

  override def typecheck(
      context: Map[Param, Type],
      tc: Expr => Map[Param, Type] => Expr,
      err: String => Nothing
  ): Expr = {
    // Check that the requested type indeed has a default
    Default.getDefault(this.typ)
    this
  }

  override def lower(): Expr = Default.getDefault(this.typ)

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: Default => this.typ == that.typ
      case _             => false
    }
  }
  override def hashCode(): Int = {
    this.typ.hashCode
  }
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

  override def typecheck(
      context: Map[Param, Type],
      tc: Expr => Map[Param, Type] => Expr,
      err: String => Nothing
  ): Expr = {
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

case class SSome(typ: Type, e: Expr /* T */ ) /* Option<T> */
    extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(e)

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => SSome(typ, e)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      tc: Expr => Map[Param, Type] => Expr,
      err: String => Nothing
  ): Expr = {
    val newE = tc(e)(context)
    rebuild(TyTuple(newE.typ, TyBool), Seq(newE))
  }

  override def lower(): Expr = {
    Tuple(e, True)(this.typ)
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: SSome => this.e == that.e
      case _           => false
    }
  }
  override def hashCode(): Int = {
    this.e.hashCode
  }
}
case object SSome {
  def apply(e: Expr): Expr = SSome(Missing, e)
}

case class OptionAccess(
    typ: Type,
    e: Expr /* Option<T> */,
    s: Function /* T -> V */,
    n: Function /* () -> V */
) /* V */
    extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(e, s, n)

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e, s: Function, n: Function) =>
        OptionAccess(typ, e, s, n)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      tc: Expr => Map[Param, Type] => Expr,
      err: String => Nothing
  ): Expr = {
    val newE = tc(e)(context)
    val innerTyp = newE.typ match {
      case TyTuple(t, TyBool) => t
      case t                  => err(s"Target of OptionAccess has type $t.")
    }
    val newS = tc(Function(s.param, innerTyp, s.body)())(context)
    val sOut = newS.typ match {
      case TyArrow(_, t2) => t2
      case _ => err(s"`Some` branch of OptionAccess is not a function.")
    }
    val newN = tc(Function(n.param, TyTuple(), n.body)())(context)
    val nOut = newN.typ match {
      case TyArrow(_, t2) => t2
      case _ => err(s"`None` branch of OptionAccess is not a function.")
    }
    if (sOut.isCompatibleWith(nOut)) {
      rebuild(sOut, Seq(newE, newS, newN))
    } else {
      err(
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
        this.typ,
        TupleAccess(e, 1)(TyBool),
        s.body.substitute(s.param -> TupleAccess(e, 0)(innerTyp)),
        n.body.substitute(n.param -> Tuple()(TyTuple()))
      )
    } else {
      IfThenElse(
        e.__1,
        s.body.substitute(s.param -> e.__0),
        n.body.substitute(n.param -> Tuple()())
      )
    }
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: OptionAccess =>
        (this.e, this.s, this.n) == (that.e, that.s, that.n)
      case _ => false
    }
  }
  override def hashCode(): Int = {
    (this.e, this.s, this.n).hashCode
  }
}
case object OptionAccess {
  def apply(e: Expr, s: Function, n: Function): Expr = {
    OptionAccess(Missing, e, s, n)
  }
}

case class OptionUnwrapUnsafe(typ: Type, e: Expr) extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(e)

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => OptionUnwrapUnsafe(typ, e)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      tc: Expr => Map[Param, Type] => Expr,
      err: String => Nothing
  ): Expr = {
    val newE = tc(e)(context)
    val t = newE.typ match {
      case TyTuple(t, TyBool) => t
      case t => err(s"Target of OptionUnwrapUnsafe has type $t.")
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

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: OptionUnwrapUnsafe => this.e == that.e
      case _                        => false
    }
  }
  override def hashCode(): Int = {
    this.e.hashCode
  }
}
case object OptionUnwrapUnsafe {
  def apply(e: Expr): OptionUnwrapUnsafe = OptionUnwrapUnsafe(Missing, e)
}

case class IsNone(typ: Type, e: Expr) extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(e)

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => IsNone(typ, e)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      tc: Expr => Map[Param, Type] => Expr,
      err: String => Nothing
  ): Expr = {
    val newE = tc(e)(context)
    val t = newE.typ match {
      case TyTuple(t, TyBool) => t
      case t                  => err(s"Target of IsNone has type $t.")
    }
    rebuild(t, Seq(newE))
  }

  override def lower(): Expr = {
    Not(TupleAccess(e, 1)(TyBool))(TyBool)
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: IsNone => this.e == that.e
      case _            => false
    }
  }
  override def hashCode(): Int = {
    this.e.hashCode
  }
}
case object IsNone {
  def apply(e: Expr): Expr = {
    IsNone(Missing, e)
  }
}

case class IsSome(typ: Type, e: Expr) extends SyntaxSugar {
  override def children: Seq[Expr] = Seq(e)

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(e) => IsSome(typ, e)
      case _ =>
        throw new IllegalArgumentException(
          s"Wrong arguments passed to rebuild: $newChildren"
        )
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      tc: Expr => Map[Param, Type] => Expr,
      err: String => Nothing
  ): Expr = {
    val newE = tc(e)(context)
    val t = newE.typ match {
      case TyTuple(t, TyBool) => t
      case t                  => err(s"Target of IsSome has type $t.")
    }
    rebuild(t, Seq(newE))
  }

  override def lower(): Expr = {
    TupleAccess(e, 1)(TyBool)
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: IsSome => this.e == that.e
      case _            => false
    }
  }
  override def hashCode(): Int = {
    this.e.hashCode
  }
}
case object IsSome {
  def apply(e: Expr): Expr = {
    IsSome(Missing, e)
  }
}
