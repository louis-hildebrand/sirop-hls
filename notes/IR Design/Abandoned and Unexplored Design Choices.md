## Abandoned Designs
- Originally, had `StmBuild(n, z, f)` where `z : A` and `f : A -> (A, B)` (i.e., all accumulators are tupled together and `f` returns both output and next accumulator value)
	- Many operations are more convenient when accumulators are separate - removing an accumulator element, adding an accumulator element, stream fusion, etc.
- Had primitive called `Iterate` which applies a function `n` times and only returns one data element at the end (quite similar to original `StmBuild`, but returns one element rather than scalar)
	- Can be used to implement things like `StmFold`, whose output is only valid at the end
	- Unclear how to synthesize this when the output is a stream
	- Unclear how this could be used for cases with alternating valid/invalid elements, like taking sum of rows in 2D stream
- Originally, had primitive `StmNext : Stm[T; n] -> (Stm[T; n - 1], T)`
	- Use `StmNext(s).__1` to get data and `StmNext(s).__0` for next stream state
	- *Problems:*
		- Semantics not defined quite the way I wanted: `StmData(empty)` is ok (see section on error handling) but actually trying to move to the next element of an empty stream will lead to deadlock
		- Too flexible: need extra syntactic restrictions to ensure the update expression for `s` is either `s` or `StmNext(s).__0`
			- *Suspicious expr:* `StmBuild(n, s, (acc: Expr) => Tuple(acc, StmNext(acc).__1))`, which repeatedly reads the first element of the stream
			- *Suspicious expr:* somehow evaluating `StmNext(StmNext(s).__0)` before `StmNext(s)`
			- *Suspicious expr:* `StmZipAlternating(a, b)`
				```
				StmBuild(
				  StmLength(a)(),
				  SSome(Tuple(StmNext(s0)().__1, StmNext(s1)().__1)())(),
				  Map(
					s0 -> (a, StmNext(s1)().__0),
					s1 -> (b, StmNext(s0)().__0)
				  )
				)()
		
				```
- Stream output used to be `Option[T]`, where `Option[T]` is really just syntax sugar for `(T, Bool)` (`Some(e) = (e, true)` and `None = (Default[T], false)`)
	- *Advantage:* makes the source language a bit nicer to read and write
	- *Problems:*
		- It introduces spurious dependencies that may cause errors
			- `StmData` cannot be used in the update expression for input streams. But what if you have something like `(f(StmData(s)), c).__1` (as you would if you used `Option[T]`)?
				- This is indeed a problem in the test case `StmMap:2D-2D:StmShiftLeft`
			- When replicating a stream (e.g., when lowering a vector of streams), the `valid` expression must not depend on the vector index `i`. But what if you have something like `(f(StmData(s)), c).__1` (as you would if you used `Option[T]`)?
				- This is indeed a problem in the test case `VecBuild:StmSum`
		- You need to do a bit of extra work to get the `data` and `valid` signals separately
			- e.g., `IsSome(x)().tchk().lower()` to get the `valid` signal on its own
		- In the `None` case you still need some data (e.g., 0) to put as the first tuple element. So `if (c) then Some(e) else None --> if (c) then (e, true) else (0, false)` when really we could just write `(e, c)`
			- Not very hard to write an optimization pass for this, but if the `data` and `valid` signals are separate to begin with then there's nothing to do
		- *Note:* It's generally not hard to address these problems by optimization, but we really shouldn't rely on the optimizer for simply synthesizing correct hardware
			- Decreases confidence in the correctness of the result, since the optimizer is the most complex part of the compiler
			- Makes it harder to evaluate results with vs without optimization
