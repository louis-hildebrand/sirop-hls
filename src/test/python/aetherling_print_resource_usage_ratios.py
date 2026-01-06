#!/usr/bin/env python3

"""
This program calculates the ratios of our resource usage to that of Aetherling.
"""

import statistics

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


def main() -> None:
    """
    Script entry point.
    """
    results = crud.read_valid_resource_usage_results(c.RESOURCE_USAGE_CSV)
    result_pairs = resource_usage_pairs(results)
    for b, (vhdl, verilog) in sorted(result_pairs.items()):
        print(b.full_name)
        print(f"     ALMs: {vhdl.alm} vs {verilog.alm} ({vhdl.alm / verilog.alm:.2%})")
        if vhdl.bram != 0 or verilog.bram != 0:
            print(f"    BRAMs: {vhdl.bram} vs {verilog.bram}")
        if vhdl.dsp != 0 or verilog.dsp != 0:
            print(f"     DSPs: {vhdl.dsp} vs {verilog.dsp}")
        print()
    print()
    alm_ratios = [vhdl.alm / verilog.alm for (vhdl, verilog) in result_pairs.values()]
    min_alm = min(alm_ratios)
    max_alm = max(alm_ratios)
    geomean_alm = statistics.geometric_mean(alm_ratios)
    print(f"ALM utilization: {min_alm:.3f} - {max_alm:.3f} (geomean: {geomean_alm:.3f})")
    bram_ratios = [
        vhdl.bram / verilog.bram
        for (vhdl, verilog) in result_pairs.values()
        if vhdl.bram != 0 and verilog.bram != 0
    ]
    min_bram = min(bram_ratios)
    max_bram = max(bram_ratios)
    geomean_bram = statistics.geometric_mean(bram_ratios)
    print(f"BRAM utilization: {min_bram:.3f} - {max_bram:.3f} (geomean: {geomean_bram:.3f})")
    dsp_ratios = [
        vhdl.dsp / verilog.dsp
        for (vhdl, verilog) in result_pairs.values()
        if vhdl.dsp != 0 and verilog.dsp != 0
    ]
    min_dsp = min(dsp_ratios)
    max_dsp = max(dsp_ratios)
    geomean_dsp = statistics.geometric_mean(dsp_ratios)
    print(f"DSP utilization: {min_dsp:.3f} - {max_dsp:.3f} (geomean: {geomean_dsp:.3f})")



if __name__ == "__main__":
    main()
