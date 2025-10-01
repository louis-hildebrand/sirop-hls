"""
Function for reading the CSV results.
"""

import csv
from fractions import Fraction
from pathlib import Path

from .benchmark import Benchmark, BenchmarkImpl
from .fmax import Fmax, Step
from .latency import LatencyResult
from .resource_usage import ResourceUsage

RESOURCE_USAGE_HEADERS = ["bench_name", "bench_throughput", "language", "alm", "bram", "dsp"]


def save_resource_usage(writer: csv.DictWriter, b: BenchmarkImpl, ru: ResourceUsage | None) -> None:
    """
    Save one resource usage result to a CSV file.
    """
    writer.writerow({
        "bench_name": b.bench.name,
        "bench_throughput": b.bench.throughput_str,
        "language": b.language.lower(),
        "alm": "" if ru is None else ru.alm,
        "bram": "" if ru is None else ru.bram,
        "dsp": "" if ru is None else ru.dsp,
    })


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


def merge_resource_usages(old: Path, new: Path) -> None:
    """
    Combine the old and new resource usage results.

    If a given benchmark has both an old result and a new result, only the new
    result will be kept.
    """
    if not old.exists():
        return
    old_results = read_all_resource_usage_results(old)
    new_results = read_all_resource_usage_results(new)
    combined_results = old_results | new_results
    combined_results = dict(sorted(combined_results.items()))
    with open(new, "w", encoding="utf-8", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=RESOURCE_USAGE_HEADERS)
        writer.writeheader()
        for b, ru in combined_results.items():
            save_resource_usage(writer, b, ru)
    old.unlink()


FMAX_ESTIMATE_HEADERS = ["bench_name", "bench_throughput", "language", "fmax"]


def save_fmax_estimate(writer: csv.DictWriter, b: BenchmarkImpl, fmax: float | None) -> None:
    """
    Save one fmax estimate to a CSV file.
    """
    writer.writerow({
        "bench_name": b.bench.name,
        "bench_throughput": b.bench.throughput_str,
        "language": b.language.lower(),
        "fmax": "" if fmax is None else fmax
    })


def read_all_fmax_estimates(results_file: Path) -> dict[BenchmarkImpl, float | None]:
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


def read_valid_fmax_estimates(results_file: Path) -> dict[BenchmarkImpl, float]:
    """
    Read results from the CSV and only return those where fmax is not `None`.
    """
    return {
        b: fmax
        for (b, fmax) in read_all_fmax_estimates(results_file).items()
        if fmax is not None
    }


def merge_fmax_estimates(old: Path, new: Path) -> None:
    """
    Combine the old and new fmax estimates.

    If a given benchmark has both an old result and a new result, only the new
    result will be kept.
    """
    if not old.exists():
        return
    old_results = read_all_fmax_estimates(old)
    new_results = read_all_fmax_estimates(new)
    combined_results = old_results | new_results
    combined_results = dict(sorted(combined_results.items()))
    with open(new, "w", encoding="utf-8", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=FMAX_ESTIMATE_HEADERS)
        writer.writeheader()
        for b, ru in combined_results.items():
            save_fmax_estimate(writer, b, ru)
    old.unlink()


FMAX_MEASUREMENT_HEADER = [
    "bench_name", "bench_throughput", "language",
    "fmax_lower", "fmax_upper", "steps",
]


def save_fmax_measurement(writer: csv.DictWriter, b: BenchmarkImpl, fmax: Fmax | None) -> None:
    """
    Save one fmax measurement to a CSV.
    """
    def success_to_str(b: bool | None) -> str:
        # pylint: disable=no-else-return
        if b is None:
            return "none"
        elif b:
            return "pass"
        else:
            return "fail"
    steps = ";".join(
        []
        if fmax is None
        else [f"{s.freq}:{success_to_str(s.success)}" for s in fmax.steps]
    )
    writer.writerow({
        "bench_name": b.bench.name,
        "bench_throughput": b.bench.throughput_str,
        "language": b.language.lower(),
        "fmax_lower": "" if fmax is None else fmax.lower,
        "fmax_upper": "" if fmax is None else fmax.upper,
        "steps": steps
    })


