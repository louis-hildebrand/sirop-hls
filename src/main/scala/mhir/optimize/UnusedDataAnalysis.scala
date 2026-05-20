package mhir.optimize

import mhir.ir._

sealed trait UseStatus {
  def setUsed(path: Seq[Int]): UseStatus = {
    (this, path) match {
      case (AllUsed, _) =>
        this
      case (_, Seq()) =>
        AllUsed
      case (SomeUnused(elems @ _*), Seq(i, is @ _*)) =>
        SomeUnused(elems.updated(i, elems(i).setUsed(is)): _*)
      case (AllUnused, path) =>
        // I would need to replace this with SomeUnused(...), but I don't know
        // how many elements to put.
        throw new IllegalArgumentException(s"$this.setUsed($path)")
    }
  }

  def simplify(): UseStatus = {
    this match {
      case AllUsed   => this
      case AllUnused => this
      case SomeUnused(elems @ _*) =>
        val newElems = elems.map(_.simplify())
        if (newElems.forall(_ == AllUsed)) {
          AllUsed
        } else if (newElems.forall(_ == AllUnused)) {
          AllUnused
        } else {
          SomeUnused(newElems: _*)
        }
    }
  }
}
case object AllUsed extends UseStatus
case object AllUnused extends UseStatus
case class SomeUnused(elems: UseStatus*) extends UseStatus

/** This analysis identifies parts of the output of an `sbuild` that are not
  * used by the consumer.
  *
  * See also: the [[UnusedDataRemover]] transformation.
  *
  * @param target
  *   the producer stream whose output should be analyzed.
  */
case class UnusedDataAnalysis(target: Param) {

  def findUnused(s: StmBuild): UseStatus = {
    require(target.typ.isInstanceOf[TyStm])
    require(s.accVars.contains(target))
    val TyStm(dataTyp, _) = target.typ
    (s.data +: s.valid +: s.nextByVar.values.toSeq)
      .foldLeft(init(dataTyp))({ case (acc, e) =>
        this.findUses(e, acc, Seq())
      })
      .simplify()
  }

  private def init(typ: Type): UseStatus = {
    typ match {
      case TyTuple(ts @ _*) => SomeUnused(ts.map(init): _*)
      case _                => AllUnused
    }
  }

  private def findUses(e: Expr, acc: UseStatus, path: Seq[Int]): UseStatus = {
    e match {
      case StmData(x: Param) if x == target =>
        acc.setUsed(path)
      case TupleAccess(t, IntCst(i)) =>
        this.findUses(t, acc, i.toInt +: path)
      // TODO: Look out for variable binders (e.g., FunCall)? In practice
      //       they'll probably all have been removed by now, so maybe it's not
      //       necessary.
      //       Ignoring them shouldn't lead to any situations where used data
      //       is marked unused.
      //       We will only miss cases where some unused data appears to be
      //       used, which is not a big deal.
      case e =>
        e.children.foldLeft(acc)({ case (acc, e) =>
          this.findUses(e, acc, Seq())
        })
    }
  }
}
