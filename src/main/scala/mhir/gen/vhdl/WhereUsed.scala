package mhir.gen.vhdl

/** Where an input stream is used in a given component.
  */
private sealed trait WhereUsed

/** The input stream is read by this component.
  */
private object Here extends WhereUsed

/** The input stream is passed along to exactly one sub-component.
  */
private object InChild extends WhereUsed

/** The input stream is not used anywhere in this component.
  */
private object Nowhere extends WhereUsed
