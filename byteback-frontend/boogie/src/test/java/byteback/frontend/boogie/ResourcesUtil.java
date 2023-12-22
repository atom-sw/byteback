package byteback.frontend.boogie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for accessing test resources.
 */
public class ResourcesUtil {

	/**
	 * Base path to the resource folder.
	 */
	final private static Path resourcesPath = Paths.get("src", "test", "resources");

	/**
	 * Base path to the Boogie resource folder.
	 */
	final private static Path boogiePath = resourcesPath.resolve("boogie");

	/**
	 * Fetches the paths to the test Boogie files.
	 *
	 * @param bplName
	 *            The name of the Boogie file.
	 * @return The path to the Boogie file.
	 * @throws FileNotFoundException
	 *             If the resource could not be located.
	 */
	public static Path getBoogiePath(final String bplName) throws FileNotFoundException {
		final Path bplPath = boogiePath.resolve(bplName + ".bpl");

		if (Files.exists(bplPath)) {
			return bplPath;
		} else {
			throw new FileNotFoundException("Could not find resource at " + bplPath.toString());
		}
	}

	/**
	 * Fetches the paths to the test Boogie files.
	 *
	 * @param bplName
	 *            The name of the Boogie file.
	 * @return The input stream of the Boogie file.
	 * @throws FileNotFoundException
	 *             If the resource could not be located.
	 */
	public static Reader getBoogieReader(final String bplName) throws FileNotFoundException {
		final Path bplPath = getBoogiePath(bplName);
		final File bplFile = bplPath.toFile();

		return new FileReader(bplFile);
	}

}
