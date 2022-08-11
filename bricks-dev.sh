#!/usr/bin/env bash
set -euo pipefail

rm -f ./dev-bricks/*.jpg
mkdir -p ./dev-bricks
./scripts/mvn-compile
./bin/bricks 130 140 150 90 70 50 7 3 ./dev-bricks JPEG
