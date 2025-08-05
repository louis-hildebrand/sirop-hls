## Streams
- "Type" of `StmBuild`:
	- With no stream inputs (e.g., counter):
		```
		StmBuild0 :
            Int               # Length
            -> (D -> T)       # Data
            -> (D -> Bool)    # Valid
            -> D -> (D -> D)  # Accumulator
            -> Stm[T]

		# Example: counter
		StmBuild(n, Some(i), i : (0, i + 1))
		```
	- With one stream inputs (e.g., map):
		```
		StmBuild1 :
            Int                    # Length
            -> (S -> D -> T)       # Data
            -> (S -> D -> Bool)    # Valid
            -> D -> (S -> D -> D)  # Accumulator
            -> S -> (D -> Bool)    # Input stream (next function cannot depend on S!)
            -> Stm[T]

		# Example: sum of non-empty stream
		StmBuild(n, if (i == ))
		```
	- With two stream inputs (e.g., zip):
		```
		StmBuild2 :
            Int                           # Length
            -> (S1 -> S2 -> D -> T)       # Output
            -> (S1 -> S2 -> D -> Bool)    # Valid
            -> D -> (S1 -> S2 -> D -> D)  # Accumulator
            -> S1 -> (D -> Bool)          # Input stream 1
            -> S2 -> (D -> Bool)          # Input stream 2
            -> Stm[T]
		```
- "Valid" expression lets you go for a few cycles without producing output (e.g., to wait until result is ready in StmFold)
	- *Pros:* very flexible, trivial to convert to hardware
	- *Cons:* perhaps *too* flexible - you can write infinite loops (e.g., `StmBuild(1, Default[T], None)`)
	- See [[Abandoned and Unexplored Design Choices]] for why `Option[T]` is not ideal here
