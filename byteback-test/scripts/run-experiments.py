import os
import re
import logging as lg
import time as tm
import pandas as pd
import subprocess as sp
import click as cl
import sys

lg.basicConfig(level=lg.DEBUG)


def timeit(f):
    start = round(tm.time() * 1000)
    f()
    end = round(tm.time() * 1000)

    return end - start


def run_command(command):
    return sp.run(command, stdout=sp.PIPE, stderr=sp.PIPE, shell=True)


def verification_benchmark(command):
    r = re.compile("Boogie program verifier finished with [0-9]+ verified, 0 errors")
    def f():
        process = run_command(command)

        if not r.search(process.stdout.decode("utf-8")):
            raise RuntimeError("Boogie program could not be verified")

        if process.returncode != 0:
            raise RuntimeError("Boogie execution failed")

    return timeit(f)


def conversion_benchmark(command):
    r = re.compile("Conversion completed in ([0-9])+ms, total time ([0-9])+ms")
    d = re.compile("[0-9]+")

    process = run_command(command)
    output = r.search(process.stderr.decode("utf-8"));

    if process.returncode != 0:
        raise RuntimeError("ByteBack execution failed")

    if not output:
        raise RuntimeError("Could not match byteback's output")

    numbers = re.findall(d, output.group(0));

    return int(numbers[0]), int(numbers[1])


def benchmark(entry, repetitions):
    conversion_time = 0
    total_conversion_overhead = 0
    total_conversion_time = 0
    total_verification_time = 0

    lg.info(f"Benchmarking {entry['Test']}")

    for _ in range(0, repetitions):
        b, t = conversion_benchmark(entry["BytebackCommand"])
        total_conversion_time += t
        total_conversion_overhead +=  b / t
        total_verification_time += verification_benchmark(entry["BoogieCommand"])

    entry["ConversionTime"] = total_conversion_time / repetitions
    entry["VerificationTime"] = total_verification_time / repetitions
    entry["ConversionOverhead"] = total_conversion_overhead / repetitions
    lg.info(f"Results:")
    lg.info(f"Conversion Time: {entry['ConversionTime']}")
    lg.info(f"Verification Time: {entry['VerificationTime']}")
    lg.info(f"Conversion Overhead: {entry['ConversionOverhead']}")

    return entry


@cl.command()
@cl.option("--output", required=True, help="Path to the output .csv file")
@cl.option("--repetitions", required=True, type=cl.INT, help="Repetitions for each test")
@cl.option("--summary", required=True, help="Path to the .csv containing the system tests' summary")
def main(output, repetitions, summary):
    output_path = output
    data = []
    idf = pd.read_csv(summary)

    for index, entry in idf.iterrows():
        data.append(benchmark(entry, repetitions))

    df = pd.DataFrame(data)
    df.to_csv(output_path)


main()
