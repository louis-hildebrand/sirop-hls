/** [[mhir]] is an intermediate representation and compiler for streaming
  * accelerators. It is meant to compile high-level algorithms—expressed using
  * familiar functional building blocks like `map` and `fold`—to
  * high-performance hardware (e.g., in VHDL). Unlike similar projects like SHIR
  * and Aetherling, it aims to be <i>minimalist</i> in that there are as few
  * primitives as possible in the core IR.
  *
  * The core IR, type checker, and evaluator are defined in [[mhir.ir]]. Syntax
  * sugar for high-level constructs is defined in [[mhir.sugar]]. The backend
  * for generating VHDL is defined in [[mhir.gen]]. The optimizer is in
  * [[mhir.optimize]]. Parsers are in [[mhir.parse]]. Command-line programs are
  * in [[mhir.main]]. Finally, see [[mhir.debug]] for help debugging the
  * compiler.
  */

package object mhir
