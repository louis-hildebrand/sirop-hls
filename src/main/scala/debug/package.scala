package object debug {
  def indent(s: String): String = {
    s.split('\n').map(ln => s"    $ln").mkString("\n")
  }
}
