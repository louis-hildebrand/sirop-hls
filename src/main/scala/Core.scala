import scala.language.implicitConversions

sealed abstract class Expr {
  def +(that: Expr): Add = Add(this, that)
  def -(that: Expr): Sub = Sub(this, that)
  def *(that: Expr): Mul = Mul(this, that)
  def /(that: Expr): Div = Div(this, that)
  def %(that: Expr): Mod = Mod(this, that)
  def eq(that: Expr): Equal = Equal(this, that)
  def ne(that: Expr): NotEqual = NotEqual(this, that)
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
case class Function(param: Param, body: Expr) extends Expr
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
case class IfThenElse(cond: Expr, trueE: Expr, falseE: Expr) extends Expr
case class Equal(e1: Expr, e2: Expr) extends BoolExpr
case class NotEqual(e1: Expr, e2: Expr) extends BoolExpr
case class LessThan(e1: Expr, e2: Expr) extends BoolExpr

// High-level function
case class Iterate(
    n: Expr /* Int */,
    z: Expr /* A */,
    f: Function /* A -> A */
) extends Expr

// Streams
case class StmBuild(
    length: Expr /* Int */,
    seed: Expr /*A*/,
    nextF: Function /* A -> (A,B)*/,
    id: String,
    index: Int
) extends Expr {
  StmBuild.stmTable += (id, index) -> false
}
object StmBuild {
  var stmTable = Map[(String, Int), Boolean]()

  def takenIds: Set[String] = stmTable.keySet.map((id, idx) => id)

  def freshId(prefix: String): String = {
    var n = 0
    while (true) {
      var id = s"${prefix}_${n}"
      if !takenIds.contains(id) then {
        return id
      }
      n += 1
    }
    // Unreachable
    ""
  }
}
case class StmLength(stream: Expr) extends IntExpr
case class StmNext(stream: Expr /* Stream<A>*/ ) /* (Stream<A>, A) */
    extends Expr // element only available for one clock cycle

// Vectors
case class VecBuild(len: Expr, f: Function /*Int => Expr*/ ) extends Expr
case class VecAccess(vec: Expr, i: Expr) extends Expr
case class VecLength(vec: Expr) extends IntExpr

object ExprEvaluator {

  private def substitute(
      e: Expr
  )(implicit substitutions: Map[Param, Expr]): Expr = {
    e match {
      case t: Tuple => Tuple(t.elems.toSeq.map(substitute(_)): _*)
      case TupleAccess(t: Expr, i: Expr) =>
        TupleAccess(substitute(t), substitute(i))

      case p: Param =>
        substitutions.get(p) match {
          case Some(v) => v
          case None    => p
        }
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
      case Equal(e1: Expr, e2: Expr) => Equal(substitute(e1), substitute(e2))
      case LessThan(e1: Expr, e2: Expr) =>
        LessThan(substitute(e1), substitute(e2))

      case Iterate(n: Expr, z: Expr, f: Function) =>
        Iterate(
          substitute(n),
          substitute(z),
          substitute(f).asInstanceOf[Function]
        )

      case s @ StmBuild(length, seed, f, id, index) =>
        StmBuild(
          substitute(length),
          substitute(seed),
          substitute(f).asInstanceOf[Function],
          id = id,
          index = index
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

  def partialEval(
      e: Expr
  )(implicit substitutions: Map[Param, Expr] = Map()): Expr = {
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
          case (e1: IntCst, e2: IntCst) => e1.i + e2.i
          case (e1 @ _, e2 @ _)         => Add(e1, e2)
        }
      case Sub(e1: Expr, e2: Expr) =>
        (partialEval(e1), partialEval(e2)) match {
          case (e1: IntCst, e2: IntCst) => e1.i - e2.i
          case (e1 @ _, e2 @ _)         => Sub(e1, e2)
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

      case Iterate(n: Expr, z: Expr, f: Function) => {
        partialEval(n) match {
          case n: IntCst => {
            val n_int: Int = n.i
            assert(n_int > -1)

            val z_interpreted = partialEval(z)
            if (n_int == 0)
              z_interpreted
            else
              partialEval(Iterate(n_int - 1, FunCall(f, z_interpreted), f))
          }
          case n @ _ => Iterate(n, partialEval(z), f)
        }
      }

      case s @ StmBuild(length, seed, f, id, index) =>
        StmBuild(
          partialEval(length),
          partialEval(seed),
          partialEval(f).asInstanceOf[
            Function
          ], /* ensures any free Param in f gets substituted */
          id = id,
          index = index
        )

      case StmLength(s) =>
        partialEval(s) match {
          case s: StmBuild => partialEval(s.length)
          case s @ _       => StmLength(s)
        }

      case StmNext(s: Expr) =>
        partialEval(s) match {
          case s: StmBuild =>
            assert(
              !StmBuild.stmTable.contains((s.id, s.index)),
              s"Attempt to call StmNext() multiple times on the same stream [id='${s.id}', index='${s.index}']."
            )
            StmBuild.stmTable += (s.id, s.index) -> true
            partialEval(s.length) match {
              case len: IntCst => {
                assert(
                  len.i > 0,
                  "Attempt to call StmNext() on a stream of length 0."
                )
                partialEval(FunCall(s.nextF, s.seed)) match {
                  case next: Tuple => {
                    // return the new stream and the next element
                    Tuple(
                      StmBuild(
                        partialEval(s.length + -1),
                        partialEval(next.__0),
                        // this function may have free parameters
                        partialEval(s.nextF).asInstanceOf[Function],
                        id = s.id,
                        index = s.index + 1
                      ),
                      partialEval(next.__1)
                    )
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

}
