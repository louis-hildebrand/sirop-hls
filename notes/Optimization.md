## General
- ==TODO: make it possible to enable and disable individual optimizations for evaluation purposes!==
- ==TODO: look at [[Cheng et al in ASPLOS24]] for rewrite rule ideas, like "if correlation"==
- ==TODO: How to handle algebraic simplification in cases like `(x + (y + z)) - z = x + y`? In general, the terms that cancel can be arbitrarily far apart.==
	- Can have special cases (e.g., `(x + y) - y = x`), but this is not general
	- Could use equality saturation, but that seems like overkill
	- In partial evaluator, try to recognize polynomials (sum of arbitrary expressions with constant coefficients) and group like terms?
		- *Advantage:* Hopefully faster than exploring every possible configuration of parentheses using equality saturation
- ==TODO: How can I take advantage of constraints in the partial evaluator?==
	- *Example:* in `StmBuild(n, 0, i => (i + 1, i, i >= 0))`, I know that `i >= 0` so the third element in the tuple can be simplified to `True`
	- [[Coward et al in EGRAPHS23]] use `ASSUME` nodes to represent assumptions and give rewrite rules to exploit those assumptions via equality saturation. Maybe we can do something similar
## Arithmetic Expressions
- Use the `ArithExpr` library to simplify expressions
	- *Challenge:* Paul and Tzung-Han say it's mostly used for *type-level* expressions; SHIR does very limited *term-level* simplification
		- It still does a reasonable job at the term level
	- *Challenge:* The library has its own `ArithExpr` IR which is of course not the same as my interpreter's `Expr` IR. It's impossible to just use `ArithExpr` nodes as-is in my IR because (1) I can't sharply separate arithmetic from non-arithmetic expressions (e.g., `x.__1` may or may not be a scalar) and (2) I can't make `ArithExpr` a subclass of my own `Expr`
		- To get around this, I could convert `Expr` nodes to `ArithExpr` (using a special `BlackBox` node where necessary), simplify, and then convert back. The `BlackBox` node must be a subclass of `ExtensibleVar` (from the library)
			- ~~Its `equals` and `hashCode` methods must be defined carefully so that the same `Expr` is recognized as being identical by the library~~
				- Actually, every `Var` (superclass of `ExtensibleVar`) must have an `id` (which the library can generate for you if needed). Equality and order seem to be determined using the `id` in some cases
					- Keep a table (weak hash map) mapping expressions in black boxes to IDs so that I can ensure `e1.id == e2.id` iff `e1 == e2`
	- *Challenge:* After rewriting my partial evaluator to use the library, my tests are non-deterministic!
		- Running only the `StreamTests` (either in IntelliJ or using `sbt 'testOnly *StreamTests'`), for example, is fine. But if I run *all* tests using `sbt test` then most of them fail, and the number of failures varies!
		- Example of a stack trace from `sbt test`. It seems like `3 * 4` is not being simplified (?!)
			```
			[info] - StmTranspose *** FAILED ***
			[info]   java.lang.IllegalArgumentException: Stream length Mul(IntCst(3),IntCst(4)) is not an integer
			[info]   at operations.StreamTests$.stm2Seq(StreamTests.scala:14)
			[info]   at operations.StreamTests.assertStreamEqual(StreamTests.scala:32)
			[info]   at operations.StreamTests.$anonfun$new$378(StreamTests.scala:2416)
			[info]   at scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.java:23)
			[info]   at org.scalatest.OutcomeOf.outcomeOf(OutcomeOf.scala:85)
			[info]   at org.scalatest.OutcomeOf.outcomeOf$(OutcomeOf.scala:83)
			[info]   at org.scalatest.OutcomeOf$.outcomeOf(OutcomeOf.scala:104)
			[info]   at org.scalatest.Transformer.apply(Transformer.scala:22)
			[info]   at org.scalatest.Transformer.apply(Transformer.scala:20)
			[info]   at org.scalatest.funsuite.AnyFunSuiteLike$$anon$1.apply(AnyFunSuiteLike.scala:226)
			[info]   ...
			```
	- *Challenge:* Some expressions are made *worse* by the simplifier!
		- e.g., it applies distributivity to transform $x + (y + z) \cdot w$ into $x + y \cdot w + z \cdot w$. The new expression has the same number of additions but more multiplications, which seems like a bad idea
	- *Challenge:* The library does not support arbitrary boolean expressions; only predicates of the form `e1 <op> e2`, where `<op>` could be `<`, `<=`, `==`, etc.
		- ==TODO:== Add support for arbitrary boolean expressions?
			- Seems like it might be a fair bit of work, so just keep the bool-related optimizations in my project for now
	- How does the library deal with ranges on expressions?
		- Variables need some kind of range
		- All expressions have a `min` and a `max` (possibly $\pm \infty$) computed based on the ranges of variables in the expression
		- There are some basic optimizations with the min and max
			- If a variable's min and max are the same, replace the variable with that value
			- If an `IfThenElse` has a predicate of the form `v < e` and the max value of `v` is definitely less than `e`, then take the "true" branch
	- Maybe it would be simpler to remove the dependency on the library and simply use the key ideas myself. In particular:
		- Use a `Sum` node with multiple terms instead of a binary `Add` node
		- Similarly, use an $n$-ary `Prod` node instead of binary `Mul`
		- Have two separate methods for algebraic manipulation? (This is inspired by a *problem* with `ArithExpr` - distributivity making expressions more expensive)
			- `simplify`: for conservatively simplifying an expression: e.g., `x - x = 0`, `x + 0 = x`, but *not* distributivity (since this increases number of multiplications)
			- `canonicalize`: for checking equality between two terms: e.g., distribute as much as possible
	- Library doesn't seem to deal with overflow
		- ~~Basically convert expressions into this magical "infinite-bitwidth" realm and truncate when converting back?~~
			- If $x \equiv x' \pmod{b}$ and $y \equiv y' \pmod{b}$, then $x + y \equiv x' + y' \pmod{b}$
				- Because $x = x' + k b$ and $y = y' + \ell b$, so $x + y = x' + y' + (k + \ell) b$
				- Therefore, computing without overflow and truncating (`(x + y) % b`) is equivalent to computing with overflow all along (`((x % b) + (y % b)) % b`)
			- If $x \equiv x' \pmod{b}$ and $y \equiv y' \pmod{b}$, then $x y \equiv x' y' \pmod{b}$
				- Because $x = x' + kb$ and $y = y' + \ell b$, so $xy = (x' + kb)(y' + \ell b) = x'y' + b (x \ell + y' k + k \ell b)$
				- Therefore, computing without overflow and truncating (`(x * y) % b`) is equivalent to computing with overflow all along (`((x % b) * (y % b)) % b`)
			- Is `(x / y) % b` the same as `((x % b) / (y % b)) % b`?
				- No! For example: `(30 / 3) % 2^4 = 10`, but `(14 / 3) % 2^4 = 4`
					- Therefore, the simplification rule `(3 * x) / 3 --> x` is NOT correct!
			- Is `(x % y) % b` the same as `((x % b) % (y % b)) % b`?
				- No! For example: `(30 % 3) % 2^4 = 0`, but `(14 % 3) % 2^4 = 2`
		- Declare overflow to be undefined behaviour?
			- Then truncation could only happen via an explicit `TruncateTo` or `ToUnsigned`, which the partial evaluator will need to be careful with
			- Then the evaluator will be non-deterministic? :(
	- Cannot use the library for simplifying booleans, if-then-else, etc.
		- The library doesn't seem to deal with overflow, so an expression like `255 + 1 > 0` may be simplified to `true` even though it's false if we're dealing with 8-bit unsigned integers
		- The types within a relational expression (e.g., `x < y`) cannot be recovered
- How to handle integer shape conversions (e.g., `PadTo`, `ToUnsigned`)?
	- Move them to the "outside" so as to have as much "uninterrupted" normal arithmetic as possible
		- Move the padding conversions (`PadTo` and `ToSigned`) towards the leaves of the AST
		- Move the truncating conversions (`TruncateTo` and `ToUnsigned`) towards the root of the AST
	- Moving padding to the leaves and truncating to the root makes the adders/multipliers/etc. wider, which is fine for simplifying analyses and transformations but not ideal for the final hardware
		- Have a separate pass that tries to move the conversions in the opposite direction so as to shrink the bit widths?
			- This pass would need to check the ranges of expressions to decide whether it is safe to move the conversions like that
## Vectors, Tuples
- For vectors and tuples, take the existing rewrite rules from Jonathan's paper
- The partial evaluator can obviously see that `VecBuild(n, f)[i] --> f(i)`. But can it also handle things like `VecMap(v, f)[i] --> f(v[i])`?
	- *Answer:* yes!
		```
		    map(build(3, i => i + 1), x => x * x)[1]
		                                                 { for legibility, use v0 to refer to build(3, i => i + 1) }
		--> build(len({v0}), i => {v0}[i] * {v0}[i])[1]  { def of map }
		--> build(3, i => {v0}[i] * {v0}[i])[1]          { len(build(n, ...)) --> n }
		--> build(3, i => (i + 1) * (i + 1))[1]          { build(n, f)[i] --> f(i) }
		--> (1 + 1) * (1 + 1)                            { build(n, f)[i] --> f(i) }
		--> 4                                            { const + const --> const and const * const --> const }
		```
- What about `VecMap(VecMap(v, f), g)`?
	- *Answer:* yes!
		```
		    map(map(v, f), g)
		--> build(len(map(v, f)), i => g(map(v, f)[i]))                                      { def of map }
		--> build(len(build(len(v), j => f(v[j]))), i => g(build(len(v), j => f(v[j]))[i]))  { def of map }
		--> build(len(v), i => g(build(len(v), j => f(v[j]))[i]))                            { len(build(n, ...)) --> n }
		--> build(len(v), i => g(f(v[i])))                                                   { build(n, f)[i] --> f(i) }

		And then...
		    map(map(v, f), g)[k]
		--> build(len(v), i => g(f(v[i])))[k]  { shown above }
		--> g(f(v[k])))                        { build(n, f)[i] --> f(i) }
		```
## Stream Canonicalization
- Not really needed anymore now that `StmBuild` can have a separate equation per accumulator!
- ~~Canonicalization rules:~~
	- Always expand `acc` to `Tuple(acc.__0, acc.__1)` if `acc` is a 2-tuple? This requires the type checker to have run.
	- Make the accumulator always a tuple
	- Flatten accumulator
	- Remove empty tuples from accumulator
	- If the seed includes any streams, move them to the lowest indices
	- etc.
## Stream Fusion
- Combine consecutive `StmBuild`s into one
- Is this always beneficial?
	- *Pros:* avoid overhead of handshake interface, may reveal other optimization opportunities (e.g., `s |> StmMap(+5) |> StmMap(-5)`)
	- *Cons:* reduces the amount of pipeline parallelism (==right?==)
- In some cases (e.g., higher-order functions like `StmMap` or `StmScanInclusive` that must reset their inputs), it's required!
	- ==TODO:== Does fusing everything make it more difficult to detect cancellation, like `Stm2Vec . Vec2Stm`?
		- ==TODO:== Add a primitive (`StmReset`?) to delay fusion and replace it with existing primitives (and perform fusion) during or right before hardware generation?
			- Implementations:
				- Higher-order stream operator that takes as input a stream and a function from stream to stream to be repeated?
				- ~~Could be used much like `StmNext()`; you're allowed to update stream accumulator `acc` using either `acc`, `StmNext(acc).__0`, `StmReset(acc)`, or `StmReset(StmNext(acc).__0)`?~~
					- But there may be a whole chain of operators to be reset. How to know which stream is input and which streams need to be reset?
			- *Problem:* would this hinder things like map fusion?
## Stream Accumulator Removal
- Can remove accumulator if:
	- It is unused
	- Its value is constant
		- In this case, replace each occurrence with that constant
		- This can be shown basically by induction: assume it is constant, see if it's still constant at the next cycle
	- Its value can be found as a function of $t$, where $t$ starts at 0 and counts up at each cycle ("induction variable elimination")
- ==TODO:== Also look at techniques for *reducing* (without necessarily fully eliminating) size of vector?
	- ==TODO:== Replace vector with tuple? Maybe then some other rule(s) will have an easier time
	- ==TODO:== Would it help to simplify operator implementations as much as possible, even at the cost of unoptimized performance? For example, in `StmSlide` convert everything to one huge vector upfront so that the access patterns are easier to analyze
		- Can we then rewrite this expanded version to use as little memory as possible? e.g., have one "write" index and one "read" index and make the output valid as long as write index > read index?
### Induction Variable Elimination
- Background: [[Encyclopedia of Parallel Computation - Automatic Parallelization]], [induction variables](https://en.wikipedia.org/wiki/Induction_variable)
- In this case, add an accumulator for $t$ and remove the original
- Example applications:
	- `Stm2Vec(StmCount(5))`, where the $i$-th element of the vector is $i$ and therefore the stream can be simplified to `StmCst(1, VecBuild(5, (i: Expr) => i))`)
	- `Vec2Stm(VecBuild(5, (i: Expr) => i + 1))`, where the $i$-th element of the stream is $i + 1$ and therefore the stream can be simplified to `StmBuild(5, 0, (i: Expr) => Tuple(i + 1, i + 1, True))`
- Can be used to deduplicate counters, for example! Express all the counters in terms of $t$
- *Problems:*
	- It may not be possible to find a closed form (e.g., for `StmReverse`)
	- The closed form may be more expensive to compute than the recursive form
		- e.g., if $x_0 = 0$ and $x_{t + 1} = x_t + 3$, then $x_t = 3t$, and multiplication by three is presumably pretty expensive compared to adding three
		- e.g., if recursive form involves multiplication then the closed form will involve *exponentiation*, which we surely want to avoid
- Probably best to use this along with equality saturation so we can explore different options and choose the best one
	- e.g., if we have a counter that increases by $\Delta$ at each tick, then the closed-form solution involves multiplying by $\Delta$. Maybe worth it if $\Delta$ is a power or two, if this enables other good optimizations, etc. but probably not worth it in general.
- How can you do this when integers can have different bit widths?
	- *Example:* `(x : i8) -> (-128, x + 32)`
		- This produces the sequence -128, -96, -64, -32, 0, 32, 64, 96
		- Closed form in the abstract: `-128 + 32*t`
		- Closed form taking into account bit widths:
			- Let `(t : i8) -> (0, t + 1:i8)` and then `x(t) = -128:i8 + 32:i8 * t`?
				- But `32:i8 * t` produces the sequence 0, 32, 64, 96, 128 <-- overflow! So originally there was no overflow, but now there is
			- Let `(t : i32) -> (0, t + 1:i32)` and then `x(t) = trunc(-128:i32 + 32:i32 * t, 8)`?
				- But the truncation is inconvenient :(
					- e.g., some other patterns in the recurrence solver look for expressions that are equivalent to `t < K`. In the abstract, `-128 + 32*t < K <==> 32*t < K + 128 <==> t < (K + 128) / 32` (maybe with ceiling or floor somewhere). But with truncation, this is not the case (because what if the result is not enough to fit in 8 bits?)
						- ==TODO:== maybe say that the input of `TruncateTo` must fit in the target type, otherwise it's undefined behaviour? But then what do you do if you wanted to truncate something that may be outside the range?
## Stream Delay Removal
- If you can show that the first $k$ stream outputs are all `None` *and* you can find the value of every accumulator at step $k$, then you can skip the first $k$ cycles!
	- Be careful about stream inputs: need to show that you never read from them in the first $k$ cycles
- ==TODO:== Generalize this a bit? For example, what if you have a stream that emits a valid element every other cycle?
## Stream Accumulator Range Analysis
- In some cases, we can find ranges for accumulator elements
	- e.g., if initial value is $z$ and the value is non-decreasing, then the accumulator is greater than or equal to $z$ by induction
	- Can we use this same trick for identifying constant-valued accumulator elements?
		- Probably not *always*, since the analysis described above that searches for constant-valued accumulator elements can actually look at multiple accumulator elements at once, whereas the range analysis only looks at one at a time (right?)
			- e.g., in `StmBuild(n, Some((i, j)), i : (0, i * j), j : (1, j + i))`, both accumulator elements are constant, *but you can't tell by looking individually*
## Stream Accumulator Re-Indexing
- Let $S \triangleq \texttt{StmBuild}(n, z, a \mapsto (f_n(a), f_o(a)))$ and let $g$ be an invertible function. Then $$S = \texttt{StmBuild}(n, g(z), a \mapsto(g(f_n(g^{-1}(a))), f_o(g^{-1}(a))))$$
	- The new stream will have exactly the same sequence of outputs as the original, including $\texttt{None}$ values.

- *Example:* suppose $S \triangleq \texttt{StmBuild}(n, c, a \mapsto (a + 1, a - c))$. This is a simple counter that counts up by one from zero. Using $g(x) = x - c$, we can rewrite $S$ to the more standard form of the counter, $\texttt{StmBuild}(n, 0, a \mapsto (a + 1, a))$
- Compared to induction variable removal:
	- In general, there are definitely cases that could be done using induction variable removal but not accumulator re-indexing (because the latter only works for invertible functions)
		- *Example:* `StmBuild(n, 1, acc => (acc * -1, acc, True)) = StmBuild(n, 0, t => (t + 1, if t % 2 === 0 then 1 else -1, True))`
	- ==Are there any cases that can be simplified with accumulator re-indexing but not induction variable removal?==
		- I can't think of any off the top of my head. Maybe induction variable removal is strictly more powerful than accumulator re-indexing
## Example Stream Transformations
- Look at existing rewrite rules in SHIR and show how they can be expressed using multiple smaller rewrite rules in minimalist IR
	- Many SHIR rewrite rules were written because human can intuitively see that two expressions are equivalent and then they think about the rewrite rules necessary to translate from one version to the other. Look for opportunities to do the same thing with minimalist IR (e.g., MUX-based vs shift register-based `Vec2Stm`)
- Simplifying `Stm2Vec(StmCst(n, c)) --> StmCst(1, VecBuild(n, i => c))` and `Stm2Vec(StmRange(n, z, delta)) --> StmCst(1, VecBuild(n, i => z + i * delta))`:
	- After induction variable removal I get the following expressions, which are close but still have some delay. How can I remove that delay?
		```
		// Stm2Vec(StmCst(n, c))
		StmBuild(
		  1,
		  t(0),
		  (acc) =>
		    t(
		      t(acc.__0 + 1),
		      VecBuild(n, i => c),
		      acc.__0 >= n - 1
		    )
		)
		// Stm2Vec(StmRange(n, z, delta))
		StmBuild(
		  1,
		  t(0),
		  (acc) =>
		    t(
		      t(acc.__0 + 1),
		      VecBuild(n, i => z + (((acc.__0 + (i + 1)) - n) * delta)),
		      acc.__0 >= n - 1
		    )
		)
		```
		- Simply say that, if `StmLength(s) = 1`, then `s = StmCst(1, StmNext(s).__1)`?
			- *Problem:* This wouldn't work for a stream whose length is small but greater than one (e.g., 2).
				- *Example:* `StmSuffix(StmCount(1000), 2)`, which can be simplified to `StmRange(2, 998, 1)` to avoid the 998-cycle delay
			- *Problem:* Seems like this wouldn't work for a case where the number of steps until a valid output is not statically known
				- *Example:* `StmAccess(StmRange(n, z, delta), i)`, which can be simplified to `StmCst(1, z + i * delta)`
			- *Problem:* Need to watch out for infinite loops in the partial evaluator: this rule would make it so that the `StmBuild` and `StmNext` cases each potentially recursively call the other
		- Possibly better approach: chain of transformations 
			1. Shift accumulator to simply skip the delay (i.e., start at `n - 1` rather than 0)
			2. Perhaps "re-index" the accumulator so that it starts at 0 (in this case, it can be done with induction variable removal)
				- We would then have:
					```
					// Stm2Vec(StmCst(n, c))
					StmBuild(
					  1,
					  t(0),
					  (acc) =>
						t(
						  t(acc.__0 + 1),
						  VecBuild(n, i => c),
						  acc.__0 + n - 1 >= n - 1
						)
					)
					// Stm2Vec(StmRange(n, z, delta))
					StmBuild(
					  1,
					  t(0),
					  (acc) =>
						t(
						  t(acc.__0 + 1),
						  VecBuild(n, i => z + (((acc.__0 + n - 1 + (i + 1)) - n) * delta)),
						  acc.__0 + n - 1 >= n - 1
						)
					)
					```
			3. Using the fact that the `valid` term is now always satisfied in each case, we can simplify them to `True`
			4. In the `Stm2Vec(StmRange(n, z, delta))` case, need to simplify `(acc.__0 + n - 1 + (i + 1)) - n` to `acc.__0 + i`.
				- This still leaves one addition too many. Maybe we can then apply the observation above that `s = StmCst(1, StmNext(s).__1)`
			5. Since `acc` is unused, we can remove it
- Simplifying `Vec2Stm(Stm2Vec(s)) --> s` and `Stm2Vec(Vec2Stm(v)) --> v`
	- *Approach 1:* partially evaluate and check that we get the sequence `StmNext(s).__1`, `StmNext(StmNext(s).__0).__1`, `StmNext(StmNext(StmNext(s).__0).__0).__1`, and so on
		- *Advantage:* easy
		- *Advantage:* more general than just `Stm2Vec` and `Vec2Stm`
		- *Problem:* requires static and small length
		- *Problem:* if the `Vec2Stm(Stm2Vec(s))` is somewhere in the middle of a pipeline of stream operators, we need to try multiple start and end points to find which part of the pipeline can be simplified (runtime is quadratic in length of pipeline)
		- *Problem:* probably won't work after stream fusion, and stream fusion is required in some cases (e.g., `StmMap`, `StmScanInclusive`)!
			- Add a primitive (`StmReset`?) to delay fusion and replace it with existing primitives (and perform fusion) during or right before hardware generation?
				- Implementations:
					- Higher-order stream operator that takes as input a stream and a function from stream to stream to be repeated?
					- ~~Could be used much like `StmNext()`; you're allowed to update stream accumulator `acc` using either `acc`, `StmNext(acc).__0`, `StmReset(acc)`, or `StmReset(StmNext(acc).__0)`?~~
						- But there may be a whole chain of operators to be reset. How to know which stream is input and which streams need to be reset?
				- Intuitively, we should be able to combine consecutive `StmReset`s if `numOut` for the input matches `numIn` for the output. This would probably be beneficial for revealing opportunities for cancellation
	- *Approach 2:* ==TODO:== for any `StmBuild` with a vector accumulator, check if the vector accumulator acts like a shift register (i.e., `nextF` assigns to it the value `IfThenElse(i > 0, VecShiftLeft(v, StmNext(s).__1), v)` and `v` is initialized to a vector of the correct length and `i` also has the right behaviour and ...). If so, replace each `VecAccess(v, i)` (where `i` goes from 0 to )
		- *Advantage:* works with large or symbolic lengths
		- *Disadvantage:* seems brittle, too specific to `Stm2Vec`
	- *Approach 3:* somehow find some rewrite rules that allow cancelling `Stm2Vec` and `Vec2Stm` without needing static stream length
		- Some optimizations that may be helpful:
			- ==TODO:== Merge identical accumulators?
				- Some induction variables (in particular, simple counters) can already be de-duplicated in this way. But what if there's a variable that cannot be expressed as a function of $t$?
			- ==TODO:== Add pattern for bounded counters in `StmInductionVarRemovalPass`
				- *Example:* `a' = if (t < T) then a + 1 else a`
				- *Example:* `a' = if (t < T) then a else a + 1`
- Simplifying `StmTranspose(StmTranspose(s)) --> s`:
	- Define `StmTranspose` as `Stm2Vec . VecTranspose . Vec2Stm`, so the stages cancel out (as long as `Vec2Stm` and `Stm2Vec` cancelling works)
		```
		    StmTranspose . StmTranspose
		--> Stm2Vec . VecTranspose . Vec2Stm . Stm2Vec . VecTranspose . Vec2Stm
		--> Stm2Vec . VecTranspose                     . VecTranspose . Vec2Stm
		--> Stm2Vec .                                                 . Vec2Stm
		--> identity
		```
- Making reduction trees:
	- One way: unfold the `Iterate()` and then re-associate the function calls
	- Another way: use `StmSplit` somehow
	- Current version of `VecFold` uses `Iterate`. The hardware for `Iterate` given by Prof. Dubach seems to take multiple cycles. The optimizer could unfold the expression to do it all in a tree-like manner using `VecSplit` and `VecJoin`, right?
		- Seems like it should be possible by rewriting `VecFold(v, z, f)` to a form that involves splitting into chunks first. But then this rewrite rule would need to be aware of the implementation of `fold` (right?), which seems a bit hacky. Alternatively, make fold a primitive, but then the IR isn't so minimalist anymore...
			- Hopefully this will already be handled by rewrite rules at the lowest level, no need for special rules like this! (Would be very cool!) But maybe we do need domain knowledge after all (e.g., need to check associativity)
