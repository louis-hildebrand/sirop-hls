package mhir.sem

import mhir.ir._

import scala.annotation.tailrec

object SemanticAnalyzer {

  def check(prog: Program): Unit = {
    val (inputs, _) = unwrapTopLevelFunction(prog.e)

    val duplicateInputs =
      inputs.groupBy(x => x).collect({ case (x, Seq(_, _, _*)) => x }).toSeq
    if (duplicateInputs.nonEmpty) {
      val paramList = duplicateInputs.map(_.name).sorted.mkString(", ")
      val paramOrParams =
        if (paramList.length == 1) "parameter" else "parameters"
      throw SemanticError(
        s"duplicate $paramOrParams in top-level function: $paramList"
      )
    }

    prog.outName match {
      case Some(name) if inputs.exists(x => x.name == name) =>
        throw SemanticError(s"output name '$name' is already used for an input")
      case _ => ()
    }
  }

  def unwrapTopLevelFunction(f: Expr): (Seq[Param], Expr) = {
    @tailrec
    def unwrap(e: Expr, inputs: Seq[Param]): (Seq[Param], Expr) = {
      e match {
        case Function(x, e) if x.typ == TyTuple() =>
          // TODO: Why is this case needed again?
          unwrap(e, inputs)
        case Function(x, e) => unwrap(e, x +: inputs)
        case e              => (inputs, e)
      }
    }
    val (inputs, body) = unwrap(f, Seq())
    (inputs.reverse, body)
  }
}
