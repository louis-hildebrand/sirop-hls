## Motivation

`StmMapDot` lowers to `StmCascade` followed by `StmMapDotCascaded`. The question is whether I can get

```
s
.StmSlideStartingWith(undefined[Vec[i18, 8*5]])
.StmMap( buf => buf[:6:8] )
.StmMapDot(StmCst(..., COEFFS), 0)
```

to be optimized to the same thing as

```
s
.StmSlideStartingWith(undefined[Vec[i18, 7*5]])
.StmMap( buf => buf[:6:7] )
.MulAddCascaded(StmCst(..., COEFFS), 0)
```

Notice how the latter has the following properties:
1. In `StmSlideStartingWith`, the buffer has size 7\*5, not 8\*5.
2. In `StmMap`, we slice the buffer using steps of 7, not 8.

## Implementation

- Property (2) should be pretty straightforward to achieve by [[Shift Register Merging]]
	- At least, it's straightforward in this case where the FIR filter depth of 6 and channel count of 8 are fixed
- Property (1) can then be achieved by [[Shift Register Shrinking]]