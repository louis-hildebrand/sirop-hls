package mhir.sugar

import mhir.ir.StreamFuser.StreamFusion
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

import scala.annotation.tailrec

/** Transformation for converting a non-streaming program to a streaming
  * program.
  *
  * To apply the transformation, use the extension method
  * [[Streamifier.Streamify.streamify]]. The implicit class
  * [[Streamifier.Streamify]] must be in scope.
  *
  * @example
  *
  * {{{
  *   import mhir.ir.sugar.Streamifier.Streamify
  *   e.streamify()
  * }}}
  */
object Streamifier {
  implicit class Streamify(expr: Expr) {
    def streamify(): Expr = {
      require(
        this.expr.hasType,
        "Expression must be type-checked before it can be streamified."
      )
      val f = Streamifier.streamify(this.expr, Seq())
      f.tchk()
    }
  }

  @tailrec
  private def streamify(e: Expr, inputs: Seq[Param]): Expr = {
    e match {
      case Function(x, body) =>
        x.typ match {
          case TyData(_) | _: TyStm => ()
          case t =>
            throw new IllegalArgumentException(
              s"Functions with inputs of type $t are not supported."
            )
        }
        streamify(body, inputs :+ x)
      case e if e.typ.isData =>
        streamifyScalar2Scalar(e, inputs)
      case s: StmBuild =>
        streamifyInputs(s, inputs)
      case x: Param if inputs.contains(x) =>
        wrapIdentity(x, inputs)
      case e =>
        throw new IllegalArgumentException(s"Cannot streamify expression $e.")
    }
  }

  private def streamifyScalar2Scalar(e: Expr, inputs: Seq[Param]): Expr = {
    require(
      inputs.forall(x => x.typ.isData), {
        val inputsStr =
          inputs.map(x => s"${x.name}:${x.typ}").mkString(", ")
        s"Cannot streamify function with data output type ${e.typ} but non-data inputs $inputsStr."
      }
    )
    val newInputs = inputs
      .map(x => x -> x.freshCopy.rebuild(TyStm(x.typ, 1)).asInstanceOf[Param])
      .toMap
    val newAccumulators = inputs
      .map(x => x -> x.freshCopy.rebuild(TyStm(x.typ, -1)).asInstanceOf[Param])
      .toMap
    val subs: Map[Expr, Expr] =
      inputs.map(x => x -> StmData(newAccumulators(x))().tchk()).toMap
    val stm = StmBuild(
      C(1)(),
      e.subPreserveType(subs),
      True,
      inputs
        .map(x => newAccumulators(x) -> (newInputs(x), True))
        .toMap
    )()
    inputs.foldRight(stm: Expr)({ case (x, acc) =>
      Function(newInputs(x), acc)()
    })
  }

  private def streamifyInputs(
      originalStm: StmBuild,
      inputs: Seq[Param]
  ): Expr = {
    if (inputs.forall(x => x.typ.isInstanceOf[TyStm])) {
      // Inputs are already streams, so nothing to do
      inputs.foldRight(originalStm: Expr)({ case (x, acc) =>
        Function(x, acc)()
      })
    } else {
      // scalar -> stream (e.g., c => StmCst(n, c), c => StmCountFrom(n, c))
      // The scalar input to the original function (`f.param`) can appear in
      // the seed of the stream (as in StmCountFrom), in the `nextF` (as in
      // StmCst), or even both (if you have something weird like a
      // StmBuild that constructs StmCountFrom(n, c) and StmCst(n, c) but
      // zipped together).
      //
      // Replace each such input with a stream.
      // In the first cycle, halt everything else, read from that new input
      // stream and save the value into a register.
      // Also use the default value for the seeds of the existing accumulators
      // and initialize them in the first step, since the seed may depend on
      // the input.
      //
      // Try to get rid of input streams by fusion in case they also depend on
      // those inputs.
      // TODO: Is there a better way than fusion?
      val stm = originalStm.fuseCompletely()

      // For each input that is data rather than a stream, define:
      // (1) new input param (a stream rather than data)
      // (2) new accumulator for the new input stream
      // (3) new accumulator to hold the value from the new input stream once
      //     it's been read
      val (newInput, newStmAcc, newRegAcc) = {
        val (a, b, c) = inputs
          .flatMap(x =>
            if (x.typ.isData) {
              val in = x.freshCopy.rebuild(TyStm(x.typ, 1)).asInstanceOf[Param]
              val s = Param(s"${x.prefix}_stm")(TyStm(x.typ, -1))
              val reg = Param(s"${x.prefix}_data")(x.typ)
              Some((x -> in, x -> s, x -> reg))
            } else {
              None
            }
          )
          .unzip3
        (a.toMap, b.toMap, c.toMap)
      }

      val isFirstStep = Param("is_first_step")(TyBool)
      val subs = inputs
        .flatMap(x =>
          if (x.typ.isData) {
            val mux = Mux(isFirstStep, StmData(newStmAcc(x))(), newRegAcc(x))()
            Some(x -> mux.tchk())
          } else None
        )
        .toMap[Expr, Expr]
      val newData = stm.data.subPreserveType(subs)
      val newValid = !isFirstStep && stm.valid.subPreserveType(subs)
      val newEquations = {
        val equationsToAdd = (inputs
          .flatMap(x =>
            if (x.typ.isData) {
              Seq(
                newStmAcc(x) -> (newInput(x), isFirstStep),
                newRegAcc(x) -> (
                  Default(x.typ),
                  Mux(
                    isFirstStep,
                    StmData(newStmAcc(x))(),
                    newRegAcc(x)
                  )()
                )
              )
            } else {
              Seq()
            }
          )
          .toMap
          + (isFirstStep -> (True, False)))
        val updatedOldEquations = stm.equations.map({ case (x, (z, next)) =>
          // TODO: Only make the seed default[T] if it actually depends on at
          //       least one input?
          if (x.typ.isData) {
            val newNext = Mux(
              isFirstStep,
              z.subPreserveType(subs),
              next.subPreserveType(subs)
            )()
            x -> (Default(x.typ), newNext)
          } else {
            x -> (z, Mux(isFirstStep, False, next.subPreserveType(subs))())
          }
        })
        updatedOldEquations ++ equationsToAdd
      }
      val newStm = StmBuild(stm.n, newData, newValid, newEquations)()
      inputs.foldRight(newStm: Expr)({ case (x, acc) =>
        Function(newInput.getOrElse(x, x), acc)()
      })
    }
  }

  private def wrapIdentity(x: Param, inputs: Seq[Param]): Expr = {
    require(inputs.contains(x))
    // e.g., (s : Stm[T, n]) => s
    // The hardware generator needs the body of the function to be a
    // StmBuild.
    // Similarly, the lowering passes for StmMap and StmScanInclusive
    // both expect the body of the function to be a StmBuild so that they
    // can reset the accumulators from time to time.
    assert(
      x.typ.isInstanceOf[TyStm],
      // It certainly can't be data, since that's handled in an earlier
      // match case
      "input should be a stream"
    )
    val typ = x.typ.asInstanceOf[TyStm]
    val y = x.freshCopy
    val stm = StmBuild(
      typ.n,
      StmData(y)(),
      True,
      Map[Param, (Expr, Expr)](
        y -> (x, True)
      )
    )()
    inputs.foldRight(stm: Expr)({ case (x, acc) => Function(x, acc)() })
  }
}