## Nested Streams
- Keep streams flat at all times (i.e., `StmNext` returns next data element rather than next "row" in matrix, cannot nest `StmBuild`s). Flatten *during lowering*
	- *Advantage:* Corresponds much more closely to the hardware
		- e.g., `StmJoin` and `StmSplit` would look a lot like no-ops, as expected!
	- *Problem:* higher-level algorithmic primitives may not compose nicely in this system. For example, `StmMap` would need to know the type of the functional unit to be able to specify the dimensions of the new stream (e.g., `StmMap(StmMap)` may return a 2D stream whereas `StmMap(StmFold)` on the same input would return a 1D stream). You'd hope it would just work regardless of shape
		- Start by instantiating the inner hardware unit and then modify the logic somehow depending on the outer hardware unit?
			- *Challenge:* operators that reduce a stream to a scalar (e.g., `StmAccess`, `StmFold`) are tricky to convert into functions from stream to stream (which is necessary for repeating them, like in `StmMap`)
				- ~~*Solution 1:* the only primitive that can be used to extract a value from a stream is `StmNext(...).__1`, right? So look for `StmNext(...).__1` and automatically rewrite it?~~
					- Can we guarantee that this will always be possible?
				- *Solution 2:* forbid reducing a stream to a scalar.
					- Could have two versions of functions like `StmFold` and `StmAccess`; a toplevel version which returns a scalar and a second version which returns a stream of length 1
						- Probably best to be consistent: always return a stream of length 1
					- *Advantage:* simpler to deal with things like `StmMap(s, (acc: Expr) => StmFold(acc, ...))` since `StmFold` is now just another function from stream to stream, which is easy to deal with
					- *Advantage:* this constraint seems like it would help with `Stm2Vec` as well: although `Stm2Vec` works ok when the input is a constant stream, partially evaluating `Stm2Vec` with an unknown input yields a pretty nasty expression (cascade of `IfThenElse`s with bodies like `StmNext(StmNext(StmNext(...).__0).__0).__1`)
						- ~~Maybe I can lean into the nasty `StmNext(StmNext(...).__0).__1` expression and just make sure the hardware generator inserts enough registers to keep values until they're required~~
					- *Disadvantage:* while lowering, would need to replace int expressions including an expression that extracts a scalar from a stream into a `StmMap`, which seems just as hard as the first challenge
						- It might actually not be that tricky, since there are a very limited number of ways to reduce stream to non-stream in the higher level IR (basically just StmFold, StmAccess, and Stm2Vec) but many ways to do it with full access to `StmNext()` (e.g., `StmNext(StmNext(StmNext(s).__0).__0).__1`)
					- *Disadvantage:* in the current interpreter, it's a bit inconvenient to make this change because I would need to update all the nested `StmFold` or `StmScan` tests and also update the implementation of `StmScan`. But hopefully it won't be as hard in the final compiler
			- *Challenge:* outer unit must reset inner unit(s). But what if there's some `StmBuild` buried deep within that inner unit which isn't easily accessible?
				- e.g., `StmMap(StmCount(3), (i: Expr) => StmSum(StmCount(i)))`
					- Not only `StmSum` but also `StmCount(...)` needs to be reset for each new `i`
				- e.g., `StmMap(StmCount2D(4, 3), (s: Expr) => StmDot(s, StmCst(3, 1)))`
				- ~~*Solution 1:* forbid constructing a stream in the body of another stream like this?~~
					- *Disadvantage:* seems like a rather harsh constraint that would eliminate a lot of reasonable programs
				- *Solution 2:* automatically prevent situations like this by applying the stream fusion rule as much as possible?
					- Can we guarantee that the problem is *always* solvable via fusion?
						- There is a general rule for fusion, so it sure seems so
				- ~~*Solution 3:* ignore this during lowering from existing SHIR IR to new minimalist IR, handle it during hardware generation?~~
					- *Disadvantage:* might make hardware gen more complicated
					- *Disadvantage:* it seems tricky to ignore this, since `StmMap` does need to be able to distinguish between the input it's providing (e.g., `s` in example 2) and other streams (e.g., `StmCst(3, 1)` in example 2)
			- *Disadvantage:* the function in `StmMap` must be a literal function (and case it returns a stream, the body must syntactically be a `StmBuild`)
			- *Disadvantage:* the `StmBuild` being repeated must be implemented in a certain way (it must be prepared to fully drain its inputs)
				- e.g., implementing `StmPrefix(sIn, k)` as simply `StmBuild(k, Some(StmData(s)), s : (sIn, true))` would probably not work inside a StmMap. Instead, the output should be `None` when it has finished reading `k` elements
				- ==Can `StmMap` be implemented so as to update the `valid` signal itself rather than making this assumption?==
		- ~~Should we think of different stream primitives than map, fold, zip, etc.?~~
			- But then how would we convert high-level SHIR IR into this minimalist IR?
	- In this case, disallow nesting of streams
		- Should be straightforward enough to check that stream elements must be "data types" (i.e., int, bool, tuple of data types, or vector of data types)
	- ~~Represent length *per dimension* in the IR? In this case, we would actually need *two* integers per dimension to specify length (one is like the current counter value, the other is the number that the counter resets to upon hitting zero)~~
		- Is it even useful to represent the length per dimension? Maybe it would be enough to just represent the total number of elements in the entire stream.
