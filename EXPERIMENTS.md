# Experiments

These are the instructions on how to run ByteBack's verification
experiments.

## Summary

ByteBack's experiments consist of a set of specified programs written in
Java 8, Java 17, Scala 2.3, and Kotlin 1.8, which are located in the
subproject at [./byteback-test](./byteback-test). Each program is
specified using BBLib, which is located in the subproject at
[./byteback-annotations](./byteback-annotations).

## Resource requirements

This replication package does not have specific hardware requirements.

For smoothly running the experiments we suggest a configuration of at
least 2 virtual CPU cores and 8GB of RAM.

We ran this replication package in iFM 2023's virtual machine on a
laptop with a CPU i7-7600U 4x 3.9Ghz and 8GB of RAM.  With the default
settings the replication of all the experiments (task `results`)
completed in 43 minutes.

For reference, running the same experiments in the same virtual
machine on better hardware (CPU i9-12950HX 24x 4.9Ghz, 64 GB of RAM)
took 24 minutes.

## Test instructions

ByteBack comes with an extensive collection of system tests, which run
some simple verification tasks in [./byteback-test](./byteback-test).
The system tests check that the verification tasks are correctly
translated into Boogie, and that the generated Boogie programs verify.

To run the system tests, trigger Gradle task `system`:

``` bash
./gradlew system
```

These tests should terminate executing without reporting any failures,
printing the message: `BUILD SUCCESSFUL`.

## Formatting the results

The experiments run the annotated programs in
[./byteback-test](./byteback-test). Experiments are subdivided in
groups, corresponding to Table 4's column `LANG`: `j8` for Java 8
programs; `j17` for Java 17 programs; `s2` for Scala 2.13.8 programs;
`k18` for Kotlin 1.8 programs.

To format the results in one table execute Gradle task `results`:

``` bash
./gradlew results
```

Execution should terminate without reporting any failures, printing the
message: `BUILD SUCCESSFUL`.

Upon successful execution, file
[./byteback-test/build/experiments/results.csv](./byteback-test/build/experiments/results.csv)
will store the data corresponding to Table 4, as we detail next.

The CSV file `results.csv` includes one row for each verified program.
Each row has the columns described below.

### Metrics

For each of the experiments the results table shows the following
metrics:

SourceLinesOfCode
The SLOCs of the experiment's source code.

BytecodeLinesOfCode   
The SLOCs of the experiment's bytecode (as given by `javap`).

BoogieLinesOfCode   
The SLOCs of the experiment's generated Boogie code.

MethodCount   
Number of methods in the experiment.

SpecRequireCount   
The number of `@Require` annotations used.

SpecEnsureCount   
Number of `@Ensure` annotations used.

SpecRaiseCount   
Number of `@Raise` annotations used.

SpecReturnCount   
Number of `@Return` annotations used.

SpecPredicateCount   
Number of `@Predicate` annotations used.

SpecPureCount   
Number of `@Pure` annotations used.

SpecAssertionCount   
Number of assertions specified.

SpecAssumptionCount   
Number of assumptionsn specified.

SpecInvariantCount   
Number of loop invariants specified.

UsesExceptionFeatures   
Whether the experiment uses exception-related features.

SpecExceptionCount   
Total number of exception-related annotations used.

SpecFunctionalCount   
Total number of functional (`@Require` and `Ensure`) annotations used.

SpecIntermediateCount   
Total number of intermediate annotations (`assertion` and loop
`invariant`) used.

ConversionTime   
Average time taken by ByteBack to convert the bytecode of the experiment
to Boogie.

VerificationTime   
Average time taken by Boogie to verify the Boogie code produced by
ByteBack.

ConversionOverhead   
Percentage of the overhead introduced by ByteBack for the conversion,
without accounting for Soot's initialization time.

The metrics `ConversionTime`, `VerificationTime`, `ConversionOverhead`
are computed as averages by running ByteBack and Boogie 5 times for each
of the experiments.

### Replication with Limited Resources

As mentioned above, to compute the performance metrics ByteBack and
Boogie are executed 5 times for each experiment. This can make the
execution of the experiments slow.

To change the number of runs it is possible to pass the property
`EXP_REPETITIONS` to the `results` task:

``` bash
./gradlew results -PEXP_REPETITIONS={Number of repetitions}
```

The `results` task depends on the `system` task used for running test.
By default the tests run on a single thread. To change this it is
possible to pass the parameter `TEST_JOBS`.

``` bash
./gradlew results -PTEST_JOBS={Number of jobs}
```

Finally, if the hardware running the experiments is much slower, some
experiments may fail because the Boogie verification tool hits its
default timeout. To increase the timeout time it is possible to pass the
parameter `BOOGIE_TIME_LIMIT` to `gradlew`.

``` bash
./gradlew results -PBOOGIE_TIME_LIMIT={Time in seconds}
```
