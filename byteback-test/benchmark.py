import lit.util
import lit.formats
from lit import TestRunner
import os
import re
import threading
from multiprocessing import Manager
import pandas as pd
import subprocess as sp
import glob as gb

manager = Manager()
summary = manager.list()
summary_lock = threading.Lock()
summary_path = "./summary.csv"
run_sh_test = TestRunner._runShTest


def re_strip(string, pattern):
    stripped_string = string.strip()
    m = re.match(pattern, stripped_string)
    
    if m:
        return stripped_string[len(m.group(0)) :].strip()
    else:
        return stripped_string


def extract_commands(script):
    return [re_strip(re_strip(command, r"%dbg\(.*\)"), ": 'RUN.*';")
            for subcommands in script for command in subcommands.split("|")]


def is_boogie_command(command):
    return command.startswith("boogie")


def is_byteback_command(command):
    return command.startswith(byteback_executable)


src_base = os.getenv('SRC_BASE')
build_base = os.getenv('CLASS_BASE')
tmp_base = os.getenv('TMP')
summary_base = os.getenv('SUMMARY')
boogie_time_limit = os.getenv('BOOGIE_TIME_LIMIT')


def get_file_extension(path):
    _, ext = os.path.splitext(path)

    return ext


def count_lines(file_path):
    return int(sp.check_output(f"cat {file_path} | grep -c '[^[:space:]]'", shell=True))


def compute_source_locs(deps):
    locs = 0

    for dep in deps:
        locs = locs + count_lines(dep)

    return locs


def compute_class_locs(classes):
    locs = 0

    for clazz in classes:
        locs = locs + count_class_lines(clazz)

    return locs


def count_class_lines(class_path):
    return int(sp.check_output(f"javap -c {class_path} | grep -c '[^[:space:]]'", shell=True))


def count_words_in(path, pattern):
    command = f"grep -c '{pattern}' {path} || true"
    return int(sp.check_output(command, shell=True))


def count_patterns(files, pattern):
    count = 0

    for path in files:
        count += count_words_in(path, pattern)

    return count


def count_methods(clazz):
    return int(sp.check_output(f"javap {clazz} | grep -c '(*)'", shell=True))


def count_methods_in_classes(classes):
    count = 0

    for clazz in classes:
        count += count_methods(clazz)

    return count


def patch_run_sh_test(test, litConfig, useExternalSh, script, tmpBase):
    ret = run_sh_test(test, litConfig, useExternalSh, script, tmpBase)
    entry = {}

    for command in extract_commands(script):
        if is_byteback_command(command):
            entry["BytebackCommand"] = command
        elif is_boogie_command(command):
            entry["BoogieCommand"] = command

    if "BytebackCommand" in entry and "BoogieCommand" in entry:
        test_path = test.getFilePath()
        test_ext = get_file_extension(test_path)
        test_name = os.path.basename(test_path)
        test_dir = os.path.dirname(test_path)
        deps_path = strip_extension(test_path) + ".dep"
        deps = [strip_extension(test_path).removeprefix(src_base)]

        if os.path.exists(deps_path):
            with open(deps_path, "r") as deps_file:
                deps.extend(map(lambda x: x.strip(), deps_file.readlines()))

        srcs = []

        for dep in deps:
            src_path = os.path.normpath(src_base + "/" + dep + test_ext)

            if os.path.exists(src_path):
                srcs.append(src_path)

        classes = []

        for dep in deps:
            class_path = os.path.normpath(build_base + "/" + dep + "*" + ".class")
            glob = gb.glob(class_path)

            if not len(glob) == 0:
                classes.extend(glob)

        entry["Test"] = deps[0]
        entry["SourcePaths"] = srcs
        entry["ClassPaths"] = classes
        entry["BoogiePath"] = os.path.normpath(test_dir + "/Output/" + test_name + ".tmp.bpl")
        entry["SourceLinesOfCode"] = compute_source_locs(srcs)
        entry["BytecodeLinesOfCode"] = compute_class_locs(classes)
        entry["BoogieLinesOfCode"] = count_lines(entry["BoogiePath"])
        entry["MethodCount"] = count_methods_in_classes(classes)
        entry["SpecRequireCount"] = count_patterns(srcs, "@.*Require")
        entry["SpecEnsureCount"] = count_patterns(srcs, "@.*Ensure")
        entry["SpecRaiseCount"] = count_patterns(srcs, "@.*Raise")
        entry["SpecReturnCount"] = count_patterns(srcs, "@.*Return")
        entry["SpecPredicateCount"] = count_patterns(srcs, "@.*Predicate")
        entry["SpecPureCount"] = count_patterns(srcs, "@.*Pure")
        entry["SpecAssertionCount"] = count_patterns(srcs, "assertion *(.*)")
        entry["SpecAssumptionCount"] = count_patterns(srcs, "assumption *(.*)")
        entry["SpecInvariantCount"] = count_patterns(srcs, "invariant *(.*)")
        entry["UsesExceptionFeatures"] = count_patterns(srcs, "try") > 0 | count_patterns(srcs, "throw");
        summary.append(entry)

    return ret


TestRunner._runShTest = patch_run_sh_test


def print_summary(*args):
    df = pd.DataFrame(list(summary))
    df.to_csv(os.path.join(summary_base, "summary.csv"), index=False)
