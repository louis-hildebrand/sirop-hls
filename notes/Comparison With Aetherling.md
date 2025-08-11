## Types
| Aetherling | Our IR            | Benchmarks | Note                 |
| ---------- | ----------------- | ---------- | -------------------- |
| `UnitT`    | `TyTuple()`       | all        |                      |
| `BitT`     | `TyBool`          |            |                      |
| `Int8T`    | ???               | -          |                      |
| `UInt8T`   | ???               | all        |                      |
| `Int16T`   | ???               | -          |                      |
| `UInt16T`  | ???               | -          |                      |
| `Int32T`   | ???               | -          |                      |
| `UInt32T`  | ???               | cam        |                      |
| `FixP1_7T` | ???               | cam        | Fixed-point? 8 bits. |
| `ATupleT`  | `TyTuple(t1, t2)` |            |                      |
| `STupleT`  | `TyVec(t, n)`     |            |                      |
| `SSeqT`    | `TyVec(t, n)`     |            |                      |
| `TSeqT`    | `TyStm(t, n)`     |            |                      |
## Integers, Booleans, Tuples
| Aetherling      | Our IR           | Benchmarks     | Note                           |
| --------------- | ---------------- | -------------- | ------------------------------ |
| `IdN`           | ???              | -              |                                |
| `AbsN`          | ???              | -              |                                |
| `NotN`          | `Not`            | cam            |                                |
| `AndN`          | `And`            |                | Input is a tuple?              |
| `OrN`           | `Or`             |                |                                |
| `AddN`          | `Sum`            |                |                                |
| `SubN`          | `e1 - e2`        |                |                                |
| `MulN`          | `Mul`            |                |                                |
| `DivN`          | `Div`            |                |                                |
| `LSRN`          | ???              | all except map |                                |
| `LSLN`          | ???              | all except map |                                |
| `LtN`           | `LessThan`       |                |                                |
| `EqN`           | `Equal`          |                |                                |
| `IfN`           | `Mux`?           |                | Fingers crossed that MUX works |
| `FstN`          | `e.__0`          | cam            |                                |
| `SndN`          | `e.__1`          | cam            |                                |
| `ATupleN`       | `Tuple(e1, e2)`? | all            |                                |
| `STupleN`       | `VecBuild`       | all but map    | ==What does this do?==         |
| `STupleAppendN` | `VecAppend`      | all but map    | ==What does this do?==         |
| `STupleToSSeqN` | no-op            | all but map    | ==What does this do?==         |
| `SSeqToSTupleN` | no-op            | -              |                                |
- ==TODO:== What about functions, function calls, function composition, etc.?
- ==TODO:== What about registers?
## "Generators"
| Aetherling    | Our IR | Benchmarks | Note                             |
| ------------- | ------ | ---------- | -------------------------------- |
| `Lut_GenN`    | ???    | -          |                                  |
| `Const_GenN`  | ???    | all        | ==What does this do?==           |
| `Counter_sN`  | ???    | -          | Need to specify desired int type |
| `Counter_tN`  | ???    | -          | Need to specify desired int type |
| `Counter_tsN` | ???    | cam        | ==What exactly does this do?==   |
| `Counter_tnN` | ???    | -          | What does this do?               |
## "Sequence Operators"
| Aetherling          | Our IR           | Benchmarks             | Note                                                                                             |
| ------------------- | ---------------- | ---------------------- | ------------------------------------------------------------------------------------------------ |
| `Shift_sN`          | `VecShiftRight`? | conv, convb2b, sharp   | Inserted elem is undefined. Also takes as input a shift amount. ==Is the undefined elem valid?== |
| `Shift_tN`          | `StmShiftRight`? | all but map            | Inserted elem is undefined. Also takes as input a shift amount.                                  |
| `Shift_tsN`         | ???              | conv, convb2b, sharp   | Inserted elem is undefined. Also takes as input a shift amount.                                  |
| `Shift_ttN`         | ???              | -                      |                                                                                                  |
| `Shift_tnN`         | ???              | conv, convb2b, sharp   | ==What does this do?==                                                                           |
| `Up_1d_sN`          | ???              | -                      |                                                                                                  |
| `Up_1d_tN`          | ???              | -                      |                                                                                                  |
| `Down_1d_sN`        | `VecAccess`?     | cam                    | Is this `select_1d_s` from paper? `select_1d_s` returns a `SSeq`.                                |
| `Down_1d_tN`        | `StmAccess`?     | cam                    | Is this `select_1d_t` from paper? `select_1d_t` returns a `TSeq`.                                |
| `Partition_s_ssN`   | ???              | all but map            | ==What does this do?== Split?                                                                    |
| `Partition_t_ttN`   | ???              | all but map            | ==What does this do?== Split?                                                                    |
| `Unpartition_s_ssN` | ???              | conv, convb2b, sharpen | ==What does this do?== Join?                                                                     |
| `Unpartition_t_ttN` | ???              | -                      |                                                                                                  |
| `SerializeN`        | ???              | conv, convb2b, sharp   | ==What does this do?==                                                                           |
| `DeserializeN`      | ???              | -                      |                                                                                                  |
| `Add_1_sN`          | ???              | -                      |                                                                                                  |
| `Add_1_0_tN`        | ???              | -                      |                                                                                                  |
| `Remove_1_sN`       | ???              | all but map            | ==What does this do?==                                                                           |
| `Remove_1_0_tN`     | ???              | -                      |                                                                                                  |
### `Shift_tsN`

