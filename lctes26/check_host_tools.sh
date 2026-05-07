#!/bin/bash

error='false'

quartus_sh --version || {
    error='true'
    echo 'MISSING: quartus_sh'
}
echo

vcom -version || {
    error='true'
    echo 'MISSING: vcom'
}
echo

vsim -version || {
    error='true'
    echo 'MISSING: vsim'
    echo
}

i++ --version || {
    error='true'
    echo 'MISSING: i++'
}
echo

python3 --version \
&& {
    actual_version="$(python3 --version | sed 's/Python //p')"
    expected_version='3.10.0'
    { echo -e "$expected_version\n$actual_version" | sort -CV; } || {
        error='true'
        echo "TOO OLD: python (need at least $expected_version)"
    }
} \
|| {
    error='true'
    echo 'MISSING: python3'
}
echo

if [[ "$error" != 'false' ]]; then
    echo "Some tools are missing or do not have the right versions."
    exit 1
else
    echo "Everything seems to be in order."
fi
