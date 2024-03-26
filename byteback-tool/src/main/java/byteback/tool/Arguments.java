
package byteback.tool;

import byteback.common.function.Lazy;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class Arguments {

    private static final Lazy<Arguments> instance = Lazy.from(Arguments::new);

    private Arguments() {
    }

    public static Arguments v() {
        return instance.get();
    }

    @Parameter(names = "--help", help = true)
    private boolean help;

    @Parameter(names = {"-cp", "--classpath"}, description = "Conversion classpath", required = true)
    private List<Path> classPaths;

    @Parameter(names = {"-c", "--class"}, description = "Starting class for the conversion", required = true)
    private List<String> startingClasses;

    @Parameter(names = {"-p", "--prelude"}, description = "Path to the prelude file")
    private Path preludePath;

    @Parameter(names = {"--npe"}, description = "Models implicit NullPointerExceptions")
    public boolean transformNullCheck = false;

    @Parameter(names = {"--iobe"}, description = "Models implicit IndexOutOfBoundsExceptions")
    public boolean transformArrayCheck = false;

    @Parameter(names = {"-o", "--output"}, description = "Path to the output verification conditions")
    private Path outputPath;

    private JCommander jCommander;

    public boolean getHelp() {
        return help;
    }

    public List<Path> getClassPaths() {
        return classPaths;
    }


    public String formatClassPaths() {
        StringBuilder finalClassPath = new StringBuilder();

        for (final Path cp : Arguments.v().getClassPaths()) {
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