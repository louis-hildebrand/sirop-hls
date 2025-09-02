package mhir.optimize.cost

/** Estimate of the area required for some expression.
  *
  * @param alm
  *   the amount of ALMs (logic).
  * @param mem
  *   the amount of memory.
  * @param dsp
  *   the number of DPSs.
  */
case class AreaCost(alm: Long, mem: Long, dsp: Long) {

  /** Adds up two costs.
    *
    * @param that
    *   the cost to add to this one.
    */
  def +(that: AreaCost): AreaCost = {
    AreaCost(this.alm + that.alm, this.mem + that.mem, this.dsp + that.dsp)
  }

  /** Multiplies this cost by a given factor.
    *
    * @param n
    *   the amount by which to multiply this cost.
    */
  def *(n: Long): AreaCost = {
    AreaCost(n * this.alm, n * this.mem, n * this.dsp)
  }
}

object AreaCost {
  val Zero: AreaCost = AreaCost(0, 0, 0)
}
