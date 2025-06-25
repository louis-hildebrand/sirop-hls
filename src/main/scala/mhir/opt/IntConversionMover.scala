package mhir.opt

import mhir.ir._

/** Transformations for moving integer conversion primitives ([[PadTo]],
  * [[TruncateTo]], [[ToSigned]], and [[ToUnsigned]]) around in the AST.
  */
object IntConversionMover {

  /** Move padding conversions ([[PadTo]] and [[ToSigned]]) towards the leaves
    * of the AST and move truncating conversions ([[TruncateTo]] and
    * [[ToUnsigned]]) towards the root of the AST.
    *
    * This transformation is always safe to perform, since it has the effect of
    * widening the intermediate results. It will probably increase the hardware
    * cost, but it should simplify other analyses and transformations by
    * separating conversions from arithmetic.
    *
    * @param expr
    *   the expression to transform.
    */
  def widen(expr: Expr): Expr = {
    val e = expr.tchk()
    val result = e match {
      case PadTo(arg, w) =>
        widen(arg) match {
          case PadTo(arg, _) =>
            PadTo(arg, w)()
          case TruncateTo(arg, _) =>
            val w0 = arg.typ.asInstanceOf[TyAnyInt].w
            if (w > w0) widen(PadTo(arg, w)())
            else if (w < w0) widen(TruncateTo(arg, w)())
            else arg
          case _: ToSigned => e
          case ToUnsigned(arg) =>
            val finalWidth = e.typ.asInstanceOf[TyAnyInt].w
            ToUnsigned(widen(PadTo(arg, finalWidth + 1)()))()
          case e @ (_: Sum | _: Prod | _: Div | _: Mod) =>
            e.map(e => widen(PadTo(e, w)()))
          case Mux(c, t, f) =>
            Mux(c, widen(PadTo(t, w)()), widen(PadTo(f, w)()))()
          case e => PadTo(e, w)()
        }
      case TruncateTo(arg, w) =>
        widen(arg) match {
          case PadTo(arg, _) =>
            val w0 = arg.typ.asInstanceOf[TyAnyInt].w
            if (w > w0) PadTo(arg, w)()
            else if (w < w0) TruncateTo(arg, w)()
            else arg
          case TruncateTo(arg, _) => TruncateTo(arg, w)()
          case _: ToSigned        => e
          case ToUnsigned(arg) =>
            val finalWidth = e.typ.asInstanceOf[TyAnyInt].w
            ToUnsigned(widen(TruncateTo(arg, finalWidth + 1)()))()
          case e => TruncateTo(e, w)()
        }
      case ToSigned(arg) =>
        widen(arg) match {
          case PadTo(arg, _) =>
            val finalWidth = e.typ.asInstanceOf[TyAnyInt].w
            PadTo(widen(ToSigned(arg)()), finalWidth)()
          case TruncateTo(arg, _) =>
            val finalWidth = e.typ.asInstanceOf[TyAnyInt].w
            TruncateTo(widen(ToSigned(arg)()), finalWidth)()
          case ToUnsigned(arg) => arg
          case e @ (_: Sum | _: Prod | _: Div | _: Mod) =>
            e.map(e => widen(ToSigned(e)()))
          case Mux(c, t, f) =>
            Mux(c, widen(ToSigned(t)()), widen(ToSigned(f)()))()
          case e =>
            assert(
              !e.isInstanceOf[ToSigned],
              "found ToSigned(ToSigned(x)) (isn't that ill-typed?)"
            )
            ToSigned(e)()
        }
      case ToUnsigned(arg) =>
        widen(arg) match {
          case _: PadTo          => e
          case trunc: TruncateTo => ToUnsigned(trunc)()
          case ToSigned(arg)     => arg
          case e =>
            assert(
              !e.isInstanceOf[ToUnsigned],
              "found ToUnsigned(ToUnsigned(x)) (isn't that ill-typed?)"
            )
            ToUnsigned(e)()
        }
      case e @ (_: Sum | _: Prod | _: Div | _: Mod) =>
        val newChildren = e.children.map(e => widen(e))
        if (newChildren.exists(e => e.isInstanceOf[ToUnsigned])) {
          ToUnsigned(
            widen(e.rebuildAndEraseType(newChildren.map(e => ToSigned(e)())))
          )()
        } else if (newChildren.exists(e => e.isInstanceOf[TruncateTo])) {
          val trunc = newChildren
            .find(e => e.isInstanceOf[TruncateTo])
            .map(e => e.asInstanceOf[TruncateTo])
            .get
          val wide = trunc.e.typ.asInstanceOf[TyAnyInt].w
          val narrow = trunc.w
          widen(
            TruncateTo(
              e.rebuildAndEraseType(newChildren.map(e => PadTo(e, wide)())),
              narrow
            )()
          )
        } else {
          e.rebuildAndEraseType(newChildren)
        }
      case Mux(c, t, f) =>
        val newC = widen(c)
        val newBranches = Seq(t, f).map(e => widen(e))
        if (newBranches.exists(e => e.isInstanceOf[ToUnsigned])) {
          ToUnsigned(
            widen(
              e.rebuildAndEraseType(newC +: newBranches.map(e => ToSigned(e)()))
            )
          )()
        } else if (newBranches.exists(e => e.isInstanceOf[TruncateTo])) {
          val trunc = newBranches
            .find(e => e.isInstanceOf[TruncateTo])
            .map(e => e.asInstanceOf[TruncateTo])
            .get
          val wide = trunc.e.typ.asInstanceOf[TyAnyInt].w
          val narrow = trunc.w
          widen(
            TruncateTo(
              e.rebuildAndEraseType(
                newC +: newBranches.map(e => PadTo(e, wide)())
              ),
              narrow
            )()
          )
        } else {
          e.rebuildAndEraseType(newC +: newBranches)
        }
      case i: IntCst =>
        // Don't erase type annotation on IntCst
        i
      // TODO: emit warning if there's syntax sugar?
      case e =>
        e.map(e => widen(e))
    }
    val typedResult = result.tchk()
    assert(
      typedResult.typ ~= e.typ,
      s"moving integer conversions around should preserve the type (expected ${e.typ}, got ${typedResult.typ})"
    )
    typedResult
  }
}
