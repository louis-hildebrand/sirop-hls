package mhir.gen.vhdl
package transform

import com.typesafe.scalalogging.Logger
import mhir.gen.vhdl.{FlatPipeline, VhdlGeneratorOptions}
import mhir.logging.time
import mhir.optimize.cost.SimpleDelayCostModel
import mhir.optimize.{EnabledBinOpTreeBalancingPass, StmOutputScheduler}
import org.slf4j.event.Level

/** Apply all the transformations needed before we can straightforwardly emit
  * VHDL: insert bounds checking for array accesses, insert intermediates
  * values, etc.
  */
object ApplyTransformations {

  private implicit val logger: Logger = Logger(getClass.getName)

  def apply(pipe: FlatPipeline, options: VhdlGeneratorOptions): FlatPipeline = {
    val pipe1 = time("looking for VecWrite", Level.DEBUG) {
      pipe.mapSbuilds(ClassifyVecAccumulators.apply)
    }
    val pipe2 = time("adding vector bounds checks", Level.DEBUG) {
      pipe1.mapSbuilds(BoundsCheckInsertion.apply)
    }
    val pipe3 = time("inserting intermediate variables", Level.DEBUG) {
      pipe2.mapSbuilds(IntermediateInsertion.apply)
    }
    val pipe4 = time("making all function arguments explicit", Level.DEBUG) {
      pipe3.mapSbuilds(MakeFreeVarsIntoParams.apply)
    }
    val pipe5 = options.deviceFamily match {
      case "Agilex 7" =>
        time(s"mapping multiplications to Agilex 7 DSP blocks", Level.DEBUG) {
          val pass = agilex7.DspSelection(
            StmOutputScheduler(
              EnabledBinOpTreeBalancingPass,
              SimpleDelayCostModel(madd = true)
            )
          )
          pipe4.mapSbuilds(pass.apply)
        }
      case family =>
        logger.debug(
          s"DSP selection is not currently implemented for FPGA family '$family'"
        )
        pipe4
    }
    pipe5
  }
}
