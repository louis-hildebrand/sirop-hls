## Shift Register Merging

## Example 1

Two shift registers can sometimes be combined into a single shift register.
For example, consider the following expression:

```
sbuild(n)(
    (big_vec, vec_outside, vec_inside, vec_start),
    true
) {
    (big_vec: Vec[u8, 8]) = {
        init: undefined,
        next: vbuild(8) { (i: u8) => if i == 7 then sdata(p) else big_vec[i + 1] }
    },
    (vec_outside: Vec[u8, 4]) = {
        init: undefined,
        next: vbuild(4) { (i: u8) => if i == 3 then big_vec[0] else vec_outside[i + 1] }
    },
    (vec_inside: Vec[u8, 4]) = {
        init: undefined,
        next: vbuild(4) { (i: u8) => if i == 3 then big_vec[5] else vec_inside[i + 1] }
    },
    (vec_start: Vec[u8, 4]) = {
        init: undefined,
        next: vbuild(4) { (i: u8) => if i == 3 then sdata(p) else vec_start[i + 1] }
    }
} {
    (p: Stm[u8, n]) = { stm: ???, ready: true }
}
```

which graphically looks like:

```
                                            sdata(p)
                        +------------+            |
<-----------------------| vec_inside |<--+        |
                        +------------+   |        |
                                         |        |
                   +---------------------------+  |
<------------------|          big_vec          |<-+
                   +---------------------------+  |
                    |                             |
   +-------------+  |                             |
<--| vec_outside |<-+                             |
   +-------------+                                |
                                                  |
                                   +-----------+  |
<----------------------------------| vec_start |<-+
                                   +-----------+
```

But we could equivalently use just one shift register.
The most obvious cases are `vec_inside` and `vec_start`, which duplicate subsets of `big_vec`:

```
sbuild(n)(
    (
        big_vec,
        vec_outside,
        /* vec_inside */ vbuild(4) { (i: u8) => big_vec[5 - 4 + i] },
        /* vec_start  */ vbuild(4) { (i: u8) => big_vec[8 - 4 + i] }
    ),
    true
) {
    (big_vec: Vec[u8, 8]) = {
        init: undefined,
        next: vbuild(8) { (i: u8) => if i == 7 then sdata(p) else big_vec[i + 1] }
    },
    (vec_outside: Vec[u8, 4]) = {
        init: undefined,
        next: vbuild(4) { (i: u8) => if i == 3 then big_vec[0] else vec_outside[i + 1] }
    }
} {
    (p: Stm[u8, n]) = { stm: ???, ready: true }
}
```

`vec_outside` and `big_vec` can also be combined into a single shift register of length 12:

```
sbuild(n)(
    (
        /* big_vec,    */ vbuild(8) { (i: u8) => combined_vec[4 + i] }
        /* vec_outside */ vbuild(4) { (i: u8) => combined_vec[i] },
        /* vec_inside  */ vbuild(4) { (i: u8) => combined_vec[5 - 4 + 4 + i] },
        /* vec_start   */ vbuild(4) { (i: u8) => combined_vec[8 - 4 + 4 + i] }
    ),
    true
) {
    (combined_vec: Vec[u8, 8+4]) = {
        init: undefined,
        next: vbuild(12) { (i: u8) => if i == 11 then sdata(p) else combined_vec[i + 1] }
    }
} {
    (p: Stm[u8, n]) = { stm: ???, ready: true }
}
```

## Example 2

What if there are many levels of duplication, as in the following example?

```
sbuild(n)(
    (v, v1, v2, v3),
    true
) {
    (v: Vec[u8, 8]) = {
        init: undefined,
        next: vbuild(8) { (i: u8) => if i == 7 then sdata(p) else v[i + 1] }
    },
    (v1: Vec[u8, 7]) = {
        init: undefined,
        next: vbuild(7) { (i: u8) => if i == 6 then v[7] else v1[i + 1] }
    },
    (v2: Vec[u8, 6]) = {
        init: undefined,
        next: vbuild(6) { (i: u8) => if i == 5 then v1[6] else v2[i + 1] }
    }
} {
    (p: Stm[u8, n]) = { stm: ???, ready: true }
}
```

In this case, I need to be careful to apply the replacements recursively.
That is, avoid the following situation:

```
sbuild(n)(
    (
        v,
        vbuild(7) { (i: u8) => v[i] },
        vbuild(6) { (i: u8) => v1[i] } // <-- ERROR: free variable v1
    ),
    true
) {
    (v: Vec[u8, 8]) = {
        init: undefined,
        next: vbuild(8) { (i: u8) => if i == 7 then sdata(p) else v[i + 1] }
    }
} {
    (p: Stm[u8, n]) = { stm: ???, ready: true }
}
```
