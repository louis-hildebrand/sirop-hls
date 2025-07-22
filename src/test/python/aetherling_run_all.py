#!/bin/python3

"""
This script runs all the Aetherling-related experiments for the given benchmarks.
"""

import subprocess
from argparse import ArgumentParser, Namespace
from pathlib import Path
from typing import Literal

import aetherling_extract_fmax_estimate
import aetherling_extract_resource_usage
import aetherling_generate
import aetherling_measure_fmax
import aetherling_plot_fmax_estimates
import aetherling_plot_fmax_measurements
import aetherling_plot_resource_usage
import lib.constants as c
import lib.list_benchmarks as lb


def open_plot(f: Path) -> None:
    """
    Open the given plot for the user to view.
    """
    subprocess.run(["open", f.as_posix()], check=False)


def main(
    bench_names: list[str],
    *,
    fmax: Literal["estimate", "measure"],
    view_plots: bool,
    clean: bool,
    skip_verilog: bool,
    skip_vhdl: bool,
) -> None:
    """
    Script entry point.
    """
    aetherling_generate.main(
        bench_names,
        skip_verilog=skip_verilog,
        skip_vhdl=skip_vhdl,
        skip_synth=False
    )

    if clean:
        c.RESOURCE_USAGE_CSV.unlink()
        c.FMAX_ESTIMATE_CSV.unlink()
        c.FMAX_MEASUREMENT_CSV.unlink()

    aetherling_extract_resource_usage.main(
        bench_names,
        skip_verilog=skip_verilog,
        skip_vhdl=skip_vhdl,
    )
    aetherling_plot_resource_usage.main()
    if view_plots:
        open_plot(c.RESOURCE_USAGE_PDF)

    if fmax == "estimate":
        aetherling_extract_fmax_estimate.main(
            bench_names,
            skip_verilog=skip_verilog,
            skip_vhdl=skip_vhdl,
            save_to_csv=True,
        )
        aetherling_plot_fmax_estimates.main()
        if view_plots:
            open_plot(c.FMAX_ESTIMATE_PDF)
    else:
        aetherling_measure_fmax.main(
            bench_names,
            skip_verilog=skip_verilog,
            skip_vhdl=skip_vhdl,
            save_to_csv=True,
            max_steps=10,
        )
        aetherling_plot_fmax_measurements.main()
        if view_plots:
            open_plot(c.FMAX_MEASUREMENT_PDF)


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
        help="the benchmarks to process",
    )
    parser.add_argument(
        "--fmax",
        choices=["measure", "estimate"],
        default="estimate",
        help=(
            "whether to carefully measure the maximum clock frequency by repeated synthesis,"
            " or simply take Quartus' first estimate of fmax"
        ),
    )
    parser.add_argument(
        "--view-plots",
        action="store_true",
        help="open the generated plots once they are generated",
    )
    parser.add_argument(
        "--clean",
        action="store_true",
        help="delete the previous results before running the experiments",
    )
    parser.add_argument(
        "--skip-verilog",
        action="store_true",
        help="skip the Verilog implementation of each benchmark",
    )
    parser.add_argument(
        "--skip-vhdl",
        action="store_true",
        help="skip the VHDL implementation of each benchmark",
    )
    args = parser.parse_args()
    args.benchmarks = lb.names_from_args(args.benchmarks)
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(
        bench_names=_args.benchmarks,
        fmax=_args.fmax,
        view_plots=_args.view_plots,
        clean=_args.clean,
        skip_verilog=_args.skip_verilog,
        skip_vhdl=_args.skip_vhdl,
    )
