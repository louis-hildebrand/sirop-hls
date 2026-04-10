package mhir.sugar
package experimental

import mhir.ir._
import mhir.optimize.StreamFuser.StreamFusion
import mhir.sugar.Streamifier.Streamify
import mhir.typecheck.{TypeCheck, TypeError}

object AsFusedStm2Stm {
  def apply(f: Function)(implicit c: Canonicalizer): (Param, StmBuild) = {
    f.lower.streamify match {
      case Function(x, body) =>
        // It is essential to fuse everything for StmMap and StmScan.
        // If f is a chain of stream producers, we want to reset them all, not
        // just the last producer.
        (x, body.fuseCompletely())
      case _ =>
        ???
    }
  }
}

// TODO: Add special cases for n = 0 and n = 1, like in StmMap?
//       Or better yet, define an operator like StmReset that works for both cases?
case class StmScanInclusive(
    input: Expr /* Stm<A; n> */,
    z: Expr /* B */,
    f: Function /* B -> A -> B */
)(typ: Type = Missing) /* Stm<B; n> */
    extends SyntaxSugar(input, z, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, z, f: Function) => StmScanInclusive(s, z, f)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newS = input.tchk(context)
    val (t1, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmScanInclusive has type $t.")
    }
    val newZ = z.tchk(context)
    val t2 = newZ.typ
    val newF = f.tchk(context)
    newF.typ match {
      case TyArrow(t3, TyArrow(t4, t5))
          if (t3 ~= t2) && (t4 ~= t1) && (t5 ~= t2) =>
        ()
      case TyArrow(t3, TyArrow(t4, TyStm(t5, IntCst(1))))
          if (t3 ~= t2) && (t4 ~= t1) && (t5 ~= t2) =>
        ()
      case t =>
        throw new TypeError(
          s"Function in StmScanInclusive has type $t. Expected ${TyArrow(t2, TyArrow(t1, t2))}."
        )
    }
    this.rebuild(TyStm(t2, n), Seq(newS, newZ, newF))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    val input = this.input.lower
    val z = this.z.lower
    // IMPORTANT: use the original input here, not the flattened version.
    // If we're scanning over a Stm[Stm[T, m], n], we will produce a stream of
    // length n, not a stream of length n * m.
    val n = this.input.typ.asInstanceOf[TyStm].n
    // IMPORTANT: use the original (possibly nested) stream here, but flatten
    // the inner dimensions.
    // If we're scanning over a Stm[Stm[Stm[T, k], m], n], then it takes k * m
    // steps per scan output, not just m.
    val inputsUntilReset = this.input.typ.asInstanceOf[TyStm].t.lower match {
      case TyStm(_, n) => n
      case _           => IntCst(1)()
    }
    val elemType = z.typ
    // TODO: Enforce the restriction that the accumulator cannot contain any streams?
    val (s, innerStm) =
      AsFusedStm2Stm(
        // The function has the form (acc) => (elem) => newAcc.
        // Take just the (elem) => newAcc part here, with acc being a free variable.
        // Later, replace that free variable with the appropriate element in the StmScan accumulator.
        f.body.asInstanceOf[Function]
      )
    assert(
      innerStm.n == IntCst(1)(),
      "the function in StmScan should return a scalar or a stream of length 1"
    )
    assert(
      innerStm.seedByVar.count({ case (_, z) => z == s }) <= 1,
      "the input stream should appear at most once in the inner StmBuild"
    )
    val (innerWithCtrs, shouldReset) = {
      val outputsUntilReset = IntCst(1)()
      val outCtr = Param("out_ctr")(U32)
      val withOutCtr = innerStm.addOutputCounter(outCtr)
      val usesInputStream = innerStm.seedByVar.exists({ case (_, z) => z == s })
      if (usesInputStream) {
        val inCtr = Param("in_ctr")(U32)
        val withCtrs = withOutCtr
          .addInputCounter(
            innerStm.seedByVar.find({ case (_, z) => z == s }).get._1,
            inCtr
          )
        // Want to reset depending on the *next* values of the in/out counters.
        val shouldReset =
          (withCtrs.nextByVar(inCtr) === inputsUntilReset
            && withCtrs.nextByVar(outCtr) === outputsUntilReset)
        (withCtrs, shouldReset)
      } else {
        val shouldReset = withOutCtr.nextByVar(outCtr) === outputsUntilReset
        (withOutCtr, shouldReset)
      }
    }
    val acc = Param("acc")(elemType)
    val nextAcc = Mux(innerWithCtrs.valid, innerWithCtrs.data, acc)()
    val outerStm = StmBuild(
      n,
      nextAcc,
      shouldReset,
      innerWithCtrs.equations.map({
        case (x, (z, ready)) if z == s =>
          // Never reset the primary input stream
          x -> (z, ready)
        case (x, (z, ready)) if x.typ.isInstanceOf[TyStm] =>
          // Repeat other input streams to give the illusion that they
          // are being reset
          x -> (StmJoin(StmRepeat(z, n)())(), ready)
        case (x, (z, next)) =>
          // Reset data accumulators
          x -> (z, Mux(shouldReset, z, next)())
      }) + (acc -> (z, nextAcc))
    )()
    assert(
      !outerStm.n.contains(s)
        && !outerStm.data.contains(s)
        && !outerStm.valid.contains(s)
        && !outerStm.nextByVar.values.toSeq.exists(nxt => nxt.contains(s)),
      "the input stream must only occur in the seed of the StmBuild"
    )
    // It is safe to do this "naive" substitution because
    //  (1) `s` definitely only occurs in the seed (where it is free), so the
    //      naive substitution behaves like the usual one
    //  (2) we want to replace f.param with the *bound* variable acc, so alpha
    //      renaming is NOT what we want here
    val scan = outerStm.rebuildAndEraseType(
      outerStm.children.map(e =>
        e.subPreserveType(Map[Expr, Expr](s -> input, f.param -> acc))
      )
    )
    val originalFreeVars =
      input.freeVars ++ z.freeVars ++ f.freeVars ++ n.freeVars
    assert(
      scan.freeVars == originalFreeVars,
      s"the set of free variables should be unchanged by StmScan (expected $originalFreeVars but got ${scan.freeVars})"
    )
    scan.tchk().lower
  }
}

