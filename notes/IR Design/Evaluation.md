## Description
- Two forms of evaluation:
	- Evaluating a combinational expression: usual big-step semantics
	- Evaluating a pipeline of streams: basically run simulation cycle-by-cycle

## Examples
### Simple Counter
```
stm:build(
	3,
	i,
	true,
	i : (0, i + 1)
)

---

Cycle 0: {
	n            : 3,
	next output  : Some(0),
	accumulators : ( i = 0 )
}
Cycle 1: {
	n            : 2,
	next output  : Some(1),
	accumulators : ( i = 1 )
}
Cycle 2: {
	n           : 1,
	next output : Some(2),
	accumulator : ( i = 2 )
}
Cycle 3: {
	n           : 0,
	next output : None,
	accumulator : ( i = 3 )
}
```

### Counter That Skips Elements
```
stm:build(
	2,
	i,
	valid,
	i : (0, i + 1),
	valid : (true, !valid)
)

---

Cycle 0: {
	n            : 2,
	next output  : Some(0),
	accumulators : ( i = 0, valid = true )
}
Cycle 1: {
	n            : 1,
	next output  : None,
	accumulators : ( i = 1, valid = false )
}
Cycle 2: {
	n            : 1,
	next output  : Some(2),
	accumulators : ( i = 2, valid = true )
}
Cycle 3: {
	n            : 0,
	next output  : None,
	accumulators : ( i = 3, valid = false )
}
```

### Interleaving Two Streams
```
stm:build(
	4,
	if b then stm:data(s0) else stm:data(s1)
	true,
	b: (true, !b),
	s0 : (
		stm:build(
			2,
			i,
			true,
			i : (0, i + 1)
		),
		b
	),
	s1 : (
		stm:build(
			2,
			j,
			j % 3 == 0,
			j : (42, j + 1)
		),
		!b
	)
)

---

Cycle 0 : {
	n            : 4,
	next output  : Some(0),
	accumulators : ( b = true ),
	inputs       : {
		s0 : {
			n            : 2,
			next output  : Some(0),
			accumulators : ( i = 0 )
		},
		s1 : {
			n            : 2,
			next output  : Some(42),
			accumulators : ( j = 42 )
		}
	}
}
Cycle 1 : {
	n            : 3,
	next output  : Some(42),
	accumulators : ( b = false ),
	inputs       : {
		s0 : {
			n            : 1,
			next output  : Some(1),
			accumulators : ( i = 1 )
		},
		s1 : {
			n            : 2,
			next output  : Some(42),
			accumulators : ( j = 42 )
		}
	}
}
Cycle 2 : {
	n            : 2,
	next output  : Some(1),
	accumulators : ( b = true ),
	inputs       : {
		s0 : {
			n            : 1,
			next output  : Some(1),
			accumulators : ( i = 1 )
		},
		s1 : {
			n            : 1,
			next output  : None,
			accumulators : ( j = 43 )
		}
	}
}
Cycle 3 : {
	n            : 1,
	next output  : None,
	accumulators : ( b = false ),
	inputs       : {
		s0 : {
			n            : 0,
			next output  : ---,
			accumulators : ( i = 2 )
		},
		s1 : {
			n            : 1,
			next output  : None,
			accumulators : ( j = 44 )
		}
	}
}
Cycle 4 : {
	n            : 1,
	next output  : Some(45),
	accumulators : ( b = false ),
	inputs       : {
		s0 : {
			n            : 0,
			next output  : ---,
			accumulators : ( i = 2 )
		},
		s1 : {
			n            : 1,
			next output  : Some(45),
			accumulators : ( j = 45 )
		}
	}
}
Cycle 5 : {
	n            : 0,
	next output  : ---,
	accumulators : ( b = true ),
	inputs       : {
		s0 : {
			n            : 0,
			next output  : ---,
			accumulators : ( i = 2 )
		},
		s1 : {
			n            : 0,
			next output  : ---,
			accumulators : ( j = 46 )
		}
	}
}
```