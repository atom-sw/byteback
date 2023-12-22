package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.Expression;
import byteback.frontend.boogie.ast.List;
import byteback.frontend.boogie.ast.TargetedCallStatement;
import byteback.frontend.boogie.ast.ValueReference;

public class TargetedCallStatementBuilder extends CallStatementBuilder {

	private List<ValueReference> targets;

	private List<Expression> arguments;

	public TargetedCallStatementBuilder() {
		this.targets = new List<>();
		this.arguments = new List<>();
	}

	public TargetedCallStatementBuilder targets(final List<ValueReference> targets) {
		this.targets = targets;

		return this;
	}

	public TargetedCallStatementBuilder addTarget(final ValueReference target) {
		targets.add(target);

		return this;
	}

	public TargetedCallStatementBuilder arguments(final List<Expression> arguments) {
		this.arguments = arguments;

		return this;
	}

	public TargetedCallStatementBuilder addArgument(final Expression argument) {
		arguments.add(argument);

		return this;
	}

	public TargetedCallStatement build() {
		return new TargetedCallStatement(accessor, targets, arguments);
	}

}
