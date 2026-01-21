"""
Functions for listing available benchmarks.
"""

from pathlib import Path

from . import constants as c
from .benchmark import Benchmark

ACTIVE_BENCHES = [
    "map", "dot", "bigmvm", "bigmmm",
    "conv1d", "bigconv2d", "bigconvb2b",
    "bigsharpen", "bigsobel", "bigcamera"
]
ACTIVE_BENCH_GLOB = (
    c.AETHERLING_SPACETIME_DIR
        / ("{" + ",".join(ACTIVE_BENCHES) + "}*.txt")
).as_posix()


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
    Return the list of all active benchmarks (i.e., all those which appear in the paper).
    """
    paths = [p for name in ACTIVE_BENCHES for p in c.AETHERLING_SPACETIME_DIR.glob(f"{name}*.txt")]
    return get_benchmark_names(paths)


def names_from_args(args: list[str]) -> list[str]:
    """
    Parse the given command-line arguments to a list of benchmark names.
    """
    return sorted(
        get_benchmark_names([Path(a) for a in args])
            or all_benchmarks(),
        key=Benchmark.parse,
    )
