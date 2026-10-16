# Sirop

Sirop is a language and compiler for generating streaming accelerators.
Similarly to projects like [HLS4ML](https://fastmachinelearning.org/hls4ml/intro/introduction.html) and [Altera HLS IP Gen](https://www.altera.com/products/software-tools/hls-ip-gen-compiler), the goal is to convert high-level code into [VHDL](https://en.wikipedia.org/wiki/VHDL) code that can be synthesized and run on an [FPGA](https://en.wikipedia.org/wiki/Field-programmable_gate_array).

<picture>
    <source srcset="/docs/source/figures/light/workflow.svg" media="(prefers-color-scheme: light)" />
    <img src="/docs/source/figures/dark/workflow.svg" alt="Flowchart showing the Sirop workflow" width="100%" />
</picture>

Programs are written in a functional style using “parallel patterns.” For example, a [dot product](https://en.wikipedia.org/wiki/Dot_product) can be expressed as follows:

```
$ sirop
Welcome to the Sirop REPL (v2.3.0)!
Type 'exit' or press Ctrl+D to exit.
> u = [1:u8, 2:u8, 3:u8, 4:u8]s
> v = [5:u8, 6:u8, 7:u8, 8:u8]s
> StmZip(u, v).StmMap( @(x, y) => x * y ).StmSum()
[70:u8]s
>
> // ===== STEP-BY-STEP =================================
> // (1) StmZip pairs up the elements of the two streams
> zipped = StmZip(u, v)
> zipped
[(1:u8, 5:u8), (2:u8, 6:u8), (3:u8, 7:u8), (4:u8, 8:u8)]s
>
> // (2) StmMap multiplies each pair of numbers
> multiplied = zipped.StmMap( @(x, y) => x * y )
> multiplied
[5:u8, 12:u8, 21:u8, 32:u8]s
>
> // (3) StmSum computes the sum of the resulting stream
> result = multiplied.StmSum()
> result
[70:u8]s
```

For more details, see the documentation at https://louis-hildebrand.github.io/sirop-hls.

## Development

Information related to the development of Sirop (e.g., how to run the tests, how to update the documentation website) is in [CONTRIBUTING.md](CONTRIBUTING.md).
