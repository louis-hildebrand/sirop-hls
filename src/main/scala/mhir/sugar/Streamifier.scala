package mhir.sugar

import mhir.gen.vhdl.VhdlGenerator
import mhir.ir.Lowering.ExprLowering
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

import scala.collection.immutable.ListMap

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
  implicit class Streamify(func: Expr) {
    def streamify(): Expr = {
      require(
        this.func.hasType,
        "Expression must be type-checked before it can be streamified."
      )
      val (inputList, stm) =
        VhdlGenerator.unwrapTopLevelFunction(this.func, rename = false)
      val oldToNewInputs = ListMap(
        inputList.map(x => x -> makeStreamParam(x)): _*
      )
      val newStm = stm match {
        case e if e.typ.isData =>
          streamifyScalar2Scalar(e, oldToNewInputs)
        case x: Param if oldToNewInputs.contains(x) =>
          wrapIdentity(x)
        case _ =>
          streamifyBody(stm, oldToNewInputs)
      }
      val f = rewrapTopLevelFunction(newStm, oldToNewInputs)
      assert(
        this.func.contains(classOf[SyntaxSugar])
          || !f.contains(classOf[SyntaxSugar]),
        s"streamification should not introduce syntax sugar if the original expression had none (found expression $f)"
      )
      f.tchk()
    }
  }

  private def rewrapTopLevelFunction(
      stm: Expr,
      oldToNewInputs: ListMap[Param, Param]
  ): Expr = {
    // Need to use LetStm for data inputs because the param may have multiple
    // consumers
    // Is it valid to use LetStm here? Yes. The shared stream has only one
    // element, so obviously the consumers will never read it out of order.
    val withLets = oldToNewInputs.foldRight(stm)({ case ((oldX, newX), acc) =>
      oldX.typ match {
        case TyData(_) =>
          LetStm(newX, newX, acc)()
        case t =>
          assert(t.isInstanceOf[TyStm])
          acc
      }
    })
    // Put back parameters
    oldToNewInputs.foldRight(withLets)({ case ((_, newX), acc) =>
      Function(newX, acc)()
    })
  }

  private def makeStreamParam(x: Param): Param = {
    x.typ match {
      case _: TyStm  => x
      case TyData(t) => x.rebuild(TyStm(t, 1)).asInstanceOf[Param]
      case t =>
        throw new IllegalArgumentException(
          s"Functions with inputs of type $t are not supported."
        )
    }
  }

  private def streamifyBody(
      stm: Expr,
      oldToNewInputs: ListMap[Param, Param]
  ): Expr = {
    require(stm.typ.isInstanceOf[TyStm])
    stm match {
      case x: Param if oldToNewInputs.contains(x) =>
        oldToNewInputs(x)
      case x: Param =>
        // Leave free variables as-is
        x
      case s: StmBuild =>
        streamifyStmBuild(s, oldToNewInputs)
      case LetStm(x, in, out) =>
        LetStm(
          x,
          streamifyBody(in, oldToNewInputs),
          streamifyBody(out, oldToNewInputs + (x -> x))
        )()
      case e =>
        throw new IllegalArgumentException(s"Cannot streamify expression $e.")
    }
  }

  private def streamifyScalar2Scalar(
      e: Expr,
      oldToNewInputs: ListMap[Param, Param]
  ): Expr = {
    val oldInputs = oldToNewInputs.keys.toSeq
    require(
      oldInputs.forall(x => x.typ.isData), {
        val inputsStr =
          oldInputs.map(x => s"${x.name}:${x.typ}").mkString(", ")
        s"Cannot streamify function with data output type ${e.typ} but non-data inputs $inputsStr."
      }
    )
    val newAccumulators = oldInputs
      .map(x => x -> x.freshCopy.rebuild(TyStm(x.typ, -1)).asInstanceOf[Param])
      .toMap
    val subs: Map[Expr, Expr] =
      oldInputs.map(x => x -> StmData(newAccumulators(x))().tchk()).toMap
    StmBuild(
      C(1)(),
      e.subPreserveType(subs),
      True,
      oldInputs
        .map(x => newAccumulators(x) -> (oldToNewInputs(x), True))
        .toMap
    )()
  }

  private def streamifyStmBuild(
      originalStm: StmBuild,
      oldToNewInputs: ListMap[Param, Param]
  ): Expr = {
    val withStreamifiedProducers =
      StmBuild(
        originalStm.n,
        originalStm.data,
        originalStm.valid,
        originalStm.equations.map({ case (x, (z, next)) =>
          x.typ match {
            case TyData(_) =>
              x -> (z, next)
            case _: TyStm =>
              x -> (streamifyBody(z, oldToNewInputs), next)
          }
        })
      )().tchk().asInstanceOf[StmBuild]
    val oldInputs = oldToNewInputs.keys.toSeq
    if (oldInputs.forall(_.typ.isInstanceOf[TyStm])) {
      // TODO: Would it be more accurate to find which data inputs are used
      //       directly in this StmBuild (as opposed to in a producer) and do
      //       nothing in case there are no such inputs?
      // Inputs are already streams, so nothing to do
      originalStm
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

      // For each input that is data rather than a stream, define:
      // (1) new input param whose type is a stream rather than data
      //     - this is already provided as input
      // (2) new accumulator for the new input stream
      // (3) new accumulator to hold the value from the new input stream once
      //     it's been read
      val (newStmAcc, newRegAcc) = {
        val (a, b) = oldInputs
          .flatMap(oldX =>
            if (oldX.typ.isData) {
              val s = Param(s"${oldX.prefix}_stm")(TyStm(oldX.typ, -1))
              val reg = Param(s"${oldX.prefix}_data")(oldX.typ)
              Some((oldX -> s, oldX -> reg))
            } else {
              None
            }
          )
          .unzip
        (a.toMap, b.toMap)
      }

      val isFirstStep = Param("is_first_step")(TyBool)
      val subs = oldInputs
        .flatMap(x =>
          if (x.typ.isData) {
            val mux = Mux(isFirstStep, StmData(newStmAcc(x))(), newRegAcc(x))()
            Some(x -> mux.tchk())
          } else None
        )
        .toMap[Expr, Expr]
      val newData = withStreamifiedProducers.data.subPreserveType(subs)
      val newValid =
        !isFirstStep && withStreamifiedProducers.valid.subPreserveType(subs)
      val newEquations = {
        val equationsToAdd = (oldInputs
          .flatMap(x =>
            if (x.typ.isData) {
              Seq(
                newStmAcc(x) -> (oldToNewInputs(x), isFirstStep),
                newRegAcc(x) -> (
                  Default(x.typ).tchk().lower(),
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
        val updatedOldEquations =
          withStreamifiedProducers.equations.map({ case (x, (z, next)) =>
            // TODO: Only make the seed default[T] if it actually depends on at
            //       least one input?
            if (x.typ.isData) {
              val newNext = Mux(
                isFirstStep,
                z.subPreserveType(subs),
                next.subPreserveType(subs)
              )()
              x -> (Default(x.typ).tchk().lower(), newNext)
            } else {
              x -> (z, Mux(isFirstStep, False, next.subPreserveType(subs))())
            }
          })
        updatedOldEquations ++ equationsToAdd
      }
      StmBuild(withStreamifiedProducers.n, newData, newValid, newEquations)()
    }
  }

  private def wrapIdentity(x: Param): Expr = {
    // e.g., (s : Stm[T, n]) => s
    // e.g., let s = ... in s
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
    StmBuild(
      typ.n,
      StmData(y)(),
      True,
      Map[Param, (Expr, Expr)](
        y -> (x, True)
      )
    )()
  }
}
