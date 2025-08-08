package mhir

/** Classes to help with debugging the compiler. These can be used in the Scala
  * code or in the debugger.
  *
  * It is sometimes useful to inspect the behaviour of a stream pipeline
  * step-by-step to understand an error. There are methods to do so in
  * [[Tracer]].
  *
  * Furthermore, it is nice to get traces in a graphical format. [[DotPrinter]]
  * can convert a trace to an image using [[https://graphviz.org/ Graphviz]].
  */

package object debug {
  def indent(s: String, n: Int = 1): String = {
    s
      .split('\n')
      .map(ln => if (ln.isBlank) "" else "    ".repeat(n) + ln)
      .mkString("\n")
  }
}
