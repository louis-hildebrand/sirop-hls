#!/bin/bash
set -ue

function check_version {
    local version; version="$1"
    local pat; pat='^[0-9]+\.[0-9]+\.[0-9]+$'
    if [[ ! "$version" =~ $pat ]]; then
        echo "Invalid version: $version"
        echo "Versions should match the regular expression '$pat'"
        exit 1
    fi
}

cd "$(git rev-parse --show-toplevel)"
version_path="$(readlink -f "./src/main/resources/version.txt")"
version="$(sed "$version_path" -e 's/-SNAPSHOT//')"
check_version "$version"
# Remove the trailing SNAPSHOT from the version.txt file for release
echo "$version" > "$version_path"

echo "Creating release for $version..."

sbt assembly
target="./target/scala-2.12/sirop-$version.jar"
mv ./target/scala-2.12/sirop.jar "$target"

git tag -a "v$version" -m "Release v$version" -e

echo "$version-SNAPSHOT" > "$version_path"

echo ""
echo "Release JAR created at $target"

echo ""
echo "WHAT TO DO NOW"
echo "1. Push the new tag: git push origin v$version"
echo "2. Create release on GitHub: https://github.com/louis-hildebrand/sirop-hls/releases"
