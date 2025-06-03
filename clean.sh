#!/bin/bash
set -ue

echo "Cleaning main repo..."
git clean -xdi -e .idea/

echo ""
echo "Cleaning ArithExpr..."
cd lib/arithexpr
git clean -xdi
