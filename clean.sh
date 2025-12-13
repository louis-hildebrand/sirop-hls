#!/bin/bash
set -ue

echo "Cleaning main repo..."
git clean -xdi -e .idea/

echo ""
echo "Removing submodules..."
git submodule deinit lib/arithexpr src/test/aetherling src/test/shir
