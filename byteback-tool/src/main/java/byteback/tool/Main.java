package byteback.tool;

import byteback.analysis.global.vimp.transformer.HierarchyAxiomTagger;
import byteback.analysis.global.vimp.transformer.PostconditionsPropagator;
import byteback.analysis.global.vimp.transformer.PreconditionsPropagator;
import byteback.analysis.local.jimple.transformer.body.InvokeFilter;
import byteback.analysis.local.jimple.transformer.body.SwitchEliminator;
import byteback.analysis.local.jimple.transformer.value.DynamicInvokeToStaticTransformer;
import byteback.analysis.local.vimp.transformer.body.*;
import byteback.analysis.local.vimp.transformer.unit.LogicConstantTransformer;
import byteback.analysis.local.vimp.transformer.unit.SpecificationStmtTransformer;
import byteback.analysis.local.vimp.transformer.value.ConditionalExprTransformer;
import byteback.analysis.local.vimp.transformer.value.OldExprTransformer;
import byteback.analysis.local.vimp.transformer.value.QuantifierValueTransformer;
import byteback.analysis.local.vimp.transformer.value.ThrownExprTransformer;
import byteback.converter.boogie.scene.SceneToBplEncoder;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import soot.*;
import soot.jimple.toolkits.scalar.LocalNameStandardizer;
import soot.options.Options;
import soot.toolkits.scalar.UnusedLocalEliminator;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "byteback", mixinStandardHelpOptions = true, description = "")
public class Main implements Callable<Integer> {

    @Option(names = "--help", help = true)
    private boolean help;

    @Option(names = {"-cp", "--classpath"}, description = "Conversion classpath", required = true)
    private List<Path> classPaths;

    @Option(names = {"-c", "--class"}, description = "Starting class for the conversion", required = true)
    private List<String> startingClasses;

    @Option(names = {"-p", "--prelude"}, description = "Path to the prelude file")
    private Path preludePath;

    @Option(names = {"--npe"}, description = "Models implicit NullPointerExceptions")
    public boolean transformNullCheck = false;

    @Option(names = {"--iobe"}, description = "Models implicit IndexOutOfBoundsExceptions")
    public boolean transformArrayCheck = false;

    @Option(names = {"-o", "--output"}, description = "Path to the output verification conditions")
    private Path outputPath;

    public boolean getHelp() {
        return help;
    }

    public List<Path> getClassPaths() {
        return classPaths;
    }

    public String formatClassPaths() {
        StringBuilder finalClassPath = new StringBuilder();

        for (final Path cp : getClassPaths()) {
            finalClassPath.append(File.pathSeparator).append(cp);
        }

        return finalClassPath.toString();
    }

    public List<String> getStartingClasses() {
        return startingClasses;
    }

    public Path getPreludePath() {
        return preludePath;
    }

    public Path getOutputPath() {
        return outputPath;
    }

    public boolean getTransformNullCheck() {
        return transformNullCheck;
    }

    public boolean getTransformArrayCheck() {
        return transformArrayCheck;
    }

