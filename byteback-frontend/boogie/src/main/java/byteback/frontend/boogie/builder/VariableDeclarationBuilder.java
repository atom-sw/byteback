package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.*;

public class VariableDeclarationBuilder extends DeclarationBuilder {

	private List<BoundedBinding> bindings;

	public VariableDeclarationBuilder() {
		this.bindings = new List<>();
	}

	public VariableDeclarationBuilder addBinding(final BoundedBinding binding) {
		bindings.add(binding);

		return this;
	}

	@Override
	public VariableDeclarationBuilder addAttribute(final Attribute attribute) {
		super.addAttribute(attribute);

		return this;
	}

	public VariableDeclaration build() {
		if (bindings.getNumChild() == 0) {
			throw new IllegalArgumentException("Variable declaration must contain at least one binding");
		}

		return new VariableDeclaration(attributes, bindings);
	}

}
