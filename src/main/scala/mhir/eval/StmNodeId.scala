package mhir.eval

/** An identifier that can be used to refer to a given node during evaluation,
  * even as the actual [[StmNode]] changes.
  *
  * @param id
  *   a name for the stream node.
  */
case class StmNodeId(id: String) extends AnyVal {
  override def toString: String = this.id
}

object StmNodeId {

  /** The special [[StmNodeId]] used to refer to the output of the stream
    * pipeline.
    */
  val Sink: StmNodeId = StmNodeId("sink")
}
