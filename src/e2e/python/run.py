#!/usr/bin/env python3

"""
Script for running end-to-end tests.
"""

from argparse import ArgumentParser, Namespace
from pathlib import Path
import os
import subprocess
import sys

from test_vhdl import is_used_for_vhdl_test, test_vhdl, vhdl_test_exists
import constants as c


def look_for_unused_files() -> None:
    """
    Scan the resources directory and exit with an error if any files are unused.
    """
    error_count = 0
    for (root, _, files) in os.walk(c.RESOURCES):
        root = Path(root)
        if root.is_relative_to(c.ACTUAL_OUTPUTS):
            continue
        files = [root.joinpath(f) for f in files]
        for f in files:
            if f.name.endswith(".sirop"):
                continue
            if f.name.endswith(".eval.txt") and f.with_suffix("").with_suffix(".sirop").is_file():
                continue
            if f.name.endswith(".repl.txt") and f.with_suffix("").with_suffix(".sirop").is_file():
                continue
            if is_used_for_vhdl_test(f):
                continue
            print(f"File {f.relative_to(c.ROOT)} is not used for testing")
            error_count += 1
    if error_count > 0:
        file_or_files = "file" if error_count == 1 else "files"
        is_or_are = "is" if error_count == 1 else "are"
        it_or_them = "it" if error_count == 1 else "them"
        print()
        print(
            f"{error_count} {file_or_files} within {c.RESOURCES.relative_to(c.ROOT)}"
            f" {is_or_are} not used for testing."
            f" Consider deleting or moving {it_or_them}."
        )
        sys.exit(1)


def test_eval(eval_output: Path) -> bool:
    """
    Test that evaluating the program produces the expected output from the given file.
    """
    name = eval_output.with_suffix("").with_suffix("").relative_to(c.RESOURCES).as_posix()
    print(f"{name} (eval) ... ", end="", flush=True)
    source_path = eval_output.with_suffix("").with_suffix(".sirop")
    result = subprocess.run(
        [
            "java", "-jar", c.JAR.as_posix(),
            "-i", source_path.as_posix(),
            "--out:eval",
            "--quiet",
        ],
        encoding="utf-8",
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=False,
    )
    actual_out_file = c.ACTUAL_OUTPUTS / f"{name}.eval.txt"
    if not actual_out_file.parent.exists():
        actual_out_file.parent.mkdir(exist_ok=True, parents=True)
    actual_out_file.write_text(result.stdout, encoding="utf-8")
    expected_code = 1 if eval_output.parent.name.endswith("Error") else 0
    if result.returncode != expected_code:
        print(f"WRONG STATUS (expected {expected_code} but got {result.returncode})")
        return False
    expected = eval_output.read_text(encoding="utf-8")
    if result.stdout != expected:
        print(
            f"WRONG OUTPUT (compare {eval_output.relative_to(c.ROOT)}"
            f" with {actual_out_file.relative_to(c.ROOT)})"
        )
        return False
    actual_out_file.unlink(missing_ok=True)
    print("OK")
    return True


def test_repl(repl_output: Path, compiler_version: str) -> bool:
    """
    Test that an interactive session produces the expected output from the given file.
    """
    name = repl_output.with_suffix("").with_suffix("").relative_to(c.RESOURCES).as_posix()
    print(f"{name} (REPL) ... ", end="", flush=True)
    source_path = repl_output.with_suffix("").with_suffix(".sirop")
    with open(source_path, "r", encoding="utf-8") as f:
        result = subprocess.run(
            [
                "java",
                # If this argument is omitted, the JVM emits a warning "An illegal reflective
                # access operation has occurred".
                # Yet when the argument --illegal-access=deny is passed, everything works fine (?!)
                # What's also strange is that I am unable to reproduce this behaviour in bash using
                #     cat file.sirop | java -jar sirop.jar
                # or
                #     java -jar sirop.jar < file.sirop
                # No warning is printed in either case.
                "--illegal-access=deny",
                "-jar", c.JAR.as_posix()
            ],
            encoding="utf-8",
            stdin=f,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            check=False,
        )
    actual_out_file = c.ACTUAL_OUTPUTS / f"{name}.repl.txt"
    if not actual_out_file.parent.exists():
        actual_out_file.parent.mkdir(exist_ok=True, parents=True)
    actual_out_file.write_text(result.stdout, encoding="utf-8")
    expected_code = 0
    if result.returncode != expected_code:
        print(f"WRONG STATUS (expected {expected_code} but got {result.returncode})")
        return False
    expected = (
        repl_output.read_text(encoding="utf-8")
            .replace("${COMPILER_VERSION}", compiler_version)
    )
    if result.stdout.rstrip() != expected.rstrip():
        print(
            f"WRONG OUTPUT (compare {repl_output.relative_to(c.ROOT)}"
            f" with {actual_out_file.relative_to(c.ROOT)})"
        )
        return False
    actual_out_file.unlink(missing_ok=True)
    print("OK")
    return True


def main(test_sources: list[Path]) -> None:
    """
    Script entry point.
    """
    look_for_unused_files()
    os.chdir(c.ROOT)
    print("Building Scala project...")
    subprocess.run(["sbt", "assembly"], check=True, capture_output=True)
    compiler_version = subprocess.run(
        ["java", "-jar", c.JAR.as_posix(), "--version"],
        capture_output=True,
        encoding="utf-8",
        check=True
    )
    compiler_version = compiler_version.stdout.strip()
    print(f"Testing v{compiler_version}")
    print(f"Found {len(test_sources)} .sirop files to test")
    print()
    error_count = 0
    for test in test_sources:
        ran = False
        if (eval_output := test.with_suffix(".eval.txt")).is_file():
            ran = True
            ok = test_eval(eval_output)
            if not ok:
                error_count += 1
        if (repl_output := test.with_suffix(".repl.txt")).is_file():
            ran = True
            ok = test_repl(repl_output, compiler_version)
            if not ok:
                error_count += 1
        if vhdl_test_exists(test):
            ran = True
            ok = test_vhdl(test)
            if not ok:
                error_count += 1
        if not ran:
            print(f"ERROR: Nothing to do for file {test.relative_to(c.ROOT)}")
            error_count += 1
    if error_count > 0:
        test_or_tests = "test" if error_count == 1 else "tests"
        print()
        print(f"{error_count} {test_or_tests} failed")
        sys.exit(1)
    print()
    print("All tests passed!")


def _parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(
        description="run end-to-end tests"
    )
    parser.add_argument(
        "test_sources",
        nargs="*",
        help="path of the .sirop files to test",
        type=Path,
    )
    args = parser.parse_args()
    if not args.test_sources:
        args.test_sources = sorted(c.RESOURCES.glob("**/*.sirop"))
    if not args.test_sources:
        parser.error("no test cases found")
    args.test_sources = [p.resolve() for p in args.test_sources]
    for p in args.test_sources:
        if not p.is_file():
            parser.error(f"file {p} does not exist")
        if not p.name.endswith(".sirop"):
            parser.error(f"invalid path {p}: all paths should end in .sirop")
    return args


if __name__ == "__main__":
    _args = _parse_args()
    main(_args.test_sources)
