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

  /** Checks whether this resource usage is less than or equal to the given
    * resource usage in every way: ALMs, memory, and DSPs.
    */
  def <=(that: AreaCost): Boolean = {
    this.alm <= that.alm && this.mem <= that.mem && this.dsp <= that.dsp
  }

  /** See [[<=]].
    */
  def >=(that: AreaCost): Boolean = that <= this

  /** Finds the element-wise maximum of two areas.
    */
  def max(that: AreaCost): AreaCost = {
    AreaCost(
      math.max(this.alm, that.alm),
      math.max(this.mem, that.mem),
      math.max(this.dsp, that.dsp)
    )
  }
}

object AreaCost {
  val Zero: AreaCost = AreaCost(0, 0, 0)
}
