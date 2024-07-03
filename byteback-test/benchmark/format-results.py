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
    print_macro("/bbe/count/scala", len(df[df["Group"] == "s2"].index))
    print_macro("/bbe/count/kotlin", len(df[df["Group"] == "k18"].index))

    print_macro("/bbe/count", len(df.index))
    print_macro("/bbe/count/method", df["MethodCount"].sum())
    print_macro("/bbe/count/raises", df["SpecRaiseCount"].sum())
    print_macro("/bbe/count/returns", df["SpecReturnCount"].sum())
    print_macro("/bbe/count/invariants", df["SpecInvariantCount"].sum())
    print_macro("/bbe/count/assertions", df["SpecAssertionCount"].sum())

    # Group-specific statistics
    for group in groups:
        # Experiments Count
        gdf = df[df["Group"] == group]
        print_macro(f"/bbe/count/{group}", len(gdf.index))

        # Encoding Time
        print_macro(
            f"/bbe/total/{group}/ConversionTime",
            gdf["ConversionTime"].sum())
        print_macro(
            f"/bbe/average/{group}/ConversionTime",
            gdf["ConversionTime"].mean())

        # Source Size
        print_macro(
            f"/bbe/total/{group}/SourceLinesOfCode",
            gdf["SourceLinesOfCode"].sum())
        print_macro(
            f"/bbe/average/{group}/SourceLinesOfCode",
            gdf["SourceLinesOfCode"].mean())

        # Boogie Size
        print_macro(
            f"/bbe/total/{group}/BoogieLinesOfCode",
            gdf["SourceLinesOfCode"].sum())
        print_macro(
            f"/bbe/average/{group}/BoogieLinesOfCode",
            gdf["SourceLinesOfCode"].mean())

        # Methods count
        print_macro(
            f"/bbe/count/method/{group}",
            gdf["MethodCount"].sum())
        print_macro(
            f"/bbe/average/method/{group}",
            gdf["MethodCount"].mean())

        # Raises count
        print_macro(
            f"/bbe/count/raises/{group}",
            gdf["SpecRaiseCount"].sum())
        print_macro(
            f"/bbe/average/raises/{group}",
            gdf["SpecRaiseCount"].mean())

        # Returns count
        print_macro(
            f"/bbe/count/returns/{group}",
            gdf["SpecReturnCount"].sum())
        print_macro(
            f"/bbe/average/returns/{group}",
            gdf["SpecReturnCount"].mean())

        # Exceptional Behavior Annotations
        gdf["SpecExceptionCount"] = gdf["SpecReturnCount"] + gdf["SpecRaiseCount"]
        gdf["SpecFunctionalCount"] = gdf["SpecEnsureCount"] + gdf["SpecRequireCount"]
        gdf["SpecIntermediateCount"] = gdf["SpecAssertionCount"] + gdf["SpecInvariantCount"]

        # Invariants count
        print_macro(
            f"/bbe/count/invariants/{group}",
            gdf["SpecInvariantCount"].sum())
        print_macro(
            f"/bbe/average/invariants/{group}",
            gdf["SpecInvariantCount"].mean())

        # Assertions count
        print_macro(
            f"/bbe/count/assertions/{group}",
            gdf["SpecAssertionCount"].sum())
        print_macro(
            f"/bbe/average/assertions/{group}",
            gdf["SpecAssertionCount"].mean())

    # Global statistics
    for index, row in df.iterrows():
        prefix = prefix if prefix != None else ""
        group = row["Group"]
        identifier = row["Test"].removeprefix(prefix)

        def print_field(field):
            print_macro(f"/bbe/{group}/{identifier}/{field}", row[field])

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
