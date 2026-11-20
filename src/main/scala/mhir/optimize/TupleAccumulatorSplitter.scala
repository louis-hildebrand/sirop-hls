package mhir.optimize

import mhir.canonicalize._
import mhir.ir._
import mhir.matchers.{TyVecs, VecAccesses, VecBuilds}
import mhir.typecheck._

import scala.annotation.tailrec

/** Transformation to split tuple accumulators into separate accumulators - even
  * tuples inside vectors.
  */
object TupleAccumulatorSplitter {

  def applyOnce(s: StmBuild): StmBuild = {
    this.applyOnce(s, s.accumulators.map({ case (x, _) => x }).toSeq)
  }

  @tailrec
  private def applyOnce(s: StmBuild, varsToTransform: Seq[Param]): StmBuild = {
    varsToTransform.headOption.map(_.typ) match {
      case Some(TyVecs(TyTuple(ts @ _*), lengths)) =>
        val varToTransform = varsToTransform.head
        val (oldInit, oldNext, oldDelay) = s.accumulators(varToTransform)
        val newParams = ts.zipWithIndex
          .map({ case (typ, i) =>
            Param(s"${varToTransform.prefix}_$i")(TyVecs(typ, lengths))
          })
        val newVbuildIndices = lengths.map(_ => Param("i")(U32))
        val newInits = ts.indices.map({ j =>
          val v = VecBuilds(
            lengths.reverse,
            newVbuildIndices,
            TupleAccess(VecAccesses(oldInit, newVbuildIndices), j)()
          ).tchk()
          PartialEvalPass.partialEval(v)
        })
        val newNexts = ts.indices.map({ j =>
          val v = VecBuilds(
            lengths.reverse,
            newVbuildIndices,
            TupleAccess(VecAccesses(oldNext, newVbuildIndices), j)()
          ).tchk()
          PartialEvalPass.partialEval(v)
        })
        val newAccumulators = newParams
          .zip(newInits.zip(newNexts))
          .map({ case (x, (init, next)) => x -> (init, next, oldDelay) })
          .toMap
        val replacement = PartialEvalPass.partialEval({
          VecBuilds(
            lengths.reverse,
            newVbuildIndices,
            Tuple(newParams.map(VecAccesses(_, newVbuildIndices)): _*)()
          ).tchk()
        })
        val newSbuild = newAccumulators
          .foldLeft(s)({ case (s, (x, (init, next, delay))) =>
            s.addAccumulator(x, init, next, delay)
          })
          .replaceVars(Map(varToTransform -> replacement))
          .tchk()
          .asInstanceOf[StmBuild]
        this.applyOnce(newSbuild, varsToTransform.tail ++ newParams)
      case Some(_) =>
        this.applyOnce(s, varsToTransform.tail)
      case None =>
        // Base case
        s
    }
  }
}
