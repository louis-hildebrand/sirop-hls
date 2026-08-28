#!/usr/bin/env python3

"""
Functions for testing the Sirop compiler's ability to run Sirop test suites.
"""

from pathlib import Path
import subprocess

from helpers import assert_equals, TestFailed
import constants as c


def uses_file(p: Path) -> bool:
    """
    Check whether the given file is used for testing the Sirop test runner.
    """
    if (
        p.name.endswith(".test.txt")
        and p.with_suffix("").with_suffix("").with_suffix(".sirop").is_file()
    ):
        return True
    if (
        p.name.endswith(".test.expected.txt")
        and p.with_suffix("").with_suffix("").with_suffix(".sirop").is_file()
    ):
        return True
    if (
        p.name.endswith(".test.actual.txt")
        and p.with_suffix("").with_suffix("").with_suffix(".sirop").is_file()
    ):
        return True
    return False


def can_run(src: Path) -> bool:
    """
    Check whether there are any files describing expected outputs for the Sirop test runner.
    """
    return src.with_suffix(".test.txt").is_file()


def run(src: Path, cli_args: list[str], save: bool) -> bool:
    """
    Test that the Sirop test runner produces the expected outputs.
    """
    name = src.with_suffix("").relative_to(c.RESOURCES).as_posix()
    print(f"{name} (test) ... ", end="", flush=True)
    # Invoke compiler
    args = [
        "java", "-jar", c.JAR.as_posix(),
        "-i", src.as_posix(),
        "--out:test",
        "--overwrite",
    ]
    args += cli_args
    actual_compiler_output_file = c.ACTUAL_OUTPUTS / f"{name}.test.txt"
    actual_expected_output_file = c.ACTUAL_OUTPUTS / f"{name}.test.expected.txt"
    actual_actual_output_file = c.ACTUAL_OUTPUTS / f"{name}.test.actual.txt"
    if not actual_compiler_output_file.parent.exists():
        actual_compiler_output_file.parent.mkdir(exist_ok=True, parents=True)
    if src.with_suffix(".test.expected.txt").is_file():
        args += ["--out:test:expected", actual_expected_output_file]
    if src.with_suffix(".test.actual.txt").is_file():
        args += ["--out:test:actual", actual_actual_output_file]
    result = subprocess.run(
        args,
        encoding="utf-8",
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=False,
    )
    actual_compiler_output_file.write_text(result.stdout, encoding="utf-8")
    try:
        # Check status code
        expected_code = 1 if src.parent.name.endswith("Error") else 0
        if result.returncode != expected_code:
            raise TestFailed(f"WRONG STATUS (expected {expected_code} but got {result.returncode})")
        # Check compiler messages
        expected_compiler_output_file = src.with_suffix(".test.txt")
        assert_equals(
            "output",
            actual_compiler_output_file,
            expected_compiler_output_file,
            save=save,
        )
        # Check dump of expected test outputs
        expected_expected_output_file = src.with_suffix(".test.expected.txt")
        if expected_expected_output_file.is_file():
            assert_equals(
                "expected dump",
                actual_expected_output_file,
                expected_expected_output_file,
                save=save,
            )
        # Check dump of actual test outputs
        expected_actual_output_file = src.with_suffix(".test.actual.txt")
        if expected_actual_output_file.is_file():
            assert_equals(
                "actual dump",
                actual_actual_output_file,
                expected_actual_output_file,
                save=save,
            )
        print("OK")
        return True
    except TestFailed as e:
        print(str(e))
        return False
