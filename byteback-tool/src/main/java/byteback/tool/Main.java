package byteback.tool;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.encoder.to_bpl.SceneToBplEncoder;
import byteback.syntax.scene.transformer.ConditionsTagPropagator;
import byteback.syntax.scene.transformer.ImplementationPropagator;
import byteback.syntax.scene.transformer.MethodResolver;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.ArraySizeCheckTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.BehaviorExprFolder;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.CastCheckTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.CheckTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.DivisionByZeroCheckTransformer;
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
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.TwoStateTagMarker;
import byteback.syntax.scene.type.declaration.member.method.transformer.ConditionsTagger;
import byteback.syntax.scene.type.declaration.member.method.transformer.HeapReadTransformer;
import byteback.syntax.scene.type.declaration.member.method.transformer.ModifierTagger;
import byteback.syntax.scene.type.declaration.member.method.transformer.OldHeapReadTransformer;
import byteback.syntax.scene.type.declaration.transformer.ClassInvariantExpander;
import byteback.syntax.scene.type.declaration.transformer.ClassInvariantTagger;
import byteback.syntax.scene.type.declaration.transformer.HierarchyAxiomTagger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import soot.Body;
import soot.LocalGenerator;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.javaToJimple.DefaultLocalGenerator;
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
	public boolean transformDivisionByZeroCheck = false;

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
		final Options options = Options.v();
		final Scene scene = Scene.v();

		// We will add the classes using Options.v().classes, instead of using the Soot
		// main.
		final List<String> startingClasses = getStartingClasses();
		startingClasses.addAll(List.of(ghostClasses));

		options.set_weak_map_structures(true);
		options.classes().addAll(startingClasses);

		// By default, Soot includes the $CLASSPATH env variable. To this we append the
		// classpath specified by the user.
		options.set_prepend_classpath(true);
		options.set_soot_classpath(formatClassPaths());
		options.set_output_format(Options.output_format_none);

		// Keeping the original names makes debugging the output simpler (though it is
		// not strictly necessary).
		options.setPhaseOption("jb", "use-original-names:true");

		// Transformations
		final var checkTransformers = new ArrayList<CheckTransformer>();

		if (transformNullCheck) {
			scene.addBasicClass("java.lang.NullPointerException", SootClass.SIGNATURES);
			checkTransformers.add(strictifyCheckTransformer(new NullCheckTransformer(scene)));
		}

		if (transformArrayCheck) {
			scene.addBasicClass("java.lang.IndexOutOfBoundsException", SootClass.SIGNATURES);
			checkTransformers.add(strictifyCheckTransformer(new IndexCheckTransformer(scene)));
		}

		if (transformDivisionByZeroCheck) {
			scene.addBasicClass("java.lang.ArithmeticException", SootClass.SIGNATURES);
			checkTransformers.add(strictifyCheckTransformer(new DivisionByZeroCheckTransformer(scene)));
		}

		if (transformNegativeArraySizeCheck) {
			scene.addBasicClass("java.lang.NegativeArraySizeException", SootClass.SIGNATURES);
			checkTransformers.add(strictifyCheckTransformer(new ArraySizeCheckTransformer(scene)));
		}

		if (transformClassCastCheck) {
			scene.addBasicClass("java.lang.ClassCastException", SootClass.SIGNATURES);
			checkTransformers.add(strictifyCheckTransformer(new CastCheckTransformer(scene)));
		}

		scene.loadBasicClasses();
		scene.loadNecessaryClasses();
		PackManager.v().runPacks();

		MethodResolver.v().transformScene(scene);
		ModifierTagger.v().transformScene(scene);
		ConditionsTagger.v().transformScene(scene);
		ClassInvariantTagger.v().transformScene(scene);
		ImplementationPropagator.v().transformScene(scene);

		for (final SootClass sootClass : scene.getClasses()) {
			if (sootClass.resolvingLevel() <= SootClass.SIGNATURES) {
				continue;
			}

			for (final SootMethod sootMethod : sootClass.getMethods()) {
				if (sootMethod.isAbstract() || !sootMethod.hasActiveBody()) {
					continue;
				}

				final Body body = sootMethod.getActiveBody();
				new LogicConstantTransformer(sootMethod.getReturnType()).transformBody(body);

				InvokeFilter.v().transformBody(body);
				GhostInliner.v().transformBody(body);

				if (!BehaviorTagMarker.v().hasTag(sootMethod)) {
					SwitchEliminator.v().transformBody(body);
					ReturnEliminator.v().transformBody(body);
					IfConditionExtractor.v().transformBody(body);
				}

				ExplicitTypeCaster.v().transformBody(body);
				CallExprTransformer.v().transformBody(body);

				QuantifierValueTransformer.v().transformBody(body);
				OldExprTransformer.v().transformBody(body);
				ThrownExprTransformer.v().transformBody(body);
				ConditionalExprTransformer.v().transformBody(body);

				if (!BehaviorTagMarker.v().hasTag(sootMethod)) {
					final var localGenerator = new DefaultLocalGenerator(body);
					ThisAssumptionInserter.v().transformBody(body);
					ThrownAssumptionInserter.v().transformBody(body);
					ThrownAssignmentTransformer.v().transformBody(body);
					SpecificationStmtTransformer.v().transformBody(body);
					SpecificationExprFolder.v().transformBody(body);
					new InvokeAssigner(localGenerator)
						.transformBody(body);

					for (final CheckTransformer checkTransformer : checkTransformers) {
						checkTransformer.transformBody(body);
					}

					NormalLoopExitSpecifier.v().transformBody(body);
					InvokeCheckTransformer.v().transformBody(body);
					CallStmtTransformer.v().transformBody(body);
					GuardTransformer.v().transformBody(body);
					InvariantExpander.v().transformBody(body);
				} else {
					BehaviorExprFolder.v().transformBody(body);
				}

				if (BehaviorTagMarker.v().hasTag(sootMethod)) {
					if (TwoStateTagMarker.v().hasTag(sootMethod)) {
						OldHeapLocalInserter.v().transformBody(body);
					}

					ThrownLocalInserter.v().transformBody(body);
					HeapLocalInserter.v().transformBody(body);
				} else {
					FrameConditionFinder.v().transformBody(body);
					FrameConditionValidator.v().transformBody(body);
				}

				// Cleanup
				UnusedLocalEliminator.v().transform(body);
				LocalNameStandardizer.v().transform(body);
			}
		}

		ConditionsTagPropagator.v().transformScene(scene);
		ClassInvariantExpander.v().transformScene(scene);

		final var hierarchyAxiomTagger = new HierarchyAxiomTagger(scene.getActiveHierarchy());

		for (final SootClass sootClass : scene.getClasses()) {
			HeapReadTransformer.v().transformClass(sootClass);
			OldHeapReadTransformer.v().transformClass(sootClass);
			hierarchyAxiomTagger.transformClass(sootClass);;
		}

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
