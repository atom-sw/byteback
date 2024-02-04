package byteback.cli;

import com.beust.jcommander.ParameterException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sootup.core.cache.provider.LRUCacheProvider;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaProject;
import sootup.java.core.JavaSootClass;
import sootup.java.core.language.JavaLanguage;
import sootup.java.core.views.JavaView;

public class Main {

	public static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void convert(final byteback.cli.Configuration configuration) {
		final PrintStream output;

		if (configuration.getOutputPath() != null) {
			final File file = configuration.getOutputPath().toFile();
			try {
				file.createNewFile();
				output = new PrintStream(new FileOutputStream(file));
			} catch (final IOException exception) {
				log.error("Cannot output program to file {}", file.getPath());
				throw new RuntimeException("Unable to produce output");
			}
		} else {
			output = System.out;
		}
	}

	public static String concatenateClassPaths(final List<Path> classPaths) {
		String finalClassPath = "";
		final Iterator<Path> classPathIterator = classPaths.iterator();
		while (classPathIterator.hasNext()) {
			final Path classPath = classPathIterator.next();
			if (!classPathIterator.hasNext()) {
				finalClassPath += File.pathSeparator;
			}
			finalClassPath += classPath.toString();
		}
		return finalClassPath;
	}

	public static JavaProject initializeProject(final byteback.cli.Configuration configuration) {
		final List<String> startingClassNames = configuration.getStartingClasses();
		final List<Path> classPaths = configuration.getClassPaths();
		final String classPath = concatenateClassPaths(classPaths);
		final Path preludePath = configuration.getPreludePath();
		final JavaLanguage javaLanguage = new JavaLanguage(22);
		final AnalysisInputLocation<JavaSootClass> inputLocation = 
			new JavaClassPathAnalysisInputLocation(classPath);
		return JavaProject.builder(javaLanguage).addInputLocation(inputLocation).build();
	}

	public static void main(final String[] args) {
		final byteback.cli.Configuration config = byteback.cli.Configuration.v();
		final long totalStart = System.currentTimeMillis();

		try {
			config.parse(args);

			if (config.getHelp()) {
				config.getJCommander().usage();
			} else {
				final JavaProject project = initializeProject(config);
				final JavaView view = project.createView(new LRUCacheProvider<>(50));
				final long conversionStart = System.currentTimeMillis();
				log.info("Converting classes");
				convert(config);
				final long endTime = System.currentTimeMillis();
				final long totalTime = endTime - totalStart;
				final long conversionTime = endTime - conversionStart;
				log.info("Conversion completed in {}ms, total time {}ms", conversionTime, totalTime);
			}
		} catch (final ParameterException exception) {
			log.error("Error while parsing program arguments: {}", exception.getMessage());
			exception.getJCommander().usage();
		}
	}

}
