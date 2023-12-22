package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.Accessor;
import byteback.frontend.boogie.ast.Expression;
import byteback.frontend.boogie.ast.FunctionReference;
import byteback.frontend.boogie.ast.List;

public class FunctionReferenceBuilder {

	protected Accessor accessor;

	protected List<Expression> arguments;

	public FunctionReferenceBuilder() {
		this.arguments = new List<>();
	}

	public FunctionReferenceBuilder name(final String name) {
		this.accessor = new Accessor(name);

		return this;
	}

	public FunctionReferenceBuilder addArgument(final Expression argument) {
		this.arguments.add(argument);

		return this;
	}

	public FunctionReferenceBuilder prependArgument(final Expression argument) {
		this.arguments.insertChild(argument, 0);

		return this;
	}

	public FunctionReferenceBuilder addArguments(final Iterable<Expression> arguments) {
		this.arguments.addAll(arguments);

		return this;
	}

	public FunctionReference build() {
		return new FunctionReference(accessor, arguments);
	}

}
