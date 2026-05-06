#!/usr/bin/env python3

"""
This script runs all the measurements for evaluating the Sirop compiler.
"""

from argparse import ArgumentParser, Namespace

import lib.constants as c
import sirop_extract_fmax
import sirop_extract_resource_usage
import sirop_generate
import sirop_measure_latency
import sirop_synth


def main(programs: list[str], skip_latency: bool, recompile_sirop: bool) -> None:
    """
    Script entry point.
    """
    sirop_generate.main(programs, recompile_sirop=recompile_sirop)
    sirop_synth.main(programs)

    sirop_extract_resource_usage.main(programs)
    sirop_extract_fmax.main(programs)
    if not skip_latency:
        sirop_measure_latency.main(programs)

def parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(
        description="extracts the resource usages for the given programs"
    )
    parser.add_argument(
        "--skip-latency",
        action="store_true",
        help="skip the latency measurement step"
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
        "--recompile-sirop",
        action="store_true",
        help="run 'sbt assembly' before invoking the Sirop compiler",
    )
    args = parser.parse_args()
    if not args.programs:
        args.programs = c.ACTIVE_BENCHES
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(
        _args.programs,
        skip_latency=_args.skip_latency,
        recompile_sirop=_args.recompile_sirop,
    )
