#!/bin/env bash

python3 ./scripts/format-results.py ./test-java-8/build/experiments/results.csv j8 ./test-java-17/build/experiments/results.csv j17 ./test-scala-2.13.8/build/experiments/results.csv s2 ./test-kotlin-1.8.0/build/experiments/results.csv k18 --prefix /byteback/test/ --output-tex ./build/experiments/experiments.tex --output-csv ./build/experiments/results.csv --index ./scripts/bbe-index.csv

echo "% DATE: $(date)" >> ./build/experiments/experiments.tex
