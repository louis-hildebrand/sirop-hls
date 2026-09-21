#!/usr/bin/env python3

"""
Script for running end-to-end tests.
"""

from argparse import ArgumentParser, Namespace
from pathlib import Path
import filecmp
import os
import shutil
import subprocess
import sys
import time

from colorama import Fore, Style
import colorama

from helpers import assert_equals, TestFailed
import constants as c
import test_test as stest
import test_vhdl as vhdl
import test_vsim as vsim


def look_for_unused_files() -> None:
    """
    Scan the resources directory and exit with an error if any files are unused.
    """
    error_count = 0
    for (root, _, files) in os.walk(c.RESOURCES):
        root = Path(root)
        should_ignore_root = any(
            root.is_relative_to(ignored_dir)
            for ignored_dir in c.IGNORE_DIRECTORIES
        )
        if should_ignore_root:
            continue
        files = [root.joinpath(f) for f in files]
        for f in files:
            if f in c.IGNORE_FILES:
                continue
            if f.name.endswith(".sirop"):
                continue
            if f.name.endswith(".swp"):
                # Vim swap file (in case the file is being edited)
                continue
            if f.name.endswith(".cliargs.txt"):
                continue
            if (
                f.name.endswith(".stderr.txt")
                and c.is_valid_source(f.with_suffix("").with_suffix(".sirop"))
            ):
                continue
            if (
                f.name.endswith(".eval.txt")
                and c.is_valid_source(f.with_suffix("").with_suffix(".sirop"))
            ):
                continue
            if (
                f.name.endswith(".repl.txt")
                and c.is_valid_source(f.with_suffix("").with_suffix(".sirop"))
            ):
                continue
            if vhdl.uses_file(f):
                continue
            if stest.uses_file(f):
                continue
            if vsim.uses_file(f):
                continue
            print(f"File {f.relative_to(c.ROOT)} is not used for testing")
            error_count += 1
    if error_count > 0:
        file_or_files = "file" if error_count == 1 else "files"
        is_or_are = "is" if error_count == 1 else "are"
        it_or_them = "it" if error_count == 1 else "them"
        print()
        print(
            Fore.RED
            + f"{error_count} {file_or_files} within {c.RESOURCES.relative_to(c.ROOT)}"
            + f" {is_or_are} not used for testing."
            + f" Consider deleting or moving {it_or_them}."
            + Style.RESET_ALL
        )
        sys.exit(1)


def copy_files_that_shouldnt_be_overwritten() -> None:
    """
    Make copies of all the files in the DO_NOT_OVERWRITE_FILES list so we can
    check that they haven't been overwritten afterwards.
    """
    for src in c.DO_NOT_OVERWRITE_FILES:
        if not src.exists():
            print(
                Fore.RED
                + f"{src} does not exist. Check DO_NOT_OVERWRITE_FILES in constants.py."
                + Style.RESET_ALL
            )
            sys.exit(1)
        dest = c.ACTUAL_OUTPUTS / src.relative_to(c.RESOURCES)
        if dest.is_file():
            dest.unlink()
        if dest.is_dir():
            shutil.rmtree(dest)
        dest.parent.mkdir(parents=True, exist_ok=True)
        if src.is_dir():
            shutil.copytree(src=src, dst=dest)
        else:
            shutil.copy(src=src, dst=dest)


def check_files_that_shouldnt_be_overwritten() -> int:
    """
    Compare the files in the DO_NOT_OVERWRITE_FILES list with the copies made
    earlier, and exit with an error if their contents have changed.
    """

    def same_file(src: Path, dest: Path) -> bool:
        return filecmp.cmp(src, dest, shallow=False)

    def same_dir(src: Path, dest: Path) -> bool:
        dircmp = filecmp.dircmp(src, dest)
        return (
            not dircmp.left_only
            and not dircmp.right_only
            and not dircmp.diff_files
            and not dircmp.funny_files
            and not dircmp.common_funny
            and all(same_file(src / f, dest / f) for f in dircmp.common_files)
            and all(same_dir(src / f, dest / f) for f in dircmp.common_dirs)
        )

    print()
    print("Checking that files have not been overwritten...")
    print()
    error_count = 0
    for src in c.DO_NOT_OVERWRITE_FILES:
        print(f"{src.relative_to(c.RESOURCES)} ... ", end="")
        dest = c.ACTUAL_OUTPUTS / src.relative_to(c.RESOURCES)
        if src.is_dir():
            ok = same_dir(src, dest)
        elif src.is_file():
            ok = same_file(src, dest)
        else:
            print(
                f"{src} is neither a file nor a directory."
                " Check DO_NOT_OVERWRITE_FILES in constants.py."
            )
            sys.exit(1)
        if ok:
            print(Fore.GREEN + "OK" + Style.RESET_ALL)
        else:
            print(Fore.RED + "CHANGED" + Style.RESET_ALL)
            error_count += 1
    return error_count


