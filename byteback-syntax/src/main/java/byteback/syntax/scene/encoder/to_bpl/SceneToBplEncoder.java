package byteback.syntax.scene.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.encoder.SceneEncoder;
import byteback.syntax.scene.type.declaration.encoder.to_bpl.ClassToBplEncoder;
import soot.Scene;
import soot.SootClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * Encodes a Soot Scene into the Boogie Intermediate Verification
 * Language.
 *
 * @author paganma
 */
public class SceneToBplEncoder extends SceneEncoder {

	private static final String PRELUDE_SOURCE_PATH = "/boogie/ByteBackPrelude.bpl";

	public SceneToBplEncoder(final Printer printer) {
		super(printer);
	}

	@Override
	public void encodeScene(final Scene scene) {
		try (final InputStream preludeStream = getClass().getResourceAsStream(PRELUDE_SOURCE_PATH)) {
			if (preludeStream == null) {
				throw new IllegalStateException("Unable to find Boogie prelude.");
			}

			final var preludeStreamReader = new InputStreamReader(preludeStream);
			final var preludeBufferedReader = new BufferedReader(preludeStreamReader);
			String line;

			while ((line = preludeBufferedReader.readLine()) != null) {
				printer.printLine(line);
			}
		} catch (final IOException exception) {
			throw new RuntimeException("Error reading the Boogie prelude.", exception);
		}

		final Iterator<SootClass> iterator = scene.getClasses().snapshotIterator();

		while (iterator.hasNext()) {
			final SootClass sootClass = iterator.next();
			new ClassToBplEncoder(printer).encodeClass(sootClass);
		}
	}

}
