#!/bin/python3

"""
This script runs all the measurements for the comparison with SHIR.
"""

from argparse import ArgumentParser, Namespace

import shir_extract_fmax
import shir_extract_resource_usage
import shir_generate
import shir_synth


def main(programs: list[str], skip_shir: bool, skip_sirop: bool) -> None:
    """
    Script entry point.
    """
    shir_generate.main(programs, skip_shir=skip_shir, skip_sirop=skip_sirop)
    shir_synth.main(programs, skip_shir=skip_shir, skip_sirop=skip_sirop)

    shir_extract_resource_usage.main(programs, skip_shir=skip_shir, skip_sirop=skip_sirop)
    shir_extract_fmax.main(programs, skip_shir=skip_shir, skip_sirop=skip_sirop)

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
        help="the names of the programs to process"
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
    return parser.parse_args()


if __name__ == "__main__":
    _args = parse_args()
    main(
        _args.programs,
        skip_shir=_args.skip_shir,
        skip_sirop=_args.skip_sirop,
    )
