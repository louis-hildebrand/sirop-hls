## Motivation

The current semantics of the `no_handshake` mode (as of 2026-08-05) are not satisfactory.

- I want the optimizer to be able to add or remove latency as required. For example, after fusing `StmCascade` with `StmSlideStartingWith` and merging the shift registers, the remaining shift register will be needlessly large. It should be possible to shrink it, but that’s not currently allowed because it would change the output stream. (It would reduce the number of garbage outputs at the beginning. At the end of the day we don’t really want to think about how many there are, but we’re forced to for latency matching purposes.)
- Latency matching should work completely, even for stream operators like `StmSlideStartingWith`. Currently, the programmer needs to think carefully about how many garbage elements there are in the output of `StmSlideStartingWith` and manually add delay on one branch to compensate for the DSP pipeline settings in the other branch.
- Testing can be kind of a pain: need to figure out how many garbage outputs you’ll have at the beginning. It would be nice to specify some condition they must all satisfy and let the compiler add or remove as required.
- Debugging can be kind of a pain: if the latency is not as expected, _everything_ will look wrong (frameshift mutation). Ideally first get the logical outputs right, then make only minor adjustments to get the physical outputs right.
- Outputs in the REPL are needlessly big in some cases (e.g., `StmSlideStartingWith`). Would be nice to have the option to hide all the garbage outputs at the beginning.
- In theory, the semantics of Sirop in the `no_handshake` mode don't say anything about the outputs for the first few cycles
  - e.g., if the design has a latency of 1 cycle, the output immediately after a reset is technically not defined in Sirop
    - It just so happens that VHDL registers of type `boolean` default to `false`, which is what we want in the LASP, but relying on this seems a bit hacky
  - In the normal mode this is fine because the `valid` output will be `false`. But in `no_handshake` mode we don't actually instantiate the `valid` output
    - Why can't we just have a `valid` output managed by the compiler, as in the normal mode?
      - In general a component may have many `valid` bits: e.g., it may produce _two_ Avalon streams, each with an independent `valid` bit
      - `StmSlideStartingWith` would probably need to be implemented with a counter to force the `valid` output to `false`, so it seems like the quality of the generated hardware would suffer
- The accumulators in a given `sbuild` are only updated once all producers have _logically_ valid data.
  - This imposes some overhead: you need a `go` port in each subcomponent and you need a `start_delay` component that basically counts off the required number of cycles
  - Even with the `start_delay` mechanism this is not satisfactory, since it doesn't account for the possibility of the inputs to the generated component having some delay
- Impossible to implement things like `StmDrop`, `StmConcat`, etc. that don't exactly follow the template of "valid data arrives at time T and valid data departs at time T+1"
  - And, as mentioned before, `StmSlideStartingWith` _can_ be implemented but in a hacky way that requires manual latency matching, hard-coding latency in test outputs, etc.

## Requirements

It may be helpful to distinguish the "logical" output sequence (the values we care about) from the "physical" output sequence (the values we observe at the output of the physical circuit).
The logical sequence will always be a suffix of the physical output sequence.
I'll use the notation `[x1, x2, x3, ...]s ++ [y1, y2, y3, ...]s` to denote the logical sequence `[y1, y2, y3, ...]s` and the physical sequence `[x1, x2, x3, ...]s ++ [y1, y2, y3, ...]s`.
I'll use the term "physical prefix" for the part of the physical output that is _not_ part of the logical output, i.e., `[x1, x2, x3, ...]s`.

- Latency matcher:
  - Need to be able to statically determine the output latency of a given node (i.e., after reset, how many clock cycles does it take for the output of this node to be _logically_ valid?)
    - Otherwise it would obviously be impossible to know how much delay to add
  - Semantics must allow _prepending_ garbage elements to any stream
    - But there may need to be restrictions: e.g., don't want to prepend (1, true) when you expect all the garbage elements to satisfy `@(data, valid) => !valid`
      - In this case, could probably just duplicate initial (physical) output from `sbuild`? If so, semantics must specifically allow _duplicating_ first physical output (e.g., transforming `[(undefined, false)]s ++ [(1, true), (2, true), ...]s` to `[(undefined, false), (undefined, false)]s ++ [(1, true), (2, true), ...]s`)
        - What if there is no previous `sbuild` (i.e., the input to the fork is an input to the component)?
          - Assume `undefined` and emit a _warning_ prompting the user to provide a value or explicitly allow prepending `undefined`?
          - Emit an _error_ prompting the user to provide a value or explicitly allow prepending `undefined`?
          - Always require the user to provide a value (possibly `undefined`)?
- Fusion:
    - Semantics must allow _dropping_ garbage elements from any stream
- Fission:
  - Semantics must allow _prepending_ garbage elements to any stream
    - May need restrictions here; see requirements related to latency matcher
      - In this case, I don't think it's feasible to do it automatically in _every_ case
      - If the `sbuild` to fission has `undefined` initial value: easy
      - If the `sbuild` to fission has non-undefined initial value: need to find inverse?
