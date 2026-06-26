package mhir.sugar

import com.typesafe.scalalogging.Logger
import mhir.ir._
import mhir.logging.time
import mhir.sem.{SemanticAnalyzer, SemanticError}
import mhir.typecheck.{TypeCheck, TypeChecker}
import org.slf4j.event.Level

import scala.annotation.tailrec
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
  *   e.streamify
  * }}}
  */
object Streamifier {

  private implicit val logger: Logger = Logger(getClass.getName)

  implicit class Streamify(func: Expr) {
    def streamify(implicit c: Canonicalizer): Expr = {
      require(
        this.func.hasType,
        "Expression must be type-checked before it can be streamified."
      )
      require(
        !this.func.hasSyntaxSugar,
        "Expression must be lowered before it can be streamified."
      )
      time("streamifying", Level.TRACE) {
        logger.trace(s"streamifying expression: ${this.func}")
        val (inputList, stm) =
          TypeChecker.unwrapTopLevelFunction(this.func)
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
          this.func.hasSyntaxSugar
            || !f.hasSyntaxSugar,
          s"streamification should not introduce syntax sugar if the original expression had none (found expression $f)"
        )
        val result = f.tchk()
        result
      }
    }
  }

  private def rewrapTopLevelFunction(
      stm: Expr,
      oldToNewInputs: ListMap[Param, Param]
  ): Expr = {
    // Need to use LetStm for data inputs because the param may have multiple
    // consumers
    val withLets = oldToNewInputs.foldRight(stm)({ case ((oldX, newX), acc) =>
      oldX.typ match {
        case TyData(_) =>
          assert(newX.typ.asInstanceOf[TyStm].n == C(1)())
          LetStm(1, newX, newX, acc)()
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

  private def makeStreamParam(x: Param)(implicit c: Canonicalizer): Param = {
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
  )(implicit c: Canonicalizer): Expr = {
    require(stm.typ.isInstanceOf[TyStm])
    if (stm.typ.freeVars.intersect(oldToNewInputs.keySet).nonEmpty) {
      throw new IllegalArgumentException(
        "Types cannot depend on any inputs."
          ++ s" (Found type ${stm.typ})"
      )
    }
    stm match {
      case x: Param if oldToNewInputs.contains(x) =>
        oldToNewInputs(x)
      case x: Param =>
        // Leave free variables as-is
        x
      case s: StmLiteral =>
        streamifyStmBuild(s.toStmBuild, oldToNewInputs)
      case s: StmBuild =>
        streamifyStmBuild(s, oldToNewInputs)
      case LetStm(bufSize, x, in, out) =>
        LetStm(
          bufSize,
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
  )(implicit c: Canonicalizer): Expr = {
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
    )().annotate(NoInputsAfterLastOut).annotateWithName("scalar2scalar")
  }

  private def streamifyStmBuild(
      originalStm: StmBuild,
      oldToNewInputs: ListMap[Param, Param]
  )(implicit c: Canonicalizer): Expr = {
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
      )(annotations = originalStm.annotations).tchk().asInstanceOf[StmBuild]
    val oldInputs = findDataInputsUsedHere(
      withStreamifiedProducers,
      oldToNewInputs.keySet
    )
    if (oldInputs.isEmpty) {
      withStreamifiedProducers
    } else {
      // There are some inputs which are not streams but are used in this
      // StmBuild.
      // The data input can be used in the seeds of accumulators, in the `next`
      // expressions for accumulators, in the `data` expression, or in the
      // `valid` expression.
      // The input may need to be read across multiple cycles (e.g., consider
      // something like StmMap(s, x => StmCst(5, x)) ).
      // Replace each such input with a stream.
      // In the first step:
      //   * Read from that new input stream and save the value
      //     into a register.
      //   * Uses of the data input must be replaced by reading from the input
      //     stream.
      //   * Suppose an accumulator's seed depends on the data input.
      //     Wherever that accumulator is used in the first step, you must
      //     instead put the accumulator's seed, but with the use of the data
      //     input replaced by reading from the stream.
      // In later steps:
      //   * Uses of the data input must be replaced by reading from the
      //     register.

      // For each input that is data rather than a stream, define:
      // (1) new input param whose type is a stream rather than data
      //     - this is already provided as input
      // (2) new accumulator for the new input stream
      // (3) new accumulator to hold the value from the new input stream once
      //     it's been read
      val (newStmAcc, newRegAcc) = {
        val (a, b) = oldInputs
          .map(oldX => {
            assert(oldX.typ.isData)
            val s = Param(s"${oldX.prefix}_stm")(TyStm(oldX.typ, -1))
            val reg = Param(s"${oldX.prefix}_data")(oldX.typ)
            (oldX -> s, oldX -> reg)
          })
          .unzip
        (a.toMap, b.toMap)
      }

      // Generally, we want to avoid introducing unnecessary latency.
      // Therefore, in the first cycle, we still continue as usual, except that
      // in some places you'll need to read from a new stream.
      // However, the `ready` expression for input streams cannot depend on
      // StmData!
      // Therefore, if any `ready` expression depends (directly or indirectly)
      // on a data input, we must halt everything on the first step so that the
      // data input can be loaded into a register.
      // This introduces one extra cycle of latency, which can be problematic if
      // the expression being streamified is used inside something like StmMap:
      // the extra latency will apply *for each iteration*.
      val haltOnFirstStep = {
        val forbiddenVars =
          oldInputs ++ withStreamifiedProducers.seedByVar
            .filter({ case (_, z) => z.freeVars.intersect(oldInputs).nonEmpty })
            .map({ case (x, _) => x })
        val halt = withStreamifiedProducers.nextByVar.exists({
          case (x, ready) if x.typ.isInstanceOf[TyStm] =>
            ready.freeVars.intersect(forbiddenVars).nonEmpty
          case _ => false
        })
        if (halt) {
          logger.warn(
            "streamification must introduce extra latency"
              + " because the `ready` expression for an input stream depends (directly or indirectly) on a data input"
          )
        }
        halt
      }
      val isFirstStep = Param("is_first_step")(TyBool)
      val subs = {
        val oldInputSubs = oldInputs
          .map(x => {
            assert(x.typ.isData)
            val mux = Mux(isFirstStep, StmData(newStmAcc(x))(), newRegAcc(x))()
            x -> mux.tchk()
          })
          .toMap[Expr, Expr]
        val oldAccSubs = if (haltOnFirstStep) {
          Map()
        } else {
          withStreamifiedProducers.seedByVar
            .flatMap({
              case (x, z) if x.typ.isData =>
                // TODO: Only do this if the seed actually depends on at least
                //       one input?
                val mux = Mux(isFirstStep, z.subPreserveType(oldInputSubs), x)()
                Some(x -> mux.tchk())
              case _ => None
            })
        }
        oldInputSubs ++ oldAccSubs
      }
      val newData = withStreamifiedProducers.data.subPreserveType(subs)
      val newValid = if (haltOnFirstStep) {
        withStreamifiedProducers.valid.subPreserveType(subs) && !isFirstStep
      } else {
        withStreamifiedProducers.valid.subPreserveType(subs)
      }
      val newEquations = {
        val equationsToAdd = (oldInputs
          .flatMap(x => {
            assert(x.typ.isData)
            val stmData = StmData(newStmAcc(x))()
            Seq(
              newStmAcc(x) -> (oldToNewInputs(x), isFirstStep),
              newRegAcc(x) -> (
                Undefined(x.typ),
                Mux(isFirstStep, stmData, newRegAcc(x))()
              )
            )
          })
          .toMap
          + (isFirstStep -> (True, False)))
        val updatedOldEquations =
          withStreamifiedProducers.equations.map({
            case (x, (z, next)) if x.typ.isData =>
              // TODO: Only make the seed undefined if it actually depends on at
              //       least one input?
              val newSeed = Undefined(x.typ).lower
              val newNext = if (haltOnFirstStep) {
                Mux(
                  isFirstStep,
                  z.subPreserveType(subs),
                  next.subPreserveType(subs)
                )()
              } else {
                next.subPreserveType(subs)
              }
              x -> (newSeed, newNext)
            case (x, (z, ready)) =>
              val newReady = if (haltOnFirstStep) {
                val subsFromRegisters = oldInputs
                  .map(x => x -> newRegAcc(x))
                  .toMap[Expr, Expr]
                ready.subPreserveType(subsFromRegisters) && !isFirstStep
              } else {
                ready.subPreserveType(subs)
              }
              assert(
                !newReady.contains(classOf[StmData]),
                "the ready signal cannot use StmData"
              )
              x -> (z, newReady)
          })
        updatedOldEquations ++ equationsToAdd
      }
      StmBuild(withStreamifiedProducers.n, newData, newValid, newEquations)(
        annotations = withStreamifiedProducers.annotations
      )
    }
  }

  private def findDataInputsUsedHere(
      stm: StmBuild,
      inputs: Set[Param]
  ): Set[Param] = {
    // Ignore producer streams
    val withoutProducers = StmBuild(
      n = stm.n,
      data = stm.data,
      valid = stm.valid,
      equations = stm.equations.map({ case (y, (z, next)) =>
        if (y.typ.isInstanceOf[TyStm]) {
          y -> (Param("ignore")(z.typ), next)
        } else {
          y -> (z, next)
        }
      })
    )()
    inputs
      .filter(_.typ.isData)
      .intersect(withoutProducers.freeVars)
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
    )().annotate(NoInputsAfterLastOut).annotateWithName("Identity")
  }
}
