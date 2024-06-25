package byteback.tool;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.encoder.to_bpl.SceneToBplEncoder;
import byteback.syntax.scene.transformer.ConditionsTagPropagator;
import byteback.syntax.scene.transformer.ImplementationPropagator;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.ArraySizeCheckTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.DivisionByZeroCheckTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.BehaviorExprFolder;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.CastCheckTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.CheckTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.ExplicitTypeCaster;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.FrameConditionFinder;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.FrameConditionValidator;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.GuardTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.HeapLocalInserter;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.IfConditionExtractor;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.IndexCheckTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.InvariantExpander;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.InvokeCheckTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.InvokeFilter;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.NormalLoopExitSpecifier;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.NullCheckTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.OldHeapLocalInserter;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.QuantifierValueTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.ReturnEliminator;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.SpecificationExprFolder;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.StrictCheckTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.SwitchEliminator;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.ThisAssumptionInserter;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.ThrownAssumptionInserter;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.ThrownLocalInserter;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.CallStmtTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.InvokeAssigner;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.LogicConstantTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.SpecificationStmtTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.ThrownAssignmentTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.value.transformer.CallExprTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.value.transformer.ConditionalExprTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.value.transformer.GhostInliner;
import byteback.syntax.scene.type.declaration.member.method.body.value.transformer.OldExprTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.value.transformer.ThrownExprTransformer;
import byteback.syntax.scene.type.declaration.member.method.transformer.ConditionsTagger;
import byteback.syntax.scene.type.declaration.member.method.transformer.HeapReadTransformer;
import byteback.syntax.scene.type.declaration.member.method.transformer.InstanceChecksTagger;
import byteback.syntax.scene.type.declaration.member.method.transformer.ModifierTagger;
import byteback.syntax.scene.type.declaration.member.method.transformer.OldHeapReadTransformer;
import byteback.syntax.scene.type.declaration.transformer.ClassInvariantTagger;
import byteback.syntax.scene.type.declaration.transformer.HierarchyAxiomTagger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.Transform;
import soot.jimple.toolkits.scalar.LocalNameStandardizer;
import soot.options.Options;
import soot.toolkits.scalar.UnusedLocalEliminator;

@Command(name = "byteback", mixinStandardHelpOptions = true, description = "")
public class Main implements Callable<Integer> {

	@Option(names = "--help", help = true)
	private boolean help;

	@Option(names = { "-cp", "--classpath" }, description = "Conversion classpath", required = true)
	private List<Path> classPaths;

	@Option(names = { "-c", "--class" }, description = "Starting class for the conversion", required = true)
	private List<String> startingClasses;

	@Option(names = { "-p", "--prelude" }, description = "Path to the prelude file")
	private Path preludePath;

	@Option(names = { "--npe" }, description = "Models implicit NullPointerExceptions")
	public boolean transformNullCheck = false;

	@Option(names = { "--iobe" }, description = "Models implicit IndexOutOfBoundsExceptions")
	public boolean transformArrayCheck = false;

	@Option(names = { "--cce" }, description = "Models implicit ClassCastExceptions")
	public boolean transformClassCastCheck = false;

	@Option(names = { "--nas" }, description = "Models implicit NegativeArraySizeExceptions")
	public boolean transformNegativeArraySizeCheck = false;

	@Option(names = { "--dbz" }, description = "Models implicit DivisionByZeroExceptions")
	public boolean transformDivisionByZero = false;

	@Option(names = { "--strict" }, description = "Enforce the absence of implicit exceptions")
	public boolean transformStrictCheck = false;

	@Option(names = { "-o", "--output" }, description = "Path to the output verification conditions")
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

	private CheckTransformer strictifyCheckTransformer(final CheckTransformer checkTransformer) {
		if (transformStrictCheck) {
			return new StrictCheckTransformer(checkTransformer);
		} else {
			return checkTransformer;
		}
	}

	private final String[] ghostClasses = new String[] {
			"byteback.specification.ghost.ObjectSpec",
			"byteback.specification.ghost.ExceptionSpec",
			"byteback.specification.ghost.InvokeDynamicSpec",
			"byteback.specification.ghost.KotlinIntrinsicsSpec"
	};

