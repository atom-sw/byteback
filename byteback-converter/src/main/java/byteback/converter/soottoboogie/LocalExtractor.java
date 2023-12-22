package byteback.converter.soottoboogie;

import byteback.analysis.JimpleValueSwitch;
import soot.Local;
import soot.Value;

/**
 * Extractor class for a {@link Local} expression.
 */
public class LocalExtractor extends JimpleValueSwitch<Local> {

	private Local local;

	@Override
	public void caseLocal(final Local local) {
		this.local = local;
	}

	@Override
	public void caseDefault(final Value expression) {
		throw new ConversionException("Expected local definition, got " + expression);
	}

	@Override
	public Local result() {
		if (local == null) {
			throw new IllegalStateException("Could not retrieve local reference");
		} else {
			return local;
		}
	}

}