def read_all_fmax_measurements(results_file: Path) -> dict[BenchmarkImpl, Fmax | None]:
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
    def get_step(s: str) -> Step:
        parts = s.split(":")
        if len(parts) != 2:
            raise ValueError(f"Invalid step: '{s}' (wrong number of parts)")
        freq = int(parts[0])
        if parts[1] == "pass":
            success = True
        elif parts[1] == "fail":
            success = False
        elif parts[1] == "none":
            success = None
        else:
            raise ValueError(f"Invalid step: '{s}' (invalid second part)")
        return Step(freq, success)
    def get_steps(s: str) -> list[Step]:
        steps = s.split(";")
        return [get_step(s) for s in steps]
    def get_result(row) -> Fmax | None:
        if not row["fmax_lower"] and not row["fmax_upper"]:
            return None
        lower = None if not row["fmax_lower"] else int(row["fmax_lower"])
        upper = None if not row["fmax_upper"] else int(row["fmax_upper"])
        steps = get_steps(row["steps"])
        return Fmax(lower=lower, upper=upper, steps=steps)
    with open(results_file, "r", encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f)
        rows = list(reader)
        return {get_bench(row) : get_result(row) for row in rows}


def read_valid_fmax_measurements(results_file: Path) -> dict[BenchmarkImpl, Fmax]:
    """
    Read results from the CSV and only return those where fmax is not `None`.
    """
    return {
        b: fmax
        for (b, fmax) in read_all_fmax_measurements(results_file).items()
        if fmax is not None
    }


def merge_fmax_measurements(old: Path, new: Path) -> None:
    """
    Combine the old and new fmax measurements.

    If a given benchmark has both an old result and a new result, only the new
    result will be kept.
    """
    if not old.exists():
        return
    old_results = read_all_fmax_measurements(old)
    new_results = read_all_fmax_measurements(new)
    combined_results = old_results | new_results
    combined_results = dict(sorted(combined_results.items()))
    with open(new, "w", encoding="utf-8", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=FMAX_MEASUREMENT_HEADER)
        writer.writeheader()
        for b, ru in combined_results.items():
            save_fmax_measurement(writer, b, ru)
    old.unlink()


LATENCY_HEADERS = ["bench_name", "bench_throughput", "language", "latency", "sim_success"]


def save_latency(writer: csv.DictWriter, b: BenchmarkImpl, result: LatencyResult) -> None:
    """
    Save one latency result to a CSV file.
    """
    writer.writerow({
        "bench_name": b.bench.name,
        "bench_throughput": b.bench.throughput_str,
        "language": b.language.lower(),
        "latency": "" if result.latency is None else str(result.latency),
        "sim_success": result.sim_success,
    })


def read_all_latency_results(results_file: Path) -> dict[BenchmarkImpl, LatencyResult]:
    """
    Read all results from the CSV, even ones where the latency is missing.
    """
    def get_bench(row) -> BenchmarkImpl:
        return BenchmarkImpl(
            bench=Benchmark(
                name=row["bench_name"],
                throughput=Fraction(row["bench_throughput"])
            ),
            language=row["language"]
        )
    def get_result(row) -> LatencyResult:
        latency = int(row["latency"]) if row["latency"] else None
        if row["sim_success"] == "True":
            sim_success = True
        elif row["sim_success"] == "False":
            sim_success = False
        else:
            raise ValueError(f"Invalid value for sim_success: {row['sim_success']}")
        return LatencyResult(latency=latency, sim_success=sim_success)
    with open(results_file, "r", encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f)
        rows = list(reader)
        return {get_bench(row) : get_result(row) for row in rows}


def read_valid_latency_results(results_file: Path) -> dict[BenchmarkImpl, LatencyResult]:
    """
    Read results from the CSV and only return those where the latency is
    not `None`.
    """
    return {
        b: lat
        for (b, lat) in read_all_latency_results(results_file).items()
        if lat.latency is not None
    }


def merge_latency_results(old: Path, new: Path) -> None:
    """
    Combine the old and new latency results.

    If a given benchmark has both an old result and a new result, only the new
    result will be kept.
    """
    if not old.exists():
        return
    old_results = read_all_latency_results(old)
    new_results = read_all_latency_results(new)
    combined_results = old_results | new_results
    combined_results = dict(sorted(combined_results.items()))
    with open(new, "w", encoding="utf-8", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=LATENCY_HEADERS)
        writer.writeheader()
        for b, ru in combined_results.items():
            save_latency(writer, b, ru)
    old.unlink()