## Data Repetition
Some higher-order functions have data access patterns that require reading a stream more than once
### Example 1
```
// a : Stm[Stm[Int, 4], 3]
// b : Stm[Int, 4]
StmMap(a, (rowA: Stm[Int, 4]) => StmZip(rowA, b))
//   : Stm[Stm[(Int, Int), 4], 3]

// Output:
[[ (a[0, 0], b[0]), (a[0, 1], b[1]), (a[0, 2], b[2]), (a[0, 3], b[3]) ],
 [ (a[1, 0], b[0]), (a[1, 1], b[1]), (a[1, 2], b[2]), (a[1, 3], b[3]) ],
 [ (a[2, 0], b[0]), (a[2, 1], b[1]), (a[2, 2], b[2]), (a[2, 3], b[3]) ]]

// a is read once
// b is read three times
```
### Example 2
```
// a : Stm[Stm[Int, 4], 2]
// b : Stm[Stm[Int, 4], 3]
StmMap(a, (rowA: Stm[Int, 4]) => StmMap(b, (rowB: Stm[Int, 4]) => StmZip(rowA, rowB)))
//   : Stm[Stm[Stm[Int, 4], 3], 2]

// Output:
[[[ (a[0, 0], b[0, 0]), (a[0, 1], b[0, 1]), (a[0, 2], b[0, 2]), (a[0, 3], b[0, 3]) ],
  [ (a[0, 0], b[1, 0]), (a[0, 1], b[1, 1]), (a[0, 2], b[1, 2]), (a[0, 3], b[1, 3]) ],
  [ (a[0, 0], b[2, 0]), (a[0, 1], b[2, 1]), (a[0, 2], b[2, 2]), (a[0, 3], b[2, 3]) ]],
 [[ (a[1, 0], b[0, 0]), (a[1, 1], b[0, 1]), (a[1, 2], b[0, 2]), (a[1, 3], b[0, 3]) ],
  [ (a[1, 0], b[1, 0]), (a[1, 1], b[1, 1]), (a[1, 2], b[1, 2]), (a[1, 3], b[1, 3]) ],
  [ (a[1, 0], b[2, 0]), (a[1, 1], b[2, 1]), (a[1, 2], b[2, 2]), (a[1, 3], b[2, 3]) ]]]

// *Each row* of `a` is read three times
// *All of* `b` is read twice
```
## Vectors of Streams
- Two possible interpretations in hardware?
	- *Interpretation 1:* Each duplicated processing element is working in lockstep, and you must read from all of them at once or none of them.
		- In other words, `Vec<Stm<T; m>; n>` is equivalent to `Stm<Vec<T; n>; m>`
		- *Advantage:*
			- This interpretation seems easier to implement. Just rewrite (perhaps during lowering) so that you have `Stm<D>`, where `D` is a data type (int, bool, `Vec<D>`, or `(D1, D2, ..., Dk)`)
			- This interpretation seems to be consistent with Aetherling's hardware
				- Elements of `SSeq` all have the same type, including timing info, so the processing elements will indeed be working in lockstep
				- Aetherling circuits are statically scheduled and a given value is only on the wire for one cycle. If you have a `SSeq 2 (TSeq n v Int)`, it is impossible to read from the first `TSeq` right away but delay the second `TSeq`
		- *Note:* inner type must *not* depend on the vector index!
			- e.g., `VecBuild(n, i -> StmCount(i))` is not valid
	- *Interpretation 2:* We actually have an array of stream producers, each of which is working at its own rate and can be read independently
		- *Advantage:* this interpretation seems a bit more flexible: allows for implementing heterogeneous pipelines
			- But you can presumably do this anyway by just using `StmBuild`s with multiple inputs
		- *Disadvantage:*
			- I suspect the hardware and hardware generator would be more complex
