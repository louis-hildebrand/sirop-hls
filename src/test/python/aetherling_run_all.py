#!/usr/bin/env python3

"""
This script runs all the Aetherling-related experiments for the given benchmarks.
"""

import subprocess
from argparse import ArgumentParser, Namespace
from pathlib import Path

import aetherling_extract_compile_time
import aetherling_extract_fmax
import aetherling_extract_resource_usage
import aetherling_generate
import aetherling_measure_latency
import aetherling_synth
import lib.list_benchmarks as lb


def open_plot(f: Path) -> None:
    """
    Open the given plot for the user to view.
    """
    with subprocess.Popen(["open", f.resolve().as_posix()]):
        pass


def main(
    bench_names: list[str],
    *,
    skip_chisel: bool,
    skip_sirop: bool,
    skip_gen: bool,
    skip_latency: bool,
    recompile_sirop: bool,
) -> None:
    """
    Script entry point.
    """
    if not skip_gen:
        aetherling_generate.main(
            bench_names,
            skip_chisel=skip_chisel,
            skip_sirop=skip_sirop,
            recompile_sirop=recompile_sirop,
        )
        aetherling_synth.main(
            bench_names,
            skip_chisel=skip_chisel,
            skip_sirop=skip_sirop,
        )

    aetherling_extract_compile_time.main(bench_names)

    aetherling_extract_resource_usage.main(
        bench_names,
        skip_chisel=skip_chisel,
        skip_sirop=skip_sirop,
    )

    if not skip_latency:
        aetherling_measure_latency.main(
            bench_names,
            skip_chisel=skip_chisel,
            skip_sirop=skip_sirop,
        )

    aetherling_extract_fmax.main(
        bench_names,
        skip_chisel=skip_chisel,
        skip_sirop=skip_sirop,
        save_to_csv=True,
    )


def parse_args() -> Namespace:
    """
    Parse the command line arguments
    """
    parser = ArgumentParser(
        description=(
            "This script runs all the Aetherling-related experiments for the given benchmarks."
        )
    )
    parser.add_argument(
        "benchmarks",
        nargs="*",
        help=(
            "the benchmarks to process"
            f" (the ones in the paper can be selected by {lb.ACTIVE_BENCH_GLOB})"
        ),
    )
    parser.add_argument(
        "--skip-gen",
        action="store_true",
        help="don't re-generate and re-synthesize, just use what's already there"
    )
    parser.add_argument(
        "--skip-latency",
        action="store_true",
        help="skip the latency measurement step"
    )
    parser.add_argument(
        "--skip-chisel",
        action="store_true",
        help="skip the Chisel-generated Verilog projects",
    )
    parser.add_argument(
        "--skip-sirop",
        action="store_true",
        help="skip the Sirop-generated VHDL projects",
    )
    parser.add_argument(
        "--recompile-sirop",
        action="store_true",
        help="run 'sbt assembly' before invoking the Sirop compiler",
    )
    args = parser.parse_args()
    args.benchmarks = lb.names_from_args(args.benchmarks)
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(
        bench_names=_args.benchmarks,
        skip_chisel=_args.skip_chisel,
        skip_sirop=_args.skip_sirop,
        skip_gen=_args.skip_gen,
        skip_latency=_args.skip_latency,
        recompile_sirop=_args.recompile_sirop,
    )
