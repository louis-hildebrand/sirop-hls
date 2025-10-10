"""
Functions for reading and writing the CSV results for the ablation study.
"""

import csv
from pathlib import Path

from .compile_time import CompileTimeReport
from .latency import LatencyResult
from .optimization_level import OptimizationLevel
from .program_variant import ProgramVariant
from .resource_usage import ResourceUsage

RESOURCE_USAGE_HEADERS = ["prog_name", "prog_opt", "alm", "bram", "dsp"]


def save_resource_usage(
        writer: csv.DictWriter,
        p: ProgramVariant,
        ru: ResourceUsage | None
) -> None:
    """
    Save one resource usage result to a CSV file.
    """
    writer.writerow({
        "prog_name": p.name,
        "prog_opt": str(p.lvl),
        "alm": "" if ru is None else ru.alm,
        "bram": "" if ru is None else ru.bram,
        "dsp": "" if ru is None else ru.dsp,
    })


def read_all_resource_usage_results(
    results_file: Path
) -> dict[ProgramVariant, ResourceUsage | None]:
    """
    Read all results from the CSV, even ones where the resource usage is
    missing.
    """
    def get_prog_variant(row) -> ProgramVariant:
        return ProgramVariant(row["prog_name"], OptimizationLevel(row["prog_opt"]))
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
        return {get_prog_variant(row) : get_results(row) for row in rows}


def read_valid_resource_usage_results(
        results_file: Path
) -> dict[ProgramVariant, ResourceUsage]:
    """
    Read results from the CSV and only return those where the resource usage is
    not `None`.
    """
    return {
        p: ru
        for (p, ru) in read_all_resource_usage_results(results_file).items()
        if ru is not None
    }


def merge_resource_usages(old: Path, new: Path) -> None:
    """
    Combine the old and new resource usage results.

    If a given program has both an old result and a new result, only the new
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
        for p, ru in combined_results.items():
            save_resource_usage(writer, p, ru)
    old.unlink()


FMAX_HEADERS = ["prog_name", "prog_opt", "fmax"]


def save_fmax(writer: csv.DictWriter, p: ProgramVariant, fmax: float | None) -> None:
    """
    Save one fmax estimate to a CSV file.
    """
    writer.writerow({
        "prog_name": p.name,
        "prog_opt": p.lvl,
        "fmax": "" if fmax is None else fmax
    })


def read_all_fmax(results_file: Path) -> dict[ProgramVariant, float | None]:
    """
    Read all results from the CSV, even ones where the fmax is missing.
    """
    def get_prog_variant(row) -> ProgramVariant:
        return ProgramVariant(
            name=row["prog_name"],
            lvl=OptimizationLevel(row["prog_opt"])
        )
    def get_results(row) -> float | None:
        if not row["fmax"]:
            return None
        return float(row["fmax"])
    with open(results_file, "r", encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f)
        rows = list(reader)
        return {get_prog_variant(row) : get_results(row) for row in rows}


def read_valid_fmax(results_file: Path) -> dict[ProgramVariant, float]:
    """
    Read results from the CSV and only return those where fmax is not `None`.
    """
    return {
        p: fmax
        for (p, fmax) in read_all_fmax(results_file).items()
        if fmax is not None
    }


def merge_fmax(old: Path, new: Path) -> None:
    """
    Combine the old and new fmax estimates.

    If a given program has both an old result and a new result, only the new
    result will be kept.
    """
    if not old.exists():
        return
    old_results = read_all_fmax(old)
    new_results = read_all_fmax(new)
    combined_results = old_results | new_results
    combined_results = dict(sorted(combined_results.items()))
    with open(new, "w", encoding="utf-8", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=FMAX_HEADERS)
        writer.writeheader()
        for p, ru in combined_results.items():
            save_fmax(writer, p, ru)
    old.unlink()


LATENCY_HEADERS = ["prog_name", "prog_opt", "latency", "sim_success"]


def save_latency(writer: csv.DictWriter, p: ProgramVariant, result: LatencyResult) -> None:
    """
    Save one latency result to a CSV file.
    """
    writer.writerow({
        "prog_name": p.name,
        "prog_opt": p.lvl,
        "latency": "" if result.latency is None else str(result.latency),
        "sim_success": result.sim_success,
    })


def read_all_latency_results(results_file: Path) -> dict[ProgramVariant, LatencyResult]:
    """
    Read all results from the CSV, even ones where the latency is missing.
    """
    def get_prog_variant(row) -> ProgramVariant:
        return ProgramVariant(
            name=row["prog_name"],
            lvl=OptimizationLevel(row["prog_opt"])
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
        return {get_prog_variant(row) : get_result(row) for row in rows}


def read_valid_latency_results(results_file: Path) -> dict[ProgramVariant, LatencyResult]:
    """
    Read results from the CSV and only return those where the latency is
    not `None`.
    """
    return {
        p: lat
        for (p, lat) in read_all_latency_results(results_file).items()
        if lat.latency is not None
    }


def merge_latency_results(old: Path, new: Path) -> None:
    """
    Combine the old and new latency results.

    If a given program has both an old result and a new result, only the new
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
        for p, ru in combined_results.items():
            save_latency(writer, p, ru)
    old.unlink()


COMPILE_TIME_HEADERS = [
    "prog_name", "prog_opt",
    "parse", "typecheck", "lower", "make_synth", "optimize", "codegen"
]


def save_compile_time(writer: csv.DictWriter, p: ProgramVariant, result: CompileTimeReport) -> None:
    """
    Save one compile time result to a CSV file.
    """
    writer.writerow({
        "prog_name": p.name,
        "prog_opt": p.lvl,
        "parse": result.parse,
        "typecheck": result.typecheck,
        "lower": result.lower,
        "make_synth": result.make_synth,
        "optimize": result.optimize,
        "codegen": result.codegen
    })


def read_compile_times(results_file: Path) -> dict[ProgramVariant, CompileTimeReport]:
    """
    Read all compile times from the given CSV.
    """
    def get_prog_variant(row) -> ProgramVariant:
        return ProgramVariant(
            name=row["prog_name"],
            lvl=OptimizationLevel(row["prog_opt"])
        )
    def get_result(row) -> CompileTimeReport:
        return CompileTimeReport(
            parse=int(row["parse"]),
            typecheck=int(row["typecheck"]),
            lower=int(row["lower"]),
            make_synth=int(row["make_synth"]),
            optimize=int(row["optimize"]),
            codegen=int(row["codegen"]),
        )
    with open(results_file, "r", encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f)
        rows = list(reader)
        return {get_prog_variant(row) : get_result(row) for row in rows}


def merge_compile_times(old: Path, new: Path) -> None:
    """
    Combine the old and new compile time results.

    If a given program has both an old result and a new result, only the new
    result will be kept.
    """
    if not old.exists():
        return
    old_results = read_compile_times(old)
    new_results = read_compile_times(new)
    combined_results = old_results | new_results
    combined_results = dict(sorted(combined_results.items()))
    with open(new, "w", encoding="utf-8", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=COMPILE_TIME_HEADERS)
        writer.writeheader()
        for p, ru in combined_results.items():
            save_compile_time(writer, p, ru)
    old.unlink()
