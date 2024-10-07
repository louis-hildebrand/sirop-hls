package opt

import ir.*

import scala.annotation.tailrec

object Optimizer {
  def optimize(e: Expr): Expr = {
    PartialEvalPass.partialEval(e) match {
      case s: StmBuild => optimizeStream(s)
      case e           => e
    }
  }

  def optimizeStream(stm: StmBuild): StmBuild = {
    val s1 = StmInductionVarRemovalPass.removeInductionVars(
      StmFusePass.fuseCompletely(stm)
    )
    s1
//    TODO
//    getInputStream(s1) match {
//      case None => s1
//      case Some(input) =>
//        PartialEvalPass.partialEval(s1.length) match {
//          case IntCst(n) =>
//            if actsLikeIdentityStream(s1, n, StmNext(input).__1) then {
//              StmCanonPass.canonicalIdentityStream(n, input)
//            } else {
//              s1
//            }
//          case _ => s1
//        }
//    }
  }

  private def getInputStream(stm: StmBuild): Option[Param] = {
    // TODO: The final compiler must check the type of the params, not only
    //       that they are params!
    val seed = stm.seed.asInstanceOf[Tuple]
    val params =
      seed.elems.filter(e => e.isInstanceOf[Param])
    if params.length == 1 then {
      Some(params.head.asInstanceOf[Param])
    } else {
      None
    }
  }

  // TODO: get rid of this, since it doesn't work for very large streams or streams with non-static length?
  @tailrec
  private def actsLikeIdentityStream(
      s: Expr,
      n: Int,
      expected: TupleAccess
  ): Boolean = {
    // TODO: Also check that there are no side effects (e.g., writing to
    //       memory) other than reading input stream?
    if n <= 0 then {
      true
    } else {
      val nextOut = PartialEvalPass.partialEval(StmNext(s).__1)
      if nextOut == expected then {
        val nextStm = PartialEvalPass.partialEval(StmNext(s).__0)
        val nextExpected = StmNext(expected.t.__0).__1
        actsLikeIdentityStream(nextStm, n - 1, nextExpected)
      } else {
        false
      }
    }
  }
}
