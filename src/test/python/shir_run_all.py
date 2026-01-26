#!/usr/bin/env python3

"""
This script runs all the measurements for the comparison with SHIR.
"""

from argparse import ArgumentParser, Namespace

import shir_extract_fmax
import shir_extract_resource_usage
import shir_generate
import shir_measure_latency
import shir_synth

ACTIVE_BENCHES = [
    "map", "dot", "matvec", "matmat",
    "conv1d", "conv2d", "convb2b",
    "sharpen", "sobel"
]


def main(programs: list[str], skip_shir: bool, skip_sirop: bool, skip_latency: bool) -> None:
    """
    Script entry point.
    """
    shir_generate.main(programs, skip_shir=skip_shir, skip_sirop=skip_sirop)
    shir_synth.main(programs, skip_shir=skip_shir, skip_sirop=skip_sirop)

    shir_extract_resource_usage.main(programs, skip_shir=skip_shir, skip_sirop=skip_sirop)
    shir_extract_fmax.main(programs, skip_shir=skip_shir, skip_sirop=skip_sirop)
    if not skip_latency:
        shir_measure_latency.main(programs, skip_shir=skip_shir, skip_sirop=skip_sirop)

def parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(
        description="extracts the resource usages for the given programs"
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
        "--skip-shir",
        action="store_true",
        help="skip the SHIR designs",
    )
    parser.add_argument(
        "--skip-sirop",
        action="store_true",
        help="skip the Sirop designs",
    )
    parser.add_argument(
        "--skip-latency",
        action="store_true",
        help="skip the latency measurement step"
    )
    args = parser.parse_args()
    if not args.programs:
        args.programs = ACTIVE_BENCHES
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(
        _args.programs,
        skip_shir=_args.skip_shir,
        skip_sirop=_args.skip_sirop,
        skip_latency=_args.skip_latency,
    )
