"""
Functions for extracting or measuring the maximum clock frequency of a design.
"""

import re
from dataclasses import dataclass
from pathlib import Path

from .synth import synthesize_design


@dataclass
class Step:
    """
    One step in an fmax measurement.
    """

    freq: int
    success: bool | None


@dataclass
class Fmax:
    """
    A measurement of the maximum frequency of a design.
    """

    lower: float | None
    """
    Inclusive lower bound for fmax.
    The design can definitely be synthesized with this target frequency.
    """
    upper: float | None
    """
    Upper bound (exclusive) for fmax.
    The synthesizer failed to meet the timing requirements with this target frequency.
    """
    steps: list[Step]
    """
    The steps that were taken for this measurement
    """


def get_sta_path(proj_dir: Path) -> Path | None:
    """
    Find the path to the .sta.rpt file in the given project.
    """
    sta_path = proj_dir.joinpath("output_files", "top.sta.rpt")
    if sta_path.exists():
        return sta_path
    sta_path = proj_dir.joinpath("output_files", "Top.sta.rpt")
    if sta_path.exists():
        return sta_path
    return None


def get_sdc_path(proj_dir: Path) -> Path | None:
    """
    Find the path to the .sdc file in the given project.
    """
    sdc_path = proj_dir.joinpath("top.sdc")
    if sdc_path.exists():
        return sdc_path
    sdc_path = proj_dir.joinpath("Top.sdc")
    if sdc_path.exists():
        return sdc_path
    return None


def extract_all_fmaxes(sta_path: Path) -> list[float]:
    """
    Extract all the fmax values from the given .sta.rpt file.
    """
    results: list[float] = []
    with open(sta_path, "r", encoding="utf-8") as f:
        while True:
            line = next(f, None)
            if line is None:
                break
            # Strip trailing \n
            line = line[:-1]
            if re.match(r'; .* Fmax Summary', line):
                line = next(f)[:-1]
                if not re.fullmatch(r'\+-+\+-+\+-+\+-+\+', line):
                    raise RuntimeError(f"Unrecognized line: {line}")
                line = next(f)[:-1]
                pattern = r';\s+Fmax\s+;\s+Restricted Fmax\s+;\s+Clock Name\s+;\s+Note\s+;'
                if not re.fullmatch(pattern, line):
                    raise RuntimeError(f"Unrecognized line: {line}")
                line = next(f)[:-1]
                if not re.fullmatch(r'\+-+\+-+\+-+\+-+\+', line):
                    raise RuntimeError(f"Unrecognized line: {line}")
                line = next(f)[:-1]
                m = re.match(r'; ([\d.]+) MHz\s+;\s+([\d.]+) MHz', line)
                if not m:
                    raise ValueError(f"Failed to extract fmax from line: {line}")
                fmax_1 = float(m[1])
                fmax_2 = float(m[2])
                results += [fmax_1, fmax_2]
    return results


def extract_fmax(proj_dir: Path) -> float | None:
    """
    Find the maximum clock frequency for the given project.
    """
    sta_path = get_sta_path(proj_dir)
    if sta_path is None:
        return None
    fmaxes = extract_all_fmaxes(sta_path)
    if not fmaxes:
        raise ValueError(f"Failed to extract fmax from {sta_path}.")
    return min(fmaxes)


def set_target_freq(proj_dir: Path, freq: float) -> None:
    """
    Set the target frequency
    """
    # freq should be in MHz
    period = 1_000_000_000 / (freq * 1_000_000)
    sdc_path = get_sdc_path(proj_dir)
    if sdc_path is None:
        raise RuntimeError(f"Could not locate .sdc file in {proj_dir.as_posix()}.")
    text = sdc_path.read_text(encoding="utf-8")
    found = False
    updated_lines: list[str] = []
    for line in text.splitlines():
        updated_line = re.sub(r"-period\s+\d+\.\d+", f"-period {period:.3f}", line)
        found = found or updated_line != line
        updated_lines.append(updated_line)
    if not found:
        raise RuntimeError(f"No existing clock constraint found in {sdc_path}.")
    updated_text = "".join([f"{line}\n" for line in updated_lines])
    sdc_path.write_text(updated_text, encoding="utf-8")


def can_synthesize_at_frequency(proj_dir: Path, freq: int) -> tuple[bool, float] | None:
    """
    Check whether the design can be synthesized at a given frequency.
    Return None if the fmax value could not be found.
    Otherwise, return a boolean indicating success of synthesis along with Quartus' estimated fmax.
    """
    set_target_freq(proj_dir, freq)
    synthesize_design(proj_dir)
    fmax = extract_fmax(proj_dir)
    if fmax is None:
        return None
    success = fmax >= freq
    return (success, fmax)


def measure_fmax(proj_dir: Path, start_freq: int = 200, max_steps: int = 4) -> Fmax | None:
    """
    Measure the maximum clock frequency by repeatedly trying to synthesize the design with
    different target frequencies.
    """
    lower: float | None = None
    upper: float | None = None
    steps: list[Step] = []
    next_freq: int = start_freq
    while True:
        if lower is not None and upper is not None and upper <= lower + 1:
            # Found an answer
            break
        if len(steps) > max_steps:
            # Too many steps
            steps.append(Step(next_freq, None))
            break
        # Try to synthesize
        result = can_synthesize_at_frequency(proj_dir, next_freq)
        if result is None:
            return None
        (success, fmax) = result
        steps.append(Step(next_freq, success))
        # Update bounds
        if success:
            lower = next_freq if lower is None else max(lower, next_freq)
        else:
            upper = next_freq if upper is None else min(upper, next_freq)
        # Choose next frequency
        next_next_freq = int(fmax)
        if lower is None:
            # Any lower bound is better than nothing
            next_next_freq -= 10
        elif upper is None:
            # Any upper bound is better than nothing
            next_next_freq += 10
        out_of_bounds = (
            (lower is not None and next_next_freq <= lower)
            or (upper is not None and next_next_freq >= upper)
        )
        if out_of_bounds:
            # Quartus' estimate is not useful in this case, so resort to binary search.
            # Can lower be None? No. Suppose lower was None. Then:
            #  * Synthesis has always failed (otherwise we would have found a lower bound).
            #  * We have an upper bound (since synthesis has failed at least once).
            #  * next_next_freq >= upper (since out_of_bounds is True).
            #  * But this is impossible, since it would mean Quartus' fmax estimate is higher than
            #    the last frequency we tried, but by definition that would indicate successful
            #    synthesis.
            assert lower is not None
            # Similarly, upper is also not None.
            assert upper is not None
            next_next_freq = lower + (upper - lower) // 2
        next_freq = next_next_freq
    return Fmax(lower=lower, upper=upper, steps=steps)
