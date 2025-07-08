#!/bin/python3
"""
This script takes all the required measurements for the Aetherling benchmarks.
"""
from __future__ import annotations

import csv
import os
import shutil
import subprocess
import sys
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path
from subprocess import CalledProcessError

AETHERLING_COMPILER = "mhir.main.aetherling.Compiler"
ROOT_DIR = Path(__file__).parent.parent.parent.parent.resolve()
AETHERLING_SPACETIME_DIR = ROOT_DIR.joinpath("src", "test", "resources", "aetherling_benchmarks", "original")
AETHERLING_VERILOG_DIR = ROOT_DIR.joinpath("src", "test", "resources", "aetherling_benchmarks", "verilog")
VHDL_DIR = ROOT_DIR.joinpath("src", "test", "vhdl", "aetherling")
VERILOG_DIR = ROOT_DIR.joinpath("src", "test", "verilog", "aetherling")
TEST_SH_DIR = ROOT_DIR.joinpath("src", "test", "sh")
LOG_DIR = ROOT_DIR.joinpath("results", "logs")
RESULTS_DIR = ROOT_DIR.joinpath("results")
DEFAULT_QPF = ROOT_DIR.joinpath("src", "main", "resources", "mhir", "gen", "vhdl", "top.qpf")
DEFAULT_QSF = ROOT_DIR.joinpath("src", "main", "resources", "mhir", "gen", "vhdl", "top.qsf")
DEFAULT_SDC = ROOT_DIR.joinpath("src", "main", "resources", "mhir", "gen", "vhdl", "top.sdc")


@dataclass(frozen=True, order=True)
class Benchmark:
    """
    A benchmark with a specific throughput.
    """
    name: str
    throughput: int

    @classmethod
    def parse(cls, name: str) -> Benchmark:
        """
        Parse the given benchmark full name (e.g., 'map_20').
        """
        parts = name.split("_")
        if len(parts) != 2:
            raise ValueError(f"Invalid benchmark name: {name}")
        return Benchmark(name=parts[0], throughput=int(parts[1]))

    @property
    def full_name(self) -> str:
        """
        Return the full name of this benchmark (e.g., 'map_20').
        """
        return f"{self.name}_{self.throughput}"


@dataclass(frozen=True)
class ResourceUsage:
    """
    The resource usage of a VHDL design.
    """
    alm: int
    bram: int
    dsp: int


def generate_verilog(benchmarks: list[str]) -> None:
    """
    Create a Verilog project for each benchmark using the Verilog files generated
    by Aetherling.
    """
    for bench in benchmarks:
        proj_dir = VERILOG_DIR.joinpath(bench)
        shutil.rmtree(proj_dir, ignore_errors=True)
        proj_dir.mkdir()
        shutil.copy(src=AETHERLING_VERILOG_DIR.joinpath(f"{bench}.v"), dst=proj_dir.joinpath("Top.v"))
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
            subprocess.run(["quartus_sh", "--flow", "compile", top], stdout=f, stderr=subprocess.STDOUT, check=True)
        except CalledProcessError:
            return None
    result = subprocess.run([TEST_SH_DIR.joinpath("extract_alm_count.sh")], check=True, capture_output=True, text=True, encoding="utf-8")
    alm = int(result.stdout)
    result = subprocess.run([TEST_SH_DIR.joinpath("extract_bram_count.sh")], check=True, capture_output=True, text=True, encoding="utf-8")
    bram = int(result.stdout)
    result = subprocess.run([TEST_SH_DIR.joinpath("extract_dsp_count.sh")], check=True, capture_output=True, text=True, encoding="utf-8")
    dsp = int(result.stdout)
    return ResourceUsage(alm=alm, bram=bram, dsp=dsp)


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

    with open(out_path, "w", encoding="utf-8") as out_file:
        writer = csv.DictWriter(out_file, fieldnames=["bench_name", "bench_throughput", "language", "alm", "bram", "dsp"])
        writer.writeheader()
        for bench in benchmarks:
            print(f"Measuring resource usage for {bench.full_name} (Verilog)...", flush=True, end="")
            ru = measure_resource_usage(project_dir=VERILOG_DIR.joinpath(bench.full_name), top="Top", log=log)
            if ru is None:
                print("failed")
                writer.writerow({
                    "bench_name": bench.name, "bench_throughput": bench.throughput,
                    "language": "verilog",
                    "alm": "", "bram": "", "dsp": ""
                })
            else:
                print("OK")
                writer.writerow({
                    "bench_name": bench.name, "bench_throughput": bench.throughput,
                    "language": "verilog",
                    "alm": ru.alm, "bram": ru.bram, "dsp": ru.dsp
                })

            print(f"Measuring resource usage for {bench.full_name} (VHDL)... ", flush=True, end="")
            ru = measure_resource_usage(project_dir=VHDL_DIR.joinpath(bench.full_name), top="top", log=log)
            if ru is None:
                print("failed")
                writer.writerow({
                    "bench_name": bench.name, "bench_throughput": bench.throughput,
                    "language": "vhdl",
                    "alm": "", "bram": "", "dsp": ""
                })
            else:
                print("OK")
                writer.writerow({
                    "bench_name": bench.name, "bench_throughput": bench.throughput,
                    "language": "vhdl",
                    "alm": ru.alm, "bram": ru.bram, "dsp": ru.dsp
                })

            out_file.flush()


if __name__ == "__main__":
    main(sys.argv)
