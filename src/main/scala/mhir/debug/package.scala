package mhir

/** Classes to help with debugging the compiler. These can be used in the Scala
  * code or in the debugger.
  *
  * =Viewing Code=
  * The default <code>toString</code> representation of the AST tends to be hard
  * to read. Use [[PrettyPrinter]] to view an expression as concrete syntax.
  *
  * =Tracing Execution=
  * It is sometimes useful to inspect the behaviour of a stream pipeline
  * step-by-step to understand an error. There are methods to do so in
  * [[Tracer]].
  */

package object debug {
  def indent(s: String, n: Int = 1): String = {
    s.split('\n').map(ln => "    ".repeat(n) + ln).mkString("\n")
  }
}
