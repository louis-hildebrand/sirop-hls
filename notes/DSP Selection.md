# DSP Selection Pass

- TODO: Use internal coefficient banks in DSPs
    - DSP user guide p. 91 makes it sound like coefficients must be non-negative (!). Will this still be useable with negative coefficients?
    - Quartus' IP Parameter Editor doesn't stop me from using negative parameters. If I type a very large negative number, Quartus seems to just take the least significant 32 bits, so I suspect they're just not checking if the coefficient is in range.
    - NOTE: you can't use internal coefficients for one product but not for the other: either both internal coefficient banks are enabled, or neither
- TODO: Make number of stages generic too? This might require some kind of "vector unrolling" operation (by a factor of 2) to be able to combine `agilex_mac1` into `agilex_mac2`
- TODO: See DSP user guide p. 81 for max bitwidth

## Basic Example

Consider the following FIR filter

```
(s : Stm[Vec[i18, 6:u3], 8:u4]) =>
  sbuild(8:u4)(
    truncate18(pad44(stage5_x_pipe[0]) *` pad44(stage5_y_pipe[0]) +` stage4_out >> 15),
    true
  ) {
    (stage0_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[0] else stage0_x_pipe[i + 1] } },
    (stage0_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[0] else stage0_y_pipe[i + 1] } },
    (stage0_out    : i44)         = { init: undefined[i44], next: pad44(stage0_x_pipe[0]) *` pad44(stage0_y_pipe[0]) },
    (stage1_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[1] else stage1_x_pipe[i + 1] } },
    (stage1_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[1] else stage1_y_pipe[i + 1] } },
    (stage1_out    : i44)         = { init: undefined[i44], next: pad44(stage1_x_pipe[0]) *` pad44(stage1_y_pipe[0]) +` stage0_out },
    (stage2_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[2] else stage2_x_pipe[i + 1] } },
    (stage2_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[2] else stage2_y_pipe[i + 1] } },
    (stage2_out    : i44)         = { init: undefined[i44], next: pad44(stage2_x_pipe[0]) *` pad44(stage2_y_pipe[0]) +` stage1_out },
    (stage3_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[3] else stage3_x_pipe[i + 1] } },
    (stage3_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[3] else stage3_y_pipe[i + 1] } },
    (stage3_out    : i44)         = { init: undefined[i44], next: pad44(stage3_x_pipe[0]) *` pad44(stage3_y_pipe[0]) +` stage2_out },
    (stage4_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[4] else stage4_x_pipe[i + 1] } },
    (stage4_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[4] else stage4_y_pipe[i + 1] } },
    (stage4_out    : i44)         = { init: undefined[i44], next: pad44(stage4_x_pipe[0]) *` pad44(stage4_y_pipe[0]) +` stage3_out },
    (stage5_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[5] else stage5_x_pipe[i + 1] } },
    (stage5_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[5] else stage5_y_pipe[i + 1] } }
  } {
    (p1 : Stm[Vec[i18, 6:u3], -1:i1]) = {
      stm: s,
      ready: true
    },
    (p2 : Stm[Vec[i18, 6:u3], -1:i1]) = {
      stm: sbuild(8:u4)([3:i18, 5:i18, 7:i18, 11:i18, 13:i18, 17:i18]v, true) {} {},
      ready: true
    }
  }
