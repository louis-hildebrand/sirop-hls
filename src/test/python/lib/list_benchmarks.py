"""
Functions for listing available benchmarks.
"""

from pathlib import Path

from . import constants as c
from .benchmark import Benchmark


def get_benchmark_names(bench_paths: list[Path]) -> list[str]:
    """
    Return the name of each benchmark.
    """
    for p in bench_paths:
        if not p.exists():
            raise ValueError(f"Path '{p.as_posix()}' does not exist")
    return [p.stem for p in bench_paths]


def all_benchmarks() -> list[str]:
    """
    Return the list of all available benchmarks.
    """
    return get_benchmark_names(list(c.AETHERLING_SPACETIME_DIR.iterdir()))


def names_from_args(args: list[str]) -> list[str]:
    """
    Parse the given command-line arguments to a list of benchmark names.
    """
    return sorted(
        get_benchmark_names([Path(a) for a in args])
            or all_benchmarks(),
        key=Benchmark.parse,
    )
