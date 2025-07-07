#!/bin/python3

"""
This script plots the results from aetherling_measure.py.
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
    with open(RESULTS_FILE, "r", encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f)
        rows = list(reader)
        return {
            Benchmark(name=row["bench_name"], throughput=int(row["bench_throughput"])) : ResourceUsage(alm=int(row["alm"]), bram=int(row["bram"]), dsp=int(row["dsp"]))
            for row in rows
            if row["alm"] and row["bram"] and row["dsp"]
        }


def plot_resource_usages(results: dict[Benchmark, ResourceUsage]) -> None:
    """
    Plot throughput vs resource usage for each benchmark.
    """
    benchmark_names = {bench.name for bench in results.keys()}
    fig, axes = plt.subplots(nrows=3, ncols=len(benchmark_names))
    axes = list(axes)
    if not isinstance(axes[0], list):
        axes = [[ax] for ax in axes]
    for col, bench_name in enumerate(benchmark_names):
        benchmarks = [b for b in results.keys() if b.name == bench_name]
        benchmarks = sorted(benchmarks, key=lambda b: b.throughput)
        # Plot ALM usage
        alm_ax = axes[0][col]
        xs = [b.throughput for b in benchmarks]
        ys = [results[b].alm for b in benchmarks]
        alm_ax.plot(xs, ys, marker="o")
        alm_ax.get_xaxis().set_ticks([])
        alm_ax.set_ylabel("ALMs")
        # Plot BRAM usage
        bram_ax = axes[1][col]
        xs = [b.throughput for b in benchmarks]
        ys = [results[b].bram for b in benchmarks]
        bram_ax.plot(xs, ys, marker="o")
        bram_ax.get_xaxis().set_ticks([])
        bram_ax.set_ylabel("BRAMs")
        if all(y == 0 for y in ys):
            bram_ax.set_ylim(-1, 1)
        # Plot DSP usage
        dsp_ax = axes[2][col]
        xs = [b.throughput for b in benchmarks]
        ys = [results[b].dsp for b in benchmarks]
        dsp_ax.plot(xs, ys, marker="o")
        dsp_ax.get_xaxis().set_ticks([b.throughput for b in benchmarks])
        dsp_ax.set_ylabel("DSPs")
        if all(y == 0 for y in ys):
            dsp_ax.set_ylim(-1, 1)
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
