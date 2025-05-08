package object debug {
  def indent(s: String, n: Int = 1): String = {
    s.split('\n').map(ln => "    ".repeat(n) + ln).mkString("\n")
  }
}
