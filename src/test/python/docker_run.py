#!/usr/bin/env python3

"""
This script runs a command in a docker container.
It takes care of setting up the required bind-mounts and running chmod afterwards.
"""

from argparse import ArgumentParser, Namespace
import subprocess

import lib.constants as c


DEFAULT_IMAGE = "louishildebrand/sirop-lctes26-artifact"


def main(args: list[str], image: str, skip_chmod: bool) -> None:
    """
    Script entry point.
    """
    container_command = " ".join([f"'{a}'" for a in args])
    if not skip_chmod:
        container_command = (
            f"{{ {container_command}; }}"
            " && chmod -R 777 /sirop/src/test/vhdl"
            " && chmod -R 777 /sirop/src/test/verilog"
        )
    command = [
        "sudo", "docker", "run",
        "--rm",
        "--mount", f"type=bind,src={c.VHDL_DIR.as_posix()},dst=/sirop/src/test/vhdl",
        "--mount", f"type=bind,src={c.VERILOG_DIR.as_posix()},dst=/sirop/src/test/verilog",
        image,
        "/bin/bash", "-c", container_command,
    ]
    subprocess.run(command, check=True)


def parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(
        description="runs the given command(s) in a docker container"
    )
    parser.add_argument(
        "args",
        nargs="+",
        help="the command(s) to run",
    )
    parser.add_argument(
        "--image",
        default=DEFAULT_IMAGE,
        help=f"the docker image to use (default: {DEFAULT_IMAGE})",
    )
    parser.add_argument(
        "--skip-chmod",
        action="store_true",
        help=(
            "by default, this script will run chmod -R 777 after the given command(s) to ensure"
            " the generated VHDL and Verilog code is accessible despite being owned by root."
            " This flag disables that behaviour."
        )
    )
    return parser.parse_args()


if __name__ == "__main__":
    _args = parse_args()
    main(
        args=_args.args,
        image=_args.image,
        skip_chmod=_args.skip_chmod,
    )
