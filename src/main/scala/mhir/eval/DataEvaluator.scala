package mhir.eval

import mhir.canonicalize._
import mhir.ir._

import scala.annotation.tailrec

private[eval] object DataEvaluator {

  def evalBigStep(stmData: Map[Param, Option[Expr]])(e: Expr): Expr = {
    val result: Expr = e match {
      case u: Undefined => u

      case x: Param =>
        throw new IllegalArgumentException(
          s"Free variable ${x.name}. Terms must be closed."
        )
      case f: Function => f
      case FunCall(f, arg) =>
        val Function(x, body) = evalBigStep(stmData)(f)
        val argVal = evalBigStep(stmData)(arg)
        evalBigStep(stmData)(body.subPreserveType(x -> argVal))

      case n: IntCst => n
      case sum @ Sum(terms @ _*) =>
        val termValues = terms.map(evalBigStep(stmData))
        if (termValues.exists(_.isInstanceOf[Undefined])) {
          Undefined(sum.typ)
        } else {
          val result = termValues.map(_.asInstanceOf[IntCst].i).sum
          IntCstOrUndefined(result)(sum.typ.asInstanceOf[TyAnyInt])
        }
      case sum @ WrappingSum(terms @ _*) =>
        val termValues = terms.map(evalBigStep(stmData))
        if (termValues.exists(_.isInstanceOf[Undefined])) {
          Undefined(sum.typ)
        } else {
          val result = termValues.map(_.asInstanceOf[IntCst].i).sum
          IntCst(truncate(result, sum.typ.asInstanceOf[TyAnyInt]))(sum.typ)
        }
      case diff @ WrappingDiff(e1, e2) =>
        val v1 = evalBigStep(stmData)(e1)
        val v2 = evalBigStep(stmData)(e2)
        (v1, v2) match {
          case (IntCst(n1), IntCst(n2)) =>
            val result = n1 - n2
            IntCst(truncate(result, diff.typ.asInstanceOf[TyAnyInt]))(diff.typ)
          case (_: Undefined, _) => Undefined(diff.typ)
          case (_, _: Undefined) => Undefined(diff.typ)
          case (v1, v2)          => badArgs(diff.className)(v1, v2)
        }
      case prod @ Prod(factors @ _*) =>
        val factorValues = factors.map(evalBigStep(stmData))
        if (factorValues.exists(_.isInstanceOf[Undefined])) {
          Undefined(prod.typ)
        } else {
          val result = factorValues.map(_.asInstanceOf[IntCst].i).product
          IntCstOrUndefined(result)(prod.typ.asInstanceOf[TyAnyInt])
        }
      case prod @ WrappingProd(factors @ _*) =>
        val factorValues = factors.map(evalBigStep(stmData))
        if (factorValues.exists(_.isInstanceOf[Undefined])) {
          Undefined(prod.typ)
        } else {
          val result = factorValues.map(_.asInstanceOf[IntCst].i).product
          IntCst(truncate(result, prod.typ.asInstanceOf[TyAnyInt]))(prod.typ)
        }
      case div @ Div(e1, e2) =>
        val numer = evalBigStep(stmData)(e1)
        val denom = evalBigStep(stmData)(e2)
        (numer, denom) match {
          case (IntCst(_), IntCst(0))   => Undefined(div.typ)
          case (IntCst(n1), IntCst(n2)) => IntCst(n1 / n2)(e.typ)
          case (_: Undefined, _)        => Undefined(div.typ)
          case (_, _: Undefined)        => Undefined(div.typ)
          case (v1, v2)                 => badArgs(div.className)(v1, v2)
        }
      case mod @ Mod(e1, e2) =>
        val numer = evalBigStep(stmData)(e1)
        val denom = evalBigStep(stmData)(e2)
        (numer, denom) match {
          case (_, IntCst(0))           => Undefined(mod.typ)
          case (IntCst(n1), IntCst(n2)) => IntCst(n1 % n2)(e.typ)
          case (_: Undefined, _)        => Undefined(mod.typ)
          case (_, _: Undefined)        => Undefined(mod.typ)
          case (v1, v2)                 => badArgs(mod.className)(v1, v2)
        }
      case pad @ PadTo(e, _) =>
        evalBigStep(stmData)(e) match {
          case k: IntCst    => k.rebuild(pad.typ)
          case _: Undefined => Undefined(pad.typ)
          case v            => badArgs(pad.className)(v)
        }
      case trunc @ TruncateTo(e, _) =>
        evalBigStep(stmData)(e) match {
          case IntCst(k) =>
            IntCstOrUndefined(k)(trunc.typ.asInstanceOf[TyAnyInt])
          case _: Undefined => Undefined(trunc.typ)
          case v            => badArgs(trunc.className)(v)
        }
      case sign @ ToSigned(e) =>
        evalBigStep(stmData)(e) match {
          case k: IntCst    => k.rebuild(sign.typ)
          case _: Undefined => Undefined(sign.typ)
          case v            => badArgs(sign.className)(v)
        }
      case usgn @ ToUnsigned(e) =>
        evalBigStep(stmData)(e) match {
          case IntCst(k) =>
            IntCstOrUndefined(k)(usgn.typ.asInstanceOf[TyAnyInt])
          case _: Undefined => Undefined(usgn.typ)
          case v            => badArgs(usgn.className)(v)
        }
      case bits @ Bits(e) =>
        VecLiteral(toBits(evalBigStep(stmData)(e)): _*)(bits.typ)
      case ia @ InterpretAs(e, _) =>
        evalBigStep(stmData)(e) match {
          case VecLiteral(elems @ _*) => fromBits(elems, ia.typ)
          case _: Undefined           => Undefined(ia.typ)
          case v                      => badArgs(ia.className)(v)
        }
      case ls @ LShift(e1, e2) =>
        (evalBigStep(stmData)(e1), evalBigStep(stmData)(e2)) match {
          case (IntCst(k1), IntCst(k2)) =>
            val result = truncate(k1 << k2, ls.typ.asInstanceOf[TyAnyInt])
            IntCst(result)(ls.typ)
          case (_: Undefined, _) => Undefined(ls.typ)
          case (_, _: Undefined) => Undefined(ls.typ)
          case (v1, v2)          => badArgs(ls.className)(v1, v2)
        }
      case ars @ ARShift(e1, e2) =>
        (evalBigStep(stmData)(e1), evalBigStep(stmData)(e2)) match {
          case (IntCst(k1), IntCst(k2)) =>
            val result = k1 >> k2
            val extendedResult = e1.typ.asInstanceOf[TyAnyInt] match {
              case TySInt(w) => signExtendToLong(result, w)
              case TyUInt(_) => result
            }
            IntCst(extendedResult)(ars.typ)
          case (_: Undefined, _) => Undefined(ars.typ)
          case (_, _: Undefined) => Undefined(ars.typ)
          case (v1, v2)          => badArgs(ars.className)(v1, v2)
        }
      case lrs @ LRShift(e1, e2) =>
        (evalBigStep(stmData)(e1), evalBigStep(stmData)(e2)) match {
          case (n1 @ IntCst(k1), IntCst(k2)) =>
            val w = n1.typ.asInstanceOf[TyAnyInt].w
            val result = maskOutHigherBits(k1, w) >>> k2
            val extendedResult = e1.typ.asInstanceOf[TyAnyInt] match {
              case TySInt(w) => signExtendToLong(result, w)
              case TyUInt(_) => result
            }
            IntCst(extendedResult)(lrs.typ)
          case (_: Undefined, _) => Undefined(lrs.typ)
          case (_, _: Undefined) => Undefined(lrs.typ)
          case (v1, v2)          => badArgs(lrs.className)(v1, v2)
        }

      case True  => True
      case False => False
      case not @ Not(e) =>
        evalBigStep(stmData)(e) match {
          case False        => True
          case True         => False
          case u: Undefined => u
          case v            => badArgs(not.className)(v)
        }
      case And(terms @ _*) =>
        // TODO: does this exception to Undefined's normal propagation cause any problems?
        all(terms.map(evalBigStep(stmData)))
      case Or(terms @ _*) =>
        // TODO: does this exception to Undefined's normal propagation cause any problems?
        any(terms.map(evalBigStep(stmData)))
      case Equal(e1, e2) =>
        areEqual(evalBigStep(stmData)(e1), evalBigStep(stmData)(e2))
      case lt @ LessThan(e1, e2) =>
        (evalBigStep(stmData)(e1), evalBigStep(stmData)(e2)) match {
          case (IntCst(n1), IntCst(n2)) => if (n1 < n2) True else False
          case (_: Undefined, _)        => Undefined(TyBool)
          case (_, _: Undefined)        => Undefined(TyBool)
          case (v1, v2)                 => badArgs(lt.className)(v1, v2)
        }
      case mux @ Mux(c, t, f) =>
        evalBigStep(stmData)(c) match {
          case True         => evalBigStep(stmData)(t)
          case False        => evalBigStep(stmData)(f)
          case _: Undefined => Undefined(mux.typ)
          case v            => badArgs(mux.className, position = "condition")(v)
        }

      case tup @ Tuple(elems @ _*) =>
        Tuple(elems.map(evalBigStep(stmData)): _*)(tup.typ)
      case ta @ TupleAccess(tup, IntCst(i)) =>
        evalBigStep(stmData)(tup) match {
          case Tuple(elems @ _*) => elems(i.toInt)
          case _: Undefined      => Undefined(ta.typ)
          case v => badArgs(ta.className, position = "left-hand side")(v)
        }

      case vb @ VecBuild(n, Function(iVar, body)) =>
        evalBigStep(stmData)(n) match {
          case IntCst(n) if n >= 0 =>
            val elemValues = (0 until n.toInt).map({ i =>
              val subs = Map[Expr, Expr](iVar -> IntCst(i)(iVar.typ))
              evalBigStep(stmData)(body.subPreserveType(subs))
            })
            VecLiteral(elemValues: _*)(vb.typ)
          case _: Undefined => Undefined(vb.typ)
          case n =>
            throw new IllegalArgumentException(
              s"Vector length $n. Vectors must have non-negative integer length."
            )
        }
      case va @ VecAccess(v, i) =>
        (evalBigStep(stmData)(v), evalBigStep(stmData)(i)) match {
          case (VecLiteral(elems @ _*), IntCst(i)) =>
            if (elems.indices.contains(i)) {
              elems(i.toInt)
            } else {
              Undefined(va.typ)
            }
          case (_: Undefined, _) => Undefined(va.typ)
          case (_, _: Undefined) => Undefined(va.typ)
          case (v, i)            => badArgs(va.className)(v, i)
        }
      case vec @ VecLiteral(elems @ _*) =>
        VecLiteral(elems.map(evalBigStep(stmData)): _*)(vec.typ)

      case c: FixCst => c
      case prod @ IntFixProd(e1, e2) =>
        (evalBigStep(stmData)(e1), evalBigStep(stmData)(e2)) match {
          case (IntCst(k), v2 @ FixCst(numer)) =>
            val result = (k * numer) >>> v2.typ.shift
            IntCst(truncate(result, prod.typ.asInstanceOf[TyUInt]))(prod.typ)
          case (_: Undefined, _) => Undefined(prod.typ)
          case (_, _: Undefined) => Undefined(prod.typ)
          case (v1, v2)          => badArgs(prod.className)(v1, v2)
        }

      case sdata @ StmData(s: Param) =>
        stmData.get(s) match {
          case None =>
            throw new IllegalArgumentException(
              s"invalid use of ${StmData.getClass.getSimpleName} (e.g., outside a stream or with incorrect arguments)."
            )
          case Some(None)    => Undefined(sdata.typ) // not ready
          case Some(Some(v)) => v
        }
      case StmData(_) =>
        throw new IllegalArgumentException(
          s"Invalid use of ${StmData.getClass.getSimpleName} (non-param input)."
        )
      case s @ (_: StmLiteral | _: StmBuild | _: LetStm) =>
        throw new IllegalArgumentException(
          s"${this.getClass.getName} does not handle streams: $s"
        )

      case s: SyntaxSugar =>
        throw new IllegalArgumentException(
          s"There should be no more syntax sugar after lowering. Found $s."
        )
    }
    assert(
      result.typ ~~= e.typ,
      s"evaluation should preserve the type (expected ${e.typ}, found ${result.typ})"
    )
    result
  }

  private def truncate(n: Long, typ: TyAnyInt): Long = {
    val masked = maskOutHigherBits(n, typ.w)
    typ match {
      case _: TySInt =>
        // Need to sign extend because the value is stored in a Scala
        // Long, which is a 64-bit signed int
        signExtendToLong(masked, typ.w)
      case _: TyUInt =>
        // Higher bits are already zero, as they should be
        masked
    }
  }

  private def maskOutHigherBits(n: Long, w: Int): Long = {
    val mask = (0 until w).foldLeft(0L)({ case (n, _) =>
      (n << 1) | 1
    })
    n & mask
  }

  private def signExtendToLong(n: Long, w: Int): Long = {
    val msbIsOne = (n & (1L << (w - 1))) != 0
    if (msbIsOne) {
      (w until 64).foldLeft(n)({ case (n, i) =>
        n | (1L << i)
      })
    } else {
      n
    }
  }

  private def toBits(e: Expr): Seq[Expr] = {
    e match {
      case b: BoolCst => Seq(b)
      case c: IntCst =>
        val w = c.typ.asInstanceOf[TyAnyInt].w
        // TODO: This duplicates functionality in mhir.gen.
        //       Share the code somehow?
        if (c.i < 0) {
          val bin = c.i.toBinaryString
            .map(_ == '1')
            .map(x => if (x) True else False)
          assert(bin.head == True)
          assert(bin.length == 64)
          val truncated = bin.takeRight(w)
          assert(truncated.head == True)
          truncated
        } else {
          val bin = c.i.toBinaryString
            .map(_ == '1')
            .map(x => if (x) True else False)
          assert(bin.length <= w)
          val padded = (0 until (w - bin.length)).map(_ => False) ++ bin
          if (c.typ.isInstanceOf[TySInt]) {
            assert(padded.head == False)
          }
          padded
        }
      case k: FixCst              => toBits(C(k.numer)(k.typ.t))
      case Tuple(elems @ _*)      => elems.flatMap(toBits)
      case VecLiteral(elems @ _*) => elems.flatMap(toBits)
      case Undefined(typ) =>
        val u = Undefined(TyBool)
        val IntCst(w) = typ.bitwidth
        (0 until w.toInt).map(_ => u)
      case v => badArgs(Bits.getClass.getName)(v)
    }
  }

  @tailrec
  private def intFromBits(bits: Seq[Expr], k: Long, typ: Type): Expr = {
    bits match {
      case Seq()                 => C(k)(typ)
      case Seq(False, bs @ _*)   => intFromBits(bs, k << 1, typ)
      case Seq(True, bs @ _*)    => intFromBits(bs, (k << 1) | 1, typ)
      case Seq(_: Undefined, _*) => Undefined(typ)
    }
  }

  private def fromBits(bits: Seq[Expr], targetTyp: Type): Expr = {
    targetTyp match {
      case TyBool       => bits.head
      case uint: TyUInt => intFromBits(bits, 0, uint)
      case int: TySInt =>
        bits.head match {
          case False =>
            intFromBits(bits, 0, int)
          case True =>
            // Start with -1 (which is all 1s in twos complement) to
            // handle sign extension
            intFromBits(bits, -1, int)
          case _: Undefined => Undefined(int)
          case v =>
            badArgs(InterpretAs.getClass.getName, position = "an input")(v)
        }
      case typ @ TyTuple(elems @ _*) =>
        val elemRangeStarts = elems.scanLeft(0)({ case (acc, t) =>
          val IntCst(w) = t.bitwidth
          acc + w.toInt
        })
        val elemRangeEnds = elemRangeStarts.drop(1)
        Tuple(
          elems
            .zip(elemRangeStarts.zip(elemRangeEnds))
            .map({ case (typ, (from, until)) =>
              fromBits(bits.slice(from, until), typ)
            }): _*
        )(typ)
      case vecTyp @ TyVec(elemTyp, IntCst(n)) =>
        val IntCst(wLong) = elemTyp.bitwidth
        val w = wLong.toInt
        VecLiteral(
          (0 until n.toInt)
            .map({ i =>
              fromBits(bits.slice(i * w, (i + 1) * w), elemTyp)
            }): _*
        )(vecTyp)
    }
  }

  private def areEqual(v1: Expr, v2: Expr): Expr = {
    (v1, v2) match {
      case (_: Undefined, _)          => Undefined(TyBool)
      case (_, _: Undefined)          => Undefined(TyBool)
      case (b1: BoolCst, b2: BoolCst) => if (b1 == b2) True else False
      case (IntCst(n1), IntCst(n2))   => if (n1 == n2) True else False
      case (Tuple(xs1 @ _*), Tuple(xs2 @ _*)) =>
        assert(xs1.length == xs2.length, "tuple lengths must match")
        all(xs1.zip(xs2).map({ case (x, y) => areEqual(x, y) }))
      case (VecLiteral(xs1 @ _*), VecLiteral(xs2 @ _*)) =>
        assert(xs1.length == xs2.length, "vector length must match")
        all(xs1.zip(xs2).map({ case (x, y) => areEqual(x, y) }))
      case (v1, v2) =>
        throw new AssertionError(s"cannot compare $v1 with $v2")
    }
  }

  @tailrec
  private def all(xs: Seq[Expr], undef: Boolean = false): Expr = {
    xs match {
      case Seq() if undef               => Undefined(TyBool)
      case Seq() if !undef              => True
      case Seq(False, _*)               => False
      case Seq(True, tail @ _*)         => all(tail, undef = undef)
      case Seq(_: Undefined, tail @ _*) => all(tail, undef = true)
    }
  }

  @tailrec
  private def any(xs: Seq[Expr], undef: Boolean = false): Expr = {
    xs match {
      case Seq() if undef               => Undefined(TyBool)
      case Seq() if !undef              => False
      case Seq(True, _*)                => True
      case Seq(False, tail @ _*)        => any(tail, undef = undef)
      case Seq(_: Undefined, tail @ _*) => any(tail, undef = true)
    }
  }

  private def badArgs(
      op: String,
      position: String = "operands"
  )(values: Expr*): Nothing = {
    throw new AssertionError(
      s"$position of $op evaluated to: ${values.mkString(", ")}"
    )
  }
}
