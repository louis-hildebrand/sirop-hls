"""
Function for reading the CSV results.
"""

import csv
from fractions import Fraction
from pathlib import Path

from .benchmark import Benchmark, BenchmarkImpl
from .resource_usage import ResourceUsage


def read_all_resource_usage_results(
    results_file: Path
) -> dict[BenchmarkImpl, ResourceUsage | None]:
    """
    Read all results from the CSV, even ones where the resource usage is
    missing.
    """
    def get_bench(row) -> BenchmarkImpl:
        return BenchmarkImpl(
            bench=Benchmark(
                name=row["bench_name"],
                throughput=Fraction(row["bench_throughput"])
            ),
            language=row["language"]
        )
    def get_results(row) -> ResourceUsage | None:
        if not row["alm"] or not row["bram"] or not row["dsp"]:
            return None
        return ResourceUsage(
            alm=int(row["alm"]),
            bram=int(row["bram"]),
            dsp=int(row["dsp"])
        )
    with open(results_file, "r", encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f)
        rows = list(reader)
        return {get_bench(row) : get_results(row) for row in rows}


def read_valid_resource_usage_results(results_file: Path) -> dict[BenchmarkImpl, ResourceUsage]:
    """
    Read results from the CSV and only return those where the resource usage is
    not `None`.
    """
    return {
        b: ru
        for (b, ru) in read_all_resource_usage_results(results_file).items()
        if ru is not None
    }


def read_all_fmax_results(results_file: Path) -> dict[BenchmarkImpl, float | None]:
    """
    Read all results from the CSV, even ones where the fmax is missing.
    """
    def get_bench(row) -> BenchmarkImpl:
        return BenchmarkImpl(
            bench=Benchmark(
                name=row["bench_name"],
                throughput=Fraction(row["bench_throughput"])
            ),
            language=row["language"]
        )
    def get_results(row) -> float | None:
        if not row["fmax"]:
            return None
        return float(row["fmax"])
    with open(results_file, "r", encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f)
        rows = list(reader)
        return {get_bench(row) : get_results(row) for row in rows}


def read_valid_fmax_results(results_file: Path) -> dict[BenchmarkImpl, float]:
    """
    Read results from the CSV and only return those where fmax is not `None`.
    """
    return {
        b: fmax
        for (b, fmax) in read_all_fmax_results(results_file).items()
        if fmax is not None
    }
