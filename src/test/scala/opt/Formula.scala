package opt

import ir._

trait Formula {

  /** Find the values produced by this formula for t = <code>tMin</code>,
    * <code>tMin</code> + 1, <code>tMin</code> + 2, ..., <code>tMin + n -
    * 1</code>.
    */
  def evalSeq(tMin: Int, n: Int): Seq[Expr]

  def tchk(): Formula
}

/** A formula that directly computes a value given the current time.
  */
case class FunctionOfTime(f: Function) extends Formula {
  override def evalSeq(tMin: Int, iterations: Int): Seq[Expr] = {
    (tMin until (tMin + iterations)).map(t => eval(FunCall(f, t)()))
  }

  override def tchk(): Formula = FunctionOfTime(f.tchk().asInstanceOf[Function])
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
        TimeRecurrence(FunCall(FunCall(f, tMin)(), z)(), f)
          .evalSeq(tMin + 1, iterations - 1)
      ir.eval(z) +: tail
    }
  }

  override def tchk(): Formula = {
    TimeRecurrence(z.tchk(), f.tchk().asInstanceOf[Function])
  }
}

case class StreamTimeRecurrence(z: Expr, f: Function) extends Formula {
  override def evalSeq(tMin: Int, iterations: Int): Seq[Expr] = {
    if (iterations <= 0) {
      Seq()
    } else {
      val head = ir.eval(z)
      val tailRec =
        StreamTimeRecurrence(
          Mux(FunCall(FunCall(f, tMin)(), head)(), StmNextK(head, 1)(), head)(),
          f
        )
      val tail = tailRec.evalSeq(tMin + 1, iterations - 1)
      head +: tail
    }
  }

  override def tchk(): Formula = {
    StreamTimeRecurrence(z.tchk(), f.tchk().asInstanceOf[Function])
  }
}
