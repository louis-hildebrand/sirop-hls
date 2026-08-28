#!/usr/bin/env python3

"""
Functions for testing the Sirop compiler's ability to generate and run VHDL testbenches.
"""

from pathlib import Path
import subprocess

from helpers import assert_equals, TestFailed
import constants as c


def uses_file(p: Path) -> bool:
    """
    Check whether the given file is used for testing Sirop testbench generation and running.
    """
    return (
        p.name.endswith(".vsim.txt")
        and p.with_suffix("").with_suffix("").with_suffix(".sirop").is_file()
    )


def can_run(src: Path) -> bool:
    """
    Check whether there are any files describing expected outputs from VHDL testbench generation
    and running.
    """
    return src.with_suffix(".vsim.txt").is_file()


def run(src: Path, cli_args: list[str], save: bool) -> bool:
    """
    Test that generating and running a VHDL testbench produces the expected outputs.
    """
    name = src.with_suffix("").relative_to(c.RESOURCES).as_posix()
    print(f"{name} (VHDL sim) ... ", end="", flush=True)
    vhdl_dir = c.ACTUAL_OUTPUTS / name
    result = subprocess.run(
        [
            "java", "-jar", c.JAR.as_posix(),
            "-i", src.as_posix(),
            "--out:vhdl", vhdl_dir.as_posix(),
            "--overwrite",
            "--out:vhdl:run-sim"
        ] + cli_args,
        encoding="utf-8",
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=False,
    )
    actual_out_file = c.ACTUAL_OUTPUTS / f"{name}.vsim.txt"
    if not actual_out_file.parent.exists():
        actual_out_file.parent.mkdir(exist_ok=True, parents=True)
    actual_out_file.write_text(result.stdout, encoding="utf-8")
    try:
        # Check status code
        expected_code = 1 if src.parent.name.endswith("Error") else 0
        if result.returncode != expected_code:
            raise TestFailed(f"WRONG STATUS (expected {expected_code} but got {result.returncode})")
        # Check output
        expected_out_file = src.with_suffix(".vsim.txt")
        assert_equals("output", actual_out_file, expected_out_file, save=save)
        print("OK")
        return True
    except TestFailed as e:
        print(str(e))
        return False