time
-->

original:
 9 6 3 0
10 7 4 1
11 8 5 2

shifted:
 8 5 2 u
 9 6 3 0
10 7 4 1

Need one accumulator to store the last value in each vector, which must be held until the next cycle.

## "Higher-Order Operators"
| Aetherling  | Our IR     | Benchmarks  | Note                                       |
| ----------- | ---------- | ----------- | ------------------------------------------ |
| `Map_sN`    | `VecMap`?  | all         |                                            |
| `Map_tN`    | `StmMap`?  | all         |                                            |
| `Map2_sN`   | ???        | all but map |                                            |
| `Map2_tN`   | ???        | all but map |                                            |
| `Reduce_sN` | ???        | all but map | Returns a vector of size 1 (not a stream!) |
| `Reduce_tN` | `StmFold`? | all but map |                                            |
## Miscellaneous Expressions
| Aetherling | Our IR | Benchmarks  | Note                   |
| ---------- | ------ | ----------- | ---------------------- |
| `InputN`   | ???    | -           |                        |
| `ErrorN`   | ???    | -           |                        |
| `FIFON`    | ???    | all         | ==What does this do?== |
| `ReshapeN` | ???    | all but map |                        |
## Miscellaneous Notes
- Algorithmic primitives in $L^\textnormal{st}$ (lower-level language):
	- For each $L^\textnormal{st}$ primitive shown above (except `tuple_to_seq`, `partition`, and `unpartition`), there is one version that applies to `SSeq` (like vectors) and one that applies to `TSeq` (like streams)
	- `reshape :: t -> t'`
		- "converts between two space-time sequence types with the same element throughput (e.g., `(TSeq 1 3 (SSeq 4 Int))` and `(TSeq 4 0 Int)`)" (p. 412)
		- The scheduler only uses the following five types:
			- `TSeq n i t` (a bit like a stream)
			- `TSeq n io (TSeq 1 ii t)` (a bit like a 2D nested stream)
			- `TSeq n io (TSeq 1 ii (TSeq 1 ii t))` (a bit like a 3D nested stream)
			- `SSeq n t` (like a vector)
			- `TSeq no io (SSeq ni t)` (a bit like a stream of vectors)
		- Seems like we can convert between these types using `StmSplit`, `StmJoin`, `Stm2Vec`, `Vec2Stm`, etc.
- How does their implementation of the algorithmic primitives compare to ours?
	- They generate Verilog via Chisel. "All $L^\textnormal{st}$ operators correspond to hardware generators written in Chisel \[...\]. These generators produce Verilog." (p. 416)
	- The implementation of the `reshape` operator is briefly described (since it's a "particularly complex" case), but I don't see any explanation for the other primitives
	- What are the pros and cons of dynamic scheduling (as in SHIR) vs static scheduling (as in Aetherling)?
		- *Pros:*
			- More flexibility when it comes to hazards (particularly waiting for memory accesses)?
			- Avoid the overhead of a central control unit?
				- ==Does Aetherling need a central control unit at all? Seems like each component will just keep ticking at its own frequency.==
		- *Cons:*
			- Some overhead due to handshake interfaces
- $L^\textnormal{seq}$ reduction operators (e.g., `reduce`, `select_1d`) return a `Seq` of length 1, like in our IR!
- Does Aetherling nest `TSeq` within `SSeq`?
	- ~~I can't find any examples of this after a quick scan through the paper~~
	- ~~Section 6.1: scheduler doesn't seem to explore any types where a `TSeq` is inside an `SSeq`~~
	- It is technically possible, as discussed in https://github.com/David-Durst/embeddedHaskellAetherling/blob/master/theory/Types.md#repeatedly-nested-tseq-and-sseq-patterns
		- But why would you write `TSeq 2 1 (SSeq 2 (TSeq 3 1 Int))` rather than `TSeq 2 1 (TSeq 3 1 (SSeq 2 Int))`?
	- The space-time code for their benchmarks does include types like this (e.g., `TSeq 1 0 (SSeq 3 (TSeq 3 0 UInt8))` in conv2d at throughput = 1/3) :/
- Aetherling pipeline is in general a directed, acyclic *graph*! But *presumably* then each consumer must read in sync (e.g., not possible to do something like `concat(s, s)`)
## TODO
- Required changes to minimalist IR:
	- ==TODO:== Add type for fixed-point numbers
	- ==TODO:== Add variable-sized int types
	- ==TODO:== Update type checker to allow ints or fixed-point numbers in addition and multiplication
		- What size should output be? Same as inputs?
		- What happens if the inputs have different sizes? What if you add signed and unsigned?
	- ==TODO:== Make versions of add, sub, mul, etc. that take as input a tuple
	- ==TODO:== Figure out what all the sequence operators mean, add them to minimalist IR
	- ==TODO:== Implement reshape