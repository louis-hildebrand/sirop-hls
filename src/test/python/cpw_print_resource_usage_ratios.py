#!/usr/bin/env python3

"""
This program calculates the ratios of our resource usage to that of the Intel
HLS compiler, SHIR, and Aetherling.
"""

import statistics
from argparse import ArgumentParser, Namespace

from lib import constants as c
from lib import results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl
from lib.resource_usage import ResourceUsage


def resource_usage_pairs(
    results: dict[BenchmarkImpl, ResourceUsage]
) -> dict[Benchmark, tuple[ResourceUsage, ResourceUsage]]:
    """
    Return (vhdl, verilog) resource usage results for each benchmark.
    """
    benchmarks = {bi.bench for bi in results.keys()}
    pairs: dict[Benchmark, tuple[ResourceUsage, ResourceUsage]] = {}
    for b in benchmarks:
        # Skip benchmarks that are not in the paper
        skip = (
            b.name.startswith("small")
            or b.name.startswith("sum")
            or b.name.startswith("sqrt")
            or "matvec" in b.name
        )
        if skip:
            continue
        vhdl_res = results.get(BenchmarkImpl(b, "vhdl"))
        verilog_res = results.get(BenchmarkImpl(b, "verilog"))
        if vhdl_res is None and verilog_res is None:
            continue
        if vhdl_res is None:
            print(f"WARNING: Missing VHDL resource usage for {b}")
            continue
        if verilog_res is None:
            print(f"WARNING: Missing Verilog resource usage for {b}")
            continue
        pairs[b] = (vhdl_res, verilog_res)
    return pairs


def main(latex: bool) -> None:
    """
    Script entry point.
    """
    (area_results, _, _) = crud.read_combined_results(
        c.AETHERLING_RESOURCE_USAGE_CSV,
        c.AETHERLING_LATENCY_CSV,
        c.AETHERLING_FMAX_ESTIMATE_CSV,
        c.SHIR_RESOURCE_USAGE_CSV,
        c.SHIR_LATENCY_CSV,
        c.SHIR_FMAX_CSV,
        c.IHC_RESOURCE_USAGE_CSV,
        c.IHC_LATENCY_CSV,
        c.IHC_FMAX_CSV,
        c.SIROP_RESOURCE_USAGE_CSV,
        c.SIROP_LATENCY_CSV,
        c.SIROP_FMAX_CSV,
    )
    bench_names = sorted({bi.bench.name for bi in area_results.keys()})
    ihc_ratios = []
    shir_ratios = []
    aetherling_ratios = []
    for bench_name in bench_names:
        sirop_area = area_results[BenchmarkImpl.sirop(bench_name)]
        ihc_area = area_results.get(BenchmarkImpl.ihc(bench_name))
        if ihc_area is not None:
            ihc_ratios.append(sirop_area.alm / ihc_area.alm)
        else:
            print(f"WARNING: missing Intel HLS ALM usage for {bench_name}")
        shir_area = area_results.get(BenchmarkImpl.shir(bench_name))
        if shir_area is not None:
            shir_ratios.append(sirop_area.alm / shir_area.alm)
        else:
            print(f"WARNING: missing SHIR ALM usage for {bench_name}")
        aetherling_area = area_results.get(BenchmarkImpl.aetherling(bench_name))
        if aetherling_area is not None:
            aetherling_ratios.append(sirop_area.alm / aetherling_area.alm)
        else:
            print(f"WARNING: missing Aetherling ALM usage for {bench_name}")
    ihc_avg = statistics.geometric_mean(ihc_ratios)
    ihc_min = min(ihc_ratios)
    ihc_max = max(ihc_ratios)
    shir_avg = statistics.geometric_mean(shir_ratios)
    shir_min = min(shir_ratios)
    shir_max = max(shir_ratios)
    aetherling_avg = statistics.geometric_mean(aetherling_ratios)
    aetherling_min = min(aetherling_ratios)
    aetherling_max = max(aetherling_ratios)
    if latex:
        print(
f"""% Results (vs SHIR)
\\newcommand{{\\shiralmreduction}}{{{1-shir_avg:.0%}\\xspace}}
\\newcommand{{\\shiralmratiogeomean}}{{{shir_avg:.2f}\\xspace}}
\\newcommand{{\\shiralmratiomin}}{{{shir_min:.2f}\\xspace}}
\\newcommand{{\\shiralmratiomax}}{{{shir_max:.2f}\\xspace}}
% Results (vs Aetherling)
\\newcommand{{\\aetherlingalmreduction}}{{{1-aetherling_avg:.0%}\\xspace}}
\\newcommand{{\\aetherlingalmratiogeomean}}{{{aetherling_avg:.2f}\\xspace}}
\\newcommand{{\\aetherlingalmratiomin}}{{{aetherling_min:.2f}\\xspace}}
\\newcommand{{\\aetherlingalmratiomax}}{{{aetherling_max:.2f}\\xspace}}
% Results (vs Intel HLS compiler)
\\newcommand{{\\ihcalmreduction}}{{{1-ihc_avg:.0%}\\xspace}}
\\newcommand{{\\ihcalmratiogeomean}}{{{ihc_avg:.2f}\\xspace}}
\\newcommand{{\\ihcalmratiomin}}{{{ihc_min:.2f}\\xspace}}
\\newcommand{{\\ihcalmratiomax}}{{{ihc_max:.2f}\\xspace}}
""".rstrip().replace("%\\", "\\%\\")
        )
    else:
        print(
            "Intel HLS ALM ratio:  "
            f"min  {ihc_min:.2%} -- "
            f"geomean {ihc_avg:.2%} -- "
            f"max {ihc_max:.2%}"
        )
        print(
            "SHIR ALM ratio:       "
            f"min {shir_min:.2%} -- "
            f"geomean {shir_avg:.2%} -- "
            f"max {shir_max:.2%}"
        )
        print(
            "Aetherling ALM ratio: "
            f"min {aetherling_min:.2%} -- "
            f"geomean {aetherling_avg:.2%} -- "
            f"max {aetherling_max:.2%}"
        )


def parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(
        description=(
            "This script prints statistics comparing the ALM usage of Sirop to prior works"
        )
    )
    parser.add_argument(
        "--latex",
        action="store_true",
        help="print the results in the same format as the LaTeX paper"
    )
    return parser.parse_args()


if __name__ == "__main__":
    _args = parse_args()
    main(latex=_args.latex)
