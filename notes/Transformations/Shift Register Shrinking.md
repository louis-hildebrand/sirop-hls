## Example

Consider the following program, where the shift register `v` is needlessly large:
```
sbuild(N @ 5)(undefined, (buf[0], buf[1], buf[2]), true) {
	(buf: Vec[u8, 4]) = {
		init: undefined,
		next: buf.VecShiftLeft(sdata(p))
	}
} {
	(p: Stm[u8, N] @ 0) = {
		stm: input,
		ready: true
	}
}
```

The program could be rewritten as follows to produce the same logical output with lower latency and lower resource usage:
```
sbuild(N @ 5)(undefined, (buf[0], buf[1], sdata(p)), true) {
	(buf: Vec[u8, 2]) = {
		init: undefined,
		next: buf.VecShiftLeft(sdata(p))
	}
} {
	(p: Stm[u8, N] @ 2) = {
		stm: input,
		ready: true
	}
}
```

## Preserving Necessary Delays

In some cases the shift register's length is actually needed. For example, `StmMapDotCascaded` has a parameter for the number of pipeline registers to enable within the DSPs. It implements this by adding shift registers of the requested length. We don't want those shift registers to be removed; it would make that parameter useless.

I see two ways of doing this:

1. Add an `sbuild` annotation that says a given accumulator should be totally exempt from [[Shift Register Merging]] and [[Shift Register Shrinking]]
	- e.g., in the first example, adding the annotation `noshrink(buf)` would completely block shift register shrinking
	- e.g., in `StmMapDotCascaded`, annotate the accumulators corresponding to the DSP pipeline registers with `noshrink` so that they don't get merged nor shrunk
	- Need to be careful to obey this annotation during [[Shift Register Merging]] and [[Shift Register Shrinking]]
	- Need to be careful to preserve this annotation even through other transformations (e.g., full accumulator deduplication, renaming accumulators)
	- Once I get to [[DSP Selection]], the accumulators from `StmMapDotCascaded` should still be there and so exactly the requested number of DSP registers should be enabled
2. Add an `sbuild` "sink" annotation, which contains expressions that should be considered "used" for the purpose of [[Shift Register Shrinking]]
	- e.g., in the first example, adding the annotation `sink=buf[3]` should block shift register shrinking entirely and `sink=buf[2]` should mean the updated shift register will have length 3
	- e.g., in `StmMapDotCascaded`, set `sink=(stage0_x_pipe[delay-1], stage1_x_pipe[delay-1], ...)`
	- Need to be careful to obey this annotation during [[Shift Register Shrinking]]
	- Need to be careful to update this annotation even through other transformations and renamings (e.g., renaming accumulators)
	- Once I get to [[DSP Selection]], the shift registers from `StmMapDotCascaded` may have been merged with others. In some cases, the backend may therefore be able to enable *more* of the DSP registers

Approach (2) sounds a tiny bit better
1. I expect it'll be a bit easier to implement full accumulator deduplication
	1. With `noshrink` annotation: block this transformation entirely? Ensure there's only one `noshrink` accumulator per equivalence class and retain its name?
	2. With `sink` annotation: just perform the necessary substitutions in the `sink` expression. It's fine if `buf[3]` gets replaced with `v[3]`, `w[5]`, whatever
2. The final IR will be a bit more readable for something like an FIR filter: just one shift register, rather than a bunch of small ones
3. The generated VHDL will be a bit better, since the DSPs will already have as many pipeline registers enabled as the Sirop compiler thinks is possible
	1. Not a huge deal, since Quartus' DSP register packing can most likely add in any registers I missed