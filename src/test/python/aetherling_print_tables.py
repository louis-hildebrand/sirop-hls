#!/usr/bin/env python3

"""
Script to print Markdown tables with results for selected benchmarks.
"""

import sys
from fractions import Fraction

import lib.constants as c
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl, min_latency
from lib.latency import LatencyResult
from lib.resource_usage import ResourceUsage

BENCHES_TO_INCLUDE = sys.argv[1:]


def benchmark_order(b: Benchmark) -> tuple[int, Fraction]:
    """
    Determine the order in which the benchmarks should be displayed.
    """
    return (BENCHES_TO_INCLUDE.index(b.name), b.throughput)


def print_alm_table(results: dict[BenchmarkImpl, ResourceUsage]) -> None:
    """
    Print a Markdown table with ALM usages.
    """
    print("| Benchmark | Throughput | Our ALMs | Aetherling ALMs |")
    print("| :-------- | :--------- | :------- | :-------------- |")
    benches = {b.bench for b in results.keys() if b.bench.name in BENCHES_TO_INCLUDE}
    benches = sorted(benches, key=benchmark_order)
    for b in benches:
        our_ru = results.get(BenchmarkImpl(b, "vhdl"))
        our_alms = str(our_ru.alm) if our_ru else "-"
        aetherling_ru = results.get(BenchmarkImpl(b, "verilog"))
        aetherling_alms = str(aetherling_ru.alm) if aetherling_ru else "-"
        title = b.name[3:] if b.name.startswith("big") else b.name
        print(f"| {title:<9} | {b.throughput_str:>10} | {our_alms:>8} | {aetherling_alms:>15} |")


def print_bram_table(results: dict[BenchmarkImpl, ResourceUsage]) -> None:
    """
    Print a Markdown table with BRAM usages.
    """
    print("| Benchmark | Throughput | Our BRAMs | Aetherling BRAMs |")
    print("| :-------- | :--------- | :-------- | :--------------- |")
    benches = {b.bench for b in results.keys() if b.bench.name in BENCHES_TO_INCLUDE}
    benches = sorted(benches, key=benchmark_order)
    for b in benches:
        our_ru = results.get(BenchmarkImpl(b, "vhdl"))
        our_brams = str(our_ru.bram) if our_ru else "-"
        aetherling_ru = results.get(BenchmarkImpl(b, "verilog"))
        aetherling_brams = str(aetherling_ru.bram) if aetherling_ru else "-"
        title = b.name[3:] if b.name.startswith("big") else b.name
        print(f"| {title:<9} | {b.throughput_str:>10} | {our_brams:>9} | {aetherling_brams:>16} |")


def print_dsp_table(results: dict[BenchmarkImpl, ResourceUsage]) -> None:
    """
    Print a Markdown table with DSP usages.
    """
    print("| Benchmark | Throughput | Our DSPs | Aetherling DSPs |")
    print("| :-------- | :--------- | :------- | :-------------- |")
    benches = {b.bench for b in results.keys() if b.bench.name in BENCHES_TO_INCLUDE}
    benches = sorted(benches, key=benchmark_order)
    for b in benches:
        our_ru = results.get(BenchmarkImpl(b, "vhdl"))
        our_dsps = str(our_ru.dsp) if our_ru else "-"
        aetherling_ru = results.get(BenchmarkImpl(b, "verilog"))
        aetherling_dsps = str(aetherling_ru.dsp) if aetherling_ru else "-"
        title = b.name[3:] if b.name.startswith("big") else b.name
        print(f"| {title:<9} | {b.throughput_str:>10} | {our_dsps:>8} | {aetherling_dsps:>15} |")


def print_latency_table(results: dict[BenchmarkImpl, LatencyResult]) -> None:
    """
    Print a Markdown table with latency results.
    """
    print("| Benchmark | Throughput | Our Latency | Aetherling Latency | Ideal Latency |")
    print("| :-------- | :--------- | :---------- | :----------------- | :------------ |")
    benches = {b.bench for b in results.keys() if b.bench.name in BENCHES_TO_INCLUDE}
    benches = sorted(benches, key=benchmark_order)
    for b in benches:
        our_latency = results.get(BenchmarkImpl(b, "vhdl"))
        our_latency = str(our_latency.latency) if our_latency else "-"
        aetherling_latency = results.get(BenchmarkImpl(b, "verilog"))
        aetherling_latency = str(aetherling_latency.latency) if aetherling_latency else "-"
        ideal = min_latency(b)
        title = b.name[3:] if b.name.startswith("big") else b.name
        # pylint: disable-next=line-too-long
        print(f"| {title:<9} | {b.throughput_str:>10} | {our_latency:>11} | {aetherling_latency:>18} | {ideal:>13} |")


def print_fmax_table(results: dict[BenchmarkImpl, float]) -> None:
    """
    Print a Markdown table with fmax results.
    """
    print("| Benchmark | Throughput | Ours | Aetherling |")
    print("| :-------- | :--------- | :--- | :--------- |")
    benches = {b.bench for b in results.keys() if b.bench.name in BENCHES_TO_INCLUDE}
    benches = sorted(benches, key=benchmark_order)
    for b in benches:
        our_fmax = results.get(BenchmarkImpl(b, "vhdl"))
        if our_fmax is not None:
            our_fmax = "yes" if our_fmax >= c.TARGET_FREQ else "no"
        else:
            our_fmax = "-"
        aetherling_fmax = results.get(BenchmarkImpl(b, "verilog"))
        if aetherling_fmax is not None:
            aetherling_fmax = "yes" if aetherling_fmax >= c.TARGET_FREQ else "no"
        else:
            aetherling_fmax = "-"
        title = b.name[3:] if b.name.startswith("big") else b.name
        print(f"| {title:<9} | {b.throughput_str:>10} | {our_fmax:>4} | {aetherling_fmax:>10} |")


def main() -> None:
    """
    Script entry point.
    """
    area_results = crud.read_valid_resource_usage_results(c.AETHERLING_RESOURCE_USAGE_CSV)
    latency_results = crud.read_valid_latency_results(c.AETHERLING_LATENCY_CSV)
    fmax_results = crud.read_valid_fmax_estimates(c.AETHERLING_FMAX_ESTIMATE_CSV)
    print_alm_table(area_results)
    print()
    print_bram_table(area_results)
    print()
    print_dsp_table(area_results)
    print()
    print_latency_table(latency_results)
    print()
    print_fmax_table(fmax_results)


if __name__ == "__main__":
    main()
