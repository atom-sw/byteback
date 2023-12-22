package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.*;

public abstract class BindingBuilder {

	protected TypeAccess typeAccess;

	public BindingBuilder typeAccess(final TypeAccess typeAccess) {
		this.typeAccess = typeAccess;

		return this;
	}

}
