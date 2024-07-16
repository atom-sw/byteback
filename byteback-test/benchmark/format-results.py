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
    df["Type"] = idf["Type"]

    df["SpecExceptionCount"] = df["SpecReturnCount"] + df["SpecRaiseCount"]
    df["SpecFunctionalCount"] = df["SpecEnsureCount"] + df["SpecRequireCount"]
    df["SpecCount"] = df["SpecFunctionalCount"] + df["SpecExceptionCount"]
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

    def print_group(gdf, group):

        # Encoding Time
        print_macro(
            f"/bbe/{group}/total/ConversionTime",
            gdf["ConversionTime"].sum())
        print_macro(
            f"/bbe/{group}/average/ConversionTime",
            gdf["ConversionTime"].mean())

        # Verification Time
        print_macro(
            f"/bbe/{group}/total/VerificationTime",
            gdf["VerificationTime"].sum())
        print_macro(
            f"/bbe/{group}/average/VerificationTime",
            gdf["VerificationTime"].mean())

        # Source Size
        print_macro(
            f"/bbe/{group}/total/SourceLinesOfCode",
            gdf["SourceLinesOfCode"].sum())
        print_macro(
            f"/bbe/{group}/average/SourceLinesOfCode",
            gdf["SourceLinesOfCode"].mean())

        # Boogie Size
        print_macro(
            f"/bbe/{group}/total/BoogieLinesOfCode",
            gdf["BoogieLinesOfCode"].sum())
        print_macro(
            f"/bbe/{group}/average/BoogieLinesOfCode",
            gdf["BoogieLinesOfCode"].mean())

        # Methods count
        print_macro(
            f"/bbe/{group}/total/MethodCount",
            gdf["MethodCount"].sum())
        print_macro(
            f"/bbe/{group}/average/MethodCount",
            gdf["MethodCount"].mean())

        # Raises count
        print_macro(
            f"/bbe/{group}/total/SpecRaiseCount",
            gdf["SpecRaiseCount"].sum())
        print_macro(
            f"/bbe/{group}/average/SpecRaiseCount",
            gdf["SpecRaiseCount"].mean())

        # Returns count
        print_macro(
            f"/bbe/{group}/total/SpecReturnCount",
            gdf["SpecReturnCount"].sum())
        print_macro(
            f"/bbe/{group}/average/SpecReturnCount",
            gdf["SpecReturnCount"].mean())

        # Exceptional Behavior Annotations
        print_macro(
            f"/bbe/{group}/total/SpecExceptionCount",
            gdf["SpecExceptionCount"].sum())
        print_macro(
            f"/bbe/{group}/average/SpecExceptionCount",
            gdf["SpecExceptionCount"].mean())

        # Functional Behavior Annotations
        print_macro(
            f"/bbe/{group}/total/SpecFunctionalCount",
            gdf["SpecFunctionalCount"].sum())
        print_macro(
            f"/bbe/{group}/average/SpecFunctionalCount",
            gdf["SpecFunctionalCount"].mean())

        # Behavior Annotations
        print_macro(
            f"/bbe/{group}/total/SpecBehaviorCount",
            gdf["SpecBehaviorCount"].sum())
        print_macro(
            f"/bbe/{group}/average/SpecBehaviorCount",
            gdf["SpecBehaviorCount"].mean())

        # Invariants count
        print_macro(
            f"/bbe/{group}/total/SpecInvariantCount",
            gdf["SpecInvariantCount"].sum())
        print_macro(
            f"/bbe/{group}/average/SpecInvariantCount",
            gdf["SpecInvariantCount"].mean())

        # Assertions count
        print_macro(
            f"/bbe/{group}/total/SpecAssertionCount",
            gdf["SpecAssertionCount"].sum())
        print_macro(
            f"/bbe/{group}/average/SpecAssertionCount",
            gdf["SpecAssertionCount"].mean())

    print_group(df[(df["Group"] == "j8") | (df["Group"] == "j17")], "j")
    print_group(df[(df["Type"] == "f")], "feature")
    print_group(df[(df["Type"] == "a")], "algorithmic")

    # Group-specific statistics
    for group in groups:
        # Experiments Count
        gdf = df[df["Group"] == group]
        print_macro(f"/bbe/count/{group}", len(gdf.index))
        print_group(gdf, group)

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
        print_field("SpecBehaviorCount")
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
    print_mean("SpecBehaviorCount")
    print_mean("SpecFunctionalCount")
    print_mean("SpecExceptionCount")

    print_total("SpecCount")
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
    print_total("SpecPureCount")
    print_total("SpecAssertionCount")
    print_total("SpecAssumptionCount")
    print_total("SpecInvariantCount")
    print_total("SpecFunctionalCount")
    print_total("SpecExceptionCount")
    print_total("SpecIntermediateCount")


main()
