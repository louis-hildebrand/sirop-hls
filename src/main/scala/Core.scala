import scala.annotation.tailrec
import scala.language.implicitConversions

sealed abstract class Expr {
  def +(that: Expr): Add = Add(this, that)
  def -(that: Expr): Sub = Sub(this, that)
  def *(that: Expr): Mul = Mul(this, that)
  def /(that: Expr): Div = Div(this, that)
  def %(that: Expr): Mod = Mod(this, that)
  def ===(that: Expr): Equal = Equal(this, that)
  def !==(that: Expr): NotEqual = NotEqual(this, that)
  def lt(that: Expr): LessThan = LessThan(this, that)

  // if we use _0, _1, ... for some reasons the Scala compiler gets confused and produces error messages when matching some of the expressions
  def __0: TupleAccess = TupleAccess(this, 0)
  def __1: TupleAccess = TupleAccess(this, 1)
  def __2: TupleAccess = TupleAccess(this, 2)
}

// Tuples
case class Tuple(elems: Expr*) extends Expr
case class TupleAccess(t: Expr, i: Expr) extends Expr

// Functions
// cannot be a case class as the reference is used to distinguish between Params
class Param() extends Expr
case class Function(param: Param, body: Expr) extends Expr {
  override def equals(x: Any): Boolean = {
    if !x.isInstanceOf[Function] then false
    else {
      val that = x.asInstanceOf[Function]
      val sub = Map[Expr, Expr](this.param -> that.param)
      ExprEvaluator.substitute(this.body)(sub) == that.body
    }
  }
}
case class FunCall(f: Expr, arg: Expr) extends Expr
implicit def scalaUnaryLambdaToFunction(sl: Expr => Expr): Function = {
  val p = Param()
  Function(p, sl(p))
}
implicit def scalaBinaryLambdaToFunction(sl: Expr => Expr => Expr): Function = {
  val p1 = Param()
  val p2 = Param()
  Function(p1, Function(p2, sl(p1)(p2)))
}
object Let {
  def apply(p: Param, v: Expr, in: Expr): Expr = {
    FunCall(Function(p, in), v)
  }
}

// Integer expressions
sealed abstract class IntExpr extends Expr
implicit def int2IntCst(i: Int): IntCst = IntCst(i)
implicit def intCst2Int(ic: IntCst): Int = ic.i
case class IntCst(i: Int) extends IntExpr
case class Add(e1: Expr, e2: Expr) extends IntExpr
case class Sub(e1: Expr, e2: Expr) extends IntExpr
case class Mul(e1: Expr, e2: Expr) extends IntExpr
case class Div(e1: Expr, e2: Expr) extends IntExpr
case class Mod(e1: Expr, e2: Expr) extends IntExpr

// Boolean expressions
sealed abstract class BoolExpr extends Expr
implicit def boolean2BoolExpr(b: Boolean): BoolExpr = if (b) True else False
implicit def boolExpr2Boolean(b: BoolExpr): Boolean = if (b == True) true
else if (b == False) false
else throw new RuntimeException("unexpected boolean value")
object True extends BoolExpr
object False extends BoolExpr
// This is similar to TupleAccess(Tuple(falseE, trueE), cond), as long as
// False is interpreted as 0 and True as 1.
// However, IfThenElse does *not* evaluate the branch that's not taken, which
// is important in cases like calling StmNext() or memory accesses.
case class IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) extends Expr
case class Equal(e1: Expr, e2: Expr) extends BoolExpr
case class NotEqual(e1: Expr, e2: Expr) extends BoolExpr
case class LessThan(e1: Expr, e2: Expr) extends BoolExpr

// Streams
case class StmBuild(
    length: Expr /* Int */,
    seed: Expr /*A*/,
    nextF: Function /* A -> (A, B, Bool)*/
) extends Expr
case class StmLength(stream: Expr) extends IntExpr
case class StmNext(stream: Expr /* Stream<A>*/ ) /* (Stream<A>, A) */
    extends Expr // element only available for one clock cycle

