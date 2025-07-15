#!/bin/python3
"""
This script takes all the required measurements for the Aetherling benchmarks.
"""
import csv
import os
import shutil
import subprocess
import sys
from datetime import datetime
from pathlib import Path
from subprocess import CalledProcessError
from typing import TextIO

from lib.benchmark import Benchmark, BenchmarkImpl
from lib.read_results import read_all_results
from lib.resource_usage import ResourceUsage

AETHERLING_COMPILER = "mhir.main.aetherling.Compiler"
ROOT_DIR = Path(__file__).parent.parent.parent.parent.resolve()
AETHERLING_SPACETIME_DIR = (
    ROOT_DIR.joinpath("src", "test", "resources", "aetherling_benchmarks", "original")
)
AETHERLING_VERILOG_DIR = (
    ROOT_DIR.joinpath("src", "test", "resources", "aetherling_benchmarks", "verilog")
)
VHDL_DIR = ROOT_DIR.joinpath("src", "test", "vhdl", "aetherling")
VERILOG_DIR = ROOT_DIR.joinpath("src", "test", "verilog", "aetherling")
TEST_SH_DIR = ROOT_DIR.joinpath("src", "test", "sh")
LOG_DIR = ROOT_DIR.joinpath("results", "logs")
RESULTS_DIR = ROOT_DIR.joinpath("results")
DEFAULT_QPF = ROOT_DIR.joinpath("src", "main", "resources", "mhir", "gen", "vhdl", "top.qpf")
DEFAULT_QSF = ROOT_DIR.joinpath("src", "main", "resources", "mhir", "gen", "vhdl", "top.qsf")
DEFAULT_SDC = ROOT_DIR.joinpath("src", "main", "resources", "mhir", "gen", "vhdl", "top.sdc")


def generate_verilog(benchmarks: list[str]) -> None:
    """
    Create a Verilog project for each benchmark using the Verilog files generated
    by Aetherling.
    """
    for bench in benchmarks:
        proj_dir = VERILOG_DIR.joinpath(bench)
        shutil.rmtree(proj_dir, ignore_errors=True)
        proj_dir.mkdir()
        shutil.copy(
            src=AETHERLING_VERILOG_DIR.joinpath(f"{bench}.v"),
            dst=proj_dir.joinpath("Top.v")
        )
        qpf = proj_dir.joinpath("Top.qpf")
        shutil.copy(src=DEFAULT_QPF, dst=qpf)
        qpf.write_text(qpf.read_text(encoding="utf-8").replace("top", "Top"), encoding="utf-8")
        qsf = proj_dir.joinpath("Top.qsf")
        shutil.copy(src=DEFAULT_QSF, dst=qsf)
        qsf.write_text(qsf.read_text(encoding="utf-8").replace("top", "Top"), encoding="utf-8")
        with open(qsf, "a", encoding="utf-8") as f:
            f.write("set_global_assignment -name VERILOG_FILE Top.v")
        sdc = proj_dir.joinpath("Top.sdc")
        shutil.copy(src=DEFAULT_SDC, dst=sdc)
        sdc.write_text(
            sdc.read_text(encoding="utf-8")
                .replace("clk", "clock")
                .replace("-period 5.0", "-period 5.714"),
            encoding="utf-8"
        )


def generate_vhdl(benchmarks: list[str], log: Path) -> None:
    """
    Call our compiler so as to produce VHDL for each benchmark.
    """
    def make_task(bench: str) -> str:
        in_file = AETHERLING_SPACETIME_DIR.joinpath(f"{bench}.txt").resolve().as_posix()
        out_dir = VHDL_DIR.joinpath(bench).resolve().as_posix()
        return f"runMain {AETHERLING_COMPILER} {in_file} {out_dir} --overwrite"
    tasks = [make_task(b) for b in benchmarks]
    with open(log, "a", encoding="utf-8") as f:
        subprocess.run(["sbt", "; ".join(tasks)], stdout=f, stderr=subprocess.STDOUT, check=True)


