package mhir.parse

/** A point in the source file.
  *
  * @param line
  *   the line number (starting at 1).
  * @param col
  *   the column number (starting at 1).
  */
case class SourcePoint(line: Int, col: Int) {
  def moveRightBy(n: Int): SourcePoint = SourcePoint(line, col + n)
  def consume(s: String): SourcePoint = {
    assert(!s.contains("\n"))
    moveRightBy(s.length)
  }
  def moveDown(): SourcePoint = SourcePoint(line + 1, 1)

  override def toString: String = s"$line:$col"
}
