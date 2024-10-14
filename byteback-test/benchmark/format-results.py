import pandas as pd
import click as cl
import datetime as dt
import sys
import warnings

warnings.filterwarnings("ignore")

LATEX_MACRO = "\\pgfkeyssetvalue"

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

    print_macro("/bbs/count/non-exceptional", len(original_df.index) - len(df.index))
    print_macro("/bbs/count/java", len(df[df["Group"] == "j8"].index) + len(df[df["Group"] == "j17"].index))

    print_macro("/bbs/count", len(df.index))
    print_macro("/bbs/count/method", df["MethodCount"].sum())
    print_macro("/bbs/count/raises", df["SpecRaiseCount"].sum())
    print_macro("/bbs/count/returns", df["SpecReturnCount"].sum())
    print_macro("/bbs/count/invariants", df["SpecInvariantCount"].sum())
    print_macro("/bbs/count/assertions", df["SpecAssertionCount"].sum())

    jdf = df[(df["Group"] == "j17") | (df["Group"] == "k18")]
    print_macro("/bbs/j17-k18/SourceToBoogieRatio", jdf["BoogieLinesOfCode"].sum() / jdf["SourceLinesOfCode"].sum())

    sdf = df[(df["Group"] == "s2")]
    print_macro("/bbs/s2/SourceToBoogieRatio", sdf["BoogieLinesOfCode"].sum() / sdf["SourceLinesOfCode"].sum())

    # Group-specific statistics
    for group in groups:
        # Experiments Count
        print_macro(f"/bbs/count/{group}", len(df[df["Group"] == group].index))

        # Methods count
        print_macro(f"/bbs/count/method/{group}", df.loc[df["Group"] == group]["MethodCount"].sum())

        # Annotation count
        print_macro(f"/bbs/count/raises/{group}", df.loc[df["Group"] == group]["SpecRaiseCount"].sum())

        # Returns count
        print_macro(f"/bbs/count/returns/{group}", df.loc[df["Group"] == group]["SpecReturnCount"].sum())

        # Invariants count
        print_macro(f"/bbs/count/invariants/{group}", df.loc[df["Group"] == group]["SpecInvariantCount"].sum())

        # Invariants count
        print_macro(f"/bbs/count/assertions/{group}", df.loc[df["Group"] == group]["SpecAssertionCount"].sum())

    # Global statistics
    for index, row in df.iterrows():
        prefix = prefix if prefix != None else ""
        group = row["Group"]
        identifier = row["Test"].removeprefix(prefix)

        def print_field(field):
            print_macro(f"/bbs/{group}/{identifier}/{field}", row[field])

        print_field("ConversionTime")
        print_field("VerificationTime")
        print_field("SourceLinesOfCode")
        print_field("BytecodeLinesOfCode")
        print_field("BoogieLinesOfCode")
        print_field("MethodCount")
        print_field("SpecRequireCount")
        print_field("SpecEnsureCount")
        print_field("SpecRaiseCount")
        print_field("SpecReturnCount")
        print_field("SpecBehaviorCount")
        print_field("SpecAttachCount")
        print_field("SpecAssertionCount")
        print_field("SpecAssumptionCount")
        print_field("SpecInvariantCount")
        print_field("SpecExceptionCount")
        print_field("SpecFunctionalCount")
        print_field("SpecIntermediateCount")
        print_field("ClassesCount")

    def print_mean(column):
        print_macro(f"/bbs/average/{column}", df[column].mean())

    def print_total(column):
        print_macro(f"/bbs/total/{column}", df[column].sum())

    print_mean("ConversionTime")
    print_mean("VerificationTime")
    print_mean("SourceLinesOfCode")
    print_mean("BytecodeLinesOfCode")
    print_mean("BoogieLinesOfCode")
    print_mean("SpecAssertionCount")
    print_mean("SpecAssumptionCount")
    print_mean("SpecInvariantCount")
    print_mean("MethodCount")
    print_mean("SpecIntermediateCount")
    print_mean("SpecBehaviorCount")
    print_mean("SpecFunctionalCount")
    print_mean("SpecExceptionCount")
    print_mean("SpecAttachCount")
    print_mean("ClassesCount")

    print_total("ConversionTime")
    print_total("VerificationTime")
    print_total("SourceLinesOfCode")
    print_total("BytecodeLinesOfCode")
    print_total("BoogieLinesOfCode")
    print_total("MethodCount")
    print_total("SpecRequireCount")
    print_total("SpecEnsureCount")
    print_total("SpecRaiseCount")
    print_total("SpecReturnCount")
    print_total("SpecBehaviorCount")
    print_total("SpecAttachCount")
    print_total("SpecAssertionCount")
    print_total("SpecAssumptionCount")
    print_total("SpecInvariantCount")
    print_total("SpecFunctionalCount")
    print_total("SpecExceptionCount")
    print_total("SpecIntermediateCount")
    print_total("ClassesCount")


main()
