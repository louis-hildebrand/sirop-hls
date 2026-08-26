package mhir.sem

import mhir.ir._
import mhir.typecheck.{NameError, TypeChecker}

object SemanticAnalyzer {

  /** Check for errors like duplicate input names, collisions between the input
    * and output names, etc.
    *
    * This check can be run before lowering.
    */
  def checkNames(prog: Program): Program = {
    val (inputs, _) = TypeChecker.unwrapTopLevelFunction(prog.body)

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

    for (((key, x), _) <- prog.accel.annotationsByParam) {
      if (!inputs.contains(x)) {
        throw NameError(
          s"unknown input '$x' (in annotation key '$key($x)')"
        )
      }
    }

    prog.go match {
      case Some(go) =>
        inputs.find(_ == go) match {
          case Some(go) =>
            // Ensure the type annotation is correct in prog.go
            go.typ match {
              case TyStm(TyBool, _) =>
                prog.copy(accel =
                  prog.accel.copy(annotations =
                    prog.accel.annotations + ("go" -> go)
                  )
                )
              case typ =>
                throw SemanticError(
                  "invalid value for annotation 'go'."
                    + s" Expected the name of a stream of booleans, but found $typ."
                )
            }
          case None =>
            val availableInputs = if (inputs.isEmpty) {
              ""
            } else {
              inputs.map(x => s"'$x'").mkString(" (e.g., ", ", ", ")")
            }
            throw SemanticError(
              s"invalid value for annotation 'go': '$go'."
                + s" Expected the name of one of the accelerator inputs$availableInputs."
            )
        }
      case None => prog
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
      checkDelays(prog.body)
    }
    checkStmData(prog.body)
  }

  /** Check that the `valid` and `ready` expressions are always `true`.
    */
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
        for ((_, (p, ready, _)) <- s.producers) {
          if (ready != True) {
            val name = s.nameAnnotation.getOrElse("(unknown name)")
            throw SemanticError(
              s"stream operator $name cannot be used without the handshake protocol:"
                + " it is not always ready to receive input"
            )
          }
          checkNoHandshake(p)
        }
      case e => e.children.foreach(checkNoHandshake)
    }
  }

  private def checkDelays(e: Expr): Unit = {
    e match {
      case s: StmBuild =>
        if (!s.delay.typ.isInstanceOf[TyUInt]) {
          assert(
            s.delay.typ.isInstanceOf[TyUInt] ||
              s.delay.typ.isInstanceOf[TyTuple],
            s"delay should be an unsigned int or (), but found ${s.delay.typ}"
          )
          val name = s.nameAnnotation.getOrElse("(unknown name)")
          throw SemanticError(
            s"missing output delay for stream operator $name"
              + s" (the delay must be specified when the handshake protocol is disabled)"
          )
        }
        for ((x, (_, _, delay)) <- s.producers) {
          assert(
            delay.typ.isInstanceOf[TyUInt] ||
              delay.typ.isInstanceOf[TyTuple],
            s"delay should be an unsigned int or (), but found ${delay.typ}"
          )
          if (!delay.typ.isInstanceOf[TyUInt]) {
            val name = s.nameAnnotation.getOrElse("(unknown name)")
            throw SemanticError(
              s"missing delay for producer $x in stream operator $name"
                + s" (the delay must be specified when the handshake protocol is disabled)"
            )
          }
        }
      case e => e.children.foreach(checkDelays)
    }
  }

  private def checkStmData(e: Expr): Unit = {
    e match {
      case s: StmBuild =>
        for ((x, (stm, ready, _)) <- s.producers) {
          checkStmData(stm)
          if (ready.contains(classOf[StmData])) {
            val name = s.nameAnnotation.getOrElse("sbuild")
            throw SemanticError(
              s"sdata is used in 'ready' expression of producer $x in $name"
            )
          }
        }
      case sdata: StmData =>
        throw SemanticError(s"$sdata is used outside sbuild")
      case e => e.children.foreach(checkStmData)
    }
  }
}
