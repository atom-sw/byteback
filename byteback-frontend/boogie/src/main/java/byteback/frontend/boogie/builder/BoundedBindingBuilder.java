package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.*;

public class BoundedBindingBuilder extends BindingBuilder {

	private List<Declarator> declarators;

	private Opt<WhereClause> whereClause;

	public BoundedBindingBuilder() {
		this.declarators = new List<>();
		this.whereClause = new Opt<>();
	}

	public BoundedBindingBuilder addName(final String name) {
		declarators.add(new Declarator(name));

		return this;
	}

	public BoundedBindingBuilder typeAccess(final TypeAccess typeAccess) {
		super.typeAccess(typeAccess);

		return this;
	}

	public BoundedBindingBuilder whereClause(final Expression clause) {
		this.whereClause = new Opt<>(new WhereClause(clause));

		return this;
	}

	public BoundedBinding build() {
		if (typeAccess == null) {
			new IllegalArgumentException("Bounded binding must include a type access");
		}

		if (declarators.getNumChild() == 0) {
			throw new IllegalArgumentException("Bounded binding must declare at least one name");
		}

		return new BoundedBinding(typeAccess, declarators, whereClause);
	}

}
