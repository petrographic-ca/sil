#!/usr/bin/env bash
set -euo pipefail

rm -f ./dev-bricks/*.jpg
mkdir -p ./dev-bricks
./scripts/mvn-compile
./bin/bricks 20 25 30 5 4 3 10 4 ./dev-bricks JPEG