def test_plain(expected_stderr_file: Path, cli_args: list[str], save: bool) -> bool:
    """
    Test that running the compiler with the given CLI arguments produces the expected output on
    stderr.
    """
    name = expected_stderr_file.with_suffix("").with_suffix("").relative_to(c.RESOURCES).as_posix()
    print(f"{name} (stderr) ... ", end="", flush=True)
    if not cli_args:
        print("MISSING CLI ARGS")
        return False
    source_path = expected_stderr_file.with_suffix("").with_suffix(".sirop")
    if source_path not in c.MISSING_FILES:
        cli_args = cli_args + ["-i", source_path.as_posix()]
    cli_args = ["java", "-jar", c.JAR.as_posix()] + cli_args
    result = subprocess.run(
        cli_args,
        encoding="utf-8",
        capture_output=True,
        check=False,
    )
    # Save actual stdout
    actual_stdout_file = c.ACTUAL_OUTPUTS / f"{name}.stdout.txt"
    actual_stdout_file.parent.mkdir(exist_ok=True, parents=True)
    actual_stdout_file.write_text(result.stdout, encoding="utf-8")
    # Save actual stderr
    actual_stderr_file = c.ACTUAL_OUTPUTS / f"{name}.stderr.txt"
    actual_stderr_file.parent.mkdir(exist_ok=True, parents=True)
    actual_stderr_file.write_text(result.stderr, encoding="utf-8")
    try:
        # Check status code
        expected_code = 1 if expected_stderr_file.parent.name.endswith("Error") else 0
        if result.returncode != expected_code:
            raise TestFailed(f"WRONG STATUS (expected {expected_code} but got {result.returncode})")
        # Check stderr
        assert_equals("stderr", actual_stderr_file, expected_stderr_file, save=save)
        print(Fore.GREEN + "OK" + Style.RESET_ALL)
        return True
    except TestFailed as e:
        print(Fore.RED + str(e) + Style.RESET_ALL)
        return False


def test_eval(eval_output: Path, cli_args: list[str], save: bool) -> bool:
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
        ] + cli_args,
        encoding="utf-8",
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=False,
    )
    actual_out_file = c.ACTUAL_OUTPUTS / f"{name}.eval.txt"
    if not actual_out_file.parent.exists():
        actual_out_file.parent.mkdir(exist_ok=True, parents=True)
    actual_out_file.write_text(result.stdout, encoding="utf-8")
    try:
        # Check status code
        expected_code = 1 if eval_output.parent.name.endswith("Error") else 0
        if result.returncode != expected_code:
            raise TestFailed(f"WRONG STATUS (expected {expected_code} but got {result.returncode})")
        # Check output
        assert_equals("output", actual_out_file, eval_output, save=save)
        print(Fore.GREEN + "OK" + Style.RESET_ALL)
        return True
    except TestFailed as e:
        print(Fore.RED + str(e) + Style.RESET_ALL)
        return False


def test_repl(repl_output: Path, compiler_version: str, cli_args: list[str]) -> bool:
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
            ] + cli_args,
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
    try:
        # Check status code
        expected_code = 0
        if result.returncode != expected_code:
            raise TestFailed(f"WRONG STATUS (expected {expected_code} but got {result.returncode})")
        # Check output
        expected = (
            repl_output.read_text(encoding="utf-8")
                .replace("${COMPILER_VERSION}", compiler_version)
        )
        if result.stdout.rstrip() != expected.rstrip():
            raise TestFailed(
                f"WRONG OUTPUT (compare {repl_output.relative_to(c.ROOT)}"
                f" with {actual_out_file.relative_to(c.ROOT)})"
            )
        actual_out_file.unlink(missing_ok=True)
        print(Fore.GREEN + "OK" + Style.RESET_ALL)
        return True
    except TestFailed as e:
        print(Fore.RED + str(e) + Style.RESET_ALL)
        return False


