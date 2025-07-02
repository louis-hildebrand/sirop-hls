package mhir

/** Classes to help with debugging the compiler. These can be used in the Scala
  * code or in the debugger.
  *
  * It is sometimes useful to inspect the behaviour of a stream pipeline
  * step-by-step to understand an error. There are methods to do so in
  * [[Tracer]].
  */

package object debug {
  def indent(s: String, n: Int = 1): String = {
    s.split('\n').map(ln => "    ".repeat(n) + ln).mkString("\n")
  }
}
