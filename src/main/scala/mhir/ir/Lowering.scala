package mhir.ir

import com.typesafe.scalalogging.Logger
import mhir.ir.typecheck.{TypeCheck, TypeError}
import mhir.sugar.StmMap

/** The lowering transformation, which removes syntax sugar from an expression
  * or a type.
  *
  * To lower an expression, use the extension method [[ExprLowering.lower]]. To
  * lower a type, use the extension method [[TypeLowering.lower]]. In either
  * case, the implicit class ([[ExprLowering]] and [[TypeLowering]],
  * respectively) must be in scope.
  *
  * @example
  *
  * {{{
  *   import mhir.ir.Lowering.ExprLowering
  *   exprWithSyntaxSugar.lower()
  * }}}
  * {{{
  *   import mhir.ir.Lowering.TypeLowering
  *   typeWithSyntaxSugar.lower()
  * }}}
  */
object Lowering {

  /** The lowering transformation for expressions.
    */
  implicit class ExprLowering(expr: Expr) {

    /** Remove all syntax sugar from this expression and its children. This is
      * guaranteed to preserve type annotations.
      */
    def lower(): Expr = {
      val desugared = expr match {
        case s: SyntaxSugar =>
          s.lowerSyntaxSugar()
        case vb: VecBuild =>
          vb.requireType()
          val n = vb.len.lower()
          val f = vb.f.lower().asInstanceOf[Function]
          vb.typ.asInstanceOf[TyVec].t.lower match {
            case _: TyStm =>
              val Function(i, s) = f
              new mhir.ir.StreamReplicator.StreamReplication(s)
                .replicate(n, i, Set())
            case t if t.isData => VecBuild(n, f)().tchk()
            case t =>
              throw new IllegalArgumentException(
                s"Cannot lower VecBuild containing elements of type $t."
              )
          }
        case va: VecAccess =>
          va.requireType()
          val v = va.vec.lower()
          val i = va.i.lower()
          v.typ match {
            case _: TyVec => VecAccess(v, i)().tchk()
            case TyStm(tv: TyVec, _) =>
              StmMap(v, tv ::+ (v => VecAccess(v, i)()))().tchk().lower()
            case t =>
              throw new TypeError(
                s"Cannot lower ${VecAccess.getClass.getSimpleName} whose first argument has type $t."
              )
          }
        case e @ (_: IntCst | _: Param | _: StmLiteral | _: VecLiteral) =>
          // These expressions may carry type information that cannot be derived
          // from the syntax alone, so be careful not to discard it.
          e.rebuild(e.typ.lower, e.children.map(e => e.lower()))
        case e =>
          // Re-run the type checker to ensure no type errors crept in during
          // lowering
          val newE = e.rebuildAndEraseType(e.children.map(e => e.lower()))
          if (e.typ != Missing) newE.tchk() else newE
      }
      // This is required because lowering may be syntax-directed (i.e., an
      // expression may need to be typed before it can be lowered) and it is no
      // good if you type check an expression but then the type is removed while
      // lowering its children.
      assert(
        !expr.hasType || (desugared.typ ~= expr.typ.lower),
        s"lowering must yield an expression whose type is the lowered version of the original type (after attempting to lower ${expr.getClass.getSimpleName}, expected ${expr.typ.lower} but found ${desugared.typ})"
      )
      assert(
        !desugared.contains(classOf[SyntaxSugar]),
        s"lowering must yield an expression without any syntax sugar (after attempting to lower ${expr.getClass.getSimpleName}, which yielded $desugared)"
      )
      desugared
    }
  }

  /** The lowering transformation for types.
    */
  implicit class TypeLowering(typ: Type) {
    def lower: Type = {
      typ match {
        case Missing | TyBool => typ
        case TySInt(w)        => TySInt(w)
        case TyUInt(w)        => TyUInt(w)
        case TyArrow(t1, t2)  => TyArrow(t1.lower, t2.lower)
        case TyTuple(ts @ _*) => TyTuple(ts.map(t => t.lower): _*)
        case TyVec(t, n) =>
          val newN = n.lower()
          t.lower match {
            case TyStm(t, m) => TyStm(TyVec(t, newN), m)
            case t           => TyVec(t, newN)
          }
        case TyStm(t, n) =>
          t.lower match {
            case TyStm(t, m) => TyStm(t, SafeProd(n, m)().tchk().lower())
            case t           => TyStm(t, n)
          }
      }
    }
  }
}
