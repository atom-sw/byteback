package byteback.converter.soottoboogie;

import byteback.frontend.boogie.ast.BoundedBinding;
import byteback.frontend.boogie.ast.Label;
import byteback.frontend.boogie.ast.TypeAccess;
import byteback.frontend.boogie.ast.ValueReference;
import byteback.frontend.boogie.builder.BoundedBindingBuilder;

public class Convention {

	public static BoundedBinding makeReturnBinding(final TypeAccess typeAccess) {
		return new BoundedBindingBuilder().addName("~ret").typeAccess(typeAccess).build();
	}

	public static ValueReference makeReturnReference() {
		return ValueReference.of("~ret");
	}

	public static BoundedBinding makeExceptionBinding(final TypeAccess typeAccess) {
		return new BoundedBindingBuilder().addName("~exc").typeAccess(typeAccess).build();
	}

	public static ValueReference makeExceptionReference() {
		return ValueReference.of("~exc");
	}

	public static Label makeLabelStatement(final int index) {
		return new Label("label" + index);
	}

	public static ValueReference makeValueReference(final int index) {
		return ValueReference.of(makeVariableName(index));
	}

	public static String makeVariableName(final int index) {
		return "~sym" + index;
	}

	public static String makeParameterName(final String name) {
		return "!" + name;
	}

}
