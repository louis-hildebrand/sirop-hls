## On-Chip Block RAM
- Semantics:
	- Can read one element per cycle and write one element per cycle
		- Assume it takes one clock cycle
		- ==Can you read from the same address you're writing to?==
- Implementation in hardware:
	- Represent it as an array; synthesizer should be able to automatically replace it with a BRAM
		- Under certain conditions (enough BRAMs available, access is synchronized with clock, etc.)
- Representation in IR:
	- New primitives:
		- `MemAlloc[T](n)`: return BRAM containing `n` elements of type `T`, all uninitialized (or initialized to `Default[T]`?)
		- `MemRead(m, i)`: return value at index `i` in BRAM
		- `MemWrite(m, v, i)`: return memory obtained by writing value `v` into address `i`
			- Must never use the old RAM again after this
		- How to share memory between different streams in pipeline?
			- *Example:* `StmRepeat` may be implemented by `Stm2Mem` followed by repeated `Mem2Stm`, right?
			- *Example:* from original SHIR paper ([[Schlaak et al in TACO 19-2-2022]]):
				```
				Let allocatedBuffer = MemAlloc(N, dataType, memLocType) in
				Let updatedBuffer =
					ReduceStm(
						λ mem => λ data => Write(mem, data),
						allocatedBuffer,
						ZipStm(inputData, Counter(N - 1))
					)
				in
				MapStm(λ addr => Read(updatedBuffer, addr), Counter(N - 1))
				```
			- Something like `LetMem(x, m, e)`, where `m` builds the memory (read/write) and `e` only reads from it
				- Lift (and SHIR?) have `Let(x, v, e)` desugar to `FunCall(Function(x, e), v)`, but have a boolean flag in `FunCall` to say whether the optimizer is allowed to beta reduce
					- This seems equivalent to just having a new primitive
				- ==TODO:== but how can I do something like `let mem = StmFold(...) in (...)` (e.g., for `StmRepeat`) if streams of memory are forbidden?
				- ==TODO:== but how can I reset a `LetMem` expression (e.g., if the function inside `StmMap` uses memory)?
					- *Example:*
						```
						// s : Stm[Stm[Int, m], n]
						// (for simplicity, assume MemRead returns the result immediately; not a stream)
						StmMap(
							s,
							(row : Stm[Int, m]) =>
								let mem = Stm2Mem(row) in
								StmMap(<0 to m twice>, (i: Int) => MemRead(mem, i))
						)

						// Function inside StmMap is equivalent to:
						StmBuild(
							2 * m,
							MemRead(mem, i),
							!filling,
							mem : (
								MemAlloc[T](m),
								mux(filling, Some((i, StmData(s))), None)
							),
							s : (s, filling),
							filling: (True, filling && i + 1 < m),
							i : (0, mux(i + 1 == m, 0, i + 1))
						)

						// This can be repeated in the usual way
						```
			- Simply allow multiple streams to access memory however they want (by each having a memory accumulator whose initial value is the shared memory)?
				- *Advantages:*
					- Seems very flexible and easy to use
					- Still need some primitive to declare the shared memory (`LetMem`?), but at least it's simpler
						- e.g., stream fusion or stream resetting (in `StmMap`, `StmScan`): just move the `Let` outside and then proceed as usual, right?
						- No need for new primitives; existing things like stream resetting in `StmMap` and `StmScan` will hopefully work as-is
				- *Disadvantages:*
					- Seems easy to shoot yourself in the foot by having race conditions (it's the programmer's responsibility to not do that)
						- ==TODO:== discuss this in the [[Errors]] page. Maybe the simulator can emit a warning in case it detects obvious data races (e.g., two writes to the same location in the same cycle)
					- Now the compiler must be very careful with side effects
						- e.g., cannot remove an unused stream if it possibly writes to memory (including if it takes as input a stream which is a variable, since *that* stream may have side effects!)
						- Equality saturation would no longer work, since it does not support side effects :(
				- What ordering guarantees are provided?
					- Basically the same as for multi-threading! Ordering within thread, ordering between threads only when explicitly demanded
					- Within a stream:
						- Obviously things happen in order
					- Between consumer stream and producer stream:
						- If consumer tries to read from producer, the consumer cannot advance until the consumer yields a valid value
						- Can synchronize between streams by having one stream emit `unit` once it's done writing (provided the compiler knows not to remove such a stream)
							```
							// Stm2Mem(stm : Stm[T, n], mem : Mem[T, n]) : Stm[(), 1]
							StmBuild(
								1,
								(),
								i + 1 == n,
								i : (0, i + 1),
								m : ( mem, Some((i, StmData(s))) ),
								s : (stm, true)
							)
		
							// StmRepeat(stm : Stm[T, n], m : Int) : Stm[Stm[T, n], m]
							let mem = MemAlloc[T](n) in
							StmBuild(
								n * m,
								mux(first_step, Default[T], MemRead(mem, i)),
								!first_step,
								first_step: (True, False),
								i : (0, mux(first_step || i + 1 == n, 0, i + 1)),
								_ : (Stm2Mem(stm, mem), first_step)
							)
							```
						- ==TODO:== In this case, you can actually return valid data even while writing into memory (after fusion). Can that optimization be automated?
					- Between unrelated streams (e.g., the two arguments of `StmZip`):
						- Basically nothing?
						- You can't read stream elements out of order - does that help in some cases?
						- ==Why would you want to synchronize like this?==
				- Initial contents should be undefined so that the compiler doesn't need to zero it out again when resetting (e.g., inside `StmMap` or `StmScan`)
					- The programmer can explicitly zero out the memory at the beginning, in which case the zeroing out will also be repeated
				- ==TODO:== at the moment, streams always eagerly produce values ("push" strategy?)
					- Using "pull" strategy would be terrible for performance (lack of pipeline parallelism?)
					- Let the programmer designate some streams as "lazy" (i.e., only run when called upon)?
						- Maybe this would also be useful for the challenge of allowing graphs of streams!
							- "Lazy" streams can have multiple consumers, but the consumer of a lazy stream can only have that one producer (otherwise it will get stuck waiting for the `valid` signal, which will never come)
							- "Eager" streams can only have one consumer (otherwise the one consumer may try to read before the other), but the consumer of an eager stream can have many producers
							- Have some special "buffer" operation (one input, one output) as interface
						- See call by push value (https://en.wikipedia.org/wiki/Call-by-push-value)?
		- ==TODO:== how to handle multiple memory accesses in one cycle?
			- Automatically insert an arbiter?
				- But then `MemRead` may take multiple clock cycles.
					- Have `MemRead` return an `Option` and leave it up to the programmer to decide what to do?
						- Or maybe have separate `MemRead` and `MemReadValid` primitives so that you can check whether read was valid without needing to do `MemRead(r).__1`
					- Make `MemRead` return a stream of length 1? Seems impractical (e.g., if you want to store value in a register, as in pointer chasing)
					- Support sequential circuits within `StmBuild`?
			- Just forbid having more than one occurrence of `MemRead` for a given BRAM? Seems far too restrictive.
				- Multiple uses may be introduced by the compiler (e.g., `let x = MemRead(m, i) in x * x`)
				- Makes it basically impossible to implement something like `let m = Stm2Mem(s) in StmConcat(Mem2Stm(m), Mem2Stm(m))`
				- What if you have something like `StmZip(StmMap(s1, (i : Int) => MemRead(mem, i)), StmMap(s2, (i : Int) => MemRead(mem, i)))`? There may or may not be conflicts depending on the rate at which `s1` and `s2` produce valid outputs
					- What if at first there are no conflicts (perhaps it is known that `s1` and `s2` never produce a valid output in the same step), but then the optimizer changes the rate of `s1` or `s2`? Seems like the optimizer should be allowed to do that as long as the stream produces the same sequence of valid elements at the end of the day
						- e.g., `StmBuild(n, i, i % 2 == 0, i : (0, i + 1))` vs `StmBuild(n, i, True, i : (0, i + 2))`
		- ==TODO:== how will `MemRead` and `MemWrite` interact with MUX? Need to avoid memory accesses in the branch that is not taken, right?
			- Replace `MemRead` with something like `MemReadIf(r, i, c)` which either reads or returns `Default`? And likewise replace `MemWrite` with `MemWriteIf(m, v, i, c)`?
				- This seems terribly non-ergonomic, so presumably we would need to translate automatically
				- ==TODO:== the current lowering pass doesn't support accumulating MUX conditions along the way, so will this require a dedicated pass?
				- ==TODO:== won't the optimizer possibly move things around and then break the expressions?
				- ==TODO:== won't this hinder certain optimizations (e.g., eliminating MUX if both branches are syntactically equal?)
		- ==TODO:== need some kind of primitive (like `Registered` in SHIR) that breaks up long combinational expression using registers?
	- ==TODO:== How to represent memory banking?
		- Nested memory (e.g., `Mem[Mem[T, m], n]`)?
		- Vector of memory (e.g., `Vec[Mem[T; m], n]`)?
			- Generally want vectors to only contain basic data types (int, bool, tuple of data, vector of data)
	- ==TODO:== How to represent double buffering?

## Off-Chip Host Memory
- Skip this because it can get quite complex