```

### New sbuild variant

At first, make output `data` register explicit so it can easily be folded into IP blocks?
`valid` register should probably stay as-is, since some special logic is required to update it when the consumer reads, even if the required producers do not currently have valid data.
(The `valid` register cannot be represented by a normal accumulator, at least not when the handshake protocol is enabled.
Hopefully the `data` register *can* be represented by a normal accumulator register.)

This may require moving zero-delay operations (e.g., truncating, bit shifts by a constant) back after the register.
That should be doable using the existing "scheduling" pass I use for fission.

```
(s : Stm[Vec[i18, 6:u3], 8:u4]) =>
  sbuild(8:u4)(
    truncate18(stage5_out >> 15), // NOTE: no implicit output register (so that output register can be folded into an IP block)
    true
  ) {
    (stage0_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[0] else stage0_x_pipe[i + 1] } },
    (stage0_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[0] else stage0_y_pipe[i + 1] } },
    (stage0_out    : i44)         = { init: undefined[i44], next: pad44(stage0_x_pipe[0]) *` pad44(stage0_y_pipe[0]) },
    (stage1_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[1] else stage1_x_pipe[i + 1] } },
    (stage1_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[1] else stage1_y_pipe[i + 1] } },
    (stage1_out    : i44)         = { init: undefined[i44], next: pad44(stage1_x_pipe[0]) *` pad44(stage1_y_pipe[0]) +` stage0_out },
    (stage2_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[2] else stage2_x_pipe[i + 1] } },
    (stage2_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[2] else stage2_y_pipe[i + 1] } },
    (stage2_out    : i44)         = { init: undefined[i44], next: pad44(stage2_x_pipe[0]) *` pad44(stage2_y_pipe[0]) +` stage1_out },
    (stage3_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[3] else stage3_x_pipe[i + 1] } },
    (stage3_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[3] else stage3_y_pipe[i + 1] } },
    (stage3_out    : i44)         = { init: undefined[i44], next: pad44(stage3_x_pipe[0]) *` pad44(stage3_y_pipe[0]) +` stage2_out },
    (stage4_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[4] else stage4_x_pipe[i + 1] } },
    (stage4_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[4] else stage4_y_pipe[i + 1] } },
    (stage4_out    : i44)         = { init: undefined[i44], next: pad44(stage4_x_pipe[0]) *` pad44(stage4_y_pipe[0]) +` stage3_out },
    (stage5_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[5] else stage5_x_pipe[i + 1] } },
    (stage5_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[5] else stage5_y_pipe[i + 1] } },
    (stage5_out    : i44)         = { init: undefined[i44], next: pad44(stage5_x_pipe[0]) *` pad44(stage5_y_pipe[0]) +` stage4_out }
  } {
    (p1 : Stm[Vec[i18, 6:u3], -1:i1]) = {
      stm: s,
      ready: true
    },
    (p2 : Stm[Vec[i18, 6:u3], -1:i1]) = {
      stm: sbuild(8:u4)([3:i18, 5:i18, 7:i18, 11:i18, 13:i18, 17:i18]v, true) {} {},
      ready: true
    }
  } {
    // NOTE: add extra section in sbuild for intermediate values.
    //       This has the following benefits:
    //        * I don't duplicate the multiplications when performing substitutions.
    //          For example, if `stage1` is used twice, I don't want to instantiate multiple copies of that DSP.
    //        * In the Scala code, I can declare each intermediate value as being either an expression or an IP block.
    //          That way I don't need to add another subclass of `Expr` (which would be a hassle)
    //          nor pretend the IP blocks are syntax sugar (which would be hacky; they don't support lowering).
    //        * It reflects the fact that you can't just instantiate a component anywhere, e.g., in a function (right?).
    //        * I can perform common subexpression elimination, if I later decide to do that.
  }
```

### Single multiplier recognition

Then recognize individual multiplications followed by a register

```
(s : Stm[Vec[i18, 6:u3], 8:u4]) =>
  sbuild(8:u4)(truncate18(stage5_out >> 15), true) {
    (stage0_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[0] else stage0_x_pipe[i + 1] } },
    (stage0_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[0] else stage0_y_pipe[i + 1] } },
    (stage1_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[1] else stage1_x_pipe[i + 1] } },
    (stage1_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[1] else stage1_y_pipe[i + 1] } },
    (stage2_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[2] else stage2_x_pipe[i + 1] } },
    (stage2_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[2] else stage2_y_pipe[i + 1] } },
    (stage3_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[3] else stage3_x_pipe[i + 1] } },
    (stage3_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[3] else stage3_y_pipe[i + 1] } },
    (stage4_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[4] else stage4_x_pipe[i + 1] } },
    (stage4_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[4] else stage4_y_pipe[i + 1] } },
    (stage5_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[5] else stage5_x_pipe[i + 1] } },
    (stage5_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[5] else stage5_y_pipe[i + 1] } },
  } {
    (p1 : Stm[Vec[i18, 6:u3], -1:i1]) = {
      stm: s,
      ready: true
    },
    (p2 : Stm[Vec[i18, 6:u3], -1:i1]) = {
      stm: sbuild(8:u4)([3:i18, 5:i18, 7:i18, 11:i18, 13:i18, 17:i18]v, true) {} {},
      ready: true
    }
  } {
    (stage0_out : i44) = agilex_mac1(stage0_x_pipe[0], stage0_y_pipe[0], 0),
    (stage1_out : i44) = agilex_mac1(stage1_x_pipe[0], stage1_y_pipe[0], stage0_out),
    (stage2_out : i44) = agilex_mac1(stage2_x_pipe[0], stage2_y_pipe[0], stage1_out),
    (stage3_out : i44) = agilex_mac1(stage3_x_pipe[0], stage3_y_pipe[0], stage2_out),
    (stage4_out : i44) = agilex_mac1(stage4_x_pipe[0], stage4_y_pipe[0], stage3_out),
    (stage5_out : i44) = agilex_mac1(stage5_x_pipe[0], stage5_y_pipe[0], stage4_out),
  }
```

### Multiplier combination

Then combine adjacent IP blocks into larger ones.
Simple case: if `c` = `agilex_mac1(ax, ay, chainin)`, then `agilex_mac1(bx, by, c)` should be replaced with `agilex_mac2(ax, ay, bx, by, chainin)`

Questions:

- TODO: how to ensure I don't start by combining `mul1` and `mul2` and thus leaving `mul0` and `mul5` unpaired?
    - Perhaps easiest: apply this rewrite to *all* intermediate values *and don't delete anything*. Only after applying the rewrite everywhere, delete unused intermediate values.
    - Sort intermediate values (depth-first search starting with `sbuild` output)?
- TODO: what if I don't succeed in combining (e.g., you have only a 5-tap FIR filter)?
    - If I can find two independent `agilex_mul1` instances, I can combine them into an `agilex_mul2` (the "independent multiplier" mode)
    - Should `agilex_mac1` be implemented still in systolic mode, or in some other mode? Or maybe just backtrack to a normal multiply and let Quartus try to infer something?

```
(s : Stm[Vec[i18, 6:u3], 8:u4]) =>
  sbuild(8:u4)(truncate18(stage5_out >> 15), true) {
    (stage0_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[0] else stage0_x_pipe[i + 1] } },
    (stage0_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[0] else stage0_y_pipe[i + 1] } },
    (stage1_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[1] else stage1_x_pipe[i + 1] } },
    (stage1_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[1] else stage1_y_pipe[i + 1] } },
    (stage2_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[2] else stage2_x_pipe[i + 1] } },
    (stage2_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[2] else stage2_y_pipe[i + 1] } },
    (stage3_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[3] else stage3_x_pipe[i + 1] } },
    (stage3_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[3] else stage3_y_pipe[i + 1] } },
    (stage4_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[4] else stage4_x_pipe[i + 1] } },
    (stage4_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[4] else stage4_y_pipe[i + 1] } },
    (stage5_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[5] else stage5_x_pipe[i + 1] } },
    (stage5_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[5] else stage5_y_pipe[i + 1] } }
  } {
    (p1 : Stm[Vec[i18, 6:u3], -1:i1]) = {
      stm: s,
      ready: true
    },
    (p2 : Stm[Vec[i18, 6:u3], -1:i1]) = {
      stm: sbuild(8:u4)([3:i18, 5:i18, 7:i18, 11:i18, 13:i18, 17:i18]v, true) {} {},
      ready: true
    }
  } {
    (stage0_out : i44) = agilex_mac1(stage0_x_pipe[0], stage0_y_pipe[0], 0),                                              // Unused now
    (stage1_out : i44) = agilex_mac2(stage0_x_pipe[0], stage0_y_pipe[0], stage1_x_pipe[0], stage1_y_pipe[0], 0),
    (stage2_out : i44) = agilex_mac2(stage1_x_pipe[0], stage1_y_pipe[0], stage2_x_pipe[0], stage2_y_pipe[0], stage0_out), // Unused now
    (stage3_out : i44) = agilex_mac2(stage2_x_pipe[0], stage2_y_pipe[0], stage3_x_pipe[0], stage3_y_pipe[0], stage1_out),
    (stage4_out : i44) = agilex_mac2(stage3_x_pipe[0], stage3_y_pipe[0], stage4_x_pipe[0], stage4_y_pipe[0], stage2_out), // Unused now
    (stage5_out : i44) = agilex_mac2(stage4_x_pipe[0], stage4_y_pipe[y], stage5_x_pipe[0], stage5_y_pipe[0], stage3_out),
  }
```

### Unused value deletion

Then delete unused intermediate values

```
(s : Stm[Vec[i18, 6:u3], 8:u4]) =>
  sbuild(8:u4)(truncate18(stage5_out >> 15), true) {
    (stage0_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[0] else stage0_x_pipe[i + 1] } },
    (stage0_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[0] else stage0_y_pipe[i + 1] } },
    (stage1_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[1] else stage1_x_pipe[i + 1] } },
    (stage1_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[1] else stage1_y_pipe[i + 1] } },
    (stage2_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[2] else stage2_x_pipe[i + 1] } },
    (stage2_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[2] else stage2_y_pipe[i + 1] } },
    (stage3_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[3] else stage3_x_pipe[i + 1] } },
    (stage3_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[3] else stage3_y_pipe[i + 1] } },
    (stage4_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[4] else stage4_x_pipe[i + 1] } },
    (stage4_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[4] else stage4_y_pipe[i + 1] } },
    (stage5_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[5] else stage5_x_pipe[i + 1] } },
    (stage5_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[5] else stage5_y_pipe[i + 1] } }
  } {
    (p1 : Stm[Vec[i18, 6:u3], -1:i1]) = {
      stm: s,
      ready: true
    },
    (p2 : Stm[Vec[i18, 6:u3], -1:i1]) = {
      stm: sbuild(8:u4)([3:i18, 5:i18, 7:i18, 11:i18, 13:i18, 17:i18]v, true) {} {},
      ready: true
    }
  } {
    (stage1_out : i44) = agilex_mac2(stage0_x_pipe[0], stage0_y_pipe[0], stage1_x_pipe[0], stage1_y_pipe[0], 0),
    (stage3_out : i44) = agilex_mac2(stage2_x_pipe[0], stage2_y_pipe[0], stage3_x_pipe[0], stage3_y_pipe[0], stage1_out),
    (stage5_out : i44) = agilex_mac2(stage4_x_pipe[0], stage4_y_pipe[y], stage5_x_pipe[0], stage5_y_pipe[0], stage3_out),
  }
```

### Merging input registers

Then merge the pipeline registers into the IP blocks.
Here we can see that we're reading from the end of 2-stage shift registers, so we can enable two of the DSPs' pipeline registers.

```
(s : Stm[Vec[i18, 6:u3], 8:u4]) =>
  sbuild(8:u4)(truncate18(stage5_out >> 15), true) {
    (stage0_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[0] else stage0_x_pipe[i + 1] } },
    (stage0_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[0] else stage0_y_pipe[i + 1] } },
    (stage1_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[1] else stage1_x_pipe[i + 1] } },
    (stage1_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[1] else stage1_y_pipe[i + 1] } },
    (stage2_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[2] else stage2_x_pipe[i + 1] } },
    (stage2_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[2] else stage2_y_pipe[i + 1] } },
    (stage3_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[3] else stage3_x_pipe[i + 1] } },
    (stage3_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[3] else stage3_y_pipe[i + 1] } },
    (stage4_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[4] else stage4_x_pipe[i + 1] } },
    (stage4_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[4] else stage4_y_pipe[i + 1] } },
    (stage5_x_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p1)[5] else stage5_x_pipe[i + 1] } },
    (stage5_y_pipe : Vec[i18, 2]) = { init: undefined[i18], next: vbuild(2) { (i: u32) => if (i == 1) then sdata(p2)[5] else stage5_y_pipe[i + 1] } }
  } {
    (p1 : Stm[Vec[i18, 6:u3], -1:i1]) = {
      stm: s,
      ready: true
    },
    (p2 : Stm[Vec[i18, 6:u3], -1:i1]) = {
      stm: sbuild(8:u4)([3:i18, 5:i18, 7:i18, 11:i18, 13:i18, 17:i18]v, true) {} {},
      ready: true
    }
  } {
    (stage1_out : i44) = agilex_mac2(sdata(p1)[0], sdata(p2)[0], sdata(p1)[1], sdata(p2)[1], 0,          /*delay*/ 2),
    (stage3_out : i44) = agilex_mac2(sdata(p1)[2], sdata(p2)[2], sdata(p1)[3], sdata(p2)[3], stage1_out, /*delay*/ 2),
    (stage5_out : i44) = agilex_mac2(sdata(p1)[4], sdata(p2)[4], sdata(p1)[5], sdata(p2)[5], stage3_out, /*delay*/ 2)
  }
```

### Unused value deletion

Then remove unused accumulators

```
(s : Stm[Vec[i18, 6:u3], 8:u4]) =>
  sbuild(8:u4)(truncate18(stage5_out >> 15), true) {
    // All gone!
  } {
    (p1 : Stm[Vec[i18, 6:u3], -1:i1]) = {
      stm: s,
      ready: true
    },
    (p2 : Stm[Vec[i18, 6:u3], -1:i1]) = {
      stm: sbuild(8:u4)([3:i18, 5:i18, 7:i18, 11:i18, 13:i18, 17:i18]v, true) {} {},
      ready: true
    }
  } {
    (stage1_out : i44) = agilex_mac2(sdata(p1)[0], sdata(p2)[0], sdata(p1)[1], sdata(p2)[1], 0,          /*delay*/ 2),
    (stage3_out : i44) = agilex_mac2(sdata(p1)[2], sdata(p2)[2], sdata(p1)[3], sdata(p2)[3], stage1_out, /*delay*/ 2),
    (stage5_out : i44) = agilex_mac2(sdata(p1)[4], sdata(p2)[4], sdata(p1)[5], sdata(p2)[5], stage3_out, /*delay*/ 2)
  }
```

In the end, `agilex_mac2` can be straightforwardly implemented in VHDL by instantiating the `tennm_mac` IP block, as in the existing LASP code.