def measure_resource_usage(project_dir: Path, top: str, log: Path) -> ResourceUsage | None:
    """
    Synthesize the given design and then extract its resource usage.
    """
    os.chdir(project_dir)
    with open(log, "a", encoding="utf-8") as f:
        try:
            subprocess.run(
                ["quartus_sh", "--flow", "compile", top],
                stdout=f,
                stderr=subprocess.STDOUT,
                check=True
            )
        except CalledProcessError:
            return None
    result = subprocess.run(
        [TEST_SH_DIR.joinpath("extract_alm_count.sh")],
        check=True, capture_output=True, text=True, encoding="utf-8"
    )
    alm = int(result.stdout)
    result = subprocess.run(
        [TEST_SH_DIR.joinpath("extract_bram_count.sh")],
        check=True, capture_output=True, text=True, encoding="utf-8"
    )
    bram = int(result.stdout)
    result = subprocess.run(
        [TEST_SH_DIR.joinpath("extract_dsp_count.sh")],
        check=True, capture_output=True, text=True, encoding="utf-8"
    )
    dsp = int(result.stdout)
    return ResourceUsage(alm=alm, bram=bram, dsp=dsp)


def save_result(writer: csv.DictWriter, b: BenchmarkImpl, ru: ResourceUsage | None) -> None:
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


def measure_and_save_resource_usage(
    b: BenchmarkImpl,
    writer: csv.DictWriter,
    f: TextIO,
    log: Path,
) -> None:
    """
    Measure the resource usage for the given benchmark and save the results.
    """
    bench = b.bench
    print(
        f"Measuring resource usage for {bench.full_name} ({b.language})...",
        flush=True,
        end="",
    )
    project_dir = (
        VERILOG_DIR.joinpath(bench.full_name)
        if b.language.lower() == "verilog"
        else VHDL_DIR.joinpath(bench.full_name)
    )
    top = "Top" if b.language.lower() == "verilog" else "top"
    ru = measure_resource_usage(project_dir=project_dir, top=top, log=log)
    print("failed" if ru is None else "OK")
    save_result(writer, b, ru)
    f.flush()


def merge_results(old: Path, new: Path) -> None:
    """
    Combine the old and new results.

    If a given benchmark has both an old result and a new result, only the new
    result will be kept.
    """
    old_results = read_all_results(old)
    new_results = read_all_results(new)
    combined_results = old_results | new_results
    with open(new, "w", encoding="utf-8", newline="") as f:
        writer = csv.DictWriter(
            f,
            fieldnames=["bench_name", "bench_throughput", "language", "alm", "bram", "dsp"]
        )
        writer.writeheader()
        for b, ru in combined_results.items():
            save_result(writer, b, ru)
    old.unlink()


def main(args: list[str]) -> None:
    """
    The program entry point.
    """
    bench_paths = [Path(p) for p in args[1:]]
    if not bench_paths:
        bench_paths = list(AETHERLING_SPACETIME_DIR.iterdir())
    for p in bench_paths:
        if not p.exists():
            sys.exit(f"Path {p.as_posix()} does not exist.")
    benchmarks = sorted([Benchmark.parse(p.stem) for p in bench_paths])

    now = datetime.now()
    log = LOG_DIR.joinpath(now.strftime("aetherling_%Y%m%d_%H%M%S.log"))
    log.parent.mkdir(exist_ok=True)

    out_path = RESULTS_DIR.joinpath("aetherling.csv")
    out_path.parent.mkdir(exist_ok=True)
    backup_out_path = out_path.with_suffix(out_path.suffix + ".bak")
    if out_path.exists():
        shutil.copy(src=out_path, dst=backup_out_path)

    VHDL_DIR.mkdir(exist_ok=True)
    VERILOG_DIR.mkdir(exist_ok=True, parents=True)

    print(f"Running benchmarks : {', '.join([b.full_name for b in benchmarks])}")
    print(f"Output file        : {out_path.as_posix()}")
    print(f"Log file           : {log.as_posix()}")
    print()
    print("-" * 25)
    print()

    os.chdir(ROOT_DIR)

    print("Creating Verilog projects (Aetherling)...")
    generate_verilog([b.full_name for b in benchmarks])

    print("Generating VHDL projects (ours)...")
    generate_vhdl([b.full_name for b in benchmarks], log=log)

    try:
        with open(out_path, "w", encoding="utf-8", newline="") as out_file:
            writer = csv.DictWriter(
                out_file,
                fieldnames=["bench_name", "bench_throughput", "language", "alm", "bram", "dsp"]
            )
            writer.writeheader()
            for bench in benchmarks:
                measure_and_save_resource_usage(
                    BenchmarkImpl(bench, "verilog"),
                    writer=writer,
                    f=out_file,
                    log=log,
                )
                measure_and_save_resource_usage(
                    BenchmarkImpl(bench, "vhdl"),
                    writer=writer,
                    f=out_file,
                    log=log,
                )
    finally:
        merge_results(old=backup_out_path, new=out_path)


if __name__ == "__main__":
    main(sys.argv)
