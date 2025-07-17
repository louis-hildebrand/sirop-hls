#!/bin/python3

"""
This script generates and synthesizes VHDL and Verilog projects for the given benchmarks.
"""

import os
import shutil
import subprocess
from argparse import ArgumentParser, Namespace

import lib.constants as c
import lib.list_benchmarks as lb
from lib import synth


def generate_verilog(benchmarks: list[str]) -> None:
    """
    Create a Verilog project for each benchmark using the Verilog files generated
    by Aetherling.
    """
    for bench in benchmarks:
        proj_dir = c.VERILOG_DIR.joinpath(bench)
        shutil.rmtree(proj_dir, ignore_errors=True)
        proj_dir.mkdir()
        shutil.copy(
            src=c.AETHERLING_VERILOG_DIR.joinpath(f"{bench}.v"),
            dst=proj_dir.joinpath("Top.v")
        )
        qpf = proj_dir.joinpath("Top.qpf")
        shutil.copy(src=c.DEFAULT_QPF, dst=qpf)
        qpf.write_text(qpf.read_text(encoding="utf-8").replace("top", "Top"), encoding="utf-8")
        qsf = proj_dir.joinpath("Top.qsf")
        shutil.copy(src=c.DEFAULT_QSF, dst=qsf)
        qsf.write_text(qsf.read_text(encoding="utf-8").replace("top", "Top"), encoding="utf-8")
        with open(qsf, "a", encoding="utf-8") as f:
            f.write("set_global_assignment -name VERILOG_FILE Top.v")
        sdc = proj_dir.joinpath("Top.sdc")
        shutil.copy(src=c.DEFAULT_SDC, dst=sdc)
        sdc.write_text(sdc.read_text(encoding="utf-8").replace("clk", "clock"), encoding="utf-8")


def generate_vhdl(benchmarks: list[str]) -> None:
    """
    Call our compiler so as to produce VHDL for each benchmark.
    """
    def make_task(bench: str) -> str:
        in_file = c.AETHERLING_SPACETIME_DIR.joinpath(f"{bench}.txt").resolve().as_posix()
        out_dir = c.VHDL_DIR.joinpath(bench).resolve().as_posix()
        return f"runMain {c.AETHERLING_COMPILER} {in_file} {out_dir} --overwrite --show-final"
    tasks = [make_task(b) for b in benchmarks]
    os.chdir(c.ROOT_DIR)
    subprocess.run(["sbt", "; ".join(tasks)], check=True)


def main(benchmarks: list[str], skip_verilog: bool, skip_vhdl: bool, skip_synth: bool) -> None:
    """
    Script entry point
    """
    print("-" * 80)
    print("- Generating and synthesizing projects...")
    print(f"- Benchmarks : {', '.join(benchmarks)}")
    print("-" * 80)

    if not skip_verilog:
        c.VERILOG_DIR.mkdir(exist_ok=True, parents=True)
        print("Generating Verilog...")
        generate_verilog(benchmarks)
        if not skip_synth:
            print("Synthesizing Verilog...")
            for b in benchmarks:
                proj_dir = c.VERILOG_DIR.joinpath(b)
                ok = synth.synthesize_design(proj_dir)
                if not ok:
                    print(f" failed to synthesize {proj_dir}")

    if not skip_vhdl:
        c.VHDL_DIR.mkdir(exist_ok=True, parents=True)
        print("Generating VHDL...")
        generate_vhdl(benchmarks)
        if not skip_synth:
            print("Synthesizing VHDL...")
            for b in benchmarks:
                proj_dir = c.VHDL_DIR.joinpath(b)
                ok = synth.synthesize_design(proj_dir)
                if not ok:
                    print(f" failed to synthesize {proj_dir}")

    print()


def parse_args() -> Namespace:
    """
    Parse the command line arguments.
    """
    parser = ArgumentParser(description="Generates and synthesizes the given benchmarks.")
    parser.add_argument(
        "benchmarks",
        nargs="*",
        help=(
            "the benchmarks to process."
            " These can be any file paths;"
            " the benchmark names will be extracted from the file names."
        )
    )
    parser.add_argument(
        "--skip-verilog",
        action="store_true",
        help="don't generate or synthesize any Verilog code"
    )
    parser.add_argument(
        "--skip-vhdl",
        action="store_true",
        help="don't generate or synthesize any VHDL code"
    )
    parser.add_argument(
        "--skip-synth",
        action="store_true",
        help="don't synthesize any projects; only generate the HDL code"
    )
    args = parser.parse_args()
    args.benchmarks = lb.names_from_args(args.benchmarks)
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(
        benchmarks=_args.benchmarks,
        skip_verilog=_args.skip_verilog,
        skip_vhdl=_args.skip_vhdl,
        skip_synth=_args.skip_synth
    )
