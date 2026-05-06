#!/usr/bin/env python3

"""
This script runs everything for the ablation study.
"""

import subprocess
from argparse import ArgumentParser, Namespace
from pathlib import Path

import ablation_extract_compile_time
import ablation_extract_fmax
import ablation_extract_resource_usage
import ablation_generate
import ablation_measure_latency
import ablation_synth
import lib.constants as c
from lib.optimization_level import OptimizationLevel


def open_plot(f: Path) -> None:
    """
    Open the given plot for the user to view.
    """
    with subprocess.Popen(["open", f.resolve().as_posix()]):
        pass


def main(
    programs: list[str],
    levels: list[OptimizationLevel],
    *,
    skip_latency: bool,
    recompile_sirop: bool
) -> None:
    """
    Script entry point.
    """
    ablation_generate.main(programs, levels, recompile_sirop=recompile_sirop)
    ablation_synth.main(programs, levels)
    ablation_extract_compile_time.main(programs, levels)
    ablation_extract_resource_usage.main(programs, levels)
    ablation_extract_fmax.main(programs, levels)
    if not skip_latency:
        ablation_measure_latency.main(programs, levels)


def parse_args() -> Namespace:
    """
    Parse the command line arguments
    """
    parser = ArgumentParser(
        description="This script runs the full ablation study."
    )
    parser.add_argument(
        "--skip-latency",
        action="store_true",
        help="skip the latency measurement step"
    )
    parser.add_argument(
        "--recompile-sirop",
        action="store_true",
        help="run 'sbt assembly' before invoking the Sirop compiler",
    )
    parser.add_argument(
        "programs",
        nargs="*",
        help=(
            "the names of the programs to process"
            f" (the ones in the paper are: {' '.join(c.ACTIVE_BENCHES)})"
        )
    )
    parser.add_argument(
        "--lvl",
        nargs="*",
        type=OptimizationLevel,
        help="the optimization levels to test",
    )
    args = parser.parse_args()
    if not args.programs:
        args.programs = c.ACTIVE_BENCHES
    if not args.lvl:
        args.lvl = c.ACTIVE_OPT_LEVELS
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(
        programs=_args.programs,
        levels=_args.lvl,
        skip_latency=_args.skip_latency,
        recompile_sirop=_args.recompile_sirop,
    )
