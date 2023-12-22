package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.*;

public class OptionalBindingBuilder extends BindingBuilder {

	protected Opt<Declarator> declarator;

	public OptionalBindingBuilder() {
		this.declarator = new Opt<>();
	}

	public OptionalBindingBuilder name(final String name) {
		declarator = new Opt<>(new Declarator(name));

		return this;
	}

	public OptionalBindingBuilder typeAccess(final TypeAccess typeAccess) {
		super.typeAccess(typeAccess);

		return this;
	}

	public OptionalBinding build() {
		if (typeAccess == null) {
			throw new IllegalArgumentException("Optional binding must include a type access");
		}

		return new OptionalBinding(typeAccess, declarator);
	}

}