- Implementing interpretation 1:
	- Move streams to the outside in the higher-level language?
		- *Challenge:* won't this be infeasible because every operator would need to know about all the other operators?
	- Move streams to the outside in the minimalist IR?
		- *Challenge:* you may end up with something like `Stm<Vec<Stm<T>>>` (i.e., indirectly nested streams), and I don't know how to flatten out nested streams in the minimalist IR (it seems simplest to do that during lowering)
	- Move streams to the outside during lowering pass?
		- This seems like a convenient time to do it
			- This is when stream flattening happens as well
			- High-level operations but simple, low-level inputs
		- What would need to change compared to the existing lowering pass that only flattens?
			- `VecBuild(n, i -> e)`:
				- If `e` is data, then no problem
				- If `e` is a `Stm<T>`, then:
					- Insist `e = StmBuild(m, Tuple(data, valid), reg_eqns ++ stm_eqns)`
					- Insist `m` does not depend on `i`
					- Insist `valid` does not depend on `i`, even indirectly (via accumulator variables that depend on `i`)?
					- Let the new output be `out' = Tuple(VecBuild(n, i -> data), valid)`
					- Registers must be partitioned into "private registers" (which depend on `i`) and "shared registers" (which do *not* depend on `i`, nor on any private registers)
						- Shared registers: unchanged
						- Private registers:
							- Replace seed with `VecBuild(n, i -> seed)`
								- Seed may depend on `i`
							- Replace all occurrences of the accumulator variable (incl. in update expressions for other private registers and in stream output)
					- Stream inputs:
						- Insist the `ready` value for each stream equation does not depend on `i`, nor on private variables?
						- ==TODO:== What if the seed is something like a `VecAccess`? Just drop the `VecAccess`? What if the `VecAccess`es are nested?
					- Output `StmBuild(m, out', reg_eqns' ++ stm_eqns)`
			- `VecAccess(v, i)`:
				- If `v` is a data vector, then no problem
				- If `v` is a vector of streams, then lower to `StmMap(v, v' => VecAccess(v', i))`
			- Other vector ops: lower to `VecBuild` and then move streams out?
				- `VecMap`: similar to basic `VecBuild`, except that you also need to replicate the input
				- `VecFold`, `VecScan`: I don't think these make sense for vectors of streams, since in general it would involve reading one stream before the other (i.e., not in lockstep).
				- `Vec2Stm`: first lower input to `Stm[Vec[Int]]`, then defer to `StmMap(Vec2Stm)`
				- `VecPrefix`, `VecSuffix`, `VecJoin`, `VecSplit`, `VecReverse`, `VecRepeat`: work out of the box due to new semantics of `VecBuild`, `VecAccess`!
			- ==TODO:== Can we perhaps do similar things for a `Mux` or a `Tuple` containing streams?
			- Stream ops: unaffected, right?
	- Examples:
		- *Example:* `VecBuild(n, i -> StmRange(m, i, i))`
			- Lowering steps:
				1. Lower `StmRange`
					1. *Result:* `StmBuild(m, Some(a), a : (i, a + i))`
				2. Lower `VecBuild`
					1. *Result:* `StmBuild(m, Some(VecBuild(n, i -> a[i])), a : (VecBuild(n, i -> i), VecBuild(n, i -> a + i)))`
		- *Example:* `VecBuild(n, i -> StmZip(StmCount(m), StmRange(m, i, i)))`
			- Lowering steps:
				1. Lower `StmCount`
					1. *Result:* `StmBuild(m, Some(a), a : (0, a + 1))`
				2. Lower `StmRange`
					1. *Result:* `StmBuild(m, Some(a), a : (i, a + i))`
				3. Lower `StmZip`
					1. *Result:* `StmBuild(m, Some((StmData(s1), StmData(s2))), s1 : (StmBuild(m, Some(a), a : (0, a + 1)), true), s2 : (StmBuild(m, Some(a), a : (i, a + i)), true))`
				4. Lower `VecBuild`
					1. *Result:*
						```
						StmBuild(
							m,
							Some(VecBuild(n, i -> (StmData(s1), StmData(s2)[i]))),
							s1 : (StmBuild(m, Some(a), a : (0, a + 1)), true),
							s2 : (StmBuild(m, Some(VecBuild(n, i -> a[i])), a : (VecBuild(n, i -> i), VecBuild(n, i -> a[i] + i))), true)
						)
						```
		- *Example:* `VecBuild(n, i -> StmBuild(m, Some((a1, a2)), a1 : (0, a1 + 1), a2 : (i, a2 + i)))`
			- Like `StmZip`, but fused
			- Lowering steps:
				1. Lower `StmBuild`
				2. Lower `VecBuild`
					```
					StmBuild(
						m,
						Some(VecBuild(n, i -> (a1, a2[i]))),
						a1 : (0, a1 + 1),
						a2 : (VecBuild(n, i -> i), VecBuild(n, i -> a2[i] + i))
					)
					```
		- *Example:* `VecBuild(n, i -> StmFold(s, 0, +))`
			- Should `s` be considered a vector of streams as well?
				- I guess it depends on the case.
			- Lowering steps:
				1. Lower `StmFold(s, 0, +)`
					1. *Result:* `StmBuild(1, if i == m then Some(a) else None, s : (s, true), a : (0, a + StmData(s)), i : (0, i + 1))`
				2. Lower `VecBuild`
					1. *Result:*
						```
						StmBuild(
							1,
							if i == m then Some(VecBuild(n, i -> a[i])) else None,
							s : (s, true),
							a : (VecBuild(n, i -> 0), VecBuild(n, i -> a[i] + StmData(s)[i])),
							i : (0, i + 1)
						)
						```
		- *Example:* `StmMap(svs, vs -> VecMap(vs, s -> StmMap(s, +42)))`
				- Types:
					- `svs : Stm[Vec[Stm[Int; k]; m]; n]`
					- `vs : Vec[Stm[Int; k]; m]`
					- `s : Stm[Int; k]`
				- Lowering steps:
					1. Lower inner `StmMap`
						- *Result:* `StmBuild(k, Some(StmData(s) + 42), s : (s, true))`
					2. Lower `VecMap`
						- `vs` will actually have type `Stm[Vec[Int; m]; k]` and the function will actually return `Stm[Vec[Int; m]; k]`
						- Usual conversion to `VecBuild`: `VecBuild(m, i -> StmBuild(k, Some(StmData(s) + 42), s : (vs[i], true)))`
						- After applying `VecBuild` lowering rules: `StmBuild(k, Some(VecBuild(m, i -> VecAccess(StmData(s), i) + 42)), s : (vs, true))`
					3. Lower outer `StmMap` as usual
						- Should get something like this (after removing counters): `StmBuild(n * k, Some(VecBuild(m, i -> VecAccess(StmData(s), i) + 42)), s : (svs, true))`
		- *Example:* `StmMap(svvs, vvs -> VecMap(vvs, vs -> VecMap(vs, s -> StmMap(s, f))))`
			- Types:
				- `svvs : Stm[Vec[Vec[Stm[Int; p]; k]; m]; n]`
				- `vvs  : Vec[Vec[Stm[Int; p]; k]; m]`
				- `vs   : Vec[Stm[Int; p]; k]`
				- `s   : Stm[Int; p]`
			- Lowering steps:
				1. Lower inner `StmMap`
					1. *Result:* `StmBuild(p, Some(f(StmData(s))), s : (s, true))`
				2. Lower inner `VecMap`
					1. Usual conversion to `VecBuild`: `VecBuild(k, i -> StmBuild(p, Some(f(StmData(s))), s : (vs[i], true)))`
					2. After applying `VecBuild` lowering rules: `StmBuild(p, Some(VecBuild(k, i -> f(StmData(s)[i]))), s : (vs, true))`
				3. Lower outer `VecMap`
					1. Usual conversion to `VecBuild`: `VecBuild(m, j -> StmBuild(p, Some(VecBuild(k, i -> f(StmData(s)[i]))), s : (vvs[j], true)))`
					2. After applying `VecBuild` lowering rules: `StmBuild(p, Some(VecBuild(m, j -> VecBuild(k, i -> f(StmData(s)[j][i])))), s : (vvs, true))`
				4. Lower outer `StmMap` as usual
					1. Basically just replace `vvs` with `svvs` and change the length from `p` to `n * p`, right?
		- *Example:* `StmMap(svs, vs -> Vec2Stm(vs))`?
			- `vs -> Vec2Stm(vs)` will become a function from stream to stream, and then we proceed as usual?
	- ==TODO:== will Aetherling programs always satisfy the constraints described above regarding private vs shared registers?
