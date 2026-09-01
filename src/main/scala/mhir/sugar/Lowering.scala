package mhir.sugar

import mhir.ir._
import mhir.typecheck.{TypeCheck, TypeError}

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
  *   exprWithSyntaxSugar.lower
  * }}}
  * {{{
  *   import mhir.ir.Lowering.TypeLowering
  *   typeWithSyntaxSugar.lower
  * }}}
  */
trait Lowering {

  /** The lowering transformation for expressions.
    */
  implicit class ExprLowering(expr: Expr) {

    /** Remove all syntax sugar from this expression and its children. This is
      * guaranteed to preserve type annotations.
      */
    def lower(implicit c: Canonicalizer): Expr = {
      val desugared = expr match {
        case s: ResolvedSyntaxSugar =>
          s.lowerSyntaxSugar(c)
        case s: SyntaxSugar =>
          throw new IllegalArgumentException(
            s"syntax sugar (${s.className}) should have been type checked before lowering"
          )
        case mux: Mux =>
          mux.requireType()
          val cond = mux.c.lower
          val t = mux.t.lower
          val f = mux.f.lower
          t.typ match {
            case TyStm(elemTyp, n) =>
              val sT = Param("s_t")(TyStm(elemTyp, -1))
              val sF = Param("s_f")(TyStm(elemTyp, -1))
              StmBuild(
                n,
                C(1)(),
                Undefined(elemTyp),
                Mux(cond, StmData(sT)(), StmData(sF)())(),
                True,
                Map(),
                Map[Param, (Expr, Expr, Expr)](
                  sT -> (t, True, C(0)()),
                  sF -> (f, True, C(0)())
                )
              )().annotate(NoInputsAfterLastOut).annotateWithName("Mux").tchk()
            case _ =>
              Mux(cond, t, f)().tchk()
          }
        case vb: VecBuild =>
          vb.requireType()
          val n = vb.len.lower
          val f = vb.f.lower.asInstanceOf[Function]
          vb.typ.asInstanceOf[TyVec].t.lower match {
            case _: TyStm =>
              val Function(i, s) = f
              new StreamReplicator.StreamReplication(s)
                .replicate(n, i, Set())
            case t if t.isData => VecBuild(n, f)().tchk()
            case t =>
              throw new IllegalArgumentException(
                s"Cannot lower VecBuild containing elements of type $t."
              )
          }
        case va: VecAccess =>
          va.requireType()
          val v = va.vec.lower
          val i = va.i.lower
          v.typ match {
            case _: TyVec => VecAccess(v, i)().tchk()
            case TyStm(tv: TyVec, _) =>
              StmMap(v, tv ::+ (v => VecAccess(v, i)()))().tchk().lower
            case t =>
              throw new TypeError(
                s"Cannot lower ${VecAccess.getClass.getSimpleName} whose first argument has type $t."
              )
          }
        case v: VecLiteral =>
          val loweredElems = v.elems.map(_.lower)
          val TyVec(elemTyp, IntCst(nLong)) = v.typ
          val n = nLong.toInt
          elemTyp.lower match {
            case TyStm(elemTyp, IntCst(mLong)) =>
              val m = mLong.toInt
              // Move streams to the outside
              val (physicalGrid, logicalGrid) =
                if (loweredElems.forall(_.isInstanceOf[StmLiteral])) {
                  (
                    loweredElems.map(_.asInstanceOf[StmLiteral].physical),
                    loweredElems.map(_.asInstanceOf[StmLiteral].logical)
                  )
                } else {
                  // TODO: Use proper error type
                  throw new IllegalArgumentException(
                    "If a VecLiteral contains elements which are streams, they must all be StmLiterals."
                  )
                }
              assert(logicalGrid.length == n)
              assert(logicalGrid.forall(row => row.length == m))
              if (physicalGrid.map(_.length).toSet.size > 1) {
                // TODO: Use proper error type
                throw new IllegalArgumentException(
                  "physical prefixes in vector of stream literals have different lengths"
                )
              }
              val physLen = if (physicalGrid.isEmpty) {
                0
              } else {
                physicalGrid.head.length
              }
              StmLiteral(
                (0 until physLen).map(j =>
                  VecLiteral(
                    (0 until n).map(i => physicalGrid(i)(j)): _*
                  )(TyVec(elemTyp, n))
                ),
                (0 until m).map(j =>
                  VecLiteral(
                    (0 until n).map(i => logicalGrid(i)(j)): _*
                  )(TyVec(elemTyp, n))
                )
              )(TyStm(TyVec(elemTyp, n), m))
            case _ =>
              v.rebuild(v.typ, loweredElems)
          }
        case s @ StmLiteral(physical, logical) =>
          val loweredPhysical = physical.map(_.lower)
          val loweredLogical = logical.map(_.lower)
          val TyStm(elemTyp, IntCst(nLong)) = s.typ
          elemTyp.lower match {
            case TyStm(elemTyp, IntCst(mLong)) =>
              // Flatten nested streams
              val logicalRows = nLong.toInt
              val logicalCols = mLong.toInt
              val physicalGrid =
                if (loweredPhysical.forall(_.isInstanceOf[StmLiteral])) {
                  val innerStreams =
                    loweredPhysical.map(_.asInstanceOf[StmLiteral])
                  if (innerStreams.forall(_.physical.isEmpty)) {
                    innerStreams.map(_.logical)
                  } else {
                    // TODO: Use proper error type
                    throw new IllegalArgumentException(
                      "If a StmLiteral contains elements which are streams, their physical prefixes must all be empty."
                    )
                  }
                } else {
                  // TODO: Use proper error type
                  throw new IllegalArgumentException(
                    "If a StmLiteral contains elements which are streams, they must all be StmLiterals."
                  )
                }
              val logicalGrid =
                if (loweredLogical.forall(_.isInstanceOf[StmLiteral])) {
                  val innerStreams =
                    loweredLogical.map(_.asInstanceOf[StmLiteral])
                  if (innerStreams.forall(_.physical.isEmpty)) {
                    innerStreams.map(_.logical)
                  } else {
                    // TODO: Use proper error type
                    throw new IllegalArgumentException(
                      "If a StmLiteral contains elements which are streams, their physical prefixes must all be empty."
                    )
                  }
                } else {
                  // TODO: Use proper error type
                  throw new IllegalArgumentException(
                    "If a StmLiteral contains elements which are streams, they must all be StmLiterals."
                  )
                }
              assert(logicalGrid.length == logicalRows)
              assert(
                logicalGrid.forall(row => row.length == logicalCols),
                "the grid of logical elements should be rectangular, not jagged"
              )
              val (physicalRows, physicalCols) = if (physicalGrid.isEmpty) {
                (0, 0)
              } else {
                (physicalGrid.length, physicalGrid.head.length)
              }
              assert(
                physicalGrid.forall(row => row.length == physicalCols),
                "the grid of physical elements should be rectangular, not jagged"
              )
              StmLiteral(
                (0 until physicalRows * physicalCols).map({ t =>
                  val i = t / physicalCols
                  val j = t % physicalRows
                  physicalGrid(i)(j)
                }),
                (0 until logicalRows * logicalCols).map({ t =>
                  val i = t / logicalCols
                  val j = t % logicalCols
                  logicalGrid(i)(j)
                })
              )(TyStm(elemTyp, logicalRows * logicalCols))
            case _ =>
              s.rebuild(s.typ, loweredPhysical ++ loweredLogical)
          }
        case e @ (_: IntCst | _: Param | _: Undefined) =>
          // These expressions may carry type information that cannot be derived
          // from the syntax alone, so be careful not to discard it.
          e.rebuild(e.typ.lower, e.children.map(e => e.lower))
        case e =>
          // Re-run the type checker to ensure no type errors crept in during
          // lowering
          val newE = e.rebuildAndEraseType(e.children.map(e => e.lower))
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
        !desugared.hasSyntaxSugar,
        s"lowering must yield an expression without any syntax sugar (after attempting to lower ${expr.getClass.getSimpleName}, which yielded $desugared)"
      )
      desugared
    }
  }

  implicit class ParamLowering(x: Param) {
    def lowerParam(implicit c: Canonicalizer): Param = {
      // A Param will always lower to a Param
      this.x.lower.asInstanceOf[Param]
    }
  }

  /** The lowering transformation for types.
    */
  implicit class TypeLowering(typ: Type) {
    def lower(implicit c: Canonicalizer): Type = {
      typ match {
        case Missing | TyBool | _: TyAnyInt | _: TyFix => typ
        case TyArrow(t1, t2)  => TyArrow(t1.lower, t2.lower)
        case TyTuple(ts @ _*) => TyTuple(ts.map(t => t.lower): _*)
        case TyVec(t, n) =>
          val newN = n.lower
          t.lower match {
            case TyStm(t, m) => TyStm(TyVec(t, newN), m)
            case t           => TyVec(t, newN)
          }
        case TyStm(t, n) =>
          t.lower match {
            case TyStm(t, m) => TyStm(t, SafeProd(n, m)().tchk().lower)
            case t           => TyStm(t, n)
          }
      }
    }
  }
}
