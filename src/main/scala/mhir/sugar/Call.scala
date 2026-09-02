package mhir.sugar

import mhir.ir._
import mhir.typecheck._

case class Call(
    callee: Expr,
    typArgs: Seq[Type],
    args: Seq[Expr]
)(typ: Type = Missing)
    extends SyntaxSugar(callee +: args: _*)(typ) {

  override def rebuild(typ: Type, newChildren: Seq[Expr]): Expr = {
    newChildren match {
      case Seq(callee, args @ _*) => Call(callee, this.typArgs, args)(typ)
      case _ => throw new BadRebuildError(this, newChildren)
    }
  }

  override def typecheck(
      context: Map[Param, Type],
      constValues: Map[Param, Expr]
  )(implicit c: Canonicalizer): Expr = {
    val error = { (f: Param) =>
      throw new TypeError(s"wrong number of arguments for $f")
    }
    val combinedArgs = (this.typArgs, this.args)
    val handshake = mhir.ir.globalOptions.handshake
    val resolved = this.callee match {
      // Arithmetic operators ----------------------------------------------
      case f @ Param("min", -1) =>
        combinedArgs match {
          case (Seq(), Seq(x, y)) => Min(x, y)()
          case _                  => error(f)
        }
      case f @ Param("max", -1) =>
        combinedArgs match {
          case (Seq(), Seq(x, y)) => Max(x, y)()
          case _                  => error(f)
        }
      case f @ Param("sign", -1) =>
        combinedArgs match {
          case (Seq(), Seq(x)) => ToSigned(x)()
          case _               => error(f)
        }
      case f @ Param("unsign", -1) =>
        combinedArgs match {
          case (Seq(), Seq(x)) => ToUnsigned(x)()
          case _               => error(f)
        }
      case f @ Param(name, -1) if name.matches("pad[0-9]+") =>
        combinedArgs match {
          case (Seq(), Seq(x)) =>
            val w = name.substring("pad".length).toInt
            PadTo(x, w)()
          case _ => error(f)
        }
      case f @ Param(name, -1) if name.matches("truncate[0-9]+") =>
        combinedArgs match {
          case (Seq(), Seq(x)) =>
            val w = name.substring("truncate".length).toInt
            TruncateTo(x, w)()
          case _ => error(f)
        }
      case f @ Param("bits", -1) =>
        combinedArgs match {
          case (Seq(), Seq(e)) => Bits(e)()
          case _               => error(f)
        }
      case f @ Param("interpret_as", -1) =>
        combinedArgs match {
          case (Seq(targetTyp), Seq(e)) => InterpretAs(e, targetTyp)()
          case _                        => error(f)
        }
      case f @ Param("zeros", -1) =>
        combinedArgs match {
          case (Seq(typ), Seq()) => AllZero(typ)
          case _                 => error(f)
        }
      case f @ Param("ones", -1) =>
        combinedArgs match {
          case (Seq(typ), Seq()) => AllOne(typ)
          case _                 => error(f)
        }
      // Vector operators --------------------------------------------------
      case f @ Param("VecLength", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) => VecLength(v)()
          case _               => error(f)
        }
      case f @ Param("Vec2Stm", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) if handshake => mhir.sugar.handshake.Vec2Stm(v)()
          case (Seq(), Seq(v)) if !handshake => ???
          case _                             => error(f)
        }
      case f @ Param("VecMap", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v, f)) => VecMap(v, f)()
          case _                  => error(f)
        }
      case f @ Param("VecMap2", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v1, v2, f)) => VecMap2(v1, v2, f)()
          case _                       => error(f)
        }
      case f @ Param("VecZip", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v1, v2)) => VecZip(v1, v2)()
          case _                    => error(f)
        }
      case f @ Param("VecReduce", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v, f)) if handshake =>
            mhir.sugar.handshake.VecReduce(v, f)()
          case (Seq(), Seq(v, f)) if !handshake =>
            ???
          case _ => error(f)
        }
      case f @ Param("VecFold", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v, z, f)) => VecFoldComb(v, z, f)()
          case _                     => error(f)
        }
      case f @ Param("VecAll", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) => VecAll(v)()
          case _               => error(f)
        }
      case f @ Param("VecAny", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) => VecAny(v)()
          case _               => error(f)
        }
      case f @ Param("VecSum", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) => VecSum(v)()
          case _               => error(f)
        }
      case f @ Param("VecSplit", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, m)) => VecSplit(s, m)()
          case _                  => error(f)
        }
      case f @ Param("VecJoin", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) => VecJoin(v)()
          case _               => error(f)
        }
      case f @ Param("VecConcat", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v1, v2)) => VecConcat(v1, v2)()
          case _                    => error(f)
        }
      case f @ Param("VecShiftLeft", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v, e)) => VecShiftLeft(v, e)()
          case _                  => error(f)
        }
      case f @ Param("VecCst", -1) =>
        combinedArgs match {
          case (Seq(), Seq(n, c)) => VecCst(n, c)()
          case _                  => error(f)
        }
      case f @ Param("VecRange", -1) =>
        combinedArgs match {
          case (Seq(), Seq(n, z, delta)) => VecRange(n, z, delta)()
          case _                         => error(f)
        }
      case f @ Param("VecReverse", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) => VecReverse(v)
          case _               => error(f)
        }
      case f @ Param("VecTranspose", -1) =>
        combinedArgs match {
          case (Seq(), Seq(v)) => VecTranspose(v)()
          case _               => error(f)
        }
      // Stream operators --------------------------------------------------
      case f @ Param("Stm2Vec", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s)) if handshake => mhir.sugar.handshake.Stm2Vec(s)()
          case (Seq(), Seq(s)) if !handshake => ???
          case _                             => error(f)
        }
      case f @ Param("StmMap", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, f)) if handshake =>
            mhir.sugar.handshake.StmMap(s, f)()
          case (Seq(), Seq(s, f)) if !handshake =>
            mhir.sugar.nohandshake.StmMap(s, f, Undefined(Missing))()
          case (Seq(), Seq(s, f, head)) if !handshake =>
            mhir.sugar.nohandshake.StmMap(s, f, head)()
          case _ => error(f)
        }
      case f @ Param("StmMap2", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s1, s2, f)) if handshake =>
            mhir.sugar.handshake.StmMap2(s1, s2, f)()
          case (Seq(), Seq(s1, s2, f)) if !handshake =>
            mhir.sugar.nohandshake.StmMap2(s1, s2, f, Undefined(Missing))()
          case (Seq(), Seq(s1, s2, f, head)) if !handshake =>
            mhir.sugar.nohandshake.StmMap2(s1, s2, f, head)()
          case _ => error(f)
        }
      case f @ Param("StmZip", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s1, s2))       => StmZip(s1, s2)()
          case (Seq(), Seq(s1, s2, head)) => StmZip(s1, s2, head)()
          case _                          => error(f)
        }
      case f @ Param("StmReduce", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, f)) if handshake =>
            mhir.sugar.handshake.StmReduce(s, f)()
          case (Seq(), Seq(s, f)) if !handshake =>
            mhir.sugar.nohandshake.StmReduce(s, f)()
          case _ => error(f)
        }
      case f @ Param("StmFold", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, z, f)) => StmFold(s, z, f)()
          case _                     => error(f)
        }
      case f @ Param("StmAll", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s)) => StmAll(s)()
          case _               => error(f)
        }
      case f @ Param("StmAny", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s)) => StmAny(s)()
          case _               => error(f)
        }
      case f @ Param("StmSum", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s)) => StmSum(s)()
          case _               => error(f)
        }
      case f @ Param("StmSplit", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, m)) => StmSplit(s, m)()
          case _                  => error(f)
        }
      case f @ Param("StmJoin", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s)) => StmJoin(s)()
          case _               => error(f)
        }
      case f @ Param("StmConcat", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s1, s2)) if handshake =>
            mhir.sugar.handshake.StmConcat(s1, s2)()
          case (Seq(), Seq(s1, s2)) if !handshake =>
            mhir.sugar.nohandshake.StmConcat(s1, s2, Undefined(Missing))()
          case (Seq(), Seq(s1, s2, head)) if !handshake =>
            mhir.sugar.nohandshake.StmConcat(s1, s2, head)()
          case _ => error(f)
        }
      case f @ Param("StmCst", -1) =>
        combinedArgs match {
          case (Seq(), Seq(n, c)) => StmCst(n, c)()
          case _                  => error(f)
        }
      case f @ Param("StmRange", -1) =>
        combinedArgs match {
          case (Seq(), Seq(n, z, delta)) => StmRange(n, z, delta)()
          case _                         => error(f)
        }
      case f @ Param("StmCount2D", -1) =>
        combinedArgs match {
          case (Seq(), Seq(n, m)) => StmCount2D(n, m)()
          case _                  => error(f)
        }
      case f @ Param("StmSlide", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, w)) if handshake =>
            mhir.sugar.handshake.StmSlide(s, w)()
          case (Seq(), Seq(s, w, stride)) if handshake =>
            mhir.sugar.handshake.StmSlide(s, w, stride)()
          case (Seq(), Seq(s, w)) if !handshake =>
            mhir.sugar.nohandshake.StmSlide(s, w)()
          case _ => error(f)
        }
      case f @ Param("StmSlideStartingWith", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, z)) => StmSlideStartingWith(s, z)()
          case _                  => error(f)
        }
      case f @ Param("StmSlide2D", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, h, w)) => StmSlide2D(s, h, w)()
          case _                     => error(f)
        }
      case f @ Param("StmAccess", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, i)) => StmAccess(s, i)()
          case _                  => error(f)
        }
      case f @ Param("StmTake", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, k)) => StmTake(s, k)()
          case _                  => error(f)
        }
      case f @ Param("StmDrop", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, k)) if handshake =>
            mhir.sugar.handshake.StmDrop(s, k)()
          case (Seq(), Seq(s, k)) if !handshake =>
            mhir.sugar.nohandshake.StmDrop(s, k, Undefined(Missing))()
          case (Seq(), Seq(s, k, head)) if !handshake =>
            mhir.sugar.nohandshake.StmDrop(s, k, head)()
          case _ => error(f)
        }
      case f @ Param("StmExtendBy", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, k)) if handshake =>
            mhir.sugar.handshake.StmExtendBy(s, k)()
          case (Seq(), Seq(s, k)) if !handshake =>
            mhir.sugar.nohandshake.StmExtendBy(s, k)()
          case _ => error(f)
        }
      case f @ Param("StmCascade", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s)) => StmCascade(s)()
          case _               => error(f)
        }
      case f @ Param("StmMapDotCascaded", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s1, s2, delay)) => StmMapDotCascaded(s1, s2, delay)()
          case _                           => error(f)
        }
      case f @ Param("StmMapDot", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s1, s2, delay)) => StmMapDot(s1, s2, delay)()
          case _                           => error(f)
        }
      case f @ Param("StmDelay", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, d)) => StmDelay(s, d)()
          case _                  => error(f)
        }
      case _ =>
        combinedArgs match {
          case (Seq(), Seq(x)) => FunCall(this.callee, x)()
          case (Seq(), args)   => FunCall(this.callee, Tuple(args: _*)())()
          case _ =>
            throw new TypeError("callee does not accept type arguments")
        }
    }
    resolved.tchk(context, constValues)
  }

  /** The precedence of this expression. See [[Precedence]].
    */
  override def precedence: Int = Precedence.FunCall

  /** See [[mhir.ir.ExprPrinter.displayOneLine]].
    *
    * @note
    *   there is no need to wrap the final result in parentheses; that will be
    *   handled outside this method.
    */
  override def displayOneLine(): String = {
    val calleeStr = ExprPrinter.displayOneLine(this.callee)
    val lhs = this.typArgs match {
      case Seq() => calleeStr
      case _ =>
        val typArgsStr = this.typArgs.mkString(", ")
        this.callee match {
          case _: Param => s"$calleeStr:[$typArgsStr]"
          case _        => s"($calleeStr):[$typArgsStr]"
        }
    }
    ExprPrinter.displayFunCallOneLine(lhs, this.args)
  }

  /** Convert this expression to a string, with this expression being wrapped.
    *
    * @note
    *   there is no need to wrap the final result in parentheses; that will be
    *   handled outside this method.
    */
  override def displayMultiLine(maxWidth: Int): String = {
    val calleeStr = ExprPrinter.displayOneLine(this.callee)
    val lhs = this.typArgs match {
      case Seq() => calleeStr
      case _ =>
        val typArgsStr = this.typArgs.mkString(", ")
        this.callee match {
          case _: Param => s"$calleeStr:[$typArgsStr]"
          case _        => s"($calleeStr):[$typArgsStr]"
        }
    }
    ExprPrinter.displayFunCallMultiLine(lhs, this.args, maxWidth)
  }
}