- Originally, streams could be nested by having the outer stream literally produce outputs which are themselves `StmBuild`
	- *Example:* `StmCount2D`
		```
		StmBuild(n, 0, (i: Expr) => Tuple(i + 1,
			StmBuild(m, 0, (j: Expr) => Tuple(j + 1,
				Tuple(i, j)
			))
		))
		```
	- *Problem:* unclear how this can be converted to hardware
	- ~~Start with existing stream IR and flatten to facilitate hardware generation? (i.e., lower and then flatten?)~~
		- This would still leave the question of how to implement `StmSplit` and `StmJoin` well...
			- Maybe that doesn't matter, as long as we can guarantee that the parts which prevent hardware generation are all removed during flattening
				- If that's the case, why not implement them using `Stm2Vec` and `Vec2Stm`? That might make partial evaluation easier...
		- How shall we flatten?
			- Prof. Dubach's suggestion: insert `StmSplit` and `StmJoin` before and after `StmBuild`s as necessary, and somehow get the partial evaluator to optimize everything away
				- Not clear that this will work 100% of the time, and in any case it seems sketchy to absolutely require partial evaluation simply for generating any hardware
			- Maybe the stream fusion rule, or something similar, would help here
	- ~~Make StmSplit and StmJoin primitives?~~
		- Defeats the purpose of the minimalist IR, doesn't it?
	- ~~Tweak the semantics of `StmBuild` to facilitate implementing `StmSplit` and `StmJoin`?~~
		- Somehow make it so that streams inside accumulator are *not* reset between inner calls to `StmBuild`? That way, we could implement `StmSplit` as follows and straightforwardly translate it to hardware
			```
			StmBuild(
				n / m,
				Tuple(),
				(_: Expr) =>
					Tuple(
						Tuple(),
						StmBuild(m, input, (acc: Expr) => Tuple(StmNext(acc).__0, StmNext(acc).__1, True)),
						True
					)
			)
			```
			- *Disadvantage:* Could there be edge cases here (e.g., related to memory accesses)?
		- Add a primitive that lets you inspect the value of the accumulator in `StmBuild` *after* the `StmBuild` is done? Or just have `StmBuild` return that directly?
			- *Advantage:* Maybe this primitive could be used to implement accumulators shared across nested `StmBuild`s (as in `StmSplit`), hopefully opening the way for `StmSplit` to be translated to a no-op in hardware
			- *Disadvantage:* Extra primitive
			- *Disadvantage:* ==Could the programmer abuse this primitive to create technically-semantically-valid-but-uncompilable programs (e.g., by doing something else with it instead of using it to update the accumulator)?==
		- *Disadvantage:* Seems like that would be tricky to implement in the interpreter
		- *Disadvantage:* Even if this works exactly as expected, it still leaves some issues related to nested streams:
			- How to handle the double `StmNext()` required for consuming nested streams?
			- How to implement `StmJoin` in a sensible way? (Probably fundamentally the same issue as above.)
- Originally, had `HasNext` primitive for streams
- Originally, could ask for length of stream in accumulator
	- The length keeps changing
	- Easy fix: keep track of the current length yourself using a down-counter
	- Easier hardware generation (no need to maybe/maybe not include `length` signal, easier stream fusion)
	- Alternative design: use dependent types + adapt hardware generator to add the implicit counter?
		- *Advantage:* more concise programs in some cases?
		- *Disadvantages:*
			- Special case makes hardware generation more complex
			- Making the counter explicit may reveal optimization opportunities
			- Dependent types are overkill for our use case (too complex)
## Unexplored Design Alternatives
- ==TODO:== Instead of every stream keeping track of its own length, just have a `done` signal (maybe as part of the stream data)?
- Should there also be a `StmSeed` primitive to get the seed? Seems like the lack of that destructor breaks symmetry.
	- At the moment, it's not clear what the practical use for this would be
- Why bother with separate `VecLength` and `StmLength` instead of just one `Length`?
	- Just a naming thing, not a huge deal
- Streams in this IR don't seem like monads, they still seem very much like Java iterators (except that `next()` also returns the new state of the iterator). Think about whether this really qualifies as a "monadic" approach. If not, would a monadic approach be more convenient?
- Build streams in a way that's more similar to cons lists: have an empty stream primitive (comparing to empty stream corresponds to `last` signal) and a `cons`-like primitive?
	- *Advantage:* simpler building blocks; only `Iterate()` needs to have a counter and accumulator and whatnot
	- *Problem:* then stream building would *always* require `Iterate`, and building streams with `Iterate` is exactly what I'm trying to avoid because I haven't got the foggiest idea how to convert it to hardware
		- Maybe with `StmCons` it'll be clearer how to generate hardware
	- Could support this *and* the length. If the stream is never compared to empty stream, then no need for `last` signal. If the length is never checked, then no need to keep track of that.
	- `StmBuild` can already express `Cons` (using `IfThenElse` to insert an element at the front), so this would not be more expressive
