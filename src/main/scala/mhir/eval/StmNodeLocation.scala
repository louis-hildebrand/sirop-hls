package mhir.eval

/** Where a given node in the pipeline is located: inside the main accelerator,
  * or external output.
  */
sealed trait StmNodeLocation

/** The node is part of the main accelerator.
  */
object InMain extends StmNodeLocation

/** The node is <i>not</i> part of the main accelerator, and is instead used for
  * generating test stimuli.
  *
  * @param name
  *   the accelerator parameter this input is feeding.
  */
case class TestStimulus(name: String) extends StmNodeLocation

/** The node is <i>not</i> part of the main accelerator, and is instead reading
  * the output of the accelerator.
  */
object Sink extends StmNodeLocation
