package byteback.client;

import byteback.common.Lazy;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

public class Configuration implements byteback.verifier.boogie.BoogieConfiguration {

	private static final Lazy<Configuration> instance = Lazy.from(Configuration::new);

	public static String concatenateClassPaths(final List<String> classPaths) {
		String finalClassPath = "";
		final Iterator<String> classPathIterator = classPaths.iterator();
		while (classPathIterator.hasNext()) {
			final String classPath = classPathIterator.next();
			finalClassPath += classPath.toString();
			if (!classPathIterator.hasNext()) {
				finalClassPath += File.pathSeparator;
			}
		}
		return finalClassPath;
	}

	private Configuration() {
	}

	public static Configuration getInstance() {
		return instance.get();
	}

	@Parameter(names = "--help", help = true)
	private boolean help;

	@Parameter(names = {"-cp", "--classpath"}, description = "Classpath to be converted", required = true)
	private List<String> classPaths;

	@Parameter(names = {"-c", "--class"}, description = "Starting class for the conversion", required = true)
	private List<String> startingClassNames;

	@Parameter(names = {"-p", "--prelude"}, description = "Path to the prelude file")
	private String preludePath;

	@Parameter(names = {"--npe"}, description = "Models implicit NullPointerExceptions")
	private boolean transformNullCheck = false;

	@Parameter(names = {"--iobe"}, description = "Models implicit IndexOutOfBoundsExceptions")
	private boolean transformArrayCheck = false;

	@Parameter(names = {"-o", "--output"}, description = "Path to the output verification conditions", required = true)
	private String outputPath;

	private JCommander jCommander;

	public boolean getHelp() {
		return help;
	}

	public List<String> getClassPaths() {
		return classPaths;
	}

	public String getClassPath() {
		return concatenateClassPaths(classPaths);
	}

	public List<String> getStartingClassNames() {
		return startingClassNames;
	}

	public InputStream getPreludeInputStream() {
		if (preludePath != null) {
			try {
				final File preludeFile = new File(preludePath);
				return new FileInputStream(preludeFile);
			} catch (final IOException exception) {
				throw new RuntimeException("Failed to open prelude file", exception);
			}
		} else {
			return getClass().getResourceAsStream("/boogie/BytebackPrelude.bpl");
		}
	}

	public String getOutputPath() {
		return outputPath;
	}

	public OutputStream getOutputStream() {
		try {
			final File outputFile = new File(outputPath);
			return new FileOutputStream(outputFile);
		} catch (final IOException exception) {
			throw new RuntimeException("Failed to open output file", exception);
		}
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
