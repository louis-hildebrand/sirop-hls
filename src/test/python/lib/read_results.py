"""
Function for reading the CSV results.
"""

import csv
from fractions import Fraction
from pathlib import Path

from .benchmark import Benchmark, BenchmarkImpl
from .resource_usage import ResourceUsage


def read_results(results_file: Path) -> dict[BenchmarkImpl, ResourceUsage]:
    """
    Read the CSV file back into a dictionary.
    """
    def get_bench(row) -> BenchmarkImpl:
        return BenchmarkImpl(
            bench=Benchmark(
                name=row["bench_name"],
                throughput=Fraction(row["bench_throughput"])
            ),
            language=row["language"]
        )
    def get_results(row) -> ResourceUsage:
        return ResourceUsage(
            alm=int(row["alm"]),
            bram=int(row["bram"]),
            dsp=int(row["dsp"])
        )
    with open(results_file, "r", encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f)
        rows = list(reader)
        return {
            get_bench(row) : get_results(row)
            for row in rows
            if row["alm"] and row["bram"] and row["dsp"]
        }
