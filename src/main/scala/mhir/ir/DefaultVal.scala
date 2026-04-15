package mhir.ir

/** Produces a "default" value for each data type.
  */
object DefaultVal {

  /** Produces a "default" value for the given type.
    *
    * The input must be a "data type" (see [[mhir.ir.TyData]]).
    */
  def apply(typ: Type): Expr = {
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
      case _: TyAnyInt => Some(IntCst(0)(typ))
      case typ: TyFix  => Some(FixCst(0)(typ))
      case TyBool      => Some(False)
      case TyTuple(ts @ _*) =>
        val childrenOptions = ts.map(getDefaultOpt)
        val children = if (childrenOptions.exists(_.isEmpty)) {
          None
        } else {
          Some(childrenOptions.map(_.head))
        }
        children.map(xs => Tuple(xs: _*)(typ))
      case TyVec(t, n) =>
        getDefaultOpt(t).map(v => VecBuild(n, n.typ ::+ (_ => v))(typ))
      case _ => None
    }
  }
}
