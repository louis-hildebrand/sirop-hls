#!/bin/python3

"""
This script plots the results for the Aetherling benchmarks.
"""

from __future__ import annotations

import csv
import sys
from dataclasses import dataclass
from pathlib import Path

import matplotlib.pyplot as plt

ROOT_DIR = Path(__file__).parent.parent.parent.parent.resolve()
RESULTS_DIR = ROOT_DIR.joinpath("results")
RESULTS_FILE = RESULTS_DIR.joinpath("aetherling.csv")
PLOT_FILE = RESULTS_DIR.joinpath("aetherling.pdf")


@dataclass(frozen=True, order=True)
class Benchmark:
    """
    A benchmark with a specific throughput.
    """
    name: str
    throughput: int

    @classmethod
    def parse(cls, name: str) -> Benchmark:
        parts = name.split("_")
        if len(parts) != 2:
            raise ValueError(f"Invalid benchmark name: {name}")
        return Benchmark(name=parts[0], throughput=int(parts[1]))

    @property
    def full_name(self) -> str:
        return f"{self.name}_{self.throughput}"


@dataclass(frozen=True, order=True)
class BenchmarkImpl:
    """
    A VHDL or Verilog which implements a specific benchmark.
    """
    bench: Benchmark
    language: str


@dataclass
class ResourceUsage:
    """
    The resource usage of a VHDL design.
    """
    alm: int
    bram: int
    dsp: int


def read_results() -> dict[Benchmark, ResourceUsage]:
    """
    Read the CSV file back into a dictionary.
    """
    def get_bench(row) -> BenchmarkImpl:
        return BenchmarkImpl(
            bench=Benchmark(
                name=row["bench_name"],
                throughput=int(row["bench_throughput"])
            ),
            language=row["language"]
        )
    def get_results(row) -> ResourceUsage:
        return ResourceUsage(
            alm=int(row["alm"]),
            bram=int(row["bram"]),
            dsp=int(row["dsp"])
        )
    with open(RESULTS_FILE, "r", encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f)
        rows = list(reader)
        return {
            get_bench(row) : get_results(row)
            for row in rows
            if row["alm"] and row["bram"] and row["dsp"]
        }


def plot_resource_usages(results: dict[BenchmarkImpl, ResourceUsage]) -> None:
    """
    Plot throughput vs resource usage for each benchmark.
    """

    AETHERLING_LABEL = "Aetherling \u2192 Chisel \u2192 Verilog"
    AETHERLING_MARKER = "^"
    OUR_LABEL = "Aetherling \u2192 mhir \u2192 VHDL"
    OUR_MARKER = "o"

    benchmark_names = {res.bench.name for res in results.keys()}
    fig, axes = plt.subplots(nrows=3, ncols=len(benchmark_names))
    axes = list(axes)
    if not isinstance(axes[0], list):
        axes = [[ax] for ax in axes]
    for col, bench_name in enumerate(benchmark_names):
        verilog_benchmarks = [
            b
            for b in results.keys()
            if b.bench.name == bench_name and b.language == "verilog"
        ]
        verilog_benchmarks = sorted(verilog_benchmarks, key=lambda b: (b.language, b.bench.throughput))
        vhdl_benchmarks = [
            b
            for b in results.keys()
            if b.bench.name == bench_name and b.language == "vhdl"
        ]
        vhdl_benchmarks = sorted(vhdl_benchmarks, key=lambda b: (b.language, b.bench.throughput))
        # Plot ALM usage
        alm_ax = axes[0][col]
        xs = [b.bench.throughput for b in verilog_benchmarks]
        ys = [results[b].alm for b in verilog_benchmarks]
        verilog_artist, = alm_ax.plot(xs, ys, marker=AETHERLING_MARKER, label=AETHERLING_LABEL)
        xs = [b.bench.throughput for b in vhdl_benchmarks]
        ys = [results[b].alm for b in vhdl_benchmarks]
        vhdl_artist, = alm_ax.plot(xs, ys, marker=OUR_MARKER, label=OUR_LABEL)
        alm_ax.get_xaxis().set_ticks([])
        alm_ax.set_ylabel("ALMs")
        # Plot BRAM usage
        bram_ax = axes[1][col]
        xs = [b.bench.throughput for b in verilog_benchmarks]
        ys = [results[b].bram for b in verilog_benchmarks]
        bram_ax.plot(xs, ys, marker=AETHERLING_MARKER, label=AETHERLING_LABEL)
        xs = [b.bench.throughput for b in vhdl_benchmarks]
        ys = [results[b].bram for b in vhdl_benchmarks]
        bram_ax.plot(xs, ys, marker=OUR_MARKER, label=OUR_LABEL)
        bram_ax.get_xaxis().set_ticks([])
        bram_ax.set_ylabel("BRAMs")
        if all(y == 0 for y in ys):
            bram_ax.set_ylim(-1, 1)
        # Plot DSP usage
        dsp_ax = axes[2][col]
        xs = [b.bench.throughput for b in verilog_benchmarks]
        ys = [results[b].dsp for b in verilog_benchmarks]
        dsp_ax.plot(xs, ys, marker=AETHERLING_MARKER, label=AETHERLING_LABEL)
        xs = [b.bench.throughput for b in vhdl_benchmarks]
        ys = [results[b].dsp for b in vhdl_benchmarks]
        dsp_ax.plot(xs, ys, marker=OUR_MARKER, label=OUR_LABEL)
        dsp_ax.get_xaxis().set_ticks([b.bench.throughput for b in vhdl_benchmarks])
        dsp_ax.set_ylabel("DSPs")
        if all(y == 0 for y in ys):
            dsp_ax.set_ylim(-1, 1)
        # Settings for the whole column
        alm_ax.legend(handles=[vhdl_artist, verilog_artist])
        alm_ax.set_title(bench_name)
        dsp_ax.set_xlabel("Throughput")
    fig.tight_layout()
    fig.savefig(PLOT_FILE)


def main() -> None:
    """
    The program entry point.
    """
    results = read_results()
    if not results:
        sys.exit("No results to plot.")
    plot_resource_usages(results)


if __name__ == "__main__":
    main()
