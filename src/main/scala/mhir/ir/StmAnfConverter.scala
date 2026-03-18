package mhir.ir

import mhir.ir.typecheck.TypeCheck

private sealed trait Binding
private case class LetInlineBinding(x: Param, e: Expr) extends Binding
private case class LetSharedBinding(bufSize: Expr, x: Param, e: Expr)
    extends Binding

object StmAnfConverter {
  def convert(e: Expr): Expr = {
    require(e.hasType)
    require(!e.hasSyntaxSugar)
    convertAndMakeLets(e)
  }

  private def convertAndMakeLets(e: Expr): Expr = {
    val (result, bindings) = convertWithBindings(e)
    val resultWithLets = bindings.foldRight(result)({
      case (LetInlineBinding(x, v), acc) =>
        LetInlineStm(x, v, acc)()
      case (LetSharedBinding(bufSize, x, v), acc) =>
        LetStm(bufSize, x, v, acc)()
    })
    val checkedResult = resultWithLets.tchk()
    assert(checkedResult.typ ~= e.typ)
    checkedResult
  }

  private def convertWithBindings(e: Expr): (Expr, Seq[Binding]) = {
    e match {
      case s: StmBuild =>
        var newEquations = Map[Param, (Expr, Expr)]()
        var newBindings = Seq[Binding]()
        for (eqn <- s.equations) {
          eqn match {
            case (x, (p, ready)) if x.typ.isInstanceOf[TyStm] =>
              val (newProducer, bindings) = convertWithBindings(p)
              newBindings ++= bindings
              newEquations += x -> (newProducer, ready)
            case (x, (z, next)) =>
              newEquations += x -> (z, next)
          }
        }
        val newStm = StmBuild(
          s.n,
          s.data,
          s.valid,
          newEquations
        )(annotations = s.annotations).tchk()
        val x = Param("s")(newStm.typ)
        (x, newBindings :+ LetInlineBinding(x, newStm))
      case LetStm(bufSize, x, in, out) =>
        val (newIn, inBindings) = convertWithBindings(in)
        val (newOut, outBindings) = convertWithBindings(out)
        val allBindings =
          (inBindings
            ++ Seq(LetSharedBinding(bufSize, x, newIn))
            ++ outBindings)
        (newOut, allBindings)
      case e =>
        (e, Seq())
    }
  }
}
