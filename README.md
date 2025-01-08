**ByteBack** is a deductive verifier for Java that works at the level
of JVM bytecode.  It provides an annotation library to specify
programs at the source code level in a way that "survives" compilation
to bytecode. Then, ByteBack encodes bytecode programs into
[Boogie](https://github.com/boogie-org/boogie), which performs the
actual verification.

# Replication

This is ByteBack's replication package for the paper:

> Reasoning about Substitutability at the Level of JVM Bytecode
>
> by Marco Paganoni and Carlo A. Furia

## Summary

ByteBack's experiments consist of a set of specified programs written in
Java 17, Scala 2.3, and Kotlin 1.8, which are located in the
subproject at [./byteback-test](./byteback-test). Each program is
specified using BBLib, which is located in the subproject at
[./byteback-specification](./byteback-specification).

## Setup

The following two subsections describe how to install and run in a
container ByteBack's Docker image: 
[using this replication package's offline copy](#offline-setup), 
or [pulling from DockerHub](#setup-with-dockerhub).

### Offline setup

The following instructions show how to install ByteBack in a container
using the Docker image located in <./image/byteback.tar>.

#### Loading the image

Load ByteBack's image into Docker:

``` bash
docker load -i byteback.tar
```

After loading the image:

``` bash
docker images
```

should list the image `localhost/byteback`.

#### Running the container

Run the image in a container:

``` bash
docker run -it localhost/byteback:latest
```

This command runs a shell in a container with ByteBack and this
replication package's content installed. Follow the instructions in
the rest of this document to test the installation and replicate the
results.

### Setup with DockerHub

Assuming you are connected to the internet, download and run
ByteBack's Docker image with:

```bash
docker run -it paganma/byteback:fase25
```

This command runs a shell in a container with ByteBack and this
replication package's content installed. Follow the instructions in
the rest of this document to test the installation and replicate the
results.

## Test instructions

ByteBack comes with an extensive collection of system tests, which run
some verification tasks in [./byteback-test](./byteback-test). The
system tests check that the verification tasks are correctly
translated into Boogie, and that the generated Boogie programs verify.

To run the system tests, trigger the `system` Gradle task:

``` bash
./gradlew system
```

These tests should terminate executing without reporting any failures,
printing the message: `BUILD SUCCESSFUL`.

## Replication instructions

This replication package allows users to replicate the results of the
experiments described in Section 4 of the paper. More precisely, the
paper's Table 1 summarizes the experimental results; in the following,
we describe how to reproduce that table's content.

The experiments run the annotated programs in
[./byteback-test](./byteback-test). Experiments are subdivided in
groups, corresponding to Table 1's column `LANG`: `J 17` for Java 17
programs; `S 2.13` for Scala 2.13.8 programs; `K 1.8` for Kotlin 1.8
programs.

To compute the experiments execute Gradle task `results`:

``` bash
./gradlew results
```

Execution should terminate without reporting any failures, printing the
message: `BUILD SUCCESSFUL`.

Upon successful execution, file
[./byteback-test/build/experiments/results.csv](./byteback-test/build/experiments/results.csv)
will store the data corresponding to Table 1, as we detail next.

The CSV file `results.csv` includes one row for each verified program,
in the same order as the paper's Table 1. Each row has the columns
described below. In the following list of column names, we put between
square brackets the name of the corresponding column in the paper's
Table 1 if one exists (several of the CSV columns are not reported in
Table 1).

### Metrics

For each of the experiments the results table shows the following
metrics:

SourceLinesOfCode [SOURCE SIZE]
The SLOCs of the experiment's source code.

BytecodeLinesOfCode   
The SLOCs of the experiment's bytecode (as given by `javap`).

BoogieLinesOfCode [BOOGIE SIZE]  
The SLOCs of the experiment's generated Boogie code.

MethodCount [MET]  
Number of methods in the experiment.

ClassesCount [CLS]  
Number of classes in the experiment.

SpecAttachCount [Annotations A]
Number of `@Attach` annotations used.

SpecRequireCount   
The number of `@Require` annotations used.

SpecEnsureCount   
Number of `@Ensure` annotations used.

SpecRaiseCount   
Number of `@Raise` annotations used.

SpecReturnCount   
Number of `@Return` annotations used.

SpecBehaviorCount [ANNOTATIONS B]  
Number of `@Behavior` annotations used.

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
Total number of functional (`@Require` and `@Ensure`) annotations used.

SpecIntermediateCount   
Total number of intermediate annotations (`assertion` and loop
`invariant`) used.

ConversionTime [ByteBack TIME]  
Average time taken by ByteBack to convert the bytecode of the experiment
to Boogie.

VerificationTime [Boogie TIME]  
Average time taken by Boogie to verify the Boogie code produced by
ByteBack.

The metrics `ConversionTime`, `VerificationTime`, `ConversionOverhead`
are computed as averages by running ByteBack and Boogie 5 times for each
of the experiments.

The paper's Table 1 also includes two bottom rows with **total** and
**average** of the quantitative results. The CSV file does not include
these, but they can simply be computed by summing or averaging the CSV's
columns.

### Reproducibility

With the exception of the performance metrics `ConversionTime` and
`VerificationTime`, all of the other metrics should be consistent with
the results reported in the paper's Table 1.

The performance metrics obviously depend on the hardware on which the
experiments were executed. Replicating the results should still
reveal a qualitative distribution of running times that is comparable
to the one shown in the paper's Table 1.

The performance metrics are expected to fluctuate in relation to the
performance of the hardware on which the experiments are being executed.

# Manual Setup

## Building

To build the project use the following command:

``` bash
./gradlew install
```

This command will build the .jar archive of the command line application
`byteback-tool` in
[./byteback-tool/build/libs/byteback-tool.jar](./byteback-tool/build/libs/byteback-tool.jar),
and for `byteback-specification` (BBLib) in
[./byteback-specification/build/libs/byteback-specification.jar](./byteback-specification/build/libs/byteback-specification.jar).
It is possible to call `byteback-tool` by using the execution script,
which will be located in
[./byteback-tool/build/install/byteback-tool/bin/byteback-tool](./byteback-tool/build/install/byteback-tool/bin/byteback-tool).

## Dependencies

In order to verify programs using ByteBack the following dependencies
need to be installed separately:

-   [Z3](https://github.com/Z3Prover/z3) solver 4.11.2+
-   [boogie](https://github.com/boogie-org/boogie) 3.2.5.0+

Z3 can be installed with pypi using the
[./requirements.txt] (./requirements.txt) file, while Boogie can be
installed by using the 
[.NET SDK 6](https://dotnet.microsoft.com/en-us/download/dotnet/6.0), 
issuing the following command:

``` bash
dotnet tool install -g boogie
```

### Testing

Most of the tests performed by ByteBack are system tests that use
`byteback-tool` on the classes located in the
[byteback-test](./byteback-test) subprojects. These tests requires the
following additional pypi dependencies (also listed at
[./byteback-test/scripts/requirements.txt](./byteback-test/scripts/requirements.txt)):

-   [lit](https://llvm.org/docs/CommandGuide/lit.html) 15.0.0
-   [filecheck](https://llvm.org/docs/CommandGuide/FileCheck.html) 0.0.22

### Experiments

Experiments are performed using the scripts located in
[./byteback-test/scripts](./byteback-test/scripts). To run the
experiments the following Python dependencies are required:

-   [pandas](https://pandas.pydata.org/) 1.4.3
-   [click](https://click.palletsprojects.com/en/8.1.x/) 8.1.3

These dependencies are listed in
[./byteback-test/requirements.txt](./byteback-test/requirements.txt)

# Usage

## Using ByteBack's CLI

`byteback-tool` provides a command line interface for the converter
from Java bytecode into the Boogie intermediate verification
language. The `byteback-tool` executable can be invoked with the
following options:

``` bash
byteback-tool \
	  -cp CLASS_PATH \
	  -c TARGET_CLASS \
	[ -o BOOGIE_OUTPUT ] \
	[ --npe ] \
	[ --iobe ] \
	[ --cce ] \
	[ --nas ] \
	[ --dbz ] \
	[ --strict ]
```

- `-cp CLASS_PATH` declares the classpath where ByteBack will look for
  bytecode to be analyzed. You can repeat this option to specify
  multiple classpaths.

- `-c TARGET_CLASS` give the fully qualified name of an entry class
  for ByteBack's analysis. You can repeat this option to declare
  multiple entry classes. ByteBack will recursively process all
  application classes that are referenced from any entry class
  (excluding standard library classes).

- `-o BOOGIE_OUTPUT` declares the name of the output Boogie file
  generated by ByteBack.  If this option is omitted, ByteBack prints
  the Boogie program to standard output.

- `--npe` enables support for verifying behavior of *implicit*
  `NullPointer` exceptions.

- `--iobe` enables support for verifying behavior of *implicit*
  `IndexOutOfBounds` exceptions.

- `--cce` enables support for verifying behavior of *implicit*
  `ClassCast` exceptions.

- `--nas` enables support for verifying behavior of *implicit*
  `NegativeArraySize` exceptions.

- `--dbz` enables support for verifying behavior of *implicit*
  `DivisionByZero` exceptions.

- `--strict` enforces the absence of the implicit exceptions specified
  using the previous flags.

After generating the output Boogie program it can be verified using
the `boogie` verification tool.

## Using ByteBack annotations

The ByteBack annotations library (BBLib) contains the necessary
annotations and static methods used to specify bytecode programs.

In order to convert the annotated code, `byteback-specification` must
be included in the classpath passed to `byteback-tool`.

## Running the Tests

To run the system tests execute the `system` gradle task as follows:

``` bash
./gradlew system
```

It is also possible to specify a single test by using the
`TEST_TARGET` property and the following command from within the test
project directory (e.g.
[./byteback-test/test-java-8](./byteback-test/test-java-17)):

``` bash
gradle system -PTARGET={Path to the test file (.java, .scala, .kt)}
```

An additional property `TEST_JOBS` can be specified to run the tests
in parallel jobs. The default value is one.

``` bash
./gradlew system -PTEST_JOBS=4 # runs the tests on 4 parallel jobs
```

In case of particularly limited hardware resources, some tests may
fail because the Boogie verification tool hits its default time
limit. To increase the timeout time pass the parameter
`BOOGIE_TIME_LIMIT` to the `system` task.

``` bash
./gradlew system -PBOOGIE_TIME_LIMIT={Time in seconds}
```

## Running the Experiments

To run the experiments and format the results run the following gradle
task:

``` bash
./gradlew results
```

This command will produce output in
[./byteback-test/build/experiments/](./byteback-test/build/experiments/).
The results of the experiments can be found in the file `results.csv`.
The CSV reports the statistics of each experiments computed over 5
runs of `byteback-tool` and `boogie`. To change the number of runs it
is possible to pass the number of repetitions with the property
`EXP_REPETITIONS`.

``` bash
./gradlew results -PEXP_REPETITIONS=1 # Repeat experiment only once
```

The experiments are ran sequentially, hence lower values for the
`EXP_REPETITIONS` parameter will decrease the computation time of the
experimental results considerably.

To further adjust the execution of the experiments, the `TEST_JOBS`
and `BOOGIE_TIME_LIMIT` parameters shown in the previous section for
the `system` task also apply to the `results` task.

## An example of usage

Here we show to use `byteback-tool` to verify the Java program
`Main.java` in [./example](./example).

`Main.java` contains a single method `positive_sum` with pre- and
postcondition:

``` java
@Behavior
public boolean positive_arguments(int a, int b) {
    return gte(a, 0) & gte(b, 0);
}

@Behavior
public boolean positive_return(int a, int b, int returns) {
    return gte(returns, 0);
}

@Require("positive_arguments")   // a >= 0 && b >= 0
@Ensure("positive_return")       // returns >= 0
public int positive_sum(int a, int b) {
    return a + b;
}
```

To verify this simple program, go to directory [./example](./example)
and follow these steps:

1. Compile `Main.java`, including the `BBlib` `.jar` in the classpath:

``` bash
javac Main.java -cp ../byteback-specification/build/libs/byteback-specification.jar
```

  This generates bytecode for class `Main`.

2. Run ByteBack using `byteback-tool` on the generated bytecode:

``` bash
# The PATH to byteback-tool is already set in the Docker image
export PATH="$PATH:../byteback-tool/build/install/byteback-tool/bin/"

byteback-tool \
	-cp ../byteback-specification/build/libs/byteback-specification.jar \
	-cp . \
	-c Main \
	-o Main.bpl
```

  This generates a Boogie program `Main.bpl`, which encodes the Java
  program's semantics and its specification in Boogie.
  
3. Finally, verify the generated Boogie program using the Boogie
   verifier:
  
``` bash
boogie Main.bpl
```

This should return with a successful verification:

`Boogie program verifier finished with 2 verified, 0 errors`