## Graphs of Streams
- Sometimes it is useful to have *multiple* consumers for the same producer
	- e.g., `StmZip(StmMap(s, f), StmMap(s, g))`
	- Aetherling programs are generally directed acyclic *graphs*, so if we want to be able to lower Aetherling programs down to our IR we'll need to support graphs
- Why not just give each consumer its own copy of the producer (as in substitution)?
	- Duplicated hardware, therefore wasted area
	- Doesn't work if the producer is a variable
- *Challenge:* need to watch out for deadlocks...
	- ... due to combinational loop in the handshake interfaces
		- In general, the consumer's `ready` signal depends on the producers' `valid` signals
		- In this case, the duplicated stream must wait until all its producers are ready - so its `valid` signal depends on the consumers' `ready` signals
		- If consumer is waiting for producer and producer is waiting for consumer, then there will be a deadlock!
		- *Solution 1:* insert single-element buffers before each consumer
			- The buffer raises its `ready` signal whenever it has no elements
			- The buffer raises its `valid` signal whenever it has an element
			- No dependency on the `valid` signal of the producer, nor on the `ready` signal of the consumer
				- This works as long as the buffer has exactly one producer and at most one consumer
		- *Solution 2:* insert one shared buffer, keep track of which consumers have read the value, continue once all consumers have read?
			- *Advantages:* probably less memory use (as long as you don't have a massive number of consumers)
			- *Disadvantage:*
				- More complex hardware generation?
					- With solution 1 you can write a generic VHDL entity for the buffer, whereas here the buffer would need to support an arbitrary number of consumers
					- With solution 2, the buffer would need to remember to lower the `valid` signal for consumers who have already read the current value
			- ==TODO:== maybe this would be worth trying out
	- ... due to the consumers having incompatible access patterns
		- e.g., `StmConcat(s, s)` (stream gets read twice, which is not valid)
		- e.g., `StmZip(StmPrefix(s, 2), StmSuffix(s, 2))` (first element must be available at the same step as second-last element, which will not be the case for a stream of length >= 3)
		- *Solutions:*
			- Just count on the programmer to not write such programs; declare it to be undefined behaviour
			- Provide a function in the higher-level IR (`StmDuplicateSafely`?) which converts stream to vector and then inserts one `Vec2Stm` for *each* consumer? Programmer can use this if in doubt
				- Why not make this safe version the primitive?
					- It will presumably use a lot more hardware
					- Seems tricky to prove that you can use the simpler single-element buffer version when you started with the safe version
						- e.g., for the Aetherling benchmarks, I'd prefer to just assume the Aetherling program is correct rather than proving it in each and every case
					- The safe version seems less general. I think it's already possible to implement the safe version with what we have, but not possible to implement the single-element buffer version
			- ==TODO:== is it possible to perform a static analysis that can guarantee absence of deadlocks?
- *Challenge:* how to represent these graphs in the IR? How to define semantics to match implementation in hardware?
	- Just allow function param to be used multiple times, even if it's a stream. Update evaluator to handle function calls differently depending on the input type.
		- *Advantage:* no need for new primitives
		- *Problems:*
			- Need to update semantics of `Function` and `FunCall` anyway, so maybe the advantage of avoiding one primitive isn't that big
			- This special-case semantics of `Function` and `FunCall` seems pretty gross to me
			- Seems easy to forget about the difference in semantics and introduce bugs or ruin the quality of the design
				- e.g., partial evaluator will just perform the substitution, which will lead to duplicated hardware
	- Add a new primitive (perhaps called `LetStm`) which assigns one stream to a variable and outputs one stream which may use the variable multiple times
		- *Disadvantage:* need new primitive
		- *Advantages:*
			- Easier hardware gen? The VHDL generator won't have to check the type of a function, how many times the input is used, etc. Just add a new case for `LetStm`
			- When adding the new primitive, the Scala compiler will give me more guidance on what parts of my codebase need to be updated to handle the new primitive
	- Split `Function` primitive into `DataFunction` (data -> data) and `StreamFunction` (stream -> stream), with semantics of `StreamFunction` *not* defined by substitution?
		- But then what about functions from data to stream (e.g., `Vec2Stm`?)? Can we always use `StreamFunction` instead?
			- Maybe this would actually simplify `Streamifier`!
- *Challenge:* will things like stream fusion now be broken? And since stream fusion is required for multi-dimensional map and scan, does that mean those will be broken too?
	- e.g., how to fuse when the producer is `let s = input_stm in StmZip(StmMap(s, f), StmMap(s, g))`?
		- Just move `LetStm` up as much as possible!
	- e.g., how to handle `StmMap` when the function body is a `LetStm`?
		- Once everything has been fused together, hopefully the variable bound by `LetStm` will only be used once. Then it should be possible to eliminate the `LetStm` and proceed as usual
			- ==TODO:== is this true in general? Will I need to go through the accumulators of the `StmBuild` and de-duplicate all stream inputs?
			- ==TODO:== What about something like `StmMap(mat, (row: Stm[Int, 10]) => let stm s = row in StmZip(s, s))`?

## Other Kinds of Streams
- Stream with a "done" signal?
	- ==TODO:== implement this!
	- *Advantages:*
		- No more need for each stream to keep track of its own length ==> lower resource usage?
			- e.g., `map` is done when its input is done - basically just pass through the `done` signal. But with bounded streams, it must have a `remaining_outputs` down-counter, which is almost certainly more expensive
		- More expressive in some ways
			- e.g., can implement things like `filter` which do not have a fixed size
	- *Disadvantages:*
		- Nested streams may be trickier
			- Just keep track of the stream length (if known) via the type?
- Unbounded stream?
	- But then how would the evaluator know when to stop?