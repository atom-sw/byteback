package byteback.verifier.boogie;

import byteback.analysis.Scheduler;
import byteback.common.Lazy;
import byteback.encoder.boogie.BplContext;
import byteback.encoder.boogie.BplEncoder;
import byteback.verifier.common.Verifier;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import sootup.core.Project;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.types.ClassType;
import sootup.core.views.View;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaProject;
import sootup.java.core.JavaSootClass;
import sootup.java.core.language.JavaLanguage;
import sootup.java.core.views.JavaView;

public class BoogieVerifier implements Verifier<BoogieConfiguration, BoogieMessage> {

	private static final Lazy<BoogieVerifier> instance = Lazy.from(BoogieVerifier::new);

	public void generateBoogie(final OutputStream outputStream, final View<?> view, final Scheduler scheduler) {
		try (final PrintWriter writer = new PrintWriter(outputStream)) {
			final BplContext context = new BplContext(view, scheduler, writer);
			final BplEncoder encoder = new BplEncoder(context);
			encoder.encodeAll();
		}
	}

	public void initializeOutput(final OutputStream outputStream, final InputStream preludeInputStream)
			throws IOException {
		preludeInputStream.transferTo(outputStream);
	}

	public Process runBoogie(final String path) {
		final String[] command = {"boogie", path};
		final ProcessBuilder processBuilder = new ProcessBuilder(command);
		try {
			final Process process = processBuilder.start();
			return process;
		} catch (final IOException exception) {
			throw new RuntimeException("Failed to start the Boogie process", exception);
		}
	}

	public View<?> initializeView(final String path) {
		final AnalysisInputLocation<JavaSootClass> inputLocation = new JavaClassPathAnalysisInputLocation(path);
		final JavaLanguage javaLanguage = new JavaLanguage(8);
		final Project<JavaSootClass, JavaView> project = JavaProject.builder(javaLanguage)
				.addInputLocation(inputLocation).build();
		return new JavaView(project);
	}

	public Scheduler initializeScheduler(final View<?> view, final List<String> startingClassNames) {
		final Scheduler scheduler = new Scheduler(view);
		for (final String className : startingClassNames) {
			final ClassType classType = view.getIdentifierFactory().getClassType(className);
			scheduler.schedule(classType);
		}
		return scheduler;
	}

	public Stream<BoogieMessage> verify(final BoogieConfiguration configuration) {
		final String classPath = configuration.getClassPath();
		final List<String> startingClassNames = configuration.getStartingClassNames();
		final View<?> view = initializeView(classPath);
		final Scheduler scheduler = initializeScheduler(view, startingClassNames);

		final InputStream preludeInputStream = configuration.getPreludeInputStream();
		final OutputStream outputStream = configuration.getOutputStream();
		try (preludeInputStream; outputStream) {
			initializeOutput(outputStream, preludeInputStream);
			generateBoogie(outputStream, view, scheduler);
		} catch (final IOException exception) {
			throw new RuntimeException("Failed to write Boogie output", exception);
		}

		final String outputPath = configuration.getOutputPath();
		final Process boogieProcess = runBoogie(outputPath);
		final BufferedReader boogieInputReader = boogieProcess.inputReader();
		final BufferedReader boogieErrorReader = boogieProcess.errorReader();
		final ArrayList<BoogieMessage> messages = new ArrayList<>();
		String line;
		try {
			while ((line = boogieInputReader.readLine()) != null) {
				messages.add(new BoogieMessage(line));
			}
			while ((line = boogieErrorReader.readLine()) != null) {
				messages.add(new BoogieMessage(line));
			}
		} catch (final IOException exception) {
			throw new RuntimeException("Failed to parse Boogie's output", exception);
		}
		return messages.stream();
	}

	private BoogieVerifier() {
	}

	public static BoogieVerifier v() {
		return instance.get();
	}

}