    public Integer call() throws Exception {
        final long startTime = System.currentTimeMillis();

        // We will add the classes using Options.v().classes, instead of using the Soot main.
        Options.v().set_unfriendly_mode(true);
        Options.v().classes().addAll(getStartingClasses());

        // For now ByteBack will not produce any output. Especially since we still haven't defined how Vimp should be
        // compiled.
        Options.v().set_output_format(Options.output_format_none);

        // By default, Soot includes the $CLASSPATH env variable. To this we append the classpath specified by the
        // user.
        Options.v().set_prepend_classpath(true);
        Options.v().set_soot_classpath(formatClassPaths());

        // Keeping the original names makes debugging the output simpler (though it is not strictly necessary).
        Options.v().setPhaseOption("jb", "use-original-names:true");

        // We will put most of the transformations needed before the conversion to the IVL in this pack.
        Options.v().setPhaseOption("jtp", "enabled:true");

        final Pack jtpPack = PackManager.v().getPack("jtp");

        // - Jimple transformations
        jtpPack.add(new Transform("jtp.ivf", InvokeFilter.v()));
        jtpPack.add(new Transform("jtp.dir", DynamicInvokeToStaticTransformer.v()));

        // - Vimp transformations
        // Basic flow transformations
        jtpPack.add(new Transform("jtp.swe", SwitchEliminator.v()));
        jtpPack.add(new Transform("jtp.rel", ReturnEliminator.v()));

        // Removes subexpressions in if conditions
        jtpPack.add(new Transform("jtp.i2j", IfConditionExtractor.v()));

        // First introduce the new Vimp expression types
        jtpPack.add(new Transform("jtp.vvt", LogicConstantTransformer.v()));
        jtpPack.add(new Transform("jtp.etc", ExplicitTypeCaster.v()));
        jtpPack.add(new Transform("jtp.qft", QuantifierValueTransformer.v()));
        jtpPack.add(new Transform("jtp.oet", OldExprTransformer.v()));
        jtpPack.add(new Transform("jtp.tet", ThrownExprTransformer.v()));
        jtpPack.add(new Transform("jtp.cot", ConditionalExprTransformer.v()));
        jtpPack.add(new Transform("jtp.cet", CallExprTransformer.v()));

        // - Transformations targeting behavioral methods
        jtpPack.add(new Transform("jtp.btg", BehaviorMethodValidator.v()));
        jtpPack.add(new Transform("jtp.bgg", BehaviorExprFolder.v()));

        // - Transformations targeting procedural methods
        // Create the specification statements and expressions
        jtpPack.add(new Transform("jtp.vut", SpecificationStmtTransformer.v()));
        jtpPack.add(new Transform("jtp.vgg", SpecificationExprFolder.v()));

        // Create initial assumptions.
        jtpPack.add(new Transform("jtp.tai", ThisAssumptionInserter.v()));
        jtpPack.add(new Transform("jtp.cai", CaughtAssumptionInserter.v()));

        // - Transformations of the exceptional control flow
        // Create explicit checks for implicit exceptions
        if (getTransformArrayCheck()) {
            jtpPack.add(new Transform("jtp.ict", IndexCheckTransformer.v()));
        }

        if (getTransformNullCheck()) {
            jtpPack.add(new Transform("jtp.nct", NullCheckTransformer.v()));
        }

        jtpPack.add(new Transform("jtp.eit", NormalLoopExitSpecifier.v()));
        jtpPack.add(new Transform("jtp.cct", InvokeCheckTransformer.v()));
        jtpPack.add(new Transform("jtp.gat", GuardTransformer.v()));
        jtpPack.add(new Transform("jtp.ine", InvariantExpander.v()));

        // Cleanup the output
        jtpPack.add(new Transform("jtp.ule", UnusedLocalEliminator.v()));
        jtpPack.add(new Transform("jtp.lns", LocalNameStandardizer.v()));

        // Assign local specification
        jtpPack.add(new Transform("jtp.lif", FrameConditionFinder.v()));
        jtpPack.add(new Transform("jtp.pcf", PreconditionsFinder.v()));
        jtpPack.add(new Transform("jtp.psf", PostconditionsFinder.v()));

        Scene.v().loadBasicClasses();
        Scene.v().loadNecessaryClasses();
        PackManager.v().runPacks();

        PreconditionsPropagator.v().transformScene(Scene.v());
        PostconditionsPropagator.v().transformScene(Scene.v());
        HierarchyAxiomTagger.v().transformScene(Scene.v());
        new SceneToBplEncoder(new PrintWriter(getOutputPath().toString())).transformScene(Scene.v());

        final long endTime = System.currentTimeMillis();

        System.out.println("Precise time (ms): ");
        System.out.println(endTime - startTime);

        return 0;
    }

    public static void main(final String... args) throws Exception {
        // Thread.sleep(10000L); // For attaching the visualvm
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

}
