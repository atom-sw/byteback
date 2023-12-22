package byteback.analysis.transformer;

import static org.junit.Assert.fail;

import soot.Value;

public class ValueTransformerFixture {

	public static void assertEquiv(final Value a, final Value b) {
		if (!a.equivTo(b)) {
			fail("Values " + a + " and " + b + "are not equivalent");
		}
	}

}
