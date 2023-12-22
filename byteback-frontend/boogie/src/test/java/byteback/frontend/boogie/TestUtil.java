package byteback.frontend.boogie;

import byteback.frontend.boogie.ast.Printable;

public class TestUtil {

	public static boolean astEquals(final Printable a, final Printable b) {
		return a.print().equals(b.print());
	}

	public static void assertAstEquals(final Printable expected, final Printable actual) {
		if (!astEquals(expected, actual)) {
			System.err.println("EXPECTED:");
			System.err.println(expected.print());
			System.err.println("ACTUAL:");
			System.err.println(actual.print());
		}
	}

}
