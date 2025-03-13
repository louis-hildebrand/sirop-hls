package ir

trait Formula {

  /** Find the values produced by this formula for t = <code>tMin</code>,
    * <code>tMin</code> + 1, <code>tMin</code> + 2, ..., <code>tMin + n -
    * 1</code>.
    */
  def evalSeq(tMin: Int, n: Int): Seq[Expr]
}

/** A formula that directly computes a value given the current time.
  */
case class FunctionOfTime(f: Function) extends Formula {
  override def evalSeq(tMin: Int, iterations: Int): Seq[Expr] = {
    (tMin until (tMin + iterations)).map(t => eval(FunCall(f, t)))
  }
}

/** A recurrence equation with an initial value <code>z</code> and a function
  * that computes the next value from the current value. That is, if
  * <code>z</code> has type <code>T</code>, then <code>f</code> must have type
  * <code>T -&gt; T</code>.
  */
case class Recurrence(z: Expr, f: Function) extends Formula {
  override def evalSeq(tMin: Int, iterations: Int): Seq[Expr] = {
    if (iterations <= 0) {
      Seq()
    } else {
      ir.eval(z) +: step().evalSeq(tMin + 1, iterations - 1)
    }
  }

  def step(): Recurrence = {
    Recurrence(FunCall(f, z), f)
  }
}

/** A recurrence equation with an initial value <code>z</code> and a function
  * that computes the next value from the current value <i>and the current
  * time</i>. That is, if <code>z</code> has type <code>T</code>, then
  * <code>f</code> must have type <code>Int -> T -&gt; T</code>.
  */
case class TimeRecurrence(z: Expr, f: Function) extends Formula {
  override def evalSeq(tMin: Int, iterations: Int): Seq[Expr] = {
    if (iterations <= 0) {
      Seq()
    } else {
      val tail =
        TimeRecurrence(FunCall(FunCall(f, tMin), z), f)
          .evalSeq(tMin + 1, iterations - 1)
      ir.eval(z) +: tail
    }
  }
}