case class StmScanExclusive(
    stm: Expr /* Stm<A; n> */,
    z: Expr /* B */,
    f: Function /* B -> A -> B */
)(typ: Type = Missing) /* Stm<B; n> */
    extends SyntaxSugar(stm, z, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, z, f: Function) => StmScanExclusive(s, z, f)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newS = stm.tchk(context)
    val (t1, n) = newS.typ match {
      case TyStm(t, n) => (t, n)
      case t => throw new TypeError(s"Stream in StmScanExclusive has type $t.")
    }
    val newZ = z.tchk(context)
    val t2 = newZ.typ
    val newF = f.tchk(context)
    newF.typ match {
      case TyArrow(t3, TyArrow(t4, t5))
          if (t3 ~= t2) && (t4 ~= t1) && (t5 ~= t2) =>
        ()
      case TyArrow(t3, TyArrow(t4, TyStm(t5, IntCst(1))))
          if (t3 ~= t2) && (t4 ~= t1) && (t5 ~= t2) =>
        ()
      case t =>
        throw new TypeError(
          s"Function in StmScanExclusive has type $t. Expected ${TyArrow(t2, TyArrow(t1, t2))}."
        )
    }
    this.rebuild(TyStm(t2, n), Seq(newS, newZ, newF))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    // Maybe it would be better to first take prefix of input stream, scan,
    // and then prepend
    StmShiftRight(StmScanInclusive(stm, z, f)(), z)().tchk().lower
  }
}

case class StmFold(
    stream: Expr /* Stm<A; n> */,
    z: Expr /* B */,
    f: Function /* B -> A -> B */
)(typ: Type = Missing) /* Stm<B; 1> */
    extends SyntaxSugar(stream, z, f)(typ) {
  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(s, z, f: Function) => StmFold(s, z, f)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type]
  )(implicit c: Canonicalizer): Expr = {
    val newS = stream.tchk(context)
    val t1 = newS.typ match {
      case TyStm(t, _) => t
      case t           => throw new TypeError(s"Stream in StmFold has type $t.")
    }
    val newZ = z.tchk(context)
    val t2 = newZ.typ
    val newF = f.tchk(context)
    newF.typ match {
      case TyArrow(t3, TyArrow(t4, t5))
          if (t3 ~= t2) && (t4 ~= t1) && (t5 ~= t2) =>
        ()
      case TyArrow(t3, TyArrow(t4, TyStm(t5, IntCst(1))))
          if (t3 ~= t2) && (t4 ~= t1) && (t5 ~= t2) =>
        ()
      case t =>
        throw new TypeError(
          s"Function in $className has type $t. Expected ${t2 ->: t1 ->: t2}."
        )
    }
    this.rebuild(TyStm(t2, 1), Seq(newS, newZ, newF))
  }

  override def lowerSyntaxSugar(implicit c: Canonicalizer): Expr = {
    requireType()
    StmSuffix(StmScanInclusive(stream, z, f)(), 1)().tchk().lower
  }
}
