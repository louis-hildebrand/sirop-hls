# Example with Dynamic Scheduling: Dot Product

As a simple example, consider the [dot product](https://en.wikipedia.org/wiki/Dot_product) of two streams.
This can be expressed as follows in Sirop:

```
// u and v are streams of 16-bit unsigned integers, each with length 4
accelerator dot = (u: Stm[u16, 4]) => (v: Stm[u16, 4]) =>
    // EXAMPLE: u = [1:u16, 2:u16, 3:u16, 4:u16]s
    // EXAMPLE: v = [5:u16, 6:u16, 7:u16, 8:u16]s
    StmZip(u, v)
    // EXAMPLE: [(1:u16, 5:u16), (2:u16, 6:u16), (3:u16, 7:u16), (4:u16, 8:u16)]s
    .StmMap( @(x, y) => x * y )
    // EXAMPLE: [5:u16, 12:u16, 21:u16, 32:u16]s
    .StmSum()
    // EXAMPLE: [70:u16]s

assert {
    // when the inputs are...
    u = [1:u16, 2:u16, 3:u16, 4:u16]s,
    v = [5:u16, 6:u16, 7:u16, 8:u16]s.StmMap(x => x) // add some delay for demonstration
}
// ... then we expect the following output
yields [70:u16]s
```

## Testing and Debugging

The Sirop compiler can test the high-level code directly, without translating it to VHDL:

```sh
$ sirop -i dot.sirop --out:test
[INFO ] test 0: PASSED
[INFO ] 1/1 test passed!
```

If you had accidentally used addition instead of multiplication in `StmMap`, the test would fail.
The compiler can dump the expected and actual outputs to files so you can compare them (e.g., with `diff`).

```sh
$ sed -i 's/x \* y/x + y/g' dot.sirop
$ sirop -i dot.sirop --out:test:actual actual.txt --out:test:expected expected.txt
[WARN ] test 0: WRONG OUTPUT
TestError: 1/1 test failed.
$ diff expected.txt actual.txt
3c3
<   70:u16
---
>   36:u16
$ sed -i 's/x + y/x * y/g' dot.sirop
```

The Sirop compiler can also generate a cycle-by-cycle trace of the execution of the program.
For example, running the following (with the working dot product program) generates a series of images in `./trace`.

```sh
# Disable fusion to show each pipeline stage (zip, map, sum).
# By default, the compiler combines everything into a single pipeline stage.
$ sirop -i dot.sirop --out:trace ./trace --opt:no-fuse
```


```{important}
The images are generated using Graphviz, which must be installed separately.
See https://graphviz.org/download/.
```

```{note}
The arrows in the diagrams show the handshake protocol in action.
- A green double-headed arrow represents a successful data transfer.
- A dashed arrow from producer to consumer shows that the producer has valid data, but the consumer is not ready to receive it yet.
- A line without any arrowheads shows that the producer does not have valid data.

Notice how `u` must wait one clock cycle for the data from `v` to arrive.
The node corresponding to `StmZip` is exerting back-pressure (i.e., its `ready` signal is lowered).
```

```{image} figures/dot-trace/step_0.svg
:alt: Time step 0 of the dot product trace
:width: 30%
```
```{image} figures/dot-trace/step_1.svg
:alt: Time step 1 of the dot product trace
:width: 30%
```
```{image} figures/dot-trace/step_2.svg
:alt: Time step 2 of the dot product trace
:width: 30%
```
```{image} figures/dot-trace/step_3.svg
:alt: Time step 3 of the dot product trace
:width: 30%
```
```{image} figures/dot-trace/step_4.svg
:alt: Time step 4 of the dot product trace
:width: 30%
```
```{image} figures/dot-trace/step_5.svg
:alt: Time step 5 of the dot product trace
:width: 30%
```
```{image} figures/dot-trace/step_6.svg
:alt: Time step 6 of the dot product trace
:width: 30%
```
```{image} figures/dot-trace/step_7.svg
:alt: Time step 7 of the dot product trace
:width: 30%
```
```{image} figures/dot-trace/step_8.svg
:alt: Time step 8 of the dot product trace
:width: 30%
```
```{image} figures/dot-trace/step_9.svg
:alt: Time step 9 of the dot product trace
:width: 30%
```

## Generating VHDL and Running Simulation

Once you're satisfied that the high-level code does what you expect, the Sirop compiler can translate it to VHDL.
It can also generate a testbench to check that the generated VHDL entity behaves the way you expect.

```{important}
The testbench is run using Questa.
The relevant commands (`vcom`, `vsim`, etc.) must be on your `PATH`.
Furthermore, you may need a license to run `vsim`.
```

```{note}
To change the compilation target, use the `--out:vhdl:family` and `--out:vhdl:device` flags.
Run `sirop --help` for more details.
```

```sh
$ sirop -i dot.sirop --out:vhdl vhdl_project_dir --out:vhdl:run-sim
[INFO ] VHDL testbench passed!
```

At this point, you have VHDL code that can be synthesized with Quartus, simulated with your own testbench in Questa, etc.
