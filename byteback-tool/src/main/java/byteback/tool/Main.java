package byteback.tool;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.encoder.to_bpl.SceneToBplEncoder;
import byteback.syntax.scene.transformer.ImplementationPropagator;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.*;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.InvokeAssigner;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.ThrownAssignmentTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.value.transformer.*;
import byteback.syntax.scene.type.declaration.member.method.transformer.*;
import byteback.syntax.scene.type.declaration.transformer.HierarchyAxiomTagger;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.LogicConstantTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.SpecificationStmtTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.QuantifierValueTransformer;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import soot.*;
import soot.jimple.toolkits.scalar.LocalNameStandardizer;
import soot.options.Options;
import soot.toolkits.scalar.UnusedLocalEliminator;

import java.io.File;
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

    private final String[] pluginClasses = new String[] {
            "byteback.specification.plugin.ObjectSpec",
            "byteback.specification.plugin.ExceptionSpec"
    };

    public Integer call() throws Exception {
        final long startTime = System.currentTimeMillis();

        // We will add the classes using Options.v().classes, instead of using the Soot main.
        Options.v().set_unfriendly_mode(true);
        final List<String> startingClasses = getStartingClasses();
        startingClasses.addAll(List.of(pluginClasses));
        Options.v().classes().addAll(startingClasses);

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

        final Scene scene = Scene.v();

        // - Jimple transformations
        jtpPack.add(new Transform("jtp.ivf", InvokeFilter.v()));

        // - Vimp transformations
        // Basic flow transformations
        jtpPack.add(new Transform("jtp.swe", SwitchEliminator.v()));
        jtpPack.add(new Transform("jtp.rel", ReturnEliminator.v()));

        // Removes subexpressions in if conditions
        jtpPack.add(new Transform("jtp.i2j", IfConditionExtractor.v()));

        // Introduce the new Vimp expression types
        jtpPack.add(new Transform("jtp.vvt", LogicConstantTransformer.v()));
        jtpPack.add(new Transform("jtp.etc", ExplicitTypeCaster.v()));
        jtpPack.add(new Transform("jtp.qft", QuantifierValueTransformer.v()));
        jtpPack.add(new Transform("jtp.oet", OldExprTransformer.v()));
        jtpPack.add(new Transform("jtp.tet", ThrownExprTransformer.v()));
        jtpPack.add(new Transform("jtp.tat", ThrownAssignmentTransformer.v()));
        jtpPack.add(new Transform("jtp.cot", ConditionalExprTransformer.v()));
        jtpPack.add(new Transform("jtp.cet", CallExprTransformer.v()));
        jtpPack.add(new Transform("jtp.ias", InvokeAssigner.v()));

        // - Transformations targeting behavioral methods
        jtpPack.add(new Transform("jtp.bgg", BehaviorExprFolder.v()));
        jtpPack.add(new Transform("jtp.ule1", UnusedLocalEliminator.v()));
        jtpPack.add(new Transform("jtp.btg", BehaviorBodyValidator.v()));

        // - Transformations targeting procedural methods
        // Create the specification statements and expressions
        jtpPack.add(new Transform("jtp.vut", SpecificationStmtTransformer.v()));
        jtpPack.add(new Transform("jtp.vgg", SpecificationExprFolder.v()));
        jtpPack.add(new Transform("jtp.plf", ParameterLocalFinalizer.v()));
        jtpPack.add(new Transform("jtp.oett", OldExprTightener.v()));

        // Generate initial assumptions.
        jtpPack.add(new Transform("jtp.tai", ThisAssumptionInserter.v()));
        jtpPack.add(new Transform("jtp.cai", CaughtAssumptionInserter.v()));

        // - Transformations of the exceptional control flow
        // Create explicit checks for implicit exceptions
        if (getTransformArrayCheck()) {
            scene.addBasicClass("java.lang.IndexOutOfBoundsException", SootClass.SIGNATURES);
            jtpPack.add(new Transform("jtp.ict", new IndexCheckTransformer(scene)));
        }

        if (getTransformNullCheck()) {
            scene.addBasicClass("java.lang.NullPointerException", SootClass.SIGNATURES);
            jtpPack.add(new Transform("jtp.nct", new NullCheckTransformer(scene)));
        }

        jtpPack.add(new Transform("jtp.eit", NormalLoopExitSpecifier.v()));
        jtpPack.add(new Transform("jtp.cct", InvokeCheckTransformer.v()));
        jtpPack.add(new Transform("jtp.gat", GuardTransformer.v()));
        jtpPack.add(new Transform("jtp.ine", InvariantExpander.v()));

        // Cleanup the output
        jtpPack.add(new Transform("jtp.ule2", UnusedLocalEliminator.v()));
        jtpPack.add(new Transform("jtp.lns", LocalNameStandardizer.v()));

        // Assign local specification
        jtpPack.add(new Transform("jtp.lif", FrameConditionFinder.v()));

        scene.loadBasicClasses();
        scene.loadNecessaryClasses();

        ModifierTagger.v().transform();
        ImplementationPropagator.v().transform();

        PackManager.v().runBodyPacks();

        ParameterLocalsTagger.v().transform();
        ConditionsTagger.v().transform();
        PreconditionsPropagator.v().transform();
        PostconditionsPropagator.v().transform();
        HierarchyAxiomTagger.v().transform();

        try (final Printer printer = new Printer(outputPath.toString())) {
            new SceneToBplEncoder(printer).encodeScene(scene);
        }

        final long endTime = System.currentTimeMillis();

        System.out.println("Precise time (ms): ");
        System.out.println(endTime - startTime);

        return 0;
    }

    public static void main(final String... args) throws Exception {
        // Thread.sleep(10000L); // For attaching visualvm.
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

}
