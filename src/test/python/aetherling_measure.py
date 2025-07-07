#!/bin/python3
"""
This script takes all the required measurements for the Aetherling benchmarks.
"""
import csv
import os
import subprocess
import sys
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path
from subprocess import CalledProcessError

AETHERLING_COMPILER = "mhir.main.aetherling.Compiler"
ROOT_DIR = Path(__file__).parent.parent.parent.parent.resolve()
AETHERLING_BENCHMARKS_DIR = ROOT_DIR.joinpath("src", "test", "resources", "aetherling_benchmarks", "original")
VHDL_DIR = ROOT_DIR.joinpath("vhdl")
TEST_SH_DIR = ROOT_DIR.joinpath("src", "test", "sh")
LOG_DIR = ROOT_DIR.joinpath("logs")
RESULTS_DIR = ROOT_DIR.joinpath("results")


@dataclass
class ResourceUsage:
    """
    The resource usage of a VHDL design.
    """
    alm: int
    bram: int
    dsp: int


def try_int(x: str) -> int | str:
    """
    Try to convert a string to an int, otherwise return the original string.
    """
    try:
        return int(x)
    except ValueError:
        return x


def sort_nat(xs: list[str]) -> list[str]:
    """
    Sort a list of strings, but try to keep numbers in ascending order.

    ## Example:
    >>> sort_nat(['map_10', 'map_1', 'map_20', 'map_2'])
    ['map_1', 'map_2', 'map_10', 'map_20']
    """
    return sorted(xs, key=lambda x: tuple(try_int(y) for y in x.split("_")))


def generate_vhdl(benchmarks: list[str], log: Path) -> None:
    """
    Call our compiler so as to produce VHDL for each benchmark.
    """
    def make_task(bench: str) -> str:
        in_file = AETHERLING_BENCHMARKS_DIR.joinpath(f"{bench}.txt").resolve().as_posix()
        out_dir = VHDL_DIR.joinpath(bench).resolve().as_posix()
        return f"runMain {AETHERLING_COMPILER} {in_file} {out_dir} --overwrite"
    tasks = [make_task(b) for b in benchmarks]
    with open(log, "a", encoding="utf-8") as f:
        subprocess.run(["sbt", "; ".join(tasks)], stdout=f, stderr=subprocess.STDOUT, check=True)


def measure_resource_usage(bench: str, log: Path) -> ResourceUsage | None:
    """
    Synthesize the given benchmark and then extract its resource usage.
    """
    os.chdir(VHDL_DIR.joinpath(bench))
    with open(log, "a", encoding="utf-8") as f:
        try:
            subprocess.run(["quartus_sh", "--flow", "compile", "top"], stdout=f, stderr=subprocess.STDOUT, check=True)
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
        bench_paths = list(AETHERLING_BENCHMARKS_DIR.iterdir())
    for p in bench_paths:
        if not p.exists():
            sys.exit(f"Path {p.as_posix()} does not exist.")
    benchmarks = sort_nat([p.stem for p in bench_paths])

    now = datetime.now()
    log = LOG_DIR.joinpath(now.strftime("aetherling_%Y%m%d_%H%M%S.log"))
    log.parent.mkdir(exist_ok=True)

    out_path = RESULTS_DIR.joinpath("aetherling.csv")
    out_path.parent.mkdir(exist_ok=True)

    print(f"Running benchmarks : {', '.join(benchmarks)}")
    print(f"Output file        : {out_path.as_posix()}")
    print(f"Log file           : {log.as_posix()}")
    print()
    print("-" * 25)
    print()

    os.chdir(ROOT_DIR)

    print("Generating VHDL...")
    generate_vhdl(benchmarks, log=log)

    with open(out_path, "w", encoding="utf-8") as out_file:
        writer = csv.DictWriter(out_file, fieldnames=["bench", "alm", "bram", "dsp"])
        writer.writeheader()
        for bench in benchmarks:
            print(f"Measuring resource usage for {bench}... ", flush=True, end="")
            ru = measure_resource_usage(bench, log=log)
            if ru is None:
                print("failed")
                writer.writerow({"bench": bench, "alm": "", "bram": "", "dsp": ""})
            else:
                print("OK")
                writer.writerow({"bench": bench, "alm": ru.alm, "bram": ru.bram, "dsp": ru.dsp})


if __name__ == "__main__":
    main(sys.argv)
