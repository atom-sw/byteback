import pandas as pd
import click as cl
import datetime as dt
import sys
import warnings

warnings.filterwarnings("ignore")

LATEX_MACRO = "\pgfkeyssetvalue"

def pairs(l):
    return zip(l[::2], l[1::2])

@cl.command()
@cl.option("--output-csv", required=False, help="Output CSV")
@cl.option("--output-tex", required=False, help="Output LaTeX")
@cl.option("--index", required=False, help="Index of the experiments")
@cl.option("--prefix", required=False, help="Excludes name prefix from pgf key")
@cl.argument("csvs", nargs=-1)
def main(csvs, output_csv, output_tex, index, prefix):
    df = pd.DataFrame()
    output_tex_file = open(output_tex, "w") if output_tex else sys.stdout
    groups = []

    for csv, group in pairs(csvs):
        nf = pd.read_csv(csv, index_col=0)
        nf["Group"] = group
        df = pd.concat([df, nf])
        groups.append(group)

    idf = pd.read_csv(index)
    df = df.drop(columns=["BytebackCommand", "BoogiePath", "BoogieCommand", "SourcePaths", "ClassPaths"])
    original_df = df
    df = df.set_index(["Group", "Test"])
    df = df.reindex(idf[["Group", "Test"]])
    df = df.reset_index()

    df["SpecExceptionCount"] = df["SpecReturnCount"] + df["SpecRaiseCount"]
    df["SpecFunctionalCount"] = df["SpecEnsureCount"] + df["SpecRequireCount"]
    df["SpecIntermediateCount"] = df["SpecAssertionCount"] + df["SpecInvariantCount"]

    df.index += 1
    df.to_csv(output_csv, index=True)

    def print_macro(key, value):
        print(f"{LATEX_MACRO}{{{key}}}{{{value}}}", file=output_tex_file)

    print_macro("/bbe/count/non-exceptional", len(original_df.index) - len(df.index))
    print_macro("/bbe/count/java", len(df[df["Group"] == "j8"].index) + len(df[df["Group"] == "j17"].index))

    print_macro("/bbe/count", len(df.index))
    print_macro("/bbe/count/method", df["MethodCount"].sum())
    print_macro("/bbe/count/raises", df["SpecRaiseCount"].sum())
    print_macro("/bbe/count/returns", df["SpecReturnCount"].sum())
    print_macro("/bbe/count/invariants", df["SpecInvariantCount"].sum())
    print_macro("/bbe/count/assertions", df["SpecAssertionCount"].sum())

    # Group-specific statistics
    for group in groups:
        # Experiments Count
        print_macro(f"/bbe/count/{group}", len(df[df["Group"] == group].index))

        # Methods count
        print_macro(f"/bbe/count/method/{group}", df.loc[df["Group"] == group]["MethodCount"].sum())

        # Annotation count
        print_macro(f"/bbe/count/raises/{group}", df.loc[df["Group"] == group]["SpecRaiseCount"].sum())

        # Returns count
        print_macro(f"/bbe/count/returns/{group}", df.loc[df["Group"] == group]["SpecReturnCount"].sum())

        # Invariants count
        print_macro(f"/bbe/count/invariants/{group}", df.loc[df["Group"] == group]["SpecInvariantCount"].sum())

        # Invariants count
        print_macro(f"/bbe/count/assertions/{group}", df.loc[df["Group"] == group]["SpecAssertionCount"].sum())

    # Global statistics
    for index, row in df.iterrows():
        prefix = prefix if prefix != None else ""
        group = row["Group"]
        identifier = row["Test"].removeprefix(prefix)

        def print_field(field):
            print_macro(f"/bbe/{group}/{identifier}/{field}", row[field])

        print_field("ConversionTime")
        print_field("ConversionOverhead")
        print_field("VerificationTime")
        print_field("SourceLinesOfCode")
        print_field("BytecodeLinesOfCode")
        print_field("BoogieLinesOfCode")
        print_field("MethodCount")
        print_field("SpecRequireCount")
        print_field("SpecEnsureCount")
        print_field("SpecRaiseCount")
        print_field("SpecReturnCount")
        print_field("SpecPredicateCount")
        print_field("SpecPureCount")
        print_field("SpecAssertionCount")
        print_field("SpecAssumptionCount")
        print_field("SpecInvariantCount")
        print_field("SpecExceptionCount")
        print_field("SpecFunctionalCount")
        print_field("SpecIntermediateCount")

    def print_mean(column):
        print_macro(f"/bbe/average/{column}", df[column].mean())

    def print_total(column):
        print_macro(f"/bbe/total/{column}", df[column].sum())

    print_mean("ConversionTime")
    print_mean("ConversionOverhead")
    print_mean("VerificationTime")
    print_mean("SourceLinesOfCode")
    print_mean("BytecodeLinesOfCode")
    print_mean("BoogieLinesOfCode")
    print_mean("SpecAssertionCount")
    print_mean("SpecAssumptionCount")
    print_mean("SpecInvariantCount")
    print_mean("MethodCount")
    print_mean("SpecIntermediateCount")
    print_mean("SpecPredicateCount")
    print_mean("SpecFunctionalCount")
    print_mean("SpecExceptionCount")

    print_total("ConversionTime")
    print_total("ConversionOverhead")
    print_total("VerificationTime")
    print_total("SourceLinesOfCode")
    print_total("BytecodeLinesOfCode")
    print_total("BoogieLinesOfCode")
    print_total("MethodCount")
    print_total("SpecRequireCount")
    print_total("SpecEnsureCount")
    print_total("SpecRaiseCount")
    print_total("SpecReturnCount")
    print_total("SpecPredicateCount")
    print_total("SpecPureCount")
    print_total("SpecAssertionCount")
    print_total("SpecAssumptionCount")
    print_total("SpecInvariantCount")
    print_total("SpecFunctionalCount")
    print_total("SpecExceptionCount")
    print_total("SpecIntermediateCount")


main()
