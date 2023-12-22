package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.Declarator;
import byteback.frontend.boogie.ast.List;
import byteback.frontend.boogie.ast.SetBinding;
import byteback.frontend.boogie.ast.TypeAccess;

public class SetBindingBuilder extends BindingBuilder {

	private List<Declarator> declarators;

	public SetBindingBuilder() {
		this.declarators = new List<>();
	}

	public SetBindingBuilder name(final String name) {
		this.declarators = new List<>(new Declarator(name));

		return this;
	}

	public SetBindingBuilder typeAccess(final TypeAccess typeAccess) {
		super.typeAccess(typeAccess);

		return this;
	}

	public SetBinding build() {
		if (declarators.getNumChild() == 0) {
			throw new IllegalArgumentException("A SetBinding must include at least one declaration");
		}

		return new SetBinding(typeAccess, declarators);
	}

}
