"""
Shared constants.
"""

from pathlib import Path


ROOT = Path(__file__).resolve().parent.parent.parent.parent
JAR = ROOT / "target" / "scala-2.12" / "sirop.jar"
RESOURCES = ROOT / "src" / "e2e" / "resources"
ACTUAL_OUTPUTS = RESOURCES / "actual"

# The test runner will error out if there are any unrecognized files.
# The following directories and files will be ignored here; they will never
# trigger the error.
# The test runner also will not complain if there is nothing to do for the
# .sirop files in this list.
IGNORE_DIRECTORIES = {
    ACTUAL_OUTPUTS,
}
IGNORE_FILES = {
    RESOURCES / "FileError" / "do_not_overwrite.txt",
    RESOURCES / "FileError" / "do_not_overwrite" / ".gitkeep",
    RESOURCES / "FileError" / "source_is_dir" / ".gitkeep",
    RESOURCES / "BadArgsError" / "multiple_source_files_1.sirop",
    RESOURCES / "BadArgsError" / "multiple_source_files_2.sirop",
}

# The test runner will check that the following files and directories have not
# been overwritten.
DO_NOT_OVERWRITE_FILES = [
    RESOURCES / "FileError" / "do_not_overwrite.txt",
    RESOURCES / "FileError" / "do_not_overwrite",
]

# The test runner will not complain that these files are missing.
# This is useful for testing the compiler output in case the source file
# does not exist.
MISSING_FILES = [
    RESOURCES / "FileError" / "missing_source.sirop",
    RESOURCES / "FileError" / "source_is_dir.sirop",
    RESOURCES / "BadArgsError" / "multiple_source_files.sirop",
]

def is_valid_source(p: Path) -> bool:
    """
    Check whether this path is a valid source file: either it should exist, or
    it should be in MISSING_FILES.
    """
    return p.is_file() or p in MISSING_FILES
