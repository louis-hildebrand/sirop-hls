package mhir.ir

sealed trait VecUses {
  def union(that: VecUses): VecUses
}

object VecUses {

  case class Indices(indices: Set[Long]) extends VecUses {
    override def union(that: VecUses): VecUses = {
      that match {
        case All           => that
        case that: Indices => Indices(this.indices.union(that.indices))
      }
    }
  }

  object All extends VecUses {
    override def union(that: VecUses): VecUses = this
  }

  def None = Indices(Set.empty)
}
