package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.Expression;
import byteback.frontend.boogie.ast.List;
import byteback.frontend.boogie.ast.Option;
import byteback.frontend.boogie.ast.Quantifier;
import byteback.frontend.boogie.ast.QuantifierExpression;
import byteback.frontend.boogie.ast.SetBinding;
import byteback.frontend.boogie.ast.Trigger;
import byteback.frontend.boogie.ast.TypeParameter;

public class QuantifierExpressionBuilder {

	private Expression operand;

	private Quantifier quantifier;

	private List<TypeParameter> typeParameters;

	private List<SetBinding> bindings;

	private List<Option> options;

	private List<Expression> triggerExpressions;

	public QuantifierExpressionBuilder() {
		this.typeParameters = new List<>();
		this.bindings = new List<>();
		this.options = new List<>();
		this.triggerExpressions = new List<>();
	}

	public QuantifierExpressionBuilder operand(final Expression operand) {
		this.operand = operand;

		return this;
	}

	public QuantifierExpressionBuilder quantifier(final Quantifier quantifier) {
		this.quantifier = quantifier;

		return this;
	}

	public QuantifierExpressionBuilder bindings(final List<SetBinding> bindings) {
		this.bindings.addAll(bindings);

		return this;
	}

	public QuantifierExpressionBuilder addBinding(final SetBinding binding) {
		this.bindings.add(binding);

		return this;
	}

	public QuantifierExpressionBuilder options(final List<Option> options) {
		this.options.addAll(options);

		return this;
	}

	public QuantifierExpressionBuilder addOption(final Option option) {
		this.options.add(option);

		return this;
	}

	public QuantifierExpressionBuilder typeParameters(final List<TypeParameter> typeParameters) {
		this.typeParameters.addAll(typeParameters);

		return this;
	}

	public QuantifierExpressionBuilder addTypeParameter(final TypeParameter typeParameter) {
		this.typeParameters.add(typeParameter);

		return this;
	}

	public QuantifierExpressionBuilder addTrigger(final Expression expression) {
		this.triggerExpressions.add(expression);

		return this;
	}

	public QuantifierExpression build() {
		if (operand == null) {
			throw new IllegalArgumentException("A quantifier expression must include an operand");
		}

		if (quantifier == null) {
			throw new IllegalArgumentException("No quantifier defined");
		}

		options.add(new Trigger(triggerExpressions));

		return new QuantifierExpression(operand, quantifier, typeParameters, bindings, options);
	}

}
