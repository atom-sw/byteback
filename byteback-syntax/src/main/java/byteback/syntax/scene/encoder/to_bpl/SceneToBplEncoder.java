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

public class SceneToBplEncoder extends SceneEncoder {

	public SceneToBplEncoder(final Printer printer) {
		super(printer);
	}

	@Override
	public void encodeScene(final Scene scene) {
		final String preludeResourcePath = "/boogie/ByteBackPrelude.bpl";

		try (final InputStream preludeStream = getClass().getResourceAsStream(preludeResourcePath)) {
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
			throw new RuntimeException("Error opening the Boogie prelude.", exception);
		}

		for (final SootClass sootClass : scene.getClasses()) {
			new ClassToBplEncoder(printer).encodeClass(sootClass);
		}

		printer.close();
	}

}
