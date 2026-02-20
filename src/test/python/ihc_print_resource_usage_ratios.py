#!/usr/bin/env python3

"""
This program calculates the ratios of our resource usage to that of the Intel
HLS compiler.
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
    Return (Sirop, Intel HLS) resource usage results for each benchmark.
    """
    benchmarks = {bi.bench for bi in results.keys()}
    pairs: dict[Benchmark, tuple[ResourceUsage, ResourceUsage]] = {}
    for b in benchmarks:
        # Skip benchmarks that are not in the paper
        skip = (
            b.name.startswith("small")
            or b.name.startswith("sum")
            or b.name.startswith("sqrt")
        )
        if skip:
            continue
        sirop_res = results.get(BenchmarkImpl(b, "sirop"))
        ihc_res = results.get(BenchmarkImpl(b, "ihc"))
        if sirop_res is None and ihc_res is None:
            continue
        if sirop_res is None:
            print(f"WARNING: Missing Sirop resource usage for {b}")
            continue
        if ihc_res is None:
            print(f"WARNING: Missing Intel HLS resource usage for {b}")
            continue
        pairs[b] = (sirop_res, ihc_res)
    return pairs


def main() -> None:
    """
    Script entry point.
    """
    results = (
        crud.read_valid_resource_usage_results(c.SHIR_RESOURCE_USAGE_CSV)
        | crud.read_valid_resource_usage_results(c.IHC_RESOURCE_USAGE_CSV)
    )
    result_pairs = resource_usage_pairs(results)
    for b, (sirop, ihc) in sorted(result_pairs.items()):
        print(b.name)
        print(f"     ALMs: {sirop.alm} vs {ihc.alm} ({sirop.alm / ihc.alm:.2%})")
        if sirop.bram != 0 or ihc.bram != 0:
            print(f"    BRAMs: {sirop.bram} vs {ihc.bram}")
        if sirop.dsp != 0 or ihc.dsp != 0:
            print(f"     DSPs: {sirop.dsp} vs {ihc.dsp}")
        print()
    print()
    alm_ratios = [sirop.alm / ihc.alm for (sirop, ihc) in result_pairs.values()]
    min_alm = min(alm_ratios)
    max_alm = max(alm_ratios)
    geomean_alm = statistics.geometric_mean(alm_ratios)
    print(f"ALM utilization: {min_alm:.3f} - {max_alm:.3f} (geomean: {geomean_alm:.3f})")
    bram_ratios = [
        sirop.bram / ihc.bram
        for (sirop, ihc) in result_pairs.values()
        if sirop.bram != 0 and ihc.bram != 0
    ]
    min_bram = min(bram_ratios)
    max_bram = max(bram_ratios)
    geomean_bram = statistics.geometric_mean(bram_ratios)
    print(f"BRAM utilization: {min_bram:.3f} - {max_bram:.3f} (geomean: {geomean_bram:.3f})")
    dsp_ratios = [
        sirop.dsp / ihc.dsp
        for (sirop, ihc) in result_pairs.values()
        if sirop.dsp != 0 and ihc.dsp != 0
    ]
    min_dsp = min(dsp_ratios)
    max_dsp = max(dsp_ratios)
    geomean_dsp = statistics.geometric_mean(dsp_ratios)
    print(f"DSP utilization: {min_dsp:.3f} - {max_dsp:.3f} (geomean: {geomean_dsp:.3f})")



if __name__ == "__main__":
    main()
