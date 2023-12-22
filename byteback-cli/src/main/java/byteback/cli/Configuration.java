package byteback.cli;

import byteback.util.Lazy;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.nio.file.Path;
import java.util.List;

public class Configuration {

	private static final Lazy<Configuration> instance = Lazy.from(Configuration::new);

	private Configuration() {
	}

	public static Configuration v() {
		return instance.get();
	}

	@Parameter(names = "--help", help = true)
	private boolean help;

	@Parameter(names = {"-cp", "--classpath"}, description = "Classpath to be converted", required = true)
	private List<Path> classPaths;

	@Parameter(names = {"-c", "--class"}, description = "Starting class for the conversion", required = true)
	private List<String> startingClasses;

	@Parameter(names = {"-p", "--prelude"}, description = "Path to the prelude file")
	private Path preludePath;

	@Parameter(names = {"-o", "--output"}, description = "Path to the output verification conditions")
	private Path outputPath;

	private JCommander jCommander;

	@Parameter(names = {"--nct"}, description = "Injects explicit null checks")
	private boolean transformNullCheck = false;

	@Parameter(names = {"--act"}, description = "Injects explicit array checks")
	private boolean transformArrayCheck = false;

	public boolean getHelp() {
		return help;
	}

	public List<Path> getClassPaths() {
		return classPaths;
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

	public JCommander getJCommander() {
		return jCommander;
	}

	public void parse(final String[] args) {
		jCommander = JCommander.newBuilder().addObject(this).build();
		jCommander.parse(args);
	}

	public boolean getTransformNullCheck() {
		return transformNullCheck;
	}

	public boolean getTransformArrayCheck() {
		return transformArrayCheck;
	}

}
