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