def main(test_sources: list[Path], skip_vsim: bool, save: bool) -> None:
    """
    Script entry point.
    """
    start_timestamp = time.monotonic()
    look_for_unused_files()
    copy_files_that_shouldnt_be_overwritten()
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
    print()
    print(f"Testing v{compiler_version}")
    print(f"Found {len(test_sources)} .sirop files to test")
    print()
    error_count = 0
    for test in test_sources:
        ran = False
        os.chdir(test.parent)
        cli_args = []
        if (cli_args_file := test.parent.joinpath(".cliargs.txt")).is_file():
            # Common CLI args
            cli_args += cli_args_file.read_text(encoding="utf-8").splitlines()
        if (cli_args_file := test.with_suffix(".cliargs.txt")).is_file():
            # Test-specific CLI args
            cli_args = cli_args_file.read_text(encoding="utf-8").splitlines()
        if (eval_output := test.with_suffix(".eval.txt")).is_file():
            ran = True
            ok = test_eval(eval_output, cli_args=cli_args, save=save)
            if not ok:
                error_count += 1
        if (repl_output := test.with_suffix(".repl.txt")).is_file():
            ran = True
            ok = test_repl(repl_output, compiler_version, cli_args=cli_args)
            if not ok:
                error_count += 1
        if (stderr_file := test.with_suffix(".stderr.txt")).is_file():
            ran = True
            ok = test_plain(stderr_file, cli_args=cli_args, save=save)
            if not ok:
                error_count += 1
        if vhdl.can_run(test):
            ran = True
            ok = vhdl.run(test, cli_args=cli_args, save=save)
            if not ok:
                error_count += 1
        if stest.can_run(test):
            ran = True
            ok = stest.run(test, cli_args=cli_args, save=save)
            if not ok:
                error_count += 1
        if vsim.can_run(test):
            ran = True
            ok = skip_vsim or vsim.run(test, cli_args=cli_args, save=save)
            if not ok:
                error_count += 1
        if not ran and test not in c.IGNORE_FILES:
            print(
                Fore.RED
                + f"ERROR: Nothing to do for file {test.relative_to(c.ROOT)}"
                + Style.RESET_ALL
            )
            error_count += 1
    error_count += check_files_that_shouldnt_be_overwritten()
    end_timestamp = time.monotonic()
    elapsed_time = end_timestamp - start_timestamp
    elapsed_time = f" (in {elapsed_time:.1f} seconds)"
    if error_count > 0:
        test_or_tests = "test" if error_count == 1 else "tests"
        print()
        print(Fore.RED + f"{error_count} {test_or_tests} failed" + Style.RESET_ALL + elapsed_time)
        sys.exit(1)
    print()
    print(Fore.GREEN + "All tests passed!" + Style.RESET_ALL + elapsed_time)


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
        type=Path,
        help="path of the .sirop files to test",
    )
    parser.add_argument(
        "--skip-vsim",
        action="store_true",
        help="don't run the tests related to VHDL simulation",
    )
    parser.add_argument(
        "--save",
        action="store_true",
        help="overwrite the expected results with whatever the compiler currently outputs",
    )
    parser.add_argument(
        "--no-color",
        action="store_true",
        help="suppress colored output",
    )
    args = parser.parse_args()
    if not args.test_sources:
        args.test_sources = sorted(list(c.RESOURCES.glob("**/*.sirop")) + c.MISSING_FILES)
    if not args.test_sources:
        parser.error("no test cases found")
    args.test_sources = [p.resolve() for p in args.test_sources]
    for p in args.test_sources:
        if not p.is_file() and not p in c.MISSING_FILES:
            parser.error(f"file {p} does not exist or is not a file")
        if not p.name.endswith(".sirop"):
            parser.error(f"invalid path {p}: all paths should end in .sirop")
    return args


if __name__ == "__main__":
    _args = _parse_args()
    colorama.init(strip=True if _args.no_color else None)
    try:
        main(_args.test_sources, skip_vsim=_args.skip_vsim, save=_args.save)
    except KeyboardInterrupt:
        print()
        print("Cancelled")
