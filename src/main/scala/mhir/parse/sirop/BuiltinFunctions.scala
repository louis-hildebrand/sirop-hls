package mhir.parse
package sirop

import mhir.ir._
import mhir.sugar._

private[sirop] object BuiltinFunctions {

  def parseFunCall(
      f: Expr,
      typArgs: Seq[Type],
      args: Seq[Expr],
      loc: SourcePoint
  ): Expr = {
    val error = { (f: Param) =>
      throw SyntaxError(s"wrong number of arguments for $f", loc)
    }
    val combinedArgs = (typArgs, args)
    f match {
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
          case (Seq(), Seq(v)) => Vec2Stm(v)()
          case _               => error(f)
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
          case (Seq(), Seq(v, f)) => VecReduceComb(v, f)()
          case _                  => error(f)
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
          case (Seq(), Seq(s)) => Stm2Vec(s)()
          case _               => error(f)
        }
      case f @ Param("StmMap", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, f)) => StmMap(s, f)()
          case _                  => error(f)
        }
      case f @ Param("StmMap2", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s1, s2, f)) => StmMap2(s1, s2, f)()
          case _                       => error(f)
        }
      case f @ Param("StmZip", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s1, s2)) => StmZip(s1, s2)()
          case _                    => error(f)
        }
      case f @ Param("StmReduce", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, f)) => StmReduce(s, f)()
          case _                  => error(f)
        }
      case f @ Param("StmFold1D", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, z, f)) => StmFold1D(s, z, f)()
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
          case (Seq(), Seq(s1, s2)) => StmConcat(s1, s2)()
          case _                    => error(f)
        }
      case f @ Param("StmShiftLeft", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, e)) => StmShiftLeft(s, e)()
          case _                  => error(f)
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
          case (Seq(), Seq(s, w)) => StmSlide(s, w)()
          case _                  => error(f)
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
      case f @ Param("StmPrefix", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, k)) => StmPrefix(s, k)()
          case _                  => error(f)
        }
      case f @ Param("StmSuffix", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, k)) => StmSuffix(s, k)()
          case _                  => error(f)
        }
      case f @ Param("MulAddCascaded", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s1, s2, delay)) => MulAddCascaded(s1, s2, delay)()
          case _                           => error(f)
        }
      case f @ Param("StmDelay", -1) =>
        combinedArgs match {
          case (Seq(), Seq(s, d)) => StmDelay(s, d)()
          case _                  => error(f)
        }
      case _ =>
        combinedArgs match {
          case (Seq(), Seq(x)) => FunCall(f, x)()
          case (Seq(), args)   => FunCall(f, Tuple(args: _*)())()
          case _ =>
            throw SyntaxError("callee does not accept type arguments", loc)
        }
    }
  }

}