	public Integer call() throws Exception {
		final long startTime = System.currentTimeMillis();

		// We will add the classes using Options.v().classes, instead of using the Soot
		// main.
		Options.v().set_weak_map_structures(true);
		final List<String> startingClasses = getStartingClasses();
		startingClasses.addAll(List.of(ghostClasses));
		Options.v().classes().addAll(startingClasses);

		// For now ByteBack will not produce any output. Especially since we still
		// haven't defined how Vimp should be
		// compiled.
		Options.v().set_output_format(Options.output_format_none);

		// By default, Soot includes the $CLASSPATH env variable. To this we append the
		// classpath specified by the
		// user.
		Options.v().set_prepend_classpath(true);
		Options.v().set_soot_classpath(formatClassPaths());

		// Keeping the original names makes debugging the output simpler (though it is
		// not strictly necessary).
		Options.v().setPhaseOption("jb", "use-original-names:true");

		// We will put most of the transformations needed before the conversion to the
		// IVL in this pack.
		Options.v().setPhaseOption("jtp", "enabled:true");

		final Pack jtpPack = PackManager.v().getPack("jtp");

		final Scene scene = Scene.v();

		// - Jimple transformations
		jtpPack.add(new Transform("jtp.ivf", InvokeFilter.v()));
		jtpPack.add(new Transform("jtp.gor", GhostInliner.v()));

		// - Vimp transformations
		// Initial structural transformations
		jtpPack.add(new Transform("jtp.swe", SwitchEliminator.v()));
		jtpPack.add(new Transform("jtp.rte", ReturnEliminator.v()));
		jtpPack.add(new Transform("jtp.i2j", IfConditionExtractor.v()));

		// Start of \tr[exp]
		// Introduce the new Vimp expression types
		jtpPack.add(new Transform("jtp.vvt", LogicConstantTransformer.v()));
		jtpPack.add(new Transform("jtp.etc", ExplicitTypeCaster.v()));
		jtpPack.add(new Transform("jtp.qft", QuantifierValueTransformer.v()));
		jtpPack.add(new Transform("jtp.oet", OldExprTransformer.v()));
		jtpPack.add(new Transform("jtp.tet", ThrownExprTransformer.v()));
		jtpPack.add(new Transform("jtp.tat", ThrownAssignmentTransformer.v()));
		jtpPack.add(new Transform("jtp.cot", ConditionalExprTransformer.v()));
		jtpPack.add(new Transform("jtp.cet", CallExprTransformer.v()));

		// - Transformations targeting behavioral methods
		jtpPack.add(new Transform("jtp.bgg", BehaviorExprFolder.v()));
		// End of \tr[exp]

		// - Transformations targeting procedural methods
		jtpPack.add(new Transform("jtp.tai", ThisAssumptionInserter.v()));

		// Start of \tr[stm]
		// Create the specification statements and expressions
		jtpPack.add(new Transform("jtp.vut", SpecificationStmtTransformer.v()));
		jtpPack.add(new Transform("jtp.vgg", SpecificationExprFolder.v()));
		jtpPack.add(new Transform("jtp.ias", InvokeAssigner.v()));
		// End of \tr[stm]

		// Start of \tr[exc]
		// - Transformations of the exceptional control flow
		// Create explicit checks for implicit exceptions
		jtpPack.add(new Transform("jtp.cai", ThrownAssumptionInserter.v()));

		if (transformArrayCheck) {
			scene.addBasicClass("java.lang.IndexOutOfBoundsException", SootClass.SIGNATURES);
			jtpPack.add(new Transform("jtp.ict",
					strictifyCheckTransformer(new IndexCheckTransformer(scene))));
		}

		if (transformNullCheck) {
			scene.addBasicClass("java.lang.NullPointerException", SootClass.SIGNATURES);
			jtpPack.add(new Transform("jtp.nct",
					strictifyCheckTransformer(new NullCheckTransformer(scene))));
		}

		if (transformClassCastCheck) {
			scene.addBasicClass("java.lang.ClassCastException", SootClass.SIGNATURES);
			jtpPack.add(new Transform("jtp.clct",
					strictifyCheckTransformer(new CastCheckTransformer(scene))));
		}

		if (transformNegativeArraySizeCheck) {
			scene.addBasicClass("java.lang.NegativeArraySizeException", SootClass.SIGNATURES);
			jtpPack.add(new Transform("jtp.asct",
					strictifyCheckTransformer(new ArraySizeCheckTransformer(scene))));
		}

		if (transformDivisionByZero) {
			scene.addBasicClass("java.lang.ArithmeticException", SootClass.SIGNATURES);
			jtpPack.add(new Transform("jtp.dbzt",
					strictifyCheckTransformer(new DivisionByZeroCheckTransformer(scene))));
		}

		jtpPack.add(new Transform("jtp.eit", NormalLoopExitSpecifier.v()));
		jtpPack.add(new Transform("jtp.cct", InvokeCheckTransformer.v()));
		jtpPack.add(new Transform("jtp.cst", CallStmtTransformer.v()));
		jtpPack.add(new Transform("jtp.gat", GuardTransformer.v()));
		// End of \tr[exc]

		// Start of \tr[loop]
		jtpPack.add(new Transform("jtp.ine", InvariantExpander.v()));
		// End of \tr[loop]

		// Cleanup the output
		jtpPack.add(new Transform("jtp.ule2", UnusedLocalEliminator.v()));
		jtpPack.add(new Transform("jtp.lns", LocalNameStandardizer.v()));

		// Assign local specification
		jtpPack.add(new Transform("jtp.fif", FrameConditionFinder.v()));
		jtpPack.add(new Transform("jtp.fcv", FrameConditionValidator.v()));
		jtpPack.add(new Transform("jtp.tli", ThrownLocalInserter.v()));
		jtpPack.add(new Transform("jtp.ohli", OldHeapLocalInserter.v()));
		jtpPack.add(new Transform("jtp.hli", HeapLocalInserter.v()));

		scene.loadBasicClasses();
		scene.loadNecessaryClasses();

		ModifierTagger.v().transform();
		ImplementationPropagator.v().transform();

		PackManager.v().runPacks();

		ConditionsTagger.v().transform();
		ClassInvariantTagger.v().transform();
		HeapReadTransformer.v().transform();
		OldHeapReadTransformer.v().transform();
		ConditionsTagPropagator.v().transform();
		InstanceChecksTagger.v().transform();
		HierarchyAxiomTagger.v().transform();

		try (final Printer printer = new Printer(outputPath.toString())) {
			new SceneToBplEncoder(printer).encodeScene(scene);
		}

		final long endTime = System.currentTimeMillis();

		System.out.println("Conversion completed in " + (endTime - startTime) + "ms");
		System.out.println();

		return 0;
	}

	public static void main(final String... args) throws Exception {
		// Thread.sleep(10000L); // For attaching visualvm.
		int exitCode = new CommandLine(new Main()).execute(args);
		System.exit(exitCode);
	}

}
