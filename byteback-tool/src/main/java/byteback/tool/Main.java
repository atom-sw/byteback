package byteback.tool;

import byteback.analysis.body.jimple.transformer.*;
import byteback.analysis.body.vimp.transformer.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import soot.*;
import soot.options.Options;
import soot.toolkits.scalar.UnusedLocalEliminator;

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

		jtpPack.add(new Transform("jtp.ii", InvokeFilter.v()));
		jtpPack.add(new Transform("jtp.dir", DynamicInvokeToStaticResolver.v()));
		jtpPack.add(new Transform("jtp.vvt", VimpValueBodyTransformer.v()));
		jtpPack.add(new Transform("jtp.cet", CallExprTransformer.v()));
		jtpPack.add(new Transform("jtp.oet", OldExprTransformer.v()));
		jtpPack.add(new Transform("jtp.qft", QuantifierValueTransformer.v()));
		jtpPack.add(new Transform("jtp.cot", ConditionalExprTransformer.v()));
		jtpPack.add(new Transform("jtp.vgg", SpecificationExprFolder.v()));
		jtpPack.add(new Transform("jtp.btg", BehaviorMethodValidator.v()));
		jtpPack.add(new Transform("jtp.bgg", BehaviorExprFolder.v()));
		jtpPack.add(new Transform("jtp.eit", NormalLoopExitSpecifier.v()));
		jtpPack.add(new Transform("jtp.ict", IndexCheckTransformer.v()));
		jtpPack.add(new Transform("jtp.nct", NullCheckTransformer.v()));
		jtpPack.add(new Transform("jtp.cct", InvokeCheckTransformer.v()));
		jtpPack.add(new Transform("jtp.tai", ThisAssumptionInserter.v()));
		jtpPack.add(new Transform("jtp.cai", CaughtAssumptionInserter.v()));
		jtpPack.add(new Transform("jtp.gt", GuardTransformer.v()));
		jtpPack.add(new Transform("jtp.ie", InvariantExpander.v()));
		jtpPack.add(new Transform("jtp.ule", UnusedLocalEliminator.v()));
		jtpPack.add(new Transform("jtp.rel", ReturnEliminator.v()));
		jtpPack.add(new Transform("jtp.fcf", FrameConditionFinder.v()));

		//jtpPack.add((new Transform("jtp.print", new BodyTransformer() {

		//	@Override
		//	protected void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
		//		System.out.println(body);
		//	}

		//})));

		soot.Main.v().run(new String[]{});

		final long endTime = System.currentTimeMillis();

		System.out.println("Precise time (ms): ");
		System.out.println(endTime - startTime);

		return 0;
	}

	public static void main(final String... args) throws Exception {
		// Thread.sleep(10000L); For attaching the visualvm
		int exitCode = new CommandLine(new Main()).execute(args);
		System.exit(exitCode);
	}

}
