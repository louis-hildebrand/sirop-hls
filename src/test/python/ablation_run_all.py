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
import ablation_plot_compile_time
import ablation_plot_fmax
import ablation_plot_latency
import ablation_plot_resource_usage
import ablation_synth
import lib.constants as c

# The benchmarks that are shown in the paper
ACTIVE_BENCHES = [
    "map", "dot", "matvec_1", "matmat",
    "conv1d", "conv2d", "convb2b",
    "sharpen", "sobel", "camera"
]


def open_plot(f: Path) -> None:
    """
    Open the given plot for the user to view.
    """
    with subprocess.Popen(["open", f.resolve().as_posix()]):
        pass


def main(
    programs: list[str],
    *,
    clean: bool,
    skip_latency: bool,
    skip_plots: bool,
    view_plots: bool,
) -> None:
    """
    Script entry point.
    """
    ablation_generate.main(programs)
    ablation_synth.main(programs)

    if clean:
        c.ABLATION_RESOURCE_USAGE_CSV.unlink(missing_ok=True)
        if not skip_latency:
            c.ABLATION_LATENCY_CSV.unlink(missing_ok=True)
        c.ABLATION_FMAX_CSV.unlink(missing_ok=True)

    ablation_extract_compile_time.main(programs)
    if not skip_plots:
        ablation_plot_compile_time.main()
        if view_plots:
            open_plot(c.ABLATION_COMPILE_TIME_PDF)

    ablation_extract_resource_usage.main(programs)
    if not skip_plots:
        ablation_plot_resource_usage.main()
        if view_plots:
            open_plot(c.ABLATION_RESOURCE_USAGE_PDF)

    if not skip_latency:
        ablation_measure_latency.main(programs)
        if not skip_plots:
            ablation_plot_latency.main()
            if view_plots:
                open_plot(c.ABLATION_LATENCY_PDF)

    ablation_extract_fmax.main(programs)
    if not skip_plots:
        ablation_plot_fmax.main()
        if view_plots:
            open_plot(c.ABLATION_FMAX_PDF)


def parse_args() -> Namespace:
    """
    Parse the command line arguments
    """
    parser = ArgumentParser(
        description="This script runs the full ablation study."
    )
    parser.add_argument(
        "programs",
        nargs="*",
        help=(
            "the names of the programs to process"
            f"(the ones in the paper are: {' '.join(ACTIVE_BENCHES)})"
        )
    )
    parser.add_argument(
        "--clean",
        action="store_true",
        help="delete the previous results before running the experiments",
    )
    parser.add_argument(
        "--skip-latency",
        action="store_true",
        help="skip the latency measurement step"
    )
    parser.add_argument(
        "--skip-plots",
        action="store_true",
        help="skip generating the plots",
    )
    parser.add_argument(
        "--view-plots",
        action="store_true",
        help="open the generated plots once they are generated",
    )
    args = parser.parse_args()

    if not args.programs:
        args.programs = ACTIVE_BENCHES

    return args


if __name__ == "__main__":
    _args = parse_args()
    main(
        programs=_args.programs,
        clean=_args.clean,
        skip_latency=_args.skip_latency,
        skip_plots=_args.skip_plots,
        view_plots=_args.view_plots,
    )
