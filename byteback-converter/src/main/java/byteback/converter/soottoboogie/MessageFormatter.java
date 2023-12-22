package byteback.converter.soottoboogie;

import byteback.frontend.boogie.ast.Attribute;
import byteback.frontend.boogie.ast.Expression;
import byteback.frontend.boogie.ast.List;
import byteback.frontend.boogie.ast.StringLiteral;
import soot.tagkit.Host;

public class MessageFormatter {

	public static String makeMessage(final Host subject, final String message) {
		final int lineStart = subject.getJavaSourceStartColumnNumber();
		final int columnStart = subject.getJavaSourceStartColumnNumber();
		final String location = lineStart + ":" + columnStart;

		return location + " " + message;
	}

	public static Attribute makeAttribute(final Host subject, final String message) {
		final List<Expression> literals = new List<>(new StringLiteral(makeMessage(subject, message)));

		return new Attribute("msg", literals);
	}

}
