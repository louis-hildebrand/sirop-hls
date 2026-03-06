# Python Test Scripts

These scripts run various benchmarks.
They are grouped as follows:

- `ablation_*`: ablation study
    - Compile each handwritten Sirop program with different optimization settings to explore the impact of each optimization pass.
- `aetherling_*`: comparison with Aetherling
    - Given the benchmarks written in Aetherling's high-level language, translate them to Sirop and compare the quality of the resulting hardware to that produced by Aetherling's existing backend.
- `cpw_*`: full comparison with prior works
    - Aggregate the results from `ihc_*`, `shir_*`, and `aetherling_*`.
- `ihc_*`: results from the Intel HLS compiler
    - Synthesize the C++ programs and measure the quality of the generated hardware.
- `shir_*`: comparison with SHIR
    - For each benchmark, programs are handwritten in Sirop and in SHIR. The programs are as similar as possible.
