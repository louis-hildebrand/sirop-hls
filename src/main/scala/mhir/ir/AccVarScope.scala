package mhir.ir

/** Scope of a variable within a stream that is parallelized by replication.
  */
private sealed trait AccVarScope

/** Scope of an accumulator variable which depends on the vector index.
  */
private object PrivateScope extends AccVarScope

/** Scope of an accumulator variable which does not depend on the vector index.
  */
private object SharedScope extends AccVarScope
