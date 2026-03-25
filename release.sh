#!/bin/bash
set -ue

function check_version {
    local version; version="$1"
    local pat; pat='[0-9]+\.[0-9]+\.[0-9]+'
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

echo "Creating release for $version..."

sbt assembly
target="./target/scala-2.12/sirop-$version.jar"
mv ./target/scala-2.12/sirop.jar "$target"

git tag -a "v$version" -m "Release v$version" -e
git push origin "v$version"

echo "$version-SNAPSHOT" > "$version_path"

echo ""
echo "Release created at $target"
echo "Don't forget to upload the .jar file to Bitbucket"