// Vectors
case class VecBuild(len: Expr, f: Function /*Int => Expr*/ ) extends Expr
case class VecAccess(vec: Expr, i: Expr) extends Expr
case class VecLength(vec: Expr) extends IntExpr

object ExprEvaluator {

  def substitute(
      e: Expr
  )(implicit substitutions: Map[Expr, Expr]): Expr = {
    substitutions.get(e) match {
      case Some(v) => v
      case None =>
        e match {
          case t: Tuple => Tuple(t.elems.toSeq.map(substitute(_)): _*)
          case TupleAccess(t: Expr, i: Expr) =>
            TupleAccess(substitute(t), substitute(i))

          case p: Param => p
          case f: Function => {
            val newParam = Param()
            Function(
              newParam,
              substitute(f.body)(substitutions + ((f.param, newParam)))
            )
            // when substituting the body, this might be come a new function if anything is susbstituted, therefore, we create a new Param
          }
          case FunCall(f: Expr, arg: Expr) =>
            FunCall(substitute(f), substitute(arg))

          case Add(e1: Expr, e2: Expr) => Add(substitute(e1), substitute(e2))
          case Sub(e1: Expr, e2: Expr) => Sub(substitute(e1), substitute(e2))
          case Mul(e1: Expr, e2: Expr) => Mul(substitute(e1), substitute(e2))
          case Div(e1: Expr, e2: Expr) => Div(substitute(e1), substitute(e2))
          case Mod(e1: Expr, e2: Expr) => Mod(substitute(e1), substitute(e2))
          case IntCst(_)               => e

          case True  => True
          case False => False
          case IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) =>
            IfThenElse(substitute(cond), substitute(trueE), substitute(falseE))
          case NotEqual(e1: Expr, e2: Expr) =>
            NotEqual(substitute(e1), substitute(e2))
          case Equal(e1: Expr, e2: Expr) =>
            Equal(substitute(e1), substitute(e2))
          case LessThan(e1: Expr, e2: Expr) =>
            LessThan(substitute(e1), substitute(e2))

          case StmBuild(length, seed, f) =>
            StmBuild(
              substitute(length),
              substitute(seed),
              substitute(f).asInstanceOf[Function]
            )
          case StmLength(s)     => StmLength(substitute(s))
          case StmNext(e: Expr) => StmNext(substitute(e))

          case VecBuild(len: Expr, f: Function) =>
            VecBuild(substitute(len), substitute(f).asInstanceOf[Function])
          case VecAccess(vec: Expr, i: Expr) =>
            VecAccess(substitute(vec), substitute(i))
          case VecLength(vec: Expr) => VecLength(substitute(vec))
        }
    }
  }

  def partialEval(
      e: Expr
  )(implicit substitutions: Map[Expr, Expr] = Map()): Expr = {
    e match {

      case t: Tuple => Tuple(t.elems.toSeq.map(partialEval(_)): _*)
      case TupleAccess(t: Expr, i: Expr) =>
        (partialEval(t), partialEval(i)) match {
          case (tuple: Tuple, index: IntCst) =>
            partialEval(tuple.elems(index.i))
          case (tuple @ _, index @ _) => TupleAccess(tuple, index)
        }

      case p: Param =>
        substitutions.get(p) match {
          case Some(v) => v
          case None    => p
        }
      case f: Function =>
        val newF = substitute(f).asInstanceOf[Function]
        Function(newF.param, partialEval(newF.body))
      case FunCall(f: Expr, arg: Expr) =>
        partialEval(f) match {
          case fun: Function =>
            partialEval(fun.body)(
              substitutions + ((fun.param, partialEval(arg)))
            )
          case fun @ _ => FunCall(fun, partialEval(arg))
        }

      case Add(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst)       => e1.i + e2.i
          case (e, IntCst(0))                 => e
          case (Add(e, IntCst(a)), IntCst(b)) => partialEval(e + (a + b))
          case (Add(IntCst(a), e), IntCst(b)) => partialEval(e + (a + b))
          case (IntCst(b), Add(e, IntCst(a))) => partialEval(e + (a + b))
          case (IntCst(b), Add(IntCst(a), e)) => partialEval(e + (a + b))
          case (Sub(e, IntCst(a)), IntCst(b)) => partialEval(e + (b - a))
          case (Sub(IntCst(a), e), IntCst(b)) => partialEval(IntCst(a + b) - e)
          case (IntCst(b), Sub(e, IntCst(a))) => partialEval(e + (b - a))
          case (IntCst(b), Sub(IntCst(a), e)) => partialEval(IntCst(a + b) - e)
          case (e1 @ _, e2 @ _)               => Add(e1, e2)
        }
      case Sub(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst)       => e1.i - e2.i
          case (x, y) if x == y               => 0
          case (e, IntCst(0))                 => e
          case (Add(e, IntCst(a)), IntCst(b)) => partialEval(e + (a - b))
          case (Add(IntCst(a), e), IntCst(b)) => partialEval(e + (a - b))
          case (IntCst(b), Add(e, IntCst(a))) => partialEval(IntCst(b - a) - e)
          case (IntCst(b), Add(IntCst(a), e)) => partialEval(IntCst(b - a) - e)
          case (Sub(e, IntCst(a)), IntCst(b)) => partialEval(e - (a + b))
          case (Sub(IntCst(a), e), IntCst(b)) => partialEval(IntCst(a - b) - e)
          case (IntCst(b), Sub(e, IntCst(a))) => partialEval(IntCst(a + b) - e)
          case (IntCst(b), Sub(IntCst(a), e)) => partialEval(e + (b - a))
          case (Sub(x, y), z) if x == z       => partialEval(IntCst(0) - y)
          case (x, Sub(y, z)) if x == y       => z
          case (e1 @ _, e2 @ _)               => Sub(e1, e2)
        }
      case Mul(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i * e2.i
          case (e1 @ _, e2 @ _)         => Mul(e1, e2)
        }
      case Div(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i / e2.i
          case (e1 @ _, e2 @ _)         => Div(e1, e2)
        }
      case Mod(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i % e2.i
          case (e1 @ _, e2 @ _)         => Mod(e1, e2)
        }
      case IntCst(_) => e

      case True  => True
      case False => False
      case IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) =>
        partialEval(cond) match {
          case True  => partialEval(trueE)
          case False => partialEval(falseE)
          case cond @ _ =>
            if (trueE == falseE)
              trueE
            else
              IfThenElse(cond, partialEval(trueE), partialEval(falseE))
        }
      case NotEqual(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i != e2.i
          case (e1 @ _, e2 @ _)         => NotEqual(e1, e2)
        }
      case Equal(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i == e2.i
          case (e1 @ _, e2 @ _)         => Equal(e1, e2)
        }
      case LessThan(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i < e2.i
          case (e1 @ _, e2 @ _)         => LessThan(e1, e2)
        }

      case StmBuild(length, seed, f) =>
        StmBuild(
          partialEval(length),
          partialEval(seed),
          // ensures any free Param in f gets substituted
          partialEval(f).asInstanceOf[Function]
        )

      case StmLength(s) =>
        partialEval(s) match {
          case s: StmBuild => partialEval(s.length)
          case s @ _       => StmLength(s)
        }

      case StmNext(s: Expr) =>
        partialEval(s) match {
          case s: StmBuild =>
            partialEval(s.length) match {
              case len: IntCst => {
                assert(
                  len.i > 0,
                  "Attempt to call StmNext() on a stream of length 0."
                )
                partialEval(FunCall(s.nextF, s.seed)) match {
                  case next: Tuple =>
                    val n = next.elems.length
                    require(
                      n == 3,
                      s"The function in StmBuild returned a ${n}-tuple instead of a 3-tuple."
                    )
                    partialEval(next.__2) match {
                      case True =>
                        // return the new stream and the next element
                        Tuple(
                          StmBuild(
                            len.i - 1,
                            partialEval(next.__0),
                            // this function may have free parameters
                            partialEval(s.nextF).asInstanceOf[Function]
                          ),
                          partialEval(next.__1)
                        )
                      case False =>
                        // skip this element, look for the next one
                        partialEval(
                          StmNext(
                            StmBuild(
                              len,
                              partialEval(next.__0),
                              // this function may have free parameters
                              partialEval(s.nextF).asInstanceOf[Function]
                            )
                          )
                        )
                      case _ =>
                        StmNext(s)
                    }
                  case next @ _ => StmNext(s)
                }
              }
              case len @ _ => StmNext(s)
            }
          case s @ _ => StmNext(s)
        }

      case VecBuild(len: Expr, f: Function) =>
        VecBuild(
          partialEval(len),
          partialEval(f).asInstanceOf[
            Function
          ] /* ensures any free Param in f gets substituted */
        )
      case VecAccess(vec: Expr, i: Expr) =>
        partialEval(vec) match {
          case vec: VecBuild => partialEval(FunCall(vec.f, partialEval(i)))
          case vec @ _       => VecAccess(vec, partialEval(i))
        }
      case VecLength(vec: Expr) =>
        partialEval(vec) match {
          case vec: VecBuild => partialEval(vec.len)
          case vec @ _       => VecLength(vec)
        }
    }
  }

  /** Fuse a `StmBuild` with its first stream input.
    */
  def fuse(stm: Expr /* Stm<A; n> */ ): Expr /* Stm<A; n> */ = {
    // TODO: Canonicalize first?
    // TODO: Simply require accumulator to be a Tuple from the start?
    val s = ExprEvaluator.partialEval(stm).asInstanceOf[StmBuild]
    val inputStmPath = locateFirstInputStream(s) match {
      case Some(s) => s
      case None => throw new IllegalArgumentException("No input streams found.")
    }
    val inputStm = extract(s.seed, inputStmPath).asInstanceOf[StmBuild]

    val newSeed =
      Tuple(replaceWith(s.seed, inputStmPath, Tuple()), inputStm.seed)
    val newNextF = fuseFunctions(s.nextF, inputStm.nextF, inputStmPath)

    // TODO: Rewrite the "outer" stream without the input stream (replace
    //       references in nextF with a new "hole" Param?)
    // Combined seed: basically combine the seeds of the two original streams
    // Combined nextF: basically the "outer" stream's nextF, but call the
    //                 "inner" stream's nextF in the right places?
    StmBuild(StmLength(stm), newSeed, newNextF)
  }

  /** Return the path to the first input stream for the given `StmBuild`. The
    * path is a sequence of tuple indices. For example, if the accumulator of
    * the given stream is of the form (Int, (Stm, Stm), Bool), then this method
    * should return Seq(1, 0) because to reach the first stream you must extract
    * element 1 from the outermost tuple and then extract element 0 from the
    * inner tuple. If the accumulator is nothing but a StmBuild, then this
    * method should return Seq().
    */
  private def locateFirstInputStream(s: StmBuild): Option[Seq[Int]] =
    locateFirstStream(s.seed)

  private def locateFirstStream(e: Expr): Option[Seq[Int]] = {
    e match {
      case _: StmBuild      => Some(Seq())
      case Tuple(elems: _*) => locateFirstStream(elems, 0)
      case _                => None
    }
  }

  @tailrec
  private def locateFirstStream(elems: Seq[Expr], i: Int): Option[Seq[Int]] = {
    elems.headOption match {
      case None => None
      case Some(e) =>
        locateFirstStream(e) match {
          case Some(indices) => Some(i +: indices)
          case None          => locateFirstStream(elems.tail, i + 1)
        }
    }
  }

  private def extract(e: Expr, path: Seq[Int]): Expr = {
    (e, path) match {
      case (_, Seq())                 => e
      case (t: Tuple, Seq(i, is: _*)) => extract(t.elems(i), is)
      // Tuple(p.__0, p.__1) is equivalent to just p (if p is a 2-tuple)
      case (p: Param, Seq(i, is: _*)) => extract(TupleAccess(p, i), is)
      case _ => throw new IllegalArgumentException("Failed to extract.")
    }
  }

  private def matches(e: Expr, p: Param, path: Seq[Int]): Boolean = {
    e == path.foldLeft(p: Expr)((e, i) => TupleAccess(e, i))
  }

  private def replaceWith(e: Expr, path: Seq[Int], replacement: Expr): Tuple = {
    path match {
      case Seq() => Tuple()
      case Seq(i, is: _*) =>
        e match {
          case t: Tuple =>
            Tuple(
              t.elems.updated(i, replaceWith(t.elems(i), is, replacement)): _*
            )
          // TODO: Handle this case properly
          // Tuple(p.__0, p.__1) is equivalent to just p (if p is a 2-tuple)
          // However, it seems like we would need to know the type of p to know
          // how many elements to put, and we don't have that information in
          // the interpreter
          case p: Param => ???
          case _ => throw new IllegalArgumentException("Failed to replace.")
        }
    }
  }

  private def fuseFunctions(
      outerNextF: Function,
      innerNextF: Function,
      // Position of the inner stream within the outer accumulator
      path: Seq[Int]
  ): Function = {
    val acc = Param()
    Function(
      acc,
      fuseFunctionBodies(
        acc,
        outerNextF.param,
        outerNextF.body,
        innerNextF,
        path
      )
    )
  }

  private def fuseFunctionBodies(
      newAcc: Param,
      oldAcc: Param,
      body: Expr,
      innerNextF: Function,
      // Position of the inner stream within the outer accumulator
      path: Seq[Int]
  ): Expr = {
    val e = body match {
      case IfThenElse(cond, trueE, falseE) =>
        IfThenElse(
          cond,
          fuseFunctionBodies(newAcc, oldAcc, trueE, innerNextF, path),
          fuseFunctionBodies(newAcc, oldAcc, falseE, innerNextF, path)
        )
      case _: TupleAccess | _: VecAccess | _: FunCall => ???
      case Tuple(a, e, valid) =>
        extract(a, path) match {
          case TupleAccess(StmNext(s), IntCst(0)) if matches(s, oldAcc, path) =>
            // StmNext() called, so update the inner accumulator
            val innerNext = Param()
            Let(
              innerNext,
              FunCall(innerNextF, TupleAccess(newAcc, 1)),
              IfThenElse(
                TupleAccess(innerNext, 2),
                // Received next element from inner stream; proceed as planned
                Tuple(
                  Tuple(
                    replaceWith(a, path, Tuple()),
                    TupleAccess(innerNext, 0)
                  ), {
                    val t =
                      path.foldLeft(oldAcc: Expr)((e, i) => TupleAccess(e, i))
                    val original = TupleAccess(StmNext(t), 1)
                    substitute(e)(Map(original -> TupleAccess(innerNext, 1)))
                  },
                  valid
                ),
                // Inner stream did not yet produce element; don't update the
                // outer accumulator
                Tuple(
                  Tuple(TupleAccess(newAcc, 0), TupleAccess(innerNext, 0)), {
                    val t =
                      path.foldLeft(oldAcc: Expr)((e, i) => TupleAccess(e, i))
                    val original = TupleAccess(StmNext(t), 1)
                    substitute(e)(Map(original -> TupleAccess(innerNext, 1)))
                  },
                  False
                )
              )
            )
          case s if matches(s, oldAcc, path) =>
            // StmNext() not called, so leave the inner accumulator as-is
            Tuple(
              Tuple(
                replaceWith(a, path, Tuple()),
                TupleAccess(newAcc, 1)
              ),
              e,
              valid
            )
          case x =>
            throw new IllegalArgumentException(
              s"I can't tell whether or not StmNext() is being called in ${x}, with oldAcc = ${oldAcc} and path = ${path}"
            )
        }
      case _: IntExpr | _: BoolExpr | _: Param | _: VecBuild | _: StmBuild |
          _: StmNext | _: Function | _: Tuple =>
        throw new IllegalArgumentException(
          "Could not fuse function bodies due to an apparent type error."
        )
    }
    substitute(e)(Map(oldAcc -> TupleAccess(newAcc, 0)))
  }
}
