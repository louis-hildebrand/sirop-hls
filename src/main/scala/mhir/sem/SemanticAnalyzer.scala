package mhir.sem

import mhir.ir._

import scala.annotation.tailrec

object SemanticAnalyzer {

  /** Check for errors like duplicate input names, collisions between the input
    * and output names, etc.
    *
    * This check can be run before lowering.
    */
  def checkNames(prog: Program): Unit = {
    val (inputs, _) = unwrapTopLevelFunction(prog.body)

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

  /** Check for semantic issues other than those covered by [[checkNames]]
    * (e.g., that the conditions for the no_handshake annotation are met).
    *
    * This cannot be run before lowering.
    */
  def check(prog: Program): Unit = {
    require(
      !prog.body.hasSyntaxSugar,
      "semantic analysis cannot be run before lowering"
    )

    if (!prog.handshake) {
      checkNoHandshake(prog.body)
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

  private def checkNoHandshake(e: Expr): Unit = {
    e match {
      case s: StmBuild =>
        if (s.valid != True) {
          val name = s.nameAnnotation.getOrElse("(unknown name)")
          throw SemanticError(
            s"stream operator $name cannot be used without the handshake protocol:" +
              s" its output is not always valid"
          )
        }
        for (eqn <- s.equations) {
          eqn match {
            case (x, (p, ready)) if x.typ.isInstanceOf[TyStm] =>
              if (ready != True) {
                val name = s.nameAnnotation.getOrElse("(unknown name)")
                throw SemanticError(
                  s"stream operator $name cannot be used without the handshake protocol:"
                    + " it is not always ready to receive input"
                )
              }
              checkNoHandshake(p)
            case _ => ()
          }
        }
      case e =>
        for (child <- e.children) {
          checkNoHandshake(child)
        }
    }
  }
}
