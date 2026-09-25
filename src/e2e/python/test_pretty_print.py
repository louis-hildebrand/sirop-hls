#!/usr/bin/env python3

"""
Functions for testing the Sirop compiler's pretty-print target.
This type of test should be used sparingly, since it'll probably be very
brittle, but it is occasionally useful to test that changes to the optimizer
haven't completely broken an important benchmark.
"""

from pathlib import Path
import subprocess

from colorama import Fore, Style

from helpers import assert_equals, TestFailed
import constants as c


def uses_file(p: Path) -> bool:
    """
    Check whether the given file is used for testing the pretty-printing target.
    """
    return (
        p.name.endswith(".pp.txt")
        and p.with_suffix("").with_suffix("").with_suffix(".sirop").is_file()
    )


def can_run(src: Path) -> bool:
    """
    Check whether there are any files describing expected outputs from pretty-printing.
    """
    return src.with_suffix(".pp.txt").is_file()


def run(src: Path, cli_args: list[str], save: bool) -> bool:
    """
    Test that the pretty-printer produces the expected outputs.
    """
    name = src.with_suffix("").relative_to(c.RESOURCES).as_posix()
    print(f"{name} (pretty-print) ... ", end="", flush=True)
    actual_path = c.ACTUAL_OUTPUTS / f"{name}.pp.txt"
    if not actual_path.parent.exists():
        actual_path.parent.mkdir(exist_ok=True, parents=True)
    result = subprocess.run(
        [
            "java", "-jar", c.JAR.as_posix(),
            src.as_posix(),
            "--out:pp", actual_path.as_posix(),
            "--overwrite",
        ] + cli_args,
        encoding="utf-8",
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=False,
    )
    try:
        # Check status code
        expected_code = 1 if src.parent.name.endswith("Error") else 0
        if result.returncode != expected_code:
            raise TestFailed(f"WRONG STATUS (expected {expected_code} but got {result.returncode})")
        # Check output
        expected_path = src.with_suffix(".pp.txt")
        assert_equals("output", actual_path, expected_path, save=save)
        print(Fore.GREEN + "OK" + Style.RESET_ALL)
        return True
    except TestFailed as e:
        print(Fore.RED + str(e) + Style.RESET_ALL)
        return False