- (Long term:) would be nice to let the latency be an expression, not necessarily a static integer. For example, might want the size of the shift register to be a generic, not hard-coded.
- Would it be OK to just _check_ whether latency matches and require the user to manually add delay if there’s a mismatch?
  - Probably not
  - The compiler has transformations (fusion and fission) that automatically change the latency. It would probably be extremely frustrating for the user to write a program with matching latency only to have it destroyed by fusion or fission.
  - How would I prevent the fusion pass from deleting the delay added by the programmer?
- `sbuild` should start updating its accumulators immediately after reset
  - If I stick to the current scheme of freezing the accumulators until the producers have valid data, there will surely be excessive overhead (see `start_delay` component in current implementation)
    - If your program happens to fuse down to a single `sbuild` then great, but for multi-stage pipelines `start_delay` will certainly add overhead
    - If you have something like `StmSlideStartingWith` with a big shift register, surely disabling the whole shift register for a while would be quite expensive (for Fmax and maybe also for ALM usage, depending on whether Quartus manages to move this to the clock enable port of the registers)
    - I think having the accumulators start updating immediately is more general: the programmer should be able to implement something like `start_delay` if they need it
  - This does require imposing some restrictions on the compiler and on the programmer
    - Programmer: code should produce the same logical output even if the input is changed by duplicating or dropping the head of the physical prefix (arbitrarily many times)
      - Example: the Sirop program must produce the same logical output for all of these inputs:
        - `[]s ++ [(0, true), (1, true), (2, true)]s`
        - `[(undefined, false)]s ++ [(0, true), (1, true), (2, true)]s`
        - `[(undefined, false), (undefined, false)]s ++ [(0, true), (1, true), (2, true)]s`
        - `[(undefined, false), (undefined, false), (undefined, false)]s ++ [(0, true), (1, true), (2, true)]s`
        - etc.
      - Is this too much to ask of the programmer?
        - Is it true that all the current built-in stream operators have this property?
          - `StmCst`, `StmCount`: all stream sources (i.e., those that don't take any streams as input) trivially have this property
          - `StmMap`, `StmZip`, `StmMap2`: all stateless stream operators have this property
          - `StmSlideStartingWith`: intuitively, it does
        - TODO: If the individual `sbuild` nodes each have this property, does the overall program have this property?
          - Expressions like `StmZip(StmCount(N), s)` are very suspicious, since they clearly depend on the timing of `s`
            - Maybe I can fix this case by introducing a generic parameter `INPUT_LATENCY` for the latency of the accelerator inputs. The compiler would then be forced to insert a delay of `max(INPUT_LATENCY - 1, 0)` after the counter and `max(1 - INPUT_LATENCY, 0)` after `s`, which would prevent fusion
        - If both of the above hold, then the question becomes "is this too much to ask of the person implementing new streaming operators?" And I think it's reasonable to assume they can handle it; parallel patterns in general rely on a competent library author
    - Compiler: need to be careful about fusion
      - Consider the expression `StmZip(StmCount(N), s.StmMap(x => x*x).StmMap(x => x*x))`
      - Expected output if `s = [X]s ++ [0, 1, 2, 3, 4]s` and `N = 5`:
        - `StmCount(N)`:                            `[X]s ++ [0, 1, 2, 3, 4]s`
        - `StmCount(N)` (delayed):                  `[X, X, X]s ++ [0, 1, 2, 3, 4]s`
        - `s.StmMap(x => x*x)`:                     `[X, X]s ++ [0, 1, 4, 9, 16]s`
        - `s.StmMap(x => x*x).StmMap(x => x*x)`:    `[X, X, X]s ++ [0, 1, 16, 81, 256]s`
        - `StmZip`:                                 `[X, X, X, X]s ++ [(0, 0), (1, 1), (2, 16), (3, 81), (4, 256)]s`
      - But if we naively fuse `StmCount` into `StmZip`, we'll be unable to perform latency matching and the output will be incorrect:
        - `[(X, X), (0, X), (1, X), (2, X)]s ++ [(3, 0), (4, 1), (5, 16), (6, 81), (7, 256)]s`
      - Cases:
        - 0 producers: nothing to fuse
        - 1 producer: should be fine in general. By the language semantics, the compiler is allowed to drop the head of the physical prefix (i.e., delete the output register). Then we might as well combine the two subcomponents
        - 2+ producers: suppose all producers have the same output latency _and_ the same "internal latency" (output - input latency). Then the compiler can fuse all the producers, or none of them. It _cannot_ fuse some but not others, since this will break the latency matching property
          - TODO: actually, it should be fine to fuse if the producer is stateless, right?
          - TODO: what can I do if the producers have different internal latencies (e.g., zipping together `StmSlide`s with different buffer sizes)? Just give up?
          - Example: `StmZip(StmCount(N), s.StmMap(x => x*x).StmMap(x => x*x))`
            - You can _not_ fuse `StmCount` with `StmZip`, as shown above
            - You _can_ fuse the second `StmMap` with `StmZip`. The result will look like `StmMap2(StmCount(N), s.StmMap(x => x*x), @(i, x) => (i, x*x))`
              - `StmCount(N)`:              `[X]s ++ [0, 1, 2, 3, 4]s`
              - `StmCount(N)` (delayed):    `[X, X]s ++ [0, 1, 2, 3, 4]s`
              - `StmMap`:                   `[X, X]s ++ [0, 1, 4, 9, 16]s`
              - `StmMap2`:                  `[X, X, X]s ++ [(0, 0), (1, 1), (2, 16), (3, 81), (4, 256)]s`
- Each producer in `sbuild` should support a delay annotation, indicating when the input is expected to start
  - Delay is relative to the delay of the `sbuild` (which is ultimately an arbitrary number of cycles after reset)
    - Very much like events and availability intervals in Filament (Nigam et al., "Modular Hardware Design with Timeline Types")
    - Example (concat): `(p1: Stm[u8, N] @ 0)` and `(p2: Stm[u8, M] @ N)` means `p1` starts arriving "immediately" (again, relative to some arbitrary time) and `p2` starts arriving `N` cycles later
  - Motivation:
    - Makes it possible to express `StmConcat`
      - This may actually be useful: could have low-area implementation of `StmRepeat` with long delay, and let programmer write `StmConcat(s, StmRepeat(s, k))` if they're in a hurry
    - Simplifies fusion
      - You can fuse one at a time; just update the arrival times of the other producers
    - I think it provides a simple rule explaining why it's problematic to apply fusion to `StmZip(StmCount(N), p)`: `StmCount(N)` has absolute latency (exactly 1 cycle after reset) whereas `s` has some unknown latency, so the two don't mix
- Programmer should be able to specify constraints on the physical prefix. For example, maybe I should extend the language with new assertion syntax like `assert physical @(_, valid) => !valid`, which checks that all the elements in the physical prefix satisfy the given condition. Notice that we don’t care about the _number_ of these elements.
- What if we didn’t have a latency-insensitive interface at all, and we want the latency to be fixed? I could certainly extend the syntax to let the programmer specify the desired delay, but this is probably not needed right away.

## Possible Solutions

- Extend the IR somehow: new annotation, new syntactic element after `data` and `valid`, etc.
  - What would this mean when the handshake protocol is enabled?
  - Won’t it be a massive amount of work to update the existing codebase to handle this new IR?
- Just let `valid` be something like `t == k`, where `(t) = { init: 0, next: if (t == k) then t else t + 1 }`. Update the semantic analyzer to allow this specific pattern and update things like `StmSlideStartingWith`, `StmSuffix`, etc. to follow it carefully.
  - Will the expression still be OK after fusion? Might need to make a version of the fusion pass for the `no_handshake` mode that’s aware of this pattern.
  - Seems very brittle in general: how do I ensure the counter keeps the right form all the time?
  - I'll probably need to update `sbuild` anyway for some of the other things (e.g., initial value of the `data` register), so I wouldn't even save much effort, if any

- In `sbuild`:
  - Add output latency (1 in the normal case, e.g., `StmMap`)
  - Add relative delay for each producer
  - Add initial value for data register
- Fusion pass:
  - TODO
- Fission pass:
  - TODO
- Latency matching pass:
  - TODO

## Implementation Plan

- _Step 0:_ refactor code so `StmBuild` has separate accumulator and producer lists

TODO

- _First step:_ assume we don’t care at all about physical outputs
  - Update semantic analyzer to allow `valid` expression to look like `t == k`
  - Update syntax sugar (at least `StmMap`, `StmMapDot`, `StmSlideStartingWith`; ideally also `StmSuffix`, `StmDelay`, etc.)
  - Update fusion pass to keep `valid` expression in the right form
    - TODO: Will any other passes need to be updated?
- _Second step:_ make it possible to control physical outputs
  - `sbuild` will probably need to be updated to have an initial value for the `data` register (default: `undefined`)
  - Syntax sugar may need to be updated to be able to set that initial value
    - e.g., in `StmMap`, add an optional argument? By default, it’s `undefined`
    - e.g., in `StmSlideStartingWith`, increase the size of the argument? The argument will be used as the initial value for the `data` register, but the first element will be dropped when setting the initial value for the shift register.
- _Third step:_ make it possible to write automated tests for the physical outputs
  - Evaluator will need to be updated
    - Have “physical mode” and “logical mode”?
    - Always produce physical outputs in no_handshake mode and logical outputs otherwise?
  - Add new assertion type for checking that physical outputs satisfy condition
- TODO: How will the fission pass work?
  - Consumer: just use the existing value
  - Producer: need to find inverse of function? Might not be possible in general.
  - Just disable it in no_handshake mode, tell the user they should split up their design manually?
